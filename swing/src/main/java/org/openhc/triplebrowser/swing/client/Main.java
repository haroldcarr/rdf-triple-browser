//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 27 (Tue) 08:52:35 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.util.Iterator;

import org.openhc.trowser.gwt.common.Constants;
import org.openhc.trowser.swing.client.MainPanel;
import org.openhc.trowser.swing.client.ServerCalls;
import org.openhc.trowser.swing.client.SwingView;

public class Main 
    implements
	Constants
{
    // TODO: these should be final.
    private static       MainPanel      mainPanel;
    private static       ServerCalls    serverCalls;
            static       String         OS_NAME;
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
	OS_NAME = System.getProperty("os.name");
	if (OS_NAME.startsWith("Windows")) {
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
	//serverCalls.initialize();
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

