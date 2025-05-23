package DWR.CSDP;

import java.util.Vector;

/**
 * Store data for a single cross-section. Calculate all cross-section properties
 */
public class Xsect {
	private int _numPoints;
	private final boolean DEBUG = false;
	private Vector<XsectPoint> _xsectPoints = new Vector<XsectPoint>();
	private double _distAlongCenterline = 0.0;
	private double _xsectLineLength = 0.0;
	private final int leftIndex = 0;
	private final int rightIndex = 0;
	private int _numUniqueElevations;
	public boolean _isUpdated = false;
	private String _metadata;

	// private double MAX_INSERT_DIST = 300.0;

	/*
	 * All stations in a cross-section should be unique. return true if they
	 * are.
	 */
	public boolean allUniqueStations() {
		boolean returnValue = true;
		int numPoints = getNumPoints();
		double lastStation = Double.MAX_VALUE;
		for (int i = 0; i < numPoints; i++) {
			XsectPoint xp = getXsectPoint(i);
			double station = xp.getStationFeet();
			if (station == lastStation)
				returnValue = false;
			lastStation = station;
		}
		return returnValue;
	}// allUniqueStations

	public String getAllStations() {
		int numPoints = getNumPoints();
		String line = "";
		for (int i = 0; i < numPoints; i++) {
			XsectPoint xp = getXsectPoint(i);
			double station = xp.getStationFeet();
			line += station + ",";
		}
		return line;
	}// getAllStations

	public String getAllElevations() {
		int numPoints = getNumPoints();
		String line = "";
		for (int i = 0; i < numPoints; i++) {
			XsectPoint xp = getXsectPoint(i);
			double elevation = xp.getElevationFeet();
			line += elevation + ",";
		}
		return line;
	}// getAllElevations

	/**
	 * Used only for moving entire cross-section drawing.
	 */
	public void adjustXCoord(double value) {
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			XsectPoint xp = getXsectPoint(i);
			xp.putStationFeet(xp.getStationFeet() + value);
		}
	}

	/**
	 * Used only for moving entire cross-section drawing.
	 */
	public void adjustYCoord(double value) {
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			XsectPoint xp = getXsectPoint(i);
			xp.putElevationFeet(xp.getElevationFeet() + value);
		}
	}

	/**
	 * returns number of points in cross-section
	 */
	public int getNumPoints() {
		return _numPoints;
	}

	/**
	 * returns number of line segments in cross-section
	 */
	public int getNumLineSegments() {
		return getNumPoints() - 1;
	}

	/**
	 * create new XsectPoint object, add to _xsectPoints vector.
	 */
	public void addXsectPoint() {
		XsectPoint point = new XsectPoint();
		_xsectPoints.addElement(point);
		_numPoints++;
	}

	/**
	 * create new XsectPoint object, add to _xsectPoints vector, set values.
	 */
	public void addXsectPoint(Integer insertPointMode, double x, double y) {
		XsectPoint point = new XsectPoint();
		//This is not the default. It works as expected if the cross-section is drawn 
		// from left bank to right bank, but not from right bank to left bank.
		if(!CsdpFunctions.ADD_XSECT_POINTS_BASED_ON_POINT_ORDER) {
			if(insertPointMode==XsectEditInteractor.ADD_LEFT_POINT) {
				_xsectPoints.insertElementAt(point, 0);
			}else if(insertPointMode==XsectEditInteractor.ADD_RIGHT_POINT) {
				_xsectPoints.addElement(point);
			}
		}else {
			//this is the default...because it will not be reversed if cross-section is drawn 
			//right to left. But it will behave inconsistently if user creates a strange shape that looks
			//nothing like a cross-section
			if(getNumPoints()>1) {
				XsectPoint firstPoint = getXsectPoint(0);
				XsectPoint lastPoint = getXsectPoint(getNumPoints()-1);
				double firstStation = firstPoint.getStationFeet();
				double lastStation = lastPoint.getStationFeet();
				boolean leftToRight = true;
				if(firstStation<lastStation) {
					leftToRight = true;
				}else {
					leftToRight = false;
				}
				boolean addToBeginningOfVector = true;
				
				if(insertPointMode==XsectEditInteractor.ADD_LEFT_POINT) {
					if(leftToRight) {
						addToBeginningOfVector = true;
					}else {
						addToBeginningOfVector = false;
					}
				}else if(insertPointMode==XsectEditInteractor.ADD_RIGHT_POINT) {
					if(leftToRight) {
						addToBeginningOfVector = false;
					}else {
						addToBeginningOfVector = true;
					}
				}else {
					System.out.println("Error in Xsect.addXsectPoint");
				}
				if(addToBeginningOfVector) {
					_xsectPoints.insertElementAt(point, 0);
				}else {
					_xsectPoints.addElement(point);
				}
			}else {
				if(insertPointMode==XsectEditInteractor.ADD_LEFT_POINT) {
					_xsectPoints.insertElementAt(point, 0);
	
				}else if(insertPointMode==XsectEditInteractor.ADD_RIGHT_POINT) {
					_xsectPoints.addElement(point);
				}
			}
		}
		point.putStationFeet(x);
		point.putElevationFeet(y);
		_numPoints++;

		if (DEBUG)
			System.out.println("added xsect point.  x,y,numpoints=" + x + "," + y + "," + getNumPoints());
	}

	/**
	 * returns elevation of lowest point
	 */
	public double getMinimumElevationFeet() {
		XsectPoint point = null;
		double minElev = Double.MAX_VALUE;
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			point = getXsectPoint(i);
			if (point.getElevationFeet() < minElev)
				minElev = point.getElevationFeet();
		}
		return minElev;
	}// getMinimumElevation

	/**
	 * returns XsectPoint object at specified index
	 */
	public XsectPoint getXsectPoint(int index) {
		XsectPoint xp = null;
		xp = (XsectPoint) _xsectPoints.elementAt(index);
		return xp;
	}

	/**
	 * returns Vector of XsectPoints
	 */
	public Vector getAllPoints() {
		return _xsectPoints;
	}

	/**
	 * returns Vector of XsectPoints
	 */
	public void putAllPoints(int numPoints, Vector allPoints) {
		_numPoints = numPoints;
		_xsectPoints = allPoints;
	}

	/**
	 * returns distance along centerline of cross-section from first point in
	 * xsect
	 */
	public double getDistAlongCenterlineFeet() {
		return _distAlongCenterline;
	}

	/**
	 * sets distance along centerline of cross-section from first point in xsect
	 */
	public void putDistAlongCenterlineFeet(double value) {
		_distAlongCenterline = value;
	}

	/**
	 * returns length of cross-section line
	 */
	public double getXsectLineLengthFeet() {
		return _xsectLineLength;
	}

	/**
	 * sets length of cross-section line
	 */
	public void putXsectLineLengthFeet(double value) {
		_xsectLineLength = value;
	}

	/**
	 * calculate cross-section width at specified elevation
	 */
	public double getWidthFeet(double elevation) {
		double width = 0.0;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			width += getWidthFeet(i, elevation);
		}
		return width;
	}

	/**
	 * calculate cross-section area at specified elevation
	 * Area in a cross-section should be calculated based on width, because 
	 * width varies linearly wrt elevation, and area varies nonlinearly.
	 */
	public double getAreaSqft(double elevation) {
		//the new way 10/16/2018, based on trapezoidal calculation
		double[] allUniqueElevations = getSortedUniqueElevations();
		//find index of highest layer that is below elevation
		int lastLayerIndex = 0;
		double lastLayerArea = 0.0;
		for(int i=0; i<allUniqueElevations.length; i++) {
			if(allUniqueElevations[i] < elevation) {
				lastLayerIndex = i;
				if(i>0) {
					lastLayerArea += 0.5 * (getWidthFeet(allUniqueElevations[i]) + getWidthFeet(allUniqueElevations[i-1])) * 
							(allUniqueElevations[i]-allUniqueElevations[i-1]);
				}
			}else {
				break;
			}
		}
		double a = 0.0;
		//if lowest elevation in cross-section is above elevation, should return 0.0
		if(getNumPoints() > 0 && allUniqueElevations[0] < elevation) {
			double lastElevation = allUniqueElevations[lastLayerIndex];
			double lastWidth = getWidthFeet(lastElevation);
			double currentWidth = getWidthFeet(elevation);
			a = lastLayerArea + 0.5 * (currentWidth+lastWidth) * (elevation - lastElevation);
		}
		return a;
		//The old way: errors were found in this calculation
//		double area = 0.0;
//		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
//			area += getAreaSqft(i, elevation);
//		}
//		return area;
	}

	/**
	 * calculate cross-section wetted perimeter at specified elevation
	 */
	public double getWettedPerimeterFeet(double elevation) {
		double wetp = 0.0;
		double w = -Double.MAX_VALUE;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			w = getWettedPerimeterFeet(i, elevation);
			wetp += w;
			if (w < 0.0f) {
				System.out.println("ERROR in Xsect.getWettedPerimeter:");
				System.out.println("value is less than zero.");
			}
		}
		return wetp;
	}

	/**
	 * calculate cross-section hydraulic depth at specified elevation
	 */
	public double getHydraulicDepthFeet(double elevation) {
		return getAreaSqft(elevation) / getWidthFeet(elevation);
	}

	/**
	 * calculate cross-section width at specified elevation for
	 * OpenWaterArea(Yolo Bypass) calculations only
	 */
	public double getWidthFeet(double elevation, String crossSectionName, ToeDrainData tdData) {
		double width = 0.0;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			width += getWidthFeet(i, elevation, crossSectionName, tdData);
		}
		return width;
	}

	/**
	 * calculate cross-section area at specified elevation for
	 * OpenWaterArea(Yolo Bypass) calculations only
	 */
	public double getAreaSqft(double elevation, String crossSectionName, ToeDrainData tdData) {
		double area = 0.0;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			area += getAreaSqft(i, elevation, crossSectionName, tdData);
		}
		return area;
	}

	/**
	 * calculate cross-section wetted perimeter at specified elevation for
	 * OpenWaterArea(Yolo Bypass) calculations only
	 */
	public double getWettedPerimeterFeet(double elevation, String crossSectionName, ToeDrainData tdData) {
		double wetp = 0.0;
		double w = -Double.MAX_VALUE;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			w = getWettedPerimeterFeet(i, elevation, crossSectionName, tdData);
			wetp += w;
			if (w < 0.0f) {
				System.out.println("ERROR in Xsect.getWettedPerimeter:");
				System.out.println("value is less than zero.");
			}
		}
		return wetp;
	}

	/**
	 * calculate cross-section hydraulic depth at specified elevation for
	 * OpenWaterArea(Yolo Bypass) calculations only
	 */
	public double getHydraulicDepthFeet(double elevation, String crossSectionName, ToeDrainData tdData) {
		return getAreaSqft(elevation, crossSectionName, tdData) / getWidthFeet(elevation, crossSectionName, tdData);
	}

	/**
	 * calculate cross-section hydraulic radius at specified elevation
	 */
	public double getHydraulicRadiusFeet(double elevation) {
		double area = getAreaSqft(elevation);
		double wetp = getWettedPerimeterFeet(elevation);
		double hr = 0.0;
		if (wetp <= 0)
			hr = 0.0;
		else
			hr = area / wetp;
		return hr;
	}

	/**
	 * calculate cross-section x centroid at specified elevation
	 */
	public double getXCentroidFeet(double elevation) {
		double xca = 0.0;
		double xc = 0.0;
		double dXCentroid = -Double.MAX_VALUE;
		// loop through all line segments. Xc = sum(Xci*Ai)/sum(Ai)
		if (DEBUG)
			System.out.println("elevation=" + elevation);
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			dXCentroid = getXCentroidFeet(i, elevation);
			if (DEBUG)
				System.out.println("i, xcai=" + i + "," + dXCentroid);
			if (dXCentroid != -Double.MAX_VALUE)
				xca += dXCentroid;
			if (DEBUG)
				System.out.println("xcentroid for line segment " + i + "=" + getXCentroidFeet(i, elevation));
		}
		xc = xca / getAreaSqft(elevation);
		return xc;
	}

	/**
	 * calculate cross-section z centroid at specified elevation
	 */
	public double getZCentroidFeet(double elevation) {
		double zc = 0.0;
		double zca = 0.0;
		double dZCentroid = -Double.MAX_VALUE;
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			dZCentroid = getZCentroidFeet(i, elevation);
			if (dZCentroid != -Double.MAX_VALUE)
				zca += dZCentroid;
		}
		zc = zca / getAreaSqft(elevation);
		return zc;
	}//getZCentroidFeet
	
	/*
	 * returns string listing all elevations with -dConeyance
	 * will return empty string if none are found
	 */
	public String getNegativeDConveyanceReport() {
		String returnString = "";
		double[] elevations = getSortedUniqueElevations();
		
		double[] dConveyanceArray = getDConveyanceValues();
		boolean returnValue = false;
		int valueIndex = 0;
		for(int i=0; i<dConveyanceArray.length; i++) {
			if(dConveyanceArray[i] < 0.0) {
				if(valueIndex > 0) {
					returnString+=",";
				}
				returnString += elevations[i];
			}
			valueIndex++;
		}
		
		return returnString;
	}//hasNegativeDConveyance
	
	/*
	 * Calculates dConveyance for all layers; returns array.
	 */
	public double[] getDConveyanceValues() {
		double[] elevations = getSortedUniqueElevations();
		double[] returnValues = new double[elevations.length];
		if(elevations.length>0) {
			//set dConveyance at bottom to zero
			returnValues[0] = 0.0;
			for(int i=1; i<elevations.length; i++) {
				double currentLayerArea = getAreaSqft(elevations[i]);
				double lowerLayerArea = getAreaSqft(elevations[i-1]);
				double currentLayerWetP = getWettedPerimeterFeet(elevations[i]);
				double lowerLayerWetP = getWettedPerimeterFeet(elevations[i-1]);
				double dz = elevations[i]-elevations[i-1];
				double da = currentLayerArea-lowerLayerArea;
				double dp = currentLayerWetP-lowerLayerWetP;
				double partialAPartialZ = da/dz;
				double partialPPartialZ = dp/dz;
				double firstTerm = (5.0/3.0) * Math.pow(currentLayerArea,2.0/3.0)/Math.pow(currentLayerWetP, 2.0/3.0) * partialAPartialZ;
				double secondTerm = (-2.0/3.0) * Math.pow(currentLayerArea,5.0/3.0)/Math.pow(currentLayerWetP, 5.0/3.0) * partialPPartialZ;
				returnValues[i]= (1.486/CsdpFunctions.MANNINGS_N) * (firstTerm + secondTerm); 
			}
		}
		return returnValues;
	}//getDConveyanceTable

	public boolean hasNoPoints() {
		boolean returnValue = false;
		if(getNumPoints()<=0) {
			returnValue = true;
		}
		return returnValue;
	}
	
	/*
	 * returns true if any layer has negative dConveyance and is in the specified intertidal zone
	 */
	public boolean hasNegDConveyanceInIntertidalZone() {
		boolean returnValue = false;
		if(getNumPoints()>0) {
			double[] dConveyanceValues = getDConveyanceValues();
			double[] elevations = getSortedUniqueElevations();
			for(int j=0; j<dConveyanceValues.length; j++) {
				double dk = dConveyanceValues[j];
				if(dk<0.0 && elevations[j]>=CsdpFunctions.INTERTIDAL_LOW_TIDE && elevations[j]<=CsdpFunctions.INTERTIDAL_HIGH_TIDE) {
					returnValue = true;
					break;
				}
			}
		}
		return returnValue;
	}//hasNegDConveyanceInIntertidalZone
	
	public double[] getAreaValues() {
		double[] elevations = getSortedUniqueElevations();
		double[] returnValues = new double[elevations.length];
		if(elevations.length>0) {
			returnValues[0] = 0.0;
			for(int i=1; i<elevations.length; i++) {
				returnValues[i]= getAreaSqft(elevations[i]); 
			}
		}
		return returnValues;
	}//getAreaValues

	public double[] getWidthValues() {
		double[] elevations = getSortedUniqueElevations();
		double[] returnValues = new double[elevations.length];
		if(elevations.length>0) {
			returnValues[0] = 0.0;
			for(int i=1; i<elevations.length; i++) {
				returnValues[i]= getWidthFeet(elevations[i]); 
			}
		}
		return returnValues;
	}//getAreaValues

	public double[] getWetPValues() {
		double[] elevations = getSortedUniqueElevations();
		double[] returnValues = new double[elevations.length];
		if(elevations.length>0) {
			//set dConveyance at bottom to zero
			returnValues[0] = 0.0;
			for(int i=1; i<elevations.length; i++) {
				returnValues[i]= getWettedPerimeterFeet(elevations[i]); 
			}
		}
		return returnValues;
	}//getWetPValues
	
	/*
	 * Returns an elevation at a specified percentage from the bottom
	 */
	public double getElevationAtFraction(double fractionFromBottom) {
		double[] allElevations = getSortedUniqueElevations();
		double minElev = allElevations[0];
		double maxElev = allElevations[allElevations.length-1];
		return minElev + fractionFromBottom * (maxElev-minElev);
	}
	
	/**
	 * returns sorted array of all unique elevations in the cross-section
	 */
	public double[] getSortedUniqueElevations() {
		double elev = 0.0;
		double[] ue = new double[getNumPoints()];
		double leftElevation = Double.MAX_VALUE;
		double rightElevation = Double.MAX_VALUE;
		double minElev = Double.MAX_VALUE;
		ResizableDoubleArray e = new ResizableDoubleArray();
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			minElev = Math.min(minElev, getXsectPoint(i).getElevationFeet());
		}
		for (int i = 0; i <= getNumLineSegments() - 1; i++) {
			leftElevation = getXsectPoint(i).getElevationFeet();
			rightElevation = getXsectPoint(i + 1).getElevationFeet();
//			if (leftElevation == rightElevation && leftElevation != minElev) {
			//DSM2 doesn't seem to notice differences of less than .01 feet.
			if(Math.abs(leftElevation - rightElevation) < .01) {
				if (DEBUG)
					System.out.println("leftElevation, rightElevation, minElev=" + leftElevation + "," + rightElevation
							+ "," + minElev);
				//previously was
				//				if(leftElevation==rightElevation && leftElevation != minElev) {
				//		    		getXsectPoint(i).putElevationFeet(leftElevation+0.01f);
				//10/2018: change to:
				//only modify right point, since we're iterating from left to right
				if(leftElevation==rightElevation && leftElevation > minElev) {
					getXsectPoint(i+1).putElevationFeet(rightElevation + 0.01);
				}else if(leftElevation > rightElevation) {
					getXsectPoint(i+1).putElevationFeet(rightElevation - 0.011);
				}else if(rightElevation > leftElevation) {
					getXsectPoint(i+1).putElevationFeet(rightElevation + 0.011);
				}
			}
		} // for i
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			ue[i] = getXsectPoint(i).getElevationFeet();
		}
		ue = sortArray(ue, getNumPoints());
		ue = findUnique(ue, getNumPoints());
		return ue;
	}//getUniqueElevations

	/**
	 * return number of unique elevations in the cross-section
	 */
	public int getNumUniqueElevations() {
		return _numUniqueElevations;
	}

	/**
	 * Used to reverse order of xsect points and multiply all stations by -1.
	 */
	public void reverse() {
		XsectPoint point;
		int numPoints = getNumPoints();
		double station;

		for (int i = 0; i <= numPoints - 1; i++) {
			point = getXsectPoint(i);
			station = point.getStationFeet();
			point.putStationFeet(-station);
		} // for i

		Vector allPoints = new Vector(numPoints);
		for (int i = 0; i <= numPoints - 1; i++) {
			allPoints.addElement(null);
		}

		for (int i = 0; i <= numPoints - 1; i++) {
			point = getXsectPoint(i);
			allPoints.setElementAt(point, numPoints - i - 1);
		} // for i

		for (int i = 0; i <= numPoints - 1; i++) {
			point = (XsectPoint) (allPoints.elementAt(i));
			putXsectPoint(i, point);
		}

		for (int i = 0; i <= numPoints - 1; i++) {
			point = getXsectPoint(i);
		} // for i
	}// reverse

	/**
	 * removes xsect point at specified location
	 */
	public void deleteXsectPoint(double station, double elevation) {
		double x = (double) station;
		double y = (double) elevation;
		int minDistIndex = getNearestPointIndex(x, y);
		_xsectPoints.removeElementAt(minDistIndex);
		_numPoints--;
	}// deleteXsectPoint

	/**
	 * removes all xsect points
	 */
	public void removeAllPoints() {
		_xsectPoints = null;
		_xsectPoints = new Vector();
		_numPoints = 0;
	}

	/**
	 * insert xsect point at specified location
	 */
	public void insertXsectPoint(double station, double elevation) {
		double x = (double) station;
		double y = (double) elevation;
		int numPoints = getNumPoints();
		XsectPoint point;
		XsectPoint point1;
		XsectPoint point2;
		double x1;
		double x2;
		double y1;
		double y2;
		double minDist = Double.MAX_VALUE;
		double dist = 0.0;
		int minDistIndex = Integer.MAX_VALUE;
		XsectPoint newPoint = new XsectPoint();
		newPoint.putStationFeet(x);
		newPoint.putElevationFeet(y);
		for (int i = 0; i <= numPoints - 2; i++) {
			point1 = getXsectPoint(i);
			point2 = getXsectPoint(i + 1);
			x1 = point1.getStationFeet();
			y1 = point1.getElevationFeet();
			x2 = point2.getStationFeet();
			y2 = point2.getElevationFeet();

			if (DEBUG)
				System.out.println("x1,y2,x2,y2,x,y=" + x1 + "," + y1 + "," + x2 + "," + y2 + "," + x + "," + y);

			dist = CsdpFunctions.shortestDistLineSegment(x1, x2, x, y1, y2, y);
			if (dist < minDist) {
				minDist = dist;
				minDistIndex = i;
			}
		} // for i
		if (DEBUG)
			System.out.println("inserting point");
		_xsectPoints.insertElementAt(newPoint, minDistIndex + 1);
		_numPoints++;
	}// insertXsectPoint

	/**
	 * sets value of isUpdated. Used to tell if xsect has changed.
	 */
	public void setIsUpdated(boolean b) {
		_isUpdated = b;
	}

	/**
	 * returns true if there are any horizontal line segments above bottom elev
	 */
	public boolean horizontalLine(int i) {
		double zMin = Double.MAX_VALUE;
		boolean returnValue = false;
		double eLeft = 0.0;
		double eRight = 0.0;

		for (int j = 0; j <= getNumPoints() - 1; j++) {
			zMin = Math.min(zMin, getXsectPoint(j).getElevationFeet());
		}
		eLeft = getXsectPoint(i).getElevationFeet();
		eRight = getXsectPoint(i + 1).getElevationFeet();
		if (eLeft == eRight && eLeft > zMin)
			returnValue = true;
		return returnValue;
	}// horizontalLine

	// /**
	// * calculates the dconveyance (derivative of conveyance wrt height)
	// */
	// public float getDConveyance(float elevation){
	// /**
	// * the lowest elevation that is above the specified elevation
	// */
	// float higherElevation = -Float.MAX_VALUE;
	// /**
	// * the highest elevation that is below the specified elevation
	// */
	// float lowerElevation = -Float.MAX_VALUE;
	// XsectPoint point = null;
	// float pointElevation = -Float.MAX_VALUE;
	// int lowerPointIndex = -Integer.MAX_VALUE;
	// int higherPointIndex = -Integer.MAX_VALUE;
	// float minDK = Float.MAX_VALUE;

	// for(int i=0; i<=getNumUniqueElevations()-1; i++){
	// point = getXsectPoint(i);
	// pointElevation = point.getElevationFeet();
	// if(pointElevation > elevation && pointElevation <= higherElevation){
	// higherElevation = pointElevation;
	// higherPointIndex = i;
	// }//if
	// if(pointElevation < elevation && pointElevation >= lowerElevation){
	// lowerElevation = pointElevation;
	// lowerPointIndex = i;
	// }//if
	// }//for
	// if(lowerPointIndex < 0 && higherPointIndex <0){
	// minDK = -Float.MAX_VALUE;
	// }else if(lowerPointIndex < 0 && higherPointIndex >=0){
	// minDK = getDConveyance(higherPointIndex);
	// }else if(higherPointIndex < 0 && lowerPointIndex >=0){
	// minDK = getDConveyance(lowerPointIndex);
	// }else if(higherPointIndex >= 0 && lowerPointIndex >=0){
	// minDK = Math.min(getDConveyance(lowerPointIndex),
	// getDConveyance(higherPointIndex));
	// }else System.out.println("Error in Xsect.getDConveyance");
	// return minDK;
	// }

	/**
	 * calculates the dconveyance (derivative of conveyance wrt height)
	 */
	private double getDConveyance(int i) {
		double elevationLeft = getXsectPoint(i - 1).getElevationFeet();
		double elevationMiddle = getXsectPoint(i).getElevationFeet();
		double elevationRight = getXsectPoint(i + 1).getElevationFeet();
		double dz0 = elevationMiddle - elevationLeft;
		double dz1 = elevationRight - elevationMiddle;
		double dp0 = getWettedPerimeterFeet(elevationMiddle) - getWettedPerimeterFeet(elevationLeft);
		double dp1 = getWettedPerimeterFeet(elevationRight) - getWettedPerimeterFeet(elevationMiddle);
		double condition0 = 5.0f * getWidthFeet(elevationMiddle)
				- 2.0f * (getAreaSqft(elevationMiddle) / getWettedPerimeterFeet(elevationMiddle)) * (dp0 / dz0);
		double condition1 = 5.0f * getWidthFeet(elevationMiddle)
				- 2.0f * (getAreaSqft(elevationMiddle) / getWettedPerimeterFeet(elevationMiddle)) * (dp1 / dz1);
		return Math.min(condition0, condition1);
	}

	/**
	 * returns point that is closest to specified coord.
	 */
	public int getNearestPointIndex(double x, double y) {
		XsectPoint point;
		double minDist = Double.MAX_VALUE;
		int minDistIndex = 0;
		double dist;
		double x1;
		double y1;
		for (int i = 0; i <= getNumPoints() - 1; i++) {
			point = getXsectPoint(i);
			x1 = point.getStationFeet();
			y1 = point.getElevationFeet();
			dist = (double) CsdpFunctions.pointDist(x, y, x1, y1);
			if (dist < minDist) {
				minDist = dist;
				minDistIndex = i;
			} // if dist
		} // for i
		return minDistIndex;
	}// getNearestPoint

	/**
	 * puts XsectPoint object at specified index--only used for reversing
	 */
	private void putXsectPoint(int index, XsectPoint point) {
		_xsectPoints.setElementAt(point, index);
	}

	/**
	 * sort and return the array
	 */
	private double[] sortArray(double[] array, int numElements) {
		int last;
		int ptr;
		int first;
		double hold;

		last = numElements - 1;
		for (int j = 0; j <= last - 1; j++) {
			ptr = j;
			first = j + 1;
			for (int k = first; k <= last; k++) {
				// if(DEBUG)System.out.println("k,j,ptr="+k+" "+j+" "+ptr);
				if (array[k] < array[ptr])
					ptr = k;
			}
			hold = array[j];
			array[j] = array[ptr];
			array[ptr] = hold;
		}
		return array;
	}// sortArray

	/**
	 * copy unique elements on to new array
	 */
	private double[] findUnique(double[] array, int numElements) {
		/**
		 * this array should contain only unique elevation. However, some of the
		 * elevations will be very close to each other. If the elevations are
		 * close but the wetted perimeter calculated at each elevation is not
		 * close, then keep both elevations. otherwise, get rid of one.
		 */
		ResizableDoubleArray uniqueValuesRSA = new ResizableDoubleArray();
		_numUniqueElevations = 0;
		/**
		 * This array stores the values from newArray that remain after the
		 * operation described above is performed
		 */
		// double[] uniqueArray = new double[numElements];
		if (numElements > 0) {
			uniqueValuesRSA.put(_numUniqueElevations, array[0]);
			_numUniqueElevations = 1;
		}
		for (int i = 1; i <= numElements - 1; i++) {
			if (array[i] != array[i - 1]) {
				uniqueValuesRSA.put(_numUniqueElevations, array[i]);
				_numUniqueElevations++;
			}
		}

		double[] returnArray = new double[_numUniqueElevations];
		for(int i=0; i<_numUniqueElevations; i++) {
			returnArray[i] = uniqueValuesRSA.get(i); 
		}
		
		return returnArray;
	}//findUnique

	/**
	 * calculate and return the contribution to the cross-section width at the
	 * specified elevation of the line segment whose leftmost point index is
	 * equal to the specified index
	 */
	private double getWidthFeet(int index, double elevation) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double w = 0.0;

		if (aboveWater(eLeft, eRight, elevation))
			w = 0.0;
		if (completelySubmerged(eLeft, eRight, elevation)) {
			w = Math.abs(sRight - sLeft);
		}
		if (partiallySubmerged(eLeft, eRight, elevation)) {
			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);

			w = Math.abs(intersectionStation - lowerPointStation);
		}
		return w;
	}// getWidth

	/**
	 * calculate and return the contribution to the cross-section width at the
	 * specified elevation of the line segment whose leftmost point index is
	 * equal to the specified index for OpenWaterArea (Yolo Bypass) calculations
	 * only.
	 */
	private double getWidthFeet(int index, double elevation, String crossSectionName, ToeDrainData tdData) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double w = 0.0;
		double tdStation = tdData.getStationFeet(crossSectionName);
		double tdElevation = tdData.getElevationFeet(crossSectionName);

		if (aboveWater(eLeft, eRight, elevation))
			w = 0.0;
		if (completelySubmerged(eLeft, eRight, elevation)) {
			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (sLeft >= tdStation) {
					// the two points are inside the toe drain
					w = Math.abs(sRight - sLeft);
				} else if (sLeft < tdStation && sRight < tdStation) {
					// the two points are west of the toe drain
					w = 0.0;
				} else if (sLeft < tdStation && sRight >= tdStation) {
					// one point is inside the toe drain and the other is
					// outside
					w = Math.abs(sRight - tdStation);
				} else
					System.out.println("Error in Xsect.getWidth!");
			} else {
				w = Math.abs(sRight - sLeft);
			}
		}
		if (partiallySubmerged(eLeft, eRight, elevation)) {
			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);

			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (intersectionStation >= tdStation && lowerPointStation >= tdStation) {
					// both points are inside toe drain
					w = Math.abs(intersectionStation - lowerPointStation);
				} else if (intersectionStation < tdStation && lowerPointStation < tdStation) {
					// both points are outside toe drain
					w = 0.0;
				} else if (intersectionStation < tdStation && lowerPointStation >= tdStation) {
					// intersection point is outside toe drain and lower point
					// is inside
					w = Math.abs(lowerPointStation - tdStation);
				} else if (lowerPointStation < tdStation && intersectionStation >= tdStation) {
					// lower point is outside toe drain and intersection point
					// is inside
					w = Math.abs(intersectionStation - tdStation);
				} else
					System.out.println("Error in Xsect.getWidth!");
			} else {
				w = Math.abs(intersectionStation - lowerPointStation);
			}
			if (w < 0.0f)
				System.out.println("Error in Xsect.getWidth:  negative value");
		} // if partially submerged
		return w;
	}// getWidth

	/**
	 * calculate and return the contribution to the cross-section area at the
	 * specified elevation of the line segment whose leftmost point index is
	 * equal to the specified index for OpenWaterArea (Yolo Bypass) calculations
	 * only.
	 */
	private double getAreaSqft(int index, double elevation, String crossSectionName, ToeDrainData tdData) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double a = 0.0;

		if (DEBUG)
			System.out.println("tdData=" + tdData);

		double tdStation = tdData.getStationFeet(crossSectionName);
		double tdElevation = tdData.getElevationFeet(crossSectionName);

		if (aboveWater(eLeft, eRight, elevation))
			a = 0.0;
		if (completelySubmerged(eLeft, eRight, elevation)) {
			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (sLeft >= tdStation) {
					// both points are inside toe drain
					a = (double) (getWidthFeet(index, elevation) * 0.5
							* (Math.abs(elevation - eLeft) + Math.abs(elevation - eRight)));
				} else if (sLeft < tdStation && sRight < tdStation) {
					// both points are outside toe drain
					a = 0.0;
				} else if (sLeft < tdStation && sRight >= tdStation) {
					// one point is inside and the other is outside
					// need to use the elevation of the point that is located at
					// the
					// tdStation--interpolate
					// tdLowerElevation should be the same as eRight or eLeft,
					// assuming
					// that the specified tdStation matches one of the station
					// values; check it.
					double tdLowerElevation = interp(eLeft, eRight, sLeft, sRight, tdStation);
					a = (double) (getWidthFeet(index, elevation, crossSectionName, tdData) * 0.5
							* (Math.abs(elevation - tdLowerElevation) + Math.abs(elevation - eRight)));
				} else {
					System.out.println("error in Xsect.getArea!");
				}
			} else {
				a = (double) (getWidthFeet(index, elevation) * 0.5
						* (Math.abs(elevation - eLeft) + Math.abs(elevation - eRight)));
			}
		}
		if (partiallySubmerged(eLeft, eRight, elevation)) {
			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);

			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (intersectionStation >= tdStation && lowerPointStation >= tdStation) {
					// both points are inside toe drain
					a = 0.5 * getWidthFeet(index, elevation) * Math.abs(elevation - lowerPointElevation);
				} else if (intersectionStation < tdStation && lowerPointStation < tdStation) {
					// both points are outside toe drain
					a = 0.0;
				} else if (intersectionStation < tdStation && lowerPointStation >= tdStation) {
					// intersection point is outside toe drain and lower point
					// is inside
					a = 0.5 * getWidthFeet(index, elevation, crossSectionName, tdData)
							* Math.abs(elevation - lowerPointElevation);
				} else if (lowerPointStation < tdStation && intersectionStation >= tdStation) {
					// lower point is outside toe drain and intersection point
					// is inside
					double tdLowerElevation = interp(lowerPointStation, intersectionStation, lowerPointElevation,
							elevation, tdStation);

					a = 0.5 * getWidthFeet(index, elevation, crossSectionName, tdData)
							* Math.abs(lowerPointElevation - tdLowerElevation);
				} else
					System.out.println("Error in Xsect.getArea!");
			} else {
				a = 0.5 * getWidthFeet(index, elevation) * Math.abs(elevation - lowerPointElevation);
			}

			if (a < 0.0f)
				System.out.println("Error in Xsect.getArea:  negative value");
		} // if partially submerged
		return a;
	}// getArea

	//This is the old way. For some reason, it's not consistent enough with width results. 
	//Result: DSM2 hits maxiter.
//	/**
//	 * calculate and return the contribution to the cross-section area at the
//	 * specified elevation of the line segment whose leftmost point index is
//	 * equal to the specified index
//	 */
//	private double getAreaSqft(int index, double elevation) {
//		double sLeft = getXsectPoint(index).getStationFeet();
//		double eLeft = getXsectPoint(index).getElevationFeet();
//		double sRight = getXsectPoint(index + 1).getStationFeet();
//		double eRight = getXsectPoint(index + 1).getElevationFeet();
//		double a = 0.0;
//		if (aboveWater(eLeft, eRight, elevation))
//			a = 0.0;
//		if (completelySubmerged(eLeft, eRight, elevation)) {
//			// trapezoidal area
//			a = getWidthFeet(index, elevation) * 0.5 * (Math.abs(elevation - eLeft) + Math.abs(elevation - eRight));
//		}
//		if (partiallySubmerged(eLeft, eRight, elevation)) {
//			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
//			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
//			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);
//			// triangular area
//			a = 0.5 * getWidthFeet(index, elevation) * Math.abs(elevation - lowerPointElevation);
//		}
//		return a;
//	}// getArea

	/**
	 * calculate and return the contribution to the cross-section wetted
	 * perimeter at the specified elevation of the line segment whose leftmost
	 * point index is equal to the specified index
	 */
	private double getWettedPerimeterFeet(int index, double elevation) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double w = 0.0;

		if (aboveWater(eLeft, eRight, elevation))
			w = 0.0;
		if (completelySubmerged(eLeft, eRight, elevation)) {
			w = Math.sqrt(Math.pow((sRight - sLeft), 2) + Math.pow((eRight - eLeft), 2));
		}
		if (partiallySubmerged(eLeft, eRight, elevation)) {
			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);

			w = Math.sqrt(Math.pow((intersectionStation - lowerPointStation), 2)
					+ Math.pow((elevation - lowerPointElevation), 2));
		}
		return w;
	}// getWettedPerimeter

	/**
	 * calculate and return the contribution to the cross-section wetted
	 * perimeter at the specified elevation of the line segment whose leftmost
	 * point index is equal to the specified index for OpenWaterArea (Yolo
	 * Bypass) calculations only.
	 */
	private double getWettedPerimeterFeet(int index, double elevation, String crossSectionName, ToeDrainData tdData) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double w = 0.0;
		double tdStation = tdData.getStationFeet(crossSectionName);
		double tdElevation = tdData.getElevationFeet(crossSectionName);

		if (aboveWater(eLeft, eRight, elevation))
			w = 0.0;
		if (completelySubmerged(eLeft, eRight, elevation)) {
			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (sLeft >= tdStation) {
					// both points are inside toe drain
					w = Math.sqrt(Math.pow((sRight - sLeft), 2) + Math.pow((eRight - eLeft), 2));

				} else if (sLeft < tdStation && sRight < tdStation) {
					// both points are outside toe drain
					w = 0.0;
				} else if (sLeft < tdStation && sRight >= tdStation) {
					// one point is inside and the other is outside
					// need to use the elevation of the point that is located at
					// the
					// tdStation--interpolate
					// tdLowerElevation should be the same as eRight or eLeft,
					// assuming
					// that the specified tdStation matches one of the station
					// values; check it.
					double tdLowerElevation = interp(eLeft, eRight, sLeft, sRight, tdStation);
					w = Math.sqrt(Math.pow((tdStation - sRight), 2) + Math.pow((tdLowerElevation - eRight), 2));
				} else
					System.out.println("Error in Xsect.getWettedPerimeter");
			} else {
				w = Math.sqrt(Math.pow((sRight - sLeft), 2) + Math.pow((eRight - eLeft), 2));
			}
		}
		if (partiallySubmerged(eLeft, eRight, elevation)) {
			double intersectionStation = interp(sLeft, sRight, eLeft, eRight, elevation);
			double lowerPointElevation = getLowerPointElevationFeet(sLeft, sRight, eLeft, eRight);
			double lowerPointStation = getLowerPointStationFeet(sLeft, sRight, eLeft, eRight);

			if (CsdpFunctions.getUseToeDrainRestriction() && tdStation >= 0.0f && elevation <= tdElevation) {
				if (intersectionStation >= tdStation && lowerPointStation >= tdStation) {
					// both points are inside toe drain
					w = Math.sqrt(Math.pow((intersectionStation - lowerPointStation), 2)
							+ Math.pow((elevation - lowerPointElevation), 2));
				} else if (intersectionStation < tdStation && lowerPointStation < tdStation) {
					// both points are outside toe drain
					w = 0.0;
				} else if (intersectionStation < tdStation && lowerPointStation >= tdStation) {
					// intersection point is outside toe drain and lower point
					// is inside
					double tdLowerElevation = interp(lowerPointElevation, elevation, lowerPointStation,
							intersectionStation, tdStation);

					w = Math.sqrt(Math.pow((tdStation - lowerPointStation), 2)
							+ Math.pow((lowerPointElevation - tdLowerElevation), 2));
				} else if (lowerPointStation < tdStation && intersectionStation >= tdStation) {
					// lower point is outside toe drain and intersection point
					// is inside
					double tdLowerElevation = interp(lowerPointElevation, elevation, lowerPointStation,
							intersectionStation, tdStation);

					w = Math.sqrt(Math.pow((tdStation - intersectionStation), 2)
							+ Math.pow((tdLowerElevation - elevation), 2));
				} else
					System.out.println("Error in Xsect.getWettedPerimeter");
			} else {
				w = Math.sqrt(Math.pow((intersectionStation - lowerPointStation), 2)
						+ Math.pow((elevation - lowerPointElevation), 2));
			}
		} // if partially submerged
		return w;
	}// getWettedPerimeter

	/**
	 * calculate and return the contribution to the cross-section x centroid at
	 * the specified elevation of the line segment whose leftmost point index is
	 * equal to the specified index
	 */
	private double getXCentroidFeet(int index, double intersectionElevation) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		double[] x = null;
		double[] z = null;
		double returnValue = -Double.MAX_VALUE;

		// if above water, returned value should not be used. How to do
		// this....?
		if (aboveWater(eLeft, eRight, intersectionElevation)) {
			returnValue = -Double.MAX_VALUE;
		} else {
			x = getTrapezoidStationValues(sLeft, sRight, eLeft, eRight, intersectionElevation);
			z = getTrapezoidElevationValues(sLeft, sRight, eLeft, eRight, intersectionElevation);

			// System.out.println("x,z="+x[0]+","+x[1]+","+x[2]+"
			// "+z[0]+","+z[1]+","+z[2]);

			returnValue = getXCentroidFeet(x[0], x[1], x[2], z[0], z[1], z[2]);
		}
		return returnValue;
	}// getXCentroid

	/**
	 * calculate and return the contribution to the cross-section z centroid at
	 * the specified elevation of the line segment whose leftmost point index is
	 * equal to the specified index
	 */
	private double getZCentroidFeet(int index, double intersectionElevation) {
		double sLeft = getXsectPoint(index).getStationFeet();
		double eLeft = getXsectPoint(index).getElevationFeet();
		double sRight = getXsectPoint(index + 1).getStationFeet();
		double eRight = getXsectPoint(index + 1).getElevationFeet();
		/*
		 * x[0] = side of rectangle closest to (x=0) x[1] = side of rectangle
		 * farthest from (x=0) x[2] = x coord. of lowest point of triangle (will
		 * be x0 or x1) z[0] = stage(top of rectangle) z[1] = bottom of
		 * rectangle z[2] = bottom of triangle
		 */
		double[] x;
		double[] z;
		double zc = -Double.MAX_VALUE;

		// if above water, returned value should not be used. How to do
		// this....?
		if (aboveWater(eLeft, eRight, intersectionElevation)) {
			zc = -Double.MAX_VALUE;
		} else {
			x = getTrapezoidStationValues(sLeft, sRight, eLeft, eRight, intersectionElevation);
			z = getTrapezoidElevationValues(sLeft, sRight, eLeft, eRight, intersectionElevation);
			zc = getZCentroidFeet(x[0], x[1], x[2], z[0], z[1], z[2]);
		}
		return zc;
	}// getZCentroid

	/**
	 * Calculate the x centroid of a trapezoid with the gived station and
	 * elevation values. If the line segment is partially submerged, the
	 * trapezoid will be a triangle (it will not have a rectangular portion).
	 * x[0] = side of rectangle closest to (x=0) x[1] = side of rectangle
	 * farthest from (x=0) x[2] = x coord. of lowest point of triangle (will be
	 * x0 or x1) z[0] = stage(top of rectangle) z[1] = bottom of rectangle z[2]
	 * = bottom of triangle
	 */
	private double getXCentroidFeet(double x0, double x1, double x2, double z0, double z1, double z2) {
		if (DEBUG)
			System.out.println("x,z=" + x0 + "," + x1 + "," + x2 + "," + z0 + "," + z1 + "," + z2);
		double aRectangle = Math.abs((z0 - z1) * (x0 - x1));
		double aTriangle = 0.5f * Math.abs((z1 - z2) * (x0 - x1));
		double xCRectangle = x0 + 0.5f * (x1 - x0);
		double xCTriangle = 0.0;

		// triangle pointing away from x (station) axis
		if (x2 == x0) {
			xCTriangle = x0 + (1.0f / 3.0f) * (x1 - x0);
		}
		if (x2 == x1) {
			xCTriangle = x0 + (2.0f / 3.0f) * (x1 - x0);
		}
		if (DEBUG)
			System.out.println("ar,xcr,at,xct=" + aRectangle + "," + xCRectangle + "," + aTriangle + "," + xCTriangle);
		return aRectangle * xCRectangle + aTriangle * xCTriangle;
	}// getXCentroid

	/**
	 * Calculate the z centroid of a trapezoid with the given elevation and
	 * station values. If the line segment is partially submerged, the trapezoid
	 * will be a triangle (it will not have a rectangular portion). x[0] = side
	 * of rectangle closest to (x=0) x[1] = side of rectangle farthest from
	 * (x=0) x[2] = x coord. of lowest point of triangle (will be x0 or x1) z[0]
	 * = stage(top of rectangle) z[1] = bottom of rectangle z[2] = bottom of
	 * triangle
	 */
	private double getZCentroidFeet(double x0, double x1, double x2, double z0, double z1, double z2) {
		if (DEBUG)
			System.out.println("zcentroid calculation");
		if (DEBUG)
			System.out.println("x,z=" + x0 + " " + x1 + " " + x2 + " " + z0 + " " + z1 + " " + z2);
		double aRectangle = Math.abs((z0 - z1) * (x0 - x1));
		double aTriangle = 0.5f * Math.abs((z1 - z2) * (x0 - x1));
		double zCRectangle = z0 + 0.5f * (z1 - z0);
		double zCTriangle = z0 + (1.0f / 3.0f) * (z2 - z1);
		double zc = 0.0;
		if (DEBUG)
			System.out.println("ar,at,zcr,zct=" + aRectangle + " " + aTriangle + " " + zCRectangle + " " + zCTriangle);
		zc = aRectangle * zCRectangle + aTriangle * zCTriangle;
		if (DEBUG)
			System.out.println("zc=" + zc);
		return zc;
	}// getZCentroid

	/**
	 * returns true if the line segment is above specified elevation
	 */
	private boolean aboveWater(double eLeft, double eRight, double elevation) {
		if (eLeft >= elevation && eRight >= elevation) {
			return true;
		} else {
			return false;
		}
	}// aboveWater

	/**
	 * Returns true if the specified elevation is above one point and below the
	 * other point of the line segment.
	 */
	private boolean partiallySubmerged(double eLeft, double eRight, double elevation) {
		if ((elevation - eLeft) * (elevation - eRight) < 0) {
			return true;
		} else {
			return false;
		}
	}// partiallySubmerged

	/**
	 * Returns true if the specified elevation is above both points in the line
	 * segment
	 */
	private boolean completelySubmerged(double eLeft, double eRight, double elevation) {
		if (elevation >= eLeft && elevation >= eRight) {
			return true;
		} else {
			return false;
		}
	}// partiallySubmerged

	/**
	 * interpolates to find x value for given Y value
	 */
	private double interp(double x1, double x2, double y1, double y2, double Y) {
		return -((y2 - Y) * ((x2 - x1) / (y2 - y1)) - x2);
	}

	/**
	 * find elevation of lower point
	 */
	private double getLowerPointElevationFeet(double sLeft, double sRight, double eLeft, double eRight) {
		if (eLeft <= eRight)
			return eLeft;
		else
			return eRight;
	}

	/**
	 * find station of lower point
	 */
	private double getLowerPointStationFeet(double sLeft, double sRight, double eLeft, double eRight) {
		if (eLeft <= eRight)
			return sLeft;
		else
			return sRight;
	}

	/**
	 * Find station values of trapezoid points: x[0] = side of rectangle closest
	 * to (x=0) x[1] = side of rectangle farthest from (x=0) x[2] = x coord. of
	 * lowest point of triangle (will be x0 or x1)
	 */
	private double[] getTrapezoidStationValues(double sLeft, double sRight, double eLeft, double eRight,
			double elevation) {
		double[] x = new double[3];

		// if left point of line segment is closer to x=0
		if (Math.abs(sLeft) < Math.abs(sRight)) {
			if (elevation < eLeft) {
				x[0] = CsdpFunctions.interpX(sLeft, sRight, eLeft, eRight, elevation);
			} else
				x[0] = sLeft;
			if (elevation < eRight) {
				x[1] = CsdpFunctions.interpX(sLeft, sRight, eLeft, eRight, elevation);
			} else
				x[1] = sRight;
		}
		// if right point of line segment is closer to x=0
		else {
			if (elevation < eRight) {
				x[0] = CsdpFunctions.interpX(sLeft, sRight, eLeft, eRight, elevation);
			} else
				x[0] = sRight;
			if (elevation < eLeft) {
				x[1] = CsdpFunctions.interpX(sLeft, sRight, eLeft, eRight, elevation);
			} else
				x[1] = sLeft;
		}
		// find station of lower point
		if (eLeft > eRight) {
			x[2] = sRight;
		} else {
			x[2] = sLeft;
		}

		return x;
	}

	/**
	 * Find Trapezoid point elevations: z[0] = stage(top of rectangle) z[1] =
	 * bottom of rectangle z[2] = bottom of triangle
	 */
	private double[] getTrapezoidElevationValues(double sLeft, double sRight, double eLeft, double eRight,
			double elevation) {
		double[] z = new double[3];

		z[0] = elevation;
		if (eLeft > eRight) {
			z[1] = eLeft;
			z[2] = eRight;
		} else {
			z[1] = eRight;
			z[2] = eLeft;
		}
		if (elevation < z[1])
			z[1] = elevation;
		return z;
	}

	/**
	 * Returns String which describes changes made to a cross-section
	 */
	public String getMetadata() {
		return _metadata;
	}

	/**
	 * Stores array of string which describe changes made to a cross-section
	 */
	public void putMetadata(String metadata) {
		_metadata = metadata;
	}

}// class Xsect
