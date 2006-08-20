//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2006 Aug 20 (Sun) 13:28:06 by Harold Carr.
//

package com.differentity.client;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryResponse
    implements IsSerializable
{
    // NOTE: cannot make fields final.  
    // GWT complains at required zero arg constructor.

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private List subject;

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private List property;

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private List value;

    private String status;

    // GWT requires zero arg constructor.
    public QueryResponse() {}

    public QueryResponse(final List subject, final List property, 
			 final List value, final String status)
    {
	this.subject  = subject;
	this.property = property;
	this.value    = value;
	this.status   = status;
    }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getSubject() { return subject; }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getProperty() { return property; }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getValue() { return value; }

    public String getStatus() { return status; }
}

// End of file.
