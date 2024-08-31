package DWR.CSDP;

/**
 * Read open water area input data
 *
 * @author
 * @version $Id: OpenWaterAreaStationInput.java,v 1.2 2005/04/08 04:00:06 btom
 *          Exp $
 */
public abstract class OpenWaterAreaStationInput {

	protected StationTimeSeriesData _stationData = new StationTimeSeriesData();
	// protected static CsdpFrame _gui;

	/**
	 * Make instance of subclass
	 */
	public static OpenWaterAreaStationInput getInstance(String filename, CsdpFrame gui) {
		// if((_directory.substring(_directory.length()-1,_directory.length())).
		// equals(File.separator) == false){
		// _directory += File.separator;
		// }
		parseFilename(filename);
		// _gui=gui;
		OpenWaterAreaStationInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		input = new OpenWaterAreaStationAsciiInput();
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
	public StationTimeSeriesData readData() {
		open();
		read();
		close();
		return _stationData;
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

	protected NetworkParsedData _pd = new NetworkParsedData(); // vector-stores
																// 6 values

	protected int _numLines;
	// protected float _station;
	// protected float _elevation;

	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "owa";
	protected static final String BINARY_TYPE = null;
	///// protected static String _directory = null;
	public static final boolean DEBUG = false;

} // class NetworkInput
