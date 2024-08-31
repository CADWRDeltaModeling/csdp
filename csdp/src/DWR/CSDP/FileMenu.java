package DWR.CSDP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import DWR.CSDP.dialog.DataEntryDialog;
import DWR.CSDP.dialog.FileIO;
import DWR.CSDP.dialog.MultipleFileIO;

public class FileMenu {

	public class FSaveBathInsideOutsidePolygon implements ActionListener {

		private CsdpFrame csdpFrame;
		public FSaveBathInsideOutsidePolygon(CsdpFrame csdpFrame) {
			this.csdpFrame = csdpFrame;
		}

		public void actionPerformed(ActionEvent arg0) {
			String names[] = new String[2];
			String initValue[] = new String[2];
			names[0] = "Output ASCII .prn file";
			names[1] = "Save points inside polygon?";
			initValue[0] = "";
			initValue[1] = "true";
			
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			String[] tooltips = new String[] {"The output file, which will be a CSDP .prn file", "If selected, save only points inside "
					+ "centerline polygon. Otherwise, save only points outside."};
			boolean[] disableIfNull = new boolean[] {true, true};
			int[] numDecimalPlaces = new int[] {0, 0};
			String[] extensions = new String[] {"prn", ""};
			
			String instructions = 
					"<HTML><BODY><B>Save bathymetry points inside polygon represented by a centerline.</B><BR>"
							+ "1. The selected centerline will be assumed to represent a polygon.<BR><BR>"
							+ "2. Specify the path to an output CSDP .prn file that you wish to create.<BR>"
							+ "3. Check the box if you wish to save points that are inside the polygon;"
							+ "uncheck if you wish to save points that are outside the polygon.<BR></font></BODY></HTML>";

			DataEntryDialog dataEntryDialog = new DataEntryDialog(csdpFrame, "Save Bathymetry points inside/outside polygon", instructions, 
					names, initValue, dataTypes, disableIfNull, numDecimalPlaces, extensions, tooltips, true);
			
			int response=dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String outputPrnDirectory = dataEntryDialog.getDirectory(names[0]).toString();
				String outputPrnFilename = dataEntryDialog.getFilename(names[0]).toString();
				boolean saveInside = Boolean.parseBoolean(dataEntryDialog.getValue(names[1]));
//				try {
				
					Network network = csdpFrame.getNetwork();
					System.out.println("app, network="+_app+","+network);
					_app.fSaveBathymetryDataInsideOrOutsidePolygonCenterline(outputPrnDirectory, outputPrnFilename, network.getSelectedCenterline(), saveInside);
//					JOptionPane.showMessageDialog(csdpFrame, "Ascii Raster Conversion to CSDP .prn file succeeded!", "Success", JOptionPane.INFORMATION_MESSAGE);
//				} catch (Exception e) {
//					JOptionPane.showMessageDialog(csdpFrame, "Ascii Raster Conversion to CSDP .prn file failed!", "Failure", JOptionPane.ERROR_MESSAGE);
//					e.printStackTrace();
//				}
				
			}//if ok clicked.
		}
	}//FSaveBathInsideOutsidePolygon
	
	public FileMenu(App app) {
		_app = app;
		_fOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_fSaveFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
		_fConvertFilter = new CsdpFileFilter(_convertExtensions, _numConvertExtensions);
	}

	/**
	 * Get bathymetry data filename and call functions to open, read, and
	 * display it.
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FOpen extends FileIO implements ActionListener {

		public FOpen(CsdpFrame gui) {
			super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openExtensions, _numOpenExtensions);
			_jfc.setDialogTitle(_openDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_fOpenFilter);
			_jfc.setFileFilter(_fOpenFilter);
		}

		/**
		 * uses dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			int filechooserState = -Integer.MAX_VALUE;
			if (CsdpFunctions.getBathymetryDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getBathymetryDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getBathymetryDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getOpenDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println();
			// _fd.setVisible(true);
			// filename = _fd.getFile();

			filechooserState = _jfc.showOpenDialog(_gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				//// CsdpFunctions.setBathymetryDirectory(_fd.getDirectory());
				//// CsdpFunctions.setOpenDirectory(_fd.getDirectory());
				CsdpFunctions.setBathymetryDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			} // else
			return filename;
		}// getFilename

		/**
		 * called by superclass. reads file
		 */
		public boolean accessFile() {
			_plot = _app.bReadStore(_gui, CsdpFunctions.getBathymetryDirectory().getPath(), _filename, _filetype);
			return true; // no need to warn user if fails
		}

		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	} // FOpenClass

	public class BImportFromAsciiRaster implements ActionListener {
		private CsdpFrame csdpFrame;
		public BImportFromAsciiRaster(CsdpFrame csdpFrame) {
			this.csdpFrame = csdpFrame;
		}
		public void actionPerformed(ActionEvent arg0) {
			String names[] = new String[6];
			String initValue[] = new String[6];
			names[0] = "Input ASCII Raster File";
			names[1] = "Output ASCII .prn file";
			names[2] = "year";
			names[3] = "source";
			names[4] = "description";
			names[5] = "Point reduction factor";
			initValue[0] = null;
			initValue[1] = null;
			initValue[2] = "2019";
			initValue[3] = "DWR";
			initValue[4] = "New bathymetry data";
			initValue[5] = "1";
			
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.NUMERIC_TYPE,
					DataEntryDialog.STRING_TYPE, DataEntryDialog.STRING_TYPE, DataEntryDialog.NUMERIC_TYPE};
			String[] tooltips = new String[] {"The input file, which is the result of using the ArcGIS 'Raster to Ascii' function", 
					"The output file, which will be a CSDP .prn file", "The year the data were collected", "The source of the data, usually agency name; keep it short",
					"A description, usually identifying the name of the body of water", "If the data set is dense, enter a factor greater than 1 to reduce."};
			boolean[] disableIfNull = new boolean[] {true, true, true, true, false, true};
			int[] numDecimalPlaces = new int[] {0,0,0,0,0,0};
			String[] extensions = new String[] {"","prn","","","",""};
			
			String instructions = 
					"<HTML><BODY><B>Import ASCII Raster Bathymetry File</B><BR>"
							+ "1. Specify the path to an ASCII raster file, created using the ArcGIS 'Raster to Ascii' function."
							+ "coordinates are assumed to be in meters, UTM Zone 10 NAD 83 and NAVD88<BR><BR>"
							+ "2. Specify the path to an output CSDP .prn file that you wish to create.<BR>"
							+ "3. Specify the year the data were collected.<BR>"
							+ "4. Specify the source of the data (usually agency name, keep it short).<BR>"
							+ "5. Specify a description (usually the name of the body of water. Not used by CSDP."
							+ "6. If the data set is dense, enter a factor greater than 1 to reduce density.<BR></font></BODY></HTML>";

			DataEntryDialog dataEntryDialog = new DataEntryDialog(csdpFrame, "Import ASCII Raster Bathymetry File", instructions, 
					names, initValue, dataTypes, disableIfNull, numDecimalPlaces, extensions, tooltips, true);
			
			int response=dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String inputRasterDirectory = dataEntryDialog.getDirectory(names[0]).toString();
				String inputRasterFilename = dataEntryDialog.getFilename(names[0]);
				String outputPrnDirectory = dataEntryDialog.getDirectory(names[1]).toString();
				String outputPrnFilename = dataEntryDialog.getFilename(names[1]).toString();
				String year = dataEntryDialog.getValue(names[2]);
				String source = dataEntryDialog.getValue(names[3]);
				String description = dataEntryDialog.getValue(names[4]);
				int pointReductionFactor = Integer.parseInt(dataEntryDialog.getValue(names[5]));
				ASCIIGridToCSDPConverter asciiGridToCSDPConverter = new ASCIIGridToCSDPConverter(csdpFrame, inputRasterDirectory+File.separator+inputRasterFilename, outputPrnDirectory+File.separator+outputPrnFilename, 
						year, source, description, pointReductionFactor);
				try {
					asciiGridToCSDPConverter.convert();
					JOptionPane.showMessageDialog(csdpFrame, "Ascii Raster Conversion to CSDP .prn file succeeded!", "Success", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(csdpFrame, "Ascii Raster Conversion to CSDP .prn file failed!", "Failure", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
			}//if ok clicked.

		}//actionPerformed

	}//inner class BImportFromAsciiRaster

	/**
	 * Convert bathymetry file: prn to cdp
	 * @author btom
	 *
	 */
	public class FConvert extends MultipleFileIO implements ActionListener {

		public FConvert(CsdpFrame gui) {
			super(gui, _convertDialogMessage, _convertErrorMessage, _convertSuccessMessage, _convertFailureMessage, false, _convertExtensions, _numConvertExtensions);
			_jfc.setDialogTitle(_convertDialogMessage);
			_jfc.setApproveButtonText("Convert");
			_jfc.addChoosableFileFilter(_fConvertFilter);
			_jfc.setFileFilter(_fConvertFilter);
		}

		/**
		 * uses dialog box to get filename from user
		 */
		protected String[] getFilenames() {
			int numLines = 0;
			String[] filenames = null;
			int filechooserState = -Integer.MAX_VALUE;
			if (CsdpFunctions.getBathymetryDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getBathymetryDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getBathymetryDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getOpenDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println();
			// _fd.setVisible(true);
			// filename = _fd.getFile();

			filechooserState = _jfc.showOpenDialog(_gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				File[] files = _jfc.getSelectedFiles();
				_filenames = new String[files.length];
				_filetypes = new String[files.length];
				filenames = new String[files.length];
				for(int i=0; i<files.length; i++) {
					//// CsdpFunctions.setBathymetryDirectory(_fd.getDirectory());
					//// CsdpFunctions.setOpenDirectory(_fd.getDirectory());
					filenames[i]= _jfc.getName(files[i]); 
					CsdpFunctions.setBathymetryDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
					CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
					parseFilename(i, filenames[i]);
					
				}
			} else if (filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filenames = null;
			} else {
				filenames = null;
			} // else
			return filenames;
		}// getFilename

		@Override
		public boolean accessFile(String filename, String filetype) {
			_app.convertPrnToCdp(_gui, CsdpFunctions.getBathymetryDirectory().getPath(), filename, filetype);
			return true; // no need to warn user if fails
		}
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}//inner class FConvert

	
	/**
	 * repaint when window resized
	 */
	public class FResizeListener extends ComponentAdapter implements ComponentListener {
		CsdpFrame _gui;

		public FResizeListener(CsdpFrame gui) {
			_gui = gui;
		}

		public void componentResized(ComponentEvent e) {
			_gui.getPlanViewCanvas(0).zoomFit();
		}
	}

	/**
	 * Exit the program
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FExit extends WindowAdapter implements ActionListener {
		CsdpFrame _gui;

		public FExit(CsdpFrame gui) {
			_gui = gui;
		}

		public void windowClosing(WindowEvent e) {
			exitProgram();
		}

		public void actionPerformed(ActionEvent e) {
			exitProgram();
		}// actionPerformed

		private void exitProgram() {
			Network net = _gui.getNetwork();
			if (net != null) {
				if (net.isUpdated()) {
					int response = JOptionPane.showConfirmDialog(_gui, "Network file is not saved.  Save(y/n)?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						_gui.saveNetwork();
						System.exit(0);
					}else if(response==JOptionPane.NO_OPTION) {
						System.exit(0);
					} else {
						// do nothing
					}
				} // if network has changed
				else if (net.isUpdated() == false) {
					if(_app.anyUnsavedXsectEdits()){
						int response = JOptionPane.showConfirmDialog(_gui, "There is at least one cross-section graph open "
								+ "with unsaved changes, which will be lost if you continue. \n\nClick Yes to quit without saving, and "
								+ "No to cancel quitting.",
								"Cross-section window open!", JOptionPane.YES_NO_OPTION);
						if(response==JOptionPane.YES_OPTION) {
							System.exit(0);
						}
					}else {
						System.exit(0);
					}
				} // else
			} // if net isn't null
			else {
				System.exit(0);
			} // else exit if network null
		}// exitProgram
	}// class FExit

	/**
	 * Setup print options
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FPrintSetup implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}// class FPrintSetup

	/**
	 * print displayed data
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FPrint implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}// FPrint

	/**
	 * Preview
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FPrintPreview implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}// FPrintPreview

	/**
	 * Extract bathymetry data
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	// public class FExtract extends FSaveAs implements ActionListener {
	// public FExtract(CsdpFrame gui){
	// super(gui, _saveDialogMessage, _saveErrorMessage, _saveExtensions,
	// _numSaveExtensions);
	// c = gui.getExtractRegion();
	// }//constructor
	// /**
	// * called by superclass. saves file
	// */
	// public void accessFile(){
	// if(_filename != null){
	// _app.fSave(CsdpFunctions.getBathymetryDirectory(),
	// _filename+"."+_filetype,c);
	// }//if
	// }//accessFile
	// /*
	// * stores coordinates of extract region
	// */
	// float[] c;
	// }//FExtract

	/**
	 * Save bathymetry in file with specified name
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FSaveAs extends FileIO implements ActionListener {

		public FSaveAs(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_fSaveFilter);
			_jfc.setFileFilter(_fSaveFilter);

		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getBathymetryDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getBathymetryDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getBathymetryDirectory());
			} else
				System.out.println("error in FileMenu.FSaveAs:no bathymetry directory");
			//// _fd.setVisible(true);
			int filechooserState = _jfc.showSaveDialog(_gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				//// filename = _fd.getFile();
				// CsdpFunctions.setBathymetryDirectory(_fd.getDirectory());
				CsdpFunctions.setBathymetryDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}

			return filename;
		}// getFilename

		/**
		 * called by superclass. saves file
		 */
		public boolean accessFile() {
			boolean success = false;
			if (_filename != null) {
				success = _app.fSave(CsdpFunctions.getBathymetryDirectory().getPath(), _filename + "." + _filetype);
			} // if
			return success;
		}// accessFile
		
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}// FSaveAs

	/**
	 * Save bathymetry in file with specified name in NAVD88
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FSaveAsNAVD88 extends FileIO implements ActionListener {

		public FSaveAsNAVD88(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_fSaveFilter);
			_jfc.setFileFilter(_fSaveFilter);

		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getBathymetryDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getBathymetryDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getBathymetryDirectory());
			} else
				System.out.println("error in FileMenu.FSaveAsNAVD88:no bathymetry directory");
			//// _fd.setVisible(true);
			int filechooserState = _jfc.showSaveDialog(_gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				//// filename = _fd.getFile();
				// CsdpFunctions.setBathymetryDirectory(_fd.getDirectory());
				CsdpFunctions.setBathymetryDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}

			return filename;
		}// getFilename

		/**
		 * called by superclass. saves file
		 */
		public boolean accessFile() {
			boolean success = false;
			if (_filename != null) {
				success = _app.fSaveAsNAVD88(CsdpFunctions.getBathymetryDirectory().getPath(),
						_filename + "." + _filetype);
			} // if
			return success;
		}// accessFile
		
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}// FSaveAsNAVD88

	/**
	 * Save bathymetry on screen (current zoom state) in file with specified
	 * name
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FSaveBathZoomed extends FileIO implements ActionListener {
		public FSaveBathZoomed(CsdpFrame gui) {
			super(gui, _saveZoomedDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveZoomedDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_fSaveFilter);
			_jfc.setFileFilter(_fSaveFilter);
		}// constructor

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getBathymetryDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getBathymetryDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getBathymetryDirectory());
			} else
				System.out.println("error in FileMenu.FSaveAs:no bathymetry directory");
			//// _fd.setVisible(true);
			int filechooserState = _jfc.showSaveDialog(_gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				//// filename = _fd.getFile();
				// CsdpFunctions.setBathymetryDirectory(_fd.getDirectory());
				CsdpFunctions.setBathymetryDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}// getFilename

		/**
		 * called by superclass. saves file
		 */
		public boolean accessFile() {
			boolean success = false;
			if (_filename != null) {
				success = _app.fSaveZoomed(CsdpFunctions.getBathymetryDirectory().getPath(),
						_filename + "." + _filetype);
			} // if
			return success;
		}// accessFile
		
		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	}// class FSaveBathZoomed

	/**
	 * Save Bathymetry Data in file with same name. This feature is not
	 * necessary because the program currently does not allow editing of
	 * bathymetry data.
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FSave implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}// FSave

	/**
	 * Clear bathymetry data from memory
	 *
	 * @author
	 * @version $Id: FileMenu.java,v 1.3 2003/07/22 22:29:53 btom Exp $
	 */
	public class FClose implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}// FClose

	App _app;
	BathymetryPlot _plot;
	protected static final boolean DEBUG = false;
	protected static final String _openDialogMessage = "Select bathymetry(.prn, .cdp, .cdp.gz) file";
	protected static final String _openErrorMessage = "Only .prn, .cdp, .cdp.gz extensions allowed";
	protected static final String[] _openExtensions = { "prn", "cdp", "cdp.gz" };
	protected static final int _numOpenExtensions = 3;

	protected static final String _saveZoomedDialogMessage = "Save currently displayed bathymetry (.cdp or .prn file)";
	protected static final String _saveDialogMessage = "Save Bathymetry(.cdp or .prn) file";
	protected static final String _saveErrorMessage = "Only .cdp, .prn extensions allowed";
	protected static final String[] _saveExtensions = { "cdp", "prn" };
	protected static final int _numSaveExtensions = 2;
	protected static final String _saveSuccessMessage = "Saved bathymetry file";
	protected static final String _saveFailureMessage = "ERROR:  BATHYMETRY FILE NOT SAVED!";
	protected static final String _openSuccessMessage = "";
	protected static final String _openFailureMessage = "ERROR: couldn't open bathymetry file";

	protected static final String _convertDialogMessage = "Select bathymetry (.prn) files";
	protected static final String _convertErrorMessage = "Only .prn extensions allowed";
	protected static final String[] _convertExtensions = {"prn"};
	protected static final int _numConvertExtensions = 1;
	protected static final String _convertSuccessMessage = "Converted files";
	protected static final String _convertFailureMessage = "Error: files not converted";
	
	CsdpFileFilter _fOpenFilter;
	CsdpFileFilter _fSaveFilter;
	CsdpFileFilter _fConvertFilter;
}// class FileMenu
