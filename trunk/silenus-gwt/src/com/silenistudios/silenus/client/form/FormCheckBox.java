package com.silenistudios.silenus.client.form;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;

public class FormCheckBox extends FormField {
	
	// value
	boolean fValue;
	
	// checkbox
	CheckBox fBox;
	
	
	// constructor
	public FormCheckBox() {
		fBox = new CheckBox();
		initWidget(fBox);
		fBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				fValue = event.getValue();
			}
		});
	}
	
	
	// set value
	@Override
	public void setValue(String text) {
		fValue = Boolean.parseBoolean(text);
		fBox.setValue(Boolean.parseBoolean(text));
	}
	
	
	// focus widget
	public FocusWidget getFocusWidget() {
		return fBox;
	}
	

	@Override
	public String getValue() {
		return Boolean.toString(fValue);
	}
}
