package DWR.CSDP;

public class CsdpFileMetadata implements Cloneable {
	public static final int UTMNAD27 = 0;
	public static final int UTMNAD83 = 1;
	public static final int METERS = 2;
	public static final int USSURVEYFEET = 3;
	public static final int NGVD1929 = 20;
	public static final int NAVD1988 = 21;

	private int _hDatum;
	private int _hZone;
	private int _hUnits;
	private int _vDatum;
	private int _vUnits;
	private int _numElements = -Integer.MAX_VALUE;

	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {
			System.err.println("CsdpFileMetadata object can't clone");
		}
		return o;
	}

	/**
	 * Configure metadata to pre-version 2.4 specifications (before csdp input
	 * files had metadata)
	 */
	public void setToDefault() {
		setHDatum(UTMNAD27);
		setHZone(10);
		setHUnits(METERS);
		setVDatum(NGVD1929);
		setVUnits(USSURVEYFEET);
	}

	public void setHDatum(int value) {
		if (value == UTMNAD27)
			_hDatum = UTMNAD27;
		else if (value == UTMNAD83)
			_hDatum = UTMNAD83;
		else
			System.out.println("error in CsdpFileMetadata: unrecognized value for hdatum");
	}

	public void setHZone(int value) {
		if (value == 10) {
		} else
			System.out.println("error in CsdpFileMetadata: unrecognized value for hzone");
		_hZone = value;
	}

	public void setHUnits(int value) {
		if (value == METERS)
			_hUnits = METERS;
		else if (value == USSURVEYFEET)
			_hUnits = USSURVEYFEET;
		else
			System.out.println("error in CsdpFileMetadata: unrecognized value for hunits");
	}

	public void setVDatum(int value) {
		if (value == NGVD1929)
			_vDatum = NGVD1929;
		else if (value == NAVD1988)
			_vDatum = NAVD1988;
		else
			System.out.println("error in CsdpFileMetadata: unrecognized value for vdatum");
	}

	public void setVUnits(int value) {
		if (value == USSURVEYFEET)
			_vUnits = USSURVEYFEET;
		else if (value == METERS)
			_vUnits = METERS;
		else
			System.out.println("error in CsdpFileMetadata: unrecognized value for vunits");
	}

	public void setNumElements(int value) {
		_numElements = value;
	}

	public String getHDatumString() {
		String rtn = null;
		if (_hDatum == UTMNAD83)
			rtn = "UTMNAD83";
		else if (_hDatum == UTMNAD27)
			rtn = "UTMNAD27";
		else {
		}
		return rtn;
	}

	public String getHUnitsString() {
		String rtn = null;
		if (_hUnits == METERS)
			rtn = "Meters";
		else if (_hUnits == USSURVEYFEET)
			rtn = "USSurveyFeet";
		else {
		}
		return rtn;
	}

	public String getVDatumString() {
		String rtn = null;
		if (_vDatum == NGVD1929)
			rtn = "NGVD29";
		else if (_vDatum == NAVD1988)
			rtn = "NAVD88";
		else {
		}
		return rtn;
	}

	public String getVUnitsString() {
		String rtn = null;
		if (_vUnits == METERS)
			rtn = "Meters";
		else if (_vUnits == USSURVEYFEET)
			rtn = "USSurveyFeet";
		else {
		}
		return rtn;
	}

	public int getHDatum() {
		return _hDatum;
	}

	public int getHZone() {
		return _hZone;
	}

	public int getHUnits() {
		return _hUnits;
	}

	public int getVDatum() {
		return _vDatum;
	}

	public int getVUnits() {
		return _vUnits;
	}

	public int getNumElements() {
		return _numElements;
	}
}// class CsdpFileMetadata
