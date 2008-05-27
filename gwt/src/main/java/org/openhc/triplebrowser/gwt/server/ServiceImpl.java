//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 17:20:02 by Harold Carr.
//

package org.openhc.trowser.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.openhc.trowser.gwt.client.Service;
import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;
import org.openhc.trowser.gwt.common.Triple;

public class ServiceImpl
    extends RemoteServiceServlet
    implements Service
{
    // ***** TODO
    public static final ServiceImplDelegate serviceImplDelegate =
	new ServiceImplDelegate();

    public String openFile(final String filename) 
    {
	return serviceImplDelegate.openFile(filename);
    }

    public String close() 
    {
	return serviceImplDelegate.close();
    }

    public QueryResponse doQuery(final QueryRequest queryRequest)
    {
	return serviceImplDelegate.doQuery(queryRequest);
    }

    public QueryResponse assertFact(QueryRequest queryRequest)
    {
	return serviceImplDelegate.assertFact(queryRequest);
    }
}

// End of file.
