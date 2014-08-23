//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:50:03 by carr.
//

package org.openhc.triplebrowser.gwt.client;

import com.google.gwt.user.client.ui.Label;

import org.openhc.triplebrowser.gwt.client.Main;

public class SPVItem
    extends
	org.openhc.triplebrowser.gwt.common.SPVItem
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
