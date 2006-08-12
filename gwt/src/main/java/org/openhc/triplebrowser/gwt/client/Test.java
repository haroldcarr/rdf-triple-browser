//
// Created       : 2006 Jul 30 (Sun) 15:53:20 by Harold Carr.
// Last Modified : 2006 Jul 30 (Sun) 15:59:40 by Harold Carr.
//

package com.differentity.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Test
{
    private Widget widget;

    public Test()
    {
	FlexTable t = new FlexTable();
	t.setText(0, 0, "upper-left corner");
	t.setText(2, 2, "bottom-right corner");
	
	t.setWidget(1, 0, new Button("Wide Button"));
	t.getFlexCellFormatter().setColSpan(1, 0, 3);

	widget = t;
    }

    public Widget getWidget() { return widget; }
}

// End of file.