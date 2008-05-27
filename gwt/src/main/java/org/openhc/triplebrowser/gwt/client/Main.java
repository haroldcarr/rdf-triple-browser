//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 18:09:38 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;

import org.openhc.trowser.gwt.common.Constants;
       
import org.openhc.trowser.gwt.client.MainPanel;
import org.openhc.trowser.gwt.client.ServerCalls;

public class Main 
    implements 
	Constants,
	EntryPoint // Entry point classes define onModuleLoad()
{
    // TODO: these should be final.
    private static BrowserHistory browserHistory;
    private static MainPanel      mainPanel;
    private static ServerCalls    serverCalls;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	serverCalls = new ServerCalls();
	// TODO: a race with next statement that sets up the HTML 
	// used by initialize
	//serverCalls.initialize();
	makeMainPanel();
    }

    // NOTE: After initial development - when the server is NOT
    // started by the client, then this async callback will not be
    // necessary.
    public static void makeMainPanel()
    {
	mainPanel = new MainPanel();

	// Need to set this up after MainPanel available in case
	// a bookmark with history is invoked.
	browserHistory = new BrowserHistory();
	// Need to create in two parts since initialize, if using
	// a bookmark, may make a query - and that query will use
	// browserHistory.  If in the constructor then it is not
	// available because above assignment happens after construction.
	browserHistory.initialize();

	// This doQuery could happen while MainPanel is begin setup.
	// But need access to MainPanel from Main for history on the query.
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
    public static BrowserHistory getBrowserHistory() { return browserHistory; }
}

// End of file.

