//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 28 (Wed) 22:39:30 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.Triple;

public class QueryPanel
{
    private final Main            main;
    private final VerticalPanel   verticalPanel;
    private       TextBox         selectedSubjectTextBox;
    private       TextBox         selectedPropertyTextBox;
    private       TextBox         selectedValueTextBox;

    QueryPanel(Main main)
    {
	this.main = main;
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
	String start = location.getParameter(main.url);
	if (start == null) {
	    subjectTextBox.setText(main.qsubject);
	} else {
	    subjectTextBox.setText(start);
	}
	propertyTextBox.setText(main.qproperty);
	valueTextBox.setText(main.qvalue);
	MenuBar subjectMenuBar  = 
	    makeMenuBar(main.qvalue,    valueTextBox,
			main.qsubject,  subjectTextBox,
			main.qproperty, propertyTextBox);
	MenuBar propertyMenuBar =
	    makeMenuBar(main.qsubject,  subjectTextBox,
			main.qproperty, propertyTextBox,
			main.qvalue,    valueTextBox);
	MenuBar valueMenuBar    =
	    makeMenuBar(main.qproperty, propertyTextBox,
			main.qvalue,    valueTextBox,
			    main.qsubject,  subjectTextBox);

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
		    List triples = new ArrayList();
		    triples.add(new Triple(selectedSubjectTextBox.getText(),
					   selectedPropertyTextBox.getText(),
					   selectedValueTextBox.getText()));
		    main.getServerCalls().assertFact(
		        new QueryRequest(
				   triples,
			           main.qsubject+main.qproperty+main.qvalue));
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
		main.getQueryManager().doQuery();
	    }
	};

	Command moveLeftCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		leftTextBox.setText(text);
		main.getQueryManager().doQuery();
	    }
	};

	Command moveRightCommand = new Command() {
	    public void execute() {
		String text = thisTextBox.getText();
		thisTextBox.setText(thisText);
		rightTextBox.setText(text);
		main.getQueryManager().doQuery();
	    }
	};

	/*
	Command showMatchCommand = new Command() {
	    public void execute() {
		main.getQueryManager().doQuery();
	    }
	};
	*/

	Command showAllCommand = new Command() {
	    public void execute() {
		main.getQueryManager()
		    .doQuery(main.qsubject, main.qproperty,
			     main.qvalue, thisText);
	    }
	};

	MenuBar inside = new MenuBar(true);
	inside.addItem(main.clear, clearCommand);
	//inside.addItem(main.showMatch, showMatchCommand);
	inside.addItem(main.showAll, showAllCommand);
	inside.addItem(main.shiftRight, moveRightCommand);
	inside.addItem(main.shiftLeft, moveLeftCommand);
	MenuBar outside = new MenuBar();
	outside.addItem("v" /*main.asteriskSymbol*/, inside);
	//outside.setAutoOpen(true);
	return outside;
    }
}

// End of file.
