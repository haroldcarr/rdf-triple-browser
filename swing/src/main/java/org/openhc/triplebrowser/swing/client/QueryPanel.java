//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 10:52:24 by Harold Carr.
//

package org.openhc.trowser.swing.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class QueryPanel
{
    private final Main        main;

    private final JPanel      queryPanel;
    private final ButtonGroup buttonGroup;

    // These should be final
    private       JRadioButton baseRadioButton;
    private       JTextField   baseSubjectTextField;
    private       JTextField   baseProperyTextField;
    private       JTextField   baseValueTextField;

    private       JTextField  selectedSubjectTextField;
    private       JTextField  selectedPropertyTextField;
    private       JTextField  selectedValueTextField;

    private       int         triplePanelID = 0;

    QueryPanel(Main main)
    {
	this.main = main;

	buttonGroup = new ButtonGroup();
	queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, 
					      BoxLayout.PAGE_AXIS));
	addToVerticalPanel(makeTriplePanel());
    }

    private void addToVerticalPanel(JPanel triplePanel)
    {
	main.getTrowserLayout().addTriplePanel(queryPanel, triplePanel);
    }

    public void removeFromVerticalPanel(JPanel triplePanel)
    {
	Component[] components = queryPanel.getComponents();
	for (int i = 0; i < components.length; i++) {
	    if (triplePanel == components[i]) {
		queryPanel.remove(components[i]);
		break;
	    }
	}
	addToVerticalPanel(null);
    }

    JTextField getSubjectTextField()  { return selectedSubjectTextField; }
    JTextField getPropertyTextField() { return selectedPropertyTextField; }
    JTextField getValueTextField()    { return selectedValueTextField; }
    JPanel     getPanel()             { return queryPanel; }

    //////////////////////////////////////////////////

    private JPanel makeTriplePanel()
    {
	final JPanel       triplePanel   = new JPanel();
	final JButton      leftButton;
	final JRadioButton radioButton       = new JRadioButton();
	final JMenuBar     subjectJMenuBar;
	final JTextField   subjectTextField  = new JTextField();
	final JMenuBar     propertyJMenuBar;
	final JTextField   propertyTextField = new JTextField();
	final JMenuBar     valueJMenuBar;
	final JTextField   valueTextField    = new JTextField();

	triplePanel.setName("triplePanel-" + ++triplePanelID);

	buttonGroup.add(radioButton); // Only one can be selected.

	// The latest created is always selected.
	radioButton.setEnabled(true);
	radioButton.setSelected(true);
	selectedSubjectTextField  = subjectTextField;
	selectedPropertyTextField = propertyTextField;
	selectedValueTextField    = valueTextField;

	if (queryPanel.getComponentCount() == 0) {
	    leftButton = new JButton("+");
	    leftButton.setText("+");
	    leftButton.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    addToVerticalPanel(makeTriplePanel());
		}});
	    baseRadioButton      = radioButton;
	    baseSubjectTextField = subjectTextField;
	    baseProperyTextField = propertyTextField;
	    baseValueTextField   = valueTextField;
	} else {
	    leftButton = new JButton("-");
	    leftButton.setText("-");
	    leftButton.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    removeFromVerticalPanel(triplePanel);
		    buttonGroup.remove(radioButton);
		    baseRadioButton.setSelected(true);
		    selectedSubjectTextField  = baseSubjectTextField;
		    selectedPropertyTextField = baseProperyTextField;
		    selectedValueTextField    = baseValueTextField;
		}});
	}

	radioButton.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		selectedSubjectTextField  = subjectTextField;
		selectedPropertyTextField = propertyTextField;
		selectedValueTextField    = valueTextField;
	    }});
		
	subjectTextField.setText(main.qsubject);
	propertyTextField.setText(main.qproperty);
	valueTextField.setText(main.qvalue);
	subjectJMenuBar  = 
	    makeJMenuBar(main.qvalue,    valueTextField,
			 main.qsubject,  subjectTextField,
			 main.qproperty, propertyTextField);
	propertyJMenuBar =
	    makeJMenuBar(main.qsubject,  subjectTextField,
			 main.qproperty, propertyTextField,
			 main.qvalue,    valueTextField);
	valueJMenuBar    =
	    makeJMenuBar(main.qproperty, propertyTextField,
			 main.qvalue,    valueTextField,
			 main.qsubject,  subjectTextField);

	main.getTrowserLayout().queryPanelLayout(
            triplePanel,
	    leftButton, radioButton,
	    subjectJMenuBar, subjectTextField,
	    propertyJMenuBar, propertyTextField,
	    valueJMenuBar, valueTextField);

	return triplePanel;
    }

    private JMenuBar makeJMenuBar(final String     leftText,
				  final JTextField leftTextField,
				  final String     thisText,
				  final JTextField thisTextField,
				  final String     rightText,
				  final JTextField rightTextField)
    {
	final ActionListener clearCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		thisTextField.setText(thisText);
		main.getQueryManager().doQuery(true);
                }
            };

	final ActionListener showAllCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		main.getQueryManager()
		    .doQuery(true,
			     main.qsubject, main.qproperty,
			     main.qvalue, thisText);
	    }
	};

	final ActionListener moveLeftCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		final String text = thisTextField.getText();
		thisTextField.setText(thisText);
		leftTextField.setText(text);
		main.getQueryManager().doQuery(true);
	    }
	};

	final ActionListener moveRightCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		final String text = thisTextField.getText();
		thisTextField.setText(thisText);
		rightTextField.setText(text);
		main.getQueryManager().doQuery(true);
	    }
	};

	final JMenuBar menuBar = new JMenuBar();
	final JMenu menu = new JMenu();
	menu.setText("v");
	menuBar.add(menu);

	JMenuItem menuItem;

	menuItem = new JMenuItem(main.clear);
	menuItem.addActionListener(clearCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(main.showAll);
	menuItem.addActionListener(showAllCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(main.shiftRight);
	menuItem.addActionListener(moveRightCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(main.shiftLeft);
	menuItem.addActionListener(moveLeftCommand);
	menu.add(menuItem);

	return menuBar;
    }
}

// End of file.
