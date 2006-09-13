//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 12 (Tue) 18:35:13 by Harold Carr.
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
	//System.out.println(start);
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
	Command resetCommand = new Command() {
	    public void execute() {
		thisTextBox.setText(thisText);
		Main.getMainPanel().doQuery();
	    }
	};

	Command moveLeftCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		leftTextBox.setText(text);
		Main.getMainPanel().doQuery();
	    }
	};

	Command moveRightCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		rightTextBox.setText(text);
		Main.getMainPanel().doQuery();
	    }
	};

	Command newCommand = new Command() {
	    public void execute() {
		Main.getMainPanel().doQuery();
	    }
	};

	Command allCommand = new Command() {
	    public void execute() {
		Main.getMainPanel()
		    .doQuery(Main.qsubject, Main.qproperty,
			     Main.qvalue, thisText);
	    }
	};

	MenuBar inside = new MenuBar(true);
	inside.addItem(Main.asteriskSymbol, resetCommand);
	inside.addItem(Main.shiftRight, moveRightCommand);
	inside.addItem(Main.shiftLeft, moveLeftCommand);
	inside.addItem(Main.NEW, newCommand);
	inside.addItem(Main.all, allCommand);
	MenuBar outside = new MenuBar();
	outside.addItem(Main.asteriskSymbol, inside);
	outside.setAutoOpen(true);
	return outside;
    }
}

// End of file.
