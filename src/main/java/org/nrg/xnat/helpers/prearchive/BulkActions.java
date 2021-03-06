/*
 * web: org.nrg.xnat.helpers.prearchive.BulkActions
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive;

import java.util.ArrayList;
import java.util.List;

public final class BulkActions {
	static List<Object> scheduledActions = java.util.Collections.synchronizedList(new ArrayList<Object>());
	
	// prevent instantiation
	private BulkActions() {} 
}
