package com.silenistudios.silenus.dom;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.xml.Node;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * This helper class will parse subsequent <Edge> nodes, and generate the appropriate path
 * objects from the somewhat strange data format that is used in the XML file.
 * @author Karel
 *
 */
public class PathGenerator {
	
	// list of completed fill paths
	Vector<Path> fFillPaths = new Vector<Path>();
	
	// list of completed stroke paths
	Vector<Path> fStrokePaths = new Vector<Path>();
	
	// open paths - paths that still have to be closed
	List<Path>[] fOpenPaths = new List[2];
	
	// constructor
	public PathGenerator() {
		fOpenPaths[0] = new LinkedList<Path>();
		fOpenPaths[1] = new LinkedList<Path>();
	}
	
	
	// generate the appropriate paths
	public void generate(XMLUtility XMLUtility, Node root) throws ParseException {
		
		// get all Edge nodes
		Vector<Node> edges = XMLUtility.findNodes(root, "Edge");
		int[] fillTypes = new int[2];
		System.out.println("Size: " + edges.size());
		for (Node edge : edges) {
			
			// get points of this edge
			Vector<Point> points = getPoints(XMLUtility, edge);
			System.out.println(points.size());
			System.out.print(points.get(0));
			for (int i = 1; i < points.size(); i += 2) {
				System.out.print(" -> " + points.get(i));
			}
			System.out.println();
			// get the different types
			fillTypes[0] = XMLUtility.getIntAttribute(edge, "fillStyle0", -1);
			fillTypes[1] = XMLUtility.getIntAttribute(edge, "fillStyle1", -1);
			int strokeType = XMLUtility.getIntAttribute(edge, "strokeStyle", -1);
			
			// walk over all lines in this edge and try to match them with the diffent open paths
			for (int i = 0; i < points.size(); i += 2) {
				
				// the two points
				Point p1 = points.get(i);
				Point p2 = points.get(i+1);
				
				// we simply add the stroke paths
				if (strokeType != -1) {
					Path path = new Path(strokeType);
					path.add(p1); path.add(p2);
					fStrokePaths.add(path);
					System.out.println("Open paths: " + fOpenPaths[0].size() + "," + fOpenPaths[1].size());
				}
				
				// we consider all open paths
				for (int fillType = 0; fillType < 2; ++fillType) {
					
					// walk over all fillType0 paths
					Iterator<Path> pathIterator = fOpenPaths[fillType].iterator();
					Path prevPath = null;
					while (pathIterator.hasNext()) {
						Path open = pathIterator.next();
						
						// we connect at the end of this path
						if (fillTypes[fillType] != -1  && fillTypes[fillType] == open.getIndex() && open.getLastPoint().equals(points.firstElement())) {
							System.out.println("A Found standard append match at end of " + open.getLastPoint());
							for (int i = 1; i < points.size(); i += 2) open.getPoints().add(points.get(i));
							fillTypes[fillType] = -1;
						}
						
						// we connect at the start of this path
						else if (fillTypes[fillType] != -1 && fillTypes[fillType] == open.getIndex() && open.getFirstPoint().equals(points.lastElement())) {
							System.out.println("B Found standard prepend match at start of " + open.getFirstPoint());
							for (int i = points.size()-2; i >= 0; i -= 2) open.add(0, points.get(i));
							fillTypes[fillType] = -1;
						}
						
						// we connect at the end of this path, but in the wrong direction
						else if (fillTypes[1-fillType] != -1 && fillTypes[1-fillType] == open.getIndex() && open.getLastPoint().equals(points.lastElement())) {
							System.out.println("C Found inverse append at end of " + open.getLastPoint());
							for (int i = points.size()-2; i >= 0; i -= 2) open.add(points.get(i));
							fillTypes[1-fillType] = -1;
						}
						
						// we connect at the start of this path, but in the wrong direction
						else if (fillTypes[1-fillType] != -1 && fillTypes[1-fillType] == open.getIndex() && open.getFirstPoint().equals(points.firstElement())) {
							System.out.println("D Found inverse prepend match at start of " + open.getFirstPoint());
							for (int i = 1; i < points.size(); i += 2) open.add(0, points.get(i));
							fillTypes[1-fillType] = -1;
						}
						
						// no match :(
						else open = null;
						
						// we got a match - see if we have to merge multiple paths
						if (open != null && prevPath != null)  {
							
							// open follows prevPath in the right direction
							if (prevPath.getLastPoint().equals(open.getFirstPoint())) {
								
								Iterator<Point> it = open.getPoints().iterator();
								it.next();
								while (it.hasNext()) prevPath.add(it.next());
								pathIterator.remove();
								open = prevPath;
							}
							
							// open follows prevPath in the wrong direction
							else if (prevPath.getLastPoint().equals(open.getLastPoint())) {
								System.out.println("B Match found between paths:");
								for (Point p : prevPath.getPoints()) System.out.print(p.toString() + " ");
								System.out.println();
								for (Point p : open.getPoints()) System.out.print(p.toString() + " ");
								System.out.println();
								ListIterator<Point> it = open.getPoints().listIterator(open.getPoints().size()-1);
								it.previous();
								while (it.hasPrevious()) prevPath.add(it.previous());
								pathIterator.remove();
								open = prevPath;
							}
							
							// prevPath follows open in the right direction
							else if (prevPath.getFirstPoint().equals(open.getLastPoint())) {
								ListIterator<Point> it = open.getPoints().listIterator(open.getPoints().size()-1);
								it.previous();
								while (it.hasPrevious()) prevPath.add(0, it.previous());
								pathIterator.remove();
								open = prevPath;
							}
							
							// prevPath follows open in the wrong direction
							else if (prevPath.getFirstPoint().equals(open.getFirstPoint())) {
								Iterator<Point> it = open.getPoints().iterator();
								it.next();
								while (it.hasNext()) prevPath.add(0, it.next());
								pathIterator.remove();
								open = prevPath;
							}
						}
						
						// no previous match was found - this is the first one
						//else if (open != null) prevPath = open;
						
						// we closed the path with this additional segment - move to closed paths
						if (open != null && open.isClosed()) {
							pathIterator.remove();
							fFillPaths.add(open);
							System.out.println("added closed path");
						}
						
						// not closed
						else if (open != null) prevPath = open;
					}
				}
				
				// we haven't connected - add an open path
				for (int fillType = 0; fillType < 2; ++fillType) {
					if (fillTypes[fillType] != -1) {
						System.out.println("added open path for " + fillType);
						Path path = new Path(fillTypes[fillType]);
						path.add(points.get(0));
						for (int i = 1; i < points.size(); i += 2) path.add(points.get(i));
						fOpenPaths[fillType].add(path);
					}
				}
				
				// cycle through all remaining open paths and make sure they're really still open (shouldn't really happen :( )
				for (int fillType = 0; fillType < 2; ++fillType) {
					for (Path path : fOpenPaths[fillType]) {
						if (path.isClosed()) fFillPaths.add(path);
					}
				}
			}
			
			
			/**
			 * 
			 * 
			 * NOTE
			 * 
			 * 
			 * TODO
			 * strokes volgen niet altijd op elkaar qua lijnen!!! fills uiteraard wel :)
			 */
		}
	}
	
	
	// the pattern for parsing a line
	// TODO what does the "[" that sometimes occurs instead of "|" mean?
	// TODO sometimes letters come behind the numbers, such as "!895 -3557S1|134 -3366!134 -3366|135 -2925". What does it mean?
	// TODO another weird construct appearing in the edges list: "!10214.5 2608.5[#27EC.6F #A5D.06 10226.5 2697.5"
	private static Pattern LinePattern = Pattern.compile("([-]?[0-9]*[\\.]?[0-9]+) ([-]?[0-9]*[\\.]?[0-9]+)[S]?[0-9]*[\\|\\[]{1}([-]?[0-9]*[\\.]?[0-9]+) ([-]?[0-9]*[\\.]?[0-9]+).*");
	
	
	// get all the points in this edge
	private Vector<Point> getPoints(XMLUtility XMLUtility, Node edge) throws ParseException {
		String edgesString = XMLUtility.getAttribute(edge, "edges");
		String[] lines = edgesString.split("!");
		//System.out.println(edgesString);
		// edgeString starts with an !, so we skip the first split
		Vector<Point> points = new Vector<Point>();
		Point prevp2 = null;
		for (int i = 1; i < lines.length; ++i) {
			
			// get the two coordinates
			Matcher matcher = LinePattern.matcher(lines[i]);
			if (!matcher.matches() || matcher.groupCount() != 4) throw new ParseException("Invalid edges attribute found in DOMShape");
			Point p1 = new Point(Double.parseDouble(matcher.group(1))/20.0, Double.parseDouble(matcher.group(2))/20.0);
			Point p2 = new Point(Double.parseDouble(matcher.group(3))/20.0, Double.parseDouble(matcher.group(4))/20.0);
			
			// make sure the lines connect
			//if (prevp2 != null && !p1.equals(prevp2)) throw new ParseException("Invalid edge: lines do not connect!");
			
			// add the point to the path
			//if (i == 1) points.add(p1);
			points.add(p1);
			points.add(p2);
		}
		
		// done
		return points;
	}
	
	
	// get stroke paths
	public Vector<Path> getStrokePaths() {
		return fStrokePaths;
	}
	
	
	// get fill paths
	public Vector<Path> getFillPaths() {
		System.out.println("Number of fill paths: " + fFillPaths.size());
		System.out.println("Number of open paths: " + fOpenPaths[0].size() + fOpenPaths[1].size());
		return fFillPaths;
	}
}
