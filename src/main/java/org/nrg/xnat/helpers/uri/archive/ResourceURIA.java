/*
 * web: org.nrg.xnat.helpers.uri.archive.ResourceURIA
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.uri.archive;

import com.google.common.collect.Lists;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.URIManager.ArchiveURI;
import org.nrg.xnat.helpers.uri.UriParserUtils;

import java.util.List;
import java.util.Map;

public abstract class ResourceURIA extends ArchiveURI implements ResourceURII {

	public ResourceURIA(Map<String, Object> props, String uri) {
		super(props, uri);
	}

	@Override
	public String getResourceLabel() {
		return (String)props.get(URIManager.XNAME);
	}

	@Override
	public String getResourceFilePath() {
		return (String)props.get(UriParserUtils._REMAINDER);
	}

	@Override
	public List<XnatAbstractresourceI> getResources(boolean includeAll) {
		return Lists.newArrayList(this.getXnatResource());
	}

}
