//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 26 (Wed) 16:31:58 by Harold Carr.
//

/*
  TODO:
  - Move initial lists to server-side.
  - Server-side integrated with Jena
  - Figure out how to make sov panels expand.
  - Style
 */

package com.differentity.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;



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
    public static Server server;
    public static MainPanel mainPanel;
    public static MyServiceAsync myServiceAsync;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() 
    {
	server = new Server();
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

class MainPanel
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

class QueryPanel
{
    final HorizontalPanel horizontalPanel;
    final TextBox         subjectTextBox;
    final TextBox         verbTextBox;
    final TextBox         objectTextBox;

    QueryPanel()
    {
	subjectTextBox = new TextBox();
	verbTextBox    = new TextBox();
	objectTextBox  = new TextBox();
	horizontalPanel = new HorizontalPanel();
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(verbTextBox);
	horizontalPanel.add(objectTextBox);
    }
    TextBox         getSubjectTextBox() { return subjectTextBox; }
    TextBox         getVerbTextBox   () { return verbTextBox; }
    TextBox         getObjectTextBox () { return objectTextBox; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
}

class SVOItem
{
    final String svoCategory;
    final String expandedName;
    final String collapsedName;
    String expandCollapseState;
    final Button button;
    final Hyperlink hyperlink;
    final HorizontalPanel horizontalPanel;
    final VerticalPanel verticalPanel;
    
    SVOItem(String svoCategory, String expandedName, String collapsedName,
	    String expandCollapseState)
    {
	this.svoCategory = svoCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
	this.expandCollapseState = expandCollapseState;
	button = new Button(Main.plusSymbol);
	hyperlink = new Hyperlink(expandedName,
				  svoCategory + " " + expandedName);
	horizontalPanel = new HorizontalPanel();
	verticalPanel = new VerticalPanel();

	// Item layout.
	horizontalPanel.add(button);
	horizontalPanel.add(hyperlink);
	verticalPanel.add(horizontalPanel);

	if (expandCollapseState.equals(Main.collapse)){
	    hyperlink.setText(collapsedName);
	} else {
	    button.setText(Main.minusSymbol);
	    verticalPanel.add(new Frame(expandedName));
	}
    }
    String getSVOCategory() { return svoCategory; }
    String getExpandedName() { return expandedName; }
    String getCollapsedName() { return collapsedName; }
    String getCurrentExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, false);
    }
    String getPendingExpandCollapseState() {
	return Main.getExpandCollapseState(expandCollapseState, true);
    }
    void   setExpandCollapseState(String x) { expandCollapseState = x; }
    Button getButton() { return button; }
    Hyperlink getHyperlink() { return hyperlink; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
    VerticalPanel getVerticalPanel() { return verticalPanel; }
}

class SVOPanel
{
    String expandCollapseState;

    final String svoCategory; // For debug only.
    List contents; // The "raw" contents.  For debugging.
    final VerticalPanel verticalInsideScroll;
    final VerticalPanel topVerticalPanel;
    final Button topButton;
    final ScrollPanel scrollPanel;

    SVOPanel(final String svoCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.svoCategory = svoCategory;

	// Begin layout.
	topVerticalPanel = new VerticalPanel();
	topVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	topButton = new Button(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new VerticalPanel();

	// Do the async call now that what it depends when it returns
	// is available.
	// "this" so that it can callback to "initialContents".
	Main.server.getInitialContents(this, svoCategory);

	scrollPanel = new ScrollPanel(verticalInsideScroll);
	scrollPanel.setStyleName(Main.subjectVerbObject);
	topVerticalPanel.add(scrollPanel);
	// End layout.
	
	topButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		final String newState = expandOrCollapse();
		topButton.setText(newState);
	    }
	});
    }

    VerticalPanel getPanel() { return topVerticalPanel; }

    String getCurrentExpandCollapseState()
    {
	return Main.getExpandCollapseState(expandCollapseState, false);
    }
    String getPendingExpandCollapseState()
    {
	return Main.getExpandCollapseState(expandCollapseState, true);
    }

    // This is calledback from an async request to server.
    public void initialContents(final List contents)
    {
	this.contents = contents;
	verticalInsideScroll.clear();
	final Iterator i = contents.iterator();
	while (i.hasNext()) {
	    final SVOItem svoItem = (SVOItem) i.next();
	    verticalInsideScroll.add(svoItem.getVerticalPanel());

	    if (expandCollapseState.equals(Main.expand)) {
		svoItem.getHyperlink().setText(svoItem.getExpandedName());
	    }

	    svoItem.getHyperlink().addClickListener(new ClickListener() {
		public void onClick(final Widget sender) {
		    Main.server.svoLinkClicked(((Hyperlink)sender).getTargetHistoryToken());
		}
	    });

	    svoItem.getButton().addClickListener(new ClickListener() {
	        public void onClick(Widget sender) {
		    // TODO: Triple expand/collapse:
		    // + = expand to full URL and N "characters" of source.
		    // * = expand to full URL and all source.
		    // - = collapse to short URL and no source.
		    if (svoItem.getPendingExpandCollapseState().equals(Main.expand)) {
			svoItem.getHyperlink().setText(svoItem.getExpandedName());
			svoItem.getVerticalPanel().add(new Frame(svoItem.getExpandedName()));
			svoItem.setExpandCollapseState(Main.expand);
			svoItem.getButton().setText(Main.minusSymbol);
		    } else {
			Widget w = svoItem.getVerticalPanel().getWidget(1);
			svoItem.getVerticalPanel().remove(w);
			svoItem.getHyperlink().setText(svoItem.getCollapsedName());
			svoItem.setExpandCollapseState(Main.collapse);
			svoItem.getButton().setText(Main.plusSymbol);
		    }
		}});
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	final Iterator i = verticalInsideScroll.iterator();
	final Iterator j = contents.iterator();
	while (i.hasNext()) {
	    final VerticalPanel verticalPanel = (VerticalPanel) i.next();
	    final SVOItem svoItem = (SVOItem) j.next();
	    if (svoItem.getCurrentExpandCollapseState().equals(Main.collapse)){
		if (pendingExpandCollapseState.equals(Main.expand)) {
		    svoItem.getHyperlink().setText(svoItem.getExpandedName());
		} else {
		    svoItem.getHyperlink().setText(svoItem.getCollapsedName());
		}
	    }
	}
	expandCollapseState = pendingExpandCollapseState;
	return getPendingExpandCollapseState();
    }
}

class Server
{
    static List initial; // For debugging.
    static List getInitial() { return initial; }
    static void setInitial(List i) { initial = i; }

    public void getInitialContents(final SVOPanel svoPanel,
				   final String svoCategory)
    {
	Main.myServiceAsync.getInitialContents(
            svoCategory,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    final Iterator i = ((List)x).iterator();
		    final List result = new ArrayList();
		    while (i.hasNext()) {
			String uri = (String) i.next();
			result.add(
                            new SVOItem(svoCategory, 
					uri,
					substringAfterLastSlash(uri),
					// NOTE: during development change to
					// Main.expand to test full range.
					Main.collapse));
		    }
		    setInitial(result);
		    svoPanel.initialContents(result);
		}

		public void onFailure(Throwable caught) {
		    Window.alert(".getInitialContents: " + caught);
		}
	    });
    }

    public void svoLinkClicked(final String categoryAndURL)
    {
	// TODO: Send to server.  Receive updates for other panels.
	int i = categoryAndURL.indexOf(" ");
	String category = categoryAndURL.substring(0, i);
	String url = categoryAndURL.substring(i+1);
	if (category.equals(Main.subject)) {
	    Main.mainPanel.getQueryPanel().getSubjectTextBox().setText(url);
	} else if (category.equals(Main.verb)) {
	    Main.mainPanel.getQueryPanel().getVerbTextBox().setText(url);
	} else if (category.equals(Main.object)) {
            Main.mainPanel.getQueryPanel().getObjectTextBox().setText(url);
	} else {
	    // TODO: FIX
	    Main.mainPanel.getQueryPanel().getSubjectTextBox().setText("ERROR");
	}
    }

    private String substringAfterLastSlash(final String x)
    {
	int indexOfLastSlash = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/') {
		indexOfLastSlash = i;
	    }
	}
	final String result = x.substring(indexOfLastSlash + 1);
	// If it ends in a slash then remove the ending slash and try again.
	if (result.length() == 0) {
	    return substringAfterLastSlash(x.substring(0, x.length()-1));
	} else {
	    return result;
	}
    }
}

// End of file.

