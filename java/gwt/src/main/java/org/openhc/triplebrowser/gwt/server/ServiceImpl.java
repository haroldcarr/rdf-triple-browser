//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:57:10 by carr.
//

package org.openhc.triplebrowser.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.openhc.triplebrowser.gwt.client.Service;
import org.openhc.triplebrowser.gwt.common.QueryRequest;
import org.openhc.triplebrowser.gwt.common.QueryResponse;
import org.openhc.triplebrowser.gwt.common.Triple;

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
