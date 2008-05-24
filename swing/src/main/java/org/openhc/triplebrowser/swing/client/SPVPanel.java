//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 08:47:50 by Harold Carr.
//

package client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Main;

public class SPVPanel
{
    private final SPVList subjectPanel;
    private final SPVList propertyPanel;
    private final SPVList valuePanel;
    private final JPanel spvHorizontalPanel;

    public SPVPanel()
    {
	//
	// Subject, Property, Value panels.
	// Create now to get contents from server.
	//

	subjectPanel  = new SPVList(Main.subject);
	propertyPanel = new SPVList(Main.property);
	valuePanel    = new SPVList(Main.value);
	spvHorizontalPanel = new JPanel();
	Main.getSwingView().spvPanelLayout(spvHorizontalPanel,
					   subjectPanel.getButton(),
					   subjectPanel.getScrollPane(),
					   propertyPanel.getButton(),
					   propertyPanel.getScrollPane(),
					   valuePanel.getButton(),
					   valuePanel.getScrollPane());
    }

    public JPanel  getPanel()         { return spvHorizontalPanel; }
    public SPVList getSubjectPanel()  { return subjectPanel; }
    public SPVList getPropertyPanel() { return propertyPanel; }
    public SPVList getValuePanel()    { return valuePanel; }
}

//////////////////////////////////////////////////////////////////////////////

class SPVList
{
    String expandCollapseState;

    private final String      spvCategory;
    private       List        contents;
    private final JList       verticalInsideScroll;
    //private final JPanel      topVerticalPanel;
    private final JButton     topButton;
    private final JScrollPane scrollPanel;

    public SPVList(final String spvCategory)
    {
	this.expandCollapseState = Main.collapse;

	this.spvCategory = spvCategory;

	/*
	// Begin layout.
	topVerticalPanel = new JPanel();
	topVerticalPanel.setLayout(new BoxLayout(topVerticalPanel, 
						 BoxLayout.PAGE_AXIS));
	*/
	topButton = new JButton();
	topButton.setText(getPendingExpandCollapseState());
	/*
	topVerticalPanel.add(topButton);
	*/
	// TODO: Would like a scroll or a cloud
	DefaultListSelectionModel m = new DefaultListSelectionModel( );
        m.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m.setLeadAnchorNotificationEnabled(false);
	verticalInsideScroll = new JList(new DefaultListModel());
	
	verticalInsideScroll.setSelectionModel(m);

	verticalInsideScroll.addListSelectionListener(
	    new ListSelectionListener( ) {
		public void valueChanged(ListSelectionEvent e) {
		    if (e.getValueIsAdjusting() == false) {
			if (verticalInsideScroll.getSelectedIndex() != -1) {

			    // We only store strings now in the list
			    // (not labels) and the string might be collapsed
			    // so need to find full length match.
			    final String choice =
				((SPVItem)contents.get(verticalInsideScroll.getSelectedIndex())).getExpandedName();
			    // Causes doQuery so no history necessary here.
			    Main.getMainPanel().spvLinkClicked(spvCategory,
							       choice);

			    Main.getMainPanel().getFrameCurrentSelection()
			        .setUrl(choice);
			}
		    }
		}
	    });

	scrollPanel = new JScrollPane(verticalInsideScroll);
	scrollPanel.setName(Main.subjectPropertyValue);

	/*
	topVerticalPanel.add(scrollPanel);
	*/
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

    //public JPanel getPanel() { return topVerticalPanel; }
    public JButton getButton() { return topButton; }
    public JScrollPane getScrollPane() { return scrollPanel; }

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
				   substringAfterLastSlashOrFirstSharp(uri)));
	}
	return result;
    }


    // This is a calledback from an async request to server.
    public void setContents(final List x)
    {
	contents = convertContents(x);
	((DefaultListModel)verticalInsideScroll.getModel()).clear();
	final Iterator i = contents.iterator();
	int n = 0;
	while (i.hasNext()) {
	    final SPVItem spvItem = (SPVItem) i.next();

	    ((DefaultListModel)verticalInsideScroll.getModel())
		.add(n++, 
		     (expandCollapseState.equals(Main.expand) ?
		      spvItem.getExpandedName() :
		      spvItem.getCollapsedName()));
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	final Enumeration i = 
	    ((DefaultListModel)verticalInsideScroll.getModel()).elements();
	final Iterator j = contents.iterator();
	int n = 0;
	while (i.hasMoreElements()) {
	    // TODO: redundant i.next - just need to make sure i and j
	    // are same size.
	    final Object object = i.nextElement();
	    final SPVItem spvItem = (SPVItem) j.next();
	    if (pendingExpandCollapseState.equals(Main.expand)) {
		((DefaultListModel)verticalInsideScroll.getModel())
		    .set(n, spvItem.getExpandedName());
	    } else {
		((DefaultListModel)verticalInsideScroll.getModel())
		    .set(n, spvItem.getCollapsedName());
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

//////////////////////////////////////////////////////////////////////////////

class SPVItem
{
    private final String spvCategory;
    private final String expandedName;
    private final String collapsedName;
    
    SPVItem(final String spvCategory, 
	    final String expandedName, 
	    final String collapsedName)
    {
	this.spvCategory = spvCategory;
	this.expandedName = expandedName;
	this.collapsedName = collapsedName;
    }

    String getSPVCategory()              { return spvCategory; }
    String getExpandedName()             { return expandedName; }
    String getCollapsedName()            { return collapsedName; }
}

// End of file.
