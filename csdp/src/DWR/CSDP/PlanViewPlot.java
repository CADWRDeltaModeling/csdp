package DWR.CSDP;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Stack;

/**
 * Plot Data on PlanViewCanvas
 *
 * @author
 * @version $Id:
 */
public abstract class PlanViewPlot {

	public PlanViewPlot(CsdpFrame gui, BathymetryData data, App app) {
		_gui = gui;
		_bathymetryData = data;
		_app = app;
	}// constructor

	/**
	 * finds boundaries of plot region and center of region, then calls plot
	 * method in subclass. Called by NetworkPlot, LandmarkPlot, and
	 * DigitalLineGraphPlot.
	 */
	public void plotData(Graphics2D g, Rectangle bounds, Rectangle zb, boolean changeZoom) {
		_width = bounds.width;
		_height = bounds.height;
		if (DEBUG)
			System.out.println("PlanViewPlot.plotData():width,height=" + _width + "," + _height);
		ZoomState currentZoomState = getCurrentZoomState();
		drawPoints(g, bounds, currentZoomState);
	}// plotData

	/**
	 * Always will be called before plotData because bathymetry is loaded first.
	 * Calls methods which need to be called before plotData is called.
	 */
	public void plotBathymetryData(Graphics2D g, Rectangle bounds, Rectangle zb, boolean changeZoom,
			Dimension zoomPanOffsets, boolean changePan, boolean zoomFit, boolean undoZoom) {
		_width = bounds.width;
		_height = bounds.height;
		// creates new zoom state
		findMinSlope(changeZoom, zb, zoomPanOffsets, changePan, zoomFit, undoZoom);
		ZoomState currentZoomState = getCurrentZoomState();
		drawPoints(g, bounds, currentZoomState);
	}// plotData

	/**
	 * Plot data points, top view. Points are plotted as square rectangles;
	 * requested size of points will determine number of pixels used for each
	 * square.
	 */
	protected abstract void drawPoints(Graphics2D g, Rectangle Bounds, ZoomState currentZoomState);

	/**
	 * find the factor that will be used to convert length to pixels. The factor
	 * will be calculated in the horizontal and vertical directions, and the min
	 * value will be used. find coordinates of the four corners of the region
	 * that will be plotted on the canvas.
	 *
	 * Sets minSlope. Used to return plotBoundaries[], but now stored in class
	 * variable (an element in a Stack)
	 */
	private void findMinSlope(boolean changeZoom, Rectangle zoomBox, Dimension zoomPanOffsets, boolean changePan,
			boolean zoomFit, boolean undoZoom) {
		if (DEBUG)
			System.out.println("planviewplot.findMinSlope: changeZoom, changePan, undoZoom, numZoomStates=" + changeZoom
					+ "," + changePan + "," + undoZoom + "," + _zoomStates.size());
		ZoomState lastZS = null;
		if (!_zoomStates.empty()) {
			lastZS = (ZoomState) _zoomStates.peek();
		}

		/*
		 * If undoing zoom, just remove the last ZoomState Object if this method
		 * called again without a change in the zoombox and zoomFit option isn't
		 * selected, don't change minSlope or plotBoundaries. This occurs when
		 * the colorBy option is changed, and possibly in other situations.
		 */
		if (undoZoom) {
			if (DEBUG)
				System.out.println("undoing zoom");
			_zoomStates.pop();
			if (_zoomStates.size() <= 1)
				_gui.setUndoZoom(false);
		} else if (lastZS != null && changeZoom == false && zoomFit == false && changePan == false) {
			if (DEBUG)
				System.out.println(
						"PlanViewPlot.findMinSlope: no change in zoomBox, not changing minSlope or plotBoundaries");
		} else {
			if (zoomFit) {
				_zoomStates.clear();
				_gui.setUndoZoom(false);
			}

			////// if fit, then clear zoomStates

			ZoomState currentZS = new ZoomState();
			_zoomStates.push(currentZS);
			if (_zoomStates.size() > 1)
				_gui.setUndoZoom(true);

			// must call all these methods. it won't know if you don't.
			currentZS.setFitByBathymetry(_gui.getFitByBathymetryOption());
			currentZS.setFitByNetwork(_gui.getFitByNetworkOption());
			currentZS.setFitByLandmark(_gui.getFitByLandmarkOption());
			if (_bathymetryData != null)
				currentZS.setBathymetryData(_bathymetryData);
			if (_net != null)
				currentZS.setNetwork(_net);
			if (_landmark != null)
				currentZS.setLandmark(_landmark);
			currentZS.setApp(_app);
			currentZS.setWidthHeight(_width, _height);
			currentZS.setZoomBox(zoomBox);
			currentZS.setChangeZoom(changeZoom);
			currentZS.setChangePan(changePan);
			currentZS.setZoomPanOffsets(zoomPanOffsets);
			currentZS.setZoomFit(zoomFit);

			// if there are previous zoomstates and not changing zoom and not
			// fitting
			if (lastZS != null)
				currentZS.setLastZoomState(lastZS);
			// zoomBox has changed. Recalculate minSlope and plotBoundaries.
			currentZS.updatePlotBoundaries();
			if (currentZS.getMinSlope() < 0)
				System.out.println("Error in PlanViewPlot: currentZS.getMinSlope()<0=" + currentZS.getMinSlope());
		} // update minSlope and plotBoundaries
	}// findMinSlope

	/*
	 * returns true if a zoom is requested and you're already zoomed in. called
	 * by findMinSlope.
	 */
	private boolean getAlreadyZoomedIn() {
		return _alreadyZoomedIn;
	}

	/*
	 * sets alreadyZoomedIn. Called by findMinSlope and PlanViewCanvas.zoomFit.
	 */
	public void setAlreadyZoomedIn(boolean a) {
		_alreadyZoomedIn = a;
	}

	public ZoomState getCurrentZoomState() {
		ZoomState zs = null;
		if (!_zoomStates.empty()) {
			zs = (ZoomState) _zoomStates.peek();
		} else {
			System.out.println("ERROR in PlanViewPlot.getCurrentZoomState: _zoomStates is empty");
		}
		return zs;
	}

	/*
	 * The Width of the canvas in pixels
	 * 
	 * static variable because the same value is used for bathymetry, network,
	 * landmark, and digitalLineGraph
	 */
	protected static float _width;
	/*
	 * The height of the canvas in pixels
	 * 
	 * static variable because the same value is used for bathymetry, network,
	 * landmark, and digitalLineGraph
	 */
	protected static float _height;

	protected Network _net;
	protected Landmark _landmark;
	protected App _app;
	protected CsdpFrame _gui = null;
	protected static BathymetryData _bathymetryData = null;
	protected static final boolean DEBUG = false;

	/**
	 * must create object here and not in constructor because it's static.
	 * Stores all zoom states.
	 */
	protected static Stack _zoomStates = new Stack();

	private boolean _alreadyZoomedIn = false;
} // Class PlanViewPlot
