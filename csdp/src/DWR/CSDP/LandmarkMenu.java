/*
    Copyright (C) 1998 State of California, Department of Water
    Resources.

    This program is licensed to you under the terms of the GNU General
    Public License, version 2, as published by the Free Software
    Foundation.

    You should have received a copy of the GNU General Public License
    along with this program; if not, contact Dr. Francis Chung, below,
    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
    02139, USA.

    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.

    For more information, contact:

    Dr. Francis Chung
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA  95814
    916-653-5601
    chung@water.ca.gov

    or see our home page: http://wwwdelmod.water.ca.gov/
*/
package DWR.CSDP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JFileChooser;

import DWR.CSDP.dialog.FileIO;
import DWR.CSDP.dialog.FileSave;
import DWR.CSDP.dialog.MessageDialog;
import DWR.CSDP.dialog.OkDialog;
import DWR.CSDP.dialog.YesNoDialog;

public class LandmarkMenu {

	public LandmarkMenu(App app, CsdpFrame gui) {
		_app = app;
		_gui = gui;
		_lOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_lSaveFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
	}

	/**
	 * Loads and displays landmarks
	 *
	 * @author
	 * @version $Id: DisplayMenu.java,v 1.3 2003/04/15 19:28:01 btom Exp $
	 */
	public class LOpen extends FileIO implements ActionListener {
		public LOpen(CsdpFrame gui) {
			super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openExtensions, _numOpenExtensions);
			_gui = gui;
			_ynd = new YesNoDialog(_gui, "A landmark file is already loaded.  Replace?", true);
			_jfc.setDialogTitle(_openDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_lOpenFilter);
			_jfc.setFileFilter(_lOpenFilter);
		}

		/**
		 * not needed here. Use if you want to save something before continuing.
		 */
		public void warnUserIfNecessary() {
		}// warnUserIfNecessary

		/**
		 * uses dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getLandmarkDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getLandmarkDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println("no file selected");

			_filechooserState = _jfc.showOpenDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setLandmarkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}// getFilename

		/**
		 * open landmark file
		 */
		public boolean accessFile() {
			if (_app._landmark != null) {
				_ynd.setVisible(true);
				if (_ynd._yes == true) {
					readFile();
				} // if
			} // if
			else if (_cancel == false) {
				readFile();
			} // else if
			_cancel = false;
			return true; // no need to tell user if failed.
		}// accessFile

		protected void readFile() {
			_landmark = _app.lReadStore(CsdpFunctions.getLandmarkDirectory().getPath(), _filename + "." + _filetype);
			((CsdpFrame) _gui).setLandmark(_landmark);
			_lplot = _app.setLandmarkPlotter();

			((CsdpFrame) _gui).getPlanViewCanvas(0).setLandmarkPlotter(_lplot);
			((CsdpFrame) _gui).getPlanViewCanvas(0).setUpdateLandmark(true);
			// removed for conversion to swing
			// _gui.getPlanViewCanvas(0).repaint();

			((CsdpFrame) _gui).enableAfterLandmark();
		}

		YesNoDialog _ynd;
	} // Class LOpen

	/**
	 * clears landmark.
	 */
	public class LClear implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			((App) _app).clearLandmarks();
		}
	}// class LClearLandmarks

	/**
	 * Save landmark file
	 *
	 * @author
	 * @version $Id: LandmarkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class LSave extends FileSave implements ActionListener {

		public LSave(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_gui = gui;
		}

		public String getCurrentFilename() {
			return CsdpFunctions.getLandmarkFilename();
		}

		public String getCurrentFiletype() {
			return CsdpFunctions.getLandmarkFiletype();
		}

		public void setFilenameAndType(String filename, String filetype) {
			CsdpFunctions.setLandmarkFilename(filename);
			CsdpFunctions.setLandmarkFiletype(filetype);
		}

		public String getFilename() {
			String filename = CsdpFunctions.getLandmarkFilename() + "." + CsdpFunctions.getLandmarkFiletype();
			parseFilename(filename);
			return filename;
		}// getFilename

		public boolean accessFile() {
			return _app.lSave();
		}

		public boolean accessFile(String filename) {
			return _app.lSaveAs(CsdpFunctions.getLandmarkDirectory().getPath(), filename);
		}
	}// LSave

	/**
	 * Save landmark file As
	 *
	 * @author
	 * @version $Id: LandmarkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class LSaveAs extends FileIO implements ActionListener {
		public LSaveAs(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_lSaveFilter);
			_jfc.setFileFilter(_lSaveFilter);
			_gui = gui;
		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getLandmarkDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getLandmarkDirectory());
			}

			_filechooserState = _jfc.showSaveDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setLandmarkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
				_cancel = false;
				CsdpFunctions._cancelSaveLandmarks = false;
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				CsdpFunctions._cancelSaveLandmarks = true;
				filename = null;
			} else {
				_cancel = true;
				CsdpFunctions._cancelSaveLandmarks = true;
				filename = null;
			} // if
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean saved = false;
			if (_cancel == false) {
				saved = _app.lSaveAs(CsdpFunctions.getLandmarkDirectory().getPath(), _filename + "." + _filetype);
				((CsdpFrame) _gui).enableAfterLandmark();
			} else {
				saved = false;
			}
			return saved;
		}

	} // LSaveAs

	/*
	 * Add landmark listener for popup menu (right click) will create new
	 * landmark file if landmark is null
	 */
	public class LAddPopup implements ActionListener {
		public LAddPopup(CsdpFrame gui) {
			_gui = gui;
			_jfc = new JFileChooser();
			_ynd = new YesNoDialog(_gui, "Replace existing landmark file?", true);
			_jfc.setDialogTitle(_newLandmarkDialogMessage);
			_jfc.setApproveButtonText("Create");
			_jfc.addChoosableFileFilter(_lOpenFilter);
			_jfc.setFileFilter(_lOpenFilter);
			_errorDialog = new OkDialog(_gui, _saveErrorMessage, true);
			_successDialog = new OkDialog(_gui, _saveSuccessMessage, true);
			_failureDialog = new OkDialog(_gui, _saveFailureMessage, true);
		}// constructor

		public void actionPerformed(ActionEvent e) {
			LandmarkInteractor li = _gui.getLandmarkInteractor();
			boolean tryAgain = true;
			while (tryAgain) {
				boolean success = false;
				if (_app._landmark == null) {
					success = createNewFile();
				} else {
					success = true;
				}
				if (success) {
					tryAgain = li.addLandmark();
					if (DEBUG)
						System.out.println("after calling add landmark");
				} else {
					if (DEBUG)
						System.out.println("LandmarkMenu.add: success is false after trying to create new file");
				}
			}
		}// actionPerformed

		private boolean createNewFile() {
			_cancel = false;
			boolean success = false;
			String filename = null;
			while (filename == null && _cancel == false) {
				String fname = getFilename();
				if (_cancel == false) {
					if (fname == null || fname.length() == 0) {
						// not necessary--use JFileChooser.CANCEL_OPTION in
						// subclass
						// _cancel = true;
						_errorDialog.setMessage("no file selected!");
						_errorDialog.setVisible(true);
					} else {
						if (accept(fname) == false) {
							fname = null;
							_errorDialog.setMessage(_saveErrorMessage);
							_errorDialog.setVisible(true);
						}
					} // else
				} // not cancelling
				filename = fname;
			} // while

			if (filename != null) {
				_landmark = _app.lCreate(CsdpFunctions.getLandmarkDirectory().getPath(), _filename + "." + _filetype);
				((CsdpFrame) _gui).updateLandmarkFilename(_filename + "." + _filetype);
				((CsdpFrame) _gui).setLandmark(_landmark);
				_lplot = _app.setLandmarkPlotter();
				((CsdpFrame) _gui).getPlanViewCanvas(0).setLandmarkPlotter(_lplot);
				((CsdpFrame) _gui).getPlanViewCanvas(0).setUpdateLandmark(true);
				((CsdpFrame) _gui).enableAfterLandmark();
			} // filename not null
			if (_landmark != null)
				success = true;
			return success;
		}// createNewFile

		/*
		 * accept is really supposed to be used by FilenameFilter, but since the
		 * FilenameFilter doesn't work, I am calling it directly. Copied from
		 * FileIO.
		 */
		private boolean accept(String name) {
			parseFilename(name);
			boolean value = false;
			if (_filetype != null) {
				for (int i = 0; i <= _numSaveExtensions - 1; i++) {
					if (_filetype.equals(_saveExtensions[i]))
						value = true;
				} // for
			} // if
			return value;
		}// accept

		private String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getLandmarkDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getLandmarkDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println("no file specified");

			// if file exists, warn user
			_filechooserState = _jfc.showOpenDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setLandmarkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}

			File selectedFile = _jfc.getSelectedFile();
			if (selectedFile.exists()) {
				_ynd.setVisible(true);
				if (_ynd._no == true || _ynd._cancel == true)
					filename = null;
			}
			return filename;
		}

		/**
		 * separates filename into prefix and extension
		 */
		private void parseFilename(String filename) throws NullPointerException {
			try {
				int dotIndex = filename.indexOf(".", 0);
				if (dotIndex >= 0) {
					_filename = filename.substring(0, dotIndex);
					_filetype = filename.substring(dotIndex + 1);
				} else if (dotIndex < 0) {
					_filename = null;
					_filetype = null;
				}
			} catch (Exception e) {
				System.out.println("no filename specified");
			} // catch
		}// parseFilename

		private JFileChooser _jfc;
		public OkDialog _errorDialog, _successDialog, _failureDialog;
		private String _filename, _filetype;
		private YesNoDialog _ynd;
		private boolean _cancel;
	}// class LAddPopup

	/*
	 * Move landmark listener for popup menu (right click)
	 */
	public class LMovePopup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			LandmarkInteractor li = _gui.getLandmarkInteractor();
			_gui.setCursor(CsdpFunctions._crosshairCursor);
		}
	}// class LMovePopup

	/*
	 * Edit landmark listener for popup menu (right click)
	 */
	public class LEditPopup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			LandmarkInteractor li = _gui.getLandmarkInteractor();
			boolean tryAgain = true;
			while (tryAgain) {
				tryAgain = li.editLandmark();
			}
		}
	}// class LEditPopup

	/*
	 * Delete landmark listener for popup menu (right click)
	 */
	public class LDeletePopup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			LandmarkInteractor li = _gui.getLandmarkInteractor();
			li.deleteLandmark();
		}
	}// class LAddPopup

	/*
	 * Add landmark by clicking on location and entering name
	 */
	public class LAdd implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}
	}// class LAdd

	/*
	 * Move landmark by dragging
	 */
	public class LMove implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}
	}// class LMove

	/*
	 * Edit landmark text
	 */
	public class LEdit implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}
	}// class LEdit

	/*
	 * Delete landmark
	 */
	public class LDelete implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}
	}// class LDelete

	public class LHelp implements ActionListener {
		private String _message = null;
		private final int _maxWidth = 90;
		int i = 0;

		/*
		 * Constructor
		 */
		public LHelp() {
			_message = "=====================\n";
			i++;
			_message += "How To Edit Landmarks\n";
			i++;
			_message += "=====================\n\n";
			i++;
			_message += "Right click to display the landmark editing menu.\n";
			i++;
			_message += "\n";
			i++;
			_message += "Add Landmark\n\n";
			i++;
			_message += "Select 'Add Landmark' to create a new landmark.\n";
			i++;
			_message += "If a landmark file is not loaded, you will be prompted to\n";
			i++;
			_message += "specify a filename and location for a new landmark file.\n";
			i++;
			_message += "You will then be asked to specify a name for the landmark\n\n";
			i++;
			_message += "Move Landmark\n\n";
			i++;
			_message += "If your right click was close to an existing landmark, the\n";
			i++;
			_message += "'Move Landmark' menu item will be enabled.  Select this menu\n";
			i++;
			_message += "item, then click on the new location for the landmark.\n";
			i++;
			_message += "Do NOT try to click and drag the landmark.\n\n";
			i++;
			_message += "Edit Landmark\n\n";
			i++;
			_message += "If your right click was close to an existing landmark, the\n";
			i++;
			_message += "'Edit Landmark' menu item will be enabled.  Select this menu\n";
			i++;
			_message += "item and you will be asked to specify the new name for the \n";
			i++;
			_message += "landmark. If the name you specify matches the name of any other\n";
			i++;
			_message += "landmark, the change will fail.\n\n";
			i++;
			_message += "Delete Landmark\n\n";
			i++;
			_message += "If your right click was close to an existing landmark, the\n";
			i++;
			_message += "'Delete Landmark' menu item will be enabled.  Select this menu\n";
			i++;
			_message += "item and the selected landmark will be deleted.  Be careful--\n";
			i++;
			_message += "for the sake of efficiency, there is no 'are you sure' dialog.\n";
			i++;
			_message += "An undo feature may be added in the future.";
			i++;
		}// constructor

		public void actionPerformed(ActionEvent e) {
			MessageDialog mm = new MessageDialog(_gui, "About CSDP", _message, true, false, _maxWidth, i);
			mm.setVisible(true);
		}// actionPerformed
	}// class LHelp

	// /**
	// * Changes option to print only channel lengths when exporting landmark
	// data
	// * to station/elevation format
	// */
	// public class NChannelLengthsOnly implements ItemListener{
	// public void itemStateChanged(ItemEvent e){
	// if(e.getStateChange() == ItemEvent.SELECTED){
	// CsdpFunctions.setChannelLengthsOnly(true);
	// }else{
	// CsdpFunctions.setChannelLengthsOnly(false);
	// }
	// }

	// }//NChannelLengthsOnly

	//// KEEP THIS CLASS--IT MIGHT BE NEEDED SOMEDAY
	// // public class LandmarkFilenameFilter implements FilenameFilter{
	// // public boolean accept(File dir, String name){
	// // boolean returnValue = false;
	// // parseFilename(name);
	// // if(_filetype.equals(LANDMARK_FILETYPE)){
	// // if(DEBUG)System.out.println("match found");
	// // returnValue = true;
	// // }
	// // else returnValue = false;
	// // return returnValue;
	// // }
	// // }//class LandmarkFilenameFilter

	App _app;
	private static final boolean DEBUG = false;

	private static final String _newLandmarkDialogMessage = "Enter landmark(.cdl) filename";
	private static final String _openDialogMessage = "Select landmark(.cdl) file";
	private static final String _openErrorMessage = "Only .cdl extension allowed";
	private static final String[] _openExtensions = { "cdl" };
	private static final int _numOpenExtensions = 1;

	private static final String _saveDialogMessage = "Save Landmark(.cdl) file";
	private static final String _saveErrorMessage = "Only .cdl extension allowed";
	private static final String[] _saveExtensions = { "cdl" };
	private static final int _numSaveExtensions = 1;

	private static final String _saveSuccessMessage = "Saved landmark file";
	private static final String _saveFailureMessage = "ERROR:  LANDMARK FILE NOT SAVED!";
	private static final String _openSuccessMessage = "";
	private static final String _openFailureMessage = "ERROR:  couldn't open landmark file";

	private Landmark _landmark;
	private CsdpFileFilter _lOpenFilter;
	private CsdpFileFilter _lSaveFilter;
	private CsdpFileFilter _nExportFilter;
	private CsdpFileFilter _3dNExportFilter;
	private LandmarkPlot _lplot;
	private CsdpFrame _gui;

	int _filechooserState = -Integer.MAX_VALUE;
} // class LandmarkMenu
