package com.silenistudios.silenus.shared;

import java.io.Serializable;

/**
 * Transformation DTO.
 * @author Karel
 *
 */
public class TransformationDTO implements Serializable {
	
	// translate
	public double translateX;
	public double translateY;
	
	// scale
	public double scaleX;
	public double scaleY;
	
	// rotate
	public double rotation;
}
