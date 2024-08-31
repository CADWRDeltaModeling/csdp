package DWR.CSDP;

import java.util.Vector;

/**
 * Landmarks are symbols with labels.  Landmarks have x and y UTM coordinates
 *
 * @author
 * @version
 */
/**
 * A line in a DigitalLineGraph
 */
public class DigitalLineGraphLine {
	public DigitalLineGraphLine(String n) {
		_name = n;
		_points = new Vector();
	}

	/**
	 * add DigitalLineGraphPoint to hashtable
	 */
	public void addPoint(int index, double x, double y) {
		DigitalLineGraphPoint point = new DigitalLineGraphPoint(x, y);
		_numPoints++;
		_points.add(index, point);
		if (DEBUG)
			System.out.println("added point:" + x + "," + y);
		if (index >= _numPoints) {
			System.out.println("Error in DigitalLineGraphLine.addPoint:");
			System.out.println("Specified point index is greater than the");
			System.out.println("specified number of points.");
			System.out.println("_numPoints, index=" + _numPoints + "," + index);

		}
	}

	public DigitalLineGraphPoint getPoint(int index) {
		return (DigitalLineGraphPoint) _points.elementAt(index);
	}

	/**
	 * Remove DigitalLineGraphPoint from hashtable
	 */
	public void removePoint(int index) {
		_points.setElementAt(null, index);
	}

	/**
	 * returns number of DigitalLineGraphPoints
	 */
	public int getNumPoints() {
		return _numPoints;
	}

	/**
	 * returns x value of specified point name
	 */
	public double getX(int index) {
		DigitalLineGraphPoint dlgPoint = (DigitalLineGraphPoint) (_points.elementAt(index));
		if (dlgPoint != null)
			return dlgPoint.getX();
		else
			return -Double.MAX_VALUE;
	}

	/**
	 * returns y value of specified point name
	 */
	public double getY(int index) {
		DigitalLineGraphPoint dlgPoint = (DigitalLineGraphPoint) (_points.elementAt(index));
		if (dlgPoint != null)
			return dlgPoint.getY();
		else
			return -Double.MAX_VALUE;
	}

	private Vector _points;
	private String _name;
	private int _numPoints;
	private final boolean DEBUG = false;
}// class DigitalLineGraphLine
