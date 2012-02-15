package com.silenistudios.silenus.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * Simple helper class for building blocks of text that can be compiled into one label, 
 * @author Karel
 *
 */
public class TextBlock extends Composite {
	
	// the block
	FlowPanel fPanel = new FlowPanel();
	
	// create a text block
	public TextBlock() {
		initWidget(fPanel);
	}
	
	
	// add text
	public TextBlock add(String s) {
		Label label = new Label(s);
		label.setStyleName("inline");
		fPanel.add(label);
		return this;
	}
	
	// add a link
	public TextBlock add(String name, ClickHandler handler) {
		Anchor a = new Anchor(name);
		a.addStyleName("link");
		a.addStyleName("inline");
		a.addClickHandler(handler);
		fPanel.add(a);
		return this;
	}
	
	// add an anchor
	public TextBlock add(String name, String href) {
		return add(name, href, "");
	}
	
	
	// add an anchor with a target
	public TextBlock add(String name, String href, String target) {
		Anchor a = new Anchor(name, href);
		a.addStyleName("link");
		a.addStyleName("inline");
		a.setTarget(target);
		fPanel.add(a);
		return this;
	}
	
	
	// line break
	public TextBlock br() {
		fPanel.add(new HTML("<br/>"));
		return this;
	}
}
