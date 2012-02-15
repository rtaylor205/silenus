package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;

public class FormButton extends FormField {
	
	// button
	Button fButton;
	
	// constructor - add a button to the form that can be clicked
	public FormButton(Button b) {
		fButton = b;
		initWidget(fButton);
	}
	
	
	// focus widget
	public FocusWidget getFocusWidget() {
		return fButton;
	}
	
	
	// nothing returned
	@Override
	public String getValue() {
		return null;
	}

}
