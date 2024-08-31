package DWR.CSDP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * writes network comparison data to ascii file
 */
public class NetworkCompareAsciiOutput extends NetworkCompareOutput {
	FileWriter _aOutFile = null; // ascii input file
	BufferedWriter _asciiOut = null;
	String _nFilename1 = null;
	String _nFilename2 = null;
	Network _net1 = null;
	Network _net2 = null;
	CsdpFrame _gui;
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };
	private StationTimeSeriesData _stationData = null;

	/**
	 * assigns data storage object to class variable
	 */
	NetworkCompareAsciiOutput(String nFilename1, Network net1, String nFilename2, Network net2, CsdpFrame gui) {
		_nFilename1 = nFilename1;
		_nFilename2 = nFilename2;
		_net1 = net1;
		_net1 = net1;
		_gui = gui;
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
				System.out.println("Directory, Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Filetype: " + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * write ascii network comparison data file
	 *
	 * Compare two network files: list of any channel names unique to different
	 * number of xs area at 0 elevation different numbers of points different
	 * metadata
	 */
	protected boolean write() {
		boolean success = false;

		_net1.sortCenterlineNames();
		_net2.sortCenterlineNames();

		// try{
		// _asciiOut.write("---------------------------------------------------------\n\n");
		// _asciiOut.write("Network file comparison"+"\n");
		// _asciiOut.write("Network1 = "+_nFilename1 +", Network2 =
		// "+_nFilename2);
		// _asciiOut.write("---------------------------------------------------------\n\n");
		// if(_net1.getNumCenterlines() != _net2.getNumCenterlines()){
		// _asciiOut.write(_nFilename1+" has "+_net1.getNumCenterlines()+"\n");
		// _asciiOut.write(_nFilename2+" has
		// "+_net2.getNumCenterlines()+"\n"+"\n");
		// }else{
		// _asciiOut.write("Both networks have "+_net1.getNumCenterlines()+"
		// centerlines"+"\n"+"\n");
		// }

		// //print unique channel names
		// _asciiOut.write("----------------------------------------------------------");
		// _asciiOut.write("Unique Channel Names");
		// _asciiOut.write(file1);
		// _asciiOut.write(uniqueNames1[i]);
		// _asciiOut.write("----------------------------------------------------------");
		// _asciiOut.write(file2);
		// _asciiOut.write(uniqueNames2[i]);
		// _asciiOut.write("----------------------------------------------------------");

		// Enumeration f1Chan = net1.getAllCenterlines();

		// //print channels with different number of xs
		// _asciiOut.write("----------------------------------------------------------");
		// _asciiOut.write("Matching channels with different # of xs");
		// _asciiOut.write("chan file1 file2");

		// _asciiOut.write("----------------------------------------------------------");

		// //print xs with areas that differ by >=10%

		// //print xs with different numbers of points

		// //print xs with different metadata

		// }catch(IOException e){
		// _asciiOut.write("error occurred while writing file "+_directory+
		// _filename+"."+ASCII_TYPE+e.getMessage());
		// }
		// make list of centerlines, compare.
		// CHANGE THIS LATER.
		success = true;
		return success;
	}// write

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

}// class NetworkCompareAsciiOutput
