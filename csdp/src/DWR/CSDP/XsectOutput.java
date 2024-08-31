
package DWR.CSDP;

import java.io.File;

/**
 * Write cross-section properties
 *
 * @author
 * @version $Id: XsectOutput.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp $
 */
public abstract class XsectOutput {

	protected static final String ASCII_TYPE = "txt";
	public static final boolean DEBUG = true;
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)

	/**
	 * Make instance of subclass of XsectOutput
	 */
	public static XsectOutput getInstance(String dir, String filename, String filetype, int xNumber, Xsect xsect) {
		_directory = dir;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		XsectOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			output = new XsectAsciiOutput(xsect);
		}
		// else if (_filetype.equals(BINARY_TYPE)) {
		// output = new NetworkBinaryOutput(data);
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println();
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write network data
	 */
	public void writeData() {
		open();
		write();
		close();
	}

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write file
	 */
	protected abstract void write();

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

	protected static String _directory;
}// class NetworkOutput
