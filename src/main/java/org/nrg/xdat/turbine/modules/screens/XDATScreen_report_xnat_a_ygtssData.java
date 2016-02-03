/*
 * GENERATED FILE
 * Created on Thu Jan 28 18:10:09 UTC 2016
 *
 */
package org.nrg.xdat.turbine.modules.screens;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.turbine.modules.screens.SecureReport;

/**
 * @author XDAT
 *
 */
public class XDATScreen_report_xnat_a_ygtssData extends SecureReport {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(XDATScreen_report_xnat_a_ygtssData.class);
	/* (non-Javadoc)
	 * @see org.nrg.xdat.turbine.modules.screens.SecureReport#finalProcessing(org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
	 */
	public void finalProcessing(RunData data, Context context) {
		try{
			org.nrg.xdat.om.XnatAygtssdata om = new org.nrg.xdat.om.XnatAygtssdata(item);
			context.put("om",om);
			System.out.println("Loaded om object (org.nrg.xdat.om.XnatAygtssdata) as context parameter 'om'.");
			context.put("subject",om.getSubjectData());
			System.out.println("Loaded subject object (org.nrg.xdat.om.XnatSubjectdata) as context parameter 'subject'.");
		} catch(Exception e){}
	}}
