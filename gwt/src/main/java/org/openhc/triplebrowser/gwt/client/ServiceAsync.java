//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2008 May 25 (Sun) 18:57:11 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.client.Service;

interface ServiceAsync {
    public void initialize(AsyncCallback callback);
    public void doQuery(QueryRequest queryRequest, AsyncCallback callback);
    public void assertFact(QueryRequest queryRequest, AsyncCallback callback);
    public void close(AsyncCallback callback);
}

// End of file.
