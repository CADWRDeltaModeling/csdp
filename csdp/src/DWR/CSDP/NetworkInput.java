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
import DWR.CSDP.dialog.*;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;

/**
 * Read ascii and binary network data
 *
 * @author
 * @version $Id: NetworkInput.java,v 1.3 2003/07/23 20:54:53 btom Exp $
 */
public abstract class NetworkInput {
    protected OkDialog _noMetadataDialog = new OkDialog(_gui, "This network file has no metadata. "+
							"UTM zone 10 NAD 27, NGVD 1929 will be assumed.",true);
    protected OkDialog _errorDialog = new OkDialog(_gui, "error message", true);

    public static final boolean DEBUG = false;
    protected static Network _net;

    /**
     * Make instance of subclass of BathymetryInput
     */
    public static NetworkInput getInstance(CsdpFrame gui, String directory, 
					   String filename, String filetype){
	_gui=gui;
	_directory = directory;
	_net = new Network("delta", _gui);
	if((_directory.substring(_directory.length()-1,_directory.length())).
	   equals(File.separator) == false){
	    _directory += File.separator;
	}

	_filename = filename;
	_filetype = filetype;
	
	NetworkInput input = null;
	if (_filetype.equals(ASCII_TYPE)) {
	    input = new NetworkAsciiInput();
	}
	//  else if (_filetype.equals(BINARY_TYPE)) {
	//    input = new NetworkBinaryInput();
	//}
	else {// throw new IllegalInputFileException(msg);
	    System.out.println();
	    _filetype = null;
	}
	return input;
    } //getInstance
    
    /**
     * ;HorizontalDatum:  UTMNAD27
     * ;HorizontalZone:   10
     * ;HorizontalUnits:  Meters
     * ;VerticalDatum:    NGVD29
     * ;VerticalUnits:    USSurveyFeet
     * ;Filetype: network
     * ;NumElements: xxxx
     */
    protected void parseMetadata(String line, CsdpFileMetadata m){
	StringTokenizer t = new StringTokenizer(line, " :");
	String nextToken=t.nextToken();
	nextToken=t.nextToken();
	if(line.indexOf("HorizontalDatum")>=0){
	    if(nextToken.equalsIgnoreCase("UTMNAD27")) m.setHDatum(CsdpFileMetadata.UTMNAD27);
	    else if(nextToken.equalsIgnoreCase("UTMNAD83")) m.setHDatum(CsdpFileMetadata.UTMNAD83);
	    else{
		_errorDialog.setMessage("HorizontalDatum " + nextToken + 
					" not recognized.  using default horizontal datum.");
		_errorDialog.setVisible(true);
	    }
	}else if(line.indexOf("HorizontalZone")>=0){
	    if(nextToken.equalsIgnoreCase("10")) m.setHZone(10);
	    else{
		_errorDialog.setMessage("HorizontalZone " + nextToken + 
					" not recognized.  using default horizontal zone.");
		_errorDialog.setVisible(true);
	    }
	}else if(line.indexOf("HorizontalUnits")>=0){
	    if(nextToken.equalsIgnoreCase("Meters")) m.setHUnits(CsdpFileMetadata.METERS);
	    else if(nextToken.equalsIgnoreCase("Feet")) m.setHUnits(CsdpFileMetadata.USSURVEYFEET);
	    else{
		_errorDialog.setMessage("HorizontalUnits " + nextToken + 
					" not recognized.  using default horizontal units.");
		_errorDialog.setVisible(true);
	    }
	}else if(line.indexOf("VerticalDatum")>=0){
	    if(nextToken.equalsIgnoreCase("NGVD29")) m.setVDatum(CsdpFileMetadata.NGVD1929);
	    else if(nextToken.equalsIgnoreCase("NAVD88")) m.setVDatum(CsdpFileMetadata.NAVD1988);
	    else{
		_errorDialog.setMessage("VerticalDatum " + nextToken + 
					" not recognized.  using default vertical datum.");
		_errorDialog.setVisible(true);
	    }
	}else if(line.indexOf("VerticalUnits")>=0){
	    if(nextToken.equalsIgnoreCase("USSurveyFeet")) m.setVUnits(CsdpFileMetadata.USSURVEYFEET);
	    else if(nextToken.equalsIgnoreCase("meters")) m.setVUnits(CsdpFileMetadata.METERS);
	    else{
		_errorDialog.setMessage("VerticalUnits " + nextToken + 
					" not recognized.  using default vertical units.");
		_errorDialog.setVisible(true);
	    }
	}else if(line.indexOf("Filetype")>=0){
	    //do nothing...
	}else if(line.indexOf("NumElements")>0){
	    try{
		//    numLines = new Integer(nextToken);
		int numLines = Integer.parseInt(nextToken);
		////		_net.putNumCenterlines(numLines);
		_pd.numCenterlines=numLines;
		m.setNumElements(numLines);
	    } catch(java.lang.NumberFormatException e) {
		_errorDialog.setMessage("Error reading metadata line. Expecting NumElements. line="+line);
		_errorDialog.setVisible(true);
	    }//try
	}else{
	    _errorDialog.setMessage("unable to parse metadata line: " + 
				    line +". File may not be loaded correctly");
	    _errorDialog.setVisible(true);
	}
    }//parseMetadata

  /**
   * Calls appropriate read method to read Network data
   */
public Network readData(){
  open();
  read();
  close();
  _net.convertToBathymetryDatum();
  return _net;
}

  /**
   * Open file
   */
protected abstract void open();
  /**
   * Read file
   */
protected abstract void read();
  /**
   * Close file
   */
protected abstract void close();

  /**
   * copy parsed centerline values to network object
   */
protected void storeCenterline(){
  String name = _pd.centerlineName;
  _net.addCenterline(name);

  for(int i=0; i<=_pd.numCenterlinePoints-1; i++){
    _net.getCenterline(name).addCenterlinePoint();
    _net.getCenterline(name).getCenterlinePoint(i).putXFeet(_pd.xUTM.get(i));
    _net.getCenterline(name).getCenterlinePoint(i).putYFeet(_pd.yUTM.get(i));
}
  //  for(int i=0; i<=_pd.numXsect-1; i++){
  //_net.getCenterline(name).addXsect();
  // }
}

  /**
   * Copy parsed cross-section values to network object
   */
protected void storeXsectLine(int xsIndex){
  String name = _pd.centerlineName;
  if(DEBUG)System.out.println("storing xsect data for centerline "+name);
  if(DEBUG)System.out.println("_net="+_net);
  //  _net.getCenterline(name).getXsect(xsIndex).
  Centerline centerline = _net.getCenterline(name);
  Xsect xsect = null;
  if(_pd.distAlongCenterline <= centerline.getLengthFeet() && 
     _pd.distAlongCenterline >= 0){
    centerline.addXsect();
    xsect = centerline.getXsect(xsIndex);
    for(int i=0; i<=_pd.numXsectPoints-1; i++){
      xsect.addXsectPoint();
      xsect.getXsectPoint(i).putStationFeet(_pd.station.get(i));
      xsect.getXsectPoint(i).putElevationFeet(_pd.elevation.get(i));
    }
    xsect.putDistAlongCenterlineFeet(_pd.distAlongCenterline);
    xsect.putXsectLineLengthFeet(_pd.xsectLineLength);
    xsect.putMetadata(_pd.metadata);
  }//if cross-section is inside centerline
}//storeXsectLine

protected NetworkParsedData _pd = new NetworkParsedData();  // vector-stores 6 values
protected static String _filename = null; // part of filename before the first dot
protected static String _filetype = null; // filename extension (after first dot)
protected static final String ASCII_TYPE = "cdn";
protected static final String BINARY_TYPE = null;
protected static String _directory = null;
protected static CsdpFrame _gui;
} // class NetworkInput
