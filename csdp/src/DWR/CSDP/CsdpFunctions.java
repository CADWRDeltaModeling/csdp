package DWR.CSDP;

import java.awt.BasicStroke;
import java.awt.Color;
//import DWR.CSDP.semmscon.UseSemmscon;
import java.awt.Cursor;
import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

import DWR.CSDP.semmscon.UseSemmscon;

/**
 * Global functions and parameters for CSDP
 */
public class CsdpFunctions {

	/**
	 * finds the distance between two points
	 */
	public static double pointDist(double x1, double y1, double x2, double y2) {
		return (double) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}// pointDist

	/**
	 * finds the distance between two points
	 */
	public static int pointDist(int x1, int y1, int x2, int y2) {
		double x1f = (double) x1;
		double x2f = (double) x2;
		double y1f = (double) y1;
		double y2f = (double) y2;
		double p = pointDist(x1f, y1f, x2f, y2f);
		return (int) p;
	}// pointDist

	// /**
	// * Convert x,y point to station (xsect point horizontal coordinate in
	// * relative coordinate system. Used to convert network coordinates
	// * for datum changes.
	// */
	// public static float xyToStation(float x1, float x2, float y1, float y2,
	// float x, float y){
	// float station=-Float.MAX_VALUE;
	// float theta = getTheta(x1, x2, y1, y2);
	// if(Math.cos(theta)==0.0){
	// station=y-y1;
	// }else if(Math.sin(theta)==0.0){
	// station=x-x1;
	// }else{
	// station=(x-x1)/Math.cos(theta);
	// }
	// }//xyToStation

	// /**
	// * Convert station (xsect point horizontal coordinate in relative coord.
	// sys)
	// * to UTM, using x1, x2 as origin and x2, y2 as other point in
	// cross-section
	// * line. Used for 3D network output.
	// */
	// public static float stationToX(float x1, float x2, float y1, float y2,
	// float station){
	// //use imaginary horizontal line with same y value as first point to find
	// theta
	// float theta = getTheta(x1, x2, y1, y2);
	// return x1 + station*(float)Math.cos(theta);
	// }//stationToX

	// /**
	// * Convert station (xsect point horizontal coordinate in relative coord.
	// sys)
	// * to UTM, using x1, x2 as origin and x2, y2 as other point in
	// cross-section
	// * line. Used for 3D network output.
	// */
	// public static float stationToY(float x1, float x2, float y1, float y2,
	// float station){
	// //use imaginary horizontal line with same y value as first point to find
	// theta
	// float theta = getTheta(x1, x2, y1, y2);
	// if(DEBUG){
	// System.out.println("got theta. x1,x2,y1,y2,station="+x1+","+x2+","+
	// ","+y1+","+y2+","+station);
	// System.out.println("theta="+theta);
	// }
	// return y1 + station*(float)Math.sin(theta);
	// }//stationToY

	/*
	 * For creating a new cross-section: given centerline and x and y dataCoordinates (point that user clicked on),
	 * determine the distance along the centerline and the distance from the centerline to the point that the user clicked on.
	 *
	 * This is also used for determining channel/distance for landmarks (output locations)
	 * If distance is such that the xsect line exceeds the maxXsectLineLength, return negative values.
	 * 
	 *  For landmark operations, limitWidth should be true; otherwise, false.
	 */
	public static double[] getXsectDistAndPointDist(Centerline centerline, double xDataCoord, double yDataCoord, 
			double maxXsectLineLength, boolean limitWidth) {
		double[] returnValues = new double[] {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
		double x1 = 0.0;
		double y1 = 0.0;
		double x2 = 0.0;
		double y2 = 0.0;
		double dist = 0.0;
		double theta = 0.0;
		double xi = 0.0;
		double yi = 0.0;
		double cumDist = 0.0;
		double minDist = Double.MAX_VALUE;
		int minDistIndex = Integer.MAX_VALUE;

		// loop through all centerline segments; find minimum perpendicular
		// distance
		// and index of first point of line segment that has minimum
		// perpendicular dist
		for (int i = 0; i <= centerline.getNumCenterlinePoints() - 2; i++) {
			x1 = centerline.getCenterlinePoint(i).getXFeet();
			y1 = centerline.getCenterlinePoint(i).getYFeet();
			x2 = centerline.getCenterlinePoint(i + 1).getXFeet();
			y2 = centerline.getCenterlinePoint(i + 1).getYFeet();
//			if(Double.isNaN(maxXsectLineLength)) {
//				dist = CsdpFunctions.shortestDistLineSegment(x1, x2, xDataCoord, y1, y2, yDataCoord);
//			}else {
				dist = CsdpFunctions.shortestDistLineSegment(x1, x2, xDataCoord, y1, y2, yDataCoord, maxXsectLineLength, limitWidth);
//			}
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
			returnValues=new double[] {cumDist, minDist, xi, yi};
		}
		return returnValues;
	}//getXsectDistAndPointDist
	
	/**
	 * find x coordinate of intersection of 2 perpendicular lines. 1st line is
	 * defined by (x1,y2) and (x2,y2) and the second line is defined by (x3,y3)
	 * and the intersection.
	 */
	public static double findXIntersection(double x1, double x2, double x3, double y1, double y2, double y3) {
		double xIntersect;
		if (x1 == x2)
			xIntersect = x1;
		else {
			double slope1 = ((y1 - y2) / (x1 - x2));
			xIntersect = (x3 + slope1 * (y3 - y1 + slope1 * x1)) / (slope1 * slope1 + 1);
		}
		return xIntersect;
	}// findXIntersection

	/**
	 * find y coordinate of intersection of 2 perpendicular lines. 1st line is
	 * defined by (x1,y2) and (x2,y2) and the second line is defined by (x3,y3)
	 * and the intersection
	 */
	public static double findYIntersection(double x1, double x2, double x3, double y1, double y2, double y3) {
		double xIntersect;
		double yIntersect;
		if (x1 == x2)
			yIntersect = y3;
		else {
			double slope1 = ((y1 - y2) / (x1 - x2));
			xIntersect = (x3 + slope1 * (y3 - y1 + slope1 * x1)) / (slope1 * slope1 + 1);
			yIntersect = slope1 * (xIntersect - x1) + y1;
		}
		return yIntersect;
	}// findYIntersection

	/**
	 * find the shortest distance between a line(infinite length) and a point.
	 * Points 1 and 2 are the line points, point 3 is the point.
	 */
	public static double shortestDistLine(double x1, double x2, double x3, double y1, double y2, double y3) {
		double xIntersect = findXIntersection(x1, x2, x3, y1, y2, y3);
		double yIntersect = findYIntersection(x1, x2, x3, y1, y2, y3);
		return (double) (Math.sqrt(Math.pow((xIntersect - x3), 2) + Math.pow((yIntersect - y3), 2)));
	}// shortestDistLine
	
	/**
	 * Find the shortest distance between a line segment (finite length) and a
	 * point. Return maximum value if angle between line segment and line
	 * connecting the point and either end of the line segment is > 90 degrees.
	 * Points 1 and 2 are the line segment endpoints, point 3 is the point.
	 * polygonWidth is the width of the rectangular polygon which will be used
	 * to see if the selected point is close enough to the line segment.
	 */
	public static double shortestDistLineSegment(double x1, double x2, double x3, double y1, double y2, double y3) {
		double dist;
		double width;
		double xIntersect = findXIntersection(x1, x2, x3, y1, y2, y3);
		double yIntersect = findYIntersection(x1, x2, x3, y1, y2, y3);
		double theta = getTheta(x1, x2, y1, y2);

		if (DEBUG)
			System.out.println("shortestDistLineSegment x1x2x3y1y2y3=" + x1 + "," + x2 + "," + x3 + "," + y1 + "," + y2
					+ "," + y3);

		// the polygon is a square region. the length of a side is equal to the
		// length
		// of the centerline segment
		width = pointDist(x1, y1, x2, y2);
		Polygon p = findPolygon(x1, x2, y1, y2, width);
		for (int i = 0; i <= 3; i++) {
			if (DEBUG)
				System.out.println("polygon coordinates: point " + i + ": x,y=" + p.xpoints[i] + "," + p.ypoints[i]);
		}
		if (p.contains((int) x3, (int) y3)) {
			dist = (double) (Math.sqrt(Math.pow((xIntersect - x3), 2) + Math.pow((yIntersect - y3), 2)));
		} else {
			dist = Double.MAX_VALUE;
		}
		return dist;
	}// shortestDistLineSegment

	/**
	 * find the shortest distance between a line segment (finite length) and a
	 * point. Return maximum value if angle between line segment and line
	 * connecting the point and either end of the line segment is > 90 degrees.
	 * Points 1 and 2 are the line segment endpoints, point 3 is the point.
	 * polygonWidth is the width of the rectangular polygon which will be used
	 * to see if the selected point is close enough to the line segment.
	 * 
	 * limitWidth should be true for landmark to chan/dist operations, false otherwise.
	 */
	public static double shortestDistLineSegment(double x1, double x2, double x3, double y1, double y2, double y3,
			double width, boolean limitWidth) {
		//adjust width if too large. This is necessary because the findPolygon won't make a good polygon if you give it a 
		//value that is too large. The value will get converted to an int, so it really can't be very big
		//This is necessary for finding chan/dist corresponding to a landmark. Sometimes big values for width get passed in,
		//and since polygons use int values, the double value could be too large to convert to an int, and the result is a useless polygon.
		if(limitWidth) {
			if(width>1.0E5) {
				width=1.0E5;
			}
		}
		double dist = 0.0;
		double xIntersect = findXIntersection(x1, x2, x3, y1, y2, y3);
		double yIntersect = findYIntersection(x1, x2, x3, y1, y2, y3);
//		double theta = getTheta(x1, x2, y1, y2);
		if (DEBUG)
			System.out.println("shortestDistLineSegment x1x2x3y1y2y3=" + x1 + "," + x2 + "," + x3 + "," + y1 + "," + y2
					+ "," + y3);

		// the polygon is a square region. the length of a side is equal to the
		// length
		// of the centerline segment
		Polygon p = findPolygon(x1, x2, y1, y2, width);
		if (DEBUG) {
			for (int i = 0; i <= 3; i++) {
				System.out.println("polygon coordinates: point " + i + ": x,y=" + p.xpoints[i] + "," + p.ypoints[i]);
			}
		}
		if (p.contains((int) x3, (int) y3)) {
			dist = (double) (Math.sqrt(Math.pow((xIntersect - x3), 2) + Math.pow((yIntersect - y3), 2)));
		} else {
			dist = Double.MAX_VALUE;
		}
		return dist;
	}// shortestDistLineSegment

	/**
	 * convert feet to meters
	 */
	public static double feetToMeters(double value) {
		return value * 0.3048;
	}

	/**
	 * convert meters to feet
	 */
	public static double metersToFeet(double value) {
		return value / 0.3048;
	}

	// public static AffineTransform _at = new AffineTransform();
	// public static AffineTransform _ati = new AffineTransform();
	// public static Point2D.Float _p2d = new Point2D.Float();
	// public static Point2D _p2dDest=null;
	// public static int[] _destInt = new int[2];
	// public static float[] _destFloat = new float[2];

	// /**
	// * Update affine transform and its inverse.
	// * always call when minSlope changes or height of canvas changes.
	// */
	// public static void updateAT(float minSlope, float height){
	// //reflect about x axis
	// _at.setToScale(1,-1);
	// //translate to move to display region
	// _at.translate(1,-height);
	// //scale with minSlope
	// _at.scale(minSlope,minSlope);

	// try{
	// _ati=_at.createInverse();
	// }catch(java.awt.geom.NoninvertibleTransformException e){
	// System.out.println("ERROR in CsdpFunctions.updateAT: "+e);
	// }

	// // System.out.println("updating affine transforms with minSlope,
	// height="+minSlope+","+height);
	// double[] m1=new double[6];
	// _at.getMatrix(m1);
	// double[] m2=new double[6];
	// _ati.getMatrix(m2);
	// }

	// /*
	// * convert pixels to length.
	// */
	// public static float[] pixelsToLength(int xPixels, int yPixels, float
	// minX, float minY){
	// _p2d.setLocation((double)xPixels,(double)yPixels);
	// _p2dDest = _ati.transform(_p2d,_p2dDest);

	// _destFloat[0] = (float)_p2dDest.getX()+minX;
	// _destFloat[1] = (float)_p2dDest.getY()+minY;

	// return _destFloat;
	// }

	// /**
	// * convert utm to pixels. consecutive calls will replace previous
	// calculated values.
	// */
	// public static int[] utmToPixels(float x, float y, float minX, float
	// minY){
	// _p2d.setLocation(x-minX,y-minY);
	// _p2dDest = _at.transform(_p2d,_p2dDest);
	// _destInt[0] = (int)_p2dDest.getX();
	// _destInt[1] = (int)_p2dDest.getY();
	// return _destInt;
	// }

	/**
	 * calculate angle in radians of centerline segment with respect to another.
	 * The angle is measured from the line defined by points 1 and 2 to the line
	 * defined by points 1 and 3. Angles are measured in the counter clockwise
	 * direction.
	 */
	public static double getTheta(double x1, double x2, double x3, double y1, double y2, double y3) {

		double theta = 0.0;
		double theta2 = 0.0;
		double theta3 = 0.0;

		theta3 = getTheta(x1, x3, y1, y3);
		theta2 = getTheta(x1, x2, y1, y2);
		theta = theta2 - theta3;

		return theta;
	}// getTheta

	/**
	 * Calculates the angle of a line in radians defined by points 1 and 2, with
	 * point 1 at the origin. The atan function alone is not sufficient because
	 * it only returns values in the range of -pi/2 < theta < pi/2.
	 */
	public static double getTheta(double x1, double x2, double y1, double y2) {
		double theta = 0.0;
		double slope = 0.0;
		double pi = (double) Math.PI;

		if (x2 == x1) {

			if (y2 > y1)
				theta = pi / 2.0;
			if (y2 < y1)
				theta = 3.0f * pi / 2.0;
		} else {
			slope = (y2 - y1) / (x2 - x1);
			if (DEBUG)
				System.out.println("tan(theta),x1,y1,x2,y2=" + slope + " " + x1 + "," + y1 + " " + x2 + "," + y2);
			theta = (double) Math.atan(slope);
			// if point 2 is to the left of point1, then it's in the 2nd or 3rd
			// quadrants
			// and must add pi radians to angle
			if (x2 < x1)
				theta += pi;
		}
		return theta;
	}// getTheta

	/**
	 * find rectangular polygon which is defined by a line segment a width, and
	 * a length. The line segment defines the length of the rectangle; the width
	 * defines the width.
	 */
	public static Polygon findPolygon(double x1, double x2, double y1, double y2, double distAlong, double thickness,
			double width) {
		if (DEBUG)
			System.out.println("in the first method: x1x2y1y2, distalong, thickness=" + x1 + "," + x2 + "," + y1 + ","
					+ y2 + " " + distAlong + "," + thickness);

		double theta = getTheta(x1, x2, y1, y2);
		double xRectangleCenter = x1 + distAlong * (double) Math.cos(theta);
		double yRectangleCenter = y1 + distAlong * (double) Math.sin(theta);
		double xFirstPoint = xRectangleCenter - 0.5f * thickness * (double) Math.cos(theta);
		double yFirstPoint = yRectangleCenter - 0.5f * thickness * (double) Math.sin(theta);
		double xSecondPoint = xRectangleCenter + 0.5f * thickness * (double) Math.cos(theta);
		double ySecondPoint = yRectangleCenter + 0.5f * thickness * (double) Math.sin(theta);
		if (DEBUG)
			System.out.println("theta,x1x2y1y2=" + theta + " " + x1 + "," + x2 + "," + y1 + "," + y2);
		if (DEBUG)
			System.out.println("firstpt x,y secondpt x,y=" + xFirstPoint + "," + yFirstPoint + "," + xSecondPoint + ","
					+ ySecondPoint);

		return findPolygon(xFirstPoint, xSecondPoint, yFirstPoint, ySecondPoint, width);
	}// findPolygon

	/**
	 * find rectangular polygon which is defined by a line segment and a width.
	 * The line segment defines the length of the rectangle; the width defines
	 * the width.
	 */
	public static Polygon findPolygon(double x1, double x2, double y1, double y2, double width) {
		double rectangleLength = pointDist(x1, y1, x2, y2);
		double theta = getTheta(x1, x2, y1, y2);

		if (DEBUG)
			System.out.println("in the second method:");
		if (DEBUG)
			System.out.println("theta=" + theta);
		if (DEBUG)
			System.out.println("x1x2y1y2=" + x1 + "," + x2 + " " + y1 + "," + y2);
		/*
		 * angle of line connecting center of polygon and upper right vertex
		 */
		double thetaUpperRightVertex = 0.0;
		/*
		 * angle of line connecting center of polygon and upper left vertex
		 */
		double thetaLowerRightVertex = 0.0;
		double distToUpperRightVertex = 0.0;
		double distToLowerRightVertex = 0.0;

		/*
		 * coordinates of vertices of rectangular polygon; measured relative to
		 * coordinate system that is rotated and translated such that middle of
		 * one end of the rectangle is at (0,0) and the middle of the other end
		 * is on the positive y axis. indices: 0 = lower left, 1 = upper left, 2
		 * = upper right, 3 = lower right
		 */
		double[] vertexXRel = new double[4];
		double[] vertexYRel = new double[4];
		/*
		 * coordinates of vertices of rectangular polygon.
		 */
		double[] vertexX = new double[4];
		double[] vertexY = new double[4];
		int[] vertexXInt = new int[4];
		int[] vertexYInt = new int[4];

		// find vertex points
		vertexXRel[0] = -0.5f * width;
		vertexXRel[1] = -0.5f * width;
		vertexXRel[2] = 0.5f * width;
		vertexXRel[3] = 0.5f * width;
		vertexYRel[0] = 0.0;
		vertexYRel[1] = rectangleLength;
		vertexYRel[2] = rectangleLength;
		vertexYRel[3] = 0.0;
		if (DEBUG)
			System.out.println("Relative X Vertices:" + vertexXRel[0] + " " + vertexXRel[1] + " " + vertexXRel[2] + " "
					+ vertexXRel[3]);
		if (DEBUG)
			System.out.println("Relative Y Vertices:" + vertexYRel[0] + " " + vertexYRel[1] + " " + vertexYRel[2] + " "
					+ vertexYRel[3]);
		Polygon p = null;

		thetaUpperRightVertex = getTheta(0.0f, vertexXRel[2], 0.0f, vertexYRel[2]);
		distToUpperRightVertex = pointDist(0.0f, 0.0f, vertexXRel[2], vertexYRel[2]);
		thetaLowerRightVertex = getTheta(0.0f, vertexXRel[3], 0.0f, vertexYRel[3]);
		distToLowerRightVertex = pointDist(0.0f, 0.0f, vertexXRel[3], vertexYRel[3]);
		double thetaLowerLeftVertex = getTheta(0.0f, vertexXRel[0], 0.0f, vertexYRel[0]);
		double thetaUpperLeftVertex = getTheta(0.0f, vertexXRel[1], 0.0f, vertexYRel[1]);

		if (DEBUG)
			System.out.println("theta upper,lower=" + thetaUpperRightVertex + "," + thetaLowerRightVertex);
		if (DEBUG)
			System.out.println("dist to upper, lower=" + distToUpperRightVertex + "," + distToLowerRightVertex);
		// rotate
		if (DEBUG)
			System.out.println("after rotation, before translation");
		if (DEBUG)
			System.out.println(
					"theta, thetalrv, thetaurv=" + theta + "," + thetaLowerRightVertex + "," + thetaUpperRightVertex);

		/*
		 * theta is the angle of the rotated y axis (of the local coordinate
		 * sys) wrt the x axis of the global coord. sys. theta-pi/2 is the angle
		 * of the rotated x axis wrt the x axis of the global coord sys.
		 */

		vertexX[0] = distToLowerRightVertex * (double) Math.cos(theta + thetaLowerLeftVertex - Math.PI / 2.0f);
		vertexX[1] = distToUpperRightVertex * (double) Math.cos(theta + thetaUpperLeftVertex - Math.PI / 2.0f);
		vertexX[2] = distToUpperRightVertex * (double) Math.cos(theta + thetaUpperRightVertex - Math.PI / 2.0f);
		vertexX[3] = distToLowerRightVertex * (double) Math.cos(theta + thetaLowerRightVertex - Math.PI / 2.0f);

		vertexY[0] = distToLowerRightVertex * (double) Math.sin(theta + thetaLowerLeftVertex - Math.PI / 2.0f);
		vertexY[1] = distToUpperRightVertex * (double) Math.sin(theta + thetaUpperLeftVertex - Math.PI / 2.0f);
		vertexY[2] = distToUpperRightVertex * (double) Math.sin(theta + thetaUpperRightVertex - Math.PI / 2.0f);
		vertexY[3] = distToLowerRightVertex * (double) Math.sin(theta + thetaLowerRightVertex - Math.PI / 2.0f);
		if (DEBUG)
			System.out.println("vertexX =" + vertexX[0] + " " + vertexX[1] + " " + vertexX[2] + " " + vertexX[3]);
		if (DEBUG)
			System.out.println("vertexY =" + vertexY[0] + " " + vertexY[1] + " " + vertexY[2] + " " + vertexY[3]);

		// translate
		vertexX[0] += x1;
		vertexX[1] += x1;
		vertexX[2] += x1;
		vertexX[3] += x1;

		vertexY[0] += y1;
		vertexY[1] += y1;
		vertexY[2] += y1;
		vertexY[3] += y1;

		for (int i = 0; i <= 3; i++) {
			vertexXInt[i] = (int) vertexX[i];
			vertexYInt[i] = (int) vertexY[i];
			_polygon.addPoint(vertexXInt[i], vertexYInt[i]);
		}
		_polygon = new Polygon(vertexXInt, vertexYInt, 4);
		return _polygon;
	}// findPolygon

	/**
	 * Interpolation search
	 */
	public static int interpolationSearch(double[] array, int n, int arraySize) {
		int low = 0;
		int high = arraySize - 1;
		int mid = 0;
		// System.out.println("about to start interpolation search:");
		// System.out.println("n,array.get(low),array.get(high)="+n+","+
		// array.get(low)+","+array.get(high));
		while (n >= array[low] && n <= array[high]) {
			// System.out.println("interpolationSearch: searching...");
			mid = low + (int) Math.abs(Math.floor((n - array[low]) * (high - low) / (array[high] - array[low])));
			if (n == array[mid]) {
				return mid;
			} else {
				if (n < array[mid]) {
					high = mid - 1;
				} else {
					low = mid + 1;
				}
			}
		}
		return mid;
	}

	/**
	 * quicksort. left is the index of the first element in the array; right is
	 * the index of the last
	 */
	public static double[] qsort(double[] array, int left, int right) {
		int last = 0;
		double ran = 0.0;
		if (left < right) {
			ran = Math.random();
			swap(array, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (array[i] < array[left]) {
					last++;
					swap(array, last, i);
				} // if
			} // for i
			swap(array, left, last);
			qsort(array, left, last - 1);
			qsort(array, last + 1, right);
		} // if
		return array;
	}// qsort

	/**
	 * swap two double values in array. used by quicksort
	 */
	private static void swap(double[] array, int i, int j) {
		double t = array[i];
		array[i] = array[j];
		array[j] = t;
	}

	/**
	 * quicksort. left is the index of the first element in the array; right is
	 * the index of the last
	 */
	public static Integer[] qsort(Integer[] array, int left, int right) {
		int last = 0;
		double ran = 0.0;
		if (left < right) {
			ran = Math.random();
			swap(array, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (array[i].intValue() < array[left].intValue()) {
					last++;
					swap(array, last, i);
				} // if
			} // for i
			swap(array, left, last);
			qsort(array, left, last - 1);
			qsort(array, last + 1, right);
		} // if
		return array;
	}// qsort

	/**
	 * swap two double values in array. used by quicksort
	 */
	private static void swap(Integer[] array, int i, int j) {
		Integer t = array[i];
		array[i] = array[j];
		array[j] = t;
	}

	/**
	 * quicksort for vectors. left is the index of the first element in the
	 * array; right is the index of the last
	 */
	public static Vector qsort(Vector xsects, int left, int right) {
		int last = 0;
		double ran = 0.0;
		if (left < right) {
			ran = Math.random();
			swap(xsects, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (((Xsect) (xsects.elementAt(i))).getDistAlongCenterlineFeet() < ((Xsect) (xsects.elementAt(left)))
						.getDistAlongCenterlineFeet()) {
					// if(array[i] < array[left]){
					last++;
					swap(xsects, last, i);
				} // if
			} // for i
			swap(xsects, left, last);
			qsort(xsects, left, last - 1);
			qsort(xsects, last + 1, right);
		} // if
		return xsects;
	}// qsort

	/**
	 * swap two double values in array. used by quicksort
	 */
	private static void swap(Vector array, int i, int j) {
		Xsect t = (Xsect) (array.elementAt(i));
		array.setElementAt(array.elementAt(j), i);
		array.setElementAt(t, j);

		// double t = array[i];
		// array[i] = array[j];
		// array[j] = t;
	}


	public static void qsort(double[] array1, double[] array2, int left, int right) {
		int last = 0;
		double ran = 0.0;

		int iValue = -Integer.MAX_VALUE;
		int leftValue = -Integer.MAX_VALUE;

		if (left < right) {
			ran = Math.random();
			swap(array1, array2, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if(array1[i] < array1[left]){
				// if(array[i] < array[left]){
				// then swap if the ith value is less then the left value
//				if (iValue < leftValue) {
					last++;
					swap(array1, array2, last, i);
				} // if
					// else if the ith value is a number but the left isn't

			} // for i
			swap(array1, array2, left, last);
			qsort(array1, array2, left, last - 1);
			qsort(array1, array2, last + 1, right);
		} // if
	}// qsort
	
	/**
	 * swap two doubles in array1 and array2. used by quicksort
	 */
	private static void swap(double[] array1, double[] array2, int i, int j) {
		double t1 = array1[i];
		double t2 = array2[i];
		array1[i]= array1[j];
		array1[j]= t1;
		array2[i]=array2[j];
		array2[j]=t2;
	}

	
	
	/**
	 * quicksort for Strings. left is the index of the first element in array;
	 * right is the index of the last.
	 */
	public static ResizableStringArray qsort(ResizableStringArray array, int left, int right) {
		int last = 0;
		double ran = 0.0;

		int iValue = -Integer.MAX_VALUE;
		int leftValue = -Integer.MAX_VALUE;

		if (left < right) {
			ran = Math.random();
			swap(array, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				// if(array[i] < array[left]){
				boolean iValueParsable = true;
				boolean leftValueParsable = true;
				try {
					iValue = Integer.parseInt(array.get(i), 10);
				} catch (NumberFormatException e) {
					iValueParsable = false;
					if (DEBUG)
						System.out.println("iValue not parsable:  " + array.get(i));
				}
				try {
					leftValue = Integer.parseInt(array.get(left), 10);
				} catch (NumberFormatException e) {
					leftValueParsable = false;
					if (DEBUG)
						System.out.println("leftValue not parsable:  " + array.get(left));
				}
				// if both values are numbers
				if (iValueParsable && leftValueParsable) {
					// then swap if the ith value is less then the left value
					if (iValue < leftValue) {
						last++;
						swap(array, last, i);
					} // if
						// else if the ith value is a number but the left isn't
				} else if (iValueParsable && leftValueParsable == false) {
					last++;
					swap(array, last, i);
					// else if they're both not numbers
				} else if (iValueParsable == false && leftValueParsable == false) {
					// then swap if ith value is lex. less then left value
					if (array.get(i).compareTo(array.get(left)) < 0) {
						last++;
						swap(array, last, i);
					}
					// else if ith value is not number and left value is
				} else if (iValueParsable == false && leftValueParsable) {
					// do nothing, because numbers should go before text
				}

			} // for i
			swap(array, left, last);
			qsort(array, left, last - 1);
			qsort(array, last + 1, right);
		} // if
		return array;
	}// qsort

	/**
	 * swap two Strings in array. used by quicksort
	 */
	private static void swap(ResizableStringArray array, int i, int j) {
		String t = array.get(i);
		// array.get(i) = array.get(j);
		// array.get(j) = t;
		array.put(i, array.get(j));
		array.put(j, t);
	}

	/**
	 * rounds value off (or truncates?) to two decimal places
	 */
	public static double twoPlaces(double f) {
		int i = (int) (f * 100.0f);
		return ((double) (i)) / 100.0;
	}// twoPlaces

	/**
	 * converts a 2 digit hex value to integer
	 */
	public static int hexToInt(char h1, char h2) {
		int hBase = 16;
		int h1int = 0;
		int h2int = 0;
		if (h2 == '0')
			h2int = 0;
		else if (h2 == '1')
			h2int = 1;
		else if (h2 == '2')
			h2int = 2;
		else if (h2 == '3')
			h2int = 3;
		else if (h2 == '4')
			h2int = 4;
		else if (h2 == '5')
			h2int = 5;
		else if (h2 == '6')
			h2int = 6;
		else if (h2 == '7')
			h2int = 7;
		else if (h2 == '8')
			h2int = 8;
		else if (h2 == '9')
			h2int = 9;
		else if (h2 == 'A')
			h2int = 10;
		else if (h2 == 'B')
			h2int = 11;
		else if (h2 == 'C')
			h2int = 12;
		else if (h2 == 'D')
			h2int = 13;
		else if (h2 == 'E')
			h2int = 14;
		else if (h2 == 'F')
			h2int = 15;

		if (h1 == '0')
			h1int = 0 * hBase;
		else if (h1 == '1')
			h1int = 1 * hBase;
		else if (h1 == '2')
			h1int = 2 * hBase;
		else if (h1 == '3')
			h1int = 3 * hBase;
		else if (h1 == '4')
			h1int = 4 * hBase;
		else if (h1 == '5')
			h1int = 5 * hBase;
		else if (h1 == '6')
			h1int = 6 * hBase;
		else if (h1 == '7')
			h1int = 7 * hBase;
		else if (h1 == '8')
			h1int = 8 * hBase;
		else if (h1 == '9')
			h1int = 9 * hBase;
		else if (h1 == 'A')
			h1int = 10 * hBase;
		else if (h1 == 'B')
			h1int = 11 * hBase;
		else if (h1 == 'C')
			h1int = 12 * hBase;
		else if (h1 == 'D')
			h1int = 13 * hBase;
		else if (h1 == 'E')
			h1int = 14 * hBase;
		else if (h1 == 'F')
			h1int = 15 * hBase;

		return h1int + h2int;
	}// hexToInt

	/**
	 * interpolate between 2 points to find x value for given y value (y3)
	 */
	public static double interpX(double x1, double x2, double y1, double y2, double y) {
		return -((y2 - y) * ((x2 - x1) / (y2 - y1)) - x2);
	}

	/**
	 * interpolate between 2 points to find y value for given x value (x3)
	 */
	public static double interpY(double x1, double x2, double y1, double y2, double x) {
		return (x - x1) * ((y2 - y1) / (x2 - x1)) + y1;
	}

	/**
	 * returns version number
	 */
	public static String getVersion() {
		return _version;
	}

	/**
	 * get name of current bathymetry file directory. _bathymtryDirectory stores
	 * the name of the last directory accessed.
	 */
	public static File getBathymetryDirectory() {
		return _bathymetryDirectory;
	}

	/**
	 * get name of current landmark directory. _landmarkDirectory stores the
	 * name of the last directory accessed.
	 */
	public static File getLandmarkDirectory() {
		return _landmarkDirectory;
	}

	/**
	 * get name of current network directory. _networkDirectory stores the name
	 * of the last directory accessed.
	 */
	public static File getNetworkDirectory() {
		return _networkDirectory;
	}

	/**
	 * get name of current digital line graph directory.
	 * _digitalLineGraphDirectory store the name of the last directory accessed.
	 */
	public static File getDigitalLineGraphDirectory() {
		return _digitalLineGraphDirectory;
	}

	/**
	 * get name of current network export directory. _networkExportDirectory
	 * stores the name of the last directory accessed.
	 */
	public static File getNetworkExportDirectory() {
		return _networkExportDirectory;
	}

	/**
	 * get name of current properties directory. _propertiesDirectory stores the
	 * name of the last directory accessed.
	 */
	public static File getPropertiesDirectory() {
		return _propertiesDirectory;
	}

	/**
	 * get name of current open water area calc directory.
	 * _openWaterAreaDirectory stores the name of the last directory accessed.
	 */
	public static File getOpenWaterAreaDirectory() {
		return _openWaterAreaDirectory;
	}

	/**
	 * returns the last directory accessed when opening a file
	 */
	public static File getOpenDirectory() {
		return _openDirectory;
	}

	/**
	 * returns name of current DigitalLineGraph file. _digitalLineGraphFilename
	 * stores the name of the last dlg filename accessed.
	 */
	public static String getDigitalLineGraphFilename() {
		return _digitalLineGraphFilename;
	}

	/**
	 * stores name of current bathymetry directory. _bathymetry directory stores
	 * the name of the last directory accessed.
	 */
	public static void setBathymetryDirectory(File d) {
		_bathymetryDirectory = d;
	}

	public static void setDSMChannelsDirectory(String d) {
		_dsmChannelsDirectory = new File(d);
	}
	public static void setNetworkCalculateDirectory(File directory) {
		_networkCalculateDirectory = directory;
	}
	public static File getNetworkCalculateDirectory() {
		return _networkCalculateDirectory;
	}
	
	public static File getDSMChannelsDirectory() {
		return _dsmChannelsDirectory;
	}
	
	public static void setDSMChannelsFilename(String filename) {
		_dsmChannelsFilename = filename;
	}
	
	public static void setDSMChannelsFiletype(String filetype) {
		_DSMChannelsFiletype = filetype;
	}
	
	public static String getDSMChannelsFilename() {
		return _dsmChannelsFilename;
	}
	
	public static void setDSM2HofDirectory(File d) {
		_dsm2HofDirectory = d;
	}

	public static void setDSM2HofFilename(String f) {
		_dsm2HofFilename = f;
	}
	
	public static File getDSM2HofDirectory() {
		return _dsm2HofDirectory;
	}
	
	public static String getDSM2HofFilename() {
		return _dsm2HofFilename;
	}
	
	/**
	 * store name of current digital line graph directory.
	 * _digitalLineGraphDirectory store the name of the last directory accessed.
	 */
	public static void setDigitalLineGraphDirectory(File name) {
		_digitalLineGraphDirectory = name;
	}

	/**
	 * stores name of current landmark directory. _landmarkDirectory stores the
	 * name of the last directory accessed.
	 */
	public static void setLandmarkDirectory(File d) {
		_landmarkDirectory = d;
	}

	/**
	 * stores name of current network directory. _networkDirectory stores the
	 * name of the last directory accessed.
	 */
	public static void setNetworkDirectory(File d) {
		_networkDirectory = d;
	}

	/**
	 * stores name of current network export directory. _networkExportDirectory
	 * stores the name of the last directory accessed.
	 */
	public static void setNetworkExportDirectory(File d) {
		_networkExportDirectory = d;
	}

	/**
	 * stores name of current properties directory. _propertiesDirectory stores
	 * the name of the last directory accessed.
	 */
	public static void setPropertiesDirectory(File d) {
		_propertiesDirectory = d;
	}

	/**
	 * stores name of current open water area calc directory.
	 * _openWaterAreaDirectory stores the name of the last directory accessed.
	 */
	public static void setOpenWaterAreaDirectory(File d) {
		_openWaterAreaDirectory = d;
	}

	/**
	 * sets the last directory accessed when opening a file
	 */
	public static void setOpenDirectory(File dir) {
		_openDirectory = dir;
	}

	/**
	 * stores name of current bathymetry directory. _bathymetry directory stores
	 * the name of the last directory accessed.
	 */
	public static void setBathymetryDirectory(String d) {
		_bathymetryDirectory = new File(d);
	}

	/**
	 * store name of current digital line graph directory.
	 * _digitalLineGraphDirectory store the name of the last directory accessed.
	 */
	public static void setDigitalLineGraphDirectory(String name) {
		_digitalLineGraphDirectory = new File(name);
	}

	/**
	 * stores name of current landmark directory. _landmarkDirectory stores the
	 * name of the last directory accessed.
	 */
	public static void setLandmarkDirectory(String d) {
		_landmarkDirectory = new File(d);
	}

	/**
	 * stores name of current network directory. _networkDirectory stores the
	 * name of the last directory accessed.
	 */
	public static void setNetworkDirectory(String d) {
		_networkDirectory = new File(d);
	}

	/**
	 * stores name of current network export directory. _networkExportDirectory
	 * stores the name of the last directory accessed.
	 */
	public static void setNetworkExportDirectory(String d) {
		_networkExportDirectory = new File(d);
	}

	/**
	 * stores name of current properties directory. _propertiesDirectory stores
	 * the name of the last directory accessed.
	 */
	public static void setPropertiesDirectory(String d) {
		_propertiesDirectory = new File(d);
	}

	/**
	 * stores name of current open water area calc directory.
	 * _openWaterAreaDirectory stores the name of the last directory accessed.
	 */
	public static void setOpenWaterAreaDirectory(String d) {
		_openWaterAreaDirectory = new File(d);
	}

	/**
	 * sets the last directory accessed when opening a file
	 */
	public static void setOpenDirectory(String dir) {
		_openDirectory = new File(dir);
	}

	/**
	 * stores name of current DigitalLineGraph file. _digitalLineGraphFilename
	 * stores the name of the last directory accessed.
	 */
	public static void setDigitalLineGraphFilename(String d) {
		_digitalLineGraphFilename = d;
	}

	/**
	 * stores name of current DigitalLineGraph filetype
	 */
	public static void setDigitalLineGraphFiletype(String d) {
		_digitalLineGraphFiletype = d;
	}

	public static String getDigitalLineGraphFiletype() {
		return _digitalLineGraphFiletype;
	}

	public static void setNetworkFilename(String f) {
		_networkFilename = f;
	}

	public static String getNetworkFilename() {
		return _networkFilename;
	}

	public static void setNetworkFiletype(String f) {
		_networkFiletype = f;
	}

	public static String getNetworkFiletype() {
		return _networkFiletype;
	}
	
	public static String getLandmarkFilename() {
		return _landmarkFilename;
	}

	public static String getLandmarkFiletype() {
		return _landmarkFiletype;
	}

	public static void setLandmarkFiletype(String f) {
		_landmarkFiletype = f;
	}

	public static void setLandmarkFilename(String f) {
		_landmarkFilename = f;
	}

	/**
	 * returns name of properties file
	 */
	public static String getPropertiesFilename() {
		return _propertiesFilename;
	}

	/**
	 * sets name of properties file
	 */
	public static void setPropertiesFilename(String f) {
		_propertiesFilename = f;
	}

	/**
	 * stores properties filename extension
	 */
	public static String getPropertiesFiletype() {
		return _propertiesFiletype;
	}

	/**
	 * stores properties filename extension
	 */
	public static void setPropertiesFiletype(String f) {
		_propertiesFiletype = f;
	}

	/**
	 * returns name of openWaterArea file
	 */
	public static String getOpenWaterAreaFilename() {
		return _openWaterAreaFilename;
	}

	/**
	 * sets name of openWaterArea file
	 */
	public static void setOpenWaterAreaFilename(String f) {
		_openWaterAreaFilename = f;
	}

	/**
	 * stores openWaterArea filename extension
	 */
	public static String getOpenWaterAreaFiletype() {
		return _openWaterAreaFiletype;
	}

	/**
	 * stores openWaterArea filename extension
	 */
	public static void setOpenWaterAreaFiletype(String f) {
		_openWaterAreaFiletype = f;
	}

	/**
	 * quicksort
	 */
	public static ResizableShortArray qsort(int left, int right, ResizableShortArray a) {
		int last = 0;
		double ran = 0.0;
		if (left < right) {
			ran = Math.random();
			swap(a, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (a.get(i) < a.get(left)) {
					last++;
					swap(a, last, i);
				} // if
			} // for i
			swap(a, left, last);
			qsort(left, last - 1, a);
			qsort(last + 1, right, a);
		} // if
		return a;
	}// qsort

	/**
	 * swap two double values in array
	 */
	public static void swap(ResizableDoubleArray a, int i, int j) {
		double t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * swap two int values in array
	 */
	private static void swap(ResizableIntArray a, int i, int j) {
		int t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * swap two short values in array
	 */
	private static void swap(ResizableShortArray a, int i, int j) {
		short t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * converts double to a string with spaces at the end--length is spcified.
	 */
	public static String formattedOutputString(double f, int fieldWidth, boolean leftJustify) {
		// dont truncate exponent!
		String returnString = Double.toString(f);
		int length = returnString.length();
		String rjString = "";

		/*
		 * If the number has more digits then the specified length, then change
		 * the requested length to the number of digits.
		 */
		if (length > fieldWidth)
			fieldWidth = length;
		if (leftJustify) {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				returnString += " ";
			}
			returnString = returnString.substring(0, fieldWidth) + " ";
		} else {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				rjString += " ";
			}
			rjString += returnString;
			returnString = rjString;
			// System.out.println("returnString before = "+returnString);
			returnString = returnString.substring(returnString.length() - fieldWidth);
			// System.out.println("returnString after = "+returnString);
		}

		return returnString;
	}

	/**
	 * converts int to a string with spaces at the end--length is spcified.
	 */
	public static String formattedOutputString(int ii, int fieldWidth, boolean leftJustify) {
		// String returnString = (new Integer(ii)).toString();
		String returnString = Integer.toString(ii);
		int length = returnString.length();
		String rjString = "";

		if (length > fieldWidth)
			fieldWidth = length;
		if (leftJustify) {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				returnString += " ";
			}
			returnString = returnString.substring(0, fieldWidth) + " ";
		} else {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				rjString += " ";
			}
			rjString += returnString;
			returnString = rjString;
			// System.out.println("returnString before = "+returnString);
			// System.out.println("returnString.length(),
			// fieldwidth="+returnString.length()+","+fieldWidth);
			returnString = returnString.substring(returnString.length() - fieldWidth);
			// System.out.println("returnString after = "+returnString);
		}
		return returnString;
	}

	/**
	 * converts string to a string with spaces at the end--length is spcified.
	 */
	public static String formattedOutputString(String s, int fieldWidth, boolean leftJustify) {
		String returnString = s;
		int length = returnString.length();
		String rjString = "";

		if (length > fieldWidth)
			fieldWidth = length;
		if (leftJustify) {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				returnString += " ";
			}
			returnString = returnString.substring(0, fieldWidth) + " ";
		} else {
			for (int i = 0; i <= fieldWidth - 1; i++) {
				rjString += " ";
			}
			rjString += returnString;
			returnString = rjString;
			// System.out.println("returnString before = "+returnString);
			returnString = returnString.substring(returnString.length() - fieldWidth);
			// System.out.println("returnString after = "+returnString);
		}
		return returnString;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static void setEchoTimeSeriesInput(boolean b) {
		_echoTimeSeriesInput = b;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static boolean getEchoTimeSeriesInput() {
		return _echoTimeSeriesInput;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static void setEchoXsectInput(boolean b) {
		_echoXsectInput = b;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static boolean getEchoXsectInput() {
		return _echoXsectInput;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static void setEchoToeDrainInput(boolean b) {
		_echoToeDrainInput = b;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static boolean getEchoToeDrainInput() {
		return _echoToeDrainInput;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static void setPrintXsectResults(boolean b) {
		_printXsectResults = b;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static boolean getPrintXsectResults() {
		return _printXsectResults;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static boolean getChannelLengthsOnly() {
		return _channelLengthsOnly;
	}

	/**
	 * for open water area calc. (Yolo Bypass study)
	 */
	public static void setChannelLengthsOnly(boolean b) {
		_channelLengthsOnly = b;
	}

	/**
	 * For Yolo Bypass Calculations
	 */
	public static void setUseFremontWeir(boolean b) {
		_useFremontWeir = b;
	}

	/**
	 * For Yolo Bypass Calculations
	 */
	public static boolean getUseFremontWeir() {
		return _useFremontWeir;
	}

	/**
	 * returns elevation of top of Fremont Weir. Used only for Yolo Bypass
	 * calculations (see StationTimeSeriesData).
	 */
	public static double getFremontWeirElevation() {
		return _fremontWeirElevation;
	}

	/**
	 * For Yolo Bypass Calculations
	 */
	public static void setUseToeDrainRestriction(boolean b) {
		_useToeDrainRestriction = b;
	}

	/**
	 * For Yolo Bypass Calculations
	 */
	public static boolean getUseToeDrainRestriction() {
		return _useToeDrainRestriction;
	}

	/**
	 * returns index of last character that isn't a tab or a space
	 */
	public static int lastNonblank(String s) {
		int returnValue = 0;
		for (int i = 0; i <= s.length() - 1; i++) {
			if (s.charAt(i) != '\t' && s.charAt(i) != ' ')
				returnValue = i;
		}
		return returnValue;
	}

	/**
	 * returns index of last character that isn't the specified character
	 */
	public static int lastIndexOf(String s, char c) {
		int returnValue = 0;
		for (int i = 0; i <= s.length() - 1; i++) {
			if (s.charAt(i) != c)
				returnValue = i;
		}
		return returnValue;
	}

	/**
	 * replaces a string that occurs within a string with another string
	 */
	public static String replaceString(String theString, String oldString, String newString) {
		int lastIndex = Integer.MAX_VALUE;
		String returnString = "";
		String oldReturnString = "";

		int stringLength = theString.length();
		while (theString.indexOf(oldString) >= 0) {
			returnString += theString.substring(0, theString.indexOf(oldString));
			returnString += newString;
			theString = theString.substring(theString.indexOf(oldString) + oldString.length());

			if (DEBUG)
				System.out.println("theString, returnString=" + theString + "," + returnString);
		} // while the string still contains the delimiter (oldString)
		returnString += theString;

		// this doesn't work with multi-character delimiters! if they
		// ever make a tokenizer that will accept multi-character delimiters,
		// then this code could replace the while loop above
		// StringTokenizer t = new StringTokenizer(theString, oldString);
		// String token = null;

		// while(t.hasMoreTokens()){
		// token = t.nextToken();
		// returnString += token + newString;
		// }

		return returnString;
	}
	
	public static double getXsectThickness() {return _xsectThickness;}
	public static void setXsectThickness(double thickness) {_xsectThickness = thickness;}

	/*
	 * Use file chooser to get directory and filename, given a list of acceptable file extensions
	 * This is ok to use when you don't need to remember the directory for a given filetype
	 * if multipleSelection == true, multiple selection will be enabled. Otherwise, disabled.
	 */
	public static String[] selectFilePath(JFrame gui, String dialogTitle, String[] extensions, String startingDirectory,
			boolean multipleSelection) {
		String[] returnValues = null;
		String directory = null;
		JFileChooser jfcChannelsInp = new JFileChooser();
		int numChannelsInpExtensions = extensions.length;
		CsdpFileFilter csdpFileFilter = new CsdpFileFilter(extensions, numChannelsInpExtensions);
		
		jfcChannelsInp.setDialogTitle(dialogTitle);
		jfcChannelsInp.setApproveButtonText("Open");
		jfcChannelsInp.addChoosableFileFilter(csdpFileFilter);
		jfcChannelsInp.setFileFilter(csdpFileFilter);
		jfcChannelsInp.setMultiSelectionEnabled(multipleSelection);

		//set starting Directory
		File startingDirectoryFileObject = null;
		if(startingDirectory != null && startingDirectory.trim().length()>0) {
			try {
				startingDirectoryFileObject = new File(startingDirectory);
			}catch(Exception e) {
			}
		}
		if (startingDirectoryFileObject==null && CsdpFunctions.getOpenDirectory() != null) {
			startingDirectoryFileObject = CsdpFunctions.getOpenDirectory();
		}
		jfcChannelsInp.setCurrentDirectory(startingDirectoryFileObject);

		int filechooserState = jfcChannelsInp.showOpenDialog(gui);
		if (filechooserState == JFileChooser.APPROVE_OPTION) {
			directory = jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;
			if(multipleSelection) {
				File[] files = jfcChannelsInp.getSelectedFiles();
				returnValues = new String[1+files.length];
				returnValues[0] = directory;
				for(int i=1; i<files.length+1; i++) {
					returnValues[i]= jfcChannelsInp.getName(files[i-1]);
				}
			}else {
				String filename = jfcChannelsInp.getName(jfcChannelsInp.getSelectedFile());
				returnValues =  new String[] {directory, filename};
			}
		}
		CsdpFunctions.setOpenDirectory(jfcChannelsInp.getCurrentDirectory());
		return returnValues;
	}//getFilePath	
	
	public static String selectDirectory(JFrame gui, String dialogTitle) {
		String directory = null;
		JFileChooser jfcChannelsInp = new JFileChooser();
		
		jfcChannelsInp.setDialogTitle(dialogTitle);
		jfcChannelsInp.setApproveButtonText("Open");
		jfcChannelsInp.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (CsdpFunctions.getOpenDirectory() != null) {
			jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
		}
		int filechooserState = jfcChannelsInp.showOpenDialog(gui);
		if (filechooserState == JFileChooser.APPROVE_OPTION) {
			directory = jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;
			directory += jfcChannelsInp.getName(jfcChannelsInp.getSelectedFile()) + File.separator;

		}
		CsdpFunctions.setOpenDirectory(jfcChannelsInp.getCurrentDirectory());
		return directory;
	}//selectDirectory
	
	/*
	 * Create chart for multiple time series
	 * Important: if your dataset(s) have any isolated single points, you should make shapes=true, otherwise
	 * isolated single points will be invisible because there will be no line connecting it to another point.
	 */
	public static JFreeChart createChartWithXYPlot(CsdpFrame csdpFrame, String title, String xLabel, String yLabel, 
			XYSeriesCollection xySeriesCollection, boolean lines, boolean shapes, float[] lineThicknessArray) {
//		JFreeChart jFreeChart = ChartFactory.createXYLineChart(title, yLabel, xLabel, datasets[0], PlotOrientation.HORIZONTAL, legend, true, true);
		JFreeChart jFreeChart = ChartFactory.createXYLineChart(title, xLabel, yLabel, xySeriesCollection);
		XYPlot plot = jFreeChart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(lines, shapes);
		for(int i=0; i<xySeriesCollection.getSeriesCount(); i++) {
			renderer.setSeriesPaint(i, csdpFrame.getColor(i));
			renderer.setSeriesStroke(i, new BasicStroke(lineThicknessArray[i]));
		}
		plot.setRenderer(renderer);
		plot.setOutlinePaint(Color.BLACK);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		plot.setBackgroundPaint(Color.DARK_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		//seems to have no effect:
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		return jFreeChart;
	}//createChartWithXYPlot

	public static JFreeChart createChartWithScatterPlotLogY(CsdpFrame csdpFrame, String title, String xLabel, String yLabel, 
			XYSeriesCollection xySeriesCollection, boolean legend) {
		JFreeChart returnChart = createChartWithScatterPlot(csdpFrame, title, xLabel, yLabel, xySeriesCollection, legend);
		returnChart.getXYPlot().setRangeAxis(new LogarithmicAxis(yLabel));
		return returnChart;
	}
	/*
	 * Create chart for multiple time series
	 */
	public static JFreeChart createChartWithScatterPlot(CsdpFrame csdpFrame, String title, String xLabel, String yLabel, 
			XYSeriesCollection xySeriesCollection, boolean legend) {
		boolean lines = false;
		boolean shapes = true;
		JFreeChart jFreeChart = ChartFactory.createScatterPlot(title, yLabel, xLabel, xySeriesCollection, PlotOrientation.HORIZONTAL, legend, true, true);
		XYPlot plot = jFreeChart.getXYPlot();
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(lines, shapes);
		for(int i=0; i<xySeriesCollection.getSeriesCount(); i++) {
			renderer.setSeriesPaint(i,  csdpFrame.getColor(i));
		}
		plot.setRenderer(renderer);
		plot.setOutlinePaint(Color.BLACK);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		plot.setBackgroundPaint(Color.DARK_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		return jFreeChart;
	}//createChartWithScatterPlot
	
	/*
	 * Create a line chart with labels instead of numeric values on the x axis
	 */
	public static JFreeChart createLineChart(CsdpFrame csdpFrame, String title, String xLabel, String yLabel, 
			DefaultCategoryDataset defaultCategoryDataset, boolean drawPoints) {
		boolean lines = true;
		boolean shapes = drawPoints;		
		JFreeChart returnChart = ChartFactory.createLineChart(title, xLabel, yLabel, defaultCategoryDataset);
		
		CategoryPlot plot = returnChart.getCategoryPlot();
		plot.setOutlinePaint(Color.BLACK);
		plot.setOutlineStroke(new BasicStroke(2.0f));
		plot.setBackgroundPaint(Color.DARK_GRAY);
		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		categoryAxis.setMaximumCategoryLabelLines(2);
		categoryAxis.setTickLabelFont(categoryAxis.getLabelFont().deriveFont(LINE_CHART_CATEGORY_TICK_LABEL_FONT_SIZE));
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(lines, shapes);
		for(int i=0; i<defaultCategoryDataset.getColumnCount(); i++) {
			renderer.setSeriesPaint(i, csdpFrame.getColor(i));
		}
		plot.setRenderer(renderer);
		return returnChart;
	}//getChannelGroupGraphs
	
	/*
	 * Example: Given "280_290-292_300", return vector containing {"280", "290", "291", "292", "300"}
	 * Also allow reverse order: Given 300_292-290_280 return vector containing {"300", "292", "291", "290", "280"}
	 */
	public static Vector<String> parseChanGroupString(CsdpFrame csdpFrame, String s) {
		Vector<String> returnValues = new Vector<String> (); 
		String[] c = s.split("_|,");
		for(int i=0; i<c.length; i++) {
			if(c[i].indexOf("-")>0) {
				String[] parts = c[i].split("-");
				try {
					int firstChan = Integer.parseInt(parts[0]);
					int lastChan = Integer.parseInt(parts[1]);
					if(firstChan<lastChan) {
						for(int j=firstChan; j<=lastChan; j++) {
							returnValues.addElement(Integer.toString(j).trim());
						}
					}else {
						//count backwards--channels are to be added in reverse order
						for(int j=firstChan; j>=lastChan; j--) {
							returnValues.addElement(Integer.toString(j).trim());
						}
					}
				}catch(Exception e) {
					JOptionPane.showMessageDialog(csdpFrame, "Unable to parse "+(String) c[i], "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				returnValues.addElement(c[i].trim());
			}
		}
		return returnValues;
	}//parseChanGroupString
	
	/**
	 * debugging statements printed if true
	 */
	public static final boolean DEBUG = false;
	/**
	 * defines region which contains bathymetry points to be displayed in xsect
	 * view
	 */
	private static Polygon _polygon = new Polygon();
	/**
	 * index of x coordinate of first point-used when storing coordinates for 2
	 * points
	 */
	public static final int x1Index = 0;
	/**
	 * index of y coordinate of first point- use when storing coordinates for 2
	 * points
	 */
	public static final int y1Index = 1;
	/**
	 * index of x coordinate of 2nd point - use when storing coordinates for 2
	 * points
	 */
	public static final int x2Index = 2;
	/**
	 * index of y coordinate of 2nd point - use when storing coordinates for 2
	 * points
	 */
	public static final int y2Index = 3;
	/**
	 * index of x coordinate of a point
	 */
	public static final int xIndex = 0;
	/**
	 * index of y coordinate of a point
	 */
	public static final int yIndex = 1;
	/**
	 * index of z coordinate of a point
	 */
	public static final int zIndex = 2;

	/**
	 * index of minimum x value-used when calculating min and max values
	 */
	public static final int minXIndex = 0;
	/**
	 * index of minimum y value-used when calculating min and max values
	 */
	public static final int minYIndex = 1;
	/**
	 * index of maximum x value-used when calculating min and max values
	 */
	public static final int maxXIndex = 2;
	/**
	 * index of maximum y value-used when calculating min and max values
	 */
	public static final int maxYIndex = 3;
	/**
	 * name of directory accessed when opening bathymetry file. used for saving
	 */
	private static File _bathymetryDirectory = null;
	/**
	 * name of directory accessed when opening network file. used for saving
	 */
	private static File _networkDirectory = null;
	/**
	 * name of directory accessed when exporting network file. used for saving
	 */
	private static File _networkExportDirectory = null;
	/**
	 * name of directory accessed when opening landmark file. used for saving
	 */
	private static File _landmarkDirectory = null;
	/**
	 * name of directory accessed when opening DigitalLineGraphFile.
	 */
	private static File _DLGDirectory = null;
	/**
	 * name of directory accessed when opening properties file. used for saving
	 */
	private static File _propertiesDirectory = null;
	/**
	 * name of directory accessed when opening file containing information to be
	 * used for open water area calculations.
	 */
	private static File _openWaterAreaDirectory = null;
	/**
	 * name of directory accessed when opening file of any type. default
	 * directory for next open command
	 */
	private static File _openDirectory = null;

	private static File _networkCalculateDirectory = null;
	private static File _dsmChannelsDirectory = null;
	private static File _dsm2HofDirectory = null;
	private static String _dsm2HofFilename = null;
	
	/**
	 *
	 */
	private static File _digitalLineGraphDirectory = null;

	/**
	 * default value of cross-section thickness (perpendicualar to screen in
	 * cross- section view). determines amount of data to be displayed in xsect
	 * view.
	 */
	private static double _xsectThickness = 1000.0;
	/**
	 * number of pixels between bathymetry data and canvas edges
	 */
	public static final double BORDER_THICKNESS = 1000.0;
	/*
	 * if cross-sections are spaced closer than this, a warning will be added to network summary report
	 */
	public static final double MAXIMUM_SUGGESTED_XS_SPACING = 500.0;

	/*
	 * For CenterlineSummaryWindow class. the elevation used to calculate volume, wetted area, and surface area 
	 */
	public static double ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS = 0.0;
	/*
	 * For determining number of computational points in a channel. Used for automatically drawing cross-section lines.
	 */
	public static double DELTAX = 5000.0;
	/*
	 * For for dconveyance calculations
	 */
	public static double MANNINGS_N = 0.030;
	/*
	 * For automatically drawing a cross-section line at each computational point.
	 */
	public static double CROSS_SECTION_LINE_LENGTH = 3000.0;
	
	public static boolean getWarnZoom() {
		return _warnZoom;
	}

	public static void setWarnZoom(boolean b) {
		_warnZoom = b;
	}

	private static String _digitalLineGraphFilename = null;
	private static String _digitalLineGraphFiletype = null;
	private static String _landmarkFilename = null;
	private static String _landmarkFiletype = null;
	private static String _dsmChannelsFilename = null;
	private static String _DSMChannelsFiletype = null;
	private static String _networkFilename = null;
	private static String _networkFiletype = null;

	private static String _propertiesFilename = null;
	private static String _propertiesFiletype = null;
	private static String _openWaterAreaFilename = null;
	private static String _openWaterAreaFiletype = null;
	
	public static double CHARACTER_TO_PIXELS = 300.0f / 44.0;
	// private static Vector _buttons = new Vector();
	// private static int NUM_BUTTONS = 0;
	// private static Vector _colors = new Vector();
	private static boolean _echoTimeSeriesInput = true;
	private static boolean _echoXsectInput = false;
	private static boolean _printXsectResults = false;
	private static boolean _echoToeDrainInput = false;
	private static boolean _channelLengthsOnly = false;

	private static boolean _useFremontWeir = true;

	// correct
	private static final double _fremontWeirElevation = 30.5;
	// old
	// private static final double _fremontWeirElevation = 33.5;

	private static boolean _useToeDrainRestriction = true;

	// private static float _zoomFactor=1.0f;
	// public static float getZoomFactor(){
	// return _zoomFactor;
	// }
	// public static void setZoomFactor(float z){
	// _zoomFactor=z;
	// }
	// private static float _oldZoomFactor=1.0f;
	// public static float getOldZoomFactor(){
	// return _oldZoomFactor;
	// }
	// public static void setOldZoomFactor(float z){
	// _oldZoomFactor=z;
	// }

	private static boolean _warnZoom = true;
	public static final Cursor _waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	public static final Cursor _defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	public static final Cursor _crosshairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	public static final Cursor _handCursor = new Cursor(Cursor.HAND_CURSOR);
	public static final Cursor _moveCursor = new Cursor(Cursor.MOVE_CURSOR);
	public static boolean _cancelSaveNetwork = true;
	public static boolean _cancelSaveLandmarks = true;

	/**
	 * the minimum x coordinate (data coordinates) of the current plot region.
	 */
	public static double minXPlot;
	/**
	 * the maximum x coordinate (data coordinates) of the current plot region.
	 */
	public static double maxXPlot;
	/**
	 * the minimum y coordinate (data coordinates) of the current plot region.
	 */
	public static double minYPlot;
	/**
	 * the maximum y coordinate (data coordinates) of the current plot region.
	 */
	public static double maxYPlot;

	public static int getNumMetadataLines() {
		return _numMetadataLines;
	}

	private static final int _numMetadataLines = 7;

	public static void setPreferredMetadata(CsdpFileMetadata b) {
		_preferredMetadata = b;
	}

	public static CsdpFileMetadata getPreferredMetadata() {
		return _preferredMetadata;
	}

	/**
	 * The preferred metadata, which is set by Csdp class
	 */
	private static CsdpFileMetadata _preferredMetadata;

	public static void setBathymetryMetadata(CsdpFileMetadata b) {
		_bathymetryMetadata = b;
	}

	public static CsdpFileMetadata getBathymetryMetadata() {
		return _bathymetryMetadata;
	}

	/**
	 * The bathymetry metadata--depends on hor/ver datum of bathymetry file. If
	 * other files are read (landmark, network, and/or channel outline, they
	 * will be converted to the datum defined by this metadata.
	 */
	private static CsdpFileMetadata _bathymetryMetadata;

	public static void setNetworkMetadata(CsdpFileMetadata b) {
		_networkMetadata = b;
	}

	public static CsdpFileMetadata getNetworkMetadata() {
		if (_networkMetadata == null) {
			_networkMetadata = (CsdpFileMetadata) _bathymetryMetadata.clone();
		}
		return _networkMetadata;
	}

	/**
	 * The network metadata--depends on hor/ver datum of network file.
	 */
	private static CsdpFileMetadata _networkMetadata;

	public static void setLandmarkMetadata(CsdpFileMetadata b) {
		_landmarkMetadata = b;
	}

	public static CsdpFileMetadata getLandmarkMetadata() {
		return _landmarkMetadata;
	}

	/**
	 * The landmark metadata--depends on hor/ver datum of landmark file.
	 */
	private static CsdpFileMetadata _landmarkMetadata;

	public static void setDigitalLineGraphMetadata(CsdpFileMetadata b) {
		_digitalLineGraphMetadata = b;
	}

	public static CsdpFileMetadata getDigitalLineGraphMetadata() {
		return _digitalLineGraphMetadata;
	}

	/**
	 * The digitalLineGraph metadata--depends on hor/ver datum of
	 * digitalLineGraph file.
	 */
	private static CsdpFileMetadata _digitalLineGraphMetadata;

	public static UseSemmscon getUseSemmscon() {
		return _us;
	}

	public static void setUseSemmscon(UseSemmscon us) {
		_us = us;
	}

	private static UseSemmscon _us;
	
	/*
	 * Will be equal to BIT_32 if the 32 bit JRE is being used, will be BIT_64 if 64 bit.
	 */
	public static int BITNESS;
	/*
	 * Negative dConveyance should be eliminated above the intertidal low and below the intertidal high 
	 */
	public static double INTERTIDAL_LOW_TIDE = -2.5;
	/*
	 * Negative dConveyance should be eliminated above the intertidal low and below the intertidal high 
	 */
	public static double INTERTIDAL_HIGH_TIDE = 17.5;

	/*
	 * If true, network elements (centerlines and cross-section lines) will be colored to identify the following issues:
	 * no points
	 * negative dConveyance in intertidal zone
	 * duplicate station values in a cross-section
	 * area ratio in centerline exceeds maximum recommended value 
	 */
	public static boolean NETWORK_COLORING = false;
	
	/*
	 * The maximum recommended ratio of largest to smallest cross-sectional areas in a channel, evaluated at 
	 * ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS, which is usually zero wrt the current datum
	 */
	public static final double MAX_AREA_RATIO = 2.0;
	/*
	 * Indicates that the 32 bit JRE is being used.
	 */
	public static final int BIT_32 = 10;
	/*
	 * Indicates that the 64 bit JRE is being used.
	 */
	public static final int BIT_64 = 20;

	/*
	 * Value used for LineSimplification algorithm, for automatic cross-section creation
	 */
	public static double RAMER_DOUGLAS_PEUCKER_EPSILON = 1.0;
	
	/*
	 * The minimum year to be used for automatic cross-section creation. Bathymetry points with years lower
	 * than this value will not be used.
	 */
	public static int AUTO_XS_MIN_YEAR = 0;
	/*
	 * The maximum year to be used for automatic cross-section creation. Bathymetry points with years higher
	 * than this value will not be used.
	 */
	public static int AUTO_XS_MAX_YEAR = 9999;

	/*
	 * If true, a dialog is already open, and don't allow another to be open.
	 */
	private static boolean MOVE_POLYGON_CENTERLINE_POINTS_TO_LEVEE_CENTERLINE_DIALOG_OPEN;

	/*
	 * When editing a cross-section, in add left point or add right point modes,
	 * compare stations of first and last points before determining where to insert points.
	 * This can behave strangely if cross-sections do not have a trapezoidal shape.
	 * If you want user to change this option, could be added to Display Parameter menu
	 */
	public static boolean ADD_XSECT_POINTS_BASED_ON_POINT_ORDER = true;
	public static double cubicMetersToCubicFeet = 35.3147;
	public static double squareMetersToSquareFeet = 10.7639;
	
	public static boolean backupFile(String fullPath) {
		String inputPath = fullPath;
		String outputPath = inputPath + ".bak";
		File f = new File(inputPath);
		boolean success = false;

		// if the file doesn't already exist, don't need to backup
		if (!f.exists()) {
			success = true;
		} else {
			try {
				int bsize = 1024;
				FileChannel in = new FileInputStream(inputPath).getChannel(),
						out = new FileOutputStream(outputPath).getChannel();
				ByteBuffer buffer = ByteBuffer.allocate(bsize);
				while (in.read(buffer) != -1) {
					buffer.flip();
					out.write(buffer);
					buffer.clear();
				}
				success = true;
			} catch (IOException e) {
				System.out.println("error in PropertiesAsciiOutput backupFile()");
				success = false;
			}
		}
		return success;
	}//backupFile

	public static boolean backupFile(String directory, String filename) {
		boolean success = backupFile(directory + filename);
		return success;
	}

	/*
	 * From Thinking in Java 4th ed., p949 Used to backup existing file
	 */
	public static boolean backupFile(String directory, String filename, String filetype) {
		boolean success = backupFile(directory, filename + "." + filetype);
		return success;
	}// backupFile

	/*
	 * creates a string representation of a Vector or HashSet of integer values 
	 * i.e. 1-2-56
	 */
	public static String abstractCollectionToString(AbstractCollection<Integer> vector) {
		String returnValue = "";
		int index=0;
		if(vector!=null && vector.size()>0) {
			Iterator<Integer> iterator = vector.iterator(); 
			while(iterator.hasNext()) {
			//			for(int i=0; i<vector.size(); i++) {
				if(index>0) {
					returnValue += ",";
				}
//				returnValue += String.valueOf(vector.get(i));
				returnValue += String.valueOf(iterator.next());
				index++;
			}
		}
		return returnValue;
	}

	/*
	 * Create instance of ImageIcon using image Url scaled to specified width and height
	 */
	public static ImageIcon createScaledImageIcon(URL imageUrl, int width, int height) {
		ImageIcon returnImageIcon = null;
		try {
			returnImageIcon =  new ImageIcon((new ImageIcon(imageUrl)).getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
		}catch(NullPointerException e){
			System.out.println("Error in CsdpFunctions.createScaledImageIcon: unable to create icon for imageUrl="+imageUrl);
			System.out.println("error message: "+e.toString());
		}
		return returnImageIcon;
	}

	/*
	 * The font size used by instances of DataEntryDialog, and possibly others.
	 */
	public static final float DIALOG_FONT_SIZE = 16.0f;

	/*
	 * Sets font size for tick labels on line charts
	 */
	private static final float LINE_CHART_CATEGORY_TICK_LABEL_FONT_SIZE = 16.0f;

	public static boolean movePolygonCenterlinePointsToLeveeCenterlineDialogOpen() {
		return MOVE_POLYGON_CENTERLINE_POINTS_TO_LEVEE_CENTERLINE_DIALOG_OPEN;
	}

	public static void setPolygonCenterlinePointsToLeveeCenterlineDialogOpen(boolean b) {
		MOVE_POLYGON_CENTERLINE_POINTS_TO_LEVEE_CENTERLINE_DIALOG_OPEN = b;		
	}

	/*
	 * If true, when requesting a 3d plot, an information dialog will be displayed first.
	 * Reason: it is necessary for user to right drag on plot before it will be displayed properly. 
	 */
	public static boolean DISPLAY_3D_PLOT_INFO_MSG = true;


	public static void display3dPlotInfoMessage(CsdpFrame csdpFrame) {
		if(CsdpFunctions.DISPLAY_3D_PLOT_INFO_MSG) {
			String message = "<HTML><BODY>For some reason, you must right click with the mouse on the graph to get it to display properly.<BR><BR>"
					+ "Usage:<BR>"
					+ "----------------------------------------------<BR>"
					+ "<TABLE>"
					+ "<TR><TD>Left drag:</TD>            <TD>rotate plot</TD></TR>"
					+ "<TR><TD>Right click:</TD>          <TD>rotate plot continuously</TD></TR>"
					+ "<TR><TD>Rotate Mouse Wheel:</TD>   <TD>zoom z axis</TD></TR>"
					+ "<TR><TD>Ctrl-Mouse Wheel:</TD>     <TD>zoom x axis</TD></TR>"
					+ "<TR><TD>Alt-Mouse Wheel:</TD>      <TD>zoom y axis</TD></TR>"
					+ "<TR><TD>Ctrl-Alt-Mouse Wheel</TD>  <TD>zoom x and y axes</TD></TR>"
					+ "<TR><TD>Right drag:</TD>           <TD>pan z axis</TD></TR>"
					+ "<TR><TD>Ctrl-Right drag:</TD>      <TD>pan x axis</TD></TR>"
					+ "<TR><TD>Alt-Right drag:</TD>       <TD>pan y axis</TD></TR>"
					+ "<TR><TD>Ctrl-Alt-Right drag</TD>   <TD>pan x and y axes</TD></TR>"
					+ "</TABLE><BR>"
					+ "Display this message next time?</BODY></HTML>";
			JLabel messageLabel = new JLabel(message);
			int response = JOptionPane.showConfirmDialog(csdpFrame, messageLabel, "Message", JOptionPane.YES_NO_OPTION);
			if(response==JOptionPane.NO_OPTION) {
				CsdpFunctions.DISPLAY_3D_PLOT_INFO_MSG = false;
			}
		}
	}

	/**
	 * separates filename into prefix and extension
	 * Adapted from method in NetworkInput
	 */
	public static String[] parseFilename(String filename) {
		String[] returnValues = new String[2];

		// int dotIndex = filename.indexOf(".",0);
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex >= 0) {
			returnValues[0] = filename.substring(0, dotIndex);
			returnValues[1] = filename.substring(dotIndex + 1);
		} else {
			returnValues[0] = filename;
			returnValues[1] = null;
		}
		return returnValues;
	}// parseFilename
	
	public static Network getNetworkInstance(CsdpFrame csdpFrame, App app, NetworkPlot networkPlot) {
		Network network = csdpFrame.getNetwork();

		if (network == null) {
			network = new Network("delta", csdpFrame);
			csdpFrame.setNetwork(network);
			app._net = network;
			networkPlot = app.setNetworkPlotter();
			csdpFrame.getPlanViewCanvas(0).setNetworkPlotter(networkPlot);
			csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			csdpFrame.getPlanViewCanvas(0).redoNextPaint();
			csdpFrame.getPlanViewCanvas(0).repaint();

			// _gui.enableAfterNetwork();
			csdpFrame.enableWhenNetworkExists();
		} // if net is null
		return network;
	}
	
	/*
	 * used to create DSM2 channels, using channels.inp file and landmark file with nodes
	 * If checkChannelExists, user will be prompted to load another channels.inp file 
	 * if the specified centerline name doesn't exist in it.
	 */
	public static DSMChannels getChannelsInpFile(CsdpFrame csdpFrame, App app, Network network, Landmark landmark, 
			String centerlineName, boolean checkChannelExists) {
		_dsmChannels = app.getDSMChannels();
		boolean loadAnotherChannelsInpFile = true;
		JFileChooser _jfcChannelsInp = new JFileChooser();
		String[] _channelsInpExtensions = { "inp" };
		int _numChannelsInpExtensions = 1;
		String directoryString = "";

		CsdpFileFilter _channelsInpFilter = new CsdpFileFilter(_channelsInpExtensions, _numChannelsInpExtensions);
		// if channels.inp file not loaded OR if channel # doesn't exist in
		// current
		// DSMChannels object. ask user if another file should be
		// loaded--don't
		// assume there is another file with the channel.
		while (loadAnotherChannelsInpFile) {
			if (checkChannelExists && _dsmChannels != null && _dsmChannels.channelExists(centerlineName) == false) {
				int response = JOptionPane.showConfirmDialog(csdpFrame, "Channel " + centerlineName
						+ " not found in channel connectivity file.  Load another file?", "Channel not found", JOptionPane.YES_NO_OPTION);
				if(response==JOptionPane.YES_OPTION) {
					loadAnotherChannelsInpFile = true;
				} else {
					loadAnotherChannelsInpFile = false;
				}
			}else {
				loadAnotherChannelsInpFile = false;
			}

			if (_dsmChannels == null || loadAnotherChannelsInpFile) {
				String channelsFilename = null;
				// FileDialog fd = new FileDialog(_gui, "Open DSM2 channel
				// connectivity file");
				// fd.setVisible(true);
				_jfcChannelsInp.setDialogTitle("Open DSM2 channel connectivity file");
				_jfcChannelsInp.setApproveButtonText("Open");
				_jfcChannelsInp.addChoosableFileFilter(_channelsInpFilter);
				_jfcChannelsInp.setFileFilter(_channelsInpFilter);

				if (CsdpFunctions.getOpenDirectory() != null) {
					_jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
				}
				_dsmChannelsFilechooserState = _jfcChannelsInp.showOpenDialog(csdpFrame);
				if (_dsmChannelsFilechooserState == JFileChooser.APPROVE_OPTION) {
					channelsFilename = _jfcChannelsInp.getName(_jfcChannelsInp.getSelectedFile());
					directoryString = _jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;

					// channelsFilename = fd.getFile();
					// _directory = fd.getDirectory();
					
					csdpFrame.setCursor(_waitCursor);
					try {
						_dsmChannels = app.chanReadStore(directoryString, channelsFilename);
//						_gui.setDSMChannels(_DSMChannels);
					}catch(Exception e1) {
						JOptionPane.showMessageDialog(csdpFrame, "Error creating DSM2 channel", "Error", JOptionPane.ERROR_MESSAGE);
					}finally {
						csdpFrame.setCursor(_defaultCursor);
						
					}
				} else {
					loadAnotherChannelsInpFile = false;
				}
			} // if DSMChannels is null

			if (_dsmChannelsFilechooserState == JFileChooser.APPROVE_OPTION) {
				if (checkChannelExists) {
					if (network.getCenterline(centerlineName) != null) {
						int response = JOptionPane.showConfirmDialog(csdpFrame, "Centerline " + centerlineName + " already exists. Replace?",
								"Replace centerline?", JOptionPane.YES_NO_OPTION);
						if(response==JOptionPane.YES_OPTION) {
							// addDSMChannel(centerlineName);
	//						loadAnotherChannelsInpFile = addDSMChannel(csdpFrame, network, landmark, centerlineName);
							loadAnotherChannelsInpFile = !okToAddDSMChannel(csdpFrame, network, landmark, centerlineName);
						}
					} else {
						// addDSMChannel(centerlineName);
	//					loadAnotherChannelsInpFile = addDSMChannel(csdpFrame, network, landmark, centerlineName);
						loadAnotherChannelsInpFile = !okToAddDSMChannel(csdpFrame, network, landmark, centerlineName);
					}
				}else {
					loadAnotherChannelsInpFile = false;
				}
			} // if the cancel button wasn't pressed
		} // while
		return _dsmChannels;
	}// getChannelsInpFile

	/*
	 * Return true of network file contains centerline name and landmark file contains both nodes. 
	 */
	public static boolean okToAddDSMChannel(CsdpFrame csdpFrame, Network network, Landmark landmark, String centerlineName) {
		boolean okToAdd = false;
		int upnode = _dsmChannels.getUpnode(centerlineName);
		int downnode = _dsmChannels.getDownnode(centerlineName);
		if (upnode < 0 || downnode < 0) {
			okToAdd = false;
		}else {
			if (landmark == null)
				landmark = csdpFrame.getLandmark(); // load landmark file
			if(landmark.containsLandmark(Integer.toString(upnode)) && landmark.containsLandmark(Integer.toString(downnode))) {
				okToAdd = true;
			}
		}
		return okToAdd;
	}//okToAddDSMChannel
	
	/**
	 * adds a centerline for the specified DSM channel number. First point
	 * is located at upstream node, last point is located at downstream
	 * node.
	 */
	public static boolean addDSMChannel(CsdpFrame csdpFrame, Network network, Landmark landmark, String centerlineName) {
		int upnode = 0;
		int downnode = 0;
		String upnodeString = null;
		String downnodeString = null;
		double upnodeX = 0.0;
		double upnodeY = 0.0;
		double downnodeX = 0.0;
		double downnodeY = 0.0;
		Centerline centerline = null;
		boolean landmarkError = false;
		boolean channelsInpError = false;

		network.addCenterline(centerlineName);
		centerline = network.getCenterline(centerlineName);
		upnode = _dsmChannels.getUpnode(centerlineName);
		downnode = _dsmChannels.getDownnode(centerlineName);

		if (upnode < 0 || downnode < 0) {
			JOptionPane.showMessageDialog(csdpFrame, "ERROR:  node not found for centerline " + centerlineName, 
					"Error", JOptionPane.ERROR_MESSAGE);
			channelsInpError = true;
		}

		// Integer upnodeInteger = new Integer(upnode);
		// Integer downnodeInteger = new Integer(downnode);
		// upnodeString = upnodeInteger.toString(upnode);
		// downnodeString = downnodeInteger.toString(downnode);

		upnodeString = Integer.toString(upnode);
		downnodeString = Integer.toString(downnode);

		boolean giveUp = false;
		double upX = -Double.MAX_VALUE;
		double upY = -Double.MAX_VALUE;
		double downX = -Double.MAX_VALUE;
		double downY = -Double.MAX_VALUE;

		while (giveUp == false) {
			if (DEBUG)
				System.out.println("landmark=" + landmark);
			if (landmark == null)
				landmark = csdpFrame.getLandmark(); // load landmark file
			upX = landmark.getXFeet(upnodeString);
			upY = landmark.getYFeet(upnodeString);
			downX = landmark.getXFeet(downnodeString);
			downY = landmark.getYFeet(downnodeString);

			if (upX < 0.0f || upY < 0.0f) {
				JOptionPane.showMessageDialog(csdpFrame, "ERROR:  insufficient information in landmark file for node " + upnodeString + ".", 
						"Error", JOptionPane.ERROR_MESSAGE);

				landmarkError = true;
			}
			if (downX < 0.0f || downY < 0.0f) {
				JOptionPane.showMessageDialog(csdpFrame, "ERROR:  insufficient information in landmark file for node " + downnodeString + ".", 
						"Error", JOptionPane.ERROR_MESSAGE);
				landmarkError = true;
			}
			if (landmarkError) {
				int response = JOptionPane.showConfirmDialog(csdpFrame, "Load another landmark file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
				if(response==JOptionPane.YES_OPTION) {
					landmark = csdpFrame.getLandmark(); // load landmark file
				}else if(response==JOptionPane.NO_OPTION || response==JOptionPane.CANCEL_OPTION) {
					giveUp = true;
				}
			} else {
				giveUp = true;
			}
		} // while

		if (channelsInpError == false && landmarkError == false) {
			// getX function returns -BIG_FLOAT if node not found in open
			// landmark file
			if (upX < 0.0f || upY < 0.0f || downX < 0.0f || downY < 0.0) {
				landmark = csdpFrame.getLandmark(); // load landmark file
			} // could use a while loop, but user would never get out if no
				// landmark file
			upnodeX = landmark.getXFeet(upnodeString);
			upnodeY = landmark.getYFeet(upnodeString);
			downnodeX = landmark.getXFeet(downnodeString);
			downnodeY = landmark.getYFeet(downnodeString);
			centerline.addDownstreamCenterlinePointFeet(upnodeX, upnodeY);
			centerline.addDownstreamCenterlinePointFeet(downnodeX, downnodeY);
			if (DEBUG)
				System.out.println("landmark coordinates: upstream xy, downstream xy=" + upnodeX + "," + upnodeY
						+ "," + downnodeX + "," + downnodeY);

			network.setSelectedCenterlineName(centerlineName);
			network.setSelectedCenterline(network.getCenterline(centerlineName));
			csdpFrame.enableAfterCenterlineSelected();
			csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			csdpFrame.getPlanViewCanvas(0).redoNextPaint();
			csdpFrame.getPlanViewCanvas(0).repaint();
		}
		if(!channelsInpError) { 
			return true;
		}else {
			return false;
		}
	}// addDSMChannel

	public static String getCurrentDatetimeFormattedForFilenames() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
	public static String getCurrentDatetimeFormattedForDSM2InputComments() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}

	public static String getIconImagePath() {
		return _iconImagePath;
	}
	
	/*
	 * The path to use to look for images used to create icons
	 */
	private static String _iconImagePath = "images";
	private static DSMChannels _dsmChannels;
	private static int _dsmChannelsFilechooserState;
	public static void appendGitHashToVersionNumber(String gitHash) {
		_version += "_#"+gitHash;
	}
	
	/**
	 * version number-displayed at top of frame
	 */
	private static String _version = "3.0_20231207";



}// class CsdpFunctions
