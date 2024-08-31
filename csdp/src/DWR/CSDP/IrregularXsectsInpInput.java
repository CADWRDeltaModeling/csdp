package DWR.CSDP;

import java.io.File;

/**
 * Read the DSM2 file channels.inp, which contains node/channel connectivity
 * information
 *
 * @author
 * @version
 */
public abstract class IrregularXsectsInpInput {

	/**
	 * Make instance of subclass of IrregularXsectsInpInput
	 */
	public static IrregularXsectsInpInput getInstance(String directory, String filename) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		IrregularXsectsInpInput input = null;
		if (filename.endsWith(ASCII_TYPE)) {
			input = new IrregularXsectsInpAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// IrregularXsectsInpInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new IrregularXsectsInpAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No IrregularXsectsInp filetype defined for " + _filetype);
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
	public IrregularXsectsInp readData() {
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

		if (DEBUG)
			System.out.println("storing data. chan, dist, path=" + _chanNum + "," + _dist + "," + _pathname);

		_data.addLine(_chanNum, _dist, _pathname);
	}// storeData

	IrregularXsectsInp _data = new IrregularXsectsInp();
	// parsed data
	protected String _chanNum = null;
	protected float _dist = -Float.MAX_VALUE;
	protected String _pathname = null;

	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "inp";
	protected static String _directory = null;
	protected int _numDSMChannels;

} // class IrregularXsecsInpInput
