/*
 * GENERATED FILE
 * Created on Thu Jan 28 18:10:01 UTC 2016
 *
 */
package org.nrg.xdat.om;
import org.nrg.xft.*;
import org.nrg.xdat.om.base.*;
import org.nrg.xft.security.UserI;

import java.util.*;

/**
 * @author XDAT
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class CatEntry extends BaseCatEntry {

	public CatEntry(ItemI item)
	{
		super(item);
	}

	public CatEntry(UserI user)
	{
		super(user);
	}

	/*
	 * @deprecated Use BaseCatEntry(UserI user)
	 **/
	public CatEntry()
	{}

	public CatEntry(Hashtable properties, UserI user)
	{
		super(properties,user);
	}

}
