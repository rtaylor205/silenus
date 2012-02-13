package com.silenistudios.silenus.raw;

import java.io.Serializable;
import java.util.Set;
import java.util.Vector;

import com.silenistudios.silenus.dom.Bitmap;

/**
 * This data structure contains all points and locations for one animation (scene).
 * Can be easily serialized and recovered later for re-use.
 * @author Karel
 *
 */
public class AnimationData implements Serializable {
	
	// list of bitmaps used on this scene
	Bitmap[] fBitmaps;
	
	// animation frames
	AnimationFrameData[] fFrames;
	
	// length of the animation
	int fAnimationLength;
	
	// constructor
	public AnimationData(int animationLength) {
		fAnimationLength = animationLength;
		fFrames = new AnimationFrameData[fAnimationLength];
	}
	
	
	// set bitmaps
	public void setBitmaps(Set<Bitmap> bitmaps) {
		fBitmaps = new Bitmap[bitmaps.size()];
		int i = 0;
		for (Bitmap bitmap : bitmaps) fBitmaps[i++] = bitmap;
	}
	
	
	// get bitmaps
	public Bitmap[] getBitmaps() {
		return fBitmaps;
	}
	
	
	// add a frame
	public void setFrame(int frameIndex, AnimationFrameData data) {
		assert(0 <= frameIndex && frameIndex < fFrames.length);
		fFrames[frameIndex] = data;
	}
	
	
	// get frame data
	public Vector<AnimationBitmapData> getFrameData(int frameIndex) {
		assert(0 <= frameIndex && frameIndex < fFrames.length);
		return fFrames[frameIndex].getBitmapData();
	}

}
