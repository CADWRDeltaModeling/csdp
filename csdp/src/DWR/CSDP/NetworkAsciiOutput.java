package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes network data to ascii file
 */
public class NetworkAsciiOutput extends NetworkOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _net = null;

	/**
	 * assigns data storage object to class variable
	 */
	NetworkAsciiOutput(Network net) {
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
			if (get3DOutput()) {
				_aOutFile = new FileWriter(_directory + _filename + "." + EXPORT_TYPE_3D);
			} else {
				_aOutFile = new FileWriter(_directory + _filename + "." + ASCII_TYPE);
			}
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
	 * write ascii network data file
	 */
	protected boolean write() {
		boolean success = false;
		String line = null;
		Centerline centerline;
		CenterlinePoint cPoint;
		Xsect xsect;
		XsectPoint xPoint;
		String metadata;
		CsdpFileMetadata nMeta = CsdpFunctions.getNetworkMetadata();

		try {
			// write metadata
			String nl = Integer.toString(_net.getNumCenterlines());
			if(_channelsToExport != null) {
				nl = Integer.toString(_channelsToExport.size());
			}
			_asciiOut.write(";HorizontalDatum:  " + nMeta.getHDatumString());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalZone:   " + nMeta.getHZone());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalUnits:  " + nMeta.getHUnitsString());
			_asciiOut.newLine();
			_asciiOut.write(";VerticalDatum:    " + nMeta.getVDatumString());
			_asciiOut.newLine();
			_asciiOut.write(";VerticalUnits:    " + nMeta.getVUnitsString());
			_asciiOut.newLine();
			_asciiOut.write(";Filetype:          network");
			_asciiOut.newLine();
			_asciiOut.write(";NumElements:      " + nl);
			_asciiOut.newLine();
			// write data
			String versionLine = null;
			Integer numLines = null;

			_net.sortCenterlineNames();

			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				if (_net.getCenterlineName(i).length() <= 0) {
					System.out.println("decrementing numCenterlines because nameless centerline found");
					_net.removeCenterline(_net.getCenterlineName(i));
					// go back to beginning of the loop just to be sure
					i = -1;
				}
			}
			// don't need version any more
			// versionLine = "Version_"+CsdpFunctions.getVersion() + " ";
			// _asciiOut.write(versionLine);
			// _asciiOut.write(nl+"\n");
			// write centerline
			int numCenterlinesWritten = 0;
			for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
				String centerlineName = _net.getCenterlineName(i);
				if(_channelsToExport == null || _channelsToExport.contains(centerlineName)) {
					centerline = _net.getCenterline(centerlineName);
					line = "  " + "\"" + centerlineName + "\"" + " ";
					line += centerline.getNumCenterlinePoints() + "\n" + "     ";
					if (centerlineName.length() <= 0) {
						System.out.println("not writing nameless centerline");
					} else {
						for (int j = 0; j <= centerline.getNumCenterlinePoints() - 1; j++) {
							cPoint = centerline.getCenterlinePoint(j);
							line += cPoint.getXFeet() + ",";
							// line+=cPoint.getY()+" ";
							line += cPoint.getYFeet() + "\n" + "     ";
						} // for j
						line += centerline.getNumXsects();
						_asciiOut.write(line + "\n");
						// _asciiOut.newLine();
						// write cross-section lines
						for (int k = 0; k <= centerline.getNumXsects() - 1; k++) {
							xsect = centerline.getXsect(k);
							line = "    " + "\"" + "\"" + " " + xsect.getNumPoints() + "\n" + "       ";
							for (int m = 0; m <= xsect.getNumPoints() - 1; m++) {
								xPoint = xsect.getXsectPoint(m);
								if (get3DOutput()) {
									double[] threeDCoord = _net.find3DXsectPointCoord(centerlineName, k, m);
									line += threeDCoord[CsdpFunctions.xIndex] + ",";
									line += threeDCoord[CsdpFunctions.yIndex] + ",";
									line += xPoint.getElevationFeet() + "\n" + "       ";
								} else {
									line += xPoint.getStationFeet() + ",";
									// line += xPoint.getElevation()+" ";
									line += xPoint.getElevationFeet() + "\n" + "       ";
								}
							} // for m
							line += xsect.getDistAlongCenterlineFeet() + " ";
							line += xsect.getXsectLineLengthFeet() + " ";
	
							// if there is no metadata for the cross-section, just
							// write a
							// set of empty quotes
							metadata = xsect.getMetadata();
							line += "\n" + "       " + "\"";
							if (metadata != null) {
								// if there are newline characters, replace with \n
								for (int mIndex = 0; mIndex <= metadata.length() - 1; mIndex++) {
									char metadataChar = metadata.charAt(mIndex);
									if (metadataChar == '\r' || metadataChar == '\n') {
										line += " *nl* ";
										// line += " \n ";
									} else {
										line += metadataChar;
									}
								}
							}
							line += "\"";
	
							_asciiOut.write(line + "\n");
							line = null;
						} // for k
							// _asciiOut.newLine();
						_asciiOut.write("\n");
					} // else if centerline has a name
					numCenterlinesWritten ++;
				}//if: only write channel if _channelsToExport is null or it's not null and channel number is contained in array
			} // for i

			System.out.println("numCenterlinesWritten="+numCenterlinesWritten);
			_net.setIsUpdated(false);
			success = true;
			//make the array null again, so the static values will be persist.
			_channelsToExport = null;
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
