//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 26 (Mon) 09:05:50 by Harold Carr.
//

package org.openhc.trowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;

import org.openhc.trowser.gwt.client.Main;
import org.openhc.trowser.gwt.client.SPVPanel;
import org.openhc.trowser.gwt.client.Service;
import org.openhc.trowser.gwt.client.ServiceAsync;

import org.openhc.trowser.gwt.common.QueryRequest;
import org.openhc.trowser.gwt.common.QueryResponse;

public class ServerCalls
{
    private ServiceAsync serviceAsync;

    public ServerCalls()
    {
	serviceAsync = (ServiceAsync) GWT.create(Service.class);
	ServiceDefTarget serviceDefTarget = (ServiceDefTarget) serviceAsync;
	serviceDefTarget
	    .setServiceEntryPoint(GWT.getModuleBaseURL() 
				  + Main.serviceEntryPoint);
    }

    public void openFile(final String filename)
    {
	serviceAsync.openFile(
	    filename,
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    Window.alert(".openFile: success: " + result);
		    //Main.getQueryPanel().reset();  // ***** TODO
		    Main.getMainPanel().doQuery(true);
		    //Main.makeMainPanel();
		    //DevTime.getJenaStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    Window.alert(".openFile: failure: " + caught);
		}
	    });
    }

    public void doQuery(final MainPanel mainPanel,
			final QueryRequest queryRequest)
    {
	final Label responseProgressLabel = 
	    Main.getMainPanel().getResponseProgressLabel();
	responseProgressLabel.setText("LOADING...");

	serviceAsync.doQuery(
            queryRequest,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    responseProgressLabel.setText("");
		    mainPanel.handleQueryResponse((QueryResponse) x);
		}

		public void onFailure(Throwable caught) {
		    responseProgressLabel.setText("");
		    Window.alert(".doQuery: " + caught);
		}
	    });
    }

    public void assertFact(final QueryRequest queryRequest)
    {
	final Label responseProgressLabel = 
	    Main.getMainPanel().getResponseProgressLabel();
	responseProgressLabel.setText("LOADING...");

	serviceAsync.assertFact(
            queryRequest,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    responseProgressLabel.setText("");
		    Main.getMainPanel().handleQueryResponse((QueryResponse) x);
		}

		public void onFailure(Throwable caught) {
		    responseProgressLabel.setText("");
		    Window.alert(".assertFact: " + caught);
		}
	    });
    }

    public void close()
    {
	serviceAsync.close(
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    DevTime.getJenaStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    Window.alert(".doQuery: " + caught);
		}
	    });
    }
}

// End of file.

