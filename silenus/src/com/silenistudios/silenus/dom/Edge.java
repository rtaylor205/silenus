package com.silenistudios.silenus.dom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.xml.Node;
import com.silenistudios.silenus.xml.XMLUtility;

/**
 * An edge is a set of coordinates connected by a stroke and filled with a fill style.
 * @author Karel
 *
 */
public class Edge {
	
	// list of lines in this "edge"
	Line[] fLines;
	
	// fill style
	int fFillStyleIndex;
	
	// stroke style
	int fStrokeStyleIndex;
	
	// the pattern for parsing a line
	// TODO what does the "[" that sometimes occurs instead of "|" mean?
	// TODO sometimes letters come behind the numbers, such as "!895 -3557S1|134 -3366!134 -3366|135 -2925". What does it mean?
	// TODO another weird construct appearing in the edges list: "!10214.5 2608.5[#27EC.6F #A5D.06 10226.5 2697.5"
	private static Pattern LinePattern = Pattern.compile("([-]?[0-9]*[\\.]?[0-9]+) ([-]?[0-9]*[\\.]?[0-9]+)[\\|\\[]{1}([-]?[0-9]*[\\.]?[0-9]+) ([-]?[0-9]*[\\.]?[0-9]+).*");
	
	
	// constructor
	public Edge(XMLUtility XMLUtility, Node root) throws ParseException {
		
		// get stroke style
		fStrokeStyleIndex = XMLUtility.getIntAttribute(root, "strokeStyle", -1);
		
		// get fill style
		// when there are multiple fill styles (this happens when two sides of the edge have different fill colors), give priority to fillStyle0
		fFillStyleIndex = XMLUtility.getIntAttribute(root,  "fillStyle1", -1);
		if (XMLUtility.hasAttribute(root,  "fillStyle0")) fFillStyleIndex = XMLUtility.getIntAttribute(root, "fillStyle0");
		// get & parse lines
		String edgesString = XMLUtility.getAttribute(root, "edges");
		String[] lines = edgesString.split("!");
		fLines = new Line[lines.length-1];
		
		// edgeString starts with an !, so we skip the first split
		for (int i = 1; i < lines.length; ++i) {
			
			// get the two coordinates
			Matcher matcher = LinePattern.matcher(lines[i]);
			if (!matcher.matches() || matcher.groupCount() != 4) throw new ParseException("Invalid edges attribute found in DOMShape");
			
			// create the line - convert from int (in *20 size) to doubles in normal coordinates
			Line line = new Line(Double.parseDouble(matcher.group(1))/20.0, Double.parseDouble(matcher.group(2))/20.0, Double.parseDouble(matcher.group(3))/20.0, Double.parseDouble(matcher.group(4))/20.0);
			fLines[i-1] = line;
		}
	}
	
	
	
	// is there a stroke style?
	public boolean hasStrokeStyle() {
		return fStrokeStyleIndex != -1;
	}
	
	
	// get stroke style
	public int getStrokeStyleIndex() {
		return fStrokeStyleIndex;
	}
	
	
	// is there a fill style?
	public boolean hasFillStyle() {
		return fFillStyleIndex != -1;
	}
	
	
	// get fill style
	public int getFillStyleIndex() {
		return fFillStyleIndex;
	}
	
	
	// get the number of lines
	public int getNLines() {
		return fLines.length;
	}
	
	
	// get a line
	public Line getLine(int index) {
		return fLines[index];
	}
}
