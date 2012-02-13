package com.silenistudios.silenus.raw;

import java.io.Serializable;
import java.util.Vector;

/**
 * This structure contains all transformation matrices for each 
 * @author Karel
 *
 */
public class AnimationFrameData implements Serializable {
	private static final long serialVersionUID = -7111305910456988265L;
	
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
