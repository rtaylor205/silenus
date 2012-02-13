package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.XFLLibrary;
import com.silenistudios.silenus.raw.TransformationMatrix;
import com.silenistudios.silenus.xml.XMLUtility;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.silenistudios.silenus.xml.Node;


/**
 * A keyframe contains a set of symbols and/or bitmaps that it animates,
 * and transformation data for these symbols.
 * @author Karel
 *
 */
public class Keyframe {
	
	// list of symbol instances in this keyframe, mapped by their library name for quick access
	Map<String, SymbolInstance> fSymbolInstancesMap = new HashMap<String, SymbolInstance>();
	
	// list of bitmap instances, mapped by their library name for quick access
	Map<String, BitmapInstance> fBitmapInstancesMap = new HashMap<String, BitmapInstance>();
	
	// list of symbol instances in the right drawing order
	List<SymbolInstance> fSymbolInstances = new LinkedList<SymbolInstance>();
	
	// list of bitmap instances in the right drawing order
	List<BitmapInstance> fBitmapInstances = new LinkedList<BitmapInstance>();
	
	// index
	int fIndex;
	
	// the bezier curve describing the motion ease
	BezierCurve fEaseCurve = null;
	
	// is there a tween defined in this keyframe?
	boolean fIsTween = false;
	
	// IK tween duration
	int fIKTweenDuration = 0;
	
	// IK Tree used for IK motion
	IKTree fIKTree = null;
	
	
	// load a keyframe
	public Keyframe(XMLUtility XMLUtility, XFLLibrary library, Node root) throws ParseException {
		
		// get frame index
		fIndex = XMLUtility.getIntAttribute(root, "index");
		
		// is there a tween here?
		fIsTween = XMLUtility.hasAttribute(root,  "tweenType");
		
		// get the tween type
		String tweenType = XMLUtility.getAttribute(root,  "tweenType", "");
		
		// if it's an IK tween, we do some preparation by loading the in-between matrices
		Vector<TransformationMatrix> inBetweenMatrices = null;
		if (tweenType.equals("IK pose")) {
			
			// get duration of the animation
			try {
				fIKTweenDuration = XMLUtility.getIntAttribute(root,  "duration");
				
				// get the IK Tree
				Node tree = XMLUtility.findNode(root, "IKTree");
				fIKTree = new IKTree(XMLUtility, tree);
				
				// get the list of in between matrices
				Node frameList = XMLUtility.findNode(root, "betweenFrameMatrixList");
				Vector<Node> matrices = XMLUtility.findNodes(frameList, "Matrix");
				inBetweenMatrices = new Vector<TransformationMatrix>();
				for (Node node : matrices) inBetweenMatrices.add(new TransformationMatrix(XMLUtility, node));
			}
			catch (ParseException e) {
				// invalid IK pose - we don't have betweenFrameMatrixList or duration, and
				// for now we're not going to simulate the bones real-time, so no animation :(
				fIsTween = false;
			}
		}
		
		// get all instances
		Node elements = XMLUtility.findNode(root,  "elements");
		Vector<Node> instances = XMLUtility.getChildNodes(elements);
		for (Node node : instances) {
			
			// one instance will be made
			Instance instance;
			
			// if adding this fails, it means we found an invalid reference
			try {
			
				// it's a bitmap instance
				if (node.getNodeName().equals("DOMBitmapInstance")) {
					instance = new BitmapInstance(XMLUtility, library, node, fIndex);
					fBitmapInstancesMap.put(instance.getLibraryItemName(), (BitmapInstance)instance);
					fBitmapInstances.add((BitmapInstance)instance);
				}
				
				// it's a symbol instance
				else if (node.getNodeName().equals("DOMSymbolInstance")) {
					instance = new SymbolInstance(XMLUtility, library, node, fIndex);
					fSymbolInstancesMap.put(instance.getLibraryItemName(), (SymbolInstance)instance);
					fSymbolInstances.add((SymbolInstance)instance);
				}
				
				// unknown instance - skip
				else {
					continue;
				}
				
				// if there's in-between matrices, we add them to the instance
				if (inBetweenMatrices != null) {
					instance.setInBetweenMatrices(fIKTree.getTransformationMatrices(instance.getReferenceId()));
				}
			}
			
			catch (ParseException e) {
				// invalid reference found - ignore it
			}
		}
		
		
		// motion tween
		if (tweenType.equals("motion")) {
		
			// see if we got a custom ease
			boolean customEase = XMLUtility.getBooleanAttribute(root, "hasCustomEase", false);
			if (customEase) {
				
				// load the custom ease
				Node ease = XMLUtility.findNode(root, "CustomEase");
				
				// get all sub-points
				Vector<Node> points = XMLUtility.findNodes(ease, "Point");
				double[] x = new double[points.size()];
				double[] y = new double[points.size()];
				for (int i = 0; i < points.size(); ++i) {
					x[i] = XMLUtility.getDoubleAttribute(points.get(i), "x", 0);
					y[i] = XMLUtility.getDoubleAttribute(points.get(i), "y", 0);
				}
				fEaseCurve = new BezierCurve(x, y);
			}
			
			// no custom ease - use acceleration value to set up the bezier curve
			else {
				int acceleration = XMLUtility.getIntAttribute(root,  "acceleration", 0);
				fEaseCurve = new BezierCurve(acceleration);
			}
		}
	}
	
	
	// is there a tween here?
	public boolean isTween() {
		return fIsTween;
	}
	
	
	// frame index
	public int getIndex() {
		return fIndex;
	}
	
	
	// get all symbol instances
	public Collection<SymbolInstance> getSymbolInstances() {
		return fSymbolInstances;
	}
	
	
	// get all bitmap instances
	public Collection<BitmapInstance> getBitmapInstances() {
		return fBitmapInstances;
	}
	
	
	// get a symbol instance by library name
	public SymbolInstance getSymbolInstance(String libraryItemName) {
		return fSymbolInstancesMap.get(libraryItemName);
	}
	
	
	// get a symbol instance by library name
	public BitmapInstance getBitmapInstance(String libraryItemName) {
		return fBitmapInstancesMap.get(libraryItemName);
	}
	
	
	// compute ease
	public double computeEase(double t) {
		return (fEaseCurve != null ? fEaseCurve.interpolate(t) : t);
	}
	
	
	// get all images used for animation in this timeline
	Set<Bitmap> getUsedImages() {
		Set<Bitmap> v = new HashSet<Bitmap>();
		for (BitmapInstance bitmap : fBitmapInstances) v.add(bitmap.getBitmap());
		for (SymbolInstance symbol : fSymbolInstances) v.addAll(symbol.getGraphic().getTimeline().getUsedImages());
		return v;
	}
	
	
	// is this an IK tween?
	public boolean isIKTween() {
		return fIKTweenDuration > 0;
	}
	
	
	// get the duration
	public int getIKTweenDuration() {
		return fIKTweenDuration;
	}
}
