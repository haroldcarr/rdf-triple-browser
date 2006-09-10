//
// Created       : 2006 Sep 04 (Mon) 10:50:21 by Harold Carr.
// Last Modified : 2006 Sep 04 (Mon) 22:21:45 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DevTime
{
    public static final String CLOSE_JENA = "Close Jena";
    // TODO - there is a race here.
    private static final HTML queryStatusHTML = new HTML();
    private static final HTML jenaStatusHTML  = new HTML();

    public static Widget makeStatusWidgets()
    {
	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.add(closeJenaButton());
	verticalPanel.add(jenaStatusHTML);
	verticalPanel.add(queryStatusHTML);
	return verticalPanel;
    }
    public static HTML getJenaStatusHTML()  { return jenaStatusHTML; }
    public static HTML getQueryStatusHTML() { return queryStatusHTML; }

    private static Button closeJenaButton()
    {
	Button button  = new Button(CLOSE_JENA);
	button.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		Main.getServerCalls().close();
	    }
	});
	return button;
    }
}

// End of file.

