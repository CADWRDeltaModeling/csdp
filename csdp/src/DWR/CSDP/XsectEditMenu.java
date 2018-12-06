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

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import vista.graph.GECanvas;

public class XsectEditMenu {

	public XsectEditMenu(XsectGraph xsectGraph, Network net, App app) {
		_xsectGraph = xsectGraph;
		_net = net;
		_app = app;
	}

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
			_xsectGraph._gC.redoNextPaint();
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
		public void windowOpened(WindowEvent e) {
		}

		public void windowActivated(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			closeWindow();
		}

		public void actionPerformed(ActionEvent e) {
			//
			closeWindow();
		}

		private void closeWindow() {
			if (_xsectGraph._xsect._isUpdated || _xsectGraph.getChangesKept()) {
				int response = JOptionPane.showConfirmDialog(_xsectGraph, "Keep changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION);
				if(response==JOptionPane.NO_OPTION) {
					if (DEBUG)
						System.out.println("not keeping changes");
					_xsectGraph.restoreXsect();
					// _xsectGraph.updateNetworkDataSet();
					_xsectGraph.updateDisplay();

					_xsectGraph._gC.redoNextPaint();
					// removed for conversion to swing
					// _xsectGraph._gC.repaint();
					_xsectGraph.dispose();
					_app.removeXsectGraph(_xsectGraph._centerlineName, _xsectGraph._xsectNum);
					_xsect.setIsUpdated(false);
				} // if
				else if(response==JOptionPane.YES_OPTION) {
					if (DEBUG)
						System.out.println("keeping changes");
					_xsectGraph.dispose();
					_app.removeXsectGraph(_xsectGraph._centerlineName, _xsectGraph._xsectNum);
					_net.setIsUpdated(true);
					_xsect.setIsUpdated(false);
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
				}else {
					//Cancel clicked; do nothing.
				}
			} // if xsect changes haven't been saved
			else {
				_xsectGraph.dispose();
				_app.removeXsectGraph(_xsectGraph._centerlineName, _xsectGraph._xsectNum);
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

	Network _net;
	Xsect _xsect;
	XsectGraph _xsectGraph;
	protected static final boolean DEBUG = false;
	App _app;
}// XsectEditMenu
