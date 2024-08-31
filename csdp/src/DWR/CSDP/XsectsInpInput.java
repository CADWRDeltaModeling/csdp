package DWR.CSDP;

import java.io.File;

/**
 * Read the DSM2 file channels.inp, which contains node/channel connectivity
 * information
 *
 * @author
 * @version
 */
public abstract class XsectsInpInput {

	/**
	 * Make instance of subclass of IrregularXsectsInpInput
	 */
	public static XsectsInpInput getInstance(String directory, String filename) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		XsectsInpInput input = null;
		if (filename.endsWith(ASCII_TYPE)) {
			input = new XsectsInpAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// XsectsInpInput input = null;
		// if (_filetype.equalsIgnoreCase(ASCII_TYPE)) {
		// input = new XsectsInpAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No XsectsInp filetype defined for " + _filetype);
			_filetype = null;
		}
		if (_filetype != null) {
			_filename = filename.substring(0, filename.lastIndexOf(_filetype) - 1);
		}
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read DSMChannels data
	 */
	public XsectsInp readData() {
		open();
		read();
		close();
		return _data;
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
	 * Stores a DSMChannels point
	 */
	protected void storeData() {
		_data.addXsect(_xsectNum, _width, _botelv, _initStage, _initFlow);
	}// storeData

	XsectsInp _data = new XsectsInp();
	// parsed data
	protected int _xsectNum = -Integer.MAX_VALUE;
	protected float _width = -Integer.MAX_VALUE;
	protected float _botelv = -Float.MAX_VALUE;
	protected float _initStage = -Float.MAX_VALUE;
	protected float _initFlow = -Float.MAX_VALUE;

	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "inp";
	protected static String _directory = null;
	protected int _numDSMChannels;

} // class XsecsInpInput
