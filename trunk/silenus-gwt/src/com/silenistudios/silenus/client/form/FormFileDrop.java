package com.silenistudios.silenus.client.form;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This form provides a field that users can drag & drop files in for processing.
 * @author Karel
 *
 */
public class FormFileDrop extends FormField {
	
	// handler
	public interface Handler {
		public void onDrop(String fileName);
	}
	
	// the handler
	Handler fHandler;
	
	// our drag widget
	FocusPanel fPanel = new FocusPanel();
	//FlowPanel fPanel = new FlowPanel();
	
	
	// constructor
	public FormFileDrop(Handler handler) {
		fHandler = handler;
		
		// the drop panel
		fPanel.setSize("100px", "100px");
		fPanel.add(new Label("Drag & drop here"));
		fPanel.setStyleName("dragndrop");
		initWidget(fPanel);
		
		fPanel.addDropHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				DataTransfer transfer = event.getDataTransfer();
				int n = getFileListSize(transfer);
				for (int i = 0; i < n; ++i) {
					String fileName = getFileName(transfer, i);
					fHandler.onDrop(fileName);
				}
			}
			
		});
		
	}
	
	
	// jsni get file list size
	private native int getFileListSize(DataTransfer obj) /*-{
		var files = obj.files;
		var count = files.length;
		return count;
	}-*/;
	
	// get name of file i
	private native String getFileName(DataTransfer obj, int i) /*-{
		var files = obj.files;
		return files[i].name;
	}-*/;

	@Override
	public String getValue() {
		return null;
	}
}
