package DWR.CSDP;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

/**
 * Read binary bathymetry data in gzip file.
 *
 * @author
 * @version $Id
 */

public class BathymetryBinaryGzipInput extends BathymetryInput {

	GZIPInputStream _gzipIn = null;
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
			_gzipIn = new GZIPInputStream(bis);
			_binaryIn = new DataInputStream(_gzipIn);
		} catch (IOException e) {
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch
	}

	/**
	 * Read binary file.
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
					JOptionPane.showMessageDialog(_gui, "incomplete metadata! there should be " + CsdpFunctions.getNumMetadataLines() + " lines.  "
							+ "The following line was expected to be a metadata line:" + metaLine, 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			} // for: read metadata

			System.out
					.println("BathymetryBinaryInput: done parsing metadata. " + "hdatum, hzone, hunits,vdatum,vunits=");
			System.out.println(bathymetryMetadata.getHDatumString() + "," + bathymetryMetadata.getHZone() + ","
					+ bathymetryMetadata.getHUnitsString() + "," + bathymetryMetadata.getVDatumString() + ","
					+ bathymetryMetadata.getVUnitsString());
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
	}// readHeaders

	protected void read() {
		try {
			for (int lineNum = 0; lineNum <= _numLines - 1; lineNum++) {
				_pd.x = _binaryIn.readFloat();
				_pd.y = _binaryIn.readFloat();
				_pd.z = _binaryIn.readFloat();
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
		System.out.println("closing binary bathymetry file");
		try {
			_binaryIn.close();
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while closing file " + _directory + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}// close

} // class BathymetryBinaryGzipInput
