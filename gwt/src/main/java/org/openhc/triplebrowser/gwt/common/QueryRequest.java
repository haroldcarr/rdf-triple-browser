//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2006 Sep 10 (Sun) 20:50:51 by Harold Carr.
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
    private String setContentsOf;

    // GWT requires zero-arg constructor.
    public QueryRequest() {}

    public QueryRequest(final String subject, final String property, 
			final String value, final String setContentsOf)
    {
	this.subject       = subject;
	this.property      = property;
	this.value         = value;
	this.setContentsOf = setContentsOf;
    }

    public String getSubject()       { return subject; }
    public String getProperty()      { return property; }
    public String getValue()         { return value; }
    public String getSetContentsOf() { return setContentsOf; }
}

// End of file.
