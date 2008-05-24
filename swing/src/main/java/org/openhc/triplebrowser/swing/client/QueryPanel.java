//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 19 (Mon) 15:58:19 by Harold Carr.
//

package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class QueryPanel
{
    private final JPanel      verticalPanel;
    private       JTextField  selectedSubjectTextField;
    private       JTextField  selectedPropertyTextField;
    private       JTextField  selectedValueTextField;
    //private final java.awt.Window window;

    //QueryPanel(final java.awt.Window window)
    QueryPanel()
    {
	// BEGIN NOT USED FOR A MOMENT.
	//this.window   = window; // so we can pack
	verticalPanel = new JPanel();
	verticalPanel.setLayout(new BoxLayout(verticalPanel, 
					      BoxLayout.PAGE_AXIS));
	//verticalPanel.setName("queryPanel");
	//verticalPanel.add(makeTriplePanel());	
	// END NOT USED FOR A MOMENT.

	addToVerticalPanel(makeTriplePanel());
    }

    private void addToVerticalPanel(JPanel horizontalPanel)
    {
	System.out.println("---------------------------");
	Component[] components = verticalPanel.getComponents();
	System.out.println("addToVerticalPanel: Number of components before: " + components.length);
	switch (components.length) {
	case 0 :
	    Main.getSwingView().xxx1(verticalPanel, 
				     horizontalPanel);
	    break;
	case 1 :
	    Main.getSwingView().xxx2(verticalPanel, 
				     (JPanel) components[0],
				     horizontalPanel);
	    break;
	case 2 :
	    Main.getSwingView().xxx3(verticalPanel, 
				     (JPanel) components[0],
				     (JPanel) components[1],
				     horizontalPanel);
	    break;
	default :
	    System.out.println("CAN'T ADD MORE: " + components.length);
	    break;
	}
	System.out.println("addToVerticalPanel: Number of components after: " + components.length);

    }

    public void removeFromVerticalPanel(JPanel horizontalPanel)
    {
	Component[] components = verticalPanel.getComponents();
	System.out.println("---------------------------");
	System.out.println("removeFromVerticalPanel: Number of components before: " + components.length);
	switch (components.length) {
	case 2 :
	    if (horizontalPanel == components[1]) {
		Main.getSwingView().xxx1(verticalPanel, 
					 (JPanel) components[0]);
	    }
	    break;
	case 3 :
	    if (horizontalPanel == components[1]) {
		Main.getSwingView().xxx2(verticalPanel, 
					 (JPanel) components[0],
					 (JPanel) components[2]);

	    } else if (horizontalPanel == components[2]) {
		Main.getSwingView().xxx2(verticalPanel, 
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
    JPanel     getPanel()             { return verticalPanel; }
    //JPanel     getPanel()             { return horizontalPanel; }

    private JPanel makeTriplePanel()
    {
	final JPanel horizontalPanel = new JPanel();
	/*
	horizontalPanel.setLayout(new BoxLayout(horizontalPanel, 
						BoxLayout.LINE_AXIS));
	*/
	final JRadioButton radioButton = new JRadioButton();
	//horizontalPanel.add(radioButton);


	final JButton leftButton;
	if (verticalPanel.getComponentCount() == 0) {
	    leftButton = new JButton("+");
	    leftButton.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    addToVerticalPanel(makeTriplePanel());
		    //window.pack();
		}});
	} else {
	    leftButton = new JButton("-");
	    leftButton.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    removeFromVerticalPanel(horizontalPanel);
		    //window.pack();
		}});
	}
	//horizontalPanel.add(leftButton);	


	final JTextField subjectTextField  = new JTextField();
	final JTextField propertyTextField = new JTextField();
	final JTextField valueTextField    = new JTextField();

	// XXXXX
	    selectedSubjectTextField  = subjectTextField;
	    selectedPropertyTextField = propertyTextField;
	    selectedValueTextField    = valueTextField;
	// XXXXX


	if (verticalPanel.getComponentCount() == 0) {
	    radioButton.setEnabled(true);
	    radioButton.setSelected(true);
	    selectedSubjectTextField  = subjectTextField;
	    selectedPropertyTextField = propertyTextField;
	    selectedValueTextField    = valueTextField;
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
	final JMenuBar subjectJMenuBar  = 
	    makeJMenuBar(Main.qvalue,    valueTextField,
			 Main.qsubject,  subjectTextField,
			 Main.qproperty, propertyTextField);
	final JMenuBar propertyJMenuBar =
	    makeJMenuBar(Main.qsubject,  subjectTextField,
			 Main.qproperty, propertyTextField,
			 Main.qvalue,    valueTextField);
	final JMenuBar valueJMenuBar    =
	    makeJMenuBar(Main.qproperty, propertyTextField,
			 Main.qvalue,    valueTextField,
			 Main.qsubject,  subjectTextField);

	/*
	horizontalPanel.add(subjectJMenuBar);
	horizontalPanel.add(subjectTextField);
	horizontalPanel.add(propertyJMenuBar);
	horizontalPanel.add(propertyTextField);
	horizontalPanel.add(valueJMenuBar);
	horizontalPanel.add(valueTextField);
	*/

	Main.getSwingView().queryPanelLayout(
            horizontalPanel,
	    leftButton, radioButton,
	    subjectJMenuBar, subjectTextField,
	    propertyJMenuBar, propertyTextField,
	    valueJMenuBar, valueTextField);

	return horizontalPanel;
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
