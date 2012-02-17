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
	
	
	// get json
	public String getJSON() {
		StringBuilder ss = new StringBuilder();
		ss.append("[");
		for (int i = 0; i < fBitmaps.size(); ++i) {
			AnimationBitmapData bitmap = fBitmaps.get(i);
			if (i != 0) ss.append(",");
			ss.append("{");
			TransformationMatrix m = bitmap.getTransformationMatrix();
			ss.append("\"translate\":[").append(m.getTranslateX()).append(",").append(m.getTranslateY()).append("],");
			ss.append("\"scale\":[").append(m.getScaleX()).append(",").append(m.getScaleY()).append("],");
			ss.append("\"rotation\":").append(m.getRotation()).append(",");
			ss.append("\"bitmap\":\"").append(bitmap.getBitmap().getSourceHref()).append("\"");
			if (bitmap.hasColorManipulation()) {
				ss.append(",\"colorManipulation\":").append(bitmap.getColorManipulation().getJSON());
			}
			ss.append("}");
		}
		ss.append("]");
		return ss.toString();
	}
}
