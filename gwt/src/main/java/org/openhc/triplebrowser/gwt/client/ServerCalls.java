//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2006 Sep 04 (Mon) 22:07:16 by Harold Carr.
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
import com.differentity.client.SPVPanel;
import com.differentity.client.Service;
import com.differentity.client.ServiceAsync;

public class ServerCalls
{
    private ServiceAsync serviceAsync;

    public ServerCalls()
    {
	serviceAsync = (ServiceAsync) GWT.create(Service.class);
	ServiceDefTarget serviceDefTarget = (ServiceDefTarget) serviceAsync;
	serviceDefTarget
	    .setServiceEntryPoint(GWT.getModuleBaseURL() + "/Service");
    }

    public void initialize()
    {
	serviceAsync.initialize(
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    Main.makeMainPanel();
		    DevTime.getJenaStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    DevTime.getJenaStatusHTML().setHTML(caught.toString());
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
		    Window.alert(".doQuery: " + caught);
		}
	    });
    }

    public void close()
    {
	serviceAsync.close(
	    new AsyncCallback() {
		public void onSuccess(Object result) {
		    Main.makeMainPanel();
		    DevTime.getJenaStatusHTML().setHTML(result.toString());
		}
		public void onFailure(Throwable caught) {
		    DevTime.getJenaStatusHTML().setHTML(caught.toString());
		}
	    });
    }
}

// End of file.

