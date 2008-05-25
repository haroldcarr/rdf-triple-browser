//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 12 (Mon) 20:55:47 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Label;

import com.differentity.client.Main;

public class SPVItem
{
    private final String spvCategory;
    private final String expandedName;
    private final String collapsedName;
    private String expandCollapseState;
    private final Label label;
    
    SPVItem(String spvCategory, String expandedName, String collapsedName,
	    String expandCollapseState)
    {
	this.spvCategory = spvCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
	this.expandCollapseState = expandCollapseState;
	label = new Label(expandedName);

	if (expandCollapseState.equals(Main.collapse)){
	    label.setText(collapsedName);
	}
    }

    String getSPVCategory()              { return spvCategory; }
    String getExpandedName()             { return expandedName; }
    String getCollapsedName()            { return collapsedName; }
    Label  getLabel()                    { return label; }

    String getCurrentExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, false);
    }

    String getPendingExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, true);
    }

    void   setExpandCollapseState(String x) {
	expandCollapseState = x; 
    }
}

// End of file.
