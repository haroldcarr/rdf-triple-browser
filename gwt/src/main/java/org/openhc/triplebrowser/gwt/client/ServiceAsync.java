//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Sep 04 (Mon) 10:14:31 by Harold Carr.
//

package com.differentity.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.differentity.client.QueryRequest;
import com.differentity.client.Service;

interface ServiceAsync {
    public void initialize(AsyncCallback callback);
    public void doQuery(QueryRequest queryRequest, AsyncCallback callback);
    public void close(AsyncCallback callback);
}

// End of file.
