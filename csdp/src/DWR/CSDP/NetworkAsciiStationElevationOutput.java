package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes network data to ascii file
 */
public class NetworkAsciiStationElevationOutput extends NetworkOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _net = null;

	/**
	 * assigns data storage object to class variable
	 */
	NetworkAsciiStationElevationOutput(Network net) {
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
			_aOutFile = new FileWriter(_directory + _filename + "." + EXPORT_TYPE);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory, Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * export network to station/elevation format (HEC-2) for use with
	 * OpenWaterAreaCalculations
	 */
	protected boolean write() {
		boolean success = false;
		String line = null;
		Centerline centerline;
		CenterlinePoint cPoint;
		Xsect xsect;
		XsectPoint xPoint;
		String metadata;

		_net.sortCenterlineNames();

		try {
			String nl = null;
			String versionLine = null;
			Integer numLines = null;
			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				if (_net.getCenterlineName(i).length() <= 0) {
					System.out.println("decrementing numCenterlines because nameless centerline found");
					_net.removeCenterline(_net.getCenterlineName(i));
					// go back to beginning of the loop just to be sure
					i = -1;
				}
			}

			// write centerline
			if (_channelLengthsOnly) {
				_asciiOut.write("Chan Length" + "\n");
			}
			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				centerline = _net.getCenterline(_net.getCenterlineName(i));
				if (_channelLengthsOnly) {
					_asciiOut.write(CsdpFunctions.formattedOutputString(_net.getCenterlineName(i), 5, true)
							+ centerline.getLengthFeet() + "\n");

				} else {
					if (_net.getCenterlineName(i).length() <= 0 || centerline.getNumXsects() <= 0
							|| centerline.getNumCenterlinePoints() <= 0) {
						System.out.println("not writing centerline");
					} else {
						// write cross-section lines
						for (int k = 0; k <= centerline.getNumXsects() - 1; k++) {
							line = "X1    ";
							line += _net.getCenterlineName(i) + "_";
							_asciiOut.write(line);
							line = null;
							xsect = centerline.getXsect(k);
							line = xsect.getDistAlongCenterlineFeet() + "    ";
							// if(k==0){
							// line += centerline.getLength()+" "+"\n";
							// }else{
							line += "\n";
							// }
							_asciiOut.write(line);
							line = null;

							for (int m = 0; m <= xsect.getNumPoints() - 1; m++) {
								xPoint = xsect.getXsectPoint(m);
								line = xPoint.getStationFeet() + " ";
								// line += xPoint.getElevation()+" ";
								line += xPoint.getElevationFeet() + "\n";
								_asciiOut.write(line);
							} // for m

							if (k == (centerline.getNumXsects() - 1)) {
								// the following is to make sure that the length
								// will be in the fourth column after importing
								// into
								// excel using an underscore delimiter
								line = "xxx_xxx_xxx_" + centerline.getLengthFeet() + "   ";
								_asciiOut.write(line);
							}

							line = null;
						} // for k
							// _asciiOut.newLine();
						_asciiOut.write("\n");
					} // else if centerline has a name
				}
			} // for i
			_asciiOut.write("END" + "\n");
			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
		return success;
	}// write

	/**
	 * Close ascii bathymetry data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _directory + _filename + "." + _filetype + ":"
					+ e.getMessage());
		} // catch
	}// close

}// class NetworkAsciiOutput
