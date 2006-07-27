//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 26 (Wed) 16:55:29 by Harold Carr.
//

package com.differentity.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.differentity.client.Main;

public class SVOPanel
{
    String expandCollapseState;

    final String svoCategory; // For debug only.
    List contents; // The "raw" contents.  For debugging.
    final VerticalPanel verticalInsideScroll;
    final VerticalPanel topVerticalPanel;
    final Button topButton;
    final ScrollPanel scrollPanel;

    SVOPanel(final String svoCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.svoCategory = svoCategory;

	// Begin layout.
	topVerticalPanel = new VerticalPanel();
	topVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	topButton = new Button(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new VerticalPanel();

	// Do the async call now that what it depends when it returns
	// is available.
	// "this" so that it can callback to "initialContents".
	Main.serverCalls.getInitialContents(this, svoCategory);

	scrollPanel = new ScrollPanel(verticalInsideScroll);
	scrollPanel.setStyleName(Main.subjectVerbObject);
	topVerticalPanel.add(scrollPanel);
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

    // This is calledback from an async request to server.
    public void initialContents(final List contents)
    {
	this.contents = contents;
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
		    Main.serverCalls.svoLinkClicked(((Hyperlink)sender).getTargetHistoryToken());
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
}

// End of file.
