package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes openWaterArea data to ascii file
 */
public class RectXSAsciiOutput extends RectXSOutput {
	/**
	 * The elevation that is used to calculate conveyance characteristics used
	 * to estimate equivalent rectangular cross-section dimensions. The
	 * assumption is that all network data are wrt NGVD.
	 */
	private static final double _rectElevNGVD = 0.0;
	private static final double _rectElevMLLW = -2.84;
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	Network _net = null;
	CsdpFrame _gui;
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };

	/**
	 * copies objects to class variables
	 */
	RectXSAsciiOutput(Network net, CsdpFrame gui) {
		_net = net;
		_gui = gui;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			_aOutFile = new FileWriter(_directory + _filename + "." + ASCII_TYPE);
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
	 * Calculate rect xs and write ascii RectXS data file. This method assumes
	 * that data in network are all NGVD. If there are no irregular
	 * cross-sections in a channel, rectangular dimensions will be used.
	 *
	 * This is how they are calculated: 1. The weighted average of cross-section
	 * areas is calculated. Areas are weighted by the portion of the channel
	 * that they represent. 2. The weighted average of cross-section widths is
	 * calculated the same way. 3. The hydraulic depth of the equivalent
	 * rectangular cross-section is calculated by dividing the average area by
	 * the average width.
	 */
	protected boolean write(DSMChannels dsmChannels, IrregularXsectsInp ixi, XsectsInp xi) {
		boolean success = false;
		Centerline centerline = null;
		String centerlineName = null;
		int numXsect = -Integer.MAX_VALUE;
		/**
		 * area of equivalent rectangular cross-section
		 */
		double rectArea = -Double.MAX_VALUE;
		/**
		 * width of equivalent rectangular cross-section
		 */
		double rectWidth = -Double.MAX_VALUE;
		/**
		 * wetted perimeter of equivalent rectangular cross-section
		 */
		double rectWetP = -Double.MAX_VALUE;
		/**
		 * bottom elevation of equivalent rectangular cross-section
		 */
		double rectBotelvMLLW = -Double.MAX_VALUE;
		/**
		 *
		 */
		double rectBotelv0NGVD = -Double.MAX_VALUE;
		/**
		 * weighted average of all areas in the current channel. Average is
		 * weighted by the proporation of the centerline that the cross-section
		 * represents.
		 */
		double averageArea = -Double.MAX_VALUE;
		/**
		 * Weighted average of all widths in the current channel. Average is
		 * weighted by the proportion of the centerline that the cross-section
		 * represents.
		 */
		double averageWidth = -Double.MAX_VALUE;
		double chanLength = -Double.MAX_VALUE;
		double averageHD = -Double.MAX_VALUE;

		_net.sortCenterlineNames();

		try {
			_asciiOut.write("Equivalent Rectangular cross-sections" + "\n");
			_asciiOut.write(CsdpFunctions.formattedOutputString("Channel# ", 8, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("lgth", 5, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("A(MLLW)", 11, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("T", 7, true));
			_asciiOut.write(CsdpFunctions.formattedOutputString("P", 8, true) + "\n");
			// _asciiOut.write(CsdpFunctions.formattedOutputString("Botelv-2.84",12)+"\n");
		} catch (IOException e) {
			System.out.println("ERROR in RectXSAsciiOutput.write: unable to write headers to output file");
		}

		// loop through all channels in the grid. get the centerline for
		// for channel from the network if there is one in the network.
		// If not, add a new centerline to the network.
		for (int i = 0; i <= dsmChannels.getNumChannels() - 1; i++) {
			centerlineName = dsmChannels.getChanNum(i);
			if (DEBUG)
				System.out.println("centerlineName = " + centerlineName);
			if (centerlineName == null) {
				System.out.println("ERROR in RectXSAsciiOutput.write: centerlineName is null");
			}
			if (centerlineName == null || centerlineName.length() <= 0) {
				System.out.println("centerlineName is null or blank. i, centerlineName=" + i + "," + centerlineName);
			}

			centerline = _net.getCenterline(centerlineName);
			if (centerline == null) {
				_net.addCenterline(centerlineName);
				centerline = _net.getCenterline(centerlineName);
				if (DEBUG)
					System.out.println("Making new centerline for name" + centerlineName);
			}

			// If centerline has no xsects, use rectangular
			if (centerline.getNumXsectsWithPoints() == 0 && centerline.getNumCopiedXsects() == 0) {
				centerline.addRectangularXsects(dsmChannels, xi);
				numXsect = centerline.getNumRectXsects();
				for (int j = 0; j <= centerline.getNumRectXsects() - 1; j++) {
					Xsect xs = centerline.getRectXsect(j);
				}
			} else {
				numXsect = centerline.getNumXsectsWithPoints() + centerline.getNumCopiedXsects();
			}

			// get actual channel length from channels.inp file
			try {
				chanLength = dsmChannels.getLength(centerlineName);
			} catch (NullPointerException e) {
				System.out.println("error getting length of centerline " + centerlineName);
				System.out.println("Number of irregular xsects = " + centerline.getNumXsectsWithPoints());
				System.out.println("Number of copied xsects = " + centerline.getNumCopiedXsects());
				System.out.println("Number of rectangular xsects=" + centerline.getNumRectXsects());
			}
			// not used?
			averageArea = centerline.getAverageAreaSqft(_rectElevMLLW, chanLength);
			averageWidth = centerline.getAverageWidthFeet(_rectElevMLLW, chanLength);

			averageHD = averageArea / averageWidth;

			rectArea = averageArea;
			rectWidth = averageWidth;

			rectWetP = (2.0f * averageHD) + averageWidth;

			try {
				if (numXsect > 0 && centerline.getMinimumElevationFeet() <= 0.0f) {
					if (DEBUG)
						System.out.println("centerlineName=" + centerlineName);
					_asciiOut.write(CsdpFunctions.formattedOutputString(centerlineName, 5, false));
					_asciiOut.write(CsdpFunctions.formattedOutputString(Math.round(chanLength), 9, false) + ".");
					_asciiOut.write(CsdpFunctions.formattedOutputString(Math.round(rectArea), 9, false) + ".");
					_asciiOut.write(CsdpFunctions.formattedOutputString(Math.round(rectWidth), 7, false) + ".");
					_asciiOut.write(CsdpFunctions.formattedOutputString(Math.round(rectWetP), 7, false) + "." + "\n");
					// if no minelev > 0
				} else if (numXsect > 0 && centerline.getMinimumElevationFeet() > 0.0f) {
					_asciiOut.write(CsdpFunctions.formattedOutputString(centerlineName, 12, true));
					_asciiOut.write("centerline contains bottom elevations above 0 NGVD" + "\n");
					System.out.println("centerline " + centerlineName + " has bottom elevations above 0 NGVD");
					System.out.println("bottom elevation=" + centerline.getMinimumElevationFeet());
				} else if (numXsect <= 0) {
					_asciiOut.write(CsdpFunctions.formattedOutputString(centerlineName, 12, true));
					_asciiOut.write("centerline has " + numXsect + " cross-sections" + "\n");
				}
			} catch (IOException e) {
				System.out.println(
						"Error in RectXSAsciiOutput.write: occurred while writing xsect results to output file");
			}
		} // for i(loop through all chan in grid)

		success = true;
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

	// /**
	// * Called if user wants to echo time series input in output file.
	// * Time series input includes date, stage, and flow information.
	// */
	// private void echoTimeSeriesInput(StationTimeSeriesData sData, int
	// numStations, int numDates){
	// if(DEBUG)System.out.println("echoing input. numStations,
	// numdates="+numStations+","+numDates);
	// String nl = "\n";

	// try{
	// _asciiOut.write("Program options" +nl);
	// _asciiOut.write("-----------------------------" +nl);
	// _asciiOut.write("echoTimeSeriesInput "+
	// _gui.getEchoTimeSeriesInput()+nl);
	// _asciiOut.write("echoXsectInput "+
	// _gui.getEchoXsectInput()+nl);
	// _asciiOut.write("echoToeDrainInput "+
	// _gui.getEchoToeDrainInput()+nl);
	// _asciiOut.write("printXsectResults "+
	// _gui.getPrintXsectResults()+nl);
	// _asciiOut.write("useFremontWeir "+
	// CsdpFunctions.getUseFremontWeir()+nl);
	// _asciiOut.write("useToeDrainRestriction "+
	// CsdpFunctions.getUseToeDrainRestriction()+nl);
	// _asciiOut.write("-----------------------------" +nl+nl);

	// _asciiOut.write("time series input" + nl);
	// _asciiOut.write("-------------------" + nl);
	// _asciiOut.write("Station Distance" + nl);
	// for(int i=0; i<=numStations-1; i++){
	// _asciiOut.write
	// (CsdpFunctions.formattedOutputString(sData.getStationName(i),10) +
	// CsdpFunctions.formattedOutputString(sData.getDistance(i),10));
	// _asciiOut.newLine();
	// }
	// _asciiOut.write("end" + nl + nl);
	// _asciiOut.write(CsdpFunctions.formattedOutputString("Date",10));
	// for(int i=0; i<=numStations-1; i++){
	// _asciiOut.write(CsdpFunctions.formattedOutputString(sData.getStationName(i),10));
	// }
	// _asciiOut.write("flow" + nl);

	// String date = null;
	// float distance = -Float.MAX_VALUE;
	// for(int i=0; i<=numDates-1; i++){
	// date = sData.getDate(i);
	// //write time series data
	// _asciiOut.write(CsdpFunctions.formattedOutputString(date,10));
	// for(int j=0; j<=numStations-1; j++){
	// distance = sData.getDistance(j);
	// _asciiOut.write(CsdpFunctions.formattedOutputString(sData.getElevationFromList(i,j),10));
	// }
	// _asciiOut.write(CsdpFunctions.formattedOutputString(sData.getFlow(i),10)
	// + nl);
	// }
	// _asciiOut.write("end" + nl + nl);
	// } catch(IOException e) {
	// System.out.println
	// ("Error ocurred while writing file " +_directory +
	// _filename + "."+ASCII_TYPE + e.getMessage());
	// } // catch()
	// }//echoInput

}// class RectXSAsciiOutput
