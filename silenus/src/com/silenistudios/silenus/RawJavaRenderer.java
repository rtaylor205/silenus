package com.silenistudios.silenus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.silenistudios.silenus.RawDataRenderer;
import com.silenistudios.silenus.XFLDocument;
import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.dom.Timeline;
import com.silenistudios.silenus.raw.AnimationBitmapData;
import com.silenistudios.silenus.raw.AnimationData;
import com.silenistudios.silenus.raw.ColorManipulation;
import com.silenistudios.silenus.raw.TransformationMatrix;

public class RawJavaRenderer extends JPanel {
	
	// default serial number
	private static final long serialVersionUID = 1L;
	
	// animation data
	AnimationData fAnimation;
	
	// images
	Map<String, BufferedImage> fImages = new HashMap<String, BufferedImage>();
	
	// current frame
	int fFrame = 0;
	
	// surface
	Graphics2D fSurface;
	
	
	// constructor
	public RawJavaRenderer(XFLDocument doc) {
		
		// get the scene
		final Timeline scene = doc.getScene();
		
		// pre-compute all the transformations once using the raw data renderer
		RawDataRenderer renderer = new RawDataRenderer(scene, doc.getWidth(), doc.getHeight(), doc.getFrameRate());
		fAnimation = renderer.getAnimationData();
		
		// load all images
		Vector<Bitmap> bitmaps = fAnimation.getBitmaps();
		for (Bitmap bitmap : bitmaps) {
			BufferedImage img;
			try {
				img = ImageIO.read(new File(bitmap.getAbsolutePath()));
				fImages.put(bitmap.getAbsolutePath(), img);
			} catch (IOException e) {
				System.out.println("Failed to load file: " + bitmap);
			}
		}
		
		// get frame rate
		int frameRate = doc.getFrameRate();
		
		// launch a timer to draw the animation
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				repaint();
				fFrame = (fFrame + 1) % scene.getAnimationLength();
			}
			
		}, 0, 1000 / frameRate);
	}
	
	
	// draw
	@Override
	public void paintComponent(Graphics g) {
		clear(g);
		fSurface = (Graphics2D)g;
		
		// get the current frame
		Vector<AnimationBitmapData> bitmaps = fAnimation.getFrameData(fFrame);
		for (AnimationBitmapData bitmap : bitmaps) {
			
			// perform the correct transformation
			TransformationMatrix m = bitmap.getTransformationMatrix();
			translate(m.getTranslateX(), m.getTranslateY());
			scale(m.getScaleX(), m.getScaleY());
			rotate(m.getRotation());
			
			// if there is color manipultion, we do it
			if (bitmap.hasColorManipulation()) {
				drawImage(bitmap.getBitmap(), bitmap.getColorManipulation());
			}
			else {
				drawImage(bitmap.getBitmap());
			}
			
			// invert back to original case
			rotate(-m.getRotation());
			scale(1.0 / m.getScaleX(), 1.0 / m.getScaleY());
			translate(-m.getTranslateX(), -m.getTranslateY());
		}
		
	}

	// super.paintComponent clears offscreen pixmap,
	// since we're using double buffering by default.

	protected void clear(Graphics g) {
		super.paintComponent(g);
	}
	
	
	public void scale(double x, double y) {
		fSurface.scale(x,  y);
	}
	
	
	public void translate(double x, double y) {
		fSurface.translate(x,  y);
	}
	
	
	public void rotate(double theta) {
		fSurface.rotate(theta);
	}
	
	
	public void drawImage(Bitmap bitmap) {
		fSurface.drawImage(fImages.get(bitmap.getAbsolutePath()), new AffineTransform(1f,0f,0f,1f,0,0), null);
	}
	
	
	public void drawImage(Bitmap bitmap, ColorManipulation c) {
		
		// get the original image
		BufferedImage img = fImages.get(bitmap.getAbsolutePath());
		
		// set up the rescale operation
		RescaleOp rescaleOp = new RescaleOp(new float[]{
				(float) c.getRedMultiplier(),
				(float) c.getGreenMultiplier(),
				(float) c.getBlueMultiplier(),
				(float) c.getAlphaMultiplier()},	new float[]{
				(float) c.getRedOffset(),
				(float) c.getGreenOffset(), 
				(float) c.getBlueOffset(), 
				(float) c.getAlphaOffset()}, null);
		
		// copy to a buffered image
		BufferedImage out = rescaleOp.createCompatibleDestImage(img, null);
		
		// apply filter
		rescaleOp.filter(img, out);
		
		// draw
		fSurface.drawImage(out, new AffineTransform(1f,0f,0f,1f,0,0), null);
	}
}
