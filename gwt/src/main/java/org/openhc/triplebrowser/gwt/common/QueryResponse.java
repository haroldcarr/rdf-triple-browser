//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 21:32:49 by Harold Carr.
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
    private List verb;

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private List object;

    private String status;

    // GWT requires zero arg constructor.
    public QueryResponse() {}

    public QueryResponse(final List subject, final List verb, 
			 final List object, final String status)
    {
	this.subject = subject;
	this.verb    = verb;
	this.object  = object;
	this.status  = status;
    }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getSubject() { return subject; }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getVerb() { return verb; }

    /**
     * @gwt.typeArgs <java.lang.String>
     */
    public List getObject() { return object; }

    public String getStatus() { return status; }
}

// End of file.
