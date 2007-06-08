//
// Created       : 2006 Jul 28 (Fri) 14:21:09 by Harold Carr.
// Last Modified : 2007 Jun 07 (Thu) 15:38:15 by Harold Carr.
//

package com.differentity.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import com.differentity.client.Main;
import com.differentity.client.QueryRequest;
import com.differentity.client.QueryResponse;

public class Jena
{
    public final String NOWHERE = "http://nowhere/";
    public final String RDF_XML = "RDF/XML";

    Model model;

    ////////////////////////////////////////////////////
    //
    // Generic Jena methods
    //

    public Jena()
    {
	model = ModelFactory.createDefaultModel();
    }

    public void close()
    {
	model.close();
    }

    public void readRDF(final String filename)
	throws IOException
    {
	readRDF(filename, RDF_XML);
    }

    public void readRDF(final String filename, final String format)
	throws IOException
    {
	FileInputStream in = null;
	try {
	    in = new FileInputStream(filename);
	    model.read(in, NOWHERE, format);
	} finally {
	    if (in != null) {
		try { in.close(); } catch (IOException e) {}
	    }
	}
    }

    public void assertFact(final String s, final String p, final String v)
    {
	final Resource subject  = model.createResource(s);
	final Property property = model.createProperty(p);
	final Resource value    = model.createResource(v);
	subject.addProperty(property, value);
    }

    public String makeQueryString(final String s, final String p, final String v)
    {
	final String subject  = formatInput(s);
	final String property = formatInput(p);
	final String value    = formatInput(v);
	String selectVars = "";
	if (subject.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + subject;
	}
	if (property.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + property;
	}
	if (value.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + value;
	}

	final String orderBy;
	if (selectVars.equals("")) {
	    selectVars = "?ddduuummmyyy";
	    orderBy = "";
	} else {
	    orderBy = " ORDER BY " + selectVars;
	}

	final String queryString =
	    " SELECT " + selectVars +
	    " WHERE { " +
	    subject  + " " +
	    property + " " +
	    value    + " . }" +
	    orderBy;

	return queryString;
    }


    private String formatInput(final String x)
    {
	if (x.startsWith("?")) {
	    return x;
	}
	return "<" + x + ">";
    }

    ////////////////////////////////////////////////////
    //
    // Differentity specific methods
    //

    public QueryResponse doQuery(final QueryRequest queryRequest,
				 final String servletContextRealPathOfSlash)
    {
	final String queryString =
	    makeQueryString(queryRequest.getSubject(),
			    queryRequest.getProperty(),
			    queryRequest.getValue());

	return doQuery(queryString, queryRequest,
		       servletContextRealPathOfSlash);
    }

    private QueryResponse doQuery(final String queryString,
				  final QueryRequest queryRequest,
				  final String servletContextRealPathOfSlash)
    {
	final Query query = QueryFactory.create(queryString);
	final QueryExecution queryExecution = 
	    QueryExecutionFactory.create(query, model);
	final ResultSet resultSet = queryExecution.execSelect();

	// This method is almost generic except for this method call:
	final QueryResponse queryResponse =
	    makeResponse(queryRequest, resultSet,
			 queryString, servletContextRealPathOfSlash);

	// VERY IMPORTANT: close Jena's query engine to release resources.
	queryExecution.close();

	return queryResponse;
    }

    private QueryResponse makeResponse(
        final QueryRequest queryRequest, final ResultSet resultSet,
	final String queryString, final String servletContextRealPathOfSlash)
    {
	//
	// Determine which parts of query were variables.
	//

	boolean isSubjectVar  = false;
	boolean isPropertyVar = false;
	boolean isValueVar    = false;
	final List list = resultSet.getResultVars();
	final Iterator i = list.iterator();
	while (i.hasNext()) {
	    final String varName = (String) i.next();
	    if (varName.equals(Main.subject)) {
		isSubjectVar  = true;
	    } else if (varName.equals(Main.property)) {
		isPropertyVar = true;
	    } else if (varName.equals(Main.value)) {
		isValueVar    = true;
	    }
	}

	final List subjectResponse  = new ArrayList();
	final List propertyResponse = new ArrayList();
	final List valueResponse    = new ArrayList();

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
	    final QuerySolution querySolution = resultSet.nextSolution();
	    if (isSubjectVar) { 
		final String x = querySolution.get(Main.subject).toString();
		if (! subjectResponse.contains(x)) {
		    subjectResponse.add(x);
		}
	    }
	    if (isPropertyVar) {
		final String x = querySolution.get(Main.property).toString();
		if (! propertyResponse.contains(x)) {
		    propertyResponse.add(x);
		}
	    }
	    if (isValueVar) {
		final String x = querySolution.get(Main.value).toString();
		if (! valueResponse.contains(x)) {
		    valueResponse.add(x);
		}
	    }
	}
	/*
	java.util.Collections.sort(subjectResponse);
	java.util.Collections.sort(propertyResponse);
	java.util.Collections.sort(valueResponse);
	*/

	final QueryResponse queryResponse = 
	    new QueryResponse(subjectResponse, propertyResponse,
			      valueResponse,
			      queryRequest.getSetContentsOf(), // Cookie
			      // Debug:
			      queryString + " " + servletContextRealPathOfSlash
			      );

	return queryResponse;
    }
}

// End of file.
