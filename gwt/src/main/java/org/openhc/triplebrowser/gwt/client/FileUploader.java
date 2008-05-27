package org.openhc.trowser.gwt.client;

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
    private final FormPanel uploadForm = new FormPanel();
    
    public FormPanel getFormPanel() { return uploadForm; }

    public FileUploader()
    {
	final HorizontalPanel panel = new HorizontalPanel();

        uploadForm.setAction("/UploadFormHandler");
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
		    Window.alert(fileUpload.getFilename());
		}
	    }
	    public void onSubmitComplete(FormSubmitCompleteEvent event) {
		Window.alert(event.getResults());
	    }
	});
    }
}

// End of file.



