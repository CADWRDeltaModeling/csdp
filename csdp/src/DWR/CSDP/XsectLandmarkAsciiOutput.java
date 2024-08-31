package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes network data to ascii file
 */
public class XsectLandmarkAsciiOutput extends XsectLandmarkOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _net = null;
	private String directory;
	protected static final String SPACES = "          ";

	/**
	 * assigns data storage object to class variable
	 */
	public XsectLandmarkAsciiOutput(Network net, String directory) {
		_net = net;
		this.directory = directory;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			_aOutFile = new FileWriter(this.directory+File.separator+FILENAME);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			System.out.println("Error ocurred while opening file " + FILENAME + e.getMessage());
		} // catch()
	}

	/**
	 * write ascii network data file
	 */
	protected void write() {
		String line = null;
		Centerline centerline;
		Xsect xsect;
		String name = null;

		double landmarkX = 0.0;
		double landmarkY = 0.0;
		int numXsects = 0;
		double[] xsectLineCoord = new double[4];
		for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
			name = _net.getCenterlineName(i);
			centerline = _net.getCenterline(name);
			numXsects += centerline.getNumXsects();
		}

		CsdpFileMetadata lMeta = CsdpFunctions.getBathymetryMetadata();
		try {
			_asciiOut.write(";HorizontalDatum:  " + lMeta.getHDatumString());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalZone:   " + lMeta.getHZone());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalUnits:  " + lMeta.getHUnitsString());
			_asciiOut.newLine();
			_asciiOut.write(";VerticalDatum:    " + lMeta.getVDatumString());
			_asciiOut.newLine();
			_asciiOut.write(";VerticalUnits:    " + lMeta.getVUnitsString());
			_asciiOut.newLine();
			_asciiOut.write(";Filetype:          landmark");
			_asciiOut.newLine();
			_asciiOut.write(";NumElements: " + numXsects);
			_asciiOut.newLine();
			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				name = _net.getCenterlineName(i);
				centerline = _net.getCenterline(name);
				for (int j = 0; j <= centerline.getNumXsects() - 1; j++) {
					xsectLineCoord = _net.findXsectLineCoord(name, j);
					xsect = centerline.getXsect(j);
					if (xsectLineCoord[CsdpFunctions.x1Index] > xsectLineCoord[CsdpFunctions.x2Index]) {
						landmarkX = xsectLineCoord[CsdpFunctions.x1Index];
						landmarkY = xsectLineCoord[CsdpFunctions.y1Index];
					} else {
						landmarkX = xsectLineCoord[CsdpFunctions.x2Index];
						landmarkY = xsectLineCoord[CsdpFunctions.y2Index];
					}
					landmarkX = CsdpFunctions.feetToMeters(landmarkX);
					landmarkY = CsdpFunctions.feetToMeters(landmarkY);
					line = landmarkX + " " + landmarkY + "  0 0 " + "\"Arial" + "\" -11 0 0 0 0 0 0 0 0 0 0 0 0 " + "\""
							+ name + "_" + j + "\"";
					_asciiOut.write(line);
					_asciiOut.newLine();
					line = null;
				} // for j
				line = null;
			} // for i
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

}// class XsectLandmarkAsciiOutput
