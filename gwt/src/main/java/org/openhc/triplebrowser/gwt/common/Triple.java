//
// Created       : 2007 Jun 07 (Thu) 19:37:21 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:54:52 by carr.
//

package org.openhc.triplebrowser.gwt.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Triple
    implements IsSerializable
{
    private String subject;
    private String property;
    private String value;

    // GWT requires zero-arg constructor.
    public Triple() {}

    public Triple(final String subject, final String property,
		  final String value)
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
