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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import DWR.CSDP.dialog.CenterlineSummaryWindow;

/**
 * calls methods for creating and editing centerlines
 *
 * @author
 * @version
 */
public class CenterlineMenu {

	/**
	 * Remove all cross-sections in centerline
	 * @author btom
	 *
	 */
	public class RemoveAllCrossSections implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_gui.getNetwork().getSelectedCenterline().removeAllCrossSections();
			_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			_gui.getPlanViewCanvas(0).redoNextPaint();
			_gui.getPlanViewCanvas(0).repaint();	
		}

	}
	public CenterlineMenu(CsdpFrame gui) {
		_gui = gui;
	}

	public void setLandmark(Landmark landmark) {
		_landmark = landmark;
	}

	public class DeleteCenterlinePointsInWindow implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_gui.pressDeleteCenterlinePointsInBoxButton();
		}
	}

	public class DeleteCenterlinePointsOutsideOfWindow implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_gui.pressDeleteCenterlinePointsOutsideBoxButton();
		}

	}

	
	/**
	 * return to cursor (turn modes off)
	 */
	public class CCursor implements ActionListener {
		CsdpFrame _gui;

		public CCursor(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setStopEditingMode();
			_gui.setCursor(CsdpFunctions._defaultCursor);
		}

	}// class CAddPoint

	/**
	 * Remove a centerline from the network.
	 *
	 * @author
	 * @version $Id:
	 */
	public class CRemove implements ActionListener {

		public CRemove(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressSelectCursorAkaArrowButton();
			_net = _gui.getNetwork();
			if (_net == null) {
				System.out.println("ERROR in CenterlineMenu.CRemove.actionPerformed: network is null!");
			} else {
				String cname = JOptionPane.showInputDialog(_gui, "Enter name of centerline to remove");
				// does specified centerline exist?
				if (_net.centerlineExists(cname)) {
					int response = JOptionPane.showConfirmDialog(_gui, "Remove Centerline "+cname+"?", "Are you sure?", JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						_net.removeCenterline(cname);
						_gui.getPlanViewCanvas(0).redoNextPaint();
						_gui.getPlanViewCanvas(0).repaint();
					}
				} else {
					// requested centerline doesn't exist
					JOptionPane.showMessageDialog(_gui, "requested centerline doesn't exist", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Draw new centerline--mouse clicks will add points
	 *
	 * @author
	 * @version $Id: CenterlineMenu.java,v 1.5 2003/07/22 22:23:11 btom Exp $
	 */
	public class CCreate implements ActionListener {

		/**
		 * assign instances of application and gui classes to class variables
		 */
		public CCreate(App app, CsdpFrame gui) {
			_app = app;
			_gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressSelectCursorAkaArrowButton();
			_net = _gui.getNetwork();
			if (_net == null) {
				_net = new Network("delta", _gui);
				_gui.setNetwork(_net);
				_app._net = _net;
				_nplot = _app.setNetworkPlotter();
				_gui.getPlanViewCanvas(0).setNetworkPlotter(_nplot);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				// removed for conversion to swing
				_gui.getPlanViewCanvas(0).redoNextPaint();
				_gui.getPlanViewCanvas(0).repaint();

				// _gui.enableAfterNetwork();
				_gui.enableWhenNetworkExists();
			} // if net is null

			String centerlineName = null;
			centerlineName = JOptionPane.showInputDialog(_gui, "Enter new centerline name");

			if (centerlineName.length() > 0) {
				if (_net.getCenterline(centerlineName) != null) {
					int response = JOptionPane.showConfirmDialog(_gui, "Centerline " + centerlineName + " already exists.  Replace?", 
							"Centerline name exists", JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						addCenterline(centerlineName);
					}
				} else {
					addCenterline(centerlineName);
				}
			} // if centerline name is not blank
		}// actionPerformed

		protected void addCenterline(String centerlineName) {
			_net.addCenterline(centerlineName);
			_net.setSelectedCenterlineName(centerlineName);
			_net.setSelectedCenterline(_net.getCenterline(centerlineName));
			_gui.enableAfterCenterlineSelected();
			_gui.setAddDownstreamPointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CCreate

	/**
	 * Create new centerline with 2 points. User will specify DSM channel number
	 * and the new centerline will have its first point at the upstream node and
	 * the second point at the downstream node.
	 *
	 * @author
	 * @version
	 */
	public class CDSMCreate implements ActionListener {
		/**
		 * assign instances of application and gui classes to class variables
		 */
		public CDSMCreate(App app, CsdpFrame gui) {
			
			_app = app;
			_gui = gui;
			_jfcChannelsInp = new JFileChooser();
			_channelsInpFilter = new CsdpFileFilter(_channelsInpExtensions, _numChannelsInpExtensions);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressSelectCursorAkaArrowButton();
			_DSMChannels = _app.getDSMChannels();
			_gui.setDefaultModesStates();
			_net = _gui.getNetwork();
			if (_net == null) {
				_net = new Network("delta", _gui);
				_gui.setNetwork(_net);
				_app._net = _net;
				_nplot = _app.setNetworkPlotter();
				_gui.getPlanViewCanvas(0).setNetworkPlotter(_nplot);
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				// removed for conversion to swing
				_gui.getPlanViewCanvas(0).redoNextPaint();
				_gui.getPlanViewCanvas(0).repaint();

				// _gui.enableAfterNetwork();
				_gui.enableWhenNetworkExists();
			} // if net is null

			String centerlineName = JOptionPane.showInputDialog(_gui, "Enter a new DSM2 channel number");
			boolean loadAnotherChannelsInpFile = true;

			// if channels.inp file not loaded OR if channel # doesn't exist in
			// current
			// DSMChannels object. ask user if another file should be
			// loaded--don't
			// assume there is another file with the channel.
			while (loadAnotherChannelsInpFile) {
				if (_DSMChannels != null && _DSMChannels.channelExists(centerlineName) == false) {
					int response = JOptionPane.showConfirmDialog(_gui, "Channel " + centerlineName
							+ " not found in channel connectivity file.  Load another file?", "Channel not found", JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						loadAnotherChannelsInpFile = true;
					} else {
						loadAnotherChannelsInpFile = false;
					}
				}else {
					loadAnotherChannelsInpFile = false;
				}

				if (_DSMChannels == null || loadAnotherChannelsInpFile) {
					String channelsFilename = null;
					// FileDialog fd = new FileDialog(_gui, "Open DSM2 channel
					// connectivity file");
					// fd.setVisible(true);
					_jfcChannelsInp.setDialogTitle("Open DSM2 channel connectivity file");
					_jfcChannelsInp.setApproveButtonText("Open");
					_jfcChannelsInp.addChoosableFileFilter(_channelsInpFilter);
					_jfcChannelsInp.setFileFilter(_channelsInpFilter);

					if (CsdpFunctions.getOpenDirectory() != null) {
						_jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
					}
					_filechooserState = _jfcChannelsInp.showOpenDialog(_gui);
					if (_filechooserState == JFileChooser.APPROVE_OPTION) {
						channelsFilename = _jfcChannelsInp.getName(_jfcChannelsInp.getSelectedFile());
						_directory = _jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;

						// channelsFilename = fd.getFile();
						// _directory = fd.getDirectory();
						
						_gui.setCursor(_waitCursor);
						try {
							_DSMChannels = _app.chanReadStore(_directory, channelsFilename);
//							_gui.setDSMChannels(_DSMChannels);
						}catch(Exception e1) {
							JOptionPane.showMessageDialog(_gui, "Error creating DSM2 channel", "Error", JOptionPane.ERROR_MESSAGE);
						}finally {
							_gui.setCursor(_defaultCursor);
							
						}
					} else {
						loadAnotherChannelsInpFile = false;
					}
				} // if DSMChannels is null

				if (_filechooserState == JFileChooser.APPROVE_OPTION) {
					if (_net.getCenterline(centerlineName) != null) {
						int response = JOptionPane.showConfirmDialog(_gui, "Centerline " + centerlineName + " already exists. Replace?",
								"Replace centerline?", JOptionPane.YES_NO_OPTION);
						if(response==JOptionPane.YES_OPTION) {
							// addDSMChannel(centerlineName);
							loadAnotherChannelsInpFile = addDSMChannel(centerlineName);
						}
					} else {
						// addDSMChannel(centerlineName);
						loadAnotherChannelsInpFile = addDSMChannel(centerlineName);
					}
				} // if the cancel button wasn't pressed
			} // while
		}// actionPerformed

		/**
		 * adds a centerline for the specified DSM channel number. First point
		 * is located at upstream node, last point is located at downstream
		 * node.
		 */
		protected boolean addDSMChannel(String centerlineName) {
			int upnode = 0;
			int downnode = 0;
			String upnodeString = null;
			String downnodeString = null;
			double upnodeX = 0.0;
			double upnodeY = 0.0;
			double downnodeX = 0.0;
			double downnodeY = 0.0;
			Centerline centerline = null;
			boolean landmarkError = false;
			boolean channelsInpError = false;

			_net.addCenterline(centerlineName);
			centerline = _net.getCenterline(centerlineName);
			upnode = _DSMChannels.getUpnode(centerlineName);
			downnode = _DSMChannels.getDownnode(centerlineName);

			if (upnode < 0 || downnode < 0) {
				JOptionPane.showMessageDialog(_gui, "ERROR:  node not found for centerline " + centerlineName, 
						"Error", JOptionPane.ERROR_MESSAGE);
				channelsInpError = true;
			}

			// Integer upnodeInteger = new Integer(upnode);
			// Integer downnodeInteger = new Integer(downnode);
			// upnodeString = upnodeInteger.toString(upnode);
			// downnodeString = downnodeInteger.toString(downnode);

			upnodeString = Integer.toString(upnode);
			downnodeString = Integer.toString(downnode);

			boolean giveUp = false;
			double upX = -Double.MAX_VALUE;
			double upY = -Double.MAX_VALUE;
			double downX = -Double.MAX_VALUE;
			double downY = -Double.MAX_VALUE;

			while (giveUp == false) {
				if (DEBUG)
					System.out.println("landmark=" + _landmark);
				if (_landmark == null)
					_landmark = _gui.getLandmark(); // load landmark file
				upX = _landmark.getXFeet(upnodeString);
				upY = _landmark.getYFeet(upnodeString);
				downX = _landmark.getXFeet(downnodeString);
				downY = _landmark.getYFeet(downnodeString);

				if (upX < 0.0f || upY < 0.0f) {
					JOptionPane.showMessageDialog(_gui, "ERROR:  insufficient information in landmark file for node " + upnodeString + ".", 
							"Error", JOptionPane.ERROR_MESSAGE);

					landmarkError = true;
				}
				if (downX < 0.0f || downY < 0.0f) {
					JOptionPane.showMessageDialog(_gui, "ERROR:  insufficient information in landmark file for node " + downnodeString + ".", 
							"Error", JOptionPane.ERROR_MESSAGE);
					landmarkError = true;
				}
				if (landmarkError) {
					int response = JOptionPane.showConfirmDialog(_gui, "Load another landmark file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						_landmark = _gui.getLandmark(); // load landmark file
					}else if(response==JOptionPane.NO_OPTION || response==JOptionPane.CANCEL_OPTION) {
						giveUp = true;
					}
				} else {
					giveUp = true;
				}
			} // while

			if (channelsInpError == false && landmarkError == false) {
				// getX function returns -BIG_FLOAT if node not found in open
				// landmark file
				if (upX < 0.0f || upY < 0.0f || downX < 0.0f || downY < 0.0) {
					_landmark = _gui.getLandmark(); // load landmark file
				} // could use a while loop, but user would never get out if no
					// landmark file
				upnodeX = _landmark.getXFeet(upnodeString);
				upnodeY = _landmark.getYFeet(upnodeString);
				downnodeX = _landmark.getXFeet(downnodeString);
				downnodeY = _landmark.getYFeet(downnodeString);
				centerline.addDownstreamCenterlinePointFeet(upnodeX, upnodeY);
				centerline.addDownstreamCenterlinePointFeet(downnodeX, downnodeY);
				if (DEBUG)
					System.out.println("landmark coordinates: upstream xy, downstream xy=" + upnodeX + "," + upnodeY
							+ "," + downnodeX + "," + downnodeY);

				_net.setSelectedCenterlineName(centerlineName);
				_net.setSelectedCenterline(_net.getCenterline(centerlineName));
				_gui.enableAfterCenterlineSelected();
				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
				// removed for conversion to swing
				_gui.getPlanViewCanvas(0).redoNextPaint();
				_gui.getPlanViewCanvas(0).repaint();
			}
			return channelsInpError;
		}// addDSMChannel

		JFileChooser _jfcChannelsInp;
		CsdpFileFilter _channelsInpFilter;
		int _filechooserState;
	}// class CDSMCreate

	/**
	 * Rename centerline
	 */
	public class CRename implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (_net != null) {
				Centerline centerline = _net.getSelectedCenterline();
				String oldCenterlineName = _net.getSelectedCenterlineName();
				if (centerline != null) {
					String newCenterlineName = JOptionPane.showInputDialog(_gui, "Enter a new centerline name");
					centerline.setCenterlineName(newCenterlineName);
					_net.renameCenterline(oldCenterlineName, newCenterlineName);
				} // if centerline has been selected
			} // if there is a network
		}// actionPerformed
	}// class CRename

	/**
	 * move point in centerline
	 */
	public class CMovePoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CMovePoint

	/**
	 * insert point in centerline
	 */
	public class CInsertPoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setInsertPointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setInsertPointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CInsertPoint

	/**
	 * add point to centerline
	 */
	public class CAddPoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setAddPointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setAddPointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CAddPoint

	/**
	 * delete point from centerline
	 */
	public class CDeletePoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setDeletePointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setDeletePointMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CDelPoint

	/**
	 * add cross-section to centerline
	 */
	public class CAddXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setAddXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setAddXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CAddXsect

	/**
	 * remove cross-section from centerline
	 */
	public class CRemoveXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setRemoveXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setRemoveXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CRemoveXsect

	/**
	 * move cross-section along centerline
	 */
	public class CMoveXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setMoveXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setMoveXsectMode();
			_gui.setCursor(CsdpFunctions._handCursor);
		}

	}// class CMoveXsect

	/**
	 * undo changes since last restore
	 */
	public class CRestore implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}// class CRestore

	/**
	 * Keeps changes for next restore command
	 */
	public class CKeep implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}

	/**
	 * view data along centerline
	 */
	public class CView implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}// class CView

	/**
	 * display centerline info (name and length) at bottom of frame or canvas
	 */
	public class CInfo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}// class CInfo

	/**
	 * ?
	 */
	public class CList implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}// class CList

	/**
	 * display centerline prop (A,P,W,Zc,Xc,r)
	 */
	public class CSummary implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}

	}// class CSummary

	/**
	 * Display scatter plot showing variation of area, width, and wetted perimeter along centerline, 
	 * with panel showing centerline length, channel volume, channel wetted area, and channel surface area
	 * @author btom
	 *
	 */
	public class DisplayCenterlineSummaryWindow implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			_net = _gui.getNetwork();
			Centerline selectedCenterline = _net.getSelectedCenterline();
			int numXsectsWithPoints = selectedCenterline.getNumXsectsWithPoints();
			if(numXsectsWithPoints>0) {
				new CenterlineSummaryWindow(_gui, _net, CenterlineSummaryWindow.START_AT_DOWNSTREAM_END);
			}else {
				JOptionPane.showMessageDialog(_gui, "Selected centerline has no user-created cross-sections", "Nothing to plot", JOptionPane.OK_OPTION);
			}
		}
	}//class DisplayCenterlineSummaryWindow

	public class PlotAllCrossSections implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_net = _gui.getNetwork();
			String centerlineName = _net.getSelectedCenterlineName();

			Centerline selectedCenterline = _net.getSelectedCenterline();
			int numXsectsWithPoints = selectedCenterline.getNumXsectsWithPoints();
			if(numXsectsWithPoints>0) {
				MultipleXsectGraph mxg = new MultipleXsectGraph(_gui, _app, _net, centerlineName);
				mxg.setVisible(true);
			}else {
				JOptionPane.showMessageDialog(_gui, "Selected centerline has no user-created cross-sections", "Nothing to plot", JOptionPane.OK_OPTION);
			}
		}
	}//class PlotAllCrossSections

	
	public class AddXSAtComputationalPoints implements ActionListener{

		private NetworkInteractor networkInteractor;

		public AddXSAtComputationalPoints(NetworkInteractor ni) {
			// TODO Auto-generated constructor stub
			this.networkInteractor = ni;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			this.networkInteractor.addXsectsAtComputationalPoints(CsdpFunctions.DELTAX, CsdpFunctions.CROSS_SECTION_LINE_LENGTH);
		}
	}//class AddXSAtComputationalPoints

	/*
	 * Displays centerline and xsect lines with bathymetry in 3d view
	 */
	public class DisplayCenterline3DView implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_net = _gui.getNetwork();
			if (DEBUG)
				System.out.println("net=" + _net);
			_gui.pressSelectCursorAkaArrowButton();
//			Xsect xsect = _net.getSelectedXsect();
			String centerlineName = _net.getSelectedCenterlineName();
//			if (_app._xsectGraph.containsKey(centerlineName + "_" + xsectNum)) {
//				JOptionPane.showMessageDialog(_gui, "You are already viewing that cross-section!", "", JOptionPane.ERROR_MESSAGE);
//				// ((XsectGraph)(_app._xsectGraph.get(centerlineName+"_"+xsectNum))).setVisible(true);
//			} else {
			_app.viewCenterlinesWithBathymetry3D(new String[] {centerlineName}, CsdpFunctions.getXsectThickness(), null, true);
//			} // if		
		}

	}//class DisplayCenterline3DView
	
	Network _net;
	App _app;
	CsdpFrame _gui;
	NetworkPlot _nplot;
	Landmark _landmark;
	DSMChannels _DSMChannels = null;
	String _directory = null;
	Cursor _waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor _defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	protected static final boolean DEBUG = false;
	String[] _channelsInpExtensions = { "inp" };
	int _numChannelsInpExtensions = 1;
}// CenterlineMenu
