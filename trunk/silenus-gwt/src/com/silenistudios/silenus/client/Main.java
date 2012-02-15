package com.silenistudios.silenus.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.silenistudios.silenus.client.form.Form;
import com.silenistudios.silenus.client.form.FormFileSelect;
import com.silenistudios.silenus.shared.AnimationDTO;
import com.silenistudios.silenus.shared.BitmapDTO;
import com.silenistudios.silenus.shared.FrameDTO;
import com.silenistudios.silenus.shared.ParseException;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint, MainCallback {
	
	// server
	SilenusServerAsync fServer = GWT.create(SilenusServer.class);
	
	// status label
	Label fStatus = new Label();
	
	// animation
	AnimationDTO fAnimation;
	
	// images
	ImageElement[] fImages;
	
	// number of images left to load
	int fImagesLeft;
	
	// frame
	int fFrame = 0;
	
	// canvas
	Canvas fCanvas;
	
	// panel
	FlowPanel fPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		// create a simple form
		FlowPanel panel = new FlowPanel();
		
		// title
		Label title = new Label("Silenus");
		title.setStyleName("title");
		panel.add(title);
		
		TextBlock description = new TextBlock();
		description.add("Starting with Adobe Flash CS5, flash animations are saved in a new data format that can be easily read and interpreted, called the XFL format. Even if you save as FLA in CS5, your file will simply be a zipped XFL directory.").br();
		description.add("Silenus", "http://code.google.com/p/silenus", "_blank").add(" is a open source java library that can read these FLA files or XFL directories and allow you to animate them without the use of Flash of Flash player.").br();
		description.add("Because Silenus is a java library, it can be hosted in a servlet environment. This website provides a web service for accessing Silenus, and will read and send back any CS5 .FLA file you toss its way.").br();
		description.add("In addition to this, you can also export the resulting data as json, allowing you to export this raw data to any language or project and use your Flash animation in any environment without needing Flash.").br().br();
		panel.add(description);
		
		info("Please select a file below to upload it to the server (max size: 2MB)");
		panel.add(fStatus);
		final Form form = new Form();
		FormFileSelect uploader = new FormFileSelect(this, false, new FormFileSelect.Handler() {
			
			@Override
			public void onFileSelected(String fileName) {
				info("Reading " + fileName + " from disk...");
			}
			@Override
			public void onFileStarted(String fileName) {
				info("Uploading " + fileName + "...");
			}
			@Override
			public void onFileLoaded(final AnimationDTO data) {
				animate(data);
			}
			
		});
		form.addFormField("FLA file:", "fla", uploader);
		panel.add(form);
		fPanel = panel;
		RootPanel.get("main").add(panel);
		
		// info panel
		TextBlock credits = new TextBlock();
		credits.br().add("Silenus was developed by ").add("Karel Crombecq", "mailto:Karel.Crombecq@gmail.com").add(" from ").add("Sileni Studios", "http://www.silenistudios.com", "_blank").add(" as part of the development of Castle Quest, a graphical browser-based strategy game that is set to be released Q4 2012.");
		RootPanel.get("main").add(credits);
		
	}
	
	
	// error
	public void error(String s) {
		fStatus.setStyleName("error");
		fStatus.setText(s);
	}
	
	
	// info
	public void info(String s) {
		fStatus.setStyleName("info");
		fStatus.setText(s);
	}
	
	
	// animate
	private void animate(AnimationDTO animation) {
		
		// set animation
		fAnimation = animation;
		
		// add JSON link
		final TextBlock block = new TextBlock();
		block.add("Download JSON", "/silenus_gwt/silenus_gwt/silenusImages?fileHash=" + fAnimation.fileHash, "_blank");
		fPanel.add(block);
		
		// prepare images
		loadImages();
	}
	

	// load all images
	private void loadImages() {
		info("Downloading images...");
		
		// create an array of image elements
		fImages = new ImageElement[fAnimation.images.length];
		
		// walk over all images and load'em
		fImagesLeft = fImages.length;
		for (int i = 0; i < fAnimation.images.length; ++i) {
			final int idx = i;
			final String fileName = fAnimation.images[i];
			String filePath = "/silenus_gwt/silenus_gwt/silenusImages?fileHash=" + fAnimation.fileHash + "&fileName=" + fileName;
			
			// load the image using the async image loader
			ImageLoader.loadImageAsync(filePath, new ResourceCallback<ImageElementResource>() {

				@Override
				public void onError(ResourceException e) {
					error("Failed to load image '" + fileName + '"');
				}

				@Override
				public void onSuccess(ImageElementResource resource) {
					fImages[idx] = resource.getImage();
					--fImagesLeft;
					if (fImagesLeft == 0) startDraw();
				}
				
			});
		}
	}
	
	
	// start drawing the canvas
	private void startDraw() {
		
		info("Done!");
		
		// set up a canvas
		fCanvas = Canvas.createIfSupported();
		
		// no canvas supported
		if (fCanvas == null) {
			error("Cannot render animation: HTML5 canvas is not supported by this browser!");
			return;
		}
		
		fFrame = 0;
		fCanvas.addStyleName("canvas-active");
		fCanvas.setWidth(fAnimation.width + "px");
		fCanvas.setHeight(fAnimation.height + "px");
		fCanvas.setCoordinateSpaceWidth(fAnimation.width);
		fCanvas.setCoordinateSpaceHeight(fAnimation.height);
		RootPanel.get("canvas").add(fCanvas);
		
		// set up a draw timer
		Timer timer = new Timer() {
			@Override
			public void run() {
				draw(fFrame++);
			}
			
		};
		timer.scheduleRepeating(1000 / fAnimation.frameRate);
	}
	
	
	// draw a frame
	private void draw(int frameIndex) {
		
		// loop
		frameIndex = frameIndex % fAnimation.frames.length;
		
		// get the context
		Context2d ctx = fCanvas.getContext2d();
		ctx.clearRect(0,  0, fAnimation.width, fAnimation.height);
		
		// go over all images in the frame and render them
		FrameDTO frame = fAnimation.frames[frameIndex];
		for (int i = 0; i < frame.bitmaps.length; ++i) {
			BitmapDTO bitmap = frame.bitmaps[i];
			
			// save state
			ctx.save();
			
			// apply the transformation
			ctx.translate(bitmap.transformation.translateX, bitmap.transformation.translateY);
			ctx.scale(bitmap.transformation.scaleX, bitmap.transformation.scaleY);
			ctx.rotate(bitmap.transformation.rotation);
			
			// draw the image
			ctx.drawImage(fImages[bitmap.imageIndex], 0.0, 0.0);
			
			// done
			ctx.restore();
		}
	}
}
