package com.silenistudios.silenus.shared;

import java.io.Serializable;

/**
 * GWT transferable animation data.
 * @author Karel
 *
 */
public class AnimationDTO implements Serializable {
	
	// width
	public int width;
	
	// height
	public int height;
	
	// FPS
	public int frameRate;
	
	// array of paths to the images
	public String[] images;
	
	// array of frames
	public FrameDTO[] frames;
	
	// file hash
	public String fileHash;
	
	// JSON
	public String json;

}
