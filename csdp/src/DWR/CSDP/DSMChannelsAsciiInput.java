package DWR.CSDP;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.jogamp.opengl.util.packrect.LevelSet;

public class DSMChannelsAsciiInput extends DSMChannelsInput {

	/**
	 * Open ascii file
	 */
	protected void open() {
		FileReader aInFile;
		try {
			if ((_directory.substring(_directory.length() - 1, _directory.length())).equals(File.separator) == false) {
				_directory += File.separator;
			}
			aInFile = new FileReader(_directory + _filename + "." + _filetype);
			_asciiIn = new LineNumberReader(aInFile);
			if (DEBUG)
				System.out.println("In ascii open " + _asciiIn);
		} catch (IOException e) {
			if (DEBUG)
				System.out.println("Directory + Filename: " + _directory + _filename);
			if (DEBUG)
				System.out.println("Directory + Filetype: " + _directory + _filetype);
			System.out
					.println("Error ocurred while opening file " + _directory + _filename + _filetype + e.getMessage());
		} // catch()
	}

	/**
	 * Read ascii file
	 */
	protected void read() {
		int numLines = 0;

		try {
			String line = null;
			boolean done = false;
			//Skip lines before header line
			while (line == null || !(line.toLowerCase().contains("chan") && line.toLowerCase().contains("length"))
					|| line.trim().indexOf("#")==0) {
				line = _asciiIn.readLine();// skip first line
			}
			int readingSection = CHAN_SECTION;
			// line should now be header line (CHAN LENGTH MANNING ....)
			parseChanHeaders(line);
			for (int i = 0; done == false; i++) {
				line = _asciiIn.readLine();
				done = parseDSMChannelsData(readingSection, i, line);
				if (_firstToken.equals("#")) {
					if (DEBUG)
						System.out.println("comment line read");
				} else {
					storeData(CHAN_SECTION, i);
				}
			} // for

			
			//now read the "XSECTS" section
			readingSection = XSECT_SECTION;
			done = false;
			//if no XSECT section don't read it (will get to null line, which will be end of file.)
			while(line==null || !(line.toLowerCase().contains("chan") && line.toLowerCase().contains("dist") &&
					line.toLowerCase().contains("elev"))) {
				line = _asciiIn.readLine();
				if (line==null){break;}
			}
			if(line!=null) {
				parseXsectHeaders(line);
				for(int i=0; done==false; i++) {
					line=_asciiIn.readLine();
					done = parseDSMChannelsData(readingSection, i, line);
					if (_firstToken.equals("#")) {
						if (DEBUG)
							System.out.println("comment line read");
					} else {
						storeData(XSECT_SECTION, i);
					}
				}	
				// line should now be header line (CHAN_NO, DIST ELEV ...)
				parseChanHeaders(line);
			}else {
				done = true;
			}
		} catch (IOException e) {
			System.out.println("Error ocurred while reading file " + _directory + _filename + ".prn:" + e.getMessage());
		} finally {
			close();
		} // catch()
	}

	/**
	 * Close ascii DSMChannels data file
	 */
	protected void close() {
		try {
			_asciiIn.close();
		} catch (IOException e) {
			System.out.println(
					"Error ocurred while closing file " + _directory + _filename + _filetype + ":" + e.getMessage());
		} // catch
	}

	/**
	 * Parses a line from an ascii DSMChannels data file (channels.inp) pLineNum
	 * is actually the line number-1. Store values in the vector parsedData.
	 */
	protected boolean parseDSMChannelsData(int readingSection, int linenum, String unparsedLine) {

		if (DEBUG)
			System.out.println("unparsedline = " + unparsedLine);

		// delimeters: space, tab
		boolean done = false;
		StringTokenizer t = new StringTokenizer(unparsedLine, " ,\011\042");
		String firstToken = null;
		try {
			firstToken = t.nextToken().trim();
		} catch (NoSuchElementException e) {
			System.out.println("error in parseDSMChannelsData.  linenum, unparsedLine=" + linenum + "," + unparsedLine);
		}
		_firstToken = firstToken;
		String nextToken = null;
		boolean foundChan = false;
		boolean foundLength = false;
		boolean foundManning = false;
		boolean foundDispersion = false;
		boolean foundUpnode = false;
		boolean foundDownnode = false;
		boolean foundDX = false;
		
		boolean foundXsectChan = false;
		boolean foundXsectDist = false;
		boolean foundXsectElev = false;
		boolean foundXsectArea = false;
		boolean foundXsectWidth = false;
		boolean foundXsectWetPerim = false;
		
		if (firstToken.indexOf("#")==0) {
			if (DEBUG)
				System.out.println("comment line ignored");
		} else {
			if (firstToken.equalsIgnoreCase(END_HEADER)) {
				done = true;
				if (DEBUG)
					System.out.println("firstToken=" + firstToken);
			} else {
				if(readingSection==CHAN_SECTION) {
					if (_chanIndex == 0) {
						_dsmChannelsParsedData.chan = firstToken;
						foundChan = true;
					}
					if (_lengthIndex == 0) {
						_dsmChannelsParsedData.length = Integer.parseInt(firstToken);
						foundLength = true;
					}else if (_manningIndex == 0) {
						_dsmChannelsParsedData.manning = nextToken;
					}else if(_dispersionIndex == 0) {
						_dsmChannelsParsedData.dispersion = nextToken;
					} else if (_upnodeIndex == 0) {
						_dsmChannelsParsedData.upnode = Integer.parseInt(firstToken);
						foundUpnode = true;
					} else if (_downnodeIndex == 0) {
						_dsmChannelsParsedData.downnode = Integer.parseInt(firstToken);
						foundDownnode = true;
					} else if (_dxIndex == 0) {
						_dsmChannelsParsedData.dx = Integer.parseInt(firstToken);
						foundDX = true;
					}
	
					for (int i = 1; t.hasMoreTokens(); i++) {
						StringTokenizer commentRemover = null;
						nextToken = t.nextToken();
	
						if (nextToken.indexOf("#") > 0) {
							if (DEBUG)
								System.out.println("about to strip comment from end of line.");
							if (DEBUG)
								System.out.println("nextToken before=" + nextToken);
							commentRemover = new StringTokenizer(nextToken, "#");
							nextToken = commentRemover.nextToken();
							if (DEBUG)
								System.out.println("nextToken after=" + nextToken);
						}
	
						if (DEBUG)
							System.out.println("i, nextToken=" + i + "," + nextToken);
						if (_chanIndex == i) {
							_dsmChannelsParsedData.chan = nextToken;
							foundChan = true;
						}
						if (_lengthIndex == i) {
							if (DEBUG)
								System.out.println("lengthIndex, i=" + _lengthIndex + "," + i);
							_dsmChannelsParsedData.length = Integer.parseInt(nextToken);
							foundLength = true;
						}
						if(_manningIndex == i) {
							_dsmChannelsParsedData.manning = nextToken;
							foundManning = true;
						}
						if(_dispersionIndex == i) {
							_dsmChannelsParsedData.dispersion = nextToken;
							foundDispersion = true;
						}
						if (_upnodeIndex == i) {
							_dsmChannelsParsedData.upnode = Integer.parseInt(nextToken);
							foundUpnode = true;
						}
						if (_downnodeIndex == i) {
							_dsmChannelsParsedData.downnode = Integer.parseInt(nextToken);
							foundDownnode = true;
						}
						if(_dxIndex == i) {
							_dsmChannelsParsedData.dx = Double.parseDouble(nextToken);
							foundDX = true;
						}
						//DX is required by DSM2 v8.5.0. For prior versions, it is not a requirement. 
						if (foundChan == false && foundLength == false && foundUpnode == false && foundDownnode == false
								&& foundManning == false && foundDispersion == false) {
							System.out.println(
									"Error in DSMChannelsAsciiInput: foundChan, foundLength, foundUpnode, foundDownnode, foundManning, foundDispersion="
											+ foundChan + "," + foundLength + "," + foundUpnode + "," + foundDownnode + ","
											+ foundManning+ "," + foundDispersion);
						}
					} // while has more tokens
				}else if(readingSection==XSECT_SECTION) {
					if (_xsectChanIndex == 0) {
						_dsmChannelsParsedData.xsectChan = firstToken;
						foundXsectChan = true;
					}
					if (_xsectDistIndex == 0) {
						_dsmChannelsParsedData.xsectDist = firstToken;
						foundXsectDist = true;
					}else if (_xsectElevIndex == 0) {
						_dsmChannelsParsedData.xsectElev = nextToken;
						foundXsectElev = true;
					}else if(_xsectAreaIndex == 0) {
						_dsmChannelsParsedData.xsectArea = nextToken;
						foundXsectArea = true;
					} else if (_xsectWidthIndex == 0) {
						_dsmChannelsParsedData.xsectWidth = firstToken;
						foundXsectWidth = true;
					} else if (_xsectWetPerimIndex == 0) {
						_dsmChannelsParsedData.xsectWetPerim = firstToken;
						foundXsectWetPerim = true;
					}
	
					for (int i = 1; t.hasMoreTokens(); i++) {
						StringTokenizer commentRemover = null;
						nextToken = t.nextToken();
	
						if (nextToken.indexOf("#") > 0) {
							if (DEBUG)
								System.out.println("about to strip comment from end of line.");
							if (DEBUG)
								System.out.println("nextToken before=" + nextToken);
							commentRemover = new StringTokenizer(nextToken, "#");
							nextToken = commentRemover.nextToken();
							if (DEBUG)
								System.out.println("nextToken after=" + nextToken);
						}
	
						if (DEBUG)
							System.out.println("i, nextToken=" + i + "," + nextToken);
						if (_xsectChanIndex == i) {
							_dsmChannelsParsedData.xsectChan = nextToken;
							foundXsectChan = true;
						}
						if (_xsectDistIndex == i) {
							_dsmChannelsParsedData.xsectDist= nextToken;
							foundXsectDist = true;
						}
						if(_xsectElevIndex == i) {
							_dsmChannelsParsedData.xsectElev = nextToken;
							foundXsectElev = true;
						}
						if(_xsectAreaIndex == i) {
							_dsmChannelsParsedData.xsectArea = nextToken;
							foundXsectArea = true;
						}
						if (_xsectWidthIndex == i) {
							_dsmChannelsParsedData.xsectWidth = nextToken;
							foundXsectWidth = true;
						}
						if (_xsectWetPerimIndex == i) {
							_dsmChannelsParsedData.xsectWetPerim = nextToken;
							foundXsectWetPerim = true;
						}
						if(!foundXsectChan && !foundXsectDist && !foundXsectElev && !foundXsectArea && !foundXsectWidth && !foundXsectWetPerim) {
							System.out.println("Error in DSMChannelsAsciiInput: foundXsectChan, foundXsectDist, foundXsectElev, foundXsectArea, foundXsectWidth, foundXsectWetPerim="
											+ foundXsectChan+ "," + foundXsectDist+ "," + foundXsectElev + "," + foundXsectArea + ","
											+ foundXsectWidth+ "," + foundXsectWetPerim);
						}
					} // while has more tokens
					
				}else {
					JOptionPane.showMessageDialog(null, "Error reading DSM2 channels input file: readingSectiom is not CHANNELS_SECTION or XSECT_SECTION", "Error", JOptionPane.OK_OPTION);
				}
			} // else
		} // if the line is not commented out
		return done;
	} // parse

	protected void parseXsectHeaders(String firstLine) {
		StringTokenizer t = new StringTokenizer(firstLine, " ");
		String tok = null;
		for(int i=0; t.hasMoreTokens(); i++) {
			tok=t.nextToken();
			if(DEBUG)
				System.out.println("next token="+tok);
			if (tok.equalsIgnoreCase(CHAN_HEADER)) {
				_xsectChanIndex=i;
				if(DEBUG)
					System.out.print("Xsect Chan_header="+i);
			}
			if(tok.equalsIgnoreCase(DIST_HEADER)){
				_xsectDistIndex = i;
			}
			if(tok.equalsIgnoreCase(ELEV_HEADER)) {
				_xsectElevIndex=i;
			}
			if(tok.equalsIgnoreCase(AREA_HEADER)) {
				_xsectAreaIndex=i;
			}
			if(tok.equalsIgnoreCase(WIDTH_HEADER)) {
				_xsectWidthIndex=i;
			}
			if(tok.equalsIgnoreCase(WET_PERIM_HEADER)) {
				_xsectWetPerimIndex=i;
			}
		}
		
	}
	
	/**
	 * Parses second line of file which contains column headers
	 */
	protected void parseChanHeaders(String firstLine) {

		StringTokenizer t = new StringTokenizer(firstLine, " ");
		String tok = null;

		for (int i = 0; t.hasMoreTokens(); i++) {
			tok = t.nextToken();
			if (DEBUG)
				System.out.println("next token=" + tok);
			if (tok.equalsIgnoreCase(CHAN_HEADER)) {
				_chanIndex = i;
				if (DEBUG)
					System.out.println("chan_header=" + i);
			}
			if (tok.equalsIgnoreCase(LENGTH_HEADER)) {
				_lengthIndex = i;
				if (DEBUG)
					System.out.println("length_header=" + i);
			}
			if (tok.equalsIgnoreCase(DOWNNODE_HEADER)) {
				_downnodeIndex = i;
				if (DEBUG)
					System.out.println("downnode_header=" + i);
			}
			if (tok.equalsIgnoreCase(UPNODE_HEADER)) {
				_upnodeIndex = i;
				if (DEBUG)
					System.out.println("upnode_header=" + i);
			}
			if(tok.equalsIgnoreCase(MANNING_HEADER)) {
				_manningIndex = i;
			}
			if(tok.equalsIgnoreCase(DISPERSION_HEADER)) {
				_dispersionIndex = i;
			}
			if(tok.equalsIgnoreCase(DX_HEADER)) {
				_dxIndex = i;
			}
//			if (tok.equalsIgnoreCase(XSECT_HEADER)) {
//				if (_xsect1Index <= 0) {
//					_xsect1Index = i;
//				} else if (_xsect1Index > 0 && _xsect2Index <= 0) {
//					_xsect2Index = i;
//				}
//			}
//			if (tok.equalsIgnoreCase(DIST_HEADER)) {
//				if (_dist1Index <= 0) {
//					_dist1Index = i;
//				} else if (_dist1Index > 0 && _dist2Index <= 0) {
//					_dist2Index = i;
//				}
//			}
		}
		if(_dxIndex>=0) {
			_dsmChannelsParsedData.setDXIncluded(true);
		}else {
			_dsmChannelsParsedData.setDXIncluded(false);
		}
	} // parseSecondLine

	LineNumberReader _asciiIn;
	protected int _chanIndex = -Integer.MAX_VALUE;
	protected int _lengthIndex = -Integer.MAX_VALUE;
	protected int _manningIndex = -Integer.MAX_VALUE;
	protected int _dispersionIndex = -Integer.MAX_VALUE;
	protected int _upnodeIndex = -Integer.MAX_VALUE;
	protected int _downnodeIndex = -Integer.MAX_VALUE;
//	protected int _xsect1Index = -Integer.MAX_VALUE;
//	protected int _dist1Index = -Integer.MAX_VALUE;
//	protected int _xsect2Index = -Integer.MAX_VALUE;
//	protected int _dist2Index = -Integer.MAX_VALUE;
	protected int _dxIndex = -Integer.MAX_VALUE;
	
	protected int _xsectChanIndex = -Integer.MAX_VALUE;
	protected int _xsectDistIndex = -Integer.MAX_VALUE;
	protected int _xsectElevIndex = -Integer.MAX_VALUE;
	protected int _xsectAreaIndex = -Integer.MAX_VALUE;
	protected int _xsectWidthIndex = -Integer.MAX_VALUE;
	protected int _xsectWetPerimIndex = -Integer.MAX_VALUE;
	protected static final String DIST_HEADER = "dist";
	protected static final String ELEV_HEADER = "elev";
	protected static final String AREA_HEADER = "area";
	protected static final String WIDTH_HEADER = "width";
	protected static final String WET_PERIM_HEADER = "wet_perim";
	
	protected static final String CHAN_HEADER = "chan_no";
	protected static final String LENGTH_HEADER = "length";
	protected static final String UPNODE_HEADER = "upnode";
	protected static final String DOWNNODE_HEADER = "downnode";
	protected static final String DX_HEADER = "dx";
	protected static final String END_HEADER = "end";
	protected static final String MANNING_HEADER = "manning";
	protected static final String DISPERSION_HEADER = "dispersion";
	//	protected static final String XSECT_HEADER = "xsect";
//	protected static final String DIST_HEADER = "dist";
	
	protected String _firstToken = null;
} // class DSMChannelsAsciiInput
