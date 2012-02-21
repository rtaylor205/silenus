package com.silenistudios.silenus.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;


import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.silenistudios.silenus.DefaultStreamFactory;
import com.silenistudios.silenus.RawDataRenderer;
import com.silenistudios.silenus.StreamFactory;
import com.silenistudios.silenus.XFLDocument;
import com.silenistudios.silenus.client.SilenusServer;
import com.silenistudios.silenus.dom.Bitmap;
import com.silenistudios.silenus.memory.MemoryStreamFactory;
import com.silenistudios.silenus.raw.AnimationBitmapData;
import com.silenistudios.silenus.raw.AnimationData;
import com.silenistudios.silenus.raw.AnimationFrameData;
import com.silenistudios.silenus.shared.*;


/**
 * This server will download a .fla file, parse it and return the animation data.
 * @author Karel
 *
 */
public class SilenusServerImpl extends RemoteServiceServlet implements SilenusServer {
	
	// static connection with the image server
	static SilenusServerImpl instance = null;
	
	// map of stream factories (file systems) that are currently hosted
	Map<String, StreamFactory> fStreamFactories = new HashMap<String, StreamFactory>();
	
	// max file size (in KB)
	int fMaxFileSize = 0;
	
	// map of file hash to JSON
	Map<String, String> fJSON = new HashMap<String, String>();
	
	
	// constructor
	public SilenusServerImpl() {
		instance = this;
	}
	
	
	// does a factory exist?
	public boolean hasFactory(String fileHash) {
		return fStreamFactories.containsKey(fileHash);
	}
	
	
	// get a factory
	public StreamFactory getFactory(String fileHash) {
		return fStreamFactories.get(fileHash);
	}
	
	
	// get json
	public String getJSON(String fileHash) {
		return fJSON.get(fileHash);
	}
	
	
	// cleanup
	public void cleanup(String fileHash) {
		fStreamFactories.remove(fileHash);
		fJSON.remove(fileHash);
	}
	
	
	// get max file size
	public int getMaxFileSize() {
		if (fMaxFileSize == 0) fMaxFileSize = Integer.parseInt(getServletContext().getInitParameter("silenus.maxSize"));
		return fMaxFileSize;
	}
	
	
	// upload a file
	public AnimationDTO parseFLA(byte[] bytes) throws ParseException {
		
		// create the file
		try {
			
			// check file size
			if (fMaxFileSize == 0) fMaxFileSize = Integer.parseInt(getServletContext().getInitParameter("silenus.maxSize"));
			if (bytes.length > fMaxFileSize * 1024) {
				String limit = fMaxFileSize + "KB";
				if (fMaxFileSize / 1024 > 2) limit = (fMaxFileSize/1024) + "MB";
				throw new ParseException("Your file is too large! Max size allowed: " + limit + ".");
			}
			
			
			// generate a unique hash to save this file as
			UUID id = UUID.randomUUID();
			final String fileHash = id.toString();
			
			// create an output stream from an appropriate factory
			StreamFactory streamFactory = new MemoryStreamFactory();
			OutputStream out = streamFactory.createOutputStream(new File(fileHash + ".fla"));
			
			// write to the output stream
			out.write(bytes);
			out.flush();
			out.close();
			
			// the document with the alternative stream factory
			XFLDocument doc = new XFLDocument();
			doc.setStreamFactory(streamFactory);
			doc.parseXFL(fileHash + ".fla");
			
			// create a raw data renderer
			RawDataRenderer renderer = new RawDataRenderer(doc.getScene(), doc.getWidth(), doc.getHeight(), doc.getFrameRate());
			
			// success! we save the file system for later requests for a period of time
			fStreamFactories.put(fileHash, streamFactory);
			fJSON.put(fileHash, renderer.getAnimationData().getJSON());
			
			// make a timer to remove the stream factory after 15 minutes
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					fJSON.remove(fileHash);
					fStreamFactories.remove(fileHash);
				}
				
			}, 120000);
			
			// turn data into DTO and return it
			AnimationDTO dto = getDTO(renderer.getAnimationData());
			dto.fileHash = fileHash;
			return dto;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new ParseException("Failed to parse file. Maybe your FLA was not created with Adobe Flash CS5, or you didn't upload an FLA fila at all?");
		}
		catch (com.silenistudios.silenus.ParseException e) {
			e.printStackTrace();
			throw new ParseException("Failed to parse file. Maybe your FLA was not created with Adobe Flash CS5, or you didn't upload an FLA fila at all?");
		}
	}
	
	
	// generate the DTO from the animation data
	private AnimationDTO getDTO(AnimationData animation) {
		
		// create the animation DTO
		AnimationDTO dto = new AnimationDTO();
		int animationLength = animation.getAnimationLength();
		dto.frames = new FrameDTO[animationLength];
		dto.width = animation.getWidth();
		dto.height = animation.getHeight();
		dto.frameRate = animation.getFrameRate();
		dto.json = animation.getJSON();
		
		// set up the bitmaps
		Vector<Bitmap> bitmaps = animation.getBitmaps();
		Map<String, Integer> nameToId = new HashMap<String, Integer>();
		dto.images = new String[bitmaps.size()];
		for (int i = 0; i < bitmaps.size(); ++i) {
			dto.images[i] = bitmaps.get(i).getAbsolutePath();
			nameToId.put(dto.images[i], i);
		}
		
		// generate frames
		for (int i = 0; i < animationLength; ++i) {
			FrameDTO frame = new FrameDTO();
			Vector<AnimationBitmapData> bitmapData = animation.getFrameData(i).getBitmapData();
			frame.bitmaps = new BitmapDTO[bitmapData.size()];
			
			// generate the different bitmaps
			for (int j = 0; j < bitmapData.size(); ++j) {
				AnimationBitmapData bitmap = bitmapData.get(j);
				BitmapDTO bitmapDTO = new BitmapDTO();
				bitmapDTO.imageIndex = nameToId.get(bitmap.getBitmap().getAbsolutePath());
				bitmapDTO.transformation = new TransformationDTO();
				bitmapDTO.transformation.translateX = bitmap.getTransformationMatrix().getTranslateX();
				bitmapDTO.transformation.translateY = bitmap.getTransformationMatrix().getTranslateY();
				bitmapDTO.transformation.scaleX = bitmap.getTransformationMatrix().getScaleX();
				bitmapDTO.transformation.scaleY = bitmap.getTransformationMatrix().getScaleY();
				bitmapDTO.transformation.rotation = bitmap.getTransformationMatrix().getRotation();
				frame.bitmaps[j] = bitmapDTO;
			}
			
			// done
			dto.frames[i] = frame;
		}
		
		// all done
		return dto;
	}
}
