//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 29 (Thu) 14:02:53 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.openhc.trowser.gwt.client.Test; // *****

import org.openhc.trowser.gwt.common.Constants;
import org.openhc.trowser.gwt.common.Util;
import org.openhc.trowser.gwt.client.QueryManager;
import org.openhc.trowser.gwt.client.ServerCalls;

public class Main 
    implements 
	Constants,
	EntryPoint // Entry point classes define onModuleLoad()
{
    // These should be final.
    private /*final*/ Util         util;
    private /*final*/ QueryManager queryManager;
    private /*final*/ ServerCalls  serverCalls;

    private /*final*/              QueryPanel queryPanel;
    private /*final*/              SPVPanel spvPanel;
    private /*final*/ Frame        webBrowser;

    private /*final*/ Label        responseProgressLabel;
    private /*final*/ DockPanel    dockPanel;
    private /*final*/ HTML         copyrightHTML;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	util         = new Util();
	queryManager = new QueryManager(this);
	serverCalls  = new ServerCalls(this);

	responseProgressLabel = new Label();

	queryPanel = new QueryPanel(this); 
	spvPanel   = new SPVPanel(this);

	// TOP

	/*
	RootPanel.get("slot0").add(new DevTime(this).makeStatusWidgets());
	*/

	final HorizontalPanel topPanel = new HorizontalPanel();
	topPanel.setStyleName("topPanel");
	topPanel.setHorizontalAlignment(DockPanel.ALIGN_LEFT);

	FileUploader fileUploader = new FileUploader(this);
	topPanel.add(fileUploader.getFormPanel());

	topPanel.add(responseProgressLabel);
	RootPanel.get("top").add(topPanel);

	// MIDDLE

	dockPanel = new DockPanel();
	RootPanel.get("middle").add(dockPanel);

	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

	dockPanel.add(queryPanel.getPanel(), DockPanel.NORTH);

	dockPanel.add(spvPanel.getPanel(), DockPanel.CENTER);

	// NOTE: - Seem to need to add most southern first.
	copyrightHTML = new HTML(this.copyright, true);
	dockPanel.add(copyrightHTML, DockPanel.SOUTH);

	webBrowser = new Frame("http://openhc.org/");
	webBrowser.setPixelSize(980, 300); // REVISIT
	dockPanel.add(webBrowser, DockPanel.SOUTH);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	// XXX - test
	//RootPanel.get("bottom").add(new Test().getWidget());
    }

    public QueryManager getQueryManager() { return queryManager; }
    public QueryPanel   getQueryPanel()   { return queryPanel; }
    public SPVPanel     getSPVPanel()     { return spvPanel; }
    public Frame        getBrowserPanel() { return webBrowser; }
    public ServerCalls  getServerCalls()  { return serverCalls; }
    public Util         getUtil()         { return util; }
    public Label        getResponseProgressLabel()
                                          { return responseProgressLabel; }
}

// End of file.

