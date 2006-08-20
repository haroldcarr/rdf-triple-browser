//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2006 Aug 20 (Sun) 13:27:45 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryRequest
    implements IsSerializable
{
    // GWT will not let me make these final.

    private String subject;
    private String property;
    private String value;

    // GWT requires zero-arg constructor.
    public QueryRequest() {}

    public QueryRequest(String subject, String property, String value)
    {
	this.subject  = subject;
	this.property = property;
	this.value    = value;
    }

    public String getSubject()  { return subject; }
    public String getProperty() { return property; }
    public String getValue()    { return value; }
}

// End of file.
