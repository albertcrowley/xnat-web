/*
 * org.nrg.xnat.helpers.prearchive.ProjectPrearchiveI
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/10/13 9:04 PM
 */
package org.nrg.xnat.helpers.prearchive;

import org.nrg.xft.XFTTable;

import java.util.Date;

public interface ProjectPrearchiveI {

	public abstract Date getLastMod();

	public abstract XFTTable getContent();

}