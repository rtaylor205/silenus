package com.silenistudios.silenus.dom;

/**
 * A single line piece used within a shape.
 * @author Karel
 *
 */
public class Line {
	
	// coordinates
	public double x0, y0, x1, y1;
	
	
	// constructor
	public Line(double x0, double y0, double x1, double y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}
	
	
	// does this line follow the previous one?
	public boolean follows(Line prev) {
		return prev.x1 - this.x0 < 0.0001 && prev.y1 - this.y0 < 0.0001;
	}
}
