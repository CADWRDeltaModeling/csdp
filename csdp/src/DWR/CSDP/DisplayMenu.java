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

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import DWR.CSDP.dialog.DataEntryDialog;
import DWR.CSDP.dialog.DataFilterCheckbox;
import DWR.CSDP.dialog.FileIO;

/**
 * calls methods for displaying landmarks and other information
 *
 * @author
 * @version
 */
public class DisplayMenu {

	public DisplayMenu(App app, Network net) {
		_app = app;
		_net = net;
		_lOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_DLGOpenFilter = new CsdpFileFilter(_openDLGExtensions, _numOpenDLGExtensions);
	}

	/**
	 * Allow user to change display parameters
	 *
	 * @author
	 * @version $Id: DisplayMenu.java,v 1.3 2003/04/15 19:28:01 btom Exp $
	 */
	public class DParameters implements ActionListener {
		CsdpFrame _gui;

		public DParameters(CsdpFrame gui) {
			_gui = gui;
		}

		/**
		 * get new cross-section thickness, bathymetry point dimension from
		 * user.
		 */
		public void actionPerformed(ActionEvent e) {
			_plot = _gui.getPlotObject();
			_net = _gui.getNetwork();

			double oldThickness = CsdpFunctions.getXsectThickness();
			int oldPointSize = _plot.getPointSize();
			int oldNetworkSelectedPointDimension = NetworkPlot.NETWORK_SELECTION_POINT_DIMENSION;
			boolean oldNetworkColoring = CsdpFunctions.NETWORK_COLORING;
			double oldSummaryCalculationElevation = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
			double oldIntertidalLowTide = CsdpFunctions.INTERTIDAL_LOW_TIDE;
			double oldIntertidalHighTide = CsdpFunctions.INTERTIDAL_HIGH_TIDE;
			double oldDeltaX = CsdpFunctions.DELTAX;
			double oldCrossSectionLineLength = CsdpFunctions.CROSS_SECTION_LINE_LENGTH;
			boolean oldAddXsectPointsBasedOnPointOrder = CsdpFunctions.ADD_XSECT_POINTS_BASED_ON_POINT_ORDER;
			
			String[] names = new String[10];
			String[] defaultValue = new String[10];
			int[] dataType = new int[] {DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, 
					DataEntryDialog.BOOLEAN_TYPE, 
					DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, 
					DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			int[] numDecimalPlaces = new int[] {0,0,0,0,2,2,2,0,0,0};
			names[0] = "Cross-Section Thickness";
			names[1] = "Bathymetry Point Dimension";
			names[2] = "Centerline Point Dimension";
			names[3] = "Network Coloring";
			names[4] = "Conveyance Characteristics Elevation";
			names[5] = "Intertidal Low Tide";
			names[6] = "Intertidal High Tide";
			names[7] = "Delta X";
			names[8] = "Cross-section line length";
			names[9] = "Add Xsect points based on point order";
			
			defaultValue[0] = String.valueOf(oldThickness);
			defaultValue[1] = String.valueOf(oldPointSize);
			defaultValue[2] = String.valueOf(oldNetworkSelectedPointDimension);
			defaultValue[3] = String.valueOf(oldNetworkColoring); 
			defaultValue[4] = String.valueOf(oldSummaryCalculationElevation);
			defaultValue[5] = String.valueOf(oldIntertidalLowTide);
			defaultValue[6] = String.valueOf(oldIntertidalHighTide);
			defaultValue[7] = String.valueOf(oldDeltaX);
			defaultValue[8] = String.valueOf(oldCrossSectionLineLength);
			defaultValue[9] = String.valueOf(oldAddXsectPointsBasedOnPointOrder);

			boolean[] disableIfNull = new boolean[] {true, true, true, true, true, true, true, true, true, true};
			
			String[] tooltips = new String[] {
					"In cross-section view, this value determines how far (feet) upstream and downstream you are looking",
					"The size, in pixels, of each of the dots that represents a bathymetry data point, displayed in plan view.",
					"The size, in pixels, of the dots that are drawn to represent, when selected, each centerline vertex "
					+ "point and each cross-section line endpoint",
					"If selected, centerlines and cross-section lines will be colored to identify issues with cross-sections "
					+ "that may need correction",
					"The elevation (feet) to be used when calculating area, width, wetted perimeter, "
					+ "volume, wetted area, and surface area. This does not affect DSM2 input files generated by the CSDP. "
					+ "This elevation is used for calculating values displayed in the conveyance characteristics panels in "
					+ "cross-section view and in centerline summary view",
					"The minimum water level that is assumed to occur throughout the system. Used for negative dConveyance reporting",
					"The maximum water level that is assumed to occur throughout the system. Used for negative dConveyance reporting",
					"The value of delta x used by DSM2. This is used for calculating the actual delta x that will be used for "
					+ "a channel by DSM2. It is needed for automatically adding cross-sections at computational points.",
					"Cross-section line length: The cross-section line length to be used when automatically adding cross-sections at "
					+ "computational points.",
					"Add Xsect points based on point order: Cross-section points can be added to the left or right side of a cross-section "
					+ "drawing. A cross-section drawing can be created starting on the left bank or the right bank. When adding points, the "
					+ "CSDP must determine which point in the drawing is on the right bank and which is on the left bank. This can be easily "
					+ "done by comparing the station values of the leftmost and rightmost points. If you uncheck this box, the left side "
					+ "will be assumed to be the first point that was drawn, and the right bank will be the last point."
			};
			
			String instructions = "<HTML><BODY><B>Cross-Section Thickness:</B> In cross-section view, this value determines how far (feet) <BR>"
					+ "upstream and downstream you are looking.<BR>"
					+ "<B>Bathymetry Point Dimension:</B> The size, in pixels, of each of the dots that represents a bathymetry data <BR>"
					+ "point, displayed in plan view.<BR>"
					+ "<B>Centerline Point Dimension:</B> The size, in pixels, of the dots that are drawn to represent, when selected, <BR>"
					+ "each centerline vertex point and each cross-section line endpoint<BR>"
					+ "<B>Network Coloring:</B> If selected, centerlines and cross-section lines will be colored to identify issues with <BR>"
					+ "cross-sections that may need correction\","
					+ "<B>Conveyance Characteristics Elevation:</B> The elevation (feet) to be used when calculating area, width, wetted perimeter, <BR>"
					+ "volume, wetted area, and surface area. This does not affect DSM2 input files generated by the CSDP. This elevation is <BR>"
					+ "used for calculating values displayed in the conveyance characteristics panels in cross-section view and in <BR>"
					+ "centerline summary view<BR>"
					+"<B>Minimum Intertidal Elevation:</B> The minimum water level that is assumed to occur throughout the system. Used for negative dConveyance reporting<BR>"
					+"<B>Maximum Intertidal Elevation:</B> The maximum water level that is assumed to occur throughout the system. Used for negative dConveyance reporting<BR>"
					+ "<B>Delta X:</B> The value of delta x used by DSM2. This is used for calculating the actual delta x that will be used for<BR>"
					+ "a channel by DSM2. It is needed for automatically adding cross-sections at computational points.<BR>"
					+ "<B>Cross-section line length:</B> The cross-section line length to be used when automatically adding cross-sections at <BR>"
					+ "computational points.<BR>"
					+ "<B>Add Xsect points based on point order:</B>  Cross-section points can be added to the left or right side of a cross-section<BR>"
					+ "drawing. A cross-section drawing can be created starting on the left bank or the right bank. When adding points, the  CSDP <BR> "
					+ "must determine which point in the drawing is on the right bank and which is on the left bank. This can be easily done by <BR>"
					+ "comparing the station values of the leftmost and rightmost points. If you uncheck this box, the left side will be assumed to be <BR>"
					+ "the first point that was drawn, and the right bank will be the last point.\".</BODY></HTML>";

			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, "Program Options", instructions, names, defaultValue, dataType, 
					disableIfNull, numDecimalPlaces, tooltips, true);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String t = dataEntryDialog.getValue(names[0]);
				String pd = dataEntryDialog.getValue(names[1]);
				String npd= dataEntryDialog.getValue(names[2]);
				String newNetworkColoringOptionString = dataEntryDialog.getValue(names[3]);
				String newElevationString = dataEntryDialog.getValue(names[4]);
				String newIntertidalLowTideString = dataEntryDialog.getValue(names[5]);
				String newIntertidalHighTideString = dataEntryDialog.getValue(names[6]);
				String newDeltaXString = dataEntryDialog.getValue(names[7]);
				String newCrossSectionLineLengthString = dataEntryDialog.getValue(names[8]);
				String newAddXsectPointsBasedOnPointOrderString = dataEntryDialog.getValue(names[9]);

				System.out.println("t,pd,npd,newNetworkColoringOptionString, newElevationString, newIntertidalLowTideString,"
						+ "newIntertidalHighTideString, newDeltaXString, newCrossSectionLineLengthString, "
						+ "newAddXsectPointsBasedOnPointOrderString="+
						t+","+pd+","+npd+","+newNetworkColoringOptionString+","+newElevationString+","+newIntertidalLowTideString+","
						+newIntertidalHighTideString+","+newDeltaXString+","+newCrossSectionLineLengthString+","+
						newAddXsectPointsBasedOnPointOrderString);
				
				int newPointSize = (int) (Double.parseDouble(pd));
				int newNetworkSelectionPointSize = (int)Double.parseDouble(npd);
				boolean newNetworkColoringOption = Boolean.parseBoolean(newNetworkColoringOptionString);
				double newThickness = Double.parseDouble(t);
				double newElevation = Double.parseDouble(newElevationString);
				double newIntertidalLowTide = Double.parseDouble(newIntertidalLowTideString);
				double newIntertidalHighTide = Double.parseDouble(newIntertidalHighTideString);
				double newDeltaX = Double.parseDouble(newDeltaXString);
				double newCrossSectionLineLength = Double.parseDouble(newCrossSectionLineLengthString);
				boolean newAddXsectPointsBasedOnPointOrder = Boolean.parseBoolean(newAddXsectPointsBasedOnPointOrderString);

				System.out.println("newThickness, newPointSize, newNetworkSelectionPointSize, newNetworkColoringOption,"
						+ "newElevation, newIntertidalLowTide, newIntertidalHighTide, newDeltaX, newCrossSectionLineLength, "
						+ "newAddXsectPointsBasedOnPointOrder="+
						newThickness+","+newPointSize+","+newNetworkSelectionPointSize+","+newNetworkColoringOption+","+
						newElevation+","+newIntertidalLowTide+","+newIntertidalHighTide+","+newDeltaX+","+newCrossSectionLineLength+","+
						newAddXsectPointsBasedOnPointOrder);
				
				if (DEBUG)
					System.out.println("thickness, point dimension=" + newThickness + "," + newPointSize);

				boolean repaintNetwork = false;
				if (newThickness != oldThickness) {
					CsdpFunctions.setXsectThickness(newThickness);
				} // change thickness
				if(newNetworkSelectionPointSize != oldNetworkSelectedPointDimension) {
					NetworkPlot.NETWORK_SELECTION_POINT_DIMENSION = newNetworkSelectionPointSize;
				}
				if (newPointSize != oldPointSize) {
					_plot.setPointSize(newPointSize);
					if(newNetworkColoringOption) {
						repaintNetwork = true;
					}
				} // change point size
				if(newNetworkColoringOption != oldNetworkColoring) {
					CsdpFunctions.NETWORK_COLORING = newNetworkColoringOption;
					if(newNetworkColoringOption) {
						repaintNetwork = true;
					}
				}
				if(newElevation != oldSummaryCalculationElevation) {
					CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS = newElevation;
					if(_net != null) {
						Xsect xsect = _net.getSelectedXsect();
						if(xsect != null) {
							_gui.updateInfoPanel(xsect.getAreaSqft(newElevation), xsect.getWidthFeet(newElevation),
									xsect.getWettedPerimeterFeet(newElevation), xsect.getHydraulicDepthFeet(newElevation));
						}
					}
				}
				if(newIntertidalLowTide != oldIntertidalLowTide) {
					CsdpFunctions.INTERTIDAL_LOW_TIDE = newIntertidalLowTide;
					if(newNetworkColoringOption) {
						repaintNetwork = true;
					}
				}
				if(newIntertidalHighTide != oldIntertidalHighTide) {
					CsdpFunctions.INTERTIDAL_HIGH_TIDE = newIntertidalHighTide;
					if(newNetworkColoringOption) {
						repaintNetwork = true;
					}
				}
				if(newDeltaX != oldDeltaX) {
					CsdpFunctions.DELTAX = newDeltaX;
				}
				if(newCrossSectionLineLength != oldCrossSectionLineLength) {
					CsdpFunctions.CROSS_SECTION_LINE_LENGTH = newCrossSectionLineLength;
				}
				if(newAddXsectPointsBasedOnPointOrder != oldAddXsectPointsBasedOnPointOrder) {
					CsdpFunctions.ADD_XSECT_POINTS_BASED_ON_POINT_ORDER = newAddXsectPointsBasedOnPointOrder;
				}
				if(repaintNetwork) {
					_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
					_gui.getPlanViewCanvas(0).repaint();
				}
			}//if ok clicked
		}//actionPerformed

	} // Class DParameters

	/**
	 * Allow user to change elevation bins for bathymetry display-- The min and
	 * max elevations and interval
	 *
	 * @author
	 * @version $Id:
	 */
	public class DElevBins implements ActionListener {
		CsdpFrame _gui;

		public DElevBins(CsdpFrame gui) {
			_gui = gui;
		}

		/**
		 * get new cross-section thickness, bathymetry point dimension from
		 * user.
		 */
		public void actionPerformed(ActionEvent e) {
			double minBin = _gui.getMinElevBin();
			double maxBin = _gui.getMaxElevBin();
			int numBins = _gui.getNumElevBins();
			String names[] = new String[3];
			String initValues[] = new String[3];
			names[0] = "First Elevation Bin Value";
			names[1] = "Last Elevation Bin Value";
			names[2] = "Number of Bins";
			boolean[] disableIfNull = new boolean[] {true, true, true};
			initValues[0] = String.valueOf(minBin);
			initValues[1] = String.valueOf(maxBin);
			initValues[2] = String.valueOf(numBins);
			int[] dataType = new int[] {DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.NUMERIC_TYPE};
			int[] numDecimalPlaces = new int[] {0,0,0};
		
			String instructions = "<HTML><BODY><B>First Elevation Bin Value</B>: the elevation value to use for the first bin for coloring points.<BR>"
					+ "<B>Last Elevation Bin value:</B> the elevation value to use for the last bin for coloring points.<BR>"
					+ "<B>Number of Bins:</B> the number of bins";
			
			String[] tooltips = new String[] {
					"the elevation value to use for the first bin for coloring points.",
					"the elevation value to use for the last bin for coloring points.",
					"Number of Bins: the number of bins"
			};
		
			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, "Change Elevation Bin Values", instructions, names, initValues, 
					dataType, disableIfNull, numDecimalPlaces, tooltips, true);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String newMinBinString = dataEntryDialog.getValue(names[0]);
				String newMaxBinString = dataEntryDialog.getValue(names[1]);
				String newNumBinsString = dataEntryDialog.getValue(names[2]);
				double newMinBin = (double) (Double.parseDouble(newMinBinString));
				double newMaxBin = (double) (Double.parseDouble(newMaxBinString));
				int newNumBins = (int) (Double.parseDouble(newNumBinsString));
	
				if (newMinBin != minBin || newMaxBin != maxBin || newNumBins != numBins) {
					_gui.updateElevBinValues(newMinBin, newMaxBin, newNumBins);
					_gui.updateColorLegend();
					_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
					_gui.getPlanViewCanvas(0).repaint();
				} // if anything changed, repaint
			}
		}// actionPerformed
	} // Class DElevBins

	/**
	 * Originally intended to load and display USGS Digital Line Graph Files.
	 * These files are available at
	 * http://bard.wr.usgs.gov/htmldir/dlg_html/hypso.html
	 *
	 * For now, it's designed to load the file I got from another agency...
	 *
	 * They are UTM zone 10 NAD 27, meters. Only lines will be read--nodes and
	 * areas (if any will be ignored. Eventually should have selector to
	 * select/unselect multiple files to load. Then should be able to save as
	 * binary file(s).
	 *
	 * @author
	 * @version $Id: DisplayMenu.java,v 1.3 2003/04/15 19:28:01 btom Exp $
	 */
	public class DDigitalLineGraph extends FileIO implements ActionListener {
		CsdpFrame _gui;

		public DDigitalLineGraph(CsdpFrame gui) {
			super(gui, _openDLGDialogMessage, _openDLGErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openDLGExtensions, _numOpenDLGExtensions);
			_gui = gui;
			_jfc.setDialogTitle(_openDLGDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_DLGOpenFilter);
			_jfc.setFileFilter(_DLGOpenFilter);
		}

		/**
		 * Called by superclass. Uses dialog box to get filename from user.
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getDigitalLineGraphDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getDigitalLineGraphDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println("no file selected");
			_filechooserState = _jfc.showOpenDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions
						.setDigitalLineGraphDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}

		/**
		 * open dlg file
		 */
		public boolean accessFile() {
			if (_cancel == false) {
				readFile();
			}
			_cancel = false;
			return true; // no need to tell user if failed.
		}

		protected void readFile() {
			_dlg = _app.digitalLineGraphReadStore(CsdpFunctions.getDigitalLineGraphDirectory().getPath(),
					_filename + "." + _filetype);
			_gui.setDigitalLineGraph(_dlg);
			_dlgPlot = _app.setDigitalLineGraphPlotter();

			_gui.getPlanViewCanvas(0).setDigitalLineGraphPlotter(_dlgPlot);
			_gui.getPlanViewCanvas(0).setUpdateDigitalLineGraph(true);
			// removed for conversion to swing
			// _gui.getPlanViewCanvas(0).repaint();

			// _gui.enableAfterDigitalLineGraph();
		}

	}// class DDigitalLineGraph

	public class ClearDigitalLineGraph implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			_app.clearChannelOutlines();
		}
	}//class ClearDigitalLineGraph

	
	// /**
	// * Loads and displays landmarks
	// *
	// * @author
	// * @version $Id: DisplayMenu.java,v 1.3 2003/04/15 19:28:01 btom Exp $
	// */
	// public class DLandmarks extends FileIO implements ActionListener {
	// CsdpFrame _gui;
	// public DLandmarks(CsdpFrame gui){
	// super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage,
	// _openFailureMessage, false, _openExtensions, _numOpenExtensions);
	// _gui = (CsdpFrame)gui;
	// _ynd = new YesNoDialog(_gui,"A landmark file is already loaded.
	// Replace?",true);
	// _jfc.setDialogTitle(_openDialogMessage);
	// _jfc.setApproveButtonText("Open");
	// _jfc.addChoosableFileFilter(_lOpenFilter);
	// _jfc.setFileFilter(_lOpenFilter);
	// }

	// /**
	// * not needed here. Use if you want to save something before continuing.
	// */
	// public void warnUserIfNecessary(){
	// }//warnUserIfNecessary

	// /**
	// * uses dialog box to get filename from user
	// */
	// protected String getFilename(){
	// int numLines=0;
	// String filename=null;
	// if(CsdpFunctions.getLandmarkDirectory() != null){
	// _jfc.setCurrentDirectory(CsdpFunctions.getLandmarkDirectory());
	// }
	// else if(CsdpFunctions.getOpenDirectory() != null){
	// _jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
	// }
	// else System.out.println("no file selected");

	// _filechooserState = _jfc.showOpenDialog(_gui);
	// if(_filechooserState==JFileChooser.APPROVE_OPTION){
	// filename = _jfc.getName(_jfc.getSelectedFile());
	// CsdpFunctions.setLandmarkDirectory
	// (_jfc.getCurrentDirectory().getAbsolutePath()+File.separator);
	// CsdpFunctions.setOpenDirectory
	// (_jfc.getCurrentDirectory().getAbsolutePath()+File.separator);
	// parseFilename(filename);
	// }else if(_filechooserState == JFileChooser.CANCEL_OPTION){
	// _cancel=true;
	// filename = null;
	// }else{
	// filename = null;
	// }
	// return filename;
	// }//getFilename

	// /**
	// * open landmark file
	// */
	// public boolean accessFile(){
	// if(_app._landmark != null){
	// _ynd.setVisible(true);
	// if(_ynd._yes == true){
	// readFile();
	// }//if
	// }//if
	// else if(_cancel == false){
	// readFile();
	// }//else if
	// _cancel = false;
	// return true; //no need to tell user if failed.
	// }//accessFile

	// protected void readFile(){
	// _landmark =
	// _app.lReadStore(CsdpFunctions.getLandmarkDirectory().getPath(),
	// _filename+"."+_filetype);
	// _gui.setLandmark(_landmark);
	// _lplot = _app.setLandmarkPlotter();

	// _gui.getPlanViewCanvas(0).setLandmarkPlotter(_lplot);
	// _gui.getPlanViewCanvas(0).setUpdateLandmark(true);
	// //removed for conversion to swing
	// // _gui.getPlanViewCanvas(0).repaint();

	// _gui.enableAfterLandmark();
	// }

	// YesNoDialog _ynd;
	// } // Class DLandmarks

	// /**
	// * clears landmark.
	// */
	// public class DClearLandmark implements ActionListener{
	// CsdpFrame _gui;
	// public DClearLandmark(CsdpFrame gui){
	// _gui = gui;
	// }
	// public void actionPerformed(ActionEvent e){
	// _app.clearLandmark();
	// }
	// }//class DClearLandmark

	/**
	 * filters display of data by source
	 *
	 * @author
	 * @version
	 */
	public class DSource implements ActionListener {

		BathymetryData _data = null;
		CsdpFrame _gui;

		public DSource(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {

			_data = _app.getBathymetryData();
			int numSources = _data.getNumSources();
			ResizableStringArray source = new ResizableStringArray(numSources, 1);

			for (int i = 0; i <= numSources - 1; i++) {
				source.put(i, _data.getSource(i));
				_sourceInitState.put(i, _data.getPlotSource(i));
			}
			DataFilterCheckbox d = new DataFilterCheckbox(_gui, "Select source(s) to be plotted", true, source,
					_sourceInitState, numSources);
			d.setVisible(true);
			JCheckBox checkbox = null;
			boolean changed = false;
			for (int i = 0; i <= numSources - 1; i++) {
				checkbox = (JCheckBox) (d._checkboxes.get(d._names.get(i)));
				if (checkbox.isSelected() != _data.getPlotSource(i)) {
					_data.putPlotSource(i, checkbox.isSelected());
					changed = true;
				}
			}
			if (changed) {
				_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
				// removed for conversion to swing--then put back because it
				// wasn't redrawing.
				_gui.getPlanViewCanvas(0).repaint();
			}
		}// actionPerformed
	}// DSource

	/**
	 * filters display of data by year
	 *
	 * @author
	 * @version
	 */
	public class DYear implements ActionListener {
		BathymetryData _data = null;
		CsdpFrame _gui;

		public DYear(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {

			_data = _app.getBathymetryData();
			int numYears = _data.getNumYears();
			ResizableStringArray year = new ResizableStringArray(numYears, 1);
			Integer yearObject = null;
			String yearString = null;
			for (int i = 0; i <= numYears - 1; i++) {
				// yearObject = new Integer(_data.getYear(i));
				yearString = Integer.toString(_data.getYear(i));
				year.put(i, yearString);
				_yearInitState.put(i, _data.getPlotYear(i));
			}
			DataFilterCheckbox d = new DataFilterCheckbox(_gui, "Select year(s) to be plotted", true, year,
					_yearInitState, numYears);
			d.setVisible(true);
			JCheckBox checkbox = null;
			boolean changed = false;
			for (int i = 0; i <= numYears - 1; i++) {
				checkbox = (JCheckBox) (d._checkboxes.get(d._names.get(i)));
				if (checkbox.isSelected() != _data.getPlotYear(i)) {
					_data.putPlotYear(i, checkbox.isSelected());
					changed = true;
				}
			}
			if (changed) {
				_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
				// removed for conversion to swing
				// then put back because it wasn't redrawing
				_gui.getPlanViewCanvas(0).repaint();
			}
		}// actionPerformed
	}// DYear

	/**
	 * Make all bathymetry points the same color
	 */
	public class DColorUniform implements ItemListener {
		CsdpFrame _gui;

		public DColorUniform(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
				// _gui.setAllColorByFalse();
				// _gui.setColorUniform();
				_gui.getPlanViewCanvas(0).setChangeZoom(false);
				_gui.getPlanViewCanvas(0).setChangePan(false);
				_gui.updateColorLegend();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
	
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();

			}
		}
	}

	/**
	 * Color points by depth
	 */
	public class DColorByElev implements ItemListener {
		CsdpFrame _gui;

		public DColorByElev(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
	
				// _gui.setAllColorByFalse();
				// _gui.setColorByDepth();
				_gui.getPlanViewCanvas(0).setChangeZoom(false);
				_gui.getPlanViewCanvas(0).setChangePan(false);
				_gui.updateColorLegend();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();
			}
		}
	}

	/**
	 * Color points by group (source of data)
	 */
	public class DColorBySource implements ItemListener {
		CsdpFrame _gui;

		public DColorBySource(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
	
				// _gui.setAllColorByFalse();
				// _gui.setColorBySource();
				_gui.getPlanViewCanvas(0).setChangeZoom(false);
				_gui.getPlanViewCanvas(0).setChangePan(false);
				_gui.updateColorLegend();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();
			}
		}
	}

	/**
	 * Color points by year
	 */
	public class DColorByYear implements ItemListener {
		CsdpFrame _gui;

		public DColorByYear(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
	
				// _gui.setAllColorByFalse();
				// _gui.setColorByYear();
				_gui.getPlanViewCanvas(0).setChangeZoom(false);
				_gui.getPlanViewCanvas(0).setChangePan(false);
				_gui.updateColorLegend();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				_gui.getPlanViewCanvas(0).redoNextPaint();
				_gui.getPlanViewCanvas(0).repaint();
			}
		}
	}

	/**
	 * fit by bathymetry
	 */
	public class DFitByBathymetry implements ItemListener {
		CsdpFrame _gui;

		public DFitByBathymetry(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if (_gui.getFitByBathymetryOption() != true) {
				// _gui.setAllFitByFalse();
				// _gui.setFitByBathymetry();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				_gui.getPlanViewCanvas(0).setUpdateLandmark(true);
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();
			} // if
		}// itemStateChanged
	}// class DFitByBathymetry

	/**
	 * fit by network
	 */
	public class DFitByNetwork implements ItemListener {
		CsdpFrame _gui;

		public DFitByNetwork(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if (_gui.getFitByNetworkOption() != true) {
				// _gui.setAllFitByFalse();
				// _gui.setFitByNetwork();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				_gui.getPlanViewCanvas(0).setUpdateLandmark(true);
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();
			} // if
		}// itemStateChanged
	}// class DFitByNetwork

	/**
	 * fit by Landmark
	 */
	public class DFitByLandmark implements ItemListener {
		CsdpFrame _gui;

		public DFitByLandmark(CsdpFrame gui) {
			_gui = gui;
		}

		public void itemStateChanged(ItemEvent e) {
			if (_gui.getFitByLandmarkOption() != true) {
				// _gui.setAllFitByFalse();
				// _gui.setFitByLandmark();
				_gui.getPlanViewCanvas(0).setUpdateBathymetry(true);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				_gui.getPlanViewCanvas(0).setUpdateLandmark(true);
				// removed for conversion to swing
				// _gui.getPlanViewCanvas(0).repaint();
			} // if
		}// itemStateChanged
	}// class DFitByLandmark

	App _app;
	BathymetryPlot _plot;
	Network _net;
	Landmark _landmark;
	LandmarkPlot _lplot;
	DigitalLineGraph _dlg;
	DigitalLineGraphPlot _dlgPlot;
	ResizableBooleanArray _sourceInitState = new ResizableBooleanArray();
	ResizableBooleanArray _yearInitState = new ResizableBooleanArray();
	// protected boolean _cancel = false;
	protected static final boolean DEBUG = false;
	protected static final String _openDialogMessage = "Select landmark(.cdl) file";
	protected static final String _openErrorMessage = "Only .cdl extension allowed";
	protected static final String[] _openExtensions = { "cdl" };
	protected static final int _numOpenExtensions = 1;

	protected static final String _openDLGDialogMessage = "Select Channel Outline (.cdo) file";
	protected static final String _openDLGErrorMessage = "Only .cdo extension allowed";
	protected static final String[] _openDLGExtensions = { "cdo" };
	protected static final int _numOpenDLGExtensions = 1;

	// protected static final String _saveDialogMessage = "Save Network(.cdl)
	// file";
	// protected static final String _saveErrorMessage = "Only .cdl extension
	// allowed";
	// protected static final String[] _saveExtensions = {"cdl"};
	// protected static final int _numSaveExtensions = 1;

	// protected static final String _saveSuccessMessage = "saved file";
	protected static final String _openSuccessMessage = "";
	// protected static final String _saveFailureMessage = "ERROR: couldn't save
	// file";
	protected static final String _openFailureMessage = "ERROR:  couldn't open file";
	protected CsdpFileFilter _lOpenFilter;
	protected CsdpFileFilter _DLGOpenFilter;
	int _filechooserState = -Integer.MAX_VALUE;
}// class ZoomMenu
