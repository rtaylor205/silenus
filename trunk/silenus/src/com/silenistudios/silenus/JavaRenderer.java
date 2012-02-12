package com.silenistudios.silenus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.dom.ColorManipulation;
import com.silenistudios.silenus.dom.Timeline;

/**
 * Example renderer for Java. Implements the RenderInterface, allowing it to render
 * any scene sent to it.
 * @author Karel
 *
 */
public class JavaRenderer extends JPanel implements RenderInterface {
	
	// default serial number
	private static final long serialVersionUID = 1L;

	// the renderer
	SceneRenderer fRenderer;
	
	// the drawing context
	Graphics2D fSurface;
	
	// stack of transformations
	Stack<AffineTransform> fTransformStack = new Stack<AffineTransform>();
	
	// stack of composite operations, for restoration on restore()
	Stack<Composite> fCompositeStack = new Stack<Composite>();
	
	// current frame
	int fFrame = 0;
	
	// images
	Map<String, BufferedImage> fImages = new HashMap<String, BufferedImage>();
	
	// the scene
	Timeline fScene;
	
	
	// constructor
	public JavaRenderer(XFLDocument doc) {
		
		// get the scene
		fScene = doc.getScene();
		
		// set the renderer
		fRenderer = new SceneRenderer(fScene, this);
		
		// load all images
		Set<Bitmap> bitmaps = fScene.getUsedImages();
		for (Bitmap bitmap : bitmaps) {
			BufferedImage img;
			try {
				img = ImageIO.read(new File(bitmap.getAbsolutePath()));
				fImages.put(bitmap.getAbsolutePath(), img);
			} catch (IOException e) {
				System.out.println("Failed to load file: " + bitmap.getAbsolutePath());
			}
		}

		// get frame rate
		int frameRate = doc.getFrameRate();
		
		// launch a timer to draw the animation
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				++fFrame;
				repaint();
			}
			
		}, 0, 1000 / frameRate);
	}
	
	
	// draw
	@Override
	public void paintComponent(Graphics g) {
		clear(g);
		fSurface = (Graphics2D)g;
		fRenderer.render(fFrame % (fScene.getMaxFrameIndex()+1));
		//fRenderer.render(0);
	}

	// super.paintComponent clears offscreen pixmap,
	// since we're using double buffering by default.

	protected void clear(Graphics g) {
		super.paintComponent(g);
	}


	@Override
	public void save() {
		fTransformStack.push(fSurface.getTransform());
		fCompositeStack.push(fSurface.getComposite());
	}


	@Override
	public void restore() {
		fSurface.setTransform(fTransformStack.pop());
		fSurface.setComposite(fCompositeStack.pop());
	}


	@Override
	public void scale(double x, double y) {
		fSurface.scale(x,  y);
	}


	@Override
	public void translate(double x, double y) {
		fSurface.translate(x,  y);
	}


	@Override
	public void rotate(double theta) {
		fSurface.rotate(theta);
	}


	@Override
	public void drawImage(Bitmap bitmap) {
		fSurface.drawImage(fImages.get(bitmap.getAbsolutePath()), new AffineTransform(1f,0f,0f,1f,0,0), null);
	}
	
	
	@Override
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
	
	
	@Override
	public void drawRectangle(double x1, double y1, double x2, double y2) {
		fSurface.fillRect((int)x1, (int)y1, (int)x2, (int)y2);
	}
}
