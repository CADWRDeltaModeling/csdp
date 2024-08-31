package DWR.CSDP;

import java.io.File;
import java.util.HashSet;

/**
 * Write ascii and binary network data
 *
 * @author
 * @version $Id: NetworkOutput.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp $
 */
public abstract class NetworkOutput {

	public static final boolean DEBUG = false;

	/**
	 * Make instance of subclass of NetworkOutput
	 * channelsToExport can be null; this would mean export every channel.
	 */
	public static NetworkOutput getInstance(String directory, String filename, String filetype, Network data, HashSet<String> channelsToExport) {
		_directory = directory;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		_filename = filename;
		_filetype = filetype;
		_channelsToExport = channelsToExport;
		NetworkOutput output = null;
		if (_filetype.equals(ASCII_TYPE)) {
			output = new NetworkAsciiOutput(data);
		} else if (_filetype.equals(EXPORT_TYPE)) {
			output = new NetworkAsciiStationElevationOutput(data);
		} else if (_filetype.equals(EXPORT_TYPE_3D)) {
			output = new NetworkAsciiOutput(data);
		} else {// throw new IllegalInputFileException(msg);
			System.out.println("Error in NetworkOutput:  filetype " + _filetype + " not recognized");
			_filetype = null;
		}
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write network data
	 */
	public boolean writeData() {
		boolean success = false;
		boolean backupSuccess = CsdpFunctions.backupFile(_directory, _filename, _filetype);
		if (backupSuccess) {
			open();
			success = write();
			close();
		}
		return success;
	}

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write file
	 */
	protected abstract boolean write();

	/**
	 * Close file
	 */
	protected abstract void close();

	/**
	 * Convert x and y values from meters(UTM) to feet
	 */
	protected float feetToMeters(float value) {
		final float FEET_TO_METERS = 1 / 3.28084f;
		return FEET_TO_METERS * value;
	}

	public void setChannelLengthsOnly(boolean b) {
		_channelLengthsOnly = b;
	}

	public void set3DOutput(boolean b) {
		_3DOutput = b;
	}

	protected boolean get3DOutput() {
		return _3DOutput;
	}

	protected static final String ASCII_TYPE = "cdn";
	protected static final String EXPORT_TYPE = "se";
	protected static final String EXPORT_TYPE_3D = "3dn";
	/*
	 * If not null, will be an array of String representations of channels 
	 * numbers that are the only numbers that should be exported. If null,
	 * all channels will be exported.
	 */
	protected static HashSet<String> _channelsToExport; 
	
	// protected static final String BINARY_TYPE = "cdp";
	protected static String _filename = null; // part of filename before the
												// first dot
	protected static String _filetype = null; // filename extension (after first
												// dot)
	protected static String _directory = null;
	protected boolean _channelLengthsOnly = false;
	protected boolean _3DOutput = false;
}// class NetworkOutput
