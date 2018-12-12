package DWR.CSDP;

/**
 * Stores information needed to plot bathymetry data in cross-section view:
 * 	The number of points ("enclosed values")
 *  The indices of the bathymetry points
 *  The station and elevation values for cross-section plotting
 *  
 *  Class is instantiated and returned by the findXsectData method
 * 	These variables and methods used to be part of the BathymetryData class, but that was 
 *  resulting in XsectGraph instances sharing some data, which was creating problems when
 *  point coloring changes were made.  
 * @author btom
 *
 */
public class XsectBathymetryData {
	private ResizableIntArray _enclosedPointIndex = new ResizableIntArray();
	private int _numEnclosedValues = 0;
	private ResizableDoubleArray _enclosedStation = new ResizableDoubleArray();
	private ResizableDoubleArray _enclosedElevation = new ResizableDoubleArray();
	private static final int stationIndex = 0;
	private static final int elevationIndex = 1;

	/**
	 * stores indices of points that are to be displayed in the xsect view
	 */
	public void storeEnclosedPointIndex(int index, int value) {_enclosedPointIndex.put(index, value);}

	/**
	 * returns indices of points that are to be displayed in the xsect view
	 */
	public int getEnclosedPointIndex(int index) {return _enclosedPointIndex.get(index);}

	/**
	 * stores point to be displayed in xsect view using local coord. sys.
	 */
	public void putEnclosedStationElevation(int index, double station, double elevation) {
		_enclosedStation.put(index, station);
		_enclosedElevation.put(index, elevation);
	}

	/**
	 * stores point to be displayed in xsect view using local coord. sys.
	 */
	public void getEnclosedStationElevation(int index, double[] point) {
		point[stationIndex] = _enclosedStation.get(index);
		point[elevationIndex] = _enclosedElevation.get(index);
	}
	
	/**
	 * store number of values to be displayed in xsect view
	 */
	public void putNumEnclosedValues(int value) {_numEnclosedValues = value;}

	/**
	 * returns number of values to be displayed in xsect view
	 */
	public int getNumEnclosedValues() {return _numEnclosedValues;}
}//class XsectBathymetryData
