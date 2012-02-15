package com.silenistudios.silenus.client.form;

import java.util.Vector;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class FormListBox extends FormField {

	// the text box
	ListBox fBox;
	
	
	// constructor
	public FormListBox(boolean multiple) {
		fBox = new ListBox(multiple);
		initWidget(fBox);
	}
	
	
	// set value
	public void setSelectedItem(String item) {
		for (int i = 0; i < fBox.getItemCount(); ++i) {
			if (fBox.getValue(i).equals(item)) fBox.setItemSelected(i,  true);
		}
	}


	@Override
	public String getValue() {
		StringBuilder ss = new StringBuilder();
		for (int i = 0; i < fBox.getItemCount(); ++i) {
			if (fBox.isItemSelected(i)) {
				if (i != 0) ss.append(";");
				ss.append(fBox.getValue(i));
			}
		}
		return ss.toString();
	}
	
	@Override
	public void setValue(String item) {
		setSelectedItem(item);
	}
	
	// focus widget
	public FocusWidget getFocusWidget() {
		return fBox;
	}
	
	
	// add an item
	public void addItem(String name, String value) {
		fBox.addItem(name, value);
	}
	
	
	// get selected items by vector
	public Vector<String> getSelectedItems() {
		Vector<String> selectedItems = new Vector<String>();
		for (int i = 0; i < fBox.getItemCount(); ++i) {
			if (fBox.isItemSelected(i)) {
				selectedItems.add(fBox.getValue(i));
			}
		}
		return selectedItems;
	}
}
