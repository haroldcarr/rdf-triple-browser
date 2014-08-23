//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2011 Aug 10 (Wed) 20:52:11 by carr.
//

package org.openhc.triplebrowser.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;

import org.openhc.triplebrowser.gwt.client.Main;
import org.openhc.triplebrowser.gwt.client.SPVPanel;
import org.openhc.triplebrowser.gwt.client.Service;
import org.openhc.triplebrowser.gwt.client.ServiceAsync;

import org.openhc.triplebrowser.gwt.common.QueryRequest;
import org.openhc.triplebrowser.gwt.common.QueryResponse;

public class ServerCalls
{
    protected final Main         main;
    private   final ServiceAsync serviceAsync;

    public ServerCalls(Main main)
    {
	this.main = main;

	serviceAsync = (ServiceAsync) GWT.create(Service.class);
        /* Now covered by an annotation in client.Service.java
	ServiceDefTarget serviceDefTarget = (ServiceDefTarget) serviceAsync;
	serviceDefTarget
	    .setServiceEntryPoint(GWT.getModuleBaseURL()
				  + main.serviceEntryPoint);
        */
    }

    public void openFile(final String filename)
    {
	serviceAsync.openFile(
	    filename,
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    //Window.alert(".openFile: success: " + result);
		    //main.getQueryPanel().reset();  // ***** TODO
		    main.getQueryManager().doQuery();
		    //main.makeMainPanel();
		    //DevTime.getJenaStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    Window.alert(".openFile: failure: " + caught);
		}
	    });
    }

    public void doQuery(final QueryRequest queryRequest)
    {
	main.getResponseProgressLabel().setText(main.querying);

	serviceAsync.doQuery(
            queryRequest,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    main.getResponseProgressLabel().setText(main.emptyString);
		    main.getQueryManager()
			.handleQueryResponse((QueryResponse) x);
		}

		public void onFailure(Throwable caught) {
		    main.getResponseProgressLabel().setText(main.emptyString);
		    Window.alert(".doQuery: " + caught);
		}
	    });
    }

    public void assertFact(final QueryRequest queryRequest)
    {
	main.getResponseProgressLabel().setText(main.querying);

	serviceAsync.assertFact(
            queryRequest,
	    new AsyncCallback() {
		public void onSuccess(Object x) {
		    main.getResponseProgressLabel().setText(main.emptyString);
		    main.getQueryManager().handleQueryResponse((QueryResponse) x);
		}

		public void onFailure(Throwable caught) {
		    main.getResponseProgressLabel().setText(main.emptyString);
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

