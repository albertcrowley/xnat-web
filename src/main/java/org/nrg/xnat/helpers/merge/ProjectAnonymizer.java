/*
 * web: org.nrg.xnat.helpers.merge.ProjectAnonymizer
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.merge;

import org.nrg.config.entities.Configuration;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xnat.helpers.editscript.DicomEdit;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ProjectAnonymizer extends AnonymizerA implements Callable<Boolean> {
    final String                projectId;
    final String                sessionPath;
    final String                label;
    final XnatImagesessiondataI s;
    final String                path;
    final String                subjectLabel;

    /**
     * @param s           The session object.
     * @param projectId   The project Id, eg. xnat_E*
     * @param sessionPath The root path of this project's session directory
     */
    public ProjectAnonymizer(XnatImagesessiondataI s, String projectId, String sessionPath) {
        this.s = s;
        this.projectId = projectId;
        this.sessionPath = sessionPath;
        this.label = s.getLabel();
        this.path = DicomEdit.buildScriptPath(DicomEdit.ResourceScope.PROJECT, projectId);
        this.subjectLabel = null;
    }

    public ProjectAnonymizer(String label, XnatImagesessiondataI s, String projectId, String sessionPath) {
        this.s = s;
        this.projectId = projectId;
        this.sessionPath = sessionPath;
        this.label = label;
        this.path = DicomEdit.buildScriptPath(DicomEdit.ResourceScope.PROJECT, projectId);
        this.subjectLabel = null;
    }

    public ProjectAnonymizer(XnatImagesessiondataI s, String subjectLabel, String projectId, String sessionPath) {
        this.s = s;
        this.projectId = projectId;
        this.sessionPath = sessionPath;
        this.label = s.getLabel();
        this.path = DicomEdit.buildScriptPath(DicomEdit.ResourceScope.PROJECT, projectId);
        this.subjectLabel = subjectLabel;
    }

    /**
     * Returns the subject string that will be passed into the
     * Anonymize.anonymize function
     *
     * @return The subject label or subject id (if label is null)
     */
    @Override
    String getSubject() {

        if (null != this.subjectLabel) {
            return this.subjectLabel;
        }

        String label = null;
        if (s instanceof XnatImagesessiondata) {
            XnatSubjectdata d = ((XnatImagesessiondata) this.s).getSubjectData();
            if (d != null) {
                label = d.getLabel();
            }
        }

        // If the label is null, return the SubjectId
        return (label != null) ? label : this.s.getSubjectId();
    }

    @Override
    String getLabel() {
        return this.label;
    }

    @Override
    String getProjectName() {
        return this.projectId;
    }

    /**
     * Retrieve a list of files that need to be anonymized.
     * By default the files are retrieved from the project's archive space.
     *
     * @return The list of files to be anonymized.
     */
    @Override
    public List<File> getFilesToAnonymize() {
        List<File> ret = new ArrayList<>();
        // anonymize everything in srcRootPath
        for (final XnatImagescandataI scan : s.getScans_scan()) {
            for (final XnatAbstractresourceI abstractResource : scan.getFile()) {
                if (abstractResource instanceof XnatResource) {
                    final XnatResource resource = (XnatResource) abstractResource;
                    if (resource.getFormat().equals("DICOM")) {
                        for (final File file : resource.getCorrespondingFiles(this.sessionPath)) {
                            ret.add(file);
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    Configuration getScript() {
        return DefaultAnonUtils.getService().getProjectScriptConfiguration(projectId);
    }

    @Override
    boolean isEnabled() {
        return DefaultAnonUtils.getService().isProjectScriptEnabled(projectId);
    }
}
