//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:49:18 by carr.
//

package org.openhc.triplebrowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

import org.openhc.triplebrowser.gwt.common.QueryRequest;
import org.openhc.triplebrowser.gwt.common.QueryResponse;
import org.openhc.triplebrowser.gwt.common.Triple;

public class QueryManager
{
    private final Main main;

    QueryManager(Main main)
    {
	this.main = main;
    }

    public void doQuery()
    {
	List triples = new ArrayList();
	Iterator hpi = main.getQueryPanel().getPanel().iterator();
	while (hpi.hasNext()) {
	    HorizontalPanel triple = (HorizontalPanel) hpi.next();
	    Iterator i = triple.iterator();
	    i.next(); // skip Button;
	    i.next(); // skip RadioButton;
	    i.next(); // skip subject MenuBar
	    final String subject  =
		getSPVQueryValue(main.qsubject,  (TextBox) i.next());
	    i.next(); // skip property MenuBar
	    final String property =
		getSPVQueryValue(main.qproperty, (TextBox) i.next());
	    i.next(); // skip value MenuBar
	    final String value    =
		getSPVQueryValue(main.qvalue,    (TextBox) i.next());
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
	    main.getQueryPanel().getSubjectTextBox().setText(url);
	} else if (category.equals(main.property)) {
	    main.getQueryPanel().getPropertyTextBox().setText(url);
	} else if (category.equals(main.value)) {
            main.getQueryPanel().getValueTextBox().setText(url);
	} else {
	    // TODO: FIX
	    main.getQueryPanel().getSubjectTextBox().setText("ERROR");
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
}

// End of file.
