package org.openhc.trowser.swing.client;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public abstract class WebBrowser
    extends
	JPanel
{
    public static WebBrowser create(final String type)
    {
	if ("DJNATIVESWING".equals(type)) {
	    return new WebBrowserImplDJNative();
	}
	if ("TEXTAREA".equals(type)) {
	    return new WebBrowserImplTextArea();
	}
	throw new RuntimeException("WebBrowser.create: unknown type: " + type);
    }

    public WebBrowser(BorderLayout borderLayout) 
    {
	super(borderLayout);
    }

    public abstract void setUrl(final String x);
}

// End of file.
