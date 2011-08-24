package org.openhc.triplebrowser.swing.client;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WebBrowserImplTextArea
    extends
	WebBrowser
{
    final JTextField webBrowser;

    public void setUrl(final String x)
    {
	webBrowser.setText(x);
    }

  public WebBrowserImplTextArea() {
    super(new BorderLayout(0, 0));
    JPanel webBrowserPanel = new JPanel(new BorderLayout(0, 0));
    webBrowserPanel.setBorder(BorderFactory.createEtchedBorder());
    webBrowser = new JTextField();
    //webBrowser.setURL("http://www.google.com");
    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    add(webBrowserPanel, BorderLayout.CENTER);
  }

}

// End of file.
