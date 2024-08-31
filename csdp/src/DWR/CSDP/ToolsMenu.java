package DWR.CSDP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

//import org.omg.PortableInterceptor.SUCCESSFUL;

//import COM.objectspace.jgl.DividesInteger;
import DWR.CSDP.Landmark.LandmarkPoint;
import DWR.CSDP.dialog.DataEntryDialog;
import DWR.CSDP.dialog.FileAndRadioDialog;
import DWR.CSDP.dialog.FileIO;

public class ToolsMenu {
	public static final int ENTER_CENTERLINE_NAMES = 10;
	public static final int READ_CENTERLINE_NAMES_FROM_FILE = 20;

	public ToolsMenu(App app, CsdpFrame gui) {
		_app = app;
		_gui = gui;
		_owaOpenFilter = new CsdpFileFilter(_openExtensions, _numOpenExtensions);
		_owaWriteFilter = new CsdpFileFilter(_saveExtensions, _numSaveExtensions);
		_stationOpenFilter = new CsdpFileFilter(_stationExtensions, _numStationExtensions);
		_toeDrainFilter = new CsdpFileFilter(_toeDrainExtensions, _numToeDrainExtensions);
		_calcRectFilter = new CsdpFileFilter(_crSaveExtensions, _crNumSaveExtensions);
		_DSMChanFilter = new CsdpFileFilter(_dsmOpenExtensions, _dsmNumOpenExtensions);
		_irregXsectsFilter = new CsdpFileFilter(_dsmOpenExtensions, _dsmNumOpenExtensions);
		_xsectsInpFilter = new CsdpFileFilter(_dsmOpenExtensions, _dsmNumOpenExtensions);
	}

	/**
	 * Create a landmark file containing DCD/SMCD nodes.
	 * The following inputs are required:
	 *   1. A DSM2 node landmark file, which should be already loaded and displayed in CSDP.
	 *   2. Two DCD/SMCD input files, containing DIV and DRAIN nodes.
	 *   3. Landmark file containing nodes that are DCD nodes, but not DSM2 nodes.
	 *   4. Output Landmark file path.
	 * @author btom
	 *
	 */
	public class TCreateDCDNodeLandmarkFile implements ActionListener {
		private CsdpFrame csdpFrame;
		public TCreateDCDNodeLandmarkFile(CsdpFrame csdpFrame) {
			this.csdpFrame = csdpFrame;
		}//constructor

		public void actionPerformed(ActionEvent e) {
			System.out.println("actionPerformed");
			Landmark landmark = csdpFrame.getLandmark();
			if(landmark != null) {
				System.out.println("creating dialog");
				String title = "Create DCD landmark file";
				String instructions = "<HTML><BODY>"
						+ "Using existing landmark file containing DSM2 nodes, and DCD DIV and DRAIN input files, create<BR> "
						+ " a landmark file containing only DCD nodes.<BR>"
						+ "If you wish to create a file containing nodes shared by DSM2 and DCD, then do NOT include a <BR>"
						+ "DCD only landmark file."
						+ "Include a DCD only landmark file only if you want to create a layer of all DCD nodes, including <BR>"
						+ "the node(s) that is/are not shared with DSM2.<BR>"
						+ "</BODY></HTML>";
				final String[] names = new String[]{"DCD DIV file", "DCD DRAIN file","DCD Only landmark file","Output Landmark File"};

				String[] defaultValues = new String[] {"","","",""};
				int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE, 
						DataEntryDialog.FILE_SPECIFICATION_TYPE,DataEntryDialog.FILE_SPECIFICATION_TYPE};
				boolean[] disableIfNull = new boolean [] {true, true, false, true};
				String[] extensions = new String[] {"","","cdl","cdl"};
				String[] tooltips = new String[] {"DCD Input file containing Diversion nodes", 
						"DCD Input file containing Drain nodes", 
						"CSDP landmark file containing nodes that are used by DCD but NOT DSM2. If making a DCD/DSM2 shared node file, do NOT include this.",
				"Output CSDP landmark file"}; 
				boolean modal = true;
				DataEntryDialog dataEntryDialog = new DataEntryDialog(csdpFrame, title, instructions, names,
						defaultValues, dataTypes, disableIfNull, extensions, tooltips, modal);

				int response = dataEntryDialog.getResponse();
				if(response==DataEntryDialog.OK) {
					String dcdDivDirectory = dataEntryDialog.getDirectory(names[0]).toString();
					String dcdDivFilename = dataEntryDialog.getFilename(names[0]);
					String dcdDrainDirectory = dataEntryDialog.getDirectory(names[1]).toString();
					String dcdDrainFilename = dataEntryDialog.getFilename(names[1]);
					//optional
					String dcdOnlyLandmarkDirectory = null;
					String dcdOnlyLandmarkFilename = null;
					if(dataEntryDialog.getValue(names[2]) != null && dataEntryDialog.getValue(names[2]).length()>0) {
						dcdOnlyLandmarkDirectory = dataEntryDialog.getDirectory(names[2]).toString();
						dcdOnlyLandmarkFilename = dataEntryDialog.getFilename(names[2]);
					}
					String outputLandmarkDirectory = dataEntryDialog.getDirectory(names[3]).toString();
					String outputLandmarkFilename = dataEntryDialog.getFilename(names[3]);

					AsciiFileReader dcdOnlyFileReader = null;
					if(dcdOnlyLandmarkDirectory!=null && dcdOnlyLandmarkDirectory.length()>0 &&
							dcdOnlyLandmarkFilename!=null && dcdOnlyLandmarkFilename.length()>0) {
						dcdOnlyFileReader = new AsciiFileReader(dcdOnlyLandmarkDirectory+File.separator+dcdOnlyLandmarkFilename);
					}
					HashSet<String> allDCDNodes = new HashSet<String>();
					boolean divSuccess = getDCDNodes(dcdDivDirectory, dcdDivFilename, allDCDNodes);
					boolean drainSuccess = getDCDNodes(dcdDrainDirectory, dcdDrainFilename, allDCDNodes);
					boolean writeSuccess = false;
					//get coordinates from landmark objects, and add to output Landmark object
					try {
//						now read landmark file and use them to get coordinates for node numbers, then create new landmark file.
						Landmark outputLandmark = new Landmark(csdpFrame);
						if(dcdOnlyFileReader!=null) {
							LandmarkInput linput = LandmarkInput.getInstance(csdpFrame, dcdOnlyLandmarkDirectory, dcdOnlyLandmarkFilename);
							Landmark dcdOnlyLandmark = linput.readData();		
							Enumeration<String> landmarkEnumeration = dcdOnlyLandmark.getLandmarkNames();
							while(landmarkEnumeration.hasMoreElements()) {
								String pointNameString = landmarkEnumeration.nextElement();
								double easting = dcdOnlyLandmark.getXMeters(pointNameString);
								double northing = dcdOnlyLandmark.getYMeters(pointNameString);
								outputLandmark.addLandmarkMeters(pointNameString, easting, northing);
							}
						}
						for(String pointNameString: allDCDNodes) {
							double easting = landmark.getXMeters(pointNameString);
							double northing = landmark.getYMeters(pointNameString);
							outputLandmark.addLandmarkMeters(pointNameString, easting, northing);
						}
						LandmarkOutput landmarkOutput = LandmarkOutput.getInstance(outputLandmarkDirectory, outputLandmarkFilename, "cdl", outputLandmark);
						writeSuccess = landmarkOutput.writeData();
						

					}catch(Exception e3) {
						System.out.println("error in ToolsMenu.TCreateDCDNodeLandmarkFile.actionPerformed");
					}finally {
						if(divSuccess && drainSuccess && writeSuccess) {
							JOptionPane.showMessageDialog(_gui, "Landmark file written", "Success", JOptionPane.OK_OPTION);
						}else {
							System.out.println("divSuccess, drainSuccess, writeSuccess="+divSuccess+","+drainSuccess+","+writeSuccess);
							JOptionPane.showMessageDialog(_gui, "An error occurred", "Error", JOptionPane.OK_OPTION);
						}
					}
				}//if response==OK
			}//if landmark!=null
		}//actionPerformed

		/*
		 * Given directory and filename of DCD input file, read node numbers and copy to HashSet 
		 */
		private boolean getDCDNodes(String directory, String filename, HashSet<String> results) {
			AsciiFileReader fileReader = new AsciiFileReader(directory+File.separator+filename);
			boolean success = true;
			int i=0;
			while(true){
				String line = fileReader.getNextLine();
				if(line==null) break;
				String[] parts = line.trim().split("\\t+|\\s+");
				if(parts.length==3) {
					try {
						Integer.parseInt(parts[1]);
						results.add(parts[1]);
					}catch(NumberFormatException e2) {
						success=false;
						System.out.println("line, parts[1]="+line+","+parts[1]);
						JOptionPane.showMessageDialog(_gui, "Error in ToolsMenu.TCreateDCDNodeLandmarkFile.getDCDNodes: parsing error", "Error", JOptionPane.OK_OPTION);
					}//try-catch
				}//if
			}//while
			fileReader.close();
			return success;
		}//getDCDNodes
		
	}//TCreateDCDNodeLandmarkFile

	/**
	 * Writes coordinates of centroids of all centerlines to a file. Used externally for calculating 
	 * smoothed Manning's n and dispersion values using curve fit. 
	 * @author btom
	 *
	 */
	public class TManningsDispersionSpatialDistribution implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String title = "Manning's n or Dispersion Spatial Distribution";
			String instructions = "<HTML><BODY>"
					+ "Using existing network file and a DSM channels file (channels.inp), create a file with 3 columns, containing:<BR>"
					+ "1. The centerline name<BR>"
					+ "2. The centerline centroid easting<BR>"
					+ "3. The centerline centroid northing<BR>"
					+ "4. The Manning's n value, if requested<BR>"
					+ "5. The Dispersion factor, if requested<BR>"
					+ "<BR><BR>This can be used to create smoothed Manning's n and dispersion values using a 3D curve fit."
					+ "</BODY></HTML>";

			final String[] names = new String[]{"Output filename (.txt)", "write Manning's n", "write Dispersion Factor"};
			String[] defaultValues = new String[] {"", "true", "true"};
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.BOOLEAN_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			boolean[] disableIfNull = new boolean [] {true, false, false};
			String[] extensions = new String[] {"txt","",""};
			String[] tooltips = new String[] {"output filename", "check to include Manning's n in output", "check to include Dispersion Factor in output"}; 
			boolean modal = true;

			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, title, instructions, names,
					defaultValues, dataTypes, disableIfNull, extensions, tooltips, modal);

			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String outputDirectory = dataEntryDialog.getDirectory(names[0]).toString();
				String outputFilename = dataEntryDialog.getFilename(names[0]);
				String writeManningString = dataEntryDialog.getValue(names[1]);
				String writeDispersionString = dataEntryDialog.getValue(names[2]);
				boolean writeManning = Boolean.parseBoolean(writeManningString);
				boolean writeDispersion = Boolean.parseBoolean(writeDispersionString);
				_DSMChannels = _app.getDSMChannels();
				Network network = _gui.getNetwork();

				AsciiFileWriter asciiFileWriter = new AsciiFileWriter(_gui, outputDirectory+File.separator+outputFilename);
				for(int i=0; i<network.getNumCenterlines(); i++) {
					String centerlineName = network.getCenterlineName(i);
					Centerline centerline = network.getCenterline(centerlineName);
					double[] centroidCoord = centerline.getCentroid();
					double centroidX = CsdpFunctions.feetToMeters(centroidCoord[CsdpFunctions.xIndex]);					
					double centroidY = CsdpFunctions.feetToMeters(centroidCoord[CsdpFunctions.yIndex]);
					String line = centerlineName+","+centroidX+","+centroidY;
					if(writeManning) {
						line += ","+_DSMChannels.getManning(centerlineName);
					}
					if(writeDispersion) {
						line += ","+_DSMChannels.getDispersion(centerlineName);
					}
					asciiFileWriter.writeLine(line);
				}
				asciiFileWriter.close();
				JOptionPane.showMessageDialog(_gui, "Manning and/or dispersion spatial distribution file written", "Success", JOptionPane.OK_OPTION);
			}
		}//actionPerformed
	}//inner class TManningsDispersionSpatialDistribution


	/**
	 * User specifies one or two network files. First will be currently loaded network file by default, but can be changed
	 * Looping through the centerlines in file #1, display a series of windows. 
	 * Pressing a key will dispose the current window and open the next.
	 * Each window will display
	 * 1. on the left, the bathymetry and cross-section drawing for a cross-section in file #1. 
	 * 2. one the right, the same for file #2. The cross-section with the closest distance will be displayed. 
	 * Above Both plots will be cross-section index, distance along centerline, and centerline length.
	 * 
	 * @author btom
	 *
	 */
	public class TCrossSectionSlideshow implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Network network = _gui.getNetwork();
			//			Centerline centerline = network.getSelectedCenterline();
			String title = "Cross-Section slideshow";
			String instructions = "<HTML><BODY>"
					+ "Display cross-section(s) for one or two network files, one at a time.<BR>"
					+ "A second bathymetry file can optionally be specified, if so, the first <BR>"
					+ "cross-section plot will display data from the first file, and the second <BR>"
					+ "from the second file. <BR>"
					+ "This can be used to compare cross-sections in two network files.<BR>"
					+ "</HTML></BODY>";

			final String[] names = new String[]{"Left Network File","Right Network File", "Left Bathymetry File",
					"Folder for saving images", "Include Xsect Conveyance Characteristics", "Include Xsect Metadata"};
			//					"Automatically create images for all cross-sections"};
			String[] defaultValues = new String[] {CsdpFunctions.getNetworkDirectory()+File.separator+
					CsdpFunctions.getNetworkFilename(), "", "", "", "false", "true"};
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.FILE_SPECIFICATION_TYPE,
					DataEntryDialog.FILE_SPECIFICATION_TYPE, DataEntryDialog.DIRECTORY_SPECIFICATION_TYPE, 
					DataEntryDialog.BOOLEAN_TYPE, DataEntryDialog.BOOLEAN_TYPE};
			boolean[] disableIfNull = new boolean [] {true, true, false, false, true, true};
			String[] extensions = new String[] {"cdn", "cdn", "prn|cdp", "","",""};
			String[] tooltips = new String[] {"network file used for cross-section data to be displayed on left hand side ", 
					"network file used for cross-section data to be displayed on right hand side", 
					"First Bathymetry file. Will be used for left hand plot", 
					"A folder for storing saved slideshow images",
					"If true, include conveyance characteristics in slideshow frames",
			"If true, include Metadata in slideshow frames"};
			//					"If true, disable interactive mode, and save an image of each frame in the slideshow to disk"}; 
			boolean modal = true;

			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, title, instructions, names,
					defaultValues, dataTypes, disableIfNull, extensions, tooltips, modal);

			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String directory0 = dataEntryDialog.getDirectory(names[0]).toString();
				String directory1 = dataEntryDialog.getDirectory(names[1]).toString();
				String filename0 = dataEntryDialog.getFilename(names[0]);
				String filename1 = dataEntryDialog.getFilename(names[1]);
				String bathymetryDirectory0 = null;
				String bathymetryFilename0 = null;
				if(names.length > 2) {
					bathymetryDirectory0 = dataEntryDialog.getDirectory(names[2]).toString();
					bathymetryFilename0 = dataEntryDialog.getFilename(names[2]);
				
					if(bathymetryFilename0.trim().length()<=0) {
						bathymetryFilename0 = null;
					}
				}
				File directorySaveImage = dataEntryDialog.getDirectory(names[3]);
				String directorySaveImageString = null;
				if(directorySaveImage != null) directorySaveImageString = directorySaveImage.toString().trim();
				String includeXsectConveyanceCharacteristicsString = dataEntryDialog.getValue(names[4]);
				String includeMetadataString = dataEntryDialog.getValue(names[5]);
				//				String autoSaveString = dataEntryDialog.getValue(names[6]);
				boolean includeXsectConveyanceCharacteristics = Boolean.parseBoolean(includeXsectConveyanceCharacteristicsString);
				boolean includeMetadata = Boolean.parseBoolean(includeMetadataString);
				//				boolean autoSave = Boolean.parseBoolean(autoSaveString);
				//auto save won't work until I can find a way to make a dialog close itself. Currently, the saving works fine but 
				//the automatic window closing doesn't.
				boolean autoSave = false;
				//				boolean autoSave = Boolean.parseBoolean(dataEntryDialog.getValue(names[5]));
				boolean reReadFile0 = false;
				if((directory0.trim()+File.separator+filename0).equalsIgnoreCase(defaultValues[0])){
					reReadFile0 = false;
				}else {
					reReadFile0 = true;
				}
				_app.xsectSlideshow(directory0, filename0, reReadFile0, directory1, filename1, 
						bathymetryDirectory0, bathymetryFilename0, directorySaveImageString, 
						includeXsectConveyanceCharacteristics, includeMetadata, autoSave);
			}
		}
	}//TCrossSectionSlideshow

	/**
	 * For each landmark, determines nearest Centerline and distance from the upstream end of the nearest point on the centerline
	 * to the landmark. Optionally draws a cross-section line, which can be useful for verification.
	 * @author btom
	 *
	 */
	public class TCreateDSM2OutputLocationsForLandmarks implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			_app.createDSM2OutputLocationsForLandmarks(_gui);
		}
	}


	public class TRemoveAllCrossSections implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			_app.removeAllCrossSections(_gui);
		}

	}

	public class TCreateDSM2ChanPolygons implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}//inner class TCreateDSM2ChanPolygons

	public class TClosePolygonCenterlines implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Network network = _app.getNetwork();
			network.closePolygonCenterlines(network);
		}
	}//inner class TClosePolygonCenterlines

	/**
	 * Create a WKT file containing a straight line for each channel connecting two nodes.
	 * @author btom
	 *
	 */
	public class TCreateStraightlineChanForGridmap implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			Network network = _gui.getNetwork();
			Landmark landmark = _gui.getLandmark();
			_DSMChannels = _app.getDSMChannels();


			String title = "Create WKT file containing straight line channels which are connected to nodes. This is used for"
					+ " for importing into GIS, to create a layer for the gridmap";
			String instructions = "<HTML><BODY>1. Specify a *.wkt filename.<BR></BODY></HTML>";
			String[] names = new String[] {"WKT filename"};
			String[] defaultValues = new String[]{""};
			int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE};
			boolean[] disableIfNull = new boolean[] {true};
			int[] numDecimalPlaces = new int[] {0};
			String[] extensions = new String[] {"wkt"};
			String[] tooltips = new String[] {"The full path to the .wkt file to be created"};
			boolean modal = true;

			DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, title, instructions, names, 
					defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
					extensions, tooltips, modal);
			int response = dataEntryDialog.getResponse();
			if(response==DataEntryDialog.OK) {
				String wktPathString = dataEntryDialog.getDirectory(names[0])+File.separator+dataEntryDialog.getFilename(names[0]);
				_app.createStraightlineWKTGridmapFile(wktPathString, network, landmark, _DSMChannels);
			}
		}
	}//inner class TCreateStraightlineGridmapConnectingNodes


	public class TExtendCenterlinesToNodes implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			Network network = _gui.getNetwork();
			Landmark landmark = _gui.getLandmark();
			for(int i=0; i<network.getNumCenterlines(); i++) {
				String centerlineName = network.getCenterlineName(i);
				Centerline centerline = network.getCenterline(centerlineName);
				_DSMChannels = _app.getDSMChannels();
				int upnode = _DSMChannels.getUpnode(centerlineName);
				int downnode = _DSMChannels.getDownnode(centerlineName);
				double upnodeX = landmark.getXFeet(Integer.toString(upnode));
				double upnodeY = landmark.getYFeet(Integer.toString(upnode));
				double downnodeX = landmark.getXFeet(Integer.toString(downnode));
				double downnodeY = landmark.getYFeet(Integer.toString(downnode));
				CenterlinePoint upPoint = centerline.getCenterlinePoint(0);
				CenterlinePoint downPoint = centerline.getCenterlinePoint(centerline.getNumCenterlinePoints()-1);
				double upPointX = upPoint.getXFeet();
				double upPointY = upPoint.getYFeet();
				double downPointX = downPoint.getXFeet();
				double downPointY = downPoint.getYFeet();
				double upDist = CsdpFunctions.pointDist(upnodeX, upnodeY, upPointX, upPointY);
				double downDist = CsdpFunctions.pointDist(downnodeX, downnodeY, downPointX, downPointY);
				if(upDist>1.0) {
					centerline.addUpstreamCenterlinePointFeet(upnodeX, upnodeY);
				}
				if(downDist>1.0) {
					centerline.addDownstreamCenterlinePointFeet(downnodeX, downnodeY);
				}
			}
			JOptionPane.showMessageDialog(_gui, "Centerlines have been extended to nodes.", "Success", JOptionPane.INFORMATION_MESSAGE);
		}//actionPerformed

	}//class TExtendCenterlinesToNodes

	/**
	 * Given 3 centerlines:
	 * 1. The channel centerline
	 * 2. A Centerline representing a polygon used to estimate GIS conveyance characteristics for the centerline
	 * 3. A centerline representing a levee
	 * 
	 * Move all the points that are on the levee side of the centerline (determined using the channel centerline) to the
	 * nearest point on the levee centerline
	 * @author btom
	 *
	 */
	public class SnapPolygonCenterlinePointsToLeveeCenterline implements ActionListener {

		private int centerlineNameSource;

		public SnapPolygonCenterlinePointsToLeveeCenterline(int coordinateSource) {
			this.centerlineNameSource = coordinateSource;
		}

		public void actionPerformed(ActionEvent arg0) {
			if(!CsdpFunctions.movePolygonCenterlinePointsToLeveeCenterlineDialogOpen()) {
				Network network = _gui.getNetwork();
				Centerline centerline = network.getSelectedCenterline();

				if(this.centerlineNameSource==ENTER_CENTERLINE_NAMES) {
					String title = "Move Polygon Centerline Points to Levee Centerline";
					String instructions = "<HTML><BODY>"
							+ "Given 3 centerlines (use buttons below to select):<BR>"
							+ "1. A channel centerline<BR>"
							+ "2. A centerline representing a polygon used to estimate GIS conveyance characteristics for the centerline<BR>"
							+ "3. A centerline representing a levee<BR><BR>"
							+ "Move some of the points in the polygon centerline to the levee centerline, using the channel centerline <BR>"
							+ "to identify which points are on the same side of the channel as the levee centerline."
							+ "</HTML></BODY>";

					final String[] names = new String[]{"Polygon Centerline Name", "Channel Centerline Name", "Levee Centerline Name"};
					String[] defaultValues = new String[] {"", "",""};
					//centerline selection type doesn't work for canceling.
					//				int[] dataTypes = new int[] {DataEntryDialog.CENTERLINE_SELECTION_TYPE, 
					//						DataEntryDialog.CENTERLINE_SELECTION_TYPE, 
					//						DataEntryDialog.CENTERLINE_SELECTION_TYPE};
					int[] dataTypes = new int[] {DataEntryDialog.STRING_TYPE,
							DataEntryDialog.NUMERIC_TYPE,
							DataEntryDialog.STRING_TYPE};
					boolean[] disableIfNull = new boolean [] {true, true, true};
					int[] numDecimalPlaces = new int[] {0,0,0};
					String[] tooltips = new String[] {"Click button to select the channel centerline", 
							"Click button to select the polygon centerline", 
					"Click button to select the levee centerline"};
					boolean modal = true;

					final DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, title, instructions, names,
							defaultValues, dataTypes, disableIfNull, numDecimalPlaces, tooltips, modal);

					int response = dataEntryDialog.getResponse();
					if(response==DataEntryDialog.OK) {
						String polygonCenterlineName = dataEntryDialog.getValue(names[0]).trim();
						String leveeCenterlineName = dataEntryDialog.getValue(names[1]).trim();
						System.out.println("response==OK: polygonCenterlineName, leveeCenterlineName="
								+polygonCenterlineName+","+leveeCenterlineName);
						Vector<String> errorMessages = null; 
						//						if(channelCenterlineName.equals(polygonCenterlineName) || channelCenterlineName.equals(leveeCenterlineName) 
						//								|| polygonCenterlineName.equals(leveeCenterlineName)){
						//							if(errorMessages==null) {
						//								errorMessages = new Vector<String>();
						//							}
						//							errorMessages.add("Your selections must all be different");
						//						}
						if(!network.centerlineExists(polygonCenterlineName) || !network.centerlineExists(leveeCenterlineName)) {
							if(errorMessages==null) {
								errorMessages = new Vector<String>();
							}
							errorMessages.add("You have specified non-existent centerline(s)");
						}
						if(errorMessages!=null) {
							String messages = "";
							for(int i=0; i<errorMessages.size(); i++) {
								messages+=errorMessages.get(i)+"\n";
							}
							JOptionPane.showMessageDialog(_gui, messages, "Error", JOptionPane.ERROR_MESSAGE);
						}else {
							_gui.pressSelectCursorAkaArrowButton();
							_app.snapPolygonCenterlinePointsToLeveeCenterline(polygonCenterlineName,  
									new String[] {leveeCenterlineName});
						}
					}else{
						//					dataEntryDialog.removeWindowListener(this);
						//					dataEntryDialog.disposeDialog();
						_gui.pressSelectCursorAkaArrowButton();
						CsdpFunctions.setPolygonCenterlinePointsToLeveeCenterlineDialogOpen(false);
					}

					//this would be better way to do it if it worked. It allows the dialog to disappear and user clicks on centerline,
					//then selected centerline name gets put into dialog. Problem is the dialog is not disposing properly
					//when ok or cancel clicked--it will go away, but will come back when user selects a centerline.
					//this is necessary because the dialog is set to invisible while centerline selection is made.
					//				dataEntryDialog.addWindowListener(new WindowListener() {
					//					public void windowOpened(WindowEvent arg0) {}
					//					public void windowIconified(WindowEvent arg0) {}
					//					public void windowDeiconified(WindowEvent arg0) {}
					//					public void windowDeactivated(WindowEvent arg0) {}
					//					public void windowClosing(WindowEvent arg0) {}
					//					
					//					public void windowClosed(WindowEvent arg0) {
					//						int response = dataEntryDialog.getResponse();
					//						if(response==DataEntryDialog.OK) {
					//							String channelCenterlineName = dataEntryDialog.getValue(names[0]);
					//							String polygonCenterlineName = dataEntryDialog.getValue(names[1]);
					//							String leveeCenterlineName = dataEntryDialog.getValue(names[2]);
					//							System.out.println("response==OK: channelCenterlineName, polygonCenterlineName, leveeCenterlineName="
					//									+ channelCenterlineName+","+polygonCenterlineName+","+leveeCenterlineName);
					//							if(channelCenterlineName.equals(polygonCenterlineName) || channelCenterlineName.equals(leveeCenterlineName) 
					//									|| polygonCenterlineName.equals(leveeCenterlineName)){
					//								JOptionPane.showMessageDialog(_gui, "Your selections must all be different!",
					//										"Error", JOptionPane.ERROR_MESSAGE);
					//							}else {
					//								dataEntryDialog.disposeDialog();
					//								_gui.pressArrowButton();
					//								_app.movePolygonCenterlinePointsToLeveeCenterline(_net, polygonCenterlineName, channelCenterlineName, 
					//										leveeCenterlineName);
					//							}
					//						}else{
					////							dataEntryDialog.removeWindowListener(this);
					//							dataEntryDialog.disposeDialog();
					//							_gui.pressArrowButton();
					//							CsdpFunctions.setPolygonCenterlinePointsToLeveeCenterlineDialogOpen(false);
					//						}
					//						CsdpFunctions.setPolygonCenterlinePointsToLeveeCenterlineDialogOpen(false);
					//						_gui.pressArrowButton();
					//					}//windowClosed
					//					
					//					public void windowActivated(WindowEvent arg0) {}
					//				});
				}else if(this.centerlineNameSource==READ_CENTERLINE_NAMES_FROM_FILE) {
					String title = "Snap Polygon Centerline Points to Levee Centerline";
					String instructions = "<HTML><BODY>"
							+ "Before proceeding, make sure you have a network file loaded that contains 3 types of centerlines:<BR>"
							+ "1. A channel centerline, with a numeric name<BR>"
							+ "2. A centerline representing a polygon used to estimate GIS conveyance characteristics for the centerline<BR>"
							+ "The name of the polygon centerline should be XXX_chanpoly where 'XXX' is the channel centerline name<BR>"
							+ "3. A centerline representing a levee. The name of the levee centerline should be XXX_levee_* where<BR>"
							+ "'XXX' is the channel centerline name.<BR><BR>"
							+ "Snaps (moves) some of the points in the polygon centerline to the levee centerline.<BR><BR>"
							+ "Specify a tab-delimited *.txt file below that contains the following comma-separated values:<BR>"
							+ "Column 0: channelCenterlineName<BR>"
							+ "Column 1: ignored<BR>"
							+ "Column 2: polygonCenterlineName<BR>"
							+ "Column 3: Left bank leveeCenterlineIndex(es)<BR>"
							+ "Column 4: Right bank leveeCenterlineIndex(es)<BR>"
							+ "Levee centerline indices begin with a number; you only need to specify the number.<BR>"
							+ "if more than one index needs to be specified for one side of the channel, values should be comma separated<BR><BR>"
							+ "</HTML></BODY>";

					final String[] names = new String[]{"Centerline specification filename"};
					String[] defaultValues = new String[] {""};
					int[] dataTypes = new int[] {DataEntryDialog.FILE_SPECIFICATION_TYPE};
					boolean[] disableIfNull = new boolean [] {true};
					String[] extensions = new String[] {"txt"};
					String[] tooltips = new String[] {""}; 
					boolean modal = true;

					DataEntryDialog dataEntryDialog = new DataEntryDialog(_gui, title, instructions, names,
							defaultValues, dataTypes, disableIfNull, extensions, tooltips, modal);

					int response = dataEntryDialog.getResponse();
					if(response==DataEntryDialog.OK) {
						String directory = dataEntryDialog.getDirectory(names[0]).toString();
						String filename = dataEntryDialog.getFilename(names[0]);
						AsciiFileReader asciiFileReader = new AsciiFileReader(directory+File.separator+filename);
						//						Vector<String> channelCenterlineNamesVector = new Vector<String>();
						Vector<String> polygonCenterlineNamesVector = new Vector<String>();
						Vector<String> leveeCenterlineIndicesVector = new Vector<String>();
						//The file will have a 4th column, which contains a second levee value (one levee for each bank)
						Vector<String> secondLeveeCenterlineIndicesVector = new Vector<String>();

						int i=0;
						while(true){
							//skip header line
							if(i>0) {
								String line = asciiFileReader.getNextLine();
								if(line==null) break;
								String parts[] = line.split("\t");
								try {
									//							    	channelCenterlineNamesVector.addElement(parts[0]);
									//skip column 1
									polygonCenterlineNamesVector.addElement(parts[2]);
									leveeCenterlineIndicesVector.addElement(parts[3]);
									secondLeveeCenterlineIndicesVector.addElement(parts[4]);
								}catch(Exception e) {
									System.out.println("skipping line: "+line);
									//							    	e.printStackTrace();
									//							    	JOptionPane.showMessageDialog(_gui, "File format error", "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
							i++;
						}
						asciiFileReader.close();
						for(int j=0; j<polygonCenterlineNamesVector.size(); j++) {
							_gui.pressSelectCursorAkaArrowButton();
							String leveeCenterlineIndicesString = leveeCenterlineIndicesVector.get(j).trim().replaceAll("\"", "");
							//							System.out.println("leveeCenterlineIndicesString="+leveeCenterlineIndicesString);
							if(!leveeCenterlineIndicesString.equals("no adj needed") && leveeCenterlineIndicesString.length()>0) {
								String[] parts = leveeCenterlineIndicesString.split(",");
								String[] leveeCenterlineIndicesArray = new String[parts.length];
								for(int k=0; k<parts.length; k++) {
									String centerlineNameOrIndex = parts[k].trim();
									if(centerlineNameOrIndex.indexOf("_")<0) {
										centerlineNameOrIndex += "_levee";
									}
									leveeCenterlineIndicesArray[k]= centerlineNameOrIndex; 
								}
								//								System.out.println("about to call movePolygonCenterlinePoints: j, polygonName, channelCenterlineName, leveeCenterlineIndices=" + 
								//										j+","+polygonCenterlineNamesVector.get(j)+","+channelCenterlineNamesVector.get(j)+","+leveeCenterlineIndicesArray);
								_app.snapPolygonCenterlinePointsToLeveeCenterline(polygonCenterlineNamesVector.get(j),  
										leveeCenterlineIndicesArray);
							}

							String secondLeveeCenterlineIndicesString = secondLeveeCenterlineIndicesVector.get(j).trim().replaceAll("\"", "");
							//							System.out.println("secondLeveeCenterlineIndicesString="+secondLeveeCenterlineIndicesString);
							if(!secondLeveeCenterlineIndicesString.equals("no adj needed") &&
									secondLeveeCenterlineIndicesString.length()>0) {
								String[] secondLeveeParts = secondLeveeCenterlineIndicesString.split(",");
								String[] secondLeveeCenterlineIndicesArray = new String[secondLeveeParts.length];
								for(int k=0; k<secondLeveeParts.length; k++) {
									String centerlineNameOrIndex = secondLeveeParts[k].trim();
									if(centerlineNameOrIndex.indexOf("_")<0) {
										centerlineNameOrIndex += "_levee";
									}
									secondLeveeCenterlineIndicesArray[k]= centerlineNameOrIndex; 
								}
								//								System.out.println("about to call movePolygonCenterlinePoints: j, polygonName, channelCenterlineName, leveeCenterlineIndices=" + 
								//										j+","+polygonCenterlineNamesVector.get(j)+","+channelCenterlineNamesVector.get(j)+","+secondLeveeCenterlineIndicesArray);
								_app.snapPolygonCenterlinePointsToLeveeCenterline(polygonCenterlineNamesVector.get(j), 
										secondLeveeCenterlineIndicesArray);
							}
						}
					}
				}else {

				}
			}else {
				JOptionPane.showMessageDialog(_gui, "A Move centerline points dialog is already open!\n"
						+ "You must finish your work in the other dialog first", "Dialog alreay open", JOptionPane.ERROR_MESSAGE);
			}
		}
	}//inner class SnapPolygonCenterlinepointsToLeveeCenterline


	/**
	 * Compares two network files. Summarizes differences, comparing every point
	 * in every centerline. Writes summary info to a file.
	 */
	public class TCompareNetwork extends FileIO implements ActionListener {
		// true if the two network filenames and output filename are
		// approved by the user.
		protected boolean _filenamesApproved = false;
		// the two networks to be compared
		Network _net1, _net2;
		String _networkFilename1, _networkFilename2, _outputFilename, _networkFiletype1, _networkFiletype2,
		_outputFiletype;
		String _directory1, _directory2, _outputDirectory;

		public TCompareNetwork(CsdpFrame gui) {
			super(gui, _networkDialogMessage1, _networkErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_networkExtensions, _numNetworkExtensions);

			// get names of two network files. Call method in App to compare
			// them.
			_jfcNetwork1.setDialogTitle(_networkDialogMessage1);
			_jfcNetwork1.setApproveButtonText("Select");
			_jfcNetwork1.addChoosableFileFilter(_networkFilter);
			_jfcNetwork1.setFileFilter(_networkFilter);

			_jfcNetwork2.setDialogTitle(_networkDialogMessage2);
			_jfcNetwork2.setApproveButtonText("Select");
			_jfcNetwork2.addChoosableFileFilter(_networkFilter);
			_jfcNetwork2.setFileFilter(_networkFilter);

			_jfcNetworkOut.setDialogTitle(_networkOutputMessage);
			_jfcNetworkOut.setApproveButtonText("Write");
			_jfcNetworkOut.addChoosableFileFilter(_owaWriteFilter);
			_jfcNetworkOut.setFileFilter(_owaWriteFilter);

		}// constructor

		/**
		 * uses dialog to get name from user
		 */
		protected String getFilename() {
			// networkFilename1 and networkFilename2 and outputFilename are used
			// to
			// store the complete filename initially; later they
			// become the filename without the extension.

			_filenamesApproved = false;

			if (CsdpFunctions.getNetworkDirectory() != null) {
				_jfcNetwork1.setCurrentDirectory(CsdpFunctions.getNetworkDirectory());
			} else if (CsdpFunctions.getOpenDirectory() != null) {
				_jfcNetwork1.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			} else
				System.out.println("no file selected(TCompare Network)");

			_networkFileState1 = _jfcNetwork1.showOpenDialog(this._gui);
			if (_networkFileState1 == JFileChooser.APPROVE_OPTION) {
				_networkFilename1 = _jfcNetwork1.getName(_jfcNetwork1.getSelectedFile());
				if (_networkFilename1 != null) {
					parseFilename(_networkFilename1);
					_networkFilename1 = _filename;
					_networkFiletype1 = _filetype;
					_directory1 = _jfcNetwork1.getCurrentDirectory().getAbsolutePath() + File.separator;
				}
				// now get the next filename

				_jfcNetwork2.setCurrentDirectory(_jfcNetwork1.getCurrentDirectory());
				_networkFileState2 = _jfcNetwork2.showOpenDialog(this._gui);
				if (_networkFileState2 == JFileChooser.APPROVE_OPTION) {
					_networkFilename2 = _jfcNetwork2.getName(_jfcNetwork2.getSelectedFile());
					if (_networkFilename2 != null) {
						parseFilename(_networkFilename2);
						_networkFilename2 = _filename;
						_networkFiletype2 = _filetype;
						_directory2 = _jfcNetwork2.getCurrentDirectory().getAbsolutePath() + File.separator;
					}

					// now get the output filename
					_jfcNetworkOut.setCurrentDirectory(_jfcNetwork2.getCurrentDirectory());
					_networkOutputFileState = _jfcNetworkOut.showOpenDialog(this._gui);
					if (_networkOutputFileState == JFileChooser.APPROVE_OPTION) {
						_outputFilename = _jfcNetworkOut.getName(_jfcNetworkOut.getSelectedFile());
						if (_outputFilename != null) {
							parseFilename(_outputFilename);
							_outputFilename = _filename;
							_outputFiletype = _filetype;
							_outputDirectory = _jfcNetworkOut.getCurrentDirectory().getAbsolutePath() + File.separator;
							_filenamesApproved = true;
							// now do the comparison
						} // if the outputfilename isn't null
					} // if the outputfilename is approved

				} // if user approves selection of 2nd network filename
				else if (_networkFileState1 == JFileChooser.CANCEL_OPTION) {
					_cancel = true;
				}
			} // if user approves selection of 1st network filename
			else if (_networkFileState2 == JFileChooser.CANCEL_OPTION) {
				_cancel = true;
			}
			return "a.a"; // no need to return anything
		}// getFilename

		/**
		 * compare two network files
		 */
		public boolean accessFile() {
			boolean success = false;
			_net1 = _app.nReadStore(this._gui, _directory1, _networkFilename1 + "." + _networkFiletype1);
			_net2 = _app.nReadStore(this._gui, _directory2, _networkFilename2 + "." + _networkFiletype2);
			// success = _app.compareNetworks(_networkFilename1,
			// _networkFiletype1,
			// _directory1, _net1,
			// _networkFilename2, _networkFileytpe2,
			// _directory2, _net2,
			// _outputFilename, _outputFiletype,
			// _outputDirectory);

			success = _app.compareNetworks(this._gui, _networkFilename1, _net1, _networkFilename2, _net2,
					_outputFilename, _outputFiletype, _outputDirectory);
			return success;
		}// accessFile

		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}


	}// TCompareNetwork

	/**
	 * Calculates equivalent rectangular cross-sections for every irregular
	 * channel in the network. Reads channels.inp, irregular_xsects_copy.inp,
	 * and xsects.inp, to make sure every channel in channels.inp has something
	 * assigned to it.
	 */
	public class TCalcRect implements ActionListener {
		CsdpFrame _gui;
		Network _net;
		String _filename;
		String _filetype;

		public TCalcRect(CsdpFrame gui) {
			_gui = gui;

			_jfcCalcRect.setDialogTitle(_crOpenDialogMessage);
			_jfcCalcRect.setApproveButtonText("Enter output filename");
			_jfcCalcRect.addChoosableFileFilter(_calcRectFilter);

			_jfcDSMChan.setDialogTitle("open DSM2 channel connectivity file");
			_jfcDSMChan.setApproveButtonText("Open");
			_jfcDSMChan.addChoosableFileFilter(_DSMChanFilter);

			_jfcIrregXsects.setDialogTitle("Open irregular_xsects_copy file");
			_jfcIrregXsects.setApproveButtonText("Open");
			_jfcIrregXsects.addChoosableFileFilter(_irregXsectsFilter);

			_jfcXsectsInp.setDialogTitle("Open DSM2 xsects file");
			_jfcXsectsInp.setApproveButtonText("Open");
			_jfcXsectsInp.addChoosableFileFilter(_xsectsInpFilter);
		}

		public void actionPerformed(ActionEvent e) {
			_net = _gui.getNetwork();
			String outputFilename = null;
			String outputFiletype = null;
			String DSMFilename = null;
			String DSMFiletype = null;
			String irregXsectsFilename = null;
			String irregXsectsFiletype = null;
			String xsectsInpFilename = null;
			String xsectsInpFiletype = null;

			boolean success = getOutputFilename();
			if (success == true) {
				outputFilename = _filename;
				outputFiletype = _filetype;
			}
			if (success == true) {
				success = getChannelsInpFilename();
				if (success == true) {
					DSMFilename = _filename;
					DSMFiletype = _filetype;
				}
			}
			if (success == true) {
				success = getIrregXsectsFilename();
				if (success == true) {
					irregXsectsFilename = _filename;
					irregXsectsFiletype = _filetype;
				}
			}
			if (success == true) {
				success = getXsectsInpFilename();
				if (success == true) {
					xsectsInpFilename = _filename;
					xsectsInpFiletype = _filetype;
				}
			}
			if (success = true) {
				accessFile(outputFilename + "." + outputFiletype, DSMFilename + "." + DSMFiletype,
						irregXsectsFilename + "." + irregXsectsFiletype, xsectsInpFilename + "." + xsectsInpFiletype);
			}
		}// actionPerformed

		/**
		 * get output filename (the name of the file that will have the
		 * equivalent rectangular cross-sections in it
		 */
		private boolean getOutputFilename() {
			String outputFilename = null;
			boolean success = false;

			if (_net != null) {
				if (CsdpFunctions.getNetworkDirectory() != null) {
					_jfcCalcRect.setCurrentDirectory(CsdpFunctions.getNetworkDirectory());
				} else if (CsdpFunctions.getOpenDirectory() != null) {
					_jfcCalcRect.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
				} else
					System.out.println("no file selected (TCalcRect)");

				_calcRectFileState = _jfcCalcRect.showOpenDialog(this._gui);
				if (_calcRectFileState == JFileChooser.APPROVE_OPTION) {
					outputFilename = _jfcCalcRect.getName(_jfcCalcRect.getSelectedFile());
					_crOpenDirectory = _jfcCalcRect.getCurrentDirectory().getAbsolutePath() + File.separator;
					if (outputFilename != null) {
						// this probably isn't necessary.
						// CsdpFunctions.setNetworkDirectory
						// (_jfcCalcRect.getCurrentDirectory().
						// getAbsolutePath()+File.separator);
						CsdpFunctions.setOpenDirectory(
								_jfcCalcRect.getCurrentDirectory().getAbsolutePath() + File.separator);
						parseFilename(outputFilename);
					} // if outputfilename isn't null
				} else if (_calcRectFileState == JFileChooser.CANCEL_OPTION) {
					_filename = null;
					_filetype = null;
					success = false;
				}
			}
			if (_filename != null && _filetype != null) {
				success = true;
			}
			return success;
		}// getOutputFilename

		/**
		 * get name of dsm channel connectivity file (channels.inp)
		 */
		private boolean getChannelsInpFilename() {
			String DSMFilename = null;
			boolean success = false;
			if (CsdpFunctions.getOpenDirectory() != null) {
				_jfcDSMChan.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			}
			_DSMChanFileState = _jfcDSMChan.showOpenDialog(this._gui);
			if (_DSMChanFileState == JFileChooser.APPROVE_OPTION) {
				DSMFilename = _jfcDSMChan.getName(_jfcDSMChan.getSelectedFile());
				_DSMOpenDirectory = _jfcDSMChan.getCurrentDirectory().getAbsolutePath() + File.separator;

				if (DSMFilename != null) {
					CsdpFunctions
					.setOpenDirectory(_jfcDSMChan.getCurrentDirectory().getAbsolutePath() + File.separator);
					parseFilename(DSMFilename);
				}

			} else if (_calcRectFileState == JFileChooser.CANCEL_OPTION) {
				DSMFilename = null;
				success = false;
			}
			if (_filename != null && _filetype != null) {
				success = true;
			}
			return success;
		}// getChannelsInpFilename

		/**
		 * get name of irregular xsects copy file (irregular_xsects_copy.inp)
		 */
		private boolean getIrregXsectsFilename() {
			String irregXsectsFilename = null;
			boolean success = false;
			if (CsdpFunctions.getOpenDirectory() != null) {
				_jfcIrregXsects.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			}
			_irregXsectsFileState = _jfcIrregXsects.showOpenDialog(this._gui);
			if (_irregXsectsFileState == JFileChooser.APPROVE_OPTION) {
				irregXsectsFilename = _jfcIrregXsects.getName(_jfcIrregXsects.getSelectedFile());
				_irregXsectsDirectory = _jfcIrregXsects.getCurrentDirectory().getAbsolutePath() + File.separator;

				if (irregXsectsFilename != null) {
					CsdpFunctions
					.setOpenDirectory(_jfcIrregXsects.getCurrentDirectory().getAbsolutePath() + File.separator);
					parseFilename(irregXsectsFilename);
				}

			} else if (_calcRectFileState == JFileChooser.CANCEL_OPTION) {
				irregXsectsFilename = null;
				success = false;
			}
			if (_filename != null && _filetype != null) {
				success = true;
			}
			return success;
		}// getIrregXsectsFilename

		/**
		 * get name of dsm2 rectangular xsect file (xsects.inp)
		 */
		private boolean getXsectsInpFilename() {
			String xsectsInpFilename = null;
			boolean success = false;
			if (CsdpFunctions.getOpenDirectory() != null) {
				_jfcXsectsInp.setCurrentDirectory(CsdpFunctions.getOpenDirectory());
			}
			_xsectsInpFileState = _jfcXsectsInp.showOpenDialog(this._gui);
			if (_xsectsInpFileState == JFileChooser.APPROVE_OPTION) {
				xsectsInpFilename = _jfcXsectsInp.getName(_jfcXsectsInp.getSelectedFile());
				_xsectsInpDirectory = _jfcXsectsInp.getCurrentDirectory().getAbsolutePath() + File.separator;

				if (xsectsInpFilename != null) {
					CsdpFunctions
					.setOpenDirectory(_jfcXsectsInp.getCurrentDirectory().getAbsolutePath() + File.separator);
					parseFilename(xsectsInpFilename);
				}

			} else if (_calcRectFileState == JFileChooser.CANCEL_OPTION) {
				xsectsInpFilename = null;
				success = false;
			}
			if (_filename != null && _filetype != null) {
				success = true;
			}
			return success;
		}// getXsectsInpFilename

		/**
		 * read the input file(s) and write equivalent rectangular xs
		 */
		private void accessFile(String outputFilename, String DSMFilename, String irregXsectsFilename,
				String xsectsInpFilename) {
			_app.calcRect(this._gui, _net, outputFilename, _crOpenDirectory, DSMFilename, _DSMOpenDirectory,
					irregXsectsFilename, _irregXsectsDirectory, xsectsInpFilename, _xsectsInpDirectory);
		}

		/**
		 * separates filename into prefix and extension
		 */
		protected void parseFilename(String filename) throws NullPointerException {
			try {
				int dotIndex = filename.indexOf(".", 0);
				if (dotIndex >= 0) {
					_filename = filename.substring(0, dotIndex);
					_filetype = filename.substring(dotIndex + 1);
				} else if (dotIndex < 0) {
					_filename = null;
					_filetype = null;
				}
			} catch (Exception e) {
				System.out.println("no filename specified");
			} // catch
		}// parseFilename

	}// class TCalcRect

	/**
	 * changes option to echo time series input (the stage values at various
	 * locations for a given date)
	 */
	public class TEchoTimeSeriesInput implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setEchoTimeSeriesInput(true);
			} else {
				CsdpFunctions.setEchoTimeSeriesInput(false);
			}
		}
	}// TEchoTimeSeriesInput

	/**
	 * changes option to echo xsect input (all the cross-section input)
	 */
	public class TEchoXsectInput implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setEchoXsectInput(true);
			} else {
				CsdpFunctions.setEchoXsectInput(false);
			}

		}

	}// TEchoXsectInput

	/**
	 * changes option to echo toe drain input
	 */
	public class TEchoToeDrainInput implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setEchoToeDrainInput(true);
			} else {
				CsdpFunctions.setEchoToeDrainInput(false);
			}

		}

	}// TEchoToeDrainInput

	/**
	 * changes option to print all calculations for every xsect--very long; for
	 * debugging
	 */
	public class TPrintXsectResults implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setPrintXsectResults(true);
			} else {
				CsdpFunctions.setPrintXsectResults(false);
			}
		}

	}// TPrintXsectResults

	/**
	 * changes option to use Fremont Weir
	 */
	public class TUseFremontWeir implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setUseFremontWeir(true);
			} else {
				CsdpFunctions.setUseFremontWeir(false);
			}
		}

	}// TUseFremontWeir

	/**
	 * changes option to use Toe Drain Restriction. This means that below a
	 * specified elevation, the water will only be in the toe drain. The minimum
	 * station and elevation are specified in a separate input file. No maximum
	 * station is specified--this is assumed to be the last station.
	 */
	public class TUseToeDrainRestriction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				CsdpFunctions.setUseToeDrainRestriction(true);
			} else {
				CsdpFunctions.setUseToeDrainRestriction(false);
			}
		}

	}// TUseToeDrainRestriction

	/**
	 * Get name of file to process and call functions to open, read, and
	 * calculate area and volume at specified elevation. This class is used to
	 * read a file with cross-sections in station/elevation format. The output
	 * will be top width and cross-sectional area for each cross-section and
	 * also the total area and volume of the reach. All values will be
	 * calculated for a specified elevation. Each cross-section must have an X1
	 * line (HEC2 format) which has a second value which is the cross-section's
	 * distance measured along the reach. Example: X1 12223
	 *
	 * @author
	 * @version $Id:
	 */
	public class TOpenWaterCalc extends FileIO implements ActionListener {
		// FileDialog _fdOutput;
		// TextDialog _td;

		FileAndRadioDialog _farDialog;

		// ResizableStringArray _fileMessages = new ResizableStringArray(4);
		String _fileMessages[] = new String[4];
		String _radioLabels[] = new String[6];
		boolean _radioStates[] = new boolean[6];
		JFileChooser _fileDialogs[] = new JFileChooser[4];
		ItemListener _radioListeners[] = new ItemListener[6];

		public TOpenWaterCalc(CsdpFrame gui) {
			super(gui, _openDialogMessage, _openErrorMessage, _openSuccessMessage, _openFailureMessage, false,
					_openExtensions, _numOpenExtensions);
			_fileMessages[0] = "owa file (.owa)";
			_fileMessages[1] = "Time series data file (.tsd)";
			_fileMessages[2] = "Toe Drain file (.txt)";
			_fileMessages[3] = "Output File (.txt)";
			_radioLabels[0] = "echo time series input";
			_radioLabels[1] = "echo xs input(long)";
			_radioLabels[2] = "echo toe drain input";
			_radioLabels[3] = "print xs results(long)";
			_radioLabels[4] = "use Fremont Weir";
			_radioLabels[5] = "use Toe Drain restriction";
			_radioStates[0] = CsdpFunctions.getEchoTimeSeriesInput();
			_radioStates[1] = CsdpFunctions.getEchoXsectInput();
			_radioStates[2] = CsdpFunctions.getEchoToeDrainInput();
			_radioStates[3] = CsdpFunctions.getPrintXsectResults();
			_radioStates[4] = CsdpFunctions.getUseFremontWeir();
			_radioStates[5] = CsdpFunctions.getUseToeDrainRestriction();
			_radioListeners[0] = new TEchoTimeSeriesInput();
			_radioListeners[1] = new TEchoXsectInput();
			_radioListeners[2] = new TEchoToeDrainInput();
			_radioListeners[3] = new TPrintXsectResults();
			_radioListeners[4] = new TUseFremontWeir();
			_radioListeners[5] = new TUseToeDrainRestriction();

			// _fdOutput = new FileDialog(gui, "Enter the output filename");

			// _td = new TextDialog((Frame)gui,"Enter the elevation for area and
			// volume estimate",true);

			// OWA file
			_jfc.setDialogTitle(_openDialogMessage);
			_jfc.setApproveButtonText("Open");
			_jfc.addChoosableFileFilter(_owaOpenFilter);
			_jfc.setFileFilter(_owaOpenFilter);
			// TSD file
			_jfcStation.setDialogTitle(_stationDialogMessage);
			_jfcStation.addChoosableFileFilter(_stationOpenFilter);
			_jfcStation.setApproveButtonText("Open");
			// TXT toe drain file
			_jfcToeDrain.setDialogTitle(_toeDrainMessage);
			_jfcToeDrain.addChoosableFileFilter(_toeDrainFilter);
			_jfcToeDrain.setFileFilter(_toeDrainFilter);
			_jfcToeDrain.setApproveButtonText("Open");
			// TXT output file
			_jfcOut.setDialogTitle(_saveDialogMessage);
			_jfcOut.setApproveButtonText("Write");
			_jfcOut.addChoosableFileFilter(_owaWriteFilter);
			_jfcOut.setFileFilter(_owaWriteFilter);

			_fileDialogs[0] = _jfc;
			_fileDialogs[1] = _jfcStation;
			_fileDialogs[2] = _jfcToeDrain;
			_fileDialogs[3] = _jfcOut;

			_farDialog = new FileAndRadioDialog(gui, "Open Water Area Calculations", true, _fileMessages, _radioLabels,
					_radioStates, _radioListeners, _fileDialogs);
		}

		public void setCancel(boolean value) {
			_cancel = value;
		}

		/**
		 * TEMPORARY? -- OVERRIDING METHOD IN SUPERCLASS. call methods to open,
		 * read, and store data files, and to plot/display data
		 */
		public void actionPerformed(ActionEvent e) {
			_cancel = false;
			boolean success = false;
			String filename = null;
			boolean filenamesOk = false;
			while (filenamesOk != true && _cancel == false) {
				// String fname = getFilename();
				String unusedFilename = getFilename();
				_cancel = _farDialog._cancel;
				if (_cancel == false) {
					// check all filenames and radio buttons
					// then get directories and filenames and filetypes.
					String owaFilename = _farDialog.getFilename(0);
					_stationFilename = _farDialog.getFilename(1);
					_toeDrainFilename = _farDialog.getFilename(2);
					_owaOutputFilename = _farDialog.getFilename(3);

					_owaInputDirectory = _jfc.getCurrentDirectory().getAbsolutePath() + File.separator;
					_stationDirectory = _jfcStation.getCurrentDirectory().getAbsolutePath() + File.separator;
					_toeDrainDirectory = _jfcToeDrain.getCurrentDirectory().getAbsolutePath() + File.separator;
					_owaOutputDirectory = _jfcOut.getCurrentDirectory().getAbsolutePath() + File.separator;
					System.out.println("TOpenWaterCalc.actionPerformed: checking filenames: " + owaFilename + ","
							+ _stationFilename + "," + _toeDrainFilename + "," + _owaOutputFilename);
					parseFilename(owaFilename);
					String owaFiletype = _filetype;
					parseFilename(_stationFilename);
					String tsdFiletype = _filetype;
					parseFilename(_toeDrainFilename);
					String tdFiletype = _filetype;
					parseFilename(_owaOutputFilename);
					String outFiletype = _filetype;
					boolean echoTSI = CsdpFunctions.getEchoTimeSeriesInput();
					boolean echoXSI = CsdpFunctions.getEchoXsectInput();
					boolean echoTDI = CsdpFunctions.getEchoToeDrainInput();
					boolean printXSR = CsdpFunctions.getPrintXsectResults();
					boolean useFremont = CsdpFunctions.getUseFremontWeir();
					boolean useToeDrain = CsdpFunctions.getUseToeDrainRestriction();

					// if all input ok, do it, otherwise display error dialog
					System.out.println(
							"before null pointer: _openExtensions, _stationExtensions, _saveExtensions, owaFiletype, tsdFiletype, outFiletype="
									+ _openExtensions + "," + _stationExtensions + "," + _saveExtensions + ","
									+ owaFiletype + "," + tsdFiletype + "," + outFiletype);
					if (owaFiletype.equals(_openExtensions[0]) && tsdFiletype.equals(_stationExtensions[0])
							&& outFiletype.equals(_saveExtensions[0])) {
						if (useToeDrain == false || (useToeDrain && tdFiletype.equals(_toeDrainExtensions[0]))) {
							filenamesOk = true;
							parseFilename(owaFilename);
						} else {
							JOptionPane.showMessageDialog(_gui, "no toe drain file selected!", "Error", JOptionPane.ERROR_MESSAGE);
							filenamesOk = false;
						}
					} else {
						if (owaFiletype.equals(_openExtensions[0]) == false) {
							JOptionPane.showMessageDialog(_gui, "specified owa file does not have .owa extension", "Error", JOptionPane.ERROR_MESSAGE);
						}
						if (tsdFiletype.equals(_stationExtensions[0]) == false) {
							JOptionPane.showMessageDialog(_gui, "specified tsd file does not have .tsd extension", "Error", JOptionPane.ERROR_MESSAGE);
						}
						if (outFiletype.equals(_saveExtensions[0]) == false) {
							JOptionPane.showMessageDialog(_gui, "specified output file does not have .txt extension", "Error", JOptionPane.ERROR_MESSAGE);
						}
						filenamesOk = false;
					}
				} // not cancelling
				// filename = fname;
			} // while

			if (filenamesOk && _cancel == false) {
				success = accessFile();
				if (success == false)
					JOptionPane.showMessageDialog(_gui, "Open Water Area Calculations failed!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				System.out.println("not doing anything.  filenamesOk, _cancel=" + filenamesOk + "," + _cancel);
			} // if
			_cancel = false;
			if (_reportSuccess && success == true) {
				JOptionPane.showMessageDialog(_gui, "Open Water Area Calculations succeeded!", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
			// }//while
		}// actionPerformed

		/**
		 * uses dialog box to get filename from user
		 */
		protected String getFilename() {
			_farDialog.pack();
			_farDialog.setVisible(true);
			String inputFilename = "test";
			return inputFilename;
		}// getFilename

		/**
		 * read and process Open Water Area data file
		 */
		public boolean accessFile() {
			// System.out.println("filename,filetype before calling
			// app.openwaterareareadcalculate="+_filename+","+_filetype);

			System.out.println("calculating.  useFremontWeir = " + CsdpFunctions.getUseFremontWeir());
			_owaNet = _app.openWaterAreaReadCalculate(this._gui, _filename, _filetype, _owaOutputFilename,
					_stationFilename, _toeDrainFilename);

			System.out.println("Done.  _owaNet=" + _owaNet);
			return true; // no need to warn if it fails.]
		}// accessFile

		@Override
		public void checkAndSaveUnsavedEdits() {
			//not needed
		}

	} // TOpenWaterCalc class

	App _app;
	BathymetryPlot _plot;
	Network _owaNet;
	private static final boolean DEBUG = false;

	private static final String _networkDialogMessage1 = "Select the 1st network file";
	private static final String _networkDialogMessage2 = "Select the 2nd network file";
	private static final String _networkErrorMessage = "Only .cdn extension allowed";
	private static final String[] _networkExtensions = { "cdn" };
	private static final int _numNetworkExtensions = 1;
	private static final String _networkOutputMessage = "Enter output filename";

	private static final String _crOpenDialogMessage = "Calculate equiv. rect. xs: Enter output filename";
	private static final String _crOpenErrorMessage = "Only .txt extension allowed";
	private static final String _crOpenSuccessMessage = "Created equivalent rect. xs file";
	private static final String _crOpenFailureMessage = "ERROR: unable to save .txt file";
	private static final String[] _crSaveExtensions = { "txt" };
	private static final int _crNumSaveExtensions = 1;
	private static final String[] _dsmOpenExtensions = { "inp" };
	private static final int _dsmNumOpenExtensions = 1;

	private static final String _openDialogMessage = "Select Open Water Area file";
	private static final String _openErrorMessage = "Only .owa extension allowed";
	private static final String[] _openExtensions = { "owa" };
	private static final int _numOpenExtensions = 1;

	private static final String _saveDialogMessage = "Save owa(.txt) file";
	private static final String _saveErrorMessage = "Only .txt extension allowed";
	private static final String[] _saveExtensions = { "txt" };
	private static final int _numSaveExtensions = 1;

	private static final String _saveSuccessMessage = "saved file";
	private static final String _saveFailureMessage = "ERROR:  UNABLE TO SAVE .txt FILE";
	private static final String _openSuccessMessage = "";
	private static final String _openFailureMessage = "ERROR: unable to open file";
	private static final String _stationDialogMessage = "Select time series data file";
	private static final String _toeDrainMessage = "Select Toe Drain File";
	private static final String[] _stationExtensions = { "tsd" };
	private static final int _numStationExtensions = 1;
	private static final String[] _toeDrainExtensions = { "txt" };
	private static final int _numToeDrainExtensions = 1;

	private String _owaOutputDirectory;
	private String _owaInputDirectory;
	private String _owaOutputFilename;
	private String _stationDirectory;
	private String _toeDrainDirectory;
	private String _crOpenDirectory;
	private String _DSMOpenDirectory;
	private String _irregXsectsDirectory;
	private String _xsectsInpDirectory;
	/**
	 * The elevation used for estimating area and volume of the open water area
	 */
	CsdpFileFilter _owaOpenFilter;
	CsdpFileFilter _owaWriteFilter;
	CsdpFileFilter _stationOpenFilter;
	CsdpFileFilter _toeDrainFilter;
	CsdpFileFilter _networkFilter;
	CsdpFileFilter _calcRectFilter;
	CsdpFileFilter _DSMChanFilter;
	CsdpFileFilter _irregXsectsFilter;
	CsdpFileFilter _xsectsInpFilter;
	JFileChooser _jfcNetwork1 = new JFileChooser();
	JFileChooser _jfcNetwork2 = new JFileChooser();
	JFileChooser _jfcNetworkOut = new JFileChooser();
	JFileChooser _jfcCalcRect = new JFileChooser();
	JFileChooser _jfcDSMChan = new JFileChooser();
	JFileChooser _jfcOut = new JFileChooser();
	JFileChooser _jfcStation = new JFileChooser();
	JFileChooser _jfcToeDrain = new JFileChooser();
	JFileChooser _jfcIrregXsects = new JFileChooser();
	JFileChooser _jfcXsectsInp = new JFileChooser();
	private int _xsectFileState = -Integer.MAX_VALUE;
	private int _stationFileState = -Integer.MAX_VALUE;
	String _stationFilename;
	private int _toeDrainFileState = -Integer.MAX_VALUE;
	private int _calcRectFileState = -Integer.MAX_VALUE;
	private int _networkFileState1 = -Integer.MAX_VALUE;
	private int _networkFileState2 = -Integer.MAX_VALUE;
	private int _networkOutputFileState = -Integer.MAX_VALUE;
	private int _DSMChanFileState = -Integer.MAX_VALUE;
	private int _irregXsectsFileState = -Integer.MAX_VALUE;
	private int _xsectsInpFileState = -Integer.MAX_VALUE;
	private String _toeDrainFilename;
	CsdpFrame _gui;
	DSMChannels _DSMChannels;
}// class ToolsMenu
