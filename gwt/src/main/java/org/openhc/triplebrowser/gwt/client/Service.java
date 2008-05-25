//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 20:44:06 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import org.openhc.trowser.gwt.client.QueryRequest;
import org.openhc.trowser.gwt.client.QueryResponse;

import com.google.gwt.user.client.rpc.RemoteService;

public interface Service extends RemoteService 
{
    public String initialize();
    public QueryResponse doQuery(QueryRequest queryRequest);
    public QueryResponse assertFact(QueryRequest queryRequest);
    public String close();
}

// End of file.
