
package com.silenistudios.silenus.raw;

import java.io.Serializable;

/** This is a data structure that holds information about color manipulation that
 * must be performed on an image before it is drawn on the screen. The operation
 * is defined as follows (for red): red_destination = red_source * redMultiplier
 * + redOffset.
 * @author Karel */
public class ColorManipulation implements Serializable {
	
	// static default color manipulation
	private static ColorManipulation DEFAULT = new ColorManipulation();
	
	private static final long serialVersionUID = 2714820886482014665L;
	
	// interpolate between two color manipulation fields
	// if one of the fields is zero, we set it to the default values
	public static ColorManipulation interpolate (ColorManipulation c1, ColorManipulation c2, double d) {
		ColorManipulation c = new ColorManipulation();
		if (c1 == null)
			c1 = DEFAULT;
		if (c2 == null)
			c2 = DEFAULT;
		c.fAlphaMultiplier = c1.fAlphaMultiplier + (c2.fAlphaMultiplier - c1.fAlphaMultiplier) * d;
		c.fRedMultiplier = c1.fRedMultiplier + (c2.fRedMultiplier - c1.fRedMultiplier) * d;
		c.fGreenMultiplier = c1.fGreenMultiplier + (c2.fGreenMultiplier - c1.fGreenMultiplier) * d;
		c.fBlueMultiplier = c1.fBlueMultiplier + (c2.fBlueMultiplier - c1.fBlueMultiplier) * d;
		c.fAlphaOffset = c1.fAlphaOffset + (c2.fAlphaOffset - c1.fAlphaOffset) * d;
		c.fRedOffset = c1.fRedOffset + (c2.fRedOffset - c1.fRedOffset) * d;
		c.fGreenOffset = c1.fGreenOffset + (c2.fGreenOffset - c1.fGreenOffset) * d;
		c.fBlueOffset = c1.fBlueOffset + (c2.fBlueOffset - c1.fBlueOffset) * d;
		return c;
	}
	
	// alpha multiplier
	double fAlphaMultiplier = 1.0;
	
	// alpha offset
	double fAlphaOffset = 0.0;
	
	// blue multiplier
	double fBlueMultiplier = 1.0;
	
	// blue offset
	double fBlueOffset = 0.0;
	
	// green multiplier
	double fGreenMultiplier = 1.0;
	
	// green offset
	double fGreenOffset = 0.0;
	
	// red multiplier
	double fRedMultiplier = 1.0;
	
	// red offset
	double fRedOffset = 0.0;
	
	// equals - allows you to create a map of different versions of the image so that it isn't calculated at real-time during animation
	public boolean equals (ColorManipulation c) {
		if (this.fAlphaMultiplier - c.fAlphaMultiplier > 0.00001)
			return false;
		if (this.fRedMultiplier - c.fRedMultiplier > 0.00001)
			return false;
		if (this.fGreenMultiplier - c.fGreenMultiplier > 0.00001)
			return false;
		if (this.fBlueMultiplier - c.fBlueMultiplier > 0.00001)
			return false;
		if (this.fAlphaOffset - c.fAlphaOffset > 0.00001)
			return false;
		if (this.fRedOffset - c.fRedOffset > 0.00001)
			return false;
		if (this.fGreenOffset - c.fGreenOffset > 0.00001)
			return false;
		if (this.fBlueOffset - c.fBlueOffset > 0.00001)
			return false;
		return true;
	}
	
	public double getAlphaMultiplier () {
		return this.fAlphaMultiplier;
	}
	
	public double getAlphaOffset () {
		return this.fAlphaOffset;
	}
	
	public double getBlueMultiplier () {
		return this.fBlueMultiplier;
	}
	
	public double getBlueOffset () {
		return this.fBlueOffset;
	}
	
	public double getGreenMultiplier () {
		return this.fGreenMultiplier;
	}
	
	public double getGreenOffset () {
		return this.fGreenOffset;
	}
	
	// get JSON representation
	public String getJSON () {
		StringBuilder ss = new StringBuilder();
		ss.append("{");
		ss.append("\"alphaMultiplier\":").append(fAlphaMultiplier).append(",");
		ss.append("\"redMultiplier\":").append(fRedMultiplier).append(",");
		ss.append("\"greenMultiplier\":").append(fGreenMultiplier).append(",");
		ss.append("\"blueMultiplier\":").append(fBlueMultiplier).append(",");
		ss.append("\"alphaOffset\":").append(fAlphaOffset).append(",");
		ss.append("\"redOffset\":").append(fRedOffset).append(",");
		ss.append("\"greenOffset\":").append(fGreenOffset).append(",");
		ss.append("\"blueOffset\":").append(fBlueOffset);
		ss.append("}");
		return ss.toString();
		
	}
	
	public double getRedMultiplier () {
		return this.fRedMultiplier;
	}
	
	public double getRedOffset () {
		return this.fRedOffset;
	}
	
	public void setAlphaMultiplier (double alphaMultiplier) {
		this.fAlphaMultiplier = alphaMultiplier;
	}
	
	public void setAlphaOffset (double alphaOffset) {
		this.fAlphaOffset = alphaOffset;
	}
	
	public void setBlueMultiplier (double blueMultiplier) {
		this.fBlueMultiplier = blueMultiplier;
	}
	
	public void setBlueOffset (double blueOffset) {
		this.fBlueOffset = blueOffset;
	}
	
	public void setGreenMultiplier (double greenMultiplier) {
		this.fGreenMultiplier = greenMultiplier;
	}
	
	public void setGreenOffset (double greenOffset) {
		this.fGreenOffset = greenOffset;
	}
	
	public void setRedMultiplier (double redMultiplier) {
		this.fRedMultiplier = redMultiplier;
	}
	
	public void setRedOffset (double redOffset) {
		this.fRedOffset = redOffset;
	}
}
