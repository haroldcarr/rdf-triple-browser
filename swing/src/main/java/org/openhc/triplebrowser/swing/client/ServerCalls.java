//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 08:57:06 by Harold Carr.
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
    final ServiceImplDelegate service;

    public ServerCalls()
    {
	service = new ServiceImplDelegate();
    }

    public String openFile(final String filename)
    {
	final String result = service.openFile(filename);
	java.lang.System.out.println(result);
	// ***** : Need to check result - only reset/query on success.
	//Main.getQueryPanel().reset(); // ***** TODO
	Main.getMainPanel().doQuery(true);
	return result;
    }

    public void doQuery(final MainPanel mainPanel,
			final QueryRequest queryRequest)
    {
	final QueryResponse queryResponse = service.doQuery(queryRequest);
	mainPanel.handleQueryResponse(queryResponse);
    }

    public void assertFact(final QueryRequest queryRequest)
    {
	final QueryResponse queryResponse = service.assertFact(queryRequest);
	Main.getMainPanel().handleQueryResponse(queryResponse);
    }

    public void close()
    {
	service.close();
    }
}

// End of file.

