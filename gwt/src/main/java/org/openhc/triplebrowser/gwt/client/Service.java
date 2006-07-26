package com.differentity.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface MyService extends RemoteService 
{
    public String myMethod(String s);
    public List getInitialContents(String svoCategory);
}

// End of file.
