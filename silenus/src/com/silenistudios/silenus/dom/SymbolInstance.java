package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.xml.Node;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.XFLLibrary;
import com.silenistudios.silenus.xml.XMLUtility;

public class SymbolInstance extends Instance {
	
	// loop type
	public enum LoopType {
		LOOP,
		PLAY_ONCE,
		SINGLE_FRAME
	};
	
	// the bitmap
	Graphic fGraphic;
	
	// loop
	LoopType fLoopType;
	
	// frame in case of SINGLE_FRAME loop type
	int fFirstFrame;
	

	// constructor
	public SymbolInstance(XMLUtility XMLUtility, XFLLibrary library, Node root, int frameIndex) throws ParseException {
		super(XMLUtility, root, frameIndex);
		
		// load the bitmap
		String libraryItemName = XMLUtility.getAttribute(root, "libraryItemName");
		
		// get loop
		String loop = XMLUtility.getAttribute(root, "loop", "loop");
		if (loop.equals("loop")) fLoopType = LoopType.LOOP;
		else if (loop.equals("play once")) fLoopType = LoopType.PLAY_ONCE;
		else {
			fLoopType = LoopType.SINGLE_FRAME;
			fFirstFrame = XMLUtility.getIntAttribute(root,  "firstFrame", 0);
		}
		
		// load the bitmap
		// this could be null if the graphic find fails
		fGraphic = library.getGraphic(libraryItemName);
	}
	
	
	// get graphic
	public Graphic getGraphic() {
		return fGraphic;
	}
	
	
	// get loop type
	public LoopType getLoopType() {
		return fLoopType;
	}
	
	
	// get frame
	public int getFirstFrame() {
		return fFirstFrame;
	}
	
	
	// get the correct frame to draw from, based on the loop type and the "real" frame
	public int getCorrectFrame(int frame) {
		
		// max frame index
		int maxFrameIndex = fGraphic.getTimeline().getMaxFrameIndex();
		
		// in play once mode, we don't draw anything when we're past the final frame
		if (fLoopType == SymbolInstance.LoopType.PLAY_ONCE) return frame;
		
		// in loop mode, we take the mod of the frame to get to the appropriate keyframe
		else if (fLoopType == SymbolInstance.LoopType.LOOP) return frame % (maxFrameIndex+1);
		
		// in single frame mode, we always return the same frame, no matter what situation we're in
		else return fFirstFrame;
	}
}
