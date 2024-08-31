package DWR.CSDP;

/**
 * Read open water area Toe drain input data. Toe drain data should contain a
 * cross-section name, station, and elevation. If the stage is at or below the
 * specified elevation, then the water is assumed to be only in the portion of
 * the cross-section that is between the specified station and the maximum
 * station value.
 *
 * @author
 * @version $Id: OpenWaterAreaToeDrainInput.java,v 1.1.1.1 2002/06/10 20:15:01
 *          btom Exp $
 */
public abstract class OpenWaterAreaToeDrainInput {

	ToeDrainData _tdData = new ToeDrainData();

	/**
	 * Make instance of subclass
	 */
	public static OpenWaterAreaToeDrainInput getInstance(String filename) {
		// _directory = directory;
		// if((_directory.substring(_directory.length()-1,_directory.length())).
		// equals(File.separator) == false){
		// _directory += File.separator;
		// }
		parseFilename(filename);

		OpenWaterAreaToeDrainInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		input = new OpenWaterAreaToeDrainAsciiInput();
		// }
		// else {// throw new IllegalInputFileException(msg);
		// System.out.println();
		// _filetype = null;
		// }
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read Network data
	 */
	public ToeDrainData readData() {
		open();
		read();
		close();
		return _tdData;
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
	 * separates filename into prefix and extension
	 */
	protected static void parseFilename(String filename) throws NullPointerException {
		try {

			_filename = null;
			_filetype = null;
			if (filename.endsWith(ASCII_TYPE)) {
				_filetype = ASCII_TYPE;
			}
			if (_filetype != null) {
				_filename = filename.substring(0, filename.lastIndexOf(_filetype) - 1);
			}
			// int dotIndex = filename.indexOf(".",0);
			// if(dotIndex >= 0){
			// _filename = filename.substring(0,dotIndex);
			// _filetype = filename.substring(dotIndex+1);
			// }
			// else if(dotIndex < 0){
			// _filename = null;
			// _filetype = null;
			// }
		} catch (Exception e) {
			System.out.println("no filename specified");
		} // catch
	}// parseFilename

	protected int _numLines;
	// protected float _station;
	// protected float _elevation;

	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "owa";
	protected static final String BINARY_TYPE = null;
	// protected static String _directory = null;
	public static final boolean DEBUG = false;

} // class NetworkInput
