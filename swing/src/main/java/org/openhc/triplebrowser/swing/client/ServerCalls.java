//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 14:05:57 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;
import org.openhc.trowser.gwt.server.ServiceImplDelegate;

import org.openhc.trowser.swing.client.Main;

public class ServerCalls
{
    final Main main;
    final ServiceImplDelegate service;

    public ServerCalls(Main main)
    {
	this.main = main;
	service = new ServiceImplDelegate();
    }

    public String openFile(final String filename)
    {
	final String result = service.openFile(filename);
	java.lang.System.out.println(result);
	// ***** : Need to check result - only reset/query on success.
	//main.getQueryPanel().reset(); // ***** TODO
	main.getQueryManager().doQuery();
	return result;
    }

    public void doQuery(final QueryRequest queryRequest)
    {
	final QueryResponse queryResponse = service.doQuery(queryRequest);
	main.getQueryManager().handleQueryResponse(queryResponse);
    }

    public void assertFact(final QueryRequest queryRequest)
    {
	final QueryResponse queryResponse = service.assertFact(queryRequest);
	main.getQueryManager().handleQueryResponse(queryResponse);
    }

    public void close()
    {
	service.close();
    }
}

// End of file.

