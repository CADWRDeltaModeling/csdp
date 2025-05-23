package DWR.CSDP;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import vista.graph.DoubleRect;

/**
 * A centerline contains centerline points and cross-sections. Centerline points
 * are stored in order.
 *
 * @author
 * @version
 */
public class Centerline {
	
	private int _numCenterlinePoints;
	private Vector _centerlinePoints = new Vector();
	private Vector _xsects = new Vector();
	private String _centerlineName = null;
	private double _maxX = -Double.MAX_VALUE;
	private double _minX = Double.MAX_VALUE;
	private double _maxY = -Double.MAX_VALUE;
	private double _minY = Double.MAX_VALUE;
	private boolean _maxMinCalled = false;
	private static final boolean DEBUG = false;

	/**
	 * number of irregular xsects
	 */
	private int _numXsects = 0;
	private int _numRectXsects = 0;
	private Vector _rectXsects = new Vector();
	private Vector _copiedXsects = new Vector();
	private int _numCopiedXsects = 0;

	Rectangle _r;
	private int _numCombinedXsects = 0;
	private Vector _allXsects = new Vector();

	/*
	 * To specify deleting points inside a window drawn by user
	 */
	public static final int DELETE_INSIDE_WINDOW = 10;
	/*
	 * To specify deleting points outside a window drawn by user
	 */
	public static final int DELETE_OUTSIDE_WINDOW = 20;

	/*
	 * Indicates that when creating a centerline from multiple centerlines, 
	 * points should be added starting from the upstream end.
	 */
	public static final int UPSTREAM_TO_DOWNSTREAM = 10; 
	/*
	 * Indicates that when creating a centerline from multiple centerlines, 
	 * points should be added starting from the downstreasm end.
	 */
	public static final int DOWNSTREAM_TO_UPSTREAM = 20;
	
	public Centerline(String name) {
		_centerlineName = name;
	}

	/**
	 * returns number of points in the centerline
	 */
	public int getNumCenterlinePoints() {
		return _numCenterlinePoints;
	}

	/*
	 * For zooming in to a centerline
	 */
	public double[] getMinMaxCenterlinePointCoordinatesFeet() {
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for(int i=0; i<getNumCenterlinePoints(); i++) {
			CenterlinePoint cp = getCenterlinePoint(i);
			double x = cp.getXFeet();
			double y = cp.getYFeet();
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}
		return new double[] {minX, maxX, minY, maxY};
	}

	/**
	 * Returns the normalized distance, which is the distance from the upstream
	 * end of the centerline to the cross-section divided by the total length of
	 * the centerline.
	 */
	public double getNormalizedDist(Xsect xsect) {
		return xsect.getDistAlongCenterlineFeet() / getLengthFeet();
	}

	/**
	 * calculates length of centerline
	 */
	public double getLengthFeet() {
		double length = 0.0;
		double x1;
		double y1;
		double x2;
		double y2;
		for (int i = 0; i <= getNumCenterlinePoints() - 2; i++) {
			x1 = getCenterlinePoint(i).getXFeet();
			y1 = getCenterlinePoint(i).getYFeet();
			x2 = getCenterlinePoint(i + 1).getXFeet();
			y2 = getCenterlinePoint(i + 1).getYFeet();
			length += CsdpFunctions.pointDist(x1, y1, x2, y2);
		}
		return length;
	}

	/**
	 * returns centerline point object at specified index.
	 */
	public CenterlinePoint getCenterlinePoint(int index) {
		return ((CenterlinePoint) _centerlinePoints.elementAt(index));
	}

	/**
	 * create new CenterlinePoint object, add to _centerlinePoints vector.
	 */
	public void addCenterlinePoint() {
		CenterlinePoint point = new CenterlinePoint();
		_centerlinePoints.addElement(point);
		_numCenterlinePoints++;
		_maxMinCalled = false;
	}

	/**
	 * create new CenterlinePoint object, add to _centerlinePoints vector.
	 */
	public void addDownstreamCenterlinePointFeet(double x, double y) {
		CenterlinePoint point = new CenterlinePoint();
		_centerlinePoints.addElement(point);
		point.putXFeet(x);
		point.putYFeet(y);
		_numCenterlinePoints++;
		_maxMinCalled = false;
	}//addDownstreamCenterlinePointFeet

	/*
	 * adds a point to the upstream end. For undoing a delete and for allowing addition of a point 
	 * to the upstream end interactively.
	 * Automatically adjusts cross-section distances so they will not move when the point is added.
	 */
	public void addUpstreamCenterlinePointFeet(double x, double y) {
		int numPoints = getNumCenterlinePoints();
		CenterlinePoint upstreamPointBefore = getCenterlinePoint(0);
		CenterlinePoint newUpstreamPoint = new CenterlinePoint();
		newUpstreamPoint.putXFeet(x);
		newUpstreamPoint.putYFeet(y);
		double centerlineLengthIncrease = getDistanceBetweenCenterlinePoints(upstreamPointBefore, newUpstreamPoint);
		_centerlinePoints.insertElementAt(newUpstreamPoint, 0);
		_numCenterlinePoints++;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			double distAlong = xsect.getDistAlongCenterlineFeet();
			xsect.putDistAlongCenterlineFeet(distAlong+centerlineLengthIncrease);
		}
	}//addUpstreamCenterlinePointFeet
	
	/**
	 * insert new CenterlinePoint object, add to _centerlinePoints vector.
	 */
	public void insertCenterlinePointFeet(double x, double y) {
		int numPoints = getNumCenterlinePoints();
		CenterlinePoint point;
		CenterlinePoint point1;
		CenterlinePoint point2;
		double x1;
		double x2;
		double y1;
		double y2;
		double minDist = Double.MAX_VALUE;
		double dist;
		int minDistIndex = Integer.MAX_VALUE;
		CenterlinePoint newPoint = new CenterlinePoint();
		newPoint.putXFeet(x);
		newPoint.putYFeet(y);
		for (int i = 0; i <= numPoints - 2; i++) {
			point1 = getCenterlinePoint(i);
			point2 = getCenterlinePoint(i + 1);
			x1 = point1.getXFeet();
			y1 = point1.getYFeet();
			x2 = point2.getXFeet();
			y2 = point2.getYFeet();

			dist = CsdpFunctions.shortestDistLineSegment(x1, x2, x, y1, y2, y);
			if (dist < minDist) {
				minDist = dist;
				minDistIndex = i;
			}
		} // for i
		_centerlinePoints.insertElementAt(newPoint, minDistIndex + 1);
		_numCenterlinePoints++;
	}// insertCenterlinePointFeet

	/*
	 * Sets new point coordinates, and adjusts positions of all cross-sections if needed, to try to
	 * keep cross-sections in the same geographic location.
	 */
	public void moveCenterlinePointFeet(int centerlinePointIndex, double x, double y) {
		CenterlinePoint centerlinePoint = getCenterlinePoint(centerlinePointIndex);
		double centerlineLengthBefore = getLengthFeet();
		//moving a point affects one line segment if it's the upstream or downstream end being moved.
		//otherwise, it affects two line segments.
		if(centerlinePointIndex==0) {
			//moving the upstream end--one line segment affected
			//adjust the positions of all cross-sections. If on the affected centerline segment, 
			//adjust the position relative to the normalized distance along the line segment.
			//if on another line segment, simply adjust the position based on the ratio of initial and final centerline lengths.
			CenterlinePoint firstCenterlinePoint = getCenterlinePoint(0);
			CenterlinePoint secondCenterlinePoint = getCenterlinePoint(1);
			double x1 = firstCenterlinePoint.getXFeet();
			double y1 = firstCenterlinePoint.getYFeet();
			double x2 = secondCenterlinePoint.getXFeet();
			double y2 = secondCenterlinePoint.getYFeet();
			double firstLineSegmentLength = CsdpFunctions.pointDist(x1, y1, x2, y2);
			double adjustedLineSegmentLength = CsdpFunctions.pointDist(x, y, x2, y2);
			double centerlineLengthAfter = centerlineLengthBefore + (adjustedLineSegmentLength-firstLineSegmentLength);
			for(int i=0; i<getNumXsects(); i++) {
				Xsect xsect = getXsect(i);
				double xsectDist = xsect.getDistAlongCenterlineFeet();
				double newDistAlong = -Double.MAX_VALUE;
				if(xsectDist <=firstLineSegmentLength) {
					newDistAlong = (adjustedLineSegmentLength-firstLineSegmentLength) + xsectDist;
				}else {
					newDistAlong = (centerlineLengthAfter-centerlineLengthBefore) + xsectDist;
				}
				xsect.putDistAlongCenterlineFeet(newDistAlong);
			}
		}else if(centerlinePointIndex==getNumCenterlinePoints()-1) {
			//moving the downstream end--one line segment affected.
			//xsect distances need no adjustment--do nothing
		}else {
			//moving one of the midpoints--two line segments affected
			CenterlinePoint pointToBeMoved = getCenterlinePoint(centerlinePointIndex);
			CenterlinePoint upstreamLineSegmentPoint = getCenterlinePoint(centerlinePointIndex-1);
			CenterlinePoint downstreamLineSegmentPoint = getCenterlinePoint(centerlinePointIndex+1);
			
			double x1 = upstreamLineSegmentPoint.getXFeet();
			double y1 = upstreamLineSegmentPoint.getYFeet();
			double x2 = pointToBeMoved.getXFeet();
			double y2 = pointToBeMoved.getYFeet();
			double x3 = downstreamLineSegmentPoint.getXFeet();
			double y3 = downstreamLineSegmentPoint.getYFeet();
			
			double firstLineSegmentLength = CsdpFunctions.pointDist(x1, y1, x2, y2);
			double secondLineSegmentLength = CsdpFunctions.pointDist(x2, y2, x3, y3);
			double adjustedFirstLineSegmentLength = CsdpFunctions.pointDist(x, y, x1, y1);
			double adjustedSecondLineSegmentLength = CsdpFunctions.pointDist(x, y, x3, y3);

			double distToDownstreamLineSegmentPointAfter = getDistToPoint(centerlinePointIndex-1) +
					CsdpFunctions.pointDist(x1, y1, x, y)+ CsdpFunctions.pointDist(x, y, x3, y3);
			double distToPointToBeMoved = getDistToPoint(centerlinePointIndex);
			double distToDownstreamLineSegmentPoint = getDistToPoint(centerlinePointIndex+1);
			double centerlineLengthAfter = centerlineLengthBefore + (adjustedFirstLineSegmentLength-firstLineSegmentLength) +
					(adjustedSecondLineSegmentLength-secondLineSegmentLength);

			for(int i=0; i<getNumXsects(); i++) {
				Xsect xsect = getXsect(i);
				double xsectDist = xsect.getDistAlongCenterlineFeet();
				double newDistAlong = -Double.MAX_VALUE;

				if(xsectDist<=distToPointToBeMoved) {
					//do nothing--the xsect is upstream from the point to be moved--the best we can do is to 
					//keep it where it is, even if it is on the line segment that will move.
					newDistAlong = xsectDist;
				}else if(xsectDist >= distToDownstreamLineSegmentPoint) {
					//adjust based on change in centerline length
					newDistAlong = (centerlineLengthAfter-centerlineLengthBefore) + xsectDist;
				}else {
					//The xsect is located on the downstream line segment that will move.
					newDistAlong = (distToDownstreamLineSegmentPointAfter-distToDownstreamLineSegmentPoint) + xsectDist;
				}
				xsect.putDistAlongCenterlineFeet(newDistAlong);
			}
		}
		
		centerlinePoint.putXFeet(x);
		centerlinePoint.putYFeet(y);
	}//moveCenterlinePointFeet
	
	/*
	 * the distance along the centerline from the upstream end to the centerlinePoint at the given index.
	 */
	public double getDistToPoint(int index) {
		double returnValue = -Double.MAX_VALUE;
		if(getNumCenterlinePoints()>2) {
			returnValue = 0.0;
			if(index>0) {
				int xsIndex=0;
				while(xsIndex<index) {
					CenterlinePoint firstPoint = getCenterlinePoint(xsIndex);
					CenterlinePoint secondPoint = getCenterlinePoint(xsIndex+1);
					returnValue += getDistanceBetweenCenterlinePoints(firstPoint, secondPoint);
					xsIndex++;
				}				
			}
		}
		return returnValue;
	}//getDistToPoint
		
	/*
	 * Delete all points that are inside or outside a box drawn by user.
	 * This was intended to be used for centerlines that represent polygons used in GIS for channel volume calculation,
	 * and not necessarily for centerlines with cross-sections
	 */
	public void deleteCenterlinePointsInBox(JFrame parent, int option, double xi, double yi, double xf, double yf) {
		Vector<Integer> pointIndicesToDelete = new Vector<Integer>();
		for(int i=0; i<_centerlinePoints.size(); i++) {
			CenterlinePoint centerlinePoint = getCenterlinePoint(i);
			double x = centerlinePoint.getXFeet();
			double y = centerlinePoint.getYFeet();
			if(option==DELETE_INSIDE_WINDOW) {
				if( ((x>=xi && x<=xf) || (x<=xi && x>=xf)) && ((y>=yi && y<=yf) || (y<=yi && y>=yf)) ) {
					pointIndicesToDelete.addElement(i);
				}
			}else if(option==DELETE_OUTSIDE_WINDOW) {
				if( (x<xi && x<xf) || (x>xi && x>xf) || (y<yi && y<yf) || (y>yi && y>yf) ) {
					pointIndicesToDelete.addElement(i);
				}
			}else {
				JOptionPane.showMessageDialog(parent, "Centerline.deleteCenterlinePointsInBox: invalid option specified", 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		Integer[] pointIndicesToDeleteArray = pointIndicesToDelete.toArray(new Integer[pointIndicesToDelete.size()]);
		pointIndicesToDeleteArray = CsdpFunctions.qsort(pointIndicesToDeleteArray, 0, pointIndicesToDeleteArray.length-1);
		for(int i=pointIndicesToDeleteArray.length-1; i>0; i--) {
			_centerlinePoints.removeElementAt(pointIndicesToDeleteArray[i]);
			_numCenterlinePoints--;
		}

	}//deleteCenterlinePointsInBox

	/**
	 * delete CenterlinePoint that is closest to coordinates
	 * adjust cross-section lines positions to compensate for shortening of centerline.
	 */
	public void deleteCenterlinePoint(double x, double y) {
		int minDistIndex = getNearestPointIndex(x, y);
		if(minDistIndex<Integer.MAX_VALUE) {
			double centerlineLengthBeforeDelete = getLengthFeet();
			double centerlineLengthAfterDelete = centerlineLengthBeforeDelete - getDistToPoint(1);
			double centerlineLengthChange = centerlineLengthBeforeDelete-centerlineLengthAfterDelete;
			if(minDistIndex==0) {
				for(int i=0; i<getNumXsects(); i++) {
					Xsect xsect = getXsect(i);
					double xsectDist = xsect.getDistAlongCenterlineFeet();
					double newXsectDist = xsectDist - centerlineLengthChange;
					xsect.putDistAlongCenterlineFeet(newXsectDist);
				}
			}else if(minDistIndex==getNumCenterlinePoints()-1) {
				//downstream point deleted. do nothing.
			}else {
				//one of the midpoints deleted.
				double distToUpstreamPoint = getDistToPoint(minDistIndex-1);
				double distToPointToBeDeleted = getDistToPoint(minDistIndex);
				double distToDownstreamPoint = getDistToPoint(minDistIndex+1);

				double twoLineSegmentLength = (distToPointToBeDeleted-distToUpstreamPoint) + 
						(distToDownstreamPoint-distToPointToBeDeleted);
				double finalSingleLineSegmentLength = getDistanceBetweenCenterlinePoints(getCenterlinePoint(minDistIndex-1), 
						getCenterlinePoint(minDistIndex+1));
				for(int i=0; i<getNumXsects(); i++) {
					Xsect xsect = getXsect(i);
					double xsectDist = xsect.getDistAlongCenterlineFeet();
					double newDistAlong = -Double.MAX_VALUE;
					if(xsectDist<=distToUpstreamPoint) {
						//cross-section is upstream from the line segments that will change; no change
						newDistAlong = xsectDist;
					}else if(xsectDist>distToUpstreamPoint && xsectDist<=distToPointToBeDeleted) {
						//cross-section is on the upstream line segment that will change.
						//adjust xsect dist to maintain the same fraction of the distance from the upstream point.
						double distFromUpstreamPoint = xsectDist-distToUpstreamPoint;
						double fractionalDistFromUpstreamPoint = distFromUpstreamPoint/twoLineSegmentLength;
						newDistAlong = fractionalDistFromUpstreamPoint * finalSingleLineSegmentLength;
					}else if(xsectDist>distToPointToBeDeleted && xsectDist<=distToDownstreamPoint) {
						//xsect is downstream from line segments that will change. Adjust xsect dist to maintain 
						//the same fraction of the distance from the downstream line segment endpoint.
						double distFromDownstreamPoint = twoLineSegmentLength - (xsectDist-distToUpstreamPoint);
						double downstreamDistanceFraction = distFromDownstreamPoint/twoLineSegmentLength;
						double newDistFromDownstreamPoint = downstreamDistanceFraction * finalSingleLineSegmentLength; 
						newDistAlong = distToUpstreamPoint+finalSingleLineSegmentLength-newDistFromDownstreamPoint;
					}else {
						//xsect is downstream from the line segments that will change. adjust by ratio of initial
						//and final centerline lengths.
						newDistAlong = xsectDist*(finalSingleLineSegmentLength/twoLineSegmentLength);
					}//if
					xsect.putDistAlongCenterlineFeet(newDistAlong);
				}//for each xsect
			}//if/else
			_centerlinePoints.removeElementAt(minDistIndex);
			_numCenterlinePoints--;
			
			for(int i=getNumXsects()-1; i>=0; i--) {
				Xsect xsect = getXsect(i);
				double xsectDist = xsect.getDistAlongCenterlineFeet();
				if(xsectDist<=0.0 || xsectDist>=getLengthFeet()) {
					removeXsect(i);
				}
			}
		}//if
	}//deleteCenterlinePoint
	
	/*
	 * delete centerline point matching specified index. Maybe just for undo/redo
	 */
	public void deleteCenterlinePoint(int index) {
		if(getNumCenterlinePoints()>index) {
			_centerlinePoints.removeElementAt(index);
			_numCenterlinePoints--;
		}else {
			System.out.println("Error in Centerline.deleteCenterlinePoint: index is too large");
		}
	}//deleteCenterlinePoint

	/**
	 * sort array that stores all of the xsects in this centerline. sort by
	 * distance from upstream end. If an XsectGraph is open for this xsect,
	 * rename the graph.
	 */
	public ResizableIntArray sortXsectArray() {
		int numXsects = getNumXsects();
		double[] distances = new double[numXsects];
		double[] unsortedDistances = new double[numXsects];
		double[] sortedDistances = new double[numXsects];
		ResizableIntArray xsectIndices = new ResizableIntArray(1, 1);
		int left = 0;
		int right = numXsects - 1;
		Vector sortedXsects = new Vector();
		Xsect xsect = null;
		for (int i = 0; i <= right; i++) {
			xsect = getXsect(i);
			distances[i] = xsect.getDistAlongCenterlineFeet();
			unsortedDistances[i] = distances[i];
			if (DEBUG)
				System.out.println("i,unsortedDistances[i]=" + i + "," + unsortedDistances[i]);
		} // for

		sortedDistances = CsdpFunctions.qsort(distances, left, right);

		for (int i = 0; i <= right; i++) {
			int index = 0;
			while (unsortedDistances[i] != sortedDistances[index])
				index++;
			xsectIndices.put(i, index);
			if (DEBUG)
				System.out.println("xsectIndices: i,value=" + i + "," + index);
		} // for

		for (int i = 0; i <= right; i++) {
			xsect = getXsect(xsectIndices.get(i));
			xsect.putDistAlongCenterlineFeet(sortedDistances[i]);
			sortedXsects.addElement(xsect);
		}
		_xsects = sortedXsects;

		return xsectIndices;
	}// sortXsectArray

	/**
	 * returns point that is closest to specified coord.
	 */
	protected int getNearestPointIndex(double x, double y) {
		CenterlinePoint point;
		double minDist = Double.MAX_VALUE;
		int minDistIndex = Integer.MAX_VALUE;
		double dist;
		double x1;
		double y1;
		for (int i = 0; i <= getNumCenterlinePoints() - 1; i++) {
			point = getCenterlinePoint(i);
			x1 = point.getXFeet();
			y1 = point.getYFeet();
			dist = (double) CsdpFunctions.pointDist(x, y, x1, y1);
			if (DEBUG)
				System.out.println(
						"e.x, e.y, point#, x1, y1, dist" + x + "," + y + " " + i + " " + x1 + "," + y1 + " " + dist);
			if (dist < minDist) {
				minDist = dist;
				minDistIndex = i;
			} // if dist
		} // for i
		return minDistIndex;
	}// getNearestPoint

	/**
	 * reverse order of points
	 */
	public void reverseOrder() {
		Vector temp = new Vector();
		for (int i = 0; i <= getNumCenterlinePoints() - 1; i++) {
			temp.addElement(getCenterlinePoint(getNumCenterlinePoints() - 1 - i));
		}
		for (int i = 0; i <= getNumCenterlinePoints() - 1; i++) {
			_centerlinePoints.setElementAt(temp.elementAt(i), i);
		}
		
		int numXsects = getNumXsects();
		if(numXsects>0) {
			double[] xsectDistances = new double[numXsects];
			for(int i=0; i<numXsects; i++) {
				xsectDistances[i] = getXsect(i).getDistAlongCenterlineFeet();
			}
			for(int i=0; i<numXsects; i++) {
				getXsect(i).putDistAlongCenterlineFeet(getLengthFeet()-xsectDistances[numXsects-i-1]);
			}
		}		
	}//reserveOrder

	/**
	 * returns number of cross-sections in the centerline
	 */
	public int getNumXsects() {
		return _numXsects;
	}

	public int getNumXsectsWithPoints() {
		int returnValue = 0;
		for (int i = 0; i <= getNumXsects() - 1; i++) {
			if (getXsect(i).getNumPoints() > 0) {
				returnValue++;
			}
		}
		return returnValue;
	}

	/**
	 * add a cross-section
	 */
	public void addXsect() {
		_numXsects++;
		Xsect xsect = new Xsect();
		_xsects.addElement(xsect);
	}

	/**
	 * remove a cross-section
	 */
	public void removeXsect(int index) {
		if (_xsects.size() > 0) {
			_numXsects--;
			_xsects.removeElementAt(index);

		}
	}// removeXsect

	/**
	 * insert a cross-section at specified index
	 */
	public void addXsectAt(int index) {
		_numXsects++;
		Xsect xsect = new Xsect();
		_xsects.insertElementAt(xsect, index);
	}

	/**
	 * returns xsect object at specified index
	 */
	public Xsect getXsect(int index) {
		return (Xsect) _xsects.elementAt(index);
	}

	/**
	 * returns elevation of lowest point in the centerline.
	 */
	public double getMinimumElevationFeet() {
		Xsect xsect = null;
		double minElev = Double.MAX_VALUE;
		getAllXsects();

		for (int i = 0; i <= _numCombinedXsects - 1; i++) {
			xsect = (Xsect) (_allXsects.get(i));
			if (xsect.getMinimumElevationFeet() < minElev) {
				minElev = xsect.getMinimumElevationFeet();
			}
		}
		return minElev;
	}// getMinimumElevation

	// /**
	// * Calculate volume of centerline for specified elevation
	// */

	// public float getVolume(float elev){
	// float totalVolume = 0.0f;
	// Xsect xsect1 = null;
	// Xsect xsect2 = null;
	// float distance1 = -Float.MAX_VALUE;
	// float distance2 = -Float.MAX_VALUE;
	// float area1 = -Float.MAX_VALUE;
	// float area2 = -Float.MAX_VALUE;

	// //estimate volume of upstream portion of channel(between
	// //upstream node and first cross-section).

	// if(getNumXsects() > 0){
	// xsect1 = getXsect(0);
	// distance1 = xsect1.getDistAlongCenterline();
	// area1 = xsect1.getAreaSqft(elev);
	// totalVolume = distance1 * area1;
	// for(int j=1; j<=getNumXsects()-2; j++){
	// xsect1 = getXsect(j);
	// xsect2 = getXsect(j+1);
	// distance1 = xsect1.getDistAlongCenterline();
	// distance2 = xsect2.getDistAlongCenterline();
	// area1 = xsect1.getAreaSqft(elev);
	// area2 = xsect2.getAreaSqft(elev);
	// totalVolume += Math.abs( (distance2-distance1) *0.5f* (area2-area1) );
	// }
	// //estimate volume of downstream portion of channel
	// //(between last cross-section and downstream node).
	// xsect2 = getXsect(getNumXsects()-1);
	// distance2 = Math.abs(getLength() - xsect2.getDistAlongCenterline());
	// area2 = xsect2.getAreaSqft(elev);
	// totalVolume += area2 * distance2;
	// }else{
	// totalVolume = 0.0f;
	// }

	// return totalVolume;
	// }

	/**
	 * make array of all xsects(including copies, if any) if distances match,
	 * eliminate the ones that aren't copied and if copies override each other,
	 * keep the most recent.
	 */
	private void getAllXsects() {
		_numCombinedXsects = 0;
		_allXsects.removeAllElements();
		for (int i = 0; i <= getNumXsects() - 1; i++) {
			Xsect xsect = getXsect(i);
			if (xsect.getNumPoints() > 0) {
				_allXsects.insertElementAt(xsect, _numCombinedXsects);
				_numCombinedXsects++;
			} else {
				if (DEBUG)
					System.out.println("calculating average area");
				System.out.println(
						"not using cross-section " + getCenterlineName() + "_" + i + " because it has no points");
			}
		}
		for (int j = 0; j <= getNumCopiedXsects() - 1; j++) {
			_allXsects.insertElementAt(getCopiedXsect(j), _numCombinedXsects);
			_numCombinedXsects++;
		}
		for (int k = 0; k <= getNumRectXsects() - 1; k++) {
			_allXsects.insertElementAt(getRectXsect(k), _numCombinedXsects);
			_numCombinedXsects++;

			if (DEBUG)
				System.out.println("inserting rectxs for centerline " + getCenterlineName());
			if (DEBUG)
				System.out.println("area at 0 ft elevation=" + getRectXsect(k).getAreaSqft(0.0f));
		}

		// sort all xsects by distAlongCenterline
		CsdpFunctions.qsort(_allXsects, 0, _numCombinedXsects - 1);
	}

	/**
	 * Calculate weighted average of cross-sectional areas.
	 */
	public double getAverageAreaSqft(double elev, double chanLength) {
		double averageArea = -Double.MAX_VALUE;
		Xsect xsect1 = null;
		Xsect xsect2 = null;
		double normalizedDistance1 = -Double.MAX_VALUE;
		double normalizedDistance2 = -Double.MAX_VALUE;
		double area1 = -Double.MAX_VALUE;
		double area2 = -Double.MAX_VALUE;
		double normalizedDistLast = -Double.MAX_VALUE;

		getAllXsects();

		// estimate volume of upstream portion of channel(between
		// upstream node and first cross-section).
		if (_numCombinedXsects > 0) {
			double normalizedDistFirst = -Double.MAX_VALUE;
			xsect1 = (Xsect) (_allXsects.elementAt(0));
			normalizedDistFirst = getNormalizedDist(xsect1);
			// if it's a copied cross-section, the distAlongCenterline is
			// actually
			// the normalized dist.
			if (Double.isNaN(normalizedDistFirst) || normalizedDistFirst < 0.0f
					|| normalizedDistFirst > Double.MAX_VALUE) {
				normalizedDistFirst = xsect1.getDistAlongCenterlineFeet();
			}
			area1 = xsect1.getAreaSqft(elev);
			averageArea = area1 * normalizedDistFirst;

			if (_numCombinedXsects == 1) {
				xsect1 = (Xsect) (_allXsects.elementAt(0));
				averageArea = xsect1.getAreaSqft(elev);
			} else if (_numCombinedXsects >= 2) {
				for (int j = 0; j <= _numCombinedXsects - 2; j++) {
					xsect1 = (Xsect) (_allXsects.elementAt(j));
					xsect2 = (Xsect) (_allXsects.elementAt(j + 1));
					normalizedDistance1 = getNormalizedDist(xsect1);
					normalizedDistance2 = getNormalizedDist(xsect2);

					// This assumes that if all cross-sections are rectangular,
					// there will be exactly 2 cross-sections
					if (getNumRectXsects() == _numCombinedXsects) {
						normalizedDistance1 = 0.0;
						normalizedDistance2 = 1.0;
					}

					if (Double.isNaN(normalizedDistance1) || normalizedDistance1 < 0.0f
							|| normalizedDistance1 > Double.MAX_VALUE) {
						normalizedDistance1 = xsect1.getDistAlongCenterlineFeet();
					}
					if (Double.isNaN(normalizedDistance2) || normalizedDistance2 < 0.0f
							|| normalizedDistance2 > Double.MAX_VALUE) {
						normalizedDistance2 = xsect2.getDistAlongCenterlineFeet();
					}
					area1 = xsect1.getAreaSqft(elev);
					area2 = xsect2.getAreaSqft(elev);

					averageArea += Math.abs((area2 + area1) * 0.5f * (normalizedDistance2 - normalizedDistance1));
				} // for j
			} // if 2 or more cross-sections
			// estimate volume of downstream portion of channel
			// (between last cross-section and downstream node).

			xsect2 = (Xsect) (_allXsects.elementAt(_numCombinedXsects - 1));

			normalizedDistLast = getNormalizedDist(xsect2);
			if (Double.isNaN(normalizedDistLast) || normalizedDistLast < 0.0 || normalizedDistLast > Double.MAX_VALUE) {
				normalizedDistLast = xsect2.getDistAlongCenterlineFeet();
			}
			if (getNumRectXsects() == _numCombinedXsects)
				normalizedDistLast = 1.0;

			area2 = xsect2.getAreaSqft(elev);
			averageArea += area2 * (1.0 - normalizedDistLast);
		} else {
			System.out.println("No xs found; setting average area to zero");
			averageArea = 0.0;
		}

		return averageArea;
	}// getAverageArea

	/**
	 * Calculate weighted average of cross-sectional widths.
	 */
	public double getAverageWidthFeet(double elev, double chanLength) {
		double averageWidth = -Double.MAX_VALUE;
		Xsect xsect1 = null;
		Xsect xsect2 = null;
		double normalizedDistance1 = -Double.MAX_VALUE;
		double normalizedDistance2 = -Double.MAX_VALUE;
		double width1 = -Double.MAX_VALUE;
		double width2 = -Double.MAX_VALUE;
		double normalizedDistLast = -Double.MAX_VALUE;

		getAllXsects();
		// estimate volume of upstream portion of channel(between
		// upstream node and first cross-section).

		if (_numCombinedXsects > 0) {

			double normalizedDistFirst = -Double.MAX_VALUE;
			xsect1 = (Xsect) (_allXsects.elementAt(0));
			normalizedDistFirst = getNormalizedDist(xsect1);
			if (Double.isNaN(normalizedDistFirst) || normalizedDistFirst < 0.0f
					|| normalizedDistFirst > Double.MAX_VALUE) {
				normalizedDistFirst = xsect1.getDistAlongCenterlineFeet();
			}
			width1 = xsect1.getWidthFeet(elev);
			averageWidth = width1 * normalizedDistFirst;

			if (_numCombinedXsects == 1) {
				xsect1 = (Xsect) (_allXsects.elementAt(0));
				averageWidth = xsect1.getWidthFeet(elev);
			} else if (_numCombinedXsects >= 2) {
				for (int j = 0; j <= _numCombinedXsects - 2; j++) {
					xsect1 = (Xsect) (_allXsects.elementAt(j));
					xsect2 = (Xsect) (_allXsects.elementAt(j + 1));
					normalizedDistance1 = getNormalizedDist(xsect1);
					normalizedDistance2 = getNormalizedDist(xsect2);
					if (getNumRectXsects() == _numCombinedXsects) {
						normalizedDistance1 = 0.0;
						normalizedDistance2 = 1.0;
					}
					if (Double.isNaN(normalizedDistance1) || normalizedDistance1 < 0.0f
							|| normalizedDistance1 > Double.MAX_VALUE) {
						normalizedDistance1 = xsect1.getDistAlongCenterlineFeet();
					}
					if (Double.isNaN(normalizedDistance2) || normalizedDistance2 < 0.0f
							|| normalizedDistance2 > Double.MAX_VALUE) {
						normalizedDistance2 = xsect2.getDistAlongCenterlineFeet();
					}
					width1 = xsect1.getWidthFeet(elev);
					width2 = xsect2.getWidthFeet(elev);

					averageWidth += Math.abs((width2 + width1) * 0.5f * (normalizedDistance2 - normalizedDistance1));

				}
			} // if 2 or more cross-sections
			// estimate volume of downstream portion of channel
			// (between last cross-section and downstream node).
			xsect2 = (Xsect) (_allXsects.elementAt(_numCombinedXsects - 1));
			normalizedDistLast = getNormalizedDist(xsect2);
			if (Double.isNaN(normalizedDistLast) || normalizedDistLast < 0.0f
					|| normalizedDistLast > Double.MAX_VALUE) {
				normalizedDistLast = xsect2.getDistAlongCenterlineFeet();
			}
			if (getNumRectXsects() == _numCombinedXsects)
				normalizedDistLast = 1.0;
			width2 = xsect2.getWidthFeet(elev);
			averageWidth += width2 * (1.0f - normalizedDistLast);
		} else {
			averageWidth = 0.0;
		}

		return averageWidth;
	}// getAverageWidth

	/**
	 * Calculate weighted average of cross-sectional wettedPerimeters.
	 */
	public double getAverageWettedPerimeterFeet(double elev, double chanLength) {
		double averageWettedPerimeter = -Double.MAX_VALUE;
		Xsect xsect1 = null;
		Xsect xsect2 = null;
		double normalizedDistance1 = -Double.MAX_VALUE;
		double normalizedDistance2 = -Double.MAX_VALUE;
		double wettedPerimeter1 = -Double.MAX_VALUE;
		double wettedPerimeter2 = -Double.MAX_VALUE;
		double normalizedDistLast = -Double.MAX_VALUE;

		getAllXsects();
		// estimate volume of upstream portion of channel(between
		// upstream node and first cross-section).
		// if(getNumXsects() > 0){
		if (_numCombinedXsects > 0) {

			double normalizedDistFirst = -Double.MAX_VALUE;

			xsect1 = (Xsect) (_allXsects.elementAt(0));

			normalizedDistFirst = getNormalizedDist(xsect1);
			if (Double.isNaN(normalizedDistFirst) || normalizedDistFirst < 0.0f
					|| normalizedDistFirst > Double.MAX_VALUE) {
				normalizedDistFirst = xsect1.getDistAlongCenterlineFeet();
			}
			wettedPerimeter1 = xsect1.getWettedPerimeterFeet(elev);
			averageWettedPerimeter = wettedPerimeter1 * normalizedDistFirst;

			if (_numCombinedXsects == 1) {
				xsect1 = (Xsect) (_allXsects.elementAt(0));
				averageWettedPerimeter = xsect1.getWettedPerimeterFeet(elev);
			} else if (_numCombinedXsects >= 2) {
				for (int j = 0; j <= _numCombinedXsects - 2; j++) {
					xsect1 = (Xsect) (_allXsects.elementAt(j));
					xsect2 = (Xsect) (_allXsects.elementAt(j + 1));

					normalizedDistance1 = getNormalizedDist(xsect1);
					normalizedDistance2 = getNormalizedDist(xsect2);
					if (getNumRectXsects() == _numCombinedXsects) {
						normalizedDistance1 = 0.0;
						normalizedDistance2 = 1.0;
					}
					if (Double.isNaN(normalizedDistance1) || normalizedDistance1 < 0.0f
							|| normalizedDistance1 > Double.MAX_VALUE) {
						normalizedDistance1 = xsect1.getDistAlongCenterlineFeet();
					}
					if (Double.isNaN(normalizedDistance2) || normalizedDistance2 < 0.0f
							|| normalizedDistance2 > Double.MAX_VALUE) {
						normalizedDistance2 = xsect2.getDistAlongCenterlineFeet();
					}
					wettedPerimeter1 = xsect1.getWettedPerimeterFeet(elev);
					wettedPerimeter2 = xsect2.getWettedPerimeterFeet(elev);

					averageWettedPerimeter += Math.abs(
							(wettedPerimeter2 + wettedPerimeter1) * 0.5f * (normalizedDistance2 - normalizedDistance1));

				}
			} // if 2 or more cross-sections
			// estimate volume of downstream portion of channel
			// (between last cross-section and downstream node).
			xsect2 = (Xsect) (_allXsects.elementAt(_numCombinedXsects - 1));

			normalizedDistLast = getNormalizedDist(xsect2);
			if (Double.isNaN(normalizedDistLast) || normalizedDistLast < 0.0f
					|| normalizedDistLast > Double.MAX_VALUE) {
				normalizedDistLast = xsect2.getDistAlongCenterlineFeet();
			}
			if (getNumRectXsects() == _numCombinedXsects)
				normalizedDistLast = 1.0;
			wettedPerimeter2 = xsect2.getWettedPerimeterFeet(elev);
			averageWettedPerimeter += wettedPerimeter2 * (1.0f - normalizedDistLast);
		} else {
			averageWettedPerimeter = 0.0;
		}

		return averageWettedPerimeter;
	}// getAverageWettedPerimeter

	/**
	 * returns width weighted hydraulic depth using all xs in centerline.
	 */
	public double getWidthWeightedHydraulicDepthFeet(double elev) {
		double wwhd = 0.0;
		double sumOfWidths = 0.0;
		double sumOfWidthTimesHD = 0.0;
		double width = -Double.MAX_VALUE;
		double hd = -Double.MAX_VALUE;
		Xsect xsect = null;

		for (int i = 0; i <= getNumXsects() - 1; i++) {
			xsect = getXsect(i);
			width = xsect.getWidthFeet(elev);
			hd = xsect.getHydraulicDepthFeet(elev);
			sumOfWidths += width;
			sumOfWidthTimesHD += width * hd;
		}
		wwhd = sumOfWidthTimesHD / sumOfWidths;
		if (wwhd < 0)
			System.out.println("ERROR in Centerline.getWidthWeightedHydraulic Depth:  value=" + wwhd);

		return wwhd;
	}

	public Xsect getCopiedXsect(int index) {
		return (Xsect) (_copiedXsects.elementAt(index));
	}

	public Xsect getRectXsect(int index) {
		return (Xsect) (_rectXsects.elementAt(index));
	}

	/**
	 * Adds a cross-section that is copied from another centerline Distance
	 * along centerline should already be set in the xsect object.
	 */
	public void addCopiedXsect(Xsect xsect) {
		_numCopiedXsects++;
		_copiedXsects.addElement(xsect);
	}

	// /**
	// * adds all cross-sections that should be copied
	// */
	// public void addCopiedXsects(Network net, IrregularXsectsInp ixi){
	// IrregularXsectsInp.IXIChan ixiChan = null;
	// String centerlineName = getCenterlineName();
	// Centerline otherCenterline = null;
	// StringTokenizer t = null;
	// String lastToken = null;
	// String nextToLastToken = null;

	// if(ixi.chanExists(centerlineName)){

	// System.out.println("inside Centerline.addCopiedXsects. found copy for
	// centerline "+centerlineName);

	// ixiChan = ixi.getChan(centerlineName);
	// for(int i=0; i<=ixiChan.getNumLines()-1; i++){
	// //delimiters=tab, space, forward slash, backslash
	// t = new StringTokenizer(ixiChan.getLine(i).getFilename(),
	// "\t \057\134");
	// for(int j=0; j<=t.countTokens()-1; j++){
	// nextToLastToken = lastToken;
	// lastToken = t.nextToken();
	// }

	// //DON't need otherCenterline!

	// if(centerlineName.equals(nextToLastToken) == false){
	// otherCenterline = net.getCenterline(nextToLastToken);
	// }else{
	// otherCenterline = this;
	// }
	// for(int k=0; k<=otherCenterline.getNumXsects()-1; k++){
	// t = new StringTokenizer(lastToken,".");
	// for(int m=0; m<=t.countTokens()-1; m++){
	// nextToLastToken = lastToken;
	// lastToken=t.nextToken();
	// }//for m
	// if(otherCenterline.getNormalizedDist
	// (otherCenterline.getXsect(k)) ==
	// Double.parseDouble(nextToLastToken)){
	// addCopiedXsect(otherCenterline.getXsect(k));
	// }//if
	// }//for k
	// }//for i
	// }//if
	// }//addCopiedXsects

	/**
	 * Adds rectangular cross-sections using dimensions from xsects.inp file.
	 * Only used for rectangular cross-section calculation.
	 */
	public void addRectangularXsects(DSMChannels dsmChannels, XsectsInp xi) {
		String centerlineName = getCenterlineName();
		int xsect1 = dsmChannels.getXsect1(centerlineName);
		int dist1 = dsmChannels.getDist1(centerlineName);
		int xsect2 = dsmChannels.getXsect2(centerlineName);
		int dist2 = dsmChannels.getDist2(centerlineName);

		if (DEBUG)
			System.out.println("addRectangularXsects for centerline " + getCenterlineName() + ":  dist1, dist2=" + dist1
					+ "," + dist2);

		// String x1 = new Integer(xsect1).toString();
		// String d1 = new Integer(dist1).toString();
		// String x2 = new Integer(xsect2).toString();
		// String d2 = new Integer(dist2).toString();

		String x1 = Integer.toString(xsect1);
		String d1 = Integer.toString(dist1);
		String x2 = Integer.toString(xsect2);
		String d2 = Integer.toString(dist2);

		double width1 = xi.getWidthFeet(x1);
		double botelv1 = xi.getBotelvFeet(x1);
		double width2 = xi.getWidthFeet(x2);
		double botelv2 = xi.getBotelvFeet(x2);

		if (DEBUG)
			System.out.println("adding rect xs.  width1,width2,botelv1,botelv2=" + width1 + "," + width2 + "," + botelv1
					+ "," + botelv2);
		_numRectXsects += 2;
		Xsect xs1 = new Xsect();
		Xsect xs2 = new Xsect();
		xs1.putDistAlongCenterlineFeet(0.0f);
		xs1.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, 0.0f, 100.0f);
		xs1.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, 0.0f, botelv1);
		xs1.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, width1, botelv1);
		xs1.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, width1, 100.0f);

		xs2.putDistAlongCenterlineFeet(0.0f);
		xs2.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, 0.0f, 100.0f);
		xs2.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, 0.0f, botelv2);
		xs2.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, width2, botelv2);
		xs2.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, width2, 100.0f);

		_rectXsects.addElement(xs1);
		_rectXsects.addElement(xs2);

	}

	public int getNumCopiedXsects() {
		return _numCopiedXsects;
	}

	public int getNumRectXsects() {
		return _numRectXsects;
	}

	/**
	 * returns name of centerline
	 */
	public String getCenterlineName() {
		return _centerlineName;
	}

	/**
	 * sets name of centerline
	 */
	public void setCenterlineName(String name) {
		_centerlineName = name;
	}

	/**
	 * returns minimum value of X
	 */
	private double getMinXFeet() {
		return _minX;
	}

	/**
	 * returns maximum value of X
	 */
	private double getMaxXFeet() {
		return _maxX;
	}

	/**
	 * returns minimum value of Y
	 */
	private double getMinYFeet() {
		return _minY;
	}

	/**
	 * returns maximum value of Y
	 */
	private double getMaxYFeet() {
		return _maxY;
	}

	/*
	 * Returns estimated volume for selected centerline. Assumptions:
	 * 1. no interpolation from adjacent channels
	 * 2. volume between cross-sections will be the average of the two volumes times the distance between them.
	 * 3. volume between ends of channel and nearest cross-section will be area times length. 
	 */
	public double getChannelVolumeEstimateNoInterp(double elevation) {
		int numXsects = getNumXsects();
		double returnValue = 0.0;
		Xsect lastXsect = null;
		for(int i=0; i<numXsects; i++) {
			Xsect currentXsect = getXsect(i);
			if(currentXsect.getNumPoints()>0) {
				double currentXSDist = currentXsect.getDistAlongCenterlineFeet();
				double currentArea = currentXsect.getAreaSqft(elevation);
				if(lastXsect!=null) {
					double lastXSDist = lastXsect.getDistAlongCenterlineFeet();
					double lastArea = lastXsect.getAreaSqft(elevation);
					returnValue += 0.5*(lastArea+currentArea) * (currentXSDist-lastXSDist);
				}else {
					returnValue += currentXSDist * currentArea;
				}
				lastXsect = currentXsect;
			}
		}
		//Add the last portion
		if(getNumXsectsWithPoints()>=1) {
			returnValue += (getLengthFeet() - lastXsect.getDistAlongCenterlineFeet()) * lastXsect.getAreaSqft(elevation);
		}
		//		System.out.println("area, length="+lastXsect.getAreaSqft(elevation)+","+getLengthFeet());
		//		System.out.println("simple volume="+getLengthFeet()*lastXsect.getAreaSqft(elevation));
		return returnValue;
	}//getChannelVolumeEstimateNoInterp

	/*
	 * Returns estimated wettedArea for selected centerline. Assumptions:
	 * 1. no interpolation from adjacent channels
	 * 2. wettedArea between cross-sections will be the average of the two wetted perimeters times the distance between them.
	 * 3. wettedArea between ends of channel and nearest cross-section will be wetted perimeter times length. 
	 */
	public double getChannelWettedAreaEstimateNoInterp(double elevation) {
		int numXsects = getNumXsects();
		double returnValue = 0.0;
		Xsect lastXsect = null;
		for(int i=0; i<numXsects; i++) {
			Xsect currentXsect = getXsect(i);
			if(currentXsect.getNumPoints()>0) {
				double currentXSDist = currentXsect.getDistAlongCenterlineFeet();
				double currentWetP = currentXsect.getWettedPerimeterFeet(elevation);
				if(lastXsect!=null) {
					double lastXSDist = lastXsect.getDistAlongCenterlineFeet();
					double lastWetP = lastXsect.getWettedPerimeterFeet(elevation);
					returnValue += 0.5*(lastWetP+currentWetP) * (currentXSDist-lastXSDist);
				}else {
					returnValue += currentXSDist * currentWetP;
				}
				lastXsect = currentXsect;
			}
		}
		//Add the last portion
		if(getNumXsectsWithPoints()>=1) {
			returnValue += (getLengthFeet() - lastXsect.getDistAlongCenterlineFeet()) * lastXsect.getWettedPerimeterFeet(elevation);
		}
		return returnValue;
	}//getChannelVolumeEstimateNoInterp

	/*
	 * Returns estimated surface area for selected centerline. Assumptions:
	 * 1. no interpolation from adjacent channels
	 * 2. surface area between cross-sections will be the average of the two widths times the distance between them.
	 * 3. surface area between ends of channel and nearest cross-section will be width times length. 
	 */
	public double getChannelSurfaceAreaEstimateNoInterp(double elevation) {
		int numXsects = getNumXsects();
		double returnValue = 0.0;
		Xsect lastXsect = null;
		for(int i=0; i<numXsects; i++) {
			Xsect currentXsect = getXsect(i);
			if(currentXsect.getNumPoints()>0) {
				double currentXSDist = currentXsect.getDistAlongCenterlineFeet();
				double currentWidth = currentXsect.getWidthFeet(elevation);
				if(lastXsect!=null) {
					double lastXSDist = lastXsect.getDistAlongCenterlineFeet();
					double lastWidth = lastXsect.getWidthFeet(elevation);
					returnValue += 0.5*(lastWidth+currentWidth) * (currentXSDist-lastXSDist);
				}else {
					returnValue += currentXSDist * currentWidth;
				}
				lastXsect = currentXsect;
			}
		}
		//Add the last portion
		if(getNumXsectsWithPoints()>0) {
			returnValue += (getLengthFeet() - lastXsect.getDistAlongCenterlineFeet()) * lastXsect.getWidthFeet(elevation);
		}
		return returnValue;
	}//getChannelVolumeEstimateNoInterp

	/*
	 * Returns the number of computational reaches based on delta x and centerline length
	 */
	public int getNumComputationalReaches(double deltaX) {
		double length = getLengthFeet();
		return 1+(int)(Math.max(0.0, length-deltaX)/deltaX);
	}

	/*
	 * Returns the number of computational points based on delta x and centerline length
	 */
	public int getNumComputationalPoints(double deltaX) {
		double length = getLengthFeet();
		return 3 + 2*(int)(Math.max(0.0, length-deltaX)/deltaX);
	}

	/*
	 * Returns the area of the cross-section with the maximum area divided by the minimum cross-sectional area
	 */
	public double getMaxAreaRatio() {
		return getMaxArea()/getMinArea();
	}//getMaxAreaRatio


	/*
	 * For a given elevation, returns the maximum ratio of adjacent cross-sectional areas, 
	 * comparing all cross-sections in the channel 
	 */
	public double getMaxAdjacentAreaRatio(double elevation) {
		double maar = 0.0;
		for(int i=0; i<getNumXsects()-1; i++) {
			Xsect currentXsect = getXsect(i);
			Xsect nextXsect = getXsect(i+1);
			double a1 = currentXsect.getAreaSqft(elevation);
			double a2 = nextXsect.getAreaSqft(elevation);
			double ar = Math.max(a1/a2, a2/a1);
			if(a1==0.0 || a2==0.0) ar=0.0;
			maar = Math.max(ar, maar);
		}
		return maar;
	}

	/*
	 * Get max maar in range (could be intertidal zone)
	 */
	public double[][] getMaxAdjacentAreaRatioInRange(int numValues, double minStage, double maxStage, double elevationIncrement) {
		
		double[][] returnValues = new double[2][numValues];

		//		double maxMaar = 0.0;
		ResizableDoubleArray elevRDA = new ResizableDoubleArray();
		int index = 0;
		for(double d = minStage; d<maxStage; d+=elevationIncrement) {
			double maar = getMaxAdjacentAreaRatio(d);
			returnValues[0][index] = d;
			returnValues[1][index] = maar;
			index++;
		}
		return returnValues;
	}
	
	public double getMinArea() {
		double minArea = Double.MAX_VALUE;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.getNumPoints()>0) {
				double area = xsect.getAreaSqft(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				minArea = Math.min(minArea, area);
			}
		}
		return minArea;
	}

	public double getMaxArea() {
		double maxArea = -Double.MAX_VALUE;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.getNumPoints()>0) {
				double area = xsect.getAreaSqft(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				maxArea = Math.max(maxArea, area);
			}
		}
		return maxArea;
	}

	/*
	 * Return the highest bottom elevation of all the cross-sections in the channel.
	 */
	public double getHighestBottomElevation() {
		double returnValue = -Double.MAX_VALUE;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			double minElev = xsect.getMinimumElevationFeet();
			//if xsect has no points, minElev will be Double.MAX_VALUE;
			if(minElev<Double.MAX_VALUE) {
				returnValue = Math.max(returnValue, minElev);
			}
		}
		return returnValue;
	}

	/*
	 * Return the lowest bottom elevation of all the cross-sections in the channel.
	 */
	public double getLowestBottomElevation() {
		double returnValue = Double.MAX_VALUE;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			double minElev = xsect.getMinimumElevationFeet();
			//if xsect has no points, minElev will be Double.MAX_VALUE;
			if(minElev<Double.MAX_VALUE) {
				returnValue = Math.min(returnValue, minElev);
			}
		}
		return returnValue;
	}

	/*
	 * Returns an array of cross-section indices of the cross-sections with min and max areas at specified elevation
	 */
	public int[] getMinMaxAreaXsectIndices() {
		double minArea = Double.MAX_VALUE;
		double maxArea = -Double.MAX_VALUE;
		int minAreaIndex = -Integer.MAX_VALUE;
		int maxAreaIndex = -Integer.MAX_VALUE;

		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.getNumPoints()>0) {
				double area = xsect.getAreaSqft(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				if(area<minArea) {
					minArea = area;
					minAreaIndex = i;
				}
				if(area>maxArea) {
					maxArea = area;
					maxAreaIndex = i;
				}
			}
		}
		return new int[] {minAreaIndex, maxAreaIndex};
	}//getMinMaxAreaIndices

	/*
	 * Returns vector of xsect indices with duplicate station values. The most likely cause of this error
	 * is an old bug which changed all of the station values in all of the cross-section drawings to zero.
	 */
	public Vector<Integer> getDuplicateStationsXsectIndices(){
		Vector<Integer> returnValues = new Vector<Integer>(); 
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(!xsect.allUniqueStations()) {
				returnValues.addElement(i);
			}
		}
		return returnValues;
	}//getDuplicateStationsXsectIndices

	/*
	 * Returns vector of xsect indices with -dk anywhere in cross-section.
	 */
	public Vector<Integer> getNegDKXsectIndices() {
		Vector<Integer> returnValues = new Vector<Integer>();
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.getNumPoints()>0) {
				double[] dConveyanceValues = xsect.getDConveyanceValues();
				for(int j=0; j<dConveyanceValues.length; j++) {
					double dk = dConveyanceValues[j];
					if(dk<0.0) {
						returnValues.addElement(i);
						break;
					}
				}
			}
		}
		return returnValues;
	}//getNegDKXsectIndices

	/*
	 * Returns vector of xsect indices with -dk in intertidal zone.
	 */
	public Vector<Integer> getNegDKIntertidalXsectIndices() {
		Vector<Integer> returnValues = new Vector<Integer>();
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.hasNegDConveyanceInIntertidalZone()) {
				returnValues.addElement(i);
			}
		}
		return returnValues;
	}

	/*
	 * return vector of indices of cross-sections with no points. 
	 */
	public Vector<Integer> getXSWithNoPointsIndices() {
		Vector<Integer> returnValues = new Vector<Integer>();
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.hasNoPoints()) {
				returnValues.addElement(i);
			}
		}
		return returnValues;
	}

	/*
	 * Returns vector containing indices of all cross-sections that are within specified distance of another cross-section.
	 */
	public HashSet<Integer> getXSWithinSpecifiedDistanceIndices(double specifiedDistance) {
		HashSet<Integer> returnValues = new HashSet<Integer>(); 
		double[] distances = new double[getNumXsects()];
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			double distanceAlong = xsect.getDistAlongCenterlineFeet();
			distances[i] = distanceAlong;  
		}

		for(int i=0; i<distances.length-1; i++) {
			double distanceToNext = distances[i+1]-distances[i];
			if(distanceToNext<specifiedDistance) {
				returnValues.add(i);
				returnValues.add(i+1);
			}
		}
		return returnValues;
	}//getXSWithinSpecifiedDistanceIndices


	/*
	 * Returns true if all xs have no points
	 */
	public boolean allXSHaveNoPoints() {
		boolean returnValue = true;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(!xsect.hasNoPoints()) {
				returnValue = false;
				break;
			}
		}
		return returnValue;
	}//allXSHaveNoPoints

	public boolean anyXSHaveNegDKInIntertidal() {
		boolean returnValue = false;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(xsect.hasNegDConveyanceInIntertidalZone()) {
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}

	public boolean anyXSHaveDuplicateStations() {
		boolean returnValue = false;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			if(!xsect.allUniqueStations()) {
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}

	/*
	 * Given two points that define a line segment, return true if the line segment intersects any 
	 * centerline segment
	 */
	public boolean intersectsLine(double x1, double x2, double y1, double y2) {
		boolean intersects = false;
		for(int i=1; i<getNumCenterlinePoints(); i++) {
			CenterlinePoint lastCenterlinePoint = getCenterlinePoint(i-1);
			CenterlinePoint currentCenterlinePoint = getCenterlinePoint(i);
			double x3 = lastCenterlinePoint.getXFeet();
			double y3 = lastCenterlinePoint.getYFeet();
			double x4 = currentCenterlinePoint.getXFeet();
			double y4 = currentCenterlinePoint.getYFeet();
			if(Line2D.Double.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}//intersectsLine 

	/*
	 * For creating a centerline from multiple centerlines. Given another centerline, return 
	 * UPSTREAM_TO_DOWNSTREAM if the points from this centerline (not "adjacentCenterline") should
	 * be added starting from the upstream end. Otherwise, return DOWNSTREAM_TO_UPSTREAM.
	 */
	public int getPointOrderingForContinuousCenterline(Centerline adjacentCenterline, 
			boolean lastCenterline) {
		//determine the minimum distance between the endpoints of each centerline. 
		CenterlinePoint thisUpstreamPoint = getCenterlinePoint(0);
		CenterlinePoint thisDownstreamPoint = getCenterlinePoint(getNumCenterlinePoints()-1);
		CenterlinePoint adjacentUpstreamPoint = getCenterlinePoint(0);
		CenterlinePoint adjacentDownstreamPoint = 
				adjacentCenterline.getCenterlinePoint(adjacentCenterline.getNumCenterlinePoints()-1);
		double upstreamUpstreamDist = getDistanceBetweenCenterlinePoints(thisUpstreamPoint, adjacentUpstreamPoint);
		double upstreamDownstreamDist = getDistanceBetweenCenterlinePoints(thisUpstreamPoint, adjacentDownstreamPoint);
		double downstreamUpstreamDist = getDistanceBetweenCenterlinePoints(thisDownstreamPoint, adjacentUpstreamPoint);
		double downstreamDownstreamDist = getDistanceBetweenCenterlinePoints(thisDownstreamPoint, adjacentDownstreamPoint);
		double minUpstreamDist = Math.min(upstreamUpstreamDist, upstreamDownstreamDist);
		double minDownstreamDist = Math.min(downstreamUpstreamDist, downstreamDownstreamDist);
		if(!lastCenterline) {
			if(minUpstreamDist>=minDownstreamDist) {
				return UPSTREAM_TO_DOWNSTREAM;
			}else {
				return DOWNSTREAM_TO_UPSTREAM;
			}
		}else {
			if(minUpstreamDist>=minDownstreamDist) {
				return DOWNSTREAM_TO_UPSTREAM;
			}else {
				return UPSTREAM_TO_DOWNSTREAM;
			}
		}
	}//getPointOrderingForContinuousCenterline

	public double getDistanceBetweenCenterlinePoints(CenterlinePoint cp1, CenterlinePoint cp2) {
		double x1 = cp1.getXFeet();
		double y1 = cp1.getYFeet();
		double x2 = cp2.getXFeet();
		double y2 = cp2.getYFeet();
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
//	/*
//	 * Given a point on another centerline, find the closest point on the centerline to the given point.
//	 * The result could be a point on one of the centerline's line segments or one of the vertices (centerline points)
//	 */
//	public double[] closestPointOnCenterlineToPoint(CenterlinePoint centerlinePoint) {
//		double[] returnValues = new double[2];
//		double minDist = Double.MAX_VALUE;
//		//first look through centerline points and find closest point
//		for(int i=0; i<getNumCenterlinePoints(); i++) {
//			CenterlinePoint centerlinePoint2 = getCenterlinePoint(i);
//			double dist = Math.sqrt(math.pow(centerlinePoint.getXFeet()-centerlinePoint2.getXFeet(),2) + 
//					Math.pow(centerlinePoint.getYFeet()-centerlinePoint2.getYFeet(), 2));
//			if(dist<minDist) {
//				minDist = dist;
//				returnValues[0] = centerlinePoint2.getXFeet();
//				returnValues[1] = centerlinePoint2.getYFeet();
//			}
//		}
//		//now find the 
//	}

	public void removeAllCrossSections() {
		for(int i=getNumXsects()-1; i>=0; i--) {
			removeXsect(i);
		}
	}

	/*
	 * For Centerline 3d plot
	 */
	public double getMaximumXsectLineLength() {
		double maxLength = -Double.MAX_VALUE;
		for(int i=0; i<getNumXsects(); i++) {
			Xsect xsect = getXsect(i);
			maxLength = Math.max(maxLength, xsect.getXsectLineLengthFeet());
		}
		return maxLength;
	}

	public int getClosestXsectIndex(double dist0) {
		int closestXsectIndex = 0;
		if(getNumXsects()<=0) {
			closestXsectIndex = -Integer.MAX_VALUE;
		}else {
			double currentXsectDist = getXsect(0).getDistAlongCenterlineFeet();
			double distDifference = Math.abs(currentXsectDist-dist0);
			for(int i=0; i<getNumXsects(); i++) {
				double newDistDifference = Math.abs(dist0-getXsect(i).getDistAlongCenterlineFeet());
				if(newDistDifference>distDifference) {
					break;
				}else {
					distDifference = newDistDifference;
					closestXsectIndex = i;
				}
			}
		}
		return closestXsectIndex;
	}//getClosestXsectIndex

	/*
	 * Finds centroid of all centerline points
	 */
	public double[] getCentroid() {
		int numCenterlinePoints = getNumCenterlinePoints();
		double x = 0.0;
		double y = 0.0;
		
		for(int i=0; i<getNumCenterlinePoints(); i++) {
			CenterlinePoint centerlinePoint = getCenterlinePoint(i);
			x += centerlinePoint._x/(double)numCenterlinePoints;
			y += centerlinePoint._y/(double)numCenterlinePoints;
		}
		double[] returnValues = new double[2];
		returnValues[CsdpFunctions.xIndex] = x;
		returnValues[CsdpFunctions.yIndex] = y;
		return returnValues;
	}//getCentroid

	/*
	 * When user tries to move a centerline endpoint, check to see if this would result in a cross-section being
	 * outside the centerline. If so, return false; otherwise, true.
	 */
	public boolean endpointMovementIsOk(int centerlineEndpointIndex, double xDataCoordFinal, double yDataCoordFinal) {
		//first check to see if there are any cross-sections in the affected line segment.
		CenterlinePoint endPoint = getCenterlinePoint(centerlineEndpointIndex);
		CenterlinePoint adjacentPoint = null;
		double xsectDistFromEndpoint = -Double.MAX_VALUE;

		if(getNumXsects()==0) {
			return true;
		}else {
			if(centerlineEndpointIndex==0) {
				adjacentPoint = getCenterlinePoint(1);
				//if first xsect dist is less than the change in line segment length, 
				//there is an xs on on the affected line segment: return false
				xsectDistFromEndpoint = getXsect(0).getDistAlongCenterlineFeet();
			}else {
				adjacentPoint = getCenterlinePoint(getNumCenterlinePoints()-2);
				//if centerline length - last xsect dist is less than change in line segment length, 
				//there is an xs on the affected line segment; return false
				xsectDistFromEndpoint = getLengthFeet() - getXsect(getNumXsects()-1).getDistAlongCenterlineFeet();
			}
			double initialLineSegmentLength = CsdpFunctions.pointDist(endPoint.getXFeet(), endPoint.getYFeet(), 
					adjacentPoint.getXFeet(), adjacentPoint.getYFeet());
			double finalLineSegmentLength = CsdpFunctions.pointDist(xDataCoordFinal, yDataCoordFinal, 
					adjacentPoint.getXFeet(), adjacentPoint.getYFeet());
			double changeInlineSegmentLength = finalLineSegmentLength-initialLineSegmentLength;
			if(changeInlineSegmentLength < 0 && xsectDistFromEndpoint < Math.abs(changeInlineSegmentLength)) {
				return false;
			}else {
				return true;
			}
		}
	}


//	public double getDistAlongToPoint(int centerlinePointIndex) {
//		for(int i=0; i<centerlinePointIndex; i++) {
//			CenterlinePoint upstreamPoint = 
//		}
//	}

}// class Centerline
