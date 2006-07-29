//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 28 (Fri) 18:12:56 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class QueryPanel
{
    private final HorizontalPanel horizontalPanel;
    private final TextBox         subjectTextBox;
    private final TextBox         verbTextBox;
    private final TextBox         objectTextBox;

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