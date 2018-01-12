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
import java.io.*;
import java.util.*;

/**
 * writes xsect data to ascii file
 */
public class XsectAsciiOutput extends XsectOutput{
  FileWriter _aOutFile     = null;                // ascii input file
  BufferedWriter _asciiOut = null;
  Xsect _xsect             = null;

  /**
   * assigns data storage object to class variable
   */
  XsectAsciiOutput(Xsect xsect){
    _xsect = xsect;
  }

  /**
   * Open ascii file for writing
   */
protected void open(){
  try {
    if((_directory.substring(_directory.length()-1,_directory.length())).
       equals(File.separator) == false){
	_directory += File.separator;
    }
    _aOutFile = new FileWriter(_directory + _filename+"."+ASCII_TYPE);
    _asciiOut = new BufferedWriter(_aOutFile);
  } catch(IOException e) {
    if (DEBUG) System.out.println("Directory,Filename: "+_directory + _filename);
    if (DEBUG) System.out.println("Filetype: " + _filetype);
    System.out.println
      ("Error ocurred while opening file "+_directory + 
       _filename + _filetype + e.getMessage());
  } // catch()
}

  /**
   * write ascii cross-section data file
   */
protected void write(){
  double aOld;
  double p;
  double rh;
  double xc;
  double zc;
  String elevation       = null;
  String area            = null;
  String areaOld         = null;
  String wettedPerimeter = null;
  String width           = null;
  String hydraulicRadius = null;
  String xCentroid       = null;
  String zCentroid       = null;
  String sLine = null; //for appending station values
  String eLine = null; // for appending elevation values
  double[] uniqueElevations=null;
  int numUnique;
  String line=null;
  String lastElevationString = null;
  double elevationDouble = Double.MAX_VALUE;
  double lastElevationDouble = Double.MAX_VALUE;

  try{
    _asciiOut.write("Cross-section:  "+ _filename + "\n");
    _asciiOut.write
      ("Elev(NGVD)       A          P          W         Rh         Xc         Zc         Aold"+"\n");
    _asciiOut.write
      ("======================================================================================"+"\n");
  } catch(IOException e) {
    System.out.println
      ("Error ocurred while writing headers to file " +_directory + 
       _filename + "."+ASCII_TYPE + e.getMessage());
  } finally {
  } // catch()

  uniqueElevations = _xsect.getUniqueElevations();
  numUnique = _xsect.getNumUniqueElevations();
  
  //use alternate equation for area--based on lower layer's area and width
  //a(z2)=a(z1)+0.5*(w(z2)+w(z1))*(z2-z1)
  double[] a = new double[numUnique];
  double[] w = new double[numUnique];
  w[0]=_xsect.getWidthFeet(uniqueElevations[0]);
  a[0]=0.0;
  for(int i=1; i<=numUnique-1; i++){
      double elev = uniqueElevations[i];
      double lowerElev = uniqueElevations[i-1];
      w[i]=_xsect.getWidthFeet(elev);
      a[i]=a[i-1]+0.5f*(w[i]+w[i-1])*(elev-lowerElev);
  }

  for(int i=numUnique-1; i>=0; i--){
      double elev = uniqueElevations[i];
    p = _xsect.getWettedPerimeterFeet(elev);
    aOld = _xsect.getAreaSqft(elev);
    rh = _xsect.getHydraulicRadiusFeet(elev);
    xc = _xsect.getXCentroidFeet(elev);
    zc = _xsect.getZCentroidFeet(elev);

    elevationDouble = elev;
    elevation       = (lastEleven(elevationDouble,2)).substring(4,11);
    if( elevation.equals(lastElevationString) ||
	elevationDouble > lastElevationDouble ){

	System.out.println("elevationDouble, lastElevationDouble="+elevationDouble+
			   ","+lastElevationDouble);
	System.out.println
	    ("adjusting elevation for cross-section "
	     +_filename+".  Elevation="+elevation+"\n");
	elevationDouble = Double.parseDouble(elevation) - 0.02;
	elevation = (lastEleven(elevationDouble,2)).substring(4,11);
    }

    //For diagnosing the problem Eli found in the area calc
    //if(aOld-a[i]!=0.0)System.out.println("area old and new differ: aold, anew="+aOld+","+a[i]);

//     area            = (lastEleven(a[i],1)).substring(1,11);
//     areaOld         = (lastEleven(aOld,1)).substring(1,11);
    area            = (lastCharacters(a[i],13,3)).substring(1,13);
    areaOld         = (lastCharacters(aOld,13,3)).substring(1,13);
    wettedPerimeter = lastEleven(p,1);
    width           = lastEleven(w[i],1);
    hydraulicRadius = lastEleven(rh,1);
    if(i>0){
      xCentroid       = lastEleven(xc,1);
      zCentroid       = lastEleven(zc,1);
    }
    else if(i==0){
      xCentroid = "        0.0";
      zCentroid = "        0.0";
    }
    //    line = elevation+area+wettedPerimeter+width+hydraulicRadius+xCentroid+zCentroid+areaOld+"\n";
    line = elevation+area+wettedPerimeter+width+hydraulicRadius+xCentroid+zCentroid+"\n";

    try{
      _asciiOut.write(line);
    } catch(IOException e) {
      System.out.println
	("Error ocurred while writing cross-section properties to file " +
	 _directory+ _filename + "."+ASCII_TYPE + e.getMessage());
    } finally {
    } // catch()
    lastElevationString = elevation;
    lastElevationDouble = elevationDouble;
  }//for 

  sLine = "station:  ";
  eLine = "elevation:";
  for(int i=0; i<=_xsect.getNumPoints()-1; i++){
    XsectPoint point = _xsect.getXsectPoint(i);
    sLine += point.getStationFeet()+" ";
    eLine += point.getElevationFeet()+" ";
  }
  try{
    _asciiOut.newLine();
    _asciiOut.write(sLine+"\n");
    _asciiOut.write(eLine+"\n");
  } catch(IOException e) {
    System.out.println
      ("Error ocurred while writing station and elevation coordinates to file "+
       _directory + _filename + "."+ASCII_TYPE + e.getMessage());
  } finally {
  } // catch()
}//write

  /**
   * Close ascii bathymetry data file
   */
protected void close(){
  try{
    _asciiOut.close();
  }catch(IOException e){
    System.out.println
      ("Error ocurred while closing file "+_directory+
       _filename+"."+_filetype+":"+e.getMessage());
  }// catch
}//close

    /*
     * 
     */
    protected String lastCharacters(double value, int stringLength, double numDecimalPlaces){
	int vFi = (int)(value*Math.pow(10.0f,numDecimalPlaces));
	double vFiFf = (double)vFi;
	vFiFf /= Math.pow(10.0f,numDecimalPlaces);
	
	String slString = "";
	for(int i=0; i<stringLength; i++){
	    slString += " ";
	}
	int length = (slString + vFiFf).length();
	String last = (slString + vFiFf).substring(length-stringLength, length);
	return last;
    }

protected String lastEleven(double value, double numDecimalPlaces){
//        Double vF = new Double(value*Math.pow(10.0f,numDecimalPlaces));
//        int vFi = vF.intValue();
//        Double vFiF = new Double(vFi);
//        double vFiFf = vFiF.doubleValue();
//        vFiFf /= Math.pow(10.0f,numDecimalPlaces);

      int vFi = (int)(value*Math.pow(10.0f,numDecimalPlaces));
      double vFiFf = (double)vFi;
      vFiFf /= Math.pow(10.0f,numDecimalPlaces);


  String eleven = "           ";
  int length = (eleven + vFiFf).length();
  String last= (eleven + vFiFf).substring(length-11,length);
  return last;
}

}//class XsectAsciiOutput
