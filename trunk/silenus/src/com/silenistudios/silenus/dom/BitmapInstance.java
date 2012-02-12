package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.xml.Node;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.XFLLibrary;
import com.silenistudios.silenus.xml.XMLUtility;

public class BitmapInstance extends Instance {
	
	// the bitmap
	Bitmap fBitmap;

	// constructor
	public BitmapInstance(XMLUtility XMLUtility, XFLLibrary library, Node root, int frameIndex) throws ParseException {
		super(XMLUtility, root, frameIndex);
		
		// load the bitmap
		String libraryItemName = XMLUtility.getAttribute(root, "libraryItemName");
		
		// load the bitmap
		fBitmap = library.getBitmap(libraryItemName);
	}
	
	
	// return bitmap
	public Bitmap getBitmap() {
		return fBitmap;
	}
}
