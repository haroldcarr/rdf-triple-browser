//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 27 (Tue) 08:51:23 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.awt.BorderLayout;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.openhc.trowser.gwt.common.Triple;
import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;

import org.openhc.trowser.swing.client.Main;

public class MainPanel
{
    private final QueryPanel queryPanel;
    private final SPVPanel   spvPanel;
    private final WebBrowser browserPanel;

    MainPanel() 
    {
	//
	// QueryPanel created before SPV panels since needed.
	//

	queryPanel = new QueryPanel();

	//
	// SPVPanel
	//

	spvPanel = new SPVPanel();

	//
	// Main panel.
	//    

	if (Main.OS_NAME.startsWith("Windows")) {
	    browserPanel = WebBrowser.create("DJNATIVESWING");
	} else {
	    browserPanel = WebBrowser.create("TEXTAREA");
	}

	Main.getSwingView().mainPanelLayout(
	    Main.getSwingView().getSwingMainPanel(),
	    queryPanel.getPanel(),
	    spvPanel.getPanel(),
	    browserPanel);
    }

    public void doQuery(final boolean keepHistory)
    {
	List triples = new ArrayList();
	Iterator hpi = 
	    Arrays.asList(queryPanel.getPanel().getComponents()).iterator();
	while (hpi.hasNext()) {
	    JPanel triple = (JPanel) hpi.next();
	    Iterator i = Arrays.asList(triple.getComponents()).iterator();
	    i.next(); // skip RadioButton;
	    i.next(); // skip Button;
	    i.next(); // skip subject MenuBar
	    final String subject  = 
		getSPVQueryValue(Main.qsubject,  (JTextField) i.next());
	    i.next(); // skip property MenuBar
	    final String property = 
		getSPVQueryValue(Main.qproperty, (JTextField) i.next());
	    i.next(); // skip value MenuBar
	    final String value    = 
		getSPVQueryValue(Main.qvalue,    (JTextField) i.next());
	    triples.add(new Triple(subject, property, value));
	}

	doQuery(keepHistory, triples,
	        Main.qsubject + Main.qproperty + Main.qvalue);
    }

    public void doQuery(final boolean keepHistory,
			final String subject, final String property,
			final String value, final String setContentsOf)
    {
	List triples = new ArrayList();
	triples.add(new Triple(subject, property, value));
	doQuery(keepHistory, triples, setContentsOf);
    }

    public void doQuery(final boolean keepHistory, final List triples,
			final String setContentsOf)
    {
	QueryRequest queryRequest = new QueryRequest(triples, setContentsOf);

	// "this" so async request can call handleQueryResponse.
	Main.getServerCalls().doQuery(this, queryRequest);
	/*
	Main.getBrowserHistory()
	    .recordDoQuery(keepHistory, triples, setContentsOf);
	*/
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	String setContentsOf = queryResponse.getSetContentsOf();
	if (setContentsOf.indexOf(Main.qsubject)  != -1) {
	    getSPVPanel().getSubjectPanel()
		.setContents(queryResponse.getSubject());
	}
	if (setContentsOf.indexOf(Main.qproperty) != -1) {
	    getSPVPanel().getPropertyPanel()
		.setContents(queryResponse.getProperty());
	}
	if (setContentsOf.indexOf(Main.qvalue)    != -1) {
	    getSPVPanel().getValuePanel()
		.setContents(queryResponse.getValue());
	}
	//topPanel.pack(); // ***** REVISIT
	//DevTime.getQueryStatusHTML().setHTML(queryResponse.getStatus());
    }

    public void spvLinkClicked(final String category, final String url)
    {
	if (category.equals(Main.subject)) {
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextField().setText(url);
	} else if (category.equals(Main.property)) {
	    Main.getMainPanel()
		.getQueryPanel().getPropertyTextField().setText(url);
	} else if (category.equals(Main.value)) {
            Main.getMainPanel()
		.getQueryPanel().getValueTextField().setText(url);
	} else {
	    // TODO: FIX
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextField().setText("ERROR");
	    return;
	}
	doQuery(true);
    }

    //
    // The default is only there in case user puts in a blank string.
    // The system will never do that.
    //
    private String getSPVQueryValue(final String def, final JTextField textBox)
    {
	String text = textBox.getText();
	if (text != null && (! text.equals(""))) {
	    return text;
	}
	return def;
    }

    public QueryPanel getQueryPanel()   { return queryPanel; }
    public SPVPanel   getSPVPanel()     { return spvPanel; }
    public WebBrowser getBrowserPanel() { return browserPanel; }
}

// End of file.
