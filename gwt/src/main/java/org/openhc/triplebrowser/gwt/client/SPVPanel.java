//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 21:10:14 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openhc.trowser.gwt.client.Main;

public class SPVPanel
{
    String expandCollapseState;

    private final String spvCategory;
    private List  contents;
    private final ListBox listBoxInsideScroll;
    private final VerticalPanel topVerticalPanel;
    private final Button topButton;

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
	listBoxInsideScroll = new ListBox();
	listBoxInsideScroll.setVisibleItemCount(10);
	listBoxInsideScroll.addChangeListener(new ChangeListener() {
	    public void onChange(Widget ignored) {
		final String choice =
		    ((SPVItem)contents.get(
					   listBoxInsideScroll.getSelectedIndex()))
		        .getExpandedName();
		Main.getMainPanel().spvLinkClicked(
		    spvCategory, choice);
		Main.getMainPanel().getFrameCurrentSelection()
		    .setUrl(choice);
	    }
	});
	listBoxInsideScroll.setStyleName(Main.subjectPropertyValue);
	listBoxInsideScroll.setVisibleItemCount(100);

	ScrollPanel scrollPanel = new ScrollPanel(listBoxInsideScroll);
	scrollPanel.setStyleName(Main.subjectPropertyValue);
	
	//topVerticalPanel.add(listBoxInsideScroll);
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
				   substringAfterLastSlashOrFirstSharp(uri)));
	}
	return result;
    }


    // This is a calledback from an async request to server.
    public void setContents(final List x)
    {
	contents = convertContents(x);
	listBoxInsideScroll.clear();
	final Iterator i = contents.iterator();
	while (i.hasNext()) {
	    final SPVItem spvItem = (SPVItem) i.next();
	    listBoxInsideScroll.addItem(
                (expandCollapseState.equals(Main.expand) ?
		 spvItem.getExpandedName() :
		 spvItem.getCollapsedName()));
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	//final Iterator i = listBoxInsideScroll.iterator();
	final Iterator j = contents.iterator();
	int n = 0;
	while (j.hasNext()) {
	    // TODO: redundant i.next - just need to make sure i and j
	    // are same size.
	    //final Object object = i.next();
	    final SPVItem spvItem = (SPVItem) j.next();
	    if (pendingExpandCollapseState.equals(Main.expand)) {
		listBoxInsideScroll.setItemText(n, spvItem.getExpandedName());
	    } else {
		listBoxInsideScroll.setItemText(n, spvItem.getCollapsedName());
	    }
	    n++;
	}
	expandCollapseState = pendingExpandCollapseState;
	return getPendingExpandCollapseState();
    }

    private String substringAfterLastSlashOrFirstSharp(final String x)
    {
	int indexOfLastSlashOrFirstSharp = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/' || x.charAt(i) == '#') {
		indexOfLastSlashOrFirstSharp = i;
	    }
	}
	final String result = x.substring(indexOfLastSlashOrFirstSharp + 1);
	// If it ends in a slash then remove the ending slash and try again.
	if (result.length() == 0) {
	    return substringAfterLastSlashOrFirstSharp(
                       x.substring(0, x.length()-1));
	} else {
	    return result;
	}
    }
}

// End of file.
