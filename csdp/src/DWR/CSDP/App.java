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


import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.jfree.data.resources.DataPackageResources;
import org.junit.experimental.theories.Theories;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.ScatterMultiColor;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.drawable.DrawableTextBitmap;

import com.jogamp.newt.event.MouseListener;

import DWR.CSDP.XsectBathymetryData;
import DWR.CSDP.dialog.CenterlineOrReachSummaryWindow;
import DWR.CSDP.dialog.XsectSlideshowDialog;
import DWR.CSDP.dialog.DataEntryDialog;
import DWR.CSDP.dialog.GISSummaryStatisticGraphFrame;
import DWR.CSDP.dialog.MessageDialog;
import vista.graph.GECanvas;

/**
 * Main application class
 *
 * @author
 * @version $Id: App.java,v 1.6 2005/04/07 22:10:41 btom Exp $
 */
public class App {

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
	Hashtable<String, XsectGraph> _xsectGraph = new Hashtable<String, XsectGraph>();

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
	private Hashtable<String, CenterlineOrReachSummaryWindow> allOpenCenterlineOrReachSummaryWindowsHashtable = 
			new Hashtable<String, CenterlineOrReachSummaryWindow>();

	public static final int ADDING_XSECT_GRAPH = 10;
	public static final int REMOVING_XSECT_GRAPH = 20;
	
	/*
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };
	
	/*
	 *  A bathymetry data object used not for display in plan view, but in one side of a XsectSlideshow window 
	 */
	private BathymetryData xsectSlideshowAlternateBathymetryData;

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

	/*
	 * Creates a BathymetryData object not for display in Plan view; only for use in XsectSlideshowDialog instances.
	 * 
	 */
	private void createXsectSlideshowAlternateBathymetryData(String directory, String filename) {
		xsectSlideshowAlternateBathymetryData = null;
		BathymetryInput binput = null;
//		_csdpFrame = (CsdpFrame) gui;
//		boolean bWrite = true;
		int numLines = 0;
		// read bathymetry file and store in BathymetryPlot object
		System.out.println("before error: filename="+filename);
		String[] parts = filename.split("\\.");
		String filetype = parts[parts.length-1];
		int extIndex = filename.lastIndexOf(".cdp");
		String filenameNoExtensionString = filename.substring(0, extIndex);
		
		binput = BathymetryInput.getInstance(_csdpFrame, directory, filenameNoExtensionString + "." + filetype);
		xsectSlideshowAlternateBathymetryData = binput.readData();
		xsectSlideshowAlternateBathymetryData.sortYearIndices();
		if (filetype.equals("prn"))
			xsectSlideshowAlternateBathymetryData.sortBathymetryData();
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

	/*
	 * Writes bathymetry data to file, only writing data that are inside or outside a polygon represented by a given centerline
	 */
	public boolean fSaveBathymetryDataInsideOrOutsidePolygonCenterline(String directory, String filename, Centerline centerline, boolean saveInside) {
		boolean success = false;
		parseFilename(filename);
//		double[] plotBoundaries = _bathymetryPlot.getCurrentZoomState().getPlotBoundaries();

		if (_filetype.equals(ASCII_TYPE)) {
			BathymetryOutput aoutput = BathymetryOutput.getInstance(directory, _filename, ASCII_TYPE, _bathymetryData);
			success = aoutput.writeData(centerline, saveInside);
		} else if (_filetype.equals(BINARY_TYPE)) {
			BathymetryOutput boutput = BathymetryOutput.getInstance(directory, _filename, BINARY_TYPE, _bathymetryData);
			success = boutput.writeData(centerline, saveInside);
		} else {
			System.out.println("filetype not defined for extension " + _filetype);
		}
		return success;
	}
	
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
//		try {
//			if (_filetype.equals(DSMChannels_TYPE)) {
				DSMChannelsInput chanInput = DSMChannelsInput.getInstance(directory, _filename + "." + DSMChannels_TYPE);
				_DSMChannels = chanInput.readData();
				if (DEBUG)
					System.out.println("Done reading ascii DSMChannels data file");
//			} else
//				System.out.println("filetype not defined for extension " + _filetype);
//		}catch(Exception e) {
//			JOptionPane.showMessageDialog(_csdpFrame, "Exception caught in App.chanReadStore: "+e.getMessage(), 
//					"Error", JOptionPane.ERROR_MESSAGE);
//		}
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
	public boolean nExportToWKT(Network net, String wktPath, boolean createPolygonObjects, boolean saveFirstLastPointsOnly) {
		System.out.println("wktPath="+wktPath);
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, wktPath);
		afw.writeLine("id;wkt");
		boolean success = true;
		try{
			int numCenterlines = net.getNumCenterlines();
			for(int i=0; i<numCenterlines; i++) {
				String centerlineName = net.getCenterlineName(i);
				Centerline centerline = (Centerline) net.getCenterline(centerlineName);
				String lineToWrite = null;
				if(createPolygonObjects) {
					lineToWrite = centerlineName+";POLYGON((";
				}else {
					lineToWrite = centerlineName+";LINESTRING(";
				}

				int numPoints = centerline.getNumCenterlinePoints();
				for(int j=0; j<numPoints; j++) {
					CenterlinePoint cp = centerline.getCenterlinePoint(j);
					boolean writePoint = false;
					//if writing straight lines (useful for gridmaps) or not
					if(saveFirstLastPointsOnly) {
						if(j==0 || j==numPoints-1) {
							writePoint = true;
						}
					}else {
						writePoint = true;
					}
					if(writePoint) {
						if(j>0) {
							lineToWrite += ",";
						}
						lineToWrite+=CsdpFunctions.feetToMeters(cp.getXFeet())+" "+CsdpFunctions.feetToMeters(cp.getYFeet());
					}
				}
				lineToWrite += ")"; 
				if(createPolygonObjects) {
					lineToWrite += ")";
				}
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
	 * save cross-section locations to WKT file
	 */
	public boolean nExportXsectMidpointCoordToWKT(Network net, String wktPath) {
		System.out.println("wktPath="+wktPath);
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, wktPath);
		afw.writeLine("id;wkt");
		boolean success = true;
		try{
			int numCenterlines = net.getNumCenterlines();
			for(int i=0; i<numCenterlines; i++) {
				String centerlineName = net.getCenterlineName(i);
				Centerline centerline = (Centerline) net.getCenterline(centerlineName);
				for(int j=0; j<centerline.getNumXsects(); j++) {
					double[] centerlineMidpointCoord = net.getCenterlineMidpointCoord(centerlineName, j);
					String lineToWrite = centerlineName+"_"+j+";POINT(";
					lineToWrite +=CsdpFunctions.feetToMeters(centerlineMidpointCoord[CsdpFunctions.x1Index])+" ";
					lineToWrite +=CsdpFunctions.feetToMeters(centerlineMidpointCoord[CsdpFunctions.y1Index]);
					lineToWrite += ")"; 
					afw.writeLine(lineToWrite);
				}
			}
			afw.close();
			success = true;
		}catch(Exception exception) {
			success = false;
		}
		return success;


	}
	
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
		JOptionPane.showMessageDialog(_csdpFrame, "Saved Specified Channels to network file","Saved",JOptionPane.INFORMATION_MESSAGE);
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
			String startingDirectory = "d:/delta/";
			String[] directoryFilename = CsdpFunctions.selectFilePath(_csdpFrame, "Open DSM2 channel connectivity file (channels.inp)", 
					new String[]{"inp"}, startingDirectory, false);
			_DSMChannels = chanReadStore(directoryFilename[0], directoryFilename[1]);
			CsdpFunctions.setDSMChannelsDirectory(directoryFilename[0]);
			CsdpFunctions.setDSMChannelsFilename(directoryFilename[1]);
			CsdpFunctions.setOpenDirectory(directoryFilename[0]);
		}
		return _DSMChannels;
	}//getInputFilePath
	
	
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
			String outputChannelsPath, boolean replaceManningsN, double manningsNReplacementValue, 
			boolean replaceDispersionFactor, double dispersionFactorReplacementValue) {
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, outputChannelsPath);
		
		String filename = null;
		String centerlineName = null;
		Centerline centerline;
		Xsect xsect;
		double length;
		double distAlongCenterline;
		double normalizedDist;

		setDSMChannels(channelsInputDirectory, channelsInputFilename);
		afw.writeLine("# " + CsdpFunctions.getCurrentDatetimeFormattedForDSM2InputComments());
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
			String dispersionFactor = null;
			if(replaceDispersionFactor) {
				dispersionFactor = String.format("%-12s", dispersionFactorReplacementValue);
			}else {
				dispersionFactor = String.format("%-12s", _DSMChannels.getDispersion(centerlineName));
			}
			String lineToWrite = 
					String.format("%-9s", centerlineName) + 
					String.format("%-8.0f", length) + 
					manning + 
					dispersionFactor +
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
		if(_landmark==null) _landmark = new Landmark(_csdpFrame);
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
	 * If name exists in XsectGraph array, then rename 1) the Hashtable key, and 2) the value of _xsectNum in XsectGraph
	 * @param addOrRemoveIndex : the index of the cross-section that is being added or removed
	 * @param addingOrRemoving indicates if addOrRemoveIndex is for adding or removing
	 * note: moving a cross-section is equivalent to removing one and adding another, so this method will simply be called twice
	 */
	public void renameOpenXsectGraphs(String centerlineName, ResizableIntArray xsectIndices, int addOrRemoveIndex, int addingOrRemoving) {
		XsectGraph graph = null;
		Hashtable<String, XsectGraph> sortedGraphs = new Hashtable<String, XsectGraph>();

		for (int i = 0; i <= xsectIndices.getSize() - 1; i++) {
			graph = getXsectGraph(centerlineName, xsectIndices.get(i));
			//when adding a cross-section, need to change the cross-section numbers of open XsectGraph windows 
			if (graph != null) {
				int newIndex = i;
				if(addingOrRemoving==ADDING_XSECT_GRAPH) {
					if(i>=addOrRemoveIndex) {
						newIndex++;
					}
				}else if(addingOrRemoving==REMOVING_XSECT_GRAPH) {
					if(i>addOrRemoveIndex) {
						newIndex--;
					}
				}else {
					JOptionPane.showMessageDialog(_csdpFrame, "error in App.renameOpenXsectGraphs: addOrRemoveIndex is not ADDING_XSECT_GRAPH or"
							+ " REMOVING_XSECT_GRAPH.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				sortedGraphs.put(centerlineName + "_" + newIndex, graph);
				graph.updateXsectNum(newIndex);
			}
		} // for i
		_xsectGraph = sortedGraphs;
	}// renameOpenXsectGraphs

	public boolean hasOpenXsectGraph(String centerlineName, int selectedXsectNum) {
		if(_xsectGraph.containsKey(centerlineName+"_"+selectedXsectNum)) {
			return true;
		}else {
			return false;
		}
	}

	/*
	 * disposes of open xsect graph
	 */
	public void disposeXsectGraph(String centerlineName, int selectedXsectNum) {
		String keyString = centerlineName+"_"+selectedXsectNum;
		XsectGraph xsectGraph = _xsectGraph.get(keyString);
		xsectGraph.dispose();
		_xsectGraph.remove(keyString);
	}
	
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
			xg.redoNextPaint();
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
				if (xg.getXsect()._isUpdated || xg.getChangesKept()) {			
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
			xg.redoNextPaint();
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

	/*
	 * Given 3 centerlines:
	 * 1. The channel centerline
	 * 2. A Centerline representing a polygon used to estimate GIS conveyance characteristics for the centerline
	 * 3. One or more centerlines representing a levee. Levee centerlines can be specified as an index, which is typically a  
	 * 		number, and is the portion of the centerline name that occurs before the first underscore in the centerline name. 
	 * 
	 * Move all the points that are on the levee side of the centerline (determined using the channel centerline) to the
	 * nearest point on the levee centerline
	 */
	public void snapPolygonCenterlinePointsToLeveeCenterline(String polygonCenterlineName, 
			String[] leveeCenterlineNamesOrIndices) {
		if(DEBUG) System.out.println("movePolygonCenterlinePointsToLeveeCenterline: polygonCenterlineName, "
				+ "leveeCenterlineName="+polygonCenterlineName+","+leveeCenterlineNamesOrIndices);

		Centerline polygonCenterline = _net.getCenterline(polygonCenterlineName);
//		Centerline channelCenterline = _net.getCenterline(channelCenterlineName);

		Centerline leveeCenterline = null;
		if(leveeCenterlineNamesOrIndices.length==1) {
			leveeCenterline = _net.getCenterlineByNameOrIndex(leveeCenterlineNamesOrIndices[0]);
		}else {
			String newCenterlineName = "";
			Centerline[] centerlines = new Centerline[leveeCenterlineNamesOrIndices.length];
			for(int i=0; i<leveeCenterlineNamesOrIndices.length; i++) {
				centerlines[i] = _net.getCenterlineByNameOrIndex(leveeCenterlineNamesOrIndices[i]);
				newCenterlineName += leveeCenterlineNamesOrIndices[i]+"_";
			}
			leveeCenterline = new Centerline(newCenterlineName);
			//the values in this array determine which order to add points--UPSTREAM_TO_DOWNSTREAM 
			//or DOWNSTREAM_TO_UPSTREAM
			int[] pointOrder = new int[centerlines.length]; 
			for(int i=0; i<centerlines.length; i++) {
				if(i==centerlines.length-1) {
					Centerline lastCenterline = centerlines[i-1];
					Centerline currentCenterline = centerlines[i];
					pointOrder[i]= currentCenterline.getPointOrderingForContinuousCenterline(lastCenterline, true); 
				}else {
					Centerline currentCenterline = centerlines[i];
					Centerline nextCenterline = centerlines[i+1];
					pointOrder[i] = currentCenterline.getPointOrderingForContinuousCenterline(nextCenterline, false);
				}
			}
			
			for(int i=0; i<centerlines.length; i++) {
				Centerline c = centerlines[i];
				if(pointOrder[i]==Centerline.UPSTREAM_TO_DOWNSTREAM) {
					for(int j=0; j<c.getNumCenterlinePoints(); j++) {
						double xFeet = c.getCenterlinePoint(j).getXFeet();
						double yFeet = c.getCenterlinePoint(j).getYFeet();
						leveeCenterline.addDownstreamCenterlinePointFeet(xFeet, yFeet);
					}
				}else if(pointOrder[i]==Centerline.DOWNSTREAM_TO_UPSTREAM) {
					for(int j=c.getNumCenterlinePoints()-1; j>0; j--) {
						double xFeet = c.getCenterlinePoint(j).getXFeet();
						double yFeet = c.getCenterlinePoint(j).getYFeet();
						leveeCenterline.addDownstreamCenterlinePointFeet(xFeet, yFeet);
					}
				}
			}
		}//if leveeCenterlineNamesOrIndices has 1 or more centerlines

		//if the perpendicular distance from the channel polygon point to the levee is greater than this 
		//value, point will not be moved 
		double maxAllowableDist = 1000.;
		//identify which line segment has the shortest dist.
		for(int i=0; i<polygonCenterline.getNumCenterlinePoints(); i++) {
			CenterlinePoint polygonCenterlinePoint = polygonCenterline.getCenterlinePoint(i);
			double x3 = polygonCenterlinePoint.getXFeet();
			double y3 = polygonCenterlinePoint.getYFeet();
			double shortestDistToLevee = Double.MAX_VALUE;
			int shortestDistLeveeSegmentIndex = -Integer.MAX_VALUE;
			double closestLeveePointX = -Double.MAX_VALUE;
			double closestLeveePointY = -Double.MAX_VALUE;
			for(int j=1; j<leveeCenterline.getNumCenterlinePoints(); j++) {
				CenterlinePoint lastLeveeCenterlinePoint = leveeCenterline.getCenterlinePoint(j-1);
				CenterlinePoint currentLeveeCenterlinePoint = leveeCenterline.getCenterlinePoint(j);
				double x1 = lastLeveeCenterlinePoint.getXFeet();
				double y1 = lastLeveeCenterlinePoint.getYFeet();
				double x2 = currentLeveeCenterlinePoint.getXFeet();
				double y2 = currentLeveeCenterlinePoint.getYFeet();

				double distToLevee = CsdpFunctions.shortestDistLineSegment(x1, x2, x3, y1, y2, y3, maxAllowableDist, false);
				//now see if any centerline point is closer. This will be necessary if the polygon point is not 
				//contained within any of the rectangles created for each levee line segment, which can occur if the levee
				//centerline is convex
				double distToLastLeveePoint = Math.sqrt((x1-x3)*(x1-x3)+ (y1-y3)*(y1-y3));
				double distToCurrentLeveePoint = Math.sqrt((x2-x3)*(x2-x3)+ (y2-y3)*(y2-y3));
				
				if(distToLevee<shortestDistToLevee) {
					shortestDistToLevee = distToLevee;
					shortestDistLeveeSegmentIndex = j;
					closestLeveePointX = CsdpFunctions.findXIntersection(x1,x2,x3,y1,y2,y3);
					closestLeveePointY = CsdpFunctions.findYIntersection(x1,x2,x3,y1,y2,y3);
					if(DEBUG) {
						System.out.println("MovePolygonCenterlinePointsToLeveeCenterline: found point: "
								+ "shortestDistToLevee, shortestDistLeveeSegmentIndex, shortestDistXIntersection, "
								+ "shortestDistYIntersection="+
								shortestDistToLevee+","+shortestDistLeveeSegmentIndex+","+closestLeveePointX+","+
								closestLeveePointY);
					}
				}
				if(distToLastLeveePoint<shortestDistToLevee) {
					shortestDistToLevee = distToLastLeveePoint;
					shortestDistLeveeSegmentIndex = j;
					closestLeveePointX = x1;
					closestLeveePointY = y1;
				}
				if(distToCurrentLeveePoint<shortestDistToLevee) {
					shortestDistToLevee = distToCurrentLeveePoint;
					shortestDistLeveeSegmentIndex = j;
					closestLeveePointX = x2;
					closestLeveePointY = y2;
				}
			}//for
			if(shortestDistToLevee < Double.MAX_VALUE && shortestDistLeveeSegmentIndex>0 &&
					closestLeveePointX>0.0 && closestLeveePointY>0.0) {
				//don't move the point if a line connecting the point and the closest levee point intersects the 
				//channel centerline. NOT a good idea...because channel centerline is sometimes in the wrong place.
//				if(!channelCenterline.intersectsLine(closestLeveePointX, x3, closestLeveePointY, y3)) {
//					//move the point
//					polygonCenterlinePoint.putXFeet(closestLeveePointX);
//					polygonCenterlinePoint.putYFeet(closestLeveePointY);
//				}

				//instead: Don't move the point if the same line (extended out beyond the closest levee point, because sometimes
				//the polygon centerline is on the far side of the levee) intersects another line segment or point on the 
				//polygon centerline.
				//need to identify a point that is .01 feet away from the polygon point x3,y3, in the direction of the
				//closest levee point
				double theta = CsdpFunctions.getTheta(x3, closestLeveePointX, y3, closestLeveePointY);
				double x3Prime = x3+0.01*Math.cos(theta);
				double y3Prime = y3+0.01*Math.sin(theta);
				double beyondClosestLeveePointX = closestLeveePointX+maxAllowableDist*Math.cos(theta);
				double beyondClosestLeveePointY = closestLeveePointY+maxAllowableDist*Math.sin(theta);
				if(!polygonCenterline.intersectsLine(beyondClosestLeveePointX, x3Prime, beyondClosestLeveePointY, y3Prime)) {
					//move the point
					polygonCenterlinePoint.putXFeet(closestLeveePointX);
					polygonCenterlinePoint.putYFeet(closestLeveePointY);
				}
			
			}//if
		}//for
		_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
		_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
		_csdpFrame.getPlanViewCanvas(0).repaint();
	}//movePolygonCenterlinePointsToLeveeCenterline

	/*
	 * Create the Network Summary Report using input from user.
	 */
	public void createNetworkSummaryReport() {
		String title = "Create Network Summary Report";
		String instructions = "<HTML><BODY><H2>A network summary report uses the following inputs:</H2><BR>"
				+ "1. An existing channels.inp file<BR>"
				+ "2. The currently loaded network file<BR>"
				+ "3. (Optional): A DSM2 output (.hof) file which was created from the network file by running DSM2-Hydro with printlevel>=5<BR><BR>"
				+ "4. (Optional): A series of comma separated strings specifying channel groups, for calculating volumes for groups of channels. <BR>"
				+ "Example: '290-294, 438_443_444_450_570_571_574_575'<BR><BR>"
				+ "5. (Optional): A series of filenames (.csv) containing GIS volume and 2D area results<BR>"
				+ "6. (Optional, required if using GIS results): A 2m validity file, indicating whether or not the 2m DEM coverage in each channel<BR>"
				+ " is sufficient<BR>"
				+ "7. (Optional, for channel min elevation): A file containing historical minimum stage elevation for each channel<BR>"
				+ "8. Check the box if you want to include dsm2-2m DEM and dsm2-10m DEM difference plots<BR>"
				+ "<H2>To write to an output file, for each channel, for a given stage (usually 0.0 NAVD)</H2><BR>"
				+ "1. A comparison of channel lengths from the channels.inp file vs channel lengths calculated using the network file<BR>"
				+ "2. Conveyance characteristics, CSDP, DSM2 Virtual Cross-section, and GIS<BR>"
				+ "3. Cross-section diagnostic information<BR>"
				+ "4. Channel volumes from CSDP, DSM2 Virtual cross-sections, and GIS, with summary statistics.<BR><BR>"
				+ "<H2>Output will be written to a tab delimited .txt file, which can be imported into Excel<H2><BR>"
				+ "If all inputs specified, GIS vs DSM2 volume comparison plots will appear in a separate window.</BODY></HTML>";

		//Create dialog to get input from user.
		String[] names = new String[] {
				"Channels.inp file", 
				"DSM2 output (.hof) file", 
				"Channel Groups", 
				"GIS Volume filenames", 
				"2m DEM CutFill Validity file",
				"Historical Min Stage file (.csv)",
				"Output file (tab delimited .txt)",
				"Include difference plots"
				};
		String[] defaultValues = new String[8];
		if(CsdpFunctions.getDSMChannelsDirectory()!=null && CsdpFunctions.getDSMChannelsFilename()!=null) {
			defaultValues[0] = CsdpFunctions.getDSMChannelsDirectory().toString()+File.separator+CsdpFunctions.getDSMChannelsFilename();
		}
		if(CsdpFunctions.getDSM2HofDirectory()!=null && CsdpFunctions.getDSM2HofFilename()!=null) {
			defaultValues[1] = CsdpFunctions.getDSM2HofDirectory().toString()+File.separator+CsdpFunctions.getDSM2HofFilename();
		}
		//these are the channel groups for which we want to calculate volumes. Some of these are represented by polygons in the 
		//March 2019 GIS Volume calculation, and some are not. For those groups that are not represented by polygons, volumes will
		//be calculated as the sum of individual channel volumes. However, entering such a long string as a default makes the dialog too wide.
//		defaultValues[2] = "WesternSuisunBay:439_440_441_451_452_453_454, "
//				+ "GrizzlyBay:448_449_572_573, "
//				+ "EasternSuisunBay:438_443_444_450_570_571_574_575,"
//				+ "SacRNrConfluence:290-294,"
//				+ "ShermanLake:281_282_295_296_297_301,"
//				+ "ThreeMileSl:309-310,"
//				+ "LowerSac:430-436_290_294,"
//				+ "LowerSJR:42-48_83_49_50_51_284_300_52,"
//				+ "UpperSac:410-424_426-429";
		defaultValues[2] = "";
		defaultValues[3] = "";
		defaultValues[4] = "";
		defaultValues[5] = "";
		defaultValues[6] = CsdpFunctions.getNetworkDirectory().toString()+File.separator+"networkSummary.txt";

		defaultValues[7] = "false";
		
		int[] dataTypes = new int[] {
				DataEntryDialog.FILE_SPECIFICATION_TYPE, 
				DataEntryDialog.FILE_SPECIFICATION_TYPE, 
				DataEntryDialog.STRING_TYPE, 
				DataEntryDialog.MULTI_FILE_SPECIFICATION_TYPE, 
				DataEntryDialog.FILE_SPECIFICATION_TYPE,
				DataEntryDialog.FILE_SPECIFICATION_TYPE,
				DataEntryDialog.FILE_SPECIFICATION_TYPE,
				DataEntryDialog.BOOLEAN_TYPE
				};
		String[] extensions = new String[] {"inp", "hof", "", "csv", "csv", "csv", "txt", ""};
		String[] tooltips = new String[] {"An existing channels.inp file", 
				"(Optional): A DSM2 output (.hof) file which was created from the "
				+ "network file by running DSM2-Hydro with printlevel>=5",
				"(Optional): A series of comma separated strings specifying channel groups, "
				+ "for calculating volumes for groups of channels."
				+ "Example: '290-294, 438_443_444_450_570_571_574_575'",
				"(Optional): A series of filenames (.csv) containing GIS volume and 2D area results",
				"(Optional, required if using GIS results): A 2m validity file, indicating whether or not the 2m DEM "
				+ "coverage in each channel is sufficient",
				"(Optional, for channel min stage): A file containing historical minimum stage for each channel",
				"Specify an output file",
				"Check the box if you want to include dsm2-2m DEM and dsm2-10m DEM difference plots"};
		
		//require channels.inp and output file name, but not hof file
		boolean[] disableIfNull = new boolean[] {true, false, false, false, false, false, true, true}; 
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

			//if dsm2 hof file is not specified, exclude related quantities from report.
			boolean dsm2HofFileSpecified = true;
			if(dsm2HofDirectory==null || dsm2HofDirectory.getName()==null || dsm2HofDirectory.getName().length()<=0 ||
					dsm2HofFilename==null || dsm2HofFilename.length()<=0) {
				dsm2HofFileSpecified = false;
			}

			Vector<String>chanGroupNamesInOrderVector = null; 
			Hashtable<String, String> chanGroupsHashtable = null;
			String chanGroupSpecificationString = dataEntryDialog.getValue(names[2]).trim();
			if(chanGroupSpecificationString!=null && chanGroupSpecificationString.length()>0) {
				String[] chanGroupStrings = chanGroupSpecificationString.split(",");
				for(int i=0; i<chanGroupStrings.length; i++) {
					if(chanGroupNamesInOrderVector==null) chanGroupNamesInOrderVector = new Vector<String>();
					if(chanGroupsHashtable==null) chanGroupsHashtable = new Hashtable<String, String>();
					String chanGroupEntry = chanGroupStrings[i].trim();
					//a chanGroupEntry may optionally have a description followed by a colon preceding the channel numbers. Example:
					//you can use GrizzlyBay:448_449_572_573 or 448_449_572_573. If there is no colon, the channel numbers will be used as the group name.
					if(chanGroupEntry.indexOf(":")>0) {
						String[] parts = chanGroupEntry.split(":");
						chanGroupsHashtable.put(parts[0], parts[1]);
						chanGroupNamesInOrderVector.addElement(parts[0]);
					}else {
						chanGroupsHashtable.put(chanGroupEntry, chanGroupEntry);
						chanGroupNamesInOrderVector.addElement(chanGroupEntry);
					}
				}
			}
			
			String[] gisVolumeFilenames = dataEntryDialog.getMultipleFilePaths(names[3]);
			File twoMeterValidityDirectory = dataEntryDialog.getDirectory(names[4]);
			String twoMeterValidityFilename = dataEntryDialog.getFilename(names[4]);
			File histMinStageDirectory = dataEntryDialog.getDirectory(names[5]);
			String histMinStageFilename = dataEntryDialog.getFilename(names[5]);
			File outputDirectory = dataEntryDialog.getDirectory(names[6]);
			String outputFilename = dataEntryDialog.getFilename(names[6]);
			NetworkSummary networkSummary = new NetworkSummary(_csdpFrame, _net, _DSMChannels, dsm2HofDirectory, dsm2HofFilename, 
					outputDirectory+File.separator+outputFilename, chanGroupNamesInOrderVector, chanGroupsHashtable, twoMeterValidityDirectory, 
					twoMeterValidityFilename, gisVolumeFilenames, histMinStageDirectory, histMinStageFilename);
			networkSummary.writeResults();

			boolean includeDifferencePlots = Boolean.parseBoolean(dataEntryDialog.getValue(names[7]));
			if(dsm2HofFileSpecified && gisVolumeFilenames!=null && gisVolumeFilenames.length>0 && twoMeterValidityDirectory!=null &&
					twoMeterValidityFilename!=null) {
				new GISSummaryStatisticGraphFrame(this._csdpFrame, networkSummary, includeDifferencePlots);
			}
		}
	}//createNetworkSummaryReport

	public void removeAllCrossSections(CsdpFrame gui) {
		int response = JOptionPane.showConfirmDialog(gui, "Remove All Cross-Sections?", "Are you sure?", JOptionPane.YES_NO_OPTION);
		System.out.println("response="+response);
		if(response==JOptionPane.YES_OPTION) {
			System.out.println("removing");
			gui.getNetwork().removeAllCrossSections();
		}
	}

	/*
	 * For each landmark, determines nearest Centerline and distance from the upstream end of the nearest point on the centerline
	 * to the landmark. Optionally draws a cross-section line, which can be useful for verification.
	 * 
	 * The first step is to identify the centerline by determining the closest centerline. Two distances are compared to determine the closest:
	 * 
	 * 1. The distance from the landmark to each endpoint of each centerline. This is necessary because sometimes landmarks are placed such that 
	 * they are not within the infinitely wide rectangle used in condition 1.  
	 * 2. The closest perpendicular distance a centerline segment. To be counted, the landmark must be contained in an infinitely wide rectangle 
	 * that has two sides parallel to the line segment, and two sides intersecting the endpoints of the centerline.
	 */
	public void createDSM2OutputLocationsForLandmarks(CsdpFrame csdpFrame) {
		String title = "Create DSM2 Output Locations Specification file (output.inp) for landmarks";
		String instructions = "<HTML><BODY><H2>Finds the nearest channel/distance for each landmark</H2><BR>"
				+ "Using the currently loaded network and landmark files (with landmarks representing <BR>"
				+ "stations/output locations, for example), for each landmark:<BR>"
				+ "1. Identify the closest channel centerline and the distance along the centerline from the upstream end <BR>"
				+ "of the closest point on the centerline to the landmark<BR>"
				+ "2. Write the results to a DSM2 output.inp file<BR>"
				+ "3. Create cross-section lines, which can be used for verification<BR><BR>"
				+ "Notes:"
				+ "1. You may want to remove all existing cross-section lines in the network. To do so:<BR>"
				+ "		a. Save-As to save the network file to a different name.<BR>"
				+ "     b. Tools-Remove all cross-sections<BR>"
				+ "     c. Save the network file.<BR>"
				+ "2. If the routine is selecting wrong centerlines, consider deleting some centerlines"
				+ "3. Optionally specify a DSM2 channels.inp file. If you do so, channel distance will be adjusted by<BR>"
				+ "     multiplying the distance by the ratio of the channels.inp length to the centerline length. This is <BR>"
				+ "     done because in DSM2 v8.2, the channel lengths are mostly not equal to the CSDP centerline lengths.<BR>"
				+ "4. Specify the output file path, and the values for the fields in the OUTPUT_CHANNEL section.<BR>"
				+ "5. Check the box if you wish to also output the distance from the centerline to the landmar</BODY></HTML>";

		//Create dialog to get input from user.
		String[] names = new String[] {"Output file (.inp)","DSM2 Channels file", "Variable(s)", "Interval", "Period Op", "File", "Output dist to centerline"};
		String[] defaultValues = new String[7];
		defaultValues[0] = CsdpFunctions.getOpenDirectory().toString()+File.separator+"landmarkChanDist.inp";
		defaultValues[1] = "";
		defaultValues[2] = "flow/stage/ec";
		defaultValues[3] = "${FINE_OUT}";
		defaultValues[4] = "inst";
		defaultValues[5] = "${HYDROOUTDSSFILE}";
		defaultValues[6] = "false";
		
		int[] dataTypes = new int[] {
				DataEntryDialog.FILE_SPECIFICATION_TYPE,
				DataEntryDialog.FILE_SPECIFICATION_TYPE,
				DataEntryDialog.STRING_TYPE,
				DataEntryDialog.STRING_TYPE,
				DataEntryDialog.STRING_TYPE,
				DataEntryDialog.STRING_TYPE,
				DataEntryDialog.BOOLEAN_TYPE
				};
		String[] extensions = new String[] {"inp","inp","","","","",""};
		String[] tooltips = new String[7];
		tooltips[0] = "Output file will contain landmark name, channel number, and distance";
		tooltips[1] = "DSM2 channels file, which is only needed if channel lengths differ from CSDP centerline lengths";
		tooltips[2] = "Value(s) for DSM2 OUTPUT_CHANNEL VARIABLE field, separated by slashes";
		tooltips[3] = "Value for DSM2 OUTPUT_CHANNEL INTERVAL field";
		tooltips[4] = "Value for DSM2 OUTPUT_CHANNEL PERIOD_OP field (inst or avg)";
		tooltips[5] = "Value for DSM2 OUTPUT_CHANNEL FILE field";
		tooltips[6] = "Output file will contain, at the end of each line, a commented out value that is the distance between the landmark point and the centerline";
//		tooltips[1] = "If checked, will create a cross-section line on the nearest channel that intersects the landmark point";
		
		//require channels.inp and output file name, but not hof file
		boolean[] disableIfNull = new boolean[] {true,false,true,true,true,true,true}; 
		DataEntryDialog dataEntryDialog = new DataEntryDialog(_csdpFrame, title, instructions, names, defaultValues, dataTypes, disableIfNull, 
				extensions, tooltips, true);
		int response = dataEntryDialog.getResponse();
		//done creating dialog. Now get input from the dialog and create report.
		if(response==DataEntryDialog.OK) {
			//for storing results: key=landmarkName, value =centerlineName 
			Hashtable<String, String> closestChanHashtable = new Hashtable<String, String>();
			//for storing results: key=landmarkName, value=distance along centerline
			Hashtable<String, Double> closestChanDistHashtable = new Hashtable<String, Double>();
			Hashtable<String, Double> closestXIntersectionCoordHashtable = new Hashtable<String, Double>();
			Hashtable<String, Double> closestYIntersectionCoordHashtable = new Hashtable<String, Double>();
			File outputFileDirectory = dataEntryDialog.getDirectory(names[0]);
			String outputFileFilename = dataEntryDialog.getFilename(names[0]);
			File dsm2ChannelsDirectory = dataEntryDialog.getDirectory(names[1]);
			String dsm2ChannelsFilename = dataEntryDialog.getFilename(names[1]);
			String variableFieldString = dataEntryDialog.getValue(names[2]);
			String intervalFieldString = dataEntryDialog.getValue(names[3]);
			String periodOpFieldString = dataEntryDialog.getValue(names[4]);
			String fileFieldString = dataEntryDialog.getValue(names[5]);
			boolean includeDistToCenterline = Boolean.parseBoolean(dataEntryDialog.getValue(names[6]));
			CsdpFunctions.setOpenDirectory(outputFileDirectory.toString());
//			boolean createCrossSectionLines = Boolean.parseBoolean(dataEntryDialog.getValue(names[1]));
			
			Network network = csdpFrame.getNetwork();
			Landmark landmark = csdpFrame.getLandmark();
			DSMChannels dsmChannelsJustForAdjustment = null;
			if(dsm2ChannelsFilename!=null && dsm2ChannelsFilename.length()>0) {
				dsmChannelsJustForAdjustment = chanReadStore(dsm2ChannelsDirectory.toString(), dsm2ChannelsFilename);
				String filename = null;
				String filetype = null;
				int dotIndex = dsm2ChannelsFilename.lastIndexOf(".");
				if (dotIndex >= 0) {
					filename = dsm2ChannelsFilename.substring(0, dotIndex);
					filetype = dsm2ChannelsFilename.substring(dotIndex + 1);
				} else {
					filename = dsm2ChannelsFilename;
					filetype = null;
				}
				DSMChannelsInput chanInput = DSMChannelsInput.getInstance(dsm2ChannelsDirectory.toString(), filename + "." + DSMChannels_TYPE);
				dsmChannelsJustForAdjustment = chanInput.readData();
			}

			AsciiFileWriter asciiFileWriter= new AsciiFileWriter(csdpFrame, outputFileDirectory.toString()+File.separator+outputFileFilename);
			asciiFileWriter.writeLine("############################################################################################");
			asciiFileWriter.writeLine("# " + CsdpFunctions.getCurrentDatetimeFormattedForDSM2InputComments());
			asciiFileWriter.writeLine("# Created automatically by CSDP, using the following network and landmark files,");
			asciiFileWriter.writeLine("# with the Find Channel/Distance for Landmarks function");
			asciiFileWriter.writeLine("# Network file: "+CsdpFunctions.getNetworkDirectory()+File.separator+CsdpFunctions.getNetworkFilename());
			asciiFileWriter.writeLine("# Landmark file: "+CsdpFunctions.getLandmarkDirectory()+File.separator+CsdpFunctions.getLandmarkFilename());
			if(includeDistToCenterline) {
				asciiFileWriter.writeLine("# \n# The commented out value at end of each line is the distance to the centerline (ft). ");
				asciiFileWriter.writeLine("# This distance should be small. If it is not, this may mean that the network file used");
				asciiFileWriter.writeLine("# to create this file was missing the matching channel. If so, the output location");
				asciiFileWriter.writeLine("# should not be used.");
			}
			asciiFileWriter.writeLine("############################################################################################");
			asciiFileWriter.writeLine("OUTPUT_CHANNEL");
			asciiFileWriter.writeLine(String.format("%-18s", "NAME")+
					String.format("%8s", "CHAN_NO")+
					String.format("%10s", "DISTANCE")+
					String.format("%-9s", " VARIABLE")+
					String.format("%-12s", " INTERVAL")+
					String.format("%-10s", " PERIOD_OP")+
					" FILE");
			Enumeration<String> landmarkNamesEnum = landmark.getLandmarkNames();
			try {
				while(landmarkNamesEnum.hasMoreElements()) {
					String landmarkName = landmarkNamesEnum.nextElement();
					double landmarkX = landmark.getXFeet(landmarkName);
					double landmarkY = landmark.getYFeet(landmarkName);
					// Condition 1: find centerline with shortest distance from one of the endpoints to the landmark.
					Object[] chanDist = network.findClosestCenterlineEndpointToLandmark(landmarkX, landmarkY);
					String closestEndpointCenterlineNameString = (String) chanDist[0];
					double minDistEndpointCenterline = (Double) chanDist[1];
					double closestEndpointCenterlineDistAlong = (Double) chanDist[2];
					CenterlinePoint closestCenterlinePoint = (CenterlinePoint)chanDist[3]; 

					double minDist = minDistEndpointCenterline;
					if(dsmChannelsJustForAdjustment!=null) {
						String cNameString = closestEndpointCenterlineNameString;
						double previousVersionCenterlineLength = dsmChannelsJustForAdjustment.getLength(cNameString);
						if(previousVersionCenterlineLength > 0 && 
								closestEndpointCenterlineDistAlong > previousVersionCenterlineLength){
							closestEndpointCenterlineDistAlong = previousVersionCenterlineLength;
						}
					}
					closestChanHashtable.put(landmarkName, closestEndpointCenterlineNameString);
					closestChanDistHashtable.put(landmarkName, closestEndpointCenterlineDistAlong);
					closestXIntersectionCoordHashtable.put(landmarkName, closestCenterlinePoint.getXFeet());
					closestYIntersectionCoordHashtable.put(landmarkName, closestCenterlinePoint.getYFeet());
					//If there is a line segment or interior centerline point that is closer, 
					//update closestChanHashtable and closestChanDistHashtable
					for(int i=0; i<network.getNumCenterlines(); i++) {
						String centerlineName = network.getCenterlineName(i);
						Centerline centerline = network.getCenterline(centerlineName);
						//Condition 2: find perpendicular distance to all centerlines. returns distance along centerline, and perpendicular distance to the closest centerline segment
						double[] cumDistMinDistIntersectionCoord = CsdpFunctions.getXsectDistAndPointDist(centerline, landmarkX, 
								landmarkY, Double.MAX_VALUE, true); 
						double cumDist = cumDistMinDistIntersectionCoord[0];
						double minCenterlineDist = cumDistMinDistIntersectionCoord[1];
						double xIntersection = cumDistMinDistIntersectionCoord[2];
						double yIntersection = cumDistMinDistIntersectionCoord[3];
						if(minCenterlineDist>0 && minCenterlineDist < minDist) {
							minDist = minCenterlineDist;
							closestChanHashtable.put(landmarkName, centerlineName);
							// if a channels inp file was specified, adjust length. This is for creating DSM2 v8.2 output locations using DSM2v 8.3 network file.
							//The reason for doing this is that the DSM2 v8.2 channels lengths are not always the same as centerline lengths.
							if(dsmChannelsJustForAdjustment!=null) {
								cumDist *= dsmChannelsJustForAdjustment.getLength(centerlineName) / centerline.getLengthFeet();
							}
							closestChanDistHashtable.put(landmarkName, cumDist);
							closestXIntersectionCoordHashtable.put(landmarkName, xIntersection);
							closestYIntersectionCoordHashtable.put(landmarkName, yIntersection);
						}
					}//for each centerline
					if(closestChanDistHashtable.containsKey(landmarkName) && closestChanDistHashtable.get(landmarkName)!=null &&
							closestChanHashtable.containsKey(landmarkName) && closestChanHashtable.get(landmarkName)!=null) {
						String[] variableParts=variableFieldString.split("/");
						for(int i=0; i<variableParts.length; i++) {
							String vString = variableParts[i];
							//if the landmark name includes the forward slash '/' character, replace it with an underscore before writing it to the
							//DSM2 output specification file. This is necessary because the output location name is used to create a DSS pathname.
							String lineToWrite = String.format("%-18s", landmarkName.replace('/', '_'))+
									String.format("%8s", closestChanHashtable.get(landmarkName))+
									String.format("%10.0f", closestChanDistHashtable.get(landmarkName))+
									String.format("%-9s", " "+vString)+
									String.format("%-12s", " "+intervalFieldString)+
									String.format("%-10s", " "+periodOpFieldString)+
									" "+fileFieldString;
									// also write minDist, which is the minimum perpendicular distance from the landmark point to the centerline
									if(includeDistToCenterline) lineToWrite += "  # " + String.format("%-20.1f", minDist);
							asciiFileWriter.writeLine(lineToWrite);
							
							//now find the coordinates of the intersection and write to a different file...for now just print to screen
							double closestXIntersection = CsdpFunctions.feetToMeters(closestXIntersectionCoordHashtable.get(landmarkName));
							double closestYIntersection = CsdpFunctions.feetToMeters(closestYIntersectionCoordHashtable.get(landmarkName));
							System.out.println(closestXIntersection+","+closestYIntersection+","+landmarkName);
						}
					}
				}//for each landmark
				asciiFileWriter.writeLine("END");
				JOptionPane.showMessageDialog(csdpFrame, "File written", "Success", JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e) {			
				JOptionPane.showMessageDialog(csdpFrame, "Error occurred when trying to write to file: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}finally {
				asciiFileWriter.close();
			}

			
		}//if Ok button clicked
	}//findChanDistForLandmarks

	/*
	 * Use jzy3d to display 3d plot of bathymetry data with cross-section lines.
	 * assume that all centerlineNames are c
	 */
	public void viewCenterlinesWithBathymetry3D(String[] centerlineNames, double xsectThickness, String windowTitle, 
			boolean displayUserDefinedCrossSections) {
		CsdpFunctions.display3dPlotInfoMessage(_csdpFrame);
		_csdpFrame.setCursor(CsdpFunctions._waitCursor);
		double[] centerlineDataDisplayBounds = _net.findCenterline3DDisplayRegion(centerlineNames, xsectThickness);
		viewCenterlinesWithBathymetry3D(centerlineDataDisplayBounds, centerlineNames, windowTitle, displayUserDefinedCrossSections);
	}
	
	/*
	 * Use jzy3d to display 3d plot of bathymetry data with cross-section lines.
	 * assume that all centerlineNames are c
	 */
	public void viewCenterlinesWithBathymetry3D(double[] centerlineDataDisplayBounds, String[] centerlineNames, String windowTitle, 
			boolean displayUserDefinedCrossSections) {
		//This is a very dark grey, which seems to provide good contrast for CSDP users, but makes the axis labels difficult to read
//		chart.getView().setBackgroundColor(new Color(59,59,59));
		//This is a good color for reports: a lighter grey. It may also be dark enough for CSDP use. Let's stay with this for a while.		Color chartBackgroundColor = new Color(90, 90, 90);
		Color chartBackgroundColor = new Color(90, 90, 90);
		Color legendBackgroundColor = new Color(150, 150, 150);
		//centerlineNames will be null if user has drawn box to specify region rather than entering centerline names
		if(DEBUG) {
			System.out.println("centerlineDataDisplayBounds="+
					centerlineDataDisplayBounds[CsdpFunctions.x1Index]+","+
					centerlineDataDisplayBounds[CsdpFunctions.y1Index]+","+
					centerlineDataDisplayBounds[CsdpFunctions.x2Index]+","+
					centerlineDataDisplayBounds[CsdpFunctions.y2Index]);
			System.out.println("centerlineDataDisplayBounds meters="+
					CsdpFunctions.feetToMeters(centerlineDataDisplayBounds[CsdpFunctions.x1Index])+","+
					CsdpFunctions.feetToMeters(centerlineDataDisplayBounds[CsdpFunctions.y1Index])+","+
					CsdpFunctions.feetToMeters(centerlineDataDisplayBounds[CsdpFunctions.x2Index])+","+
					CsdpFunctions.feetToMeters(centerlineDataDisplayBounds[CsdpFunctions.y2Index]));
		}		
		
		double x1 = centerlineDataDisplayBounds[CsdpFunctions.x1Index];
		double y1 = centerlineDataDisplayBounds[CsdpFunctions.y1Index];
		double x2 = centerlineDataDisplayBounds[CsdpFunctions.x2Index];
		double y2 = centerlineDataDisplayBounds[CsdpFunctions.y2Index];
		double rectWidth = Math.abs(x1-x2);
		double rectHeight = Math.abs(y1-y2);
		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		Rectangle2D.Double displayBoundsRectangle = new Rectangle2D.Double(minX, minY, rectWidth, rectHeight);

		if(_net==null) {
			centerlineNames = new String[0];
		}else if(centerlineNames == null) {
			Vector<String> centerlineNamesVector = new Vector<String>();
			
			for(int i=0; i < _net.getNumCenterlines(); i++) {
				String centerlineName = _net.getCenterlineName(i);
				Centerline centerline = _net.getCenterline(centerlineName);
				//find coordinates of first and last points of cross-section lines, and determine if a line connecting the two points
				//intersects centerlineDataDisplayBounds, which if centerlineNames is null, actually represents the window drawn by user.
				for(int j=0; j<centerline.getNumXsects(); j++) {
					Xsect xsect = centerline.getXsect(j);
					if(xsect.getNumPoints()>0) {
						double[] firstPoint = _net.find3DXsectPointCoord(centerlineName, j, 0);
						double[] lastPoint = _net.find3DXsectPointCoord(centerlineName, j, xsect.getNumPoints()-1);
						Line2D line2d = new Line2D.Double(firstPoint[CsdpFunctions.xIndex], firstPoint[CsdpFunctions.yIndex],
								lastPoint[CsdpFunctions.xIndex], lastPoint[CsdpFunctions.yIndex]);
						if(displayBoundsRectangle.intersectsLine(line2d)) {
							centerlineNamesVector.add(centerlineName);
							break;
						}
					}
				}
			}
			centerlineNames = new String[centerlineNamesVector.size()];
			for(int i=0; i<centerlineNamesVector.size(); i++) {
				centerlineNames[i] = centerlineNamesVector.get(i); 
				System.out.println("adding centelrineName to array= "+centerlineNamesVector.get(i));
			}
		}//if centerlineNames==null
		
		final Chart chart = new AWTChartComponentFactory().newChart(Quality.Advanced, "awt");
		Vector<Integer> bathymetryPointIndexes = _bathymetryData.findPointIndexesInRegionFor3dDisplay(centerlineDataDisplayBounds);
		int size = bathymetryPointIndexes.size();
		Coord3d[] bathymetryCoord3dArray = new Coord3d[size];
		//for delaunay chart
//		Vector<Coord3d> bathymetryCoord3dVector = new Vector<Coord3d>();

		// Create scatter points
		double[] point = new double[3];
		double minZ = Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		
		//now get bathymetry data
		for (int i = 0; i < size; i++) {
			_bathymetryData.getPointFeet(bathymetryPointIndexes.get(i), point);
			double x = point[0];
			double y = point[1];
			double z = point[2];
			minZ = Math.min(minZ, z);
			maxZ = Math.max(maxZ, z);
			bathymetryCoord3dArray[i] = new Coord3d(x, y, z);
//			bathymetryCoord3dVector.add(new Coord3d(x,y,z));
		}
		ScatterMultiColor scatter = new ScatterMultiColor(bathymetryCoord3dArray, new ColorMapper(new ColorMapRainbow(), minZ, maxZ));
		//this will change the point width. setting to a value less than 1 seems to have no effect. This would be helpful for dense data sets...
		//		scatter.setWidth(1.0f);
		//this one doesn't work...
//	    scatter.setLegend( new AWTColorbarLegend(scatter, chart.getView().getAxe().getLayout(), Color.WHITE, null));
		AWTColorbarLegend legend = new AWTColorbarLegend(scatter, new AxeBoxLayout(), Color.BLACK, legendBackgroundColor);
		scatter.setLegend(legend);
		scatter.setLegendDisplayed(true);
		chart.getView().setBackgroundColor(chartBackgroundColor);
		chart.getScene().add(scatter);

		//now add the user-created cross-section points 
		Coord3d[] xsectPoints = null;
		int totalNumXsects = 0;
		for(int i=0; i<centerlineNames.length; i++) {
			String centerlineName = centerlineNames[i];
			Centerline centerline = _net.getCenterline(centerlineName);
			totalNumXsects += centerline.getNumXsects();
		}
		Vector<LineStrip> lineStripVector = new Vector<LineStrip>();
		Vector<Scatter> xsScatterVector = new Vector<Scatter>();
		Vector<DrawableTextBitmap> beginningXsLabelsVector = new Vector<DrawableTextBitmap>();
		Vector<DrawableTextBitmap> endingXsLabelsVector = new Vector<DrawableTextBitmap>();
		
		int xsectIndex = 0;
		for(int i=0; i<centerlineNames.length; i++) {
			String centerlineName = centerlineNames[i];
			Centerline centerline = _net.getCenterline(centerlineName);
			for(int j=0; j<centerline.getNumXsects(); j++) {
				Xsect xsect = centerline.getXsect(j);
				Vector<Coord3d> xsectPointsVector = new Vector<Coord3d>();
//				xsectPoints = new Coord3d[xsect.getNumPoints()];
				//now add a linestrip to connect all the user-defined cross-section points
				// (from https://groups.google.com/forum/#!msg/jzy3d/kN8-a2W1laY/Ul7fxN1EPLUJ)
				LineStrip lineStrip = new LineStrip();
				for(int k=0; k<xsect.getNumPoints(); k++) {
					double[] xsectPointCoord = _net.find3DXsectPointCoord(centerlineName, j, k);
					double x = xsectPointCoord[CsdpFunctions.xIndex];
					double y = xsectPointCoord[CsdpFunctions.yIndex];
					double z = xsectPointCoord[CsdpFunctions.zIndex];
					if(displayBoundsRectangle.contains(x, y)) {
						Coord3d coord3d = new Coord3d(x, y, z);
						xsectPointsVector.add(coord3d); 
						lineStrip.add(new Point(coord3d, Color.WHITE));
					}
				}
				lineStrip.setDisplayed(true);
				lineStrip.setWidth(3.0f);
				if(xsectPointsVector.size()>0) {
					xsectPoints = new Coord3d[xsectPointsVector.size()];
					for(int k=0; k<xsectPointsVector.size(); k++) {
						xsectPoints[k]= xsectPointsVector.get(k); 
					}
					Scatter xsectScatter = new Scatter(xsectPoints, Color.WHITE);
					xsectScatter.setWidth(5.0f);
					xsScatterVector.add(xsectScatter);

					lineStripVector.add(lineStrip);
//					chart.getScene().add(xsectScatter);
					//adding here results in an error if channels are not in upstream to downstream order. add them below instead
//					chart.getScene().add(lineStrip);

					//now add cross-section labels
					DrawableTextBitmap endingDrawableTextBitmap = new DrawableTextBitmap("  "+centerlineName+"_"+j, 
							lineStrip.getLastPoint().getCoord(), Color.WHITE);
					endingDrawableTextBitmap.setHalign(Halign.RIGHT);
					endingXsLabelsVector.add(endingDrawableTextBitmap);
					//you can also use other text objects, such as DrawableTextWrapper objects, but I don't know what the difference is.
					//add this later
					//				chart.getScene().getGraph().add(downstreamDrawableTextBitmap);
					DrawableTextBitmap beginningDrawableTextBitmap = new DrawableTextBitmap("  "+centerlineName+"_"+j, 
							lineStrip.getPoints().get(0).getCoord(), Color.WHITE);
					beginningDrawableTextBitmap.setHalign(Halign.RIGHT);
					beginningXsLabelsVector.add(beginningDrawableTextBitmap);
					//add this later
					//				chart.getScene().getGraph().add(upstreamDrawableTextBitmap);
				}
				xsectIndex++;
			}
		}//for each centerlineName, add user-created cross-section points

		if(displayUserDefinedCrossSections) {
			//now add the scatters
			for(int j=0; j<xsScatterVector.size(); j++) {
				chart.getScene().add(xsScatterVector.get(j));
			}
			for(int j=0; j<xsScatterVector.size(); j++) {
				chart.getScene().add(beginningXsLabelsVector.get(j));
				chart.getScene().add(endingXsLabelsVector.get(j));
			}
			for(int j=0; j<xsScatterVector.size(); j++) {
				chart.getScene().add(lineStripVector.get(j));
			}

			
//			xsectIndex = 0;
//			for(int i=0; i<centerlineNames.length; i++) {
//				String centerlineName = centerlineNames[i];
//				Centerline centerline = _net.getCenterline(centerlineName);
//				for(int j=0; j<centerline.getNumXsects(); j++) {
//					chart.getScene().add(xsScatterArray[xsectIndex]);
//					xsectIndex++;
//				}
//			}
			
//			//add cross-section labels
//			xsectIndex = 0;
//			for(int i=0; i<centerlineNames.length; i++) {
//				String centerlineName = centerlineNames[i];
//				Centerline centerline = _net.getCenterline(centerlineName);
//				for(int j=0; j<centerline.getNumXsects(); j++) {
//					chart.getScene().getGraph().add(beginningXsLabelsArray[xsectIndex]);
//					chart.getScene().getGraph().add(endingXsLabelsArray[xsectIndex]);
//					xsectIndex++;
//				}
//			}
//			
//			//now add the linestrips. Adding them here instead of above prevents error that occurs when channels are not in upstream to downstream order.
//			xsectIndex = 0;
//			for(int i=0; i<centerlineNames.length; i++) {
//				String centerlineName = centerlineNames[i];
//				Centerline centerline = _net.getCenterline(centerlineName);
//				for(int j=0; j<centerline.getNumXsects(); j++) {
//					chart.getScene().add(lineStripArray[xsectIndex]);
//					xsectIndex++;
//				}
//			}
		}
		
		//create shape from bathymetry points--doesn't look right yet...
//		ArrayList<Coord3d>bathymetryCoord3dList = new ArrayList<Coord3d>(Arrays.asList(bathymetryCoord3dArray)); 
//		Shape bathymetrySurface = (Shape) Builder.buildDelaunay(bathymetryCoord3dList);
//		chart.getScene().getGraph().add(bathymetrySurface);
		
		if(windowTitle==null || windowTitle.length()<1) {
			windowTitle = "";
			for(int i=0; i<centerlineNames.length; i++) {
				if(i>0) windowTitle += "_";
				windowTitle += centerlineNames[i];
			}
		}
		// using chart.open will create a static chart (you can't rotate it)
		//		chart.open(chartTitle, 1000, 800);
//		Light light = chart.addLight(new Coord3d(10000, 10000, 10000));
//		light.setAmbiantColor(Color.WHITE);
//		light.setRepresentationRadius(1000);
//		chart.getCanvas().addKeyController(new KeyListener(){
//			public void keyPressed(KeyEvent e) {
//				switch( e.getKeyChar()){
//				case KeyEvent.VK_2: chart.getScene().getLightSet().get(0).getPosition().x -= 10; chart.render(); break;
//				case KeyEvent.VK_8: chart.getScene().getLightSet().get(0).getPosition().x += 10; chart.render(); break;
//				case KeyEvent.VK_4: chart.getScene().getLightSet().get(0).getPosition().y -= 10; chart.render(); break;
//				case KeyEvent.VK_6: chart.getScene().getLightSet().get(0).getPosition().y += 10; chart.render(); break;
//				case KeyEvent.VK_9: chart.getScene().getLightSet().get(0).getPosition().z += 10; chart.render(); break;
//				case KeyEvent.VK_7: chart.getScene().getLightSet().get(0).getPosition().z -= 10; chart.render(); break;
//				
//				//KeyEvent.;
//		        default: break;
//		        }
//			}
//			public void keyReleased(KeyEvent e) {}
//			public void keyTyped(KeyEvent e) {}
//			
//		});

		//add my custom controller which enables scaling in x and y directions (default was only z).
		//Ctrl-MouseWheel: zoom x axis
		//Alt-MouseWheel:  zoom y axis
		//MouseWheel:      zoom z axis
		AWTCameraMouseController awtCameraMouseController = new Bathymetry3dAWTCameraMouseController();
		chart.addController(awtCameraMouseController);
//		chart.addKeyboardCameraController(new AWTLightKeyController(chart));
		ChartLauncher.openChart(chart, new Rectangle(1000, 800), windowTitle);

		//For some reason, the plot won't display correctly until user right-drags on the window.
		//Sending a right-drag event could help eliminate this problem.
		//but need to figure out how to create a MouseEvent object programmatically. Code below is not working.
		//The chart object only takes awt MouseEvent objects, but the MouseEvent constructor needs a Component, and the chart object is not a Component.
//		short eventType = MouseEvent.MOUSE_DRAGGED;
//		long when = System.currentTimeMillis();
//		short clickCount = 1;
//		int x = 100;
//		int y = 100;
//		int modifiers = InputEvent.BUTTON2_DOWN_MASK;
//		short button = MouseEvent.BUTTON2;
//		
//		float[] rotationXYZ = new float[] {0f, 0f, 0f};
//		float rotationScale = 1f;
//
//		MouseEvent rightDragEvent = new MouseEvent(eventType, chart, when, 
//				modifiers, x, y, clickCount, button, rotationXYZ, rotationScale);
//		
//		for(AbstractCameraController cameraController: chart.getControllers()) {
//			((AWTCameraMouseController)cameraController).mouseDragged(rightDragEvent);
//		}
		
		//		Chart chart2 = getDelaunayChart(bathymetryCoord3dVector);
//		ChartLauncher.openChart(chart2, new Rectangle(1000, 800), windowTitle);
		_csdpFrame.setCursor(CsdpFunctions._defaultCursor);

		//now write ply file
		//https://stackoverflow.com/questions/4342901/is-there-a-java-library-for-writing-out-ply-files
		//https://people.sc.fsu.edu/~jburkardt/data/ply/ply.html
//		https://threejs.org/docs/#examples/exporters/GLTFExporter
		
//		https://people.sc.fsu.edu/~jburkardt/data/ply/ply.html
//        FileOutputStream fos = new FileOutputStream(file);
//        Writer writer= new OutputStreamWriter(fos, "UTF8");
//        writer.write("ply\n");
//        writer.write("format ");
//        writer.write(isBinary() ? "binary_big_endian" : "ascii");
//        writer.write(" 1.0\n");
//        BufferedReader r=new BufferedReader(new StringReader(comment));
//        String commentLine;
//        while ((commentLine=r.readLine())!=null) {
//            writer.write("comment ");
//            writer.write(commentLine);
//            writer.write('\n');
//        }
//        // lat,lon,alt as example
//        writer.write("element vertex 3\n");
//        writer.write("property double x\n");
//        writer.write("property double y\n");
//        writer.write("property double z\n");
//        //writer.write("element face 0\n"); // no element like faces
//        //writer.write("property list uchar int vertex_indices\n");
//        writer.write("end_header\n");
//        writer.flush();
//        DataOutputStream dos=new DataOutputStream(fos);
//        dos.writeDouble(x);
//        dos.writeDouble(y);
//        dos.writeDouble(z);
//        dos.close();

        
        
	}//viewCenterlinesWithBathymetry3D

	/**
	 * This method currently unused, but this may be useful in the future
	 * copied from this page (good examples here)
	 * https://www.programcreek.com/java-api-examples/?code=gsi-upm/BARMAS/BARMAS-master/src/main/java/es/upm/dit/gsi/barmas/launcher/utils/plot/Plotter.java
	 * @param coordinates
	 * @return
	 */
	public Chart getDelaunayChart(List<Coord3d> coordinates) {
		AWTChartComponentFactory factory = new AWTChartComponentFactory();

		// Create the object to represent the function over the given range.
		Shape surface = Builder.buildDelaunay(coordinates);

		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(),
				surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1,
						0.75f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		AWTColorbarLegend legend = new AWTColorbarLegend(surface, new AxeBoxLayout());
		surface.setLegend(legend);

		// Create a chart
		Chart chart = new Chart(factory, Quality.Nicest, "awt", Settings.getInstance()
				.getGLCapabilities());
		chart.setAxeDisplayed(true);
		chart.getScene().getGraph().add(surface);

		return chart;
	}

	public void createCenterlineOrReachSummaryWindow(CsdpFrame csdpFrame, Network net, int centerlineStartingEnd) {
		_csdpFrame = csdpFrame;
		_net = net;
		String keyString = net.getSelectedCenterlineName();
		if(!this.allOpenCenterlineOrReachSummaryWindowsHashtable.containsKey(keyString)) {
			CenterlineOrReachSummaryWindow centerlineOrReachSummaryWindow = 
					new CenterlineOrReachSummaryWindow(csdpFrame, this, _net, centerlineStartingEnd);
			this.allOpenCenterlineOrReachSummaryWindowsHashtable.put(keyString, centerlineOrReachSummaryWindow);
		}else {
			JOptionPane.showMessageDialog(_csdpFrame, "A centerline summary window for this channel is already open.", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void createCenterlineOrReachSummaryWindow(CsdpFrame csdpFrame, Network net, String reachTitle,
			String channelNumbersString, int centerlineStartingEnd) {
		_csdpFrame = csdpFrame;
		_net = net;
		
		String keyString = channelNumbersString;
		if(!this.allOpenCenterlineOrReachSummaryWindowsHashtable.containsKey(keyString)) {
			CenterlineOrReachSummaryWindow centerlineOrReachSummaryWindow = 
					new CenterlineOrReachSummaryWindow(csdpFrame, this, net, reachTitle, channelNumbersString, centerlineStartingEnd);
			this.allOpenCenterlineOrReachSummaryWindowsHashtable.put(keyString, centerlineOrReachSummaryWindow);
		}else {
			JOptionPane.showMessageDialog(_csdpFrame, "A reach summary window for given channel numbers specification is already open.", 
					"MESSAGE", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/*
	 * Called when xsectGraph object is updated or centerline updated
	 */
	public void updateAllOpenCenterlineOrReachSummaries(String centerlineName) {
		Enumeration<String> keysEnumeration = this.allOpenCenterlineOrReachSummaryWindowsHashtable.keys(); 
		while(keysEnumeration.hasMoreElements()) {
			String keyString = keysEnumeration.nextElement();
//			System.out.println("keyString="+keyString);
			CenterlineOrReachSummaryWindow centerlineOrReachSummaryWindow = this.allOpenCenterlineOrReachSummaryWindowsHashtable.get(keyString);
			if(centerlineOrReachSummaryWindow.containsCenterline(centerlineName)) {
				centerlineOrReachSummaryWindow.updateWindow();
//				System.out.println("Updated Window for key "+keyString);
			}
		}
	}
	
	/*
	 * Called when window closes
	 */
	public void removeCenterlineOrReachSummaryWindow(String keyString) {
		this.allOpenCenterlineOrReachSummaryWindowsHashtable.remove(keyString);
	}

	/*
	 * User specifies one or two network files. First will be currently loaded network file by default, but can be changed
	 * Looping through the centerlines in file #1, display a series of windows. 
	 * Pressing a key will dispose the current window and open the next.
	 * Each window will display
	 * 1. on the left, the bathymetry and cross-section drawing for a cross-section in file #1. 
	 * 2. one the right, the same for file #2. The cross-section with the closest distance will be displayed. 
	 * Above Both plots will be cross-section index, distance along centerline, and centerline length.
	 * 
	 * bathyetryDirectory1 and bathymetryFilename1 can be null. If not null, they specify a bathymetry data file
	 * that is to be used for the plots on the right hand side of the window.
	 * The first file may be the currently loaded network file. If this is the case, do not re-read it.
	 */
	public void xsectSlideshow(String networkDirectory0, String networkFilename0, boolean reReadFile0, 
			String networkDirectory1, String networkFilename1, String bathymetryDirectory0, String bathymetryFilename0,
			String directorySaveImageString, boolean includeXsectConveyanceCharacteristics, boolean includeMetadata, boolean autoSave) {
		// need to remove extension from filenames, because NetworkInput adds it for you
		Network network0 = null;
		if(reReadFile0) {
			String[] filenameParts = CsdpFunctions.parseFilename(networkFilename0);
			String filenameWithoutExtension0 = filenameParts[0];
			NetworkInput ninput0 = NetworkInput.getInstance(_csdpFrame, networkDirectory0, filenameWithoutExtension0, NETWORK_TYPE);
			network0 = ninput0.readData();
		}else {
			network0 = _net;
		}
		
		BathymetryData bathymetryData0 = null;
		if(bathymetryDirectory0!=null && bathymetryFilename0!=null) {
			createXsectSlideshowAlternateBathymetryData(bathymetryDirectory0, bathymetryFilename0);
			bathymetryData0 = xsectSlideshowAlternateBathymetryData;
		}else {
			bathymetryData0 = _bathymetryData;
		}
		
		String[] filenameParts = CsdpFunctions.parseFilename(networkFilename1);
		String filenameWithoutExtension1 = filenameParts[0];
		NetworkInput ninput1 = NetworkInput.getInstance(_csdpFrame, networkDirectory1, filenameWithoutExtension1, NETWORK_TYPE);
		Network network1 = ninput1.readData();
		
		boolean quitSlideshow = false;

		if(autoSave) {
			//non-interactively open an XsectSlideshowWindow and save to image.
			for(int i=0; i<network0.getNumCenterlines(); i++) {
				String centerlineName0 = network0.getCenterlineName(i);
				Centerline centerline0 = network0.getCenterline(centerlineName0);
				if(network1.centerlineExists(centerlineName0)) {
					Centerline centerline1 = network1.getCenterline(centerlineName0);
					for(int j=0; j<centerline0.getNumXsects(); j++) {
						int xsectIndex0 = j;
						Xsect xsect0 = centerline0.getXsect(j);
						double xsectDist0 = xsect0.getDistAlongCenterlineFeet();
						int xsectIndex1 = centerline1.getClosestXsectIndex(xsectDist0);
						XsectSlideshowDialog xsectSlideshowDialog = 
								new XsectSlideshowDialog(_csdpFrame, this, bathymetryData0, _bathymetryData, _xsectColorOption, 
										network0, centerlineName0, xsectIndex0, 
										network1, xsectIndex1, networkDirectory0, networkFilename0, networkDirectory1, networkFilename1,
										directorySaveImageString, includeXsectConveyanceCharacteristics, includeMetadata,
										autoSave);
						xsectSlideshowDialog.requestFocusInWindow();
						xsectSlideshowDialog.dispatchEvent(new WindowEvent(xsectSlideshowDialog, WindowEvent.WINDOW_CLOSING));
					}
				}
			}
		}else {
		
			int centerlineIndex=0;
			while(true) {
				String centerlineName0 = network0.getCenterlineName(centerlineIndex);
				//both centerlines have the same name 
				Centerline centerline0 = network0.getCenterline(centerlineName0);
				boolean matchingCenterline = network1.centerlineExists(centerlineName0);
				if(matchingCenterline) {
					//display cross-sections from both networks
					Centerline centerline1 = network1.getCenterline(centerlineName0);
					int j = 0;
					while(true) {
						boolean noPreviousCenterline = false;
						boolean noNextCenterline = false;
						boolean noNextXsect = false;
						boolean noPreviousXsect = false;
						if(j<=0) noPreviousXsect = true;
						if(j>=centerline0.getNumXsects()-1) noNextXsect = true;
						if(centerlineIndex<=0) noPreviousCenterline = true;
						if(centerlineIndex>=network0.getNumCenterlines()-1) noNextCenterline = true;
						
						Xsect xsect0 = centerline0.getXsect(j);
						double xsectDist0 = xsect0.getDistAlongCenterlineFeet();
						
//						System.out.println("before exception: centerlineName, centerlineExists, centerline1="+
//								centerlineName0+","+network1.centerlineExists(centerlineName0)+","+centerline1);
						int xsectIndex0 = j;
						int xsectIndex1 = -Integer.MAX_VALUE;
						if(centerline1!=null) {
							xsectIndex1 = centerline1.getClosestXsectIndex(xsectDist0);
						}
						
						XsectSlideshowDialog xsectSlideshowDialog = 
								new XsectSlideshowDialog(_csdpFrame, this, bathymetryData0, _bathymetryData, _xsectColorOption, 
										network0, centerlineName0, xsectIndex0, 
										network1, xsectIndex1, networkDirectory0, networkFilename0, networkDirectory1, networkFilename1,
										directorySaveImageString, includeXsectConveyanceCharacteristics, includeMetadata,
										noPreviousCenterline, noNextCenterline, noPreviousXsect, noNextXsect);
	
						int response = xsectSlideshowDialog.getResponse();
						if(response==XsectSlideshowDialog.PREVIOUS_OPTION) {
							if(j>0) j--;
						}else if(response==XsectSlideshowDialog.NEXT_OPTION) {
							if(j<centerline0.getNumXsects()-1) j++;
						}else if(response==XsectSlideshowDialog.PREVIOUS_CENTERLINE_OPTION) {
							centerlineIndex--;
							centerlineName0 = network0.getCenterlineName(centerlineIndex);
//							while(!network1.centerlineExists(centerlineName0) && centerlineIndex > 0) {
//								centerlineIndex--;
//							}
							centerline0 = network0.getCenterline(centerlineName0);
							centerline1 = network1.getCenterline(centerlineName0);
							j=0;
						}else if(response==XsectSlideshowDialog.NEXT_CENTERLINE_OPTION) {
							centerlineIndex++;
							centerlineName0 = network0.getCenterlineName(centerlineIndex);
							//not sure what this was for--it messes things up. Keep here for now... 
							//							while(!network1.centerlineExists(centerlineName0) && 
							//									centerlineIndex<network0.getNumCenterlines()-1 && centerlineIndex<network1.getNumCenterlines()-1) {
							//								centerlineIndex++;
							//							}
							centerline0 = network0.getCenterline(centerlineName0);
							centerline1 = network1.getCenterline(centerlineName0);
							j=0;
						}else if(response==XsectSlideshowDialog.JUMP_TO_CENTERLINE_OPTION) {
							String requestedCenterlineNameString = xsectSlideshowDialog.getRequestedCenterlineName();
							if(network0.centerlineExists(requestedCenterlineNameString)) {
								if(network1.centerlineExists(requestedCenterlineNameString)) {
									centerlineIndex = network0.getCenterlineIndex(requestedCenterlineNameString);
									centerlineName0 = network0.getCenterlineName(centerlineIndex);
									//both centerlines have the same name 
									centerline0 = network0.getCenterline(centerlineName0);
									//display cross-sections from both networks
									centerline1 = network1.getCenterline(centerlineName0);
									j=0;
								}
							}else {
								JOptionPane.showMessageDialog(_csdpFrame, "Centerline "+requestedCenterlineNameString+ " does not exist in network", 
										"Error", JOptionPane.OK_OPTION);
							}
						}else {
							//response is next centerline or quit
							if(response==XsectSlideshowDialog.QUIT_OPTION) {
								quitSlideshow = true;
							}
							break;
						}
					}//while
					if(quitSlideshow) {
						break;
					}
				}else {
					//just display cross-sections from centerline0
				}
			}//while
		}//if autoSave or not
	}//xsectSlideshow

	/*
	 * Create a WKT file containing a straight line for each channel connecting two nodes.
	 */
	public boolean createStraightlineWKTGridmapFile(String wktPath, Network net, Landmark landmark, DSMChannels _DSMChannels2) {
		System.out.println("wktPath="+wktPath);
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, wktPath);
		afw.writeLine("id;wkt");
		boolean success = true;
		try{
			int numCenterlines = net.getNumCenterlines();
			for(int i=0; i<numCenterlines; i++) {
				String centerlineName = net.getCenterlineName(i);
				String lineToWrite = centerlineName+";LINESTRING(";
				int upnode = _DSMChannels.getUpnode(centerlineName);
				int downnode = _DSMChannels.getDownnode(centerlineName);
				double upnodeX = landmark.getXFeet(Integer.toString(upnode));
				double upnodeY = landmark.getYFeet(Integer.toString(upnode));
				double downnodeX = landmark.getXFeet(Integer.toString(downnode));
				double downnodeY = landmark.getYFeet(Integer.toString(downnode));
				lineToWrite+=CsdpFunctions.feetToMeters(upnodeX)+" "+CsdpFunctions.feetToMeters(upnodeY) + ","+
						CsdpFunctions.feetToMeters(downnodeX)+" "+CsdpFunctions.feetToMeters(downnodeY);
				lineToWrite += ")"; 
				afw.writeLine(lineToWrite);
			}
			afw.close();
			success = true;
		}catch(Exception exception) {
			success = false;
		}
		return success;
	}//createStraightlineWKTGridmapFile

	/*
	 * Exports network information to csv file, containing 
	 * XsectName, XsectEasting, XsectNorthing, Metadata
	 */
	public boolean nExportMetadataTable(Network net, String csvPath) {
		boolean success = true;
		AsciiFileWriter afw = new AsciiFileWriter(_csdpFrame, csvPath);
		try {
			afw.writeLine("XsectName, XsectEasting, XsectNorthing, Metadata");
			int numCenterlines = net.getNumCenterlines();
			for(int i=0; i<numCenterlines; i++) {
				String centerlineNameString = net.getCenterlineName(i);
				Centerline centerline = net.getCenterline(centerlineNameString);
				for(int j=0; j<centerline.getNumXsects(); j++) {
					Xsect xsect = centerline.getXsect(j);
					double[] centerlineMidpointCoord = net.getCenterlineMidpointCoord(centerlineNameString, j);
					double intersectionX = centerlineMidpointCoord[0];
					double intersectionY = centerlineMidpointCoord[1];
					String metadata = xsect.getMetadata().replace("\n", "|").replace("\r", "|").replace(",", "-");
					String line = centerlineNameString+"_"+j+","+intersectionX+","+intersectionY+","+metadata;
					afw.writeLine(line);
				}
			}
		}catch (Exception e) {
//			JOptionPane.showMessageDialog(_csdpFrame, "Exception caught in App.chanReadStore: "+e.getMessage(), 
//			"Error", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(_csdpFrame, "Exception caught in App.nExportMetadataTable:"+e.getMessage(), 
					"Error", JOptionPane.ERROR_MESSAGE);
			success = false;
		}finally {
			afw.close();
		}
		
		return success;
	}

	public void clearChannelsInp() {
		_DSMChannels = null;
		CsdpFunctions.setDSMChannelsFilename(null);
		CsdpFunctions.setDSMChannelsFiletype(null);
	}


}// class App
