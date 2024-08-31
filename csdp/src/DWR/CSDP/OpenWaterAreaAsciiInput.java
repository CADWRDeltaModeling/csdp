package DWR.CSDP;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class OpenWaterAreaAsciiInput extends OpenWaterAreaInput {

	public OpenWaterAreaAsciiInput(CsdpFrame gui) {
		_gui = gui;
		_owaNet = new Network("openwaterarea", gui);
	}

	LineNumberReader _asciiIn;
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };

	/**
	 * Open ascii file
	 */
	protected void open() {
		FileReader aInFile;
		try {
			// if((_directory.substring(_directory.length()-1,_directory.length())).
			// equals(File.separator) == false){
			// _directory += File.separator;
			// }
			aInFile = new FileReader(_filename + "." + _filetype);
			_asciiIn = new LineNumberReader(aInFile);
			if (DEBUG)
				System.out.println("In ascii open " + _asciiIn);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out.println("Error ocurred while opening file " + _filename + "." + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii file
	 */
	protected void read() {
		Centerline centerline = null;
		try {
			// set line to non null value so the while loop will continue
			// reading
			String line = " ";
			while (line != null) {
				while (line != null && line.indexOf("X1") < 0) {
					line = _asciiIn.readLine();
				}
				centerline = null;
				if (line != null) {
					parseX1Line(line);
					centerline = storeX1Line();
				} // if
					// now read lines until a blank line or another "X1" is
					// found
				int pointIndex = 0;
				line = _asciiIn.readLine();

				while (line != null && line.indexOf("X1") < 0 && line.length() > 1
						&& CsdpFunctions.lastNonblank(line) > 0) {

					if (DEBUG)
						System.out.println("About to parse line. length,line=" + line.length() + "," + line);

					parseXsectLine(line, pointIndex);
					storeXsectLine(centerline, pointIndex);
					pointIndex++;
					line = _asciiIn.readLine();
					if (DEBUG && line != null && CsdpFunctions.lastNonblank(line) == 0) {
						System.out.println("The following line will be ignored:" + line);
					}
				}

			} // while
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _filename + ".prn:" + e.getMessage());
		} finally {
			close();
		} // catch()
	}// read()

	/**
	 * Close ascii Network data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}

	/**
	 * Parses a line from an ascii open water area data file (*.owa)
	 */
	protected void parseX1Line(String unparsedLine) {
		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\"\042\011");

		String x1String = new String(t.nextToken());
		String xd = new String(t.nextToken());
		// Integer xsDistance = null;
		int xsDistance = -Integer.MAX_VALUE;

		if (x1String.indexOf("X1") < 0)
			System.out.println("Error in OpenWaterAsciiInput:  The line does not begin with X1!");
		try {
			// xsDistance = new Integer(xd);
			xsDistance = Integer.parseInt(xd);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in OpenWaterAreaAsciiInput.parseCenterline");
			System.out.println("Unable to parse " + xd + " as an int");
		} // try

		// I will use the distance as a centerline name, using one centerline
		// for each xs.
		_pd.centerlineName = Integer.toString(xsDistance);
		// _xsDistance = xsDistance.intValue();
		// System.out.println("xsDistance="+xsDistance.toString());
	} // parseX1Line

	/**
	 * Parses a line from an ascii open water area data file (*.owa) previously,
	 * all xsect values were stored in one line. now they are stored with one
	 * point on each line. must read multiple lines and put all values in an
	 * array.
	 */
	protected void parseXsectLine(String unparsedLine, int index) throws NoSuchElementException {
		float station = -Float.MAX_VALUE;
		float elevation = -Float.MAX_VALUE;
		try {
			StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011");

			String sToken = t.nextToken();
			// System.out.println("sToken="+sToken);
			String eToken = t.nextToken();
			// System.out.println("eToken="+eToken);
			try {
				station = Float.parseFloat(sToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Error in OpenWaterAreaAsciiInput.parseXsectLine");
				System.out.println("Unable to parse " + sToken + " as an int");
			} // try

			try {
				elevation = Float.parseFloat(eToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Error in OpenWaterAreaAsciiInput.parseXsectLine");
				System.out.println("Unable to parse " + eToken + " as an int");
			} // try
		} catch (NoSuchElementException ee) {
			System.out.println("ERROR:  not enough values in line! index, line, line length=" + index + ","
					+ unparsedLine + "," + unparsedLine.length());
			JOptionPane.showOptionDialog(null,
					"ERROR!  THE FILE CONTAINS A LINE THAT DOES NOT HAVE ENOUGH VALUES!  UNABLE TO CONTINUE.  LINE="
							+ unparsedLine,
					"ERROR! OPERATION FAILED", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, _options,
					_options[0]);

		}
		// System.out.println("parsed
		// "+index+","+station.floatValue()+","+elevation.floatValue());
		_pd.station.put(index, station);
		_pd.elevation.put(index, elevation);
		// _station = station.floatValue()
		// _elevation = elevation.floatValue()

	}// parseXsectLine

} // class NetworkAsciiInput
