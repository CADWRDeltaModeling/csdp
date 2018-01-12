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
import DWR.CSDP.semmscon.UseSemmscon;
import java.io.*;
import java.awt.*;
import java.util.*;

/**
 * Landmarks are symbols with labels.  Landmarks have x and y UTM coordinates
 *
 * @author
 * @version
 */
public class DigitalLineGraph {

    /**
     * Constructor
     */
    public DigitalLineGraph(CsdpFrame gui){
	_warningDialog = new OkDialog(gui, "warning", true);
    }

  /**
   * Returns number of Landmarks
   */
  public int getNumLines(){
    return _numLines;
  }//getNumLines
  
  /**
   * Stores number of lines
   */
  public void putNumLines(int value){
    _numLines=value;
  }//putNumLines

  /**
   * adds a line
   */
  public void addLine(String name){
    DigitalLineGraphLine line = new DigitalLineGraphLine(name);
    _dlgLineTable.put(name, line);
    putDigitalLineGraphLineName(_numLines, name);
    _numLines++;
    if(DEBUG)System.out.println("adding line number "+_numLines);
  }//addLine

    private void putDigitalLineGraphLineName(int index, String name){
	_dlgLineNames.put(index, name);
    }

    public String getDigitalLineGraphLineName(int index){
	return _dlgLineNames.get(index);
    }

    public DigitalLineGraphLine getLine(String name){
	return (DigitalLineGraphLine)_dlgLineTable.get(name);
    }
    public DigitalLineGraphLine getLine(int index){
	return (DigitalLineGraphLine)
	    //	    _dlgLineTable.get((new Integer(index)).toString());
	    _dlgLineTable.get(getDigitalLineGraphLineName(index));
    }

  /**
   * removes DigitalLineGraphLine from hashtable
   */
  public void removeDigitalLineGraphLine(String name){
    Object value = _dlgLineTable.remove(name);
    _numLines--;
  }

    /**
     * compare metadata object to default metadata values.
     * If different, convert coordinates using semmscon.
     */
    public void convertToBathymetryDatum(){
  	//hor and ver units should now be stored in US Survey feet.
  	short utm83_units = 1;
  	short utm27_units = 1;
  	short utm83_zone = 10;
  	short utm27_zone = 10;
  	CsdpFileMetadata m = CsdpFunctions.getDigitalLineGraphMetadata();
  	CsdpFileMetadata bm = CsdpFunctions.getBathymetryMetadata();
  	int bathymetryHDatum = bm.getHDatum();
  	int currentHDatum = m.getHDatum();
  	int currentZone = m.getHZone();
  	int bathymetryZone = bm.getHZone();

	boolean sameZone = false;
	boolean utm83to27 = false;
	boolean utm27to83 = false;

  	if(currentZone != bathymetryZone){
	    sameZone=false;
  	    //zone always expected to be 10
  	    _warningDialog.setMessage("ERROR in DigitalLineGraph.convertToBathymetryDatum: Coordinates need "+
				      "to be converted, but I can't convert them. "+
				      "DigitalLineGraph.convertToBathymetryDatum: currentZone "+
				      "is different from bathymetry zone.  They should be the same!");
	    _warningDialog.setVisible(true);
  	}else{
	    sameZone=true;
	}

	if(bathymetryHDatum!=currentHDatum){
	    if(bathymetryHDatum==CsdpFileMetadata.UTMNAD83){ 
		if(currentHDatum==CsdpFileMetadata.UTMNAD27){
		    utm27to83=true;
		}else{
		    _warningDialog.setMessage("DigitalLineGraph horizontal datum is different from "+
					    "bathymetry horizontal datum, but I don't know "+
					    "how to convert the DigitalLineGraph datum,"+
					    currentHDatum+ ", to the bathymetry datum, "+
					    bathymetryHDatum);
		    _warningDialog.setVisible(true);
		}//if need to convert from utm nad27
	    }else if(bathymetryHDatum==CsdpFileMetadata.UTMNAD27){
		if(currentHDatum==CsdpFileMetadata.UTMNAD83){
		    utm83to27 = true;
		}else{
		    _warningDialog.setMessage("DigitalLineGraph horizontal datum is different from "+
					    "bathymetry horizontal datum, but I don't know "+
					    "how to convert the DigitalLineGraph datum,"+
					    currentHDatum+ ", to the bathymetry datum, "+
					    bathymetryHDatum);
		    _warningDialog.setVisible(true);
		}
	    }
	}else{
	    //no need to convert horizontal coordinates
	}//need to convert horizontal coordinates?

	if(DEBUG)System.out.println("sameZone, utm83to27, utm27to83="+
				    sameZone+","+ utm83to27+","+ utm27to83);
	/*
	 * Do the conversion if the horizontal datums are in the same zone
	 */ 
  	if(sameZone){
  	    if(m.getHDatum()==bm.getHDatum()){
  		System.out.println("DigitalLineGraph.convertToBathymetryDatum: already in "+
  				   "bathymetry datum. Not converting horizontal coordinates.");
  	    }else{
  		System.out.println("m.getHDatum, bm.getHDatum="+
				   m.getHDatum()+","+bm.getHDatum());

  		_warningDialog.setMessage("Converting DigitalLineGraph ("+ m.getHDatumString()+")"+
					  " to bathymetry datum ("+ bm.getHDatumString()+")");
		_warningDialog.setVisible(true);
		UseSemmscon us = CsdpFunctions.getUseSemmscon();
		//for each DigitalLineGraph, convert coordinates

		DigitalLineGraphLine line = null;
		DigitalLineGraphPoint point = null;
		DigitalLineGraphPoint point2 = null;
		double x = -Double.MAX_VALUE;
		double y = -Double.MAX_VALUE;
		double x2 = -Double.MAX_VALUE;
		double y2 = -Double.MAX_VALUE;

		for(int i=0; i<=getNumLines()-1; i++){
		    line = getLine(i);
		    for(int j=0; j<=line.getNumPoints()-2; j+=2){
			point = line.getPoint(j);
			point2 = line.getPoint(j+1);
			x = (point.getX());
			y = (point.getY());
			x2= (point2.getX());
			y2= (point2.getY());
			double[] xynew = null;
			double[] xynew2 = null;
			if(utm27to83){
			    xynew = us.utm27ToUtm83((double)x, (double)y, utm27_zone, utm27_units,
						    utm83_zone, utm83_units);
			    xynew2 = us.utm27ToUtm83((double)x2, (double)y2, utm27_zone, utm27_units,
						    utm83_zone, utm83_units);
			}else if(utm83to27){
			    xynew = us.utm83ToUtm27((double)x, (double)y, utm83_zone, utm83_units,
						 utm27_zone, utm27_units);
			    xynew2 = us.utm83ToUtm27((double)x2, (double)y2, utm83_zone, utm83_units,
						 utm27_zone, utm27_units);
			}
			point.putX((double)xynew[CsdpFunctions.xIndex]);
			point.putY((double)xynew[CsdpFunctions.yIndex]);
			point2.putX((double)xynew2[CsdpFunctions.xIndex]);
			point2.putY((double)xynew2[CsdpFunctions.yIndex]);
		    }//for each pair of points
		}//for each dlg line
	    }//if need to change datum
	}//if same zone or not (if not, won't do datum change)
	//set dlg datum to bath datum
	m.setHDatum(bm.getHDatum());
    }//convertToBathymetryDatum

//    /**
//     * returns x value
//     */
//    public float getX(String pointName){
//      DigitalLineGraphPoint dlgPoint = 
//  	(DigitalLineGraphPoint)(_dlgLineTable.get(pointName));
//      if(dlgPoint != null) return dlgPoint.x;
//      else return -CsdpFunctions.BIG_FLOAT;
//    }

//    /**
//     * returns y value
//     */
//    public float getY(String pointName){
//      DigitalLineGraphPoint dlgPoint = 
//  	(DigitalLineGraphPoint)(_dlgLineTable.get(pointName));
//      if(dlgPoint != null) return dlgPoint.y;
//      else return -CsdpFunctions.BIG_FLOAT;
//    }

    /**
     * stores value of minimum x coordinate.
     */
    public void putMinXFeet(double value){
      _minX = value;
    }

    /**
     * stores value of maximum x coordinate.
     */
    public void putMaxXFeet(double value){
      _maxX = value;
    }
    /**
     * stores value of minimum y coordinate.
     */
    public void putMinYFeet(double value){
      _minY = value;
    }
    /**
     * stores value of maximum y coordinate.
     */
    public void putMaxYFeet(double value){
      _maxY = value;
    }
    /**
     * returns value of minimum x coordinate
     */
    protected double getMinXFeet(){
      return _minX;
    }
    /**
     * returns value of maximum x coordinate
     */
    protected double getMaxXFeet(){
      return _maxX;
    }
    /**
     * returns value of minimum y coordinate
     */
    protected double getMinYFeet(){
      return _minY;
    }
    /**
     * returns value of maximum y coordinate
     */
    protected double getMaxYFeet(){
      return _maxY;
    }

  protected int _numLines=0;
  protected Hashtable _dlgLineTable = new Hashtable();
  protected ResizableStringArray _dlgLineNames = new ResizableStringArray();
    protected double _minX = 0.0;
    protected double _maxX = 0.0;
    protected double _minY = 0.0;
    protected double _maxY = 0.0;
    private final boolean DEBUG = false;
    private OkDialog _warningDialog;
}//class DigitalLineGraph
