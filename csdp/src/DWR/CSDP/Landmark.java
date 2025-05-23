package DWR.CSDP;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import DWR.CSDP.semmscon.UseSemmscon;

/**
 * Landmarks are symbols with labels. Landmarks have x and y UTM coordinates
 *
 * @author
 * @version
 */
public class Landmark {

	private CsdpFrame _gui;
	protected Hashtable<String, LandmarkPoint> _landmarkTable = new Hashtable<String, LandmarkPoint>();
	protected double _minX = 0.0;
	protected double _maxX = 0.0;
	protected double _minY = 0.0;
	protected double _maxY = 0.0;

	private boolean _isUpdated = false;
	private static final boolean DEBUG = false;
	private String _selectedLandmarkName;

	/**
	 * Constructor
	 */
	public Landmark(CsdpFrame gui) {
		this._gui = gui;
	}

	public Enumeration<String> getLandmarkNames() {
		return _landmarkTable.keys();
	}

	public Enumeration<LandmarkPoint> getLandmarkPointObjects() {
		return _landmarkTable.elements();
	}

	/**
	 * Returns number of Landmarks
	 */
	public int getNumLandmarks() {
		return _landmarkTable.size();
	}// getNumLandmarks

	/**
	 * adds a landmark
	 */
	public boolean addLandmarkMeters(String name, double x, double y) {
		boolean success = false;
		if (!_landmarkTable.containsKey(name)) {
			LandmarkPoint point = new LandmarkPoint(name);
			point.setXFeet(CsdpFunctions.metersToFeet(x));
			point.setYFeet(CsdpFunctions.metersToFeet(y));
			_landmarkTable.put(name, point);
			setIsUpdated(true);
			success = true;
		} else {
			// _warningDialog.setMessage("A landmark already exists with that
			// name!");
			// _warningDialog.setVisible(true);
			success = false;
		}
		return success;
	}// addLandmark

	/**
	 * adds a landmark, coord in feet.
	 */
	public boolean addLandmarkFeet(String name, double x, double y) {
		boolean success = false;
		if (DEBUG)
			System.out.println("contains name=" + _landmarkTable.contains(name));
		if (!_landmarkTable.containsKey(name)) {
			LandmarkPoint point = new LandmarkPoint(name);
			point.setXFeet(x);
			point.setYFeet(y);
			_landmarkTable.put(name, point);
			setIsUpdated(true);
			success = true;
		} else {
			// _warningDialog.setMessage("A landmark already exists with that
			// name!");
			// _warningDialog.setVisible(true);
			success = false;
		}
		return success;
	}// addLandmark

	public void deleteSelectedLandmarkPoint() {
		removeLandmarkPoint(getSelectedLandmarkName());
	}

	/**
	 * removes LandmarkPoint from hashtable
	 */
	public void removeLandmarkPoint(String name) {
		Object value = _landmarkTable.remove(name);
		setIsUpdated(true);
	}

	/**
	 * Find maximum and minimum values of x and y and store them.
	 */
	public void findMaxMin(int numLines) {
		double x = 0.0;
		double y = 0.0;
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		String landmarkName = null;

		if (DEBUG)
			System.out.println("finding max and min values.  number of points=" + numLines);
		for (Enumeration<String> names = _landmarkTable.keys(); names.hasMoreElements();) {
			landmarkName = names.nextElement();
			if (DEBUG)
				System.out.println("landmarkName=" + landmarkName);
			x = getXFeet(landmarkName);
			y = getYFeet(landmarkName);
			if (x != 0)
				minX = Math.min(x, minX);
			if (x != 0)
				maxX = Math.max(x, maxX);
			if (y != 0)
				minY = Math.min(y, minY);
			if (y != 0)
				maxY = Math.max(y, maxY);
		}
		putMinXFeet(minX);
		putMaxXFeet(maxX);
		putMinYFeet(minY);
		putMaxYFeet(maxY);
		if (DEBUG)
			System.out.println("findMaxMin called. MinX,MaxX,MinY,MaxY=" + getMinXMeters() + " " + getMaxXMeters() + " "
					+ getMinYMeters() + " " + getMaxYMeters());
	}// findMaxMin

	/**
	 * compare metadata object to default metadata values. If different, convert
	 * coordinates using semmscon.
	 */
	public void convertToBathymetryDatum() {
		// hor and ver units should now be stored in US Survey feet.
		short utm83_units = 1;
		short utm27_units = 1;
		short utm83_zone = 10;
		short utm27_zone = 10;
		CsdpFileMetadata m = CsdpFunctions.getLandmarkMetadata();
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
			JOptionPane.showMessageDialog(_gui, "ERROR in Landmark.convertToBathymetryDatum: Coordinates need "
					+ "to be converted, but I can't convert them. " + "Landmark.convertToBathymetryDatum: currentZone "
					+ "is different from bathymetry zone.  They should be the same!", 
					"Error", JOptionPane.ERROR_MESSAGE);
		} else {
			sameZone = true;
		}

		if (bathymetryHDatum != currentHDatum) {
			if (bathymetryHDatum == CsdpFileMetadata.UTMNAD83) {
				if (currentHDatum == CsdpFileMetadata.UTMNAD27) {
					utm27to83 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "landmark horizontal datum is different from "
							+ "bathymetry horizontal datum, but I don't know " + "how to convert the landmark datum,"
							+ currentHDatum + ", to the bathymetry datum, " + bathymetryHDatum, 
							"Error", JOptionPane.ERROR_MESSAGE);

				} // if need to convert from utm nad27
			} else if (bathymetryHDatum == CsdpFileMetadata.UTMNAD27) {
				if (currentHDatum == CsdpFileMetadata.UTMNAD83) {
					utm83to27 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "landmark horizontal datum is different from "
							+ "bathymetry horizontal datum, but I don't know " + "how to convert the landmark datum,"
							+ currentHDatum + ", to the bathymetry datum, " + bathymetryHDatum, 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			// no need to convert horizontal coordinates
		} // need to convert horizontal coordinates?

		if (bathymetryVDatum != currentVDatum) {
			if (bathymetryVDatum == CsdpFileMetadata.NAVD1988) {
				if (currentVDatum == CsdpFileMetadata.NGVD1929) {
					ngvd29toNavd88 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "landmark vertical datum is different from "
							+ "bathymetry vertical datum, but I don't know " + "how to convert the landmark datum,"
							+ currentVDatum + ", to the bathymetry datum, " + bathymetryVDatum, 
							"Error", JOptionPane.ERROR_MESSAGE);
				} //
			} else if (bathymetryVDatum == CsdpFileMetadata.NGVD1929) {
				if (currentVDatum == CsdpFileMetadata.NAVD1988) {
					navd88toNgvd29 = true;
				} else {
					JOptionPane.showMessageDialog(_gui, "landmark vertical datum is different from "
							+ "bathymetry vertical datum, but I don't know " + "how to convert the landmark datum,"
							+ currentVDatum + ", to the bathymetry datum, " + bathymetryVDatum, 
							"Error", JOptionPane.ERROR_MESSAGE);
				} //
			} // if bath datum is...
		} else {
			// no need to convert v datum
		} // if different datum or the same

		if (DEBUG)
			System.out.println("sameZone, utm83to27, utm27to83, navd88toNgvd29, ngvd29toNavd88=" + sameZone + ","
					+ utm83to27 + "," + utm27to83 + "," + navd88toNgvd29 + "," + ngvd29toNavd88);
		/*
		 * Do the conversion if the horizontal datums are in the same zone
		 */
		if (sameZone) {
			if (m.getHDatum() == bm.getHDatum() && m.getVDatum() == bm.getVDatum()) {
				System.out.println("Landmark.convertToBathymetryDatum: already in "
						+ "bathymetry datum. Not converting horizontal coordinates.");
			} else {
				System.out.println("m.getHDatum, bm.getHDatum, m.getVDatum, bm.getVDatum=" + m.getHDatum() + ","
						+ bm.getHDatum() + "," + m.getVDatum() + "," + bm.getVDatum());

				JOptionPane.showMessageDialog(_gui, "Converting landmark (" + m.getHDatumString() + "," + m.getVDatumString()
				+ ")" + " to bathymetry datum (" + bm.getHDatumString() + "," + bm.getVDatumString() + ")", 
						"Error", JOptionPane.ERROR_MESSAGE);
				UseSemmscon us = CsdpFunctions.getUseSemmscon();
				// for each landmark, convert coordinates
				String name = null;
				for (Enumeration<LandmarkPoint> e = _landmarkTable.elements(); e.hasMoreElements();) {
					LandmarkPoint lp = e.nextElement();
					double xold = lp.getXFeet();
					double yold = lp.getYFeet();
					double xnew = -Double.MAX_VALUE;
					double ynew = -Double.MAX_VALUE;
					double[] xynew = null;
					boolean converted = false;
					if (utm27to83) {
						xynew = us.utm27ToUtm83((double) xold, (double) yold, utm27_zone, utm27_units, utm83_zone,
								utm83_units);
						converted = true;
					} else if (utm83to27) {
						xynew = us.utm83ToUtm27((double) xold, (double) yold, utm83_zone, utm83_units, utm27_zone,
								utm27_units);
						converted = true;
					}
					if (converted) {
						lp.setXFeet((double) xynew[CsdpFunctions.xIndex]);
						lp.setYFeet((double) xynew[CsdpFunctions.yIndex]);
					} else {
						lp.setXFeet((double) xold);
						lp.setYFeet((double) yold);
					}
				}

			} // if need to change datum
		} // if same zone or not (if not, won't do datum change)
			// set landmark datum to bath datum
		m.setHDatum(bm.getHDatum());
		m.setVDatum(bm.getVDatum());
		setIsUpdated(true);
	}// convertToBathymetryDatum

	/**
	 * returns x value in feet
	 */
	public double getXFeet(String landmarkName) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null)
			return lp.x;
		else
			return -Double.MAX_VALUE;
	}

	/**
	 * returns y value in feet
	 */
	public double getYFeet(String landmarkName) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null)
			return lp.y;
		else
			return -Double.MAX_VALUE;
	}

	/**
	 * returns x value in meters
	 */
	public double getXMeters(String landmarkName) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null)
			return CsdpFunctions.feetToMeters(lp.x);
		else
			return -Double.MAX_VALUE;
	}

	/**
	 * called by landmark interactor
	 */
	public void putXFeet(String landmarkName, double newX) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null) {
			lp.setXFeet(newX);
			if (newX < getMinXMeters())
				putMinXMeters(newX);
			if (newX > getMaxXMeters())
				putMaxXMeters(newX);
		}
	}// putXMeters

	/**
	 * called by landmark interactor
	 */
	public void putYFeet(String landmarkName, double newY) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null) {
			lp.setYFeet(newY);
			if (newY < getMinXMeters())
				putMinXMeters(newY);
			if (newY > getMaxXMeters())
				putMaxXMeters(newY);
		}
	}// putXMeters

	/**
	 * returns y value in meters
	 */
	public double getYMeters(String landmarkName) {
		LandmarkPoint lp = _landmarkTable.get(landmarkName);
		if (lp != null)
			return CsdpFunctions.feetToMeters(lp.y);
		else
			return -Double.MAX_VALUE;
	}

	/**
	 * stores value of minimum x coordinate in feet
	 */
	private void putMinXFeet(double value) {
		_minX = value;
	}

	/**
	 * stores value of maximum x coordinate in feet
	 */
	private void putMaxXFeet(double value) {
		_maxX = value;
	}

	/**
	 * stores value of minimum y coordinate in feet
	 */
	private void putMinYFeet(double value) {
		_minY = value;
	}

	/**
	 * stores value of maximum y coordinate in feet
	 */
	private void putMaxYFeet(double value) {
		_maxY = value;
	}

	/**
	 * returns value of minimum x coordinate in feet
	 */
	protected double getMinXFeet() {
		return _minX;
	}

	/**
	 * returns value of maximum x coordinate in feet
	 */
	protected double getMaxXFeet() {
		return _maxX;
	}

	/**
	 * returns value of minimum y coordinate in feet
	 */
	protected double getMinYFeet() {
		return _minY;
	}

	/**
	 * returns value of maximum y coordinate in feet
	 */
	protected double getMaxYFeet() {
		return _maxY;
	}

	/**
	 * stores value of minimum x coordinate in meters
	 */
	private void putMinXMeters(double value) {
		_minX = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * stores value of maximum x coordinate in meters
	 */
	private void putMaxXMeters(double value) {
		_maxX = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * stores value of minimum y coordinate in meters
	 */
	private void putMinYMeters(double value) {
		_minY = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * stores value of maximum y coordinate in meters
	 */
	private void putMaxYMeters(double value) {
		_maxY = CsdpFunctions.metersToFeet(value);
	}

	/**
	 * returns value of minimum x coordinate in meters
	 */
	protected double getMinXMeters() {
		return CsdpFunctions.feetToMeters(_minX);
	}

	/**
	 * returns value of maximum x coordinate in meters
	 */
	protected double getMaxXMeters() {
		return CsdpFunctions.feetToMeters(_maxX);
	}

	/**
	 * returns value of minimum y coordinate in meters
	 */
	protected double getMinYMeters() {
		return CsdpFunctions.feetToMeters(_minY);
	}

	/**
	 * returns value of maximum y coordinate in meters
	 */
	protected double getMaxYMeters() {
		return CsdpFunctions.feetToMeters(_maxY);
	}

	/**
	 * true if landmark has been changed:warning will be displayed before ending
	 * program
	 */
	public boolean isUpdated() {
		return _isUpdated;
	}

	public boolean containsLandmark(String landmarkName) {
		return _landmarkTable.containsKey(landmarkName);
	}
	
	/**
	 * set to true whenever network is changed
	 */
	public void setIsUpdated(boolean value) {
		_isUpdated = value;

		if (DEBUG)
			System.out.println("setting is updated to " + value);

	}
	
	/*
	 * This will be used when saving landmarks, to make sure they're sorted. Having every 
	 * landmark file sorted will help with file comparison/version control.
	 */
	public ResizableStringArray getSortedLandmarkNameRSA() {
		Enumeration<String> landmarkKeysEnum = _landmarkTable.keys();
		
		int numLandmarks = _landmarkTable.size();
		ResizableStringArray returnRSA = new ResizableStringArray(numLandmarks);
		int i=0;
		while(landmarkKeysEnum.hasMoreElements()) {
			returnRSA.put(i, landmarkKeysEnum.nextElement());
			i++;
		}
		if(_landmarkTable.size()>1) {
			returnRSA = CsdpFunctions.qsort(returnRSA, 0, returnRSA.getSize()-1);
		}
		return returnRSA;
	}//getSortedLandmarkNameRSA


	public class LandmarkPoint {
		/*
		 * Constructor. Don't create a constructor that tries to set x and y,
		 * because x and y will then become static variables
		 */
		public LandmarkPoint(String n) {
			name = n;
		}

		public double getXFeet() {
			return x;
		}

		public double getYFeet() {
			return y;
		}

		public void setXFeet(double value) {
			x = value;
		}

		public void setYFeet(double value) {
			y = value;
		}

		public void rename(String newName) {
			name = newName;
		}

		private double x;
		private double y;
		String name;
	}// class LandmarkPoint

	/*
	 * make sure newName isn't an existing name
	 */
	public boolean renameLandmark(String oldName, String newName) {
		boolean success = false;
		if (!_landmarkTable.containsKey(newName)) {
			LandmarkPoint lp = _landmarkTable.get(oldName);
			lp.rename(newName);
			_landmarkTable.put(newName, lp);
			_landmarkTable.remove(oldName);
			success = true;
		} else {
			System.out.println("Landmark.renameLandmark: not renaming because newName already exists");
			success = false;
		} // if
		return success;
	}// renameLandmark

	public void setSelectedLandmarkName(String ln) {
		_selectedLandmarkName = ln;
	}

	public String getSelectedLandmarkName() {
		return _selectedLandmarkName;
	}


}// class Landmark
