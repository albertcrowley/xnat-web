/*
 * web: org.nrg.xnat.helpers.uri.archive.impl.ProjSubjExptURI
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.uri.archive.impl;

import com.google.common.collect.Lists;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatReconstructedimagedataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.URIManager.ArchiveItemURI;
import org.nrg.xnat.helpers.uri.archive.ExperimentURII;
import org.nrg.xnat.turbine.utils.ArchivableItem;

import java.util.List;
import java.util.Map;

public class ProjSubjExptURI extends ProjSubjURI implements ArchiveItemURI,ExperimentURII{
	private XnatExperimentdata expt=null;
	
	public ProjSubjExptURI(Map<String, Object> props, String uri) {
		super(props, uri);
	}

	protected void populateExperiment(){
		super.populateSubject();
		
		if(expt==null){
			final XnatProjectdata proj=getProject();
			
			final String exptID= (String)props.get(URIManager.EXPT_ID);
			
			if(proj!=null){
				expt=XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), exptID,null, false);
			}
			
			if(expt==null){
				expt=XnatExperimentdata.getXnatExperimentdatasById(exptID, null, false);
				if(expt!=null && (proj!=null && !expt.hasProject(proj.getId()))){
					expt=null;
				}
			}
		}
	}

	@Override
	public ArchivableItem getSecurityItem() {
		return getExperiment();
	}
	
	public XnatExperimentdata getExperiment(){
		this.populateExperiment();
		return expt;
	}

	@Override
	public List<XnatAbstractresourceI> getResources(boolean includeAll) {
		List<XnatAbstractresourceI> res=Lists.newArrayList();
		final XnatExperimentdata expt=getExperiment();
		res.addAll(expt.getResources_resource());
		
		if(expt instanceof XnatImagesessiondata && includeAll){
			for(XnatImagescandataI scan:((XnatImagesessiondata)expt).getScans_scan()){
				res.addAll(scan.getFile());
			}
			for(XnatReconstructedimagedataI scan:((XnatImagesessiondata)expt).getReconstructions_reconstructedimage()){
				res.addAll(scan.getOut_file());
			}
			for(XnatImageassessordataI scan:((XnatImagesessiondata)expt).getAssessors_assessor()){
				res.addAll(scan.getOut_file());
			}
		}
		
		return res;
	}
	
	
}
