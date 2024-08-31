package DWR.CSDP;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

public class OpenWaterAreaToeDrainAsciiInput extends OpenWaterAreaToeDrainInput {

	LineNumberReader _asciiIn;

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
			System.out.println("Error ocurred while opening file " + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii file
	 */
	protected void read() {
		int numLines = 0;
		String line = null;
		try {
			line = _asciiIn.readLine();
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _filename + ".prn:" + e.getMessage());
		}
		if (line.toLowerCase().indexOf("end") < 0) {
			parseLine(line);
		}
		while (line.toLowerCase().indexOf("end") < 0) {
			try {
				line = _asciiIn.readLine();
			} catch (IOException e) {
				System.out.println("Error ocurred while reading file " + _filename + ".prn:" + e.getMessage());
			} // catch
			if (line.toLowerCase().indexOf("end") < 0) {
				parseLine(line);
			}
		}
		close();
	}// read()

	/**
	 * Close open water area station data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}

	/**
	 * Parses a line from an ascii station data file which contains time series
	 * data. assume station/elevation are in feet.
	 */
	protected void parseLine(String unparsedLine) {
		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011");

		String name = t.nextToken();
		float station = Float.parseFloat(t.nextToken());
		float elevation = Float.parseFloat(t.nextToken());

		_tdData.addName(name);
		_tdData.setStationFeet(name, station);
		_tdData.setElevationFeet(name, elevation);
	}// parseLine

} // class NetworkAsciiInput
