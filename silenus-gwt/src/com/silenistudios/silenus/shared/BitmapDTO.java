package com.silenistudios.silenus.shared;

import java.io.Serializable;

public class BitmapDTO implements Serializable {
	
	// transformation
	public TransformationDTO transformation;
	
	// reference to the right image
	public int imageIndex;
}
