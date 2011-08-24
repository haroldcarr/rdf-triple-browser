//
// Created       : 2006 Aug 12 (Sat) 14:56:41 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:55:21 by carr.
//

package org.openhc.triplebrowser.gwt.common;

import org.openhc.triplebrowser.gwt.common.Constants;

public class Util
{
    public String substringAfterLastSlashOrFirstSharp(final String x)
    {
	int indexOfLastSlashOrFirstSharp = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/' || x.charAt(i) == '#') {
		indexOfLastSlashOrFirstSharp = i;
	    }
	}
	if (indexOfLastSlashOrFirstSharp == 0) {
	    // It doesn't contain any.
	    return x;
	}
	final String result = x.substring(indexOfLastSlashOrFirstSharp + 1);
	// If it ends in a slash then remove the ending slash and try again.
	if (result.length() == 0) {
	    return substringAfterLastSlashOrFirstSharp(
                       x.substring(0, x.length()-1));
	} else {
	    return result;
	}
    }

    // Utility
    public String getExpandCollapseState(
	final String expandCollapseState,
	final boolean pending)
    {
	if (expandCollapseState.equals(Constants.expand)) {
	    return (pending ? Constants.collapse : Constants.expand);
	} else {
	    return (pending ? Constants.expand : Constants.collapse);
	}
    }
}

// End of file.
