package org.openhc.triplebrowser.swing.client;

import org.openhc.triplebrowser.swing.client.Main;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

//
// The application's main frame.
//

public class TripleBrowserLayout
    extends
	FrameView
{
    private Main     main;
    private JPanel   mainPanel;
    private JMenuBar menuBar;
    private JDialog  aboutBox;

    public TripleBrowserLayout(SingleFrameApplication app)
    {
        super(app);
        initComponents();
	this.main = new Main(this);
        ResourceMap resourceMap = getResourceMap();
    }

    @Action
    public void openFile() {
        JFileChooser fc = new JFileChooser();
        int option = fc.showOpenDialog(getFrame());
        if (JFileChooser.APPROVE_OPTION == option) {
            System.out.println(fc.getSelectedFile());
	    main.getServerCalls().openFile(fc.getSelectedFile().toString());
        }
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null) {
            JFrame mainFrame = TripleBrowser.getApplication().getMainFrame();
            aboutBox = new TripleBrowserAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        TripleBrowser.getApplication().show(aboutBox);
    }

    public JPanel getSwingMainPanel()
    {
	return mainPanel;
    }

    @SuppressWarnings("unchecked")
    private void initComponents()
    {
        mainPanel = new JPanel();
        mainPanel.setName("mainPanel"); // NOI18N

        ResourceMap resourceMap =
	    Application.getInstance(TripleBrowser.class)
	        .getContext().getResourceMap(TripleBrowserLayout.class);

                        menuBar          = new JMenuBar();
        final JMenu     fileMenu         = new JMenu();
        final JMenuItem openFileMenuItem = new JMenuItem();
        final JMenuItem exitMenuItem     = new JMenuItem();
        final JMenu     helpMenu         = new JMenu();
        final JMenuItem aboutMenuItem    = new JMenuItem();

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        ActionMap actionMap =
	    Application.getInstance(TripleBrowser.class)
	        .getContext().getActionMap(TripleBrowserLayout.class, this);


        openFileMenuItem.setAction(actionMap.get("openFile")); // NOI18N
        openFileMenuItem.setName("openFileMenuItem"); // NOI18N
        fileMenu.add(openFileMenuItem);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }

    //////////////////////////////////////////////////
    //
    // queryPanelLayout
    //
    public void queryPanelLayout(final JPanel       jPanel1,
				 final JButton      jButton1,
				 final JRadioButton jRadioButton1,
				 final JMenuBar     jComboBox1,
				 final JTextField   jTextField1,
				 final JMenuBar     jComboBox2,
				 final JTextField   jTextField2,
				 final JMenuBar     jComboBox3,
				 final JTextField   jTextField3)
    {
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
		 .add(jButton1, GroupLayout.PREFERRED_SIZE,
		      40, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jRadioButton1, GroupLayout.PREFERRED_SIZE,
		      21, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jComboBox1, GroupLayout.PREFERRED_SIZE,
		      17, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jTextField1, GroupLayout.DEFAULT_SIZE,
		      148, Short.MAX_VALUE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jComboBox2, GroupLayout.PREFERRED_SIZE,
		      17, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jTextField2, GroupLayout.DEFAULT_SIZE,
		      148, Short.MAX_VALUE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jComboBox3, GroupLayout.PREFERRED_SIZE,
		      17, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jTextField3, GroupLayout.DEFAULT_SIZE,
		      148, Short.MAX_VALUE)
		 )
	    );

        jPanel1Layout.linkSize(
            new Component[] {
		jComboBox1, jComboBox2, jComboBox3
	    },
	    GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
		 .add(jButton1)
		 .add(jRadioButton1)
		 .add(jComboBox1, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .add(jTextField1, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .add(jComboBox2, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .add(jTextField2, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .add(jComboBox3, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .add(jTextField3, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 )
	    );

        jPanel1Layout.linkSize(
            new Component[] {
		jComboBox1, jComboBox2, jComboBox3
	    },
	    GroupLayout.VERTICAL);

        jPanel1Layout.linkSize(
            new Component[] {
		jTextField1, jTextField2, jTextField3},
	    GroupLayout.VERTICAL);
    }

    public void addTriplePanel(final JPanel queryPanel,
			       final JPanel jPanel1)
    {
        GroupLayout queryPanelLayout = new GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);

	GroupLayout.ParallelGroup parallelGroup =
	    queryPanelLayout.createParallelGroup(GroupLayout.LEADING);

	GroupLayout.SequentialGroup sequentialGroup =
	    queryPanelLayout.createSequentialGroup();

	// Redo existing components.
	Component[] component = queryPanel.getComponents();
	for (int i = 0; i < component.length; i++) {
	    parallelGroup.add(component[i], GroupLayout.DEFAULT_SIZE,
			      601, Short.MAX_VALUE);
	    sequentialGroup.add(component[i], GroupLayout.DEFAULT_SIZE,
				25, Short.MAX_VALUE);
	}
	// Add the new one.
	if (jPanel1 != null) {
	    parallelGroup.add(jPanel1, GroupLayout.DEFAULT_SIZE,
			      601, Short.MAX_VALUE);
	    sequentialGroup.add(jPanel1, GroupLayout.DEFAULT_SIZE,
				25, Short.MAX_VALUE);
	}

        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(GroupLayout.LEADING)
	    .add(queryPanelLayout.createSequentialGroup()
		 .add(parallelGroup)
		 )
	    );
        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(sequentialGroup)
	    );
    }

    //////////////////////////////////////////////////
    //
    // spvPanelLayout
    //
    public void spvPanelLayout(final JPanel      spvPanel,
			       final JButton     jButton1,
			       final JScrollPane jScrollPane1,
			       final JButton     jButton2,
			       final JScrollPane jScrollPane2,
			       final JButton     jButton3,
			       final JScrollPane jScrollPane3)
    {
        GroupLayout spvPanelLayout = new GroupLayout(spvPanel);
        spvPanel.setLayout(spvPanelLayout);
        spvPanelLayout.setHorizontalGroup(
            spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(spvPanelLayout.createSequentialGroup()
		 .add(spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
		      .add(jScrollPane1, GroupLayout.DEFAULT_SIZE,
			   196, Short.MAX_VALUE)
		      .add(jButton1, GroupLayout.PREFERRED_SIZE,
			   40, GroupLayout.PREFERRED_SIZE))
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
		      .add(jScrollPane2, GroupLayout.DEFAULT_SIZE,
			   196, Short.MAX_VALUE)
		      .add(jButton2, GroupLayout.PREFERRED_SIZE,
			   40, GroupLayout.PREFERRED_SIZE))
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
		      .add(jScrollPane3, GroupLayout.DEFAULT_SIZE,
			   196, Short.MAX_VALUE)
		      .add(jButton3, GroupLayout.PREFERRED_SIZE,
			   40, GroupLayout.PREFERRED_SIZE)
		      )
		 )
	    );

        spvPanelLayout.linkSize(
            new Component[] {
		jButton1, jButton2, jButton3
	    },
	    GroupLayout.HORIZONTAL);

        spvPanelLayout.setVerticalGroup(
            spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, spvPanelLayout.createSequentialGroup()
		 .add(spvPanelLayout.createParallelGroup(GroupLayout.BASELINE)
		      .add(jButton1)
		      .add(jButton2)
		      .add(jButton3))
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(spvPanelLayout.createParallelGroup(GroupLayout.LEADING)
		      .add(jScrollPane2, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE)
		      .add(jScrollPane1, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE)
		      .add(jScrollPane3, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE))
		 .add(2, 2, 2)
		 )
	    );

        spvPanelLayout.linkSize(
            new Component[] {
		jButton1, jButton2, jButton3
	    },
	    GroupLayout.VERTICAL);
    }

    ////////////////////////////////////////////////////
    //
    // mainPanelLayout
    //
    public void mainPanelLayout(final JPanel mainPanel,
				final JPanel jPanel1,
				final JPanel jPanel2,
				final JPanel jPanel3)
    {
	final JPanel topPanel = new JPanel();
	mainTopPanelLayout(topPanel, jPanel1, jPanel2);
        JSplitPane splitPanel = new JSplitPane();
        splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPanel.setTopComponent(topPanel);
	splitPanel.setBottomComponent(jPanel3);

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
		 .add(splitPanel, 0,
		      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		 )
	    );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
		 .add(splitPanel, 0,
		      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		 )
	    );
    }

    public void mainTopPanelLayout(final JPanel topPanel,
				   final JPanel jPanel1,
				   final JPanel jPanel2)
    {
        GroupLayout topPanelLayout = new GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
		 //.addContainerGap()
		 .add(topPanelLayout.createParallelGroup(GroupLayout.LEADING)
		      .add(GroupLayout.TRAILING, jPanel2,
			   GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
			   Short.MAX_VALUE)
		      .add(GroupLayout.TRAILING, jPanel1,
			   GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
			   Short.MAX_VALUE)
		      )
		 //.addContainerGap()
		 )
	    );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
		 .add(jPanel1, GroupLayout.PREFERRED_SIZE,
		      GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jPanel2, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
		 .addPreferredGap(LayoutStyle.RELATED)
		 )
	    );
    }
}

// End of file
