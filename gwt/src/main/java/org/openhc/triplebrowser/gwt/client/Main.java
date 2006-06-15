package com.differentity.client;

import java.util.ArrayList;
import java.util.Iterator;

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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main 
    implements 
	EntryPoint 
{
    private static String collapse = "collapse";
    private static String expand   = "expand";
    private static String differentityDotCom = "differentity.com";
    private static String copyright = "copyright 2006";
    private static String subjectVerbObject = "subjectVerbObject";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

	Button currentButton = null;
	HTML   currentHTML   = null;

	Widget[] result = null;
	result = buildSVOPanel();
	VerticalPanel subjectVerticalPanel = (VerticalPanel) result[0];
	Button subjectExpandCollapseButton = (Button) result[1];
	HTML subjectHTML                   = (HTML) result[2];

	result = buildSVOPanel();
	VerticalPanel verbVerticalPanel    = (VerticalPanel) result[0];
	Button verbExpandCollapseButton    = (Button) result[1];
	HTML verbHTML                      = (HTML) result[2];

	result = buildSVOPanel();
	VerticalPanel objectVerticalPanel  = (VerticalPanel) result[0];
	Button objectExpandCollapseButton  = (Button) result[1];
	HTML objectHTML                    = (HTML) result[2];

	HorizontalPanel horizontalPanel = new HorizontalPanel();
	horizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	horizontalPanel.add(subjectVerticalPanel);
	horizontalPanel.add(verbVerticalPanel);
	horizontalPanel.add(objectVerticalPanel);
    

	DockPanel dockPanel = new DockPanel();
	dockPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	HTML north = new HTML(differentityDotCom, true);
	HTML south = new HTML(copyright, true);
	dockPanel.add(north, DockPanel.NORTH);
	// NOTE: - if added after CENTER does not show up.
	dockPanel.add(south, DockPanel.SOUTH);
	dockPanel.add(horizontalPanel, DockPanel.CENTER);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	RootPanel.get("slot1").add(dockPanel);
    }

    private Widget[] buildSVOPanel()
    {
	VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	Button button = new Button(collapse);
	button.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		Button x = (Button) sender;
		if (x.getText().equals(collapse)) {
		    x.setText(expand);
		} else {
		    x.setText(collapse);
		}
	    }
	});
	verticalPanel.add(button);
	HTML html = buildSVO();
	ScrollPanel scrollPanel = new ScrollPanel(html);
	scrollPanel.setStyleName(subjectVerbObject);
	verticalPanel.add(scrollPanel);
	return new Widget[] { verticalPanel,  button, html };
    }

    private HTML buildSVO()
    {
	Iterator i = svoList.iterator();
	StringBuffer result = new StringBuffer();
	while (i.hasNext()) {
	    result.append(i.next());
	    result.append("<br/>");
	}
	return new HTML(result.toString());
    }

    ArrayList svoList = new ArrayList();
    { // NOTE: block needed for this to compile. 
      // And x to get result needed too.
    boolean x = true;
    x = svoList.add("http://differentity.com/haroldcarr/author");
    x = svoList.add("http://differentity.com/haroldcarr/authorPrefix");
    x = svoList.add("http://differentity.com/haroldcarr/authorFirstName");
    x = svoList.add("http://differentity.com/haroldcarr/authorMiddleName");
    x = svoList.add("http://differentity.com/haroldcarr/authorLastName");
    x = svoList.add("http://differentity.com/haroldcarr/authorSuffix");
    x = svoList.add("http://differentity.com/haroldcarr/authorBorn");
    x = svoList.add("http://differentity.com/haroldcarr/authorDied");
    x = svoList.add("http://differentity.com/haroldcarr/authorIdea");
    x = svoList.add("http://differentity.com/haroldcarr/author");
    x = svoList.add("http://differentity.com/haroldcarr/ideaInCategory");
    x = svoList.add("http://differentity.com/haroldcarr/work");
    x = svoList.add("http://differentity.com/haroldcarr/workAuthor");
    x = svoList.add("http://differentity.com/haroldcarr/workTitle");
    x = svoList.add("http://differentity.com/haroldcarr/workPublished");
    x = svoList.add("http://differentity.com/haroldcarr/workWritten");
    x = svoList.add("http://differentity.com/haroldcarr/bataille");
    x = svoList.add("http://differentity.com/haroldcarr/book");
    x = svoList.add("http://differentity.com/haroldcarr/guilty");
    x = svoList.add("http://differentity.com/haroldcarr/eroticism");
    x = svoList.add("http://differentity.com/haroldcarr/blue_of_noon");
    x = svoList.add("http://differentity.com/haroldcarr/inner_experience");

    x = svoList.add("http://differentity.com/haroldcarr/similarTo");
    x = svoList.add("http://differentity.com/haroldcarr/equalTo");
    x = svoList.add("http://differentity.com/haroldcarr/contraryTo");
    x = svoList.add("http://differentity.com/haroldcarr/contrarstWith");
    x = svoList.add("http://differentity.com/haroldcarr/relatesTo");
    x = svoList.add("http://differentity.com/haroldcarr/fate");
    x = svoList.add("http://differentity.com/haroldcarr/formlessness");
    x = svoList.add("http://differentity.com/haroldcarr/god");
    x = svoList.add("http://differentity.com/haroldcarr/stability");
    x = svoList.add("http://differentity.com/haroldcarr/reason");
    x = svoList.add("http://differentity.com/haroldcarr/nothingness");
    x = svoList.add("http://differentity.com/haroldcarr/chance");
    x = svoList.add("http://differentity.com/haroldcarr/strength");
    x = svoList.add("http://differentity.com/haroldcarr/death");
    x = svoList.add("http://differentity.com/haroldcarr/anguish");
    x = svoList.add("http://differentity.com/haroldcarr/silence");
    x = svoList.add("http://differentity.com/haroldcarr/limit");
    }
}
