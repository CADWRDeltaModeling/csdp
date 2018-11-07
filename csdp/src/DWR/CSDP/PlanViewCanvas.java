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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

/**
 * canvas used for plotting bathymetry, landmark, and network data in plan view
 *
 * @author
 * @version
 */

public class PlanViewCanvas extends JPanel {
	public PlanViewCanvas(CsdpFrame gui) {
		_gui = gui;
		// setOpaque(true);
		// setLegend(new Legend(new LegendAttr()));
	}

	/**
	 * This prepares the next paint call for redoing the whole GraphicElement.
	 * This means layout + actual drawing of components
	 */
	public void redoNextPaint() {
		_updateNeeded = true;
	}

	public void setUpdateCanvas(boolean b) {
		setUpdateBathymetry(b);
		setUpdateNetwork(b);
		setUpdateLandmark(b);
		setUpdateDigitalLineGraph(b);
	}

	public void setUpdateBathymetry(boolean b) {
		_updateBathymetry = b;
		_updateNeeded = b;
	}

	public void setUpdateNetwork(boolean b) {
		_updateNetwork = b;
		_updateNeeded = b;
	}

	public void setUpdateLandmark(boolean b) {
		_updateLandmark = b;
		_updateNeeded = b;
	}

	public void setUpdateDigitalLineGraph(boolean b) {
		_updateDlg = b;
		_updateNeeded = b;
	}

	/**
	 * Paint screen, using (optional) double buffering. Used to be paint();
	 * changed to paintComponent() for conversion to swing.
	 */
	public void paintComponent(Graphics g) throws OutOfMemoryError {
		//// try{
		///// _gui.setCursor(CsdpFunctions._waitCursor);
		// for java2d
		Graphics2D g2 = (Graphics2D) g;

		super.paintComponent(g2);
		// not necessary for swing
		// setSize((int)((float)_dim.width*CsdpFunctions.getZoomFactor()),
		// (int)((float)_dim.height*CsdpFunctions.getZoomFactor()));

		Rectangle r = this.getBounds();

		if (r.width != oldR.width || r.height != oldR.height)
			_updateNeeded = true;

		r.x = 0;
		r.y = 0;
		// don't use set clip! it makes the canvas overwrite components in the
		// BorderLayout!!!
		// g2.setClip(r);
		g2.clipRect(0, 0, r.width, r.height);

		if (_updateNeeded) {
			_geImage = createImage(r.width, r.height);
			Graphics2D gg = (Graphics2D) _geImage.getGraphics();
			if (_plotter != null && _updateBathymetry) {
				_backgroundImage = createImage(r.width, r.height);
				Graphics2D gb = (Graphics2D) _backgroundImage.getGraphics();
				_plotter.plotBathymetryData(gb, this.getBounds(), _zoomBox, _changeZoom, getZoomPanOffsets(),
						_changePan, _useZoomFit, _undoZoom);
				setUpdateBathymetry(false);
			}
			if (_backgroundImage != null) {
				gg.drawImage(_backgroundImage, 0, 0, this);
			}
			if (_networkPlotter != null) {
				// _networkImage = createImage(r.width, r.height);
				// _network2D = (Graphics2D)_networkImage.getGraphics();

				_networkPlotter.setNetwork(_net);

				_networkPlotter.plotData(gg, this.getBounds(), _zoomBox, _changeZoom);
				// _networkPlotter.plotData(_network2D,this.getBounds(),_zoomBox,_changeZoom);
				setUpdateNetwork(false);
			}
			if (_landmarkPlotter != null) {
				_landmarkPlotter.setLandmark(_landmark);
				_landmarkPlotter.plotData(gg, this.getBounds(), _zoomBox, _changeZoom);
				setUpdateLandmark(false);
			}
			if (_dlgPlotter != null && _dlg != null) {
				if (DEBUG)
					System.out.println("dlgplotter not null!");
				_dlgPlotter.setDigitalLineGraph(_dlg);
				_dlgPlotter.plotData(gg, this.getBounds(), _zoomBox, _changeZoom);
				setUpdateDigitalLineGraph(false);
			}

		} // updateNeeded

		g2.drawImage(_geImage, 0, 0, this);
		///
		oldR = r;

		_updateNeeded = false;

		// }catch(OutOfMemoryError e){
		// System.out.println("OUTOFMEMORY ERROR CAUGHT!!!!!!!!!!!!!!!!!!");
		// CsdpFunctions.setZoomFactor(CsdpFunctions.getOldZoomFactor());
		// _plotter.setPointSize(1);
		// resetSize();
		// setUpdateCanvas(true);
		// JOptionPane.showOptionDialog
		// (null, "ERROR! You haven't allocated enough memory to zoom in that
		// far!",
		// "ERROR! OPERATION FAILED",
		// JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
		// _options, _options[0]);
		// //removed for conversion to swing
		// _gui._canvas1.repaint();
		// }

		//// _gui.setCursor(CsdpFunctions._defaultCursor);

		resetZoomOptions();
	} // paintComponent

	/**
	 * Called by paintComponent so if next repaint is not resizing anything, it
	 * won't try to zoom in again or pan or fit.
	 */
	private void resetZoomOptions() {
		setChangeZoom(false);
		setChangePan(false);
		setZoomFit(false);
		setUndoZoom(false);
	}

	// geImage only for double buffering

	public Image getBackgroundImage() {
		return _backgroundImage;
	}

	public Image getForegroundImage() {
		return _geImage;
	}

	public void setPlotter(BathymetryPlot plotter, Dimension dim) {
		_plotter = plotter;
		_dim = dim;
		// for scrollable jpanel
		// _ps.setSize((int)((float)_dim.width*CsdpFunctions.getZoomFactor()),
		// (int)((float)_dim.height*CsdpFunctions.getZoomFactor()));
		_ps.setSize((int) ((float) _dim.width), (int) ((float) _dim.height));
		setPreferredSize(_ps);
	}

	public void setDigitalLineGraphPlotter(DigitalLineGraphPlot dlgPlotter) {
		_dlgPlotter = dlgPlotter;
	}

	public void setNetworkPlotter(NetworkPlot networkPlotter) {
		_networkPlotter = networkPlotter;
	}

	public void setNetwork(Network net) {
		_net = net;
	}

	public void setLandmarkPlotter(LandmarkPlot landmarkPlotter) {
		_landmarkPlotter = landmarkPlotter;
	}

	public void setLandmark(Landmark landmark) {
		_landmark = landmark;
	}

	public void setDigitalLineGraph(DigitalLineGraph dlg) {
		_dlg = dlg;
	}

	/**
	 * fit data to display, turn off zoomBox, and repaint. Also tell plotter
	 * that is should no longer consider itself to be zoomed in. This method is
	 * called when the fit button is clicked or when the window is resized.
	 * _plotter will be null when the application is started.
	 */
	public void zoomFit() {
		if (_plotter != null) {
			_changeZoom = false;
			_changePan = false;
			_plotter.setAlreadyZoomedIn(false);
			setZoomFit(true);
			setUpdateBathymetry(true);
			setUpdateNetwork(true);
			setUpdateLandmark(true);
			setUpdateCanvas(true);
			redoNextPaint();
			repaint();
		}
	}

	/**
	 * zoomInOut--called by NetworkInteractor.mouseReleased
	 */
	public void zoomInOut(Rectangle zr) {
		_zoomBox = zr;
		if (getSize().width == zr.width && getSize().height == zr.height) {
			_changeZoom = false;
		} else {
			_changeZoom = true;
		}
		setZoomFit(false);
		setChangePan(false);
		setUpdateBathymetry(true);
		setUpdateNetwork(true);
		setUpdateLandmark(true);
		setUpdateCanvas(true);
		redoNextPaint();
		repaint();
		// if you want the mode to be turned off when...
		// if(_gui.getZoomBoxMode()){
		// _gui.pressZoomBoxButton();
		// }
	}// zoomInOut

	/**
	 * 
	 */
	public void undoZoom() {
		setUndoZoom(true);
		setUpdateCanvas(true);
		redoNextPaint();
		repaint();
		// setUndoZoom(false);
	}

	public void setUndoZoom(boolean b) {
		_undoZoom = b;
	}

	public boolean getUndoZoom() {
		return _undoZoom;
	}

	/**
	 * called by NetworkInteractor.mouseReleased
	 */
	public void zoomPan(int xi, int yi, int xf, int yf) {
		int deltaX = -(xf - xi);
		int deltaY = yf - yi;
		// add to Centerx, y?
		_changePan = true;
		setZoomPanOffsets(new Dimension(deltaX, deltaY));
		setZoomFit(false);
		setChangeZoom(false);

		setUpdateBathymetry(true);
		setUpdateNetwork(true);
		setUpdateLandmark(true);
		setUpdateCanvas(true);
		redoNextPaint();
		repaint();

		// if(zoomModeOff)
		// if(_gui.getZoomPanMode()){
		// _gui.pressZoomPanButton();
		// }
	}// zoomPan

	public void setChangeZoom(boolean b) {
		_changeZoom = b;
	}

	public void setChangePan(boolean b) {
		_changePan = b;
	}

	/**
	 * true if update is needed, i.e. GraphicElement layout + paint
	 */
	protected boolean _updateNeeded = true;
	/**
	 * allow double buffering
	 */
	protected boolean _doubleBuffer = false;
	/**
	 * stores the previous layout size of GraphicElement
	 */
	protected Rectangle oldR = new Rectangle(0, 0, 0, 0);
	/**
	 * stores the current image of GraphicElement if double buffered
	 */
	protected BathymetryPlot _plotter;
	protected NetworkPlot _networkPlotter;
	protected DigitalLineGraph _dlg;
	protected DigitalLineGraphPlot _dlgPlotter;
	protected boolean _updateBathymetry = false;
	protected boolean _updateNetwork = false;
	protected boolean _updateLandmark = false;
	protected boolean _updateDlg = false;
	protected String _name = null;

	protected Dimension _dim = null;
	protected Image _geImage;
	protected Image _backgroundImage;
	protected Image _networkImage;
	protected Network _net;
	protected Landmark _landmark;
	protected LandmarkPlot _landmarkPlotter;
	protected CsdpFrame _gui;
	protected static final boolean DEBUG = false;
	protected Rectangle _zoomBox;
	protected boolean _changeZoom = false;
	protected boolean _changePan = false;
	protected boolean _useZoomFit = false;
	protected Dimension _zoomPanOffsets;
	private boolean _undoZoom = false;

	protected void setZoomPanOffsets(Dimension offsets) {
		_zoomPanOffsets = offsets;
	}

	protected Dimension getZoomPanOffsets() {
		return _zoomPanOffsets;
	}

	protected boolean getZoomFit() {
		return _useZoomFit;
	}

	protected void setZoomFit(boolean b) {
		_useZoomFit = b;
	}

	Dimension _ps = new Dimension();
	Rectangle _bounds = new Rectangle();
	/**
	 * used for JOptionPane
	 */
	private Object[] _options = { "OK" };
}// class PlanViewCanvas
