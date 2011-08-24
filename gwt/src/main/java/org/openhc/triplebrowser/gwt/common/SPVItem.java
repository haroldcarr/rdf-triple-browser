//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:54:33 by carr.
//

package org.openhc.triplebrowser.gwt.common;

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
