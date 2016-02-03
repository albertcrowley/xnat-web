/*
 * org.nrg.xnat.turbine.modules.actions.EditImageSessionAction
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/10/13 9:04 PM
 */
package org.nrg.xnat.turbine.modules.actions;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xft.XFTItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tim
 */
@SuppressWarnings("unused")
public class EditImageSessionAction extends ModifySubjectAssessorData {
    private static final Logger logger = LoggerFactory.getLogger(EditImageSessionAction.class);


    /* (non-Javadoc)
     * @see org.nrg.xdat.turbine.modules.actions.ModifyItem#preSave(org.nrg.xft.XFTItem, org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
     */
    @Override
    public void preSave(XFTItem item, RunData data, Context context) throws Exception {
        try {

            // Migration: Is there a UserI-friendly version of XDATUser.clearBrowseableElementDisplays() and its kin?
            ((XDATUser) data.getSession().getAttribute("user")).clearBrowseableElementDisplays();

            if (item.getProperty("note") == null) {
                item.setProperty("note", "NULL");
            }

            for (final XFTItem scan : item.getChildItems("scans.scan")) {
                if (scan.getProperty("note") == null) {
                    scan.setProperty("note", "NULL");
                }
            }
        } catch (RuntimeException e1) {
            logger.error("", e1);
        }
    }


}
