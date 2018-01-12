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
 * Plot Network Data on screen
 *
 * @author
 * @version $Id: NetworkPlot.java,v 1.3 2003/04/15 19:46:14 btom Exp $
 */
public class NetworkPlot extends PlanViewPlot{
    
    public NetworkPlot (CsdpFrame gui, BathymetryData data, App app) {
	super(gui, data, app);
    }

    /**
     * update bathymetry data
     */
    public void setBathymetryData(BathymetryData data){
	if(DEBUG)System.out.println("setting bathymetry data object in networkplot. minx,miny="
				    +data.getMinXFeet()+","+data.getMinYFeet());
	_bathymetryData = data;
    }
    
    /**
     * sets network object
     */
    public void setNetwork(Network net){
	_net = net;
    }
    
    /**
     * Plot network data, top view.
     */
//     public void drawPoints(Graphics2D g, Rectangle bounds, float[] dataBoundaries, 
// 			   boolean useZoomBox){
    public void drawPoints(Graphics2D g, Rectangle bounds, ZoomState zs){
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

	//	System.out.println("plotNetwork: minX, minY, minSlope, height="+minX+","+minY+","+zs.getMinSlope()+","+height);
	double[] xy;
	int x1Index = 0;
	int y1Index = 1;
	int x2Index = 2;
	int y2Index = 3;
	String centerlineName;
	
	//	if(DEBUG)System.out.println("NetworkPlot: centerX, centerY="+_centerX+","+_centerY);

	for(int c=0; c<=(_net.getNumCenterlines()-1); c++) {
	    if(DEBUG)System.out.println
			 ("networkplot: numcenterlines="+_net.getNumCenterlines());
	    centerlineName = _net.getCenterlineName(c);
	    centerline = _net.getCenterline(centerlineName);
	    // plot centerline
	    if(DEBUG)System.out.println("centerlineName, centerline object="+centerlineName+","+centerline);
	    for(int p=0; p<=centerline.getNumCenterlinePoints()-2; p++) {
		centerlinePoint = centerline.getCenterlinePoint(p);
		nextCenterlinePoint = centerline.getCenterlinePoint(p+1);

		int[] pt1;
		int[] pt2;
		//		if(zs.getUseZoomBox()){
		    //must define x?pixels and y?pixels here, not after if statement
		    // because consecutive called to utmtopixels replace values in 
		    // previous returned arrays.
		    pt1 = cc.utmToPixels(centerlinePoint.getXFeet(),
							   centerlinePoint.getYFeet(),
							   minX,minY);
		    _x1Pixels = pt1[0];
		    _y1Pixels = pt1[1];
		    pt2 = cc.utmToPixels(nextCenterlinePoint.getXFeet(),
							   nextCenterlinePoint.getYFeet(),
							   minX,minY);
		    _x2Pixels = pt2[0];
		    _y2Pixels = pt2[1];
// 		    if(useZoomPan){
// 			_x1Pixels+=_xZoomPanOffset;
// 			_y1Pixels+=_yZoomPanOffset;
// 			_x2Pixels+=_xZoomPanOffset;
// 			_y2Pixels+=_yZoomPanOffset;
// 		    }
// 		}else{
// 		    pt1 = cc.utmToPixels(centerlinePoint.getXFeet()+_centerX,
// 					 centerlinePoint.getYFeet()+_centerY,
// 					 minX,minY);
// 		    _x1Pixels = pt1[0];
// 		    _y1Pixels = pt1[1];
// 		    pt2 = cc.utmToPixels(nextCenterlinePoint.getXFeet()+_centerX,
// 					 nextCenterlinePoint.getYFeet()+_centerY,
// 					 minX,minY);
// 		    _x2Pixels = pt2[0];
// 		    _y2Pixels = pt2[1];
// 		}
		
// 		if(DEBUG)System.out.println
// 			     ("centerline: x1,y1,x2,y2(meters)="+
// 			      centerlinePoint.getXFeet()+_centerX+","+
// 			      centerlinePoint.getYFeet()+_centerY+" "+
// 			      nextCenterlinePoint.getXFeet()+_centerX+","+
// 			      nextCenterlinePoint.getYFeet()+_centerY);
		if(DEBUG)System.out.println
			     ("centerline: x1,y1,x2,y2(pixels)="+
			      _x1Pixels+","+_y1Pixels+" "+_x2Pixels+","+_y2Pixels);
		
		g.drawLine((int)_x1Pixels,(int)_y1Pixels,(int)_x2Pixels,(int)_y2Pixels);
	    } // for p
	    // draw squares on points if centerline is selected
	    if(DEBUG)System.out.println("net="+_net);
	    if(DEBUG)System.out.println("centerlineName, selectedcenterlinename="+centerlineName+","+_net.getSelectedCenterlineName());
	    if(centerlineName.equals(_net.getSelectedCenterlineName())){
		if(DEBUG)System.out.println("match found");
		for(int p=0; p<=centerline.getNumCenterlinePoints()-1; p++){
		    centerlinePoint = centerline.getCenterlinePoint(p);
		    
		    int[] pt;
		    //		    if(zs.getUseZoomBox()){
			pt = cc.utmToPixels(centerlinePoint.getXFeet(),
					    centerlinePoint.getYFeet(),
					    minX,minY);
// 			if(useZoomPan){
// 			    pt[0]+=_xZoomPanOffset;
// 			    pt[1]+=_yZoomPanOffset;
// 			}
// 		    }else{
// 			pt = cc.utmToPixels(centerlinePoint.getXFeet()+_centerX,
// 					    centerlinePoint.getYFeet()+_centerY,
// 					    minX,minY);
// 		    }
		    _x1Pixels = pt[0];
		    _y1Pixels = pt[1];

		    g.drawRect((int)_x1Pixels-POINT_DIMENSION/2, 
			       (int)_y1Pixels-POINT_DIMENSION/2,
			       POINT_DIMENSION, POINT_DIMENSION);
		    //if upstream point, draw a "U"; if downstream point, draw a "D"
		    if(p==0) g.drawString("U",(int)_x1Pixels, (int)_y1Pixels);
		    else if(p==centerline.getNumCenterlinePoints()-1){
			g.drawString("D",(int)_x1Pixels,(int)_y1Pixels);
		    }
		}//for p
	    }//if centerline is selected
	    // plot xsect line
	    for(int xs=0; xs<=centerline.getNumXsects()-1; xs++){
		xsect=centerline.getXsect(xs);
		//xy=findXsectLineCoord(centerline, xsect, xs);
		if(DEBUG)System.out.println("number of xsect = "+centerline.getNumXsects());
		if(DEBUG)System.out.println("centerlineName, xs#="+centerlineName+","+xs);
		xy = _net.findXsectLineCoord(centerlineName, xs);
		int[] pt1 = null;
		int[] pt2 = null;
		    //must define x?pixels and y?pixels here, not after if statement
		    // because consecutive called to utmtopixels replace values in 
		    // previous returned arrays.
		//		if(zs.getUseZoomBox()){
		    pt1 = cc.utmToPixels(xy[x1Index],xy[y1Index],minX,minY);
		    _x1Pixels = pt1[0];
		    _y1Pixels = pt1[1];
		    pt2 = cc.utmToPixels(xy[x2Index],xy[y2Index],minX,minY);
		    _x2Pixels = pt2[0];
		    _y2Pixels = pt2[1];
// 		    if(useZoomPan){
// 			_x1Pixels+=_xZoomPanOffset;
// 			_y1Pixels+=_yZoomPanOffset;
// 			_x2Pixels+=_xZoomPanOffset;
// 			_y2Pixels+=_yZoomPanOffset;
// 		    }
// 		}else{
// 		    pt1 = cc.utmToPixels(xy[x1Index]+_centerX,
// 						    xy[y1Index]+_centerY,minX,minY);
// 		    _x1Pixels = pt1[0];
// 		    _y1Pixels = pt1[1];
// 		    pt2 = cc.utmToPixels(xy[x2Index]+_centerX,
// 						    xy[y2Index]+_centerY,minX,minY);
// 		    _x2Pixels = pt2[0];
// 		    _y2Pixels = pt2[1];
// 		}
		
// 		if(DEBUG)System.out.println("Cross-section line coord: x1,y1,x2,y2="+
// 					    xy[x1Index]+_centerX+","+xy[y1Index]+_centerX+" "+
// 					    xy[x2Index]+_centerY+","+xy[y2Index]+_centerY);
		if(DEBUG)System.out.println("Cross-section line coord: x1,y1,x2,y2="+
					    _x1Pixels+","+_y1Pixels+" "+
					    _x2Pixels+","+_y2Pixels);
		
		g.drawLine((int)_x1Pixels,(int)_y1Pixels,(int)_x2Pixels,(int)_y2Pixels);
		if(xsect == _net.getSelectedXsect()){
		    g.drawRect((int)_x1Pixels-POINT_DIMENSION/2, 
			       (int)_y1Pixels-POINT_DIMENSION/2,
			       POINT_DIMENSION, POINT_DIMENSION);
		    g.drawRect((int)_x2Pixels-POINT_DIMENSION/2, 
			       (int)_y2Pixels-POINT_DIMENSION/2,
			       POINT_DIMENSION, POINT_DIMENSION);
		}//if xsect
	    } // for xs
	} // for c
    }//plot
    
  protected static final int POINT_SIZE = 1;// size of displayed data point (square)
  protected double _x1Pixels; // coordinates of centerline points converted to pixels
  protected double _y1Pixels;
  protected double _x2Pixels;
  protected double _y2Pixels;
  Network _net = null;
  protected int POINT_DIMENSION = 2;
  protected static final boolean DEBUG = false;
} // class NetworkPlot
