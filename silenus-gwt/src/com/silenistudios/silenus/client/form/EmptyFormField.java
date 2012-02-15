package com.silenistudios.silenus.client.form;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class EmptyFormField extends FormField {
	
	public EmptyFormField(int height) {
		FlowPanel pan = new FlowPanel();
		initWidget(pan);
		pan.setHeight("" + height + "px");
	}

	@Override
	public String getValue() {
		return null;
	}

}
