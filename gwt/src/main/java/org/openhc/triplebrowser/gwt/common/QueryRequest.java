//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 16:22:53 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryRequest
    implements IsSerializable
{
    // GWT will not let me make these final.

    private String subject;
    private String verb;
    private String object;

    // GWT requires zero-arg constructor.
    public QueryRequest() {}

    public QueryRequest(String subject, String verb, 
			String object)
    {
	this.subject = subject;
	this.verb    = verb;
	this.object  = object;
    }

    public String getSubject() { return subject; }
    public String getVerb()    { return verb; }
    public String getObject()  { return object; }
}

// End of file.
