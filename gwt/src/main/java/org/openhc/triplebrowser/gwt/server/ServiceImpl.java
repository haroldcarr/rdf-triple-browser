//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Sep 09 (Sat) 20:46:16 by Harold Carr.
//

package com.differentity.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;

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
	    return "already initialized" + " " + params();
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

	return "initialization complete" + " " + params();
    }

    public String close() 
    {
	if (! initialized) {
	    return "not initialized";
	}
	
	try {
	    jena.close();
	    jena = null;
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

    private String params()
    {
	String result = "Method: " + getThreadLocalRequest().getMethod();
	result += " URI: " + getThreadLocalRequest().getRequestURI();
	result += " Path: " + getThreadLocalRequest().getServletPath();
	result += " Parameters: ";
	java.util.Enumeration i = getThreadLocalRequest().getParameterNames();
	for(; i.hasMoreElements() ;) {
	    String name = (String)i.nextElement();
	    result += " " + name;
	    String value = getThreadLocalRequest().getParameter(name);
	    result += "=" + value;
	}
	return result;
    }

    private boolean firstTime = false;

    public QueryResponse doQuery(QueryRequest queryRequest)
    {
	if (firstTime) {
	    firstTime = false;
	    return doQueryTest(queryRequest);
	}
	QueryResults queryResults = 
	    jena.doQuery(queryRequest.getSubject(),
			 queryRequest.getProperty(),
			 queryRequest.getValue());

	return makeResponse(queryRequest, queryResults);
    }

    private QueryResponse makeResponse(QueryRequest queryRequest,
				       QueryResults queryResults)
    {
	String status =
	    " Service: " +
	    "[subject "  + queryRequest.getSubject()  + "]" +
	    "[property " + queryRequest.getProperty() + "]" +
	    "[value "    + queryRequest.getValue()    + "]" + "<br/>" +
	    Jena.lastQueryString;
	/*
	List first  = new ArrayList();
	List second = new ArrayList();
	List third  = new ArrayList();
	first.add(queryRequest.getSubject());
	second.add(queryRequest.getProperty());
	third.add(queryRequest.getValue());

	List list = queryResults.getResultVars();
	Iterator i = list.iterator();
	if (i.hasNext()) {
	    first.add(0, "http://one/" + i.next());
	}
	if (i.hasNext()) {
	    second.add(0, "http://two/" + i.next());
	}
	if (i.hasNext()) {
	    third.add(0, "http://three/" + i.next());
	}
	return new QueryResponse(first, second, third, status);
	*/
	boolean isSubjectVar  = false;
	boolean isPropertyVar = false;
	boolean isValueVar    = false;
	List list = queryResults.getResultVars();
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
	if (!isSubjectVar) {
	    subjectResponse.add(queryRequest.getSubject()); 
	}
	if (!isPropertyVar) {
	    propertyResponse.add(queryRequest.getProperty());
	}
	if (!isValueVar) {
	    valueResponse.add (queryRequest.getValue()); 
	}
	while (queryResults.hasNext()) {
	    ResultBinding resultBinding = (ResultBinding) queryResults.next();
	    if (isSubjectVar) { 
		String x = resultBinding.get(Main.subject).toString();
		if (! subjectResponse.contains(x)) {
		    subjectResponse.add(x);
		}
	    }
	    if (isPropertyVar) {
		String x = resultBinding.get(Main.property).toString();
		if (! propertyResponse.contains(x)) {
		    propertyResponse.add(x);
		}
	    }
	    if (isValueVar) {
		String x = resultBinding.get(Main.value).toString();
		if (! valueResponse.contains(x)) {
		    valueResponse.add(x);
		}
	    }
	}
	queryResults.close();
	return new QueryResponse(subjectResponse, propertyResponse,
				 valueResponse, status);
    }

    ////////////////////////////////////////////////////
    //
    // Test.
    //

    private QueryResponse doQueryTest(QueryRequest queryRequest)
    {
	List subjectList  = (List) spvList.clone();
	List propertyList = (List) spvList.clone();
	List valueList    = (List) spvList.clone();

	subjectList.add(0, "http://subject.com/");
	subjectList.add(0, queryRequest.getSubject());
	propertyList.add(0, "http://property.com/");
	propertyList.add(0, queryRequest.getProperty());
	valueList.add(0, "http://value.com/");
	valueList.add(0, queryRequest.getValue());

	return new QueryResponse(subjectList, propertyList, valueList, "TEST");
    }

    // TEST DATA
    private ArrayList spvList = new ArrayList();
    {
    spvList.add("http://haroldcarr.com");
    spvList.add("http://www.rojo.com/");
    spvList.add("http://google.com");
    spvList.add("http://del.icio.us/");
    spvList.add("http://differentity.com/haroldcarr/author");
    spvList.add("http://differentity.com/haroldcarr/authorPrefix");
    spvList.add("http://differentity.com/haroldcarr/authorFirstName");
    spvList.add("http://differentity.com/haroldcarr/authorMiddleName");
    spvList.add("http://differentity.com/haroldcarr/authorLastName");
    spvList.add("http://differentity.com/haroldcarr/authorSuffix");
    spvList.add("http://differentity.com/haroldcarr/authorBorn");
    spvList.add("http://differentity.com/haroldcarr/authorDied");
    spvList.add("http://differentity.com/haroldcarr/authorIdea");
    spvList.add("http://differentity.com/haroldcarr/author");
    spvList.add("http://differentity.com/haroldcarr/ideaInCategory");
    spvList.add("http://differentity.com/haroldcarr/work");
    spvList.add("http://differentity.com/haroldcarr/workAuthor");
    spvList.add("http://differentity.com/haroldcarr/workTitle");
    spvList.add("http://differentity.com/haroldcarr/workPublished");
    spvList.add("http://differentity.com/haroldcarr/workWritten");
    spvList.add("http://differentity.com/haroldcarr/bataille");
    spvList.add("http://differentity.com/haroldcarr/book");
    spvList.add("http://differentity.com/haroldcarr/guilty");
    spvList.add("http://differentity.com/haroldcarr/eroticism");
    spvList.add("http://differentity.com/haroldcarr/blue_of_noon");
    spvList.add("http://differentity.com/haroldcarr/inner_experience");

    spvList.add("http://differentity.com/haroldcarr/similarTo");
    spvList.add("http://differentity.com/haroldcarr/equalTo");
    spvList.add("http://differentity.com/haroldcarr/contraryTo");
    spvList.add("http://differentity.com/haroldcarr/contrarstWith");
    spvList.add("http://differentity.com/haroldcarr/relatesTo");
    spvList.add("http://differentity.com/haroldcarr/fate");
    spvList.add("http://differentity.com/haroldcarr/formlessness");
    spvList.add("http://differentity.com/haroldcarr/god");
    spvList.add("http://differentity.com/haroldcarr/stability");
    spvList.add("http://differentity.com/haroldcarr/reason");
    spvList.add("http://differentity.com/haroldcarr/nothingness");
    spvList.add("http://differentity.com/haroldcarr/chance");
    spvList.add("http://differentity.com/haroldcarr/strength");
    spvList.add("http://differentity.com/haroldcarr/death");
    spvList.add("http://differentity.com/haroldcarr/anguish");
    spvList.add("http://differentity.com/haroldcarr/silence");
    spvList.add("http://differentity.com/haroldcarr/limit");
    }
}

// End of file.
