//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 24 (Sat) 11:07:01 by Harold Carr.
//

package client;

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

    QueryPanel()
    {
	buttonGroup = new ButtonGroup();
	queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, 
					      BoxLayout.PAGE_AXIS));
	addToVerticalPanel(makeTriplePanel());
    }

    private void addToVerticalPanel(JPanel triplePanel)
    {
	System.out.println("---------------------------");
	Component[] components = queryPanel.getComponents();
	System.out.println("addToVerticalPanel: Number of components before: " + components.length);
	switch (components.length) {
	case 0 :
	    Main.getSwingView().xxx1(queryPanel, 
				     triplePanel);
	    break;
	case 1 :
	    Main.getSwingView().xxx2(queryPanel, 
				     (JPanel) components[0],
				     triplePanel);
	    break;
	case 2 :
	    Main.getSwingView().xxx3(queryPanel, 
				     (JPanel) components[0],
				     (JPanel) components[1],
				     triplePanel);
	    break;
	default :
	    System.out.println("CAN'T ADD MORE: " + components.length);
	    break;
	}
	System.out.println("addToVerticalPanel: Number of components after: " + components.length);

    }

    public void removeFromVerticalPanel(JPanel triplePanel)
    {
	Component[] components = queryPanel.getComponents();
	System.out.println("---------------------------");
	System.out.println("removeFromVerticalPanel: Number of components before: " + components.length);
	switch (components.length) {
	case 2 :
	    if (triplePanel == components[1]) {
		queryPanel.remove(components[1]);
		Main.getSwingView().xxx1(queryPanel, 
					 (JPanel) components[0]);
	    }
	    break;
	case 3 :
	    if (triplePanel == components[1]) {
		queryPanel.remove(components[1]);
		Main.getSwingView().xxx2(queryPanel, 
					 (JPanel) components[0],
					 (JPanel) components[2]);

	    } else if (triplePanel == components[2]) {
		queryPanel.remove(components[2]);
		Main.getSwingView().xxx2(queryPanel, 
					 (JPanel) components[0],
					 (JPanel) components[1]);

	    }
	    break;
	case 4 :
	    System.out.println("CAN'T REMOVE: " + components.length);
	    break;
	default: 
	}
	System.out.println("removeFromVerticalPanel: Number of components after: " + components.length);
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
		    //window.pack();
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
		
	subjectTextField.setText(Main.qsubject);
	propertyTextField.setText(Main.qproperty);
	valueTextField.setText(Main.qvalue);
	subjectJMenuBar  = 
	    makeJMenuBar(Main.qvalue,    valueTextField,
			 Main.qsubject,  subjectTextField,
			 Main.qproperty, propertyTextField);
	propertyJMenuBar =
	    makeJMenuBar(Main.qsubject,  subjectTextField,
			 Main.qproperty, propertyTextField,
			 Main.qvalue,    valueTextField);
	valueJMenuBar    =
	    makeJMenuBar(Main.qproperty, propertyTextField,
			 Main.qvalue,    valueTextField,
			 Main.qsubject,  subjectTextField);

	Main.getSwingView().queryPanelLayout(
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
		Main.getMainPanel().doQuery(true);
                }
            };

	final ActionListener showAllCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		Main.getMainPanel()
		    .doQuery(true,
			     Main.qsubject, Main.qproperty,
			     Main.qvalue, thisText);
	    }
	};

	final ActionListener moveLeftCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		final String text = thisTextField.getText();
		thisTextField.setText(thisText);
		leftTextField.setText(text);
		Main.getMainPanel().doQuery(true);
	    }
	};

	final ActionListener moveRightCommand = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
		final String text = thisTextField.getText();
		thisTextField.setText(thisText);
		rightTextField.setText(text);
		Main.getMainPanel().doQuery(true);
	    }
	};

	final JMenuBar menuBar = new JMenuBar();
	final JMenu menu = new JMenu();
	menu.setText("v");
	menuBar.add(menu);

	JMenuItem menuItem;

	menuItem = new JMenuItem(Main.clear);
	menuItem.addActionListener(clearCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(Main.showAll);
	menuItem.addActionListener(showAllCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(Main.shiftRight);
	menuItem.addActionListener(moveRightCommand);
	menu.add(menuItem);

	menuItem = new JMenuItem(Main.shiftLeft);
	menuItem.addActionListener(moveLeftCommand);
	menu.add(menuItem);

	return menuBar;
    }
}

// End of file.
