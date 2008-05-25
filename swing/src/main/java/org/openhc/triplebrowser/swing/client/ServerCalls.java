//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 19:34:59 by Harold Carr.
//

package client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import client.Main;

public class ServerCalls
{
    final ServiceImpl service;

    public ServerCalls()
    {
	service = new ServiceImpl();
    }

    public String initialize()
    {
	final String result = service.initialize();
	java.lang.System.out.println(result);
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

