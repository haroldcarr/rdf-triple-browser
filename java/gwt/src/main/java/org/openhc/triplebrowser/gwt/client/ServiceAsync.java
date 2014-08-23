//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:52:35 by carr.
//

package org.openhc.triplebrowser.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openhc.triplebrowser.gwt.common.QueryRequest;
import org.openhc.triplebrowser.gwt.client.Service;

interface ServiceAsync {
    public void openFile(String filename, AsyncCallback callback);
    public void doQuery(QueryRequest queryRequest, AsyncCallback callback);
    public void assertFact(QueryRequest queryRequest, AsyncCallback callback);
    public void close(AsyncCallback callback);
}

// End of file.
