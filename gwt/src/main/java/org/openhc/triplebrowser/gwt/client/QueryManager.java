//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 21:34:24 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame; // *****
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window; // *****

import com.differentity.client.Main;
import com.differentity.client.QueryRequest;
import com.differentity.client.Test; // *****


public class MainPanel
{
    private final SVOPanel subjectPanel;
    private final SVOPanel verbPanel;
    private final SVOPanel objectPanel;
    private final VerticalPanel subjectVerticalPanel;
    private final VerticalPanel verbVerticalPanel;
    private final VerticalPanel objectVerticalPanel;
    private final HorizontalPanel svoHorizontalPanel;
    private final DockPanel dockPanel;
    private final HTML north;
    private final HTML south;
    private final QueryPanel queryPanel;
    // TODO - there is a race here.
    private static final HTML statusHTML = new HTML();

    MainPanel() {

	//
	// QueryPanel created before SVO panels since needed.
	//

	queryPanel = new QueryPanel();

	//
	// Subject, Verb, Object panels.
	// Create now to get contents from server.
	//

	subjectPanel = new SVOPanel(Main.subject);
	verbPanel    = new SVOPanel(Main.verb);
	objectPanel  = new SVOPanel(Main.object);
	subjectVerticalPanel = subjectPanel.getPanel();
	verbVerticalPanel    = verbPanel.getPanel();
	objectVerticalPanel  = objectPanel.getPanel();
	svoHorizontalPanel = new HorizontalPanel();
	svoHorizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	svoHorizontalPanel.add(subjectVerticalPanel);
	svoHorizontalPanel.add(verbVerticalPanel);
	svoHorizontalPanel.add(objectVerticalPanel);

	doQuery();

	//
	// Main panel.
	//    

	dockPanel = new DockPanel();
	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	north = new HTML(Main.differentityDotCom, true);
	south = new HTML(Main.copyright, true);
	dockPanel.add(north, DockPanel.NORTH);

	dockPanel.add(queryPanel.getHorizontalPanel(), DockPanel.NORTH);
	// NOTE: - if SOUTH added after CENTER does not show up.
	dockPanel.add(south, DockPanel.SOUTH);
	dockPanel.add(svoHorizontalPanel, DockPanel.CENTER);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	RootPanel.get("slot1").add(statusHTML);
	RootPanel.get("slot2").add(dockPanel);

	// XXX - frame test
	RootPanel.get("slot3").add(new Frame("http://www.google.com/"));

	// XXX - test
	RootPanel.get("slot4"). add(new Test().getWidget());
    }

    public void doQuery()
    {
	String subject = 
	    getSVOQueryValue(Main.subject, queryPanel.getSubjectTextBox());
	String verb    = 
	    getSVOQueryValue(Main.verb,    queryPanel.getVerbTextBox());
	String object  = 
	    getSVOQueryValue(Main.object,  queryPanel.getObjectTextBox());

	QueryRequest queryRequest = new QueryRequest(subject, verb, object);

	// "this" so async request can call handleQueryResponse.
	Main.getServerCalls().doQuery(this, queryRequest);
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	subjectPanel.setContents(queryResponse.getSubject());
	verbPanel.setContents(queryResponse.getVerb());
	objectPanel.setContents(queryResponse.getObject());
	getStatusHTML().setHTML(queryResponse.getStatus());
    }

    public void svoLinkClicked(final String categoryAndURL)
    {
	// TODO: Send to server.  Receive updates for other panels.
	int i = categoryAndURL.indexOf(" ");
	String category = categoryAndURL.substring(0, i);
	String url = categoryAndURL.substring(i+1);
	if (category.equals(Main.subject)) {
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextBox().setText(url);
	} else if (category.equals(Main.verb)) {
	    Main.getMainPanel()
		.getQueryPanel().getVerbTextBox().setText(url);
	} else if (category.equals(Main.object)) {
            Main.getMainPanel()
		.getQueryPanel().getObjectTextBox().setText(url);
	} else {
	    // TODO: FIX
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextBox().setText("ERROR");
	    return;
	}
	doQuery();
    }

    private String getSVOQueryValue(String def, TextBox textBox)
    {
	String text = textBox.getText();
	if (text != null && (! text.equals(""))) {
	    return text;
	}
	return Main.questionMarkSymbol + def;
    }

    public QueryPanel getQueryPanel()   {return queryPanel;}
    public static HTML getStatusHTML()  {return statusHTML;}
}

// End of file.
