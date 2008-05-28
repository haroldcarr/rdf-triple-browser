//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 12:36:44 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;

import org.openhc.trowser.gwt.common.Constants;
import org.openhc.trowser.gwt.common.Util;
       
import org.openhc.trowser.gwt.client.MainPanel;
import org.openhc.trowser.gwt.client.ServerCalls;

public class Main 
    implements 
	Constants,
	EntryPoint // Entry point classes define onModuleLoad()
{
    // TODO: these should be final.
    private static MainPanel      mainPanel;
    private static ServerCalls    serverCalls;
    private static Util           util;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	util = new Util();
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
    }	

    public static MainPanel      getMainPanel()      { return mainPanel; }
    public static ServerCalls    getServerCalls()    { return serverCalls; }
    public static Util           getUtil()           { return util; }
}

// End of file.

