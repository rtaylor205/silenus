
package com.silenistudios.silenus.memory;

/** A virtual file in memory. Just keeps track of a byte array.
 * @author Karel */
public class MemoryFile {
	
	// path
	String fPath;
	
	// data array
	private byte[] fData;
	
	// set?
	private boolean fSet = false;
	
	// create a new memory file
	public MemoryFile (String path) {
		fPath = path;
	}
	
	// data
	public byte[] getData () {
		return fData;
	}
	
	// get path
	public String getPath () {
		return fPath;
	}
	
	// set?
	public boolean isSet () {
		return fSet;
	}
	
	// update
	public void update (byte[] data) {
		fData = data;
		fSet = true;
	}
}
