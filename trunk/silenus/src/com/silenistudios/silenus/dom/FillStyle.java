package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.xml.Node;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * A fill style represents how a vector face is filled.
 * @author Karel
 *
 */
public class FillStyle {
	
	// index
	int fIndex;
	
	// color
	Color fColor = new Color();
	
	
	// constructor
	public FillStyle(XMLUtility XMLUtility, Node root) throws ParseException {
		
		// get index
		fIndex = XMLUtility.getIntAttribute(root, "index");
		
		// get solid color
		Node node = XMLUtility.findNode(root, "SolidColor");
		fColor = Color.parseColor(XMLUtility.getAttribute(node, "color", "#000000"));
		fColor.setAlpha(XMLUtility.getDoubleAttribute(node, "alpha", 1.0));
	}
	
	
	// get color
	public Color getColor() {
		return fColor;
	}
	
	
	// get the index
	public int getIndex() {
		return fIndex;
	}
}
