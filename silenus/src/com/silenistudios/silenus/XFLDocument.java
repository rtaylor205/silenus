package com.silenistudios.silenus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.silenistudios.silenus.dom.*;
import com.silenistudios.silenus.xml.XMLUtility;
import com.silenistudios.silenus.xml.Node;

/**
 * This class will parse an entire XFL scene and generate keyframe data for all objects.
 * It will load the different graphics and call the RenderInterface to draw the animation on screen.
 * @author Karel
 *
 */
public class XFLDocument implements XFLLibrary{
	
	// root directory
	String fRoot;
	
	// renderer
	RenderInterface fRenderer;
	
	// XML Utility
	XMLUtility XMLUtility;
	
	// map of all bitmaps
	Map<String, Bitmap> fBitmaps = new HashMap<String, Bitmap>();
	
	// graphics
	Map<String, Graphic> fGraphics = new HashMap<String, Graphic>();
	
	// scenes
	Map<String, Timeline> fScenes = new HashMap<String, Timeline>();
	
	// width of the document
	int fWidth;
	
	// height of the document
	int fHeight;
	
	// frame rate
	int fFrameRate;
	
	// number of includes not yet loaded
	int fNIncludesLeft;
	
	// create an XFL parser
	public XFLDocument(XMLUtility XMLUtility) {
		this.XMLUtility = XMLUtility;
	}
	
	
	// parse an XFL directory
	public void parseXFL(String directoryName) throws ParseException {
		
		// strip "/" from the path at the end if necessary
		if (directoryName.charAt(directoryName.length()-1) == '/') {
			directoryName = directoryName.substring(0, directoryName.length()-2);
		}
		
		// root dir
		fRoot = directoryName;
		
		// read DOMDocument.xml, the root document
		Node rootNode = XMLUtility.parseXML(fRoot, "DOMDocument.xml");
		loadDOMDocument(rootNode);
	}
	
	
	// load the DOM document
	private void loadDOMDocument(Node root) throws ParseException {
		
		// width and height
		fWidth = XMLUtility.getIntAttribute(root, "width");
		fHeight = XMLUtility.getIntAttribute(root, "height");
		
		// frame rate
		fFrameRate = XMLUtility.getIntAttribute(root, "frameRate", 24);
		
		// load the media and convert the binary files back to png
		Node media = XMLUtility.findNode(root,  "media");
		Vector<Node> bitmaps = XMLUtility.findNodes(media, "DOMBitmapItem");
		for (Node node : bitmaps) {
			Bitmap bitmap = new Bitmap(XMLUtility, fRoot, node);
			fBitmaps.put(bitmap.getName(), bitmap);
		}
		
		
		// read all symbols - don't load them yet!
		Node symbols = XMLUtility.findNode(root, "symbols");
		Vector<Node> includes = XMLUtility.findNodes(symbols, "Include");
		Map<String, Node> nameToNode = new HashMap<String, Node>();
		for (Node node : includes) 	loadInclude(nameToNode, node);
		
		// now, load the graphics
		// by using this trick, we can resolve references immediately
		for (Entry<String, Node> entry : nameToNode.entrySet()) {
			getGraphic(entry.getKey()).loadGraphic(XMLUtility, this, entry.getValue());
		}
		
		// read the scenes
		Vector<Node> scenes = XMLUtility.findNodes(root, "DOMTimeline");
		for (Node node : scenes) {
			Timeline timeline = new Timeline(XMLUtility, this, node);
			fScenes.put(timeline.getName(), timeline);
		}
	}
	
	
	// load an include from a separate XML
	private void loadInclude(final Map<String, Node> nameToNode, Node node) throws ParseException {
		
		// get the href
		String href = XMLUtility.getAttribute(node, "href");
		
		// load the XML file
		Node include = XMLUtility.parseXML(fRoot, "LIBRARY/" + href);
		
		// get name
		String name = XMLUtility.getAttribute(include, "name", "");
		if (name.equals("")) throw new ParseException("Invalid filename found for include: '" + href + "'");
		
		// add this item to the map
		nameToNode.put(name, include);
		
		// see if the symbol type exists
		/*if (!XMLUtility.hasAttribute(include, "symbolType")) {
			// unknown type, skip
			nameToNode.remove(name);
			return;
		}*/
		
		// get the symbol type
		String symbolType = XMLUtility.getAttribute(include, "symbolType", "graphic");
		
		// graphic - only one supported right now
		if (symbolType.equals("graphic")) {
			
			// create a new graphic from this data - this is just a dummy at the moment
			Graphic graphic = new Graphic();
			
			// add to list
			fGraphics.put(name, graphic);
		}
	}
	
	
	// get a graphic
	@Override
	public Graphic getGraphic(String href) throws ParseException {
		
		// try to find the graphic
		Graphic graphic = fGraphics.get(href);
		
		// not found - throw exception
		if (graphic == null) throw new ParseException("Graphic reference could not be resolved: " + href);
		
		// all ok
		return graphic;
	}
	
	
	// get a bitmap
	@Override
	public Bitmap getBitmap(String href) throws ParseException {
		
		// try to find the graphic
		Bitmap bitmap = fBitmaps.get(href);
		
		// not found - throw exception
		if (bitmap == null) throw new ParseException("Bitmap reference could not be resolved: " + href);
		
		// all ok
		return bitmap;
	}
	
	
	// get a scene
	public Timeline getScene(String name) {
		return fScenes.get(name);
	}
	
	
	// get the first scene
	public Timeline getScene() {
		return fScenes.values().iterator().next();
	}
	
	
	// get width
	public int getWidth() {
		return fWidth;
	}
	
	
	// get height
	public int getHeight() {
		return fHeight;
	}
	
	
	// get frame rate
	public int getFrameRate() {
		return fFrameRate;
	}
}
