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

import javax.swing.JOptionPane;


/**
 * Read landmark data. Landmarks are symbols with labels that are displayed on
 * map.
 *
 * @author
 * @version
 */
public abstract class LandmarkInput {

	/**
	 * Make instance of subclass of LandmarkInput
	 */
	public static LandmarkInput getInstance(CsdpFrame gui, String directory, String filename) {
		_directory = directory;
		_landmark = new Landmark(gui);
		_gui = gui;

		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}

		_filename = null;
		_filetype = null;
		LandmarkInput input = null;
		if (filename.endsWith(ASCII_TYPE)) {
			input = new LandmarkAsciiInput();
			_filetype = ASCII_TYPE;
		}

		// int dotIndex = filename.indexOf(".",0);
		// _filename = filename.substring(0,dotIndex);
		// _filetype = filename.substring(dotIndex+1);
		// LandmarkInput input = null;
		// if (_filetype.equals(ASCII_TYPE)) {
		// input = new LandmarkAsciiInput();
		// }
		else {// throw new IllegalInputFileException(msg);
			System.out.println("No landmark filetype defined for " + _filetype);
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
	public Landmark readData() {
		open();
		read();
		close();
		_landmark.convertToBathymetryDatum();
		return _landmark;
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

	/*
	 * ;HorizontalDatum: UTMNAD27 ;HorizontalZone: 10 ;HorizontalUnits: Meters
	 * ;VerticalDatum: NGVD29 ;VerticalUnits: USSurveyFeet ;Filetype: network
	 * ;NumElements: xxxx
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
				JOptionPane.showMessageDialog(_gui, "HorizontalDatum " + nextToken + " not recognized.  using default horizontal datum.", 
						"Using default zone", JOptionPane.INFORMATION_MESSAGE);

			}
		} else if (line.indexOf("HorizontalZone") >= 0) {
			if (nextToken.equalsIgnoreCase("10"))
				m.setHZone(10);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalZone " + nextToken + " not recognized.  using default horizontal zone.", 
						"Using default zone", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("HorizontalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("Meters"))
				m.setHUnits(CsdpFileMetadata.METERS);
			else if (nextToken.equalsIgnoreCase("Feet"))
				m.setHUnits(CsdpFileMetadata.USSURVEYFEET);
			else {
				JOptionPane.showMessageDialog(_gui, "HorizontalUnits " + nextToken + " not recognized.  using default horizontal units.", 
						"Units not found", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalDatum") >= 0) {
			if (nextToken.equalsIgnoreCase("NGVD29"))
				m.setVDatum(CsdpFileMetadata.NGVD1929);
			else if (nextToken.equalsIgnoreCase("NAVD88"))
				m.setVDatum(CsdpFileMetadata.NAVD1988);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalDatum " + nextToken + " not recognized.  using default vertical datum.", 
						"Using default datum", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("VerticalUnits") >= 0) {
			if (nextToken.equalsIgnoreCase("USSurveyFeet"))
				m.setVUnits(CsdpFileMetadata.USSURVEYFEET);
			else if (nextToken.equalsIgnoreCase("meters"))
				m.setVUnits(CsdpFileMetadata.METERS);
			else {
				JOptionPane.showMessageDialog(_gui, "VerticalUnits " + nextToken + " not recognized.  using default vertical units.", 
						"Using default units", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (line.indexOf("Filetype") >= 0) {
			// do nothing...
		} else if (line.indexOf("NumElements") > 0) {
			try {
				// numLines = new Integer(nextToken);
				int numLines = Integer.parseInt(nextToken);
				//// _net.putNumCenterlines(numLines);
				_numLandmarks = numLines;
				m.setNumElements(numLines);
			} catch (java.lang.NumberFormatException e) {
				JOptionPane.showMessageDialog(_gui, "Error reading metadata line. Expecting NumElements. line=" + line, 
						"Metadata error", JOptionPane.ERROR_MESSAGE);
			} // try
		} else {
			JOptionPane.showMessageDialog(_gui, "unable to parse metadata line: " + line + ". File may not be loaded correctly", 
					"Metadata error", JOptionPane.ERROR_MESSAGE);
		}
	}// parseMetadata

	/**
	 * Stores a landmark point
	 */
	protected void storeDataMeters(int dataNumber) {
		float x = _pd.x;
		float y = _pd.y;
		String name = _pd.name;
		_landmark.addLandmarkMeters(name, x, y);
	}// storeData

	/**
	 * Find maximum and minimum values of x and y and store them.
	 */
	public void findMaxMin(int numLines) {
		_landmark.findMaxMin(numLines);
	}

	protected static Landmark _landmark;
	protected LandmarkParsedData _pd = new LandmarkParsedData(); // vector-stores
																	// 6 values
	public static final boolean DEBUG = false;

	protected static String _filename = null;// part of filename before the
												// first dot
	protected static String _filetype = null;// filename extension (after first
												// dot)
	protected static final String ASCII_TYPE = "cdl";
	protected static String _directory = null;
	protected int _numLandmarks;
	protected static CsdpFrame _gui;
} // class LandmarkInput
