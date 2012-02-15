package com.silenistudios.silenus.client.form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class facilitates the generation of generic forms.
 * @author Karel
 *
 */
public class Form extends Composite {
	
	// the main panel
	VerticalPanel fPanel;
	
	// the grid
	Grid fGrid;
	
	// number of fields
	int fNFields = 0;
	
	// submit button
	Button fSubmit = null;
	
	// the different fields
	Map<String, FormField> fFields = new HashMap<String, FormField>();
	
	// the required fields
	List<String> fRequiredFields = new LinkedList<String>();
	
	
	// constructor
	public Form() {
		
		// create the basic form stuff
		fPanel = new VerticalPanel();
		fPanel.setStyleName("form-panel");
		fGrid = new Grid(0, 2);
		fGrid.setStyleName("form-grid");
		fPanel.add(fGrid);
		
		// done
		initWidget(fPanel);
	}
	
	
	// when the form is added to the document, focus the first widget that has a focus widget
	@Override
	protected void onLoad() {
		for (int i = 0; i < fNFields; ++i) {
			FormField widget = (FormField)fGrid.getWidget(i,  1);
			FocusWidget focusWidget = widget.getFocusWidget();
			if (focusWidget != null) {
				focusWidget.setFocus(true);
				break;
			}
		}
	}
	
	
	// add a form field with standard description
	public void addFormField(String fieldName, FormField field) {
		addFormField(fieldName, fieldName, field);
	}
	
	
	// add a form field
	public void addFormField(String fieldDescription, String fieldName, FormField field) {
		
		// add to form
		++fNFields;
		fGrid.resize(fNFields, 2);
		fGrid.setWidget(fNFields-1, 0, new Label(fieldDescription));
		fGrid.setWidget(fNFields-1, 1, field);
		fGrid.getCellFormatter().setStyleName(fNFields-1, 0, "form-field");
		fGrid.getCellFormatter().setStyleName(fNFields-1, 1, "form-input");
		field.setStyleName("form-input");
		
		// add to map
		fFields.put(fieldName, field);
		
		// add enter handler so that we submit on enter
		FocusWidget focusWidget = field.getFocusWidget();
		if (focusWidget != null) {
			focusWidget.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), fSubmit);
					}
				}
			});
		}
	}
	
	
	// add an input box
	public void addTextBox(String fieldDescription, final String fieldName, String defaultValue) {
		FormTextBox box = new FormTextBox();
		box.setValue(defaultValue);
		addFormField(fieldDescription, fieldName, box);
	}
	public void addTextBox(String fieldDescription, final String fieldName) {
		FormTextBox box = new FormTextBox();
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add text area input
	public void addTextArea(String fieldDescription, final String fieldName, String defaultValue) {
		FormTextArea box = new FormTextArea();
		box.setValue(defaultValue);
		addFormField(fieldDescription, fieldName, box);
	}
	public void addTextArea(String fieldDescription, final String fieldName) {
		FormTextArea box = new FormTextArea();
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add a password box
	public void addPasswordTextBox(String fieldDescription, final String fieldName, String defaultValue) {
		FormPasswordTextBox box = new FormPasswordTextBox();
		box.setValue(defaultValue);
		addFormField(fieldDescription, fieldName, box);
	}
	public void addPasswordTextBox(String fieldDescription, final String fieldName) {
		FormPasswordTextBox box = new FormPasswordTextBox();
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add a checkbox
	public void addCheckBox(String fieldDescription, final String fieldName, Boolean defaultValue) {
		FormCheckBox box = new FormCheckBox();
		box.setValue(defaultValue.toString());
		addFormField(fieldDescription, fieldName, box);
	}
	public void addCheckBox(String fieldDescription, final String fieldName) {
		FormCheckBox box = new FormCheckBox();
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add a list box and add items later
	public void addListBox(String fieldDescription, final String fieldName) {
		FormListBox box = new FormListBox(false);
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add a combo (multiple select) box and add items later
	public void addComboBox(String fieldDescription, final String fieldName) {
		FormListBox box = new FormListBox(true);
		addFormField(fieldDescription, fieldName, box);
	}
	
	
	// add an item to an existing list box
	public void addListBoxItem(String fieldName, String item, String value) {
		FormListBox box = (FormListBox)fFields.get(fieldName);
		if (box == null) return;
		box.addItem(item, value);
	}
	
	
	// set the default selected item for a list box
	public void setListBoxSelectedItem(String fieldName, String item) {
		FormListBox box = (FormListBox)fFields.get(fieldName);
		box.setSelectedItem(item);
	}
	
	
	// add submit button
	public void addSubmitButton(final String name) {
		if (fSubmit != null) fSubmit.removeFromParent();
		fSubmit = new Button(name);
		Grid grid = new Grid(1,1);
		grid.setStyleName("form-submit-grid");
		grid.setWidget(0,  0, fSubmit);
		grid.getCellFormatter().setStyleName(0,  0, "form-submit");
		fPanel.add(grid);
		//fPanel.add(fSubmit);
	}
	
	
	// get the submit button
	public Button getSubmitButton() {
		return fSubmit;
	}
	
	
	// set a field to required
	public void setRequired(String fieldName) {
		fRequiredFields.add(fieldName);
	}
	
	
	// check if all required fields were entered
	public boolean isValid() {
		for (String fieldName : fRequiredFields) {
			if (!fFields.containsKey(fieldName) || fFields.get(fieldName).getValue() == null || fFields.get(fieldName).getValue().equals("")) return false;
		}
		return true;
	}
	
	
	// add click handler
	public void addClickHandler(ClickHandler handler) {
		fSubmit.addClickHandler(handler);
	}
	
	
	// get values
	public Map<String, String> getValues() {
		Map<String, String> values = new HashMap<String, String>();
		for (Entry<String, FormField> entry : fFields.entrySet()) values.put(entry.getKey(), entry.getValue().getValue());
		return values;
	}
	
	
	// get one value
	public String getValue(String fieldName) {
		return fFields.get(fieldName).getValue();
	}
	
	
	// set value
	public void setValue(String fieldName, String value) {
		fFields.get(fieldName).setValue(value);
	}
	
	
	// get a form field
	public FormField getFormField(String fieldName) {
		return fFields.get(fieldName);
	}

}
