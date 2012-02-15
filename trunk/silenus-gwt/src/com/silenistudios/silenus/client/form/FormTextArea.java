package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.TextArea;

public class FormTextArea extends FormField {
	
	// the text box
	TextArea fBox = new TextArea();
	
	
	// constructor
	public FormTextArea() {
		initWidget(fBox);
	}
	
	
	// set value
	@Override
	public void setValue(String text) {
		fBox.setText(text);
	}


	@Override
	public String getValue() {
		return fBox.getText();
	}
	
	// focus widget
	public FocusWidget getFocusWidget() {
		return fBox;
	}
}
