package DWR.CSDP;

import java.io.File;

/**
 * Write ascii and binary properties data
 *
 * @author
 * @version $Id: PropertiesOutput.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp $
 */
public abstract class PropertiesOutput {

	public static final boolean DEBUG = false;

	/**
	 * Make instance of subclass of PropertiesOutput
	 */
	public static PropertiesOutput getInstance(CsdpFrame gui, String directory, String filename, String filetype) {
		_gui = gui;
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		PropertiesOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			output = new PropertiesAsciiOutput();
		} else {// throw new IllegalInputFileException(msg);
			System.out.println("No properties filetype defined for " + _filetype);
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write properties data
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
	}

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

	protected static final String ASCII_TYPE = "prp";
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static String _directory = null;
	protected static CsdpFrame _gui;
} // PropertiesOutput
