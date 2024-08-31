package DWR.CSDP;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import DWR.CSDP.dialog.FileIO;
import DWR.CSDP.dialog.FileSave;

public class PropertiesMenu {

	public PropertiesMenu(App app) {
		_app = app;
		_pOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_pSaveFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
	}

	/**
	 * Get properties filename and call functions to open, read, and display it.
	 *
	 * @author
	 * @version $Id:
	 */
	public class PLoad extends FileIO implements ActionListener {
		public PLoad(CsdpFrame gui) {
			super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openExtensions, _numOpenExtensions);
			_jfc.setDialogTitle(_openDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_pOpenFilter);
			_jfc.setFileFilter(_pOpenFilter);
		}

		/**
		 * uses dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getPropertiesDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getPropertiesDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getPropertiesDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getOpenDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println("no file selected");
			// _fd.show();

			// filename = _fd.getFile();
			// if(filename != null){
			// CsdpFunctions.setPropertiesDirectory(_fd.getDirectory());
			// CsdpFunctions.setOpenDirectory(_fd.getDirectory());
			// parseFilename(filename);
			// CsdpFunctions.setPropertiesFilename(_filename);
			// CsdpFunctions.setPropertiesFiletype(_filetype);
			// }
			_filechooserState = _jfc.showOpenDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setPropertiesDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
				CsdpFunctions.setPropertiesFilename(_filename);
				CsdpFunctions.setPropertiesFiletype(_filetype);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}// getFilename

		/**
		 * read properties file
		 */
		public boolean accessFile() {
			_app.pReadStore(_gui, CsdpFunctions.getPropertiesDirectory().getPath(), _filename, _filetype);
			((CsdpFrame) _gui).enableAfterProperties();
			return true; // no need to warn if it fails.
		}// accessFile
		
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}


	} // PLoad class

	/**
	 * Save properties in file with specified name
	 *
	 * @author
	 * @version $Id:
	 */
	public class PSaveAs extends FileIO implements ActionListener {
		public PSaveAs(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_pSaveFilter);
			_jfc.setFileFilter(_pSaveFilter);
		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getPropertiesDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getPropertiesDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getPropertiesDirectory());
			}
			// _fd.show();
			// filename = _fd.getFile();
			// CsdpFunctions.setPropertiesDirectory(_fd.getDirectory());
			// parseFilename(filename);
			// CsdpFunctions.setPropertiesFilename(_filename);
			// CsdpFunctions.setPropertiesFiletype(_filetype);
			_filechooserState = _jfc.showSaveDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setPropertiesDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
				CsdpFunctions.setPropertiesFilename(_filename);
				CsdpFunctions.setPropertiesFiletype(_filetype);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean success = _app.pSaveAs(CsdpFunctions.getPropertiesDirectory().getPath(),
					_filename + "." + _filetype);
			((CsdpFrame) _gui).enableAfterProperties();
			return success;
		}// accessFile
		
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}// PSaveAs

	/**
	 * Save Properties Data in file with same name
	 *
	 * @author
	 * @version $Id:
	 */
	public class PSave extends FileSave implements ActionListener {

		public PSave(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
		}

		public String getCurrentFilename() {
			return CsdpFunctions.getPropertiesFilename();
		}

		public String getCurrentFiletype() {
			return CsdpFunctions.getPropertiesFiletype();
		}

		public void setFilenameAndType(String filename, String filetype) {
			CsdpFunctions.setPropertiesFilename(filename);
			CsdpFunctions.setPropertiesFiletype(filetype);
		}

		public String getFilename() {
			String filename = CsdpFunctions.getPropertiesFilename() + "." + CsdpFunctions.getPropertiesFiletype();
			parseFilename(filename);
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean success = _app.pSave();
			if (DEBUG) {
				if (success == false)
					System.out.println("save properties failed");
				if (success)
					System.out.println("save properties succeeded");
			} // if
			return success;
		}// accessFile

		public boolean accessFile(String filename) {
			boolean success = _app.pSaveAs(CsdpFunctions.getPropertiesDirectory().getPath(), filename);
			return success;
		}// accessFile

		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}// PSave

	App _app;
	BathymetryPlot _plot;
	protected static final boolean DEBUG = false;
	protected static final String _openDialogMessage = "Select properties(.prp) file";
	protected static final String _openErrorMessage = "Only .prp extension allowed";
	protected static final String[] _openExtensions = { "prp" };
	protected static final int _numOpenExtensions = 1;

	protected static final String _saveDialogMessage = "Save Properties(.prp) file";
	protected static final String _saveErrorMessage = "Only .prp extension allowed";
	protected static final String[] _saveExtensions = { "prp" };
	protected static final int _numSaveExtensions = 1;

	protected static final String _saveSuccessMessage = "saved properties file";
	protected static final String _saveFailureMessage = "ERROR:  UNABLE TO SAVE PROPERTIES FILE";
	protected static final String _openSuccessMessage = "";
	protected static final String _openFailureMessage = "ERROR:  couldn't open properties file";
	CsdpFileFilter _pOpenFilter;
	CsdpFileFilter _pSaveFilter;
	int _filechooserState = -Integer.MAX_VALUE;
}// class PropertiesMenu
