//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2007 Jun 08 (Fri) 06:39:30 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryPanel
{
    private final VerticalPanel   verticalPanel;
    private       TextBox         selectedSubjectTextBox;
    private       TextBox         selectedPropertyTextBox;
    private       TextBox         selectedValueTextBox;

    QueryPanel()
    {
	verticalPanel = new VerticalPanel();
	verticalPanel.setStyleName("queryPanel");
	verticalPanel.add(makeTriplePanel());	
    }

    TextBox         getSubjectTextBox()  { return selectedSubjectTextBox; }
    TextBox         getPropertyTextBox() { return selectedPropertyTextBox; }
    TextBox         getValueTextBox()    { return selectedValueTextBox; }
    VerticalPanel   getPanel()           { return verticalPanel; }

    private HorizontalPanel makeTriplePanel()
    {
	final HorizontalPanel horizontalPanel = new HorizontalPanel();

	final RadioButton radioButton = new RadioButton("current-triple");
	horizontalPanel.add(radioButton);


	final Button leftButton;
	if (!verticalPanel.iterator().hasNext()) {
	    leftButton = new Button("+");
	    leftButton.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    verticalPanel.add(makeTriplePanel());
		}});
	} else {
	    leftButton = new Button("-");
	    leftButton.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    verticalPanel.remove(horizontalPanel);
		}});
	}
	horizontalPanel.add(leftButton);	


	final TextBox subjectTextBox  = new TextBox();
	final TextBox propertyTextBox = new TextBox();
	final TextBox valueTextBox    = new TextBox();

	if (!verticalPanel.iterator().hasNext()) {
	    radioButton.setEnabled(true);
	    radioButton.setChecked(true);
	    selectedSubjectTextBox  = subjectTextBox;
	    selectedPropertyTextBox = propertyTextBox;
	    selectedValueTextBox    = valueTextBox;
	}
	radioButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		selectedSubjectTextBox  = subjectTextBox;
		selectedPropertyTextBox = propertyTextBox;
		selectedValueTextBox    = valueTextBox;
	    }});
		
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

	horizontalPanel.add(subjectMenuBar);
	horizontalPanel.add(subjectTextBox);
	horizontalPanel.add(propertyMenuBar);
	horizontalPanel.add(propertyTextBox);
	horizontalPanel.add(valueMenuBar);
	horizontalPanel.add(valueTextBox);

	if (!verticalPanel.iterator().hasNext()) {
	    Button saveButton = new Button("s");
	    saveButton.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    Main.getServerCalls().assertFact(
	                new QueryRequest(selectedSubjectTextBox.getText(),
					 selectedPropertyTextBox.getText(),
					 selectedValueTextBox.getText(),
					 Main.qsubject+Main.qproperty+Main.qvalue));
		}});

	    horizontalPanel.add(saveButton);
	}

	return horizontalPanel;
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
