package DWR.CSDP;

/**
 * A point in a line in a DigitalLineGraph
 */
public class DigitalLineGraphPoint {
	public DigitalLineGraphPoint(double x, double y) {
		putX(x);
		putY(y);
	}

	public double getX() {
		return _x;
	}

	public void putX(double value) {
		_x = value;
	}

	public double getY() {
		return _y;
	}

	public void putY(double value) {
		_y = value;
	}

	private double _x;
	private double _y;
}// class DigitalLineGraphPoint
