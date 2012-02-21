package com.silenistudios.silenus;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.RenderingHints;
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

import com.silenistudios.silenus.RenderInterface;
import com.silenistudios.silenus.SceneRenderer;
import com.silenistudios.silenus.XFLDocument;
import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.dom.Color;
import com.silenistudios.silenus.dom.FillStyle;
import com.silenistudios.silenus.dom.Path;
import com.silenistudios.silenus.dom.Point;
import com.silenistudios.silenus.dom.StrokeStyle;
import com.silenistudios.silenus.dom.Timeline;
import com.silenistudios.silenus.raw.ColorManipulation;

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
	
	// general path
	GeneralPath fPath;
	
	
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
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				repaint();
				++fFrame;
			}
			
		}, 0, 1000 / frameRate);
	}
	
	
	// draw
	@Override
	public void paintComponent(Graphics g) {
		clear(g);
		fSurface = (Graphics2D)g;
		fSurface.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		fRenderer.render(fFrame % (fScene.getMaxFrameIndex()+1));
	}
	
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
		fSurface.translate((int)x,  (int)y);
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
	public void drawPath(Path path) {
		fPath = new GeneralPath();
		boolean first = true;
		for (Point p : path.getPoints()) {
			if (first) {
				fPath.moveTo(p.getX(), p.getY());
				first = false;
			}
			else fPath.lineTo(p.getX(), p.getY());
		}
	}


	@Override
	public void fill(FillStyle fillStyle) {
		Color color = fillStyle.getColor();
		Paint paint = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * 255));
		fSurface.setPaint(paint);
		fPath.closePath();
		fSurface.fill(fPath);
	}


	@Override
	public void stroke(StrokeStyle strokeStyle) {
		Color color = strokeStyle.getColor();
		Paint paint = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * 255));
		fSurface.setStroke(new BasicStroke((float)strokeStyle.getWeight()));
		fSurface.setPaint(paint);
		fSurface.draw(fPath);
	}

	@Override
	public void transform(double m00, double m01, double m10, double m11, double tx, double ty) {
		fSurface.transform(new AffineTransform(m00, m01, m10, m11, tx, ty));
		
	}
	
}
