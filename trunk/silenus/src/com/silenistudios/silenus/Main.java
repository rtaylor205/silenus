package com.silenistudios.silenus;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

import com.silenistudios.silenus.xml.java.JavaXMLUtility;

public class Main {

	public static void main(String[] args) {
		
		// parse an XFL document and render it to screen
		XFLDocument xfl = new XFLDocument(new JavaXMLUtility());
		try {
			
			// parse document
			System.out.println("Parsing document...");
			xfl.parseXFL("D:/cq3/images/parser/cs5.5");
			
			// draw document
			System.out.println("Drawing document...");
			openInJFrame(new JavaRenderer(xfl), xfl.getWidth(), xfl.getHeight(), "Silenus demo");
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
