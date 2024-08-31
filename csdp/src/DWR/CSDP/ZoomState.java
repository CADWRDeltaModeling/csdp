package DWR.CSDP;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Contains zoomBox, calculates minslope and plotBoundaries for zoomBox and
 * Canvas size, and contains AffineTransforms for coordinate conversion (in
 * CoordConv object).
 */
public class ZoomState {
	private boolean _fitByBathymetry = false;
	private boolean _fitByNetwork = false;
	private boolean _fitByLandmark = false;
	private final boolean DEBUG = false;
	private boolean _zoomFit = false;
	private boolean _changeZoom = false;
	private boolean _changePan = false;
	private double _minSlope;
	private Dimension _zoomPanOffsets;
	private Rectangle _zoomBox;
	/**
	 * ZoomState object for previous zoom. will be null if this is the first
	 * zoom
	 */
	private ZoomState _lastZS = null;
	private BathymetryData _bathymetryData;
	private Network _net;
	private Landmark _landmark;
	private App _app;
	private double _width;
	private double _height;
	/**
	 * plot boundaries. returns minx, miny, maxx, maxy
	 */
	private double[] _plotBoundaries;

	/**
	 * the object that contains the coord conversion routines (incl.
	 * AffineTransform objects)
	 */
	private CoordConv _coordConv;

	/**
	 * constructor
	 */
	public ZoomState() {
		_changeZoom = false;
		_changePan = false;
		_plotBoundaries = new double[4];
		_coordConv = new CoordConv();
		_minSlope = -Double.MAX_VALUE;
	}// constructor

	/**
	 * Finds boundaries of the region that should be displayed, based on
	 * zoom/pan/fit options, and boundaries of data.
	 */
	public void updatePlotBoundaries() {
		double[] pb = getPlotBoundaries();
		double[] lastPb = null;
		if (_lastZS != null)
			lastPb = _lastZS.getPlotBoundaries();
		if (getFitByBathymetry()) {
			pb[CsdpFunctions.minXIndex] = _bathymetryData.getMinXFeet();
			pb[CsdpFunctions.maxXIndex] = _bathymetryData.getMaxXFeet();
			pb[CsdpFunctions.minYIndex] = _bathymetryData.getMinYFeet();
			pb[CsdpFunctions.maxYIndex] = _bathymetryData.getMaxYFeet();
		} else if (getFitByNetwork()) {
			if (_net == null)
				_net = _app.getNetwork();
			pb[CsdpFunctions.minXIndex] = _net.getMinXFeet();
			pb[CsdpFunctions.maxXIndex] = _net.getMaxXFeet();
			pb[CsdpFunctions.minYIndex] = _net.getMinYFeet();
			pb[CsdpFunctions.maxYIndex] = _net.getMaxYFeet();
		} else if (getFitByLandmark()) {
			if (_landmark == null)
				_landmark = _app.getLandmark();
			pb[CsdpFunctions.minXIndex] = _landmark.getMinXFeet();
			pb[CsdpFunctions.maxXIndex] = _landmark.getMaxXFeet();
			pb[CsdpFunctions.minYIndex] = _landmark.getMinYFeet();
			pb[CsdpFunctions.maxYIndex] = _landmark.getMaxYFeet();
		} else {
			System.out.println("error in ZoomState.findPlotBoundaries: no fit option selected");
		} // if fitBy...

		double minX = pb[CsdpFunctions.minXIndex];
		double maxX = pb[CsdpFunctions.maxXIndex];
		double minY = pb[CsdpFunctions.minYIndex];
		double maxY = pb[CsdpFunctions.maxYIndex];

		double oldMinX = minX;
		double oldMaxX = maxX;
		double oldMinY = minY;
		double oldMaxY = maxY;

		/**
		 * use last plotBoundaries if 1. There are lpb NO DOESN'T MATTER 2.
		 * zoomed last time 3. zooming this time or panning 4. not fitting
		 */
		if (lastPb != null && (getChangeZoom() || getChangePan()) && getZoomFit() == false) {
			if (DEBUG)
				System.out.println("using last plotBoundaries");
			oldMinX = lastPb[CsdpFunctions.minXIndex];
			oldMaxX = lastPb[CsdpFunctions.maxXIndex];
			oldMinY = lastPb[CsdpFunctions.minYIndex];
			oldMaxY = lastPb[CsdpFunctions.maxYIndex];
		} // use lastPlotBoundaries if...

		// if not using zoomBox, these will be the values
		_minSlope = Math.min(_width / (maxX - minX), _height / (maxY - minY));
		_minSlope = Math.min(_width / (oldMaxX - oldMinX), _height / (oldMaxY - oldMinY));
		_coordConv.updateAT(_minSlope, _height);
		pb[CsdpFunctions.minXIndex] = oldMinX;
		pb[CsdpFunctions.maxXIndex] = oldMaxX;
		pb[CsdpFunctions.minYIndex] = oldMinY;
		pb[CsdpFunctions.maxYIndex] = oldMaxY;

		/**
		 * update minSlope, AffineTransform, and plotBoundaries if 1. changing
		 * zoom 2. not fitting this time
		 */
		if (getChangeZoom() && getZoomFit() == false && getChangePan() == false) {
			if (DEBUG)
				System.out.println("updatePlotBoundaries; changing zoom");
			// reset plot boundaries to area contained within zoom box
			// _width=width of canvas in pixels
			// _height=height of canvas in pixels
			// oldMaxX-oldMinX = width of data (in data units)
			_coordConv.updateAT(_minSlope, _height);
			// adjust zoombox: if its short and wide, make it taller.
			// If it's tall and narrow, make it wider.
			if (_width / _zoomBox.width <= _height / _zoomBox.height) {
				/*
				 * adjust requested height because the zoombox is short and wide
				 */
				double adjustedZoomBoxHeight = _zoomBox.width * (_height / _width);
				double adjustedZoomBoxY = _zoomBox.y - (0.5f * Math.abs(adjustedZoomBoxHeight - _zoomBox.height));
				double adjustedZoomBoxMinY = _zoomBox.y + (0.5f * Math.abs(adjustedZoomBoxHeight - _zoomBox.height));
				int zoomBoxMinX = _zoomBox.x;
				int zoomBoxMaxX = _zoomBox.x + _zoomBox.width;

				_coordConv.pixelsToLength((int) 0, (int) (adjustedZoomBoxMinY + _zoomBox.height), oldMinX, oldMinY,
						_lengthMinY);
				pb[CsdpFunctions.minYIndex] = _lengthMinY[1];
				_coordConv.pixelsToLength((int) 0, (int) adjustedZoomBoxY, oldMinX, oldMinY, _lengthMaxY);
				pb[CsdpFunctions.maxYIndex] = _lengthMaxY[1];

				// y doesn't matter for X pixels to length conversions
				_coordConv.pixelsToLength(zoomBoxMinX, (int) 0, oldMinX, oldMinY, _lengthMinX);
				pb[CsdpFunctions.minXIndex] = _lengthMinX[0];
				_coordConv.pixelsToLength(zoomBoxMaxX, (int) 0, oldMinX, oldMinY, _lengthMaxX);
				pb[CsdpFunctions.maxXIndex] = _lengthMaxX[0];
			} else {
				/*
				 * adjust requested width because the zoombox is tall and narrow
				 */
				double adjustedZoomBoxWidth = (_zoomBox.height / _height) * _width;
				double adjustedZoomBoxX = _zoomBox.x - (0.5f * Math.abs(adjustedZoomBoxWidth - _zoomBox.width));
				double adjustedZoomBoxMaxX = _zoomBox.x + (0.5f * Math.abs(adjustedZoomBoxWidth - _zoomBox.width));
				int zoomBoxMinY = _zoomBox.y + _zoomBox.height;
				int zoomBoxMaxY = _zoomBox.y;

				_coordConv.pixelsToLength((int) adjustedZoomBoxX, (int) 0, oldMinX, oldMinY, _lengthMinX);
				pb[CsdpFunctions.minXIndex] = _lengthMinX[0];
				_coordConv.pixelsToLength((int) adjustedZoomBoxX + _zoomBox.width, (int) 0, oldMinX, oldMinY,
						_lengthMaxX);
				pb[CsdpFunctions.maxXIndex] = _lengthMaxX[0];

				// calling csdpfunctions.pixelstolength twice in a row changes
				// the values of the first one...

				_coordConv.pixelsToLength((int) 0, (int) (zoomBoxMinY), oldMinX, oldMinY, _lengthMinY);
				pb[CsdpFunctions.minYIndex] = _lengthMinY[1];
				_coordConv.pixelsToLength((int) 0, (int) (zoomBoxMaxY), oldMinX, oldMinY, _lengthMaxY);
				pb[CsdpFunctions.maxYIndex] = _lengthMaxY[1];
			}
			// update minSlope for use by plotData methods
			_minSlope = Math.min((_width * _width / _zoomBox.width) / (oldMaxX - oldMinX),
					(_height * _height / _zoomBox.height) / (oldMaxY - oldMinY));
			_coordConv.updateAT(_minSlope, _height);
		} // If changeZoom

		if (getChangePan()) {
			if (DEBUG)
				System.out.println("changing pan");
			Dimension offsets = getZoomPanOffsets();
			int deltaX = (int) offsets.getWidth();
			int deltaY = (int) offsets.getHeight();
			double dXDataCoord = deltaX / _minSlope;
			double dYDataCoord = deltaY / _minSlope;
			pb[CsdpFunctions.minXIndex] += dXDataCoord;
			pb[CsdpFunctions.maxXIndex] += dXDataCoord;
			pb[CsdpFunctions.minYIndex] += dYDataCoord;
			pb[CsdpFunctions.maxYIndex] += dYDataCoord;
			if (DEBUG)
				System.out.println("ZoomState.updatePlotBoundaries: adjusting data coord for Pan; dx, dy" + dXDataCoord
						+ "," + dYDataCoord);
		}

		// print results
		if (DEBUG)
			System.out.println("minx,maxx,miny,maxy=" + CsdpFunctions.feetToMeters(pb[CsdpFunctions.minXIndex]) + ","
					+ CsdpFunctions.feetToMeters(pb[CsdpFunctions.maxXIndex]) + ","
					+ CsdpFunctions.feetToMeters(pb[CsdpFunctions.minYIndex]) + ","
					+ CsdpFunctions.feetToMeters(pb[CsdpFunctions.maxYIndex]));
	}// updatePlotBoundaries

	/**
	 * Compares current and last zoom box. Returns true if location and
	 * width/height are same.
	 */
	public boolean sameZoomBox() {
		boolean returnValue = false;
		if (_lastZS != null) {
			if (_lastZS.getZoomBox() != null) {
				if (getZoomBox().equals(_lastZS.getZoomBox())) {
					returnValue = true;
				} else {
					returnValue = false;
				}
				return returnValue;
			} else {
				System.out.println("lastZoomBox is null.  Assuming this is first zoom; returning false");
				returnValue = false;
			}
		} else {
			System.out.println("ERROR in ZoomState.sameZoomBox: last zoomState is null");
		}
		return returnValue;
	}// sameZoomBox

	public void printZoomState() {
		System.out.println("------------------------------------------");
		System.out.println("ZoomState.printZoomState()");
		System.out.println("------------------------------------------");
		System.out.println("changeZoom, changePan=" + getChangeZoom() + "," + getChangePan());
		System.out.println("zoomBox, panOffsets=" + getZoomBox() + "," + getZoomPanOffsets());
		System.out.println("zoomFit=" + getZoomFit());
		System.out.println("------------------------------------------");
	}

	public void setWidthHeight(double width, double height) {
		_width = width;
		_height = height;
	}

	public void setBathymetryData(BathymetryData bd) {
		_bathymetryData = bd;
	}

	public void setNetwork(Network net) {
		_net = net;
	}

	public void setLandmark(Landmark landmark) {
		_landmark = landmark;
	}

	public void setApp(App app) {
		_app = app;
	}

	public void setPlotBoundaries(double[] pb) {
		_plotBoundaries = pb;
	}

	public double[] getPlotBoundaries() {
		return _plotBoundaries;
	}

	public void setMinSlope(double ms) {
		_minSlope = ms;
	}

	public double getMinSlope() {
		return _minSlope;
	}

	public void setChangeZoom(boolean uzb) {
		_changeZoom = uzb;
	}

	public boolean getChangeZoom() {
		return _changeZoom;
	}

	public void setChangePan(boolean uzp) {
		_changePan = uzp;
	}

	public boolean getChangePan() {
		return _changePan;
	}

	/**
	 * save new instance of zoombox
	 */
	public void setZoomBox(Rectangle zb) {
		if (zb != null)
			_zoomBox = (Rectangle) (zb.clone());
	}

	public Rectangle getZoomBox() {
		return _zoomBox;
	}

	public void setZoomPanOffsets(Dimension offsets) {
		_zoomPanOffsets = offsets;
	}

	public Dimension getZoomPanOffsets() {
		return _zoomPanOffsets;
	}

	public void setFitByBathymetry(boolean value) {
		_fitByBathymetry = value;
	}

	public void setFitByNetwork(boolean value) {
		_fitByNetwork = value;
	}

	public void setFitByLandmark(boolean value) {
		_fitByLandmark = value;
	}

	public void setZoomFit(boolean value) {
		_zoomFit = value;
	}

	public boolean getFitByBathymetry() {
		return _fitByBathymetry;
	}

	public boolean getFitByNetwork() {
		return _fitByNetwork;
	}

	public boolean getFitByLandmark() {
		return _fitByLandmark;
	}

	public boolean getZoomFit() {
		return _zoomFit;
	}

	public CoordConv getCoordConv() {
		return _coordConv;
	}

	public void setLastZoomState(ZoomState lastZS) {
		_lastZS = lastZS;
	}

	/*
	 * location of minx of zoom box converted to data coordinates
	 */
	private double[] _lengthMinX = new double[2];
	/*
	 * location of maxx of zoom box converted to data coordinates
	 */
	private double[] _lengthMaxX = new double[2];
	/*
	 * location of miny of zoom box converted to data coordinates
	 */
	private double[] _lengthMinY = new double[2];
	/*
	 * location of maxy of zoom box converted to data coordinates
	 */
	private double[] _lengthMaxY = new double[2];

}// class ZoomState
