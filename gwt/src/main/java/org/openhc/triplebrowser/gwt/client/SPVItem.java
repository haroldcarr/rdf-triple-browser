//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2007 Jun 09 (Sat) 07:51:37 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.differentity.client.Main;

public class SPVItem
{
    private final String spvCategory;
    private final String expandedName;
    private final String collapsedName;
    private String expandCollapseState;
    private final Button button;
    private final Label label;
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
	label = new Label(expandedName);
	horizontalPanel = new HorizontalPanel();
	verticalPanel = new VerticalPanel();

	// Item layout.
	horizontalPanel.add(button);
	horizontalPanel.add(label);
	verticalPanel.add(horizontalPanel);

	if (expandCollapseState.equals(Main.collapse)){
	    label.setText(collapsedName);
	} else {
	    button.setText(Main.minusSymbol);
	    final Frame frame = new Frame(expandedName);
	    frame.setPixelSize(280, 100); // REVISIT
	    verticalPanel.add(frame);
	}
    }

    String getSPVCategory()              { return spvCategory; }
    String getExpandedName()             { return expandedName; }
    String getCollapsedName()            { return collapsedName; }
    Button getButton()                   { return button; }
    Label  getLabel()                    { return label; }
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
