package DWR.CSDP;

/**
 * A cross-section point has station and elevation coordinates. The station is a
 * function of both x and y (UTM)
 */
public class XsectPoint {

	/**
	 * returns value of station at specified index
	 */
	public double getStationFeet() {
		return _station;
	}

	/**
	 * returns value of elevation at specified index
	 */
	public double getElevationFeet() {
		return _elevation;
	}

	/**
	 * puts value of station
	 */
	public void putStationFeet(double value) {
		_station = value;
	}

	/**
	 * puts value of elevation
	 */
	public void putElevationFeet(double value) {
		_elevation = value;
	}
	
	public XsectPoint clone() {
		XsectPoint xsectPoint = new XsectPoint();
		xsectPoint.putStationFeet(getStationFeet());
		xsectPoint.putElevationFeet(getElevationFeet());
		return xsectPoint;
	}

	protected double _station = 0.0f;
	protected double _elevation = 0.0f;

}// class XsectPoint
