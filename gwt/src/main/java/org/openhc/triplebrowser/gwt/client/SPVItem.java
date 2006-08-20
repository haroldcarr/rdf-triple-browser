//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 20 (Sun) 13:29:46 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.differentity.client.Main;

public class SPVItem
{
    private final String spvCategory;
    private final String expandedName;
    private final String collapsedName;
    private String expandCollapseState;
    private final Button button;
    private final Hyperlink hyperlink;
    private final HorizontalPanel horizontalPanel;
    private final VerticalPanel verticalPanel;
    
    SPVItem(String spvCategory, String expandedName, String collapsedName,
	    String expandCollapseState)
    {
	this.spvCategory = spvCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
	this.expandCollapseState = expandCollapseState;
	button = new Button(Main.plusSymbol);
	hyperlink = new Hyperlink(expandedName,
				  spvCategory + " " + expandedName);
	horizontalPanel = new HorizontalPanel();
	verticalPanel = new VerticalPanel();

	// Item layout.
	horizontalPanel.add(button);
	horizontalPanel.add(hyperlink);
	verticalPanel.add(horizontalPanel);

	if (expandCollapseState.equals(Main.collapse)){
	    hyperlink.setText(collapsedName);
	} else {
	    button.setText(Main.minusSymbol);
	    verticalPanel.add(new Frame(expandedName));
	}
    }

    String getSPVCategory()              { return spvCategory; }
    String getExpandedName()             { return expandedName; }
    String getCollapsedName()            { return collapsedName; }
    Button getButton()                   { return button; }
    Hyperlink getHyperlink()             { return hyperlink; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
    VerticalPanel getVerticalPanel()     { return verticalPanel; }

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
