package com.differentity.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.differentity.client.MyService;

public class MyServiceImpl
    extends RemoteServiceServlet
    implements MyService
{
    public String myMethod(String s) 
    {
	return "YES:" + s;
    }

    public List getInitialContents(String svoCategory)
    {
	return svoList;
    }

    private List svoList = new ArrayList();
    {
    svoList.add("http://haroldcarr.com");
    svoList.add("http://www.rojo.com/");
    svoList.add("http://google.com");
    svoList.add("http://del.icio.us/");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/authorPrefix");
    svoList.add("http://differentity.com/haroldcarr/authorFirstName");
    svoList.add("http://differentity.com/haroldcarr/authorMiddleName");
    svoList.add("http://differentity.com/haroldcarr/authorLastName");
    svoList.add("http://differentity.com/haroldcarr/authorSuffix");
    svoList.add("http://differentity.com/haroldcarr/authorBorn");
    svoList.add("http://differentity.com/haroldcarr/authorDied");
    svoList.add("http://differentity.com/haroldcarr/authorIdea");
    svoList.add("http://differentity.com/haroldcarr/author");
    svoList.add("http://differentity.com/haroldcarr/ideaInCategory");
    svoList.add("http://differentity.com/haroldcarr/work");
    svoList.add("http://differentity.com/haroldcarr/workAuthor");
    svoList.add("http://differentity.com/haroldcarr/workTitle");
    svoList.add("http://differentity.com/haroldcarr/workPublished");
    svoList.add("http://differentity.com/haroldcarr/workWritten");
    svoList.add("http://differentity.com/haroldcarr/bataille");
    svoList.add("http://differentity.com/haroldcarr/book");
    svoList.add("http://differentity.com/haroldcarr/guilty");
    svoList.add("http://differentity.com/haroldcarr/eroticism");
    svoList.add("http://differentity.com/haroldcarr/blue_of_noon");
    svoList.add("http://differentity.com/haroldcarr/inner_experience");

    svoList.add("http://differentity.com/haroldcarr/similarTo");
    svoList.add("http://differentity.com/haroldcarr/equalTo");
    svoList.add("http://differentity.com/haroldcarr/contraryTo");
    svoList.add("http://differentity.com/haroldcarr/contrarstWith");
    svoList.add("http://differentity.com/haroldcarr/relatesTo");
    svoList.add("http://differentity.com/haroldcarr/fate");
    svoList.add("http://differentity.com/haroldcarr/formlessness");
    svoList.add("http://differentity.com/haroldcarr/god");
    svoList.add("http://differentity.com/haroldcarr/stability");
    svoList.add("http://differentity.com/haroldcarr/reason");
    svoList.add("http://differentity.com/haroldcarr/nothingness");
    svoList.add("http://differentity.com/haroldcarr/chance");
    svoList.add("http://differentity.com/haroldcarr/strength");
    svoList.add("http://differentity.com/haroldcarr/death");
    svoList.add("http://differentity.com/haroldcarr/anguish");
    svoList.add("http://differentity.com/haroldcarr/silence");
    svoList.add("http://differentity.com/haroldcarr/limit");
    }
}

// End of file.
