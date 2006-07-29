//
// Created       : 2006 Jul 28 (Fri) 17:52:12 by Harold Carr.
// Last Modified : 2006 Jul 28 (Fri) 17:52:51 by Harold Carr.
//

package com.differentity.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface Service extends RemoteService 
{
    public String initialize(String notUsed);
    public List getInitialContents(String svoCategory);
}

// End of file.
