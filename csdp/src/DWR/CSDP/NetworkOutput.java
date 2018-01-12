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
 * Write ascii and binary network data
 *
 * @author
 * @version $Id: NetworkOutput.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp $
 */
public abstract class NetworkOutput {

public static final boolean DEBUG = false;

  /**
   * Make instance of subclass of NetworkOutput
   */
public static NetworkOutput getInstance(String directory, String filename, 
					String filetype, Network data) {
  _directory = directory;
    if((_directory.substring(_directory.length()-1,_directory.length())).
       equals(File.separator) == false){
	_directory += File.separator;
    }
  _filename = filename;
  _filetype = filetype;
  NetworkOutput output = null;
  if (_filetype.equals(ASCII_TYPE)) {
      output = new NetworkAsciiOutput(data);
  }
  else if(_filetype.equals(EXPORT_TYPE)){
      output = new NetworkAsciiStationElevationOutput(data);
  }
  else if(_filetype.equals(EXPORT_TYPE_3D)){
      output = new NetworkAsciiOutput(data);
  }
  else {// throw new IllegalInputFileException(msg);
     System.out.println("Error in NetworkOutput:  filetype "+_filetype+" not recognized");
     _filetype = null;
  }
  return output;
} //getInstance

  /**
   * Calls appropriate write method to write network data
   */
public boolean writeData(){
  boolean success = false;
  boolean backupSuccess = CsdpFunctions.backupFile
      (_directory, _filename, _filetype);
  if(backupSuccess){
      open();
      success = write();
      close();
  }
  return success;
}

  /**
   * Open file
   */
protected abstract void open();
  /**
   * write file
   */
protected abstract boolean write();
  /**
   * Close file
   */
protected abstract void close();

  /**
   * Convert x and y values from meters(UTM) to feet
   */
protected float feetToMeters(float value){
  final float FEET_TO_METERS=1/3.28084f;
  return FEET_TO_METERS * value;
}

    public void setChannelLengthsOnly(boolean b){
	_channelLengthsOnly = b;
    }

    public void set3DOutput(boolean b){
	_3DOutput = b;
    }
    protected boolean get3DOutput(){
	return _3DOutput;
    }


protected static final String ASCII_TYPE = "cdn";
protected static final String EXPORT_TYPE = "se";
protected static final String EXPORT_TYPE_3D = "3dn";
  //protected static final String BINARY_TYPE = "cdp";
protected static String _filename = null; // part of filename before the first dot
protected static String _filetype = null; // filename extension (after first dot)
protected static String _directory = null;
    protected boolean _channelLengthsOnly = false;
    protected boolean _3DOutput = false;
}//class NetworkOutput
