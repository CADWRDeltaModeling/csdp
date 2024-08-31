package DWR.CSDP;

import java.io.File;

/**
 * Read the DSM2 file channels.inp, which contains node/channel connectivity
 * information
 *
 * @author
 * @version
 */
public abstract class DSMChannelsInput {

	/**
	 * Make instance of subclass of DSMChannelsInput
	 */
	public static DSMChannelsInput getInstance(String directory, String filename) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		DSMChannelsInput input = null;

		if (filename.endsWith(ASCII_TYPE)) {
			input = new DSMChannelsAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// DSMChannelsInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new DSMChannelsAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No DSMChannels filetype defined for " + _filetype);
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
	public DSMChannels readData() {
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
	protected void storeData(int dataType, int dataNumber) {
		if(dataType==CHAN_SECTION) {
			String name = _pd.chan;
			int length = _pd.length;
			String manning = _pd.manning;
			String dispersion = _pd.dispersion;
			int upnode = _pd.upnode;
			int downnode = _pd.downnode;
	//		int xsect1 = _pd.xsect1;
	//		int dist1 = _pd.dist1;
	//		int xsect2 = _pd.xsect2;
	//		int dist2 = _pd.dist2;
	
			if (DEBUG)
				System.out.println("storing data: name, length, upnode, downnode=" + name + "," + length + "," + upnode
						+ "," + downnode);
	//		_data.addDSMChannel(dataNumber, name, length, upnode, downnode, xsect1, dist1, xsect2, dist2);
			_data.addDSMChannel(dataNumber, name, length, manning, dispersion, upnode, downnode);
		}else if (dataType==XSECT_SECTION) {
			String xsectChan = _pd.xsectChan;
			String xsectDist = _pd.xsectDist;
			String xsectElev = _pd.xsectElev;
			String xsectArea = _pd.xsectArea;
			String xsectWidth = _pd.xsectWidth;
			String xsectWetPerim = _pd.xsectWetPerim;
			
			_data.addDSMXsectLayer(dataNumber, xsectChan, xsectDist, xsectElev, xsectArea, xsectWidth, xsectWetPerim);
		}
	}// storeData

	DSMChannels _data = new DSMChannels();
	DSMChannelsParsedData _pd = new DSMChannelsParsedData();
	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "inp";
	protected final int CHAN_SECTION = 10;
	protected final int XSECT_SECTION = 20;

	protected static String _directory = null;
	protected int _numDSMChannels;

	/**
	 * Stores the values that are parsed from a line that is read from an ascii
	 * file.
	 *
	 * @author
	 * @version
	 */
	public class DSMChannelsParsedData {
		String chan = null;
		public int length = 0;
		public String manning = null;
		public String dispersion = null;
		public int upnode = 0;
		public int downnode = 0;
//		public int xsect1 = 0;
//		public int dist1 = 0;
//		public int xsect2 = 0;
//		public int dist2 = 0;
		String xsectChan = null;
		public String xsectDist = null;
		public String xsectElev = null;
		public String xsectArea = null;
		public String xsectWidth = null;
		public String xsectWetPerim = null;

	} // class DSMChannelsParsedData

} // class LandmarkInput
