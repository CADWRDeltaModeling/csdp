package DWR.CSDP;

import java.util.Hashtable;

/**
 * Stores the station locations and names that are used for open water area
 * calculations
 *
 * @author
 * @version $Id:
 */
public class StationTimeSeriesData {
	private static final boolean DEBUG = false;
	private int _numDates = 0;
	private ResizableStringArray _date = new ResizableStringArray();
	private Hashtable _flow = new Hashtable();
	/*
	 * the index of the column in the input file that contains the flow values
	 */
	private int _flowIndex = -Integer.MAX_VALUE;
	private int _numStations = 0;
	private Hashtable _nameDistance = new Hashtable();
	private Hashtable _indexStationName = new Hashtable();

	/*
	 * key=date_distance, value=elevation
	 */
	private Hashtable _dateDistanceElevation = new Hashtable();

	/*
	 * add a date to the array
	 */
	public void addDate(int dateIndex, String date) {
		if (DEBUG)
			System.out.println("adding date.  index, date=" + dateIndex + "," + date);
		_date.put(dateIndex, date);
		_numDates++;
	}

	/*
	 * stores index of station name header in time series input section, NOT in
	 * station name/distance section
	 */
	public void addStationIndex(int index, String header) {
		_indexStationName.put(new Integer(index), header);
	}

	/*
	 * returns station name for specified index.
	 */
	public String getStationName(int index) {
		// return _stationName.get(index);
		if (DEBUG)
			System.out.println("getting station name. index=" + index);
		if (DEBUG)
			System.out.println("string of the index=" + (new Integer(index)).toString());
		if (DEBUG)
			System.out.println("station name returned=" + _indexStationName.get(new Integer(index)));
		return (String) (_indexStationName.get(new Integer(index)));
	}

	/*
	 * find index of station with specified name. used only to replace fremont
	 * with yolo with fremont weir is operating
	 */
	public int getStationIndex(String name) {
		int returnIndex = -Integer.MAX_VALUE;
		for (int i = 0; i <= getNumStations() - 1; i++) {
			if (getStationName(i).equalsIgnoreCase(name)) {
				returnIndex = i;
			}
		}
		return returnIndex;
	}

	/*
	 * column number of flow values in station input file
	 */
	public void setFlowIndex(int index) {
		_flowIndex = index;
	}

	/*
	 * adds flow to hashtable. values are not sorted.
	 */
	public void addFlow(String date, double value) {
		_flow.put(date, new Double(value));
	}

	public double getFlow(int index) {
		return (getFlow(getDate(index)));
	}

	public double getFlow(String date) {
		return ((Double) (_flow.get(date))).doubleValue();
	}

	public int getNumStations() {
		return _numStations;
	}

	public int getNumDates() {
		return _numDates;
	}

	/*
	 * adds a station name. The index value is not the same....
	 */
	// public void addStation(int index, String name){
	// _stationName.put(index,name);
	// }

	/*
	 * adds a station name and its distance
	 */
	public void addStation(String name, double distance) {
		_nameDistance.put(name.toLowerCase(), new Double(distance));
		_numStations++;
	}

	/*
	 * returns a distance for the specified station name
	 */
	public double getDistance(String name) {

		if (DEBUG)
			System.out.println("StationTimeSeriesData.getDistance. name=" + name);

		if (DEBUG)
			System.out.println("getting distance for station " + name);
		double distance = -Double.MAX_VALUE;
		// if(name == null){
		// }else{
		name = name.toLowerCase();
		try {
			distance = ((Double) (_nameDistance.get(name))).doubleValue();
		} catch (java.lang.NullPointerException e) {
			System.out.println("exception caught in StationTimeSeriesData.getDistance:");
			System.out.println("unable to parse " + _nameDistance.get(name) + " as a Double");
			System.out.println("requested station name=" + name);
			System.out.println(
					"check your .tsd file:  did you spell the station name the same way in both input sections?");
		}
		// }
		return distance;
	}

	public double getDistance(int index) {
		return getDistance(getStationName(index));
	}

	// public void addElevation(String date, int stationIndex, double
	// elevation){
	// return addElevation(date, getDistance(stationIndex), elevation);
	// }

	/*
	 * adds an elevation value at the specified distance and for the specified
	 * date
	 */

	public void addElevation(String date, String stationName, double elevation) {
		// boolean match = false;
		// for (int i=0; i<=_numDates-1; i++){
		// if(_date.get(i) == date){
		// match = true;
		// }
		// }
		// if(match == false){
		// _numDates++;
		// _date.put(_numDates, date);
		// }

		if (DEBUG)
			System.out.println("adding elevation. key=" + date + "_" + stationName);
		_dateDistanceElevation.put(date + "_" + stationName, new Double(elevation));
	}

	public double getElevationFromList(int dateIndex, int stationIndex) {
		// String key = null;
		String date = getDate(dateIndex);
		String stationName = getStationName(stationIndex);
		// key = _dateDistanceElevation.get(getDate(dateIndex))+"_";
		// key += (new Double(distance)).toString();
		// Double e = new Double(key);
		if (DEBUG)
			System.out.println("trying to get elevation. key=" + date + "_" + stationName);
		return ((Double) (_dateDistanceElevation.get(date + "_" + stationName))).doubleValue();
	}

	/*
	 * find stations that are closest on either side (nearest upstream and
	 * nearest downstream stations). if only one station found, extrapolate. if
	 * two stations found, interpolate. "SecondNearest" values are used for
	 * extrapolation
	 */
	public double getElevation(int dateIndex, double distance) {
		double nearestUpElevation = Double.MAX_VALUE;
		double nearestDownElevation = Double.MAX_VALUE;
		double secondNearestUpElevation = Double.MAX_VALUE;
		double secondNearestDownElevation = Double.MAX_VALUE;

		double nearestUpDistance = -Double.MAX_VALUE;
		double nearestDownDistance = Double.MAX_VALUE;
		double secondNearestUpDistance = -Double.MAX_VALUE;
		double secondNearestDownDistance = Double.MAX_VALUE;

		int nearestUpIndex = -Integer.MAX_VALUE;
		int nearestDownIndex = -Integer.MAX_VALUE;
		int secondNearestUpIndex = -Integer.MAX_VALUE;
		int secondNearestDownIndex = -Integer.MAX_VALUE;

		double elevation = -Double.MAX_VALUE;
		String date = getDate(dateIndex);
		double x1 = -Double.MAX_VALUE;
		double x2 = -Double.MAX_VALUE;
		double y1 = -Double.MAX_VALUE;
		double y2 = -Double.MAX_VALUE;

		double stationDistance = -Double.MAX_VALUE;

		boolean fremontIsOnlyStation = true;

		for (int i = 0; i <= getNumStations() - 1; i++) {
			if (getStationName(i).equalsIgnoreCase("fremont") == false
					&& getElevationFromList(dateIndex, i) >= -999.0f) {
				fremontIsOnlyStation = false;
			}
		} // for
		if (DEBUG && fremontIsOnlyStation)
			System.out.println("Fremont is only station for " + date);

		for (int i = 0; i <= getNumStations() - 1; i++) {
			stationDistance = getDistance(i);
			if (getElevationFromList(dateIndex, i) < -999.0f || (fremontIsOnlyStation == false
					&& CsdpFunctions.getUseFremontWeir() && getStationName(i).equalsIgnoreCase("fremont")
					&& getElevationFromList(dateIndex, i) <= CsdpFunctions.getFremontWeirElevation())) {
				stationDistance = -Double.MAX_VALUE;
				if (DEBUG)
					System.out.println("using Fremont Weir! date, stage=" + getDate(dateIndex) + ","
							+ getElevationFromList(dateIndex, i));
			}

			if (stationDistance <= distance) { // up station
				if (stationDistance > nearestUpDistance) {
					secondNearestUpDistance = nearestUpDistance;
					nearestUpDistance = stationDistance;
					secondNearestUpIndex = nearestUpIndex;
					nearestUpIndex = i;
				} else if (stationDistance <= nearestUpDistance && stationDistance > secondNearestUpDistance) {
					secondNearestUpDistance = stationDistance;
					secondNearestUpIndex = i;
				}
			} else if (stationDistance > distance) { // down station
				if (stationDistance < nearestDownDistance) {
					secondNearestDownDistance = nearestDownDistance;
					nearestDownDistance = stationDistance;
					secondNearestDownIndex = nearestDownIndex;
					nearestDownIndex = i;
				} else if (stationDistance >= nearestDownDistance && stationDistance < secondNearestDownDistance) {
					secondNearestDownDistance = stationDistance;
					secondNearestDownIndex = i;
				}
			}
		} // if

		if (DEBUG) {
			System.out.println("nearestUpIndex=" + nearestUpIndex);
			System.out.println("nearestDownIndex=" + nearestDownIndex);
			System.out.println("secondNearestUpIndex=" + secondNearestUpIndex);
			System.out.println("secondNearestDownIndex=" + secondNearestDownIndex);
			System.out.println("nearestUpDistance=" + nearestUpDistance);
			System.out.println("nearestDownDistance=" + nearestDownDistance);
			System.out.println("secondNearestUpDistance=" + secondNearestUpDistance);
			System.out.println("secondNearestDownDistance=" + secondNearestDownDistance);
		}

		// find nearest up station

		// find nearest down station

		// if two, interpolate and return elevation
		// if only one, extrapolate
		// if not one or two, print error message & warn user.
		boolean interpolateElevation = true;
		if (nearestUpIndex >= 0 && nearestDownIndex >= 0) {
			x1 = getDistance(nearestUpIndex);
			x2 = getDistance(nearestDownIndex);
			y1 = getElevationFromList(dateIndex, nearestUpIndex);
			y2 = getElevationFromList(dateIndex, nearestDownIndex);
		} else if (nearestUpIndex >= 0 && nearestDownIndex < 0 && secondNearestUpIndex >= 0) { // extrapolate
																								// downstream
			x1 = getDistance(secondNearestUpIndex);
			x2 = getDistance(nearestUpIndex);
			y1 = getElevationFromList(dateIndex, secondNearestUpIndex);
			y2 = getElevationFromList(dateIndex, nearestUpIndex);
		} else if (nearestDownIndex >= 0 && nearestUpIndex < 0 && secondNearestDownIndex >= 0) { // extrapolate
																									// upstream
			x1 = getDistance(nearestDownIndex);
			x2 = getDistance(secondNearestDownIndex);
			y1 = getElevationFromList(dateIndex, nearestDownIndex);
			y2 = getElevationFromList(dateIndex, secondNearestDownIndex);
		} else if (nearestDownIndex < 0 && secondNearestDownIndex < 0 && nearestUpIndex >= 0
				&& secondNearestUpIndex < 0) {
			interpolateElevation = false;
			elevation = getElevationFromList(dateIndex, nearestUpIndex);
		} else if (nearestDownIndex >= 0 && secondNearestDownIndex < 0 && nearestUpIndex < 0
				&& secondNearestUpIndex < 0) {
			interpolateElevation = false;
			elevation = getElevationFromList(dateIndex, nearestDownIndex);
		} else {
			if (DEBUG)
				System.out.println("no elevations found for " + date);
			interpolateElevation = false;
			elevation = -Double.MAX_VALUE;
		}
		if (interpolateElevation)
			elevation = CsdpFunctions.interpY(x1, x2, y1, y2, distance);

		return elevation;
	}// getElevation

	public String getDate(int dateIndex) {
		return _date.get(dateIndex);
	}

} // class StationTimeSeriesData
