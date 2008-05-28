//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 12:30:22 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.util.Iterator;

import org.openhc.trowser.gwt.common.Constants;
import org.openhc.trowser.gwt.common.Util;

import org.openhc.trowser.swing.client.SPVPanel;
import org.openhc.trowser.swing.client.QueryPanel;
import org.openhc.trowser.swing.client.ServerCalls;
import org.openhc.trowser.swing.client.TrowserLayout;
import org.openhc.trowser.swing.client.WebBrowser;

public class Main 
    implements
	Constants
{
    private final Util          util;
    private final QueryManager  queryManager;
    private final ServerCalls   serverCalls;
            final String        operatingSystemName; // *****
    private final TrowserLayout trowserLayout;

    private final QueryPanel   queryPanel;
    private final SPVPanel     spvPanel;
    private final WebBrowser   browserPanel;

    public Main(TrowserLayout trowserLayout)
    {
	this.util = new Util();
	this.trowserLayout = trowserLayout;

	operatingSystemName = System.getProperty("os.name");
	if (operatingSystemName.startsWith("Windows")) {
	    chrriis.dj.nativeswing.NativeInterfaceHandler.init();
	}

	queryManager = new QueryManager(this);
	serverCalls = new ServerCalls(this);

	queryPanel = new QueryPanel(this); // Must exist before SPVPanel.
	spvPanel = new SPVPanel(this);
	if (this.operatingSystemName.startsWith("Windows")) {
	    browserPanel = WebBrowser.create("DJNATIVESWING");
	} else {
	    browserPanel = WebBrowser.create("TEXTAREA");
	}

	this.getTrowserLayout().mainPanelLayout(
	    this.getTrowserLayout().getSwingMainPanel(),
	    queryPanel.getPanel(),
	    spvPanel.getPanel(),
	    browserPanel);
    }	

    public QueryManager  getQueryManager()  { return queryManager; }
    public QueryPanel    getQueryPanel()    { return queryPanel; }
    public SPVPanel      getSPVPanel()      { return spvPanel; }
    public WebBrowser    getBrowserPanel()  { return browserPanel; }
    public ServerCalls   getServerCalls()   { return serverCalls; }
    public TrowserLayout getTrowserLayout() { return trowserLayout; }
    public Util          getUtil()          { return util; }
}

// End of file.

