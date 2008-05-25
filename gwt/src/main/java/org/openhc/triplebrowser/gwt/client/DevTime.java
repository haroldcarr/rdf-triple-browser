//
// Created       : 2006 Sep 04 (Mon) 10:50:21 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 20:42:57 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DevTime
{
    public static final String CLOSE_JENA = "Close Jena";
    // TODO - there is a race here.
    private static final Label browserHistoryLabel = new Label();
    private static final HTML jenaStatusHTML  = new HTML();
    private static final HTML queryStatusHTML = new HTML();

    public static Widget makeStatusWidgets()
    {
	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.add(closeJenaButton());
	verticalPanel.add(jenaStatusHTML);
	verticalPanel.add(queryStatusHTML);
	verticalPanel.add(browserHistoryLabel);
	return verticalPanel;
    }

    public static Label getBrowserHistoryLabel() { return browserHistoryLabel;}
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

