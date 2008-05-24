//
// Created       : 2006 Jun 14 (Wed) 18:29:38 by Harold Carr.
// Last Modified : 2008 May 19 (Mon) 16:09:34 by Harold Carr.
//

package client;

import java.awt.BorderLayout;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

//import com.differentity.client.BrowserHistory;
import client.Main;
//import com.differentity.client.QueryRequest;
//import com.differentity.client.Test; // *****


public class MainPanel
{
    //private final Label responseProgressLabel;
    private final SPVPanel subjectPanel;
    private final SPVPanel propertyPanel;
    private final SPVPanel valuePanel;
    //private final JPanel subjectVerticalPanel;
    //private final JPanel propertyVerticalPanel;
    //private final JPanel valueVerticalPanel;
    private final JPanel spvHorizontalPanel;
    //private final java.awt.Container dockPanel;
    //private final HTML copyright;
    private final QueryPanel queryPanel;
    private final WebBrowser frameCurrentSelection;
    //private final JFrame topPanel;

    MainPanel() 
    {

	//responseProgressLabel = new Label();

	/*
	topPanel = new JFrame();
        topPanel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        topPanel.setName("FUHZ.BIZ");
	*/

	//
	// QueryPanel created before SPV panels since needed.
	//

	//queryPanel = new QueryPanel(topPanel);
	queryPanel = new QueryPanel();

	//
	// Subject, Property, Value panels.
	// Create now to get contents from server.
	//

	subjectPanel  = new SPVPanel(Main.subject);
	propertyPanel = new SPVPanel(Main.property);
	valuePanel    = new SPVPanel(Main.value);
	//subjectVerticalPanel  = subjectPanel.getPanel();
	//propertyVerticalPanel = propertyPanel.getPanel();
	//valueVerticalPanel    = valuePanel.getPanel();
	spvHorizontalPanel = new JPanel();
	/*
	spvHorizontalPanel.setLayout(new BoxLayout(spvHorizontalPanel,
						   BoxLayout.LINE_AXIS));
	spvHorizontalPanel.setBorder(BorderFactory.createEtchedBorder());
	spvHorizontalPanel.add(subjectVerticalPanel);
	spvHorizontalPanel.add(propertyVerticalPanel);
	spvHorizontalPanel.add(valueVerticalPanel);
	*/
	Main.getSwingView().spvPanelLayout(spvHorizontalPanel,
					   subjectPanel.getButton(),
					   subjectPanel.getScrollPane(),
					   propertyPanel.getButton(),
					   propertyPanel.getScrollPane(),
					   valuePanel.getButton(),
					   valuePanel.getScrollPane());

	//
	// Main panel.
	//    

	// TOP

	/*
	RootPanel.get("slot0").add(DevTime.makeStatusWidgets());
	*/
	/*
	final HorizontalPanel topPanel = new HorizontalPanel();
	topPanel.setStyleName("topPanel");
	topPanel.setHorizontalAlignment(DockPanel.ALIGN_LEFT);
	topPanel.add(new HTML("a.nalogo.us / haroldcarr"));
	topPanel.add(responseProgressLabel);
	RootPanel.get("top").add(topPanel);
	*/
	// MIDDLE
	/*
	dockPanel = topPanel.getContentPane();




	dockPanel.add(queryPanel.getPanel(), BorderLayout.NORTH);

	dockPanel.add(spvHorizontalPanel, BorderLayout.CENTER);
	*/



	//JPanel browserPanel = new JPanel();
	if (Main.realBrowser) {
	    frameCurrentSelection = WebBrowser.create("DJNATIVESWING");
	} else {
	    frameCurrentSelection = WebBrowser.create("TEXTAREA");
	}
	//Main.getSwingView().browserLayout(browserPanel, frameCurrentSelection);

	Main.getSwingView().mainPanelLayout(
	    Main.getSwingView().getSwingMainPanel(),
	    queryPanel.getPanel(),
	    spvHorizontalPanel,
	    /*browserPanel*/ frameCurrentSelection);

	/*
	//frameCurrentSelection.setPixelSize(980, 300); // REVISIT
	dockPanel.add(frameCurrentSelection, BorderLayout.SOUTH);

	// Host HTML has elements with IDs are "slot1", "slot2".
	// Better: Search for all elements with a particular CSS class 
	// and replace them with widgets.

	// XXX - test
	//RootPanel.get("bottom").add(new Test().getWidget());

	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		topPanel.pack();
		topPanel.setVisible(true);
		Main.getMainPanel().doQuery(true);
	    }
	});
	*/
    }

    public void doQuery(final boolean keepHistory)
    {
	List triples = new ArrayList();
	Iterator hpi = 
	    Arrays.asList(queryPanel.getPanel().getComponents()).iterator();
	while (hpi.hasNext()) {
	    JPanel triple = (JPanel) hpi.next();
	    Iterator i = Arrays.asList(triple.getComponents()).iterator();
	    i.next(); // skip RadioButton;
	    i.next(); // skip Button;
	    i.next(); // skip subject MenuBar
	    final String subject  = 
		getSPVQueryValue(Main.qsubject,  (JTextField) i.next());
	    i.next(); // skip property MenuBar
	    final String property = 
		getSPVQueryValue(Main.qproperty, (JTextField) i.next());
	    i.next(); // skip value MenuBar
	    final String value    = 
		getSPVQueryValue(Main.qvalue,    (JTextField) i.next());
	    triples.add(new Triple(subject, property, value));
	}

	doQuery(keepHistory, triples,
	        Main.qsubject + Main.qproperty + Main.qvalue);
    }
    /*
    public void doQuery(final boolean keepHistory)
    {
	List triples = new ArrayList();
	Iterator i = 
	    Arrays.asList(queryPanel.getPanel().getComponents()).iterator();
	    i.next(); // skip RadioButton;
	    i.next(); // skip Button;
	    i.next(); // skip subject MenuBar
	    final String subject  = 
		getSPVQueryValue(Main.qsubject,  (JTextField) i.next());
	    i.next(); // skip property MenuBar
	    final String property = 
		getSPVQueryValue(Main.qproperty, (JTextField) i.next());
	    i.next(); // skip value MenuBar
	    final String value    = 
		getSPVQueryValue(Main.qvalue,    (JTextField) i.next());
	    triples.add(new Triple(subject, property, value));

	doQuery(keepHistory, triples,
	        Main.qsubject + Main.qproperty + Main.qvalue);
    }
    */
    public void doQuery(final boolean keepHistory,
			final String subject, final String property,
			final String value, final String setContentsOf)
    {
	List triples = new ArrayList();
	triples.add(new Triple(subject, property, value));
	doQuery(keepHistory, triples, setContentsOf);
    }

    public void doQuery(final boolean keepHistory, final List triples,
			final String setContentsOf)
    {
	QueryRequest queryRequest = new QueryRequest(triples, setContentsOf);

	// "this" so async request can call handleQueryResponse.
	Main.getServerCalls().doQuery(this, queryRequest);
	/*
	Main.getBrowserHistory()
	    .recordDoQuery(keepHistory, triples, setContentsOf);
	*/
    }

    public void handleQueryResponse(QueryResponse queryResponse)
    {
	String setContentsOf = queryResponse.getSetContentsOf();
	if (setContentsOf.indexOf(Main.qsubject)  != -1) {
	    subjectPanel .setContents(queryResponse.getSubject());
	}
	if (setContentsOf.indexOf(Main.qproperty) != -1) {
	    propertyPanel.setContents(queryResponse.getProperty());
	}
	if (setContentsOf.indexOf(Main.qvalue)    != -1) {
	    valuePanel   .setContents(queryResponse.getValue());
	}
	//topPanel.pack(); // ***** REVISIT
	//DevTime.getQueryStatusHTML().setHTML(queryResponse.getStatus());
    }

    public void spvLinkClicked(final String category, final String url)
    {
	if (category.equals(Main.subject)) {
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextField().setText(url);
	} else if (category.equals(Main.property)) {
	    Main.getMainPanel()
		.getQueryPanel().getPropertyTextField().setText(url);
	} else if (category.equals(Main.value)) {
            Main.getMainPanel()
		.getQueryPanel().getValueTextField().setText(url);
	} else {
	    // TODO: FIX
	    Main.getMainPanel()
		.getQueryPanel().getSubjectTextField().setText("ERROR");
	    return;
	}
	doQuery(true);
    }

    //
    // The default is only there in case user puts in a blank string.
    // The system will never do that.
    //
    private String getSPVQueryValue(final String def, final JTextField textBox)
    {
	String text = textBox.getText();
	if (text != null && (! text.equals(""))) {
	    return text;
	}
	return def;
    }

    //public Label      getResponseProgressLabel()
    //    { return responseProgressLabel; }
    public QueryPanel getQueryPanel()    { return queryPanel; }
    public SPVPanel   getSubjectPanel()  { return subjectPanel; }
    public SPVPanel   getPropertyPanel() { return propertyPanel; }
    public SPVPanel   getValuePanel()    { return valuePanel; }
    public WebBrowser getFrameCurrentSelection() 
                                         { return frameCurrentSelection; }
}

// End of file.
