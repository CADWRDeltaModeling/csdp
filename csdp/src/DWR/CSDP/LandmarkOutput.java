package DWR.CSDP;

import java.io.File;

/**
 * Write ascii landmark data
 *
 * @author
 * @version $Id: LandmarkOutput.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp $
 */
public abstract class LandmarkOutput {

	public static final boolean DEBUG = false;

	/**
	 * Make instance of subclass of LandmarkOutput
	 */
	public static LandmarkOutput getInstance(String directory, String filename, String filetype, Landmark data) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		LandmarkOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			ResizableStringArray landmarkNamesRSA = data.getSortedLandmarkNameRSA();
			output = new LandmarkAsciiOutput(landmarkNamesRSA, data);
		} else {// throw new IllegalInputFileException(msg);
			System.out.println("Error in LandmarkOutput:  filetype " + _filetype + " not recognized");
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write landmark data
	 */
	public boolean writeData() {
		boolean success = false;
		boolean backupSuccess = CsdpFunctions.backupFile(_directory, _filename, _filetype);
		if (backupSuccess) {
			open();
			success = write();
			close();
		}
		return success;
	}// writeData

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write file
	 */
	protected abstract boolean write();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * Convert x and y values from meters(UTM) to feet
	 */
	protected float feetToMeters(float value) {
		final float FEET_TO_METERS = 1 / 3.28084f;
		return FEET_TO_METERS * value;
	}

	protected static final String ASCII_TYPE = "cdl";
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static String _directory = null;
}// class LandmarkOutput
