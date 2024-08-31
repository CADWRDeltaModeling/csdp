package DWR.CSDP;

/**
 * A centerline point has x and y (UTM) coordinates All values in feet-never use
 * meters
 */
public class CenterlinePoint {

	/**
	 * returns value of x at specified index
	 */
	public double getXFeet() {
		return _x;
	}

	/**
	 * returns value of y at specified index
	 */
	public double getYFeet() {
		return _y;
	}

	/**
	 * puts value of x
	 */
	public void putXFeet(double value) {
		_x = value;
	}

	/**
	 * puts value of y
	 */
	public void putYFeet(double value) {
		_y = value;
	}

	protected double _x;
	protected double _y;

}
