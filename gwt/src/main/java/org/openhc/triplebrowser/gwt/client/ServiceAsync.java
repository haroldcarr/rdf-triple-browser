package com.differentity.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

interface MyServiceAsync {
    public void myMethod(String s, AsyncCallback callback);
    public void getInitialContents(String svoCategory, AsyncCallback callback);
}
