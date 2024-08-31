package DWR.CSDP;

/**
 * Stores the values that are parsed from a line that is read from an ascii
 * file.
 *
 * @author
 * @version $Id: BathymetryParsedData.java,v 1.1.1.1 2002/06/10 20:15:01 btom
 *          Exp $
 */
public class BathymetryParsedData {

	public float x;
	public float y;
	public float z;
	public short year;
	public String source = null;
	public String description = null;

	public int ctrlineName;
	public int numCtrlinePoints;
	public int numXsects;
	public int firstCtrlinePoint;

	public int numXsectPoints; // depends on # of pts. calculated.
	public int distFromUpstream;
	public int xsectLineLength;
	public int firstXsectPoint;

} // class BathymetryParsedData
