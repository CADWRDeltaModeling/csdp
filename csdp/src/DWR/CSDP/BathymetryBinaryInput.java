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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Read binary bathymetry data.
 *
 * @author
 * @version $Id: BathymetryBinaryInput.java,v 1.3 2005/04/07 23:59:24 btom Exp $
 */

public class BathymetryBinaryInput extends BathymetryInput {

	DataInputStream _binaryIn = null;

	/**
	 * Open ascii file
	 */
	protected void open() {
		try {
			FileInputStream _bInStream;

			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}

			_bInStream = new FileInputStream(_directory + _filename + "." + _filetype);
			BufferedInputStream bis = new BufferedInputStream(_bInStream, 2048 * 32);
			_binaryIn = new DataInputStream(bis);

		} catch (IOException e) {
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch
	}

	/**
	 * Read binary file headers (first line or metadata)
	 */
	protected void readHeaders() {
		CsdpFileMetadata bathymetryMetadata = new CsdpFileMetadata();
		boolean fileHasMetadata = false;
		int numData = -Integer.MAX_VALUE;
		try {
			// try to read metadata
			for (int i = 0; i <= CsdpFunctions.getNumMetadataLines() - 1; i++) {
				String metaLine = _binaryIn.readUTF();
				if (metaLine.indexOf(";") >= 0) {
					parseMetadata(metaLine, bathymetryMetadata);
					if (DEBUG)
						System.out.println("parsed metadataline:" + metaLine);
				} else {
					_errorDialog.setMessage(
							"incomplete metadata! there should be " + CsdpFunctions.getNumMetadataLines() + " lines.  "
									+ "The following line was expected to be a metadata line:" + metaLine);
					_errorDialog.setVisible(true);
				}
			} // for: read metadata

			if (DEBUG)
				System.out.println(
						"BathymetryBinaryInput: done parsing metadata. " + "hdatum, hzone, hunits,vdatum,vunits=");
			if (DEBUG)
				System.out.println(bathymetryMetadata.getHDatumString() + "," + bathymetryMetadata.getHZone() + ","
						+ bathymetryMetadata.getHUnitsString() + "," + bathymetryMetadata.getVDatumString() + ","
						+ bathymetryMetadata.getVUnitsString());
			CsdpFunctions.setBathymetryMetadata(bathymetryMetadata);
			fileHasMetadata = true;
		} catch (IOException e1) {
			System.out
					.println("exception trying to read file with metadata." + "  Trying to read as file w/o metadata.");
			// just read first value(numLines)
			try {
				if (DEBUG)
					System.out.println("binaryIn = " + _binaryIn);
				numData = _binaryIn.readInt();
				// _data.putNumLines(numData);
				System.out.println("number of lines in binary file=" + _data.getNumLines());
			} catch (IOException e2) {
				System.out.println("error reading number of lines in binary bathymetry file.");
			}
			fileHasMetadata = false;
		} // catch
	}// read

	/**
	 * Read data
	 */
	protected void read() {
		try {
			for (int lineNum = 0; lineNum <= _numLines - 1; lineNum++) {
				_pd.x = (float) _binaryIn.readDouble();
				_pd.y = (float) _binaryIn.readDouble();
				_pd.z = (float) _binaryIn.readDouble();
				_pd.year = _binaryIn.readShort();
				_pd.source = _binaryIn.readUTF();
				storeData(lineNum, CsdpFunctions.getBathymetryMetadata());
			} // for
			close();
			_data.findMaxMin(_numLines);
		} catch (IOException e3) {
			System.out
					.println("Error ocurred while reading file " + _directory + _filename + ".cdp: " + e3.getMessage());
		} // catch
	}// read

	/**
	 * Close binary bathymetry data file
	 */
	protected void close() {
		if (DEBUG)
			System.out.println("closing binary bathymetry file");
		try {
			_binaryIn.close();
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while closing file " + _directory + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}// close

} // class BathymetryBinaryInput
