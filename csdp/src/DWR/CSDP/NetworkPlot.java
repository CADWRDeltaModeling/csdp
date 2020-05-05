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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Plot Network Data on screen
 *
 * @author
 * @version $Id: NetworkPlot.java,v 1.3 2003/04/15 19:46:14 btom Exp $
 */
public class NetworkPlot extends PlanViewPlot {

	//	protected static final int NETWORK_SELECTION_POINT_DIMENSION = 1;// size of displayed data point
		// (square)
	protected double _x1Pixels; // coordinates of centerline points converted to
	// pixels
	protected double _y1Pixels;
	protected double _x2Pixels;
	protected double _y2Pixels;
	Network _net = null;
	protected static int NETWORK_SELECTION_POINT_DIMENSION = 4;
	protected static final boolean DEBUG = false;
	/*
	 * (Dark Grey) Color to use to identify a centerline or cross-section line with no points
	 */
	public static final Color NO_POINTS_COLOR = new Color(102,102,102);
	/*
	 * (Blue) Color to use to identify a centerline or cross-section line with negative dConveyance in the intertidal range.
	 */
	public static final Color NEG_DK_IN_INTERTIDAL_COLOR = new Color(40, 63, 255);
	/*
	 * (Brown) Color used to identify a cross-section line with duplicate station values 
	 */
	public static final Color DUPLICATE_STATIONS_COLOR = new Color(204, 102, 0);
	/*
	 * (Red) Color used to identify a centerline that exceeds the maximum area ratio 
	 * (largest xs area > 2.0 * smallest xs area)
	 */
	public static final Color EXCEEDS_MAX_AREA_RATIO_COLOR = new Color(255, 0, 0);
	/*
	 * (Orange) Color used to identify centerline and cross-section line with duplicate station values
	 * and negative dConveyance in the intertidal zone
	 */
	public static final Color DUP_STN_AND_NEG_DK_IN_INTERTIDAL_COLOR = new Color(255, 134, 0);
	/*
	 * (Purple) Color used to identify centerline that exceeds the max area ratio and has negative dConveyance in intertidal zone.
	 */
	public static final Color EXCEEDS_MAX_AREA_RATIO_AND_NEG_DK_IN_INTERTIDAL_COLOR = new Color(162, 0, 255);
	/*
	 * (Green) Color used to identify centerlines with duplicate station values and that exceeds the max area ratio
	 */
	public static final Color DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR = new Color(0, 138, 0);
	/*
	 * (Magenta) Color used to identify centerlines with duplicate station values and that exceeds the max area ratio and 
	 * have negative dConveyance in the intertidal zone.
	 */
	public static final Color DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR_AND_NEG_DK_IN_INTERTIDAL_COLOR = new Color(255,0,255);
	
	/*
	 * (Black) Color used to identify centerlines with no issues
	 */
	public static final Color NO_ISSUES_COLOR = Color.black;
	/*
	 * Constructor
	 */
	public NetworkPlot(CsdpFrame gui, BathymetryData data, App app) {
		super(gui, data, app);
	}

	/**
	 * update bathymetry data
	 */
	public void setBathymetryData(BathymetryData data) {
		if (DEBUG)
			System.out.println("setting bathymetry data object in networkplot. minx,miny=" + data.getMinXFeet() + ","
					+ data.getMinYFeet());
		_bathymetryData = data;
	}

	/**
	 * sets network object
	 */
	public void setNetwork(Network net) {
		_net = net;
	}

	/*
	 * Returns a color to use for centerline segments and points based on status of its cross-sections
	 */
	private Color getCenterlineColor(Centerline centerline) {
		boolean allXSHaveNoPoints = centerline.allXSHaveNoPoints();
		boolean anyXSHasNegDkInIntertidal = centerline.anyXSHaveNegDKInIntertidal();
		boolean anyXSHasDuplicateStations = centerline.anyXSHaveDuplicateStations();
		boolean exceedsMaxRatio = centerline.getMaxAreaRatio() >= CsdpFunctions.MAX_AREA_RATIO;
		Color returnColor = null;
		if(!anyXSHasNegDkInIntertidal && !anyXSHasDuplicateStations && exceedsMaxRatio) {
			returnColor = EXCEEDS_MAX_AREA_RATIO_COLOR;
		}else if(!anyXSHasNegDkInIntertidal && anyXSHasDuplicateStations && !exceedsMaxRatio) {
			returnColor = DUPLICATE_STATIONS_COLOR;
		}else if(!anyXSHasNegDkInIntertidal && anyXSHasDuplicateStations && exceedsMaxRatio) {
			returnColor = DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR;
		}else if(anyXSHasNegDkInIntertidal && !anyXSHasDuplicateStations && !exceedsMaxRatio) {
			returnColor = NEG_DK_IN_INTERTIDAL_COLOR;
		}else if(anyXSHasNegDkInIntertidal && !anyXSHasDuplicateStations && exceedsMaxRatio) {
			returnColor = EXCEEDS_MAX_AREA_RATIO_AND_NEG_DK_IN_INTERTIDAL_COLOR;
		}else if(anyXSHasNegDkInIntertidal && anyXSHasDuplicateStations && !exceedsMaxRatio) {
			returnColor = DUP_STN_AND_NEG_DK_IN_INTERTIDAL_COLOR;
		}else if(anyXSHasNegDkInIntertidal && anyXSHasDuplicateStations && exceedsMaxRatio) {
			returnColor = DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR_AND_NEG_DK_IN_INTERTIDAL_COLOR;
		}else if(allXSHaveNoPoints) {
			returnColor = NO_POINTS_COLOR;
		}else {
			returnColor = NO_ISSUES_COLOR;
		}
		return returnColor;
	}//getCenterlineColor

	/*
	 * returns a color to use for xsect line.
	 */
	private Color getXsectColor(Xsect xsect) {
		boolean hasNoPoints = xsect.hasNoPoints();
		boolean negDkInIntertidal = xsect.hasNegDConveyanceInIntertidalZone();
		boolean duplicateStations = !xsect.allUniqueStations();
		
		Color returnColor = null;
		if(hasNoPoints) {
			returnColor = NO_POINTS_COLOR;
		}else if(negDkInIntertidal && ! duplicateStations) {
			returnColor = NEG_DK_IN_INTERTIDAL_COLOR;
		}else if(!negDkInIntertidal && duplicateStations) {
			returnColor = DUPLICATE_STATIONS_COLOR;
		}else if(negDkInIntertidal && duplicateStations) {
			returnColor = DUP_STN_AND_NEG_DK_IN_INTERTIDAL_COLOR;
		}else {
			returnColor = NO_ISSUES_COLOR;
		}
		return returnColor;
	}//getXsectColor
	
	/**
	 * Plot network data, top view.
	 */
	// public void drawPoints(Graphics2D g, Rectangle bounds, float[]
	// dataBoundaries,
	// boolean useZoomBox){
	public void drawPoints(Graphics2D g, Rectangle bounds, ZoomState zs) {
		g.setColor(Color.black);
		Centerline centerline;
		CenterlinePoint centerlinePoint;
		CenterlinePoint nextCenterlinePoint;
		Xsect xsect;
		double height = bounds.height;
		CoordConv cc = zs.getCoordConv();
		double[] plotBoundaries = zs.getPlotBoundaries();
		double minX = plotBoundaries[CsdpFunctions.minXIndex];
		double minY = plotBoundaries[CsdpFunctions.minYIndex];

		// System.out.println("plotNetwork: minX, minY, minSlope,
		// height="+minX+","+minY+","+zs.getMinSlope()+","+height);
		double[] xy;
		int x1Index = 0;
		int y1Index = 1;
		int x2Index = 2;
		int y2Index = 3;
		String centerlineName;

		// if(DEBUG)System.out.println("NetworkPlot: centerX,
		// centerY="+_centerX+","+_centerY);

		for (int c = 0; c <= (_net.getNumCenterlines() - 1); c++) {
			if (DEBUG)
				System.out.println("networkplot: numcenterlines=" + _net.getNumCenterlines());
			centerlineName = _net.getCenterlineName(c);
			centerline = _net.getCenterline(centerlineName);

			if(CsdpFunctions.NETWORK_COLORING) {
				Color centerlineColor = getCenterlineColor(centerline);
				g.setColor(centerlineColor);
			}			
			// plot centerline
			if (DEBUG)
				System.out.println("centerlineName, centerline object=" + centerlineName + "," + centerline);
			for (int p = 0; p <= centerline.getNumCenterlinePoints() - 2; p++) {
				centerlinePoint = centerline.getCenterlinePoint(p);
				nextCenterlinePoint = centerline.getCenterlinePoint(p + 1);

				int[] pt1;
				int[] pt2;
				// if(zs.getUseZoomBox()){
				// must define x?pixels and y?pixels here, not after if
				// statement
				// because consecutive called to utmtopixels replace values in
				// previous returned arrays.
				pt1 = cc.utmToPixels(centerlinePoint.getXFeet(), centerlinePoint.getYFeet(), minX, minY);
				_x1Pixels = pt1[0];
				_y1Pixels = pt1[1];
				pt2 = cc.utmToPixels(nextCenterlinePoint.getXFeet(), nextCenterlinePoint.getYFeet(), minX, minY);
				_x2Pixels = pt2[0];
				_y2Pixels = pt2[1];
				if (DEBUG)
					System.out.println("centerline: x1,y1,x2,y2(pixels)=" + _x1Pixels + "," + _y1Pixels + " "
							+ _x2Pixels + "," + _y2Pixels);
				g.drawLine((int) _x1Pixels, (int) _y1Pixels, (int) _x2Pixels, (int) _y2Pixels);
			} // for p
				// draw squares on points if centerline is selected
			if (DEBUG)
				System.out.println("net=" + _net);
			if (DEBUG)
				System.out.println("centerlineName, selectedcenterlinename=" + centerlineName + ","
						+ _net.getSelectedCenterlineName());
			if (centerlineName.equals(_net.getSelectedCenterlineName())) {
				if (DEBUG)
					System.out.println("match found");
				for (int p = 0; p <= centerline.getNumCenterlinePoints() - 1; p++) {
					centerlinePoint = centerline.getCenterlinePoint(p);

					int[] pt;
					pt = cc.utmToPixels(centerlinePoint.getXFeet(), centerlinePoint.getYFeet(), minX, minY);
					_x1Pixels = pt[0];
					_y1Pixels = pt[1];

					g.fillRect((int) _x1Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2, (int) _y1Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2,
							NETWORK_SELECTION_POINT_DIMENSION, NETWORK_SELECTION_POINT_DIMENSION);
					// if upstream point, draw a "U"; if downstream point, draw
					// a "D"
					if (p == 0)
						g.drawString("U", (int) _x1Pixels, (int) _y1Pixels);
					else if (p == centerline.getNumCenterlinePoints() - 1) {
						g.drawString("D", (int) _x1Pixels, (int) _y1Pixels);
					}
				} // for p
			} // if centerline is selected
				// plot xsect line
			for (int xs = 0; xs <= centerline.getNumXsects() - 1; xs++) {
				xsect = centerline.getXsect(xs);
				if(CsdpFunctions.NETWORK_COLORING) {
					Color xsColor = getXsectColor(xsect);
					g.setColor(xsColor);
				}
				// xy=findXsectLineCoord(centerline, xsect, xs);
				if (DEBUG)
					System.out.println("number of xsect = " + centerline.getNumXsects());
				if (DEBUG)
					System.out.println("centerlineName, xs#=" + centerlineName + "," + xs);
				xy = _net.findXsectLineCoord(centerlineName, xs);
				int[] pt1 = null;
				int[] pt2 = null;
				// must define x?pixels and y?pixels here, not after if
				// statement
				// because consecutive called to utmtopixels replace values in
				// previous returned arrays.
				// if(zs.getUseZoomBox()){
				pt1 = cc.utmToPixels(xy[x1Index], xy[y1Index], minX, minY);
				_x1Pixels = pt1[0];
				_y1Pixels = pt1[1];
				pt2 = cc.utmToPixels(xy[x2Index], xy[y2Index], minX, minY);
				_x2Pixels = pt2[0];
				_y2Pixels = pt2[1];
				if (DEBUG)
					System.out.println("Cross-section line coord: x1,y1,x2,y2=" + _x1Pixels + "," + _y1Pixels + " "
							+ _x2Pixels + "," + _y2Pixels);

				g.drawLine((int) _x1Pixels, (int) _y1Pixels, (int) _x2Pixels, (int) _y2Pixels);
				if (xsect == _net.getSelectedXsect()) {
					g.fillRect((int) _x1Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2, (int) _y1Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2,
							NETWORK_SELECTION_POINT_DIMENSION, NETWORK_SELECTION_POINT_DIMENSION);
					g.fillRect((int) _x2Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2, (int) _y2Pixels - NETWORK_SELECTION_POINT_DIMENSION / 2,
							NETWORK_SELECTION_POINT_DIMENSION, NETWORK_SELECTION_POINT_DIMENSION);
				} // if xsect
			} // for xs
		} // for c
	}// plot

} // class NetworkPlot
