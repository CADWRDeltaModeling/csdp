
package DWR.CSDP.dialog;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Allows selection of more than one file.
 * @author btom
 *
 */
public abstract class MultipleFileIO extends FileIO{

	public MultipleFileIO(JFrame gui, String dialogMessage, String errorMessage, String successMessage, String failureMessage,
			boolean reportSuccess, String[] fileExtensions, int numExtensions) {
		super(gui, dialogMessage, errorMessage, successMessage, failureMessage, reportSuccess, fileExtensions, numExtensions);
		_jfc.setMultiSelectionEnabled(true);
	}

	/**
	 * override superclass implementation to work with multiple files
	 * call methods to open, read, and store data files, and to plot/display
	 * data
	 */
	public void actionPerformed(ActionEvent e) {
		_cancel = false;
		boolean success = false;

		String[] filenames = getFilenames();
		for(int i=0; i<filenames.length; i++) {
			String filename = null;
			while (filename == null && _cancel == false) {
				String fname = filenames[i];
				if (_cancel == false) {
					if (fname == null || fname.length() == 0) {
						// not necessary--use JFileChooser.CANCEL_OPTION in subclass
						// _cancel = true;
						JOptionPane.showMessageDialog(_gui, "No file selected!", "Error", JOptionPane.ERROR_MESSAGE);
	//					_errorDialog.setMessage("no file selected!");
	//					_errorDialog.setVisible(true);
					} else {
						if (accept(fname) == false) {
							fname = null;
							JOptionPane.showMessageDialog(_gui, _errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	//						_errorDialog.setMessage(_errorMessage);
	//						_errorDialog.setVisible(true);
						}
					} // else
				} // not cancelling
				filename = fname;
			} // while
	
			if (filename != null && _cancel == false) {
				success = accessFile(_filenames[i], _filetypes[i]);
				if (success == false)
					JOptionPane.showMessageDialog(_gui, _failureMessage, "Failed!", JOptionPane.ERROR_MESSAGE);
	//				_failureDialog.setVisible(true);
			} // if
			_cancel = false;
			if (_reportSuccess && success == true) {
				JOptionPane.showMessageDialog(_gui, _successMessage, "Done", JOptionPane.INFORMATION_MESSAGE);
	//			_successDialog.setVisible(true);
			}
		}
		// }//while
	}// actionPerformed
	
	/**
	 * separates filename into prefix and extension
	 */
	protected void parseFilename(int index, String filename) throws NullPointerException {
		try {
			// int dotIndex = filename.indexOf(".",0);

			// if it's .cdp.gz, then use that as the filetype. otherwise,
			// everything after the last dot...

			System.out.println("MultipleFileIO.parseFilename: filename=" + filename);

			boolean matchFound = false;
			for (int i = 0; i <= _numExtensions - 1; i++) {
				if (filename.endsWith(_fileExtensions[i])) {
					matchFound = true;
					_filetypes[index] = _fileExtensions[i];
					_filenames[index] = filename.substring(0, filename.lastIndexOf(_filetypes[index]) - 1);
				}
			} // for
			if (!matchFound) {
				_filenames[index] = null;
				_filetypes[index] = null;
			}

			System.out.println("matchFound, _filenames[index], _filetype[index]=" + matchFound + "," + 
					_filenames[index] + "," + _filetypes[index]);

			// if(dotIndex >= 0){
			// _filename = filename.substring(0,dotIndex);
			// _filetype = filename.substring(dotIndex+1);
			// }
			// else if(dotIndex < 0){
			// _filename = null;
			// _filetype = null;
			// }
		} catch (Exception e) {
			System.out.println("no filename specified");
		} // catch
	}// parseFilename
	
	/*
	 * override abstract method in superclass, which is not needed for this implementation
	 * @see DWR.CSDP.dialog.FileIO#getFilename()
	 */
	protected String getFilename() {return null; }
	/*
	 * override abstract method in superclass, which is not needed for this implementation
	 * @see DWR.CSDP.dialog.FileIO#accessFile()
	 */
	public boolean accessFile() {return false;}
	public abstract boolean accessFile(String filename, String filetype);
	protected abstract String[] getFilenames();

	
	protected String[] _filenames;
	protected String[] _filetypes;
	

}// class MultipleFileIO

