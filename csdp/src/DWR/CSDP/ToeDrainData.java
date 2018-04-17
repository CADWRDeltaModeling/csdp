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

import java.util.Hashtable;

/**
 * Stores the Toe Drain data that are used for open water area calculations
 *
 * @author
 * @version $Id:
 */
public class ToeDrainData {
	private static final boolean DEBUG = false;
	private int _numXsects;
	private ResizableStringArray _xsectName = new ResizableStringArray();
	private Hashtable _station = new Hashtable();
	private Hashtable _elevation = new Hashtable();

	public void printAll() {
		System.out.println("xsect     station     elevation");
		String name = null;
		for (int i = 0; i <= getNumXsects() - 1; i++) {
			name = getXsectName(i);
			System.out.println(name + "  " + getStationFeet(name) + "  " + getElevationFeet(name));
		}
	}

	public void addName(String name) {
		_xsectName.put(_numXsects, name);
		_numXsects++;
	}

	public String getXsectName(int index) {
		return _xsectName.get(index);
	}

	public int getNumXsects() {
		return _numXsects;
	}

	public void setStationFeet(String name, double station) {
		_station.put(name, new Double(station));
	}

	public void setElevationFeet(String name, double elevation) {
		_elevation.put(name, new Double(elevation));
	}

	public double getStationFeet(String name) {
		double returnValue = -Double.MAX_VALUE;
		if (_station.get(name) != null) {
			returnValue = ((Double) (_station.get(name))).doubleValue();
		}
		return returnValue;
	}

	public double getElevationFeet(String name) {
		double returnValue = -Double.MAX_VALUE;
		if (_elevation.get(name) != null) {
			returnValue = ((Double) (_elevation.get(name))).doubleValue();
		}
		return returnValue;
	}

} // class ToeDrainData
