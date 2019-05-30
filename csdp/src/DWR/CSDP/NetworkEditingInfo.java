package DWR.CSDP;

import javax.swing.JOptionPane;

/**
 * Contains all information needed to undo/redo an network editing action.
 * @author btom
 *
 */
public class NetworkEditingInfo {
	private static final int MOVE_CENTERLINE_POINT = 10;
	private static final int ADD_CENTERLINE_POINT = 20;
	private static final int INSERT_CENTERLINE_POINT = 30;
	private static final int DELETE_CENTERLINE_POINT = 40;
	private static final int ADD_CROSS_SECTION_LINE = 50;
	private static final int MOVE_CROSS_SECTION_LINE = 60;
	private static final int DELETE_CROSS_SECTION_LINE = 70;
	private static final int DELETE_POINTS_INSIDE_BOX = 80;
	private static final int DELETE_POINTS_OUTSIDE_BOX = 90;
	private static final int ADD_XSECTS_AT_COMPUTATIONAL_POINTS = 100;
	
	private static final int CENTERLINE_POINT_WAS_UPSTREAM = 110;
	private static final int CENTERLINE_POINT_WAS_DOWNSTREAM = 120;
	private static final int CENTERLINE_POINT_WAS_MIDPOINT = 130;
	
	private double xi;
	private double yi;
	private double xf;
	private double yf;
	private int editType;
	private Xsect oldXsect;
	private Xsect[] xsectsAddedOrDeleted;
	private CenterlinePoint centerlinePointAddedOrDeleted;
	private NetworkInteractor networkInteractor;
	private CsdpFrame gui;
	private Centerline modifiedCenterline;
	/*
	 * If a point was deleted, was it an upstream end point, and downstream end point, or a midpoint?
	 * This information is needed to restore a point that was deleted.
	 */
	private int centerlinePointHistory;
	private int[] xsectIndices;
	
	/*
	 * Constructor for mousePressed event
	 */
	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor,  
			int editType, double xi, double yi, double xf, double yf) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.xi = xi;
		this.yi = yi;
		this.xf = xf;
		this.yf = yf;
	}
	
	/*
	 * Constructor for mouseDragged event
	 */
	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor, 
			int editType, double xf, double yf) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.xi = -Double.MAX_VALUE;
		this.yi = -Double.MAX_VALUE;
		this.xf = xf;
		this.yf = yf;
	}

	/*
	 * Constructor for xsect move, delete command
	 */
	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor, 
			int editType, double xf, double yf, Xsect oldXsect) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.xi = -Double.MAX_VALUE;
		this.yi = -Double.MAX_VALUE;
		this.xf = xf;
		this.yf = yf;
		this.oldXsect = oldXsect;
	}

	/*
	 * Constructor for adding or deleting(?) multiple xs
	 */
	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor, int editType, 
			Xsect[] xsectsAddedOrDeleted, int[] xsectIndices) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.xsectsAddedOrDeleted = xsectsAddedOrDeleted;
		this.xsectIndices = xsectIndices;
	}

	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor, 
			int editType, CenterlinePoint centerlinePointAddedOrDeleted, int centerlinePointHistory) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.centerlinePointAddedOrDeleted = new CenterlinePoint();
		this.centerlinePointAddedOrDeleted.putXFeet(centerlinePointAddedOrDeleted.getXFeet());
		this.centerlinePointAddedOrDeleted.putYFeet(centerlinePointAddedOrDeleted.getYFeet());
		this.centerlinePointHistory = centerlinePointHistory;
	}
	
	public NetworkEditingInfo(CsdpFrame gui, NetworkInteractor networkInteractor, 
			int editType, Centerline modifiedCenterline) {
		this.gui = gui;
		this.networkInteractor = networkInteractor; 
		this.editType = editType;
		this.modifiedCenterline = modifiedCenterline;
	}

	public double getXi() {return this.xi;}
	public double getYi() {return this.yi;}
	public double getXf() {return this.xf;}
	public double getYf() {return this.yf;}
	
	/*
	 * This will be called when user clicks Ctrl-Z
	 */
	public void undoAction() {
//		ZoomState zs = this.bathymetryPlot.getCurrentZoomState();
//		CoordConv coordConv = zs.getCoordConv();
//		double[] bb = zs.getPlotBoundaries();
//		double minX = bb[CsdpFunctions.minXIndex];
//		double minY = bb[CsdpFunctions.minYIndex];
		boolean update = true;
		if(this.editType==MOVE_CENTERLINE_POINT) {
			//to undo a move, find closest point to xf, yf, and change its coordinates to xi, yi
			this.centerlinePointAddedOrDeleted.putXFeet(this.xi);
			this.centerlinePointAddedOrDeleted.putYFeet(this.yi);
		}else if(this.editType==ADD_CENTERLINE_POINT) {
			//to undo an add, remove last centerline point
			this.modifiedCenterline.deleteCenterlinePoint(this.modifiedCenterline.getNumCenterlinePoints()-1);
		}else if(this.editType==INSERT_CENTERLINE_POINT) {
			//to undo an insert, find closest point to xf, yf, and remove it
			this.modifiedCenterline.deleteCenterlinePoint(this.xf, this.yf);
		}else if(this.editType==DELETE_CENTERLINE_POINT) {
			//to undo a delete, need to use remembered values of the point that was deleted.
			//this means using the NetworkInteractor to simulate clicking on the deleted point's location
			//was the centerline point added or inserted???
			double x = this.centerlinePointAddedOrDeleted.getXFeet();
			double y = this.centerlinePointAddedOrDeleted.getYFeet();
			if(this.centerlinePointHistory==CENTERLINE_POINT_WAS_UPSTREAM) {
				this.modifiedCenterline.addUpstreamCenterlinePointFeet(x, y);
			}else if(this.centerlinePointHistory==CENTERLINE_POINT_WAS_DOWNSTREAM) {
				this.modifiedCenterline.addDownstreamCenterlinePointFeet(x, y);
			}else if(this.centerlinePointHistory==CENTERLINE_POINT_WAS_MIDPOINT) {
//				int[] xyPixels = coordConv.utmToPixels(this.centerlinePointAddedOrDeleted.getXFeet(), 
//						this.centerlinePointAddedOrDeleted.getYFeet(), minX, minY);
//				MouseEvent mouseEvent = new MouseEvent(gui, MouseEvent.BUTTON1, System.currentTimeMillis(), 0, 
//						xyPixels[0], xyPixels[1], 1, false);
//				this.gui.setInsertCenterlinePointMode();
//				this.networkInteractor.mousePressed(mouseEvent);
				this.modifiedCenterline.insertCenterlinePointFeet(x, y);
			}
		}else if(this.editType==ADD_CROSS_SECTION_LINE) {
			//to undo an add cross-section line, need to find closest cross-section endpoints to xf, yf
			this.modifiedCenterline.removeXsect(this.xsectIndices[0]);
		}else if(this.editType==MOVE_CROSS_SECTION_LINE) {
			//to undo a move cross-section line, need to use remembered values of 
			// distance along centerline, cross-section line length, and 
			//Xsect object, to restore cross-section drawing
			
			
			
		}else if(this.editType==DELETE_CROSS_SECTION_LINE) {
			//to undo a delete cross-section line, same info needed as undoing a move.
		}else if(this.editType==DELETE_POINTS_INSIDE_BOX) {
			//to undo a delete points inside box, need to store all centerline points. Cannot... 
		}else if(this.editType==DELETE_POINTS_OUTSIDE_BOX) {
			//to undo a delete points outside box, need to store all centerline points. Cannot... 
		
		}else if(this.editType==ADD_XSECTS_AT_COMPUTATIONAL_POINTS) {	
			//to undo an add xsects at computational points action, just need the Xsect objects, because they store
			//the line length and distance along centerline
		}else{
			update = false;
			JOptionPane.showMessageDialog(gui, "Error in CSDPPlanViewEditingInfo.undoCommand: editType unrecognized", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		if(update) {
			this.gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			this.gui.getPlanViewCanvas(0).redoNextPaint();
			this.gui.getPlanViewCanvas(0).repaint();
		}
	}//undoAction
	
	/*
	 * This will be called when user clicks Ctrl-Y
	 */
	public void redoAction() {
		if(this.editType==MOVE_CENTERLINE_POINT) {
			//to redo a move, find closest point to xi, yi, and change its coordinates to xf, yf
		}else if(this.editType==ADD_CENTERLINE_POINT) {
			//to redo an add centerline point, add a point at xf, yf
		}else if(this.editType==INSERT_CENTERLINE_POINT) {
			//to redo an insert centerline point, call the insert method with xf, yf as arguments
		}else if(this.editType==DELETE_CENTERLINE_POINT) {
			//to redo a delete centerline point, delete the closest point to xf, yf
		}else if(this.editType==ADD_CROSS_SECTION_LINE) {
			//to redo an add cross-section line, use the information in the deleted Xsect object to restore it
		}else if(this.editType==MOVE_CROSS_SECTION_LINE) {
			//to redo a move cross-section line, use the information in the delete Xsedt object to restore it
		}else if(this.editType==DELETE_CROSS_SECTION_LINE) {
			//to redo a delete cross-section line, use the xf and yf coordinates
		}else if(this.editType==DELETE_POINTS_INSIDE_BOX) {
			//to redo a delete points inside box, use the xi, yi and xf, yf coordinates
		}else if(this.editType==DELETE_POINTS_OUTSIDE_BOX) {
			//to redo a delete points outside box, use the xi, yi, and xf, yf coordinates
		}else if(this.editType==ADD_XSECTS_AT_COMPUTATIONAL_POINTS) {	
			//to redo an add xsects at computational points, simply execute the command
		}else{
			JOptionPane.showMessageDialog(gui, "Error in CSDPPlanViewEditingInfo.redoCommand: editType unrecognized", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}//redoAction
}//inner class CSDPPlanViewEditingCommand

