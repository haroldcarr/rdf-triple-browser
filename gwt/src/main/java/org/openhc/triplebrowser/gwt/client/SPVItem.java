//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 27 (Tue) 10:15:40 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import com.google.gwt.user.client.ui.Label;

import org.openhc.trowser.gwt.client.Main;

public class SPVItem
    extends 
	org.openhc.trowser.gwt.common.SPVItem
{
    private final Label label;
    
    SPVItem(final String spvCategory, 
	    final String expandedName, 
	    final String collapsedName)
    {
	super(spvCategory, expandedName, collapsedName);
	label = new Label(expandedName);
    }

    Label  getLabel() { return label; }
}

// End of file.
