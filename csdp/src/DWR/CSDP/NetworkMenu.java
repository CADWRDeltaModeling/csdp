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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import DWR.CSDP.dialog.CenterlineSummaryWindow;
import DWR.CSDP.dialog.DataEntryDialog;
import DWR.CSDP.dialog.DialogLegendFactory;
import DWR.CSDP.dialog.FileIO;
import DWR.CSDP.dialog.FileSave;

public class NetworkMenu {

	public NetworkMenu(App app) {
		_app = app;
		_nOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_nSaveFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
		_nExportToWKTFilter = new CsdpFileFilter(_wktExtensions, _numWKTExtensions);
		_nExportFilter = new CsdpFileFilter(_exportExtensions, _numExportExtensions);
		_3dNExportFilter = new CsdpFileFilter(_3dExportExtensions, _3dNumExportExtensions);
	}

	/**
	 * Create new file
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NOpen extends FileIO implements ActionListener {
		NetworkPlot _nplot;
		Network _net;

		public NOpen(CsdpFrame gui) {
			super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openExtensions, _numOpenExtensions);
			_jfc.setDialogTitle(_openDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_nOpenFilter);
			_jfc.setFileFilter(_nOpenFilter);
		}

		/**
		 * Option to save network before continuing.
		 */
		public void warnUserIfNecessary() {
			_net = ((CsdpFrame) _gui).getNetwork();
			if (_net != null) {
				if (_net.isUpdated()) {
					int response = JOptionPane.showConfirmDialog(_gui, "Network file is not saved.  Save(y/n)?", "save network?",
							JOptionPane.YES_NO_OPTION);
					if(response == JOptionPane.YES_OPTION)
						((CsdpFrame) _gui).saveNetwork();
				} // if network has changed
			} // if net isn't null
		}// warningNeeded

		/**
		 * uses dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;

			if (CsdpFunctions.getNetworkDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getNetworkDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println();
			_filechooserState = _jfc.showOpenDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setNetworkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				CsdpFunctions.setOpenDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
				CsdpFunctions.setNetworkFilename(_filename);
				CsdpFunctions.setNetworkFiletype(_filetype);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			}
			return filename;
		}// getFilename

		public boolean accessFile() {
			_net = _app.nReadStore(_gui, CsdpFunctions.getNetworkDirectory().getPath(),
					CsdpFunctions.getNetworkFilename() + "." + CsdpFunctions.getNetworkFiletype());
			((CsdpFrame) _gui).setNetwork(_net);
			_nplot = _app.setNetworkPlotter();
			((CsdpFrame) _gui).getPlanViewCanvas(0).setNetworkPlotter(_nplot);
			((CsdpFrame) _gui).getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			// _gui.getPlanViewCanvas(0).repaint();

			((CsdpFrame) _gui).enableAfterNetwork();
			return true; // no need to warn if it fails
		}// accessFile

	} // NOpen

	/**
	 * clear network from memory
	 */
	public class NClearNetwork implements ActionListener {
		CsdpFrame _gui;

		public NClearNetwork(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			Network net = _gui.getNetwork();
			if (net != null) {
				if (net.isUpdated()) {
					int response = JOptionPane.showConfirmDialog(_gui, "Network file is not saved. Save(y/n)?", "save network?",
							JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						_gui.saveNetwork();
						_app.clearNetwork();
					}else if(response == JOptionPane.NO_OPTION) {
						_app.clearNetwork();
					} else {
						// do nothing
					}
				} // if network has changed
				else if (net.isUpdated() == false) {
					_app.clearNetwork();
				} // else
			} // if net isn't null
			else {
				_app.clearNetwork();
			} // else clear if network null
		}
	}

	/**
	 * Save network file
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NSave extends FileSave implements ActionListener {

		public NSave(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
		}

		public String getCurrentFilename() {
			return CsdpFunctions.getNetworkFilename();
		}

		public String getCurrentFiletype() {
			return CsdpFunctions.getNetworkFiletype();
		}

		public void setFilenameAndType(String filename, String filetype) {
			CsdpFunctions.setNetworkFilename(filename);
			CsdpFunctions.setNetworkFiletype(filetype);
		}

		public String getFilename() {
			String filename = CsdpFunctions.getNetworkFilename() + "." + CsdpFunctions.getNetworkFiletype();
			parseFilename(filename);
			return filename;
		}// getFilename

		public boolean accessFile() {
			return _app.nSave();
		}

		public boolean accessFile(String filename) {
			return _app.nSaveAs(CsdpFunctions.getNetworkDirectory().getPath(), filename);
		}
	}// NSave

	/**
	 * Save network file As
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NSaveAs extends FileIO implements ActionListener {
		public NSaveAs(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, true,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Save");
			_jfc.addChoosableFileFilter(_nSaveFilter);
			_jfc.setFileFilter(_nSaveFilter);
		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getNetworkDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getNetworkDirectory());
			}

			_filechooserState = _jfc.showSaveDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setNetworkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
				_cancel = false;
				CsdpFunctions._cancelSaveNetwork = false;
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				CsdpFunctions._cancelSaveNetwork = true;
				filename = null;
			} else {
				_cancel = true;
				CsdpFunctions._cancelSaveNetwork = true;
				filename = null;
			} // if
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean saved = false;
			if (_cancel == false) {
				saved = _app.nSaveAs(CsdpFunctions.getNetworkDirectory().getPath(), _filename + "." + _filetype);
				((CsdpFrame) _gui).enableAfterNetwork();
			} else {
				saved = false;
			}
			return saved;
		}

	} // NSaveAs

	public class NExportToWKTFormat implements ActionListener {
		private CsdpFrame gui;

		public NExportToWKTFormat(CsdpFrame gui) {
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent arg0) {
			boolean saved = false;
			
			String title = "Export Network to WKT for importing into GIS";
			String instructions = "<HTML><BODY>1. Specify a *.wkt filename.<BR>"
					+ "2. Check the box if you want the results to be identified as POLYGON objects. Default is LINESTRING.<BR><BR>"
					+ "</BODY></HTML>";
			String[] names = new String[] {"WKT filename", "Create POLYGON objects?"};
			String[] defaultValues = new String[]{"", "true"};
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			boolean[] disableIfNull = new boolean[] {true, false};
			int[] numDecimalPlaces = new int[] {0,0};
			String[] extensions = new String[] {"wkt", ""};
			String[] tooltips = new String[] {"The full path to the .wkt file to be created", 
					"if selected, create POLYGON objects. If not, create LINESTRING objects"};
			boolean modal = true;
			
			DataEntryDialog dataEntryDialog = new DataEntryDialog(this.gui, title, instructions, names, 
					defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
					extensions, tooltips, modal);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				System.out.println("ok clicked");
				Network net = gui.getNetwork();
				String wktPath = dataEntryDialog.getDirectory(names[0])+File.separator+dataEntryDialog.getFilename(names[0]);
				String createPolygonObjectsString = dataEntryDialog.getValue(names[1]);
				boolean createPolygonObjects = true;
				if(createPolygonObjectsString.equalsIgnoreCase("true")) {
					createPolygonObjects = true;
				}else {
					createPolygonObjects = false;
				}
				boolean success = _app.nExportToWKT(net, wktPath, createPolygonObjects);
				if(success) {
					JOptionPane.showMessageDialog(gui, "Export to wkt complete.", "Success", JOptionPane.OK_OPTION);
				}else {
					JOptionPane.showMessageDialog(gui, "Export to wkt failed.", "Failure", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				System.out.println("export to wkt canceled.");
			}
			
		}

//		/**
//		 * uses a dialog box to get filename from user
//		 */
//		protected String getFilename() {
//			int numLines = 0;
//			String filename = null;
//			if (CsdpFunctions.getNetworkDirectory() != null) {
//				_jfc.setCurrentDirectory(CsdpFunctions.getNetworkDirectory());
//			}
//
//			_filechooserState = _jfc.showSaveDialog(_gui);
//			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
//				filename = _jfc.getName(_jfc.getSelectedFile());
//				CsdpFunctions.setNetworkDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
//				parseFilename(filename);
//				_cancel = false;
//				CsdpFunctions._cancelSaveNetwork = false;
//			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
//				_cancel = true;
//				CsdpFunctions._cancelSaveNetwork = true;
//				filename = null;
//			} else {
//				_cancel = true;
//				CsdpFunctions._cancelSaveNetwork = true;
//				filename = null;
//			} // if
//			return filename;
//		}// getFilename
	}//class NExportToWKTFormat

	/**
	 * Save network file As
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NSaveSpecifiedChannelsAs implements ActionListener {
		CsdpFrame _csdpFrame;
		public NSaveSpecifiedChannelsAs(CsdpFrame gui) {
			_csdpFrame = gui;
		}

		public void actionPerformed(ActionEvent e) {
			//enter channel numbers
			String[] defaultValues = new String[] {"", "", "false"};
			int[] types = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.STRING_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			String instructions = "<HTML><BODY><B>Save Specified Channels</B><BR><BR>"
					+ "Click the button below to specify a <B>network file name</B> for writing results. If file exists, it will be overwritten.<BR>"
					+ "Enter a <B>list of channel numbers</B> separated by commas, spaces, or tabs. To export all polygon"
					+ "centerlines (for GIS volume calculation), you may use an asterisk preceded by a '.' as a wildcard. Example: '.*_chanpoly'<BR>"
					+ "Leave the <B>box</B> unchecked if you wish to export ONLY the specified numbers.<BR>"
					+ "Check the <B>box</B> if you wish to export all channels EXCEPT the specified numbers.</BODY></HTML>";
			String title = "Write specified centerlines to a network file.";
			String[] tooltips = new String[] {"Specify a network filename for output. If file exists, will overwrite.", 
					"Enter a list of channel numbers. Can be space, tab, or comma delimited", 
			"Check box if you want to export all channels EXCEPT those with the specified numbers."};
			boolean[] disableIfNull = new boolean[] {true, true, false};
			String[] extensions = new String[]{"cdn", null, null};
			String[] names = new String[] {"New Network Filename", "Channel Numbers", "Don't export specified channels"};

			DataEntryDialog exportChannelsDialog = new DataEntryDialog(_csdpFrame, title, instructions, names, defaultValues, 
					types, disableIfNull, extensions, tooltips, true);

			int response=exportChannelsDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				File newNetworkDirectory = exportChannelsDialog.getDirectory(names[0]);
				String newNetworkFilename = exportChannelsDialog.getFilename(names[0]);

				String channelNumbers = exportChannelsDialog.getValue(names[1]);
				String dontExportSpecifiedChannelsString = exportChannelsDialog.getValue(names[2]);
				boolean dontExportSpecifiedChannels = false;
				if(dontExportSpecifiedChannelsString.equalsIgnoreCase("true")) {
					dontExportSpecifiedChannels = true;
				}else if(dontExportSpecifiedChannelsString.equalsIgnoreCase("false")) {
					dontExportSpecifiedChannels = false;
				}else {
					dontExportSpecifiedChannels = false;
				}

				HashSet<String> channelNumbersHashSet = null;
				System.out.println("Channel numbers="+channelNumbers);
				if(channelNumbers.length() >0) {
					channelNumbersHashSet = new HashSet<String>();
					String[] parts = channelNumbers.split(" |,|\t");
					Network network = _csdpFrame.getNetwork();
					if(dontExportSpecifiedChannels) {
						for(int i=0; i<network.getNumCenterlines(); i++) {
							String centerlineName = network.getCenterlineName(i);
							boolean exportCenterline = true;
							for(int j=0; j<parts.length; j++) {
								if(centerlineName.equals(parts[j])) {
									exportCenterline = false;
									break;
								}
							}
							if(exportCenterline) {
								channelNumbersHashSet.add(centerlineName);
							}
						}
					}else {
						for(int i=0; i<parts.length; i++) {
							//if user enters something like .*_chanpoly
							if(parts[i].indexOf("*")>=0) {
								Pattern pattern = Pattern.compile(parts[i]);
								for(int j=0; j<network.getNumCenterlines(); j++) {
									String centerlineName = network.getCenterlineName(j);
									Matcher matcher = pattern.matcher(centerlineName);
									if(matcher.matches()) {
										channelNumbersHashSet.add(centerlineName);
									}
								}
							}else {
								if(network.centerlineExists(parts[i])) {
									channelNumbersHashSet.add(parts[i]);
								}
							}
						}
					}
					_app.nSaveSpecifiedChannelsAs(newNetworkDirectory.toString(), newNetworkFilename, channelNumbersHashSet);
					_csdpFrame.enableAfterNetwork();
				}
			}
		}
	} // NSaveAs

	/**
	 * saves all cross-sections in network file in HEC-2 format. Name of each
	 * cross-section will be channel number + distance along centerline
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NExportToSEFormat extends FileIO implements ActionListener {
		public NExportToSEFormat(CsdpFrame gui) {
			super(gui, _exportDialogMessage, _exportErrorMessage, _exportSuccessMessage, _exportFailureMessage, true,
					_exportExtensions, _numExportExtensions);
			_jfc.setDialogTitle(_exportDialogMessage);
			_jfc.setApproveButtonText("Export");
			_jfc.addChoosableFileFilter(_nExportFilter);
			_jfc.setFileFilter(_nExportFilter);
		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getNetworkExportDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getNetworkExportDirectory());
			}

			_filechooserState = _jfc.showSaveDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setNetworkExportDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			} // if
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean saved = _app.nExportToSEFormat(CsdpFunctions.getNetworkExportDirectory().getPath(),
					_filename + "." + _filetype, CsdpFunctions.getChannelLengthsOnly());
			((CsdpFrame) _gui).enableAfterNetwork();
			return saved;
		}

	} // NExportToSEFormat

	/**
	 * saves all cross-sections in network file in 3D format.. Name of each
	 * cross-section will be channel number + distance along centerline
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NExportTo3DFormat extends FileIO implements ActionListener {
		public NExportTo3DFormat(CsdpFrame gui) {
			super(gui, _3dExportDialogMessage, _3dExportErrorMessage, _3dExportSuccessMessage, _3dExportFailureMessage,
					true, _3dExportExtensions, _3dNumExportExtensions);
			_jfc.setDialogTitle(_3dExportDialogMessage);
			_jfc.setApproveButtonText("Export");
			_jfc.addChoosableFileFilter(_3dNExportFilter);
			_jfc.setFileFilter(_3dNExportFilter);
		}

		/**
		 * uses a dialog box to get filename from user
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			if (CsdpFunctions.getNetworkExportDirectory() != null) {
				_jfc.setCurrentDirectory(CsdpFunctions.getNetworkExportDirectory());
			}

			_filechooserState = _jfc.showSaveDialog(_gui);
			if (_filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
				CsdpFunctions.setNetworkExportDirectory(_jfc.getCurrentDirectory().getAbsolutePath() + File.separator);
				parseFilename(filename);
			} else if (_filechooserState == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
				filename = null;
			} else {
				filename = null;
			} // if
			return filename;
		}// getFilename

		public boolean accessFile() {
			boolean saved = _app.nExportTo3DFormat(CsdpFunctions.getNetworkExportDirectory().getPath(),
					_filename + "." + _filetype);
			((CsdpFrame) _gui).enableAfterNetwork();
			return saved;
		}

	} // NExportTo3DFormat

	/**
	 * Changes option to print only channel lengths when exporting network data
	 * to station/elevation format
	 */
	public class NChannelLengthsOnly implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setChannelLengthsOnly(true);
			} else {
				CsdpFunctions.setChannelLengthsOnly(false);
			}
		}

	}// NChannelLengthsOnly

	/**
	 * Clear network from memory
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	/// public class NClear implements ActionListener {
	/// App _app;
	/// CsdpFrame _gui;
	/// NetworkPlot _plot;
	/// public NClear(App app, CsdpFrame gui) {
	/// _app = app;
	/// _gui = gui;
	/// }
	/// public void actionPerformed(ActionEvent e) {
	/// }
	/// } // NClear

	public class NShowNetworkColorLegend implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			Color[] colors = new Color[] {
					NetworkPlot.NO_POINTS_COLOR, 
					NetworkPlot.NEG_DK_IN_INTERTIDAL_COLOR,
					NetworkPlot.DUPLICATE_STATIONS_COLOR, 
					NetworkPlot.EXCEEDS_MAX_AREA_RATIO_COLOR, 
					NetworkPlot.DUP_STN_AND_NEG_DK_IN_INTERTIDAL_COLOR, 
					NetworkPlot.EXCEEDS_MAX_AREA_RATIO_AND_NEG_DK_IN_INTERTIDAL_COLOR, 
					NetworkPlot.DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR,
					NetworkPlot.DUP_STN_AND_EXCEEDS_MAX_AREA_RATIO_COLOR_AND_NEG_DK_IN_INTERTIDAL_COLOR};
			String[] text = new String[] {
					"No Points",
					"-dK in Intertidal Zone",
					"Duplicate Station Values",
					"Exceeds Max Area Ratio",
					"Duplicate Station Values and -dK in Intertidal Zone",
					"Exceeds Max Area Ratio & -dK in Intertidal Zone",
					"Duplicate Station Values & Exceeds Max Area Ratio",
					"Duplicate Station Values & Exceeds Max Area Ratio & -dK in Intertidal Zone"
			};
			String title = "Network Color Legend";
			JPanel legendPanel = DialogLegendFactory.createLegendPanel(title, colors, text);
			JFrame legendFrame = new JFrame(title);
			legendFrame.setLayout(new BorderLayout());
			legendFrame.getContentPane().add(legendPanel, BorderLayout.NORTH);
			legendFrame.pack();
			legendFrame.validate();
			legendFrame.setVisible(true);
		}
	}//class NShowNetworkColorLegend

	public class NDisplayReachSummaryWindow implements ActionListener {

		private CsdpFrame gui;

		public NDisplayReachSummaryWindow(CsdpFrame gui) {
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent arg0) {
			Network net = gui.getNetwork();

			String names[] = new String[3];
			String initValue[] = new String[3];
			names[0] = "Reach Name";
			names[1] = "Channel Numbers";
			names[2] = "profile plots begin at downstream end";
			initValue[0] = "Reach";
			initValue[1] = "17,1-5";
			initValue[2] = "true";
			
			int[] dataTypes = new int[] {DataEntryDialog.STRING_TYPE, DataEntryDialog.STRING_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			String[] tooltips = new String[] {null, null, "If true, profile plots will begin at the downstream end of the reach, and "
					+ "end at the upstream end. Otherwise, they will do the opposite. "};
			boolean[] disableIfNull = new boolean[] {true, true, true};

			String instructions = 
					"<HTML><BODY><B>Display Reach Summary</B><BR>"
							+ "1. Enter a <B>reach name</B>. This will appear in graph and window titles.<BR><BR>"
							+ "2. Enter a string that identifies a <B>range of channels</B> that you would like to summarize. The string should only<BR> "
							+ "consist of numbers separated by commas or hyphens. A range of channel numbers can be specified using two numbers separated <BR>"
							+ "by a hyphen. Hyphen-separated values can be speficied in reverse order, to reverse the order of display. <BR>"
							+ "3. Check the checkbox if you want profile plots to be downstream to upstream.<BR>"
							+ "The example below indicates channels 1 through 5, followed by channel 10. This is equivalent to '1,2,3,4,5,10'<BR></font></BODY></HTML>";

//			int numFields = 3;
			DataEntryDialog dataEntryDialog = new DataEntryDialog(gui, "Reach Summary Information", instructions, 
					names, initValue, dataTypes, disableIfNull, tooltips, true);
			int response=dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String reachTitle = dataEntryDialog.getValue(names[0]);
				String channelNumbersString = dataEntryDialog.getValue(names[1]);
				boolean downstreamToUpstream = Boolean.parseBoolean(dataEntryDialog.getValue(names[2]));
				int downstreamToUpstreamInt = -Integer.MAX_VALUE;
				if(downstreamToUpstream) {
					downstreamToUpstreamInt = CenterlineSummaryWindow.START_AT_DOWNSTREAM_END;
				}else {
					downstreamToUpstreamInt = CenterlineSummaryWindow.START_AT_UPSTREAM_END;
				}
				new CenterlineSummaryWindow(gui, net, reachTitle, channelNumbersString, downstreamToUpstreamInt);
			}
		}
	}//inner class DisplayReachSummaryWindow

	/**
	 * Calculate network file: calculate cross-section properties for all cross-
	 * sections in network and write to individual ascii files.
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NCalculate implements ActionListener {
		CsdpFrame _gui; 

		public NCalculate(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressArrowButton();
			String instructions = "<HTML><BODY>"
					+ "Calculating the network means creating DSM2 input files using the currently loaded network file.<BR><BR>"
					+ "You must also specify a <B>DSM2 channels file</B> (i.e. 'channel_std_delta_grid_NAVD_20150129.inp'),<BR>"
					+ "and an <B>directory</B> in which you would like the DSM2 geometry files to be written<BR>"
					+ "Entering a value for Manning's n will replace all Manning's n values with the specfied value<BR>"
					+ "You may also create DSM2 geometry files in the pre-DSM2 v8 format. <BR>"
					+ "You may also create a CSDP landmark file called xsects.cdl, which labels all of the cross-section lines<BR>"
					+ "in the network<BR>"
					+ "A landmark file called xsects.cdl, which labels all cross-sections, will also be created in the <BR>"
					+ "specified output directory."
					+ "</BODY></HTML>";
			String[] names = new String[] {"DSM2 Channels File", "Output Directory", "Manning's n replacement", 
					"Create pre-DSM2 v8 files", "Create xsects.cdl file"};

			String defaultChannelsInp = "";
			File defaultDSMChannelsDirectoryFile = CsdpFunctions.getDSMChannelsDirectory();
			String defaultDSMChannelsDirectoryString = "";
			if(defaultDSMChannelsDirectoryFile!=null) {
				defaultDSMChannelsDirectoryString = defaultDSMChannelsDirectoryFile.toString();
			}
			String defaultDSMChannelsFilename = CsdpFunctions.getDSMChannelsFilename();
			if(defaultDSMChannelsDirectoryFile!=null && defaultDSMChannelsDirectoryString.length()>0 &&
					defaultDSMChannelsFilename!=null && defaultDSMChannelsFilename.length()>0) {
				defaultChannelsInp = defaultDSMChannelsDirectoryFile.toString()+File.separator+defaultDSMChannelsFilename;
			}
			String defaultNetworkCalculateDirectory = "";
			if(CsdpFunctions.getNetworkCalculateDirectory()!=null && 
					CsdpFunctions.getNetworkCalculateDirectory().toString().length()>0) {
				defaultNetworkCalculateDirectory=CsdpFunctions.getNetworkCalculateDirectory().toString();
			}

			String[] defaultValues = new String[] {defaultChannelsInp, defaultNetworkCalculateDirectory, "", "false", "false"};
			int[] dataTypes = new int[] {
					DataEntryDialog.FILE_SPECIFICATION_TYPE, 
					DataEntryDialog.DIRECTORY_SPECIFICATION_TYPE, 
					DataEntryDialog.NUMERIC_TYPE,
					DataEntryDialog.BOOLEAN_TYPE,
					DataEntryDialog.BOOLEAN_TYPE};
			boolean[] disableIfNull = new boolean[] {true, true, false, true, true};
			String[] extensions = new String[] {"inp", null, null, null, null};
			String[] tooltips = new String[] {
					"A DSM2 channels file, i.e. 'channel_std_delta_grid_NAVD_20150129.inp'",
					"The directory where you would like the DSM2 geometry files should be written",
					"Enter a value here if you would like to replace all Manning's n values with the specified value",
					"Create DSM2 geometry files in the pre-DSM2 v8 format",
					"Create a CSDP landmark file (xsects.cdl) which labels all of the cross-section lines in the network"
					};
			boolean modal = true;
			int[] numDecimalPlaces = new int[] {0,0,4,0,0};
			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, "Calculate Network", instructions, names, 
					defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
					extensions, tooltips, modal);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String channelsDirectory = dataEntryDialog.getDirectory(names[0]).toString();
				String channelsFilename = dataEntryDialog.getFilename(names[0]);
				String calculateDirectory = dataEntryDialog.getDirectory(names[1]).toString();
				calculateDirectory+=File.separator+dataEntryDialog.getFilename(names[1]);
				CsdpFunctions.setNetworkCalculateDirectory(new File(calculateDirectory));
				String manningsReplacementString = dataEntryDialog.getValue(names[2]);
				double manningsReplacementValue = -Double.MAX_VALUE;
				boolean replaceMannings = false;
				if(manningsReplacementString!=null && manningsReplacementString.length()>0) {
					manningsReplacementValue = Double.parseDouble(manningsReplacementString);
					replaceMannings = true;
				}
				boolean calculatePreDsm2V8Files = Boolean.valueOf(dataEntryDialog.getValue(names[3]));
				boolean createCrossSectionLandmarkFile = Boolean.valueOf(dataEntryDialog.getValue(names[4]));
				
				_app.nCalculateDSM2V8Format(channelsDirectory, channelsFilename, calculateDirectory+
						File.separator+"channel_std_delta_grid_from_CSDP_NAVD.inp", replaceMannings, 
						manningsReplacementValue);
				// The old calculations. Uncomment these lines to create files in the old format.
				if(calculatePreDsm2V8Files) {
					_app.nCalculate(calculateDirectory);
					_app.writeIrregularXsectsInp(calculateDirectory);
				}
				if(createCrossSectionLandmarkFile) {
					_app.writeXsectLandmark(calculateDirectory);
				}
				JOptionPane.showMessageDialog(_gui, "DSM2 input has been created", "Done", JOptionPane.INFORMATION_MESSAGE);
			}
		}// actionPerformed
	} // NCalculate

	// public class NAreaSummary implements ActionListener{
	// CsdpFrame _gui;
	// public NAWDSummary(CsdpFrame gui){
	// _gui = gui;
	// }

	// public void actionPerformed(ActionEvent e) {
	// _app.areaSummary();
	// }//actionPerformed

	// }

	/**
	 * Print Area, width, and depth for each cross-section
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NAWDSummaryReport implements ActionListener {
		CsdpFrame _gui;

		public NAWDSummaryReport(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressArrowButton();
			_app.awdSummary(0.0f);
			//			_app.awdSummary(0.292f); // MSL wrt NGVD
		}// actionPerformed
	} // NAWDSummary

	public class NNetworkSummaryReport implements ActionListener {
		private CsdpFrame _gui;
		public NNetworkSummaryReport(CsdpFrame gui) {
			_gui = gui;
		}
		public void actionPerformed(ActionEvent arg0) {
			_gui.pressArrowButton();
			_app.createNetworkSummaryReport();
		}

	}//inner class NNetworkSummaryReport


	/**
	 * Check all cross-sections for possible errors (zero area at elevation 0)
	 * and duplicate station values
	 *
	 * @author
	 * @version $Id: NetworkMenu.java,v 1.3 2003/04/15 19:46:14 btom Exp $
	 */
	public class NXSCheckReport implements ActionListener {
		CsdpFrame _gui;

		public NXSCheckReport(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressArrowButton();
			_app.xsCheck(_gui);
		}// actionPerformed
	} // NXSCheck

	public class NDConveyanceReport implements ActionListener {
		CsdpFrame _gui;
		public NDConveyanceReport(CsdpFrame gui) {
			_gui = gui;
		}
		public void actionPerformed(ActionEvent arg0) {
			_gui.pressArrowButton();
			_app.dConveyanceReport(_gui);
		}

	}

	//// KEEP THIS CLASS--IT MIGHT BE NEEDED SOMEDAY
	// // public class NetworkFilenameFilter implements FilenameFilter{
	// // public boolean accept(File dir, String name){
	// // boolean returnValue = false;
	// // parseFilename(name);
	// // if(_filetype.equals(NETWORK_FILETYPE)){
	// // if(DEBUG)System.out.println("match found");
	// // returnValue = true;
	// // }
	// // else returnValue = false;
	// // return returnValue;
	// // }
	// // }//class NetworkFilenameFilter

	App _app;
	protected static final boolean DEBUG = false;

	protected static final String _openDialogMessage = "Select network(.cdn) file";
	protected static final String _openErrorMessage = "Only .cdn extension allowed";
	protected static final String[] _openExtensions = { "cdn" };
	protected static final int _numOpenExtensions = 1;

	protected static final String _saveDialogMessage = "Save Network(.cdn) file";
	protected static final String _saveErrorMessage = "Only .cdn extension allowed";
	protected static final String[] _saveExtensions = { "cdn" };
	protected static final int _numSaveExtensions = 1;

	protected static final String _exportWKTDialogMessage = "Export network to WKT(.wkt) format";
	protected static final String _exportWKTErrorMessage = "Only .wkt extension allowed";
	protected static final String _exportWKTSuccessMessage = "Exported to .wkt";
	protected static final String _exportWKTFailureMessage = "Failed to export to .wkt!";
	protected static final String[] _wktExtensions = {"wkt"};
	protected static final int _numWKTExtensions = 1;

	protected static final String _saveSuccessMessage = "Saved network file";
	protected static final String _saveFailureMessage = "ERROR:  NETWORK FILE NOT SAVED!";
	protected static final String _openSuccessMessage = "";
	protected static final String _openFailureMessage = "ERROR:  couldn't open network file";

	protected static final String _exportDialogMessage = "Export Network to Station/Elevation format(.se) file";
	protected static final String _exportErrorMessage = "Only .se extension allowed";
	protected static final String[] _exportExtensions = { "se" };
	protected static final int _numExportExtensions = 1;

	protected static final String _exportSuccessMessage = "Exported network to station/elevation format";
	protected static final String _exportFailureMessage = "ERROR:   EXPORT FAILED!";

	protected static final String _3dExportDialogMessage = "Export Network to 3D format(.3dn) file";
	protected static final String _3dExportErrorMessage = "Only .3dn extension allowed";
	protected static final String[] _3dExportExtensions = { "3dn" };
	protected static final int _3dNumExportExtensions = 1;

	protected static final String _3dExportSuccessMessage = "Exported network to 3D format";
	protected static final String _3dExportFailureMessage = "ERROR:   EXPORT FAILED!";

	CsdpFileFilter _nOpenFilter;
	CsdpFileFilter _nSaveFilter;
	CsdpFileFilter _nExportToWKTFilter;
	CsdpFileFilter _nExportFilter;
	CsdpFileFilter _3dNExportFilter;

	int _filechooserState = -Integer.MAX_VALUE;

} // class NetworkMenu
