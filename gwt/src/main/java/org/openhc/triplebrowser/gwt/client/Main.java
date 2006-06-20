//
// Created : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jun 19 (Mon) 22:53:05 by Harold Carr.
//

/*
  TODO:
  - Fix item expandCollapseState viz svoCategory expandCollapseState state.
  - Reuse existing button/panels in expandCollapse (maybe not when filtering)
  - Figure out how to make sov panels expand.
  - Make +/- on sov links replace with full URL and frame
  - Style
  - Server-side
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
    private static String copyright          = "copyright 2006";
    private static String differentityDotCom = "differentity.com";
    public  static String expand             = "expand";
    public  static String minusSymbol        = "-";
    private static String object             = "object";
    public  static String plusSymbol         = "+";
    private static String subject            = "subject";
    private static String subjectVerbObject  = "subjectVerbObject";
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
	    buildSVOPanel(new SVOManager(subject));
	final VerticalPanel verbVerticalPanel    = 
	    buildSVOPanel(new SVOManager(verb));
	final VerticalPanel objectVerticalPanel  =
	    buildSVOPanel(new SVOManager(object));
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

    private VerticalPanel buildSVOPanel(final SVOManager svoManager)
    {
	final VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	final Button button = 
	    new Button(svoManager.getExpandCollapseState(true));
	button.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		final String newState = svoManager.expandOrCollapse();
		final Button button = (Button) sender;
		button.setText(newState);
	    }
	});
	verticalPanel.add(button);
	// TODO: Would like a scroll or a cloud
	final ScrollPanel scrollPanel =
	    new ScrollPanel(svoManager.getWidget());
	scrollPanel.setStyleName(subjectVerbObject);
	verticalPanel.add(scrollPanel);
	return verticalPanel;
    }
}

class SVOItem
{
    String svoCategory;
    String expandedName;
    String collapsedName;
    String expandCollapseState;
    SVOItem(String svoCategory, String expandedName, String collapsedName,
	    String expandCollapseState)
    {
	this.svoCategory = svoCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
	this.expandCollapseState = expandCollapseState;
    }
    String getSVOCategory() { return svoCategory; }
    String getExpandedName() { return expandedName; }
    String getCollapsedName() { return collapsedName; }
    String getExpandCollapseState() { return expandCollapseState; }
    void   setExpandCollapseState(String x) { expandCollapseState = x; }
}

class SVOManager
{
    final String svoCategory; // For debug only.
    final List contents;
    final VerticalPanel widget;
    String expandCollapseState;

    SVOManager(final String svoCategory)
    {
	this.svoCategory = svoCategory;
	// TODO: Get from service.
	this.contents = fakeServerInitialization(svoCategory, Main.collapse);
	this.widget = new VerticalPanel();
	this.expandCollapseState = Main.expand;
	// This call will reverse initialization value in previous line.
	expandOrCollapse();
    }

    Widget getWidget()
    {
	return widget;
    }

    String getExpandCollapseState(final boolean oppositeP)
    {
	if (expandCollapseState.equals(Main.expand)) {
	    return (oppositeP ? Main.collapse : Main.expand);
	} else {
	    return (oppositeP ? Main.expand : Main.collapse);
	}
    }

    String expandOrCollapse()
    {
	widget.clear();
	expandCollapseState = getExpandCollapseState(true);

	final Iterator i = contents.iterator();
	while (i.hasNext()) {
	    final SVOItem svoItem = (SVOItem) i.next();
	    final Button button = new Button();
	    final Hyperlink hyperlink =
		new Hyperlink(svoItem.getExpandedName(),
			      svoItem.getSVOCategory()
			      + " " + svoItem.getExpandedName());
	    // Begin layout.
	    final HorizontalPanel horizontalPanel = new HorizontalPanel();
	    horizontalPanel.add(button);
	    horizontalPanel.add(hyperlink);
	    final VerticalPanel verticalPanel = new VerticalPanel();
	    verticalPanel.add(horizontalPanel);
	    widget.add(verticalPanel);
	    // End layout.

	    if (expandCollapseState.equals(Main.expand)) {
		if (svoItem.getExpandCollapseState().equals(Main.expand)) {
		    hyperlink.setText(svoItem.getExpandedName());
		    button.setText(Main.minusSymbol);
		} else {
		    hyperlink.setText(svoItem.getExpandedName());
		    button.setText(Main.plusSymbol);
		}
	    } else {
		if (svoItem.getExpandCollapseState().equals(Main.expand)) {
		    hyperlink.setText(svoItem.getExpandedName());
		    button.setText(Main.minusSymbol);
		} else {
		    hyperlink.setText(svoItem.getCollapsedName());
		    button.setText(Main.plusSymbol);
		}
	    }
	    hyperlink.addClickListener(new ClickListener() {
		public void onClick(final Widget sender) {
		    // TODO: Send to server.  Receive updates for other panels.
		    Main.lbl.setText(((Hyperlink)sender).getTargetHistoryToken());
		}
	    });
	    button.addClickListener(new ClickListener() {
	        public void onClick(Widget sender) {
		    final Button button = (Button) sender;
		    // TODO: Triple expand/collapse:
		    // Expand to URL and N "characters" of source.
		    // Expand to URL and frame of full source.
		    // TODO: manage item state like category state
		    if (button.getText().equals(Main.plusSymbol)) {
			verticalPanel.add(new Frame(svoItem.getExpandedName()));
			svoItem.setExpandCollapseState(Main.collapse);
			button.setText(Main.minusSymbol);
		    } else {
			Widget w = verticalPanel.getWidget(1);
			verticalPanel.remove(w);
			svoItem.setExpandCollapseState(Main.expand);
			button.setText(Main.plusSymbol);
		    }
		}});
	}
	return expandCollapseState;
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
	return x.substring(indexOfLastSlash + 1);
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
