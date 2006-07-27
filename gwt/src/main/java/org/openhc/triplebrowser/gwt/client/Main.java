//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 26 (Wed) 17:06:03 by Harold Carr.
//

/*
  TODO:
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
    public static String collapse           = "collapse";
    public static String collapseAllTags    = "collapse all tags";
    public static String copyright          = "copyright 2006";
    public static String differentityDotCom = "differentity.com";
    public static String expand             = "expand";
    public static String expandAllTags      = "expand all tags";
    public static String minusSymbol        = "-";
    public static String object             = "object";
    public static String plusSymbol         = "+";
    public static String subject            = "subject";
    public static String subjectVerbObject  = "subjectVerbObject";
    public static String verb               = "verb";

    // TODO: these should be final.
    public static ServerCalls serverCalls;
    public static MainPanel mainPanel;
    public static MyServiceAsync myServiceAsync;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	serverCalls = new ServerCalls();
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
}

// End of file.

