package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * writes landmark data to ascii file
 */
public class LandmarkAsciiOutput extends LandmarkOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Landmark _landmark = null;
	private ResizableStringArray _landmarkNamesRSA;

	public LandmarkAsciiOutput(ResizableStringArray landmarkNamesRSA, Landmark landmark) {
		_landmarkNamesRSA = landmarkNamesRSA;
		_landmark = landmark;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			_aOutFile = new FileWriter(_directory + _filename + "." + ASCII_TYPE);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory, Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * write ascii landmark data file
	 */
	protected boolean write() {
		boolean success = false;
		CsdpFileMetadata lMeta = CsdpFunctions.getLandmarkMetadata();

		try {
			// write metadata
			String nl = Integer.toString(_landmark.getNumLandmarks());
			if(lMeta!=null) {
				_asciiOut.write(";HorizontalDatum:  " + lMeta.getHDatumString());
				_asciiOut.newLine();
				_asciiOut.write(";HorizontalZone:   " + lMeta.getHZone());
				_asciiOut.newLine();
				_asciiOut.write(";HorizontalUnits:  " + lMeta.getHUnitsString());
				_asciiOut.newLine();
				_asciiOut.write(";VerticalDatum:    " + lMeta.getVDatumString());
				_asciiOut.newLine();
				_asciiOut.write(";VerticalUnits:    " + lMeta.getVUnitsString());
				_asciiOut.newLine();
				_asciiOut.write(";Filetype:          landmark");
				_asciiOut.newLine();
				_asciiOut.write(";NumElements:      " + nl);
				_asciiOut.newLine();
			}
			// write data
			String versionLine = null;
			Integer numLines = null;
			for(int i=0; i<_landmarkNamesRSA.getSize(); i++) {
				String name = _landmarkNamesRSA.get(i);
				if(name!=null && name.length()>0) {
					double x = _landmark.getXMeters(name);
					double y = _landmark.getYMeters(name);
					String line = x + "," + y + "," + name;
					_asciiOut.write(line + "\n");
				}
			} // for i
			_landmark.setIsUpdated(false);
			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
		return success;
	}// write

	/**
	 * Close ascii bathymetry data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _directory + _filename + "." + _filetype + ":"
					+ e.getMessage());
		} // catch
	}// close

}// class LandmarkAsciiOutput
