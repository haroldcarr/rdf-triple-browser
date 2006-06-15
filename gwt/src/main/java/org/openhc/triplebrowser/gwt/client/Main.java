//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jun 14 (Wed) 21:26:03 by Harold Carr.
//

package com.differentity.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

// From layout
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

// XXX
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main 
    implements 
	EntryPoint,
	HistoryListener // XXX
{
    private static String collapse = "collapse";
    private static String expand   = "expand";
    private static String differentityDotCom = "differentity.com";
    private static String copyright = "copyright 2006";
    private static String subject = "subject";
    private static String verb = "verb";
    private static String object = "object";
    private static String subjectVerbObject = "subjectVerbObject";

    private Label lbl = new Label(); // XXX

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

	//
	// BEGIN XXX - Hyperlink test
	//

	// Create three hyperlinks that change the application's history.

	Hyperlink link0 = new Hyperlink("link to foo", "foo");
	link0.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    lbl.setText("DDD");
		}
	    });
	Hyperlink link1 = new Hyperlink("link to bar", "bar");
	Hyperlink link2 = new Hyperlink("link to baz", "baz");

	// If the application starts with no history token, start it off in the
	// 'baz' state.
	String initToken = History.getToken();
	if (initToken.length() == 0)
	    initToken = "baz";

	// onHistoryChanged() is not called when the application first runs.
	// Call it now in order to reflect the initial state.
	onHistoryChanged(initToken);

	// Add them.
	FlowPanel panel = new FlowPanel();
	panel.add(lbl); panel.add(link0); panel.add(link1); panel.add(link2);
	RootPanel.get("slot2").add(panel);

	//
	// END XXX - Hyperlink test
	//

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	RootPanel.get("slot2").add(dockPanel);

    }

    // XXX
    public void onHistoryChanged(String historyToken) {
	// This method is called whenever the application's history changes.
	// Set the label to reflect the current history token.
	lbl.setText("The current history token is: " + historyToken);
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
    HTML html;

    SVOManager(String name)
    {
	this.name = name;
	this.contents = svoList;
	this.html = new HTML();
	collapse();
    }

    HTML getWidget()
    {
	return html;
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
	Iterator i = contents.iterator();
	StringBuffer result = new StringBuffer();
	while (i.hasNext()) {
	    String item = (String) i.next();
	    if (isExpand) {
		result.append(item);
	    } else {
		result.append(lastSlash(item));
	    }
	    result.append("<br/>");
	}
	html.setHTML(result.toString());
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
