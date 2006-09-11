//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 10 (Sun) 20:07:04 by Harold Carr.
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
	String start = location.getParameter("location");
	//System.out.println(start);
	if (start == null) {
	    subjectTextBox.setText(Main.qsubject);
	} else {
	    subjectTextBox.setText(start);
	}
	propertyTextBox.setText(Main.qproperty);
	valueTextBox.setText(Main.qvalue);
	subjectMenuBar  = makeMenuBar(Main.qsubject, valueTextBox,
				      subjectTextBox, propertyTextBox);
	propertyMenuBar = makeMenuBar(Main.qproperty, subjectTextBox,
				      propertyTextBox, valueTextBox);
	valueMenuBar    = makeMenuBar(Main.qvalue, propertyTextBox,
				      valueTextBox, subjectTextBox);
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

    private MenuBar makeMenuBar(final String text,
				final TextBox leftTextBox,
				final TextBox thisTextBox,
				final TextBox rightTextBox)
    {
	Command cmd = new Command() {
	    public void execute() {
		thisTextBox.setText(text);
		Main.getMainPanel().doQuery();
	    }
	};

	MenuBar inside = new MenuBar(true);
	inside.addItem("*", cmd);
	inside.addItem("->", cmd);
	inside.addItem("<-", cmd);
	inside.addItem("new", cmd);
	inside.addItem("all", cmd);
	MenuBar outside = new MenuBar();
	outside.addItem(Main.asteriskSymbol, inside);
	outside.setAutoOpen(true);
	return outside;
    }
}

// End of file.
