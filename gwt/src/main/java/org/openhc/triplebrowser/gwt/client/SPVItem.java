//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 28 (Fri) 18:13:32 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.differentity.client.Main;

public class SVOItem
{
    private final String svoCategory;
    private final String expandedName;
    private final String collapsedName;
    private String expandCollapseState;
    private final Button button;
    private final Hyperlink hyperlink;
    private final HorizontalPanel horizontalPanel;
    private final VerticalPanel verticalPanel;
    
    SVOItem(String svoCategory, String expandedName, String collapsedName,
	    String expandCollapseState)
    {
	this.svoCategory = svoCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
	this.expandCollapseState = expandCollapseState;
	button = new Button(Main.plusSymbol);
	hyperlink = new Hyperlink(expandedName,
				  svoCategory + " " + expandedName);
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

    String getSVOCategory() { return svoCategory; }
    String getExpandedName() { return expandedName; }
    String getCollapsedName() { return collapsedName; }
    String getCurrentExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, false);
    }
    String getPendingExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, true);
    }
    void   setExpandCollapseState(String x) { expandCollapseState = x; }
    Button getButton() { return button; }
    Hyperlink getHyperlink() { return hyperlink; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
    VerticalPanel getVerticalPanel() { return verticalPanel; }
}

// End of file.
