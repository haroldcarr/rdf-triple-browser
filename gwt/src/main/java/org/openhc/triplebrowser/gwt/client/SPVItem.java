//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 20:55:39 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import com.google.gwt.user.client.ui.Label;

import org.openhc.trowser.gwt.client.Main;

public class SPVItem
{
    private final String spvCategory;
    private final String expandedName;
    private final String collapsedName;
    
    public SPVItem(String spvCategory, 
		   String expandedName, 
		   String collapsedName)
    {
	this.spvCategory = spvCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
    }

    public String getSPVCategory()   { return spvCategory; }
    public String getExpandedName()  { return expandedName; }
    public String getCollapsedName() { return collapsedName; }
}

// End of file.
