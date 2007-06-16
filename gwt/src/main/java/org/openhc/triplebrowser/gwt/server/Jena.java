//
// Created       : 2006 Jul 28 (Fri) 14:21:09 by Harold Carr.
// Last Modified : 2007 Jun 15 (Fri) 23:32:11 by Harold Carr.
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
import com.differentity.client.Triple;

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

    public String makeQueryString(final QueryRequest queryRequest)
    {
	StringBuffer selectVars = new StringBuffer();
	StringBuffer query = new StringBuffer();
	for (Iterator i = queryRequest.getTriples().iterator(); i.hasNext();){
	    Triple triple = (Triple) i.next();
	    final String subject  = formatInput(triple.getSubject());
	    final String property = formatInput(triple.getProperty());
	    final String value    = formatInput(triple.getValue());
	    if (subject.startsWith(Main.questionMarkSymbol)) {
		selectVars.append(" ").append(subject);
	    }
	    if (property.startsWith(Main.questionMarkSymbol)) {
		selectVars.append(" ").append(property);
	    }
	    if (value.startsWith(Main.questionMarkSymbol)) {
		selectVars.append(" ").append(value);
	    }
	    query.append(subject).append(" ")
		 .append(property).append(" ")
		 .append(value).append(" . ");
	}

	String selectVariables = selectVars.toString();
	if (selectVariables.equals("")) {
	    selectVariables = "?ddduuummmyyy";
	}

	final String queryString =
	    " SELECT " + selectVariables +
	    " WHERE { " +
	    query.toString() +
	    " }";

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
	final String queryString = makeQueryString(queryRequest);

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
	    // REVISIT: limits variables to "subject*"/"property*"/"value*"
	    final String varName = (String) i.next();
	    if (varName.startsWith(Main.subject)) {
		isSubjectVar  = true;
	    } else if (varName.startsWith(Main.property)) {
		isPropertyVar = true;
	    } else if (varName.startsWith(Main.value)) {
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

	Iterator triples = queryRequest.getTriples().iterator();
	while (triples.hasNext()) {
	    Triple triple = (Triple) triples.next();
	    if (!isSubjectVar) {
		subjectResponse.add(triple.getSubject()); 
	    }
	    if (!isPropertyVar) {
		propertyResponse.add(triple.getProperty());
	    }
	    if (!isValueVar) {
		valueResponse.add(triple.getValue()); 
	    }
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

	sort(subjectResponse);
	sort(propertyResponse);
	sort(valueResponse);

	final QueryResponse queryResponse = 
	    new QueryResponse(subjectResponse, propertyResponse,
			      valueResponse,
			      queryRequest.getSetContentsOf(), // Cookie
			      // Debug:
			      queryString + " " + servletContextRealPathOfSlash
			      );

	return queryResponse;
    }

    private void sort(final List list)
    {
	// In alphabetic order - including URL.
	java.util.Collections.sort(list);

	// But do not start with numbers, put them last (if possible).

	try {
	    System.out.println("-------------------------");
	    while ( xxx(list.get(0),
			list.get(list.size()-1)) ) {
		Object x = list.get(0);
		list.remove(0);
		list.add(x);
	    }
	} catch (Throwable t) {
	    System.out.println(t);
	}
    }

    private boolean xxx(Object x, Object y)
    {
	System.out.println(x + " " + y);
	boolean xb = startsWithDigit((String)x);
	boolean yb = startsWithDigit((String)y);
	System.out.println(xb + " " + yb);
	return xb && !yb;
    }

    private boolean startsWithDigit(String x)
    {
	// REVISIT - unicode
	return 
	   Character.isDigit(substringAfterLastSlashOrFirstSharp(x).charAt(0));
    }

    // REVISIT - factor into shared utility.
    private String substringAfterLastSlashOrFirstSharp(final String x)
    {
	int indexOfLastSlashOrFirstSharp = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/' || x.charAt(i) == '#') {
		indexOfLastSlashOrFirstSharp = i;
	    }
	}
	final String result = x.substring(indexOfLastSlashOrFirstSharp + 1);
	// If it ends in a slash then remove the ending slash and try again.
	if (result.length() == 0) {
	    return substringAfterLastSlashOrFirstSharp(
                       x.substring(0, x.length()-1));
	} else {
	    return result;
	}
    }

}

// End of file.
