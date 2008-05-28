//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 10:31:52 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.util.Iterator;

import org.openhc.trowser.gwt.common.Constants;
import org.openhc.trowser.swing.client.MainPanel;
import org.openhc.trowser.swing.client.ServerCalls;
import org.openhc.trowser.swing.client.TrowserLayout;

public class Main 
    implements
	Constants
{
    private final MainPanel      mainPanel;
    private final ServerCalls    serverCalls;
            final String         operatingSystemName; // *****
    private final TrowserLayout      trowserLayout;

    public Main(TrowserLayout trowserLayout)
    {
	this.trowserLayout = trowserLayout;
	operatingSystemName = System.getProperty("os.name");
	if (operatingSystemName.startsWith("Windows")) {
	    chrriis.dj.nativeswing.NativeInterfaceHandler.init();
	}
	serverCalls = new ServerCalls(this);
	mainPanel = new MainPanel(this);
    }	

    // Utility
    public String getExpandCollapseState(
	final String expandCollapseState,
	final boolean pending)
    {
	if (expandCollapseState.equals(this.expand)) {
	    return (pending ? this.collapse : this.expand);
	} else {
	    return (pending ? this.expand : this.collapse);
	}
    }

    public MainPanel     getMainPanel()     { return mainPanel; }
    public ServerCalls   getServerCalls()   { return serverCalls; }
    public TrowserLayout getTrowserLayout() { return trowserLayout; }
}

// End of file.

