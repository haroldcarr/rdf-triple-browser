//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Sep 23 (Sat) 14:20:02 by Harold Carr.
//

package com.differentity.client;

import com.differentity.client.QueryRequest;
import com.differentity.client.QueryResponse;

import com.google.gwt.user.client.rpc.RemoteService;

public interface Service extends RemoteService 
{
    public String initialize();
    public QueryResponse doQuery(QueryRequest queryRequest);
    public QueryResponse assertFact(QueryRequest queryRequest);
    public String close();
}

// End of file.
