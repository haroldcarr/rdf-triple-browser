//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2007 Jun 07 (Thu) 20:25:38 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryPanel
{
    private final HorizontalPanel horizontalPanel;
    private final VerticalPanel   verticalPanel;
    private       TextBox         TsubjectTextBox;
    private       TextBox         TpropertyTextBox;
    private       TextBox         TvalueTextBox;
    private       MenuBar         TsubjectMenuBar;
    private       MenuBar         TpropertyMenuBar;
    private       MenuBar         TvalueMenuBar;

    QueryPanel()
    {

	horizontalPanel = new HorizontalPanel();

	Button button = new Button("+");
	horizontalPanel.add(button);	

	verticalPanel = new VerticalPanel();

	verticalPanel.add(makeTriplePanel());

	horizontalPanel.add(verticalPanel);

	Button saveButton = new Button("s");
	saveButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		Main.getServerCalls().assertFact(
	            new QueryRequest(TsubjectTextBox.getText(),
				     TpropertyTextBox.getText(),
				     TvalueTextBox.getText(),
				     Main.qsubject+Main.qproperty+Main.qvalue));
	    }});
	horizontalPanel.add(saveButton);
    }

    TextBox         getSubjectTextBox()  { return TsubjectTextBox; }
    TextBox         getPropertyTextBox() { return TpropertyTextBox; }
    TextBox         getValueTextBox()    { return TvalueTextBox; }
    HorizontalPanel getPanel()           { return horizontalPanel; }

    private HorizontalPanel makeTriplePanel()
    {
	TextBox subjectTextBox  = new TextBox();
	TextBox propertyTextBox = new TextBox();
	TextBox valueTextBox    = new TextBox();
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
	MenuBar subjectMenuBar  = 
	    makeMenuBar(Main.qvalue,    valueTextBox,
			Main.qsubject,  subjectTextBox,
			Main.qproperty, propertyTextBox);
	MenuBar propertyMenuBar =
	    makeMenuBar(Main.qsubject,  subjectTextBox,
			Main.qproperty, propertyTextBox,
			Main.qvalue,    valueTextBox);
	MenuBar valueMenuBar    =
	    makeMenuBar(Main.qproperty, propertyTextBox,
			Main.qvalue,    valueTextBox,
			    Main.qsubject,  subjectTextBox);
	
	if (TsubjectTextBox  == null) TsubjectTextBox  = subjectTextBox;
	if (TsubjectMenuBar  == null) TsubjectMenuBar  = subjectMenuBar;
	if (TpropertyTextBox == null) TpropertyTextBox = propertyTextBox;
	if (TpropertyMenuBar == null) TpropertyMenuBar = propertyMenuBar;
	if (TvalueTextBox    == null) TvalueTextBox    = valueTextBox;
	if (TvalueMenuBar    == null) TvalueMenuBar    = valueMenuBar;
	
	HorizontalPanel triplePanel = new HorizontalPanel();

	triplePanel.add(subjectMenuBar);
	triplePanel.add(subjectTextBox);
	triplePanel.add(propertyMenuBar);
	triplePanel.add(propertyTextBox);
	triplePanel.add(valueMenuBar);
	triplePanel.add(valueTextBox);

	return triplePanel;
    }

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
