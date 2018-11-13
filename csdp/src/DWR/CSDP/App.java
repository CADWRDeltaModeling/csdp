/*
    Copyright (C) 1998 State of California, Department of Water
    Resources.

    This program is licensed to you under the terms of the GNU General
    Public License, version 2, as published by the Free Software
    Foundation.

    You should have received a copy of the GNU General Public License
    along with this program; if not, contact Dr. Francis Chung, below,
    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
    02139, USA.

    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.

    For more information, contact:

    Dr. Francis Chung
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA  95814
    916-653-5601
    chung@water.ca.gov

    or see our home page: http://wwwdelmod.water.ca.gov/
*/
package DWR.CSDP;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import DWR.CSDP.dialog.MessageDialog;

/**
 * Main application class
 *
 * @author
 * @version $Id: App.java,v 1.6 2005/04/07 22:10:41 btom Exp $
 */
public class App {

	public App() {
	}

	/**
	 * Open ascii or binary bathymetry data file and store data in arrays
	 */
	public BathymetryPlot bReadStore(JFrame gui, String directory, String filename, String filetype) {
		BathymetryInput binput = null;
		_gui = (CsdpFrame) gui;
		boolean bWrite = true;
		int numLines = 0;
		_gui.setCursor(CsdpFunctions._waitCursor);

		// read bathymetry file and store in BathymetryPlot object

		// if(DEBUG)
		System.out.println("about to read file.  directory, filename, filetype=");
		// if(DEBUG)
		System.out.println(directory + "," + filename + "," + filetype);

		binput = BathymetryInput.getInstance(_gui, directory, filename + "." + filetype);

		System.out.println("binput=" + binput);

		_bathymetryData = null;
		_bathymetryData = binput.readData();
		_bathymetryData.sortYearIndices();
		if (filetype.equals("prn"))
			_bathymetryData.sortBathymetryData();

		// plot data on canvas (BathymetryPlot)
		_bathymetryPlot = setBathymetryPlotter();
		updateNetworkPlotter();
		_gui.setPlotObject(_bathymetryPlot);
		_gui.updateColorLegend();

		_gui.getPlanViewCanvas(0).setPlotter(_bathymetryPlot, _gui._dim);
		_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);

		if (_gui.getPlanViewCanvas(0)._networkPlotter != null) {
			if (DEBUG)
				System.out.println("should update network...");
			_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			_net.convertToBathymetryDatum();
		}
		if (_gui.getPlanViewCanvas(0)._landmarkPlotter != null) {
			_landmark.convertToBathymetryDatum();
		}

		if (_gui.getPlanViewCanvas(0)._dlgPlotter != null) {
			// _gui.getPlanViewCanvas(0).setUpdateCanvas(true);
			_gui.getPlanViewCanvas(0).setUpdateDigitalLineGraph(true);
			_dlg.convertToBathymetryDatum();
		}

		// tells canvas to zoom out and redraw itself.
		_gui.getPlanViewCanvas(0).zoomFit();
		_gui.setCursor(CsdpFunctions._defaultCursor);
		if (DEBUG)
			displayData();
		_gui.enableAfterBathymetry();
		_gui.updateBathymetryFilename(filename + "." + filetype);
		_gui.updateMetadataDisplay(CsdpFunctions.getBathymetryMetadata());

		return _bathymetryPlot;
	}// bReadStore

	/**
	 * Open property data file and store data in CsdpFunctions
	 */
	public void pReadStore(JFrame gui, String directory, String filename, String filetype) {
		parseFilename(filename);
		CsdpFunctions._propertiesFilename = _filename;
		CsdpFunctions._propertiesFiletype = _filetype;
		_gui = (CsdpFrame) gui;
		boolean bWrite = true;
		int numLines = 0;
		_gui.setCursor(CsdpFunctions._waitCursor);

		_pinput = PropertiesInput.getInstance(_gui, directory, filename + "." + filetype);
		_pinput.readData();

		_gui.updateColorLegend();
		_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
		_gui.updatePropertiesFilename(_filename + "." + _filetype);
		// removed for conversion to swing
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
		_gui.setCursor(CsdpFunctions._defaultCursor);
	}// pReadStore

	public boolean compareNetworks(JFrame gui, String nFilename1, Network net1, String nFilename2, Network net2,
			String outputFilename, String outputFiletype, String outputDirectory) {
		_gui = (CsdpFrame) gui;
		boolean success = false;

		NetworkCompareOutput ncOutput = NetworkCompareOutput.getInstance(nFilename1, net1, nFilename2, net2,
				outputDirectory, outputFilename + outputFiletype, _gui);
		success = ncOutput.writeData();

		return success;
	}// compareNetworks

	/**
	 * Calculates equivalent rectangular cross-sections for every channel in the
	 * network
	 */
	public void calcRect(CsdpFrame gui, Network net, String outputFilename, String outputDirectory, String dsmFilename,
			String dsmDirectory, String irregXsectsFilename, String irregXsectsDirectory, String xsectsInpFilename,
			String xsectsInpDirectory) {
		_gui = gui;
		DSMChannels dc = null;
		IrregularXsectsInp ixi = null;
		XsectsInp xi = null;

		DSMChannelsInput chanInput = DSMChannelsInput.getInstance(dsmDirectory, dsmFilename);
		dc = chanInput.readData();

		IrregularXsectsInpInput ixInput = IrregularXsectsInpInput.getInstance(irregXsectsDirectory,
				irregXsectsFilename);
		ixi = ixInput.readData();
		XsectsInpInput xiInput = XsectsInpInput.getInstance(xsectsInpDirectory, xsectsInpFilename);
		xi = xiInput.readData();

		for (int i = 0; i <= ixi.getNumChan() - 1; i++) {
			String chanNum = ixi.getChanNum(i);
			IrregularXsectsInp.IXIChan ixiChan = ixi.getChan(chanNum);
			Centerline centerline = _net.getCenterline(chanNum);
			if (centerline == null) {
				_net.addCenterline(chanNum);
				centerline = _net.getCenterline(chanNum);
			}
			if (DEBUG)
				System.out.println("about to get ixiLine for chan " + chanNum);
			if (DEBUG)
				System.out.println("centerline for the chan =" + centerline);

			for (int j = 0; j <= ixiChan.getNumLines() - 1; j++) {
				IrregularXsectsInp.IXILine ixiLine = ixiChan.getLine(j);
				if (DEBUG) {
					System.out.println("ixiLine = " + ixiLine);
					System.out.println("distance = " + ixiLine.getDistance());
					System.out.println("filename = " + ixiLine.getFilename());
				}
				if (ixiLine.getFilename() != null) {
					if (DEBUG)
						System.out.println("ixiLine=" + ixiLine);
					double distance = ixiLine.getDistance();
					// filename includes complete path
					String filename = ixiLine.getFilename();

					XsectInput xInput = XsectInput.getInstance(filename);
					Xsect xsect = xInput.readData();
					xsect.putDistAlongCenterlineFeet(distance);
					if (DEBUG)
						System.out.println("set xsect dist=" + xsect.getDistAlongCenterlineFeet());
					if (DEBUG)
						System.out.println(
								"about to add xsect to centerline. xsect, centerline=" + xsect + "," + centerline);
					centerline.addCopiedXsect(xsect);
				}
			}
		}

		RectXSOutput rxOutput = RectXSOutput.getInstance(outputDirectory, outputFilename, net, _gui);
		rxOutput.writeData(dc, ixi, xi);

	}// calcRect

	/**
	 * Open open water area data file and calculate
	 */
	public Network openWaterAreaReadCalculate(JFrame gui, String filename, String filetype, String outputFilename,
			String stationFilename, String toeDrainFilename) {

		if (DEBUG)
			System.out.println("calculate called");
		Network owaNet = null;
		StationTimeSeriesData stationData = null;
		ToeDrainData toeDrainData = null;
		CsdpFunctions.setOpenWaterAreaFilename(filename);
		CsdpFunctions.setOpenWaterAreaFiletype(filetype);
		_gui = (CsdpFrame) gui;
		// boolean bWrite = true;
		int numLines = 0;
		_gui.setCursor(CsdpFunctions._waitCursor);

		// read time series data(flow and stage at different locations for each
		// date)
		OpenWaterAreaInput owaInput = OpenWaterAreaInput.getInstance(filename, filetype, _gui);
		owaNet = owaInput.readData();

		if (DEBUG)
			System.out.println("About to read time series file.  directory, filename=" + stationFilename);

		// read cross-section info
		OpenWaterAreaStationInput owaSInput = OpenWaterAreaStationInput.getInstance(stationFilename, _gui);
		stationData = owaSInput.readData();

		// read toe drain info
		if (CsdpFunctions.getUseToeDrainRestriction()) {
			OpenWaterAreaToeDrainInput owaTDInput = OpenWaterAreaToeDrainInput.getInstance(toeDrainFilename);

			toeDrainData = owaTDInput.readData();
		}

		// write output
		OpenWaterAreaOutput owaOutput = OpenWaterAreaOutput.getInstance(outputFilename, owaNet, _gui);
		owaOutput.writeData(stationData, toeDrainData);
		if (DEBUG)
			toeDrainData.printAll();

		_gui.setCursor(CsdpFunctions._defaultCursor);

		return owaNet;
	}// openWaterAreaReadCalculate

	/**
	 * Display all bathymetry data on screen
	 */
	public void displayData() {
		_bathymetryData.test();
	}// displayData

	/**
	 * Saves zoomed bathymetry data (currently visible) as ascii(prn) or
	 * binary(cdp) file
	 */
	public boolean fSaveZoomed(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		double[] plotBoundaries = _bathymetryPlot.getCurrentZoomState().getPlotBoundaries();

		if (_filetype.equals(ASCII_TYPE)) {
			BathymetryOutput aoutput = BathymetryOutput.getInstance(directory, _filename, ASCII_TYPE, _bathymetryData);
			success = aoutput.writeData(plotBoundaries);
		} else if (_filetype.equals(BINARY_TYPE)) {
			BathymetryOutput boutput = BathymetryOutput.getInstance(directory, _filename, BINARY_TYPE, _bathymetryData);
			success = boutput.writeData(plotBoundaries);
		} else {
			System.out.println("filetype not defined for extension " + _filetype);
		}
		return success;
	} // fSaveZoomed

	/**
	 * Saves bathymetry data as ascii(prn) or binary(cdp) file
	 */
	public boolean fSave(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);

		if (DEBUG)
			System.out.println("filename=" + filename);

		if (_filetype.equals(ASCII_TYPE)) {
			BathymetryOutput aoutput = BathymetryOutput.getInstance(directory, _filename, ASCII_TYPE, _bathymetryData);
			success = aoutput.writeData();
			if (DEBUG)
				System.out.println("Done writing ascii bathymetry data file");
		} else if (_filetype.equals(BINARY_TYPE)) {
			BathymetryOutput boutput = BathymetryOutput.getInstance(directory, _filename, BINARY_TYPE, _bathymetryData);
			success = boutput.writeData();
			if (DEBUG)
				System.out.println("Done writing binary bathymetry data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		return success;
	} // fSave

	/**
	 * Saves bathymetry data as ascii(prn) in NAVD88 datum
	 */
	public boolean fSaveAsNAVD88(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);

		if (DEBUG)
			System.out.println("filename=" + filename);

		if (_filetype.equals(ASCII_TYPE)) {
			BathymetryOutput aoutput = BathymetryOutput.getInstanceForNAVD88(directory, _filename, ASCII_TYPE,
					_bathymetryData);
			success = aoutput.writeData();
			if (DEBUG)
				System.out.println("Done writing ascii bathymetry data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		return success;
	} // fSaveAsNAVD88

	/**
	 * write properties file
	 */
	public boolean pSaveAs(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._propertiesFilename = _filename;
		CsdpFunctions._propertiesFiletype = _filetype;

		if (DEBUG)
			System.out.println("propertiesFilename, propertiesFiletype=" + CsdpFunctions._propertiesFilename + ","
					+ CsdpFunctions._propertiesFiletype);

		PropertiesOutput poutput = PropertiesOutput.getInstance(_gui, directory, CsdpFunctions._propertiesFilename,
				CsdpFunctions._propertiesFiletype);
		success = poutput.writeData();
		if (DEBUG)
			System.out.println("Done writing properties file " + CsdpFunctions._propertiesFilename + "."
					+ CsdpFunctions._propertiesFiletype);
		_gui.updatePropertiesFilename(_filename + "." + _filetype);
		return success;
	}// pSaveAs

	/**
	 * save properties file. return true if saved. If user entered bad extension
	 * (other than .cdn) save it anyway.
	 */
	public boolean pSave() {
		boolean success = false;
		String directory = CsdpFunctions.getPropertiesDirectory().getPath();
		if (CsdpFunctions._propertiesFilename != null) {
			if (CsdpFunctions._propertiesFiletype == null)
				CsdpFunctions._propertiesFiletype = PROPERTIES_TYPE;
			PropertiesOutput poutput = PropertiesOutput.getInstance(_gui, directory, CsdpFunctions._propertiesFilename,
					CsdpFunctions._propertiesFiletype);
			success = poutput.writeData();
			if (DEBUG)
				System.out.println("Done writing properties file " + CsdpFunctions._propertiesFilename + "."
						+ CsdpFunctions._propertiesFiletype);
		}
		return success;
	}// pSave

	/**
	 * return instance of plotter object that is used by this class
	 */
	public BathymetryPlot setBathymetryPlotter() {
		_bathymetryPlot = new BathymetryPlot(_gui, _bathymetryData, this);
		return _bathymetryPlot;
	}// setBathymetryPlotter

	/**
	 * return instance of network plotter object that is used by this class
	 */
	public NetworkPlot setNetworkPlotter() {
		_nPlot = new NetworkPlot(_gui, _bathymetryData, this);
		return _nPlot;
	}// setNetworkPlotter

	/**
	 * updates BathymetryData object in NetworkPlot
	 */
	public void updateNetworkPlotter() {
		if (_nPlot != null) {
			_nPlot.setBathymetryData(_bathymetryData);
		}
	}// updateNetworkPlotter

	/**
	 * Return instance of landmark plotter object that is used by this class
	 */
	public LandmarkPlot setLandmarkPlotter() {
		_lPlot = new LandmarkPlot(_gui, _bathymetryData, this);
		return _lPlot;
	}// setLandmarkPlotter

	/**
	 * Return instance of dlg plotter object that is used by this class
	 */
	public DigitalLineGraphPlot setDigitalLineGraphPlotter() {
		_dlgPlot = new DigitalLineGraphPlot(_gui, _bathymetryData, this);
		return _dlgPlot;
	}// setDLGPlotter

	/**
	 * read network data file
	 */
	public Network nReadStore(JFrame gui, String directory, String filename) {
		_gui = (CsdpFrame) gui;
		parseFilename(filename);
		CsdpFunctions._networkFilename = _filename;
		CsdpFunctions._networkFiletype = _filetype;
		NetworkInput ninput = NetworkInput.getInstance(_gui, directory, _filename, _filetype);
		_net = ninput.readData();
		if (DEBUG)
			System.out.println("Done reading ascii network data file");
		_gui.updateNetworkFilename(_filename + "." + _filetype);

		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
		return _net;
	}// nReadStore

	/**
	 * remove network file from memory and display
	 */
	public void clearNetwork() {
		_net = null;
		_gui.setNetwork(null);
		_gui.updateNetworkFilename(null);
		_gui.disableWhenNetworkCleared();
		_gui.getPlanViewCanvas(0).setNetworkPlotter(null);
		_gui.getPlanViewCanvas(0).setUpdateNetwork(false);
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
	}

	/*
	 * Remove channel outlines from memory and display
	 */
	public void clearChannelOutlines() {
		_dlg = null;
		_gui.setDigitalLineGraph(null);
		_gui.updateDigitalLineGraphFilename(null);
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
	}
	
	/*
	 * Create new landmark file (when landmark not loaded and user wants to
	 * create landmarks)
	 */
	public Landmark lCreate(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions._landmarkFilename = _filename;
		CsdpFunctions._landmarkFiletype = _filetype;

		// create landmark object
		_landmark = new Landmark(_gui);
		CsdpFileMetadata landmarkMetadata = (CsdpFileMetadata) ((CsdpFunctions.getBathymetryMetadata()).clone());
		CsdpFunctions.setLandmarkMetadata(landmarkMetadata);
		if (_filetype.equals(LANDMARK_TYPE)) {
			LandmarkOutput loutput = LandmarkOutput.getInstance(directory, _filename, _filetype, _landmark);
		}
		return _landmark;
	}// lCreate

	/**
	 * read landmark data file
	 */
	public Landmark lReadStore(String directory, String filename) {
		parseFilename(filename);

		System.out.println("lreadstore: filename, filetype=" + _filename + "," + _filetype);

		CsdpFunctions._landmarkFilename = _filename;
		CsdpFunctions._landmarkFiletype = _filetype;
		if (_filetype.equals(LANDMARK_TYPE)) {
			LandmarkInput linput = LandmarkInput.getInstance(_gui, directory, _filename + "." + _filetype);
			_landmark = linput.readData();
			if (DEBUG)
				System.out.println("Done reading ascii landmark data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		_gui.updateLandmarkFilename(_filename + "." + _filetype);
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
		return _landmark;
	}// lReadStore

	/*
	 * removes landmark from memory and from display.
	 */
	public void clearLandmarks() {
		_landmark = null;
		_gui.updateLandmarkFilename(null);
		_gui.setLandmark(null);
		_gui.getPlanViewCanvas(0).setLandmarkPlotter(null);
		_gui.getPlanViewCanvas(0).setUpdateLandmark(false);
		_gui.disableWhenLandmarkCleared();
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
	}

	/**
	 * read Digital Line Graph data file
	 */
	public DigitalLineGraph digitalLineGraphReadStore(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions.setDigitalLineGraphFilename(_filename);
		CsdpFunctions.setDigitalLineGraphFiletype(_filetype);
		if (_filetype.equals(DLG_TYPE)) {
			DigitalLineGraphInput dlgInput = DigitalLineGraphInput.getInstance(_gui, directory,
					_filename + "." + _filetype);
			_dlg = dlgInput.readData();
			if (DEBUG)
				System.out.println("Done reading ascii Digital Line Graph data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		_gui.updateDigitalLineGraphFilename(_filename + "." + _filetype);
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
		return _dlg;
	}// dlgReadStore

	/**
	 * read channelsInp data file
	 */
	public DSMChannels chanReadStore(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions._DSMChannelsFilename = _filename;
		CsdpFunctions._DSMChannelsFiletype = _filetype;
		if (_filetype.equals(DSMChannels_TYPE)) {
			DSMChannelsInput chanInput = DSMChannelsInput.getInstance(directory, _filename + "." + _filetype);
			_DSMChannels = chanInput.readData();
			if (DEBUG)
				System.out.println("Done reading ascii DSMChannels data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		return _DSMChannels;
	}// chanReadStore

	/**
	 * save network file. return true if saved. If user entered bad extension
	 * (other than .cdn) save it anyway.
	 */
	public boolean nSave() {
		boolean success = false;
		String directory = CsdpFunctions.getNetworkDirectory().getPath();
		if (CsdpFunctions._networkFilename != null) {
			if (CsdpFunctions._networkFiletype == null)
				CsdpFunctions._networkFiletype = NETWORK_TYPE;
			_net.sortCenterlineNames();
			NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions._networkFilename,
					CsdpFunctions._networkFiletype, _net, null);
			success = noutput.writeData();
			if (DEBUG)
				System.out.println("Done writing network file " + CsdpFunctions._networkFilename + "."
						+ CsdpFunctions._networkFiletype);
		}
		return success;
	}// nSave

	/**
	 * write network file
	 */
	public boolean nSaveAs(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._networkFilename = _filename;
		CsdpFunctions._networkFiletype = _filetype;

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions._networkFilename + ","
					+ CsdpFunctions._networkFiletype);

		_net.sortCenterlineNames();
		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions._networkFilename,
				CsdpFunctions._networkFiletype, _net, null);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file " + CsdpFunctions._networkFilename + "."
					+ CsdpFunctions._networkFiletype);
		_gui.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nSaveAs

	public boolean nExportToWKT(Network net, String directory, String filename) {
		AsciiFileWriter afw = new AsciiFileWriter(directory+File.separator+filename);
		afw.writeLine("id;wkt");
		boolean success = true;
		try{
			int numCenterlines = net.getNumCenterlines();
			for(int i=0; i<numCenterlines; i++) {
				String centerlineName = net.getCenterlineName(i);
				Centerline centerline = (Centerline) net.getCenterline(centerlineName);
				String lineToWrite = centerlineName+";LINESTRING(";
				int numPoints = centerline.getNumCenterlinePoints();
				for(int j=0; j<numPoints; j++) {
					if(j>0) {
						lineToWrite += ",";
					}
					CenterlinePoint cp = centerline.getCenterlinePoint(j);
					lineToWrite+=cp._x+" "+cp._y;
				}
				lineToWrite += ")"; 
				afw.writeLine(lineToWrite);
			}
			afw.close();
			success = true;
		}catch(Exception exception) {
			success = false;
		}
		return success;
	}//nExportToWKT

	public boolean lExportToWKT(Landmark landmark, String directory, String filename) {
		AsciiFileWriter afw = new AsciiFileWriter(directory+File.separator+filename);
		afw.writeLine("id;wkt");
		boolean success = true;
		Enumeration<String> e = landmark.getLandmarkNames();
		landmark.getNumLandmarks();
		try {
			while(e.hasMoreElements()) {
				String landmarkName = e.nextElement();
				double easting = landmark.getXMeters(landmarkName);
				double northing = landmark.getYMeters(landmarkName);
				afw.writeLine(landmarkName+";POINT("+easting+" "+northing+")");
			}
			afw.close();	
			success = true;
		}catch(Exception exception) {
			success = false;
		}
		return success;
	}//lExportToWKT
	
	/**
	 * write network file
	 */
	public boolean nSaveSpecifiedChannelsAs(String directory, String filename, HashSet<String> channelsToSave) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._networkFilename = _filename;
		CsdpFunctions._networkFiletype = _filetype;

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions._networkFilename + ","
					+ CsdpFunctions._networkFiletype);

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions._networkFilename,
				CsdpFunctions._networkFiletype, _net, channelsToSave);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file " + CsdpFunctions._networkFilename + "."
					+ CsdpFunctions._networkFiletype);
		_gui.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nSaveAs

	/**
	 * export network file to station/elevation format
	 */
	public boolean nExportToSEFormat(String directory, String filename, boolean channelLengthsOnly) {

		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._networkFilename = _filename;
		CsdpFunctions._networkFiletype = _filetype;

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions._networkFilename + ","
					+ CsdpFunctions._networkFiletype);

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions._networkFilename,
				CsdpFunctions._networkFiletype, _net, null);
		noutput.setChannelLengthsOnly(channelLengthsOnly);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file in SE format" + CsdpFunctions._networkFilename + "."
					+ CsdpFunctions._networkFiletype);
		_gui.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nExportToSEFormat

	/**
	 * export network file to 3D format
	 */
	public boolean nExportTo3DFormat(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._networkFilename = _filename;
		CsdpFunctions._networkFiletype = _filetype;

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions._networkFilename + ","
					+ CsdpFunctions._networkFiletype);

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions._networkFilename,
				CsdpFunctions._networkFiletype, _net, null);
		noutput.set3DOutput(true);
		success = noutput.writeData();
		noutput.set3DOutput(false);
		if (DEBUG)
			System.out.println("Done writing network file in SE format" + CsdpFunctions._networkFilename + "."
					+ CsdpFunctions._networkFiletype);
		_gui.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nExportToSEFormat

	// /**
	// * Calculate all cross-sections in network--IS THIS USED???????????????
	// */
	// public void nCalculate(String dir, String chanFile, String nodeFile){
	// String filename = null;
	// String centerlineName = null;
	// Centerline centerline;
	// Xsect xsect;
	// float length;
	// float distAlongCenterline;
	// float normalizedDist;

	// for(int i=0; i<=_net.getNumCenterlines()-1; i++){
	// centerlineName = _net.getCenterlineName(i);
	// centerline = _net.getCenterline(centerlineName);
	// length = centerline.getLengthFeet();
	// for(int j=0; j<=centerline.getNumXsects()-1; j++){
	// xsect = centerline.getXsect(j);
	// distAlongCenterline = xsect.getDistAlongCenterlineFeet();
	// normalizedDist = distAlongCenterline/length;
	// String s = Float.toString(normalizedDist);
	// if(DEBUG)System.out.println("channel, normalized distance = "+
	// centerlineName+s);
	// XsectOutput xoutput =
	// XsectOutput.getInstance(dir, centerlineName+"_"+s.substring(0,7),
	// "txt", j, xsect);
	// xoutput.writeData();
	// }//for i
	// }//for j
	// }//nCalculate

	/*
	 * Create DSM2 Input file with channel and irregular cross-section information
	 */
	public void nCalculateDSM2V8Format(String fullPath) {
		AsciiFileWriter afw = new AsciiFileWriter(fullPath);
		
		String filename = null;
		String centerlineName = null;
		Centerline centerline;
		Xsect xsect;
		double length;
		double distAlongCenterline;
		double normalizedDist;
		
		//First re-create CHANNEL information with CSDP channel lengths 
		if (_DSMChannels == null) {
			String channelsFilename = null;
			// FileDialog fd = new FileDialog(_gui, "Open DSM2 channel
			// connectivity file");
			// fd.setVisible(true);
			JFileChooser jfcChannelsInp = new JFileChooser();
			String[] channelsInpExtensions = { "inp" };
			int numChannelsInpExtensions = 1;
			CsdpFileFilter channelsInpFilter = new CsdpFileFilter(channelsInpExtensions, numChannelsInpExtensions);
			
			jfcChannelsInp.setDialogTitle("Open DSM2 channel connectivity file");
			jfcChannelsInp.setApproveButtonText("Open");
			jfcChannelsInp.addChoosableFileFilter(channelsInpFilter);
			jfcChannelsInp.setFileFilter(channelsInpFilter);

			if (CsdpFunctions.getOpenDirectory() != null) {
				jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			}
			int filechooserState = jfcChannelsInp.showOpenDialog(_gui);
			String directory = null;
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				channelsFilename = jfcChannelsInp.getName(jfcChannelsInp.getSelectedFile());
				directory = jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;

				// channelsFilename = fd.getFile();
				// _directory = fd.getDirectory();
				_DSMChannels = chanReadStore(directory, channelsFilename);
//				_gui.setDSMChannels(_DSMChannels);
			}
		} // if DSMChannels is null		

		afw.writeLine("#Created automatically by CSDP, using CSDP channel lengths");
		afw.writeLine("CHANNEL");
		String channelHeaderLine = 
				String.format("%-9s", "CHAN_NO") +
				String.format("%-8s", "LENGTH") +
				String.format("%-9s", "MANNING")+
				String.format("%-12s", "DISPERSION")+
				String.format("%-8s", "UPNODE")+
				String.format("%-8s", "DOWNNODE");
		afw.writeLine(channelHeaderLine);
		for(int i=0; i<_net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			String chan = _DSMChannels.getChanNum(i);
			String lineToWrite = 
					String.format("%-9s", chan) + 
					String.format("%-8.0f", length) + 
					String.format("%-10s", _DSMChannels.getManning(chan)) + 
					String.format("%-12s", _DSMChannels.getDispersion(chan)) +
					String.format("%-8d", _DSMChannels.getUpnode(chan))+
					String.format("%-8d", _DSMChannels.getDownnode(chan));
			afw.writeLine(lineToWrite);
		}
		
		afw.writeLine("END");
		afw.writeLine("");
		afw.writeLine("XSECT_LAYER");
		afw.writeLine("CHAN_NO  DIST  ELEV  AREA  WIDTH  WET_PERIM");
		
		_net.sortCenterlineNames();
		
		for(int i=0; i<_net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			for(int j=0; j<centerline.getNumXsects(); j++) {
				xsect = centerline.getXsect(j);
				if(xsect.getNumPoints()>0) {
					distAlongCenterline = xsect.getDistAlongCenterlineFeet();
					normalizedDist = distAlongCenterline / length;
					double[] elevations = xsect.getUniqueElevations();
					//remove elevations if within .01 feet of another layer
					double[] goodElevations = removeCloseElevations(elevations);

					for(int k=0; k<goodElevations.length; k++) {
						double area = xsect.getAreaSqft(goodElevations[k]);
						double width = xsect.getWidthFeet(goodElevations[k]);
						double wetP = xsect.getWettedPerimeterFeet(goodElevations[k]);
						String[] centerlineNameParts = centerlineName.split("_");
						String channelNum = centerlineNameParts[0];
//						String s = String.format("%-8s%-8.5f%-10.3f%-12.3f%-12.3f%-13.3f", channelNum,normalizedDist,elevations[k],area,width,wetP);
						String s = String.format("%-9s", channelNum)+
								String.format("%-8.5f", normalizedDist)+
								String.format("%-10.3f", goodElevations[k])+
								String.format("%-12.3f", area)+
								String.format("%-12.3f", width)+
								String.format("%-12.3f", wetP); 
						// afw.writeLine(centerlineName+"\t"+normalizedDist+"\t"+elevations[k]+"\t"+area+"\t"+width+"\t"+wetP);
						afw.writeLine(s);
					}
				}//if
			}//for j
		}//for i
		afw.writeLine("END");
		afw.close();
	}//nCalculateDSM2V812Format
		
	/*
	 * Remove elevations that are within .01 feet of each other
	 */
	private double[] removeCloseElevations(double[] elevations) {
		boolean[] removeElevation = new boolean[elevations.length];
		int numKeep = 1;
		removeElevation[0]=false;
		for(int k=1; k<elevations.length; k++) {
			if(Math.abs(elevations[k]-elevations[k-1]) < 0.01) {
				removeElevation[k]=true;
			}else {
				removeElevation[k]=false;
				numKeep++;
			}
		}

		double[] goodElevations = new double[numKeep];
		int goodElevationsIndex=0;
		for(int k=0 ; k<elevations.length; k++) {
			if(!removeElevation[k]) {
				goodElevations[goodElevationsIndex] = elevations[k];
				goodElevationsIndex++;
			}
		}
		return goodElevations;
	}
	
	/**
	 * Calculate all cross-sections in network
	 */
	public void nCalculate(String dir) {
		String filename = null;
		String centerlineName = null;
		Centerline centerline;
		Xsect xsect;
		double length;
		double distAlongCenterline;
		double normalizedDist;

		for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			for (int j = 0; j <= centerline.getNumXsects() - 1; j++) {
				xsect = centerline.getXsect(j);
				if (xsect.getNumPoints() > 0) {
					distAlongCenterline = xsect.getDistAlongCenterlineFeet();
					normalizedDist = distAlongCenterline / length;
					String s = Double.toString(normalizedDist);
					if (s.indexOf(".", 0) > 0)
						s = s + "0000000";
					else
						s = s + ".000000";
					filename = centerlineName + "_" + s.substring(0, 7);
					XsectOutput xoutput = XsectOutput.getInstance(dir, filename, XSECT_TYPE, j, xsect);
					xoutput.writeData();
				} else {
					System.out
							.println("not calculating xsect " + centerlineName + "_" + j + " because it has no points");
				}
			} // for i
		} // for j
		System.out.println("Done writing cross-section files");
	}// nCalculate

	/**
	 * save landmark file. return true if saved. If user entered bad extension
	 * (other than .cdl) save it anyway.
	 */
	public boolean lSave() {
		boolean success = false;
		String directory = CsdpFunctions.getLandmarkDirectory().getPath();
		if (CsdpFunctions._landmarkFilename != null) {
			if (CsdpFunctions._landmarkFiletype == null)
				CsdpFunctions._landmarkFiletype = LANDMARK_TYPE;
			LandmarkOutput loutput = LandmarkOutput.getInstance(directory, CsdpFunctions._landmarkFilename,
					CsdpFunctions._landmarkFiletype, _landmark);
			success = loutput.writeData();
			if (DEBUG)
				System.out.println("Done writing landmark file " + CsdpFunctions._landmarkFilename + "."
						+ CsdpFunctions._landmarkFiletype);
		}
		return success;
	}// nSave

	/**
	 * write landmark file
	 */
	public boolean lSaveAs(String directory, String filename) {
		System.out.println("App.lSaveAs: directory, filename=" + directory + "," + filename);

		boolean success = false;
		parseFilename(filename);
		CsdpFunctions._landmarkFilename = _filename;
		CsdpFunctions._landmarkFiletype = _filetype;

		if (DEBUG)
			System.out.println("landmarkFilename, landmarkFiletype=" + CsdpFunctions._landmarkFilename + ","
					+ CsdpFunctions._landmarkFiletype);

		LandmarkOutput loutput = LandmarkOutput.getInstance(directory, CsdpFunctions._landmarkFilename,
				CsdpFunctions._landmarkFiletype, _landmark);
		success = loutput.writeData();
		if (DEBUG)
			System.out.println("Done writing landmark file " + CsdpFunctions._landmarkFilename + "."
					+ CsdpFunctions._landmarkFiletype);
		_gui.updateLandmarkFilename(_filename + "." + _filetype);
		return success;
	}// nSaveAs

	/**
	 * writes the DSM2 input file "irregular_xsects.inp"
	 */
	public void writeIrregularXsectsInp(String dir) {
		IrregularXsectsInpOutput ixioutput = IrregularXsectsInpOutput.getInstance(dir, _net);
		ixioutput.writeData();
	}// writeIrregularXsectsInp

	/**
	 * write a landmark file which labels all the cross-sections
	 */
	public void writeXsectLandmark(String directory) {
		XsectLandmarkOutput xloutput = XsectLandmarkOutput.getInstance(_net, directory);
		xloutput.writeData();
	}// writeXsectLandmark

	/*
	 * Similar to viewXsect, but write data to file instead. For HECRAS.
	 */
	public boolean extractXsectData(String filename, String filetype, Xsect xsect, String centerlineName, int xsectNum,
			double thickness) {
		boolean success = false;
		_gui.setCursor(CsdpFunctions._waitCursor);
		Hashtable xsectDisplayData = _net.findXsectDisplayRegion(centerlineName, xsectNum, thickness);
		_bathymetryData.findXsectData(xsectDisplayData);

		if (_xsectGraph.contains(centerlineName + "_" + xsectNum)) {

		} else {
			if (_bathymetryData.getNumEnclosedValues() <= 0) {
				JOptionPane.showOptionDialog(null, "ERROR!  THERE ARE NO POINTS TO EXPORT! TRY INCREASING THICKNESS",
						"ERROR! NO POINTS TO DISPLAY", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
						_options, _options[0]);

			} else {
				// export data
				String directory = CsdpFunctions.getOpenDirectory().toString();

				BathymetryOutput aoutput = BathymetryOutput.getInstance(directory, filename, filetype, _bathymetryData);
				if (DEBUG)
					System.out.println("aoutput=" + aoutput);
				success = aoutput.extractXsectData(centerlineName, xsectNum, thickness);
			}
			_gui.setCursor(CsdpFunctions._defaultCursor);
		}
		return success;
	}// extractXsectData

	/**
	 * plot bathymetry and network data in cross-section view
	 */
	public void viewXsect(Xsect xsect, String centerlineName, int xsectNum, double thickness) {
		_gui.setCursor(CsdpFunctions._waitCursor);
		Hashtable xsectDisplayData = _net.findXsectDisplayRegion(centerlineName, xsectNum, thickness);
		_bathymetryData.findXsectData(xsectDisplayData);

		if (_xsectGraph.contains(centerlineName + "_" + xsectNum)) {

		} else {
			// if(_bathymetryData.getNumEnclosedValues() <= 0){
			// JOptionPane.showOptionDialog
			// (null, "ERROR! THERE ARE NO POINTS TO DISPLAY! TRY INCREASING
			// THICKNESS",
			// "ERROR! NO POINTS TO DISPLAY",
			// JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
			// _options, _options[0]);

			// }else{
			_xsectGraph.put(centerlineName + "_" + xsectNum, new XsectGraph(_gui, this, _bathymetryData, _net,
					centerlineName, xsectNum, thickness, _xsectColorOption));
			getXsectGraph(centerlineName, xsectNum).pack();
			getXsectGraph(centerlineName, xsectNum).setVisible(true);
			// }
			_gui.setCursor(CsdpFunctions._defaultCursor);
		}
	}// viewXsect

	/**
	 * If name exists in XsectGraph array, then rename...store first...
	 */
	public void renameOpenXsectGraphs(String centerlineName, ResizableIntArray xsectIndices) {
		XsectGraph graph = null;
		Hashtable sortedGraphs = new Hashtable();

		for (int i = 0; i <= xsectIndices.getSize() - 1; i++) {
			graph = getXsectGraph(centerlineName, xsectIndices.get(i));
			if (graph != null) {
				sortedGraphs.put(centerlineName + "_" + i, graph);
				graph.updateXsectNum(i);
			}
		} // for i
		_xsectGraph = sortedGraphs;
	}// renameOpenXsectGraphs

	/**
	 * updates xsect display when its xsect line is moved
	 */
	public void updateXsect(String centerlineName, int xsectNum) {
		XsectGraph xg = getXsectGraph(centerlineName, xsectNum);
		if (xg != null) {
			xg.updateGraphCanvas();
			xg.updateDisplay();
		}
	}

	/**
	 * rename centerline
	 */
	public String centerlineRename(Centerline c) {
		// FileDialog fd = new FileDialog("Enter new Centerline name");
		String centerlineName = null;
		// centerline names are actually stored in array in network class.
		// should
		// really be getting name from _net
		// if(c.getCenterlineName() != null) fd.setFile(c.getCenterlineName());
		// fd.show();
		// String centerlineName = fd.getFile();
		return centerlineName;
	}

	/**
	 * returns BathymetryData
	 */
	public BathymetryData getBathymetryData() {
		return _bathymetryData;
	}// getBathymetryData

	/**
	 * Causes all open xsect graphs to be updated. Called when color table
	 * adjusted.
	 */
	public void updateAllXsectGraphs() {
		Enumeration e = _xsectGraph.elements();
		while (e.hasMoreElements()) {
			XsectGraph xg = (XsectGraph) (e.nextElement());
			xg.updateGraphCanvas();
			xg._gC.redoNextPaint();
			xg.validate();

			// removed for conversion to swing
			// xg._gC.repaint();
		}
	}// updateAllXsectGraphs

	public boolean anyUnsavedXsectEdits() {
		boolean returnValue = false;
		if (_xsectGraph.size()>0) {
			Enumeration e=_xsectGraph.elements();
			while(e.hasMoreElements()) {
				XsectGraph xg=(XsectGraph) e.nextElement();
				System.out.println("_xsectGraph.size(), xg="+_xsectGraph.size()+","+xg);
				if (xg._xsect._isUpdated || xg.getChangesKept()) {			
					returnValue = true;
					break;
				}
			}
		}
		return returnValue;
	}//anyUnsavedXsectEdits
	
	/**
	 * redraw the xsect graph
	 */
	public void updateXsectGraph(String centerlineName, int xsectNum) {
		XsectGraph xg = getXsectGraph(centerlineName, xsectNum);
		if (xg != null) {

			// System.out.println("about to call updateGraphCanvas");
			// System.out.println(xg._gC.getGraph());
			// System.out.println(xg._gC.getGraph().getPlot());
			// System.out.println(xg._gC.getGraph().getPlot().getAttributes());

			xg.updateGraphCanvas();
			xg._gC.redoNextPaint();
			xg.validate();
			// removed for conversion to swing
			// xg._gC.repaint();
		}
	}

	/*
	 * return network object
	 */
	public Network getNetwork() {
		return _net;
	}

	/*
	 * return landmark object
	 */
	public Landmark getLandmark() {
		return _landmark;
	}

	public void setXsectColorOption(int value) {
		_xsectColorOption = value;
	}

	/**
	 * separates filename into prefix and extension
	 */
	protected void parseFilename(String filename) {

		_filename = null;
		_filetype = null;

		// int dotIndex = filename.indexOf(".",0);
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex >= 0) {
			_filename = filename.substring(0, dotIndex);
			_filetype = filename.substring(dotIndex + 1);
		} else {
			_filename = filename;
			_filetype = null;
		}
	}// parseFilename

	/**
	 * returns XsectGraph object for specified centerlineName and xsectNum
	 */
	protected XsectGraph getXsectGraph(String centerlineName, int xsectNum) {
		return (XsectGraph) (_xsectGraph.get(centerlineName + "_" + xsectNum));
	}

	/**
	 * removes XsectGraph object for specified centerlineName and xsectNum
	 */
	protected void removeXsectGraph(String centerlineName, int xsectNum) {
		_xsectGraph.remove(centerlineName + "_" + xsectNum);
	}

	public void setSquareDimension(int size) {
		_squareDimension = size;
	}

	public int getSquareDimension() {
		return _squareDimension;
	}

	/*
	 * Check cross-sections for zero area at 0 elevation and duplicate station
	 * values
	 */
	public void xsCheck(CsdpFrame gui) {
		String centerlineName = null;
		Centerline centerline = null;
		double length;
		Xsect xsect = null;
		double distAlongCenterline;
		double normalizedDist;
		String allZeroArea = "";
		String allDuplicateStations = "";

		int numLinesInMessage = 0;
		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			for (int j = 0; j < centerline.getNumXsects(); j++) {
				xsect = centerline.getXsect(j);
				double area = xsect.getAreaSqft(0);
				boolean uniqueStations = xsect.allUniqueStations();
				int numPoints = xsect.getNumPoints();
				if (area <= 0) {
					allZeroArea += centerlineName + "_" + j + "   " + numPoints + "\n";
					numLinesInMessage++;
				}
				if (!uniqueStations) {
					allDuplicateStations += centerlineName + "_" + j + "\n";
					allDuplicateStations += "   " + xsect.getAllStations() + "\n";
					allDuplicateStations += "   " + xsect.getAllElevations() + "\n";
					numLinesInMessage += 3;
				}
			}
		}

		String message = "Cross-Sections with <=zero area at elevation 0\n" + "Cross-Section     numPoints\n"
				+ "----------------------------------------------------------\n";
		message += allZeroArea;
		message += "\n\n Cross-Sections with duplicate stations\n"
				+ "----------------------------------------------------------\n";
		message += allDuplicateStations;
		MessageDialog md = new MessageDialog(gui, "Cross-Sections that may need repair", message, false, false, 90,
				numLinesInMessage);
		md.setVisible(true);
	}// xsCheck

	/*
	 * Print a table of area, width, and depth for the given elevation Then
	 * estimate volume
	 */
	public void awdSummary(double elevation) {
		String centerlineName = null;
		Centerline centerline = null;
		double length;
		Xsect xsect = null;
		double distAlongCenterline;
		double normalizedDist;
		String line = "";

		System.out.println("====================================================================");
		System.out.println("centerline, length, channel volume-thousand sqft, for elevation " + elevation);
		System.out.println("====================================================================");

		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			double volume = 0.0;
			double lastDistAlongCenterline = -Double.MAX_VALUE;
			double lastArea = -Double.MAX_VALUE;
			for (int j = 0; j < centerline.getNumXsects(); j++) {
				xsect = centerline.getXsect(j);
				distAlongCenterline = xsect.getDistAlongCenterlineFeet();
				double area = xsect.getAreaSqft(elevation);
				// calculate volume
				if (j == 0) {
					volume += distAlongCenterline * area / 1000;
				} else {
					volume += (distAlongCenterline - lastDistAlongCenterline) * 0.5 * (area + lastArea) / 1000;
				}
				if (j == centerline.getNumXsects() - 1) {
					volume += area * (length - distAlongCenterline) / 1000;
				}
				lastDistAlongCenterline = distAlongCenterline;
				lastArea = area;
			}
			System.out.println(centerlineName + "," + length + "," + volume);
		}
		System.out.println("====================================================================\n");

		System.out.println("====================================================================");
		System.out.println("cross-section, Area, Width, Depth, Hydraulic Depth, for elevation " + elevation);
		System.out.println("====================================================================");

		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			for (int j = 0; j < centerline.getNumXsects(); j++) {
				line = "";
				xsect = centerline.getXsect(j);
				distAlongCenterline = xsect.getDistAlongCenterlineFeet();
				normalizedDist = distAlongCenterline / length;
				String s = Double.toString(normalizedDist);
				line += centerlineName + "_" + s.substring(0, 7);
				double area = xsect.getAreaSqft(elevation);
				double width = xsect.getWidthFeet(elevation);
				double depth = elevation - xsect.getMinimumElevationFeet();
				double hydraulicDepth = xsect.getHydraulicDepthFeet(elevation);
				line += "," + area + "," + width + "," + depth + "," + hydraulicDepth;
				System.out.println(line);
			}
		}
		System.out.println("====================================================================");
	}

	// /*
	// * Print a table of area, width, and depth for the given elevation
	// * Then estimate volume
	// */
	// public void areaSummary(){
	// String centerlineName = null;
	// Centerline centerline = null;
	// float length;
	// Xsect xsect = null;
	// float distAlongCenterline;
	// float normalizedDist;
	// String line = "";

	// float[] distAlong;
	// float[][] area;

	// System.out.println("====================================================================");
	// System.out.println("cross-section,area for elevation");
	// System.out.println("====================================================================");

	// //get min, max elevations
	// float minElevation = Float.MAX_VALUE;
	// float maxElevation = -Float.MAX_VALUE;
	// for(int i=0; i<_net.getNumCenterlines(); i++){
	// centerlineName = _net.getCenterlineName(i);
	// centerline = _net.getCenterline(centerlineName);
	// length = centerline.getLengthFeet();
	// for(int j=0; j<centerline.getNumXsects(); j++){
	// xsect = centerline.getXsect(j);
	// distAlong[j] = xsect.getDistAlongCenterlineFeet();
	// minElevation = xsect.getMinimumElevationFeet();
	// }
	// System.out.println(centerlineName+","+length+","+volume);
	// }

	// for(int i=0; i<_net.getNumCenterlines(); i++){
	// centerlineName = _net.getCenterlineName(i);
	// centerline = _net.getCenterline(centerlineName);
	// length = centerline.getLengthFeet();
	// float lastDistAlongCenterline = -Float.MAX_VALUE;
	// float lastArea = -Float.MAX_VALUE;
	// for(int j=0; j<centerline.getNumXsects(); j++){
	// xsect = centerline.getXsect(j);
	// distAlongCenterline = xsect.getDistAlongCenterlineFeet();
	// float area = xsect.getAreaSqft(elevation);
	// lastDistAlongCenterline = distAlongCenterline;
	// lastArea = area;
	// }
	// System.out.println(centerlineName+","+length+","+volume);
	// }
	// System.out.println("====================================================================\n");

	// System.out.println("====================================================================");
	// System.out.println("cross-section, Area, Width, Depth, Hydraulic Depth,
	// for elevation "+elevation);
	// System.out.println("====================================================================");

	// for(int i=0; i<_net.getNumCenterlines(); i++){
	// centerlineName = _net.getCenterlineName(i);
	// centerline = _net.getCenterline(centerlineName);
	// length = centerline.getLengthFeet();
	// for(int j=0; j<centerline.getNumXsects(); j++){
	// line="";
	// xsect = centerline.getXsect(j);
	// distAlongCenterline = xsect.getDistAlongCenterlineFeet();
	// normalizedDist = distAlongCenterline/length;
	// String s = Float.toString(normalizedDist);
	// line += centerlineName+"_"+s.substring(0,7);
	// float area = xsect.getAreaSqft(elevation);
	// float width = xsect.getWidthFeet(elevation);
	// float depth = elevation - xsect.getMinimumElevationFeet();
	// float hydraulicDepth = xsect.getHydraulicDepthFeet(elevation);
	// line += ","+area+","+width+","+depth+","+hydraulicDepth;
	// System.out.println(line);
	// }
	// }
	// System.out.println("====================================================================");
	// }

	CsdpFrame _gui = null;
	BathymetryData _bathymetryData = null;
	Network _net;
	Landmark _landmark;
	DSMChannels _DSMChannels;
	BathymetryPlot _bathymetryPlot = null;
	NetworkPlot _nPlot = null;
	LandmarkPlot _lPlot = null;
	DigitalLineGraph _dlg;
	DigitalLineGraphPlot _dlgPlot = null;
	// XsectGraph _xsectGraph;
	Hashtable _xsectGraph = new Hashtable();

	protected String _filename = null;
	protected String _filetype = null;
	protected static final String PROPERTIES_TYPE = "prp";
	protected static final String ASCII_TYPE = "prn";
	protected static final String BINARY_TYPE = "cdp";
	protected static final String NETWORK_TYPE = "cdn";
	protected static final String LANDMARK_TYPE = "cdl";
	protected static final String XSECT_TYPE = "txt";
	protected static final String DLG_TYPE = "cdo";
	protected static final String DSMChannels_TYPE = "inp";
	// BathymetryInput _binput = null;
	PropertiesInput _pinput = null;
	protected static boolean DEBUG = false;
	public int _xsectColorOption = 0;
	public static int _squareDimension = 4;
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };

}// class App
