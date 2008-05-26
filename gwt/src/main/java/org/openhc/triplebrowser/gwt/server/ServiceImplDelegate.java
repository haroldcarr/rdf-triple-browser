//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 25 (Sun) 19:16:28 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;
import org.openhc.trowser.gwt.common.Triple;
import org.openhc.trowser.gwt.server.Jena;

import org.openhc.trowser.swing.client.Main;

public class ServiceImpl
{
    private boolean initialized = false;
    private Jena jena;

    private final String RDF_FILENAME = "rdf.rdf";

    public String initialize() 
    {
	if (initialized) {
	    return "already initialized";
	}
	
	jena = new Jena();
	try {
	    jena.readRDF(RDF_FILENAME);
	    initialized = true;
	} catch (Throwable t) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    PrintWriter printWriter = new PrintWriter(stream);
	    t.printStackTrace(printWriter);
	    printWriter.flush();
	    printWriter.close();
	    return stream.toString();
	}

	return "initialization complete";
    }

    public String close() 
    {
	if (! initialized) {
	    return "not initialized";
	}
	
	try {
	    jena.close();
	    jena = null;
	    initialized = false;
	} catch (Throwable t) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    PrintWriter printWriter = new PrintWriter(stream);
	    t.printStackTrace(printWriter);
	    printWriter.flush();
	    printWriter.close();
	    return stream.toString();
	}

	return "close complete";
    }

    public QueryResponse doQuery(final QueryRequest queryRequest)
    {
	return jena.doQuery(queryRequest,
			    "NOT NEEDED");
    }

    public QueryResponse assertFact(QueryRequest queryRequest)
    {
	// REVISIT - not general yet or needs error checking.
	Iterator i = queryRequest.getTriples().iterator();
	Triple triple = (Triple) i.next();
	final String subject  = triple.getSubject();
	final String property = triple.getProperty();
	final String value    = triple.getValue();
	jena.assertFact(subject, property, value);

	List subjectResponse  = new ArrayList();
	List propertyResponse = new ArrayList();
	List valueResponse    = new ArrayList();
	subjectResponse.add(subject);
	propertyResponse.add(property);
	valueResponse.add(value);
	return new QueryResponse(subjectResponse, propertyResponse,
				 valueResponse,
				 queryRequest.getSetContentsOf(), "OK");
    }
}

// End of file.
