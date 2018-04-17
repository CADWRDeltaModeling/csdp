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

import java.io.File;
import java.util.StringTokenizer;

import DWR.CSDP.dialog.OkDialog;

/**
 * Read USGS Digital Line Graph Files. These files are available at
 * http://bard.wr.usgs.gov/htmldir/dlg_html/hypso.html
 *
 * @author
 * @version
 */
public abstract class DigitalLineGraphInput {

	/**
	 * Make instance of subclass of DigitalLineGraphInput
	 */
	public static DigitalLineGraphInput getInstance(CsdpFrame gui, String directory, String filename) {
		_gui = gui;
		_noMetadataDialog = new OkDialog(_gui,
				"This DigitalLineGraph file has no metadata. " + "UTM zone 10 NAD 27, NGVD 1929 will be assumed.",
				true);
		_errorDialog = new OkDialog(_gui, "error message", true);
		_dlg = new DigitalLineGraph(_gui);
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		DigitalLineGraphInput input = null;
		if (filename.endsWith(ASCII_TYPE)) {
			input = new DigitalLineGraphAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// DigitalLineGraphInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new DigitalLineGraphAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No digital line graph filetype defined for " + _filetype);
			_filetype = null;
		}
		if (_filetype != null) {
			_filename = filename.substring(0, filename.lastIndexOf(_filetype) - 1);
		}
		return input;
	} // getInstance

	/**
	 * Calls appropriate read method to read Landmark data
	 */
	public DigitalLineGraph readData() {
		open();
		read();
		close();
		_dlg.convertToBathymetryDatum();
		return _dlg;
	}

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * Read file
	 */
	protected abstract void read();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * add digital line graph line
	 */
	protected void addLine() {
		_dlg.addLine(_currentLineName);
	}

	/**
	 * add digital line graph point
	 */
	protected void addAndStorePoint() {
		DigitalLineGraphLine line = _dlg.getLine(_currentLineName);
		double x = CsdpFunctions.metersToFeet(_x);
		double y = CsdpFunctions.metersToFeet(_y);
		line.addPoint(_currentPointIndex, x, y);
		if (DEBUG)
			System.out.println("adding point for line " + _currentLineName + ",x,y=" + _x + "," + _y);

		_currentPointIndex++;
	}// addAndStorePoint

	/**
	 * Find maximum and minimum values of x and y and store them.
	 */
	protected void findMaxMin(int numLines) {
		double x = 0.0;
		double y = 0.0;
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		DigitalLineGraphLine line = null;

		if (DEBUG)
			System.out.println("finding max and min values.  number of points=" + numLines);
		for (int i = 0; i <= numLines - 1; i++) {
			line = _dlg.getLine(_dlg.getDigitalLineGraphLineName(i));
			for (int j = 0; j <= line.getNumPoints() - 1; j++) {
				x = line.getX(j);
				y = line.getY(j);
				if (x != 0)
					minX = Math.min(x, minX);
				if (x != 0)
					maxX = Math.max(x, maxX);
				if (y != 0)
					minY = Math.min(y, minY);
				if (y != 0)
					maxY = Math.max(y, maxY);
			}
		}
		_dlg.putMinXFeet(minX);
		_dlg.putMaxXFeet(maxX);
		_dlg.putMinYFeet(minY);
		_dlg.putMaxYFeet(maxY);
		if (DEBUG)
			System.out.println("findMaxMin called. MinX,MaxX,MinY,MaxY=" + _dlg.getMinXFeet() + " " + _dlg.getMaxXFeet()
					+ " " + _dlg.getMinYFeet() + " " + _dlg.getMaxYFeet());
	}// findMaxMin

	/**
	 * ;HorizontalDatum: UTMNAD27 ;HorizontalZone: 10 ;HorizontalUnits: Meters
	 * ;VerticalDatum: NGVD29 ;VerticalUnits: USSurveyFeet ;Filetype: bathymetry
	 * ;NumElements: 581913
	 */
	protected void parseMetadata(String line, CsdpFileMetadata m) {
		StringTokenizer t = new StringTokenizer(line, " :");
		String nextToken = t.nextToken();
		nextToken = t.nextToken();
		if (line.indexOf("HorizontalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("UTMNAD27"))
				m.setHDatum(CsdpFileMetadata.UTMNAD27);
			else if (nextToken.equalsIgnoreCase("UTMNAD83"))
				m.setHDatum(CsdpFileMetadata.UTMNAD83);
			else {
				_errorDialog.setMessage(
						"HorizontalDatum " + nextToken + " not recognized.  using default horizontal datum.");
				_errorDialog.setVisible(true);
			}
		} else if (line.indexOf("HorizontalZone") >= 0) {
			if (nextToken.equalsIgnoreCase("10"))
				m.setHZone(10);
			else {
				_errorDialog
						.setMessage("HorizontalZone " + nextToken + " not recognized.  using default horizontal zone.");
				_errorDialog.setVisible(true);
			}
		} else if (line.indexOf("HorizontalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("Meters"))
				m.setHUnits(CsdpFileMetadata.METERS);
			else if (nextToken.equalsIgnoreCase("Feet"))
				m.setHUnits(CsdpFileMetadata.USSURVEYFEET);
			else {
				_errorDialog.setMessage(
						"HorizontalUnits " + nextToken + " not recognized.  using default horizontal units.");
				_errorDialog.setVisible(true);
			}
		} else if (line.indexOf("VerticalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("NGVD29"))
				m.setVDatum(CsdpFileMetadata.NGVD1929);
			else if (nextToken.equalsIgnoreCase("NAVD88"))
				m.setVDatum(CsdpFileMetadata.NAVD1988);
			else {
				_errorDialog
						.setMessage("VerticalDatum " + nextToken + " not recognized.  using default vertical datum.");
				_errorDialog.setVisible(true);
			}
		} else if (line.indexOf("VerticalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("USSurveyFeet"))
				m.setVUnits(CsdpFileMetadata.USSURVEYFEET);
			else if (nextToken.equalsIgnoreCase("meters"))
				m.setVUnits(CsdpFileMetadata.METERS);
			else {
				_errorDialog
						.setMessage("VerticalUnits " + nextToken + " not recognized.  using default vertical units.");
				_errorDialog.setVisible(true);
			}
		} else if (line.indexOf("Filetype") >= 0) {
			// do nothing...
		} else if (line.indexOf("NumElements") > 0) {
			try {
				_numLines = Integer.parseInt(nextToken);
				m.setNumElements(_numLines);
			} catch (java.lang.NumberFormatException e) {
				_errorDialog.setMessage("Error reading metadata line. Expecting NumElements. line=" + line);
				_errorDialog.setVisible(true);
			} // try
		} else {
			_errorDialog.setMessage("unable to parse metadata line: " + line + ". File may not be loaded correctly");
			_errorDialog.setVisible(true);
		}
	}// parseMetadata

	protected static DigitalLineGraph _dlg;
	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "cdo";
	protected static String _directory = null;
	protected int _numLines;
	protected double _a1;
	protected double _a2;
	protected double _a3;
	protected double _a4;
	protected ResizableStringArray _unparsedLineCoordinates;
	protected String _currentLineName;
	protected int _currentPointIndex;
	protected double _x;
	protected double _y;
	protected static OkDialog _noMetadataDialog;
	protected static OkDialog _errorDialog;
	protected static CsdpFrame _gui;
} // class DigitalLineGraphInput
