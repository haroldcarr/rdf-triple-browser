//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 19:35:19 by Harold Carr.
//

/*
  TODO:
  - FIX: (when trying from any separate browser while gwt-shell is running):
Cannot find resource 'Main.html' in the public path of module 'com.differentity.Main'
  - Server-side integrated with Jena
  - Figure out how to make sov panels expand.
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
    public static final String collapse           = "collapse";
    public static final String collapseAllTags    = "collapse all tags";
    public static final String copyright          = "copyright 2006";
    public static final String differentityDotCom = "differentity.com";
    public static final String expand             = "expand";
    public static final String expandAllTags      = "expand all tags";
    public static final String minusSymbol        = "-";
    public static final String object             = "object";
    public static final String plusSymbol         = "+";
    public static final String subject            = "subject";
    public static final String subjectVerbObject  = "subjectVerbObject";
    public static final String verb               = "verb";

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

