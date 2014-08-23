//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 21:00:37 by carr.
//

package org.openhc.triplebrowser.swing.client;

import java.util.Iterator;

import org.openhc.triplebrowser.gwt.common.Constants;
import org.openhc.triplebrowser.gwt.common.Util;

import org.openhc.triplebrowser.swing.client.SPVPanel;
import org.openhc.triplebrowser.swing.client.QueryPanel;
import org.openhc.triplebrowser.swing.client.ServerCalls;
import org.openhc.triplebrowser.swing.client.TripleBrowserLayout;
import org.openhc.triplebrowser.swing.client.WebBrowser;

public class Main
    implements
	Constants
{
    private final Util                util;
    private final QueryManager        queryManager;
    private final ServerCalls         serverCalls;
            final String              operatingSystemName; // *****
    private final TripleBrowserLayout tripleBrowserLayout;

    private final QueryPanel   queryPanel;
    private final SPVPanel     spvPanel;
    private final WebBrowser   browserPanel;

    public Main(TripleBrowserLayout tripleBrowserLayout)
    {
	this.util = new Util();
	this.tripleBrowserLayout = tripleBrowserLayout;

	operatingSystemName = System.getProperty("os.name");
        /*
	if (operatingSystemName.startsWith("Windows")) {
	    chrriis.dj.nativeswing.NativeInterfaceHandler.init();
	}
        */
	queryManager = new QueryManager(this);
	serverCalls = new ServerCalls(this);

	queryPanel = new QueryPanel(this); // Must exist before SPVPanel.
	spvPanel = new SPVPanel(this);
	if (this.operatingSystemName.startsWith("Windows")) {
	    browserPanel = WebBrowser.create("DJNATIVESWING");
	} else {
	    browserPanel = WebBrowser.create("TEXTAREA");
	}

	this.getTripleBrowserLayout().mainPanelLayout(
	    this.getTripleBrowserLayout().getSwingMainPanel(),
	    queryPanel.getPanel(),
	    spvPanel.getPanel(),
	    browserPanel);
    }

    public QueryManager  getQueryManager()  { return queryManager; }
    public QueryPanel    getQueryPanel()    { return queryPanel; }
    public SPVPanel      getSPVPanel()      { return spvPanel; }
    public WebBrowser    getBrowserPanel()  { return browserPanel; }
    public ServerCalls   getServerCalls()   { return serverCalls; }
    public TripleBrowserLayout getTripleBrowserLayout()
                                            { return tripleBrowserLayout; }
    public Util          getUtil()          { return util; }
}

// End of file.

