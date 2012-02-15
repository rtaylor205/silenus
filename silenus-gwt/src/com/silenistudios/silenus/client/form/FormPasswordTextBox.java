package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class FormPasswordTextBox extends FormField {
	
	// the text box
	PasswordTextBox fBox = new PasswordTextBox();
	
	
	// constructor
	public FormPasswordTextBox() {
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
