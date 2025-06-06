package DWR.CSDP;

import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Read ascii and binary bathymetry data
 *
 * @author
 * @version $Id: BathymetryInput.java,v 1.4 2005/04/08 00:14:52 btom Exp $
 */
public abstract class BathymetryInput {
	protected int _numLines = -Integer.MAX_VALUE;

	/**
	 * Make instance of subclass of BathymetryInput
	 */
	public static BathymetryInput getInstance(CsdpFrame gui, String directory, String filename) {
		_gui = gui;
		_directory = directory;

		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		BathymetryInput input = null;
		_filename = null;
		_filetype = null;
		if (filename.endsWith(GZIP_TYPE)) {
			input = new BathymetryBinaryGzipInput();
			_filetype = GZIP_TYPE;
		} else if (filename.endsWith(BINARY_TYPE)) {
			input = new BathymetryBinaryInput();
			_filetype = BINARY_TYPE;
		} else if (filename.endsWith(ASCII_TYPE)) {
			input = new BathymetryAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// BathymetryInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new BathymetryAsciiInput();
		// }
		// else if (_filetype.equals(BINARY_TYPE)) {
		// input = new BathymetryBinaryInput();
		// }
		// else if (_filetype.equals(GZIP_TYPE)) {
		// input = new BathymetryBinaryGzipInput();
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
	 * Calls appropriate read method to read bathymetry data
	 */
	public BathymetryData readData() {
		open();
		readHeaders();
		if (_data == null) {
			_data = new BathymetryData(_numLines);
			if (DEBUG)
				System.out.println("creating new BathymetryData object");
		} else {
			_data.initializeVariables(_numLines);
			if (DEBUG)
				System.out.println("reinitializing BathymetryData object");
		} // if
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
	 * Read headers (single line or metadata). get number of lines.
	 */
	protected abstract void readHeaders();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * ;HorizontalDatum: UTMNAD27 ;HorizontalZone: 10 ;HorizontalUnits: Meters
	 * ;VerticalDatum: NGVD29 ;VerticalUnits: USSurveyFeet ;Filetype: bathymetry
	 * ;NumElements: 581913
	 */
	protected void parseMetadata(String line, CsdpFileMetadata m) {
		StringTokenizer t = new StringTokenizer(line, " :");
		String nextToken = t.nextToken();
		nextToken = t.nextToken();
		if (line.indexOf("HorizontalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("UTMNAD27"))
				m.setHDatum(CsdpFileMetadata.UTMNAD27);
			else if (nextToken.equalsIgnoreCase("UTMNAD83"))
				m.setHDatum(CsdpFileMetadata.UTMNAD83);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalDatum " + nextToken + " not recognized.  using default horizontal datum.", 
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("HorizontalZone") >= 0) {
			if (nextToken.equalsIgnoreCase("10"))
				m.setHZone(10);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalZone " + nextToken + " not recognized.  using default horizontal zone.", 
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("HorizontalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("Meters"))
				m.setHUnits(CsdpFileMetadata.METERS);
			else if (nextToken.equalsIgnoreCase("Feet"))
				m.setHUnits(CsdpFileMetadata.USSURVEYFEET);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalUnits " + nextToken + " not recognized.  using default horizontal units.", 
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("NGVD29"))
				m.setVDatum(CsdpFileMetadata.NGVD1929);
			else if (nextToken.equalsIgnoreCase("NAVD88"))
				m.setVDatum(CsdpFileMetadata.NAVD1988);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalDatum " + nextToken + " not recognized.  using default vertical datum.", 
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("USSurveyFeet"))
				m.setVUnits(CsdpFileMetadata.USSURVEYFEET);
			else if (nextToken.equalsIgnoreCase("meters"))
				m.setVUnits(CsdpFileMetadata.METERS);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalUnits " + nextToken + " not recognized.  using default vertical units.", 
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("Filetype") >= 0) {
			// do nothing...
		} else if (line.indexOf("NumElements") > 0) {
			try {
				// numLines = new Integer(nextToken);
				_numLines = Integer.parseInt(nextToken);
				// _data.putNumLines(_numLines);
				m.setNumElements(_numLines);
			} catch (java.lang.NumberFormatException e) {
				JOptionPane.showMessageDialog(_gui, "Error reading metadata line. Expecting NumElements. line=" + line, 
						"Error", JOptionPane.ERROR_MESSAGE);
			} // try
		} else {
			JOptionPane.showMessageDialog(_gui, "unable to parse metadata line: " + line + ". File may not be loaded correctly", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}// parseMetadata

	/**
	 * Stores one line of parsed data in adjustable arrays. Checks current file
	 * metadata.
	 */
	protected void storeData(int dataNumber, CsdpFileMetadata m) {
		float x = _pd.x;
		float y = _pd.y;
		float z = _pd.z;
		int hUnits = m.getHUnits();
		int vUnits = m.getVUnits();
		if (hUnits == CsdpFileMetadata.METERS && vUnits == CsdpFileMetadata.USSURVEYFEET) {
			_data.setPointMetersFeet(dataNumber, x, y, z);
		} else if (hUnits == CsdpFileMetadata.USSURVEYFEET && vUnits == CsdpFileMetadata.USSURVEYFEET) {
			_data.setPointFeet(dataNumber, x, y, z);
		} else if (hUnits == CsdpFileMetadata.METERS && vUnits == CsdpFileMetadata.METERS) {
			_data.setPointMeters(dataNumber, x, y, z);
		} else {
			if (DEBUG)
				System.out.println("ERROR in BathymetryInput.storeData: horizontal and vertical units not set");
		}
		storeUnique(dataNumber);
	}// storeData

	/**
	 * If year/source matches any previous years, don't save. If it doesn't,
	 * save. Save index of matching (or new) year.
	 */
	protected void storeUnique(int dataNumber) {
		boolean pMatch = false;
		// YEAR
		for (short j = 0; j <= _data.getNumYears() - 1; j++) {
			if (_data.getYear(j) == _pd.year) {
				_data.putYearIndex(dataNumber, j);
				pMatch = true;
			}
		}
		if (pMatch == false) {
			_data.putYear(_data.getNumYears(), _pd.year);
			_data.putYearIndex(dataNumber, _data.getNumYears() - 1);
		}

		// SOURCE
		pMatch = false;
		for (short j = 0; j <= _data.getNumSources() - 1; j++) {
			if (_data.getSource(j) != null && _data.getSource(j).equals(_pd.source)) {
				_data.putSourceIndex(dataNumber, j);
				pMatch = true;
			}
		}
		if (pMatch == false) {
			_data.putSource(_data.getNumSources(), _pd.source);
			_data.putSourceIndex(dataNumber, _data.getNumSources() - 1);

			if (DEBUG)
				System.out.println("numSources=" + _data.getNumSources() + " " + _pd.source);
		}
	} // storeData()

	BathymetryData _data = null;
	BathymetryParsedData _pd = new BathymetryParsedData(); // vector-stores 6
															// values
	public static final boolean DEBUG = false;

	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "prn";
	protected static final String BINARY_TYPE = "cdp";
	protected static final String GZIP_TYPE = "cdp.gz";
	protected static String _directory = null;
	protected static JFrame _gui;
} // class BathymetryInput
