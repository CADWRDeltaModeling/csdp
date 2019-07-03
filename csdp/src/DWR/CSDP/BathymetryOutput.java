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
import java.io.File;

import DWR.CSDP.XsectBathymetryData;

/**
 * Write ascii and binary bathymetry data
 *
 * @author
 * @version $Id: BathymetryOutput.java,v 1.2 2003/07/22 22:20:58 btom Exp $
 */
public abstract class BathymetryOutput {

	public static final boolean DEBUG = false;

	/**
	 * Make instance of subclass of BathymetryOutput
	 */
	public static BathymetryOutput getInstance(String directory, String filename, String filetype,
			BathymetryData data) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		BathymetryOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			output = new BathymetryAsciiOutput(data);
		} else if (_filetype.equals(BINARY_TYPE)) {
			output = new BathymetryBinaryOutput(data);
		} else {// throw new IllegalInputFileException(msg);
			System.out.println();
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Make instance of subclass of BathymetryOutput
	 */
	public static BathymetryOutput getInstanceForNAVD88(String directory, String filename, String filetype,
			BathymetryData data) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		BathymetryOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			output = new BathymetryAsciiOutput(data, true);
		} else {// throw new IllegalInputFileException(msg);
			System.out.println();
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write bathymetry data
	 */
	public boolean writeData() {
		open();
		boolean success = write();
		close();
		return success;
	}

	/**
	 * Calls appropriate write method to write bathymetry data
	 */
	public boolean writeData(double[] plotBoundaries) {
		open();
		boolean success = write(plotBoundaries);
		close();
		return success;
	}

	public boolean extractXsectData(XsectBathymetryData xsectBathymetryData, String centerlineName, int xsectNum, double thickness) {
		openExtractFile();
		boolean success = writeExtractedXsectData(xsectBathymetryData, centerlineName, xsectNum, thickness);
		close();
		return success;
	}

	protected abstract void openExtractFile();

	protected abstract boolean writeExtractedXsectData(XsectBathymetryData xsectBathymetryData, String centerlineName, 
			int xsectNum, double thickness);

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write zoomed data
	 */
	protected abstract boolean write(double[] plotBoundaries);

	/**
	 * write all data
	 */
	protected abstract boolean write();
	
	protected Polygon getPolygon(Centerline centerline) {
		Polygon polygon = new Polygon();
		for(int i=0; i<centerline.getNumCenterlinePoints(); i++) {
			CenterlinePoint centerlinePoint = centerline.getCenterlinePoint(i);
			polygon.addPoint((int)CsdpFunctions.feetToMeters(centerlinePoint._x), (int)CsdpFunctions.feetToMeters(centerlinePoint._y));
		}
		return polygon;
	}

	protected int countDataInsideOrOutsidePolygon(Centerline centerline, boolean saveInside) {
		Polygon polygon = getPolygon(centerline);
		int numData = 0;
		for(int i=0; i<_data.getNumLines(); i++) {
			_data.getPointFeet(i, _point);
			double x = _point[CsdpFunctions.xIndex];
			double y = _point[CsdpFunctions.yIndex];
			if(saveInside) {
				if(polygon.contains(x, y)) {
					numData++;
				}
			}else {
				if(!polygon.contains(x, y)) {
					numData++;
					
				}
			}
		}
		return numData;
	}//countDataInsideOrOutsidePolygon
	
	/**
	 * count number of points to be written (if not writing entire file)
	 */
	protected int countZoomedData(double[] plotBoundaries) {
		String line = null;
		short year;
		String source = null;
		boolean success = false;

		double xMin = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.minXIndex]);
		double xMax = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.maxXIndex]);
		double yMin = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.minYIndex]);
		double yMax = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.maxYIndex]);

		int numData = 0;

		for (int i = 0; i <= _data.getNumLines() - 1; i++) {
			_data.getPointMetersFeet(i, _point);
			if (_point[CsdpFunctions.xIndex] > xMin && _point[CsdpFunctions.xIndex] < xMax
					&& _point[CsdpFunctions.yIndex] > yMin && _point[CsdpFunctions.yIndex] < yMax) {
				numData++;
			}
		}
		return numData;
	}// countZoomedData

//	/**
//	 * No longer needed. Gets stuck in infinite loop sometimes.
//	 */
//	protected int findStartEndIndex(int numData, double[] plotBoundaries, int startOrEnd) {
//		int numWritten = 0;
//		double yMinZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.minYIndex]);
//		double yMaxZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.maxYIndex]);
//
//		// data are sorted in ascending y order.
//		// find indexes of first and last y values that are greater than and
//		// less
//		// than the min and max y values of the plotBoundaries
//		int numLines = _data.getNumLines();
//		// The number of lines(points) that are before
//		// and afer the middle of the search region
//		int halfSize = numLines / 2;
//		// The index of the beginning or last bathymetry point whose
//		// y values are ~ the min and max y values of the region.
//		// initialize to middle of the search region
//		int foundIndex = numLines / 2;
//		double valueToFind = -Double.MAX_VALUE;
//		if (startOrEnd == START_INDEX)
//			valueToFind = yMinZoom;
//		else if (startOrEnd == END_INDEX)
//			valueToFind = yMaxZoom;
//
//		// find the foundIndex
//		double yPoint = -Double.MAX_VALUE;
//		double yAfterPoint = -Double.MAX_VALUE;
//		double yBeforePoint = -Double.MAX_VALUE;
//		while (halfSize > 100 || (!(valueToFind > yBeforePoint && valueToFind < yAfterPoint))) {
//			boolean done = false;
//			_data.getPointMetersFeet(foundIndex, _point);
//			yPoint = _point[CsdpFunctions.yIndex];
//			_data.getPointMetersFeet(foundIndex - 1, _point);
//			yBeforePoint = _point[CsdpFunctions.yIndex];
//			_data.getPointMetersFeet(foundIndex + 1, _point);
//			yAfterPoint = _point[CsdpFunctions.yIndex];
//
//			if (DEBUG)
//				System.out.println("valueToFind,ybefore,y,yafter,foundIndex=" + valueToFind + "," + yBeforePoint + ","
//						+ yPoint + "," + yAfterPoint + "," + foundIndex);
//
//			if (valueToFind < yBeforePoint) {
//				// change middle index to middle of second half of region
//				halfSize /= 2;
//				int newStartIndex = foundIndex - halfSize;
//				if (DEBUG)
//					System.out.println("changing foundIndex from " + foundIndex + " to " + newStartIndex);
//				foundIndex = newStartIndex;
//			} else if (valueToFind > yAfterPoint) {
//				// change middle index to middle of first half of region
//				halfSize /= 2;
//				int newStartIndex = foundIndex + halfSize;
//				if (DEBUG)
//					System.out.println("changing foundIndex from " + foundIndex + " to " + newStartIndex);
//				foundIndex = newStartIndex;
//			} else if (valueToFind == yBeforePoint || valueToFind == yAfterPoint) {
//				while (valueToFind == yBeforePoint && foundIndex > 0) {
//					foundIndex--;
//					_data.getPointMetersFeet(foundIndex, _point);
//					yPoint = _point[CsdpFunctions.yIndex];
//					_data.getPointMetersFeet(foundIndex - 1, _point);
//					yBeforePoint = _point[CsdpFunctions.yIndex];
//					_data.getPointMetersFeet(foundIndex + 1, _point);
//					yAfterPoint = _point[CsdpFunctions.yIndex];
//					if (DEBUG)
//						System.out.println("valueToFind,ybefore,y,yafter,foundIndex=" + valueToFind + "," + yBeforePoint
//								+ "," + yPoint + "," + yAfterPoint + "," + foundIndex);
//				}
//				done = true;
//			} else if (valueToFind > yBeforePoint && valueToFind < yAfterPoint) {
//				// we're done!
//				done = true;
//			} else {
//				System.out.println("ERROR in BathymetryOutput.writeBathymetry");
//			}
//			if (done)
//				break;
//		} // while
//		return foundIndex;
//	}// writeBathymetry

	/*
	 * write metadata
	 */
	protected abstract boolean writeMetadata(int numValues);

	/*
	 * write bath
	 */
	protected abstract boolean writeBathymetry(int numValues, double[] plotBoundaries);

	/*
	 * write bath
	 */
	protected abstract boolean writeBathymetry();

	/**
	 * Close file
	 */
	protected abstract void close();

	protected static final String ASCII_TYPE = "prn";
	protected static final String BINARY_TYPE = "cdp";
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static String _directory = null;
	protected static double[] _point = new double[3];
	protected BathymetryData _data = null;
	protected boolean _convertToDatum = false;

	protected static final int START_INDEX = 0;
	protected static final int END_INDEX = 1;

	protected boolean writeData(Centerline centerline, boolean saveInside) {
		open();
		boolean success = write(centerline, saveInside);
		close();
		return success;
	}

	protected abstract boolean write(Centerline centerline, boolean saveInside);
} // BathymetryOutput
