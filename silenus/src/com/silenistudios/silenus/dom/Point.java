package com.silenistudios.silenus.dom;

/**
 * A simple point used in vector graphics.
 * @author Karel
 *
 */
public class Point {
	
	// x-value in double twips (cause half-twips can appear)
	int fX;
	
	// y in double twips (cause half-twips can appear)
	int fY;
	
	// constructor
	public Point(double xTwip, double yTwip) {
		fX = (int)(xTwip * 2.0);
		fY = (int)(yTwip * 2.0);
	}
	
	
	// get x
	public double getX() {
		return fX / 20.0 / 2.0;
	}
	
	
	// get y
	public double getY() {
		return fY / 20.0 / 2.0;
	}
	
	
	// get twip x
	public int getTwipX() {
		return fX;
	}
	
	
	// get twip y
	public int getTwipY() {
		return fY;
	}
	
	
	// equals
	public boolean equals(Point p) {
		//return Math.abs(fX - p.fX) < 0.0001 && Math.abs(fY - p.fY) < 0.0001;  
		return fX == p.fX && fY == p.fY;
	}
	
	
	// to string
	@Override
	public String toString() {
		return new StringBuilder().append("[").append(getX()).append(",").append(getY()).append("]").toString();
	}
}
