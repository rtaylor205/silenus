package com.silenistudios.silenus.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.silenistudios.silenus.StreamFactory;

public class ImageServlet extends HttpServlet {
	

	/**
	 * Method to receive get requests from the web server
	 * (Passes them onto the doPost method)
	 * @param req The HttpServletRequest which contains the
	 *   information submitted via get
	 * @param res A response containing the required response
	 *  data for this request
	 **/

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req,res);
	}

	/**
	 * Method to receive and process Post requests from the web server
	 * @param req The HttpServletRequest which contains the
	 *   information submitted via post
	 * @param res A response containing the required response data
	 *   for this request
	 **/

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// file hash and image name
		String fileHash = null;
		String fileName = null;
		
		// get the file hash and image name from the request
		String queryString = URLDecoder.decode(req.getQueryString(), "UTF-8");
		String[] parameters = queryString.split("&");
		for (int i = 0; i < parameters.length; ++i) {
			String[] split = parameters[i].split("=");
			if (split.length == 2) {
				if (split[0].equals("fileHash")) fileHash = split[1];
				if (split[0].equals("fileName")) fileName = split[1];
			}
		}
		
		// invalid arguments
		if (fileHash == null) return;
		
		// try to get an input stream from the main servlet
		SilenusServerImpl silenus = SilenusServerImpl.instance;
		if (silenus == null) return;
		
		// see if the factory exists
		if (!silenus.hasFactory(fileHash)) return;
		
		// no filename - we get the JSON
		if (fileName == null) {
			
			// get the json
			String json = silenus.getJSON(fileHash);
			res.setContentType("application/json");
			PrintWriter w = new PrintWriter(res.getOutputStream());
			w.write(json);
			w.flush();
		}
		
		// there is a filename - get the image
		else {
			
			// get the factory
			StreamFactory factory = silenus.getFactory(fileHash);
			
			// open a stream with the file
			InputStream in = factory.createInputStream(new File(fileName));
			
			// copy to the output stream
			res.setContentType("image/png");
			OutputStream out = res.getOutputStream();
			int bufferSize = res.getBufferSize();
			byte[] buffer = new byte[bufferSize];
			int count;
			while ((count = in.read(buffer, 0, bufferSize)) != -1) {
				out.write(buffer, 0, bufferSize);
			}
		}
	}
}
