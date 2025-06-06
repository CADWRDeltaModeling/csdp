package DWR.CSDP;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

//    import DWR.Graph.*;
//    import DWR.Graph.Canvas.*;
import vista.graph.AxisAttr;
import vista.graph.ElementInteractor;
import vista.graph.GECanvas;
import vista.graph.Graph;
import vista.graph.Plot;

/**
 * This class handles interaction of mouse and mouse motion with Graph class and
 * its components.
 * 
 * @author
 * @version
 */
public class XsectEditInteractor extends ElementInteractor {
	//identifies what type of add: upstream or downstream
	public static final int ADD_LEFT_POINT = 100;
	public static final int ADD_RIGHT_POINT = 200;
	/**
	 * for debugging
	 */
	public static final boolean DEBUG = false;
	/**
	 * Buffers image so as to avoid flickering while selecting zoom region
	 */
	protected Image _gCImage;
	/**
	 * Graph canvas
	 */
	/// protected ElementContext _gC;
	protected GECanvas _gC;
	/**
	 * Stores flag to indicate if double buffering was being used before
	 */
	protected boolean _previouslyDoubleBuffered = false;
	/**
	 * Flag to indicate zoom region selection is in progress.
	 */
	protected boolean _drawDragRect = true;
	/**
	 * Flag to indicate whether mouse was dragged after mouse button was
	 * pressed.
	 */
	protected boolean _mouseDragged = false;
	/**
	 * Initial point's x value
	 */
	protected int _xi = 0;
	/**
	 * Initial point's y value
	 */
	protected int _yi = 0;
	/**
	 * Final point's x value
	 */
	protected int _xf = 0;
	/**
	 * Final point's y value
	 */
	protected int _yf = 0;
	/**
	 * Current zooming region
	 */
	protected Rectangle _zoomRect = new Rectangle(0, 0, 0, 0);
	/**
	 * color used to draw the zoom rectangle
	 */
	protected Color _zoomRectColor = Color.black;
	/**
	 * Plot object
	 */
	protected Plot _plot;
	/**
	 * XsectGraph object
	 */
	protected XsectGraph _xsectGraph;
	protected Xsect _xsect;
	private Graph _graph;
	private App _app;
	private CsdpFrame _gui;
	private Network _net;

	/**
	 * constructor
	 */
	/// public XsectEditInteractor(XsectGraph xg, Xsect xsect, ElementContext
	/// gC, Graph[] graphs){
	public XsectEditInteractor(CsdpFrame csdpFrame, App app, Network network, XsectGraph xg, Xsect xsect, GECanvas gC, Graph graph) {
		this._gui = csdpFrame;
		this._app = app;
		this._net = network;
		_xsectGraph = xg;
		_xsect = xsect;
		_gC = gC;
		_plot = ((Graph) _gC.getGraphicElement()).getPlot();
		_graph = graph;
		// _zoom = new Zoom( (Graph) _gC.getGraphicElement());
	}

	/**
	 *
	 */
	public void mouseClicked(MouseEvent e) {
		setInitialPoint(e.getX(), e.getY());
		// if(_xsectGraph.getAddPointMode()) addPoint();
		// else if(_xsectGraph.getInsertPointMode()) insertPoint();
		// else if(_xsectGraph.getDeletePointMode()) deletePoint();
	}

	/**
	 * Invoked when a mouse button has been pressed on a component. This sets
	 * the initial point of the zoom region
	 */
	public void mousePressed(MouseEvent e) {
		setInitialPoint(e.getX(), e.getY());
		if (_xsectGraph.getAddBeginningPointMode())
			addPoint(ADD_LEFT_POINT);
		else if(_xsectGraph.getAddEndingPointMode()) {
			addPoint(ADD_RIGHT_POINT);
		}
		else if (_xsectGraph.getInsertPointMode())
			insertPoint();
		else if (_xsectGraph.getDeletePointMode())
			deletePoint();

		// _plot = ((Graph) _gC.getGraphicElement()).getPlot();
		// _drawDragRect = true;

		// _previouslyDoubleBuffered = _gC.isDoubleBuffered();
		// if (!_previouslyDoubleBuffered) _gC.setDoubleBuffered(true);

		// Rectangle r = _gC.getBounds();
		// _gCImage = _gC.createImage(r.width, r.height);

	}

	/**
	 * Invoked when a mouse button has been released on a component. If mouse
	 * was dragged this marks the diagonally opposite point to the initial point
	 * of the zoom region. Then region is zoomed into.
	 * <p>
	 * If mouse was clicked without dragging then zoom out is done to the
	 * previous zoom state.
	 */
	public void mouseReleased(MouseEvent e) {
		//// if (_mouseDragged) {
		// _drawDragRect = false;

		setFinalPoint(e.getX(), e.getY());
		if (_xsectGraph.getMovePointMode())
			movePoint();

		// if(_zoomRect.width == 0 || _zoomRect.height == 0) return;
		// _zoom.zoomIn(_zoomRect);

		// // g.setColor(previousColor);

		// _gC.redoNextPaint();
		// // _gC.setDoubleBuffered(_previouslyDoubleBuffered);
		// _gC.repaint();

		// _mouseDragged = false;

		// if (_zoom.zoomOut()){

		// _gC.redoNextPaint();
		// // _gC.setDoubleBuffered(_previouslyDoubleBuffered);
		// _gC.repaint();

		_mouseDragged = false;
		// }
		//// }
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * As mouse is dragged a rectangle showing the currently selected zoom
	 * region is displayed as a rectangle. To achieve good performance double
	 * buffering was used, however there seems to be a bug in JDK 1.1.2 which
	 * does not draw the complete image when a complicated drawing is done. This
	 * affects multiple curve plot.
	 */
	public void mouseDragged(MouseEvent e) {
		// if(_xsectGraph._movePointMode){
		// //search for nearest point and grab
		// }

		// if ( _drawDragRect ){
		_mouseDragged = true;

		// Graphics g = _gCImage.getGraphics();
		// Rectangle bounds = _gC.getBounds(); bounds.x=0; bounds.y=0;
		// g.setClip(bounds);
		// g.drawImage(_gC.getGraphicElementImage(), 0, 0, null);

		// setFinalPoint(e.getX(), e.getY());
		// Rectangle r = _zoomRect;
		// // Color previousColor = g.getColor();
		// g.setColor(_zoomRectColor);
		// g.drawRect(r.x, r.y, r.width, r.height);
		// _gC.getGraphics().drawImage(_gCImage, 0, 0, null);
		// }
	}

	/**
	 * Sets initial point of zoom rectangle region
	 */
	public void setInitialPoint(int x, int y) {
		_xi = x;
		_yi = y;
	}

	/**
	 * Sets final point of zoom rectangle region. This would be the point
	 * diagonally opposite the initial point
	 */
	public void setFinalPoint(int x, int y) {
		_xf = x;
		_yf = y;
		// constructRectangle();
	}

	/**
	 * moves the nearest point in xsect
	 */
	protected void movePoint() {
//		double xDataCoordInitial;
//		double yDataCoordInitial;
		double xDataCoordFinal;
		double yDataCoordFinal;
		XsectPoint point = null;
		int minDistIndex = Integer.MAX_VALUE;
		int numPoints = _xsect.getNumPoints();
		if (DEBUG)
			System.out.println("moving xsect point");
		
		xDataCoordFinal = (_graph.getPlot().getAxis(AxisAttr.BOTTOM)).getScale().scaleToDC(_xf);
		yDataCoordFinal = (_graph.getPlot().getAxis(AxisAttr.LEFT)).getScale().scaleToDC(_yf);

		//7/3/2019: The previous approach to finding the closest point used data coordinates. This often resulted in a point being moved
		//that was not the point the user indended to move. Reason: large differences in x and y axis scales resulted in the closest point
		//in data coordinates being different from the closest point in pixel coordinates. This new approach uses pixel coordinates to 
		//determine the closest point to the user's click.
		int numCurves = _graph.getPlot().getNumberOfCurves();
//		_graph.getPlot().getAxis(AxisAttr.BOTTOM).getScale().scaleToUC(v);
		NetworkDataSet networkDataSet = _xsectGraph.getNetworkDataSet();
		double[] stations = networkDataSet.getXArray();
		double[] elevations = networkDataSet.getYArray();
		double minDist = Double.MAX_VALUE;
		for(int i=0; i<networkDataSet.size(); i++) {
			double station = stations[i];
			double elevation = elevations[i];
			int stationPixels = _graph.getPlot().getAxis(AxisAttr.BOTTOM).getScale().scaleToUC(station);
			int elevationPixels = _graph.getPlot().getAxis(AxisAttr.LEFT).getScale().scaleToUC(elevation);
			double dist = CsdpFunctions.pointDist(_xi, _yi, stationPixels, elevationPixels);
			if(dist<minDist) {
				minDist = dist;
				minDistIndex = i;
			}
		}
		
		point = _xsect.getXsectPoint(minDistIndex);

		point.putStationFeet((float) xDataCoordFinal);
		point.putElevationFeet((float) yDataCoordFinal);
		// _xsectGraph.updateNetworkDataSet();
		_xsectGraph.updateDisplay();
		_xsect.setIsUpdated(true);
//		System.out.println("_app, _net="+_app+","+_net);
		_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
		_gui.updateInfoPanel(_net.getSelectedCenterlineName());

	}// movePoint

	/**
	 * deletes the nearest point in xsect
	 */
	protected void deletePoint() {
		double xDataCoord;
		double yDataCoord;
		int numPoints = _xsect.getNumPoints();
		if (DEBUG)
			System.out.println("deleting xsect point");
		// xDataCoord = (((GraphCanvas)_gC).getPlot().
		// getAxis(AxisAttr.BOTTOM)).getScale().scaleToDC(_xi);
		// yDataCoord = (((GraphCanvas)_gC).getPlot().
		// getAxis(AxisAttr.LEFT)).getScale().scaleToDC(_yi);

		xDataCoord = (_graph.getPlot().getAxis(AxisAttr.BOTTOM)).getScale().scaleToDC(_xi);
		yDataCoord = (_graph.getPlot().getAxis(AxisAttr.LEFT)).getScale().scaleToDC(_yi);
		_xsect.deleteXsectPoint(xDataCoord, yDataCoord);
		// _xsectGraph.updateNetworkDataSet();
		_xsectGraph.updateDisplay();
		_xsect.setIsUpdated(true);
		_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
		_gui.updateInfoPanel(_net.getSelectedCenterlineName());

	}// deletePoint

	/**
	 * inserts a point between the nearest two points in xsect
	 */
	protected void insertPoint() {
		double xDataCoord;
		double yDataCoord;
		int numPoints = _xsect.getNumPoints();
		if (DEBUG)
			System.out.println("inserting xsect point");
		xDataCoord = (_graph.getPlot().getAxis(AxisAttr.BOTTOM)).getScale().scaleToDC(_xi);
		yDataCoord = (_graph.getPlot().getAxis(AxisAttr.LEFT)).getScale().scaleToDC(_yi);
		_xsect.insertXsectPoint(xDataCoord, yDataCoord);
		// _xsectGraph.updateNetworkDataSet();
		_xsectGraph.updateDisplay();
		_xsect.setIsUpdated(true);
		_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
		_gui.updateInfoPanel(_net.getSelectedCenterlineName());

	}

	/**
	 * adds a point after the last point in xsect
	 */
	protected void addPoint(int insertPointMode) {
		double xDataCoord;
		double yDataCoord;
		if (DEBUG)
			System.out.println("adding xsect point");
		xDataCoord = (_graph.getPlot().getAxis(AxisAttr.BOTTOM)).getScale().scaleToDC(_xi);
		yDataCoord = (_graph.getPlot().getAxis(AxisAttr.LEFT)).getScale().scaleToDC(_yi);
		_xsect.addXsectPoint(insertPointMode, (float) xDataCoord, (float) yDataCoord);
		// if there are no points, create net networkdataset
		// if(_xsectGraph._gC.getPlot().getCurve(_xsectGraph._networkDataSet) ==
		// null){
		// NOT SURE IF THIS IS THE RIGHT CURVE....
		System.out.println("num curves,num bathymetry datasets=" + _graph.getPlot().getNumberOfCurves() + ","
				+ _xsectGraph._numBathymetryDataSets);

		// _xsectGraph.updateNetworkDataSet();
		_xsectGraph.updateDisplay();
		_xsect.setIsUpdated(true);
		_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
		_gui.updateInfoPanel(_net.getSelectedCenterlineName());

	}// addPoint

	public void updateXsect(Xsect xsect) {
		this._xsect = xsect;
	}


}
