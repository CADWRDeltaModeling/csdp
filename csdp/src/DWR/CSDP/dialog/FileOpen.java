
package DWR.CSDP.dialog;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

/**
 * This is the superclass of all classes that implement a file-open feature that
 * checks to see if a similar filetype is already open.
 * 
 * currently unused
 *
 * @author
 * @version $Id:
 */
public abstract class FileOpen extends FileIO {

	public FileOpen(JFrame gui, String dialogMessage, String errorMessage, String successMessage, String failureMessage,
			boolean reportSuccess, String[] extensions, int numExtensions) {
		super(gui, dialogMessage, errorMessage, successMessage, failureMessage, reportSuccess, extensions,
				numExtensions);
	}

	public void actionPerformed(ActionEvent e) {
		String filename = null;
		boolean success = false;
		warnUserIfNecessary();
		// the filename filter doesn't work in 1.1.4. maybe in 1.2?
		// fd.setFilenameFilter(((FilenameFilter)(new
		// NetworkFilenameFilter())));

		while (filename == null && _cancel == false) {
			String fname = getFilename();
			if (fname == null) {
				_cancel = true;
				// }
				// else if(accept(fname) == false){
				// fname = null;
				// _okd.show();
			}
			filename = fname;
		} // while
		if (filename != null && _cancel != false) {
			success = accessFile();
		}
		_cancel = false;

	}

	public abstract void warnUserIfNecessary();

}// class FileOpen
