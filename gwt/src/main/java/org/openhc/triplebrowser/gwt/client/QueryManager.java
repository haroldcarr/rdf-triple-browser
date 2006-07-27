//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 26 (Wed) 17:12:25 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.core.client.GWT;

import com.google.gwt.user.client.rpc.AsyncCallback; // *****
import com.google.gwt.user.client.rpc.ServiceDefTarget; // *****

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame; // *****
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window; // *****

import com.differentity.client.Main;

public class MainPanel
{
    final VerticalPanel subjectVerticalPanel;
    final VerticalPanel verbVerticalPanel;
    final VerticalPanel objectVerticalPanel;
    final HorizontalPanel svoHorizontalPanel;
    final DockPanel dockPanel;
    final HTML north;
    final HTML south;
    final QueryPanel queryPanel;

    MainPanel() {
	//
	// Setup reference to service.
	// Must be done before setting up panels.
	// Setup uses results from service.
	//
	Main.myServiceAsync = (MyServiceAsync) GWT.create(MyService.class);
	ServiceDefTarget serviceDefTarget = (ServiceDefTarget) 
	    Main.myServiceAsync;
	serviceDefTarget.setServiceEntryPoint("/MyService");

	//
	// Subject, Verb, Object panels.
	//

	subjectVerticalPanel = new SVOPanel(Main.subject).getPanel();
	verbVerticalPanel    = new SVOPanel(Main.verb).getPanel();
	objectVerticalPanel  = new SVOPanel(Main.object).getPanel();
	svoHorizontalPanel = new HorizontalPanel();
	svoHorizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	svoHorizontalPanel.add(subjectVerticalPanel);
	svoHorizontalPanel.add(verbVerticalPanel);
	svoHorizontalPanel.add(objectVerticalPanel);

	//
	// Main panel.
	//    

	dockPanel = new DockPanel();
	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	north = new HTML(Main.differentityDotCom, true);
	south = new HTML(Main.copyright, true);
	dockPanel.add(north, DockPanel.NORTH);
	queryPanel = new QueryPanel();
	dockPanel.add(queryPanel.getHorizontalPanel(), DockPanel.NORTH);
	// NOTE: - if SOUTH added after CENTER does not show up.
	dockPanel.add(south, DockPanel.SOUTH);
	dockPanel.add(svoHorizontalPanel, DockPanel.CENTER);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	// XXX BEGIN RPC TEST
	final HTML remoteHTML = new HTML();
	Main.myServiceAsync.myMethod(
            "FOO",
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    remoteHTML.setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    remoteHTML.setHTML(caught.toString());
		}
	    });
	// XXX END RPC TEST

	RootPanel.get("slot1").add(remoteHTML);
	RootPanel.get("slot2").add(dockPanel);

	// XXX - frame test
	RootPanel.get("slot3").add(new Frame("http://www.google.com/"));
    }
    public QueryPanel getQueryPanel() { return queryPanel; }
}

// End of file.
