//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 16:12:37 by Harold Carr.
//

package com.differentity.client;

import com.differentity.client.QueryRequest;
import com.differentity.client.QueryResponse;

import com.google.gwt.user.client.rpc.RemoteService;

public interface Service extends RemoteService 
{
    public String initialize(String notUsed);
    public QueryResponse doQuery(QueryRequest queryRequest);
}

// End of file.
