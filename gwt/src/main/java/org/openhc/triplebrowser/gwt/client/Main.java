//
// Created : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jun 23 (Fri) 10:30:19 by Harold Carr.
//

/*
  TODO:
  - Server-side
  - Figure out how to make sov panels expand.
  - Style
 */

package com.differentity.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;

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
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Main 
    implements 
	EntryPoint // Entry point classes define onModuleLoad()
{
    public  static String collapse           = "collapse";
    public  static String collapseAllTags    = "collapse all tags";
    private static String copyright          = "copyright 2006";
    private static String differentityDotCom = "differentity.com";
    public  static String expand             = "expand";
    public  static String expandAllTags      = "expand all tags";
    public  static String minusSymbol        = "-";
    private static String object             = "object";
    public  static String plusSymbol         = "+";
    private static String subject            = "subject";
    public  static String subjectVerbObject  = "subjectVerbObject";
    private static String verb               = "verb";

    public static final Label lbl = new Label("XXX"); // XXX

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

	//
	// Subject, Verb, Object panels.
	//

	final VerticalPanel subjectVerticalPanel =
	    new SVOManager(subject).getPanel();
	final VerticalPanel verbVerticalPanel    = 
	    new SVOManager(verb).getPanel();
	final VerticalPanel objectVerticalPanel  =
	    new SVOManager(object).getPanel();
	final HorizontalPanel horizontalPanel = new HorizontalPanel();
	horizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	horizontalPanel.add(subjectVerticalPanel);
	horizontalPanel.add(verbVerticalPanel);
	horizontalPanel.add(objectVerticalPanel);

	//
	// Main panel.
	//    

	final DockPanel dockPanel = new DockPanel();
	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	final HTML north = new HTML(differentityDotCom, true);
	final HTML south = new HTML(copyright, true);
	dockPanel.add(north, DockPanel.NORTH);
	// NOTE: - if SOUTH added after CENTER does not show up.
	dockPanel.add(south, DockPanel.SOUTH);
	dockPanel.add(horizontalPanel, DockPanel.CENTER);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	RootPanel.get("slot2").add(dockPanel);

	// XXX - Hyperlink test
	RootPanel.get("slot1").add(lbl);

	// XXX - frame test
	RootPanel.get("slot3").add(new Frame("http://www.google.com/"));
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

class SVOManager
{
    String expandCollapseState;

    final String svoCategory; // For debug only.
    final List contents; // The "raw" contents.
    final VerticalPanel verticalInsideScroll;
    final VerticalPanel topVerticalPanel;
    final Button topButton;
    final ScrollPanel scrollPanel;

    SVOManager(final String svoCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.svoCategory = svoCategory;

	// TODO: Get from service.
	// NOTE: during development change to Main.expand to test full range.
	contents = fakeServerInitialization(svoCategory, Main.collapse);

	// Begin layout.
	topVerticalPanel = new VerticalPanel();
	topVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	topButton = new Button(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new VerticalPanel();
	scrollPanel = new ScrollPanel(verticalInsideScroll);
	scrollPanel.setStyleName(Main.subjectVerbObject);
	topVerticalPanel.add(scrollPanel);
	initialContents();
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

    private void initialContents()
    {
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
		    // TODO: Send to server.  Receive updates for other panels.
		    Main.lbl.setText(((Hyperlink)sender).getTargetHistoryToken());
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

    private List fakeServerInitialization(String svoCategory, 
					  String expandOrCollapse)
    {
	final Iterator i = svoList.iterator();
	final List result = new ArrayList();
	while (i.hasNext()) {
	    String uri = (String) i.next();
	    result.add(new SVOItem(svoCategory, 
				   uri,
				   substringAfterLastSlash(uri),
				   expandOrCollapse));
	}
	return result;
    }

    // TODO: replace with real list from server.
    private List svoList = new ArrayList();
    { // NOTE: block needed for this to compile. 
    svoList.add("http://haroldcarr.com");
    svoList.add("http://www.rojo.com/");
    svoList.add("http://google.com");
    svoList.add("http://del.icio.us/");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/authorPrefix");
    svoList.add("http://differentity.com/haroldcarr/authorFirstName");
    svoList.add("http://differentity.com/haroldcarr/authorMiddleName");
    svoList.add("http://differentity.com/haroldcarr/authorLastName");
    svoList.add("http://differentity.com/haroldcarr/authorSuffix");
    svoList.add("http://differentity.com/haroldcarr/authorBorn");
    svoList.add("http://differentity.com/haroldcarr/authorDied");
    svoList.add("http://differentity.com/haroldcarr/authorIdea");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/ideaInCategory");
    svoList.add("http://differentity.com/haroldcarr/work");
    svoList.add("http://differentity.com/haroldcarr/workAuthor");
    svoList.add("http://differentity.com/haroldcarr/workTitle");
    svoList.add("http://differentity.com/haroldcarr/workPublished");
    svoList.add("http://differentity.com/haroldcarr/workWritten");
    svoList.add("http://differentity.com/haroldcarr/bataille");
    svoList.add("http://differentity.com/haroldcarr/book");
    svoList.add("http://differentity.com/haroldcarr/guilty");
    svoList.add("http://differentity.com/haroldcarr/eroticism");
    svoList.add("http://differentity.com/haroldcarr/blue_of_noon");
    svoList.add("http://differentity.com/haroldcarr/inner_experience");

    svoList.add("http://differentity.com/haroldcarr/similarTo");
    svoList.add("http://differentity.com/haroldcarr/equalTo");
    svoList.add("http://differentity.com/haroldcarr/contraryTo");
    svoList.add("http://differentity.com/haroldcarr/contrarstWith");
    svoList.add("http://differentity.com/haroldcarr/relatesTo");
    svoList.add("http://differentity.com/haroldcarr/fate");
    svoList.add("http://differentity.com/haroldcarr/formlessness");
    svoList.add("http://differentity.com/haroldcarr/god");
    svoList.add("http://differentity.com/haroldcarr/stability");
    svoList.add("http://differentity.com/haroldcarr/reason");
    svoList.add("http://differentity.com/haroldcarr/nothingness");
    svoList.add("http://differentity.com/haroldcarr/chance");
    svoList.add("http://differentity.com/haroldcarr/strength");
    svoList.add("http://differentity.com/haroldcarr/death");
    svoList.add("http://differentity.com/haroldcarr/anguish");
    svoList.add("http://differentity.com/haroldcarr/silence");
    svoList.add("http://differentity.com/haroldcarr/limit");
    }
}

// End of file.

