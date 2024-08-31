package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes network data to ascii file
 */
public class IrregularXsectsInpAsciiOutput extends IrregularXsectsInpOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _net = null;
	protected static final String SPACES = "          ";

	/**
	 * assigns data storage object to class variable
	 */
	public IrregularXsectsInpAsciiOutput(Network net) {
		_net = net;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			_aOutFile = new FileWriter(_directory + FILENAME);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			System.out.println("Error ocurred while opening file " + _directory + FILENAME + e.getMessage());
		} // catch()
	}

	/**
	 * write ascii irregular_xsects.inp file
	 */
	protected void write() {
		String line = null;
		Centerline centerline;
		CenterlinePoint cPoint;
		Xsect xsect;
		XsectPoint xPoint;
		String name = null;
		double dist = 0.0;
		double length = 0.0;
		double normalizedDist = 0.0;
		try {
			_asciiOut.write(FIRST_LINE);
			_asciiOut.newLine();
			_asciiOut.write(SECOND_LINE);
			_asciiOut.newLine();
			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				name = _net.getCenterlineName(i);
				centerline = _net.getCenterline(name);
				for (int j = 0; j <= centerline.getNumXsects() - 1; j++) {
					if (name.length() <= 4) {
						line = SPACES.substring(0, 4 - name.length()) + name + SPACES.substring(0, 5);
					} else {
						line = " " + name + " ";
					}
					xsect = centerline.getXsect(j);
					length = centerline.getLengthFeet();
					dist = xsect.getDistAlongCenterlineFeet();
					normalizedDist = dist / length;
					if (xsect.getNumPoints() > 0) {
						String s = Double.toString(normalizedDist);
						if (s.indexOf(".", 0) > 0)
							s = s + "0000000";
						else
							s = s + ".000000";
						line += s.substring(0, 7) + SPACES.substring(0, 4);
						line += "$IRREG/" + name + "_" + s.substring(0, 7) + ".txt";
						_asciiOut.write(line);
						_asciiOut.newLine();
					} else {
						System.out.println("not writing xsect " + name + "_" + j
								+ " to irregular_xsects.inp file because it has no points");

					}
					line = null;
				} // for j
				line = null;
			} // for i
			_asciiOut.write(LAST_LINE);
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + FILENAME + e.getMessage());
		} finally {
		} // catch()
	}// write

	/**
	 * Close ascii bathymetry data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + FILENAME + e.getMessage());
		} // catch
	}// close

}// class IrregularXsectsInpAsciiOutput
