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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JOptionPane;

//import DWR.Graph.*;
import vista.graph.ElementInteractor;

/**
 * This class handles interaction of mouse and mouse motion.
 *
 * @author
 * @version
 */

public class NetworkInteractor extends ElementInteractor {

	public NetworkInteractor(CsdpFrame gui, PlanViewCanvas can, App app) {
		_gui = gui;
		_can = can;
		_app = app;
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 * 
	 */
	public void mouseClicked(MouseEvent e) {
		if (DEBUG)
			System.out.println("Mouse Clicked at ( " + e.getX() + ", " + e.getY() + " )");
		setInitialPoint(e.getX(), e.getY());
		if (DEBUG)
			System.out.println("mouse clicked:  setting initial point " + e.getX() + "," + e.getY());
	}// mouseClicked

	/**
	 * Invoked when a mouse button has been pressed. this selects the nearest
	 * centerline.
	 */
	public void mousePressed(MouseEvent e) {
		setInitialPoint(e.getX(), e.getY());

		boolean leftButton = false;
		int button = e.getModifiers();

		if (button == MouseEvent.BUTTON1_MASK && _bathymetryPlot != null) {
			Xsect xsect = null;
			if (DEBUG)
				System.out.println("mouse pressed:  setting initial point " + e.getX() + "," + e.getY());

			_nPlotter = _can._networkPlotter;

			ZoomState zs = _bathymetryPlot.getCurrentZoomState();
			CoordConv cc = zs.getCoordConv();
			if (_nPlotter != null) {
				_minSlope = zs.getMinSlope();
				_height = _nPlotter._height;
				// _centerX = _nPlotter._centerX;
				// _centerY = _nPlotter._centerY;
				// if(DEBUG)System.out.println("NetworkInteractor:
				// centerX,centerY="+
				// _centerX+","+_centerY);
			} // if _nPlotter is null
			if (_bathymetryPlot != null && _bathymetryPlot._bathymetryData != null) {
				// _minX = _bathymetryPlot._bathymetryData.getMinX();
				// _minY = _bathymetryPlot._bathymetryData.getMinY();
				double[] bb = zs.getPlotBoundaries();
				_minX = bb[CsdpFunctions.minXIndex];
				_minY = bb[CsdpFunctions.minYIndex];
			}
			if (_gui.getAddPointMode())
				addPoint();
			else if (_gui.getInsertPointMode())
				insertPoint();
			else if (_gui.getDeletePointMode())
				deletePoint();
			else if (_gui.getAddXsectMode())
				addXsect();
			else if (_gui.getMoveXsectMode())
				moveXsect();
			else if (_gui.getZoomBoxMode() || _gui.getZoomPanMode()) {
				_drawDragRect = true;
				_previouslyDoubleBuffered = _can.isDoubleBuffered();
				if (!_previouslyDoubleBuffered)
					_can.setDoubleBuffered(true);

				Rectangle r = _can.getBounds();

				_gCImage = _can.createImage(r.width, r.height);
			} // else if zoomBoxMode
				// if no modes set, select centerline and/or cross-section line
			else if (!_gui.getMovePointMode() && !_gui.getMoveLandmarkMode()) {
				Centerline centerline;
				int numCenterlinesFound = 0;
				ResizableStringArray cFoundName = new ResizableStringArray();
				ResizableIntArray cFoundDist = new ResizableIntArray();
				double dist = 0.0;
				int minDist = Integer.MAX_VALUE;
				int selectIndex = 0;
				boolean pointSelected = false;
				double x1Meters = 0.0;
				double y1Meters = 0.0;
				double x2Meters = 0.0;
				double y2Meters = 0.0;

				cc.pixelsToLength(e.getX(), e.getY(), _minX, _minY, _lengthI);

				// _nPlotter = _can._networkPlotter;
				// select centerline and/or cross-section line
				if (_nPlotter != null && _net != null) {
					for (int i = 0; i <= _net.getNumCenterlines() - 1; i++) {
						centerline = _net.getCenterline(_net.getCenterlineName(i));
						pointSelected = false;
						if (DEBUG)
							System.out.println("trying to select centerline " + _net.getCenterlineName(i));
						minDist = Integer.MAX_VALUE;
						for (int j = 0; j <= centerline.getNumCenterlinePoints() - 2; j++) {
							if (DEBUG)
								System.out.println("centerline segment " + j);
							x1Meters = centerline.getCenterlinePoint(j).getXFeet();
							y1Meters = centerline.getCenterlinePoint(j).getYFeet();
							x2Meters = centerline.getCenterlinePoint(j + 1).getXFeet();
							y2Meters = centerline.getCenterlinePoint(j + 1).getYFeet();
							// if(DEBUG)System.out.println
							// ("x1,x2,y1,y2,selectedX,
							// selectedY="+x1Meters+","+x2Meters+","+
							// y1Meters+","+y2Meters+","+
							// _centerX+length[0]+","+
							// _centerY+length[1]);

							// if(_gui.getPlanViewCanvas(0)._useZoomBox){
							dist = CsdpFunctions.shortestDistLineSegment(x1Meters, x2Meters, _lengthI[0], y1Meters,
									y2Meters, _lengthI[1]);
							// }else{
							// dist=CsdpFunctions.shortestDistLineSegment
							// (x1Meters,x2Meters,
							// length[0]-_centerX,
							// y1Meters, y2Meters,length[1]-_centerY);
							// }
							if (DEBUG)
								System.out.println("shortest dist to line segment=" + dist);
							if (dist < Double.MAX_VALUE) {
								pointSelected = true;
								if (DEBUG)
									System.out.println(
											"before min function: minDist, (int)dist=" + minDist + "," + (int) dist);
								minDist = Math.min(minDist, (int) dist);
								if (DEBUG)
									System.out.println(
											"after min function: minDist, (int)dist=" + minDist + "," + (int) dist);
							} // if
						} // for j
						if (DEBUG)
							System.out.println("minDist=" + minDist);
						// if centerline is close enough to clicked point, save
						// name and distance
						// from point.
						if (pointSelected == true) {
							cFoundDist.put(numCenterlinesFound, minDist);
							cFoundName.put(numCenterlinesFound, _net.getCenterlineName(i));
							numCenterlinesFound++;
						} // if
					} // for i
						// loop through all centerlines found and pick closest
						// one
					minDist = Integer.MAX_VALUE;
					if (DEBUG)
						System.out.println("looping through possible centerlines to find closest");
					for (int j = 0; j <= numCenterlinesFound - 1; j++) {
						centerline = _net.getCenterline(cFoundName.get(j));
						if (DEBUG)
							System.out.println("centerline, dist=" + cFoundName.get(j) + "," + cFoundDist.get(j));
						if (cFoundDist.get(j) < minDist) {
							selectIndex = j;
							minDist = cFoundDist.get(j);
						} // if
					} // for j

					/*
					 * select closest centerline
					 */
					if (numCenterlinesFound > 0) {
						if (DEBUG)
							System.out.println("selected centerline " + cFoundName.get(selectIndex));
						centerline = _net.getCenterline(cFoundName.get(selectIndex));
						_net.setSelectedCenterlineName(cFoundName.get(selectIndex));
						_net.setSelectedCenterline(_net.getCenterline(_net.getSelectedCenterlineName()));
						_net.setSelectedXsectNum(0);
						_net.setSelectedXsect(null);
						_gui.enableAfterCenterlineSelected();
						_gui.disableIfNoXsectSelected();
						_net.setNewCenterlineName(cFoundName.get(selectIndex));
						_gui.updateInfoPanel(cFoundName.get(selectIndex));
						// removed for conversion to swing

						_can.redoNextPaint();
						_can.repaint();
						/*
						 * If user clicks on same centerline, select xsect line
						 */
						//// if(_newCenterlineName.equals(_oldCenterlineName)){
						// select xsect: loop through all in centerline and see
						//// which is closest
						xsect = null;
						boolean xsectSelected = false;
						ResizableIntArray xFoundNum = new ResizableIntArray();
						ResizableIntArray xFoundDist = new ResizableIntArray();
						int numXsectFound = 0;

						minDist = Integer.MAX_VALUE;
						for (int k = 0; k <= centerline.getNumXsects() - 1; k++) {
							xsectSelected = false;
							xsect = centerline.getXsect(k);
							double[] xsectLine = _net.findXsectLineCoord(_net.getSelectedCenterlineName(), k);
							// if(_gui.getPlanViewCanvas(0)._useZoomBox){

							dist = CsdpFunctions.shortestDistLineSegment(xsectLine[x1Index], xsectLine[x2Index],
									_lengthI[0], xsectLine[y1Index], xsectLine[y2Index], _lengthI[1]);
							// }else{
							// dist = CsdpFunctions.shortestDistLineSegment
							// (xsectLine[x1Index],xsectLine[x2Index],
							// length[0]-_centerX,
							// xsectLine[y1Index],xsectLine[y2Index],
							// length[1]-_centerY);
							// }

							// if(DEBUG)System.out.println("trying to select
							// xsect line:
							// dist,x1x2x3y1y2y3="+dist+","+xsectLine[x1Index]+","+xsectLine[x2Index]+","+_centerX+length[0]+","+xsectLine[y1Index]+","+xsectLine[y2Index]+","+_centerY+length[1]);
							if (dist < Double.MAX_VALUE) {
								xsectSelected = true;
								minDist = Math.min(minDist, (int) dist);

								xFoundNum.put(numXsectFound, k);
								xFoundDist.put(numXsectFound, (int) dist);
								numXsectFound++;
							} // if searchBox
						} // for k

						System.out.println();
						for (int m = 0; m <= numXsectFound - 1; m++) {
							if (DEBUG)
								System.out.println(
										"m, xFoundDist, minDist=" + m + "," + xFoundDist.get(m) + "," + minDist);
							if (xFoundDist.get(m) <= minDist) {
								selectIndex = xFoundNum.get(m);
							} // if xFoundDist
						} // for m
						/*
						 * select closest xsect line
						 */
						if (numXsectFound > 0) {
							if (DEBUG)
								System.out.println("select xsect " + selectIndex);
							xsect = centerline.getXsect(selectIndex);
							_net.setSelectedXsectNum(selectIndex);

							_net.setSelectedXsect(centerline.getXsect(_net.getSelectedXsectNum()));
							_gui.enableAfterXsectSelected();
							_gui.updateInfoPanel(_net.getSelectedXsectNum());
							double elev = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
							if(xsect.getNumPoints()>0) {
								_gui.updateInfoPanel(xsect.getAreaSqft(elev), xsect.getWidthFeet(elev),
										xsect.getWettedPerimeterFeet(elev), xsect.getHydraulicDepthFeet(elev));
							}else {
								_gui.updateInfoPanel(0.0, 0.0, 0.0, 0.0);
							}
							// removed for conversion to swing
							_can.redoNextPaint();
							_can.repaint();
						} // if xsect found
						else {
							_gui.updateInfoPanel(-Integer.MAX_VALUE);
							_gui.updateInfoPanel(-Integer.MAX_VALUE, -Integer.MAX_VALUE, -Integer.MAX_VALUE,
									-Integer.MAX_VALUE);
						}
						//// }//if it's the same centerline
						// added for conversion to swing
						_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
					} // if found > 0 centerlines
					else {
						_net.setSelectedCenterlineName(null);
						_net.setSelectedCenterline(null);
						_net.setSelectedXsectNum(-Integer.MAX_VALUE);
						_net.setSelectedXsect(null);
						_gui.disableIfNoCenterlineSelected();
						_gui.disableIfNoXsectSelected();
						_gui.updateInfoPanel("");
						_gui.updateInfoPanel(-Integer.MAX_VALUE);
						_gui.updateInfoPanel(-Integer.MAX_VALUE, -Integer.MAX_VALUE, -Integer.MAX_VALUE,
								-Integer.MAX_VALUE);
						_net.setNewCenterlineName(cFoundName.get(selectIndex));

						// removed for conversion to swing
						_can.redoNextPaint();
						_can.repaint();
					} // else select no centerline or xsect

					_net._oldCenterlineName = _net._newCenterlineName;
				} // if _nPlotter != null
				if (_gui.getRemoveXsectMode()) {
					String centerlineName = _net.getSelectedCenterlineName();
					int selectedXsectNum = _net.getSelectedXsectNum();
					String xsectName = centerlineName+"_"+selectedXsectNum;
					int response = JOptionPane.showConfirmDialog(_gui, "Delete selected cross-section line("+xsectName+")?", 
							"Are you sure?", JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						_net.getSelectedCenterline().removeXsect(_net.getSelectedXsectNum());
						_gui.updateInfoPanel(_net.getSelectedCenterlineName());
						_gui.updateInfoPanel(_net.getSelectedXsectNum());
						// _gui.setRemoveXsectMode();
						_net.setIsUpdated(true);
						_can.redoNextPaint();
						_can.repaint();
					}
					_gui.turnOffEditModes();
				}
			} // else
		} else if (button == MouseEvent.BUTTON2_MASK) {
		} else if (button == MouseEvent.BUTTON3_MASK) {
		} // if left/middle/right click
	}// mousePressed

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
		constructRectangle();
	}

	/**
	 * Invoked when component has been moved.
	 */
	public void mouseMoved(MouseEvent e) {
		if (DEBUG)
			System.out.println("Component Event: " + e.toString());
		_gui.updateInfoPanel(e.getX(), e.getY());
	}

	/**
	 * add point to the end (usually downstream end) of centerline
	 */
	protected void addPoint() {
		double xDataCoord;
		double yDataCoord;
		Centerline centerline;
		_nPlotter = _can._networkPlotter;
		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		if (_net.getSelectedCenterlineName() != null) {
			if (DEBUG)
				System.out.println("adding centerline point");
			centerline = _net.getCenterline(_net.getSelectedCenterlineName());
			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
			// if(_gui.getPlanViewCanvas(0)._useZoomBox){
			xDataCoord = _lengthI[0];
			yDataCoord = _lengthI[1];
			// }else{
			// xDataCoord = length[0]-_centerX;
			// yDataCoord = length[1]-_centerY;
			// }
			centerline.addCenterlinePointFeet(xDataCoord, yDataCoord);
			_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			_gui.getPlanViewCanvas(0).redoNextPaint();
			_gui.getPlanViewCanvas(0).repaint();
			_net.setIsUpdated(true);
		} // if centerlineName not null
	}// addPoint

//	/*
//	 * Allows user to measure the distance between two points.
//	 * Used to determine cross-section line length when automatically drawing 
//	 * cross-section lines at computational points. 
//	 */
//	private void drawMeasurementLine() {
//		double xDataCoord;
//		double yDataCoord;
//		_nPlotter = _can._networkPlotter;
//		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
//		CoordConv cc = zs.getCoordConv();
//		if(_numMeasurementPoints>0) {
//			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
//			xDataCoord = _lengthI[0];
//			yDataCoord = _lengthI[1];
//		}else {
//			
//		}
//	}
	
	/**
	 * move centerline point
	 */
	protected void movePoint() {
		if (DEBUG)
			System.out.println("NetworkInteractor.movePoint called");
		double xDataCoordInitial = -Double.MAX_VALUE;
		double yDataCoordInitial = -Double.MAX_VALUE;
		double xDataCoordFinal = -Double.MAX_VALUE;
		double yDataCoordFinal = -Double.MAX_VALUE;
		Centerline centerline = null;
		int minDistIndex = -Integer.MAX_VALUE;
		double distToNearestPoint = 0.0;
		CenterlinePoint point = null;
		if (_net.getSelectedCenterlineName() != null) {
			if (DEBUG)
				System.out.println("moving centerline point");
			centerline = _net.getCenterline(_net.getSelectedCenterlineName());

			if (DEBUG)
				System.out.println("xi, yi, xf, yf=" + _xi + "," + _yi + " " + _xf + "," + _yf);

			_nPlotter = _can._networkPlotter;
			ZoomState zs = _bathymetryPlot.getCurrentZoomState();
			CoordConv cc = zs.getCoordConv();

			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
			cc.pixelsToLength(_xf, _yf, _minX, _minY, _lengthF);
			// if(_gui.getPlanViewCanvas(0)._useZoomBox){
			xDataCoordInitial = _lengthI[0];
			yDataCoordInitial = _lengthI[1];
			xDataCoordFinal = _lengthF[0];
			yDataCoordFinal = _lengthF[1];
			// }else{
			// xDataCoordInitial = lengthI[0]-_centerX;
			// yDataCoordInitial = lengthI[1]-_centerY;
			// xDataCoordFinal = lengthF[0]-_centerX;
			// yDataCoordFinal = lengthF[1]-_centerY;
			// }

			minDistIndex = centerline.getNearestPointIndex(xDataCoordInitial, yDataCoordInitial);
			point = centerline.getCenterlinePoint(minDistIndex);
			distToNearestPoint = CsdpFunctions.pointDist(xDataCoordInitial, yDataCoordInitial, point.getXFeet(),
					point.getYFeet());

			double[] pb = zs.getPlotBoundaries();
			double minX = pb[CsdpFunctions.minXIndex];
			double maxX = pb[CsdpFunctions.maxXIndex];
			double minY = pb[CsdpFunctions.minYIndex];
			double maxY = pb[CsdpFunctions.maxYIndex];
			double minDimension = Math.min(maxX - minX, maxY - minY);
			// double maxSearchDist = minDimension/100.0;
			double maxSearchDist = minDimension / 10.0;

			if (distToNearestPoint < maxSearchDist) {
				point.putXFeet(xDataCoordFinal);
				point.putYFeet(yDataCoordFinal);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				// removed for conversion to swing
				//
				_gui.getPlanViewCanvas(0).redoNextPaint();
				_gui.getPlanViewCanvas(0).repaint();
				// _gui.getPlanViewCanvas(0).validate();
			} else {
				if (DEBUG)
					System.out.println("not changing point coord: distToNearestPoint =" + distToNearestPoint);
			}
		} // if centerlineName not null
		_net.setIsUpdated(true);
	}// movePoint

	/**
	 * insert point between two existing points in a centerline
	 */
	protected void insertPoint() {
		double xDataCoord;
		double yDataCoord;
		Centerline centerline;
		_nPlotter = _can._networkPlotter;
		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		if (_net.getSelectedCenterlineName() != null) {
			if (DEBUG)
				System.out.println("inserting centerline point");
			centerline = _net.getCenterline(_net.getSelectedCenterlineName());

			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
			// if(_gui.getPlanViewCanvas(0)._useZoomBox){
			xDataCoord = _lengthI[0];
			yDataCoord = _lengthI[1];
			// }else{
			// xDataCoord = length[0]-_centerX;
			// yDataCoord = length[1]-_centerY;
			// }
			centerline.insertCenterlinePointFeet(xDataCoord, yDataCoord);
			_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			_gui.getPlanViewCanvas(0).redoNextPaint();
			_gui.getPlanViewCanvas(0).repaint();
		} // if centerlineName not null
		_net.setIsUpdated(true);
	}// insertPoint

	/**
	 * delete point in centerline
	 */
	protected void deletePoint() {
		double xDataCoord;
		double yDataCoord;
		Centerline centerline;
		Xsect xsect;
		_nPlotter = _can._networkPlotter;
		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		if (_net.getSelectedCenterlineName() != null) {
			if (DEBUG)
				System.out.println("deleting centerline point");
			centerline = _net.getCenterline(_net.getSelectedCenterlineName());
			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
			// if(_gui.getPlanViewCanvas(0)._useZoomBox){
			xDataCoord = _lengthI[0];
			yDataCoord = _lengthI[1];
			// }else{
			// xDataCoord = length[0]-_centerX;
			// yDataCoord = length[1]-_centerY;
			// }
			centerline.deleteCenterlinePoint(xDataCoord, yDataCoord);
			if (centerline.getNumCenterlinePoints() <= 0) {
				_net.removeCenterline(_net.getSelectedCenterlineName());
				_gui.disableIfNoCenterlineSelected();
			}
			for (int i = centerline.getNumXsects() - 1; i > 0; i--) {
				xsect = centerline.getXsect(i);
				if (xsect.getDistAlongCenterlineFeet() > centerline.getLengthFeet()
						|| xsect.getDistAlongCenterlineFeet() < 0) {
					centerline.removeXsect(i);
				}
			}
			_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			_gui.getPlanViewCanvas(0).redoNextPaint();
			_gui.getPlanViewCanvas(0).repaint();
		} // if centerlineName not null
		_net.setIsUpdated(true);
	}

	/**
	 * move a cross-section along its centerline
	 */
	protected void moveXsect() {
		if (DEBUG)
			System.out.println("moving xsect");
		Centerline centerline = _net.getSelectedCenterline();
		String centerlineName = centerline.getCenterlineName();
		double minDist = Double.MAX_VALUE;
		double dist = 0.0;
		int xsectNum = _net.getSelectedXsectNum();
		Xsect xsect = _net.getSelectedXsect();
		Vector xsectPoints = null;
		int numPoints = 0;
		double x1 = 0.0;
		double x2 = 0.0;
		double y1 = 0.0;
		double y2 = 0.0;
		double xDataCoord = -Double.MAX_VALUE;
		double yDataCoord = -Double.MAX_VALUE;
		int minDistIndex = 0;
		_nPlotter = _can._networkPlotter;
		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
		// if(_gui.getPlanViewCanvas(0)._useZoomBox){
		xDataCoord = _lengthI[0];
		yDataCoord = _lengthI[1];
		// }else{
		// xDataCoord = length[0]-_centerX;
		// yDataCoord = length[1]-_centerY;
		// }

		if (xsect != null) {
			xsectPoints = xsect.getAllPoints();
			numPoints = xsect.getNumPoints();
		}

		// removing by number: sometimes number changes.
		for (int i = 0; i <= centerline.getNumCenterlinePoints() - 2; i++) {
			x1 = centerline.getCenterlinePoint(i).getXFeet();
			y1 = centerline.getCenterlinePoint(i).getYFeet();
			x2 = centerline.getCenterlinePoint(i + 1).getXFeet();
			y2 = centerline.getCenterlinePoint(i + 1).getYFeet();
			dist = CsdpFunctions.shortestDistLineSegment(x1, x2, xDataCoord, y1, y2, yDataCoord, MAX_XSECT_LINE_LENGTH);
			if (dist < minDist) {
				minDist = dist;
				minDistIndex = i;
			}
		} // for i

		if (DEBUG)
			System.out.println("minDist, Double.MAX_VALUE " + minDist + "," + Double.MAX_VALUE);

		if (minDist < Double.MAX_VALUE) {
			centerline.removeXsect(xsectNum);
			addXsect();
			if (xsectPoints != null)
				xsect.putAllPoints(numPoints, xsectPoints);
		}
		// centerline.sortXsects();
		_net.setIsUpdated(true);
		//// the following method isn't working yet.
		//// _app.updateXsectGraph(centerlineName, xsectNum);

		_gui.setDefaultModesStates();

		_app.updateXsect(centerlineName, xsectNum);
	}// moveXsect

	/**
	 * add cross-section line to existing centerline
	 */
	protected void addXsect() {
		int selectedXsectNum = 0;
		Xsect xsect = null;
		double xDataCoord = 0.0;
		double yDataCoord = 0.0;
		Centerline centerline = null;
		/*
		 * Minimum perpendicular distance from the point on which the mouse was
		 * pressed to a centerline segment
		 */
		double minDist = Double.MAX_VALUE;
		/*
		 * Index of first centerline point in the centerline segment that is
		 * closest to the point on which the mouse was pressed
		 */
		int minDistIndex = Integer.MAX_VALUE;
		double x1 = 0.0;
		double y1 = 0.0;
		double x2 = 0.0;
		double y2 = 0.0;
		double dist = 0.0;
		double theta = 0.0;
		double xi = 0.0;
		double yi = 0.0;
		double cumDist = 0.0;
		ResizableIntArray xsectIndices = new ResizableIntArray();
		_nPlotter = _can._networkPlotter;
		ZoomState zs = _bathymetryPlot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		if (_net.getSelectedCenterlineName() != null) {
			if (DEBUG)
				System.out.println("adding cross-section");
			centerline = _net.getCenterline(_net.getSelectedCenterlineName());
			cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
			// if(_gui.getPlanViewCanvas(0)._useZoomBox){
			xDataCoord = _lengthI[0];
			yDataCoord = _lengthI[1];
			// }else{
			// xDataCoord = length[0]-_centerX;
			// yDataCoord = length[1]-_centerY;
			// }
			if (DEBUG)
				System.out.println("trying to add xsect to centerline " + _net.getSelectedCenterlineName()
						+ ", x,y initial point=" + xDataCoord + "," + yDataCoord);

			// loop through all centerline segments; find minimum perpendicular
			// distance
			// and index of first point of line segment that has minimum
			// perpendicular dist
			for (int i = 0; i <= centerline.getNumCenterlinePoints() - 2; i++) {
				x1 = centerline.getCenterlinePoint(i).getXFeet();
				y1 = centerline.getCenterlinePoint(i).getYFeet();
				x2 = centerline.getCenterlinePoint(i + 1).getXFeet();
				y2 = centerline.getCenterlinePoint(i + 1).getYFeet();
				dist = CsdpFunctions.shortestDistLineSegment(x1, x2, xDataCoord, y1, y2, yDataCoord,
						MAX_XSECT_LINE_LENGTH);
				if (DEBUG)
					System.out.println("line segment, shortest dist, x1x2x3y1y2y3=" + i + "," + dist + "," + x1 + ","
							+ x2 + "," + xDataCoord + "," + y1 + "," + y2 + "," + yDataCoord);
				if (dist < minDist) {
					minDist = dist;
					minDistIndex = i;
				} // if
			} // for i

			if (DEBUG)
				System.out.println("minDistIndex, min dist=" + minDistIndex + "," + minDist);
			if (minDist < Double.MAX_VALUE) {
				x1 = centerline.getCenterlinePoint(minDistIndex).getXFeet();
				y1 = centerline.getCenterlinePoint(minDistIndex).getYFeet();
				x2 = centerline.getCenterlinePoint(minDistIndex + 1).getXFeet();
				y2 = centerline.getCenterlinePoint(minDistIndex + 1).getYFeet();
				theta = CsdpFunctions.getTheta(x1, x2, y1, y2);
				xi = CsdpFunctions.findXIntersection(x1, x2, xDataCoord, y1, y2, yDataCoord);
				yi = CsdpFunctions.findYIntersection(x1, x2, xDataCoord, y1, y2, yDataCoord);

				if (DEBUG)
					System.out.println("Intersection coord:" + xi + "," + yi);

				// find dist from first point in centerline to first point in
				// centerline
				// segment that contains the xsect
				cumDist = 0.0;
				if (DEBUG)
					System.out.println("minDistIndex=" + minDistIndex);
				for (int i = 0; i <= minDistIndex - 1 && minDistIndex > 0; i++) {
					x1 = centerline.getCenterlinePoint(i).getXFeet();
					y1 = centerline.getCenterlinePoint(i).getYFeet();
					x2 = centerline.getCenterlinePoint(i + 1).getXFeet();
					y2 = centerline.getCenterlinePoint(i + 1).getYFeet();
					cumDist += CsdpFunctions.pointDist(x1, y1, x2, y2);
					if (DEBUG)
						System.out.println("increasing cumDist:" + cumDist);
				} // for i

				x1 = centerline.getCenterlinePoint(minDistIndex).getXFeet();
				y1 = centerline.getCenterlinePoint(minDistIndex).getYFeet();
				cumDist += CsdpFunctions.pointDist(x1, y1, xi, yi);

				if (DEBUG)
					System.out.println("cumDist=" + cumDist);

				// find index of last xsect that is closer to first point in
				// centerline.
				// Xsect xsect = null;
				int lastIndex = 0;
				int numXsects = 0;
				if (minDist < Double.MAX_VALUE) {
					numXsects = centerline.getNumXsects();
					if (numXsects == 0) {
						centerline.addXsect();
						xsect = centerline.getXsect(0);
					} else {
						for (int i = 0; i <= numXsects - 1; i++) {
							xsect = centerline.getXsect(i);
							if (xsect.getDistAlongCenterlineFeet() < cumDist)
								lastIndex = i;
						}
						if (DEBUG)
							System.out.println("lastindex = " + lastIndex);
						centerline.addXsectAt(lastIndex + 1);
						xsect = centerline.getXsect(lastIndex + 1);
					} // else

					xsect.putDistAlongCenterlineFeet(cumDist);
					xsect.putXsectLineLengthFeet(minDist * 2.0f);

					// sort
					xsectIndices = centerline.sortXsectArray();
					_app.renameOpenXsectGraphs(_net.getSelectedCenterlineName(), xsectIndices);

					_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
					// removed for conversion to swing
					_gui.getPlanViewCanvas(0).redoNextPaint();
					_gui.getPlanViewCanvas(0).repaint();

					if (numXsects == 0)
						selectedXsectNum = lastIndex;
					else
						selectedXsectNum = lastIndex + 1;

					selectedXsectNum = xsectIndices.get(selectedXsectNum);

					if (DEBUG)
						System.out.println("selected xsect number =" + selectedXsectNum);

					_net.setSelectedXsectNum(selectedXsectNum);
					_net.setSelectedXsect(xsect);
					_gui.enableAfterXsectSelected();

					_gui.updateInfoPanel(_net.getSelectedXsectNum());
				} // if
			} // if minDist < BIG_DOUBLE
		} // if a centerline has been selected
		_net.setIsUpdated(true);
		_gui.setDefaultModesStates();
	}// addXsect

	
	/*
	 * Create a cross-section line at every computational point location
	 */
	public void addXsectsAtComputationalPoints(double deltaX, double xsectLineLength) {
		Centerline centerline = _net.getSelectedCenterline();
		int numXSToAdd = centerline.getNumComputationalPoints(deltaX);
		double length = centerline.getLengthFeet();
		double deltaXActual = length / (double)(numXSToAdd - 1);
		for(int i=0; i<numXSToAdd; i++) {
			double distance = deltaXActual*(double)i;
			//if placing cross-section at endpoints, make it at least 10 feet from end, or 1% of channel length. 
			//being within 5% of channel length from endpoint is desirable, because in DSM2, this will prevent interpolation
			//from connected channel.
			//This should make it easier to distinguish from cross-section at endpoint of connecting channel
			if(distance == 0.0) {
				distance += Math.max(10.0, .01*length);
			}else if(distance==length) {
				distance -= Math.max(10.0, .01*length);
			}
			addXsectAtDistance(centerline, distance, xsectLineLength);
		}
	}//addXsectsAtComputationalPoints
	
	/*
	 * Add a cross-section line at the specified distance. If distance is zero or 
	 * equal to the channel length, move so it's not right at the end of the channel.
	 */
	private void addXsectAtDistance(Centerline centerline, double distance, double xsectLineLength) {
		int lastIndex = 0;
		Xsect xsect = null;
		int numXsects = centerline.getNumXsects();
		if(numXsects == 0){
			centerline.addXsect();
			xsect = centerline.getXsect(0);
		}else {
			for (int i = 0; i < centerline.getNumXsects(); i++) {
				xsect = centerline.getXsect(i);
				if (xsect.getDistAlongCenterlineFeet() < distance)
					lastIndex = i;
			}
			centerline.addXsectAt(lastIndex + 1);
			xsect = centerline.getXsect(lastIndex + 1);
		}
		xsect.putDistAlongCenterlineFeet(distance);
		xsect.putXsectLineLengthFeet(xsectLineLength);

		// sort
		ResizableIntArray xsectIndices = centerline.sortXsectArray();
		_app.renameOpenXsectGraphs(_net.getSelectedCenterlineName(), xsectIndices);
	
		_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
		// removed for conversion to swing
		_gui.getPlanViewCanvas(0).redoNextPaint();
		_gui.getPlanViewCanvas(0).repaint();
	
		int selectedXsectNum = 0;
		if (numXsects == 0)
			selectedXsectNum = lastIndex;
		else
			selectedXsectNum = lastIndex + 1;
	
		selectedXsectNum = xsectIndices.get(selectedXsectNum);
	
		if (DEBUG)
			System.out.println("selected xsect number =" + selectedXsectNum);

		_net.setSelectedXsectNum(selectedXsectNum);
		_net.setSelectedXsect(xsect);
		_gui.enableAfterXsectSelected();
	
		_gui.updateInfoPanel(_net.getSelectedXsectNum());
		
		_net.setIsUpdated(true);
		_gui.setDefaultModesStates();
	}//addXsectAtDistance

	
	
	/**
	 * find midpoint of xsect line, which is the intersection of the xsect line
	 * and the centerline. Arguments are coordinates of xsect line.
	 */
	protected int[] findIntersection(int x1, int y1, int x2, int y2) {
		int[] returnValues = new int[2];
		double x1double = (double) x1;
		double y1double = (double) y1;
		double x2double = (double) x2;
		double y2double = (double) y2;

		double slope = ((y2double - y1double) / (x2double - x1double));
		double x = x1double + 0.5f * (x2double - x1double);
		double y = slope * (x - x1double) + y1double;
		returnValues[x1Index] = (int) x;
		returnValues[y1Index] = (int) y;
		return returnValues;
	}// findIntersection

	/**
	 * Invoked when a mouse button has been released on a component.
	 */
	public void mouseReleased(MouseEvent e) {
		setFinalPoint(e.getX(), e.getY());
		if (_bathymetryPlot != null) {

			if (DEBUG)
				System.out.println("mouse released:  final point=" + e.getX() + "," + e.getY());
			_nPlotter = _can._networkPlotter;
			_nPlotter = _can._networkPlotter;
			ZoomState zs = _bathymetryPlot.getCurrentZoomState();
			if (_nPlotter != null) {
				_minSlope = zs.getMinSlope();
				_height = _nPlotter._height;
				// _centerX = _nPlotter._centerX;
				// _centerY = _nPlotter._centerY;
			}
			if (_bathymetryPlot != null && _bathymetryPlot._bathymetryData != null) {
				// _minX = _bathymetryPlot._bathymetryData.getMinX();
				// _minY = _bathymetryPlot._bathymetryData.getMinY();
				double[] bb = zs.getPlotBoundaries();
				_minX = bb[CsdpFunctions.minXIndex];
				_minY = bb[CsdpFunctions.minYIndex];
			}
			if (_gui.getMovePointMode())
				movePoint();
			if (_gui.getZoomBoxMode()) {
				_drawDragRect = false;
				if (_zoomRect.width <= 5 || _zoomRect.height <= 5) {
					_gui.pressCursorButton();
				} else {
					_can.zoomInOut(_zoomRect);
				}
			} // if zoom mode
			if (_gui.getZoomPanMode()) {
				if (Math.abs(_xi - _xf) < 5 && Math.abs(_yi - _yf) < 5) {
					_gui.pressCursorButton();
				} else {
					_can.zoomPan(_xi, _yi, _xf, _yf);
				}
			}
		}
		_mouseDragged = false;
	}// mouseReleased

//	public void selectChannelByName(String chan) {
//		
//	}
	
	/*
	 * If the specified centerline exists in the network, 
	 * select the centerline
	 * find the min/max easting and northings of all the centerline points or
	 *    maybe just the first point, and zoom in to a size that is about 
	 */
	public void zoomToCenterline(String centerlineName) {
		if(_net.centerlineExists(centerlineName)) {
			//find point that is in the middle of all centerline pionts
			Centerline centerline = _net.getCenterline(centerlineName);
			double[] centerlineMinMax = centerline.getMinMaxCenterlinePointCoordinatesFeet();
			double xMin = centerlineMinMax[0];
			double xMax = centerlineMinMax[1];
			double yMin = centerlineMinMax[2];
			double yMax = centerlineMinMax[3];
			double xMid = xMin+0.5*(xMax-xMin);
			double yMid = yMin+0.5*(yMax-yMin);

			//set zoom window to 10x the centerline dimensions
			double zoomWidth = 5.0*(xMax-xMin);
			double zoomHeight = 5.0*(yMax-yMin);
			
//			these need to be Screen coordinates, not data coordinates. see BathymetryPlot.drawpoints for how to do this
			ZoomState zs = _bathymetryPlot.getCurrentZoomState();
			CoordConv cc = zs.getCoordConv();

			double[] pb = zs.getPlotBoundaries();
			double minX = pb[CsdpFunctions.minXIndex];
			double minY = pb[CsdpFunctions.minYIndex];
			double maxY = pb[CsdpFunctions.maxYIndex];
			int[] pix = null;

			double xInitial = xMid-0.5*zoomWidth;
			double yInitial = yMid+0.5*zoomHeight;
			double xFinal = xMid+0.5*zoomWidth;
			double yFinal = yMid-0.5*zoomHeight;
			
			int[] initialPoint = cc.utmToPixels(xInitial, yInitial, minX, minY);
			int xInitialPixels = initialPoint[0];
			int yInitialPixels = initialPoint[1];
			int[] finalPoint = cc.utmToPixels(xFinal, yFinal, minX, minY);
			int xFinalPixels = finalPoint[0];
			int yFinalPixels = finalPoint[1];
			
			if(DEBUG) {
				System.out.println("zoomToCenterline: xInitial, yInitial, xFinal, yFinal="+
						xInitial+","+yInitial+","+xFinal+","+yFinal);
				System.out.println("zoomToCenterline: xInitialPixels, yInitialPixels, xFinalPixels, yFinalPixels="+
						xInitialPixels+","+yInitialPixels+","+xFinalPixels+","+yFinalPixels);
			}
			setInitialPoint(xInitialPixels, yInitialPixels);
			setFinalPoint(xFinalPixels, yFinalPixels);

			if (_bathymetryPlot != null) {
				_nPlotter = _can._networkPlotter;
				if (_nPlotter != null) {
					_minSlope = zs.getMinSlope();
					_height = _nPlotter._height;
					// _centerX = _nPlotter._centerX;
					// _centerY = _nPlotter._centerY;
				}
				if (_bathymetryPlot != null && _bathymetryPlot._bathymetryData != null) {
					// _minX = _bathymetryPlot._bathymetryData.getMinX();
					// _minY = _bathymetryPlot._bathymetryData.getMinY();
					double[] bb = zs.getPlotBoundaries();
					_minX = bb[CsdpFunctions.minXIndex];
					_minY = bb[CsdpFunctions.minYIndex];
				}
				_drawDragRect = false;
				if (_zoomRect.width <= 5 || _zoomRect.height <= 5) {
					if(DEBUG) System.out.println("Not zooming");
					_gui.pressCursorButton();
				} else {
					if(DEBUG) System.out.println("zooming");

					_net.setSelectedCenterlineName(centerlineName);
					_net.setSelectedCenterline(centerline);
					_net.setSelectedXsectNum(0);
					_net.setSelectedXsect(null);
					_gui.enableAfterCenterlineSelected();
					_gui.disableIfNoXsectSelected();
					_net.setNewCenterlineName(centerlineName);
					_gui.updateInfoPanel(centerlineName);
					
					_can.zoomInOut(_zoomRect);
				}
			}
			_mouseDragged = false;
		}else {
			JOptionPane.showMessageDialog(_gui, "Centerline name not found", "Error", JOptionPane.OK_OPTION);
		}
	}//zoomToChannel
	
	public void zoomToNode(String node) {
		Landmark landmark = _gui.getLandmark();
		if(landmark != null && landmark.containsLandmark(node)) {
			//find point that is in the middle of all centerline pionts
			double landmarkX = landmark.getXFeet(node);
			double landmarkY = landmark.getYFeet(node);
			//an arbitrary value...seems reasonable.
			double zoomWidowDimension = 20000.0;
			double xMin = landmarkX-zoomWidowDimension/2.0;
			double xMax = landmarkX+zoomWidowDimension/2.0;
			double yMin = landmarkY-zoomWidowDimension/2.0;
			double yMax = landmarkY+zoomWidowDimension/2.0;
			double xMid = landmarkX;
			double yMid = landmarkY;

			double zoomWidth = (xMax-xMin);
			double zoomHeight = (yMax-yMin);
			
//			these need to be Screen coordinates, not data coordinates. see BathymetryPlot.drawpoints for how to do this
			ZoomState zs = _bathymetryPlot.getCurrentZoomState();
			CoordConv cc = zs.getCoordConv();

			double[] pb = zs.getPlotBoundaries();
			double minX = pb[CsdpFunctions.minXIndex];
			double minY = pb[CsdpFunctions.minYIndex];

			double xInitial = xMid-0.5*zoomWidth;
			double yInitial = yMid+0.5*zoomHeight;
			double xFinal = xMid+0.5*zoomWidth;
			double yFinal = yMid-0.5*zoomHeight;
			
			int[] initialPoint = cc.utmToPixels(xInitial, yInitial, minX, minY);
			int xInitialPixels = initialPoint[0];
			int yInitialPixels = initialPoint[1];
			int[] finalPoint = cc.utmToPixels(xFinal, yFinal, minX, minY);
			int xFinalPixels = finalPoint[0];
			int yFinalPixels = finalPoint[1];
			
//			if(DEBUG) {
				System.out.println("zoomToNode: xInitial, yInitial, xFinal, yFinal="+
						xInitial+","+yInitial+","+xFinal+","+yFinal);
				System.out.println("zoomToNode: xInitialPixels, yInitialPixels, xFinalPixels, yFinalPixels="+
						xInitialPixels+","+yInitialPixels+","+xFinalPixels+","+yFinalPixels);
//			}
			setInitialPoint(xInitialPixels, yInitialPixels);
			setFinalPoint(xFinalPixels, yFinalPixels);

			if (_bathymetryPlot != null) {
				_nPlotter = _can._networkPlotter;
				_minSlope = zs.getMinSlope();
				if (_nPlotter != null) {
					_minSlope = zs.getMinSlope();
					_height = _nPlotter._height;
					// _centerX = _nPlotter._centerX;
					// _centerY = _nPlotter._centerY;
				}
				if (_bathymetryPlot != null && _bathymetryPlot._bathymetryData != null) {
					// _minX = _bathymetryPlot._bathymetryData.getMinX();
					// _minY = _bathymetryPlot._bathymetryData.getMinY();
					double[] bb = zs.getPlotBoundaries();
					_minX = bb[CsdpFunctions.minXIndex];
					_minY = bb[CsdpFunctions.minYIndex];
				}
				_drawDragRect = false;
				if (_zoomRect.width <= 5 || _zoomRect.height <= 5) {
					if(DEBUG) System.out.println("Not zooming");
					_gui.pressCursorButton();
				} else {
					if(DEBUG) System.out.println("zooming");
					_can.zoomInOut(_zoomRect);
					_gui.pressCursorButton();
				}
			}
			_mouseDragged = false;
		}else {
			JOptionPane.showMessageDialog(_gui, "Node name not found", "Error", JOptionPane.OK_OPTION);
		}
	}//zoomToNode
	
	/**
	 * Constructs a rectangle from a set of diagonally opposite points. Stores
	 * rectangle in a stack.
	 */
	protected void constructRectangle() {
		_zoomRect.x = Math.min(_xi, _xf);
		_zoomRect.y = Math.min(_yi, _yf);
		_zoomRect.width = Math.abs(_xi - _xf);
		_zoomRect.height = Math.abs(_yi - _yf);

		// System.out.println("constructing rectangle. _xi, _xf, _yi,
		// _yf="+_xi+","+_xf+","+_yi+","+_yf);

	}// contructRectangle

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
		if (DEBUG)
			System.out.println("Mouse Entered at ( " + e.getX() + ", " + e.getY() + " )");
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
		if (DEBUG)
			System.out.println("Mouse Exited at ( " + e.getX() + ", " + e.getY() + " )");
	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * Mouse drag events will continue to be delivered to the component where
	 * the first originated until the mouse button is released (regardless of
	 * whether the mouse position is within the bounds of the component).
	 */
	public void mouseDragged(MouseEvent e) {
		if (_drawDragRect)
			_mouseDragged = true;
		if (_gui.getZoomBoxMode()) {
			Graphics g = _gCImage.getGraphics();
			Rectangle bounds = _can.getBounds();
			bounds.x = 0;
			bounds.y = 0;
			//// g.setClip(bounds);
			g.clipRect(0, 0, bounds.width, bounds.height);

			// for changing to Swing
			// g.drawImage(_can.getBackgroundImage(),0,0,null);
			g.drawImage(_can.getForegroundImage(), 0, 0, null);

			setFinalPoint(e.getX(), e.getY());
			Rectangle r = _zoomRect;
			g.setColor(_zoomRectColor);
			g.drawRect(r.x, r.y, r.width, r.height);
			_can.getGraphics().drawImage(_gCImage, 0, 0, null);

		} else if (_gui.getZoomPanMode()) {
			setFinalPoint(e.getX(), e.getY());
			Graphics g = _gCImage.getGraphics();
			Rectangle bounds = _can.getBounds();
			bounds.x = 0;
			bounds.y = 0;
			g.clipRect(0, 0, bounds.width, bounds.height);
			g.drawImage(_can.getForegroundImage(), 0, 0, null);
			g.setColor(_zoomRectColor);
			g.drawLine(_xi, _yi, _xf, _yf);
			_can.getGraphics().drawImage(_gCImage, 0, 0, null);
		}
	}// mouseDragged

	/**
	 * Invoked when component has been moved.
	 */
	public void componentMoved(ComponentEvent e) {
		if (DEBUG)
			System.out.println("Component Event: " + e.toString());
	}

	/**
	 * sets Network object
	 */
	public void setNetwork(Network net) {
		_net = net;
	}

	/**
	 * sets BathymetryPlot object
	 */
	public void setPlotter(BathymetryPlot plot) {
		_bathymetryPlot = plot;
	}

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
	public static final boolean DEBUG = false;
	PlanViewCanvas _can;
	Network _net;
	CsdpFrame _gui;
	BathymetryPlot _bathymetryPlot;
	// protected boolean _centerlineSelected = false;
	NetworkPlot _nPlotter;
	/*
	 * number of pixels to search for centerline
	 */
	protected int SELECT_RANGE = 30;
	protected static final int x1Index = 0;
	protected static final int y1Index = 1;
	protected static final int x2Index = 2;
	protected static final int y2Index = 3;
	protected boolean _mouseDragged = false;
	protected static final double MAX_XSECT_LINE_LENGTH = 1.0e6;
	protected double _minSlope = 0.0;
	protected double _minX = 0.0;
	protected double _minY = 0.0;
	protected double _height = 0.0;
	// /**
	// * centerX is the value in meters to add to all x coordinates so that the
	// data
	// * will be centered in the window in the x direction
	// */
	// float _centerX = 0.0f;
	// /**
	// * centerY is the value in meters to add to all y coordinates so that the
	// data
	// * will be centered in the window in the y direction
	// */
	// float _centerY = 0.0f;
	// private final float MAX_SEARCH_DIST = 1000.0f;

	protected boolean _drawDragRect = true;
	protected boolean _previouslyDoubleBuffered = true;
	protected Image _gCImage;
	protected Rectangle _zoomRect = new Rectangle(0, 0, 0, 0);
	protected Color _zoomRectColor = Color.black;
	protected App _app;
	/*
	 * Initial cursor position converted to length units.
	 */
	private double[] _lengthI = new double[2];
	/**
	 * Final cursor position converted to length units.
	 */
	private double[] _lengthF = new double[2];

}// NetworkInteractor
