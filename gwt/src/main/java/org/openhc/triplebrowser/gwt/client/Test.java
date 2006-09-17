//
// Created       : 2006 Jul 30 (Sun) 15:53:20 by Harold Carr.
// Last Modified : 2006 Sep 17 (Sun) 09:32:04 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Test
    implements HistoryListener 
{
    private Widget widget;

    private Label label = new Label();

    public Test()
    {
	// If the application starts with no history token, start it off.
	String initToken = History.getToken();
	if (initToken.length() == 0) {
	    initToken = "baz";
	}

	// onHistoryChanged() is not called when the application first runs.
	// Call it now in order to reflect the initial state.
	onHistoryChanged(initToken);

	widget = label;

	// Add history listener
	History.addHistoryListener(this);
    }

    public void onHistoryChanged(final String historyToken) {
	// This method is called whenever the application's history changes.
	// Set the label to reflect the current history token.
	label.setText("token: " + historyToken);

	String[] token = historyToken.split(Main.historyFieldSeparator);
	for (int i = 0; i < token.length; i++) {
	    System.out.println(token[i]);
	}
	if (token[0].equals("doQuery")) {
	    QueryPanel queryPanel = Main.getMainPanel().getQueryPanel();
	    queryPanel.getSubjectTextBox().setText(token[1]);
	    queryPanel.getPropertyTextBox().setText(token[2]);
	    queryPanel.getValueTextBox().setText(token[3]);
	    Main.getMainPanel()
		.doQuery(false, token[4], token[5], token[6], token[7]);
	}
    }

    public Widget getWidget() { return widget; }

    //
    // MainPanel
    //

    // MainPanel()
    //  -> doQuery()
    //   -> doQuery(stb, ptb, vtb, spv) : initial query (including post)
    // QQQQ
    public void recordDoQuery(final boolean keepHistory,
			      final String subject, final String property, 
			      final String value, final String setContentsOf)
    {
	if (! keepHistory) {
	    return;
	}

	QueryPanel queryPanel = Main.getMainPanel().getQueryPanel();

	History.newItem("doQuery" 
			+ Main.historyFieldSeparator
			+ queryPanel.getSubjectTextBox().getText()
			+ Main.historyFieldSeparator
			+ queryPanel.getPropertyTextBox().getText()
			+ Main.historyFieldSeparator
			+ queryPanel.getValueTextBox().getText()
			+ Main.historyFieldSeparator
			+ subject 
			+ Main.historyFieldSeparator
			+ property 
			+ Main.historyFieldSeparator
			+ value 
			+ Main.historyFieldSeparator
			+ setContentsOf);
    }

    // spvLinkClicked(categoryAndURL) 
    //  --> category.setText(URL)
    //  --> doQuery()
    // QQQQ
    public void recordSpvLinkClicked(final String category, final String URL)
    {
	//History.newItem("spvLinkClicked:" + category + ":" + URL);
    }

    //
    // QueryPanel
    //

    // Set subjectTextBox to URL parameter?  NO.  Covered by initial query.

    // NOTE: All these actions are followed by doQuery 
    // (handled above - but that doesn't reset the text).

    // resetCommand()
    //  -> setText(question)
    //  -> doQuery()
    // QQQQ
    public void recordResetCommand(final String thisText)
    {
	//History.newItem("resetCommand:" + thisText);
    }

    // moveLeftCommand()
    //  -> get/setText
    //  -> doQuery()
    // QQQQ
    public void recordMoveLeftCommand(final String leftText,
				      final String thisText)
    {
	//History.newItem("moveLeftCommand:" + leftText + ":" + thisText);
    }

    // moveLeftCommand()
    //  -> get/setText
    //  -> doQuery()
    // QQQQ
    public void recordMoveRightCommand(final String thisText,
				       final String rightText)
    {
	//History.newItem("moveLeftCommand:" + thisText + ":" + rightText);
    }

    // newCommand
    //  -> doQuery()
    // QQQQ
    public void recordNewCommand(final String thisText)
    {
	//History.newItem("newCommand:" + thisText);
    }

    // allCommand
    //  -> doQuery(qs, qp, qv, category)
    // QQQQ
    public void recordAllCommand(final String thisText)
    {
	//History.newItem("allCommand:" + thisText);
    }

    //
    // SPVPanel
    //

    //
    // onClick()
    //  -> expandOrCollapse()
    //  -> setText(newState)

    public void recordExpandOrCollapseSPVClick(final String spvCategory)
    {
	History.newItem("expandOrCollapseSPVClick:" + spvCategory);
    }

    // ***** Rethink usage of getTargetHistoryToken in SPVPanel
    // click listener for spv link clicked already covered above.

    // onClick()
    //  -> MainPanel.spvLinkClicked(targetHistoryToken)
    //   -> ... doQuery
    // QQQQ
    public void recordSPVItemLinkClick(final String link)
    {
	//History.newItem("spvItemLinkClick:" + link);
    }

    // onClick()
    //  -> setText(expand/contract)
    //  -> add/remove frame

    public void recordExpandOrCollapseSPVItemClick(final String state)
    {
	History.newItem("expandOrCollapseSPVItemClick:" + state);
    }
}

// End of file.