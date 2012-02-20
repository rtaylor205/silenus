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
	List<Path> fOpenPaths = new LinkedList<Path>();
	
	// constructor
	public PathGenerator() {
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
			for (int i = 0; i < points.size(); i += 2) {
				System.out.print("[ " + points.get(i) + " -> " + points.get(i+1) + " ] ");
			}
			System.out.println();
			
			// walk over all lines in this edge and try to match them with the diffent open paths
			for (int i = 0; i < points.size(); i += 2) {
				
				// get the different types
				fillTypes[0] = XMLUtility.getIntAttribute(edge, "fillStyle0", -1);
				fillTypes[1] = XMLUtility.getIntAttribute(edge, "fillStyle1", -1);
				int strokeType = XMLUtility.getIntAttribute(edge, "strokeStyle", -1);
				System.out.println("next " + i + "...");
				// we simply add the stroke paths
				if (strokeType != -1) {
					Path path = new Path(strokeType);
					path.add(points.get(i)); path.add(points.get(i+1));
					fStrokePaths.add(path);
					System.out.println("Open paths: " + fOpenPaths.size());
				}
				
				
				// the connections with other paths
				Path[][] connections = new Path[][]{new Path[]{null, null}, new Path[]{null, null}};
				
				// we consider all open paths
				for (int fillType = 0; fillType < 2; ++fillType) {
					
					// no match
					if (fillTypes[fillType] == -1) continue;
					
					System.out.println("fill type " + fillType);
					// walk over all fillType paths
					Iterator<Path> pathIterator = fOpenPaths.iterator();
					
					// the two points - inverted if the fillType is 1
					Point p1 = points.get(i + fillType);
					Point p2 = points.get(i + 1-fillType);
					System.out.println(p1 + " -> " + p2);
					while (pathIterator.hasNext()) {
						
						// which side of the path was matched by this open?
						Path open = pathIterator.next();
						
						// already connected to this path - skip
						boolean alreadyConnected = false;
						for (int k = 0; k < 4; ++k) {
							if (open.equals(connections[k/2][k%2])) alreadyConnected = true;
						}
						if (alreadyConnected) continue;
						
						// open connects at the start of this path
						if (connections[fillType][0] == null && fillTypes[fillType] == open.getIndex() && open.getLastPoint().equals(p1)) {
							System.out.println("A Found standard append match at end of " + open.getLastPoint());
							open.add(p2);
							connections[fillType][0] = open;
						}
						
						// open connects at the end of this path
						else if (connections[fillType][1] == null && fillTypes[fillType] == open.getIndex() && open.getFirstPoint().equals(p2)) {
							System.out.println("B Found standard prepend match at start of " + open.getFirstPoint());
							open.add(0, p1);
							connections[fillType][1] = open;
						}
						
						// found - are we closed?
						if ((open.equals(connections[fillType][0]) || open.equals(connections[fillType][1])) &&  open.isClosed()) {
							pathIterator.remove();
							fFillPaths.add(open);
							System.out.println("added closed path");
						}
						
						// shortcut - we are done here
						if (connections[fillType][0] != null && connections[fillType][1] != null)  break;
					}
					
					// we connected on both ends of this fill type - we merge the two paths
					if (connections[fillType][0] != null && connections[fillType][1] != null)  {
						
						Iterator<Point> it = connections[fillType][1].getPoints().iterator();
						it.next(); it.next();
						while (it.hasNext()) connections[fillType][0].add(it.next());
					
						// done - remove connections[1]
						fOpenPaths.remove(connections[fillType][1]);
					}
				}
				
				// we haven't connected - add an open path
				for (int fillType = 0; fillType < 2; ++fillType) {
					if (fillTypes[0] != -1 && connections[fillType][0] == null && connections[fillType][1] == null) {
						Path path = new Path(fillTypes[fillType]);
						path.add(points.get(i + fillType));
						path.add(points.get(i + 1-fillType));
						fOpenPaths.add(path);
					}
				}
			}
			
			// cycle through all remaining open paths and make sure they're really still open (shouldn't really happen :( )
			/*for (Path path : fOpenPaths) {
				if (path.isClosed()) fFillPaths.add(path);
			}*/
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
		System.out.println("Number of open paths: " + fOpenPaths.size());
		return fFillPaths;
	}
}
