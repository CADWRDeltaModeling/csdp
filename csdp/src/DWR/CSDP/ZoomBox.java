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

import java.awt.Rectangle;
import java.util.Stack;

/**
 * This class handles the zooming in and out on a canvas. It stores the previous
 * zoom state for zooming out.
 *
 * @author
 * @version
 */

public class ZoomBox {
	public ZoomBox(PlanViewCanvas can) {
		_can = can;
	}// constructor

	public void zoomIn(Rectangle zoomBox) {
		// find new plot region
		if (DEBUG)
			System.out.println("zoomIn in ZoomBox called");
		if (DEBUG)
			System.out.println("zoomBox=" + zoomBox);
		// int y = _can.getSize().height - zoomBox.y;
		// zoomBox.y = y;

		_zoomHistory.push(zoomBox);
		_can.zoomInOut(zoomBox);
	}// zoomIn

	public void zoomOut() {
		// find new plot region
		_can.zoomInOut((Rectangle) (_zoomHistory.pop()));
	}// zoomOut

	PlanViewCanvas _can;
	/**
	 * stores zoom history
	 */
	protected Stack _zoomHistory = new Stack();
	private static final boolean DEBUG = true;
}// class ZoomBox
