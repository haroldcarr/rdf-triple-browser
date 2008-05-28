//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 10:32:34 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.awt.BorderLayout;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openhc.trowser.gwt.common.Triple;
import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;

import org.openhc.trowser.swing.client.Main;

public class MainPanel
{
    private final Main       main;
    private final QueryPanel queryPanel;
    private final SPVPanel   spvPanel;
    private final WebBrowser browserPanel;

    MainPanel(Main main) 
    {
	this.main = main;

	//
	// QueryPanel created before SPV panels since needed.
	//

	queryPanel = new QueryPanel(main);

	//
	// SPVPanel
	//

	spvPanel = new SPVPanel(main);

	//
	// Main panel.
	//    

	if (main.operatingSystemName.startsWith("Windows")) {
	    browserPanel = WebBrowser.create("DJNATIVESWING");
	} else {
	    browserPanel = WebBrowser.create("TEXTAREA");
	}

	main.getTrowserLayout().mainPanelLayout(
	    main.getTrowserLayout().getSwingMainPanel(),
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
		getSPVQueryValue(main.qsubject,  (JTextField) i.next());
	    i.next(); // skip property MenuBar
	    final String property = 
		getSPVQueryValue(main.qproperty, (JTextField) i.next());
	    i.next(); // skip value MenuBar
	    final String value    = 
		getSPVQueryValue(main.qvalue,    (JTextField) i.next());
	    triples.add(new Triple(subject, property, value));
	}

	doQuery(keepHistory, triples,
	        main.qsubject + main.qproperty + main.qvalue);
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
	main.getServerCalls().doQuery(this, queryRequest);
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	String setContentsOf = queryResponse.getSetContentsOf();
	if (setContentsOf.indexOf(main.qsubject)  != -1) {
	    getSPVPanel().getSubjectPanel()
		.setContents(queryResponse.getSubject());
	}
	if (setContentsOf.indexOf(main.qproperty) != -1) {
	    getSPVPanel().getPropertyPanel()
		.setContents(queryResponse.getProperty());
	}
	if (setContentsOf.indexOf(main.qvalue)    != -1) {
	    getSPVPanel().getValuePanel()
		.setContents(queryResponse.getValue());
	}
    }

    public void spvLinkClicked(final String category, final String url)
    {
	if (category.equals(main.subject)) {
	    main.getMainPanel()
		.getQueryPanel().getSubjectTextField().setText(url);
	} else if (category.equals(main.property)) {
	    main.getMainPanel()
		.getQueryPanel().getPropertyTextField().setText(url);
	} else if (category.equals(main.value)) {
            main.getMainPanel()
		.getQueryPanel().getValueTextField().setText(url);
	} else {
	    // TODO: FIX
	    main.getMainPanel()
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
