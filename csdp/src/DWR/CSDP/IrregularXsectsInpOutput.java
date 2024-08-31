package DWR.CSDP;

import java.io.File;

/**
 * Write DSM2 input file "irregular_xsects.inp"
 *
 * @author
 * @version
 */
public abstract class IrregularXsectsInpOutput {

	public static final boolean DEBUG = false;
	protected static final String FILENAME = "irregular_xsects.inp";
	protected static final String FIRST_LINE = "IRREG_GEOM";
	protected static final String SECOND_LINE = "CHAN     DIST       FILENAME";
	protected static final String LAST_LINE = "END";
	protected static String _directory;

	/**
	 * Make instance of subclass of IrregularXsectsInpOutput
	 */
	public static IrregularXsectsInpOutput getInstance(String dir, Network net) {
		_directory = dir;
		if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
			_directory += File.separator;
		}
		IrregularXsectsInpOutput output = null;
		output = new IrregularXsectsInpAsciiOutput(net);
		return output;
	} // getInstance

	/**
	 * Calls appropriate write method to write irregularXsectsInp data
	 */
	public void writeData() {
		boolean success = false;
		boolean backupSuccess = CsdpFunctions.backupFile(_directory, FILENAME);
		if (backupSuccess) {
			open();
			write();
			close();
		}
	}

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

}// class IrregularXsectsInpOutput
