//
// Created       : 2006 Jul 28 (Fri) 14:21:09 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 22:24:34 by Harold Carr.
//

package com.differentity.server;

import java.io.FileInputStream;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryResults;

import com.differentity.client.Main;

public class Jena
{
    public final String NOWHERE = "http://nowhere/";
    public final String RDF_XML = "RDF/XML";
    public static String lastQueryString; // for debug

    Model model;

    public Jena()
    {
	model = ModelFactory.createDefaultModel();
    }

    public void readRDF(String filename)
	throws IOException
    {
	readRDF(filename, RDF_XML);
    }

    public void readRDF(String filename, String format)
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

    public QueryResults doQuery(String s, String v, String o)
    {
	String subject = formatInput(s);
	String verb    = formatInput(v);
	String object  = formatInput(o);
	String selectVars = "";
	if (subject.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + subject;
	}
	if (verb.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + verb;
	}
	if (object.startsWith(Main.questionMarkSymbol)) {
	    selectVars = selectVars + " " + object;
	}

	String queryString =
	    " SELECT " + selectVars +
	    " WHERE ( " +
	    subject + " " +
	    verb    + " " +
	    object  + " )";

	// Spent a lot of time here trying to figure out why URIs
	// disappeared in the status output.
	// It's simple: because they look like "<URI>" - when
	// that is put in an HTML element it disappears as an unknown tag.
	// TODO: make it visible - either "ampersand" it our change
	// status area from HTML to TextBox.

	lastQueryString =
	    " Jena: " +
	    "[subject " + s + " / " + subject + "]" +
	    "[verb "    + v + " / " + verb    + "]" +
	    "[object "  + o + " / " + object  + "]" + "<br/>" +
	    queryString;

	return doQuery(queryString);
    }

    public QueryResults doQuery(String queryString)
    {
	Query query = new Query(queryString);
	query.setSource(model);
	QueryEngine qe = new QueryEngine(query);
	return qe.exec();
    }

    private String formatInput(String x)
    {
	if (x.startsWith("?")) {
	    return x;
	}
	return "<" + x + ">";
    }
}

// End of file.
