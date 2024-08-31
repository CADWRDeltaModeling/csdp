package DWR.CSDP;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * calls methods for zooming
 *
 * @author
 * @version $Id: ZoomMenu.java,v 1.4 2005/04/08 04:28:25 btom Exp $
 */
public class ZoomMenu {
	App _app;
	CsdpFrame _gui;
	BathymetryPlot _plot;

	protected static final boolean DEBUG = false;
	protected Rectangle _r = new Rectangle(0, 0);
	
	public ZoomMenu(CsdpFrame gui) {
		_gui = gui;
	}

	/**
	 * Allow user to draw rectangle for zoom box mode.
	 *
	 * @author
	 * @version
	 */
	public class ZBox implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._crosshairCursor);
		}// actionPerformed

	}// class ZBox

	/**
	 * For MenuItem. Allow user to draw rectangle for zoom box mode. Clicks the
	 * zoom box button.
	 *
	 * @author
	 * @version
	 */
	public class ZBoxMI implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			_gui.pressZoomBoxButton();
		}// actionPerformed

	}// class ZBox

	/**
	 * Pan--user specifies initial point and offset
	 *
	 * @author
	 * @version
	 */
	public class ZPan implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_gui.setCursor(CsdpFunctions._moveCursor);
		}
	}// class ZPan

	/**
	 * For MenuItem. Pan--user specifies initial point and offset
	 *
	 * @author
	 * @version
	 */
	public class ZPanMI implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_gui.pressZoomPanButton();
		}
	}// class ZPan

	/**
	 * Undo last zoom. Will destroy current ZoomFit object.
	 *
	 * @author
	 * @version
	 */
	public class ZUndo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_gui.getPlanViewCanvas(0).undoZoom();
		}
	}

	/**
	 * return to original view
	 *
	 * @author
	 * @version
	 */
	public class ZFit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (_gui.getZoomBoxMode()) {
				_gui.pressZoomBoxButton();
			}
			if (_gui.getZoomPanMode()) {
				_gui.pressZoomPanButton();
			}
			_gui.pressSelectCursorAkaArrowButton();
			_gui.getPlanViewCanvas(0).zoomFit();
		}
	}// class ZFit

	/**
	 * Zooms in by a factor of 2(result will be half as wide and high)
	 */
	public class ZIn implements ActionListener {
		public ZIn() {

		}

		public void actionPerformed(ActionEvent e) {

		}// actionPerformed
	}// class ZIn

	/**
	 * Zooms out by a factor of 2 (result will be twice as wide and high)
	 */

	public class ZOut implements ActionListener {
		public ZOut() {

		}

		public void actionPerformed(ActionEvent e) {

		}// actionPerformed
	}// class ZOut

	public class NZoomToCenterline implements ActionListener {
		CsdpFrame _gui;
		public NZoomToCenterline(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent arg0) {
			_gui.pressSelectCursorAkaArrowButton();
			String response = JOptionPane.showInputDialog(_gui, "Enter centerline name", "Zoom to centerline", JOptionPane.OK_CANCEL_OPTION);
			if(response!=null && response.length()>0) {
				NetworkInteractor networkInteractor = _gui.getNetworkInteractor();
				networkInteractor.zoomToCenterline(response);
			}
		}
	}

	public class NZoomToNode implements ActionListener {
		CsdpFrame _gui;
		public NZoomToNode(CsdpFrame gui) {
			_gui = gui;
		}

		public void actionPerformed(ActionEvent arg0) {
			_gui.pressSelectCursorAkaArrowButton();
			String response = JOptionPane.showInputDialog(_gui, "Enter node name", "Zoom to node", JOptionPane.OK_CANCEL_OPTION);
			if(response!=null && response.length()>0) {
				NetworkInteractor networkInteractor = _gui.getNetworkInteractor();
				networkInteractor.zoomToNode(response);
			}
		}
	}

	
}// class ZoomMenu

