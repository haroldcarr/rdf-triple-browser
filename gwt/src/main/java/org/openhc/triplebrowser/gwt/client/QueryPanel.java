//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 14 (Mon) 22:21:15 by Harold Carr.
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
    private final TextBox         verbTextBox;
    private final TextBox         objectTextBox;
    private final Button          subjectResetButton;
    private final Button          verbResetButton;
    private final Button          objectResetButton;

    QueryPanel()
    {
	subjectTextBox = new TextBox();
	verbTextBox    = new TextBox();
	objectTextBox  = new TextBox();
	subjectResetButton = new Button("*");
	verbResetButton    = new Button("*");
	objectResetButton  = new Button("*");
	subjectResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		subjectTextBox.setText("");
		Main.getMainPanel().doQuery();
	    }
	});
	verbResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		verbTextBox.setText("");
		Main.getMainPanel().doQuery();
	    }
	});
	objectResetButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		objectTextBox.setText("");
		Main.getMainPanel().doQuery();
	    }
	});
	horizontalPanel = new HorizontalPanel();
	horizontalPanel.add(subjectResetButton);
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(verbResetButton);
	horizontalPanel.add(verbTextBox);
	horizontalPanel.add(objectResetButton);
	horizontalPanel.add(objectTextBox);
    }
    TextBox         getSubjectTextBox() { return subjectTextBox; }
    TextBox         getVerbTextBox   () { return verbTextBox; }
    TextBox         getObjectTextBox () { return objectTextBox; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
}

// End of file.