
package com.silenistudios.silenus.dom.fillstyles;

/** A color stop used in a LinearGradient.
 * @author Karel */
public class ColorStop {
	
	// color
	Color fColor;
	
	// ratio
	double fRatio;
	
	// create color stop
	public ColorStop (double ratio, Color color) {
		fRatio = ratio;
		fColor = color;
	}
	
	// get color
	public Color getColor () {
		return fColor;
	}
	
	// get ratio
	public double getRatio () {
		return fRatio;
	}
}
