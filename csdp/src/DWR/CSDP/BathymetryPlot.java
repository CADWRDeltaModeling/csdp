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
 * Plot Bathymetry Data on screen
 *
 * @author
 * @version $Id: BathymetryPlot.java,v 1.6 2005/04/08 00:16:46 btom Exp $
 */
public class BathymetryPlot extends PlanViewPlot {

	/**
	 * constructor
	 */
	public BathymetryPlot(CsdpFrame gui, BathymetryData data, App app) {
		super(gui, data, app);
		_binSelected = new boolean[_gui.getNumColors()];
		for (int i = 0; i <= _gui.getNumColors() - 1; i++) {
			_binSelected[i] = false;
		} // for
	}// constructor

	/**
	 * Plot data points, top view. Points are plotted as square rectangles;
	 * requested size of points will determine number of pixels used for each
	 * square.
	 */
	public void drawPoints(Graphics2D g, Rectangle bounds, ZoomState zs) {
		double ps = (double) POINT_SIZE;
		int pointSize = (int) ps;// height and width of points (sqaures)

		// update AffineTransform
		CoordConv cc = zs.getCoordConv();
		// double minSlope = zs.getMinSlope();

		double[] pb = zs.getPlotBoundaries();
		double minX = pb[CsdpFunctions.minXIndex];
		double minY = pb[CsdpFunctions.minYIndex];

		double maxY = pb[CsdpFunctions.maxYIndex];
		// System.out.println("BathymetryPlot.drawPoints: minX, minY, minSlope,
		// _height="+
		// CsdpFunctions.feetToMeters(minX)+","+
		// CsdpFunctions.feetToMeters(minY)+","+
		// minSlope+","+_height);

		// not working
		// plotGrid(g, bounds);

		int yearIndex = 0;
		int sourceIndex = 0;

		int minBathIndex = -Integer.MAX_VALUE;
		int maxBathIndex = -Integer.MAX_VALUE;
		if (zs.getZoomFit()) {
			minBathIndex = 0;
			maxBathIndex = _bathymetryData.getNumLines() - 1;
			if (DEBUG)
				System.out.println("BathymetryPlot: zoomFIT!");
		} else {
			int[] bathIndices = findDisplayRegionIndices(minY, maxY, pb);
			minBathIndex = bathIndices[0];
			maxBathIndex = bathIndices[1];
		}
		// for(int i=0; i<=_bathymetryData.getNumLines()-1; i++){
		for (int i = minBathIndex; i <= maxBathIndex; i++) {
			yearIndex = _bathymetryData.getYearIndex(i);
			sourceIndex = _bathymetryData.getSourceIndex(i);
			if (_bathymetryData.getPlotYear(yearIndex) && _bathymetryData.getPlotSource(sourceIndex)) {
				_bathymetryData.getPointFeet(i, _bPlotPoint);
				if (_gui.getColorUniform())
					g.setColor(_gui.getColor(7));
				else if (_gui.getColorByElev())
					g.setColor(colorByDepth(_bPlotPoint[CsdpFunctions.zIndex]));
				else if (_gui.getColorBySource()) {
					g.setColor(colorByIndex(sourceIndex));
				} else if (_gui.getColorByYear()) {
					g.setColor(colorByIndex(yearIndex));
				}

				int[] pix = null;

				pix = cc.utmToPixels(_bPlotPoint[CsdpFunctions.xIndex], _bPlotPoint[CsdpFunctions.yIndex], minX, minY);
				_xPixels = pix[0];
				_yPixels = pix[1];

				if (_xPixels > 0 && _yPixels > 0 && _xPixels < _width && _yPixels < _height) {
					if (pointSize > 1)
						g.fillRect(_xPixels, _yPixels, pointSize, pointSize);
					else if (pointSize == 1)
						g.drawLine(_xPixels, _yPixels, _xPixels, _yPixels);
				}
			} // if data display for this year and source
		} // for i

	} // drawPoints

	/**
	 * Use modified binary search to find indexes of last value above zoomBox
	 * and first value after. at end, high will be equal to low+1. In a standard
	 * binary search, you're trying to find unique exact match, and low=high.
	 */
	private int[] findDisplayRegionIndices(double minY, double maxY, double[] pb) {
		int minBathIndex = -Integer.MAX_VALUE;
		int maxBathIndex = -Integer.MAX_VALUE;
		int[] returnValues = new int[2];
		int low = 0;
		int high = _bathymetryData.getNumLines() - 1;
		int iter = 0;
		while (high - low > 1) {
			iter++;
			if (DEBUG)
				System.out
						.println("finding minBathIndex: iteration number, low, high " + iter + "," + low + "," + high);
			int mid = (low + high) / 2;
			int c = -Integer.MAX_VALUE;
			_bathymetryData.getPointFeet(mid, _bPlotPointMid);

			if (minY < _bPlotPointMid[CsdpFunctions.yIndex])
				high = mid - 1;
			else if (minY > _bPlotPointMid[CsdpFunctions.yIndex])
				low = mid;
			else if (minY == _bPlotPointMid[CsdpFunctions.yIndex])
				high = mid - 1;
			if (low > high)
				System.out.println("ERROR in BathymetryPlot.drawPoints: low>high");
		}

		minBathIndex = low;
		low = 0;
		high = _bathymetryData.getNumLines() - 1;
		iter = 0;
		while (high - low > 1) {
			iter++;
			if (DEBUG)
				System.out
						.println("finding maxBathIndex: iteration number, low, high= " + iter + "," + low + "," + high);
			int mid = (low + high) / 2;
			int c = -Integer.MAX_VALUE;
			_bathymetryData.getPointFeet(mid, _bPlotPointMid);
			if (maxY < _bPlotPointMid[CsdpFunctions.yIndex])
				high = mid;
			else if (maxY > _bPlotPointMid[CsdpFunctions.yIndex])
				low = mid + 1;
			else if (maxY == _bPlotPointMid[CsdpFunctions.yIndex])
				low = mid + 1;
			if (low > high)
				System.out.println("ERROR in BathymetryPlot.drawPoints: low>high");
		}
		maxBathIndex = high;
		if (DEBUG)
			System.out.println("BathymetryPlot.findDisplayRegionIndices: plotBoundaries, min,max index=" + pb + ","
					+ minBathIndex + "," + maxBathIndex);
		returnValues[0] = minBathIndex;
		returnValues[1] = maxBathIndex;
		return returnValues;
	}

	// public void plotGrid(Graphics g, Rectangle bounds) {
	// int pixelSpacing =
	// CsdpFunctions.xUTMToPixels(_bathymetryData.getMinX(), _minSlope,
	// GRID_SPACING+_bathymetryData.getMinX());

	// if(DEBUG)System.out.println("pixelSpacing = "+pixelSpacing);
	// if(DEBUG)System.out.println("bounds.width="+bounds.width+"
	// bounds.height="+bounds.height);
	// if (pixelSpacing < 0) {
	// System.out.print ("Error: Negative grid spacing.");
	// System.out.println(" pixelSpacing = "+pixelSpacing);
	// } // if

	// g.setColor(Color.blue);

	// if(pixelSpacing > 50) {
	// // draw vertical lines
	// int i=1;
	// while(i*pixelSpacing <= bounds.width) {
	// g.drawLine(i*pixelSpacing, 1, i*pixelSpacing, bounds.height);
	// i++;
	// } // while
	// // draw horizontal lines
	// i=1;
	// while(i*pixelSpacing <= bounds.height) {
	// g.drawLine(1, i*pixelSpacing, bounds.width, i*pixelSpacing);
	// i++;
	// } // while
	// } // if
	// } // method plotGrid

	/**
	 * Sets color of displayed point to RGB color values based on elevation
	 */
	public Color colorByDepth(double z) {
		Color c = null;

		if (z >= _gui.getElevationBin(0))
			c = _gui.getColor(0);
		else {
			for (int i = 0; i <= _gui.getNumColors() - 2; i++) {
				if (z < _gui.getElevationBin(i) && z >= _gui.getElevationBin(i + 1)) {
					c = _gui.getColor(i);
				} // if
			} // for
		} // else
		if (z <= _gui.getElevationBin(_gui.getNumColors() - 1)) {
			c = _gui.getColor(_gui.getNumColors() - 1);
		} // if
		return c;
	}// colorByDepth

	/**
	 * Sets color of displayed point to RBG color values based on elevation
	 */
	protected Color colorByIndex(int index) {
		Color c = null;
		if (index < _gui.getNumColors()) {
			c = _gui.getColor(index);
		} else
			c = _gui.getColor(_gui.getNumColors() - 1);
		return c;
	}// colorByIndex

	/**
	 * returns a value that will be used to label a color legend item by
	 * CsdpFrame
	 */
	public String getLegendItemName(int index) {
		return Double.toString(_gui.getElevationBin(index));
	}// getLegendItemName

	/**
	 * returns a color which will be used to color a legend item by CsdpFrame
	 */
	public Color getLegendItemColor(int index) {
		Color c = null;
		if (index <= _gui.getNumColors())
			c = _gui.getColor(index);
		else
			c = _gui.getColor(_gui.getNumColors() - 1);
		return c;
	}// getLegendItemColor

	/**
	 * return bathymetry point dimension
	 */
	public int getPointSize() {
		return POINT_SIZE;
	}// getPointSize

	/**
	 * set bathymetry point dimension
	 */
	public void setPointSize(int value) {
		POINT_SIZE = value;
	}// putPointSize

	protected static int POINT_SIZE = 1;
	// size of displayed data point (square)
	protected static final double GRID_SPACING = 2000.0;
	protected int _xPixels = 0;
	protected int _yPixels = 0;
	boolean[] _binSelected;
	private static double[] _bPlotPoint = new double[3];
	private static double[] _bPlotPointMid = new double[3];
} // Class BathymetryPlot
