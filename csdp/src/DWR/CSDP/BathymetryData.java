/*
    Copyright (C) 1998 State of California, Department of Water
    Resources.

    This program is licensed to you under the terms of the GNU General
    Public License, version 2, as published by the Free Software
    Foundation.

    You should have received a copy of the GNU General Public License
    along with this program; if not, contact Dr. Francis Chung, below,
    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
    02139, USA.

    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.

    For more information, contact:

    Dr. Francis Chung
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA  95814
    916-653-5601
    chung@water.ca.gov

    or see our home page: http://wwwdelmod.water.ca.gov/
*/
package DWR.CSDP;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Random;

/**
 * Stores bathymetry data using automatically resized arrays. All coordinates
 * stored in feet.
 *
 * @author
 * @version $Id: BathymetryData.java,v 1.4 2005/04/08 00:14:10 btom Exp $
 */
public class BathymetryData {
	/*
	 * initial number of elements in source array
	 */
	int MAX_SOURCE = 1000;
	/*
	 * initial number of elements in year array
	 */
	int MAX_YEAR = 1000;

	private int _numLines;

	public BathymetryData(int numLines) {
		_numLines = numLines;
		initializeVariables(numLines);
	}// constructor

	/**
	 * Print all stored data to screen
	 */
	public void test() {
		String line = null;
		double[] point = new double[3];
		for (int i = 0; i <= getNumLines() - 1; i++) {
			getPointFeet(i, point);
			System.out.println(point[0] + " " + point[1] + " " + point[2] + " " + getSource(getSourceIndex(i)));
		}
	}// test

	/**
	 * returns total number of lines in the bathymetry data file
	 */
	public int getNumLines() {
		return _numLines;
	}// getNumLines

	/**
	 * Increments the number of years
	 */
	private void incrementNumYears() {
		numYears++;
	}// incrementNumYears

	/**
	 * Increments the number of sources
	 */
	private void incrementNumSources() {
		numSources++;
	}// incrementNumSources

	/**
	 * Return array of x,y,z values: xy in meters, z in feet
	 */
	public void getPointMetersFeet(int index, double[] point) {
		point[0] = getXMeters(index);
		point[1] = getYMeters(index);
		point[2] = getZFeet(index);
	}// getPoint

	/**
	 * Return array of x,y,z values
	 */
	public void getPointFeet(int index, double[] point) {
		point[0] = getXFeet(index);
		point[1] = getYFeet(index);
		point[2] = getZFeet(index);
	}

	/**
	 * Set array of x,y,z values. assumes x,y in meters and z in feet.
	 */
	public void setPointMetersFeet(int index, double x, double y, double z) {
		setXMeters(index, x);
		setYMeters(index, y);
		setZFeet(index, z);
	}// setPoint

	/**
	 * Set array of x,y,z values. assumes all arguments are in feet.
	 */
	public void setPointFeet(int index, double x, double y, double z) {
		setXFeet(index, x);
		setYFeet(index, y);
		setZFeet(index, z);
	}// setPoint

	/**
	 * Set array of x,y,z values. assumes all arguments are in meters.
	 */
	public void setPointMeters(int index, double x, double y, double z) {
		setXMeters(index, x);
		setYMeters(index, y);
		setZMeters(index, z);
	}// setPoint

	/**
	 * Set value of year
	 */
	public void putYear(int index, short value) {
		year.put(index, value);
		_plotYear.put(index, true);
		incrementNumYears();
	}// putYear

	/**
	 * Return value of year
	 */
	public short getYear(int index) {
		return year.get(index);
	}// getYear

	/**
	 * sets plotting option for year at specified index
	 */
	public void putPlotYear(int index, boolean value) {
		_plotYear.put(index, value);
	}// putPlotYear

	/**
	 * gets plotting option for year at specified index
	 */
	public boolean getPlotYear(int index) {
		return (_plotYear.get(index)).booleanValue();
	}// getPlotYear

	/**
	 * Return value of numSources
	 */
	public short getNumSources() {
		return numSources;
	}// getNumSources

	/**
	 * sets plotting option for source at specified index
	 */
	public void putPlotSource(int index, boolean value) {
		_plotSource.put(index, value);
	}// putPlotSource

	/**
	 * gets plotting option for source at specified index
	 */
	public boolean getPlotSource(int index) {
		return (_plotSource.get(index)).booleanValue();
	}// getPlotSource

	/**
	 * Return value of numYears
	 */
	public short getNumYears() {
		return numYears;
	}// getNumYears

	/**
	 * Return value of source
	 */
	public String getSource(int index) {
		return source.get(index);
	}// getSource

	/**
	 * Put value of source
	 */
	public void putSource(int index, String value) {
		source.put(index, value);
		_plotSource.put(index, true);
		incrementNumSources();
	}// putSource

	/**
	 * Set value of yearIndex
	 */
	public void putYearIndex(int index, int value) {
		yearIndex[index] = value;
	}// putYearIndex

	/**
	 * Return value of yearIndex
	 */
	public int getYearIndex(int index) {
		return yearIndex[index];
	}// getYearIndex

	/**
	 * sorts year array, adjusts values in yearIndex array
	 */
	public void sortYearIndices() {
		int left = 0;
		int right = getNumYears() - 1;
		ResizableShortArray oldArray = new ResizableShortArray();

		for (int i = 0; i <= getNumYears() - 1; i++) {
			oldArray.put(i, getYear(i));
		} // for

		ResizableShortArray newArray = CsdpFunctions.qsort(left, right, year);
		for (int i = 0; i <= getNumLines() - 1; i++) {
			int j = 0;
			while (oldArray.get(getYearIndex(i)) != newArray.get(j))
				j++;
			putYearIndex(i, j);
		} // for

	}// sortYearIndices

	public int[] getYearIndexArray() {
		return yearIndex;
	}

	/**
	 * Set value of sourceIndex
	 */
	public void putSourceIndex(int index, int value) {
		sourceIndex[index] = value;
	}// putSourceIndex

	/**
	 * Return value of yearIndex
	 */
	public int getSourceIndex(int index) {
		return sourceIndex[index];
	}// getSourceIndex

	/**
	 * Store min value of X in feet
	 */
	public void putMinXFeet(double value) {
		minX = value;
	}// putMinX

	/**
	 * Return min value of X in feet
	 */
	public double getMinXFeet() {
		return minX;
	}// getMinX

	/**
	 * Store max value of X in feet
	 */
	public void putMaxXFeet(double value) {
		maxX = value;
	}// putMaxX

	/**
	 * Return max value of X in feet
	 */
	public double getMaxXFeet() {
		return maxX;
	}// getMaxX

	/**
	 * Store min value of Y in feet
	 */
	public void putMinYFeet(double value) {
		minY = value;
	}// putMinY

	/**
	 * Return min value of Y in feet
	 */
	public double getMinYFeet() {
		return minY;
	}// getMinY

	/**
	 * Store max value of Y in feet
	 */
	public void putMaxYFeet(double value) {
		maxY = value;
	}// putMaxY

	/**
	 * Return max value of Y in feet
	 */
	public double getMaxYFeet() {
		return maxY;
	}// getMaxY

	/**
	 * Store min value of Z in feet
	 */
	public void putMinZFeet(double value) {
		minZ = value;
	}// putMinZ

	/**
	 * Return min value of Z in feet
	 */
	public double getMinZFeet() {
		return minZ;
	}// getMinZ

	/**
	 * Store max value of Z in feet
	 */
	public void putMaxZFeet(double value) {
		maxZ = value;
	}// putMaxZ

	/**
	 * Return max value of Z in feet
	 */
	public double getMaxZFeet() {
		return maxZ;
	}// getMaxZ

	/**
	 * Store min value of X in meters
	 */
	public void putMinXMeters(double value) {
		minX = CsdpFunctions.metersToFeet(value);
	}// putMinX

	/**
	 * Return min value of X in meters
	 */
	public double getMinXMeters() {
		return CsdpFunctions.feetToMeters(minX);
	}// getMinX

	/**
	 * Store max value of X in meters
	 */
	public void putMaxXMeters(double value) {
		maxX = CsdpFunctions.metersToFeet(value);
	}// putMaxX

	/**
	 * Return max value of X in meters
	 */
	public double getMaxXMeters() {
		return CsdpFunctions.feetToMeters(maxX);
	}// getMaxX

	/**
	 * Store min value of Y in meters
	 */
	public void putMinYMeters(double value) {
		minY = CsdpFunctions.metersToFeet(value);
	}// putMinY

	/**
	 * Return min value of Y in meters
	 */
	public double getMinYMeters() {
		return CsdpFunctions.feetToMeters(minY);
	}// getMinY

	/**
	 * Store max value of Y in meters
	 */
	public void putMaxYMeters(double value) {
		maxY = CsdpFunctions.metersToFeet(value);
	}// putMaxY

	/**
	 * Return max value of Y in meters
	 */
	public double getMaxYMeters() {
		return CsdpFunctions.feetToMeters(maxY);
	}// getMaxY

	/**
	 * Store min value of Z in meters--probably never used
	 */
	public void putMinZMeters(double value) {
		minZ = CsdpFunctions.metersToFeet(value);
	}// putMinZ

	/**
	 * Return min value of Z in meters--probably never used
	 */
	public double getMinZMeters() {
		return CsdpFunctions.feetToMeters(minZ);
	}// getMinZ

	/**
	 * Store max value of Z in meters--probably never used
	 */
	public void putMaxZMeters(double value) {
		maxZ = CsdpFunctions.metersToFeet(value);
	}// putMaxZ

	/**
	 * Return max value of Z in meters--probably never used
	 */
	public double getMaxZMeters() {
		return CsdpFunctions.feetToMeters(maxZ);
	}// getMaxZ

	/**
	 * Returns the string representation of the first five points
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		// int nPoints = getNumberOfDataPoints();
		int nPoints = 5;
		buf.append("Number of Data Points: ").append(nPoints).append("\n");
		for (int i = 0; i < Math.min(nPoints, 5); i++) {
			buf.append("Point ").append(i).append(" = ").append("\n");
			buf.append(" X: ").append(getXFeet(i)).append(", ");
			buf.append(" Y: ").append(getYFeet(i)).append(", ");
			buf.append(" Z: ").append(getZFeet(i)).append(", ").append("\n");
		}
		return buf.toString();
	}// toString

	/**
	 * sorts bathymetry data by x and y
	 */
	public void sortBathymetryData() {
		System.out.println("sorting data...");
		int left = 0;
		int right = getNumLines();
		qsort(left, right - 1);
	} // sortBathymetryData

	/**
	 * finds all bathymetry data points that are within the specified polygon.
	 * this is used to select points to be plotted in cross-section view
	 */
	public void findXsectData(Hashtable displayData) {

		Polygon displayRegion = (Polygon) (displayData.get("xsectDisplayRegion"));

		if (DEBUG)
			System.out.println("polygon coordinates:");
		for (int i = 0; i <= displayRegion.npoints - 1; i++) {
			if (DEBUG)
				System.out.println(displayRegion.xpoints[i] + "," + displayRegion.ypoints[i]);
		}

		double[] centerlineSegmentEndpoints = (double[]) (displayData.get("centerlineSegmentEndpoints"));
		Rectangle r = displayRegion.getBounds();
		Point point = new Point();
		int numValues = 0;
		int i = 0;
		int yearIndex = 0;
		int sourceIndex = 0;

		// find the closest point that has a y value that is below the
		// rectangle.
		// if it's the first point (your region extends beyond the southern
		// boundary)
		// then i will be zero...
		i = CsdpFunctions.interpolationSearch(y, r.y, getNumLines());
		if (i >= 1)
			i -= 1;
		if (i > 1 && getYFeet(i - 1) > r.y) {
			System.out.println("ERROR in BathymetryData.findXsectData");
		}
		while (getYFeet(i) < r.y && i < getNumLines()) {
			i++;
		}
		double yFinal = getYFeet(i);
		while (i - 1 > 0 && getYFeet(i - 1) == yFinal) {
			i--;
		}

		int firstIndex = i;
		while (i < getNumLines() - 1 && (getYFeet(i) > 0.0f) && (getYFeet(i) < (r.y + r.height))) {
			i++;
			if (DEBUG)
				System.out.println(
						"found value; index,y, r.y, r.height= " + i + "," + getYFeet(i) + "," + r.y + "," + r.height);
		}
		int lastIndex = i;

		if (DEBUG)
			System.out.println("firstIndex, lastIndex" + firstIndex + "," + lastIndex);

		for (int j = firstIndex; j <= lastIndex; j++) {
			yearIndex = getYearIndex(j);
			sourceIndex = getSourceIndex(j);
			point.setLocation((int) getXFeet(j), (int) getYFeet(j));
			if (displayRegion.contains(point) && getPlotYear(yearIndex) && getPlotSource(sourceIndex)) {
				// System.out.println("saving point");
				storeEnclosedPointIndex(numValues, j);
				// System.out.println("storing values
				// "+getX(j)+","+getY(j)+","+getZ(j));
				numValues++;
			} // if contains point
		} // for i
		putNumEnclosedValues(numValues);
		rotateXsectData(centerlineSegmentEndpoints);
	}// findXsectData

	/**
	 * Find maximum and minimum values of x,y, and z and store them.
	 */
	public void findMaxMin(int numLines) {
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		double[] point = new double[3];
		getPointFeet(0, point);
		double minX = point[CsdpFunctions.xIndex];
		double maxX = point[CsdpFunctions.xIndex];
		double minY = point[CsdpFunctions.yIndex];
		double maxY = point[CsdpFunctions.yIndex];
		double minZ = point[CsdpFunctions.zIndex];
		double maxZ = point[CsdpFunctions.zIndex];
		for (int i = 0; i <= numLines - 1; i++) {
			getPointFeet(i, point);
			x = point[CsdpFunctions.xIndex];
			y = point[CsdpFunctions.yIndex];
			z = point[CsdpFunctions.zIndex];
			if (x != 0)
				minX = Math.min(x, minX);
			if (x != 0)
				maxX = Math.max(x, maxX);
			if (y != 0)
				minY = Math.min(y, minY);
			if (y != 0)
				maxY = Math.max(y, maxY);
			minZ = Math.min(z, minZ);
			maxZ = Math.max(z, maxZ);
		}
		putMinXFeet(minX - CsdpFunctions.BORDER_THICKNESS);
		putMaxXFeet(maxX + CsdpFunctions.BORDER_THICKNESS);
		putMinYFeet(minY - CsdpFunctions.BORDER_THICKNESS);
		putMaxYFeet(maxY + CsdpFunctions.BORDER_THICKNESS);
		putMinZFeet(minZ);
		putMaxZFeet(maxZ);
	}// findMaxMin

	/**
	 * Converts bathymetry data (which is enclosed in xsect plot region) to
	 * local coordinate system with intersection of centerline and xsect line as
	 * its origin.
	 */
	private void rotateXsectData(double[] endpoints) {

		/*
		 * x1, y1 are coord. of upstream centerline segment point. x2, y2 are
		 * coord. of downstream centerline segment point. x3, y3 are coord. of
		 * data point
		 */
		double x1 = endpoints[x1Index];
		double x2 = endpoints[x2Index];
		double x3;
		double y1 = endpoints[y1Index];
		double y2 = endpoints[y2Index];
		double y3;
		double z;
		double dist;
		double sign;

		if (DEBUG)
			System.out.println("centerline coord:" + x1 + "," + y1 + "," + x2 + "," + y2);
		for (int i = 0; i <= getNumEnclosedValues() - 1; i++) {
			x3 = getXFeet(getEnclosedPointIndex(i));
			y3 = getYFeet(getEnclosedPointIndex(i));
			dist = CsdpFunctions.shortestDistLine(x1, x2, x3, y1, y2, y3);

			if (DEBUG)
				System.out.println("x3,y3,shortestDist = " + x3 + "," + y3 + "," + dist);

			sign = getSign(x1, x2, x3, y1, y2, y3);
			putEnclosedStationElevation(i, sign * dist, getZFeet(getEnclosedPointIndex(i)));
		}

	}// rotateXsectData

	/**
	 * return +1 if point is closer to right bank (sin will be >0) return -1 if
	 * point is closer to left bank (sin will be <0)
	 */
	private int getSign(double x1, double x2, double x3, double y1, double y2, double y3) {
		int s = 0;
		double theta = CsdpFunctions.getTheta(x1, x2, x3, y1, y2, y3);
		if (Math.sin(theta) >= 0)
			s = 1;
		if (Math.sin(theta) < 0)
			s = -1;
		return s;
	}// getSign

	/**
	 * Get value of x in feet
	 */
	private double getXFeet(int index) {
		return x[index];
	}

	/**
	 * Set value of x in feet
	 */
	private void setXFeet(int index, double value) {
		x[index] = value;
	}

	/**
	 * Return value of y in feet
	 */
	private double getYFeet(int index) {
		// float returnValue = -CsdpFunctions.BIG_FLOAT;
		// if(index >= y.getSize()){
		// JOptionPane.showOptionDialog
		// (null, "ERROR! ATTEMPT TO ACCESS ARRAY ELEMENT THAT DOES NOT EXIST",
		// "UNABLE TO COMPLY",
		// JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
		// _options, _options[0]);
		// }else returnValue = y.get(index);
		// return returnValue;
		return y[index];
	}

	/**
	 * Set value of y in feet
	 */
	private void setYFeet(int index, double value) {
		y[index] = value;
	}

	/**
	 * Return value of z
	 */
	private double getZFeet(int index) {
		return z[index];
	}

	/**
	 * Get value of z in feet
	 */
	private void setZFeet(int index, double value) {
		z[index] = value;
	}

	/**
	 * Get value of x in meters
	 */
	private double getXMeters(int index) {
		return CsdpFunctions.feetToMeters(x[index]);
	}

	/**
	 * Set value of x in meters
	 */
	private void setXMeters(int index, double value) {
		x[index] = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * Return value of y in meters
	 */
	private double getYMeters(int index) {
		return CsdpFunctions.feetToMeters(y[index]);
	}

	/**
	 * Set value of y in meters
	 */
	private void setYMeters(int index, double value) {
		y[index] = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * Return value of z
	 */
	private double getZMeters(int index) {
		return CsdpFunctions.feetToMeters(z[index]);
	}

	/**
	 * Get value of z in meters
	 */
	private void setZMeters(int index, double value) {
		z[index] = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * quicksort
	 */
	private void qsort(int left, int right) {
		int last = 0;
		if (left < right) {
			Random rand = new Random();
			int pivotIndex = left + rand.nextInt(right - left + 1);
			swap(x, left, pivotIndex);
			swap(y, left, pivotIndex);
			swap(z, left, pivotIndex);
			swap(yearIndex, left, pivotIndex);
			swap(sourceIndex, left, pivotIndex);
			swap(descriptionIndex, left, pivotIndex);
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (y[i] < y[left]) {
					last++;
					swap(x, last, i);
					swap(y, last, i);
					swap(z, last, i);
					swap(yearIndex, last, i);
					swap(sourceIndex, last, i);
					swap(descriptionIndex, last, i);
				} // if
			} // for i
			swap(x, left, last);
			swap(y, left, last);
			swap(z, left, last);
			swap(yearIndex, left, last);
			swap(sourceIndex, left, last);
			swap(descriptionIndex, left, last);
			qsort(left, last - 1);
			qsort(last + 1, right);
		} // if
	}// qsort

	private void swap(double[] a, int i, int j) {
		double t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	private void swap(int[] a, int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	private void swap(short[] a, int i, int j) {
		short t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	/**
	 * swap two double values in array
	 */
	private void swap(ResizableDoubleArray a, int i, int j) {
		double t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * swap two int values in array
	 */
	private void swap(ResizableIntArray a, int i, int j) {
		int t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * swap two short values in array
	 */
	private void swap(ResizableShortArray a, int i, int j) {
		short t = a.get(i);
		a.put(i, a.get(j));
		a.put(j, t);
	}// swap

	/**
	 * called by contructor and called when new bathymetry loaded
	 */
	public void initializeVariables(int numLines) {
		x = new double[numLines];
		y = new double[numLines];
		z = new double[numLines];
		yearIndex = new int[numLines];
		sourceIndex = new int[numLines];
		descriptionIndex = new int[numLines];

		year = new ResizableShortArray(MAX_YEAR, 5);
		source = new ResizableStringArray(MAX_SOURCE, 5);
		_plotYear = new ResizableBooleanArray(MAX_YEAR, 5);
		_plotSource = new ResizableBooleanArray(MAX_YEAR, 5);
		// description = new String[];
		numYears = 0;
		numSources = 0;
		// _numLines = 0;
		minX = Double.MAX_VALUE;
		maxX = Double.MIN_VALUE;
		minY = Double.MAX_VALUE;
		maxY = Double.MIN_VALUE;
	}// initialize variables

	/**
	 * store number of values to be displayed in xsect view
	 */
	private void putNumEnclosedValues(int value) {
		_numEnclosedValues = value;
	}

	/**
	 * returns number of values to be displayed in xsect view
	 */
	public int getNumEnclosedValues() {
		return _numEnclosedValues;
	}

	/**
	 * stores indices of points that are to be displayed in the xsect view
	 */
	private void storeEnclosedPointIndex(int index, int value) {
		_enclosedPointIndex.put(index, value);
	}

	/**
	 * returns indices of points that are to be displayed in the xsect view
	 */
	public int getEnclosedPointIndex(int index) {
		return _enclosedPointIndex.get(index);
	}

	/**
	 * stores point to be displayed in xsect view using local coord. sys.
	 */
	private void putEnclosedStationElevation(int index, double station, double elevation) {
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

	// /**
	// * compare metadata object to default metadata values.
	// * If different, convert coordinates using semmscon.
	// */
	// public void convertToDefaultDatum(){
	// //hor and ver units should now be stored in US Survey feet.
	// short utm83_units = 1;
	// short utm27_units = 1;
	// short utm83_zone = 10;
	// short utm27_zone = 10;
	// CsdpFileMetadata m = CsdpFunctions.getBathymetryMetadata();
	// CsdpFileMetadata dm = CsdpFunctions.getPreferredMetadata();
	// int defaultHDatum = dm.getHDatum();
	// int currentHDatum = m.getHDatum();
	// int defaultVDatum = dm.getVDatum();
	// int currentVDatum = m.getVDatum();
	// int currentZone = m.getHZone();
	// int defaultZone = dm.getHZone();

	// if(currentZone != defaultZone){
	// //zone always expected to be 10
	// System.out.println("BathymetryData.convertToDefaultDatum: currentZone is
	// different from default zone. not converting coordinates");
	// }else{
	// if(m.getHDatum()==dm.getHDatum() && m.getVDatum()==dm.getVDatum()){
	// System.out.println("BathymetryData.convertToDefaultDatum: already in "+
	// "default datum. Not converting horizontal coordinates.");
	// }else{
	// System.out.println("m.getHDatum, dm.getHDatum, m.getVDatum,
	// dm.getVDatum="+
	// m.getHDatum()+","+dm.getHDatum()+","+m.getVDatum()+","+dm.getVDatum());

	// System.out.println("Converting coordinates to default datum. ");
	// System.out.println("There will be loss of precision. You should consider
	// converting ");
	// System.out.println("bathymetry to the default datum before loading into
	// the csdp");
	// if(defaultHDatum==CsdpFileMetadata.UTMNAD27){
	// for(int i=0; i<=getNumLines()-1; i++){
	// float[] point = getPointFeet(i);
	// double[] utm83 =
	// CsdpFunctions._us.utm27ToUtm83((double)point[CsdpFunctions.xIndex],
	// (double)point[CsdpFunctions.yIndex],
	// utm27_zone, utm27_units,
	// utm83_zone, utm83_units);
	// setXFeet(i,(float)utm83[CsdpFunctions.xIndex]);
	// setYFeet(i,(float)utm83[CsdpFunctions.yIndex]);
	// }
	// }//if need to convert h coord
	// if(m.getVDatum() == dm.getVDatum()){
	// System.out.println("BathymetryData.convertToDefaultDatum: already in "+
	// "NAVD1988. Not converting vertical coordinates");
	// }else{
	// for(int i=0; i<=getNumLines()-1; i++){
	// float[] point = getPointFeet(i);
	// double navd88 = CsdpFunctions._us.ngvd29_to_navd88_utm83
	// ((double)point[CsdpFunctions.xIndex],
	// (double)point[CsdpFunctions.yIndex],
	// utm83_zone, utm83_units, (double)point[CsdpFunctions.zIndex],
	// utm83_units,
	// utm83_units);
	// setZFeet(i, (float)navd88);
	// // System.out.println("converted vertical datum. x,y,zBefore, zAfter="+
	// // CsdpFunctions.feetToMeters(point[CsdpFunctions.xIndex])+","+
	// // CsdpFunctions.feetToMeters(point[CsdpFunctions.yIndex])+","+
	// // point[CsdpFunctions.zIndex]+","+navd88);
	// }//for i: convert all points
	// }//if vertical datum the same of not
	// }//if need to do any convertion
	// }//if zone is 10 or not.
	// }//convertToDefaultDatum

	private double[] _returnPoint = new double[2];
	private ResizableIntArray _enclosedPointIndex = new ResizableIntArray();
	private int _numEnclosedValues = 0;
	private ResizableDoubleArray _enclosedStation = new ResizableDoubleArray();
	private ResizableDoubleArray _enclosedElevation = new ResizableDoubleArray();

	/**
	 * number of unique year values in bathymetry data set
	 */
	private short numYears = 0;
	/**
	 * number of unique source values in bathymetry data set
	 */
	private short numSources = 0;
	// /**
	// * number of points in bathymetry data set
	// */
	// private int _numLines = 0;

	/**
	 * minimum x(UTM) value
	 */
	private double minX = Double.MAX_VALUE;
	/**
	 * maximum x(UTM) value
	 */
	private double maxX = Double.MIN_VALUE;
	/**
	 * minimum y(UTM) value
	 */
	private double minY = Double.MAX_VALUE;
	/**
	 * maximum y(UTM) value
	 */
	private double maxY = Double.MIN_VALUE;
	/**
	 * minimum z(UTM) value
	 */
	private double minZ = Double.MAX_VALUE;
	/**
	 * maximum y(UTM) value
	 */
	private double maxZ = Double.MIN_VALUE;

	private final int x1Index = 0;
	private final int y1Index = 1;
	private final int x2Index = 2;
	private final int y2Index = 3;
	private static final int stationIndex = 0;
	private static final int elevationIndex = 1;
	private static final boolean DEBUG = false;

	/*
	 * the easting values
	 */
	private double[] x = null;
	/*
	 * the northing values
	 */
	private double[] y = null;
	/*
	 * the elevation values
	 */
	private double[] z = null;
	/*
	 * the array index of the year
	 */
	private int[] yearIndex;
	/*
	 * the array index of the source
	 */
	private int[] sourceIndex;
	/*
	 * the array index of the description
	 */
	private int[] descriptionIndex;

	/**
	 * Stores the year that the data were collected.
	 */
	private ResizableShortArray year = null;

	/**
	 * Stores the source (name of agency) of the bathymetry data.
	 */
	private ResizableStringArray source = null;

	/**
	 * true if data from this year should be plotted
	 */
	private ResizableBooleanArray _plotYear = null;
	/**
	 * true if data from this source should be plotted
	 */
	private ResizableBooleanArray _plotSource = null;

	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };

}// BathymetryData
