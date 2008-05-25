//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 21:15:02 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.util.Iterator;

import org.openhc.trowser.swing.client.MainPanel;
import org.openhc.trowser.swing.client.ServerCalls;

import org.openhc.trowser.swing.swing.SwingView;

public class Main 
{
    public static final String serviceEntryPoint    = "/Service";

    public static final String clear                = "clear";
    public static final String asteriskSymbol       = "*";
    public static final String blankSpace           = " ";
    public static final String collapse             = "-";
    public static final String copyright            = "copyright 2007";
    public static final String doQuery              = "doQuery";
    public static final String emptyString          = "emptyString";
    public static final String expand               = "+";
    public static final String expandOrCollapseSPVClick 
	= "expandOrCollapseSPVClick";
    public static final String expandOrCollapseSPVItemClick
	= "expandOrCollapseSPVItemClick";
    public static final String historyFieldSeparator = ":;:";
    public static final String minusSymbol          = "-";
    public static final String plusSymbol           = "+";
    public static final String property             = "property";
    public static final String questionMarkSymbol   = "?";
    public static final String qsubject             = "?subject";
    public static final String qproperty            = "?property";
    public static final String qvalue               = "?value";
    public static final String shiftLeft            = "<-";
    public static final String shiftRight           = "->";
    public static final String showAll              = "show all";
    public static final String showMatch            = "show match";
    public static final String subject              = "subject";
    public static final String subjectPropertyValue = "subjectPropertyValue";
    public static final String url                  = "url";
    public static final String value                = "value";

    // TODO: these should be final.
    private static       MainPanel      mainPanel;
    private static       ServerCalls    serverCalls;
            static       boolean        realBrowser = false;
    private static       SwingView      swingView;

    public static void main(String[] av)
    {
    }

    public Main(SwingView swingView)
    {
	this.swingView = swingView;
	init();
    }

    private void init()
    {
	if (/*av.length == 0*/ true) {
	    realBrowser = true;
	    chrriis.dj.nativeswing.NativeInterfaceHandler.init();
	}
	onModuleLoad();
	makeMainPanel();
    }

    // This is the entry point method.
    public static void onModuleLoad() 
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

	// NOTE: In Swing a race can happen here.  Put in main swing "run".
	//getMainPanel().doQuery(true);
    }	

    // Utility
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

    public static MainPanel      getMainPanel()      { return mainPanel; }
    public static ServerCalls    getServerCalls()    { return serverCalls; }
    public static SwingView      getSwingView()      { return swingView; }
}

// End of file.

