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

public class NetworkAsciiInput extends NetworkInput{

    LineNumberReader _asciiIn;
    /**
     * True if it's a version 2.00 or greater file, which 
     * means that it has cross-section metadata.  Also 
     * true if it has file metadata (geodetic datum info.)
     */
    boolean _newFormat = false;
    OkDialog _parseXsectDialog = new OkDialog
	(_gui, "This network file appears to have cross-section metadata, and will be converted to a version "+
	 CsdpFunctions.getVersion()+" file", true);
    OkDialog _parseFirstLineDialog = new OkDialog
	(_gui, "This network file was made with an old version of the CSDP, and will be converted to a version "+
	 CsdpFunctions.getVersion()+" file", true);

  /**
   * Open ascii file
   */
protected void open(){
  FileReader aInFile;
  try {
      if((_directory.substring(_directory.length()-1,_directory.length())).
	 equals(File.separator) == false){
	  _directory += File.separator;
      }
    aInFile = new FileReader(_directory + _filename + "." + _filetype);
    _asciiIn = new LineNumberReader(aInFile);
    if (DEBUG) System.out.println("In ascii open " + _asciiIn);
  } catch(IOException e) {
    if (DEBUG) System.out.println("Directory + Filename: " +_directory + _filename);
    if (DEBUG) System.out.println("Filetype: " + _filetype);
    System.out.println
      ("Error ocurred while opening file "+_directory+
       _filename + _filetype + e.getMessage());
  } // catch()
}

    /**
     * Read ascii network file
     */
    protected void read(){
	int numLines=0;
	CsdpFileMetadata networkMetadata = new CsdpFileMetadata();
	boolean fileHasMetadata = false;
	
	String line = null;
	try {
	    line=_asciiIn.readLine();

	    if(line.indexOf(";")<0){
		fileHasMetadata=false;
		_noMetadataDialog.setVisible(true);
		networkMetadata.setToDefault();
		parseFirstLine(line);
	    }else{
		fileHasMetadata=true;
		_newFormat = true;
		if(DEBUG)System.out.println("parse network metadata");
		for(int i=0; i<=CsdpFunctions.getNumMetadataLines()-1; i++){
		    //first line already read
		    if(i>0) line=_asciiIn.readLine();
		    if(line.indexOf(";")>=0){
			parseMetadata(line, networkMetadata);
		    }else{
			_errorDialog.setMessage("incomplete network metadata! there should be "+
						CsdpFunctions.getNumMetadataLines()+" lines.  "+
						"The following line was expected to be a metadata line:"+
						line);
			_errorDialog.setVisible(true);
		    }//if it's a metadata line (should be)
		}//read all metadata lines.
	    }
	    if(DEBUG)System.out.println("done parsing network metadata.hdatum,hzone,hunits,vdatum,vunits, numCenterlines="+
			       networkMetadata.getHDatumString()+","+
			       networkMetadata.getHZone()+","+
			       networkMetadata.getHUnitsString()+","+
			       networkMetadata.getVDatumString()+","+
			       networkMetadata.getVUnitsString()+","+
			       networkMetadata.getNumElements());
	    CsdpFunctions.setNetworkMetadata(networkMetadata);
	}catch(IOException e){
	    System.out.println("ERROR in NetworkAsciiInput.read");
	}

	try {
	    //	    String line=null;
// 	    line = _asciiIn.readLine();
// 	    parseFirstLine(line);
// 	    System.out.println("number of pts in ascii network file=" +  _pd.numCenterlines);
	    if(_newFormat){
		String centerlineLines = null;
		String xsectLines = null;
		for(int i=0; i<=_pd.numCenterlines-1; i++){
		    centerlineLines = null;
		    centerlineLines = "";
		    line = _asciiIn.readLine();
		    centerlineLines += line+"\n";
		    if(DEBUG)System.out.println("about to parse Centerline header="+line);
		    int numCenterlinePoints = parseCenterlineHeader(line);
		    for(int j=0; j<=numCenterlinePoints-1; j++){
			line = _asciiIn.readLine();
			centerlineLines += line +"\n";
		    }//for each centerline point
		    //read the number of xsects
		    line = _asciiIn.readLine();
		    centerlineLines += line;
		    if(DEBUG)System.out.println("about to parse centerline="+centerlineLines);
		    parseCenterline(i, centerlineLines);
		    storeCenterline();
		    //read cross-section data
		    for(int xs=0; xs<=_pd.numXsect-1; xs++){
			xsectLines = null;
			xsectLines = "";
			line=_asciiIn.readLine();
			if(DEBUG)System.out.println("about to parse xsect header="+line);
			int numXsectPoints = parseXsectHeader(line);
			xsectLines += numXsectPoints+"\n";
			for(int j=0; j<=numXsectPoints-1; j++){
			    line=_asciiIn.readLine();
			    xsectLines += line +"\n";
			}
			line=_asciiIn.readLine();
			xsectLines += line+"\n";
			line=_asciiIn.readLine();
			xsectLines += line+"\n";
			if(DEBUG)System.out.println("about to parse xsect line="+xsectLines);
			parseXsectLine(xs, xsectLines);
			storeXsectLine(xs);
		    }//for each cross-section
		    line = _asciiIn.readLine();  //skip blank line
		}//for each centerline
	    }else{ //it's the old format--all points in one line, no metadata, etc.
		for(int i=0; i<=_pd.numCenterlines-1; i++){
		    line = _asciiIn.readLine();
		    parseCenterline(i,line);
		    storeCenterline();
		    if(DEBUG)System.out.println("centerline="+line);
		    for(int j=0; j<=_pd.numXsect-1; j++){
			line = _asciiIn.readLine();
			parseXsectLine(j, line);
			storeXsectLine(j);
		    }
		    line = _asciiIn.readLine(); // skip blank line
		} // for
	    }//else
	} catch(IOException e) {
	    System.out.println
		("Error ocurred while reading file " +_directory+ 
		 _filename + ".prn:" + e.getMessage());
	} finally {
	    close();
	} // catch()
    }//read

  /**
   * Close ascii Network data file
   */
protected void close(){
  try{
    _asciiIn.close();
  }catch(IOException e){
    System.out.println
      ("Error ocurred while closing file "+_directory+
       _filename+_filetype+":"+e.getMessage());
  }// catch
}

    /**
     * parses centerline header to determine number of centerline points
     */
    protected int parseCenterlineHeader(String unparsedLine){
	StringTokenizer t = new StringTokenizer(unparsedLine, " ,\"\042\011\n");
	int np = -Integer.MAX_VALUE;
	String nextToken = t.nextToken();
	nextToken = t.nextToken();
	try{
	    np = Integer.parseInt(nextToken);
	}catch(java.lang.NumberFormatException e){
	    System.out.println("Error in NetworkAsciiInput.parseCenterlineHeader");
	    System.out.println("Unable to parse "+nextToken+" as an int");
	    System.out.println("The following line caused the problem: "+unparsedLine);
	}
	return np;
    }//parseCenterlineHeader

    /**
     * parses xsect header to determine number of centerline points
     */
    protected int parseXsectHeader(String unparsedLine){
	StringTokenizer t = new StringTokenizer(unparsedLine, " ,\"\042\011\n");
	int np = -Integer.MAX_VALUE;
	String nextToken=null;
	//	try{
	    nextToken = t.nextToken();
	    //	}catch(java.util.NoSuchElementException e){
	    //	    System.out.println("ERROR in NetworkAsciiInput.parseXsectHeader:");
	    //	    System.out.println("no more tokens in line: " + unparsedLine);
	    //	}
	try{
	    np = Integer.parseInt(nextToken);
	}catch(java.lang.NumberFormatException e){
	    System.out.println("Error in NetworkAsciiInput.parseXsectHeader");
	    System.out.println("Unable to parse "+nextToken+" as an int");
	    System.out.println("The following line caused the problem: "+unparsedLine);
	}
	return np;
    }//parseXsectHeader



  /**
   * Parses a line from an ascii Network data file (*.cdn)
   */
protected void parseCenterline(int linenum, String unparsedLine){
  StringTokenizer t = new StringTokenizer(unparsedLine, " ,\"\042\011\n");

  String cn = new String(t.nextToken());
  int np = -Integer.MAX_VALUE;
  String nextToken = t.nextToken();
  float x = -Float.MAX_VALUE;
  float y = -Float.MAX_VALUE;
  int nx = -Integer.MAX_VALUE;

  try{
    np = Integer.parseInt(nextToken);
  } catch(java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput.parseCenterline");
    System.out.println("Unable to parse "+nextToken+" as an int");
    System.out.println("The following line caused the problem: "+unparsedLine);
  }//try
  _pd.centerlineName = cn;
  _pd.numCenterlinePoints = np;

  if(DEBUG)System.out.println("NetworkAsciiInput.parseCenterline: linenum, unparsedLine, "+
			      "centerlineName, numPoints="+linenum+","+unparsedLine+","+ cn+","+np);

  for(int i=0; i<=_pd.numCenterlinePoints-1; i++){
    nextToken=t.nextToken();
    try{
      x = Float.parseFloat(nextToken);
    } catch(java.lang.NumberFormatException e){
      System.out.println("Error in NetworkAsciiInput.parseCenterline -- x value");
      System.out.println("Unable to parse "+nextToken+" as a float");
    }//try
    nextToken = t.nextToken();
    try{
      y = Float.parseFloat(nextToken);
    } catch(java.lang.NumberFormatException e){
      System.out.println("Error in NetworkAsciiInput.parseCenterline -- y value");
      System.out.println("Unable to parse "+nextToken+" as a float");
    }//try
    _pd.xUTM.put(i, x);
    _pd.yUTM.put(i, y);
  }

  nextToken = t.nextToken();
  try{
    nx = Integer.parseInt(nextToken);
  } catch(java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput.parseCenterline -- # of xsect");
    System.out.println("Unable to parse "+nextToken+" as an int");
  }//try

  _pd.numXsect = nx;
} // parseCenterline

  /**
   * Parses a line from an ascii Network data file (*.prn)
   * pLineNum is actually the line number-1.  Store values in the vector parsedData.
   */
protected void parseXsectLine(int linenum, String unparsedLine){
  StringTokenizer t = new StringTokenizer(unparsedLine, " ,\042\011\n");
  StringTokenizer metadataTokenizer = new StringTokenizer(unparsedLine, "\"");

  //String emptyQuotes = new String(t.nextToken());
  int np = -Integer.MAX_VALUE;
  String nextToken = null;
  try{
      nextToken = t.nextToken();
  }catch(java.util.NoSuchElementException e){
      System.out.println("Error in NetworkAsciiInput.parseXsectLine");
      System.out.println("Line number "+linenum+" has a problem.");
	  System.out.println(":" +unparsedLine);
  }
  float station = -Float.MAX_VALUE;
  float elevation = -Float.MAX_VALUE;
  float dc = -Float.MAX_VALUE;
  float cl = -Float.MAX_VALUE;
  String metadata = null;

  try{
    np = Integer.parseInt(nextToken);
  } catch (java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput.parseXsectLine");
    System.out.println("Unable to parse "+nextToken+ " as an int");
  }//try
  _pd.numXsectPoints = np;
  for(int i=0; i<=_pd.numXsectPoints-1; i++){
      try{
	  nextToken = t.nextToken();
      } catch(java.util.NoSuchElementException e){
	  System.out.println("Error in NetworkAsciiInput.parseXsectLine");
	  System.out.println("The following line in the network file caused the problem:");
	  System.out.println(unparsedLine);
      }
    try{
      station = Float.parseFloat(nextToken);
    } catch(java.lang.NumberFormatException e){
      System.out.println("Error in NetworkAsciiInput.parseXsectLine");
      System.out.println("Unable to parse "+nextToken+" as a float");
    }//try
    try{
	nextToken = t.nextToken();
    } catch(java.util.NoSuchElementException e){
	System.out.println("error in NetworkAsciiInput.parseXsectLine: next token not found");
	System.out.println("The following line in the network file caused the problem:");
	System.out.println(unparsedLine);
    }
    try{
      elevation = Float.parseFloat(nextToken);
    } catch(java.lang.NumberFormatException e){
      System.out.println("Error in NetworkAsciiInput.parseXsectLine");
      System.out.println("Unable to parse "+nextToken+" as a float");
    }//try
    _pd.station.put(i,station);
    _pd.elevation.put(i,elevation);
  }

  nextToken = t.nextToken();
  try{
    dc = Float.parseFloat(nextToken);
  } catch(java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput.parseXsectLine");
    System.out.println("Unable to parse "+nextToken+" as a float");
  }//try
  nextToken = t.nextToken();
  try{
    cl = Float.parseFloat(nextToken);
  } catch(java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput.parseXsectLine");
    System.out.println("Unable to parse "+nextToken+" as a float");
  }//try
  _pd.distAlongCenterline = dc;
  _pd.xsectLineLength = cl;
  
  if(_newFormat == false && t.hasMoreTokens()){
      _parseXsectDialog.setVisible(true);
      _newFormat = true;
  }

  if(_newFormat){
      //skip the first part of the xsect line which doesn't have metadata
      metadataTokenizer.nextToken();
      //      metadataTokenizer.nextToken();  
      if(metadataTokenizer.hasMoreTokens()){
	  metadata = metadataTokenizer.nextToken();
      }else{
	  metadata = "";
      }
      metadata = CsdpFunctions.replaceString(metadata, " *nl* ","\n");
//        metadata = CsdpFunctions.replaceString(metadata, " \n ","\n");
      _pd.metadata = metadata;
  }

}//parseXsectLine

  /**
   * Parses 1st line of file which contains number of points in file
   */
protected void parseFirstLine(String firstLine) {

  StringTokenizer t = new StringTokenizer(firstLine, " ,\042\011");

  String nextToken = t.nextToken();
  int numLines = -Integer.MAX_VALUE;

  if(nextToken.indexOf("Version") >= 0){
      //it's a version 2.00 or greater file, which means that 
      //it has cross-section metadata
      _newFormat = true;
      System.out.println("This network file was made with "+nextToken);
      nextToken = t.nextToken();
  }else if(nextToken.indexOf("Version") < 0){
      _parseFirstLineDialog.setVisible(true);
  }else System.out.println("Error in NetworkAsciiInput.parseFirstLine");

  try{
    numLines = Integer.parseInt(nextToken);
  } catch(java.lang.NumberFormatException e){
    System.out.println("Error in NetworkAsciiInput");
    System.out.println("Unable to parse"+nextToken+"as an int");
  }//try
  if(DEBUG) System.out.println(numLines);
  _pd.numCenterlines=numLines;
} // parseFirstLine

} // class NetworkAsciiInput
