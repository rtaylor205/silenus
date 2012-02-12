package com.silenistudios.silenus.dat;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.silenistudios.silenus.ParseException;

/**
 * Will take care of handling .bin files that refer to jpeg's.
 * Jpeg's are strangely not compressed at all, so they are just copied.
 * @author Karel
 *
 */
public class DatJPEGReader implements DatReader {

	@Override
	public void parse(String inputFileName, String outputFileName)
			throws ParseException {
		
		// just perform a simple copy & paste
		try {
			Files.copy(FileSystems.getDefault().getPath(inputFileName), FileSystems.getDefault().getPath(outputFileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new ParseException(e.getMessage(), e);
		}
	}
}
