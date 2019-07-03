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
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import DWR.CSDP.dialog.FileIO;

/**
 * calls methods for editing and viewing cross-sections
 *
 * @author
 * @version $Id:
 */
public class XsectMenu {
	public XsectMenu(App app, CsdpFrame gui, NetworkInteractor ni) {
		_app = app;
		_gui = gui;
		_ni = ni;
		_fSaveFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
	}

	public void setNetwork(Network net) {
		_net = net;
	}

	App _app;
	CsdpFrame _gui;
	NetworkInteractor _ni;
	Network _net;

	/**
	 * View selected cross-section
	 *
	 * @author
	 * @version $Id:
	 */
	public class XView implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (DEBUG)
				System.out.println("net=" + _net);
			_gui.pressSelectCursorAkaArrowButton();
			Xsect xsect = _net.getSelectedXsect();
			String centerlineName = _net.getSelectedCenterlineName();
			int xsectNum = _net.getSelectedXsectNum();
			// _gui.addXsectWindow("Xsect", new XsectGraph());
			if (DEBUG)
				System.out.println("about to call app.viewxsect: app, xsect=" + _app + "," + xsect);
			if (DEBUG)
				System.out.println(centerlineName + "_" + xsectNum);
			if (_app._xsectGraph.containsKey(centerlineName + "_" + xsectNum)) {
				JOptionPane.showMessageDialog(_gui, "You are already viewing that cross-section!", "", JOptionPane.ERROR_MESSAGE);
				// ((XsectGraph)(_app._xsectGraph.get(centerlineName+"_"+xsectNum))).setVisible(true);
			} else {
				_app.viewXsect(xsect, centerlineName, xsectNum, CsdpFunctions.getXsectThickness());
			} // if
		}// viewXsect
	} // class XView

	/**
	 * Extract data for cross-section Instead of displaying data, send to file
	 *
	 * @author
	 * @version $Id:
	 */
	public class XExtractData extends FileIO implements ActionListener {

		public XExtractData(CsdpFrame gui) {
			super(gui, _saveDialogMessage, _saveErrorMessage, _saveSuccessMessage, _saveFailureMessage, false,
					_saveExtensions, _numSaveExtensions);
			_jfc.setDialogTitle(_saveDialogMessage);
			_jfc.setApproveButtonText("Create File");
			_jfc.addChoosableFileFilter(_fSaveFilter);
			_jfc.setFileFilter(_fSaveFilter);

		}

		/**
		 * called by superclass.
		 */
		protected String getFilename() {
			int numLines = 0;
			String filename = null;
			int filechooserState = -Integer.MAX_VALUE;
			if (CsdpFunctions.getOpenDirectory() != null) {
				// _fd.setDirectory(CsdpFunctions.getOpenDirectory());
				_jfc.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println();

			filechooserState = _jfc.showOpenDialog(this._gui);
			if (filechooserState == JFileChooser.APPROVE_OPTION) {
				filename = _jfc.getName(_jfc.getSelectedFile());
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

		public boolean accessFile() {
			Xsect xsect = _net.getSelectedXsect();
			String centerlineName = _net.getSelectedCenterlineName();
			int xsectNum = _net.getSelectedXsectNum();
			if (DEBUG) {
				System.out.println("about to call app.extractXsectData: app, xsect=" + _app + "," + xsect);
				System.out.println(centerlineName + "_" + xsectNum);
			}
			boolean success = _app.extractXsectData(_filename, _filetype, xsect, centerlineName, xsectNum,
					CsdpFunctions.getXsectThickness());
			return success;
		}// extractXsect
	} // class XView

	/**
	 * Adjust length of cross-section line
	 *
	 * @author
	 * @version $Id:
	 */
	public class XAdjustLength implements ActionListener {
		ScrollbarDialog _s;
		double _length;
		double _newLength;

		public XAdjustLength() {
			int min = 0;
			int max = 3;
			int factor = 10;
			int numScrollbars = 1;
			double[] initialValue = new double[1];
			initialValue[0] = 1;

			_s = new ScrollbarDialog(_gui, "Select Factor for adjusting cross-section line length", true, numScrollbars,
					initialValue, min, max, factor);
		}

		public void actionPerformed(ActionEvent e) {
			_gui.pressSelectCursorAkaArrowButton();

			Xsect xsect = _net.getSelectedXsect();
			_length = xsect.getXsectLineLengthFeet();
			_newLength = -Double.MAX_VALUE;
			_s.setVisible(true);
			double adjustmentFactor = _s.getScrollbarValue(0);
			_newLength = _length * adjustmentFactor;

			if (DEBUG)
				System.out.println(
						"length, adjustmentFactor, newlength=" + _length + "," + adjustmentFactor + "," + _newLength);

			if (_length == _newLength) {
			} else {
				xsect.putXsectLineLengthFeet(_newLength);
				_gui.getPlanViewCanvas(0).redoNextPaint();
				// removed for conversion to swing
				_gui.getPlanViewCanvas(0).repaint();
				_net.setIsUpdated(true);
			}
		}

	}// Xadjustlength

	protected static final boolean DEBUG = false;
	protected static final String _saveDialogMessage = "Select a directory and enter a filename(.prn)";
	protected static final String _saveErrorMessage = "Only .prn extension allowed";
	protected static final String[] _saveExtensions = { "prn" };
	protected static final int _numSaveExtensions = 1;

	protected static final String _saveSuccessMessage = "Saved bathymetry file";
	protected static final String _saveFailureMessage = "ERROR:  BATHYMETRY FILE NOT SAVED!";
	protected static final String _openSuccessMessage = "";
	protected static final String _openFailureMessage = "ERROR: couldn't open bathymetry file";

	CsdpFileFilter _fSaveFilter;
}// class XsectMenu
