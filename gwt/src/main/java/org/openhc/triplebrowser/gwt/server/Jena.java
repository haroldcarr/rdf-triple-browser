//
// Created       : 2006 Jul 28 (Fri) 14:21:09 by Harold Carr.
// Last Modified : 2006 Oct 07 (Sat) 15:57:11 by Harold Carr.
//

package com.differentity.server;

import java.io.FileInputStream;
import java.io.IOException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import com.differentity.client.Main;

public class Jena
{
    public final String NOWHERE = "http://nowhere/";
    public final String RDF_XML = "RDF/XML";

    Model model;

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
	Resource subject  = model.createResource(s);
	Property property = model.createProperty(p);
	Resource value    = model.createResource(v);
	subject.addProperty(property, value);
    }

    public QueryReturnValue doQuery(String subject, 
				    String property, String value)
    {
	subject  = formatInput(subject);
	property = formatInput(property);
	value    = formatInput(value);
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

	if (selectVars.equals("")) {
	    selectVars = "?ddduuummmyyy";
	}

	String queryString =
	    " SELECT " + selectVars +
	    " WHERE { " +
	    subject  + " " +
	    property + " " +
	    value    + " . }";

	return doQuery(queryString);
    }

    public QueryReturnValue doQuery(final String queryString)
    {
	Query query = QueryFactory.create(queryString);
	QueryExecution queryExecution = 
	    QueryExecutionFactory.create(query, model);
	ResultSet resultSet = queryExecution.execSelect();
	return new QueryReturnValue(queryString, queryExecution, resultSet);
    }

    private String formatInput(final String x)
    {
	if (x.startsWith("?")) {
	    return x;
	}
	return "<" + x + ">";
    }

    class QueryReturnValue
    {
	private String         queryString;
	private QueryExecution queryExecution;
	private ResultSet      resultSet;
	QueryReturnValue(final String         queryString,
			 final QueryExecution queryExecution,
			 final ResultSet      resultSet)
	{
	    this.queryString    = queryString;
	    this.queryExecution = queryExecution;
	    this.resultSet      = resultSet;
	}
	String         getQueryString()    { return queryString; }
	QueryExecution getQueryExecution() { return queryExecution; }
	ResultSet      getResultSet()      { return resultSet; }
    }
}

// End of file.
