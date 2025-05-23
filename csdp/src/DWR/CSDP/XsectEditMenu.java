package DWR.CSDP;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import DWR.CSDP.dialog.DataEntryDialog;
import vista.graph.GECanvas;

public class XsectEditMenu {

	private CsdpFrame _csdpFrame;
	private Network _net;
	private Xsect _xsect;
	private XsectGraph _xsectGraph;
	protected static final boolean DEBUG = false;
	private App _app;
	
	public XsectEditMenu(XsectGraph xsectGraph, CsdpFrame csdpFrame, Network net, App app) {
		_xsectGraph = xsectGraph;
		_csdpFrame = csdpFrame;
		_net = net;
		_app = app;
	}//constructor

	public class XAutoCreate implements ActionListener {
		public XAutoCreate(XsectGraph xsectGraph) {
			_xsectGraph = xsectGraph;
		}
		public void actionPerformed(ActionEvent e) {
			_xsectGraph.averageBathymetryForAutoXS();
			_xsectGraph.createAutoXS();
		}
	}//inner class XAutoCreate

	public class XUGC implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_xsectGraph.updateDisplay();
		}
	}

	/**
	 * Change order of network points in xsect and multiply stations by -1
	 *
	 * @author
	 * @version $Id:
	 */
	public class XReverse implements ActionListener {
		public XReverse(Xsect xsect) {
			_xsect = xsect;
		}

		public void actionPerformed(ActionEvent e) {
			_xsect.reverse();
			_xsectGraph.updateNetworkDataSet();
			_xsectGraph.updateDisplay();
			_xsect.setIsUpdated(true);
		}// actionPerformed

	}// xReverse

	/**
	 * Keep changes made to xsect (doesn't save file)
	 *
	 * @author
	 * @version $Id:
	 */
	public class XKeep implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("xkeep actionperformed!");
			_xsectGraph.keepChanges();
			_xsectGraph.setIsUpdated(false);
			_xsectGraph.setChangesKept(true);
			_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
			_csdpFrame.updateInfoPanel(_net.getSelectedCenterlineName());
		}// actionPerformed

	}// xKeep

	/**
	 * Restore original xsect(don't keep changes);
	 *
	 * @author
	 * @version $Id:
	 */
	public class XRestore implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (DEBUG)
				System.out.println("xsectGraph=" + _xsectGraph);
			System.out.println("XRestore actionPerformed!");
			_xsectGraph.restoreXsect();
			_xsectGraph.updateDisplay();
			// removed for conversion to swing
			//
			_xsect.setIsUpdated(true);
		}// actionPerformed

	}// xRestore

	/**
	 * Change elevation used to calculate cross-section properties displayed at
	 * bottom of frame
	 *
	 * @author
	 * @version $Id:
	 */
	public class XChangeElevation implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String elevString = JOptionPane.showInputDialog(_xsectGraph, "Enter new elevation", CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			double nf = Double.parseDouble(elevString);
			_xsectGraph.setXSPropElevation(nf);
			_xsectGraph.updateDisplay();
			_xsectGraph.redoNextPaint();
			_xsectGraph.validate();
			// removed for conversion to swing
			// _xsectGraph._gC.repaint();
		}// actionPerformed
	}// XChangeElevation

	/**
	 * print cross-section
	 */
	public class XPrint implements ActionListener {
		XsectGraph _xg = null;
		GECanvas _gC = null;

		public XPrint(XsectGraph xg) {
			_xg = xg;
		}

		public void actionPerformed(ActionEvent e) {
			//now that XsectGraph is a JDialog and not a JFrame, this won't work.
//			_gC = _xg.getGC();
//			// set size to 8.5 X 11 inches == 21.25 cm X 27.5 cm
//			Dimension pSize = _xg.getSize();
//			int resolution = 72; // in pixels per inch
//			_xg.setSize((int) 8.5 * resolution, 11 * resolution);
//
//			// PrintJob pj = Toolkit.getDefaultToolkit().getPrintJob
//			// (_xg, "GraphCanvas Print Job", null);
//
//			Toolkit t = _xg.getToolkit();
//			PrintJob pj = t.getPrintJob(_xg, "Cross-Section", null);
//			if (pj != null) {
//				Graphics pg = pj.getGraphics();
//				if (pg != null) {
//					_xg.printAll(pg);
//					pg.dispose();
//				}
//				pj.end();
//			} else {
//				System.out.println("no print job!");
//			}

			// if(pj != null){
			// Graphics pg = pj.getGraphics();
			// try{
			// _gC.paintAll(pg);
			// } finally{
			// // pg.dispose();
			// }//finally
			// }//if pj not null
			// _xg.setSize(pSize.width, pSize.height);
			// _xg.repaint();
		}// actionPerformed
	}// class XPrint

	/*
	 * Save cross-section window to image
	 */
	public class XSaveToImage implements ActionListener {

		private XsectGraph xsectGraph;

		public XSaveToImage(XsectGraph xsectGraph) {
			this.xsectGraph = xsectGraph;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			Container container = xsectGraph.getContentPane();
			BufferedImage im = new BufferedImage(container.getWidth(), container.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			container.paint(im.getGraphics());
			try {
				JFileChooser jfc = new JFileChooser();
				String[] extensions = { "png" };
				int numExtensions = 1;
				CsdpFileFilter channelsInpFilter = new CsdpFileFilter(extensions, numExtensions);
				
				jfc.setDialogTitle("Create Image File for Cross-Section window");
				jfc.setApproveButtonText("Save");
				jfc.addChoosableFileFilter(channelsInpFilter);
				jfc.setFileFilter(channelsInpFilter);
				jfc.setName(xsectGraph.getName()+".png");
				if (CsdpFunctions.getOpenDirectory() != null) {
					jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
				}
				int filechooserState = jfc.showOpenDialog(xsectGraph);
				String directory = null;
				if (filechooserState == JFileChooser.APPROVE_OPTION) {
					String filename = jfc.getName(jfc.getSelectedFile());
					directory = jfc.getCurrentDirectory().getAbsolutePath() + File.separator;
					ImageIO.write(im, "PNG", new File(directory+File.separator+filename));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}//class XSaveToImage
	
	/**
	 * close xsect frame
	 */
	public class XClose implements ActionListener, WindowListener {
		public void windowOpened(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			closeWindow();
		}

		public void actionPerformed(ActionEvent e) {
			//
			closeWindow();
		}

		private void closeWindow() {
			if (_xsectGraph.getXsect()._isUpdated || _xsectGraph.getChangesKept()) {
				int response = JOptionPane.showConfirmDialog(_xsectGraph, "Keep changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);
				if(response==JOptionPane.NO_OPTION) {
					if (DEBUG)
						System.out.println("not keeping changes");
					_xsectGraph.restoreXsect();
					// _xsectGraph.updateNetworkDataSet();
					_xsectGraph.updateDisplay();

					_xsectGraph.redoNextPaint();
					// removed for conversion to swing
					// _xsectGraph._gC.repaint();
					_xsectGraph.dispose();
					_app.removeXsectGraph(_xsectGraph.getCenterlineName(), _xsectGraph.getXsectNum());
					_xsect.setIsUpdated(false);
					_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
					_csdpFrame.updateInfoPanel(_net.getSelectedCenterlineName());
				} // if
				else if(response==JOptionPane.YES_OPTION) {
					if (DEBUG)
						System.out.println("keeping changes");
					_xsectGraph.dispose();
					_app.removeXsectGraph(_xsectGraph.getCenterlineName(), _xsectGraph.getXsectNum());
					_net.setIsUpdated(true);
					_xsect.setIsUpdated(false);
					_app.repaintNetwork();
					int metadataLength = _xsectGraph._metadataJTextArea.getDocument().getLength();
					Document doc = _xsectGraph._metadataJTextArea.getDocument();
					String newmd = null;
					try {
						newmd = doc.getText(0, metadataLength);
					} catch (javax.swing.text.BadLocationException e) {
						System.out.println("BadLocationException thrown in XsectEditMenu.XClose.closeWindow. e=" + e);
						System.out.println("This probably just means that there is no metadata--no problem.");
					}
					_xsect.putMetadata(newmd);
					_xsectGraph.updateMainWindowInfoPanel();
					_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
					_csdpFrame.updateInfoPanel(_net.getSelectedCenterlineName());

				}else {
					//Cancel clicked; do nothing.
				}
			} // if xsect changes haven't been saved
			else {
				_xsectGraph.dispose();
				_app.removeXsectGraph(_xsectGraph.getCenterlineName(), _xsectGraph.getXsectNum());
				_xsect.setIsUpdated(false);
			} // else
		}// closeWindow
	}// XClose

	/**
	 * move xsect point
	 *
	 * @author
	 * @version $Id:
	 */
	public class XMovePoint implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			_xsectGraph.setCursor(CsdpFunctions._crosshairCursor);
		}
	}// XMovePoint

	/**
	 * add xsect point
	 *
	 * @author
	 * @version $Id:
	 */
	public class XAddPoint implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			_xsectGraph.setCursor(CsdpFunctions._crosshairCursor);
		}
	}// XAddPoint

	/**
	 * insert xsect point
	 *
	 * @author
	 * @version $Id:
	 */
	public class XInsertPoint implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			_xsectGraph.setCursor(CsdpFunctions._crosshairCursor);
		}
	}// XAddPoint

	/**
	 * delete xsect point
	 *
	 * @author
	 * @version $Id:
	 */
	public class XDeletePoint implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			_xsectGraph.setCursor(CsdpFunctions._crosshairCursor);
		}
	}// XDeletePoint

	/**
	 * stop editing
	 *
	 * @author
	 * @version $Id:
	 */
	public class XStopEdit implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			_xsectGraph.setCursor(CsdpFunctions._defaultCursor);
		}
	}// XStopEdit

	/**
	 * View and/or edit metadata--information about the cross-section
	 *
	 * @author
	 * @version $Id:
	 */
	// public class XMetadata implements ActionListener{
	// Xsect _xsect;
	// // ResizableStringArray _message;
	// String _message;
	// MessageDialog _md;
	// CsdpFrame _gui;
	// String _centerlineName;
	// int _xsectNum;
	// private final int _stringWidth=80;
	// private final int _numLines = 10;

	// public XMetadata(String centerlineName, int xsectNum, Xsect xsect,
	// CsdpFrame gui){
	// _centerlineName = centerlineName;
	// _xsectNum = xsectNum;
	// _xsect = xsect;
	// _gui = gui;
	// _message = _xsect.getMetadata();

	// _md = new MessageDialog(_gui, "Metadata for cross-section "+
	// _centerlineName+"_"+_xsectNum, _message,
	// true, true, _stringWidth, _numLines);
	// }

	// public void actionPerformed(ActionEvent e){
	// _message = _xsect.getMetadata();
	// String oldMessage = _message;
	// _md.updateMessage(_message);
	// _md.setTitle("Metadata for cross-section "+
	// _centerlineName+"_"+_xsectNum);
	// _md.setVisible(true);
	// _message = _md.getMessage();

	// if(oldMessage != null && oldMessage.equals(_message)){
	// }else{
	// _xsect.putMetadata(_message);
	// _xsect.setIsUpdated(true);
	// }
	// if(DEBUG)System.out.println("getText="+_md.getMessage());
	// }

	// }//XMetadata

	/**
	 * edit metadata using JTextArea--information about the cross-section
	 *
	 * @author
	 * @version $Id:
	 */
	public class XMetadata implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			updated();
		}

		public void removeUpdate(DocumentEvent e) {
			updated();
		}

		public void changedUpdate(DocumentEvent e) {
			// Plain text components don't fire these events
		}

		public void updated() {
			_xsect.setIsUpdated(true);
		}
	}// XMetadata

	/**
	 * moves cross-section points by a user specified amount in the x direction.
	 *
	 * @author
	 * @version $Id:
	 */
	public class XMoveXsectX implements ActionListener {
		JTextField _xField;
		Xsect _xsect;

		public XMoveXsectX(JTextField xField, Xsect xsect) {
			_xField = xField;
			_xsect = xsect;
		}

		public void actionPerformed(ActionEvent e) {
			double value = Double.parseDouble(_xField.getText());
			_xsect.adjustXCoord(value);
//			_xsectGraph.updateXsectProp();
			_xsectGraph.updateDisplay();
			_xsect.setIsUpdated(true);
		}

	}

	/**
	 * moves cross-section points by a user specified amount in the y direction.
	 *
	 * @author
	 * @version $Id:
	 */
	public class XMoveXsectY implements ActionListener {
		JTextField _yField;
		Xsect _xsect;

		public XMoveXsectY(JTextField yField, Xsect xsect) {
			_yField = yField;
			_xsect = xsect;
		}

		public void actionPerformed(ActionEvent e) {
			double value = Double.parseDouble(_yField.getText());
			_xsect.adjustYCoord(value);
			_xsectGraph.updateDisplay();
			_xsect.setIsUpdated(true);
		}

	}

	/**
	 * Cloning a cross-sections means copying the user-created points from a specified cross-section to the current cross-section window
	 * @author btom
	 *
	 */
	public class CloneXsect implements ActionListener {

		private String centerlineName;
		private XsectGraph xsectGraph;
		private CsdpFrame csdpFrame;
		private App app;

		public CloneXsect(CsdpFrame csdpFrame, App app, String centerlineName, XsectGraph xsectGraph) {
			this.csdpFrame = csdpFrame;
			this.app = app;
			this.centerlineName = centerlineName;
			this.xsectGraph = xsectGraph;
		}

		public void actionPerformed(ActionEvent arg0) {
			String[] names = new String[] {"Centerline Name", "Cross-Section Index"};
			String[] defaultValue = new String[] {this.centerlineName, ""};
			int[] dataType = new int[] {DataEntryDialog.STRING_TYPE, DataEntryDialog.NUMERIC_TYPE};
			int[] numDecimalPlaces = new int[] {0,0};
			boolean[] disableIfNull = new boolean[] {true, true};
			
			String[] tooltips = new String[] {
					"Enter the name of the centerline (usually a DSM2 channel number) from which you would like to "
					+ "copy cross-section points",
					"Enter the index of the cross-section from which you would like to copy cross-section points. "
					+ "The index of the first cross-section is 0"
			};
			
			String instructions = "<HTML><BODY>"
					+ "Cloning a cross-section will remove all of the points in the current cross-section window, and replace them with the<BR>"
					+ "points from a specified cross-section. <BR><BR>"
					+ "<B>Centerline Name:</B> This is the name of the centerline that contains the cross-section from which you would <BR>"
					+ "like to copy points.<BR>"
					+ "<B>Cross-Section Index:</B> The number of the cross-section from which you would like to copy points. <BR>"
					+ "Examples: the furthest upstream cross-section has index=0, the next has index=1.<BR></BODY></HTML>";

			DataEntryDialog dataEntryDialog = new DataEntryDialog(_csdpFrame, "Clone a cross-section FROM another cross-section", instructions, names, defaultValue, dataType, 
					disableIfNull, numDecimalPlaces, tooltips, true);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String sourceCenterlineNameString = dataEntryDialog.getValue(names[0]);
				String sourceXsectIndexString = dataEntryDialog.getValue(names[1]);
				int sourceXsectIndex = Integer.parseInt(sourceXsectIndexString);
				Xsect xsect = this.xsectGraph.getXsect();
				xsect.removeAllPoints();
				Centerline sourceCenterline = _net.getCenterline(sourceCenterlineNameString);
				Xsect sourceXsect = sourceCenterline.getXsect(sourceXsectIndex);
				Vector<XsectPoint> xsectPointsVector = sourceXsect.getAllPoints();
				int numPoints = sourceXsect.getNumPoints();
				//Tried cloning the Vector object, but that fails to create a separate instance, so clone the XsectPoint objects instead
				Vector<XsectPoint> newXsectPointsVector = new Vector<XsectPoint>();
				for(int i=0; i<xsectPointsVector.size(); i++) {
					newXsectPointsVector.add(xsectPointsVector.get(i).clone());
				}
					
				xsect.putAllPoints(numPoints, newXsectPointsVector);
				this.csdpFrame.updateInfoPanel(this.centerlineName);
				this.app.updateAllOpenCenterlineOrReachSummaries(this.centerlineName);
				this.xsectGraph.updateDisplay();
			}
		}

	}


	
}// XsectEditMenu
