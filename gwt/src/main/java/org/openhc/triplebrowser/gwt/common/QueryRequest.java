//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2007 Jun 08 (Fri) 21:15:29 by Harold Carr.
//

package com.differentity.client;

import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryRequest
    implements IsSerializable
{
    // GWT will not let me make these final.

    private List   triples;
    private String setContentsOf;

    // GWT requires zero-arg constructor.
    public QueryRequest() {}

    public QueryRequest(final List triples, final String setContentsOf)
    {
	this.triples       = triples;
	this.setContentsOf = setContentsOf;
    }

    public List   getTriples()       { return triples; }
    public String getSetContentsOf() { return setContentsOf; }
}

// End of file.
