package DWR.CSDP;

/**
 * Read open water area input data
 *
 * @author
 * @version $Id: OpenWaterAreaInput.java,v 1.3 2005/04/08 03:57:50 btom Exp $
 */
public abstract class OpenWaterAreaInput {

	Network _owaNet;

	/**
	 * Make instance of subclass of BathymetryInput
	 */
	public static OpenWaterAreaInput getInstance(String filename, String filetype, CsdpFrame gui) {
		// _directory = directory;
		// if((_directory.substring(_directory.length()-1,_directory.length())).
		// equals(File.separator) == false){
		// _directory += File.separator;
		// }
		_filename = filename;
		_filetype = filetype;

		OpenWaterAreaInput input = null;
		if (_filetype.equals(ASCII_TYPE)) {
			input = new OpenWaterAreaAsciiInput(gui);
		} else {// throw new IllegalInputFileException(msg);
			System.out.println();
			_filetype = null;
		}
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read Network data
	 */
	public Network readData() {
		open();
		read();
		close();
		return _owaNet;
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
	 * copy parsed centerline values to network object
	 */
	protected void storeCenterline() {
		String name = _pd.centerlineName;
		_owaNet.addCenterline(name);

		for (int i = 0; i <= _pd.numCenterlinePoints - 1; i++) {
			_owaNet.getCenterline(name).addCenterlinePoint();
			_owaNet.getCenterline(name).getCenterlinePoint(i).putXFeet(_pd.xUTM.get(i));
			_owaNet.getCenterline(name).getCenterlinePoint(i).putYFeet(_pd.yUTM.get(i));
		}
	}

	/**
	 * makes a centerline
	 */
	protected Centerline storeX1Line() {
		_owaNet.addCenterline(_pd.centerlineName);
		// System.out.println(_pd.centerlineName);
		return _owaNet.getCenterline(_pd.centerlineName);
		// System.out.println("storing centerline. name="+_pd.centerlineName);
	}

	/**
	 * Copy parsed cross-section values to xsect
	 */
	protected void storeXsectLine(Centerline centerline, int xsPointIndex) {
		if (DEBUG)
			System.out.println("xsPointIndex=" + xsPointIndex);

		int xsIndex = 0; // THERE IS ONLY ONE XS/centerline for OWA
							// calculations.
		String name = _pd.centerlineName;
		if (DEBUG)
			System.out.println("storing xsect data for centerline " + name);
		if (DEBUG)
			System.out.println("_owaNet=" + _owaNet);

		Xsect xsect = null;

		if (centerline.getNumXsects() <= 0) {
			centerline.addXsect();
		}
		xsect = centerline.getXsect(xsIndex);

		xsect.addXsectPoint();
		xsect.getXsectPoint(xsPointIndex).putStationFeet(_pd.station.get(xsPointIndex));
		xsect.getXsectPoint(xsPointIndex).putElevationFeet(_pd.elevation.get(xsPointIndex));

		// System.out.println("storing.
		// index,station,elevation="+xsPointIndex+","+_pd.station.get(xsPointIndex)+","+_pd.elevation.get(xsPointIndex));

	}// storeXsectLine

	/**
	 * Convert x and y values from meters(UTM) to feet
	 */
	protected float metersToFeet(float value) {
		final float METERS_TO_FEET = 3.28084f;
		return METERS_TO_FEET * value;
	}

	protected NetworkParsedData _pd = new NetworkParsedData(); // vector-stores
																// 6 values

	// protected int _numLines;
	// protected float _station;
	// protected float _elevation;

	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "owa";
	protected static final String BINARY_TYPE = null;
	public static final boolean DEBUG = false;
	protected static CsdpFrame _gui;

} // class NetworkInput
