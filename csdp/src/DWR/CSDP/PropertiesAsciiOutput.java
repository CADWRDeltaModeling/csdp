package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PropertiesAsciiOutput extends PropertiesOutput {
	FileWriter _aOutFile = null;
	BufferedWriter _asciiOut = null;

	/**
	 * Open ascii file for writing
	 */
	protected void open() {
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			File f = new File(_directory + _filename + "." + ASCII_TYPE);
			if (f.exists()) {

			}
			_aOutFile = new FileWriter(f);
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
	 * write ascii properties data file
	 */
	protected boolean write() {
		boolean success = false;
		float[] point;
		String line = null;
		short year;
		String source = null;

		try {
			String nl = null;
			for (int i = 0; i <= _gui.getNumColors() - 1; i++) {
				line = _gui.getColor(i).getRed() + " " + _gui.getColor(i).getGreen() + " " + _gui.getColor(i).getBlue();
				_asciiOut.write(line);
				_asciiOut.newLine();
			}
			_asciiOut.newLine();
			_asciiOut.write("minelevbin=" + _gui.getMinElevBin());
			_asciiOut.newLine();
			_asciiOut.write("maxelevbin=" + _gui.getMaxElevBin());
			_asciiOut.newLine();
			_asciiOut.write("numelevbins=" + _gui.getNumElevBins());
			_asciiOut.newLine();

			success = true;
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while writing file " + _directory + _filename + "." + ASCII_TYPE + e.getMessage());
		} finally {
		} // catch()
		return success;
	}

	/**
	 * Close ascii properties data file
	 */
	protected void close() {
		try {
			_asciiOut.close();
		} catch (IOException e) {
			System.out.println("Error ocurred while closing file " + _directory + _filename + "." + _filetype + ":"
					+ e.getMessage());
		} // catch
	}// close

} // class PropertiesAsciiInput
