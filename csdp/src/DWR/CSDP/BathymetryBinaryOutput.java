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

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import DWR.CSDP.XsectBathymetryData;

/**
 * Write binary bathymetry data.
 *
 * @author
 * @version $Id: BathymetryBinaryOutput.java,v 1.4 2005/04/08 00:02:08 btom Exp
 *          $
 */

public class BathymetryBinaryOutput extends BathymetryOutput {

	FileOutputStream _outFile; // binary output file
	DataOutputStream _binaryOut = null;

	/**
	 * assigns data storage object to class variable
	 */
	BathymetryBinaryOutput(BathymetryData data) {
		_data = data;
	}

	/**
	 * Open binary bathymetry output file
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			_outFile = new FileOutputStream(_directory + _filename + "." + BINARY_TYPE);
			_binaryOut = new DataOutputStream(_outFile);
		} catch (IOException e) {
			System.out.println("Error ocurred while opening file " + _directory + _filename + ".cdp:" + e.getMessage());
		}
	} // open

	/*
	 * write zoomed data (data currently visible on screen)
	 */
	protected boolean write(double[] plotBoundaries) {
		int numData = countZoomedData(plotBoundaries);
		// don't write metadata first; writeBathymetry will write metadata
		// after counting number of points.
		// boolean success1 = writeMetadata(numData);
		boolean success = writeBathymetry(numData, plotBoundaries);
		return success;
	}// write

	@Override
	protected boolean write(Centerline centerline, boolean saveInside) {
		int numData = countDataInsideOrOutsidePolygon(centerline, saveInside);
		boolean success = writeBathymetry(numData, getPolygon(centerline), saveInside);
		return success;
	}


	
	/*
	 * write all data
	 */
	protected boolean write() {
		int numData = _data.getNumLines();
		boolean success1 = writeMetadata(numData);
		boolean success2 = writeBathymetry();
		boolean success = true;
		if (success1 && success2) {
			success = true;
		} else {
			success = false;
		}
		return success;
	}

	
	/**
	 * Write binary bathymetry data file
	 */
	protected boolean writeMetadata(int numData) {
		boolean success = false;
		CsdpFileMetadata m = CsdpFunctions.getBathymetryMetadata();
		try {
			// write metadata
			_binaryOut.writeUTF(";HorizontalDatum:  " + m.getHDatumString());
			_binaryOut.writeUTF(";HorizontalZone:  " + m.getHZone());
			_binaryOut.writeUTF(";HorizontalUnits:  " + m.getHUnitsString());
			_binaryOut.writeUTF(";VerticalDatum:  " + m.getVDatumString());
			_binaryOut.writeUTF(";VerticalUnits:  " + m.getVUnitsString());
			_binaryOut.writeUTF(";Filetype: bathymetry");
			_binaryOut.writeUTF(";NumElements:  " + numData);
			success = true;
		} catch (IOException e) {
			System.out.println("Error ocurred while writing to file " + _directory + _filename + "." + BINARY_TYPE
					+ e.getMessage());
		} // catch
		return success;
	}// writeMetadata

	protected boolean writeBathymetry(int numData, double[] plotBoundaries) {
		Polygon polygon = new Polygon();
		int x1 = (int)CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.x1Index]);
		int y1 = (int)CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.y1Index]);
		int x2 = (int)CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.x2Index]);
		int y2 = (int)CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.y2Index]);

		polygon.addPoint(x1, y2);
		polygon.addPoint(x2, y2);
		polygon.addPoint(x2, y1);
		polygon.addPoint(x1, y1);
		polygon.addPoint(x1, y2);
		
		return writeBathymetry(numData, polygon, true);
	}
	
	/**
	 * Write binary bathymetry data file
	 * @param saveInside 
	 */
	protected boolean writeBathymetry(int numData, Polygon polygon, boolean saveInside) {
		boolean success = false;
		CsdpFileMetadata m = CsdpFunctions.getBathymetryMetadata();
		int numWritten = 0;
		try {
//			double xMinZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.minXIndex]);
//			double xMaxZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.maxXIndex]);
			// write data
			// _binaryOut.writeInt(_data.getNumLines());
			System.out.println("BathymetryBinaryOutput.writeBathymetry: about to enter loop. _data.getNumLines="
					+ _data.getNumLines());

			// The index of the beginning and last bathymetry point whose
			// y values are ~ the min and max y values of the region.
			// initialize to middle of the search region
//			int startIndex = findStartEndIndex(numData, plotBoundaries, START_INDEX);
//			int endIndex = findStartEndIndex(numData, plotBoundaries, END_INDEX);

			double[] plotBoundaries = new double[4];
			Rectangle rectangle = polygon.getBounds();
			double minX = rectangle.getMinX();
			double maxX = rectangle.getMaxX();
			double minY = rectangle.getMinY();
			double maxY = rectangle.getMaxY();
			plotBoundaries[CsdpFunctions.x1Index] = minX;
			plotBoundaries[CsdpFunctions.x2Index] = maxX;
			plotBoundaries[CsdpFunctions.y1Index] = minY;
			plotBoundaries[CsdpFunctions.y2Index] = maxY;
			
//			int startIndex = findStartEndIndex(numData, plotBoundaries, START_INDEX);
//			int endIndex = findStartEndIndex(numData, plotBoundaries, END_INDEX);
			
			// System.out.println("found start and end
			// index:"+startIndex+","+endIndex);

			// write data to temporary file, count numvalues, then write
			// metadata with numvalues,
			// and copy data from temporary file
			FileOutputStream tempFOS = new FileOutputStream(_directory + "tempbath." + BINARY_TYPE);
			DataOutputStream tempDOS = new DataOutputStream(tempFOS);
//			for (int dataNum = startIndex; dataNum < endIndex; dataNum++) {
			for(int dataNum=0; dataNum<_data.getNumLines(); dataNum++) {
				// for (int dataNum=0; dataNum<=_data.getNumLines()-1;
				// dataNum++) {
				_data.getPointMetersFeet(dataNum, _point);
				boolean writePoint = true;
//				if (_point[CsdpFunctions.xIndex] > xMinZoom && _point[CsdpFunctions.xIndex] < xMaxZoom) {
				if(polygon.contains(_point[CsdpFunctions.xIndex], _point[CsdpFunctions.yIndex])) {
					if(saveInside) {
						writePoint = true;
					}else {
						writePoint = false;
					}
				}if(!polygon.contains(_point[CsdpFunctions.xIndex], _point[CsdpFunctions.yIndex])) {
					if(saveInside) {
						writePoint = false;
					}else {
						writePoint = true;
					}
				}
				if(writePoint) {
					// don't need to compare y
					// _point[CsdpFunctions.yIndex] > yMinZoom &&
					// _point[CsdpFunctions.yIndex] < yMaxZoom){
					tempDOS.writeDouble(_point[CsdpFunctions.xIndex]);
					tempDOS.writeDouble(_point[CsdpFunctions.yIndex]);
					tempDOS.writeDouble(_point[CsdpFunctions.zIndex]);
					tempDOS.writeShort(_data.getYear(_data.getYearIndex(dataNum)));
					tempDOS.writeUTF(_data.getSource(_data.getSourceIndex(dataNum)));
					numWritten++;
				}
				// don't write description since it's not used--too much memory
			} // for

			tempDOS.close();
			FileInputStream fis = new FileInputStream(_directory + "tempbath." + BINARY_TYPE);
			BufferedInputStream bis = new BufferedInputStream(fis, 2048 * 32);
			DataInputStream dis = new DataInputStream(bis);

			writeMetadata(numWritten);

			for (int i = 0; i < numWritten; i++) {
				double x = dis.readDouble();
				double y = dis.readDouble();
				double z = dis.readDouble();
				short year = dis.readShort();
				String source = dis.readUTF();
				_binaryOut.writeDouble(x);
				_binaryOut.writeDouble(y);
				_binaryOut.writeDouble(z);
				_binaryOut.writeShort(year);
				_binaryOut.writeUTF(source);
			} // for
			success = true;
		} catch (IOException e) {
			System.out.println("Error ocurred while writing to file " + _directory + _filename + "." + BINARY_TYPE
					+ e.getMessage());
		} // catch
		if (numWritten != numData)
			System.out.println("error in BathymetryAsciiOutput.writeData: numWritten!=numData.  numWritten, numData="
					+ numWritten + "," + numData);
		return success;
	}// writeBathymetry

	/**
	 * Write binary bathymetry data file
	 */
	protected boolean writeBathymetry() {
		boolean success = false;
		CsdpFileMetadata m = CsdpFunctions.getBathymetryMetadata();
		try {
			// write data
			// _binaryOut.writeInt(_data.getNumLines());
			for (int dataNum = 0; dataNum <= _data.getNumLines() - 1; dataNum++) {
				_data.getPointMetersFeet(dataNum, _point);
				_binaryOut.writeDouble(_point[CsdpFunctions.xIndex]);
				_binaryOut.writeDouble(_point[CsdpFunctions.yIndex]);
				_binaryOut.writeDouble(_point[CsdpFunctions.zIndex]);
				_binaryOut.writeShort(_data.getYear(_data.getYearIndex(dataNum)));
				_binaryOut.writeUTF(_data.getSource(_data.getSourceIndex(dataNum)));
				// don't write description since it's not used--too much memory
			}
			success = true;
		} catch (IOException e) {
			System.out.println("Error ocurred while writing to file " + _directory + _filename + "." + BINARY_TYPE
					+ e.getMessage());
		} // catch
		return success;
	}// write

	/*
	 * not really needed...only for ascii.
	 */
	protected boolean writeExtractedXsectData(XsectBathymetryData xsectBathymetryData, String centerlineName, int xsectNum, double thickness) {
		boolean success = false;
		return success;
	}

	protected void openExtractFile() {
	}

	/**
	 * Close binary bathymetry data file
	 */
	protected void close() {
		if (DEBUG)
			System.out.println("closing binary bathymetry file");
		try {
			_binaryOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _directory + _filename + "." + _filetype + ":"
					+ e.getMessage());
		} // catch
	}// close


} // class BathymetryBinaryOutput
