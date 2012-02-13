package com.silenistudios.silenus.raw;

import java.util.Vector;

import com.silenistudios.silenus.RenderInterface;
import com.silenistudios.silenus.SceneRenderer;
import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.dom.ColorManipulation;
import com.silenistudios.silenus.dom.Timeline;
import com.silenistudios.silenus.dom.TransformationMatrix;

/**
 * This class will not render the animation to any video output, but will instead
 * "render" the resulting locations of each object at each frame to a data structure
 * that can be saved later for easy reconstruction of the animation.
 * This allows you to send the original .png images along with this data to completely
 * reproduce the animation without having to compute it real-time.
 * @author Karel
 *
 */
public class RawDataRenderer implements RenderInterface {
	
	// the scene
	Timeline fScene;
	
	// animation data
	AnimationData fData;
	
	// cuirrent frame data
	AnimationFrameData fFrame;
	
	// trandformation matrix stack
	Vector<TransformationMatrix> fTransformationStack = new Vector<TransformationMatrix>();
	
	// current transformation matrix
	TransformationMatrix fTransformationMatrix = new TransformationMatrix();
	
	// last animation bitmap data
	AnimationBitmapData fBitmapData;
	
	
	// render all data for a scene
	public RawDataRenderer(Timeline scene) {
		
		// create animation data
		fData = new AnimationData(scene.getAnimationLength());
		
		// set bitmaps used
		fData.setBitmaps(scene.getUsedImages());
		
		// create scene renderer
		SceneRenderer renderer = new SceneRenderer(scene, this);
		
		// go over all frames and render them
		for (int i = 0; i < scene.getAnimationLength(); ++i) {
			fFrame = new AnimationFrameData();
			renderer.render(i);
			fData.setFrame(i, fFrame);
		}
	}
	
	
	// get animation data
	public AnimationData getAnimationData() {
		return fData;
	}

	
	@Override
	public void save() {
		fTransformationStack.add(new TransformationMatrix(fTransformationMatrix.getMatrix(), fTransformationMatrix.getTranslateX(), fTransformationMatrix.getTranslateY()));
	}

	@Override
	public void restore() {
		fTransformationMatrix = fTransformationStack.lastElement();
		fTransformationStack.remove(fTransformationStack.size()-1);
	}

	@Override
	public void scale(double x, double y) {
		fTransformationMatrix = TransformationMatrix.compose(fTransformationMatrix, new TransformationMatrix(0.0, 0.0, x, y, 0.0));
	}

	@Override
	public void translate(double x, double y) {
		fTransformationMatrix = TransformationMatrix.compose(fTransformationMatrix, new TransformationMatrix(x, y, 1.0, 1.0, 0.0));
	}

	@Override
	public void rotate(double theta) {
		fTransformationMatrix = TransformationMatrix.compose(fTransformationMatrix, new TransformationMatrix(0.0, 0.0, 1.0, 1.0, theta));
	}

	@Override
	public void drawImage(Bitmap img) {
		fBitmapData = new AnimationBitmapData(img, fTransformationMatrix);
		fFrame.addBitmapData(fBitmapData);
	}
	
	@Override
	public void drawImage(Bitmap img, ColorManipulation colorManipulation) {
		drawImage(img);
		fBitmapData.setColorManipulation(colorManipulation);
	}

}
