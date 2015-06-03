
package com.silenistudios.silenus.raw;

import com.silenistudios.silenus.dom.Path;
import com.silenistudios.silenus.dom.fillstyles.FillStyle;

/** Contains data for drawing a fill shape.
 * @author Karel */
public class FillData {
	
	// transformation matrix
	TransformationMatrix fMatrix;
	
	// the path
	Path fPath;
	
	// the style
	FillStyle fStyle;
	
	// constructor
	public FillData (FillStyle style, Path path, TransformationMatrix matrix) {
		fStyle = style;
		fPath = path;
		fMatrix = matrix;
	}
	
	// get json
	public String getJSON () {
		StringBuilder ss = new StringBuilder();
		ss.append("{");
		ss.append("\"path\":").append(fPath.getJSON()).append(",");
		ss.append("\"style\":").append(fStyle.getJSON());
		ss.append("}");
		return ss.toString();
	}
	
	// get path
	public Path getPath () {
		return fPath;
	}
	
	// get style
	public FillStyle getStyle () {
		return fStyle;
	}
	
	// the matrix
	public TransformationMatrix getTransformationMatrix () {
		return fMatrix;
	}
}
