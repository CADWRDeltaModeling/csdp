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

import DWR.CSDP.semmscon.UseSemmscon;

/**
 * Cross-section development Program
 *
 * @author
 * @version $Id: Csdp.java,v 1.4 2005/04/08 00:21:53 btom Exp $
 */
public class Csdp {

	/**
	 * main method for Csdp
	 */
	public static void main(String args[]) {
		try {
			App app = new App();
			// if(args.length > 0){
			// CsdpFunctions._csdpHome=args[0];
			// }else{
			// CsdpFunctions._csdpHome="";
			// }

			CsdpFrame gui = new CsdpFrame(app);
			System.out.println("Cross-Section Development Program Version " + CsdpFunctions.getVersion());
		} catch (Exception e) {
			System.out.println("Error in Csdp.main:");
			e.printStackTrace();
		}

		/**
		 * Make CsdpFileMetadata object which has default datum: horizontal: UTM
		 * Zone 10 Nad 83 meters vertical: NAVD1988 U.S. Survey Feet
		 */

		CsdpFileMetadata b = new CsdpFileMetadata();
		b.setHDatum(CsdpFileMetadata.UTMNAD83);
		b.setHZone(10);
		b.setHUnits(CsdpFileMetadata.METERS);
		b.setVDatum(CsdpFileMetadata.NAVD1988);
		b.setVUnits(CsdpFileMetadata.USSURVEYFEET);
		CsdpFunctions.setBathymetryMetadata(b);

		/**
		 * Test semmscon
		 */
		UseSemmscon us = new UseSemmscon();
		try {
			us.init_convert();
		} catch (java.lang.UnsatisfiedLinkError e) {
			System.out.println("java.lang.UnsatisfiedLinkError caught. e=" + e);
		} // try/catch

		final short utm83_units = 3;
		final short utm83_zone = 10;
		final short utm27_zone = 10;
		final short utm27_units = 3;

		final double utm27x = 629355.0;
		final double utm27y = 4.199384e6;
		/**
		 * IMPORTANT: It seems that utm83ToUtm27 must be called first in order
		 * for utm28ToUtm83 to work properly
		 */

		double[] utm83 = us.utm83ToUtm27(utm27x, utm27y, utm27_zone, utm27_units, utm83_zone, utm83_units);
		utm83 = us.utm27ToUtm83(utm27x, utm27y, utm27_zone, utm27_units, utm83_zone, utm83_units);
		System.out.println("---------- Semmscon Test Results -----------");
		System.out.println("Input: utm27x, utm27y=" + utm27x + "," + utm27y);
		System.out.println("NOTE:  the following values should be ");
		System.out.println(" 629258.5972470464 and 4199579.604951745");
		System.out.println("If they are not, there may be a problem ");
		System.out.println("with the installation of the coordinate ");
		System.out.println("conversion routines");
		System.out.println("result of utm27ToUtm83:\n");
		System.out.println("utm83x, utm83y=" + utm83[0] + "," + utm83[1]);
		System.out.println("--------------------------------------------");
		CsdpFunctions.setUseSemmscon(us);
	}// main

}// class Csdp
