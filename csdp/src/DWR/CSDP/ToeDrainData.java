package DWR.CSDP;

import java.util.Hashtable;

/**
 * Stores the Toe Drain data that are used for open water area calculations
 *
 * @author
 * @version $Id:
 */
public class ToeDrainData {
	private static final boolean DEBUG = false;
	private int _numXsects;
	private ResizableStringArray _xsectName = new ResizableStringArray();
	private Hashtable _station = new Hashtable();
	private Hashtable _elevation = new Hashtable();

	public void printAll() {
		System.out.println("xsect     station     elevation");
		String name = null;
		for (int i = 0; i <= getNumXsects() - 1; i++) {
			name = getXsectName(i);
			System.out.println(name + "  " + getStationFeet(name) + "  " + getElevationFeet(name));
		}
	}

	public void addName(String name) {
		_xsectName.put(_numXsects, name);
		_numXsects++;
	}

	public String getXsectName(int index) {
		return _xsectName.get(index);
	}

	public int getNumXsects() {
		return _numXsects;
	}

	public void setStationFeet(String name, double station) {
		_station.put(name, new Double(station));
	}

	public void setElevationFeet(String name, double elevation) {
		_elevation.put(name, new Double(elevation));
	}

	public double getStationFeet(String name) {
		double returnValue = -Double.MAX_VALUE;
		if (_station.get(name) != null) {
			returnValue = ((Double) (_station.get(name))).doubleValue();
		}
		return returnValue;
	}

	public double getElevationFeet(String name) {
		double returnValue = -Double.MAX_VALUE;
		if (_elevation.get(name) != null) {
			returnValue = ((Double) (_elevation.get(name))).doubleValue();
		}
		return returnValue;
	}

} // class ToeDrainData
