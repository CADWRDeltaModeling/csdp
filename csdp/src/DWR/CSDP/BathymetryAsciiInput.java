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

public class BathymetryAsciiInput extends BathymetryInput {

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
	 * Read ascii file. Headers should have following format: ;HorizontalDatum:
	 * UTMNAD27 ;HorizontalZone: 10 ;HorizontalUnits: Meters ;VerticalDatum:
	 * NGVD29 ;VerticalUnits: USSurveyFeet ;Filetype: bathymetry ;NumElements:
	 * 581913
	 *
	 * If it's an older version of the file, the header will just be a single
	 * integer that is the number of points in the file. Example: 581983
	 */
	protected void readHeaders() {
		// int numLines=0;
		// metadata for current file. will compare to default metadata,
		// which was created by the Csdp class, to determine if any
		// coordinate conversions need to be made.
		CsdpFileMetadata bathymetryMetadata = new CsdpFileMetadata();
		boolean fileHasMetadata = false;

		try {
			String line = null;
			line = _asciiIn.readLine();
			if (line.indexOf(";") < 0) {
				fileHasMetadata = false;
				JOptionPane.showMessageDialog(_gui, "UTM zone 10 NAD 27, NGVD 1929 will be assumed.", 
						"This bathymetry file has no metadata. ", JOptionPane.INFORMATION_MESSAGE);
				bathymetryMetadata.setToDefault();
				parseFirstLine(line);
			} else {
				fileHasMetadata = true;
				if (DEBUG)
					System.out.println("parsing bathymetry metadata");
				// parseFirstLine(line);
				/// require 7 lines of metadata
				// already read the first line
				for (int i = 0; i <= CsdpFunctions.getNumMetadataLines() - 1; i++) {
					// first line already read
					if (i > 0)
						line = _asciiIn.readLine();
					if (line.indexOf(";") >= 0) {
						parseMetadata(line, bathymetryMetadata);
					} else {
						JOptionPane.showMessageDialog(_gui, "incomplete metadata! there should be "
								+ CsdpFunctions.getNumMetadataLines() + " lines.  "
								+ "The following line was expected to be a metadata line:" + line, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (DEBUG)
					System.out.println("done parsing metadata. hdatum, hzone, hunits,vdatum,vunits="
							+ bathymetryMetadata.getHDatumString() + "," + bathymetryMetadata.getHZone() + ","
							+ bathymetryMetadata.getHUnitsString() + "," + bathymetryMetadata.getVDatumString() + ","
							+ bathymetryMetadata.getVUnitsString());
			} // if metadata or not
			CsdpFunctions.setBathymetryMetadata(bathymetryMetadata);

		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} // try - catch
	}// read

	protected void read() {
		try {
			// now read data
			for (int i = 0; i <= _data.getNumLines() - 1; i++) {
				String line = _asciiIn.readLine();
				parseBathymetryData(i, line);
				storeData(i, CsdpFunctions.getBathymetryMetadata());
			} // for
			_data.findMaxMin(_data.getNumLines());
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} finally {
			// maybe not a good idea to change bathymetry?
			// _data.convertToDefaultDatum(bathymetryMetadata, fileHasMetadata);
			close();
		} // catch()
	}// read

	/**
	 * Close ascii bathymetry data file
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
	 * Parses a line from an ascii bathymetry data file (*.prn) pLineNum is
	 * actually the line number-1. Store values in the vector parsedData.
	 */
	protected void parseBathymetryData(int linenum, String unparsedLine) {
		StringTokenizer t = null;
		try {
			t = new StringTokenizer(unparsedLine, " ,\042\011");
		} catch (java.lang.NullPointerException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("unable to create StringTokenizer for line # " + linenum);
			System.out.println("line: " + unparsedLine);
		} // catch
		float pX = -Float.MAX_VALUE;
		float pY = -Float.MAX_VALUE;
		float pZ = -Float.MAX_VALUE;
		short pYear = -Short.MAX_VALUE;
		String nextToken = t.nextToken();

		try {
			pX = Float.parseFloat(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("Unable to parse " + nextToken + " as a float");
			System.out.println("line number=" + linenum);
			System.out.println("line=" + unparsedLine);
		}
		nextToken = t.nextToken();
		try {
			pY = Float.parseFloat(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("Unable to parse " + nextToken + " as a float");
			System.out.println("line number=" + linenum);
			System.out.println("line=" + unparsedLine);
		}
		nextToken = t.nextToken();
		try {
			pZ = Float.parseFloat(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("Unable to parse " + nextToken + " as a float");
			System.out.println("line=" + unparsedLine);
		}
		nextToken = t.nextToken();
		try {
			pYear = Short.parseShort(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("Unable to parse " + nextToken + " as a float");
			System.out.println("line number=" + linenum);
			System.out.println("line=" + unparsedLine);
		}

		try {
			_pd.x = pX;
			_pd.y = pY;
			_pd.z = pZ;
			_pd.year = pYear;
			_pd.source = t.nextToken();
			if (t.hasMoreTokens())
				_pd.description = t.nextToken();
		} catch (java.lang.NullPointerException e) {
			System.out.println("Error in BathymetryAsciiInput.parseBathymetryData");
			System.out.println("Unable to convert values to primitives.");
			System.out.println("line number=" + linenum);
			System.out.println("line=" + unparsedLine);
		} // catch
	} // parse

	/**
	 * Parses 1st line of file which contains number of points in file
	 */
	protected void parseFirstLine(String firstLine) {
		StringTokenizer t = new StringTokenizer(firstLine, " ");
		String nextToken = t.nextToken();
		if (firstLine.indexOf("num") >= 0) {
			nextToken = t.nextToken();
		}
		// int numLines = -Integer.MAX_VALUE;
		try {
			// numLines = new Integer(nextToken);
			_numLines = Integer.parseInt(nextToken);
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Error reading first line: " + nextToken);
			System.out.println("The first line must contain only one value, which");
			System.out.println("should be an integer which is the number of values");
			System.out.println("in the file.  Did you forget to do this?");
		} // try
		if (DEBUG)
			System.out.println(_numLines);
		// _data.putNumLines(numLines);
	} // parseFirstLine

} // class BathymetryAsciiInput
