package DWR.CSDP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DWR.CSDP.dialog.MessageDialog;

public class HelpMenu {

	/**
	 * display license agreement
	 *
	 * @author
	 * @version $Id:
	 */
	public class HAbout implements ActionListener {

		// ResizableStringArray _licenseMessage = new ResizableStringArray();
		String _licenseMessage = null;
		/**
		 * the maximum number of characters in a line
		 */
		private final int _maxWidth = 90;

		int i = 0;

		public HAbout(CsdpFrame gui) {
			_gui = gui;
			_licenseMessage = "Cross-section Development Program(CSDP) Version " + CsdpFunctions.getVersion() + "\n";
			i++;
			_licenseMessage += "Copyright (C) 1998 State of California, Department of Water" + "\n";
			i++;
			_licenseMessage += "Resources." + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "This program is licensed to you under the terms of the GNU General" + "\n";
			i++;
			_licenseMessage += "Public License, version 2, as published by the Free Software Foundation." + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "You should have received a copy of the GNU General Public License" + "\n";
			i++;
			_licenseMessage += "along with this program; if not, contact Brad Tom, below," + "\n";
			i++;
			_licenseMessage += "or the Free Software Foundation, 675 Mass Ave, Cambridge, MA" + "\n";
			i++;
			_licenseMessage += "02139, USA." + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA" + "\n";
			i++;
			_licenseMessage += "DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS \"AS IS\" AND ANY" + "\n";
			i++;
			_licenseMessage += "EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE" + "\n";
			i++;
			_licenseMessage += "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR" + "\n";
			i++;
			_licenseMessage += "PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA" + "\n";
			i++;
			_licenseMessage += "DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR" + "\n";
			i++;
			_licenseMessage += "ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR" + "\n";
			i++;
			_licenseMessage += "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT" + "\n";
			i++;
			_licenseMessage += "OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR" + "\n";
			i++;
			_licenseMessage += "BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF" + "\n";
			i++;
			_licenseMessage += "LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT" + "\n";
			i++;
			_licenseMessage += "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE" + "\n";
			i++;
			_licenseMessage += "USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH" + "\n";
			i++;
			_licenseMessage += "DAMAGE." + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "For more information, contact:" + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "Brad Tom" + "\n";
			i++;
			_licenseMessage += "California Dept. of Water Resources" + "\n";
			i++;
			_licenseMessage += "Modeling Support Office, Delta Modeling Section" + "\n";
			i++;
			_licenseMessage += "1516 Ninth Street" + "\n";
			i++;
			_licenseMessage += "Sacramento, CA  95814" + "\n";
			i++;
			_licenseMessage += "Bradley.Tom@water.ca.gov" + "\n";
			i++;
			_licenseMessage += "" + "\n";
			i++;
			_licenseMessage += "or see our home page: http://modeling.water.ca.gov/" + "\n";
			i++;
		}// HAbout

		public void actionPerformed(ActionEvent e) {
			MessageDialog mm = new MessageDialog(_gui, "About CSDP", _licenseMessage, true, false, _maxWidth, i);
			mm.setVisible(true);
		}
	}// class HAbout

	protected CsdpFrame _gui = null;
}// class FileMenu
