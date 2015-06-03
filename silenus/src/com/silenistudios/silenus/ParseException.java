
package com.silenistudios.silenus;

public class ParseException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1199001350660181232L;
	
	public ParseException (String msg) {
		super(msg);
	}
	
	public ParseException (String msg, Throwable cause) {
		super(msg, cause);
	}
}
