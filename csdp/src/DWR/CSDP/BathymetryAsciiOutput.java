package DWR.CSDP;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import DWR.CSDP.XsectBathymetryData;

public class BathymetryAsciiOutput extends BathymetryOutput {
	FileWriter _aOutFile = null;
	BufferedWriter _asciiOut = null;

	// for xs export
	double[] _utmX;
	double[] _utmY;
	double[] _elev;
	short[] _year;
	String[] _source;
	double[] _station;

	/**
	 * assigns data storage object to class variable
	 */
	BathymetryAsciiOutput(BathymetryData data) {
		_data = data;
		_convertToDatum = false;
	}

	/**
	 * assigns data storage object to class variable and asks for saving in
	 * datum as indicated by string value
	 */
	BathymetryAsciiOutput(BathymetryData data, boolean convertToDatum) {
		_data = data;
		_convertToDatum = convertToDatum;
	}

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			_aOutFile = new FileWriter(_directory + _filename + "." + ASCII_TYPE);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Open ascii file for appending
	 */
	protected void openExtractFile() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			// append!
			_aOutFile = new FileWriter(_directory + _filename + "." + ASCII_TYPE, true);
			_asciiOut = new BufferedWriter(_aOutFile);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * quicksort for extracted xsect data. left is the index of the first
	 * element in the array, right is the index of the last
	 */
	public void qsortExtractedData(int left, int right) {
		int last = 0;
		double ran = 0.0;
		if (left < right) {
			ran = Math.random();
			swap(_station, left, left + (int) ((right - left + 1) * ran));
			swap(_utmX, left, left + (int) ((right - left + 1) * ran));
			swap(_utmY, left, left + (int) ((right - left + 1) * ran));
			swap(_elev, left, left + (int) ((right - left + 1) * ran));
			swap(_year, left, left + (int) ((right - left + 1) * ran));
			swap(_source, left, left + (int) ((right - left + 1) * ran));
			last = left;
			for (int i = left + 1; i <= right; i++) {
				if (_station[i] < _station[left]) {
					last++;
					swap(_station, last, i);
					swap(_utmX, last, i);
					swap(_utmY, last, i);
					swap(_elev, last, i);
					swap(_year, last, i);
					swap(_source, last, i);
				} // if
			} // for i
			swap(_station, left, last);
			swap(_utmX, left, last);
			swap(_utmY, left, last);
			swap(_elev, left, last);
			swap(_year, left, last);
			swap(_source, left, last);
			qsortExtractedData(left, last - 1);
			qsortExtractedData(last + 1, right);
		} // if
	}// qsort

	/**
	 * swap two double values in array. used by quicksort
	 */
	protected void swap(double[] array, int i, int j) {
		double t = array[i];
		array[i] = array[j];
		array[j] = t;
	}

	/**
	 * swap two double values in array. used by quicksort
	 */
	protected void swap(short[] array, int i, int j) {
		short t = array[i];
		array[i] = array[j];
		array[j] = t;
	}

	/**
	 * swap two double values in array. used by quicksort
	 */
	protected void swap(String[] array, int i, int j) {
		String t = array[i];
		array[i] = array[j];
		array[j] = t;
	}

	/**
	 * called by superclass
	 */
	protected boolean writeExtractedXsectData(XsectBathymetryData xsectBathymetryData, String centerlineName, int xsectNum, double thickness) {
		double[] stationElevation = new double[3];
		String line = null;
		short year = -Short.MAX_VALUE;
		String source = null;
		boolean success = false;
		int numValues = xsectBathymetryData.getNumEnclosedValues();
		int pointIndex = -Integer.MAX_VALUE;
		// arrays for sorted data.
		_utmX = new double[numValues];
		_utmY = new double[numValues];
		_elev = new double[numValues];
		_year = new short[numValues];
		_source = new String[numValues];
		_station = new double[numValues];

		try {
			line = "=============" + centerlineName + "_" + xsectNum + " thickness=" + thickness + "=============";
			_asciiOut.write(line);
			_asciiOut.newLine();
			for (int i = 0; i <= numValues - 1; i++) {
				pointIndex = xsectBathymetryData.getEnclosedPointIndex(i);
				_data.getPointMetersFeet(pointIndex, _point);
				xsectBathymetryData.getEnclosedStationElevation(i, stationElevation);
				year = _data.getYear(_data.getYearIndex(pointIndex));
				source = _data.getSource(_data.getSourceIndex(pointIndex));

				_utmX[i] = _point[CsdpFunctions.xIndex];
				_utmY[i] = _point[CsdpFunctions.yIndex];
				_elev[i] = _point[CsdpFunctions.zIndex];
				_year[i] = year;
				_source[i] = source;
				_station[i] = stationElevation[CsdpFunctions.xIndex];
			}
			qsortExtractedData(0, numValues - 1);

			for (int i = 0; i <= numValues - 1; i++) {
				line = _utmX[i] + " " + _utmY[i] + " " + _elev[i] + " " + _year[i] + " " + _source[i] + " "
						+ _station[i] + " " + _elev[i] + " " + _year[i] + " " + _source[i];
				_asciiOut.write(line);
				_asciiOut.newLine();
			}
			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} // catch()
		return success;
	}

	/*
	 * write zoomed data (data currently visible on screen)
	 */
	protected boolean write(double[] plotBoundaries) {
		int numData = countZoomedData(plotBoundaries);
		// metadata will be written later
		// boolean success1=writeMetadata(numData);
		boolean success = writeBathymetry(numData, plotBoundaries);
		return success;
	}

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
	 * write metadata
	 */
	protected boolean writeMetadata(int numData) {
		String line = null;
		boolean success = false;
		CsdpFileMetadata m = CsdpFunctions.getBathymetryMetadata();

		try {
			// write metadata
			_asciiOut.write(";HorizontalDatum:  " + m.getHDatumString());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalZone:   " + m.getHZone());
			_asciiOut.newLine();
			_asciiOut.write(";HorizontalUnits:  " + m.getHUnitsString());
			_asciiOut.newLine();
			if (_convertToDatum) {
				_asciiOut.write(";VerticalDatum:    " + "NAVD88");
			} else {
				_asciiOut.write(";VerticalDatum:    " + m.getVDatumString());
			}
			_asciiOut.newLine();
			_asciiOut.write(";VerticalUnits:    " + m.getVUnitsString());
			_asciiOut.newLine();
			_asciiOut.write(";Filetype:          bathymetry");
			_asciiOut.newLine();
			_asciiOut.write(";NumElements:      " + numData);
			_asciiOut.newLine();
			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
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
	 * write ascii bathymetry data
	 * @param saveInside 
	 */
	protected boolean writeBathymetry(int numData, Polygon polygon, boolean saveInside) {
		String line = null;
		short year;
		String source = null;
		boolean success = false;
		int numWritten = 0;
		try {
//			double xMinZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.minXIndex]);
//			double xMaxZoom = CsdpFunctions.feetToMeters(plotBoundaries[CsdpFunctions.maxXIndex]);

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

			// write data to temporary file
			FileWriter tempFW = new FileWriter(_directory + "tempbath" + "." + ASCII_TYPE);
			BufferedWriter tempBW = new BufferedWriter(tempFW);

			boolean convertToNAVD88 = _convertToDatum
					&& (CsdpFunctions.getBathymetryMetadata().getVDatum() != CsdpFileMetadata.NAVD1988);
			// units: 1=Survey feet, 2=international feet, 3=meters
			// test utm83ToUtm27
			final short utm83_units = 3;
			final short utm83_zone = 10;
			final short utm27_zone = 10;
			final short utm27_units = 3;
			final short elevUnitsIn = 1;
			final short unitsOut = 1;
			// write data
//			for (int dataNum = startIndex; dataNum < endIndex; dataNum++) {
			for(int dataNum=0; dataNum<_data.getNumLines(); dataNum++) {
				
				// for(int i=0; i<=_data.getNumLines()-1; i++){
//				System.out.println("writing point");
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
					if (convertToNAVD88) {
						_point[CsdpFunctions.zIndex] = CsdpFunctions.getUseSemmscon().ngvd29_to_navd88_utm83(
								_point[CsdpFunctions.xIndex], _point[CsdpFunctions.yIndex], utm83_zone, utm83_units,
								_point[CsdpFunctions.zIndex], elevUnitsIn, unitsOut);
					}
					year = _data.getYear(_data.getYearIndex(dataNum));
					source = _data.getSource(_data.getSourceIndex(dataNum));
					line = _point[CsdpFunctions.xIndex] + " " + _point[CsdpFunctions.yIndex] + " "
							+ _point[CsdpFunctions.zIndex] + " " + year + " " + source;
					tempBW.write(line);
					tempBW.newLine();
					// _asciiOut.write(line);
					// _asciiOut.newLine();
					numWritten++;
				} // if
				else {
//					System.out.println("not writing point");
				}
			} // for

			tempBW.close();
			FileReader tempFR = new FileReader(_directory + "tempbath" + "." + ASCII_TYPE);
			BufferedReader tempBR = new BufferedReader(tempFR);
			writeMetadata(numWritten);

			for (int dataNum = 0; dataNum < numWritten; dataNum++) {
				line = tempBR.readLine();
				// System.out.println("about to write line:"+line);
				_asciiOut.write(line);
				_asciiOut.newLine();
			}

			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
			// if(numWritten!=numData) System.out.println("error in
			// BathymetryAsciiOutput.writeData: numWritten!=numData. numWritten,
			// numData="+numWritten+","+numData);
		return success;
	}// writeBathymetry

	/**
	 * write ascii bathymetry data
	 */
	protected boolean writeBathymetry() {
		String line = null;
		short year;
		String source = null;
		boolean success = false;
		boolean convertToNAVD88 = _convertToDatum
				&& (CsdpFunctions.getBathymetryMetadata().getVDatum() != CsdpFileMetadata.NAVD1988);
		// units: 1=Survey feet, 2=international feet, 3=meters
		// test utm83ToUtm27
		final short utm83_units = 3;
		final short utm83_zone = 10;
		final short utm27_zone = 10;
		final short utm27_units = 3;
		final short elevUnitsIn = 1;
		final short unitsOut = 1;
		try {
			// write data
			for (int i = 0; i <= _data.getNumLines() - 1; i++) {
				_data.getPointMetersFeet(i, _point);
				if (convertToNAVD88) {
					_point[CsdpFunctions.zIndex] = CsdpFunctions.getUseSemmscon().ngvd29_to_navd88_utm83(
							_point[CsdpFunctions.xIndex], _point[CsdpFunctions.yIndex], utm83_zone, utm83_units,
							_point[CsdpFunctions.zIndex], elevUnitsIn, unitsOut);
				}
				year = _data.getYear(_data.getYearIndex(i));
				source = _data.getSource(_data.getSourceIndex(i));
				line = _point[CsdpFunctions.xIndex] + " " + _point[CsdpFunctions.yIndex] + " "
						+ _point[CsdpFunctions.zIndex] + " " + year + " " + source;
				_asciiOut.write(line);
				_asciiOut.newLine();
			}
			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
		return success;
	}// writeData

	/**
	 * Close ascii bathymetry data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _directory + _filename + "." + _filetype + ":"
					+ e.getMessage());
		} // catch
	}// close



} // class BathymetryAsciiInput
