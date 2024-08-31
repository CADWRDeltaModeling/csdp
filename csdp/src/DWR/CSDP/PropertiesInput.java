package DWR.CSDP;

import java.awt.Color;
import java.io.File;

/**
 * Read ascii and binary properties data
 *
 * @author
 * @version $Id:
 */
public abstract class PropertiesInput {

	/**
	 * Make instance of subclass of PropertiesInput
	 */
	public static PropertiesInput getInstance(CsdpFrame gui, String directory, String filename) {
		_gui = gui;
		_directory = directory;

		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		PropertiesInput input = null;

		if (filename.endsWith(ASCII_TYPE)) {
			input = new PropertiesAsciiInput();
			_filetype = ASCII_TYPE;
		}
		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// PropertiesInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new PropertiesAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println();
			_filetype = null;
		}

		if (_filetype != null) {
			_filename = filename.substring(0, filename.lastIndexOf(_filetype) - 1);
		}
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read properties data
	 */
	public void readData() {
		open();
		read();
		close();
	}

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * Read file
	 */
	protected abstract void read();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * Stores one line of parsed data in adjustable arrays. Converts x&y to
	 * feet.
	 */
	protected void storeData(int index, int[] data) {
		// CsdpFunctions.setColor(index, new Color(data[0],data[1],data[2]));
		_gui.setColor(index, new Color(data[0], data[1], data[2]));

		if (DEBUG)
			System.out.println(index + ":" + data[0] + "," + data[1] + "," + data[2]);
	}

	public static final boolean DEBUG = false;

	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "prp";
	protected static String _directory = null;

	protected static CsdpFrame _gui;
} // class PropertiesInput
