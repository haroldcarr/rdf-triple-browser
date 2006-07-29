//
// Created       : 2006 Jul 28 (Fri) 14:21:09 by Harold Carr.
// Last Modified : 2006 Jul 29 (Sat) 09:20:00 by Harold Carr.
//

package com.differentity.server;

import java.io.FileInputStream;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryResults;

public class Jena
{
    public final String NOWHERE = "http://nowhere/";
    public final String RDF_XML = "RDF/XML";

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

    public QueryResults doQuery(String queryString)
    {
	Query query =new Query(queryString);
	query.setSource(model);
	QueryEngine qe = new QueryEngine(query);
	return qe.exec();
    }
}

// End of file.
