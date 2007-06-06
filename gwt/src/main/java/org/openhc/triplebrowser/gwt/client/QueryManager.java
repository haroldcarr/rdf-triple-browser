//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2007 Jun 05 (Tue) 18:22:46 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window; // *****

import com.differentity.client.BrowserHistory;
import com.differentity.client.Main;
import com.differentity.client.QueryRequest;
import com.differentity.client.Test; // *****


public class MainPanel
{
    private final Label responseProgressLabel;
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
    private final Frame frameCurrentSelection;

    MainPanel() {

	responseProgressLabel = new Label();

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

	//
	// Main panel.
	//    

	// ** RootPanel.get("slot0").add(DevTime.makeStatusWidgets());

	RootPanel.get("slot1").add(responseProgressLabel);

	dockPanel = new DockPanel();
	RootPanel.get("slot2").add(dockPanel);

	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	north = new HTML(Main.differentityDotCom, true);
	dockPanel.add(north, DockPanel.NORTH);

	dockPanel.add(queryPanel.getPanel(), DockPanel.NORTH);

	dockPanel.add(spvHorizontalPanel, DockPanel.CENTER);

	// NOTE: - Seem to need to add most southern first.
	south = new HTML(Main.copyright, true);
	dockPanel.add(south, DockPanel.SOUTH);

	frameCurrentSelection = new Frame("http://openhc.org/");
	frameCurrentSelection.setPixelSize(1000, 300);
	RootPanel.get("slot3").add(frameCurrentSelection);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	// XXX - test
	//RootPanel.get("slotXXX").add(new Test().getWidget());
    }

    public void doQuery(boolean keepHistory)
    {
	String subject = 
	    getSPVQueryValue(Main.qsubject,  queryPanel.getSubjectTextBox());
	String property    = 
	    getSPVQueryValue(Main.qproperty, queryPanel.getPropertyTextBox());
	String value  = 
	    getSPVQueryValue(Main.qvalue,    queryPanel.getValueTextBox());
	doQuery(keepHistory,
		subject, property, value, 
		Main.qsubject + Main.qproperty + Main.qvalue);
    }

    public void doQuery(final boolean keepHistory,
			final String subject, final String property, 
			final String value, final String setContentsOf)
    {
	QueryRequest queryRequest = 
	    new QueryRequest(subject, property, value, setContentsOf);

	// "this" so async request can call handleQueryResponse.
	Main.getServerCalls().doQuery(this, queryRequest);

	Main.getBrowserHistory()
	    .recordDoQuery(keepHistory, 
			   subject, property, value, setContentsOf);
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	String setContentsOf = queryResponse.getSetContentsOf();
	if (setContentsOf.indexOf(Main.qsubject)  != -1) {
	    subjectPanel .setContents(queryResponse.getSubject());
	}
	if (setContentsOf.indexOf(Main.qproperty) != -1) {
	    propertyPanel.setContents(queryResponse.getProperty());
	}
	if (setContentsOf.indexOf(Main.qvalue)    != -1) {
	    valuePanel   .setContents(queryResponse.getValue());
	}
	DevTime.getQueryStatusHTML().setHTML(queryResponse.getStatus());
    }

    public void spvLinkClicked(final String category, final String url)
    {
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
	doQuery(true);
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

    public Label      getResponseProgressLabel()
        { return responseProgressLabel; }
    public QueryPanel getQueryPanel()    { return queryPanel; }
    public SPVPanel   getSubjectPanel()  { return subjectPanel; }
    public SPVPanel   getPropertyPanel() { return propertyPanel; }
    public SPVPanel   getValuePanel()    { return valuePanel; }
    public Frame      getFrameCurrentSelection() 
                                         { return frameCurrentSelection; }
}

// End of file.
