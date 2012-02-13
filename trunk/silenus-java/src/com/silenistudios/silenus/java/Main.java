package com.silenistudios.silenus.java;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.silenistudios.silenus.ParseException;
import com.silenistudios.silenus.XFLDocument;
import com.silenistudios.silenus.java.xml.JavaXMLUtility;

/**
 * This demo will take any XFL directory from the command line, and render it to screen.
 * @author Karel
 *
 */
public class Main {

	public static void main(String[] args) {
		
		// if no argument is provided, we show the default example
		String directoryName = "example";
		if (args.length > 0) directoryName = args[0];
		
		// parse an XFL document and render it to screen
		XFLDocument xfl = new XFLDocument(new JavaXMLUtility());
		try {
			
			// parse document
			System.out.println("Parsing document in directory '" + directoryName + "'");
			xfl.parseXFL(directoryName);
			
			// draw document
			System.out.println("Drawing document...");
			
			/**
			 * RawJavaRenderer will compute all the locations for the different bitmaps
			 * once at the start and then just draws the different bitmaps at the computed locations
			 * at real-time. This results in higher memory consumption, faster drawing.
			 * 
			 * Use the JavaRenderer for live animation and low memory footprint.
			 */
			openInJFrame(new RawJavaRenderer(xfl), xfl.getWidth(), xfl.getHeight(), "Silenus demo");
		}
		catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		// done
		System.out.println("Done!");
	}
	
	// open a simple JFrame to display the animation
	public static JFrame openInJFrame(Container content,
			int width,
			int height,
			String title) {
		JFrame frame = new JFrame(title);
		frame.setBackground(Color.white);
		content.setBackground(Color.white);
		frame.setSize(width, height + 50); // 50 for the header, which is included here
		frame.setContentPane(content);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		return(frame);
	}
}
