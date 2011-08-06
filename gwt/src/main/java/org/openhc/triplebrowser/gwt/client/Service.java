//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2011 Aug 04 (Thu) 13:06:28 by carr.
//

package org.openhc.trowser.gwt.client;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;

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
