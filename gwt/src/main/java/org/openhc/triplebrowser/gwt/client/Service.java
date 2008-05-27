//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 25 (Sun) 21:59:18 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;

import com.google.gwt.user.client.rpc.RemoteService;

public interface Service extends RemoteService 
{
    public String openFile(String filename);
    public QueryResponse doQuery(QueryRequest queryRequest);
    public QueryResponse assertFact(QueryRequest queryRequest);
    public String close();
}

// End of file.
