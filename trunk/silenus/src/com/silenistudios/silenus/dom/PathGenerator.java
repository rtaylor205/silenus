package com.silenistudios.silenus.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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
		
		// list of all edges, sorted by fill type and then mapped by a hash defined by their endpoint for easy connection
		Map<Integer, Map<String, List<Path>>> pathsByColor = new HashMap<Integer, Map<String, List<Path>>>();
		
		// get all Edge nodes
		Vector<Node> edges = XMLUtility.findNodes(root, "Edge");
		int[] fillTypes = new int[2];
		for (Node edge : edges) {
			
			// get points of this edge
			Vector<Point> points = getPoints(XMLUtility, edge);
			for (int i = 0; i < points.size(); i += 2) {
				System.out.print("[ " + points.get(i) + " -> " + points.get(i+1) + " ] ");
			}
			
			// walk over all lines in this edge and try to match them with the diffent open paths
			for (int i = 0; i < points.size(); i += 2) {
				
				// get the different types
				fillTypes[0] = XMLUtility.getIntAttribute(edge, "fillStyle0", -1);
				fillTypes[1] = XMLUtility.getIntAttribute(edge, "fillStyle1", -1);
				int strokeType = XMLUtility.getIntAttribute(edge, "strokeStyle", -1);
				
				// we simply add the stroke paths
				if (strokeType != -1) {
					Path path = new Path(strokeType);
					path.add(points.get(i)); path.add(points.get(i+1));
					fStrokePaths.add(path);
				}
				
				// we consider all open paths
				for (int fillType = 0; fillType < 2; ++fillType) {
					
					// no no fill type set
					if (fillTypes[fillType] == -1) continue;
					
					// create the path - inverted if it's a fillType1
					Path path = new Path(fillTypes[fillType]);
					path.add(points.get(i + fillType));
					path.add(points.get(i + 1-fillType));
					
					// add to the list of paths
					String hash = getPointHash(path.getFirstPoint());
					if (!pathsByColor.containsKey(fillTypes[fillType])) pathsByColor.put(fillTypes[fillType], new HashMap<String, List<Path>>());
					Map<String, List<Path>> paths = pathsByColor.get(fillTypes[fillType]);
					if (!paths.containsKey(hash)) paths.put(hash, new ArrayList<Path>());
					paths.get(hash).add(path);
				}
			}
		}
		
		// now we walk over all paths with the same color and try to merge them
		for (Map<String, List<Path>> hash : pathsByColor.values()) {
			
			// keep going until the hash is empty
			while (hash.size() > 0) {
				
				// get the first list of paths in the hash
				List<Path> paths = hash.values().iterator().next();
				
				// get the first point in there and remove it from the list
				Path path1 = paths.get(0);
				paths.remove(0);
				
				// this list is now empty - delete it from the map
				if (paths.size() == 0) hash.remove(getPointHash(path1.getFirstPoint()));
				
				// compute the hash for the endpoint
				String endHash = getPointHash(path1.getLastPoint());
				
				// find all points that match this hash in the first point
				List<Path> connections = hash.get(endHash);
				
				// invalid connection - might be unknown edge type
				if (connections == null) continue;
				
				// keep going until we can't find any connections anymore
				Path connection = path1;
				while (connections.size() > 0) {
					
					// add the last connection to the collection for sorting, but only do so when the inverse is not already present
					boolean alreadyInverse = false;
					Path connectionInverted = null;
					for (Path p : connections) {
						if (p.getFirstPoint().equals(connection.getLastPoint()) && p.getLastPoint().equals(connection.getFirstPoint())) {
							alreadyInverse = true;
							connectionInverted = p;
						}
					}
					
					
					// invert the last connection and add it to the list, so we can find the next angle
					if (!alreadyInverse) {
						connectionInverted = new Path(connection.getIndex());
						connectionInverted.add(connection.getLastPoint());
						connectionInverted.add(connection.getFirstPoint());
						
						// add ourselves to the list and sort them by angle
						connections.add(connectionInverted);
					}
					
					// sort the connections by angle
					Collections.sort(connections);
					
					// now find our own point in the list - the point previous to it is our connection!
					ListIterator<Path> it = connections.listIterator(connections.size());
					//while (it.hasPrevious() && !it.previous().equals(connectionInverted));
					while (it.hasPrevious()) {
						Path p = it.previous();
						if (!p.equals(connectionInverted)) {
						}
						else {
							if (!alreadyInverse) it.remove();
							break;
						}
					}
					
					// there is a previous path - this is the one!
					if (it.hasPrevious()) {
						connection = it.previous();
						it.remove();
					}
					
					// no previous path - our own line is the smallest angle, get the largest one
					else {
						connection = connections.get(connections.size()-1);
						connections.remove(connections.size()-1);
					}					
					
					// no connections in here anymore
					if (connections.size() == 0) {
						hash.remove(endHash);
					}
					
					// we add this point to the path
					path1.add(connection.getLastPoint());
					
					// closed?
					if (path1.isClosed()) {
						fFillPaths.add(path1);
						break;
					}
					
					// recompute the hash
					endHash = getPointHash(path1.getLastPoint());
					
					// find all points that match this hash in the first point
					connections = hash.get(endHash);
					
					// invalid connection - might be unknown edge type
					if (connections == null) break;
				}
			}
		}
	}
	
	
	// compute a hash for a point
	private static String getPointHash(Point p) {
		return p.getTwipX() + "_" + p.getTwipY();
	}
	

	// the pattern for parsing a line
	// TODO what does the "[" that sometimes occurs instead of "|" mean?
	// TODO sometimes letters come behind the numbers, such as "!895 -3557S1|134 -3366!134 -3366|135 -2925". What does it mean?
	// TODO another weird construct appearing in the edges list: "!10214.5 2608.5[#27EC.6F #A5D.06 10226.5 2697.5"
	private static Pattern LinePattern = Pattern.compile("([-]?[0-9]*[\\.]?[0-9]+)\\s+([-]?[0-9]*[\\.]?[0-9]+)[S]?[0-9]*[\\|\\[]{1}([-]?[0-9]*[\\.]?[0-9]+) ([-]?[0-9]*[\\.]?[0-9]+).*");
	
	
	// get all the points in this edge
	private Vector<Point> getPoints(XMLUtility XMLUtility, Node edge) throws ParseException {
		String edgesString = XMLUtility.getAttribute(edge, "edges");
		String[] lines = edgesString.split("!");
		
		// edgeString starts with an !, so we skip the first split
		Vector<Point> points = new Vector<Point>();
		Point prevp2 = null;
		for (int i = 1; i < lines.length; ++i) {
			
			// get the two coordinates
			Matcher matcher = LinePattern.matcher(lines[i]);
			if (!matcher.matches() || matcher.groupCount() != 4) throw new ParseException("Invalid edges attribute found in DOMShape");
			Point p1 = new Point(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(2)));
			Point p2 = new Point(Double.parseDouble(matcher.group(3)), Double.parseDouble(matcher.group(4)));
			
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
		return fFillPaths;
	}
}
