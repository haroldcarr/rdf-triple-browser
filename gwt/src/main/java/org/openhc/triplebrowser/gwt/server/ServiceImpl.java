//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Oct 07 (Sat) 15:57:52 by Harold Carr.
//

package com.differentity.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import com.differentity.client.Main;
import com.differentity.client.QueryRequest;
import com.differentity.client.QueryResponse;
import com.differentity.client.Service;

import com.differentity.server.Jena;

public class ServiceImpl
    extends RemoteServiceServlet
    implements Service
{
    private boolean initialized = false;
    private Jena jena;

    private final String RDF_FILENAME = 
	"C:/cygwin/home/carr/ftptmp/gwt/differentity/all.rdf";

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
	Jena.QueryReturnValue returnValue = 
	    jena.doQuery(queryRequest.getSubject(),
			 queryRequest.getProperty(),
			 queryRequest.getValue());

	return makeResponse(queryRequest, returnValue);
    }

    private QueryResponse makeResponse(final QueryRequest queryRequest,
				       final Jena.QueryReturnValue returnValue)
    {
	ResultSet resultSet = returnValue.getResultSet();

	//
	// Determine which parts of query were variables.
	//

	boolean isSubjectVar  = false;
	boolean isPropertyVar = false;
	boolean isValueVar    = false;
	List list = resultSet.getResultVars();
	Iterator i = list.iterator();
	while (i.hasNext()) {
	    String varName = (String) i.next();
	    if (varName.equals(Main.subject)) {
		isSubjectVar  = true;
	    } else if (varName.equals(Main.property)) {
		isPropertyVar = true;
	    } else if (varName.equals(Main.value)) {
		isValueVar    = true;
	    }
	}

	List subjectResponse  = new ArrayList();
	List propertyResponse = new ArrayList();
	List valueResponse    = new ArrayList();

	//
	// For the parts of the query that are NOT variables
	// return what was given in the query.
	//

	if (!isSubjectVar) {
	    subjectResponse.add(queryRequest.getSubject()); 
	}
	if (!isPropertyVar) {
	    propertyResponse.add(queryRequest.getProperty());
	}
	if (!isValueVar) {
	    valueResponse.add (queryRequest.getValue()); 
	}

	//
	// For the parts of the query that ARE variables
	// return what was found.
	// 

	while (resultSet.hasNext()) {
	    QuerySolution querySolution = resultSet.nextSolution();
	    if (isSubjectVar) { 
		String x = querySolution.get(Main.subject).toString();
		if (! subjectResponse.contains(x)) {
		    subjectResponse.add(x);
		}
	    }
	    if (isPropertyVar) {
		String x = querySolution.get(Main.property).toString();
		if (! propertyResponse.contains(x)) {
		    propertyResponse.add(x);
		}
	    }
	    if (isValueVar) {
		String x = querySolution.get(Main.value).toString();
		if (! valueResponse.contains(x)) {
		    valueResponse.add(x);
		}
	    }
	}
	QueryResponse queryResponse = 
	    new QueryResponse(subjectResponse, propertyResponse,
			      valueResponse,
			      queryRequest.getSetContentsOf(),
			      returnValue.getQueryString());

	// VERY IMPORTANT: close Jena's query engine to release resources.
	returnValue.getQueryExecution().close();

	return queryResponse;
    }

    public QueryResponse assertFact(QueryRequest queryRequest)
    {
	jena.assertFact(queryRequest.getSubject(),
			queryRequest.getProperty(),
			queryRequest.getValue());

	List subjectResponse  = new ArrayList();
	List propertyResponse = new ArrayList();
	List valueResponse    = new ArrayList();
	subjectResponse.add(queryRequest.getSubject());
	propertyResponse.add(queryRequest.getProperty());
	valueResponse.add(queryRequest.getValue());
	return new QueryResponse(subjectResponse, propertyResponse,
				 valueResponse,
				 queryRequest.getSetContentsOf(), "OK");
    }
}

// End of file.
