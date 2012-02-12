package com.silenistudios.silenus.dom;

import java.io.File;
import com.silenistudios.silenus.xml.Node;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.dat.*;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * A bitmap is an imported bitmap that corresponds to a binary .dat file, and optionally to the original image,
 * but this image is not always included in the XFL distribution. If not, it will be reconstructed from
 * the binary data and saved.
 * @author Karel
 *
 */
public class Bitmap {
	
	// name of the bitmap
	private String fName;
	
	// external file path
	private String fExternalFilePath;
	
	// the source file
	private String fSourceHref;
	
	// the binary href
	private String fBinaryHref;
	
	// root directory of the project
	String fRoot;
	
	
	// read a bitmap from a node
	public Bitmap(XMLUtility XMLUtility, String root, Node node) throws ParseException {
		
		// get name
		fName = XMLUtility.getAttribute(node, "name");
		
		// get external path
		fExternalFilePath = XMLUtility.getAttribute(node, "sourceExternalFilepath");
		
		// get hrefs
		fSourceHref = XMLUtility.getAttribute(node, "href");
		fBinaryHref = XMLUtility.getAttribute(node, "bitmapDataHRef");
		
		// set root
		fRoot = root;
		
		// see if the source file exists
		if (!new File(fSourceHref).exists()) {
			
			// file does not exist, try to convert to binary
			convertBinary(root);
		}
	}
	
	
	// get name
	public String getName() {
		return fName;
	}
	
	
	// get source href - this is the path to the original image
	public String getSourceHref() {
		return fSourceHref;
	}
	
	
	// get the binary href - this is the path to the internal binary representation used by flash
	public String getBinaryHref() {
		return fBinaryHref;
	}
	
	
	// get external file path - this is a relative path to the original image before it was imported
	public String getExternalFilePath() {
		return fExternalFilePath;
	}
	
	
	// get the absolute path to the bitmap - this one should be used for drawing
	public String getAbsolutePath() {
		return fRoot + "/LIBRARY/" + fSourceHref;
	}
	
	
	// convert the binary data back - for now only png is supported
	private void convertBinary(String root) {
		
		// get extension
		String extension = null;
		String[] rootSplit = fSourceHref.split("\\.");
		if (rootSplit.length > 0) extension = rootSplit[rootSplit.length-1];
		
		// no extension found - no way to figure out how to convert
		if (extension == null) return;
		
		// convert png
		if (extension.equalsIgnoreCase("png")) {
			convertBinary(new DatPNGReader(), root);
		}
		
		// convert jpeg
		else if (extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg")) {
			convertBinary(new DatJPEGReader(), root);
		}
	}
	
	
	// convert binary png
	private void convertBinary(DatReader reader, String root) {
		
		// if the file already exists, don't re-write if
		String outputFileName = root + "/LIBRARY/" + fSourceHref;
		if (new File(outputFileName).exists()) return;
		
		// it doesn't exist, try to make it
		try {
			reader.parse(root + "/bin/" + fBinaryHref, outputFileName);
		}
		catch (ParseException e) {
			// failed to convert - no biggy, we continue
			System.out.println("Failed to convert .dat file '" + fBinaryHref + "': " + e.getMessage());
		}
	}
	
	
	// equals - implemented for use in a Set
	public boolean equals(Bitmap obj) {
		return fSourceHref.equals(obj.fSourceHref);
	}
}
