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

import java.awt.Polygon;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import DWR.CSDP.semmscon.UseSemmscon;

/**
 * A network contains Centerlines which can contain cross-sections
 *
 * @author
 * @version
 */
public class Network {
	/**
	 * constructor
	 */
	public Network(String name, CsdpFrame gui) {
		_networkName = name;
		_gui = gui;
	}

	public boolean centerlineExists(String name) {
		boolean returnValue = false;
		if (_centerlines.containsKey(name)) {
			returnValue = true;
		} else {
			returnValue = false;
		}
		return returnValue;
	}

	/**
	 * returns name of network
	 */
	public String getNetworkName() {
		return _networkName;
	}

	/**
	 * Returns number of Centerlines
	 */
	public int getNumCenterlines() {
		return _numCenterlines;
	}

	/**
	 * Stores number of Centerlines
	 */
	public void putNumCenterlines(int value) {
		_numCenterlines = value;
	}

	/**
	 * returns Centerline object
	 */
	public Centerline getCenterline(String name) {
		return _centerlines.get(name);
	}

	/*
	 * This is for centerlines whose name begins with an index (number) followed by an underscore
	 */
	public Centerline getCenterlineByNameOrIndex(String name) {
		Centerline returnValue = null;
		if(_centerlines.containsKey(name)) {
			returnValue = _centerlines.get(name);
		}else {
			//find a centerline name that has the specified "name" at the beginning
			for(int i=0; i<_centerlineNames.getSize(); i++) {
				String centerlineName = _centerlineNames.get(i);
				if(centerlineName.indexOf(name)==0) {
					returnValue = getCenterline(centerlineName);
					break;
				}
			}
		}
		return returnValue;
	}
	
	/**
	 * sort centerline names lexicographically
	 */
	public void sortCenterlineNames() {
		_centerlineNames = CsdpFunctions.qsort(_centerlineNames, 0, getNumCenterlines() - 1);
	}// sortCenterlineNames

	/**
	 * print listing of all Centerline objects
	 */
	public Enumeration getAllCenterlines() {
		return _centerlines.elements();
	}

	/**
	 * create new Centerline object
	 */
	public void addCenterline(String name) {
		// if there is already a centerline with the name, erase the old one.
		if (_centerlines.containsKey(name)) {
			removeCenterline(name);
		}
		Centerline cent = new Centerline(name);
		_centerlines.put(name, cent);
		putCenterlineName(name);
		_numCenterlines++;
	}// addCenterline

	/**
	 * rename Centerline
	 */
	public void renameCenterline(String oldName, String newName) {
		Centerline cent = getCenterline(oldName);
		int index = 0;
		_centerlines.put(newName, cent);
		removeCenterline(oldName);
		for (int i = 0; i <= getNumCenterlines(); i++) {
			if (getCenterlineName(i).equals(oldName)) {
				index = i;
			} // if
		} // for
		_centerlineNames.put(index, newName);
		_numCenterlines++;
		setSelectedCenterline(cent);
		setSelectedCenterlineName(newName);
		setIsUpdated(true);
	}// renameCenterline

	/**
	 * removes Centerline from hashtable
	 */
	public void removeCenterline(String name) {

		if (DEBUG)
			System.out.println("about to remove centerline: " + name);
		String centerlineName = null;
		Centerline centerline = null;

		Object value = _centerlines.remove(name);
		_centerlineNames.removeElement(name);
		setIsUpdated(true);
		setSelectedCenterline(null);
		setSelectedXsect(null);
		setSelectedCenterlineName(null);
		// setSelectedXsectNum = 0;
		_numCenterlines--;

	}// removeCenterline

	/**
	 * prints all network data to screen
	 */
	public void printNetwork() {
		System.out.println("data for network " + getNetworkName());
		System.out.println(getNumCenterlines());
		String line = null;
		Centerline centerline;
		CenterlinePoint cPoint;
		Xsect xsect;
		XsectPoint xPoint;

		for (int i = 0; i <= getNumCenterlines() - 1; i++) {
			centerline = getCenterline(getCenterlineName(i));
			line = getCenterlineName(i) + " ";
			line += centerline.getNumCenterlinePoints() + " ";
			for (int j = 0; j <= centerline.getNumCenterlinePoints() - 1; j++) {
				cPoint = centerline.getCenterlinePoint(j);
				line += cPoint.getXFeet() + ",";
				line += cPoint.getYFeet() + " ";
			} // for j
			line += centerline.getNumXsects();
			System.out.println(line);
			for (int k = 0; k <= getCenterline(getCenterlineName(i)).getNumXsects() - 1; k++) {
				xsect = centerline.getXsect(k);
				line = xsect.getNumPoints() + " ";
				for (int m = 0; m <= xsect.getNumPoints() - 1; m++) {
					xPoint = xsect.getXsectPoint(m);
					line += xPoint.getStationFeet() + ",";
					line += xPoint.getElevationFeet() + " ";
				} // for m
				line += xsect.getDistAlongCenterlineFeet() + " ";
				line += xsect.getXsectLineLengthFeet();
				System.out.println(line);
				line = null;
			} // for k
			System.out.println();
		} // for i
	}// printNetwork

	/**
	 * true if network has been changed:warning will be displayed before ending
	 * program
	 */
	public boolean isUpdated() {
		return _isUpdated;
	}

	/**
	 * set to true whenever network is changed
	 */
	public void setIsUpdated(boolean value) {
		_isUpdated = value;

		if (DEBUG)
			System.out.println("setting is updated to " + value);

	}

	/**
	 * find 4 points that define polygon(rectangle) of data to be displayed The
	 * origin of the xsect plot will be the intersection of the centerline
	 * segment and the cross-section line. Data will be displayed from left to
	 * right bank, assuming that the first point in the centerline segment is
	 * upstream from the second point (using the direction defined in the DSM
	 * grid)
	 */
	public Hashtable findXsectDisplayRegion(String centerlineName, int xsectNum, double thickness) {
		if (DEBUG)
			System.out.println("finding xsect display region");
		Centerline centerline = getCenterline(centerlineName);
		Xsect xsect = centerline.getXsect(xsectNum);
		Polygon xsectDisplayRegion = null;
		Hashtable returnValues = new Hashtable();
		double[] xsectEndpoints;
		double[] centerlineSegmentEndpoints;
		double distToUpperLeftVertex;
		double distToLowerLeftVertex;
		double[] vertexXRel = new double[4];
		double[] vertexYRel = new double[4];
		double[] vertexX = new double[4];
		double[] vertexY = new double[4];
		int[] vertexXInt = new int[4];
		int[] vertexYInt = new int[4];
		double xsectLineLength;
		double distAlongSegment;
		/*
		 * angle of centerline
		 */
		double theta;
		double thetaUpperVertex;
		double lineSegmentLength;
		double thetaLowerVertex;
		double slope;
		/*
		 * 1. find endpoints of centerline segment that contains xsect 2. find
		 * endpoints of xsect line 3. find rectangle using cross-section
		 * thickness
		 */
		centerlineSegmentEndpoints = findCenterlineSegmentCoord(centerlineName, xsectNum);
		xsectEndpoints = findXsectLineCoord(centerlineName, xsectNum);

		if (DEBUG)
			System.out.println(
					"centerlineSegmentEndpoints=" + centerlineSegmentEndpoints[0] + " " + centerlineSegmentEndpoints[1]
							+ " " + centerlineSegmentEndpoints[2] + " " + centerlineSegmentEndpoints[3]);
		/*
		 * find coordinates of upstream, downstream, left, and right boundaries
		 * of rectangle which is rotated so that the upstream centerline segment
		 * point is at the origin and the downstream centerline segment point is
		 * on the positive y axis. After finding relative coordinates, translate
		 * all points back to original orientation.
		 */
		distAlongSegment = xsect.getDistAlongCenterlineFeet() - centerlineSegmentEndpoints[distIndex];
		if (DEBUG)
			System.out.println("distAlongSegment=" + distAlongSegment);

		xsectLineLength = xsect.getXsectLineLengthFeet();

		lineSegmentLength = CsdpFunctions.pointDist(centerlineSegmentEndpoints[CsdpFunctions.x1Index],
				centerlineSegmentEndpoints[CsdpFunctions.y1Index], centerlineSegmentEndpoints[CsdpFunctions.x2Index],
				centerlineSegmentEndpoints[CsdpFunctions.y2Index]);

		theta = CsdpFunctions.getTheta(centerlineSegmentEndpoints[CsdpFunctions.x1Index],
				centerlineSegmentEndpoints[CsdpFunctions.x2Index], centerlineSegmentEndpoints[CsdpFunctions.y1Index],
				centerlineSegmentEndpoints[CsdpFunctions.y2Index]);
		if (DEBUG)
			System.out.println("theta,x1x2y1y2=" + theta + " " + centerlineSegmentEndpoints[CsdpFunctions.x1Index] + ","
					+ centerlineSegmentEndpoints[CsdpFunctions.x2Index] + ","
					+ centerlineSegmentEndpoints[CsdpFunctions.y1Index] + ","
					+ centerlineSegmentEndpoints[CsdpFunctions.y2Index]);

		xsectDisplayRegion = CsdpFunctions.findPolygon(centerlineSegmentEndpoints[CsdpFunctions.x1Index],
				centerlineSegmentEndpoints[CsdpFunctions.x2Index], centerlineSegmentEndpoints[CsdpFunctions.y1Index],
				centerlineSegmentEndpoints[CsdpFunctions.y2Index], distAlongSegment, thickness, xsectLineLength);

		//Polygon object
		returnValues.put("xsectDisplayRegion", xsectDisplayRegion);
		//double[] array
		returnValues.put("centerlineSegmentEndpoints", centerlineSegmentEndpoints);

		return returnValues;
	}// findXsectDisplayRegion

	/**
	 * find coordinates of the line segment in the centerline that contains the
	 * xsect also finds the distance from the first pt in the centerline to the
	 * point that is upstream of the xsect
	 */
	protected double[] findCenterlineSegmentCoord(String centerlineName, int xsectNum) {
		double[] returnValues = new double[5];
		Centerline centerline = getCenterline(centerlineName);
		Xsect xsect = centerline.getXsect(xsectNum);
		double xsectDistAlong = xsect.getDistAlongCenterlineFeet();
		CenterlinePoint cp = null;
		CenterlinePoint cpPrevious = null;
		CenterlinePoint cpUpstream = null;
		CenterlinePoint cpDownstream = null;
		double dist = 0.0;

		for (int i = 1; i <= centerline.getNumCenterlinePoints() - 1 && dist < xsectDistAlong; i++) {
			cp = centerline.getCenterlinePoint(i);
			cpPrevious = centerline.getCenterlinePoint(i - 1);
			dist += CsdpFunctions.pointDist(cpPrevious.getXFeet(), cpPrevious.getYFeet(), cp.getXFeet(), cp.getYFeet());
		} // for i
		cpUpstream = cpPrevious;
		cpDownstream = cp;
		returnValues[CsdpFunctions.x1Index] = cpUpstream.getXFeet();
		returnValues[CsdpFunctions.y1Index] = cpUpstream.getYFeet();
		returnValues[CsdpFunctions.x2Index] = cpDownstream.getXFeet();
		returnValues[CsdpFunctions.y2Index] = cpDownstream.getYFeet();
		returnValues[distIndex] = dist
				- CsdpFunctions.pointDist(cpPrevious.getXFeet(), cpPrevious.getYFeet(), cp.getXFeet(), cp.getYFeet());

		return returnValues;
	}// findCenterlineSegmentCoord

	/**
	 * find the horizontal coordinates (probably utm) of the specified
	 * cross-section point
	 */
	public double[] find3DXsectPointCoord(String centerlineName, int xsectNum, int xsectPointNum) {
		double[] returnValue = new double[2];
		double[] originCoord = findXsectOriginVector(centerlineName, xsectNum);
		double xOrigin = originCoord[CsdpFunctions.x1Index];
		double yOrigin = originCoord[CsdpFunctions.y1Index];
		double xEndpoint = originCoord[CsdpFunctions.x2Index];
		double yEndpoint = originCoord[CsdpFunctions.y2Index];

		if (DEBUG) {
			System.out.println("Network.find3DXsectPointCoord");
			System.out.println(
					"centerlineName, xsectNum, xsectPointNum=" + centerlineName + "," + xsectNum + "," + xsectPointNum);
			System.out.println("xOrigin, yOrigin, xEndpoint, yEndpoint=" + xOrigin + "," + yOrigin + "," + xEndpoint
					+ "," + yEndpoint);
		}

		Centerline centerline = getCenterline(centerlineName);
		Xsect xsect = centerline.getXsect(xsectNum);
		XsectPoint xsectPoint = xsect.getXsectPoint(xsectPointNum);
		double station = xsectPoint.getStationFeet();
		double x = stationToX(xOrigin, xEndpoint, yOrigin, yEndpoint, station);
		double y = stationToY(xOrigin, xEndpoint, yOrigin, yEndpoint, station);
		returnValue[CsdpFunctions.x1Index] = x;
		returnValue[CsdpFunctions.y1Index] = y;
		return returnValue;
	}

	/**
	 * find coordinates of intersection of cross-section line and centerline.
	 * Used for 3D network output. Returns an array of 4 values: x1,y1 of the
	 * origin and x2,y2 of the endpoint of the cross-section line that is in the
	 * positive direction.
	 */
	private double[] findXsectOriginVector(String centerlineName, int xsectNum) {
		Centerline centerline = getCenterline(centerlineName);
		Xsect xsect = centerline.getXsect(xsectNum);
		double dist = xsect.getDistAlongCenterlineFeet();
		double length = xsect.getXsectLineLengthFeet();

		if (DEBUG)
			System.out.println("inside findXsectLineCoord: dist, length=" + dist + "," + length);

		CenterlinePoint point1;
		CenterlinePoint point2;
		double x1 = 0.0;
		double x2 = 0.0;
		double y1 = 0.0;
		double y2 = 0.0;
		double xOrigin = 0.0;
		double yOrigin = 0.0;
		double[] returnValues = null;
		double centerlineDist = 0.0;
		// find 2 points on either side
		int i = 0;
		for (i = 0; dist > centerlineDist && i <= centerline.getNumCenterlinePoints() - 2; i++) {
			point1 = centerline.getCenterlinePoint(i);
			point2 = centerline.getCenterlinePoint(i + 1);
			x1 = point1.getXFeet();
			x2 = point2.getXFeet();
			y1 = point1.getYFeet();
			y2 = point2.getYFeet();
			centerlineDist += CsdpFunctions.pointDist(x1, y1, x2, y2);
		} // for i dist
		if (dist < centerlineDist)
			centerlineDist -= CsdpFunctions.pointDist(x1, y1, x2, y2);
		i--;
		point1 = centerline.getCenterlinePoint(i);
		point2 = centerline.getCenterlinePoint(i + 1);
		x1 = point1.getXFeet();
		x2 = point2.getXFeet();
		y1 = point1.getYFeet();
		y2 = point2.getYFeet();

		returnValues = findXsectLineCoord(x1, y1, x2, y2, xsect, centerlineDist);
		// now replace x1,y1 with the coordinates of the intersection.
		xOrigin = CsdpFunctions.findXIntersection(x1, x2, returnValues[CsdpFunctions.x1Index], y1, y2,
				returnValues[CsdpFunctions.y1Index]);
		yOrigin = CsdpFunctions.findYIntersection(x1, x2, returnValues[CsdpFunctions.x1Index], y1, y2,
				returnValues[CsdpFunctions.y1Index]);
		returnValues[CsdpFunctions.x1Index] = xOrigin;
		returnValues[CsdpFunctions.y1Index] = yOrigin;

		if (DEBUG)
			System.out.println(
					"coordinates of points before and after xsect x1y1x2y2=" + x1 + "," + y1 + " " + x2 + "," + y2);

		return returnValues;

	}// findXsectOriginVector

	/**
	 * find coordinates of endpoints of xsect line
	 */
	public double[] findXsectLineCoord(String centerlineName, int xsectNum) {
		Centerline centerline = getCenterline(centerlineName);
		Xsect xsect = centerline.getXsect(xsectNum);
		double dist = xsect.getDistAlongCenterlineFeet();
		double length = xsect.getXsectLineLengthFeet();

		if (DEBUG)
			System.out.println("inside findXsectLineCoord: dist, length=" + dist + "," + length);

		CenterlinePoint point1;
		CenterlinePoint point2;
		double x1 = 0.0;
		double x2 = 0.0;
		double y1 = 0.0;
		double y2 = 0.0;
		double[] returnValues = null;
		double centerlineDist = 0.0;
		// find 2 points on either side
		int i = 0;
		for (i = 0; dist > centerlineDist && i <= centerline.getNumCenterlinePoints() - 2; i++) {
			point1 = centerline.getCenterlinePoint(i);
			point2 = centerline.getCenterlinePoint(i + 1);
			x1 = point1.getXFeet();
			x2 = point2.getXFeet();
			y1 = point1.getYFeet();
			y2 = point2.getYFeet();
			centerlineDist += CsdpFunctions.pointDist(x1, y1, x2, y2);
		} // for i dist
		if (dist < centerlineDist)
			centerlineDist -= CsdpFunctions.pointDist(x1, y1, x2, y2);
		i--;
		point1 = centerline.getCenterlinePoint(i);
		point2 = centerline.getCenterlinePoint(i + 1);
		x1 = point1.getXFeet();
		x2 = point2.getXFeet();
		y1 = point1.getYFeet();
		y2 = point2.getYFeet();
		returnValues = findXsectLineCoord(x1, y1, x2, y2, xsect, centerlineDist);

		if (DEBUG)
			System.out.println(
					"coordinates of points before and after xsect x1y1x2y2=" + x1 + "," + y1 + " " + x2 + "," + y2);

		return returnValues;
	}// findXsectLineCoord

	/**
	 * find coordinates of xsect line
	 */
	protected double[] findXsectLineCoord(double x1, double y1, double x2, double y2, Xsect xsect,
			double centerlineDist) {
		double[] returnValues = new double[4];
		// distance of xsect from nearest upstream centerline point
		double dist = xsect.getDistAlongCenterlineFeet() - centerlineDist;
		double length = xsect.getXsectLineLengthFeet();
		double pi = (double) Math.PI;
		double theta = 0.0; // angle of centerline segment, radians
		double thetaXsect = 0.0; // angle of cross-section line
		// coordinates of intersection of centerline segment & xsect line
		double xi = 0.0;
		double yi = 0.0;

		theta = CsdpFunctions.getTheta(x1, x2, y1, y2);

		if (DEBUG)
			System.out.println("x1y1x2y2,theta=" + x1 + "," + y1 + " " + x2 + "," + y2 + " " + theta);

		xi = x1 + dist * (double) Math.cos(theta);
		yi = y1 + dist * (double) Math.sin(theta);

		if (DEBUG)
			System.out.println("intersection coordinates x,y=" + xi + "," + yi);

		thetaXsect = theta + 0.5f * pi;

		returnValues[CsdpFunctions.x1Index] = xi + (length / 2) * (double) Math.cos(thetaXsect);
		returnValues[CsdpFunctions.y1Index] = yi + (length / 2) * (double) Math.sin(thetaXsect);
		returnValues[CsdpFunctions.x2Index] = xi + (length / 2) * (double) Math.cos(thetaXsect + pi);
		returnValues[CsdpFunctions.y2Index] = yi + (length / 2) * (double) Math.sin(thetaXsect + pi);

		return returnValues;
	}// findXsectLineCoord

	/**
	 * stores name of centerline
	 */
	protected void putCenterlineName(String name) {
		_centerlineNames.put(getNumCenterlines(), name);
	}

	/**
	 * returns name of centerline
	 */
	public String getCenterlineName(int index) {
		return _centerlineNames.get(index);
	}

	/**
	 * returns instance of centerline that has been selected
	 */
	public Centerline getSelectedCenterline() {
		return _selectedCenterline;
	}

	/**
	 * return name of selected centerline. Names will usually be DSM channel
	 * numbers, but they don't have to be.
	 */
	public String getSelectedCenterlineName() {
		return _selectedCenterlineName;
	}

	/**
	 * return number of selected cross-section. Numbers start at zero.
	 */
	public int getSelectedXsectNum() {
		return _selectedXsectNum;
	}

	/**
	 * returns instance of xsect that has been selected
	 */
	public Xsect getSelectedXsect() {
		return _selectedXsect;
	}

	/**
	 * store the name of the selected centerline. The name will usually be a DSM
	 * channel number, but not necessarily.
	 */
	public void setSelectedCenterlineName(String name) {
		_selectedCenterlineName = name;
	}

	/**
	 * store the number of the selected cross-section. The numbers start at
	 * zero.
	 */
	public void setSelectedXsectNum(int num) {
		_selectedXsectNum = num;
	}

	/**
	 * assign selected centerline to class variable
	 */
	public void setSelectedCenterline(Centerline c) {
		_selectedCenterline = c;
	}

	/**
	 * assign selected xsect to class variable
	 */
	public void setSelectedXsect(Xsect x) {
		_selectedXsect = x;
	}

	/**
	 * store name of selected centerline.
	 */
	public void setNewCenterlineName(String name) {
		_newCenterlineName = name;
	}

	public double getMinXFeet() {
		if (_isUpdated || _maxMinFound == false)
			findMaxMin();
		return _minX;
	}

	public double getMaxXFeet() {
		if (_isUpdated || _maxMinFound == false)
			findMaxMin();
		return _maxX;
	}

	public double getMinYFeet() {
		if (_isUpdated || _maxMinFound == false)
			findMaxMin();
		return _minY;
	}

	public double getMaxYFeet() {
		if (_isUpdated || _maxMinFound == false)
			findMaxMin();
		return _maxY;
	}

	public void findMaxMin() {
		_minX = Double.MAX_VALUE;
		_maxX = -Double.MAX_VALUE;
		_minY = Double.MAX_VALUE;
		_maxY = -Double.MAX_VALUE;
		Centerline centerline = null;
		CenterlinePoint cp = null;
		for (int i = 0; i <= getNumCenterlines() - 1; i++) {
			centerline = getCenterline(_centerlineNames.get(i));
			for (int j = 0; j <= centerline.getNumCenterlinePoints() - 1; j++) {
				cp = centerline.getCenterlinePoint(j);
				if (_minX > cp.getXFeet())
					_minX = cp.getXFeet();
				if (_maxX < cp.getXFeet())
					_maxX = cp.getXFeet();
				if (_minY > cp.getYFeet())
					_minY = cp.getYFeet();
				if (_maxY < cp.getYFeet())
					_maxY = cp.getYFeet();
			}
		}
	}// findMaxMin

	/**
	 * compare metadata object to default metadata values. If different, convert
	 * coordinates using semmscon. Does not do unit conversions. Assumes
	 * horizontal coord in meters and vertical in U.S. survey feet.
	 */
	public void convertToBathymetryDatum() {
		// hor and ver units should now be stored in US Survey feet.
		short utm83_units = 1;
		short utm27_units = 1;
		short elev_units = 1;
		short utm83_zone = 10;
		short utm27_zone = 10;
		CsdpFileMetadata m = CsdpFunctions.getNetworkMetadata();
		CsdpFileMetadata bm = CsdpFunctions.getBathymetryMetadata();
		int bathymetryHDatum = bm.getHDatum();
		int currentHDatum = m.getHDatum();
		int bathymetryVDatum = bm.getVDatum();
		int currentVDatum = m.getVDatum();
		int currentZone = m.getHZone();
		int bathymetryZone = bm.getHZone();

		boolean sameZone = false;
		boolean utm83to27 = false;
		boolean utm27to83 = false;
		boolean navd88toNgvd29 = false;
		boolean ngvd29toNavd88 = false;

		if (currentZone != bathymetryZone) {
			sameZone = false;
			// zone always expected to be 10
			JOptionPane.showMessageDialog(_gui, "ERROR in Network.convertToBathymetryDatum: Coordinates need "
					+ "to be converted, but I can't convert them. " + "Network.convertToBathymetryDatum: currentZone "
					+ "is different from bathymetry zone.  They should be the same!", "Error", JOptionPane.ERROR_MESSAGE);

		} else {
			sameZone = true;
		}

		// Convert horizontal coordinates
		if (bathymetryHDatum != currentHDatum) {
			if (bathymetryHDatum == CsdpFileMetadata.UTMNAD83) {
				if (currentHDatum == CsdpFileMetadata.UTMNAD27) {
					utm27to83 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "network horizontal datum is different from "
							+ "bathymetry horizontal datum, but I don't know " + "how to convert the network datum,"
							+ m.getHDatumString() + ", to the bathymetry datum, " + bm.getHDatumString(), 
							"Error", JOptionPane.ERROR_MESSAGE);
				} // if need to convert from utm nad27
			} else if (bathymetryHDatum == CsdpFileMetadata.UTMNAD27) {
				if (currentHDatum == CsdpFileMetadata.UTMNAD83) {
					utm83to27 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "network horizontal datum is different from "
							+ "bathymetry horizontal datum, but I don't know " + "how to convert the network datum,"
							+ m.getHDatumString() + ", to the bathymetry datum, " + bm.getHDatumString(), 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(_gui, "bathymetryHDatum is not nad83 or 27 "
						+ "currentHDatum, bathymetryHDatum, nad27, nad83=" + currentHDatum + "," + bathymetryHDatum
						+ "," + +CsdpFileMetadata.UTMNAD27 + "," + CsdpFileMetadata.UTMNAD83, 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// no need to convert horizontal coordinates
		} // need to convert horizontal coordinates?

		// convert vertical coordinates
		if (bathymetryVDatum != currentVDatum) {
			if (bathymetryVDatum == CsdpFileMetadata.NAVD1988) {
				if (currentVDatum == CsdpFileMetadata.NGVD1929) {
					ngvd29toNavd88 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "network vertical datum is different from "
							+ "bathymetry vertical datum, but I don't know " + "how to convert the network datum,"
							+ m.getVDatumString() + ", to the bathymetry datum, " + bm.getVDatumString(), 
							"Error", JOptionPane.ERROR_MESSAGE);
				} //
			} else if (bathymetryVDatum == CsdpFileMetadata.NGVD1929) {
				if (currentVDatum == CsdpFileMetadata.NAVD1988) {
					navd88toNgvd29 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "network vertical datum is different from "
							+ "bathymetry vertical datum, but I don't know " + "how to convert the network datum,"
							+ m.getVDatumString() + ", to the bathymetry datum, " + bm.getVDatumString(), 
							"Error", JOptionPane.ERROR_MESSAGE);
				} //
			} else {
				JOptionPane.showMessageDialog(_gui, "bathymetryVDatum is not navd88 or ngvd1929", 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// no need to convert v datum
		} // if different datum or the same

		System.out.println("sameZone, utm83to27, utm27to83, navd88toNgvd29, ngvd29toNavd88=" + sameZone + ","
				+ utm83to27 + "," + utm27to83 + "," + navd88toNgvd29 + "," + ngvd29toNavd88);

		/*
		 * Do the conversion if the horizontal datums are in the same zone Only
		 * zone 10 is expected. If network file has no zone specification, zone
		 * 10 will be assumed.
		 */
		if (sameZone) {
			if (DEBUG)
				System.out.println("mhdatum, bhdatum, mvdatum, bvdatum=" + m.getHDatum() + "," + bm.getHDatum() + ","
						+ m.getVDatum() + "," + bm.getVDatum());

			if (m.getHDatum() == bm.getHDatum() && m.getVDatum() == bm.getVDatum()) {
				if (DEBUG)
					System.out.println("Network.convertToBathymetryDatum: already in "
							+ "bathymetry datum. Not converting horizontal coordinates.");
			} else {
				System.out.println("m.getHDatum, bm.getHDatum, m.getVDatum, bm.getVDatum=" + m.getHDatum() + ","
						+ bm.getHDatum() + "," + m.getVDatum() + "," + bm.getVDatum());

				JOptionPane.showMessageDialog(_gui, "Converting network (" + m.getHDatumString() + "," + m.getVDatumString() + ")"
						+ " to bathymetry datum (" + bm.getHDatumString() + "," + bm.getVDatumString() + ")", 
						"Error", JOptionPane.ERROR_MESSAGE);
				UseSemmscon us = CsdpFunctions.getUseSemmscon();
				// for each centerline
				for (int i = 0; i <= getNumCenterlines() - 1; i++) {
					String centerlineName = getCenterlineName(i);
					Centerline c = getCenterline(centerlineName);

					// for each xsect: find vector, convert vector,
					// convert xs points, then convert points back
					// to stations using converted vector, then convert
					// elevations. Finally, convert centerline points

					// now adjust centerline coordinates if necessary.
					CenterlinePoint cp = null;
					double[] cpConverted = null;
					if (utm27to83 || utm83to27) {
						for (int n = 0; n <= c.getNumCenterlinePoints() - 1; n++) {
							cp = c.getCenterlinePoint(n);
							double x = cp.getXFeet();
							double y = cp.getYFeet();
							// horizontal conversion
							if (utm27to83) {
								cpConverted = us.utm27ToUtm83((double) x, (double) y, utm27_zone, utm27_units,
										utm83_zone, utm83_units);
							} else if (utm83to27) {
								cpConverted = us.utm83ToUtm27((double) x, (double) y, utm83_zone, utm83_units,
										utm27_zone, utm27_units);
							}

							if (cpConverted != null) {
								cp.putXFeet((double) cpConverted[CsdpFunctions.xIndex]);
								cp.putYFeet((double) cpConverted[CsdpFunctions.yIndex]);
							}
						} // for each centerline point
					}

					if (navd88toNgvd29 || ngvd29toNavd88) {
						// convert xsects
						for (int j = 0; j < c.getNumXsects(); j++) {
							Xsect x = c.getXsect(j);
							for (int k = 0; k < x.getNumPoints(); k++) {
								XsectPoint xp = x.getXsectPoint(k);
								double[] xsectLineCoord = findXsectLineCoord(centerlineName, j);
								double x1 = xsectLineCoord[CsdpFunctions.x1Index];
								double y1 = xsectLineCoord[CsdpFunctions.y1Index];
								double x2 = xsectLineCoord[CsdpFunctions.x2Index];
								double y2 = xsectLineCoord[CsdpFunctions.y2Index];

								double station = xp.getStationFeet();
								double theta = CsdpFunctions.getTheta(x1, x2, y1, y2);
								double x3 = x1 + (double) Math.cos(theta) * station;
								double y3 = y1 + (double) Math.sin(theta) * station;
								double elev = xp.getElevationFeet();
								double elevConverted = Double.MAX_VALUE;
								// vertical conversion
								// double newX = (double)(cp.getXFeet());
								// double newY = (double)(cp.getYFeet());
								if (bathymetryHDatum == CsdpFileMetadata.UTMNAD83) {
									// if converting one vert datum or the other
									if (navd88toNgvd29) {
										elevConverted = us.navd88_to_ngvd29_utm83(x3, y3, utm83_zone, utm83_units, elev,
												elev_units, elev_units);
										// System.out.println("Network: after
										// converting 88 to 29: elev88,
										// elev29="+elev+","+elevConverted);
									} else if (ngvd29toNavd88) {
										elevConverted = us.ngvd29_to_navd88_utm83(x3, y3, utm83_zone, utm83_units, elev,
												elev_units, elev_units);
										// System.out.println("Network: after
										// converting 29 to 88: elev88,
										// elev29="+elev+","+elevConverted);

									} else {
										JOptionPane.showMessageDialog(_gui, "ERROR in Network.convertToBathymetryDatum: cross-section conversion: "
												+ "Bathymetry H Datum is UTMNAD83, but requested conversion is not "
												+ "us.navd88_to_ngvd29_utm83 or us.ngvd29_to_navd88_utm83. "
												+ "conversion will not be performed", 
												"Error", JOptionPane.ERROR_MESSAGE);
									}
								} else if (bathymetryHDatum == CsdpFileMetadata.UTMNAD27) {
									// if converting one vert datum or the other
									if (navd88toNgvd29) {
										elevConverted = us.navd88_to_ngvd29_utm27(x3, y3, utm27_zone, utm27_units, elev,
												elev_units, elev_units);
									} else if (ngvd29toNavd88) {
										elevConverted = us.ngvd29_to_navd88_utm27(x3, y3, utm27_zone, utm27_units, elev,
												elev_units, elev_units);
									} else {
										JOptionPane.showMessageDialog(_gui, "ERROR in Network.convertToBathymetryDatum: cross-section conversion: "
												+ "Bathymetry H Datum is UTMNAD27, but requested conversion is not "
												+ "us.navd88_to_ngvd29_utm27 or us.ngvd29_to_navd88_utm27"
												+ "conversion will not be performed", 
												"Error", JOptionPane.ERROR_MESSAGE);
									}
								} else {
									elevConverted = elev;
								}
								xp.putElevationFeet((double) elevConverted);
							} // for each xsect point
						} // for each xsect
					} // if vertical datum conversion requested
				} // for each centerline
			} // if need to change datum
		} else {
			JOptionPane.showMessageDialog(_gui, "bathmetry and network file are in different UTM zones. Conversion will NOT be performed", 
					"Error", JOptionPane.ERROR_MESSAGE);
		} // if same zone or not (if not, won't do datum change)
			// set network datum to bath datum
		m.setHDatum(bm.getHDatum());
		m.setVDatum(bm.getVDatum());
	}// convertToBathymetryDatum

	/**
	 * Convert x,y point to station (xsect point horizontal coordinate in
	 * relative coordinate system. Used to convert network coordinates for datum
	 * changes.
	 */
	private double xyToStation(double x1, double x2, double y1, double y2, double x, double y) {
		double station = -Double.MAX_VALUE;
		double theta = CsdpFunctions.getTheta(x1, x2, y1, y2);
		if (Math.cos(theta) == 0.0) {
			station = y - y1;
		} else if (Math.sin(theta) == 0.0) {
			station = x - x1;
		} else {
			station = (x - x1) / (double) Math.cos(theta);
		}
		return station;
	}// xyToStation

	/**
	 * Convert station (xsect point horizontal coordinate in relative coord.
	 * sys) to UTM, using x1, x2 as origin and x2, y2 as other point in
	 * cross-section line. Used for 3D network output.
	 */
	private double stationToX(double x1, double x2, double y1, double y2, double station) {
		// use imaginary horizontal line with same y value as first point to
		// find theta
		double theta = CsdpFunctions.getTheta(x1, x2, y1, y2);
		return x1 + station * (double) Math.cos(theta);
	}// stationToX

	/**
	 * Convert station (xsect point horizontal coordinate in relative coord.
	 * sys) to UTM, using x1, x2 as origin and x2, y2 as other point in
	 * cross-section line. Used for 3D network output.
	 */
	private double stationToY(double x1, double x2, double y1, double y2, double station) {
		// use imaginary horizontal line with same y value as first point to
		// find theta
		double theta = CsdpFunctions.getTheta(x1, x2, y1, y2);
		if (DEBUG) {
			System.out.println(
					"got theta.  x1,x2,y1,y2,station=" + x1 + "," + x2 + "," + "," + y1 + "," + y2 + "," + station);
			System.out.println("theta=" + theta);
		}
		return y1 + station * (double) Math.sin(theta);
	}// stationToY

	protected String _oldCenterlineName = null;
	protected String _newCenterlineName = null;
	/**
	 * centerline that has been selected by the user
	 */
	protected Centerline _selectedCenterline;
	/**
	 * xsect that has been selected by the user
	 */
	protected Xsect _selectedXsect;
	/**
	 * name of centerline that has been selected
	 */
	protected String _selectedCenterlineName;
	/**
	 * number of xsect that has been selected. The numbers start with zero, with
	 * xsect number zero being the closest xsect to the upstream end.
	 */
	protected int _selectedXsectNum;
	/**
	 * the number of centerlines in the network
	 */
	protected int _numCenterlines;
	/**
	 * the name of the network. no use for this now; currently only one instance
	 * of the Network class is allowed.
	 */
	protected String _networkName = null;
	/**
	 * stores all Centerline objects
	 */
	protected Hashtable<String, Centerline> _centerlines = new Hashtable<String, Centerline>();
	/**
	 * stores all centerline names
	 */
	protected ResizableStringArray _centerlineNames = new ResizableStringArray();
	/**
	 * turn on to print debugging statements
	 */
	protected static final boolean DEBUG = false;
	/**
	 * true if any change has been made to the network or anything it contains
	 * This value is checked when exiting program, loading new network, etc.
	 */
	protected boolean _isUpdated = false;
	protected boolean _maxMinFound = false;
	/**
	 * array index of
	 */
	protected final int distIndex = 4;
	protected double _maxX;
	protected double _minX;
	protected double _maxY;
	protected double _minY;
	private CsdpFrame _gui;
} // class Network
