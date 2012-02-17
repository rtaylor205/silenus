package com.silenistudios.silenus.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.xml.Node;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * A shape represents a vector drawing in flash.
 * @author Karel
 *
 */
public class Shape extends Instance {
	
	// fill styles defined for this shape
	Map<Integer, FillStyle> fFillStyles = new HashMap<Integer, FillStyle>();
	
	// stroke styles defined for this shape
	Map<Integer, StrokeStyle> fStrokeStyles = new HashMap<Integer, StrokeStyle>();
	
	// list of completed fill paths
	Vector<Path> fFillPaths = new Vector<Path>();
	
	// list of completed stroke paths
	Vector<Path> fStrokePaths = new Vector<Path>();
	
	
	// constructor
	public Shape(XMLUtility XMLUtility, Node root, int frameIndex) throws ParseException {
		super(XMLUtility, root, frameIndex);
		
		// get all the fills
		if (XMLUtility.hasNode(root, "fills")) {
			Node node = XMLUtility.findNode(root,  "fills");
			Vector<Node> fills = XMLUtility.findNodes(node, "FillStyle");
			System.out.println(fills.size() + " fills");
			for (Node fillNode : fills) {
				FillStyle style = new FillStyle(XMLUtility, fillNode);
				fFillStyles.put(style.getIndex(), style);
			}
		}
		
		// get all the strokes
		if (XMLUtility.hasNode(root,  "strokes")) {
			Node node = XMLUtility.findNode(root,  "strokes");
			Vector<Node> strokes = XMLUtility.findNodes(node, "StrokeStyle");
			for (Node strokeNode : strokes) {
				StrokeStyle style = new StrokeStyle(XMLUtility, strokeNode);
				fStrokeStyles.put(style.getIndex(), style);
			}
		}
		
		// generate the paths from the edges using PathGenerator
		Node node = XMLUtility.findNode(root,  "edges");
		PathGenerator pathGenerator = new PathGenerator();
		pathGenerator.generate(XMLUtility, node);
		fStrokePaths = pathGenerator.getStrokePaths();
		fFillPaths = pathGenerator.getFillPaths();
		
		// verify all the links with fill/stroke styles
		for (Path path : fStrokePaths) if (!fStrokeStyles.containsKey(path.getIndex())) throw new ParseException("Non-existing stroke style refered in path");
		for (Path path : fFillPaths) if (!fFillStyles.containsKey(path.getIndex())) throw new ParseException("Non-existing stroke style refered in path");
	}
	
	
	// get a stroke style
	public StrokeStyle getStrokeStyle(int index) {
		return fStrokeStyles.get(index);
	}
	
	
	// get a fill style
	public FillStyle getFillStyle(int index) {
		return fFillStyles.get(index);
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
