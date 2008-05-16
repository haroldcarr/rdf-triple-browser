//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 15 (Thu) 12:40:36 by Harold Carr.
//

package client;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import client.Main;

public class SPVPanel
{
    String expandCollapseState;

    private final String      spvCategory;
    private       List        contents;
    private final JList       verticalInsideScroll;
    private final JPanel      topVerticalPanel;
    private final JButton     topButton;
    private final JScrollPane scrollPanel;

    public SPVPanel(final String spvCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.spvCategory = spvCategory;

	// Begin layout.
	topVerticalPanel = new JPanel();
	topVerticalPanel.setLayout(new BoxLayout(topVerticalPanel, 
						 BoxLayout.PAGE_AXIS));
	topButton = new JButton(getPendingExpandCollapseState());
	topVerticalPanel.add(topButton);
	// TODO: Would like a scroll or a cloud
	verticalInsideScroll = new JList();



	scrollPanel = new JScrollPane(verticalInsideScroll);
	scrollPanel.setName(Main.subjectPropertyValue);
	topVerticalPanel.add(scrollPanel);
	// End layout.

	topButton.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		expandOrCollapseSPVClick(true);	
	    }
	});
    }

    public void expandOrCollapseSPVClick(final boolean keepHistory)
    {
	final String newState = expandOrCollapse();
	topButton.setText(newState);
    }

    public JPanel getPanel() { return topVerticalPanel; }

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
				   substringAfterLastSlashOrFirstSharp(uri),
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
	verticalInsideScroll.removeAll();
	final Iterator i = contents.iterator();
	while (i.hasNext()) {
	    final SPVItem spvItem = (SPVItem) i.next();
	    verticalInsideScroll.add(spvItem.getLabel());

	    if (expandCollapseState.equals(Main.expand)) {
		spvItem.getLabel().setText(spvItem.getExpandedName());
	    }

	    spvItem.getLabel().addMouseListener(new MouseAdapter() {
	        public void mouseClicked(MouseEvent e) {
		    // Causes doQuery so no history necessary here.
		    Main.getMainPanel().spvLinkClicked(
		        spvCategory, spvItem.getExpandedName());

		    //Main.getMainPanel().getFrameCurrentSelection()
		    //    .setUrl(spvItem.getExpandedName());
		}
	    });
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	final Iterator i = Arrays.asList(verticalInsideScroll.getComponents()).iterator();
	final Iterator j = contents.iterator();
	while (i.hasNext()) {
	    // TODO: redundant i.next - just need to make sure i and j
	    // are same size.
	    final Object object = i.next();
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
