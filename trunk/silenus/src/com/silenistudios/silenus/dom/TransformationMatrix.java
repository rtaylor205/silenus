package com.silenistudios.silenus.dom;

import com.silenistudios.silenus.xml.Node;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * A transformation matrix is a 3x3 matrix containing scaling, shearing and translation components, like this:
 * [ a b tx ]
 * [ c d ty ]
 * [ 0 0 1  ]
 * This is the typical definition of an affine transformation.
 * @author Karel
 *
 */
public class TransformationMatrix {
	
	// the scale/rotation matrix (a,b,c,d)
	double[][] fMatrix = new double[2][2];
	
	// translation x value (tx)
	double fTranslateX;
	
	// translation y value (ty)
	double fTranslateY;
	
	
	// default transformation matrix that does nothing
	public TransformationMatrix() {
		fMatrix[0][0] = 1.0; fMatrix[0][1] = 0.0;
		fMatrix[1][0] = 0.0; fMatrix[1][1] = 1.0;
		fTranslateX = 0.0;
		fTranslateY = 0.0;
	}
	
	
	// create a transformation matrix from a node
	public TransformationMatrix(XMLUtility XMLUtility, Node matrix) throws ParseException {
		
		// load translate values if they are set
		setTranslateX(XMLUtility.getDoubleAttribute(matrix, "tx", 0.0));
		setTranslateY(XMLUtility.getDoubleAttribute(matrix, "ty", 0.0));
		
		// set matrix values
		setMatrixElement(0, 0, XMLUtility.getDoubleAttribute(matrix,  "a", 1.0));
		setMatrixElement(0, 1, XMLUtility.getDoubleAttribute(matrix,  "b", 0.0));
		setMatrixElement(1, 0, XMLUtility.getDoubleAttribute(matrix,  "c", 0.0));
		setMatrixElement(1, 1, XMLUtility.getDoubleAttribute(matrix,  "d", 1.0));
	}
	
	
	// create a transformation matrix
	public TransformationMatrix(double[][] matrix, double translateX, double translateY) {
		fMatrix = matrix;
		fTranslateX = translateX;
		fTranslateY = translateY;
	}
	
	
	// create a transformation matrix from scale, rotate and translate values
	public TransformationMatrix(double translateX, double translateY, double scaleX, double scaleY, double rotation) {
		fTranslateX = translateX;
		fTranslateY = translateY;
		fMatrix[0][0] = Math.cos(rotation) * scaleX;
		fMatrix[0][1] = -Math.sin(rotation) * scaleY;
		fMatrix[1][0] = Math.sin(rotation) * scaleX;
		fMatrix[1][1] = Math.cos(rotation) * scaleY;
	}
	
	
	// get matrix
	public double[][] getMatrix() {
		return fMatrix;
	}
	
	
	// get translate x
	public double getTranslateX() {
		return fTranslateX;
	}
	
	
	// get translate y
	public double getTranslateY() {
		return fTranslateY;
	}
	
	
	// get x scale value
	public double getScaleX() {
		return Math.sqrt(fMatrix[0][0]*fMatrix[0][0] + fMatrix[0][1]*fMatrix[0][1]);
	}
	
	
	// get y scale
	public double getScaleY() {
		return Math.sqrt(fMatrix[1][0]*fMatrix[1][0] + fMatrix[1][1]*fMatrix[1][1]);
	}
	
	
	// get rotation
	public double getRotation() {
		return Math.atan2(fMatrix[1][0], fMatrix[1][1]);
	}
	
	
	// set matrix element
	public void setMatrixElement(int x, int y, double value) {
		fMatrix[x][y] = value;
	}
	
	
	// set translate x
	public void setTranslateX(double tx) {
		fTranslateX = tx;
	}
	
	
	// set translate y
	public void setTranslateY(double ty) {
		fTranslateY = ty;
	}
	
	
	// add two matrices
	public static TransformationMatrix compose(TransformationMatrix A, TransformationMatrix B) {
		TransformationMatrix C = new TransformationMatrix();
		C.fMatrix[0][0] = B.fMatrix[0][0] * A.fMatrix[0][0] + B.fMatrix[0][1] * A.fMatrix[1][0];
		C.fMatrix[0][1] = B.fMatrix[0][0] * A.fMatrix[0][1] + B.fMatrix[0][1] * A.fMatrix[1][1];
		C.fMatrix[1][0] = B.fMatrix[1][0] * A.fMatrix[0][0] + B.fMatrix[1][1] * A.fMatrix[1][0];
		C.fMatrix[1][1] = B.fMatrix[1][0] * A.fMatrix[0][1] + B.fMatrix[1][1] * A.fMatrix[1][1];
		C.fTranslateX = A.fTranslateX + B.fTranslateX;
		C.fTranslateY = A.fTranslateY + B.fTranslateY;
		return C;
	}
	
	
	// to string for convenience
	@Override
	public String toString() {
		return "[" + fMatrix[0][0] + " " + fMatrix[0][1] + " ; " + fMatrix[1][0] + " " + fMatrix[1][1] + "] + [" + fTranslateX + " " + fTranslateY + "]";
	}
}
