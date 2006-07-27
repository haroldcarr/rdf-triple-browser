//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 26 (Wed) 17:00:04 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class QueryPanel
{
    final HorizontalPanel horizontalPanel;
    final TextBox         subjectTextBox;
    final TextBox         verbTextBox;
    final TextBox         objectTextBox;

    QueryPanel()
    {
	subjectTextBox = new TextBox();
	verbTextBox    = new TextBox();
	objectTextBox  = new TextBox();
	horizontalPanel = new HorizontalPanel();
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(verbTextBox);
	horizontalPanel.add(objectTextBox);
    }
    TextBox         getSubjectTextBox() { return subjectTextBox; }
    TextBox         getVerbTextBox   () { return verbTextBox; }
    TextBox         getObjectTextBox () { return objectTextBox; }
    HorizontalPanel getHorizontalPanel() { return horizontalPanel; }
}

// End of file.