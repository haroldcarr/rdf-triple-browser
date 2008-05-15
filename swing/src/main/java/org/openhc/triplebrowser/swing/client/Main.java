//
// Created       : 2008 May 14 (Wed) 13:09:35 by Harold Carr.
// Last Modified : 2008 May 14 (Wed) 20:13:57 by Harold Carr.
//

package client;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class Main 
    extends
	JFrame 
{

    private final JRadioButton selectedQueryLineButton = new JRadioButton();
    private final JButton addQueryLineButton = new JButton();

    private final JComboBox subjectComboBox = new JComboBox();
    private final JTextField subjectTextField = new JTextField();

    private final JComboBox propertyComboBox = new JComboBox();
    private final JTextField propertyTextField = new JTextField();

    private final JComboBox valueComboBox = new JComboBox();
    private final JTextField valueTextField = new JTextField();

    private final JButton subjectExpandButton = new JButton();
    private final JList subjectList = new JList();
    private final JScrollPane subjectScrollPane = new JScrollPane(subjectList);

    private final JButton propertyExpandButton = new JButton();
    private final JList propertyList = new JList();
    private final JScrollPane propertyScrollPane = new JScrollPane(propertyList);

    private final JButton valueExpandButton = new JButton();
    private final JList valueList = new JList();
    private final JScrollPane valueScrollPane = new JScrollPane(valueList);

    private final JTextArea webBrowser = new JTextArea();

    private final java.util.Properties resourceMap = 
	new java.util.Properties();

    public Main()
    {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() 
    {
	resourceMap.setProperty("selectedQueryLineButton", 
				"selectedQueryLineButton");
	resourceMap.setProperty("addQueryLineButton.text", "+");
	resourceMap.setProperty("subjectTextField.text", "?subject");
	resourceMap.setProperty("propertyTextField.text", "?property");
	resourceMap.setProperty("valueTextField.text", "?value");

	resourceMap.setProperty("subjectExpandButton.text", "+");
	resourceMap.setProperty("propertyExpandButton.text", "+");
	resourceMap.setProperty("valueExpandButton.text", "+");
	resourceMap.setProperty("subjectExpandButton.font",
				"subjectExpandButton.font");
	resourceMap.setProperty("propertyExpandButton.font",
				"propertyExpandButton.font");
	resourceMap.setProperty("valueExpandButton.font",
				"valueExpandButton.font");

	resourceMap.setProperty("subjectList.font", "subjectList.font");
	resourceMap.setProperty("propertyList.font", "propertyList.font");
	resourceMap.setProperty("valueList.font", "valueList.font");


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setName("FUHZ.BIZ");

	// Query line

        selectedQueryLineButton.setText(
            resourceMap.getProperty("selectedQueryLineButton.text"));
        selectedQueryLineButton.setName("selectedQueryLineButton");

        addQueryLineButton.setText(
            resourceMap.getProperty("addQueryLineButton.text"));
        addQueryLineButton.setName("addQueryLineButton");

        subjectComboBox.setModel(
            new DefaultComboBoxModel(new String[]
                {
		    "v",
		    "clear", 
		    "all",
		    "-->",
		    "<--"
		}));
        subjectComboBox.setName("subjectComboBox");
        subjectExpandButton.setText(
            resourceMap.getProperty("subjectExpandButton.text"));
        //subjectExpandButton.setFont(resourceMap.getProperty("subjectExpandButton.font"));
        subjectExpandButton.setName("subjectExpandButton");
        subjectTextField.setText(
            resourceMap.getProperty("subjectTextField.text"));
        subjectTextField.setName("subjectTextField");


        propertyComboBox.setModel(
	    new DefaultComboBoxModel(new String[]
		{
		    "v",
		    "clear", 
		    "all",
		    "-->",
		    "<--"
		}));
        propertyComboBox.setName("propertyComboBox");
        propertyTextField.setText(
            resourceMap.getProperty("propertyTextField.text"));
        propertyTextField.setName("propertyTextField");
        //propertyExpandButton.setFont(resourceMap.getProperty("propertyExpandButton.font"));
        propertyExpandButton.setText(
            resourceMap.getProperty("propertyExpandButton.text"));
        propertyExpandButton.setName("propertyExpandButton");


        valueComboBox.setModel(
            new DefaultComboBoxModel(new String[]
		{
		    "v",
		    "clear", 
		    "all",
		    "-->",
		    "<--"
		}));
        valueComboBox.setName("valueComboBox");
        valueTextField.setText(
            resourceMap.getProperty("valueTextField.text"));
        valueTextField.setName("valueTextField");
        //valueExpandButton.setFont(resourceMap.getProperty("valueExpandButton.font"));
        valueExpandButton.setText(
            resourceMap.getProperty("valueExpandButton.text"));
        valueExpandButton.setName("valueExpandButton");

	// DATA

        subjectScrollPane.setName("subjectScrollPane");
        //subjectList.setFont(resourceMap.getProperty("subjectList.font"));
        subjectList.setModel(
            new AbstractListModel() 
	    { String[] strings =
	     {
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/",
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/"
	     };
		public int getSize() { return strings.length; }
		public Object getElementAt(int i) { return strings[i]; }
	    });
        subjectList.setName("subjectList");
        subjectScrollPane.setViewportView(subjectList);
	//subjectScrollPane.setPreferredSize(new Dimension(250, 80));
	//subjectScrollPane.setMinimumSize(new Dimension(250, 80));
	//subjectScrollPane.setAlignmentX(LEFT_ALIGNMENT);



        propertyScrollPane.setName("propertyScrollPane");
        //propertyList.setFont(resourceMap.getProperty("propertyList.font"));
        propertyList.setModel(
            new AbstractListModel() 
	    { String[] strings =
	      {
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/",
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/"
	      };
		public int getSize() { return strings.length; }
		public Object getElementAt(int i) { return strings[i]; }
	    });
        propertyList.setName("propertyList");
        propertyScrollPane.setViewportView(propertyList);

        valueScrollPane.setName("valueScrollPane");
        //valueList.setFont(resourceMap.getProperty("valueList.font"));
        valueList.setModel(
            new AbstractListModel() 
	    { String[] strings = 
	      {
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/",
		 "http://google.com/", 
		 "http://openhc.org/",
		 "http://haroldcarr.org/",
		 "http://flaviacwood.org/",
		 "http://cowdaddies.com/",
		 "http://differntity.com/",
		 "http://fuhz.biz/"
	      };
		public int getSize() { return strings.length; }
		public Object getElementAt(int i) { return strings[i]; }
	    });
        valueList.setName("valueList");
        valueScrollPane.setViewportView(valueList);


	// Browse current selection

        webBrowser.setColumns(20);
        webBrowser.setRows(5);
        webBrowser.setName("webBrowser");


	// Query Layout

	JPanel queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.LINE_AXIS));
	queryPanel.add(selectedQueryLineButton);
	queryPanel.add(Box.createRigidArea(new Dimension(0,5)));
	queryPanel.add(addQueryLineButton);
	queryPanel.add(Box.createRigidArea(new Dimension(0,5)));
	queryPanel.add(subjectComboBox);
	queryPanel.add(subjectTextField);
	queryPanel.add(Box.createRigidArea(new Dimension(0,5)));
	queryPanel.add(propertyComboBox);
	queryPanel.add(propertyTextField);
	queryPanel.add(Box.createRigidArea(new Dimension(0,5)));
	queryPanel.add(valueComboBox);
	queryPanel.add(valueTextField);
	queryPanel.add(Box.createRigidArea(new Dimension(0,5)));
	queryPanel.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(10,10,10,10));

	// Data list layout

	JPanel dataPanel = new JPanel();
	dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.LINE_AXIS));
	dataPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

	dataPanel.add(Box.createHorizontalGlue());

	dataPanel.add(subjectExpandButton);
	dataPanel.add(subjectScrollPane);
	dataPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	dataPanel.add(propertyExpandButton);
	dataPanel.add(propertyScrollPane);
	dataPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	dataPanel.add(valueExpandButton);
	dataPanel.add(valueScrollPane);
	dataPanel.add(Box.createRigidArea(new Dimension(10, 0)));



	// Put everything together using the content pane's BorderLayout.
	java.awt.Container contentPane = getContentPane();
	contentPane.add(queryPanel, java.awt.BorderLayout.NORTH);
	contentPane.add(dataPanel, java.awt.BorderLayout.CENTER);
	contentPane.add(webBrowser, java.awt.BorderLayout.SOUTH); 



        pack();





    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
