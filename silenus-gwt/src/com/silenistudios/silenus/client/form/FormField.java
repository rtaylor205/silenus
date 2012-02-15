package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * A form field is a field that can be embedded in the form framework.
 * @author Karel
 *
 */
public abstract class FormField extends Composite {
	
	// get the current value of the field
	public abstract String getValue();
	
	// forward enter? - override if you want
	public FocusWidget getFocusWidget() {
		return null;
	}
	
	// set value - optional
	public void setValue(String s) {
		// do nothing by default
	}
}
