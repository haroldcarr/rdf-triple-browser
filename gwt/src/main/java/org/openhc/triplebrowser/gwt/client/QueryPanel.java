//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 10 (Sun) 15:37:32 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QueryPanel
{
    private final HorizontalPanel horizontalPanel;
    private final TextBox         subjectTextBox;
    private final TextBox         propertyTextBox;
    private final TextBox         valueTextBox;
    private final Button          subjectResetButton;
    private final Button          propertyResetButton;
    private final Button          valueResetButton;

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
	subjectResetButton  = new Button(Main.asteriskSymbol);
	propertyResetButton = new Button(Main.asteriskSymbol);
	valueResetButton    = new Button(Main.asteriskSymbol);
	subjectResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		subjectTextBox.setText(Main.qsubject);
		Main.getMainPanel().doQuery();
	    }
	});
	propertyResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		propertyTextBox.setText(Main.qproperty);
		Main.getMainPanel().doQuery();
	    }
	});
	valueResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		valueTextBox.setText(Main.qvalue);
		Main.getMainPanel().doQuery();
	    }
	});
	horizontalPanel = new HorizontalPanel();
	horizontalPanel.add(subjectResetButton);
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(propertyResetButton);
	horizontalPanel.add(propertyTextBox);
	horizontalPanel.add(valueResetButton);
	horizontalPanel.add(valueTextBox);
    }
    TextBox         getSubjectTextBox()  { return subjectTextBox; }
    TextBox         getPropertyTextBox() { return propertyTextBox; }
    TextBox         getValueTextBox()    { return valueTextBox; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
}

// End of file.
