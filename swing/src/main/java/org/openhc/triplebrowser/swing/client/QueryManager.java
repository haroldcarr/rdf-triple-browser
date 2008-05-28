//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 14:05:49 by Harold Carr.
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

public class QueryManager
{
    private final Main       main;

    QueryManager(Main main) 
    {
	this.main = main;
    }

    public void doQuery()
    {
	List triples = new ArrayList();
	Iterator hpi = 
	    Arrays.asList(
                main.getQueryPanel().getPanel().getComponents()).iterator();
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

	doQuery(triples, main.qsubject + main.qproperty + main.qvalue);
    }

    public void doQuery(final String subject, final String property,
			final String value, final String setContentsOf)
    {
	List triples = new ArrayList();
	triples.add(new Triple(subject, property, value));
	doQuery(triples, setContentsOf);
    }

    public void doQuery(final List triples, final String setContentsOf)
    {
	QueryRequest queryRequest = new QueryRequest(triples, setContentsOf);

	// "this" so async request can call handleQueryResponse.
	main.getServerCalls().doQuery(queryRequest);
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	String setContentsOf = queryResponse.getSetContentsOf();
	if (setContentsOf.indexOf(main.qsubject)  != -1) {
	    main.getSPVPanel().getSubjectPanel()
		.setContents(queryResponse.getSubject());
	}
	if (setContentsOf.indexOf(main.qproperty) != -1) {
	    main.getSPVPanel().getPropertyPanel()
		.setContents(queryResponse.getProperty());
	}
	if (setContentsOf.indexOf(main.qvalue)    != -1) {
	    main.getSPVPanel().getValuePanel()
		.setContents(queryResponse.getValue());
	}
    }

    public void spvLinkClicked(final String category, final String url)
    {
	if (category.equals(main.subject)) {
	    main.getQueryPanel().getSubjectTextField().setText(url);
	} else if (category.equals(main.property)) {
	    main.getQueryPanel().getPropertyTextField().setText(url);
	} else if (category.equals(main.value)) {
            main.getQueryPanel().getValueTextField().setText(url);
	} else {
	    // TODO: FIX
	    main.getQueryPanel().getSubjectTextField().setText("ERROR");
	    return;
	}
	doQuery();
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
}

// End of file.
