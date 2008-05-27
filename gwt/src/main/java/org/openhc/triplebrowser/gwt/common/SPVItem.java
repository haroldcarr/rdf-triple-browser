//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 27 (Tue) 10:10:39 by Harold Carr.
//

package org.openhc.trowser.gwt.common;

public class SPVItem
{
    protected final String spvCategory;
    protected final String expandedName;
    protected final String collapsedName;
    
    public SPVItem(final String spvCategory, 
		   final String expandedName, 
		   final String collapsedName)
    {
	this.spvCategory   = spvCategory;
	this.expandedName  = expandedName;
	this.collapsedName = collapsedName;
    }

    public String getSPVCategory()   { return spvCategory; }
    public String getExpandedName()  { return expandedName; }
    public String getCollapsedName() { return collapsedName; }
}

// End of file.
