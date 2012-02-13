package com.silenistudios.silenus.raw;

import java.io.Serializable;

import com.silenistudios.silenus.dom.Bitmap;


/**
 * This class contains all information about one particular bitmap on one frame of an animation
 * @author Karel
 *
 */
public class AnimationBitmapData implements Serializable {
	private static final long serialVersionUID = 4750362074054547165L;

	// path to the bitmap
	Bitmap fBitmap;
	
	// the transformation matrix
	TransformationMatrix fTransformationMatrix;
	
	// the color manipulation
	ColorManipulation fColorManipulation = null;
	
	
	// constructor
	public AnimationBitmapData(Bitmap bitmap, TransformationMatrix matrix) {
		
		// set the bitmap
		fBitmap = bitmap;
		
		// we copy the transformation matrix because it will be further manipulated later
		fTransformationMatrix = new TransformationMatrix(matrix.getMatrix(), matrix.getTranslateX(), matrix.getTranslateY());
	}
	
	
	// set color manipulation
	public void setColorManipulation(ColorManipulation colorManipulation) {
		fColorManipulation = colorManipulation;
	}
	
	
	// get source href
	public Bitmap getBitmap() {
		return fBitmap;
	}
	
	
	// get matrix
	public TransformationMatrix getTransformationMatrix() {
		return fTransformationMatrix;
	}
	
	
	// get color manipulation
	public ColorManipulation getColorManipulation() {
		return fColorManipulation;
	}
	
	
	// is there color manipulation?
	public boolean hasColorManipulation() {
		return fColorManipulation != null;
	}
}
