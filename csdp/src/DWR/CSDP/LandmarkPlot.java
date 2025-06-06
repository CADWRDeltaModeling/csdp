package DWR.CSDP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;

/**
 * Plot Landmark Data on screen
 *
 * @author
 * @version $Id: LandmarkPlot.java,v 1.3 2003/04/15 19:43:56 btom Exp $
 */
public class LandmarkPlot extends PlanViewPlot {

	/**
	 * sets value of _bathymetryData in superclass
	 */
	public LandmarkPlot(CsdpFrame gui, BathymetryData data, App app) {
		super(gui, data, app);
	}

	/**
	 * sets landmark object
	 */
	public void setLandmark(Landmark landmark) {
		_landmark = landmark;
	}

	/**
	 * Plot landmark data, top view.
	 */
	// public void drawPoints(Graphics2D g, Rectangle bounds, float[]
	// dataBoundaries,
	// boolean useZoomBox){
	public void drawPoints(Graphics2D g, Rectangle bounds, ZoomState zs) {
		g.setColor(Color.black);
		String name = null;
		double height = bounds.height;

		double[] dataBoundaries = zs.getPlotBoundaries();
		double minX = dataBoundaries[CsdpFunctions.minXIndex];
		double minY = dataBoundaries[CsdpFunctions.minYIndex];
		CoordConv cc = zs.getCoordConv();

		double x;
		double y;
		int xPixels;
		int yPixels;

		// if(DEBUG)System.out.println("in LandmarkPlot: centerx,
		// centery="+_centerX+","+_centerY);
		for (Enumeration<String> e = _landmark.getLandmarkNames(); e.hasMoreElements();) {
			name = e.nextElement();
			x = (_landmark.getXFeet(name));
			y = (_landmark.getYFeet(name));
			if (DEBUG)
				System.out.println("LandmarkPlot: name,x,y=" + name + "," + x + "," + y);
			int[] pt = null;
			// if(zs.getUseZoomBox()){
			pt = cc.utmToPixels(x, y, minX, minY);
			// if(useZoomPan){
			// pt[0]+=_xZoomPanOffset;
			// pt[1]+=_yZoomPanOffset;
			// }
			// }else{
			// pt = cc.utmToPixels(x+_centerX, y+_centerY, minX, minY);
			// }
			xPixels = pt[0];
			yPixels = pt[1];

			int pd = 0;

			if (_landmark.getSelectedLandmarkName() != null && _landmark.getSelectedLandmarkName().equals(name)) {
				pd = POINT_DIMENSION * 2;
			} else {
				pd = POINT_DIMENSION;
			}
			if (DEBUG)
				System.out.println("x, y=" + xPixels + "," + yPixels);
			g.fillRect((int) xPixels - pd / 2, (int) yPixels - pd / 2, pd, pd);
			//we want the name to be shifted to the left by a number of pixels equal to 1.5 times the font size
			int fontSize = _gui.getFont().getSize();
			g.drawString(name, xPixels-(int)(1.5*fontSize), yPixels);
		}
	}// plot landmark

	protected static final int POINT_SIZE = 1;// size of displayed data point
												// (square)
	protected double _x1Pixels; // coordinates of centerline points converted to
								// pixels
	protected double _y1Pixels;
	protected double _x2Pixels;
	protected double _y2Pixels;
	Landmark _landmark = null;
	protected final int POINT_DIMENSION = 3;
} // class LandmarkPlot
