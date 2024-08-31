package DWR.CSDP;

import java.io.File;

/**
 * Write ascii and binary open water area data
 *
 * @author
 * @version $Id:
 */
public abstract class RectXSOutput {

	public static final boolean DEBUG = false;

	/**
	 * Make instance of subclass of OpenWaterAreaOutput
	 */
	public static RectXSOutput getInstance(String directory, String filename, Network net, CsdpFrame gui) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		parseFilename(filename);
		// _filename = filename;
		// _filetype = filetype;
		RectXSOutput output = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		output = new RectXSAsciiOutput(net, gui);
		// }
		// else {// throw new IllegalInputFileException(msg);
		// System.out.println();
		// _filetype = null;
		// }
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write openWaterArea data
	 */
	public boolean writeData(DSMChannels dsmChannels, IrregularXsectsInp ixi, XsectsInp xi) {
		boolean success = false;
		open();
		success = write(dsmChannels, ixi, xi);
		close();
		return success;
	}

	/**
	 * separates filename into prefix and extension
	 */
	protected static void parseFilename(String filename) throws NullPointerException {
		try {
			int dotIndex = filename.indexOf(".", 0);
			if (dotIndex >= 0) {
				_filename = filename.substring(0, dotIndex);
				_filetype = filename.substring(dotIndex + 1);
			} else if (dotIndex < 0) {
				_filename = null;
				_filetype = null;
			}
		} catch (Exception e) {
			System.out.println("no filename specified");
		} // catch
	}// parseFilename

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write file
	 */
	protected abstract boolean write(DSMChannels dsmChannels, IrregularXsectsInp ixi, XsectsInp xi);

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

	protected static final String ASCII_TYPE = "txt";
	// protected static final String BINARY_TYPE = "cdp";
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static String _directory = null;

}// class OpenWaterAreaOutput
