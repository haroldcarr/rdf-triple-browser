//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 20 (Sun) 18:23:12 by Harold Carr.
//

/*
  TODO:
  - FIX: (when trying from any separate browser while gwt-shell is running):
         When selectinga SPV URI starts extra new window.
         Kill that window and the original window with the Request/Response
         done.
  - Implement AND (recursively)
  - Implement adding triples.
  - More Jena.
  - Figure out how to make spv panels expand.
  - Style
 */

package com.differentity.client;

import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;

import com.differentity.client.MainPanel;
import com.differentity.client.ServerCalls;

public class Main 
    implements 
	EntryPoint // Entry point classes define onModuleLoad()
{
    public static final String asteriskSymbol       = "*";
    public static final String collapse             = "collapse";
    public static final String collapseAllTags      = "collapse all tags";
    public static final String copyright            = "copyright 2006";
    public static final String differentityDotCom   = "differentity.com";
    public static final String emptyString          = "emptyString";
    public static final String expand               = "expand";
    public static final String expandAllTags        = "expand all tags";
    public static final String minusSymbol          = "-";
    public static final String plusSymbol           = "+";
    public static final String property             = "property";
    public static final String questionMarkSymbol   = "?";
    public static final String qsubject             = "?subject";
    public static final String qproperty            = "?property";
    public static final String qvalue               = "?value";
    public static final String subject              = "subject";
    public static final String subjectPropertyValue = "subjectPropertyValue";
    public static final String value                = "value";

    // TODO: these should be final.
    private static MainPanel mainPanel;
    private static ServerCalls serverCalls;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	serverCalls = new ServerCalls();
	// TODO: a race with next statement that sets up the HTML 
	// used by initialize
	serverCalls.initialize();
    }

    // NOTE: After initial development - when the server is NOT
    // started by the client, then this async callback will not be
    // necessary.
    public static void makeMainPanel()
    {
	mainPanel = new MainPanel();
    }	

    public static String getExpandCollapseState(
	final String expandCollapseState,
	final boolean pending)
    {
	if (expandCollapseState.equals(Main.expand)) {
	    return (pending ? Main.collapse : Main.expand);
	} else {
	    return (pending ? Main.expand : Main.collapse);
	}
    }
    public static MainPanel getMainPanel()     { return mainPanel; }
    public static ServerCalls getServerCalls() { return serverCalls; }
}

// End of file.

