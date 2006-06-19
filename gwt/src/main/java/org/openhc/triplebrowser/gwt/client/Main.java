//
// Created : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jun 19 (Mon) 14:28:27 by Harold Carr.
//

/*
  TODO:

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
    private static String expand             = "expand";
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

	VerticalPanel subjectVerticalPanel =
	    buildSVOPanel(new SVOManager(subject));
	VerticalPanel verbVerticalPanel    = 
	    buildSVOPanel(new SVOManager(verb));
	VerticalPanel objectVerticalPanel  =
	    buildSVOPanel(new SVOManager(object));
	HorizontalPanel horizontalPanel = new HorizontalPanel();
	horizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	horizontalPanel.add(subjectVerticalPanel);
	horizontalPanel.add(verbVerticalPanel);
	horizontalPanel.add(objectVerticalPanel);

	//
	// Main panel.
	//    

	DockPanel dockPanel = new DockPanel();
	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	HTML north = new HTML(differentityDotCom, true);
	HTML south = new HTML(copyright, true);
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
	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	Button button = new Button(expand); // STATE
	button.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		Button x = (Button) sender;
		svoManager.expandOrCollapse(x.getText());
		if (x.getText().equals(collapse)) {
		    x.setText(expand);
		} else {
		    x.setText(collapse);
		}
	    }
	});
	verticalPanel.add(button);
	// TODO: Would like a scroll or a cloud
	ScrollPanel scrollPanel = new ScrollPanel(svoManager.getWidget());
	scrollPanel.setStyleName(subjectVerbObject);
	verticalPanel.add(scrollPanel);
	return verticalPanel;
    }
}

class SVOManager
{
    String name;
    List contents;
    VerticalPanel widget;

    SVOManager(String name)
    {
	this.name = name;
	// TODO: Get from service.
	this.contents = svoList;
	this.widget = new VerticalPanel();
	expandOrCollapse(Main.collapse);
    }

    Widget getWidget()
    {
	return widget;
    }

    void expandOrCollapse(String expandCollapse)
    {
	widget.clear();

	Iterator i = contents.iterator();
	while (i.hasNext()) {
	    HorizontalPanel horizontalPanel = new HorizontalPanel();
	    Button button = new Button(Main.plusSymbol);
	    button.addClickListener(new ClickListener() {
	        public void onClick(Widget sender) {
		    Button x = (Button) sender;
		    // TODO: Triple expand/collapse:
		    // Expand to URL and N "characters" of source.
		    // Expand to URL and frame of full source.
		    if (x.getText().equals(Main.plusSymbol)) {
			x.setText(Main.minusSymbol);
		    } else {
			x.setText(Main.plusSymbol);
		    }
		}});
	    horizontalPanel.add(button);
	    String item = (String) i.next();
	    Hyperlink hyperlink = new Hyperlink(item, name + " " + item);
	    horizontalPanel.add(hyperlink);
	    if (expandCollapse.equals(Main.collapse)) {
		hyperlink.setText(substringAfterLastSlash(item));
	    }
	    hyperlink.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    // TODO: Send to server.  Receive updates for other panels.
		    Main.lbl.setText(((Hyperlink)sender).getTargetHistoryToken());
		}
	    });
	    widget.add(horizontalPanel);
	}
    }

    private String substringAfterLastSlash(String x)
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

    // TODO: replace with real list from server.
    private List svoList = new ArrayList();
    { // NOTE: block needed for this to compile. 
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
