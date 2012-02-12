package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.xml.Node;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.XFLLibrary;
import com.silenistudios.silenus.xml.XMLUtility;

public class SymbolInstance extends Instance {
	
	// the bitmap
	Graphic fGraphic;

	// constructor
	public SymbolInstance(XMLUtility XMLUtility, XFLLibrary library, Node root, int frameIndex) throws ParseException {
		super(XMLUtility, root, frameIndex);
		
		// load the bitmap
		String libraryItemName = XMLUtility.getAttribute(root, "libraryItemName");
		
		// load the bitmap
		// this could be null if the graphic find fails
		fGraphic = library.getGraphic(libraryItemName);
	}
	
	
	// get graphic
	public Graphic getGraphic() {
		return fGraphic;
	}
}
