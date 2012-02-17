package com.silenistudios.silenus.dom;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * A path represents a set of points that follow each other, and that can be filled or stroked.
 * @author Karel
 *
 */
public class Path {
	
	// set of points in this path
	List<Point> fPoints = new LinkedList<Point>();
	
	// index - can represent a stroke or fill index
	int fIndex;
	
	// constructor
	public Path(int index) {
		fIndex = index;
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
}
