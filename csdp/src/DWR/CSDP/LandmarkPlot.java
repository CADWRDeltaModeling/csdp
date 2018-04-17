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
			g.drawString(name, xPixels, yPixels);
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
