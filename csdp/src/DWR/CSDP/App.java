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
import java.util.AbstractCollection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import DWR.CSDP.XsectBathymetryData;
import DWR.CSDP.dialog.DataEntryDialog;
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

	/*
	 * Given .prn filename, create a .cdp file
	 */
	public void convertPrnToCdp(JFrame gui, String directory, String filename, String filetype) {
		BathymetryInput binput = null;
		_csdpFrame = (CsdpFrame) gui;
		boolean bWrite = true;
		int numLines = 0;
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);

		// read bathymetry file and store in BathymetryPlot object

		// if(DEBUG)
		System.out.println("about to read file.  directory, filename, filetype=");
		// if(DEBUG)
		System.out.println(directory + "," + filename + "," + filetype);

		binput = BathymetryInput.getInstance(_csdpFrame, directory, filename + "." + filetype);

		System.out.println("binput=" + binput);

		BathymetryData bathymetryData = null;
		bathymetryData = binput.readData();
		bathymetryData.sortYearIndices();
		if (filetype.equals("prn"))
			bathymetryData.sortBathymetryData();
		System.out.println("about to write data for directory, filename, filetype, bathymetryData="+
			directory+","+filename+","+filetype+","+bathymetryData);
		BathymetryOutput bOutput = BathymetryOutput.getInstance(directory, filename, "cdp", bathymetryData);
		bOutput.writeData();
	}

	
	/**
	 * Open ascii or binary bathymetry data file and store data in arrays
	 */
	public BathymetryPlot bReadStore(JFrame gui, String directory, String filename, String filetype) {
		BathymetryInput binput = null;
		_csdpFrame = (CsdpFrame) gui;
		boolean bWrite = true;
		int numLines = 0;
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);

		// read bathymetry file and store in BathymetryPlot object

		// if(DEBUG)
		System.out.println("about to read file.  directory, filename, filetype=");
		// if(DEBUG)
		System.out.println(directory + "," + filename + "," + filetype);

		binput = BathymetryInput.getInstance(_csdpFrame, directory, filename + "." + filetype);

		System.out.println("binput=" + binput);

		_bathymetryData = null;
		_bathymetryData = binput.readData();
		_bathymetryData.sortYearIndices();
		if (filetype.equals("prn"))
			_bathymetryData.sortBathymetryData();

		// plot data on canvas (BathymetryPlot)
		_bathymetryPlot = setBathymetryPlotter();
		updateNetworkPlotter();
		_csdpFrame.setPlotObject(_bathymetryPlot);
		_csdpFrame.updateColorLegend();

		_csdpFrame.getPlanViewCanvas(0).setPlotter(_bathymetryPlot, _csdpFrame._dim);
		_csdpFrame.getPlanViewCanvas(0).setUpdateBathymetry(true);

		if (_csdpFrame.getPlanViewCanvas(0)._networkPlotter != null) {
			if (DEBUG)
				System.out.println("should update network...");
			_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
			_net.convertToBathymetryDatum();
		}
		if (_csdpFrame.getPlanViewCanvas(0)._landmarkPlotter != null) {
			_landmark.convertToBathymetryDatum();
		}

		if (_csdpFrame.getPlanViewCanvas(0)._dlgPlotter != null) {
			// _gui.getPlanViewCanvas(0).setUpdateCanvas(true);
			_csdpFrame.getPlanViewCanvas(0).setUpdateDigitalLineGraph(true);
			_dlg.convertToBathymetryDatum();
		}

		// tells canvas to zoom out and redraw itself.
		_csdpFrame.getPlanViewCanvas(0).zoomFit();
		_csdpFrame.setCursor(CsdpFunctions._defaultCursor);
		if (DEBUG)
			displayData();
		_csdpFrame.enableAfterBathymetry();
		_csdpFrame.updateBathymetryFilename(filename + "." + filetype);
		_csdpFrame.updateMetadataDisplay(CsdpFunctions.getBathymetryMetadata());

		return _bathymetryPlot;
	}// bReadStore

	/**
	 * Open property data file and store data in CsdpFunctions
	 */
	public void pReadStore(JFrame gui, String directory, String filename, String filetype) {
		parseFilename(filename);
		CsdpFunctions.setPropertiesFilename(_filename);
		CsdpFunctions.setPropertiesFiletype(_filetype);
		_csdpFrame = (CsdpFrame) gui;
		boolean bWrite = true;
		int numLines = 0;
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);

		_pinput = PropertiesInput.getInstance(_csdpFrame, directory, filename + "." + filetype);
		_pinput.readData();

		_csdpFrame.updateColorLegend();
		_csdpFrame.getPlanViewCanvas(0).setUpdateCanvas(true);
		_csdpFrame.updatePropertiesFilename(_filename + "." + _filetype);
		// removed for conversion to swing
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
		_csdpFrame.setCursor(CsdpFunctions._defaultCursor);
	}// pReadStore

	public boolean compareNetworks(JFrame gui, String nFilename1, Network net1, String nFilename2, Network net2,
			String outputFilename, String outputFiletype, String outputDirectory) {
		_csdpFrame = (CsdpFrame) gui;
		boolean success = false;

		NetworkCompareOutput ncOutput = NetworkCompareOutput.getInstance(nFilename1, net1, nFilename2, net2,
				outputDirectory, outputFilename + outputFiletype, _csdpFrame);
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
		_csdpFrame = gui;
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

		RectXSOutput rxOutput = RectXSOutput.getInstance(outputDirectory, outputFilename, net, _csdpFrame);
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
		_csdpFrame = (CsdpFrame) gui;
		// boolean bWrite = true;
		int numLines = 0;
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);

		// read time series data(flow and stage at different locations for each
		// date)
		OpenWaterAreaInput owaInput = OpenWaterAreaInput.getInstance(filename, filetype, _csdpFrame);
		owaNet = owaInput.readData();

		if (DEBUG)
			System.out.println("About to read time series file.  directory, filename=" + stationFilename);

		// read cross-section info
		OpenWaterAreaStationInput owaSInput = OpenWaterAreaStationInput.getInstance(stationFilename, _csdpFrame);
		stationData = owaSInput.readData();

		// read toe drain info
		if (CsdpFunctions.getUseToeDrainRestriction()) {
			OpenWaterAreaToeDrainInput owaTDInput = OpenWaterAreaToeDrainInput.getInstance(toeDrainFilename);

			toeDrainData = owaTDInput.readData();
		}

		// write output
		OpenWaterAreaOutput owaOutput = OpenWaterAreaOutput.getInstance(outputFilename, owaNet, _csdpFrame);
		owaOutput.writeData(stationData, toeDrainData);
		if (DEBUG)
			toeDrainData.printAll();

		_csdpFrame.setCursor(CsdpFunctions._defaultCursor);

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
		CsdpFunctions.setPropertiesFilename(_filename);
		CsdpFunctions.setPropertiesFiletype(_filetype);

		if (DEBUG)
			System.out.println("propertiesFilename, propertiesFiletype=" + CsdpFunctions.getPropertiesFilename() + ","
					+ CsdpFunctions.getPropertiesFiletype());

		PropertiesOutput poutput = PropertiesOutput.getInstance(_csdpFrame, directory, CsdpFunctions.getPropertiesFilename(),
				CsdpFunctions.getPropertiesFiletype());
		success = poutput.writeData();
		if (DEBUG)
			System.out.println("Done writing properties file " + CsdpFunctions.getPropertiesFilename() + "."
					+ CsdpFunctions.getPropertiesFiletype());
		_csdpFrame.updatePropertiesFilename(_filename + "." + _filetype);
		return success;
	}// pSaveAs

	/**
	 * save properties file. return true if saved. If user entered bad extension
	 * (other than .cdn) save it anyway.
	 */
	public boolean pSave() {
		boolean success = false;
		String directory = CsdpFunctions.getPropertiesDirectory().getPath();
		if (CsdpFunctions.getPropertiesFilename() != null) {
			if (CsdpFunctions.getPropertiesFiletype() == null)
				CsdpFunctions.setPropertiesFiletype(PROPERTIES_TYPE);
			PropertiesOutput poutput = PropertiesOutput.getInstance(_csdpFrame, directory, CsdpFunctions.getPropertiesFilename(),
					CsdpFunctions.getPropertiesFiletype());
			success = poutput.writeData();
			if (DEBUG)
				System.out.println("Done writing properties file " + CsdpFunctions.getPropertiesFilename() + "."
						+ CsdpFunctions.getPropertiesFiletype());
		}
		return success;
	}// pSave

	/**
	 * return instance of plotter object that is used by this class
	 */
	public BathymetryPlot setBathymetryPlotter() {
		_bathymetryPlot = new BathymetryPlot(_csdpFrame, _bathymetryData, this);
		return _bathymetryPlot;
	}// setBathymetryPlotter

	/**
	 * return instance of network plotter object that is used by this class
	 */
	public NetworkPlot setNetworkPlotter() {
		_nPlot = new NetworkPlot(_csdpFrame, _bathymetryData, this);
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
		_lPlot = new LandmarkPlot(_csdpFrame, _bathymetryData, this);
		return _lPlot;
	}// setLandmarkPlotter

	/**
	 * Return instance of dlg plotter object that is used by this class
	 */
	public DigitalLineGraphPlot setDigitalLineGraphPlotter() {
		_dlgPlot = new DigitalLineGraphPlot(_csdpFrame, _bathymetryData, this);
		return _dlgPlot;
	}// setDLGPlotter

	/**
	 * read network data file
	 */
	public Network nReadStore(JFrame gui, String directory, String filename) {
		_csdpFrame = (CsdpFrame) gui;

		_net = justReadNetwork(directory, filename); 
		_csdpFrame.updateNetworkFilename(_filename + "." + _filetype);

		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
		return _net;
	}// nReadStore

	/*
	 * This enables other applications to just read the network file and get a Network object.
	 */
	public Network justReadNetwork(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions.setNetworkFilename(_filename);
		CsdpFunctions.setNetworkFiletype(_filetype);
		NetworkInput ninput = NetworkInput.getInstance(_csdpFrame, directory, _filename, _filetype);
		_net = ninput.readData();
		if (DEBUG)
			System.out.println("Done reading ascii network data file");
		return _net;
	}
	
	/**
	 * remove network file from memory and display
	 */
	public void clearNetwork() {
		_net = null;
		_csdpFrame.setNetwork(null);
		_csdpFrame.updateNetworkFilename(null);
		_csdpFrame.disableWhenNetworkCleared();
		_csdpFrame.getPlanViewCanvas(0).setNetworkPlotter(null);
		_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(false);
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
	}

	/*
	 * Remove channel outlines from memory and display
	 */
	public void clearChannelOutlines() {
		_dlg = null;
		_csdpFrame.setDigitalLineGraph(null);
		_csdpFrame.updateDigitalLineGraphFilename(null);
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
	}
	
	/*
	 * Create new landmark file (when landmark not loaded and user wants to
	 * create landmarks)
	 */
	public Landmark lCreate(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions.setLandmarkFilename(_filename);
		CsdpFunctions.setLandmarkFiletype(_filetype);

		// create landmark object
		_landmark = new Landmark(_csdpFrame);
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

		CsdpFunctions.setLandmarkFilename(_filename);
		CsdpFunctions.setLandmarkFiletype(_filetype);
		if (_filetype.equals(LANDMARK_TYPE)) {
			LandmarkInput linput = LandmarkInput.getInstance(_csdpFrame, directory, _filename + "." + _filetype);
			_landmark = linput.readData();
			if (DEBUG)
				System.out.println("Done reading ascii landmark data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		_csdpFrame.updateLandmarkFilename(_filename + "." + _filetype);
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
		return _landmark;
	}// lReadStore

	/*
	 * removes landmark from memory and from display.
	 */
	public void clearLandmarks() {
		_landmark = null;
		_csdpFrame.updateLandmarkFilename(null);
		_csdpFrame.setLandmark(null);
		_csdpFrame.getPlanViewCanvas(0).setLandmarkPlotter(null);
		_csdpFrame.getPlanViewCanvas(0).setUpdateLandmark(false);
		_csdpFrame.disableWhenLandmarkCleared();
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
	}

	/**
	 * read Digital Line Graph data file
	 */
	public DigitalLineGraph digitalLineGraphReadStore(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions.setDigitalLineGraphFilename(_filename);
		CsdpFunctions.setDigitalLineGraphFiletype(_filetype);
		if (_filetype.equals(DLG_TYPE)) {
			DigitalLineGraphInput dlgInput = DigitalLineGraphInput.getInstance(_csdpFrame, directory,
					_filename + "." + _filetype);
			_dlg = dlgInput.readData();
			if (DEBUG)
				System.out.println("Done reading ascii Digital Line Graph data file");
		} else
			System.out.println("filetype not defined for extension " + _filetype);
		_csdpFrame.updateDigitalLineGraphFilename(_filename + "." + _filetype);
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
		return _dlg;
	}// dlgReadStore

	/**
	 * read channelsInp data file
	 */
	public DSMChannels chanReadStore(String directory, String filename) {
		parseFilename(filename);
		CsdpFunctions.setDSMChannelsFilename(_filename+"."+_filetype);
		CsdpFunctions.setDSMChannelsFiletype(_filetype);
		try {
			if (_filetype.equals(DSMChannels_TYPE)) {
				DSMChannelsInput chanInput = DSMChannelsInput.getInstance(directory, _filename + "." + _filetype);
				_DSMChannels = chanInput.readData();
				if (DEBUG)
					System.out.println("Done reading ascii DSMChannels data file");
			} else
				System.out.println("filetype not defined for extension " + _filetype);
		}catch(Exception e) {
			JOptionPane.showMessageDialog(_csdpFrame, "Exception caught in App.chanReadStore: "+e.getMessage(), 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		return _DSMChannels;
	}// chanReadStore

	/**
	 * save network file. return true if saved. If user entered bad extension
	 * (other than .cdn) save it anyway.
	 */
	public boolean nSave() {
		boolean success = false;
		String directory = CsdpFunctions.getNetworkDirectory().getPath();
		if (CsdpFunctions.getNetworkFilename() != null) {
			if (CsdpFunctions.getNetworkFiletype() == null)
				CsdpFunctions.setNetworkFiletype(NETWORK_TYPE);
			_net.sortCenterlineNames();
			NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions.getNetworkFilename(),
					CsdpFunctions.getNetworkFiletype(), _net, null);
			success = noutput.writeData();
			if (DEBUG)
				System.out.println("Done writing network file " + CsdpFunctions.getNetworkFilename() + "."
						+ CsdpFunctions.getNetworkFiletype());
		}
		return success;
	}// nSave

	/**
	 * write network file
	 */
	public boolean nSaveAs(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions.setNetworkFilename(_filename);
		CsdpFunctions.setNetworkFiletype(_filetype);

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions.getNetworkFilename() + ","
					+ CsdpFunctions.getNetworkFiletype());

		_net.sortCenterlineNames();
		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions.getNetworkFilename(),
				CsdpFunctions.getNetworkFiletype(), _net, null);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file " + CsdpFunctions.getNetworkFilename() + "."
					+ CsdpFunctions.getNetworkFiletype());
		_csdpFrame.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nSaveAs

	/*
	 * Export network to WKT format for importing into GIS 
	 */
	public boolean nExportToWKT(Network net, String directory, String filename) {
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, directory+File.separator+filename);
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

	/*
	 * Export landmark to WKT format for importing into GIS 
	 */
	public boolean lExportToWKT(Landmark landmark, String directory, String filename) {
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, directory+File.separator+filename);
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
		CsdpFunctions.setNetworkFilename(_filename);
		CsdpFunctions.setNetworkFiletype(_filetype);

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions.getNetworkFilename() + ","
					+ CsdpFunctions.getNetworkFiletype());

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions.getNetworkFilename(),
				CsdpFunctions.getNetworkFiletype(), _net, channelsToSave);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file " + CsdpFunctions.getNetworkFilename() + "."
					+ CsdpFunctions.getNetworkFiletype());
		_csdpFrame.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nSaveAs

	/**
	 * export network file to station/elevation format
	 */
	public boolean nExportToSEFormat(String directory, String filename, boolean channelLengthsOnly) {

		boolean success = false;
		parseFilename(filename);
		CsdpFunctions.setNetworkFilename(_filename);
		CsdpFunctions.setNetworkFiletype(_filetype);

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions.getNetworkFilename() + ","
					+ CsdpFunctions.getNetworkFiletype());

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions.getNetworkFilename(),
				CsdpFunctions.getNetworkFiletype(), _net, null);
		noutput.setChannelLengthsOnly(channelLengthsOnly);
		success = noutput.writeData();
		if (DEBUG)
			System.out.println("Done writing network file in SE format" + CsdpFunctions.getNetworkFilename() + "."
					+ CsdpFunctions.getNetworkFiletype());
		_csdpFrame.updateNetworkFilename(_filename + "." + _filetype);
		return success;
	}// nExportToSEFormat

	/**
	 * export network file to 3D format
	 */
	public boolean nExportTo3DFormat(String directory, String filename) {
		boolean success = false;
		parseFilename(filename);
		CsdpFunctions.setNetworkFilename(_filename);
		CsdpFunctions.setNetworkFiletype(_filetype);

		if (DEBUG)
			System.out.println("networkFilename, networkFiletype=" + CsdpFunctions.getNetworkFilename() + ","
					+ CsdpFunctions.getNetworkFiletype());

		NetworkOutput noutput = NetworkOutput.getInstance(directory, CsdpFunctions.getNetworkFilename(),
				CsdpFunctions.getNetworkFiletype(), _net, null);
		noutput.set3DOutput(true);
		success = noutput.writeData();
		noutput.set3DOutput(false);
		if (DEBUG)
			System.out.println("Done writing network file in SE format" + CsdpFunctions.getNetworkFilename() + "."
					+ CsdpFunctions.getNetworkFiletype());
		_csdpFrame.updateNetworkFilename(_filename + "." + _filetype);
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
	
	public void setDSMChannels(String directory, String filename) {
		boolean updateDsmChannels = false;
		if(CsdpFunctions.getDSMChannelsDirectory() != null && CsdpFunctions.getDSMChannelsFilename()!=null) { 
			if(!CsdpFunctions.getDSMChannelsDirectory().equals(directory) || 
					!CsdpFunctions.getDSMChannelsFilename().equals(filename)) {
				updateDsmChannels = true;
			}
		}else {
			updateDsmChannels = true;
		}
		if(updateDsmChannels) {
			_DSMChannels = chanReadStore(directory, filename);
			CsdpFunctions.setDSMChannelsDirectory(directory);
			CsdpFunctions.setDSMChannelsFilename(filename);
			
		}
	}
	
	public DSMChannels getDSMChannels() {
		if(_DSMChannels == null) {
			String[] directoryFilename = CsdpFunctions.selectFilePath(_csdpFrame, "Open DSM2 channel connectivity file (channels.inp)", new String[]{"inp"});
			_DSMChannels = chanReadStore(directoryFilename[0], directoryFilename[1]);
			CsdpFunctions.setDSMChannelsDirectory(directoryFilename[0]);
			CsdpFunctions.setDSMChannelsFilename(directoryFilename[1]);
			CsdpFunctions.setOpenDirectory(directoryFilename[0]);
		}
		return _DSMChannels;
	}//getInputFilePath
	
	/*
	 * Creates a report with summary statistics. 
	 * User specifies 3 files:
	 * 1. DSM2 Channels.inp file
	 * 2. DSM2 .hof file, created using printlevel>=5 
	 * 		(Optional; if included report will include additional information calculated from virtual cross-sections)
	 * 3. output .txt file
	 * Output written to tab delimited ASCII file with specified name. 
	 */
	public void createNetworkSummaryReport() {
		String title = "Create Network Summary Report";
		String instructions = "<HTML><BODY><H2>A network summary report uses the following input files:</H2><BR>"
				+ "1. An existing channels.imp file<BR>"
				+ "2. The currently loaded network file<BR>"
				+ "3. A DSM2 output (.hof) file which was created from the network file by running DSM2-Hydro with printlevel>=5</font><BR><BR>"
				+ "<H2>To calculate, for each channel, for a given stage (usually 0.0 NAVD)</H2><BR>"
				+ "1. A comparison of channels lengths from the channels.inp file vs channels lengths calculated using the network file<BR>"
				+ "2. Volume, wetted area, and surface area using CSDP cross-sections, assuming no inter-channel interpolation<BR>"
				+ "3. (If hof file specified) Volume, wetted area, and surface area using Virtual cross-sections (which are calculated using inter-channel interpolation<BR>"
				+ "4. The maximum ratio of CSDP cross-sectional areas within the channel<BR>"
				+ "5. (If hof file specified) The maximum ratio of Virtual cross-sectional areas within the channel<BR>"
				+ "6. Highest bottom elevation in the channel<BR>"
				+ "7. The indices of the CSDP cross-sections containing no points<BR>"
				+ "8. The indices of the CSDP cross-sections that are within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+
				" feet of each other<BR>"
				+ "9. The index of the CSDP cross-section with the minimum area<BR>"
				+ "10. The index of the CSDP cross-section with the maximum area<BR>"
				+ "11. The indices of all CSDP cross-sections with duplicate station values<BR>"
				+ "12. The indices of all CSDP cross-sections with negative dConveyance anywhere in the cross-section<BR>"
				+ "13. The indices of all CSDP cross-sections with negative dConveyance in the intertidal zone, defined as -2.5 ft < Z < 17.5 ft (NAVD88)</font><BR><BR>"
				+ "<H2>Output will be written to a tab delimited .txt file, which can be imported into Excel<H2><BR></BODY></HTML>";

		//Create dialog to get input from user.
		String[] names = new String[] {"Channels.inp file", "DSM2 output (.hof) file", "Output file (tab delimited .txt)"};
		String[] defaultValues = new String[3];
		if(CsdpFunctions.getDSMChannelsDirectory()!=null && CsdpFunctions.getDSMChannelsFilename()!=null) {
			defaultValues[0] = CsdpFunctions.getDSMChannelsDirectory().toString()+File.separator+CsdpFunctions.getDSMChannelsFilename();
		}
		if(CsdpFunctions.getDSM2HofDirectory()!=null && CsdpFunctions.getDSM2HofFilename()!=null) {
			defaultValues[1] = CsdpFunctions.getDSM2HofDirectory().toString()+File.separator+CsdpFunctions.getDSM2HofFilename();
		}
		defaultValues[2] = CsdpFunctions.getNetworkDirectory().toString()+File.separator+"networkSummary.txt";
		
		int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE, 
				DataEntryDialog.FILE_SPECIFICATION_TYPE};
		String[] extensions = new String[] {"inp", "hof", "txt"};
		String[] tooltips = new String[3];
		
		//require channels.inp and output file name, but not hof file
		boolean[] disableIfNull = new boolean[] {true, false, true}; 
		DataEntryDialog dataEntryDialog = new DataEntryDialog(_csdpFrame, title, instructions, names, defaultValues, dataTypes, disableIfNull, 
				extensions, tooltips, true);
		int response = dataEntryDialog.getResponse();
		//done creating dialog. Now get input from the dialog and create report.
		if(response==DataEntryDialog.OK) {
			File dsm2ChannelsDirectory = dataEntryDialog.getDirectory(names[0]);
			String dsm2ChannelsFilename = dataEntryDialog.getFilename(names[0]);
			
			System.out.println("dsm2ChannelsDirectory, filename="+dsm2ChannelsDirectory+","+dsm2ChannelsFilename);
			
			CsdpFunctions.setDSMChannelsDirectory(dsm2ChannelsDirectory.toString());
			CsdpFunctions.setDSMChannelsFilename(dsm2ChannelsFilename);
			_DSMChannels = chanReadStore(dsm2ChannelsDirectory.toString(), dsm2ChannelsFilename);
	
			File dsm2HofDirectory = dataEntryDialog.getDirectory(names[1]);
			String dsm2HofFilename = dataEntryDialog.getFilename(names[1]);
			CsdpFunctions.setDSM2HofDirectory(dsm2HofDirectory);
			CsdpFunctions.setDSM2HofFilename(dsm2HofFilename);
			File outputDirectory = dataEntryDialog.getDirectory(names[2]);
			String outputFilename = dataEntryDialog.getFilename(names[2]);

			//if dsm2 hof file is not specified, exclude related quantities from report.
			boolean dsm2HofFileSpecified = true;
			if(dsm2HofDirectory==null || dsm2HofDirectory.getName()==null || dsm2HofDirectory.getName().length()<=0 ||
					dsm2HofFilename==null || dsm2HofFilename.length()<=0) {
				dsm2HofFileSpecified = false;
			}
				
			DSM2VirtualCrossSectionVolume dsm2VirtualCrossSectionVolume = null;
			Hashtable<String, Double> chanToVol = null;
			Hashtable<String, Double> chanToWettedArea = null;
			Hashtable<String, Double> chanToSurfArea = null;
			Hashtable<String, Double> chanToMaxAreaRatio = null;
			if(dsm2HofFileSpecified) {
				dsm2VirtualCrossSectionVolume = new DSM2VirtualCrossSectionVolume(dsm2HofDirectory.toString(), dsm2HofFilename);
				chanToVol = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.VOLUME_RESULTS);
				chanToWettedArea = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.WETTED_AREA_RESULTS);
				chanToSurfArea = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.SURFACE_AREA_RESULTS);
				chanToMaxAreaRatio = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.MAX_AREA_RATIO_RESULTS);
			}			
			_net.sortCenterlineNames();
	
			Vector<String> reportText = new Vector<String>(); 
			reportText.addElement("CSDP Network Summary Report for Elevation="+CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			reportText.addElement("");
			reportText.addElement("Network path: "+CsdpFunctions.getNetworkDirectory()+File.separator+CsdpFunctions.getNetworkFilename()+"."+CsdpFunctions.getNetworkFiletype());
			reportText.addElement("DSM2 channels path: "+CsdpFunctions.getDSMChannelsDirectory()+File.separator+CsdpFunctions.getDSMChannelsFilename());
			reportText.addElement("DSM2 output path: "+CsdpFunctions.getDSM2HofDirectory()+File.separator+CsdpFunctions.getDSM2HofFilename());
			reportText.addElement("");
			reportText.addElement("Channels.inp length: length specified for DSM2 in the DSM2 channels file above.");
			reportText.addElement("CSDP length: length calculated by the CSDP that will be used to replace the 'Channels.inp length'.");
			reportText.addElement("% change: the change in length CSDP vs Channels.inp");
			reportText.addElement("CSDP Volume: Channel volume calculated by CSDP for specified elevation assuming no inter-channel interpolation.");		
			reportText.addElement("CSDP Wetted Area: Wetted area calculated by CSDP for specified elevation assuming no inter-channel interpolation.");
			reportText.addElement("CSDP Surface Area: Surface area calculated by CSDP for specified elevation assuming no inter-channel interpolation.");
			if(dsm2HofFileSpecified) {
				reportText.addElement("DSM2 Volume: Channel volume calculated at specified elevation using virtual cross-sections from DSM2 output file");
				reportText.addElement("DSM2 Wetted Area: Wetted area calculated at specified elevation using virtual cross-sections from DSM2 output file");
				reportText.addElement("DSM2 Surface Area: Surface area calculated at specified elevation using virtual cross-sections from DSM2 output file");
			}
			reportText.addElement("CSDP Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using CSDP cross-sections");
			if(dsm2HofFileSpecified) {
				reportText.addElement("DSM2 Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using virtual cross-sections");
			}
			reportText.addElement("CSDP highest bottom elevation: The highest bottom elevation of all the cross-sections within the chanenl.");
			reportText.addElement("CSDP XS with no points: The indices of the cross-sections in the channel that have no points.");
			reportText.addElement("CSDP XS within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+
					" feet: The indices of the cross-sections in the channel that are within "+
					CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+" feet of each other.");
			reportText.addElement("CSDP XS with Min area: The index of the cross-section in the channel that has the smallest area at the specified elevation");
			reportText.addElement("CSDP XS with Max area: The index of the cross-section in the channel that has the largest area at the specified elevation");
			reportText.addElement("CSDP XS with duplicate stations: The indices of the cross-sections in the channel that have duplicate station values.");
			reportText.addElement("CSDP XS with -dK: The indices of the cross-sections in the channel that have negative dConveyance at any elevation.");
			reportText.addElement("CSDP XS with -dK in intertidal zone: the indices of the cross-sections in the channel that have negative dConveyance in the intertidal zone.");
			reportText.addElement("(intertidal zone is assumed to be limited to the range "+CsdpFunctions.INTERTIDAL_LOW_TIDE+" < Z < "+CsdpFunctions.INTERTIDAL_HIGH_TIDE+" ft NAVD88)");
			reportText.addElement("");

			if(dsm2HofFileSpecified) {
				reportText.addElement("Channel\t"
						+ "Channels.inp length\t"
						+ "CSDP Length\t"
						+ "% change\t"
						+ "CSDP Volume\t"
						+ "CSDP Wetted Area\t"
						+ "CSDP Surface Area\t"
						+ "DSM2 Volume\t"
						+ "DSM2 Wetted Area\t"
						+ "DSM2 Surface Area\t"
						+ "CSDP Max Area Ratio\t"
						+ "DSM2 Max Area Ratio\t"
						+ "CSDP Highest Bottom Elevation\t"
						+ "CSDP XS with no points\t"
						+ "CSDP XS within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+" feet\t"
						+ "CSDP XS with Min area\t"
						+ "CSDP XS with Max area\t"
						+ "CSDP XS with duplicate stations\t"
						+ "CSDP XS With -dk\t"
						+ "CSDP XS with -dk in intertidal zone");
			}else{
				reportText.addElement("Channel\t"
						+ "Channels.inp length\t"
						+ "CSDP Length\t"
						+ "% change\t"
						+ "CSDP Volume\t"
						+ "CSDP Wetted Area\t"
						+ "CSDP Surface Area\t"
						+ "CSDP Max Area Ratio\t"
						+ "CSDP Highest Bottom Elevation\t"
						+ "CSDP XS with no points\t"
						+ "CSDP XS within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+" feet\t"
						+ "CSDP XS with Min area\t"
						+ "CSDP XS with Max area\t"
						+ "CSDP XS with duplicate stations\t"
						+ "CSDP XS With -dk\t"
						+ "CSDP XS with -dk in intertidal zone");
			}//if hof file specified or not
//			Vector<String> externalChannelNumbers = dsm2VirtualCrossSectionVolume.getExternalChannelNumbers(); 
			for(int i=0; i<_DSMChannels.getNumChannels(); i++) {
				String chan = _DSMChannels.getChanNum(i);
//			}
//			for(int i=0; i<externalChannelNumbers.size(); i++) {
//				String chan = externalChannelNumbers.get(i);
				Centerline centerline = null;
				if(_net.centerlineExists(chan)) {
					centerline = _net.getCenterline(chan);
				}
				double csdpChanLength = -Double.MAX_VALUE; 
				double channelsInpLength = _DSMChannels.getLength(chan);
				double percentChange = -Integer.MAX_VALUE;
				double csdpVolume = -Double.MAX_VALUE;
				double csdpWettedArea = -Double.MAX_VALUE;
				double csdpSurfaceArea = -Double.MAX_VALUE;
				if(centerline!=null) {
					csdpChanLength=centerline.getLengthFeet();
					percentChange = 100.0 * ((csdpChanLength-channelsInpLength) / channelsInpLength);
					csdpVolume = centerline.getChannelVolumeEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
					csdpWettedArea = centerline.getChannelWettedAreaEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
					csdpSurfaceArea = centerline.getChannelSurfaceAreaEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				}
				double dsm2Vol = -Double.MAX_VALUE;
				double dsm2WetArea = -Double.MAX_VALUE;
				double dsm2SurfArea = -Double.MAX_VALUE;
				double dsm2MaxAreaRatio = -Double.MAX_VALUE;
				if(dsm2HofFileSpecified) {
					dsm2Vol = chanToVol.get(chan);
					dsm2WetArea = chanToWettedArea.get(chan);
					dsm2SurfArea = chanToSurfArea.get(chan);
					dsm2MaxAreaRatio = chanToMaxAreaRatio.get(chan);
				}
				double csdpMaxAreaRatio = -Double.MAX_VALUE;
				double csdpHighestBottomElev = -Double.MAX_VALUE;
				Vector<Integer> csdpXsWithNoPoints = null;
				HashSet<Integer> csdpXsWithinSpecifiedDistanceHashSet = null;
				int csdpXSWithMinArea = -Integer.MAX_VALUE;
				int csdpXSWithMaxArea = -Integer.MAX_VALUE;
				Vector<Integer> csdpXsWithDuplicateStations = null;
				Vector<Integer> csdpXSWithNegDK = null;
				Vector<Integer> csdpXSWithNegDKInIntertidal = null;
				if(centerline!=null) {
					csdpMaxAreaRatio = centerline.getMaxAreaRatio();
					csdpHighestBottomElev = centerline.getHighestBottomElevation();
					csdpXsWithNoPoints = centerline.getXSWithNoPointsIndices();
					csdpXsWithinSpecifiedDistanceHashSet = centerline.getXSWithinSpecifiedDistanceIndices(CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING);
					int[] minMaxAreaIndices = centerline.getMinMaxAreaXsectIndices();
					csdpXSWithMinArea = minMaxAreaIndices[0];
					csdpXSWithMaxArea = minMaxAreaIndices[1];
					csdpXsWithDuplicateStations = centerline.getDuplicateStationsXsectIndices();
					csdpXSWithNegDK = centerline.getNegDKXsectIndices();
					csdpXSWithNegDKInIntertidal = centerline.getNegDKIntertidalXsectIndices();
				}
				String na = "N/A";
				String csdpXSWithMinAreaString = null;
				String csdpXSWithMaxAreaString = null;
				if(csdpXSWithMinArea>=0 && csdpXSWithMaxArea>=0) {
					csdpXSWithMinAreaString = String.valueOf(csdpXSWithMinArea);
					csdpXSWithMaxAreaString = String.valueOf(csdpXSWithMaxArea);
				}else {
					csdpXSWithMinAreaString = na;
					csdpXSWithMaxAreaString = na;
				}
				
				String csdpChanLengthString = String.format("%.0f",  csdpChanLength);
				String percentLengthChangeString = String.format("%.0f", percentChange);
				String csdpVolumeString = String.format("%.1f", csdpVolume);
				String csdpWettedAreaString = String.format("%.1f", csdpWettedArea);
				String csdpSurfaceAreaString = String.format("%.1f", csdpSurfaceArea);
				String dsm2VolString = String.format("%.1f", dsm2Vol);
				String dsm2WetAreaString = String.format("%.1f", dsm2WetArea);
				String dsm2SurfAreaString = String.format("%.1f", dsm2SurfArea);
				String csdpMaxAreaRatioString = String.format("%.2f", csdpMaxAreaRatio);
				String dsm2MaxAreaRatioString = String.format("%.2f", dsm2MaxAreaRatio);
				String csdpHighestBottomElevString = String.format("%.2f", csdpHighestBottomElev);
				String csdpXsWithNoPointsString = abstractCollectionToString(csdpXsWithNoPoints);
				String csdpXsWithinSpecifiedDistanceString = abstractCollectionToString(csdpXsWithinSpecifiedDistanceHashSet);
				String csdpXsWithDuplicateStationsString = abstractCollectionToString(csdpXsWithDuplicateStations);
				String csdpXsWithNegDKString = abstractCollectionToString(csdpXSWithNegDK);
				String csdpXsWithNegDKInIntertidalString = abstractCollectionToString(csdpXSWithNegDKInIntertidal);
				
				if(dsm2HofFileSpecified) {
					if(centerline==null) {
						csdpChanLengthString = na;
						percentLengthChangeString = na;
						csdpVolumeString = na;
						csdpWettedAreaString = na;
						csdpSurfaceAreaString = na;

						csdpMaxAreaRatioString = na;
						csdpHighestBottomElevString = na;
						csdpXsWithNoPointsString = na;
						csdpXsWithinSpecifiedDistanceString = na;
						csdpXSWithMinAreaString = na;
						csdpXSWithMaxAreaString = na;
						csdpXsWithDuplicateStationsString = na;
						csdpXsWithNegDKString = na;
						csdpXsWithNegDKInIntertidalString = na;
					}
					reportText.addElement(chan+"\t"+channelsInpLength+"\t"+csdpChanLengthString+"\t"+
							percentLengthChangeString+"\t"+csdpVolumeString+"\t"+csdpWettedAreaString+"\t"+
							csdpSurfaceAreaString+"\t"+dsm2VolString+"\t"+dsm2WetAreaString+"\t"+dsm2SurfAreaString+"\t"+
							csdpMaxAreaRatioString+"\t"+dsm2MaxAreaRatioString+"\t"+csdpHighestBottomElevString+"\t"+
							csdpXsWithNoPointsString+"\t"+csdpXsWithinSpecifiedDistanceString+"\t"+csdpXSWithMinAreaString+"\t"+
							csdpXSWithMaxAreaString+"\t"+csdpXsWithDuplicateStationsString+"\t"+
							csdpXsWithNegDKString+"\t"+csdpXsWithNegDKInIntertidalString);
				}else {
					//no hof file
					if(centerline==null) {
						csdpChanLengthString = na;
						percentLengthChangeString = na;
						csdpVolumeString = na;
						csdpWettedAreaString = na;
						csdpSurfaceAreaString = na;

						csdpMaxAreaRatioString = na;
						csdpHighestBottomElevString = na;
						csdpXsWithNoPointsString = na;
						csdpXsWithinSpecifiedDistanceString = na;
						csdpXSWithMinAreaString = na;
						csdpXSWithMaxAreaString = na;
						csdpXsWithDuplicateStationsString = na;
						csdpXsWithNegDKString = na;
						csdpXsWithNegDKInIntertidalString = na;
					}
					//with no hof file, don't include dsm2 volume, wetted area, surface area, or max area ratio
					reportText.addElement(chan+"\t"+channelsInpLength+"\t"+csdpChanLengthString+"\t"+
							percentLengthChangeString+"\t"+csdpVolumeString+"\t"+csdpWettedAreaString+"\t"+
							csdpSurfaceAreaString+"\t"+
							csdpMaxAreaRatioString+"\t"+csdpHighestBottomElevString+"\t"+
							csdpXsWithNoPointsString+"\t"+csdpXsWithinSpecifiedDistanceString+"\t"+csdpXSWithMinAreaString+"\t"+
							csdpXSWithMaxAreaString+"\t"+csdpXsWithDuplicateStationsString+"\t"+
							csdpXsWithNegDKString+"\t"+csdpXsWithNegDKInIntertidalString);
					
				}
			}//while (for each channel)
	
			String outputPath = outputDirectory.toString()+File.separator+outputFilename;
			AsciiFileWriter asciiFileWriter = new AsciiFileWriter(_csdpFrame, outputPath);
			for(int i=0; i<reportText.size(); i++) {
				String line = reportText.get(i);
				asciiFileWriter.writeLine(line);
			}
			asciiFileWriter.close();
			JOptionPane.showMessageDialog(_csdpFrame, "Done writing Network Summary report", "Done", JOptionPane.INFORMATION_MESSAGE);
		}//if ok clicked		
	}//createNetworkSummaryReport
	
	/*
	 * creates a string representation of a Vector or HashSet of integer values 
	 * i.e. 1-2-56
	 */
	private String abstractCollectionToString(AbstractCollection<Integer> vector) {
		String returnValue = "";
		int index=0;
		if(vector!=null && vector.size()>0) {
			Iterator<Integer> iterator = vector.iterator(); 
			while(iterator.hasNext()) {
			//			for(int i=0; i<vector.size(); i++) {
				if(index>0) {
					returnValue += ",";
				}
//				returnValue += String.valueOf(vector.get(i));
				returnValue += String.valueOf(iterator.next());
				index++;
			}
		}
		return returnValue;
	}
	
//	public DSMChannels getDSMChannels() {
//		//First re-create CHANNEL information with CSDP channel lengths 
//		if (_DSMChannels == null) {
//			String channelsFilename = null;
//			// FileDialog fd = new FileDialog(_gui, "Open DSM2 channel
//			// connectivity file");
//			// fd.setVisible(true);
//			JFileChooser jfcChannelsInp = new JFileChooser();
//			String[] channelsInpExtensions = { "inp" };
//			int numChannelsInpExtensions = 1;
//			CsdpFileFilter channelsInpFilter = new CsdpFileFilter(channelsInpExtensions, numChannelsInpExtensions);
//			
//			jfcChannelsInp.setDialogTitle("Open DSM2 channel connectivity file");
//			jfcChannelsInp.setApproveButtonText("Open");
//			jfcChannelsInp.addChoosableFileFilter(channelsInpFilter);
//			jfcChannelsInp.setFileFilter(channelsInpFilter);
//
//			if (CsdpFunctions.getOpenDirectory() != null) {
//				jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
//			}
//			int filechooserState = jfcChannelsInp.showOpenDialog(_gui);
//			String directory = null;
//			if (filechooserState == JFileChooser.APPROVE_OPTION) {
//				channelsFilename = jfcChannelsInp.getName(jfcChannelsInp.getSelectedFile());
//				directory = jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;
//
//				// channelsFilename = fd.getFile();
//				// _directory = fd.getDirectory();
//				_DSMChannels = chanReadStore(directory, channelsFilename);
////				_gui.setDSMChannels(_DSMChannels);
//			}
//		} // if DSMChannels is null	
//		return _DSMChannels;
//	}
	
	/*
	 * Create DSM2 Input file with channel and irregular cross-section information
	 * optionally replace manning's n with constant value--perhaps useful for geometry updates.
	 */
	public void nCalculateDSM2V8Format(String channelsInputDirectory, String channelsInputFilename, 
			String outputChannelsPath, boolean replaceManningsN, double manningsNReplacementValue) {
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, outputChannelsPath);
		
		String filename = null;
		String centerlineName = null;
		Centerline centerline;
		Xsect xsect;
		double length;
		double distAlongCenterline;
		double normalizedDist;

		setDSMChannels(channelsInputDirectory, channelsInputFilename);
		
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
			String manning = null;
			if(replaceManningsN) {
				manning = String.format("%-10s", manningsNReplacementValue);
			}else {
				manning = String.format("%-10s", _DSMChannels.getManning(centerlineName)); 
			}
			String lineToWrite = 
					String.format("%-9s", centerlineName) + 
					String.format("%-8.0f", length) + 
					manning + 
					String.format("%-12s", _DSMChannels.getDispersion(centerlineName)) +
					String.format("%-8d", _DSMChannels.getUpnode(centerlineName))+
					String.format("%-8d", _DSMChannels.getDownnode(centerlineName));
			afw.writeLine(lineToWrite);
		}
		
		// now if there are any missing centerlines, write the data that was read from the dsm2 channels file
		for(int i=0; i<_DSMChannels.getNumChannels(); i++) {
			if(i==0) {
				afw.writeLine("# The following channels are missing from the CSDP network file that was used to create this file.");
				afw.writeLine("# data are copied from the previous version of the DSM2 input file. ");
			}
			String chan = _DSMChannels.getChanNum(i);
			if(!_net.centerlineExists(chan)) {
				String manning = null;
				if(replaceManningsN) {
					manning = String.format("%-10s", manningsNReplacementValue);
				}else {
					manning = String.format("%-10s", _DSMChannels.getManning(chan)); 
				}

				String channelDataLine = 
					String.format("%-9s", chan) + 
					String.format("%-8d", _DSMChannels.getLength(chan)) + 
					manning + 
					String.format("%-12s", _DSMChannels.getDispersion(chan)) +
					String.format("%-8d", _DSMChannels.getUpnode(chan))+
					String.format("%-8d", _DSMChannels.getDownnode(chan));
				afw.writeLine(channelDataLine);
			}
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
					double[] elevations = xsect.getSortedUniqueElevations();
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
		
		// now if there are any missing centerlines, write the cross-section data that was read from the dsm2 channels file
		for(int i=0; i<_DSMChannels.getNumXsectLayers(); i++) {
			
			if(i==0) {
				afw.writeLine("# The following cross-section layers belong to channels that are missing from the CSDP network file "
						+ "that was used to create this file.");
				afw.writeLine("# data are copied here from the previous version of the DSM2 input file. ");
			}
			String xsectLayerID = _DSMChannels.getXsectLayerID(i);
			String[] xliParts = xsectLayerID.split("_");
			String chan = xliParts[0];
//			System.out.println("xsectLayerID, chan="+xsectLayerID+","+chan);
			//if the network does not contain a centerline with the given name AND there are no cross-sections with points.
			if(!_net.centerlineExists(chan) || _net.getCenterline(chan).getNumXsectsWithPoints()<=0) {
				String xsectDataLine = 
					String.format("%-9s", chan) + 
					String.format("%-8s", _DSMChannels.getXsectDist(xsectLayerID)) + 
					String.format("%-10s", _DSMChannels.getXsectElev(xsectLayerID)) + 
					String.format("%-12s", _DSMChannels.getXsectArea(xsectLayerID)) +
					String.format("%-12s", _DSMChannels.getXsectWidth(xsectLayerID))+
					String.format("%-12s", _DSMChannels.getXsectWetPerim(xsectLayerID));
				afw.writeLine(xsectDataLine);
			}
		}//for each xsect layer in the DSM2 channels input file.
		
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
		if (CsdpFunctions.getLandmarkFilename() != null) {
			if (CsdpFunctions.getLandmarkFiletype() == null)
				CsdpFunctions.setLandmarkFiletype(LANDMARK_TYPE);
			LandmarkOutput loutput = LandmarkOutput.getInstance(directory, CsdpFunctions.getLandmarkFilename(),
					CsdpFunctions.getLandmarkFiletype(), _landmark);
			success = loutput.writeData();
			if (DEBUG)
				System.out.println("Done writing landmark file " + CsdpFunctions.getLandmarkFilename() + "."
						+ CsdpFunctions.getLandmarkFiletype());
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
		CsdpFunctions.setLandmarkFilename(_filename);
		CsdpFunctions.setLandmarkFiletype(_filetype);

		if (DEBUG)
			System.out.println("landmarkFilename, landmarkFiletype=" + CsdpFunctions.getLandmarkFilename() + ","
					+ CsdpFunctions.getLandmarkFiletype());

		LandmarkOutput loutput = LandmarkOutput.getInstance(directory, CsdpFunctions.getLandmarkFilename(),
				CsdpFunctions.getLandmarkFiletype(), _landmark);
		success = loutput.writeData();
		if (DEBUG)
			System.out.println("Done writing landmark file " + CsdpFunctions.getLandmarkFilename() + "."
					+ CsdpFunctions.getLandmarkFiletype());
		_csdpFrame.updateLandmarkFilename(_filename + "." + _filetype);
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
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);
		Hashtable xsectDisplayData = _net.findXsectDisplayRegion(centerlineName, xsectNum, thickness);
		XsectBathymetryData xsectBathymetryData = _bathymetryData.findXsectData(xsectDisplayData);

		if (_xsectGraph.contains(centerlineName + "_" + xsectNum)) {

		} else {
			if (xsectBathymetryData.getNumEnclosedValues() <= 0) {
				JOptionPane.showOptionDialog(null, "ERROR!  THERE ARE NO POINTS TO EXPORT! TRY INCREASING THICKNESS",
						"ERROR! NO POINTS TO DISPLAY", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
						_options, _options[0]);

			} else {
				// export data
				String directory = CsdpFunctions.getOpenDirectory().toString();

				BathymetryOutput aoutput = BathymetryOutput.getInstance(directory, filename, filetype, _bathymetryData);
				if (DEBUG)
					System.out.println("aoutput=" + aoutput);
				success = aoutput.extractXsectData(xsectBathymetryData, centerlineName, xsectNum, thickness);
			}
			_csdpFrame.setCursor(CsdpFunctions._defaultCursor);
		}
		return success;
	}// extractXsectData

	/**
	 * plot bathymetry and network data in cross-section view
	 */
	public void viewXsect(Xsect xsect, String centerlineName, int xsectNum, double thickness) {
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);
		Hashtable xsectDisplayData = _net.findXsectDisplayRegion(centerlineName, xsectNum, thickness);
		XsectBathymetryData xsectBathymetryData = _bathymetryData.findXsectData(xsectDisplayData);

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
			_xsectGraph.put(centerlineName + "_" + xsectNum, new XsectGraph(_csdpFrame, this, _bathymetryData, xsectBathymetryData,
					_net, centerlineName, xsectNum, thickness, _xsectColorOption));
			getXsectGraph(centerlineName, xsectNum).pack();
			getXsectGraph(centerlineName, xsectNum).setVisible(true);
			// }
			_csdpFrame.setCursor(CsdpFunctions._defaultCursor);
		}
	}// viewXsect

	public void repaintNetwork() {
		_csdpFrame.getPlanViewCanvas(0).setUpdateCanvas(true);
		_csdpFrame.getPlanViewCanvas(0).repaint();
	}
	
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

	public void dConveyanceReport(CsdpFrame gui) {
		_net.sortCenterlineNames();
		
		String message = "Cross-sections with negative dConveyance\n";
		message+="===========================================\n";
		int numLinesInMessage = 2;
		String centerlineName = null;
		Centerline centerline = null;
		double length;
		Xsect xsect = null;
		double distAlongCenterline;
		double normalizedDist;
		String allZeroArea = "";
		String allDuplicateStations = "";

		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			for(int j=0; j<centerline.getNumXsectsWithPoints(); j++) {
				xsect = centerline.getXsect(j);
				String dConveyanceReport = xsect.getNegativeDConveyanceReport();
				if(dConveyanceReport.length()>0) {
					message+=centerlineName+="_"+j+": ";
					message+=dConveyanceReport+"\n";
					numLinesInMessage++;
				}
			}//for each cross-section with user created points
		}//for each centerline
		message+="===========================================\n";
		MessageDialog mDialog = new MessageDialog(gui, "Cross-sections with negative dConveyance", message, false, 
				false, 90, numLinesInMessage);
		mDialog.setVisible(true);
	}//dConveyanceReport

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

		String message = "====================================================================\n";
		message+="centerline, length, channel volume-thousand sqft, for elevation " + elevation+"\n";
		message+="====================================================================\n";
		int numLinesInMessage=3;
		
		for (int i = 0; i < _net.getNumCenterlines(); i++) {
			centerlineName = _net.getCenterlineName(i);
			centerline = _net.getCenterline(centerlineName);
			length = centerline.getLengthFeet();
			double volume = centerline.getChannelVolumeEstimateNoInterp(elevation);
//			double volume = 0.0;
//			double lastDistAlongCenterline = -Double.MAX_VALUE;
//			double lastArea = -Double.MAX_VALUE;
//			for (int j = 0; j < centerline.getNumXsects(); j++) {
//				xsect = centerline.getXsect(j);
//				distAlongCenterline = xsect.getDistAlongCenterlineFeet();
//				double area = xsect.getAreaSqft(elevation);
//				// calculate volume
//				if (j == 0) {
//					volume += distAlongCenterline * area / 1000;
//				} else {
//					volume += (distAlongCenterline - lastDistAlongCenterline) * 0.5 * (area + lastArea) / 1000;
//				}
//				if (j == centerline.getNumXsects() - 1) {
//					volume += area * (length - distAlongCenterline) / 1000;
//				}
//				lastDistAlongCenterline = distAlongCenterline;
//				lastArea = area;
//			}
			message+=centerlineName + "," + length + "," + volume+"\n";
			numLinesInMessage++;
		}
		message+="====================================================================\n\n";

		message+="====================================================================\n";
		message+="cross-section, Area, Width, Depth, Hydraulic Depth, for elevation " + elevation+"\n";
		message+="====================================================================\n";
		numLinesInMessage+=4;
		
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
				line += "," + area + "," + width + "," + depth + "," + hydraulicDepth+"\n";
				message+=line;
				numLinesInMessage++;
			}
		}
		message+="====================================================================\n";
		numLinesInMessage++;
		MessageDialog md = new MessageDialog(_csdpFrame, "Cross-section area, width, depth summary", message, false, false, 90, numLinesInMessage);
		md.setVisible(true);
	}//awdSummary

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

	CsdpFrame _csdpFrame = null;
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
