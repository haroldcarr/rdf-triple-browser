//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 12:59:55 by Harold Carr.
//

package org.openhc.trowser.gwt.server;

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

public class ServiceImplDelegate
{
    private boolean initialized = false;
    private Jena jena;

    public String loadData(final String data)
    {
	jena = new Jena();
	try {
	    jena.loadData(data);
	    initialized = true;
	} catch (Throwable t) {
	    return stackTraceToString(t);
	}

	return "data loaded successfully";
	}

    public String openFile(final String filename) 
    {
	jena = new Jena();
	try {
	    jena.readRDF(filename);
	    initialized = true;
	} catch (Throwable t) {
	    return stackTraceToString(t);
	}

	return filename + "read successfully";
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
	if (initialized) {
	    return jena.doQuery(queryRequest,
				"NOT NEEDED");
	}
	return null;
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

    private String stackTraceToString(Throwable t)
    {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	PrintWriter printWriter = new PrintWriter(stream);
	t.printStackTrace(printWriter);
	printWriter.flush();
	printWriter.close();
	return stream.toString();
    }
}

// End of file.
