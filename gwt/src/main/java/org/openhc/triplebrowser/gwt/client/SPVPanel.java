//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2007 Jun 05 (Tue) 17:49:45 by Harold Carr.
//

package com.differentity.client;

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

import com.differentity.client.Main;

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
	    result.add(
		       new SPVItem(spvCategory, 
				   uri,
				   substringAfterLastSlash(uri),
				   // NOTE: during development change to
				   // Main.expand to test full range.
				   Main.collapse));
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
	    verticalInsideScroll.add(spvItem.getVerticalPanel());

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

	    spvItem.getButton().addClickListener(new ClickListener() {
	        public void onClick(Widget sender) {
		    // TODO: Triple expand/collapse:
		    // + = expand to full URL and N "characters" of source.
		    // * = expand to full URL and all source.
		    // - = collapse to short URL and no source.
		    if (spvItem.getPendingExpandCollapseState().equals(Main.expand)) {
			spvItem.getLabel().setText(spvItem.getExpandedName());
			spvItem.getVerticalPanel().add(new Frame(spvItem.getExpandedName()));
			spvItem.setExpandCollapseState(Main.expand);
			spvItem.getButton().setText(Main.minusSymbol);
		    } else {
			Widget w = spvItem.getVerticalPanel().getWidget(1);
			spvItem.getVerticalPanel().remove(w);
			spvItem.getLabel().setText(spvItem.getCollapsedName());
			spvItem.setExpandCollapseState(Main.collapse);
			spvItem.getButton().setText(Main.plusSymbol);
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
	    final SPVItem spvItem = (SPVItem) j.next();
	    if (spvItem.getCurrentExpandCollapseState().equals(Main.collapse)){
		if (pendingExpandCollapseState.equals(Main.expand)) {
		    spvItem.getLabel().setText(spvItem.getExpandedName());
		} else {
		    spvItem.getLabel().setText(spvItem.getCollapsedName());
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
}

// End of file.
