package DWR.CSDP;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class DigitalLineGraphAsciiInput extends DigitalLineGraphInput {

	LineNumberReader _asciiIn;

	/**
	 * Open ascii file
	 */
	protected void open() {
		FileReader aInFile;
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			aInFile = new FileReader(_directory + _filename + "." + _filetype);
			_asciiIn = new LineNumberReader(aInFile);
			if (DEBUG)
				System.out.println("In ascii open " + _asciiIn);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Directory + Filetype: " + _directory + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii file
	 */
	protected void read() {
		// int numLines=0;
		CsdpFileMetadata dlgMetadata = new CsdpFileMetadata();
		boolean fileHasMetadata = false;

		String line = null;
		// these lines are for reading USGS digital line graph files
		// for(int i=0; i<=9; i++){
		// line = _asciiIn.readLine();
		// }
		// parseCoefficientLine(line);
		// if(DEBUG)System.out.println("number of landmarks = "+_numLines);
		// //skip next 5 lines; read next line
		// for(int i=0; i<=5; i++){
		// line = _asciiIn.readLine();
		// }
		// //skip area and node lines
		// while(line.indexOf("L") < 0){
		// line = _asciiIn.readLine();
		// }

		try {
			line = _asciiIn.readLine();
			if (line.indexOf(";") < 0) {
				fileHasMetadata = false;
				JOptionPane.showMessageDialog(_gui, "This DigitalLineGraph file has no metadata. " + 
				"UTM zone 10 NAD 27, NGVD 1929 will be assumed.", "No Metadata", JOptionPane.INFORMATION_MESSAGE);

				dlgMetadata.setToDefault();
				_numLines = parseFirstLine(line);
			} else {
				fileHasMetadata = true;
				System.out.println("parse DigitalLineGraph metadata");
				for (int i = 0; i <= CsdpFunctions.getNumMetadataLines() - 1; i++) {
					// first line already read
					if (i > 0)
						line = _asciiIn.readLine();
					if (line.indexOf(";") >= 0) {
						parseMetadata(line, dlgMetadata);
					} else {
						JOptionPane.showMessageDialog(_gui, "incomplete DigitalLineGraph metadata!  there should be "
								+ CsdpFunctions.getNumMetadataLines() + " lines.  "
								+ "The following line was expected to be metadata line:" + line, "Error", JOptionPane.ERROR_MESSAGE);
					} // if it's a metadata line (should be)
				} // read all metadata lines
			}
			System.out.println("done parsing DigitalLineGraph metadata.hdatum,hzone"
					+ ",hunits,vdatum,vunits, numCenterlines=" + dlgMetadata.getHDatumString() + ","
					+ dlgMetadata.getHZone() + "," + dlgMetadata.getHUnitsString() + "," + dlgMetadata.getVDatumString()
					+ "," + dlgMetadata.getVUnitsString() + "," + dlgMetadata.getNumElements());
			CsdpFunctions.setDigitalLineGraphMetadata(dlgMetadata);
		} catch (IOException e) {
			System.out.println("ERROR in DigitalLineGraphAsciiInput.read");
		}

		try {
			// start reading line lines
			System.out.println("reading file...");
			System.out.println("numlines=" + _numLines);
			for (int i = 0; i <= _numLines - 1; i++) {
				// finds _currentLineName
				line = _asciiIn.readLine();
				parseDigitalLineGraphLineHeader(i, line);
				addLine();
				_currentPointIndex = 0;
				while (line.indexOf("END") < 0) {
					line = _asciiIn.readLine();
					if (line.indexOf("END") < 0) {
						if (DEBUG)
							System.out.println("about to parse line " + line);
						parseAndStoreDigitalLineGraphPoint(line);
					}
				} // while
			} // for i

			findMaxMin(_dlg.getNumLines());
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} finally {
			close();
		} // catch()
	}// read

	/**
	 * Close ascii Landmark data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while closing file " + _directory + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}

	/**
	 * parses line header. Line headers begin with "L" and contain the number of
	 * coordinate pairs(points) in the line.
	 */
	protected void parseDigitalLineGraphLineHeader(int index, String line) {
		StringTokenizer t = new StringTokenizer(line, ",\042\011");
		// int numPoints = -Integer.MAX_VALUE;
		String lineName = null;
		String nextToken = null;

		try {
			// nextToken = t.nextToken();
			lineName = t.nextToken();
			// for(int i=0; i<=3; i++){
			// t.nextToken();
			// }
			// nextToken = t.nextToken();
			// numPoints = Integer.parseInt(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in DigitalLineGraphAsciiInput.parseDigitalLineGraphLineHeader:");
			System.out.println("Unable to parse " + nextToken);
		}
		_currentLineName = lineName;
		if (DEBUG)
			System.out.println("currentLineName=" + _currentLineName);
	}// parseDigitalLineGraphLineHeader

	/**
	 * parses first line to an int. The first line should be the number of lines
	 * in the file
	 */
	protected int parseFirstLine(String line) {
		// delimiters: comma, quote, tab, space
		StringTokenizer t = new StringTokenizer(line, ",\042\011\040");
		// int numPoints = -Integer.MAX_VALUE;
		String nextToken = null;
		int returnValue = -Integer.MAX_VALUE;

		try {
			nextToken = t.nextToken();
			returnValue = Integer.parseInt(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in DigitalLineGraphAsciiInput.parseFirstLine:");
			System.out.println("Unable to parse " + nextToken);
		}
		return returnValue;
	}// parseDigitalLineGraphLineHeader

	/**
	 * Parses a line from an ascii DigitalLineGraph data file (*.dlg) pLineNum
	 * is actually the line number-1. Store values in the vector parsedData.
	 */
	protected void parseAndStoreDigitalLineGraphPoint(String unparsedLine) {

		if (DEBUG)
			System.out.println("unparsedline = " + unparsedLine);

		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011");
		String pName = null;
		String nextToken = null;

		try {
			while (t.hasMoreTokens()) {
				nextToken = t.nextToken();
				_x = Float.parseFloat(nextToken);
				nextToken = t.nextToken();
				_y = Float.parseFloat(nextToken);
				addAndStorePoint();
			}
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in DigitalLineGraphAsciiInput.parseDigitalLineGraphPoint");
			System.out.println("Unable to parse " + nextToken + " as a float");
		} // try

	} // parseAndStoreDigitalLineGraphPoint

	// /**
	// * Parses the 10th line of the dlg file which contains the coefficients
	// used
	// * for converting to the internal coordinate system.
	// */
	// protected void parseCoefficientLine(String theLine) {

	// StringTokenizer t = new StringTokenizer(theLine, " ");
	// String nextToken = t.nextToken();
	// float a1 = -Float.MAX_VALUE;
	// float a2 = -Float.MAX_VALUE;
	// float a3 = -Float.MAX_VALUE;
	// float a4 = -Float.MAX_VALUE;
	// try{
	// a1 = Float.parseFloat(nextToken);
	// a2 = Float.parseFloat(t.nextToken());
	// a3 = Float.parseFloat(t.nextToken());
	// a4 = Float.parseFloat(t.nextToken());
	// } catch(java.lang.NumberFormatException e) {
	// System.out.println("Error reading coefficient line: "+theLine);
	// }
	// _a1 = a1;
	// _a2 = a2;
	// _a3 = a3;
	// _a4 = a4;
	// } // parseCoefficientLine

} // class DigitalLineGraphAsciiInput
