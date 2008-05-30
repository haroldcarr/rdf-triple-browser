//
// Created       : 2008 May 26 (Mon) 13:01:58 by Harold Carr.
// Last Modified : 2008 May 29 (Thu) 18:48:25 by Harold Carr.
// 
// from http://home.izforge.com/index.php/2006/10/29/295-handling-file-uploads-with-the-google-web-toolkit
//

package org.openhc.trowser.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import org.openhc.trowser.gwt.common.Constants;

public class FileUploader
{
    protected final Main main;
    private   final FormPanel uploadForm = new FormPanel();
    
    public FormPanel getFormPanel() { return uploadForm; }

    public FileUploader(Main x)
    {
	this.main = x;

	final HorizontalPanel panel = new HorizontalPanel();

        uploadForm.setAction(GWT.getModuleBaseURL() + main.uploadFormHandler);
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        uploadForm.setMethod(FormPanel.METHOD_POST);

        uploadForm.setWidget(panel);

        final FileUpload fileUpload = new FileUpload();
        fileUpload.setName(Constants.trowserUploadedFile);
        panel.add(fileUpload);

        panel.add(new Button("Submit", new ClickListener() {
	    public void onClick(Widget sender) {
		uploadForm.submit();
	    }
	}));

        // Add an event handler to the form.
        uploadForm.addFormHandler(new FormHandler() {
	    public void onSubmit(FormSubmitEvent event) {
		if (fileUpload.getFilename().length() == 0) {
		    Window.alert("nothing selected");
		    event.setCancelled(true);
		} else {
		    //Window.alert(fileUpload.getFilename());
		    main.getResponseProgressLabel().setText(main.uploading);
		}
	    }
	    public void onSubmitComplete(FormSubmitCompleteEvent event) {
		main.getResponseProgressLabel().setText(main.emptyString);
		//Window.alert(event.getResults());
		// TODO: reset query panel
		main.getQueryManager().doQuery();
	    }
	});
    }
}

// End of file.



