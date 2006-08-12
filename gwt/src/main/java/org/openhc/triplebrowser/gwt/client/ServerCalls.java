//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Aug 12 (Sat) 15:30:06 by Harold Carr.
//

package com.differentity.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.differentity.client.Main;
import com.differentity.client.SVOPanel;
import com.differentity.client.Service;
import com.differentity.client.ServiceAsync;

public class ServerCalls
{
    private ServiceAsync serviceAsync;

    public ServerCalls()
    {
	serviceAsync = (ServiceAsync) GWT.create(Service.class);
	ServiceDefTarget serviceDefTarget = (ServiceDefTarget) serviceAsync;
	serviceDefTarget.setServiceEntryPoint("/Service");
    }

    public void initialize()
    {
	serviceAsync.initialize(
            "FOO",
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    MainPanel.getStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    MainPanel.getStatusHTML().setHTML(caught.toString());
		}
	    });
    }

    public void doQuery(final MainPanel mainPanel,
			final QueryRequest queryRequest)
    {
	serviceAsync.doQuery(
            queryRequest,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    mainPanel.handleQueryResponse((QueryResponse) x);
		}

		public void onFailure(Throwable caught) {
		    Window.alert(".getInitialContents: " + caught);
		}
	    });
    }

    public void svoLinkClicked(final String categoryAndURL)
    {
	// TODO: Send to server.  Receive updates for other panels.
	int i = categoryAndURL.indexOf(" ");
	String category = categoryAndURL.substring(0, i);
	String url = categoryAndURL.substring(i+1);
	if (category.equals(Main.subject)) {
	    Main.mainPanel.getQueryPanel().getSubjectTextBox().setText(url);
	} else if (category.equals(Main.verb)) {
	    Main.mainPanel.getQueryPanel().getVerbTextBox().setText(url);
	} else if (category.equals(Main.object)) {
            Main.mainPanel.getQueryPanel().getObjectTextBox().setText(url);
	} else {
	    // TODO: FIX
	    Main.mainPanel.getQueryPanel().getSubjectTextBox().setText("ERROR");
	}
    }
}

// End of file.

