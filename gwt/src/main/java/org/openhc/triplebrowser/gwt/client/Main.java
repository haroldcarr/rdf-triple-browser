//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jun 14 (Wed) 23:46:20 by Harold Carr.
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
    private static String collapse = "collapse";
    private static String copyright = "copyright 2006";
    private static String differentityDotCom = "differentity.com";
    private static String expand   = "expand";
    private static String object = "object";
    private static String subject = "subject";
    private static String subjectVerbObject = "subjectVerbObject";
    private static String verb = "verb";

    public static final Label lbl = new Label("XXX"); // XXX

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

	//
	// Subject, Verb, Object panels.
	//

	Widget[] result = null;
	result = buildSVOPanel(new SVOManager(subject));
	VerticalPanel subjectVerticalPanel = (VerticalPanel) result[0];
	Button subjectExpandCollapseButton = (Button) result[1];

	result = buildSVOPanel(new SVOManager(verb));
	VerticalPanel verbVerticalPanel    = (VerticalPanel) result[0];
	Button verbExpandCollapseButton    = (Button) result[1];

	result = buildSVOPanel(new SVOManager(object));
	VerticalPanel objectVerticalPanel  = (VerticalPanel) result[0];
	Button objectExpandCollapseButton  = (Button) result[1];

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

    private Widget[] buildSVOPanel(final SVOManager svoManager)
    {
	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	Button button = new Button(expand); // STATE
	button.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		Button x = (Button) sender;
		if (x.getText().equals(collapse)) {
		    svoManager.collapse();
		    x.setText(expand);
		} else {
		    svoManager.expand();
		    x.setText(collapse);
		}
	    }
	});
	verticalPanel.add(button);
	ScrollPanel scrollPanel = new ScrollPanel(svoManager.getWidget());
	scrollPanel.setStyleName(subjectVerbObject);
	verticalPanel.add(scrollPanel);
	return new Widget[] { verticalPanel,  button };
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
	this.contents = svoList;
	this.widget = new VerticalPanel();
	collapse();
    }

    Widget getWidget()
    {
	return widget;
    }

    void expand()
    {
	expandCollapse(true);
    }

    void collapse()
    {
	expandCollapse(false);
    }

    private void expandCollapse(boolean isExpand)
    {
	widget.clear();

	Iterator i = contents.iterator();
	while (i.hasNext()) {
	    HorizontalPanel horizontalPanel = new HorizontalPanel();
	    Button button = new Button("+");
	    button.addClickListener(new ClickListener() {
	        public void onClick(Widget sender) {
		    Button x = (Button) sender;
		    if (x.getText().equals("+")) {
			x.setText("-");
		    } else {
			x.setText("+");
		    }
		}});
	    horizontalPanel.add(button);
	    String item = (String) i.next();
	    Hyperlink hyperlink = new Hyperlink(item, name + " " + item);
	    horizontalPanel.add(hyperlink);
	    if (! isExpand) {
		hyperlink.setText(lastSlash(item));
	    }
	    hyperlink.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    Main.lbl.setText(((Hyperlink)sender).getTargetHistoryToken());
		}
	    });
	    widget.add(horizontalPanel);
	}
    }

    private String lastSlash(String x)
    {
	int lastSlash = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/') {
		lastSlash = i;
	    }
	}
	return x.substring(lastSlash + 1);
    }

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
