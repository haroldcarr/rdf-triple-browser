//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Aug 13 (Sun) 20:31:42 by Harold Carr.
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

    public String initialize(String notUsed) 
    {
	if (initialized) {
	    return "already initialized";
	}
	
	jena = new Jena();
	try {
	    jena.readRDF("all.rdf");
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

    private boolean firstTime = false;

    public QueryResponse doQuery(QueryRequest queryRequest)
    {
	if (firstTime) {
	    firstTime = false;
	    return doQueryTest(queryRequest);
	}
	QueryResults queryResults = 
	    jena.doQuery(queryRequest.getSubject(),
			 queryRequest.getVerb(),
			 queryRequest.getObject());

	return makeResponse(queryRequest, queryResults);
    }

    private QueryResponse makeResponse(QueryRequest queryRequest,
				       QueryResults queryResults)
    {
	String status =
	    " Service: " +
	    "[subject " + queryRequest.getSubject() + "]" +
	    "[verb "    + queryRequest.getVerb()    + "]" +
	    "[object "  + queryRequest.getObject()  + "]" + "<br/>" +
	    Jena.lastQueryString;
	/*
	List first  = new ArrayList();
	List second = new ArrayList();
	List third  = new ArrayList();
	first.add(queryRequest.getSubject());
	second.add(queryRequest.getVerb());
	third.add(queryRequest.getObject());

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
	boolean isSubjectVar = false;
	boolean isVerbVar = false;
	boolean isObjectVar = false;
	List list = queryResults.getResultVars();
	Iterator i = list.iterator();
	while (i.hasNext()) {
	    String varName = (String) i.next();
	    if (varName.equals(Main.subject)) {
		isSubjectVar = true;
	    } else if (varName.equals(Main.verb)) {
		isVerbVar = true;
	    } else if (varName.equals(Main.object)) {
		isObjectVar = true;
	    }
	}
	List subjectResponse = new ArrayList();
	List verbResponse    = new ArrayList();
	List objectResponse  = new ArrayList();
	if (!isSubjectVar) { subjectResponse.add(queryRequest.getSubject()); }
	if (!isVerbVar)    { verbResponse.add   (queryRequest.getVerb()); }
	if (!isObjectVar)  { objectResponse.add (queryRequest.getObject()); }
	while (queryResults.hasNext()) {
	    ResultBinding resultBinding = (ResultBinding) queryResults.next();
	    if (isSubjectVar) { 
		String x = resultBinding.get(Main.subject).toString();
		if (! subjectResponse.contains(x)) {
		    subjectResponse.add(x);
		}
	    }
	    if (isVerbVar) {
		String x = resultBinding.get(Main.verb).toString();
		if (! verbResponse.contains(x)) {
		    verbResponse.add(x);
		}
	    }
	    if (isObjectVar) {
		String x = resultBinding.get(Main.object).toString();
		if (! objectResponse.contains(x)) {
		    objectResponse.add(x);
		}
	    }
	}
	return new QueryResponse(subjectResponse, verbResponse, objectResponse,
				 status);
    }

    ////////////////////////////////////////////////////
    //
    // Test.
    //

    private QueryResponse doQueryTest(QueryRequest queryRequest)
    {
	List subjectList = (List) svoList.clone();
	List verbList    = (List) svoList.clone();
	List objectList  = (List) svoList.clone();

	subjectList.add(0, "http://subject.com/");
	subjectList.add(0, queryRequest.getSubject());
	verbList.add(0, "http://verb.com/");
	verbList.add(0, queryRequest.getVerb());
	objectList.add(0, "http://object.com/");
	objectList.add(0, queryRequest.getObject());

	return new QueryResponse(subjectList, verbList, objectList, "TEST");
    }

    private ArrayList svoList = new ArrayList();
    {
    svoList.add("http://haroldcarr.com");
    svoList.add("http://www.rojo.com/");
    svoList.add("http://google.com");
    svoList.add("http://del.icio.us/");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/authorPrefix");
    svoList.add("http://differentity.com/haroldcarr/authorFirstName");
    svoList.add("http://differentity.com/haroldcarr/authorMiddleName");
    svoList.add("http://differentity.com/haroldcarr/authorLastName");
    svoList.add("http://differentity.com/haroldcarr/authorSuffix");
    svoList.add("http://differentity.com/haroldcarr/authorBorn");
    svoList.add("http://differentity.com/haroldcarr/authorDied");
    svoList.add("http://differentity.com/haroldcarr/authorIdea");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/ideaInCategory");
    svoList.add("http://differentity.com/haroldcarr/work");
    svoList.add("http://differentity.com/haroldcarr/workAuthor");
    svoList.add("http://differentity.com/haroldcarr/workTitle");
    svoList.add("http://differentity.com/haroldcarr/workPublished");
    svoList.add("http://differentity.com/haroldcarr/workWritten");
    svoList.add("http://differentity.com/haroldcarr/bataille");
    svoList.add("http://differentity.com/haroldcarr/book");
    svoList.add("http://differentity.com/haroldcarr/guilty");
    svoList.add("http://differentity.com/haroldcarr/eroticism");
    svoList.add("http://differentity.com/haroldcarr/blue_of_noon");
    svoList.add("http://differentity.com/haroldcarr/inner_experience");

    svoList.add("http://differentity.com/haroldcarr/similarTo");
    svoList.add("http://differentity.com/haroldcarr/equalTo");
    svoList.add("http://differentity.com/haroldcarr/contraryTo");
    svoList.add("http://differentity.com/haroldcarr/contrarstWith");
    svoList.add("http://differentity.com/haroldcarr/relatesTo");
    svoList.add("http://differentity.com/haroldcarr/fate");
    svoList.add("http://differentity.com/haroldcarr/formlessness");
    svoList.add("http://differentity.com/haroldcarr/god");
    svoList.add("http://differentity.com/haroldcarr/stability");
    svoList.add("http://differentity.com/haroldcarr/reason");
    svoList.add("http://differentity.com/haroldcarr/nothingness");
    svoList.add("http://differentity.com/haroldcarr/chance");
    svoList.add("http://differentity.com/haroldcarr/strength");
    svoList.add("http://differentity.com/haroldcarr/death");
    svoList.add("http://differentity.com/haroldcarr/anguish");
    svoList.add("http://differentity.com/haroldcarr/silence");
    svoList.add("http://differentity.com/haroldcarr/limit");
    }
}

// End of file.
