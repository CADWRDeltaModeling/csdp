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
import java.awt.*;
import java.awt.event.*;
/**
 * Plot DigitalLineGraph Data on screen
 *
 * @author
 * @version $Id: DigitalLineGraphPlot.java,v 1.3 2003/04/15 19:22:04 btom Exp $
 */
public class DigitalLineGraphPlot extends PlanViewPlot{

    /**
     * sets value of _bathymetryData in superclass
     */
    public DigitalLineGraphPlot (CsdpFrame gui, BathymetryData data, App app) {
	super(gui, data, app);
    }

  /**
   * sets dlg object
   */
  public void setDigitalLineGraph(DigitalLineGraph dlg){
    _dlg = dlg;
  }

    /**
     * Plot dlg data, top view.
     */
//     public void drawPoints(Graphics2D g, Rectangle bounds, float[] dataBoundaries, 
// 			   boolean useZoomBox){
    public void drawPoints(Graphics2D g, Rectangle bounds, ZoomState zs){
	g.setColor(Color.black);
	//    String name = null;
	double height = bounds.height;
	double[] dataBoundaries = zs.getPlotBoundaries();
	double minX = dataBoundaries[CsdpFunctions.minXIndex];
	double minY = dataBoundaries[CsdpFunctions.minYIndex];
	double x = -Double.MAX_VALUE;
	double y = -Double.MAX_VALUE;
	double x2 = -Double.MAX_VALUE;
	double y2 = -Double.MAX_VALUE;
	int xPixels = -Integer.MAX_VALUE;
	int yPixels = -Integer.MAX_VALUE;
	int xPixels2 = -Integer.MAX_VALUE;
	int yPixels2 = -Integer.MAX_VALUE;
	DigitalLineGraphLine line = null;
	DigitalLineGraphPoint point = null;
	DigitalLineGraphPoint point2 = null;
	CoordConv cc = zs.getCoordConv();
	
	//	System.out.println("in DigitalLineGraphPlot: centerx, centery="+_centerX+","+_centerY);
	
	//    for(int i=0; i<=_landmark.getNumLandmarks()-1; i++){
	//line numbers in the dlg file start at 1
	for(int i=0; i<=_dlg.getNumLines()-1; i++){
	    if(DEBUG)System.out.println("DigitalLineGraphPlot.drawPoints: numlines="+_dlg.getNumLines());
	    //      name = _dlg.getDigitalLineGraphName(i);
	    line = _dlg.getLine(i);
	    if(DEBUG)System.out.println("line number "+i+"="+line);
	    for(int j=0; j<=line.getNumPoints()-2; j++){
		point = line.getPoint(j);
		point2= line.getPoint(j+1);
		x = (point.getX());
		y = (point.getY());
		x2= (point2.getX());
		y2= (point2.getY());
		if(DEBUG)System.out.println("x, y, minx, miny="+x+","+y+" "+minX+","+minY);
		int[] pt1 = null;
		int[] pt2 = null;
		//		if(zs.getUseZoomBox()){
		    pt1 = cc.utmToPixels(x,y,minX,minY);
		    xPixels = pt1[0];
		    yPixels = pt1[1];
		    pt2 = cc.utmToPixels(x2,y2,minX,minY);
		    xPixels2 = pt2[0];
		    yPixels2 = pt2[1];
// 		    if(useZoomPan){
// 			xPixels+=_xZoomPanOffset;
// 			yPixels+=_yZoomPanOffset;
// 			xPixels2+=_xZoomPanOffset;
// 			yPixels2+=_yZoomPanOffset;
// 		    }
// 		}else{
// 		    pt1 = cc.utmToPixels(x+_centerX,y+_centerY,minX,minY);
// 		    xPixels = pt1[0];
// 		    yPixels = pt1[1];
// 		    pt2 = cc.utmToPixels(x2+_centerX,y2+_centerY,minX,minY);
// 		    xPixels2 = pt2[0];
// 		    yPixels2 = pt2[1];
// 		}
		
		g.drawLine((int)xPixels,(int)yPixels,(int)xPixels2,(int)yPixels2);
	    }
	    
	    if(DEBUG)System.out.println("x, y="+xPixels+","+yPixels);
	    
	}
    }//plot digital line graph
    
  protected static final int POINT_SIZE = 1;// size of displayed data point (square)
  protected double _x1Pixels; // coordinates of centerline points converted to pixels
  protected double _y1Pixels;
  protected double _x2Pixels;
  protected double _y2Pixels;
  DigitalLineGraph _dlg = null;
  protected int POINT_DIMENSION = 2;
} // class DigitalLineGraphPlot
