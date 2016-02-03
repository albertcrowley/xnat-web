/*
 * org.nrg.xnat.archive.GradualDicomImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 1/3/14 9:54 AM
 */
package org.nrg.xnat.archive;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.dcm4che2.data.*;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.util.TagUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.config.entities.Configuration;
import org.nrg.dcm.Anonymize;
import org.nrg.dcm.Decompress;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dcm.xnat.SOPHashDicomFileNamer;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.dicomtools.filters.SeriesImportFilter;
import org.nrg.framework.constants.PrearchiveCode;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.Files;
import org.nrg.xnat.Labels;
import org.nrg.xnat.helpers.merge.AnonUtils;
import org.nrg.xnat.helpers.prearchive.*;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase.Either;
import org.nrg.xnat.helpers.prearchive.PrearcUtils.SessionFileLockException;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.Callable;

@SuppressWarnings("ThrowFromFinallyBlock")
@Service
public class GradualDicomImporter extends ImporterHandlerA {
    public static final String SENDER_AE_TITLE_PARAM = "Sender-AE-Title";
    public static final String SENDER_ID_PARAM = "Sender-ID";
    public static final String TSUID_PARAM = "Transfer-Syntax-UID";
    public static final int LAST_TAG = Tag.SeriesDescription;

    private static final Logger logger = LoggerFactory.getLogger(GradualDicomImporter.class);
    private static final Object NOT_A_WRITABLE_PROJECT = new Object();
    private static final String DEFAULT_TRANSFER_SYNTAX = TransferSyntax.ImplicitVRLittleEndian.uid();
    private static final String RENAME_PARAM = "rename";
    private static final DicomFileNamer DEFAULT_NAMER = new SOPHashDicomFileNamer();
    private static final long PROJECT_CACHE_EXPIRY_SECONDS = 120;
    private static final boolean canDecompress = initializeCanDecompress();
    private static final CacheManager cacheManager = CacheManager.getInstance();
    private DicomFilterService _filterService;

    private static boolean initializeCanDecompress() {
        try {
            return Decompress.isSupported();
        } catch (NoClassDefFoundError error) {
            return false;
        }
    }

    private final FileWriterWrapperI fw;
    private final UserI user;
    private final Map<String, Object> params;
    private DicomObjectIdentifier<XnatProjectdata> dicomObjectIdentifier;
    private DicomFileNamer namer = DEFAULT_NAMER;
    private TransferSyntax ts = null;
    private Cache projectCache = null;

    public GradualDicomImporter(final Object listenerControl,
                                final UserI u,
                                final FileWriterWrapperI fw,
                                final Map<String, Object> params)
            throws IOException, ClientException {
        super(listenerControl, u, fw, params);
        this.user = u;
        this.fw = fw;
        this.params = params;
        if (params.containsKey(TSUID_PARAM)) {
            ts = TransferSyntax.valueOf((String) params.get(TSUID_PARAM));
        }
    }

    private boolean canCreateIn(final XnatProjectdata p) {
        try {
            return PrearcUtils.canModify(user, p.getId());
        } catch (Throwable t) {
            logger.error("Unable to check permissions for " + user + " in " + p, t);
            return false;
        }
    }

    /**
     * Does e contain the sentinel value indicating that its alias
     * does not map to a project where user has create perms?
     *
     * @param e cache element (not null)
     * @return true if this element indicates no writable project
     */
    public static boolean isCachedNotWriteableProject(final Element e) {
        assert null != e;
        return NOT_A_WRITABLE_PROJECT == e.getObjectValue();
    }

    /**
     * Indicate in the provided cache that the provided name does not describe
     * a writable project.
     *
     * @param cache The cache to use
     * @param name  The name of the project to cache as non-writable.
     */
    public static void cacheNonWriteableProject(final Cache cache, final String name) {
        cache.put(new Element(name, NOT_A_WRITABLE_PROJECT));
    }

    private XnatProjectdata getProject(final Object alias, final Callable<XnatProjectdata> lookupProject) {
        if (null == projectCache) {
            projectCache = getUserProjectCache(user);
        }
        if (null != alias) {
            logger.debug("looking for project matching alias {} from query parameters", alias);
            final Element pe = projectCache.get(alias);
            if (null != pe) {
                if (isCachedNotWriteableProject(pe)) {
                    // this alias is cached as a non-writable project name, but user is specifying it.
                    // maybe they know something we don't; clear cache entry so we can try again.
                    projectCache.remove(alias);
                    return getProject(alias, lookupProject);
                } else {
                    return (XnatProjectdata) pe.getObjectValue();
                }
            } else {
                logger.trace("cache miss for project alias {}, trying database", alias);
                final XnatProjectdata p = XnatProjectdata.getXnatProjectdatasById(alias, user, false);
                if (null != p && canCreateIn(p)) {
                    projectCache.put(new Element(alias, p));
                    return p;
                } else {
                    for (final XnatProjectdata pa :
                            XnatProjectdata.getXnatProjectdatasByField("xnat:projectData/aliases/alias/alias",
                                    alias, user, false)) {
                        if (canCreateIn(pa)) {
                            projectCache.put(new Element(alias, pa));
                            return pa;
                        }
                    }
                }
            }
            logger.info("storage request specified project {}, which does not exist or user does not have create perms", alias);
        } else {
            logger.trace("no project alias found in query parameters");
        }
        // No alias, or we couldn't match it to a project. Run the identifier to see if that can get a project name/alias.
        // (Don't cache alias->identifier-derived-project because we didn't use the alias to derive the project.)
        try {
            return null == lookupProject ? null : lookupProject.call();
        } catch (Throwable t) {
            logger.error("error in project lookup", t);
            return null;
        }
    }

    private File getSafeFile(File sessionDir, String scan, String name, DicomObject o, boolean forceRename) {
        String fileName = namer.makeFileName(o);
        while (fileName.charAt(0) == '.') {
            fileName = fileName.substring(1);
        }
        final File safeFile = Files.getImageFile(sessionDir, scan, fileName);
        if (forceRename) {
            return safeFile;
        }
        final String valname = Files.toFileNameChars(name);
        if (!Files.isValidFilename(valname)) {
            return safeFile;
        }
        final File reqFile = Files.getImageFile(sessionDir, scan, valname);
        if (reqFile.exists()) {
            try (final FileInputStream fin = new FileInputStream(reqFile)) {
                final DicomObject o1 = read(fin, name);
                if (Objects.equal(o.get(Tag.SOPInstanceUID), o1.get(Tag.SOPInstanceUID)) &&
                        Objects.equal(o.get(Tag.SOPClassUID), o1.get(Tag.SOPClassUID))) {
                    return reqFile;  // object are equivalent; ok to overwrite
                } else {
                    return safeFile;
                }
            } catch (Throwable t) {
                return safeFile;
            }
        } else {
            return reqFile;
        }
    }

    private static <K, V> String getString(final Map<K, V> m, final K k, final V defaultValue) {
        final V v = m.get(k);
        if (null == v) {
            return null == defaultValue ? null : defaultValue.toString();
        } else {
            return v.toString();
        }
    }

    @Override
    public List<String> call() throws ClientException, ServerException {
        final String name = fw.getName();
        final DicomObject dicom;
        final XnatProjectdata project;
        try (final BufferedInputStream bis = new BufferedInputStream(fw.getInputStream());
             final DicomInputStream dis = null == ts ? new DicomInputStream(bis) : new DicomInputStream(bis, ts)) {
            final int lastTag = Math.max(dicomObjectIdentifier.getTags().last(), LAST_TAG) + 1;
            logger.trace("reading object into memory up to {}", TagUtils.toString(lastTag));
            dis.setHandler(new StopTagInputHandler(lastTag));
            dicom = dis.readDicomObject();

            logger.trace("handling file with query parameters {}", params);
            try {
                // project identifier is expensive, so avoid if possible
                project = getProject(PrearcUtils.identifyProject(params),
                        new Callable<XnatProjectdata>() {
                            public XnatProjectdata call() {
                                return dicomObjectIdentifier.getProject(dicom);
                            }
                        });
            } catch (MalformedURLException e1) {
                logger.error("unable to parse supplied destination flag", e1);
                throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST, e1);
            }
            final String projectId = project != null ? (String) project.getProps().get("id") : null;
            final SeriesImportFilter siteFilter = getDicomFilterService().getSeriesImportFilter();
            final SeriesImportFilter projectFilter = StringUtils.isNotBlank(projectId) ? getDicomFilterService().getSeriesImportFilter(projectId) : null;
            if (logger.isDebugEnabled()) {
                if (siteFilter != null) {
                    if (projectFilter != null) {
                        logger.debug("Found " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " site-wide series import filter and " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " series import filter for the project " + projectId);
                    } else if (StringUtils.isNotBlank(projectId)) {
                        logger.debug("Found " + (siteFilter.isEnabled() ? "enabled" : "disabled") + " site-wide series import filter and no series import filter for the project " + projectId);
                    } else {
                        logger.debug("Found a site-wide series import filter and no project ID was specified");
                    }
                } else if (projectFilter != null) {
                    logger.debug("Found no site-wide series import filter and " + (projectFilter.isEnabled() ? "enabled" : "disabled") + " series import filter for the project " + projectId);
                }
            }
            if (!(shouldIncludeDicomObject(siteFilter, dicom) && shouldIncludeDicomObject(projectFilter, dicom))) {
                return new ArrayList<>();
                /** TODO: Return information to user on rejected files. Unfortunately throwing an
                 *  exception causes DicomBrowser to display a panicked error message. Some way of
                 *  returning the information that a particular file type was not accepted would be
                 *  nice, though. Possibly record the information and display on an admin page.
                 *  Work to be done for 1.7
                 */
            }
            try {
                bis.reset();
            } catch (IOException e) {
                logger.error("unable to reset DICOM data stream", e);
            }
            if (Strings.isNullOrEmpty(dicom.getString(Tag.SOPClassUID))) {
                throw new ClientException("object " + name + " contains no SOP Class UID");
            }
            if (Strings.isNullOrEmpty(dicom.getString(Tag.SOPInstanceUID))) {
                throw new ClientException("object " + name + " contains no SOP Instance UID");
            }

            final String studyInstanceUID = dicom.getString(Tag.StudyInstanceUID);
            logger.trace("Looking for study {} in project {}", studyInstanceUID, null == project ? null : project.getId());

            // Fill a SessionData object in case it is the first upload
            SessionData session;
            final File root;
            if (null == project) {
                root = new File(ArcSpecManager.GetInstance().getGlobalPrearchivePath());
            } else {
                //root = new File(project.getPrearchivePath());
                root = new File(ArcSpecManager.GetInstance().getGlobalPrearchivePath() + "/" + project.getId());
            }
            final File tsdir, sessdir;

            tsdir = new File(root, PrearcUtils.makeTimestamp());

            String sessionLabel;
            if (params.containsKey(URIManager.EXPT_LABEL)) {
                sessionLabel = (String) params.get(URIManager.EXPT_LABEL);
                logger.trace("using provided experiment label {}", params.get(URIManager.EXPT_LABEL));
            } else {
                sessionLabel = dicomObjectIdentifier.getSessionLabel(dicom);
            }

            String visit;
            if (params.containsKey(URIManager.VISIT_LABEL)) {
                visit = (String) params.get(URIManager.VISIT_LABEL);
                logger.trace("using provided visit label {}", params.get(URIManager.VISIT_LABEL));
            } else {
                visit = null;
            }

            if (Strings.isNullOrEmpty(sessionLabel)) {
                sessionLabel = "dicom_upload";
            }

            final String subject;
            if (params.containsKey(URIManager.SUBJECT_ID)) {
                subject = (String) params.get(URIManager.SUBJECT_ID);
            } else {
                subject = dicomObjectIdentifier.getSubjectLabel(dicom);
            }
            if (null == subject) {
                logger.trace("subject is null for session {}/{}", tsdir, sessionLabel);
            }

            session = new SessionData();
            session.setFolderName(sessionLabel);
            session.setName(sessionLabel);
            session.setProject(project == null ? null : project.getId());
            session.setVisit(visit);
            session.setScan_date(dicom.getDate(Tag.StudyDate));
            session.setTag(studyInstanceUID);
            session.setTimestamp(tsdir.getName());
            session.setStatus(PrearcUtils.PrearcStatus.RECEIVING);
            session.setLastBuiltDate(Calendar.getInstance().getTime());
            session.setSubject(subject);
            session.setUrl((new File(tsdir, sessionLabel)).getAbsolutePath());
            session.setSource(params.get(URIManager.SOURCE));
            session.setPreventAnon(Boolean.valueOf((String) params.get(URIManager.PREVENT_ANON)));
            session.setPreventAutoCommit(Boolean.valueOf((String) params.get(URIManager.PREVENT_AUTO_COMMIT)));

            // Query the cache for an existing session that has this Study Instance UID, project name, and optional modality.
            // If found the SessionData object we just created is over-ridden with the values from the cache.
            // Additionally a record of which operation was performed is contained in the Either<SessionData,SessionData>
            // object returned.
            //
            // This record is necessary so that, if this row was created by this call, it can be deleted if anonymization
            // goes wrong. In case of any other error the file is left on the filesystem.
            Either<SessionData, SessionData> getOrCreate;
            try {
                getOrCreate = PrearcDatabase.eitherGetOrCreateSession(session, tsdir, shouldAutoArchive(project, dicom));
                if (getOrCreate.isLeft()) {
                    session = getOrCreate.getLeft();
                } else {
                    session = getOrCreate.getRight();
                }
            } catch (Exception e) {
                throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);
            }

            try {
                //if the status isn't RECEIVING, fix it
                //else if the last mod time is more then 15 seconds ago, update it.
                //this code builds and executes the sql directly, because the APIs for doing so generate multiple SELECT statements (to confirm the row is there)
                //we've confirmed the row is there in line 338, so that shouldn't be necessary here.
                // this code executes for every file received, so any unnecessary sql should be eliminated.
                if (!PrearcUtils.PrearcStatus.RECEIVING.equals(session.getStatus())) {
                    //update the last modified time and set the status
                    PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.updateSessionStatusSQL(session.getName(), session.getTimestamp(), session.getProject(), PrearcUtils.PrearcStatus.RECEIVING), null, null);
                } else if (Calendar.getInstance().getTime().after(DateUtils.addSeconds(session.getLastBuiltDate(), 15))) {
                    PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.updateSessionLastModSQL(session.getName(), session.getTimestamp(), session.getProject()), null, null);
                }
            } catch (Exception e) {
                logger.error("An error occurred while trying to set the session status to RECEIVING", e);
                //not exactly sure what we should do here.  should we throw an exception, and the received file won't be stored locally?  Or should we let it go and let the file be saved but unreferenced.
                //the old code threw an exception, so we'll keep that logic.
                throw new ServerException("An error occurred while trying to set the session status to RECEIVING", e);
            }

            sessdir = new File(new File(root, session.getTimestamp()), session.getFolderName());

            // Build the scan label
            final String seriesNum = dicom.getString(Tag.SeriesNumber);
            final String seriesUID = dicom.getString(Tag.SeriesInstanceUID);
            final String scan;
            if (Files.isValidFilename(seriesNum)) {
                scan = seriesNum;
            } else if (!Strings.isNullOrEmpty(seriesUID)) {
                scan = Labels.toLabelChars(seriesUID);
            } else {
                scan = null;
            }

            final String source = getString(params, SENDER_ID_PARAM, user.getLogin());

            final DicomObject fmi;
            if (dicom.contains(Tag.TransferSyntaxUID)) {
                fmi = dicom.fileMetaInfo();
            } else {
                final String sopClassUID = dicom.getString(Tag.SOPClassUID);
                final String sopInstanceUID = dicom.getString(Tag.SOPInstanceUID);
                final String transferSyntaxUID;
                if (null == ts) {
                    transferSyntaxUID = dicom.getString(Tag.TransferSyntaxUID, DEFAULT_TRANSFER_SYNTAX);
                } else {
                    transferSyntaxUID = ts.uid();
                }
                fmi = new BasicDicomObject();
                fmi.initFileMetaInformation(sopClassUID, sopInstanceUID, transferSyntaxUID);
                if (params.containsKey(SENDER_AE_TITLE_PARAM)) {
                    fmi.putString(Tag.SourceApplicationEntityTitle, VR.AE, (String) params.get(SENDER_AE_TITLE_PARAM));
                }
            }

            final File f = getSafeFile(sessdir, scan, name, dicom, Boolean.valueOf((String) params.get(RENAME_PARAM)));
            f.getParentFile().mkdirs();

            PrearcUtils.PrearcFileLock lock;
            try {
                lock = PrearcUtils.lockFile(session.getSessionDataTriple(), f.getName());
                write(fmi, dicom, bis, f, source);

            } catch (IOException e) {
                throw new ServerException(Status.SERVER_ERROR_INSUFFICIENT_STORAGE, e);
            } catch (SessionFileLockException e) {
                throw new ClientException("Concurrent file sends of the same data is not supported.");
            }
            try {
                // check to see of this session came in through the upload applet
                if (!session.getPreventAnon()) {
                    Configuration c = AnonUtils.getCachedSitewideAnon();
                    if (c != null && c.getStatus().equals(Configuration.ENABLED_STRING)) {
                        //noinspection deprecation
                        Anonymize.anonymize(f,
                                session.getProject(),
                                session.getSubject(),
                                session.getFolderName(),
                                true,
                                c.getId(),
                                c.getContents());
                    } else {
                        logger.debug("Anonymization is not enabled, allowing session {} {} {} to proceed without anonymization.", session.getProject(), session.getSubject(), session.getName());
                    }
                } else {
                    logger.debug("The session {} {} {} has already been anonymized by the uploader, proceeding without further anonymization.", session.getProject(), session.getSubject(), session.getName());
                }
            } catch (Throwable e) {
                logger.debug("Dicom anonymization failed: " + f, e);
                try {
                    // if we created a row in the database table for this session
                    // delete it.
                    if (getOrCreate.isRight()) {
                        PrearcDatabase.deleteSession(session.getFolderName(), session.getTimestamp(), session.getProject());
                    } else {
                        f.delete();
                    }
                } catch (Throwable t) {
                    logger.debug("Unable to delete relevant file :" + f, e);
                    throw new ServerException(Status.SERVER_ERROR_INTERNAL, t);
                }
                throw new ServerException(Status.SERVER_ERROR_INTERNAL, e);
            } finally {
                if (null != lock) {
                    //release the file lock
                    lock.release();
                }
            }

            logger.trace("Stored object {}/{}/{} as {} for {}", project, studyInstanceUID, dicom.getString(Tag.SOPInstanceUID), session.getUrl(), source);
            return Collections.singletonList(session.getExternalUrl());

        } catch (ClientException e) {
            throw e;
        } catch (Throwable t) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST, "unable to read DICOM object " + name, t);
        }

    }

    private boolean shouldIncludeDicomObject(final SeriesImportFilter filter, final DicomObject dicom) {
        // If we don't have a filter or the filter is turned off, then we include the DICOM object by default (no filtering)
        if (filter == null || !filter.isEnabled()) {
            return true;
        }
        final boolean shouldInclude = filter.shouldIncludeDicomObject(dicom);
        if (logger.isDebugEnabled()) {
            final String association = StringUtils.isBlank(filter.getProjectId()) ? "site" : "project " + filter.getProjectId();
            logger.debug("The series import filter for " + association + " indicated a DICOM object from series \"" + dicom.get(Tag.SeriesDescription).getString(dicom.getSpecificCharacterSet(), true) + "\" " + (shouldInclude ? "should" : "shouldn't") + " be included.");
        }
        return shouldInclude;
    }

    private DicomFilterService getDicomFilterService() {
        if (_filterService == null) {
            synchronized (logger) {
                _filterService = XDAT.getContextService().getBean(DicomFilterService.class);
            }
        }
        return _filterService;
    }

    private PrearchiveCode shouldAutoArchive(final XnatProjectdata project, final DicomObject o) {
        if (null == project) {
            return null;
        }
        Boolean fromDicomObject = dicomObjectIdentifier.requestsAutoarchive(o);
        if (fromDicomObject != null) {
            return fromDicomObject ? PrearchiveCode.AutoArchive : PrearchiveCode.Manual;
        }
        return PrearchiveCode.code(project.getArcSpecification().getPrearchiveCode());
    }

    /**
     * Adds a cache of project objects on a per-user basis.  This is currently used by GradualDicomImporter and DbBackedProjectIdentifier
     *
     * @param user The user for whom to retrieve the cache.
     * @return The user's cache.
     */
    public static Cache getUserProjectCache(final UserI user) {
        final String cacheName = user.getLogin() + "-projects";
        synchronized (cacheManager) {
            if (!cacheManager.cacheExists(cacheName)) {
                final CacheConfiguration config = new CacheConfiguration(cacheName, 0)
                        .copyOnRead(false).copyOnWrite(false)
                        .eternal(false)
                        .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE))
                        .timeToLiveSeconds(PROJECT_CACHE_EXPIRY_SECONDS);
                final Cache cache = new Cache(config);
                cacheManager.addCache(cache);
                return cache;
            } else {
                return cacheManager.getCache(cacheName);
            }
        }
    }

    public GradualDicomImporter setIdentifier(final DicomObjectIdentifier<XnatProjectdata> identifier) {
        this.dicomObjectIdentifier = identifier;
        return this;
    }

    public GradualDicomImporter setNamer(final DicomFileNamer namer) {
        this.namer = namer;
        return this;
    }


    private static DicomObject read(final InputStream in, final String name) throws ClientException {
        try (final BufferedInputStream bis = new BufferedInputStream(in);
             final DicomInputStream dis = new DicomInputStream(bis)) {
            final DicomObject o = dis.readDicomObject();
            if (Strings.isNullOrEmpty(o.getString(Tag.SOPClassUID))) {
                throw new ClientException("object " + name + " contains no SOP Class UID");
            }
            if (Strings.isNullOrEmpty(o.getString(Tag.SOPInstanceUID))) {
                throw new ClientException("object " + name + " contains no SOP Instance UID");
            }
            return o;
        } catch (IOException e) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST, "unable to parse or close DICOM object", e);
        }
    }

    private static Logger slog() {
        return LoggerFactory.getLogger(GradualDicomImporter.class);
    }

    private static void write(final DicomObject fmi, final DicomObject dataset,
                              BufferedInputStream remainder, final File f, final String source)
            throws ClientException, IOException {
        IOException ioexception = null;
        final FileOutputStream fos = new FileOutputStream(f);
        final BufferedOutputStream bos = new BufferedOutputStream(fos);
        try {
            final DicomOutputStream dos = new DicomOutputStream(bos);
            try {
                final String tsuid = fmi.getString(Tag.TransferSyntaxUID, DEFAULT_TRANSFER_SYNTAX);
                try {
                    if (Decompress.needsDecompress(tsuid) && canDecompress) {
                        try {
                            // Read the rest of the object into memory so the pixel data can be decompressed.
                            final DicomInputStream dis = new DicomInputStream(remainder, tsuid);
                            try {
                                dis.readDicomObject(dataset, -1);
                            } catch (IOException e) {
                                ioexception = e;
                                throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST,
                                        "error parsing DICOM object", e);
                            }
                            final ByteArrayInputStream bis = new ByteArrayInputStream(Decompress.dicomObject2Bytes(dataset, tsuid));
                            final DicomObject d = Decompress.decompress_image(bis, tsuid);
                            final String dtsdui = Decompress.getTsuid(d);
                            try {
                                fmi.putString(Tag.TransferSyntaxUID, VR.UI, dtsdui);
                                dos.writeFileMetaInformation(fmi);
                                dos.writeDataset(d.dataset(), dtsdui);
                            } catch (Throwable t) {
                                if (t instanceof IOException) {
                                    ioexception = (IOException) t;
                                } else {
                                    slog().error("Unable to write decompressed dataset", t);
                                }
                                try {
                                    dos.close();
                                } catch (IOException e) {
                                    throw ioexception = null == ioexception ? e : ioexception;
                                }
                            }
                        } catch (ClientException e) {
                            throw e;
                        } catch (Throwable t) {
                            slog().error("Decompression failed; storing in original format " + tsuid, t);
                            dos.writeFileMetaInformation(fmi);
                            dos.writeDataset(dataset, tsuid);
                            if (null != remainder) {
                                final long copied = ByteStreams.copy(remainder, bos);
                                slog().trace("copied {} additional bytes to {}", copied, f);
                            }
                        }
                    } else {
                        dos.writeFileMetaInformation(fmi);
                        dos.writeDataset(dataset, tsuid);
                        if (null != remainder) {
                            final long copied = ByteStreams.copy(remainder, bos);
                            slog().trace("copied {} additional bytes to {}", copied, f);
                        }
                    }
                } catch (NoClassDefFoundError t) {
                    slog().error("Unable to check compression status; storing in original format " + tsuid, t);
                    dos.writeFileMetaInformation(fmi);
                    dos.writeDataset(dataset, tsuid);
                    if (null != remainder) {
                        final long copied = ByteStreams.copy(remainder, bos);
                        slog().trace("copied {} additional bytes to {}", copied, f);
                    }
                }
            } catch (IOException e) {
                throw ioexception = null == ioexception ? e : ioexception;
            } finally {
                try {
                    dos.close();
                    LoggerFactory.getLogger("org.nrg.xnat.received").info("{}:{}", source, f);
                } catch (IOException e) {
                    throw null == ioexception ? e : ioexception;
                }
            }
        } catch (IOException e) {
            throw ioexception = e;
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                throw null == ioexception ? e : ioexception;
            }
        }
    }
}
