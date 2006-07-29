//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Jul 28 (Fri) 21:42:25 by Harold Carr.
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
    private List initial; // For debugging.
    List getInitial() { return initial; }
    void setInitial(List i) { initial = i; }
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

    public void getInitialContents(final SVOPanel svoPanel,
				   final String svoCategory)
    {
	serviceAsync.getInitialContents(
            svoCategory,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    final Iterator i = ((List)x).iterator();
		    final List result = new ArrayList();
		    while (i.hasNext()) {
			String uri = (String) i.next();
			result.add(
                            new SVOItem(svoCategory, 
					uri,
					substringAfterLastSlash(uri),
					// NOTE: during development change to
					// Main.expand to test full range.
					Main.collapse));
		    }
		    setInitial(result);
		    svoPanel.initialContents(result);
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

    private String substringAfterLastSlash(final String x)
    {
	int indexOfLastSlash = 0;
	int i;
	for (i = 0; i < x.length(); ++i) {
	    if (x.charAt(i) == '/') {
		indexOfLastSlash = i;
	    }
	}
	final String result = x.substring(indexOfLastSlash + 1);
	// If it ends in a slash then remove the ending slash and try again.
	if (result.length() == 0) {
	    return substringAfterLastSlash(x.substring(0, x.length()-1));
	} else {
	    return result;
	}
    }
}

// End of file.

