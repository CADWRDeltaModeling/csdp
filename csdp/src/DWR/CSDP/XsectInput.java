package DWR.CSDP;

/**
 * Read the DSM2 file channels.inp, which contains node/channel connectivity
 * information
 *
 * @author
 * @version
 */
public abstract class XsectInput {

	/**
	 * Make instance of subclass of IrregularXsectsInpInput
	 */
	public static XsectInput getInstance(String fullPathname) {
		_fullPathname = fullPathname;
		XsectInput input = null;
		input = new XsectAsciiInput();
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read DSMChannels data
	 */
	public Xsect readData() {
		Xsect xsect = null;
		open();
		xsect = read();
		close();
		return xsect;
	}

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * Read file
	 */
	protected abstract Xsect read();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * Stores a Xsect point
	 */
	protected Xsect storeData() {
		Xsect xsect = new Xsect();

		if (_numStationValues == 0 && _numElevationValues == 0) {
			System.out.println("ERROR in XsectInput.storeData while reading file " + _fullPathname);
			System.out.println("numstationvalues, numelevationvalues=" + _numStationValues + "," + _numElevationValues);

		}

		if (_numStationValues == _numElevationValues) {
			for (int i = 0; i <= _numStationValues - 1; i++) {
				xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, _station.get(i), _elevation.get(i));

				if (DEBUG)
					System.out.println(
							"adding xsect point:  station, elevation=" + _station.get(i) + "," + _elevation.get(i));
			}
			// xsect.setDistAlongCenterline(_distAlong);
		} else {
			System.out.println("ERROR in XsectInput.storeData:");
			System.out.println("numStationValues,numElevationValues=" + _numStationValues + "," + _numElevationValues);
		}
		return xsect;
	}// storeData

	IrregularXsectsInp _data = new IrregularXsectsInp();
	// parsed data

	// protected float _distAlong = -CsdpFunctions.BIG_FLOAT;
	protected ResizableFloatArray _station = new ResizableFloatArray(20, 10);
	protected ResizableFloatArray _elevation = new ResizableFloatArray(20, 10);
	protected int _numStationValues = -Integer.MAX_VALUE;
	protected int _numElevationValues = -Integer.MAX_VALUE;

	public static final boolean DEBUG = false;

	protected static String _fullPathname = null;
} // class XsectInput
