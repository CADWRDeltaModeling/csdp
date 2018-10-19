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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class LandmarkAsciiInput extends LandmarkInput {

	LineNumberReader _asciiIn;

	/**
	 * Open ascii file
	 */
	protected void open() {
		FileReader aInFile;
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}

			aInFile = new FileReader(_directory + _filename + "." + _filetype);
			_asciiIn = new LineNumberReader(aInFile);
			if (DEBUG)
				System.out.println("In ascii open " + _asciiIn);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Directory + Filetype: " + _directory + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii landmark file
	 */
	protected void read() {
		int numLines = 0;
		CsdpFileMetadata landmarkMetadata = new CsdpFileMetadata();
		boolean fileHasMetadata = false;

		String line = null;
		try {
			line = _asciiIn.readLine();
			if (line.indexOf(";") < 0) {
				fileHasMetadata = false;
				JOptionPane.showMessageDialog(_gui, "This landmark file has no metadata. " + "UTM zone 10 NAD 27, NGVD 1929 will be assumed.", 
						"No Metadata Found", JOptionPane.INFORMATION_MESSAGE);
				landmarkMetadata.setToDefault();
				parseFirstLine(line);
			} else {
				fileHasMetadata = true;
				System.out.println("parse landmark metadata");
				for (int i = 0; i <= CsdpFunctions.getNumMetadataLines() - 1; i++) {
					// first line already read
					if (i > 0)
						line = _asciiIn.readLine();
					if (line.indexOf(";") >= 0) {
						parseMetadata(line, landmarkMetadata);
					} else {
						JOptionPane.showMessageDialog(_gui, "incomplete landmark metadata! there should be " + CsdpFunctions.getNumMetadataLines()
						+ " lines.  " + "The following line was expected to be metadata line:" + line, 
								"No Metadata Found", JOptionPane.ERROR_MESSAGE);
					} // if it's a metadata line (should be)
				} // read all metadata lines
			}
			if (DEBUG)
				System.out.println("done parsing landmark metadata.hdatum,hzone,hunits,vdatum,vunits, numCenterlines="
						+ landmarkMetadata.getHDatumString() + "," + landmarkMetadata.getHZone() + ","
						+ landmarkMetadata.getHUnitsString() + "," + landmarkMetadata.getVDatumString() + ","
						+ landmarkMetadata.getVUnitsString() + "," + landmarkMetadata.getNumElements());
			CsdpFunctions.setLandmarkMetadata(landmarkMetadata);
		} catch (IOException e) {
			System.out.println("ERROR in LandmarkAsciiInput.read");
		}

		try {
			if (DEBUG)
				System.out.println("number of landmarks = " + _numLandmarks);
			for (int i = 0; i <= _numLandmarks - 1; i++) {
				line = _asciiIn.readLine();
				parseLandmarkData(i, line);
				storeDataMeters(i);
			} // for
			findMaxMin(_landmark.getNumLandmarks());
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} finally {
			close();
		} // catch()
	}

	/**
	 * Close ascii Landmark data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while closing file " + _directory + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}

	/**
	 * Parses a line from an ascii Landmark data file (*.prn) pLineNum is
	 * actually the line number-1. Store values in the vector parsedData.
	 */
	protected void parseLandmarkData(int linenum, String unparsedLine) {

		if (DEBUG)
			System.out.println("unparsedline = " + unparsedLine);

		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011");
		String pName = null;
		String nextToken = t.nextToken();
		float pX = -Float.MAX_VALUE;
		float pY = -Float.MAX_VALUE;

		try {
			pX = Float.parseFloat(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in LandmarkAsciiInput.parseLandmarkData");
			System.out.println("Unable to parse " + nextToken + " as a float");
		} // try

		nextToken = t.nextToken();

		try {
			pY = Float.parseFloat(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in LandmarkAsciiInput.parseLandmarkData");
			System.out.println("Unable to parse " + nextToken + " as a float");
		}
		while (t.hasMoreTokens())
			pName = t.nextToken();

		_pd.x = pX;
		_pd.y = pY;
		_pd.name = pName;
	} // parse

	/**
	 * Parses 1st line of file which contains number of points in file
	 */
	protected void parseFirstLine(String firstLine) {

		StringTokenizer t = new StringTokenizer(firstLine, " ");
		String nextToken = t.nextToken();
		int numLines = -Integer.MAX_VALUE;

		try {
			numLines = Integer.parseInt(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error reading first line:" + nextToken);
			System.out.println("The first line must contain only one value, which");
			System.out.println("should be an integer which is the number of values");
			System.out.println("in the file.  Did you forget to do this?");
		}
		if (DEBUG)
			System.out.println(numLines);
		_numLandmarks = numLines;
		// _landmark.putNumLandmarks(numLines.intValue());//don't do this. it's
		// incremented
		// when line is added
	} // parseFirstLine

} // class LandmarkAsciiInput
