package DWR.CSDP;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import DWR.CSDP.dialog.CenterlineOrReachSummaryWindow;
import DWR.CSDP.dialog.DataEntryDialog;

/**
 * calls methods for creating and editing centerlines
 *
 * @author
 * @version
 */
public class CenterlineMenu {
	
	public class ScaleCrossSectionLineLengths implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String response = JOptionPane.showInputDialog(_csdpFrame, "Enter scale factor", "Scale cross-section line lengths", JOptionPane.OK_CANCEL_OPTION);
			if(response!=null && response.length()>0) {
				boolean success=false;
				try {
					double scaleFactor = Double.parseDouble(response);
					double averageAdjustedLineLength = 0.0; 
					Network network = CsdpFunctions.getNetwork();
					Centerline centerline = network.getSelectedCenterline();
					for(int i=0; i<centerline.getNumXsects(); i++) {
						Xsect xsect = centerline.getXsect(i);
						Double xsectLineLength = xsect.getXsectLineLengthFeet();
						xsect.putXsectLineLengthFeet(xsectLineLength * scaleFactor);
						averageAdjustedLineLength = (averageAdjustedLineLength*(double)i + xsectLineLength*scaleFactor)/(double)(i + 1);
					}
					_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
					_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
					_csdpFrame.getPlanViewCanvas(0).repaint();
					network.setIsUpdated(true);
					CsdpFunctions.CROSS_SECTION_LINE_LENGTH = averageAdjustedLineLength;
					success=true;
				}catch(Exception e1) {
					System.out.println("exception caught!");
				}finally {
					if(!success) {
						JOptionPane.showMessageDialog(_csdpFrame, "Unable to scale cross-section lines. Factor must be a number");
					}
				}
			}else {
				System.out.println("no input received");
			}
		}//actionPerformed
	}//inner class ScaleCrossSectionLineLengths

	/**
	 * Remove all cross-sections in centerline
	 * @author btom
	 *
	 */
	public class RemoveAllCrossSections implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			CsdpFunctions.getNetwork().getSelectedCenterline().removeAllCrossSections();
			_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
			// removed for conversion to swing
			_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
			_csdpFrame.getPlanViewCanvas(0).repaint();	
		}

	}
	public CenterlineMenu(CsdpFrame gui) {
		_csdpFrame = gui;
	}

//	public void setLandmark(Landmark landmark) {
//		_landmark = landmark;
//	}

	public class DeleteCenterlinePointsInWindow implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_csdpFrame.pressDeleteCenterlinePointsInBoxButton();
		}
	}

	public class DeleteCenterlinePointsOutsideOfWindow implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_csdpFrame.pressDeleteCenterlinePointsOutsideBoxButton();
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
			_csdpFrame = gui;
		}

		public void actionPerformed(ActionEvent e) {
			Network net = CsdpFunctions.getNetwork();
			_csdpFrame.pressSelectCursorAkaArrowButton();
			net = CsdpFunctions.getNetwork();
			if (net == null) {
				System.out.println("ERROR in CenterlineMenu.CRemove.actionPerformed: network is null!");
			} else {
				String cname = JOptionPane.showInputDialog(_csdpFrame, "Enter name of centerline to remove");
				// does specified centerline exist?
				if (net.centerlineExists(cname)) {
					int response = JOptionPane.showConfirmDialog(_csdpFrame, "Remove Centerline "+cname+"?", "Are you sure?", JOptionPane.YES_NO_OPTION);
					if(response==JOptionPane.YES_OPTION) {
						net.removeCenterline(cname);
						_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
						_csdpFrame.getPlanViewCanvas(0).repaint();
					}
				} else {
					// requested centerline doesn't exist
					JOptionPane.showMessageDialog(_csdpFrame, "requested centerline doesn't exist", 
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
			_csdpFrame = gui;
		}

		public void actionPerformed(ActionEvent e) {
			_csdpFrame.pressSelectCursorAkaArrowButton();
			Network net = CsdpFunctions.createNetworkIfNull(_csdpFrame, _app, _nplot);

			String centerlineName = null;
			centerlineName = JOptionPane.showInputDialog(_csdpFrame, "Enter new centerline name");

			if (centerlineName.length() > 0) {
				if (net.getCenterline(centerlineName) != null) {
					int response = JOptionPane.showConfirmDialog(_csdpFrame, "Centerline " + centerlineName + " already exists.  Replace?", 
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
			Network net = CsdpFunctions.getNetwork();
			net.addCenterline(centerlineName);
			net.setSelectedCenterlineName(centerlineName);
			net.setSelectedCenterline(net.getCenterline(centerlineName));
			_csdpFrame.enableAfterCenterlineSelected();
			_csdpFrame.setAddDownstreamPointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
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
	public class CDSM2Create implements ActionListener {
		/**
		 * assign instances of application and gui classes to class variables
		 */
		public CDSM2Create(App app, CsdpFrame gui) {
			_app = app;
			_csdpFrame = gui;
		}

		public void actionPerformed(ActionEvent e) {
			boolean success = false;
			_csdpFrame.pressSelectCursorAkaArrowButton();
			_csdpFrame.setDefaultModesStates();
			Network net = CsdpFunctions.createNetworkIfNull(_csdpFrame, _app, _nplot);
		
			String title = "Create Centerline for DSM2 Channel";
			String instructions = "<HTML><BODY>"
					+ "Using existing network file, landmark file containing DSM2 nodes, and a DSM channels file <BR>"
					+ "(channels.inp), create a CSDP centerline for a specified channel number:<BR>"
					+ "Landmark and channels input files must be loaded before proceeding. You can optionally load <BR>"
					+ "new files as needed. <BR>"
					+ "1. The centerline name<BR>"
					+ "2. The DSM2 channels file to use. If a file is already loaded, file path will be displayed. <BR>"
					+ "3. The CSDP landmark file containing DSM2 nodes. If a file is already loaded, file path will be displayed.<BR>"
					+ "4. Check the box if you want to force reloading of all files. <BR>"
					+ "</BODY></HTML>";

			final String[] names = new String[]{"Centerline name", "DSM2 channels input file", "CSDP landmark file", "reload files"};
			
			//If a channels.inp file is already loaded, get its path and use it as the default
			String existingChannelsInpFilePath = "";
			File existingDSMChannelsDirectory = CsdpFunctions.getDSMChannelsDirectory();
			String existingDSMChannelsFilename = CsdpFunctions.getDSMChannelsFilename()+"."+CsdpFunctions.getDSMChannelsFiletype();
			boolean defaultLoadNewFile = true;
			if(existingDSMChannelsDirectory!=null && existingDSMChannelsFilename != null && existingDSMChannelsFilename.length()>0) {
				existingChannelsInpFilePath = existingDSMChannelsDirectory.toString()+File.separator+existingDSMChannelsFilename;
				defaultLoadNewFile = false;
			}
			
			//If a landmark file is already loaded, get its path and use it as the default
			String existingLandmarkInpFilePath = "";
			File existingLandmarkFileDirectory = CsdpFunctions.getLandmarkDirectory();
			String existingLandmarkFilename = CsdpFunctions.getLandmarkFilename()+"."+CsdpFunctions.getLandmarkFiletype();
			if(existingLandmarkFileDirectory!=null && existingLandmarkFilename!=null && existingLandmarkFilename.length()>0) {
				existingLandmarkInpFilePath = existingLandmarkFileDirectory.toString()+File.separator+existingLandmarkFilename;
			}
			
			//create dialog specifications
			String[] defaultValues = new String[] {"", existingChannelsInpFilePath, existingLandmarkInpFilePath, Boolean.toString(defaultLoadNewFile)};
			int[] dataTypes = new int[] {DataEntryDialog.NUMERIC_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			boolean[] disableIfNull = new boolean [] {true, true, true, true};
			String[] extensions = new String[] {"","inp","cdl", ""};
			String[] tooltips = new String[] {"centerline name (integer)", "DSM2 channels file", "CSDP Landmark file (DSM2 nodes)", "load new DSM2 channels file"}; 
			boolean modal = true;

			
			//display the dialog, and get responses
			DataEntryDialog dataEntryDialog = new DataEntryDialog(_csdpFrame, title, instructions, names,
					defaultValues, dataTypes, disableIfNull, extensions, tooltips, modal);

			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String centerlineName = dataEntryDialog.getValue(names[0]);
				String dsmChanDirectory = dataEntryDialog.getDirectory(names[1]).toString();
				String dsmChanFilename = dataEntryDialog.getFilename(names[1]);
				String landmarkDirectory = dataEntryDialog.getDirectory(names[2]).toString();
				String landmarkFilename = dataEntryDialog.getFilename(names[2]);
				boolean loadNewFile = Boolean.parseBoolean(dataEntryDialog.getValue(names[3]));
			
				CsdpFunctions.getChannelsInpFile(_csdpFrame, centerlineName, dsmChanDirectory, dsmChanFilename, true, loadNewFile);
				CsdpFunctions.getLandmarkFile(_csdpFrame, _app, centerlineName, landmarkDirectory, landmarkFilename, true, loadNewFile);
				
				if(CsdpFunctions.okToAddDSMChannel(_csdpFrame, centerlineName)) {
					if (net.getCenterline(centerlineName) != null) {
						int replaceResponse = JOptionPane.showConfirmDialog(_csdpFrame, "Centerline " + centerlineName + " already exists. Replace?",
								"Replace centerline?", JOptionPane.YES_NO_OPTION);
						if(replaceResponse==JOptionPane.YES_OPTION) {
							CsdpFunctions.addDSMChannel(_csdpFrame, centerlineName);
							success=true;
						}else {
							success = true;
						}
					}else {
						CsdpFunctions.addDSMChannel(_csdpFrame, centerlineName);
						success = true;
					}
				}else {
					JOptionPane.showMessageDialog(_csdpFrame, "Failed to add centerline. Try again with a different landmark file.",
							"Error", JOptionPane.OK_OPTION);
					success=false;
				}
				net.setIsUpdated(true);
			}else if(response==DataEntryDialog.CANCEL) {
				success=true;
			}
		}//actionPerformed
		
		
//		public static void getNetworkInstance() {
//			_net = _gui.getNetwork();
//
//			if (_net == null) {
//				_net = new Network("delta", _gui);
//				_gui.setNetwork(_net);
//				_app._net = _net;
//				_nplot = _app.setNetworkPlotter();
//				_gui.getPlanViewCanvas(0).setNetworkPlotter(_nplot);
//				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
//				// removed for conversion to swing
//				_gui.getPlanViewCanvas(0).redoNextPaint();
//				_gui.getPlanViewCanvas(0).repaint();
//
//				// _gui.enableAfterNetwork();
//				_gui.enableWhenNetworkExists();
//			} // if net is null
//		}

		
//		protected void getChannelsInpFile(String centerlineName) {
//			boolean loadAnotherChannelsInpFile = true;
//
//			// if channels.inp file not loaded OR if channel # doesn't exist in
//			// current
//			// DSMChannels object. ask user if another file should be
//			// loaded--don't
//			// assume there is another file with the channel.
//			while (loadAnotherChannelsInpFile) {
//				if (_DSMChannels != null && _DSMChannels.channelExists(centerlineName) == false) {
//					int response = JOptionPane.showConfirmDialog(_gui, "Channel " + centerlineName
//							+ " not found in channel connectivity file.  Load another file?", "Channel not found", JOptionPane.YES_NO_OPTION);
//					if(response==JOptionPane.YES_OPTION) {
//						loadAnotherChannelsInpFile = true;
//					} else {
//						loadAnotherChannelsInpFile = false;
//					}
//				}else {
//					loadAnotherChannelsInpFile = false;
//				}
//
//				if (_DSMChannels == null || loadAnotherChannelsInpFile) {
//					String channelsFilename = null;
//					// FileDialog fd = new FileDialog(_gui, "Open DSM2 channel
//					// connectivity file");
//					// fd.setVisible(true);
//					_jfcChannelsInp.setDialogTitle("Open DSM2 channel connectivity file");
//					_jfcChannelsInp.setApproveButtonText("Open");
//					_jfcChannelsInp.addChoosableFileFilter(_channelsInpFilter);
//					_jfcChannelsInp.setFileFilter(_channelsInpFilter);
//
//					if (CsdpFunctions.getOpenDirectory() != null) {
//						_jfcChannelsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
//					}
//					_filechooserState = _jfcChannelsInp.showOpenDialog(_gui);
//					if (_filechooserState == JFileChooser.APPROVE_OPTION) {
//						channelsFilename = _jfcChannelsInp.getName(_jfcChannelsInp.getSelectedFile());
//						_directory = _jfcChannelsInp.getCurrentDirectory().getAbsolutePath() + File.separator;
//
//						// channelsFilename = fd.getFile();
//						// _directory = fd.getDirectory();
//						
//						_gui.setCursor(_waitCursor);
//						try {
//							_DSMChannels = _app.chanReadStore(_directory, channelsFilename);
////							_gui.setDSMChannels(_DSMChannels);
//						}catch(Exception e1) {
//							JOptionPane.showMessageDialog(_gui, "Error creating DSM2 channel", "Error", JOptionPane.ERROR_MESSAGE);
//						}finally {
//							_gui.setCursor(_defaultCursor);
//							
//						}
//					} else {
//						loadAnotherChannelsInpFile = false;
//					}
//				} // if DSMChannels is null
//
//				if (_filechooserState == JFileChooser.APPROVE_OPTION) {
//					if (_net.getCenterline(centerlineName) != null) {
//						int response = JOptionPane.showConfirmDialog(_gui, "Centerline " + centerlineName + " already exists. Replace?",
//								"Replace centerline?", JOptionPane.YES_NO_OPTION);
//						if(response==JOptionPane.YES_OPTION) {
//							// addDSMChannel(centerlineName);
//							loadAnotherChannelsInpFile = addDSMChannel(centerlineName);
//						}
//					} else {
//						// addDSMChannel(centerlineName);
//						loadAnotherChannelsInpFile = addDSMChannel(centerlineName);
//					}
//				} // if the cancel button wasn't pressed
//			} // while
//		}// getChannelsInpFile

//		/**
//		 * adds a centerline for the specified DSM channel number. First point
//		 * is located at upstream node, last point is located at downstream
//		 * node.
//		 */
//		protected boolean addDSMChannel(String centerlineName) {
//			int upnode = 0;
//			int downnode = 0;
//			String upnodeString = null;
//			String downnodeString = null;
//			double upnodeX = 0.0;
//			double upnodeY = 0.0;
//			double downnodeX = 0.0;
//			double downnodeY = 0.0;
//			Centerline centerline = null;
//			boolean landmarkError = false;
//			boolean channelsInpError = false;
//
//			_net.addCenterline(centerlineName);
//			centerline = _net.getCenterline(centerlineName);
//			upnode = _DSMChannels.getUpnode(centerlineName);
//			downnode = _DSMChannels.getDownnode(centerlineName);
//
//			if (upnode < 0 || downnode < 0) {
//				JOptionPane.showMessageDialog(_gui, "ERROR:  node not found for centerline " + centerlineName, 
//						"Error", JOptionPane.ERROR_MESSAGE);
//				channelsInpError = true;
//			}
//
//			// Integer upnodeInteger = new Integer(upnode);
//			// Integer downnodeInteger = new Integer(downnode);
//			// upnodeString = upnodeInteger.toString(upnode);
//			// downnodeString = downnodeInteger.toString(downnode);
//
//			upnodeString = Integer.toString(upnode);
//			downnodeString = Integer.toString(downnode);
//
//			boolean giveUp = false;
//			double upX = -Double.MAX_VALUE;
//			double upY = -Double.MAX_VALUE;
//			double downX = -Double.MAX_VALUE;
//			double downY = -Double.MAX_VALUE;
//
//			while (giveUp == false) {
//				if (DEBUG)
//					System.out.println("landmark=" + _landmark);
//				if (_landmark == null)
//					_landmark = _gui.getLandmark(); // load landmark file
//				upX = _landmark.getXFeet(upnodeString);
//				upY = _landmark.getYFeet(upnodeString);
//				downX = _landmark.getXFeet(downnodeString);
//				downY = _landmark.getYFeet(downnodeString);
//
//				if (upX < 0.0f || upY < 0.0f) {
//					JOptionPane.showMessageDialog(_gui, "ERROR:  insufficient information in landmark file for node " + upnodeString + ".", 
//							"Error", JOptionPane.ERROR_MESSAGE);
//
//					landmarkError = true;
//				}
//				if (downX < 0.0f || downY < 0.0f) {
//					JOptionPane.showMessageDialog(_gui, "ERROR:  insufficient information in landmark file for node " + downnodeString + ".", 
//							"Error", JOptionPane.ERROR_MESSAGE);
//					landmarkError = true;
//				}
//				if (landmarkError) {
//					int response = JOptionPane.showConfirmDialog(_gui, "Load another landmark file?", "", JOptionPane.YES_NO_CANCEL_OPTION);
//					if(response==JOptionPane.YES_OPTION) {
//						_landmark = _gui.getLandmark(); // load landmark file
//					}else if(response==JOptionPane.NO_OPTION || response==JOptionPane.CANCEL_OPTION) {
//						giveUp = true;
//					}
//				} else {
//					giveUp = true;
//				}
//			} // while
//
//			if (channelsInpError == false && landmarkError == false) {
//				// getX function returns -BIG_FLOAT if node not found in open
//				// landmark file
//				if (upX < 0.0f || upY < 0.0f || downX < 0.0f || downY < 0.0) {
//					_landmark = _gui.getLandmark(); // load landmark file
//				} // could use a while loop, but user would never get out if no
//					// landmark file
//				upnodeX = _landmark.getXFeet(upnodeString);
//				upnodeY = _landmark.getYFeet(upnodeString);
//				downnodeX = _landmark.getXFeet(downnodeString);
//				downnodeY = _landmark.getYFeet(downnodeString);
//				centerline.addDownstreamCenterlinePointFeet(upnodeX, upnodeY);
//				centerline.addDownstreamCenterlinePointFeet(downnodeX, downnodeY);
//				if (DEBUG)
//					System.out.println("landmark coordinates: upstream xy, downstream xy=" + upnodeX + "," + upnodeY
//							+ "," + downnodeX + "," + downnodeY);
//
//				_net.setSelectedCenterlineName(centerlineName);
//				_net.setSelectedCenterline(_net.getCenterline(centerlineName));
//				_gui.enableAfterCenterlineSelected();
//				_gui.getPlanViewCanvas(0).setUpdateNetwork(true);
//				// removed for conversion to swing
//				_gui.getPlanViewCanvas(0).redoNextPaint();
//				_gui.getPlanViewCanvas(0).repaint();
//			}
//			return channelsInpError;
//		}// addDSMChannel

		JFileChooser _jfcChannelsInp;
		CsdpFileFilter _channelsInpFilter;
		int _filechooserState;
	}// class CDSMCreate

//	CenterlineMenu.CDSM2Create cDSMCreate = new CenterlineMenu.CDSM2Create(app, gui)
//	public class NCreateNetworkAllDSM2Chan extends centerlineMenu.CDSM2Create{
//		public NCreateNetworkAllDSM2Chan(App app, CsdpFrame csdpFrame) {
//			.super(app, csdpFrame);
//		}
//		public void actionPerformed(ActionEvent e) {
//			// TODO Auto-generated method stub
//
//		}
//
//	}

	
	/**
	 * Rename centerline
	 */
	public class CRename implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Network net = CsdpFunctions.getNetwork();
			if (net != null) {
				Centerline centerline = net.getSelectedCenterline();
				String oldCenterlineName = net.getSelectedCenterlineName();
				if (centerline != null) {
					String newCenterlineName = JOptionPane.showInputDialog(_csdpFrame, "Enter a new centerline name");
					centerline.setCenterlineName(newCenterlineName);
					net.renameCenterline(oldCenterlineName, newCenterlineName);
				} // if centerline has been selected
			} // if there is a network
		}// actionPerformed
	}// class CRename

	public class ReverseCenterline implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Network net = CsdpFunctions.getNetwork();
			net = CsdpFunctions.getNetwork();
			if (net != null) {
				Centerline centerline = net.getSelectedCenterline();
				centerline.reverseOrder();
				_csdpFrame.getPlanViewCanvas(0).setUpdateNetwork(true);
				_csdpFrame.getPlanViewCanvas(0).redoNextPaint();
				_csdpFrame.getPlanViewCanvas(0).repaint();
			} // if there is a network
		}
	}//class ReverseCenterline


	
	/**
	 * move point in centerline
	 */
	public class CMovePoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CMovePoint

	/**
	 * insert point in centerline
	 */
	public class CInsertPoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setInsertPointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setInsertPointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CInsertPoint

	/**
	 * add point to centerline
	 */
	public class CAddPoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setAddPointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setAddPointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CAddPoint

	/**
	 * delete point from centerline
	 */
	public class CDeletePoint implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setDeletePointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setDeletePointMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CDelPoint

	/**
	 * add cross-section to centerline
	 */
	public class CAddXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setAddXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setAddXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CAddXsect

	/**
	 * remove cross-section from centerline
	 */
	public class CRemoveXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setRemoveXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setRemoveXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

	}// class CRemoveXsect

	/**
	 * move cross-section along centerline
	 */
	public class CMoveXsect implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			// _gui.setMoveXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
		}

		public void actionPerformed(ActionEvent e) {
			// _gui.setMoveXsectMode();
			_csdpFrame.setCursor(CsdpFunctions._handCursor);
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
			Network net = CsdpFunctions.getNetwork();
			net = CsdpFunctions.getNetwork();
			Centerline selectedCenterline = net.getSelectedCenterline();
			int numXsectsWithPoints = selectedCenterline.getNumXsectsWithPoints();
			if(numXsectsWithPoints>0) {
//				new CenterlineSummaryWindow(_gui, _net, CenterlineSummaryWindow.START_AT_DOWNSTREAM_END);
				_app.createCenterlineOrReachSummaryWindow(_csdpFrame, net, CenterlineOrReachSummaryWindow.START_AT_DOWNSTREAM_END);

			}else {
				JOptionPane.showMessageDialog(_csdpFrame, "Selected centerline has no user-created cross-sections", "Nothing to plot", JOptionPane.OK_OPTION);
			}
		}
	}//class DisplayCenterlineSummaryWindow

	public class PlotAllCrossSections implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			Network net = CsdpFunctions.getNetwork();
			net = CsdpFunctions.getNetwork();
			String centerlineName = net.getSelectedCenterlineName();

			Centerline selectedCenterline = net.getSelectedCenterline();
			int numXsectsWithPoints = selectedCenterline.getNumXsectsWithPoints();
			if(numXsectsWithPoints>0) {
				MultipleXsectGraph mxg = new MultipleXsectGraph(_csdpFrame, _app, net, centerlineName);
				mxg.setVisible(true);
			}else {
				JOptionPane.showMessageDialog(_csdpFrame, "Selected centerline has no user-created cross-sections", "Nothing to plot", JOptionPane.OK_OPTION);
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
			this.networkInteractor.addXsectsAtComputationalPoints(CsdpFunctions.CROSS_SECTION_LINE_LENGTH);
		}
	}//class AddXSAtComputationalPoints

	/*
	 * Displays centerline and xsect lines with bathymetry in 3d view
	 */
	public class DisplayCenterline3DView implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			Network net = CsdpFunctions.getNetwork();
			net = CsdpFunctions.getNetwork();
			if (DEBUG)
				System.out.println("net=" + net);
			_csdpFrame.pressSelectCursorAkaArrowButton();
//			Xsect xsect = _net.getSelectedXsect();
			String centerlineName = net.getSelectedCenterlineName();
//			if (_app._xsectGraph.containsKey(centerlineName + "_" + xsectNum)) {
//				JOptionPane.showMessageDialog(_gui, "You are already viewing that cross-section!", "", JOptionPane.ERROR_MESSAGE);
//				// ((XsectGraph)(_app._xsectGraph.get(centerlineName+"_"+xsectNum))).setVisible(true);
//			} else {
			_app.viewCenterlinesWithBathymetry3D(new String[] {centerlineName}, CsdpFunctions.getXsectThickness(), null, true);
//			} // if		
		}

	}//class DisplayCenterline3DView
	
	App _app;
	CsdpFrame _csdpFrame;
	NetworkPlot _nplot;
//	DSMChannels _DSMChannels = null;
	String _directory = null;
	Cursor _waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor _defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	protected static final boolean DEBUG = false;
	String[] _channelsInpExtensions = { "inp" };
	int _numChannelsInpExtensions = 1;
}// CenterlineMenu
