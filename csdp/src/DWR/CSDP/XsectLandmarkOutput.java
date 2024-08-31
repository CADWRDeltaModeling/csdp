package DWR.CSDP;

/**
 * Write landmark file .cdl
 *
 * @author
 * @version
 */
public abstract class XsectLandmarkOutput {

	public static final boolean DEBUG = false;
	protected static final String FILENAME = "xsects.cdl";

	/**
	 * Make instance of subclass of XsectLandmarkOutput
	 */
	public static XsectLandmarkOutput getInstance(Network net, String directory) {
		XsectLandmarkOutput output = null;
		output = new XsectLandmarkAsciiOutput(net, directory);
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write XsectLandmark data
	 */
	public boolean writeData() {
		boolean success = false;
		boolean backupSuccess = CsdpFunctions.backupFile(FILENAME);
		if (backupSuccess) {
			open();
			write();
			close();
		}
		return success;
	}// writeData

	/**
	 * Open file
	 */
	protected abstract void open();

	/**
	 * write file
	 */
	protected abstract void write();

	/**
	 * Close file
	 */
	protected abstract void close();

}// class XsectLandmarkOutput
