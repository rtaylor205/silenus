package com.silenistudios.silenus.raw;

import java.io.Serializable;
import java.util.Vector;

/**
 * This data structure contains all points and locations for one animation (scene).
 * Can be easily serialized and recovered later for re-use.
 * @author Karel
 *
 */
public class AnimationData implements Serializable {
	private static final long serialVersionUID = 2283183510219011650L;

	// list of bitmaps used on this scene
	Vector<String> fBitmapPaths = new Vector<String>();
	
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
	public void addBitmapPath(String bitmapPath) {
		fBitmapPaths.add(bitmapPath);
	}
	
	
	// get bitmap paths
	public Vector<String> getBitmapPaths() {
		return fBitmapPaths;
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
