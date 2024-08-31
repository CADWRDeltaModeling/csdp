package DWR.CSDP;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

public class XsectAsciiInput extends XsectInput {

	/**
	 * Open ascii file
	 */
	protected void open() {
		FileReader aInFile;
		try {
			if (DEBUG)
				System.out.println("about to read file " + _fullPathname);
			aInFile = new FileReader(_fullPathname);
			_asciiIn = new LineNumberReader(aInFile);
		} catch (IOException e) {
			System.out.println("Error ocurred while opening file " + _fullPathname + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii file
	 */
	protected Xsect read() {
		Xsect xsect = null;
		int numLines = 0;

		try {
			String line = null;
			line = _asciiIn.readLine();// skip first line
			while (line.indexOf("station") < 0) {
				line = _asciiIn.readLine();
			}
			parseStationLine(line);
			while (line.indexOf("elevation") < 0) {
				line = _asciiIn.readLine();
			}
			parseElevationLine(line);

			xsect = storeData();

		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _fullPathname + e.getMessage());
		} finally {
			close();
		} // catch()
		return xsect;
	}// read

	/**
	 * Close ascii IrregularXsectsInp data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _fullPathname + ":" + e.getMessage());
		} // catch
	}

	/**
	 * parses line of station values
	 */
	protected void parseStationLine(String unparsedLine) {
		if (DEBUG)
			System.out.println("station line = " + unparsedLine);

		StringTokenizer t = new StringTokenizer(unparsedLine, " ,:");
		int numTokens = 0;
		String nextToken = t.nextToken();

		if (nextToken.indexOf("station") < 0) {
			System.out.println("ERROR in XsectAsciiInput.parseStationLine:  ");
			System.out.println("First token should be 'station' but it's actually " + nextToken);
		}
		while (t.hasMoreTokens()) {
			_station.put(numTokens, Float.parseFloat(t.nextToken()));
			numTokens++;
		}
		_numStationValues = numTokens;
	} // parseStationLine

	/**
	 * parses line of elevation values
	 */
	protected void parseElevationLine(String unparsedLine) {

		if (DEBUG)
			System.out.println("elevation line = " + unparsedLine);

		StringTokenizer t = new StringTokenizer(unparsedLine, " ,:");
		int numTokens = 0;
		String nextToken = t.nextToken();

		if (nextToken.indexOf("elevation") < 0) {
			System.out.println("ERROR in XsectAsciiInput.parseElevationLine:  ");
			System.out.println("First token should be 'elevation' but it's actually " + nextToken);
		}
		while (t.hasMoreTokens()) {
			_elevation.put(numTokens, Float.parseFloat(t.nextToken()));
			numTokens++;
		}
		_numElevationValues = numTokens;
	} // parseElevationLine

	LineNumberReader _asciiIn;
} // class XsectAsciiInput
