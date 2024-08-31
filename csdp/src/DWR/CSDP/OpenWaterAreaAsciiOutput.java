package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes openWaterArea data to ascii file
 */
public class OpenWaterAreaAsciiOutput extends OpenWaterAreaOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _owaNet = null;
	CsdpFrame _gui;
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };
	private StationTimeSeriesData _stationData = null;
	// private static final boolean ECHO_TIME_SERIES_INPUT = true;
	// private static final boolean ECHO_XSECT_INPUT = false;
	// private static final boolean PRINT_XSECT_RESULTS = true;
	/*
	 * the elevation difference used for calculating wetted area. 2 meters was
	 * specified for the Yolo Bypass project The program uses feet.
	 */
	private static final double _elevationDifference = CsdpFunctions.metersToFeet(2.0);
	// private static final double _elevationDifference = 6.0;

	/**
	 * assigns data storage object to class variable
	 */
	OpenWaterAreaAsciiOutput(Network owaNet, CsdpFrame gui) {
		_owaNet = owaNet;
		_gui = gui;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			// if((_directory.substring(_directory.length()-1,_directory.length())).
			// equals(File.separator) == false){
			// _directory += File.separator;
			// }
			_aOutFile = new FileWriter(_filename + "." + ASCII_TYPE);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory, Filename: " + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out.println("Error ocurred while opening file " + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * write ascii openWaterArea data file
	 */
	protected boolean write(StationTimeSeriesData sData, ToeDrainData tdData) {
		boolean success = false;
		int numDates = sData.getNumDates();
		int numStations = sData.getNumStations();

		// if(ECHO_TIME_SERIES_INPUT){
		if (CsdpFunctions.getEchoTimeSeriesInput()) {
			echoTimeSeriesInput(sData, numStations, numDates);
		}

		// if(ECHO_XSECT_INPUT){
		// all cross-section input--station, elevation, for each xs. Long.
		if (CsdpFunctions.getEchoXsectInput()) {
			echoCrossSectionInput(sData);
		} // if ECHO_INPUT

		// if(PRINT_XSECT_RESULTS){
		// prints values for each cross-section for each date--very long
		if (CsdpFunctions.getPrintXsectResults()) {
			success = writeXsectData(sData);
		}

		// print toe drain data-short
		if (CsdpFunctions.getEchoToeDrainInput()) {
			success = writeToeDrainInput(tdData);
		}

		success = writeReachData(sData, tdData);
		return success;
	}// write

	/**
	 * Close ascii bathymetry data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out
					.println("Error ocurred while closing file " + _filename + "." + _filetype + ":" + e.getMessage());
		} // catch
	}// close

	/**
	 * Called if user wants to echo time series input in output file. Time
	 * series input includes date, stage, and flow information.
	 */
	private void echoTimeSeriesInput(StationTimeSeriesData sData, int numStations, int numDates) {
		if (DEBUG)
			System.out.println("echoing input.  numStations, numdates=" + numStations + "," + numDates);
		String nl = "\n";

		try {
			_asciiOut.write("Program options" + nl);
			_asciiOut.write("-----------------------------" + nl);
			_asciiOut.write("echoTimeSeriesInput    " + CsdpFunctions.getEchoTimeSeriesInput() + nl);
			_asciiOut.write("echoXsectInput         " + CsdpFunctions.getEchoXsectInput() + nl);
			_asciiOut.write("echoToeDrainInput      " + CsdpFunctions.getEchoToeDrainInput() + nl);
			_asciiOut.write("printXsectResults      " + CsdpFunctions.getPrintXsectResults() + nl);
			_asciiOut.write("useFremontWeir         " + CsdpFunctions.getUseFremontWeir() + nl);
			_asciiOut.write("useToeDrainRestriction " + CsdpFunctions.getUseToeDrainRestriction() + nl);
			_asciiOut.write("-----------------------------" + nl + nl);

			_asciiOut.write("time series input" + nl);
			_asciiOut.write("-------------------" + nl);
			_asciiOut.write("Station   Distance" + nl);
			for (int i = 0; i <= numStations - 1; i++) {
				_asciiOut.write(CsdpFunctions.formattedOutputString(sData.getStationName(i), 10, true)
						+ CsdpFunctions.formattedOutputString(sData.getDistance(i), 10, true) + nl);
			}
			_asciiOut.write("end" + nl + nl);
			_asciiOut.write(CsdpFunctions.formattedOutputString("Date", 12, true));
			for (int i = 0; i <= numStations - 1; i++) {
				_asciiOut.write(CsdpFunctions.formattedOutputString(sData.getStationName(i), 12, true));
			}
			_asciiOut.write("flow" + nl);

			String date = null;
			double distance = -Double.MAX_VALUE;
			for (int i = 0; i <= numDates - 1; i++) {
				date = sData.getDate(i);
				// write time series data
				_asciiOut.write(CsdpFunctions.formattedOutputString(date, 12, true));
				for (int j = 0; j <= numStations - 1; j++) {
					distance = sData.getDistance(j);
					_asciiOut.write(CsdpFunctions.formattedOutputString(sData.getElevationFromList(i, j), 12, true));
				}
				_asciiOut.write(CsdpFunctions.formattedOutputString(sData.getFlow(i), 12, true) + nl);
			}
			_asciiOut.write("end" + nl + nl);
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
	}// echoInput

	/**
	 * Called if user wants output file to include all cross-section input.
	 */
	private void echoCrossSectionInput(StationTimeSeriesData sData) {
		Xsect xsect = null;
		XsectPoint xPoint = null;
		String line = null;
		Centerline centerline = null;
		try {
			_asciiOut.write("cross-section input" + "\n");
			_asciiOut.write("-------------------" + "\n");
			for (int i = 0; i <= _owaNet.getNumCenterlines() - 1; i++) {
				centerline = _owaNet.getCenterline(_owaNet.getCenterlineName(i));
				line = _owaNet.getCenterlineName(i);

				_asciiOut.write(line + "\n");
				line = null;
				for (int k = 0; k <= centerline.getNumXsects() - 1; k++) {
					xsect = centerline.getXsect(k);
					for (int m = 0; m <= xsect.getNumPoints() - 1; m++) {
						xPoint = xsect.getXsectPoint(m);
						line = xPoint.getStationFeet() + ",";
						line += xPoint.getElevationFeet();
						_asciiOut.write(line + "\n");
						line = null;
					} // for m
					line = null;
				} // for k
				_asciiOut.newLine();
			} // for i
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
	}// writeCrossSectionInput

	/**
	 * echo toe drain data(only one line for each cross-section that has a toe
	 * drain
	 */
	private boolean writeToeDrainInput(ToeDrainData tdData) {
		boolean success = false;
		String name = null;
		try {
			_asciiOut.newLine();
			_asciiOut.write("Toe Drain Input" + "\n");
			_asciiOut.write(CsdpFunctions.formattedOutputString("xsect", 10, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("station", 10, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("elevation", 10, true));
			_asciiOut.newLine();
			_asciiOut.write("------------------------------------------------------------------------------" + "\n");
			for (int i = 0; i <= tdData.getNumXsects() - 1; i++) {
				name = tdData.getXsectName(i);
				_asciiOut.write(CsdpFunctions.formattedOutputString(name, 10, true));
				_asciiOut.write(CsdpFunctions.formattedOutputString(tdData.getStationFeet(name), 10, true));
				_asciiOut.write(CsdpFunctions.formattedOutputString(tdData.getElevationFeet(name), 10, true));
				_asciiOut.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
		return success;
	}// write toe drain input

	/**
	 * writes out results of all calculations for every cross-section for every
	 * date. Produces a lot of output.
	 */
	private boolean writeXsectData(StationTimeSeriesData sData) {
		boolean success = false;
		try {
			_asciiOut.write(CsdpFunctions.formattedOutputString("distance", 9, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("width", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("area", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("hDepth", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("upperWetP", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("lowerWetP", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("elevation", 12, true));
			_asciiOut.newLine();
			_asciiOut.write("------------------------------------------------------------------------------" + "\n");

			String centerlineName = null;
			Centerline centerline = null;
			String line = null;
			Xsect xsect = null;
			String date = null;

			// Double distanceObject = null;
			double distance = -Double.MAX_VALUE;
			double elevation = -Double.MAX_VALUE;

			for (int dateIndex = 0;

					dateIndex <= sData.getNumDates() - 1; dateIndex++) {
				date = sData.getDate(dateIndex);
				_asciiOut.write("Date:  " + date + "\n");
				for (int i = 0; i <= _owaNet.getNumCenterlines() - 1; i++) {
					centerlineName = _owaNet.getCenterlineName(i);
					centerline = _owaNet.getCenterline(centerlineName);
					line = CsdpFunctions.formattedOutputString(centerlineName, 9, true);
					// distanceObject = new Double(centerlineName);
					// distance = distanceObject.intValue();
					distance = (int) (Double.parseDouble(centerlineName));
					elevation = sData.getElevation(dateIndex, distance);
					// xsDistance.put(i,distance);

					xsect = centerline.getXsect(0); // centerlines in owa data
													// only have 1 xsect

					// xsWidth.put(i,xsect.getWidthFeet(elevation));
					// xsArea.put(i,xsect.getAreaSqft(elevation));

					line += CsdpFunctions.formattedOutputString(xsect.getWidthFeet(elevation), 12, true)
							+ CsdpFunctions.formattedOutputString(xsect.getAreaSqft(elevation), 12, true)
							+ CsdpFunctions.formattedOutputString(xsect.getHydraulicDepthFeet(elevation), 12, true)
							+ CsdpFunctions.formattedOutputString(xsect.getWettedPerimeterFeet(elevation), 12, true)
							+ CsdpFunctions.formattedOutputString(
									xsect.getWettedPerimeterFeet(elevation - _elevationDifference), 12, true)
							+ CsdpFunctions.formattedOutputString(elevation, 12, true);

					// line +=
					// xsect.getWidthFeet(elevation)+xsect.getAreaSqft(elevation)+
					// xsect.getHydraulicDepthFeet(elevation)+
					// xsect.getWettedPerimeterFeet(elevation)+
					// xsect.getWettedPerimeterFeet(elevation-_elevationDifference);
					_asciiOut.write(line + "\n");
					line = null;
				} // for i
				_asciiOut.newLine();
				_asciiOut.newLine();
			} // for dateIndex
				// _owaNet.setIsUpdated(false);
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
		return success;
	}// writeXsectData

	/**
	 * writes data for entire reach (area, vol. etc. integrated over entire
	 * reach
	 */
	private boolean writeReachData(StationTimeSeriesData sData, ToeDrainData tdData) {
		boolean success = false;
		try {
			_asciiOut.write("wwhd = width weighted hydraulic depth" + "\n");
			_asciiOut.write("waisr = wetted area in specified range = 0 to " + _elevationDifference + "\n" + "\n");
			_asciiOut.write(CsdpFunctions.formattedOutputString("date", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("area", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("volume", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("wwhd", 12, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("waisr", 12, true));
			_asciiOut.write("residence time" + "\n");
			_asciiOut.write("--------------------------------------------------------------------------" + "\n");

			String centerlineName = null;
			Centerline centerline = null;
			String line = null;
			Xsect xsect = null;
			ResizableDoubleArray xsDistance = new ResizableDoubleArray();
			ResizableDoubleArray xsWidth = new ResizableDoubleArray();
			ResizableDoubleArray xsArea = new ResizableDoubleArray();
			ResizableDoubleArray xsWidthTimesHydraulicDepth = new ResizableDoubleArray();
			ResizableDoubleArray xsWettedPerimeterDifference = new ResizableDoubleArray();
			String date = null;
			double totalWettedAreaInSpecifiedRange = 0.0;

			for (int dateIndex = 0; dateIndex <= sData.getNumDates() - 1; dateIndex++) {
				date = sData.getDate(dateIndex);
				_asciiOut.write(CsdpFunctions.formattedOutputString(date, 12, true));
				// Double distanceObject = null;
				double distance = -Double.MAX_VALUE;
				double elevation = -Double.MAX_VALUE;
				double area = -Double.MAX_VALUE;
				double hydraulicDepth = -Double.MAX_VALUE;
				double width = -Double.MAX_VALUE;
				double wettedPerimeterUpper = -Double.MAX_VALUE;
				double wettedPerimeterLower = -Double.MAX_VALUE;
				boolean elevationFound = true;
				for (int i = 0; i <= _owaNet.getNumCenterlines() - 1; i++) {
					centerlineName = _owaNet.getCenterlineName(i);
					centerline = _owaNet.getCenterline(centerlineName);
					xsect = centerline.getXsect(0); // centerlines in owa data
													// only have 1 xsect
					line = centerlineName;
					// distanceObject = new Double(centerlineName);
					// distance = distanceObject.intValue();
					distance = (int) (Double.parseDouble(centerlineName));
					elevation = sData.getElevation(dateIndex, distance);

					if (elevation > -Double.MAX_VALUE) {

						if (DEBUG)
							System.out.println(
									"date, centerlineName, elevation=" + date + "," + centerlineName + "," + elevation);

						area = xsect.getAreaSqft(elevation);
						hydraulicDepth = xsect.getHydraulicDepthFeet(elevation);
						width = xsect.getWidthFeet(elevation);
						wettedPerimeterUpper = xsect.getWettedPerimeterFeet(elevation);
						wettedPerimeterLower = xsect.getWettedPerimeterFeet(elevation - _elevationDifference);

						if (wettedPerimeterUpper < wettedPerimeterLower) {
							System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!");
						}

						if (CsdpFunctions.getUseToeDrainRestriction()) {
							area = xsect.getAreaSqft(elevation, centerlineName, tdData);
							hydraulicDepth = xsect.getHydraulicDepthFeet(elevation, centerlineName, tdData);
							width = xsect.getWidthFeet(elevation, centerlineName, tdData);
							wettedPerimeterUpper = xsect.getWettedPerimeterFeet(elevation, centerlineName, tdData);
							wettedPerimeterLower = xsect.getWettedPerimeterFeet(elevation - _elevationDifference,
									centerlineName, tdData);

							if (wettedPerimeterUpper > 800000.0) {
								System.out.println(
										"wettedPerimeterUpper just became large when using toe drain restriction");
								System.out.println("xsect, elevation=" + centerlineName + "," + elevation);
							}
							if (wettedPerimeterLower > 800000.0) {
								System.out.println(
										"wettedPerimeterLower just became large when using toe drain restriction");
								System.out.println("xsect, elevation=" + centerlineName + "," + elevation);
							}
						} else {
							area = xsect.getAreaSqft(elevation);
							hydraulicDepth = xsect.getHydraulicDepthFeet(elevation);
							width = xsect.getWidthFeet(elevation);
							wettedPerimeterUpper = xsect.getWettedPerimeterFeet(elevation);
							wettedPerimeterLower = xsect.getWettedPerimeterFeet(elevation - _elevationDifference);

							if (wettedPerimeterUpper > 800000.0) {
								System.out.println(
										"wettedPerimeterUpper just became large when not using toe drain restriction");
								System.out.println("xsect, elevation=" + centerlineName + "," + elevation);
							}
							if (wettedPerimeterLower > 800000.0) {
								System.out.println(
										"wettedPerimeterLower just became large when not using toe drain restriction");
								System.out.println("xsect, elevation=" + centerlineName + "," + elevation);
							}

						}

						xsDistance.put(i, distance);
						xsWidth.put(i, width);
						xsArea.put(i, area);
						xsWidthTimesHydraulicDepth.put(i, width * hydraulicDepth);
						if (wettedPerimeterUpper > wettedPerimeterLower) {
							xsWettedPerimeterDifference.put(i, wettedPerimeterUpper - wettedPerimeterLower);
						} else if (wettedPerimeterUpper == wettedPerimeterLower) {
							xsWettedPerimeterDifference.put(i, 2.0f * _elevationDifference);
							if (DEBUG)
								System.out.println(
										"water surface above top. using 2*elevationDifference for wetted perimeter difference ");
						} else {
							System.out.println("ERROR:  wetted perimeter is decreasing wrt elevation!  Could");
							System.out.println("be a bug in the program!!!");
							System.out.println("pUpper, pLower=" + wettedPerimeterUpper + "," + wettedPerimeterLower);
							System.out.println("elevation, elevation-_elevationDifference=" + elevation + ","
									+ (elevation - _elevationDifference));
							System.out.println("centerline: " + centerlineName);
							System.out.println("calculating both p values again: upper,lower=");
							System.out.println(xsect.getWettedPerimeterFeet(elevation) + "," + xsect
									.getWettedPerimeterFeet(elevation - _elevationDifference, centerlineName, tdData));
						} // if
					} else {
						if (DEBUG)
							System.out.println("no elevations found for " + date);
						elevationFound = false;
					}
				} // for i

				double distance1 = 0.0;
				double distance2 = 0.0;
				double width1 = 0.0;
				double width2 = 0.0;
				double area1 = 0.0;
				double area2 = 0.0;
				double wettedPerimeterDifference1 = 0.0;
				double wettedPerimeterDifference2 = 0.0;
				double totalArea = 0.0;
				double totalVolume = 0.0;
				double totalWidthTimesHydraulicDepth = 0.0;
				double widthWeightedHydraulicDepth = -Double.MAX_VALUE;
				double totalWidth = 0.0;
				boolean wettedAreaOk = true;
				if (elevationFound) {
					// calculate total area, volume
					// this assumes that the reach begins at the first
					// cross-section
					// and ends at the last.
					totalWettedAreaInSpecifiedRange = 0.0;
					for (int i = 0; i <= _owaNet.getNumCenterlines() - 2; i++) {
						distance1 = xsDistance.get(i);
						distance2 = xsDistance.get(i + 1);
						width1 = xsWidth.get(i);
						width2 = xsWidth.get(i + 1);
						area1 = xsArea.get(i);
						area2 = xsArea.get(i + 1);
						wettedPerimeterDifference1 = xsWettedPerimeterDifference.get(i);
						wettedPerimeterDifference2 = xsWettedPerimeterDifference.get(i + 1);
						totalArea += Math.abs((distance2 - distance1) * 0.5 * (width1 + width2));
						totalVolume += Math.abs((distance2 - distance1) * 0.5 * (area2 + area1));
						totalWettedAreaInSpecifiedRange += Math.abs((distance2 - distance1) * 0.5
								* (wettedPerimeterDifference1 + wettedPerimeterDifference2));
						if (wettedPerimeterDifference1 < 0.0 || wettedPerimeterDifference2 < 0.0) {
							wettedAreaOk = false;
						}
					}
					if (wettedAreaOk == false) {
						totalWettedAreaInSpecifiedRange = -Double.MAX_VALUE;
					}
					for (int i = 0; i <= _owaNet.getNumCenterlines() - 1; i++) {
						totalWidthTimesHydraulicDepth += xsWidthTimesHydraulicDepth.get(i);
						totalWidth += xsWidth.get(i);
					}
					// if(totalWidth == 0.0){
					// System.out.println("DIVISION BY ZERO ERROR in width
					// weighted hydraulic depth calculation!");
					// JOptionPane.showOptionDialog
					// (null, "ERROR! totalWidth in OpenWaterAreaAsciiOutput is
					// zero,"+"\n"+
					// "resulting in division by zero error."+"\n"+
					// "date,distance = "+date+","+distance, "ERROR! OPERATION
					// FAILED",
					// JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					// null,
					// _options, _options[0]);
					// }

					widthWeightedHydraulicDepth = totalWidthTimesHydraulicDepth / totalWidth;
					// _asciiOut.write("Results");

					System.out.println("date, totalArea, volume=" + date + "," + totalArea + "," + totalVolume);

					_asciiOut.write(CsdpFunctions.formattedOutputString(totalArea, 12, true));
					_asciiOut.write(CsdpFunctions.formattedOutputString(totalVolume, 12, true));
				} else {
					totalWidth = 0.0;
					widthWeightedHydraulicDepth = -Double.MAX_VALUE;
					totalWettedAreaInSpecifiedRange = 0.0;
					totalArea = 0.0;
					totalVolume = 0.0;
				}
				if (totalWidth <= 0.0) {
					_asciiOut.write(CsdpFunctions.formattedOutputString("reach is dry", 12, true));
				} else {
					_asciiOut.write(CsdpFunctions.formattedOutputString(widthWeightedHydraulicDepth, 12, true));
				}
				_asciiOut.write(CsdpFunctions.formattedOutputString(totalWettedAreaInSpecifiedRange, 12, true));
				if (DEBUG)
					System.out.println("flow, volume=" + sData.getFlow(dateIndex) + "," + totalVolume);
				_asciiOut.write(
						CsdpFunctions.formattedOutputString(totalVolume / sData.getFlow(dateIndex), 12, true) + "\n");
			} // for dateIndex

			// _owaNet.setIsUpdated(false);
		} catch (IOException e) {
			System.out.println("Error ocurred while writing file " + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
		return success;
	}// writeReachData

}// class OpenWaterAreaAsciiOutput
