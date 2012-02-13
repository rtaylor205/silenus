package com.silenistudios.silenus.raw;

import java.util.Vector;

/**
 * This structure contains all transformation matrices for each 
 * @author Karel
 *
 */
public class AnimationFrameData {
	
	// list of bitmaps drawn in this frame
	Vector<AnimationBitmapData> fBitmaps = new Vector<AnimationBitmapData>();
	
	
	// constructor
	public AnimationFrameData() {
	}
	
	
	// add bitmap data
	public void addBitmapData(AnimationBitmapData data) {
		fBitmaps.add(data);
	}
	
	
	// get bitmap data
	public Vector<AnimationBitmapData> getBitmapData() {
		return fBitmaps;
	}
}
