//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 29 (Thu) 10:35:33 by Harold Carr.
//

package org.openhc.trowser.swing.client;

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

import org.openhc.trowser.gwt.common.SPVItem;
import org.openhc.trowser.gwt.common.Util;
import org.openhc.trowser.swing.client.Main;

public class SPVPanel
{
    private final Main    main;
    private final SPVList subjectPanel;
    private final SPVList propertyPanel;
    private final SPVList valuePanel;
    private final JPanel  spvHorizontalPanel;

    public SPVPanel(Main main)
    {
	this.main = main;

	subjectPanel  = new SPVList(main.subject, main);
	propertyPanel = new SPVList(main.property, main);
	valuePanel    = new SPVList(main.value, main);
	spvHorizontalPanel = new JPanel();
	main.getTrowserLayout().spvPanelLayout(spvHorizontalPanel,
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
    private        String      expandCollapseState;

    private final Main        main;
    private final String      spvCategory;
    private       List        contents;
    private final JList       verticalInsideScroll;
    private final JButton     topButton;
    private final JScrollPane scrollPanel;

    public SPVList(final String spvCategory, final Main main)
    {
	this.main = main;
	this.expandCollapseState = main.collapse;
	this.spvCategory = spvCategory;
	topButton = new JButton();
	topButton.setText(getPendingExpandCollapseState());

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
			    // Strings stored in list might be collapsed
			    // so need to find full length match.
			    final String choice =
				((SPVItem)contents.get(
				    verticalInsideScroll.getSelectedIndex()))
				        .getExpandedName();
			    main.getQueryManager().spvLinkClicked(
                                spvCategory, choice);
			    main.getBrowserPanel()
				.setUrl(choice);
			}
		    }
		}
	    });

	scrollPanel = new JScrollPane(verticalInsideScroll);
	scrollPanel.setName(main.subjectPropertyValue);

	topButton.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		expandOrCollapseSPVClick();	
	    }
	});
    }

    public void expandOrCollapseSPVClick()
    {
	final String newState = expandOrCollapse();
	topButton.setText(newState);
    }

    public JButton getButton()         { return topButton; }
    public JScrollPane getScrollPane() { return scrollPanel; }

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
	getModel().clear();
	final Iterator i = contents.iterator();
	int n = 0;
	while (i.hasNext()) {
	    final SPVItem spvItem = (SPVItem) i.next();
	    getModel().add(n++, (expandCollapseState.equals(main.expand) ?
				 spvItem.getExpandedName() :
				 spvItem.getCollapsedName()));
	}
    }

    String expandOrCollapse()
    {
	String pendingExpandCollapseState = getPendingExpandCollapseState();
	final Enumeration i = getModel().elements();
	final Iterator j = contents.iterator();
	int n = 0;
	while (i.hasMoreElements()) {
	    // TODO: redundant i.next - just need to make sure i and j
	    // are same size.
	    final Object object = i.nextElement();
	    final SPVItem spvItem = (SPVItem) j.next();
	    if (pendingExpandCollapseState.equals(main.expand)) {
		getModel().set(n, spvItem.getExpandedName());
	    } else {
		getModel().set(n, spvItem.getCollapsedName());
	    }
	    n++;
	}
	expandCollapseState = pendingExpandCollapseState;
	return getPendingExpandCollapseState();
    }

    private DefaultListModel getModel()
    {
	return (DefaultListModel) verticalInsideScroll.getModel();
    }
}

// End of file.
