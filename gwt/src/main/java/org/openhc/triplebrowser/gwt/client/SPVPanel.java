//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 27 (Tue) 10:37:29 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.openhc.trowser.gwt.client.Main;
import org.openhc.trowser.gwt.common.Util;

public class SPVPanel
{
    String expandCollapseState;

    private final String spvCategory;
    private List  contents;
    private final VerticalPanel verticalInsideScroll;
    private final VerticalPanel topVerticalPanel;
    private final Button topButton;
    private final ScrollPanel scrollPanel;

    public SPVPanel(final String spvCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.spvCategory = spvCategory;

	// Begin layout.
	topVerticalPanel = new VerticalPanel();
	topVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	topButton = new Button(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new VerticalPanel();



	scrollPanel = new ScrollPanel(verticalInsideScroll);
	scrollPanel.setStyleName(Main.subjectPropertyValue);
	topVerticalPanel.add(scrollPanel);
	// End layout.
	
	topButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		expandOrCollapseSPVClick(true);
	    }
	});
    }

    public void expandOrCollapseSPVClick(final boolean keepHistory)
    {
	final String newState = expandOrCollapse();
	topButton.setText(newState);
    }

    public VerticalPanel getPanel() { return topVerticalPanel; }

    private String getCurrentExpandCollapseState()
    {
	return Main.getExpandCollapseState(expandCollapseState, false);
    }
    private String getPendingExpandCollapseState()
    {
	return Main.getExpandCollapseState(expandCollapseState, true);
    }

    private List convertContents(final List x)
    {
	final Iterator i = ((List)x).iterator();
	final List result = new ArrayList();
	while (i.hasNext()) {
	    String uri = (String) i.next();
	    result.add(new SPVItem(spvCategory, 
				   uri,
				   Util.substringAfterLastSlashOrFirstSharp(uri)));
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

	    if (expandCollapseState.equals(Main.expand)) {
		spvItem.getLabel().setText(spvItem.getExpandedName());
	    }

	    spvItem.getLabel().addClickListener(new ClickListener() {
		public void onClick(final Widget sender) {
		    // Causes doQuery so no history necessary here.
		    Main.getMainPanel().spvLinkClicked(
		        spvCategory, spvItem.getExpandedName());

		    Main.getMainPanel().getFrameCurrentSelection()
			.setUrl(spvItem.getExpandedName());
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
	    if (pendingExpandCollapseState.equals(Main.expand)) {
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
