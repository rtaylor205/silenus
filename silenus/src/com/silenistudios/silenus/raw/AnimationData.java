package com.silenistudios.silenus.raw;

import java.io.Serializable;
import java.util.Set;
import java.util.Vector;

import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.dom.Shape;

/**
 * This data structure contains all points and locations for one animation (scene).
 * Can be easily serialized and recovered later for re-use.
 * @author Karel
 *
 */
public class AnimationData implements Serializable {
	private static final long serialVersionUID = 2283183510219011650L;

	// list of bitmaps used on this scene
	Vector<Bitmap> fBitmaps = new Vector<Bitmap>();
	
	// list of shapes used
	Vector<Shape> fShapes = new Vector<Shape>();
	
	// animation frames
	AnimationFrameData[] fFrames;
	
	// length of the animation
	int fAnimationLength;
	
	// width of the animation
	int fWidth;
	
	// height
	int fHeight;
	
	// frame rate
	int fFrameRate;
	
	// constructor
	public AnimationData(int animationLength, int width, int height, int frameRate) {
		fAnimationLength = animationLength;
		fWidth = width;
		fHeight = height;
		fFrameRate = frameRate;
		fFrames = new AnimationFrameData[fAnimationLength];
	}
	
	
	// set bitmaps
	public void setBitmaps(Set<Bitmap> bitmaps) {
		for (Bitmap bitmap : bitmaps) fBitmaps.add(bitmap);
	}
	
	
	// get bitmap paths
	public Vector<Bitmap> getBitmaps() {
		return fBitmaps;
	}
	
	
	// add a frame
	public void setFrame(int frameIndex, AnimationFrameData data) {
		assert(0 <= frameIndex && frameIndex < fFrames.length);
		fFrames[frameIndex] = data;
	}
	
	
	// get frame data
	public AnimationFrameData getFrameData(int frameIndex) {
		assert(0 <= frameIndex && frameIndex < fFrames.length);
		return fFrames[frameIndex];
	}
	
	
	// get animation length
	public int getAnimationLength() {
		return fFrames.length;
	}
	
	
	// get FPS
	public int getFrameRate() {
		return fFrameRate;
	}
	
	
	// get width
	public int getWidth() {
		return fWidth;
	}
	
	
	// height
	public int getHeight() {
		return fHeight;
	}
	
	
	// export to json
	public String getJSON() {
		StringBuilder ss = new StringBuilder();
		ss.append("{");
		ss.append("\"frameRate\":").append(fFrameRate).append(",");
		ss.append("\"width\":").append(fWidth).append(",");
		ss.append("\"height\":").append(fHeight).append(",");
		ss.append("\"bitmaps\":[");
		for (int i = 0; i < fBitmaps.size(); ++i) {
			if (i != 0) ss.append(",");
			ss.append("\"").append(fBitmaps.get(i).getSourceHref()).append("\"");
		}
		ss.append("],");
		ss.append("\"frames\":[");
		for (int i = 0; i < fFrames.length; ++i) {
			if (i != 0) ss.append(",");
			ss.append(fFrames[i].getJSON());
		}
		ss.append("]}");
		return ss.toString();
	}
}
