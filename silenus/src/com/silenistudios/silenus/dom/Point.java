package com.silenistudios.silenus.dom;

/**
 * A simple point used in vector graphics.
 * @author Karel
 *
 */
public class Point {
	
	// x
	double fX;
	
	// y
	double fY;
	
	// constructor
	public Point(double x, double y) {
		fX = x;
		fY = y;
	}
	
	
	// get x
	public double getX() {
		return fX;
	}
	
	
	// get y
	public double getY() {
		return fY;
	}
	
	
	// equals
	public boolean equals(Point p) {
		return Math.abs(fX - p.fX) < 0.0001 && Math.abs(fY - p.fY) < 0.0001;  
	}
	
	
	// to string
	public String toString() {
		return new StringBuilder().append("[").append(fX).append(",").append(fY).append("]").toString();
	}
}
