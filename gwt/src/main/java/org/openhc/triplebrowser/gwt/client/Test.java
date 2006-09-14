//
// Created       : 2006 Jul 30 (Sun) 15:53:20 by Harold Carr.
// Last Modified : 2006 Sep 12 (Tue) 22:22:31 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Test
    implements HistoryListener 
{
    private Widget widget;

    private Label lbl = new Label();

    public Test()
    {

	// Create three hyperlinks that change the application's history.
	Hyperlink link0 = new Hyperlink("link to foo", "foo");
	Hyperlink link1 = new Hyperlink("link to bar", "bar");
	Hyperlink link2 = new Hyperlink("link to baz", "baz");

	// If the application starts with no history token, start it off.
	String initToken = History.getToken();
	if (initToken.length() == 0) {
	    initToken = "baz";
	}

	// onHistoryChanged() is not called when the application first runs.
	// Call it now in order to reflect the initial state.
	onHistoryChanged(initToken);

	// Add widgets to the root panel.
	VerticalPanel panel = new VerticalPanel();
	panel.add(lbl);
	panel.add(link0);
	panel.add(link1);
	panel.add(link2);

	widget = panel;

	// Add history listener
	History.addHistoryListener(this);
    }

    public void onHistoryChanged(String historyToken) {
	// This method is called whenever the application's history changes.
	// Set the label to reflect the current history token.
	lbl.setText("The current history token is: " + historyToken);
    }

    public Widget getWidget() { return widget; }
}

// End of file.