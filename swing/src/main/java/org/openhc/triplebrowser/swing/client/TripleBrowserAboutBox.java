package org.openhc.trowser.swing.client;

import java.awt.Font;
import java.awt.Frame;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

public class TrowserAboutBox
    extends 
	JDialog 
{
    
    private final JButton closeButton;

    public TrowserAboutBox(Frame parent) 
    {
        super(parent);

	closeButton = new JButton();
        initComponents();
        getRootPane().setDefaultButton(closeButton);
    }

    @Action 
    public void closeAboutBox() 
    {
        setVisible(false);
    }

    private void initComponents() 
    {
        final JLabel appTitleLabel    = new JLabel();
        final JLabel versionLabel     = new JLabel();
        final JLabel appVersionLabel  = new JLabel();
        final JLabel vendorLabel      = new JLabel();
        final JLabel appVendorLabel   = new JLabel();
        final JLabel homepageLabel    = new JLabel();
        final JLabel appHomepageLabel = new JLabel();
        final JLabel appDescLabel     = new JLabel();
        final JLabel imageLabel       = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final ResourceMap resourceMap = 
	    Application.getInstance(Trowser.class)
	        .getContext().getResourceMap(TrowserAboutBox.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setModal(true);
        setName("aboutBox"); // NOI18N
        setResizable(false);

        final ActionMap actionMap = 
	    Application.getInstance(Trowser.class)
	        .getContext().getActionMap(TrowserAboutBox.class, this);
        closeButton.setAction(actionMap.get("closeAboutBox")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N

        appTitleLabel.setFont(
          appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle()
          |
          Font.BOLD, appTitleLabel.getFont().getSize()+4));
        appTitleLabel.setText(resourceMap.getString("Application.title")); // NOI18N
        appTitleLabel.setName("appTitleLabel"); // NOI18N

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | Font.BOLD));
        versionLabel.setText(resourceMap.getString("versionLabel.text")); // NOI18N
        versionLabel.setName("versionLabel"); // NOI18N

        appVersionLabel.setText(resourceMap.getString("Application.version")); // NOI18N
        appVersionLabel.setName("appVersionLabel"); // NOI18N

        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | Font.BOLD));
        vendorLabel.setText(resourceMap.getString("vendorLabel.text")); // NOI18N
        vendorLabel.setName("vendorLabel"); // NOI18N

        appVendorLabel.setText(resourceMap.getString("Application.vendor")); // NOI18N
        appVendorLabel.setName("appVendorLabel"); // NOI18N

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | Font.BOLD));
        homepageLabel.setText(resourceMap.getString("homepageLabel.text")); // NOI18N
        homepageLabel.setName("homepageLabel"); // NOI18N

        appHomepageLabel.setText(resourceMap.getString("Application.homepage")); // NOI18N
        appHomepageLabel.setName("appHomepageLabel"); // NOI18N

        appDescLabel.setText(resourceMap.getString("appDescLabel.text")); // NOI18N
        appDescLabel.setName("appDescLabel"); // NOI18N

        imageLabel.setIcon(resourceMap.getIcon("imageLabel.icon")); // NOI18N
        imageLabel.setName("imageLabel"); // NOI18N

        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(imageLabel)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                    .add(GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(versionLabel)
                            .add(vendorLabel)
                            .add(homepageLabel))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(appVersionLabel)
                            .add(appVendorLabel)
                            .add(appHomepageLabel)))
                    .add(GroupLayout.LEADING, appTitleLabel)
                    .add(GroupLayout.LEADING, appDescLabel, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .add(closeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(imageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(appTitleLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(appDescLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(versionLabel)
                    .add(appVersionLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(vendorLabel)
                    .add(appVendorLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(homepageLabel)
                    .add(appHomepageLabel))
                .add(19, 19, Short.MAX_VALUE)
                .add(closeButton)
                .addContainerGap())
        );

        pack();
    }
}
