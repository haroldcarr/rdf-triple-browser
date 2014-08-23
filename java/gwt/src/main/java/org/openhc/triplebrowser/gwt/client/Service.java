//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:52:23 by carr.
//

package org.openhc.triplebrowser.gwt.client;

import org.openhc.triplebrowser.gwt.common.QueryRequest;
import org.openhc.triplebrowser.gwt.common.QueryResponse;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Service")
public interface Service extends RemoteService
{
    public String openFile(String filename);
    public QueryResponse doQuery(QueryRequest queryRequest);
    public QueryResponse assertFact(QueryRequest queryRequest);
    public String close();
}

// End of file.
