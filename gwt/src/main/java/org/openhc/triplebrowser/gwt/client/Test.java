//
// Created       : 2006 Jul 30 (Sun) 15:53:20 by Harold Carr.
// Last Modified : 2006 Sep 10 (Sun) 20:02:37 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;

public class Test
{
    private Widget widget;

    public Test()
    {

	Command cmd = new Command() {
	    public void execute() {
		Window.alert("You selected a menu item!");
	    }
	};

	MenuBar fooMenu = new MenuBar(true);
	fooMenu.addItem("*", cmd);
	fooMenu.addItem("->", cmd);
	fooMenu.addItem("<-", cmd);
	fooMenu.addItem("new", cmd);
	fooMenu.addItem("all", cmd);
	MenuBar barMenu = new MenuBar();
	barMenu.addItem("xxx", fooMenu);
	barMenu.setAutoOpen(true);
	widget = barMenu;
    }

    public Widget getWidget() { return widget; }
}

// End of file.