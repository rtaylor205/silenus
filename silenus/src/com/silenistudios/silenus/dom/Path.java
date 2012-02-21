package com.silenistudios.silenus.dom;

import java.util.LinkedList;
import java.util.List;

/**
 * A path represents a set of points that follow each other, and that can be filled or stroked.
 * @author Karel
 *
 */
public class Path implements Comparable<Path> {
	
	// internal ID counter
	private static int IdCounter = 0;
	
	// unique ID of this path, used for quick comparison
	long fId;
	
	// set of points in this path
	List<Point> fPoints = new LinkedList<Point>();
	
	// index - can represent a stroke or fill index
	int fIndex;
	
	
	// constructor
	public Path(int index) {
		fIndex = index;
		fId = ++IdCounter;
	}
	
	
	// add a point at the end
	public void add(Point p) {
		fPoints.add(p);
	}
	
	
	// add a point at the specified index
	public void add(int index, Point p) {
		fPoints.add(index, p);
	}
	
	
	// get number of points
	public int getNPoints() {
		return fPoints.size();
	}
	
	
	// get first point
	public Point getFirstPoint() {
		return fPoints.get(0);
	}
	
	
	// get last point
	public Point getLastPoint() {
		return fPoints.get(fPoints.size()-1);
	}
	
	
	// get all points
	public List<Point> getPoints() {
		return fPoints;
	}
	
	
	// get stroke or fill index
	public int getIndex() {
		return fIndex;
	}
	
	
	// closed?
	public boolean isClosed() {
		return fPoints.get(0).equals(fPoints.get(fPoints.size()-1));
	}
	
	
	// equals for list
	public boolean equals(Path path) {
		if (path == null) return false;
		return fId == path.fId;
	}
	
	
	// to string
	@Override
	public String toString() {
		StringBuilder ss = new StringBuilder();
		boolean first = true;
		for (Point p : fPoints) {
			if (first) first = false;
			else ss.append(" -> ");
			ss.append(p.toString());
		}
		return ss.toString();
	}


	// this function will sort paths in terms of their angle between first and end point, from 0° to 360° in a highly efficient manner
	// source: http://stackoverflow.com/questions/7774241/sort-points-by-angle-from-given-axis
	// note: in flash, the topleft is the origin, so we need to invert some stuff to make sure we get the correct angle ordening
	@Override
	public int compareTo(Path path) {
		
		// translate both vectors to the origin - should be the same firstPoint for both!
		int ax = this.getLastPoint().getTwipX() - this.getFirstPoint().getTwipX();
		int ay = this.getLastPoint().getTwipY() - this.getFirstPoint().getTwipY();
		int bx = path.getLastPoint().getTwipX() - path.getFirstPoint().getTwipX();
		int by = path.getLastPoint().getTwipY() - path.getFirstPoint().getTwipY();
		
		if (ay > 0) { // a between 180 and 360
			if (by < 0)  // b between 0 and 360
				return 1;
			return ax < bx ? -1 : 1; // both between 180 and 360 
		} else { // a between 0 and 180
			if (by > 0) // b between 180 and 360
				return -1;
			return ax > bx ? -1 : 1; // both between 0 and 180
		}
	}
	
	
	// get JSON
	public String getJSON() {
		StringBuilder ss = new StringBuilder();
		boolean first = true;
		ss.append("{");
		ss.append("\"points\":[");
		for (Point p : fPoints) {
			if (!first) ss.append(",");
			else first = false;
			ss.append(p.toString());
		}
		ss.append("]");
		ss.append("}");
		return ss.toString();
	}
}
