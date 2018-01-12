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
//import DWR.Graph.*;
import vista.graph.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import DWR.CSDP.dialog.*;
import javax.swing.*;
import java.io.*;

/**
 * This class handles interaction of mouse and mouse motion.
 * This will be used to allow user to create points, move points, 
 * and assign and edit names
 *
 * @author
 * @version
 */

public class LandmarkInteractor extends ElementInteractor{
    
    /*
     * Constructor
     */
    public LandmarkInteractor(CsdpFrame gui, PlanViewCanvas can, App app){
	_gui = gui;
	_can = can;
	_app = app;
	_td = new TextDialog((Frame)gui, ADD_LANDMARK_TITLE, true);
	_tad = new TryAgainDialog(gui, "A landmark already exists with that name! Try again?", true);
    }//constructor

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e){
	if(DEBUG)System.out.println("Mouse Clicked at ( "
				    + e.getX() + ", " + e.getY() + " )");
	setInitialPoint(e.getX(), e.getY());
	if(DEBUG)System.out.println
		     ("mouse clicked:  setting initial point "+e.getX()+","+e.getY());
    }//mouseClicked

    /*
     * will return true if landmark selected, false if not.
     */
    private boolean selectLandmark(MouseEvent e){
	boolean landmarkSelected=false;
	_lPlotter = _can._landmarkPlotter;

	if(_bathymetryPlot!=null){
	    ZoomState zs = _bathymetryPlot.getCurrentZoomState();
	    CoordConv cc = zs.getCoordConv();
	    
	    if(_lPlotter != null){
		_minSlope = zs.getMinSlope();
		_height   = _lPlotter._height;
	    }//if _lPlotter is null
	    if(_bathymetryPlot != null && _bathymetryPlot._bathymetryData != null){
		double[] bb = zs.getPlotBoundaries();
		_minX = bb[CsdpFunctions.minXIndex];
		_minY = bb[CsdpFunctions.minYIndex];
	    }

	    //calculates distance btwn points and assigns it to _lengthI
	    cc.pixelsToLength(e.getX(),e.getY(),_minX,_minY, _lengthI);
	    //select landmark
	    if(_lPlotter != null && _landmark != null){
		double minDist = Double.MAX_VALUE;
		for(Enumeration<String>lNames = _landmark.getLandmarkNames(); 
		    lNames.hasMoreElements();){
		    String landmarkName = lNames.nextElement();
		    if(DEBUG)System.out.println("trying to select landmark "+landmarkName);
		    //find the closest landmark
		    double xFeet = _landmark.getXFeet(landmarkName);
		    double yFeet = _landmark.getYFeet(landmarkName);
		    cc.pixelsToLength(e.getX(),e.getY(),_minX,_minY, _lengthF);
		    
		    if(DEBUG)System.out.println
				 ("about to compare dist. xFeet, yFeet, _lengthF 0and1="+
				  xFeet+","+yFeet+","+_lengthF[0]+","+_lengthF[1]);
		    
		    double dist=CsdpFunctions.pointDist(xFeet, yFeet, _lengthF[0],_lengthF[1]);
		    
		    if(DEBUG)System.out.println("LandmarkInteractor.mousePressed: trying to select. dist, minDist="+dist+","+MAX_SEARCH_DIST);
		    
		    if(dist<minDist){
			minDist=dist;
			if(minDist <= MAX_SEARCH_DIST){
			    _landmark.setSelectedLandmarkName(landmarkName);
			    landmarkSelected=true;
			    if(DEBUG)System.out.println("selected landmark. name="+_landmark.getSelectedLandmarkName());
			}
		    }
		}//for i
		if(minDist > MAX_SEARCH_DIST){
		    _landmark.setSelectedLandmarkName(null);
		    landmarkSelected=false;
		    if(DEBUG)System.out.println("LandmarkInteractor.selectLandmark: setting selected landmark to null");
		}
		
		_can.redoNextPaint();
		_can.repaint();
	    }//if _lPlotter != null
	}//if _bathymetryPlot !=null
	return landmarkSelected;
    }//selectLandmark

    /**
     * Invoked when a mouse button has been pressed.
     * this selects the nearest landmark.
     */
    public void mousePressed(MouseEvent e){
	setInitialPoint(e.getX(), e.getY());
	if(DEBUG)System.out.println
		     ("LandmarkInteractor: mouse pressed:  setting initial point "+e.getX()+","+e.getY());
 	boolean landmarkSelected = false;
	if(!_gui.getMoveLandmarkMode()) landmarkSelected = selectLandmark(e);
	int button = e.getModifiers();
	//If the popup menu is visible when the user clicks on window, do nothing
	//otherwise, check to see which edit mode we're in.
	if(_gui.landmarkMenuIsVisible()){
	    _gui.turnOffEditModes();
	}else{
	    switch(button) {
	    case MouseEvent.BUTTON1_MASK: //left button
		break;
	    case MouseEvent.BUTTON2_MASK: //middle button (wheel?)
		break;
	    case MouseEvent.BUTTON3_MASK: //right click shows menu
		if(_bathymetryPlot != null){
		    if(_landmark==null || !landmarkSelected){
			//show only "Add Landmark" option
			_gui.showEditLandmarkMenu(false, e);
		    }else{
			//show only landmark editing options
			_gui.showEditLandmarkMenu(true, e);
		    }
		}
	    }
	}//if popupmenu visible
    }//mousePressed

    private void updateLandmarkDisplay(){
	_lPlotter = _can._landmarkPlotter;
	_gui.getPlanViewCanvas(0).setUpdateLandmark(true);
	_landmark.setIsUpdated(true);
	//removed for conversion to swing
	_gui.getPlanViewCanvas(0).redoNextPaint();
	_gui.getPlanViewCanvas(0).repaint();
	_gui.setStopEditingMode();
	_gui.setCursor(CsdpFunctions._defaultCursor);
    }
  
    /**
     * Sets initial point of zoom rectangle region
     */
    public void setInitialPoint(int x, int y){
	_xi = x;
	_yi = y;
    }//setInitialPoint
    
    /**
     * Sets final point of zoom rectangle region. This would be
     * the point diagonally opposite the initial point
     */
    public void setFinalPoint(int x, int y){
	_xf = x;
	_yf = y;
	constructRectangle();
    }//setFinalPoint
    
    /**
     * Invoked when component has been moved.
     */    
    public void mouseMoved(MouseEvent e){
	if(DEBUG)System.out.println("Component Event: " + e.toString());
	_gui.updateInfoPanel(e.getX(),e.getY());
    }
    
    /*
     * Edit landmark text
     */
    public boolean editLandmark(){
	boolean tryAgain = false;
	_td.setTitle(EDIT_LANDMARK_TITLE);
	_td.tf.setText(_landmark.getSelectedLandmarkName());
	_td.setVisible(true);
	String newLandmarkName = _td.tf.getText();
	if(!newLandmarkName.equalsIgnoreCase(_landmark.getSelectedLandmarkName())){
	    boolean success = _landmark.renameLandmark
		(_landmark.getSelectedLandmarkName(), newLandmarkName);
	    if(success){
		updateLandmarkDisplay();
		_gui.turnOffEditModes();
	    }else{
		_tad.setVisible(true);
		if(_tad._tryAgain==true){
		    tryAgain=true;
		}else{
		    tryAgain=false;
		    _gui.turnOffEditModes();
		}//if
	    }//if
	}//if
	return tryAgain;
    }//renameLandmark

    public void deleteLandmark(){
	_landmark.deleteSelectedLandmarkPoint();
	updateLandmarkDisplay();
	_gui.turnOffEditModes();
    }//deleteLandmark

    /**
     * add a landmark
     */
    public boolean addLandmark(){
	boolean tryAgain = false;
	double xDataCoord;
	double yDataCoord;
	_lPlotter = _can._landmarkPlotter;
	ZoomState zs = _bathymetryPlot.getCurrentZoomState();
	CoordConv cc = zs.getCoordConv();
	
	_td.setTitle(ADD_LANDMARK_TITLE);
	_td.setVisible(true);
	String landmarkName = _td.tf.getText();
	
	cc.pixelsToLength(_xi, _yi, _minX, _minY, _lengthI);
	if(DEBUG)System.out.println("adding landmark: name, x,y="+landmarkName+","+_lengthI[0]+","+_lengthI[1]);
	boolean success = _landmark.addLandmarkFeet(landmarkName, _lengthI[0], _lengthI[1]);
	if(success){
	    _landmark.setSelectedLandmarkName(landmarkName);
	    updateLandmarkDisplay();
	    _gui.turnOffEditModes();
	}else{
	    _tad.setVisible(true);
	    if(_tad._tryAgain==true){
		tryAgain=true;
	    }else{
		tryAgain=false;
		_gui.turnOffEditModes();
	    }//if
	}//if
	return tryAgain;
    }//addLandmark

    /**
     * move landmark-just click in new location
     */
    protected void moveLandmark(){
	if(DEBUG)System.out.println("LandmarkInteractor.moveLandmark called");
	double xDataCoord = -Double.MAX_VALUE;
	double yDataCoord = -Double.MAX_VALUE;
	String selectedLandmarkName = _landmark.getSelectedLandmarkName();
	if(_landmark.getSelectedLandmarkName() != null){
	    if(DEBUG)System.out.println("move landmark");
	    _lPlotter = _can._landmarkPlotter;
	    ZoomState zs = _bathymetryPlot.getCurrentZoomState();
	    CoordConv cc = zs.getCoordConv();
	    cc.pixelsToLength(_xi,_yi,_minX,_minY, _lengthI);
	    
	    xDataCoord = _lengthI[0];
	    yDataCoord = _lengthI[1];
	    _landmark.putXFeet(selectedLandmarkName, xDataCoord);
	    _landmark.putYFeet(selectedLandmarkName, yDataCoord);
	    updateLandmarkDisplay();
	    _gui.turnOffEditModes();
	}else{
	    System.out.println("selected landmark name is null. not moving landmark.");
	    _gui.turnOffEditModes();
	}//if centerlineName not null
	_landmark.setIsUpdated(true);
    }//movePoint

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e){
	setFinalPoint(e.getX(), e.getY());
	if(DEBUG)System.out.println("LandmarkInteractor.mouseReleased. about to call moveLandmark? landmark mode="+_gui.getMoveLandmarkMode());
	if(_gui.getMoveLandmarkMode()){
	    moveLandmark();
	    _gui.turnOffEditModes();
	}
	_mouseDragged = false;
    }//mouseReleased

    /**
     * Constructs a rectangle from a set of diagonally opposite points.  Stores
     * rectangle in a stack.
     */
    protected void constructRectangle(){
	_zoomRect.x = Math.min(_xi, _xf);
	_zoomRect.y = Math.min(_yi, _yf);
	_zoomRect.width = Math.abs(_xi - _xf);
	_zoomRect.height = Math.abs(_yi - _yf);
    }//contructRectangle
  
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e){
	if (DEBUG) System.out.println("Mouse Entered at ( " 
				      + e.getX() + ", " + e.getY() + " )");
    }//mouseEntered
  
    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e){
	if (DEBUG) System.out.println("Mouse Exited at ( " 
				      + e.getX() + ", " + e.getY() + " )");
    }//mouseExited

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e){
	if(_drawDragRect) _mouseDragged = true;
    }//mouseDragged
    
    /**
     * Invoked when component has been moved.
     */    
    public void componentMoved(ComponentEvent e){
	if (DEBUG) System.out.println("Component Event: " + e.toString());
    }//componentMoved
    
    /**
     * sets Landmark object
     */
    public void setLandmark(Landmark landmark){
	_landmark = landmark;
    }//setLandmark
    
    /**
     * sets BathymetryPlot object
     */
    public void setPlotter(BathymetryPlot plot){
	_bathymetryPlot = plot;
    }//setPlotter

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
    Landmark _landmark;
    CsdpFrame _gui;
    BathymetryPlot _bathymetryPlot;
    LandmarkPlot _lPlotter;
    /*
     * number of pixels to search for landmark
     */
    protected int SELECT_RANGE=30;
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
    
    double MAX_SEARCH_DIST = 30000.0;
    
    protected boolean _drawDragRect = true;
    protected boolean _previouslyDoubleBuffered = true;
    protected Image _gCImage;
    protected Rectangle _zoomRect = new Rectangle(0,0,0,0);
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
    private TextDialog _td;
    private TryAgainDialog _tad;

    private final String ADD_LANDMARK_TITLE = "Enter new landmark name";
    private final String EDIT_LANDMARK_TITLE = "Edit landmark name";
}//LandmarkInteractor
