package com.silenistudios.silenus.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MemoryOutputStream extends OutputStream {
	
	// datastore file
	MemoryFile fFile;
	
	// data buffer
	ByteArrayOutputStream fData = new ByteArrayOutputStream();
	
	// create a datastore object with this data
	public MemoryOutputStream(MemoryFile file) {
		fFile = file;
	}

	@Override
	public void write(int b) throws IOException {
		fData.write(b);
	}
	
	
	@Override
	public void flush() throws IOException {
		super.flush();
		fFile.update(fData.toByteArray());
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}
}
