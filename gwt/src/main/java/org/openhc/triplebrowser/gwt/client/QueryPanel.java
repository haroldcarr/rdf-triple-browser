//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 29 (Thu) 19:24:08 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
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

    private       TextBox         selectedSubjectTextBox;
    private       TextBox         selectedPropertyTextBox;
    private       TextBox         selectedValueTextBox;

    private final VerticalPanel   queryPanel;

    private/*final*/RadioButton baseRadioButton;
    private/*final*/TextBox     baseSubjectTextBox;
    private/*final*/TextBox     baseProperyTextBox;
    private/*final*/TextBox     baseValueTextBox;

    QueryPanel(Main main)
    {
	this.main = main;

	queryPanel = new VerticalPanel();
	queryPanel.setStyleName("queryPanel");
	queryPanel.add(makeTriplePanel());	
    }

    TextBox       getSubjectTextBox()  { return selectedSubjectTextBox; }
    TextBox       getPropertyTextBox() { return selectedPropertyTextBox; }
    TextBox       getValueTextBox()    { return selectedValueTextBox; }
    VerticalPanel getPanel()           { return queryPanel; }

    private HorizontalPanel makeTriplePanel()
    {
	final HorizontalPanel triplePanel = new HorizontalPanel();
	final Button          leftButton;
	final RadioButton     radioButton = new RadioButton("Mutual-Excl");
	final MenuBar         subjectMenuBar;
	final TextBox         subjectTextBox  = new TextBox();
	final MenuBar         propertyMenuBar;
	final TextBox         propertyTextBox = new TextBox();
	final MenuBar         valueMenuBar;
	final TextBox         valueTextBox    = new TextBox();

	// Looking to future when used like bookmarker.
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

	if (!queryPanel.iterator().hasNext()) {
	    leftButton = new Button(main.plusSymbol);
	    leftButton.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    queryPanel.add(makeTriplePanel());
		}});
	    baseRadioButton    = radioButton;
	    baseSubjectTextBox = subjectTextBox;
	    baseProperyTextBox = propertyTextBox;
	    baseValueTextBox   = valueTextBox;
	} else {
	    leftButton = new Button(main.minusSymbol);
	    leftButton.addClickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    queryPanel.remove(triplePanel);
		    baseRadioButton.setChecked(true);
		    selectedSubjectTextBox  = baseSubjectTextBox;
		    selectedPropertyTextBox = baseProperyTextBox;
		    selectedValueTextBox    = baseValueTextBox;
		}});
	}

	// The latest created is always selected.
	uncheckAllRadioButtons(); // I swear I didn't need this before.
	radioButton.setEnabled(true);
	radioButton.setChecked(true);
	selectedSubjectTextBox  = subjectTextBox;
	selectedPropertyTextBox = propertyTextBox;
	selectedValueTextBox    = valueTextBox;

	radioButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		selectedSubjectTextBox  = subjectTextBox;
		selectedPropertyTextBox = propertyTextBox;
		selectedValueTextBox    = valueTextBox;
	    }});

	subjectMenuBar  = 
	    makeMenuBar(main.qvalue,    valueTextBox,
			main.qsubject,  subjectTextBox,
			main.qproperty, propertyTextBox);
	propertyMenuBar =
	    makeMenuBar(main.qsubject,  subjectTextBox,
			main.qproperty, propertyTextBox,
			main.qvalue,    valueTextBox);
	valueMenuBar    =
	    makeMenuBar(main.qproperty, propertyTextBox,
			main.qvalue,    valueTextBox,
			    main.qsubject,  subjectTextBox);

	triplePanel.add(leftButton);	
	triplePanel.add(radioButton);
	triplePanel.add(subjectMenuBar);
	triplePanel.add(subjectTextBox);
	triplePanel.add(propertyMenuBar);
	triplePanel.add(propertyTextBox);
	triplePanel.add(valueMenuBar);
	triplePanel.add(valueTextBox);

	if (!queryPanel.iterator().hasNext()) {
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

	    triplePanel.add(saveButton);
	}

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
		main.getQueryManager().doQuery();
	    }
	};

	Command showAllCommand = new Command() {
	    public void execute() {
		main.getQueryManager()
		    .doQuery(main.qsubject, main.qproperty,
			     main.qvalue, thisText);
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

	MenuBar inside = new MenuBar(true);
	inside.addItem(main.clear,      clearCommand);
	inside.addItem(main.showAll,    showAllCommand);
	inside.addItem(main.shiftRight, moveRightCommand);
	inside.addItem(main.shiftLeft,  moveLeftCommand);
	MenuBar outside = new MenuBar();
	outside.addItem(main.littleV, inside);
	return outside;
    }

    private void uncheckAllRadioButtons()
    {
	Iterator hpi = getPanel().iterator();
	while (hpi.hasNext()) {
	    HorizontalPanel triple = (HorizontalPanel) hpi.next();
	    Iterator i = triple.iterator();
	    i.next(); // skip button
	    ((RadioButton)i.next()).setChecked(false);
	}
    }
}

// End of file.
