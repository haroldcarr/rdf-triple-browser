//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 04 (Mon) 22:20:05 by Harold Carr.
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
    private final SPVPanel subjectPanel;
    private final SPVPanel propertyPanel;
    private final SPVPanel valuePanel;
    private final VerticalPanel subjectVerticalPanel;
    private final VerticalPanel propertyVerticalPanel;
    private final VerticalPanel valueVerticalPanel;
    private final HorizontalPanel spvHorizontalPanel;
    private final DockPanel dockPanel;
    private final HTML north;
    private final HTML south;
    private final QueryPanel queryPanel;

    MainPanel() {

	//
	// QueryPanel created before SPV panels since needed.
	//

	queryPanel = new QueryPanel();

	//
	// Subject, Property, Value panels.
	// Create now to get contents from server.
	//

	subjectPanel  = new SPVPanel(Main.subject);
	propertyPanel = new SPVPanel(Main.property);
	valuePanel    = new SPVPanel(Main.value);
	subjectVerticalPanel  = subjectPanel.getPanel();
	propertyVerticalPanel = propertyPanel.getPanel();
	valueVerticalPanel    = valuePanel.getPanel();
	spvHorizontalPanel = new HorizontalPanel();
	spvHorizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	spvHorizontalPanel.add(subjectVerticalPanel);
	spvHorizontalPanel.add(propertyVerticalPanel);
	spvHorizontalPanel.add(valueVerticalPanel);

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
	dockPanel.add(spvHorizontalPanel, DockPanel.CENTER);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	RootPanel.get("slot1").add(DevTime.makeStatusWidgets());
	RootPanel.get("slot2").add(dockPanel);

	// XXX - frame test
	RootPanel.get("slot3").add(new Frame("http://www.google.com/"));

	// XXX - test
	RootPanel.get("slot4"). add(new Test().getWidget());
    }

    public void doQuery()
    {
	String subject = 
	    getSPVQueryValue(Main.qsubject,  queryPanel.getSubjectTextBox());
	String property    = 
	    getSPVQueryValue(Main.qproperty, queryPanel.getPropertyTextBox());
	String value  = 
	    getSPVQueryValue(Main.qvalue,    queryPanel.getValueTextBox());

	QueryRequest queryRequest = new QueryRequest(subject, property, value);

	// "this" so async request can call handleQueryResponse.
	Main.getServerCalls().doQuery(this, queryRequest);
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	subjectPanel .setContents(queryResponse.getSubject());
	propertyPanel.setContents(queryResponse.getProperty());
	valuePanel   .setContents(queryResponse.getValue());
	DevTime.getQueryStatusHTML().setHTML(queryResponse.getStatus());
    }

    public void spvLinkClicked(final String categoryAndURL)
    {
	// TODO: Send to server.  Receive updates for other panels.
	int i = categoryAndURL.indexOf(" ");
	String category = categoryAndURL.substring(0, i);
	String url = categoryAndURL.substring(i+1);
	if (category.equals(Main.subject)) {
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextBox().setText(url);
	} else if (category.equals(Main.property)) {
	    Main.getMainPanel()
		.getQueryPanel().getPropertyTextBox().setText(url);
	} else if (category.equals(Main.value)) {
            Main.getMainPanel()
		.getQueryPanel().getValueTextBox().setText(url);
	} else {
	    // TODO: FIX
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextBox().setText("ERROR");
	    return;
	}
	doQuery();
    }

    //
    // The default is only there in case user puts in a blank string.
    // The system will never do that.
    //
    private String getSPVQueryValue(String def, TextBox textBox)
    {
	String text = textBox.getText();
	if (text != null && (! text.equals(""))) {
	    return text;
	}
	return def;
    }

    public QueryPanel getQueryPanel()  { return queryPanel; }
}

// End of file.
