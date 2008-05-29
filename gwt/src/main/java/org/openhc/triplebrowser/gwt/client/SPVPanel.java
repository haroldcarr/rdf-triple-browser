//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 29 (Thu) 10:46:30 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.openhc.trowser.gwt.client.Main;
import org.openhc.trowser.gwt.common.Util;

public class SPVPanel
{
    private final Main            main;
    private final SPVList         subjectPanel;
    private final SPVList         propertyPanel;
    private final SPVList         valuePanel;
    private final VerticalPanel   subjectVerticalPanel;
    private final VerticalPanel   propertyVerticalPanel;
    private final VerticalPanel   valueVerticalPanel;
    private final HorizontalPanel spvHorizontalPanel;

    public SPVPanel(Main main)
    {
	this.main = main;

	subjectPanel          = new SPVList(main.subject, main);
	propertyPanel         = new SPVList(main.property, main);
	valuePanel            = new SPVList(main.value, main);
	subjectVerticalPanel  = subjectPanel.getPanel();
	propertyVerticalPanel = propertyPanel.getPanel();
	valueVerticalPanel    = valuePanel.getPanel();
	spvHorizontalPanel    = new HorizontalPanel();
	spvHorizontalPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	spvHorizontalPanel.add(subjectVerticalPanel);
	spvHorizontalPanel.add(propertyVerticalPanel);
	spvHorizontalPanel.add(valueVerticalPanel);
    }

    public HorizontalPanel getPanel()         { return spvHorizontalPanel; }
    public SPVList         getSubjectPanel()  { return subjectPanel; }
    public SPVList         getPropertyPanel() { return propertyPanel; }
    public SPVList         getValuePanel()    { return valuePanel; }
}

class SPVList
{
    String expandCollapseState;

    private final Main main;
    private final String spvCategory;
    private       List  contents;
    private final VerticalPanel verticalInsideScroll;
    private final VerticalPanel topVerticalPanel;
    private final Button topButton;
    private final ScrollPanel scrollPanel;

    public SPVList(final String spvCategory, final Main main)
    {
	this.main = main;

	this.expandCollapseState = main.collapse;

	this.spvCategory = spvCategory;

	// Begin layout.
	topVerticalPanel = new VerticalPanel();
	topVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	topButton = new Button(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new VerticalPanel();



	scrollPanel = new ScrollPanel(verticalInsideScroll);
	scrollPanel.setStyleName(main.subjectPropertyValue);
	topVerticalPanel.add(scrollPanel);
	// End layout.
	
	topButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		expandOrCollapseSPVClick();
	    }
	});
    }

    public void expandOrCollapseSPVClick()
    {
	final String newState = expandOrCollapse();
	topButton.setText(newState);
    }

    public VerticalPanel getPanel() { return topVerticalPanel; }

    private String getCurrentExpandCollapseState()
    {
	return main.getUtil()
	    .getExpandCollapseState(expandCollapseState, false);
    }

    private String getPendingExpandCollapseState()
    {
	return main.getUtil()
	    .getExpandCollapseState(expandCollapseState, true);
    }

    private List convertContents(final List x)
    {
	final Iterator i = ((List)x).iterator();
	final List result = new ArrayList();
	while (i.hasNext()) {
	    String uri = (String) i.next();
	    result.add(new SPVItem(spvCategory, 
				   uri,
				   main.getUtil().substringAfterLastSlashOrFirstSharp(uri)));
	}
	return result;
    }


    // This is a calledback from an async request to server.
    public void setContents(final List x)
    {
	contents = convertContents(x);
	verticalInsideScroll.clear();
	final Iterator i = contents.iterator();
	while (i.hasNext()) {
	    final SPVItem spvItem = (SPVItem) i.next();
	    verticalInsideScroll.add(spvItem.getLabel());

	    spvItem.getLabel().setText(
                expandCollapseState.equals(main.expand) ?
		spvItem.getExpandedName() :
		spvItem.getCollapsedName());

	    spvItem.getLabel().addClickListener(new ClickListener() {
		public void onClick(final Widget sender) {
		    // Causes doQuery so no history necessary here.
		    main.getQueryManager().spvLinkClicked(
		        spvCategory, spvItem.getExpandedName());

		    main.getWebBrowser().setUrl(spvItem.getExpandedName());
		}
	    });
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	final Iterator i = verticalInsideScroll.iterator();
	final Iterator j = contents.iterator();
	while (i.hasNext()) {
	    // TODO: redundant i.next - just need to make sure i and j
	    // are same size.
	    final Object object = i.next();
	    final SPVItem spvItem = (SPVItem) j.next();
	    if (pendingExpandCollapseState.equals(main.expand)) {
		spvItem.getLabel().setText(spvItem.getExpandedName());
	    } else {
		spvItem.getLabel().setText(spvItem.getCollapsedName());
	    }
	}
	expandCollapseState = pendingExpandCollapseState;
	return getPendingExpandCollapseState();
    }
}

// End of file.
