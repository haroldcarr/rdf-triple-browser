package swing;

import client.Main;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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

public class SwingView 
    extends 
	FrameView 
{
    private JPanel   mainPanel;
    private JMenuBar menuBar;
    private JDialog  aboutBox;

    public SwingView(SingleFrameApplication app) 
    {
        super(app);
        initComponents();
	new Main(this);
        ResourceMap resourceMap = getResourceMap();
    }

    @Action
    public void showAboutBox() 
    {
        if (aboutBox == null) {
            JFrame mainFrame = SwingApp.getApplication().getMainFrame();
            aboutBox = new SwingAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SwingApp.getApplication().show(aboutBox);
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
	    Application.getInstance(SwingApp.class)
	        .getContext().getResourceMap(SwingView.class);

                  menuBar       = new JMenuBar();
        JMenu     fileMenu      = new JMenu();
        JMenuItem exitMenuItem  = new JMenuItem();
        JMenu     helpMenu      = new JMenu();
        JMenuItem aboutMenuItem = new JMenuItem();

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        ActionMap actionMap = 
	    Application.getInstance(SwingApp.class)
	        .getContext().getActionMap(SwingView.class, this);
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

    public void xxx1(final JPanel jPanel3, 
		     final JPanel jTextField4)
    {
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
	    .add(jPanel3Layout.createSequentialGroup()
		 .add(jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
		      .add(jTextField4, GroupLayout.DEFAULT_SIZE,
			   601, Short.MAX_VALUE)
		      )
		 )
	    );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
		 .add(jTextField4, GroupLayout.DEFAULT_SIZE,
		      25, Short.MAX_VALUE)
		 )
	    );
    }

    public void xxx2(final JPanel jPanel3, 
		     final JPanel jTextField4,
		     final JPanel x2)
    {
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
	    .add(jPanel3Layout.createSequentialGroup()
		 .add(jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
		      .add(jTextField4, GroupLayout.DEFAULT_SIZE,
			   601, Short.MAX_VALUE)
		      .add(x2, GroupLayout.DEFAULT_SIZE, 
			   601, Short.MAX_VALUE)
		      )
		 )
	    );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
		 .add(jTextField4, GroupLayout.DEFAULT_SIZE,
		      25, Short.MAX_VALUE)
		 .add(x2, GroupLayout.DEFAULT_SIZE,
		      25, Short.MAX_VALUE)
		 )
	    );
    }

    public void xxx3(final JPanel jPanel3,
		     final JPanel jTextField4,
		     final JPanel x2,
		     final JPanel x3)
    {
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
	    .add(jPanel3Layout.createSequentialGroup()
		 .add(jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
		      .add(jTextField4, GroupLayout.DEFAULT_SIZE,
			   601, Short.MAX_VALUE)
		      .add(x2, GroupLayout.DEFAULT_SIZE,
			   601, Short.MAX_VALUE)
		      .add(x3, GroupLayout.DEFAULT_SIZE,
			   601, Short.MAX_VALUE)
		      )
		 )
	    );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
		 .add(jTextField4, GroupLayout.DEFAULT_SIZE,
		      25, Short.MAX_VALUE)
		 .add(x2, GroupLayout.DEFAULT_SIZE, 
		      25, Short.MAX_VALUE)
		 .add(x3, GroupLayout.DEFAULT_SIZE, 
		      25, Short.MAX_VALUE)
		 )
	    );
    }

    //////////////////////////////////////////////////
    //
    // spvPanelLayout
    //
    public void spvPanelLayout(final JPanel      jPanel2,
			       final JButton     jButton2,
			       final JScrollPane jScrollPane1,
			       final JButton     jButton3,
			       final JScrollPane jScrollPane2,
			       final JButton     jButton4,
			       final JScrollPane jScrollPane3)
    {
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jScrollPane1, GroupLayout.DEFAULT_SIZE,
			 196, Short.MAX_VALUE)
                    .add(jButton2, GroupLayout.PREFERRED_SIZE,
			 40, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
		     .add(jScrollPane2, GroupLayout.DEFAULT_SIZE,
			  196, Short.MAX_VALUE)
		     .add(jButton3, GroupLayout.PREFERRED_SIZE,
			  40, GroupLayout.PREFERRED_SIZE))
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
		      .add(jPanel2Layout.createSequentialGroup()
			   .add(jButton4, GroupLayout.PREFERRED_SIZE,
				40, GroupLayout.PREFERRED_SIZE)
			   .add(148, 148, 148))
		      .add(jScrollPane3, GroupLayout.DEFAULT_SIZE,
			   197, Short.MAX_VALUE)
		      )
		 )
	    );

        jPanel2Layout.linkSize(
            new Component[] {
		jButton2, jButton3, jButton4
	    },
	    GroupLayout.HORIZONTAL);

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
		 .add(jPanel2Layout.createParallelGroup(GroupLayout.BASELINE)
		      .add(jButton2)
		      .add(jButton3)
		      .add(jButton4))
		 .addPreferredGap(LayoutStyle.RELATED)
		 .add(jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
		      .add(jScrollPane2, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE)
		      .add(jScrollPane1, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE)
		      .add(jScrollPane3, GroupLayout.DEFAULT_SIZE,
			   102, Short.MAX_VALUE))
		 .add(2, 2, 2)
		 )
	    );

        jPanel2Layout.linkSize(
            new Component[] {
		jButton2, jButton3, jButton4
	    },
	    GroupLayout.VERTICAL);
    }

    ////////////////////////////////////////////////////
    //
    // browserLayout
    //
    public void browserLayout(final JPanel jPanel3, 
			      final JTextField jTextField4)
    {
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jTextField4, GroupLayout.DEFAULT_SIZE,
		 601, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jTextField4, GroupLayout.DEFAULT_SIZE,
		 156, Short.MAX_VALUE)
	    );
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
        JSplitPane jSplitPane1 = new JSplitPane();
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setTopComponent(topPanel);
	jSplitPane1.setBottomComponent(jPanel3);

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
		 .add(jSplitPane1, 0, 
		      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		 )
	    );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
		 .add(jSplitPane1, 0, 
		      GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		 )
	    );
    }

    public void mainTopPanelLayout(final JPanel mainPanel, 
				   final JPanel jPanel1, 
				   final JPanel jPanel2)
    {
        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
		 //.addContainerGap()
		 .add(mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
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
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
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
