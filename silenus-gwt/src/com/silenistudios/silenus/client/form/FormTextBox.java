package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.TextBox;

public class FormTextBox extends FormField {
	
	// the text box
	TextBox fBox = new TextBox();
	
	
	// constructor
	public FormTextBox() {
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
