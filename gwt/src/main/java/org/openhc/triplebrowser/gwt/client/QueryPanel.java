//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 21 (Thu) 21:11:27 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QueryPanel
{
    private final HorizontalPanel horizontalPanel;
    private final TextBox         subjectTextBox;
    private final TextBox         propertyTextBox;
    private final TextBox         valueTextBox;
    private final MenuBar         subjectMenuBar;
    private final MenuBar         propertyMenuBar;
    private final MenuBar         valueMenuBar;

    QueryPanel()
    {
	subjectTextBox  = new TextBox();
	propertyTextBox = new TextBox();
	valueTextBox    = new TextBox();
	org.gwtwidgets.client.util.WindowUtils wu =
	    new org.gwtwidgets.client.util.WindowUtils();
	org.gwtwidgets.client.util.Location location = wu.getLocation();
	String start = location.getParameter(Main.url);
	if (start == null) {
	    subjectTextBox.setText(Main.qsubject);
	} else {
	    subjectTextBox.setText(start);
	}
	propertyTextBox.setText(Main.qproperty);
	valueTextBox.setText(Main.qvalue);
	subjectMenuBar  = 
	    makeMenuBar(Main.qvalue,    valueTextBox,
			Main.qsubject,  subjectTextBox,
			Main.qproperty, propertyTextBox);
	propertyMenuBar =
	    makeMenuBar(Main.qsubject,  subjectTextBox,
			Main.qproperty, propertyTextBox,
			Main.qvalue,    valueTextBox);
	valueMenuBar    =
	    makeMenuBar(Main.qproperty, propertyTextBox,
			Main.qvalue,    valueTextBox,
			Main.qsubject,  subjectTextBox);
	horizontalPanel = new HorizontalPanel();
	horizontalPanel.add(subjectMenuBar);
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(propertyMenuBar);
	horizontalPanel.add(propertyTextBox);
	horizontalPanel.add(valueMenuBar);
	horizontalPanel.add(valueTextBox);
    }
    TextBox         getSubjectTextBox()  { return subjectTextBox; }
    TextBox         getPropertyTextBox() { return propertyTextBox; }
    TextBox         getValueTextBox()    { return valueTextBox; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }

    private MenuBar makeMenuBar(final String  leftText,
				final TextBox leftTextBox,
				final String  thisText,
				final TextBox thisTextBox,
				final String  rightText,
				final TextBox rightTextBox)
    {
	Command clearCommand = new Command() {
	    public void execute() {
		thisTextBox.setText(thisText);
		Main.getMainPanel().doQuery(true);
	    }
	};

	Command moveLeftCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		leftTextBox.setText(text);
		Main.getMainPanel().doQuery(true);
	    }
	};

	Command moveRightCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		rightTextBox.setText(text);
		Main.getMainPanel().doQuery(true);
	    }
	};

	/*
	Command showMatchCommand = new Command() {
	    public void execute() {
		Main.getMainPanel().doQuery(true);
	    }
	};
	*/

	Command showAllCommand = new Command() {
	    public void execute() {
		Main.getMainPanel()
		    .doQuery(true,
			     Main.qsubject, Main.qproperty,
			     Main.qvalue, thisText);
	    }
	};

	MenuBar inside = new MenuBar(true);
	inside.addItem(Main.clear, clearCommand);
	//inside.addItem(Main.showMatch, showMatchCommand);
	inside.addItem(Main.showAll, showAllCommand);
	inside.addItem(Main.shiftRight, moveRightCommand);
	inside.addItem(Main.shiftLeft, moveLeftCommand);
	MenuBar outside = new MenuBar();
	outside.addItem("v" /*Main.asteriskSymbol*/, inside);
	//outside.setAutoOpen(true);
	return outside;
    }
}

// End of file.
