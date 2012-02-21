package com.silenistudios.silenus.raw;

import java.io.Serializable;
import java.util.Vector;

import com.silenistudios.silenus.dom.Shape;

/**
 * This structure contains all transformation matrices for each 
 * @author Karel
 *
 */
public class AnimationFrameData implements Serializable {
	private static final long serialVersionUID = -7111305910456988265L;
	
	// list of bitmaps drawn in this frame
	Vector<AnimationBitmapData> fBitmaps = new Vector<AnimationBitmapData>();
	
	// strokes
	Vector<StrokeData> fStrokes = new Vector<StrokeData>();
	
	// fills
	Vector<FillData> fFills = new Vector<FillData>();
	
	
	// constructor
	public AnimationFrameData() {
	}
	
	
	// add stroke path
	public void addStroke(StrokeData data) {
		fStrokes.add(data);
	}
	
	
	// add fill path
	public void addFill(FillData data) {
		fFills.add(data);
	}
	
	
	// add bitmap data
	public void addBitmapData(AnimationBitmapData data) {
		fBitmaps.add(data);
	}
	
	
	// get bitmap data
	public Vector<AnimationBitmapData> getBitmapData() {
		return fBitmaps;
	}
	
	
	// get strokes
	public Vector<StrokeData> getStrokes() {
		return fStrokes;
	}
	
	
	// get fills
	public Vector<FillData> getFills() {
		return fFills;
	}
	
	
	// get json
	public String getJSON() {
		StringBuilder ss = new StringBuilder();
		ss.append("{");
		ss.append("\"bitmaps\":[");
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
		ss.append("],");
		ss.append("\"strokes\":[");
		for (int i = 0; i < fStrokes.size(); ++i) {
			StrokeData stroke = fStrokes.get(i);
			if (i != 0) ss.append(",");
			ss.append(stroke.getJSON());
		}
		ss.append("],");
		ss.append("\"fills\":[");
		for (int i = 0; i < fFills.size(); ++i) {
			FillData fill = fFills.get(i);
			if (i != 0) ss.append(",");
			ss.append(fill.getJSON());
		}
		ss.append("]");
		ss.append("}");
		return ss.toString();
	}
}
