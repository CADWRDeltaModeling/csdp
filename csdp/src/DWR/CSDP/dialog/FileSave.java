
package DWR.CSDP.dialog;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This is the superclass of all classes that implement a file-save feature,
 * which saves a file to the directory in/from which it was last saved/read.
 *
 * @author
 * @version $Id: FileSave.java,v 1.1 2002/06/12 18:48:38 btom Exp $
 */
public abstract class FileSave extends FileIO {

	public FileSave(JFrame gui, String dialogMessage, String errorMessage, String successMessage, String failureMessage,
			boolean reportSuccess, String[] extensions, int numExtensions) {
		super(gui, dialogMessage, errorMessage, successMessage, failureMessage, reportSuccess, extensions,
				numExtensions);
		_reportSuccess = reportSuccess;
	}// FileSave

	public void actionPerformed(ActionEvent e) {
		boolean success = false;
		String filename = getCurrentFilename();
		String filetype = getCurrentFiletype();
		String newFilename = null;
		if (filename == null && filetype == null) {
			while (filename == null && _cancel == false) {
				newFilename = getFilename();
				// if(accept(newFilename) == false){
				// if(newFilename == null) _cancel = true;
				// newFilename = null;
				// _okd.show();
				// }//if
				filename = newFilename;
			} // while
			if (filename != null) {
				success = accessFile(filename);
				setFilenameAndType(filename, filetype);
			} // if
		} // if no filename
		else if (filename != null) {
			success = accessFile();
			setFilenameAndType(filename, filetype);
		} // else
			// else printErrorMessage();
		if (_reportSuccess) {
			if (success == true) {
				JOptionPane.showMessageDialog(_gui, _successMessage, "Done", JOptionPane.INFORMATION_MESSAGE);
//				_successDialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(_gui, _failureMessage, "Failed!", JOptionPane.ERROR_MESSAGE);
//				_failureDialog.setVisible(true);
			} // if
		} // if

	}// actionPerformed

	public abstract String getCurrentFilename();

	public abstract String getCurrentFiletype();

	public abstract void setFilenameAndType(String filename, String filetype);

	public abstract boolean accessFile(String filename);

	public abstract boolean accessFile();

	protected boolean reportSuccess;
	// public abstract void printErrorMessage();
}// FileSave
