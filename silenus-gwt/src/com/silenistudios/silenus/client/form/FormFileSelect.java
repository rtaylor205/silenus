package com.silenistudios.silenus.client.form;

import java.util.LinkedList;
import java.util.List;

import org.vectomatic.arrays.ArrayBuffer;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import org.vectomatic.file.events.ProgressEvent;
import org.vectomatic.file.events.ProgressHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.silenistudios.silenus.client.BasicCallback;
import com.silenistudios.silenus.client.MainCallback;
import com.silenistudios.silenus.client.SilenusServer;
import com.silenistudios.silenus.client.SilenusServerAsync;
import com.silenistudios.silenus.shared.AnimationDTO;

/**
 * A field that provides an upload selection button, and fires a handler
 * whenever an object has been uploaded.
 * @author Karel
 *
 */
public class FormFileSelect extends FormField {
	
	// handler
	public interface Handler {
		public void onFileSelected(String fileName);
		public void onFileLoaded(AnimationDTO fileName);
		public void onFileStarted(String fileName);
	}
	
	
	// admin server
	protected SilenusServerAsync fServer = GWT.create(SilenusServer.class);
	
	// handler
	Handler fHandler;
	
	// main panel
	FlowPanel fPanel = new FlowPanel();
	
	// percent
	Label fPercent = new Label();
	
	// read queue
	List<File> fReadQueue = new LinkedList<File>();
	
	// file path prefix
	String fPrefix;
	
	// reader
	FileReader fReader;
	
	// main
	MainCallback fMain;
	
	
	// create a form uploader
	public FormFileSelect(MainCallback main, boolean multiple, Handler handler) {
		fHandler = handler;
		fMain = main;
		
		// create the form input
		final FileUploadExt uploader = new FileUploadExt();
		uploader.setMultiple(multiple);
		fPanel.add(uploader);
		
		// add progress bar
		FlowPanel bar = new FlowPanel();
		bar.setStyleName("form-upload-progressbar");
		fPercent = new Label("0%");
		fPercent.setWidth("0%");
		fPercent.setStyleName("form-upload-percent");
		bar.add(fPercent);
		//fPanel.add(bar);
		// disabled for now because pointless (uploading is not included in this progress)
		
		// uploader
		uploader.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				FileList files = uploader.getFiles();
				for (File file : files) {
					fReadQueue.add(file);
				}
				readNext();
			}
			
		});
		
		
		// create reader
		fReader = new FileReader();
		fReader.addProgressHandler(new ProgressHandler() {

			@Override
			public void onProgress(ProgressEvent evt) {
				updateProgress(evt);
			}
			
		});
		fReader.addLoadEndHandler(new LoadEndHandler() {

			@Override
			public void onLoadEnd(LoadEndEvent evt) {
				if (fReader.getError() == null) {
					if (fReadQueue.size() > 0) {
						File file = fReadQueue.get(0);
						try {
							
							// convert into byte array
							String s = fReader.getStringResult();
							byte[] bytes = new byte[s.length()];
							for (int i = 0; i < s.length(); ++i) bytes[i] = (byte)s.charAt(i);
							
							// upload the file
							uploadFile(file.getName(), bytes);
						} finally {
							fReadQueue.remove(0);
							readNext();
						}
					}
				}
			}
			
		});
		
		
		// done
		initWidget(fPanel);
	}
	
	
	// set prefix
	public void setPrefix(String prefix) {
		fPrefix = prefix;
	}
	
	
	// upload file
	private void uploadFile(String fileName, byte[] bytes) {
		if (bytes.length > 1024 * 1024 * 2) fMain.error("This file is larger than 2MB! (size: " + ((double)bytes.length / 1024.0 / 1024.0) + "MB)");
		else {
			fHandler.onFileStarted(fileName);
			fServer.parseFLA(bytes, new BasicCallback<AnimationDTO>(fMain) {
				
				@Override
				public void onSuccess(AnimationDTO result) {
					fHandler.onFileLoaded(result);
				}
				
			});
		}
	}
	
	// update progress
	private void updateProgress(ProgressEvent evt) {
		if (evt.lengthComputable()) {
			int percentLoaded = (int)Math.round((evt.loaded() / evt.total()) * 100);
			if (percentLoaded < 100) {
				fPercent.setWidth(percentLoaded + "%");
				fPercent.setText(percentLoaded + "%");
			}
			else {
				fPercent.setWidth("0%");
				fPercent.setText("0%");
			}
		}
	}
	
	
	// read next file
	private void readNext() {
		if (fReadQueue.size() > 0) {
			File file = fReadQueue.get(0);
			try {
				fHandler.onFileSelected(file.getName());
				fReader.readAsBinaryString(file);
			}
			catch(Throwable t) {
				// Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154)
				// Standard-complying browsers will not go in this branch
				handleError(file);
				fReadQueue.remove(0);
				readNext();
			}
		}
	}
	
	
	// handle error in file reading
	private void handleError(File file) {
		FileError error = fReader.getError();
		String errorDesc = "";
		if (error != null) {
			ErrorCode errorCode = error.getCode();
			if (errorCode != null) {
				errorDesc = ": " + errorCode.name();
			}
		}
		fMain.error("File loading error for file: " + file.getName() + "\n" + errorDesc);
	}


	@Override
	public String getValue() {
		return null;
	}
}
