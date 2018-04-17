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

public class PropertiesAsciiInput extends PropertiesInput {

	LineNumberReader _asciiIn;
	private float _parsedMinElevBin = -Float.MAX_VALUE;
	private float _parsedMaxElevBin = -Float.MAX_VALUE;
	private int _parsedNumElevBins = -Integer.MAX_VALUE;

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
	 * Read ascii file
	 */
	protected void read() {
		int numLines = 0;
		_parsedMinElevBin = -Float.MAX_VALUE;
		_parsedMaxElevBin = -Float.MAX_VALUE;
		_parsedNumElevBins = -Integer.MAX_VALUE;

		try {
			String line = "0";
			// for(int i=0; i<=CsdpFunctions.getNumColors()-1; i++){
			int i = 0;
			while (line.length() > 0) {
				// for(int i=0; i<=_gui.getNumColors()-1; i++){
				line = _asciiIn.readLine();
				parsePropertiesData(i, line);
				storeData(i, _colorValue);
				i++;
			} // for
			try {
				// look for bin settings
				while (line.indexOf("minelevbin") < 0) {
					line = _asciiIn.readLine();
				}
				parseBinData(line);
				while (line.indexOf("maxelevbin") < 0) {
					line = _asciiIn.readLine();
				}
				parseBinData(line);
				while (line.indexOf("numelevbin") < 0) {
					line = _asciiIn.readLine();
				}
				parseBinData(line);

				// update values in gui
				if (_parsedMinElevBin > -999999.0 && _parsedMaxElevBin > -999999.0 && _parsedNumElevBins > -999999) {
					_gui.updateElevBinValues(_parsedMinElevBin, _parsedMaxElevBin, _parsedNumElevBins);
				}
			} catch (java.lang.NullPointerException e) {
				System.out.println("nullPointer in PropertiesAsciiInput.read.  Could be an incomplete properties file");
			}
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} finally {
			close();
		} // catch()
	}

	/**
	 * Close ascii properties data file
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
	 * Parses a line from an ascii properties data file *.prn.
	 */
	protected void parseBinData(String line) {
		StringTokenizer t = new StringTokenizer(line, "=");
		String nextToken = t.nextToken();

		if (nextToken.indexOf("minelevbin") >= 0) {
			nextToken = t.nextToken();
			try {
				_parsedMinElevBin = Float.parseFloat(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("error in PropertiesAsciiInput.parseBinData:");
				System.out.println("Unable to parse minelevbin " + nextToken + " as a float");
			}
		}

		if (nextToken.indexOf("maxelevbin") >= 0) {
			nextToken = t.nextToken();
			try {
				_parsedMaxElevBin = Float.parseFloat(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("error in PropertiesAsciiInput.parseBinData:");
				System.out.println("Unable to parse maxelevbin " + nextToken + " as a float");
			}
		}
		if (nextToken.indexOf("numelevbins") >= 0) {
			nextToken = t.nextToken();
			try {
				_parsedNumElevBins = Integer.parseInt(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("error in PropertiesAsciiInput.parseBinData:");
				System.out.println("Unable to parse numelevbins " + nextToken + " as an int");
			}
		}
	}// parseBinData

	/**
	 * Parses a line from an ascii properties data file (*.prn) pLineNum is
	 * actually the line number-1. Store values in the vector parsedData.
	 */
	protected void parsePropertiesData(int linenum, String unparsedLine) {
		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011");

		int red = -Integer.MAX_VALUE;
		int green = -Integer.MAX_VALUE;
		int blue = -Integer.MAX_VALUE;
		String nextToken = null;
		try {
			nextToken = t.nextToken();
			try {
				red = Integer.parseInt(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Error in PropertiesAsciiInput.parsePropertiesData");
				System.out.println("Unable to parse " + nextToken + " as an int");
			} // try
			nextToken = t.nextToken();
			try {
				green = Integer.parseInt(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Error in PropertiesAsciiInput.parsePropertiesData");
				System.out.println("Unable to parse " + nextToken + " as an int");
			} // try
			nextToken = t.nextToken();
			try {
				blue = Integer.parseInt(nextToken);
			} catch (java.lang.NumberFormatException e) {
				System.out.println("Error in PropertiesAsciiInput.parsePropertiesData");
				System.out.println("Unable to parse " + nextToken + " as an int");
			} // try

			_colorValue[0] = red;
			_colorValue[1] = green;
			_colorValue[2] = blue;
		} catch (java.util.NoSuchElementException e) {
			System.out.println("error in PropertiesAsciiInput.parsePropertiesData");
		}
	} // parse

	int[] _colorValue = new int[3];

} // class PropertiesAsciiInput
