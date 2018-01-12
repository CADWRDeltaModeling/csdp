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
 * writes landmark data to ascii file
 */
public class LandmarkAsciiOutput extends LandmarkOutput{
  FileWriter _aOutFile           = null;                // ascii input file
  BufferedWriter _asciiOut       = null;
  Landmark _landmark             = null;

    public LandmarkAsciiOutput(Landmark landmark){
	_landmark = landmark;
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
    if (DEBUG) System.out.println("Directory, Filename: " +_directory + _filename);
    if (DEBUG) System.out.println("Filetype: " + _filetype);
    System.out.println
      ("Error ocurred while opening file "+_directory +
       _filename + _filetype + e.getMessage());
  } // catch()
}

  /**
   * write ascii landmark data file
   */
protected boolean write(){
  boolean success = false;
  CsdpFileMetadata lMeta = CsdpFunctions.getLandmarkMetadata();

  try{
      //write metadata
      String nl=Integer.toString(_landmark.getNumLandmarks());
      _asciiOut.write(";HorizontalDatum:  "+lMeta.getHDatumString());
      _asciiOut.newLine();
      _asciiOut.write(";HorizontalZone:   "+lMeta.getHZone());
      _asciiOut.newLine();
      _asciiOut.write(";HorizontalUnits:  "+lMeta.getHUnitsString());
      _asciiOut.newLine();
      _asciiOut.write(";VerticalDatum:    " + lMeta.getVDatumString());
      _asciiOut.newLine();
      _asciiOut.write(";VerticalUnits:    " + lMeta.getVUnitsString());
      _asciiOut.newLine();
      _asciiOut.write(";Filetype:          landmark");
      _asciiOut.newLine();
      _asciiOut.write(";NumElements:      "+nl);
      _asciiOut.newLine();
      //write data
      String versionLine = null;
      Integer numLines=null;
      
      for(Enumeration<String>e = _landmark.getLandmarkNames(); e.hasMoreElements();){
	  String name = e.nextElement();
	  double x = _landmark.getXMeters(name);
	  double y = _landmark.getYMeters(name);
	  String line = x+","+y+","+name;
	  _asciiOut.write(line+"\n");
      }//for i
      _landmark.setIsUpdated(false);
      success = true;
  } catch(IOException e) {
      System.out.println
	  ("Error ocurred while writing file " +_directory + 
	   _filename + "."+ASCII_TYPE + e.getMessage());
  } finally {
  } // catch()
  return success;
}//write

  /**
   * Close ascii bathymetry data file
   */
protected void close(){
  try{
    _asciiOut.close();
  }catch(IOException e){
    System.out.println
      ("Error ocurred while closing file "+_directory + 
       _filename+"."+_filetype+":"+e.getMessage());
  }// catch
}//close

}//class LandmarkAsciiOutput
