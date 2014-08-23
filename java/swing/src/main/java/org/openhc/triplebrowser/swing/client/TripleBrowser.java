package org.openhc.triplebrowser.swing.client;

import java.awt.Window;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class TripleBrowser
    extends
	SingleFrameApplication
{

    public static void main(String[] args)
    {
        launch(TripleBrowser.class, args);
    }

    @Override
    protected void startup()
    {
        show(new TripleBrowserLayout(this));
    }

    public static TripleBrowser getApplication()
    {
        return Application.getInstance(TripleBrowser.class);
    }

    /**
     * This method is to initialize the specified window by
     * injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(Window root)
    {
    }
}

// End of file.

