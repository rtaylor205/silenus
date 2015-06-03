
package com.silenistudios.silenus.dom;

import java.io.File;
import java.io.Serializable;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.StreamFactory;
import com.silenistudios.silenus.dat.DatJPEGReader;
import com.silenistudios.silenus.dat.DatPNGReader;
import com.silenistudios.silenus.dat.DatReader;
import com.silenistudios.silenus.xml.Node;
import com.silenistudios.silenus.xml.XMLUtility;

/** A bitmap is an imported bitmap that corresponds to a binary .dat file, and
 * optionally to the original image, but this image is not always included in
 * the XFL distribution. If not, it will be reconstructed from the binary data
 * and saved.
 * @author Karel */
public class Bitmap implements Serializable {
	
	// root directory of the project
	String fRoot;
	
	// the binary href
	private String fBinaryHref;
	
	// external file path
	private String fExternalFilePath;
	
	// height
	private int fHeight;
	
	// name of the bitmap
	private String fName;
	
	// the source file
	private String fSourceHref;
	
	// width
	private int fWidth;
	
	// read a bitmap from a node
	public Bitmap (XMLUtility XMLUtility, StreamFactory factory, String root, Node node) throws ParseException {
		
		// get name
		fName = XMLUtility.getAttribute(node, "name");
		
		// get external path
		// is not set when we're dealing with an extracted .fla file
		fExternalFilePath = XMLUtility.getAttribute(node, "sourceExternalFilepath", "");
		
		// get hrefs
		fSourceHref = XMLUtility.getAttribute(node, "href");
		fBinaryHref = XMLUtility.getAttribute(node, "bitmapDataHRef");
		
		// set root
		fRoot = root;
		
		// width and height in twips
		fWidth = XMLUtility.getIntAttribute(node, "frameRight") / 20;
		fHeight = XMLUtility.getIntAttribute(node, "frameBottom") / 20;
		
		// if the file already exists, don't re-write if
		String outputFileName = root + "/LIBRARY/" + fSourceHref;
		
		// see if the source file exists
		if (!factory.exists(new File(outputFileName))) {
			
			// file does not exist, try to convert to binary
			convertBinary(factory, outputFileName);
		}
	}
	
	// equals - implemented for use in a Set
	public boolean equals (Bitmap obj) {
		return fSourceHref.equals(obj.fSourceHref);
	}
	
	// get the absolute path to the bitmap - this one should be used for drawing
	public String getAbsolutePath () {
		return fRoot + "/LIBRARY/" + fSourceHref;
	}
	
	// get the binary href - this is the path to the internal binary representation used by flash
	public String getBinaryHref () {
		return fBinaryHref;
	}
	
	// get external file path - this is a relative path to the original image before it was imported
	public String getExternalFilePath () {
		return fExternalFilePath;
	}
	
	// get height
	public int getHeight () {
		return fHeight;
	}
	
	// get name
	public String getName () {
		return fName;
	}
	
	// get source href - this is the path to the original image
	public String getSourceHref () {
		return fSourceHref;
	}
	
	// get width
	public int getWidth () {
		return fWidth;
	}
	
	// convert binary png
	private void convertBinary (DatReader reader, String outputFileName) {
		
		// it doesn't exist, try to make it
		try {
			reader.parse(fRoot + "/bin/" + fBinaryHref, outputFileName);
		} catch (ParseException e) {
			// failed to convert - no biggy, we continue
			//System.out.println("Failed to convert .dat file '" + fBinaryHref + "': " + e.getMessage());
		}
	}
	
	// convert the binary data back - for now only png is supported
	private void convertBinary (StreamFactory factory, String outputFileName) {
		
		// get extension
		String extension = null;
		String[] rootSplit = fSourceHref.split("\\.");
		if (rootSplit.length > 0)
			extension = rootSplit[rootSplit.length - 1];
		
		// no extension found - no way to figure out how to convert
		if (extension == null)
			return;
		
		// convert png
		if (extension.equalsIgnoreCase("png")) {
			convertBinary(new DatPNGReader(factory), outputFileName);
		}
		
		// convert jpeg
		else if (extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg")) {
			convertBinary(new DatJPEGReader(factory), outputFileName);
		}
	}
}
