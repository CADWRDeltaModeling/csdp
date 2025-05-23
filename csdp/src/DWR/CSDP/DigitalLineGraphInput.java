package DWR.CSDP;

import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


/**
 * Read USGS Digital Line Graph Files. These files are available at
 * http://bard.wr.usgs.gov/htmldir/dlg_html/hypso.html
 *
 * @author
 * @version
 */
public abstract class DigitalLineGraphInput {

	/**
	 * Make instance of subclass of DigitalLineGraphInput
	 */
	public static DigitalLineGraphInput getInstance(CsdpFrame gui, String directory, String filename) {
		_gui = gui;
		_dlg = new DigitalLineGraph(_gui);
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		DigitalLineGraphInput input = null;
		if (filename.endsWith(ASCII_TYPE)) {
			input = new DigitalLineGraphAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// DigitalLineGraphInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new DigitalLineGraphAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No digital line graph filetype defined for " + _filetype);
			_filetype = null;
		}
		if (_filetype != null) {
			_filename = filename.substring(0, filename.lastIndexOf(_filetype) - 1);
		}
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read Landmark data
	 */
	public DigitalLineGraph readData() {
		open();
		read();
		close();
		_dlg.convertToBathymetryDatum();
		return _dlg;
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
	 * add digital line graph line
	 */
	protected void addLine() {
		_dlg.addLine(_currentLineName);
	}

	/**
	 * add digital line graph point
	 */
	protected void addAndStorePoint() {
		DigitalLineGraphLine line = _dlg.getLine(_currentLineName);
		double x = CsdpFunctions.metersToFeet(_x);
		double y = CsdpFunctions.metersToFeet(_y);
		line.addPoint(_currentPointIndex, x, y);
		if (DEBUG)
			System.out.println("adding point for line " + _currentLineName + ",x,y=" + _x + "," + _y);

		_currentPointIndex++;
	}// addAndStorePoint

	/**
	 * Find maximum and minimum values of x and y and store them.
	 */
	protected void findMaxMin(int numLines) {
		double x = 0.0;
		double y = 0.0;
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		DigitalLineGraphLine line = null;

		if (DEBUG)
			System.out.println("finding max and min values.  number of points=" + numLines);
		for (int i = 0; i <= numLines - 1; i++) {
			line = _dlg.getLine(_dlg.getDigitalLineGraphLineName(i));
			for (int j = 0; j <= line.getNumPoints() - 1; j++) {
				x = line.getX(j);
				y = line.getY(j);
				if (x != 0)
					minX = Math.min(x, minX);
				if (x != 0)
					maxX = Math.max(x, maxX);
				if (y != 0)
					minY = Math.min(y, minY);
				if (y != 0)
					maxY = Math.max(y, maxY);
			}
		}
		_dlg.putMinXFeet(minX);
		_dlg.putMaxXFeet(maxX);
		_dlg.putMinYFeet(minY);
		_dlg.putMaxYFeet(maxY);
		if (DEBUG)
			System.out.println("findMaxMin called. MinX,MaxX,MinY,MaxY=" + _dlg.getMinXFeet() + " " + _dlg.getMaxXFeet()
					+ " " + _dlg.getMinYFeet() + " " + _dlg.getMaxYFeet());
	}// findMaxMin

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
						"Using default", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("HorizontalZone") >= 0) {
			if (nextToken.equalsIgnoreCase("10"))
				m.setHZone(10);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalZone " + nextToken + " not recognized.  using default horizontal zone.", 
						"Using default", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("HorizontalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("Meters"))
				m.setHUnits(CsdpFileMetadata.METERS);
			else if (nextToken.equalsIgnoreCase("Feet"))
				m.setHUnits(CsdpFileMetadata.USSURVEYFEET);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalUnits " + nextToken + " not recognized.  using default horizontal units.", 
						"Using default", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("NGVD29"))
				m.setVDatum(CsdpFileMetadata.NGVD1929);
			else if (nextToken.equalsIgnoreCase("NAVD88"))
				m.setVDatum(CsdpFileMetadata.NAVD1988);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalDatum " + nextToken + " not recognized.  using default vertical datum.", 
						"Using default", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("USSurveyFeet"))
				m.setVUnits(CsdpFileMetadata.USSURVEYFEET);
			else if (nextToken.equalsIgnoreCase("meters"))
				m.setVUnits(CsdpFileMetadata.METERS);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalUnits " + nextToken + " not recognized.  using default vertical units.", 
						"Using default", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("Filetype") >= 0) {
			// do nothing...
		} else if (line.indexOf("NumElements") > 0) {
			try {
				_numLines = Integer.parseInt(nextToken);
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

	protected static DigitalLineGraph _dlg;
	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "cdo";
	protected static String _directory = null;
	protected int _numLines;
	protected double _a1;
	protected double _a2;
	protected double _a3;
	protected double _a4;
	protected ResizableStringArray _unparsedLineCoordinates;
	protected String _currentLineName;
	protected int _currentPointIndex;
	protected double _x;
	protected double _y;
	protected static CsdpFrame _gui;
} // class DigitalLineGraphInput
