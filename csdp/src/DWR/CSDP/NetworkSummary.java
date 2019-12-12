package DWR.CSDP;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;

import org.w3c.dom.html.HTMLIsIndexElement;

import Acme.Nnrpd.NewsDb;

/**
 * Creates a report containing diagnostic information. 
 * Instances of this class are also used by the class GISSummaryStatisticGraphFrame, to create a window containing plots of 
 * GIS vs DSM2 channel volume summary statistics. However, the writeResults method must be called before doing so--bad design. This should 
 * be fixed.
 * A network summary report uses the following input files:

	An existing channels.inp file (such as channel_std_delta_grid_NAVD_20150129.inp). This file is used to get existing channel lengths for comparison, and to determine channel connectivity.
	The currently loaded network file.
	A DSM2 output (.hof) file which was created from the network file by running DSM2-Hydro with geometry created using the currently loaded network file with printlevel>=5
	A 2m meter DEM CutFill validity file, which was created based upon a visual inspection of the extent of the coverage of channel polygons with data in the 2m DEM files, using ArcGIS. If coverage is complete or very nearly complete, the validity is true.
	CutFill results files, each containing results from the CutFill operations for a given DEM.
	(Optional): a list of channel groups. Default is the list of groups for which polygons were created and used in the CutFill operations: "448_449_572_573, 439_440_441_451_452_453_454, 438_443_444_450_570_571_574_575,290-294,281_282_295_296_297_301". You can add to this list.
	The report contains, for a given stage (usually 0.0 NAVD)
	
	Channel: The name/number of the DSM2 channel. Could also be a group of channels. Examples: Sherman Lake would be identified as: 290-294, Grizzly Bay would be identified as: 448_449_572_573
	Comparison of channels.inp length vs CSDP length:
	Channels.inp length: length specified for DSM2 in the DSM2 channels file above.
	CSDP length: length calculated by the CSDP that will be used to replace the 'Channels.inp length'.
	% change: the change in length CSDP vs Channels.inp
	CSDP Average width: For determining GIS volume estimate validity–average width should be at least 3 times the DEM grid size.
	If CSDP Volume is significantly different from DSM2 Volume, that would mean the effects of interpolation should be considered when modifying cross-sections.
	CSDP Volume: Channel volume calculated by CSDP for specified elevation assuming no inter-channel interpolation. Not used for comparison, but may be of interest to some.
	Not used for comparison, but may be of interest to some.
	CSDP Wetted Area: Wetted area calculated by CSDP for specified elevation assuming no inter-channel interpolation. Not used for comparison, but may be of interest to some.
	CSDP Surface Area: Surface area calculated by CSDP for specified elevation assuming no inter-channel interpolation.
	CSDP Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using CSDP cross-sections. Important for numerical stability. Max area ratios should be < 2.0.
	If CSDP Volume is significantly different from DSM2 Volume, that would mean the effects of interpolation should be considered when modifying cross-sections.
	DSM2 Volume: Channel volume calculated at specified elevation using virtual cross-sections from DSM2 output file. Used for comparison with GIS volumes.
	Not used for comparison, but may be of interest to some:
	DSM2 Wetted Area: Wetted area calculated at specified elevation using virtual cross-sections from DSM2 output file
	DSM2 Surface Area: Surface area calculated at specified elevation using virtual cross-sections from DSM2 output file
	DSM2 Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using virtual cross-sections. Important for numerical stability. Max area ratios should be < 2.0.
	GIS 2m Max* Volume: The GIS calculated channel volume, converted to ft3, using 2m DEM.
	GIS 2m Max* Area: The GIS calculated 2d area, converted to ft2, using 2m DEM.
	GIS 10 Max* Volume: The GIS calculated channel volume, converted to ft3, using 10m DEM.
	GIS 10m Max* Area: The GIS calculated 2D area, converted to ft2, using 10m DEM.
	DSM2-2m Vol: The difference between the DSM2 virtual cross-section volume and the 2m DEM volume.
	DSM2-10m Vol: The difference between the DSM2 virtual cross-section volume and the 10m DEM volume.
	2m Vol % diff: The % difference between the DSM2 virtual cross-section volume and the 2m DEM volume.
	10m Vol % diff: The % difference between the DSM2 virtual cross-section volume and the 10m DEM volume.
	CSDP Avg Width: The average width of all the CSDP cross-sections in a channel at the specified elevation.
	2m Width Ratio: the CSDP Avg Width / 2m.
	10m Width Ratio: the CSDP Avg Width / 10m.
	2m Validity: 2m DEM volume and area calculations will be considered valid if a 2m DEM covers (or nearly covers) the entire channel 
		polygon, and the 2m Width Ratio >= specified value, and the max bottom elevation < specified value.
	10m Validity: 10m DEM volume and area calculations will be considered valid if the 10m Width Ratio >= specified value and the 
		max bottom elevation < specified value. Coverage is assumed to be complete for all channels.
	Valid 2m Vol: The value of GIS 2m Volume, if 2m Validity==true, null otherwise.
	Valid 10m Vol: The value of GIS 10m Volume, if 10m Validity==true, null otherwise.
	DSM2-Valid 2m Vol: The value of DSM2-2m Vol if 2m Validity==true, null otherwise.
	DSM2-Valid 10m Vol: The value of DSM2-10m Vol if 10m Validity==true, null otherwise.
	Valid 2m Vol % diff: The value of 2m Vol % diff if 2m Validity==true, null otherwise.
	Valid 10m Vol % diff: The value of 10m Vol % diff if 10m Validity==true, null otherwise.
	Valid Combined 2m10m Vol % diff: The value of 2m Vol % diff if not null, otherwise 10m Vol % diff if not null, otherwise null
	CSDP highest bottom elevation: The highest bottom elevation of all the cross-sections within the channel. Can help identify cross-sections that are likely to dry up.
	CSDP lowest bottom elevation: The lowest bottom elevation of all the cross-seections within the channel. can help identify cross-sections that are likely to dry up.
	historical min stage: the minimum stage from a historical hydro run for the channel.
	histMinStage - CSDP lowest elevation: the difference between the two. Should always be positive, probably above a minimum value to prevent drying up.
	CSDP XS with no points: The indices of the cross-sections in the channel that have no points. These cross-sections should be removed or edited.
	CSDP XS within 500.0 feet: The indices of the cross-sections in the channel that are within 500.0 feet of each other. This could help identify duplicate cross-sections or unnecessary cross-sections. 
	These can help identify cross-sections that need to be adjusted to improve Max Area Ratio.
	CSDP XS with Min area: The index of the cross-section in the channel that has the smallest area at the specified elevation
	CSDP XS with Max area: The index of the cross-section in the channel that has the largest area at the specified elevation
	CSDP XS with duplicate stations: The indices of the cross-sections in the channel that have duplicate station values. These cross-section need to be fixed.
	We no longer care about negative dConveyance, so these can probably be ignored:
	CSDP XS with -dK: The indices of the cross-sections in the channel that have negative dConveyance at any elevation.
	CSDP XS with -dK in intertidal zone: the indices of the cross-sections in the channel that have negative dConveyance in the intertidal zone. (intertidal zone is assumed to be limited to the range -2.5 < Z < 17.5 ft NAVD88)
 *When calculating GIS results, some channels overlap more than one DEM.  When this happens, only the largest values of Volume and 2D Area are used, because they are assumed to be associated with the DEM that covers a greater portion of the polygon. If the coverage is not complete, the value should be invalidated visually in the "2m DEM Validity" file.
 * @author btom
 *
 */
public class NetworkSummary {
	private DSMChannels dsmChannels;
	private String outputFilePath;
	private CsdpFrame csdpFrame;
	private Network network;
	/*
	 * Optional: directory containing a DSM2 ASCII output file (.hof)
	 */
	private File dsm2HofDirectory;
	/*
	 * Optional: filename of a DSM2 ASCII output file (.hof)
	 */
	private String dsm2HofFilename;
	/*
	 * If true, a .hof file has been specified
	 */
	private boolean dsm2HofFileSpecified;
	/*
	 * A vector containing all channel numbers as strings, read from the DSM2 channels.inp file.
	 */
	private Vector<String> chanVector;
	/*
	 * Optional: Hashtable with key=description, value=channel range
	 * Example channel range strings:
	 * 	290-294
	 *  281_282_400
	 */
	private Hashtable<String, String> chanGroupsHashtable = new Hashtable<String, String>();
	/*
	 * Optional. If not null, contains the filenames of .csv files containing gis calculated channel volumes.
	 */
	private String[] gisVolumeFilenames;
	private boolean gisVolumeFilesSpecified;

	/*
	 * results using 2m DEM with CutFill operation
	 */
	private Hashtable<String, Double> gis2mVolCuFtHashtable = new Hashtable<String, Double>();
	/*
	 * results using 2m DEM with CutFill operation
	 */
	private Hashtable<String, Double> gis2mSurfAreaSqFtHashtable = new Hashtable<String, Double>();
	/*
	 * results using 10m DEM with CutFill operation
	 */
	private Hashtable<String, Double> gis10mVolCuFtHashtable = new Hashtable<String, Double>();
	/*
	 * results using 10m DEM with CutFill operation
	 */
	private Hashtable<String, Double> gis10mSurfAreaSqFtHashtable = new Hashtable<String, Double>();
	
	private Hashtable<String, Double> dsm2VolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2WettedAreaHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2SurfAreaHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2MaxAreaRatioHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpChanLengthHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> channelsInpLengthHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> percentChangeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpAverageWidthHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpWettedAreaHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpSurfaceAreaHashtable = new Hashtable<String, Double>();

	private Hashtable<String, Double> csdpMaxAreaRatioHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpHighestBottomElevHashtable = new Hashtable<String, Double>();
	private Hashtable<String, String> csdpXsWithNoPointsHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXsWithinSpecifiedDistanceHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXSWithMinAreaHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXSWithMaxAreaHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXsWithDuplicateStationsHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXSWithNegDKHashtable = new Hashtable<String, String>();
	private Hashtable<String, String> csdpXSWithNegDKInIntertidalHashtable = new Hashtable<String, String>();
	
	/*
	 * For plotting individual channel results in GISSummaryGraphFrame
	 */
	private Hashtable<String, Double> gisValid2mVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gisValid10mVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTwoMeterVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTenMeterVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2ValidPercentVolumeDifferenceVsTenMeterHashtable = new Hashtable<String, Double>();
	/*
	 * For plotting channel group results in GISSummaryGraphFrame
	 */
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeChanGroupsHashtable = new Hashtable<String, Double>();
	/*
	 * To keep groups in order as specified, which should be generally from west to east
	 */
	private Vector<String> chanGroupNamesInOrderVector = new Vector<String>();
	private File twoMeterValidityDirectory;
	private String twoMeterValidityFilename;
	private boolean twoMeterValiditySpecified;
	/*
	 * Key is chan, value is true if the channel has complete or very close to complete 2m DEM coverage
	 * In this class, the value is updated based on width ratio, which is channel width/DEM grid size. Valid if >3.
	 * Also depends upon channel bottom elevation--if close to zero, invalid.
	 */
	private Hashtable<String, Boolean> twoMeterDEMValidityHashtable = new Hashtable<String, Boolean>();
	
	/*
	 * Currently depends only on width ratio, which is channel width/DEM grid size. Valid if >3.
	 * Also depends upon channel bottom elevation--if close to zero, invalid.
	 */
	private Hashtable<String, Boolean> tenMeterDEMValidityHashtable = new Hashtable<String, Boolean>(); 

	/*
	 * If bottom elevation is higher than this value, DEM estimates will be considered invalid.
	 */
	private static final double MAX_VALID_BOTTOM_ELEVATION = 0.0; 
	/*
	 * Width ratio is the average channel width/DEM grid size, which is either 2 or 10 since we're using 2m and 10m DEMs.
	 */
	private static final double MAX_WIDTH_RATIO = 3.5;
	
	/*
	 * Key is chan, value is any notes made regarding the validity/polygon coverage
	 */
	private Hashtable<String, String> twoMeterDEMValidityNotesHashtable;
	
	private Hashtable<String, Double> dsm2VolumeMinusGISTwoMeterVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2PercentVolumeDifferenceVsTwoMeterHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> twoMeterWidthRatioHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISTenMeterVolumeHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2PercentVolumeDifferenceVsTenMeterHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> tenMeterWidthRatioHashtable = new Hashtable<String, Double>();
	
	/*
	 * The above hashtables will contain channel group information only when there was a GIS polygon for the given channel
	 * group. The hashtables below use the channel group name (i.e. "Grizzly Bay") as keys. Most if not all of these values 
	 * are calculated in the createNetworkSummary method.
	 */
	
	/*
	 * Validity for 2m DEM results for channel groups is based upon 
	 * 1) the value read from the 2m DEM validity file, only if the group has a corresponding GIS polygon, otherwise
	 * 2) otherwise, it is assumed to be valid.
	 */
	private Hashtable<String, Boolean> twoMeterDEMValidityChanGroupsHashtable = new Hashtable<String, Boolean>();
	private Hashtable<String, Boolean> tenMeterDEMValidityChanGroupsHashtable = new Hashtable<String, Boolean>();
	private Hashtable<String, Double> dsm2PercentVolumeDifferenceVsTwoMeterChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gisValid2mVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISTwoMeterVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2PercentVolumeDifferenceVsTenMeterChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gisValid10mVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpVolumeChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpWetAreaChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> csdpSurfaceAreaChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2WetAreaChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2SurfAreaChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis2mVolCuFtChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis2mSurfAreaSqFtChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis10mVolCuFtChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis10mSurfAreaSqFtChanGroupsHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2VolumeMinusGISTenMeterVolumeChanGroupsHashtable = new Hashtable<String, Double>();

	private Hashtable<String, Double> dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterChanGroupsHashtable = new Hashtable<String, Double>();
	private String histMinStageFilename;
	private File histMinStageDirectory;
	private boolean histMinStageFileSpecified;
	private Hashtable<String, Double> csdpLowestBottomElevHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> histMinStageHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> minHistStageMinusChanBottomHashtable = new Hashtable<String, Double>();
	

	/*
	 * Constructor
	 */
	public NetworkSummary(CsdpFrame csdpFrame, Network network, DSMChannels dsmChannels, File dsm2HofDirectory, 
			String dsm2HofFilename, String outputFilePath, Vector<String> chanGroupNamesInOrderVector, 
			Hashtable<String, String> chanGroupsHashtable, File twoMeterValidityDirectory, String twoMeterValidityFilename, 
			String[] gisVolumeFilenames, File histMinStageDirectory, String histMinStageFilename) {
		this.csdpFrame = csdpFrame;
		this.network = network;
		this.dsmChannels = dsmChannels;
		this.dsm2HofDirectory = dsm2HofDirectory;
		this.dsm2HofFilename = dsm2HofFilename;
		this.outputFilePath = outputFilePath;
		this.dsm2HofFileSpecified = false;
		if(this.dsm2HofDirectory!=null && this.dsm2HofFilename!=null) {
			this.dsm2HofFileSpecified = true;
		}
		this.chanVector = new Vector<String>();
		this.chanGroupNamesInOrderVector = chanGroupNamesInOrderVector;
		this.chanGroupsHashtable = chanGroupsHashtable;
		this.twoMeterValidityDirectory = twoMeterValidityDirectory;
		this.twoMeterValidityFilename = twoMeterValidityFilename;
		this.twoMeterValiditySpecified = false;
		if(this.twoMeterValidityDirectory != null && this.twoMeterValidityFilename!=null && this.twoMeterValidityFilename.length()>0) {
			this.twoMeterValiditySpecified = true;
		}
		this.gisVolumeFilesSpecified = false;
		this.gisVolumeFilenames = gisVolumeFilenames;
		if(gisVolumeFilenames != null && gisVolumeFilenames.length>0) {
			this.gisVolumeFilesSpecified = true;
		}
		
		this.histMinStageFilename = histMinStageFilename;
		this.histMinStageDirectory = histMinStageDirectory;
		this.histMinStageFileSpecified = false;
		if(this.histMinStageDirectory != null && this.histMinStageFilename!=null) {
			this.histMinStageFileSpecified = true;
		}
		createNetworkSummary();
	}//constructor

	/*
	 * Creates a report with summary statistics. 
	 * User specifies 3 files:
	 * 1. DSM2 Channels.inp file
	 * 2. DSM2 .hof file, created using printlevel>=5 
	 * 		(Optional; if included report will include additional information calculated from virtual cross-sections)
	 * 3. output .txt file
	 * Output written to tab delimited ASCII file with specified name. 
	 */
	public void createNetworkSummary() {
		DSM2VirtualCrossSectionVolume dsm2VirtualCrossSectionVolume = null;
		if(this.dsm2HofFileSpecified) {
			dsm2VirtualCrossSectionVolume = new DSM2VirtualCrossSectionVolume(dsm2HofDirectory.toString(), dsm2HofFilename);
			this.dsm2VolumeHashtable = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.VOLUME_RESULTS);
			this.dsm2WettedAreaHashtable = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.WETTED_AREA_RESULTS);
			this.dsm2SurfAreaHashtable = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.SURFACE_AREA_RESULTS);
			this.dsm2MaxAreaRatioHashtable = dsm2VirtualCrossSectionVolume.getResults(DSM2VirtualCrossSectionVolume.MAX_AREA_RATIO_RESULTS);
		}			

		GISCutFillResults gisCutFillResults = null;
		if(this.gisVolumeFilesSpecified) {
			gisCutFillResults = new GISCutFillResults(this.gisVolumeFilenames);
			this.gis2mVolCuFtHashtable = gisCutFillResults.get2mVolumeCuFtHashtable();
			this.gis2mSurfAreaSqFtHashtable = gisCutFillResults.get2mSurfAreaSqFtHashtable();
			this.gis10mVolCuFtHashtable = gisCutFillResults.get10mVolumeCuFtHashtable();
			this.gis10mSurfAreaSqFtHashtable = gisCutFillResults.get10mSurfAreaSqFtHashtable();
		}
		
		TwoMeterDEMValidity twoMeterDEMValidityObject = null;
		if(this.twoMeterValiditySpecified) {
			twoMeterDEMValidityObject = new TwoMeterDEMValidity(this.twoMeterValidityDirectory, this.twoMeterValidityFilename);
			this.twoMeterDEMValidityHashtable = twoMeterDEMValidityObject.getValidityHashtable();
			this.twoMeterDEMValidityNotesHashtable = twoMeterDEMValidityObject.getNotesHashtable();
		}
		
		HistMinStageResults histMinStageResults = null;
		if(this.histMinStageFileSpecified) {
			histMinStageResults = new HistMinStageResults(this.histMinStageDirectory, this.histMinStageFilename);
			this.histMinStageHashtable  = histMinStageResults.getMinStageHashtable();
		}
		
		this.network.sortCenterlineNames();
		for(int i=0; i<this.dsmChannels.getNumChannels(); i++) {
			this.chanVector.addElement(this.dsmChannels.getChanNum(i).trim());
		}

		for(int i=0; i<this.chanVector.size(); i++) {
			String chan = this.chanVector.get(i);
			Centerline centerline = null;
			if(this.network.centerlineExists(chan)) {
				centerline = this.network.getCenterline(chan);
			}

			double csdpChanLength = -Double.MAX_VALUE; 
			double channelsInpLength = this.dsmChannels.getLength(chan);
			channelsInpLengthHashtable.put(chan, channelsInpLength);
			double percentChange = -Integer.MAX_VALUE;
			double csdpAverageWidth = -Double.MAX_VALUE;
			double csdpVolume = -Double.MAX_VALUE;
			double csdpWettedArea = -Double.MAX_VALUE;
			double csdpSurfaceArea = -Double.MAX_VALUE;
			if(centerline!=null) {
				csdpChanLength=centerline.getLengthFeet();
				percentChange = 100.0 * ((csdpChanLength-channelsInpLength) / channelsInpLength);
				csdpAverageWidth = centerline.getAverageWidthFeet(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS, centerline.getLengthFeet());
				csdpVolume = centerline.getChannelVolumeEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				csdpWettedArea = centerline.getChannelWettedAreaEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
				csdpSurfaceArea = centerline.getChannelSurfaceAreaEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			}
			this.csdpChanLengthHashtable.put(chan, csdpChanLength);
			this.percentChangeHashtable.put(chan, percentChange);
			this.csdpAverageWidthHashtable.put(chan, csdpAverageWidth);
			this.csdpVolumeHashtable.put(chan, csdpVolume);
			this.csdpWettedAreaHashtable.put(chan, csdpWettedArea);
			this.csdpSurfaceAreaHashtable.put(chan, csdpSurfaceArea);

			if(this.dsm2HofFileSpecified) {
				if(!dsm2VolumeHashtable.containsKey(chan) || !dsm2WettedAreaHashtable.containsKey(chan) || 
						!dsm2SurfAreaHashtable.containsKey(chan) || !dsm2MaxAreaRatioHashtable.containsKey(chan)) {
					JOptionPane.showMessageDialog(this.csdpFrame, "Error in App.createNetworkSummaryReport: a .hof file \n"
							+ "was specified, but the .hof file is missing \n"
							+ "information for channel "+chan, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			double csdpMaxAreaRatio = -Double.MAX_VALUE;
			double csdpHighestBottomElev = -Double.MAX_VALUE;
			double csdpLowestBottomElev = Double.MAX_VALUE;
			Vector<Integer> csdpXsWithNoPoints = null;
			HashSet<Integer> csdpXsWithinSpecifiedDistanceHashSet = null;
			int csdpXSWithMinArea = -Integer.MAX_VALUE;
			int csdpXSWithMaxArea = -Integer.MAX_VALUE;
			Vector<Integer> csdpXsWithDuplicateStations = null;
			Vector<Integer> csdpXSWithNegDK = null;
			Vector<Integer> csdpXSWithNegDKInIntertidal = null;
			if(centerline!=null) {
				csdpMaxAreaRatio = centerline.getMaxAreaRatio();
				csdpHighestBottomElev = centerline.getHighestBottomElevation();
				csdpLowestBottomElev = centerline.getLowestBottomElevation();
				double lowestHistStage = -Double.MAX_VALUE;
				double minHistStageMinusChanBottom = -Double.MAX_VALUE;
				if(this.histMinStageHashtable.containsKey(chan)) {
					lowestHistStage = this.histMinStageHashtable.get(chan);
					minHistStageMinusChanBottom = lowestHistStage - csdpLowestBottomElev;
				}else {
					JOptionPane.showMessageDialog(csdpFrame, "Channel "+chan+" does not exist in historical run; unable to determine the minimum historical stage.", 
							"WARNING", JOptionPane.WARNING_MESSAGE);
				}
				
				this.minHistStageMinusChanBottomHashtable.put(chan, minHistStageMinusChanBottom);
				csdpXsWithNoPoints = centerline.getXSWithNoPointsIndices();
				csdpXsWithinSpecifiedDistanceHashSet = centerline.getXSWithinSpecifiedDistanceIndices(CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING);
				int[] minMaxAreaIndices = centerline.getMinMaxAreaXsectIndices();
				csdpXSWithMinArea = minMaxAreaIndices[0];
				csdpXSWithMaxArea = minMaxAreaIndices[1];
				csdpXsWithDuplicateStations = centerline.getDuplicateStationsXsectIndices();
				csdpXSWithNegDK = centerline.getNegDKXsectIndices();
				csdpXSWithNegDKInIntertidal = centerline.getNegDKIntertidalXsectIndices();
			}
			String na = "N/A";
			String csdpXSWithMinAreaString = null;
			String csdpXSWithMaxAreaString = null;
			if(csdpXSWithMinArea>=0 && csdpXSWithMaxArea>=0) {
				csdpXSWithMinAreaString = String.valueOf(csdpXSWithMinArea);
				csdpXSWithMaxAreaString = String.valueOf(csdpXSWithMaxArea);
			}else {
				csdpXSWithMinAreaString = na;
				csdpXSWithMaxAreaString = na;
			}

			this.csdpChanLengthHashtable.put(chan, csdpChanLength);
			this.percentChangeHashtable.put(chan, percentChange);
			this.csdpAverageWidthHashtable.put(chan, csdpAverageWidth);
			this.csdpVolumeHashtable.put(chan, csdpVolume);
			this.csdpWettedAreaHashtable.put(chan, csdpWettedArea);
			this.csdpSurfaceAreaHashtable.put(chan, csdpSurfaceArea);
			this.csdpMaxAreaRatioHashtable.put(chan, csdpMaxAreaRatio);
			this.csdpHighestBottomElevHashtable.put(chan, csdpHighestBottomElev);
			this.csdpLowestBottomElevHashtable .put(chan, csdpLowestBottomElev);
			this.csdpXSWithMinAreaHashtable.put(chan, csdpXSWithMinAreaString);
			this.csdpXSWithMaxAreaHashtable.put(chan, csdpXSWithMaxAreaString);

			String csdpXsWithNoPointsString = CsdpFunctions.abstractCollectionToString(csdpXsWithNoPoints);
			String csdpXsWithinSpecifiedDistanceString = CsdpFunctions.abstractCollectionToString(csdpXsWithinSpecifiedDistanceHashSet);
			String csdpXsWithDuplicateStationsString = CsdpFunctions.abstractCollectionToString(csdpXsWithDuplicateStations);
			String csdpXsWithNegDKString = CsdpFunctions.abstractCollectionToString(csdpXSWithNegDK);
			String csdpXsWithNegDKInIntertidalString = CsdpFunctions.abstractCollectionToString(csdpXSWithNegDKInIntertidal);

			this.csdpXsWithNoPointsHashtable.put(chan, csdpXsWithNoPointsString);
			this.csdpXsWithinSpecifiedDistanceHashtable.put(chan, csdpXsWithinSpecifiedDistanceString);
			this.csdpXsWithDuplicateStationsHashtable.put(chan, csdpXsWithDuplicateStationsString);
			this.csdpXSWithNegDKHashtable.put(chan, csdpXsWithNegDKString);
			this.csdpXSWithNegDKInIntertidalHashtable.put(chan, csdpXsWithNegDKInIntertidalString);

			//now calculate summary statistics 
			if(this.twoMeterValiditySpecified) {
				double dsm2Volume = this.dsm2VolumeHashtable.get(chan);
				double csdpAvgWidth = this.csdpAverageWidthHashtable.get(chan);
				
				if(this.gis2mVolCuFtHashtable.containsKey(chan)){
					double twoMeterVolume = this.gis2mVolCuFtHashtable.get(chan);
					double dsm2Minus2mVol = dsm2Volume - twoMeterVolume;
					double twoMeterVolPercentDiff = 100.0 * (dsm2Minus2mVol/twoMeterVolume);
					double twoMeterWidthRatio = csdpAvgWidth / CsdpFunctions.metersToFeet(2.0);
					double centerlineMaxBotElv = -Double.MAX_VALUE;
					if(centerline!=null) {
						centerlineMaxBotElv = centerline.getHighestBottomElevation();
					}
					boolean twoMeterDEMValidity = this.twoMeterDEMValidityHashtable.get(chan) && 
							twoMeterWidthRatio >= MAX_WIDTH_RATIO && 
							centerlineMaxBotElv <= MAX_VALID_BOTTOM_ELEVATION; 
					
					this.twoMeterDEMValidityHashtable.put(chan, twoMeterDEMValidity);
					this.dsm2VolumeMinusGISTwoMeterVolumeHashtable.put(chan, dsm2Minus2mVol);
					this.dsm2PercentVolumeDifferenceVsTwoMeterHashtable.put(chan,  twoMeterVolPercentDiff);
					this.twoMeterWidthRatioHashtable.put(chan, twoMeterWidthRatio);

					if(twoMeterDEMValidity) {
						//store values needed for GISSummaryGraphWindow
						this.gisValid2mVolumeHashtable.put(chan, twoMeterVolume);
						this.dsm2VolumeMinusGISValidTwoMeterVolumeHashtable.put(chan, dsm2Minus2mVol);
						this.dsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable.put(chan, twoMeterVolPercentDiff);
					}
				}//if

				if(this.gis10mVolCuFtHashtable.containsKey(chan)) {
					double tenMeterVolume = this.gis10mVolCuFtHashtable.get(chan);
					double dsm2Minus10mVol = dsm2Volume - tenMeterVolume;
					double tenMeterVolPercentDiff = 100.0 * (dsm2Minus10mVol/tenMeterVolume);
					double tenMeterWidthRatio = csdpAvgWidth / CsdpFunctions.metersToFeet(10.0);
					boolean tenMeterDEMValidity = tenMeterWidthRatio >= MAX_WIDTH_RATIO && 
							centerline.getHighestBottomElevation() <= MAX_VALID_BOTTOM_ELEVATION;
					
					this.tenMeterDEMValidityHashtable.put(chan, tenMeterDEMValidity);
					this.dsm2VolumeMinusGISTenMeterVolumeHashtable.put(chan, dsm2Minus10mVol);
					this.dsm2PercentVolumeDifferenceVsTenMeterHashtable.put(chan, tenMeterVolPercentDiff);
					this.tenMeterWidthRatioHashtable.put(chan, tenMeterWidthRatio);
	
					if(tenMeterDEMValidity) {
						this.gisValid10mVolumeHashtable.put(chan, tenMeterVolume);
						this.dsm2VolumeMinusGISValidTenMeterVolumeHashtable.put(chan, dsm2Minus10mVol);
						this.dsm2ValidPercentVolumeDifferenceVsTenMeterHashtable.put(chan, tenMeterVolPercentDiff);
					}
				}//if
			}//if twoMeterValiditySpecified

			if(this.dsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable.containsKey(chan)) {
				dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterHashtable.put(chan, this.dsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable.get(chan));
			}else if(this.dsm2ValidPercentVolumeDifferenceVsTenMeterHashtable.containsKey(chan)) {
				dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterHashtable.put(chan, this.dsm2ValidPercentVolumeDifferenceVsTenMeterHashtable.get(chan));
			}
		}//while (for each channel)

		String na = "N/A";
		if(this.chanGroupsHashtable !=null && this.chanGroupsHashtable.size()>0) {
			for(int i=0; i<this.chanGroupNamesInOrderVector.size(); i++) {
				String chanGroupName = this.chanGroupNamesInOrderVector.get(i);
				String chanGroup = this.chanGroupsHashtable.get(chanGroupName);
				Vector<String> allChanInGroup = CsdpFunctions.parseChanGroupString(this.csdpFrame, chanGroup);
				//channels groups have no values for channels.inp length, csdpChannelLength, percentLengthChange, csdpAverageWidth
				double csdpVol = 0.0;
				double csdpWetArea = 0.0;
				double csdpSurfaceArea = 0.0;
				double dsm2Vol = 0.0;
				double dsm2WetArea = 0.0;
				double dsm2SurfArea = 0.0;
				
				double gis2mVol = 0.0;
				double gis2mSurfArea = 0.0;
				double gis10mVol = 0.0;
				double gis10mSurfArea = 0.0;
				
				//Gis results may have a channel group value in the hashtables.
				for(int j=0; j<allChanInGroup.size(); j++) {
					String chan = allChanInGroup.get(j).trim();
					csdpVol += this.csdpVolumeHashtable.get(chan);
					csdpWetArea += this.csdpWettedAreaHashtable.get(chan);
					csdpSurfaceArea += this.csdpSurfaceAreaHashtable.get(chan);
					dsm2Vol += this.dsm2VolumeHashtable.get(chan);
					dsm2WetArea += this.dsm2WettedAreaHashtable.get(chan);
					dsm2SurfArea += this.dsm2SurfAreaHashtable.get(chan);

					//when doing the gis calculation for some channels, individual polygons were not created, but the 
					//channels were included in groups.
					if(this.gis2mVolCuFtHashtable.containsKey(chan)) {
						gis2mVol += this.gis2mVolCuFtHashtable.get(chan);
					}
					if(this.gis2mSurfAreaSqFtHashtable.containsKey(chan)) {
						gis2mSurfArea += this.gis2mSurfAreaSqFtHashtable.get(chan);
					}
					if(this.gis10mVolCuFtHashtable.containsKey(chan)) {
						gis10mVol += this.gis10mVolCuFtHashtable.get(chan);
					}
					if(this.gis10mSurfAreaSqFtHashtable.containsKey(chan)) {
						gis10mSurfArea += this.gis10mSurfAreaSqFtHashtable.get(chan);
					}
				}//for each chan
				
				//If there was a polygon used in the GIS calculation matching the channel group description, 
				//get the results and use them if they are larger than individual channel results, if any.
				if(this.gis2mVolCuFtHashtable.containsKey(chanGroup)) {
					gis2mVol = Math.max(gis2mVol, this.gis2mVolCuFtHashtable.get(chanGroup));
				}
				if(this.gis2mSurfAreaSqFtHashtable.containsKey(chanGroup)) {
					gis2mSurfArea = Math.max(gis2mSurfArea, this.gis2mSurfAreaSqFtHashtable.get(chanGroup));
				}
				if(this.gis10mVolCuFtHashtable.containsKey(chanGroup)) {
					gis10mVol = Math.max(gis10mVol, this.gis10mVolCuFtHashtable.get(chanGroup));
				}
				if(this.gis10mSurfAreaSqFtHashtable.containsKey(chanGroup)) {
					gis10mSurfArea = Math.max(gis10mSurfArea, this.gis10mSurfAreaSqFtHashtable.get(chanGroup));
				}
				
				this.csdpVolumeChanGroupsHashtable.put(chanGroupName, csdpVol);
				this.csdpWetAreaChanGroupsHashtable.put(chanGroupName, csdpWetArea);
				this.csdpSurfaceAreaChanGroupsHashtable.put(chanGroupName, csdpSurfaceArea);
				
				if(this.dsm2HofFileSpecified) {
					this.dsm2VolumeChanGroupsHashtable.put(chanGroupName, dsm2Vol);
					this.dsm2WetAreaChanGroupsHashtable.put(chanGroupName, dsm2WetArea);
					this.dsm2SurfAreaChanGroupsHashtable.put(chanGroupName, dsm2SurfArea);
				}
				if(this.gisVolumeFilesSpecified) {
					this.gis2mVolCuFtChanGroupsHashtable.put(chanGroupName, gis2mVol);
					this.gis2mSurfAreaSqFtChanGroupsHashtable.put(chanGroupName, gis2mSurfArea);
					this.gis10mVolCuFtChanGroupsHashtable.put(chanGroupName, gis10mVol);
					this.gis10mSurfAreaSqFtChanGroupsHashtable.put(chanGroupName, gis10mSurfArea);
				}

				this.dsm2VolumeChanGroupsHashtable.put(chanGroupName, dsm2Vol);
				if(this.twoMeterValiditySpecified) {
					double dsm2Minus2mVol = dsm2Vol - gis2mVol;
					double twoMeterVolPercentDiff = 100.0 * (dsm2Minus2mVol/gis2mVol);
					boolean twoMeterDEMValidity = true;
					try {
						twoMeterDEMValidity = this.twoMeterDEMValidityHashtable.get(chanGroup); //not using width ratio for channel groups 
					}catch(NullPointerException e) {
						JOptionPane.showMessageDialog(this.csdpFrame, "Can't determine 2m DEM CutFill Validity for chanGroup: "+chanGroupName+
								". \n\nThis could be a channel group for which there was no GIS polygon. \n"
								+ "I will assume it's valid, and attempt to use individual channel results to calculate volume.", "Validity Not Found", 
								JOptionPane.INFORMATION_MESSAGE);
					}
					this.dsm2VolumeMinusGISTwoMeterVolumeChanGroupsHashtable.put(chanGroupName, dsm2Minus2mVol);
					this.dsm2PercentVolumeDifferenceVsTwoMeterChanGroupsHashtable.put(chanGroupName, twoMeterVolPercentDiff);
					this.twoMeterDEMValidityChanGroupsHashtable.put(chanGroupName, twoMeterDEMValidity);
					if(twoMeterDEMValidity) {
						this.gisValid2mVolumeChanGroupsHashtable.put(chanGroupName, gis2mVol);
						this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable.put(chanGroupName, dsm2Minus2mVol);
						this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.put(chanGroupName, twoMeterVolPercentDiff);
					}
					double dsm2Minus10mVol = dsm2Vol - gis10mVol;
					double tenMeterVolPercentDiff = 100.0 * (dsm2Minus10mVol/gis10mVol);
					boolean tenMeterDEMValidity = true; //not using width ratio for channel groups

					this.dsm2VolumeMinusGISTenMeterVolumeChanGroupsHashtable.put(chanGroupName, dsm2Minus10mVol);
					this.dsm2PercentVolumeDifferenceVsTenMeterChanGroupsHashtable.put(chanGroupName, tenMeterVolPercentDiff);
					this.tenMeterDEMValidityChanGroupsHashtable.put(chanGroupName, tenMeterDEMValidity);
					
					if(tenMeterDEMValidity) {
						this.gisValid10mVolumeChanGroupsHashtable.put(chanGroupName, gis10mVol);
						//now store values needed for GISSummaryGraphFrame
						this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable.put(chanGroupName, dsm2Minus10mVol);
						this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.put(chanGroupName, tenMeterVolPercentDiff);
					}
				}//if two meter validity specified
				
				if(this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.containsKey(chanGroupName)) {
					dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterChanGroupsHashtable.put(chanGroupName, this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.get(chanGroupName));
				}else if(this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.containsKey(chanGroupName)) {
					dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterChanGroupsHashtable.put(chanGroupName, this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.get(chanGroupName));
				}
			}//for each chanGroup
		}//if there are any chanGroup strings
	}//createNetworkSummaryReport

	/*
	 * Writes the results to the network summary .txt file
	 */
	public void writeResults() {
		Vector<String> reportText = new Vector<String>(); 
		reportText.addElement("CSDP Network Summary Report for Elevation="+CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
		reportText.addElement("");
		reportText.addElement("Network path: "+CsdpFunctions.getNetworkDirectory()+File.separator+CsdpFunctions.getNetworkFilename()+"."+CsdpFunctions.getNetworkFiletype());
		reportText.addElement("DSM2 channels path: "+CsdpFunctions.getDSMChannelsDirectory()+File.separator+CsdpFunctions.getDSMChannelsFilename());
		reportText.addElement("DSM2 output path: "+CsdpFunctions.getDSM2HofDirectory()+File.separator+CsdpFunctions.getDSM2HofFilename());
		reportText.addElement("");
		reportText.addElement("Channels.inp length: length specified for DSM2 in the DSM2 channels file above.");
		reportText.addElement("CSDP length: length calculated by the CSDP that will be used to replace the 'Channels.inp length'.");
		reportText.addElement("% change: the change in length CSDP vs Channels.inp");
		reportText.addElement("CSDP Average width");
		reportText.addElement("CSDP Volume: Channel volume calculated by CSDP for specified elevation assuming no inter-channel interpolation.");		
		reportText.addElement("CSDP Wetted Area: Wetted area calculated by CSDP for specified elevation assuming no inter-channel interpolation.");
		reportText.addElement("CSDP Surface Area: Surface area calculated by CSDP for specified elevation assuming no inter-channel interpolation.");
		reportText.addElement("CSDP Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using CSDP cross-sections");
		if(this.dsm2HofFileSpecified) {
			reportText.addElement("DSM2 Volume: Channel volume calculated at specified elevation using virtual cross-sections from DSM2 output file");
			reportText.addElement("DSM2 Wetted Area: Wetted area calculated at specified elevation using virtual cross-sections from DSM2 output file");
			reportText.addElement("DSM2 Surface Area: Surface area calculated at specified elevation using virtual cross-sections from DSM2 output file");
			reportText.addElement("DSM2 Max Area Ratio: The maximum ratio of cross-sectional areas within a channel using virtual cross-sections");
		}
		if(this.gisVolumeFilesSpecified) {
			reportText.addElement("GIS 2m Volume: Channel volume calculated at specified elevation using CutFill operation with 2m DEM");
			reportText.addElement("GIS 2m Area: Channel 2D area calculated at specified elevation using CutFill operation with 2m DEM");
			reportText.addElement("GIS 10m Volume: Channel volume calculated at specified elevation using CutFill operation with 10m DEM");
			reportText.addElement("GIS 10m Area: Channel 2D area calculated at specified elevation using CutFill operation with 10m DEM");
		}
		reportText.addElement("CSDP highest bottom elevation: The highest bottom elevation of all the cross-sections within the chanenl.");
		if(this.histMinStageFileSpecified) {
			reportText.addElement("CSDP lowest bottom elevation: The lowest bottom elevation of all the cross-sections within the chanenl.");
			reportText.addElement("Min Historical Stage: The lowest stage value in a channel from a historical DSM2 run");
			reportText.addElement("Min Hist Stage Minus Chan Bottom: minHistStage - lowest bottom elevation");
		}
		reportText.addElement("CSDP XS with no points: The indices of the cross-sections in the channel that have no points.");
		reportText.addElement("CSDP XS within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+
				" feet: The indices of the cross-sections in the channel that are within "+
				CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+" feet of each other.");
		reportText.addElement("CSDP XS with Min area: The index of the cross-section in the channel that has the smallest area at the specified elevation");
		reportText.addElement("CSDP XS with Max area: The index of the cross-section in the channel that has the largest area at the specified elevation");
		reportText.addElement("CSDP XS with duplicate stations: The indices of the cross-sections in the channel that have duplicate station values.");
		reportText.addElement("CSDP XS with -dK: The indices of the cross-sections in the channel that have negative dConveyance at any elevation.");
		reportText.addElement("CSDP XS with -dK in intertidal zone: the indices of the cross-sections in the channel that have negative dConveyance in the intertidal zone.");
		reportText.addElement("(intertidal zone is assumed to be limited to the range "+CsdpFunctions.INTERTIDAL_LOW_TIDE+" < Z < "+CsdpFunctions.INTERTIDAL_HIGH_TIDE+" ft NAVD88)");
		reportText.addElement("");

		String headerLine = 
				"Channel\t"
				+ "Channels.inp length\t"
				+ "CSDP Length\t"
				+ "% change\t"
				+ "CSDP Average Width\t"
				+ "CSDP Volume\t"
				+ "CSDP Wetted Area\t"
				+ "CSDP Surface Area\t"
				+ "CSDP Max Area Ratio\t";
		if(this.dsm2HofFileSpecified) {
			headerLine += 
					"DSM2 Volume\t"
					+ "DSM2 Wetted Area\t"
					+ "DSM2 Surface Area\t"
					+ "DSM2 Max Area Ratio\t";
		}
		if(this.gisVolumeFilesSpecified) {
			headerLine += 
					"GIS 2m Max Volume\t"
					+ "GIS 2m Max Area\t"
					+ "GIS 10m Max Volume\t"
					+ "GIS 10m Max Area\t";
			if(this.twoMeterValiditySpecified) {
				headerLine += 
						"DSM2-2m Vol\t"
						+ "DSM2-10m Vol\t"
						+ "2m Vol % diff\t"
						+ "10m Vol % diff\t"
						+ "CSDP Avg Width\t"
						+ "2m Width Ratio\t"
						+ "10m Width Ratio\t"
						+ "2m Validity\t"
						+ "10m Validity\t"
						+ "Valid 2m Max Vol\t"
						+ "Valid 10m Max Vol\t"
						+ "DSM2-Valid 2m Max Vol\t"
						+ "DSM2-Valid 10m Max Vol\t"
						+ "Valid 2m Vol % diff\t"
						+ "Valid 10m Vol % diff\t"
						+ "Valid 2m10m Combined Vol % diff\t";
			}
		}
		headerLine +=
				"CSDP Highest Bottom Elevation\t";
		if(this.histMinStageFileSpecified) {
			headerLine+= "CSDP Lowest Bottom Elevation\t"
					+ "Hist Min Stage\t"
					+ "Min Hist Stage Minus Chan Bottom\t";
		}
		headerLine +=
				"CSDP XS with no points\t"
				+ "CSDP XS within "+CsdpFunctions.MAXIMUM_SUGGESTED_XS_SPACING+" feet\t"
				+ "CSDP XS with Min area\t"
				+ "CSDP XS with Max area\t"
				+ "CSDP XS with duplicate stations\t"
				+ "CSDP XS With -dk\t"
				+ "CSDP XS with -dk in intertidal zone";
		reportText.addElement(headerLine);

		for(int i=0; i<this.chanVector.size(); i++) {
			String chan = this.chanVector.get(i);
			Centerline centerline = null;
			if(this.network.centerlineExists(chan)) {
				centerline = this.network.getCenterline(chan);
			}

			String csdpChanLengthString = String.format("%.0f",  this.csdpChanLengthHashtable.get(chan));
			String percentLengthChangeString = String.format("%.0f", this.percentChangeHashtable.get(chan));
			String csdpAverageWidthString = String.format("%.0f", this.csdpAverageWidthHashtable.get(chan)); 
			String csdpVolumeString = String.format("%.1f", this.csdpVolumeHashtable.get(chan));
			String csdpWettedAreaString = String.format("%.1f", this.csdpWettedAreaHashtable.get(chan));
			String csdpSurfaceAreaString = String.format("%.1f", this.csdpSurfaceAreaHashtable.get(chan));
			String dsm2VolString = String.format("%.1f", this.dsm2VolumeHashtable.get(chan));
			String dsm2WetAreaString = String.format("%.1f", this.dsm2WettedAreaHashtable.get(chan));
			String dsm2SurfAreaString = String.format("%.1f", this.dsm2SurfAreaHashtable.get(chan));

			String gis2mVolString = null;
			String gis2mSurfAreaString = null;
			String gis10mVolString = null;
			String gis10mSurfAreaString = null;

			String dsm2Minus2mVolString = "";
			String dsm2Minus10mVolString = "";
			String twoMeterVolPercentDiffString = "";
			String tenMeterVolPercentDiffString = "";
			String csdpAvgWidthString = "";
			String twoMeterWidthRatioString = "";
			String tenMeterWidthRatioString = "";
			String twoMeterValidityString = "";
			String tenMeterValidityString = "";
			String validTwoMeterVolString = "";
			String validTenMeterVolString = "";
			String dsm2MinusValid2mVolString = "";
			String dsm2MinusValid10mVolString = "";
			String validTwoMeterVolPercentDiffString = "";
			String validTenMeterVolPercentDiffString = "";
			String validCombinedTwoTenMeterVolPercentDiffString = "";
			
			if(this.gisVolumeFilesSpecified) {
				if(this.gis2mVolCuFtHashtable.containsKey(chan)) {
					gis2mVolString = String.format("%.1f",  this.gis2mVolCuFtHashtable.get(chan));
				}
				if(this.gis2mSurfAreaSqFtHashtable.containsKey(chan)) {
					gis2mSurfAreaString = String.format("%.1f",  this.gis2mSurfAreaSqFtHashtable.get(chan));
				}
				if(this.gis10mVolCuFtHashtable.containsKey(chan)) {
					gis10mVolString = String.format("%.1f",  this.gis10mVolCuFtHashtable.get(chan));
				}
				if(this.gis10mSurfAreaSqFtHashtable.containsKey(chan)) {
					gis10mSurfAreaString = String.format("%.1f",  this.gis10mSurfAreaSqFtHashtable.get(chan));
				}
				if(this.twoMeterValiditySpecified) {
					double dsm2Volume = this.dsm2VolumeHashtable.get(chan);
					double csdpAvgWidth = this.csdpAverageWidthHashtable.get(chan);
					csdpAvgWidthString = String.format("%.1f", csdpAvgWidth);
					
					if(this.gis2mVolCuFtHashtable.containsKey(chan)){
						double twoMeterVolume = this.gis2mVolCuFtHashtable.get(chan);
						double dsm2Minus2mVol = this.dsm2VolumeMinusGISTwoMeterVolumeHashtable.get(chan);
						double twoMeterVolPercentDiff = this.dsm2PercentVolumeDifferenceVsTwoMeterHashtable.get(chan);
						double twoMeterWidthRatio = this.twoMeterWidthRatioHashtable.get(chan);
						boolean twoMeterDEMValidity = this.twoMeterDEMValidityHashtable.get(chan);
						
						dsm2Minus2mVolString = String.format("%.1f", dsm2Minus2mVol);
						twoMeterVolPercentDiffString = String.format("%.1f", twoMeterVolPercentDiff);
						twoMeterWidthRatioString = String.format("%.1f", twoMeterWidthRatio);
						twoMeterValidityString = Boolean.toString(twoMeterDEMValidity);
						if(twoMeterDEMValidity) {
							validTwoMeterVolString = String.format("%.1f", twoMeterVolume);
							dsm2MinusValid2mVolString = String.format("%.1f", dsm2Minus2mVol);
							validTwoMeterVolPercentDiffString = String.format("%.1f", twoMeterVolPercentDiff);
						}
					}
					if(this.gis10mVolCuFtHashtable.containsKey(chan)) {
						double tenMeterVolume = this.gis10mVolCuFtHashtable.get(chan);
						double dsm2Minus10mVol = this.dsm2VolumeMinusGISTenMeterVolumeHashtable.get(chan);
						double tenMeterVolPercentDiff = this.dsm2PercentVolumeDifferenceVsTenMeterHashtable.get(chan);
						double tenMeterWidthRatio = this.tenMeterWidthRatioHashtable.get(chan);
						boolean tenMeterDEMValidity = this.tenMeterDEMValidityHashtable.get(chan);

						dsm2Minus10mVolString = String.format("%.1f", dsm2Minus10mVol);
						tenMeterVolPercentDiffString = String.format("%.1f", tenMeterVolPercentDiff);
						tenMeterWidthRatioString = String.format("%.1f", tenMeterWidthRatio);
						tenMeterValidityString = Boolean.toString(tenMeterDEMValidity);

						if(tenMeterDEMValidity) {
							validTenMeterVolString = String.format("%.1f", tenMeterVolume);
							dsm2MinusValid10mVolString = String.format("%.1f", dsm2Minus10mVol);
							validTenMeterVolPercentDiffString = String.format("%.1f", tenMeterVolPercentDiff);
						}
					}
					if(this.dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterHashtable.containsKey(chan)) {
						double dsm2ValidCombinedPercentVolumeDiff = this.dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterHashtable.get(chan);
						validCombinedTwoTenMeterVolPercentDiffString = String.format("%.1f", dsm2ValidCombinedPercentVolumeDiff);
					}
				}//if two meter validity file specified
			}//if gis volume files specified
			
			String csdpMaxAreaRatioString = String.format("%.2f", this.csdpMaxAreaRatioHashtable.get(chan));
			String dsm2MaxAreaRatioString = String.format("%.2f", this.dsm2MaxAreaRatioHashtable.get(chan));
			String csdpHighestBottomElevString = String.format("%.2f", this.csdpHighestBottomElevHashtable.get(chan));
			String csdpLowestBottomElevString = String.format("%.2f", this.csdpLowestBottomElevHashtable.get(chan));
			String minHistStageString = null;
			String minHistStageMinusChanBottomString = null;
			if(this.histMinStageFileSpecified) {
				minHistStageString = String.format("%.2f", this.histMinStageHashtable.get(chan));
				minHistStageMinusChanBottomString = String.format("%.2f", this.minHistStageMinusChanBottomHashtable.get(chan));
			}
			String na = "N/A";
			String csdpXsWithNoPointsString = csdpXsWithNoPointsHashtable.get(chan);
			String csdpXsWithinSpecifiedDistanceString = csdpXsWithinSpecifiedDistanceHashtable.get(chan);
			String csdpXSWithMinAreaString = csdpXSWithMinAreaHashtable.get(chan);
			String csdpXSWithMaxAreaString = csdpXSWithMaxAreaHashtable.get(chan);
			String csdpXsWithDuplicateStationsString = csdpXsWithDuplicateStationsHashtable.get(chan);
			String csdpXsWithNegDKString = csdpXSWithNegDKHashtable.get(chan);
			String csdpXsWithNegDKInIntertidalString = csdpXSWithNegDKInIntertidalHashtable.get(chan);
			double channelsInpLength = channelsInpLengthHashtable.get(chan);
			
			if(centerline==null) {
				csdpChanLengthString = na;
				percentLengthChangeString = na;
				csdpAverageWidthString = na;
				csdpVolumeString = na;
				csdpWettedAreaString = na;
				csdpSurfaceAreaString = na;
				csdpMaxAreaRatioString = na;
				csdpHighestBottomElevString = na;
				csdpXsWithNoPointsString = na;
				csdpXsWithinSpecifiedDistanceString = na;
				csdpXSWithMinAreaString = na;
				csdpXSWithMaxAreaString = na;
				csdpXsWithDuplicateStationsString = na;
				csdpXsWithNegDKString = na;
				csdpXsWithNegDKInIntertidalString = na;
			}

			String resultsLine = chan+"\t"+
					channelsInpLength+"\t"+
					csdpChanLengthString+"\t"+
					percentLengthChangeString+"\t"+
					csdpAverageWidthString+"\t"+
					csdpVolumeString+"\t"+
					csdpWettedAreaString+"\t"+
					csdpSurfaceAreaString+"\t"+
					csdpMaxAreaRatioString+"\t";
			if(this.dsm2HofFileSpecified) {
				resultsLine += dsm2VolString+"\t"+
						dsm2WetAreaString+"\t"+
						dsm2SurfAreaString+"\t"+
						dsm2MaxAreaRatioString+"\t";
			}
			if(this.gisVolumeFilesSpecified) {
				resultsLine += gis2mVolString+"\t"+
						gis2mSurfAreaString+"\t"+
						gis10mVolString+"\t"+
						gis10mSurfAreaString+"\t";
				if(this.twoMeterValiditySpecified) {
					resultsLine += 
							dsm2Minus2mVolString+"\t"+
							dsm2Minus10mVolString+"\t"+
							twoMeterVolPercentDiffString+"\t"+
							tenMeterVolPercentDiffString+"\t"+
							csdpAvgWidthString+"\t"+
							twoMeterWidthRatioString+"\t"+
							tenMeterWidthRatioString+"\t"+
							twoMeterValidityString+"\t"+
							tenMeterValidityString+"\t"+
							validTwoMeterVolString+"\t"+
							validTenMeterVolString+"\t"+
							dsm2MinusValid2mVolString+"\t"+
							dsm2MinusValid10mVolString+"\t"+
							validTwoMeterVolPercentDiffString+"\t"+
							validTenMeterVolPercentDiffString+"\t"+
							validCombinedTwoTenMeterVolPercentDiffString+"\t";
							
				}
			}
			resultsLine += csdpHighestBottomElevString+"\t";
			if(this.histMinStageFileSpecified) {
				resultsLine += csdpLowestBottomElevString+"\t"+
						minHistStageString+"\t"+
						minHistStageMinusChanBottomString+"\t";
			}
			resultsLine += 
					csdpXsWithNoPointsString+"\t"+
					csdpXsWithinSpecifiedDistanceString+"\t"+
					csdpXSWithMinAreaString+"\t"+
					csdpXSWithMaxAreaString+"\t"+
					csdpXsWithDuplicateStationsString+"\t"+
					csdpXsWithNegDKString+"\t"+
					csdpXsWithNegDKInIntertidalString;
			reportText.addElement(resultsLine);
			

		}//for each individual channel

		//////////////////////////////////////////////
		// now calculate channel group information	//
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//As of 3/20/2019, the following channel groups were calculated using GIS. This means that polygons were created
		// for the channel groups, but not for the individual channels. 
		// 448_449_572_573
		// 439_440_441_451_452_453_454
		// 438_443_444_450_570_571_574_575
		// 290-294
		// 281_282_295_296_297_301 
		// We want to be able to specify additional channel groups, for which there may not be polygons, but there may
		// be results for the individual channels that make up the specified group. For example, we could define a group for
		// Three Mile Slough, which includes channels 309 and 310. In the network summary dialog, we would enter this as 
		// 309-310 or 309_310. In the GIS calculation, there was no polygon that included both channels, but there is a 
		// polygon for 309 and a polygon for 310. We can then calculate the volume by adding the results for 309 and 310.
		// So here we want to try both ways: First trying to calculate volume by adding the results for individual channels,
		// if they exist, then second, checking the volume hashtable to see if there is a key matching 309_310 or 309-310.
		// The value we use will be the maximum of the results of the two approaches.
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String[] chanGroupsResultsLines = null;
		String na = "N/A";
		if(this.chanGroupsHashtable !=null && this.chanGroupsHashtable.size()>0) {
			chanGroupsResultsLines = new String[this.chanGroupsHashtable.size()];
			for(int i=0; i<this.chanGroupNamesInOrderVector.size(); i++) {
				String chanGroupName = this.chanGroupNamesInOrderVector.get(i);
				String chanGroup = this.chanGroupsHashtable.get(chanGroupName);
				chanGroupsResultsLines[i] = chanGroupName.trim();
//				Vector<String> allChanInGroup = CsdpFunctions.parseChanGroupString(this.csdpFrame, chanGroup);
				//channels groups have no values for channels.inp length, csdpChannelLength, percentLengthChange, csdpAverageWidth
				chanGroupsResultsLines[i] += "\t"+na+"\t"+na+"\t"+na+"\t"+na;
				
				chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.csdpVolumeChanGroupsHashtable.get(chanGroupName));
				chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.csdpWetAreaChanGroupsHashtable.get(chanGroupName));
				chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.csdpSurfaceAreaChanGroupsHashtable.get(chanGroupName));
				chanGroupsResultsLines[i] += "\t"+na; //CSDP Max Area Ratio
				if(this.dsm2HofFileSpecified) {
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.dsm2VolumeChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.dsm2WetAreaChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.dsm2SurfAreaChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+na; //DSM2 Max Area Ratio
				}
				if(this.gisVolumeFilesSpecified) {
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.gis2mVolCuFtChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.gis2mSurfAreaSqFtChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.gis10mVolCuFtChanGroupsHashtable.get(chanGroupName));
					chanGroupsResultsLines[i] += "\t"+String.format("%.1f", this.gis10mSurfAreaSqFtChanGroupsHashtable.get(chanGroupName));
				}

				String dsm2Minus2mVolString = "";
				String dsm2Minus10mVolString = "";
				String twoMeterVolPercentDiffString = "";
				String tenMeterVolPercentDiffString = "";
				String csdpAvgWidthString = ""; // N/A for channel groups
				String twoMeterWidthRatioString = ""; // N/A for channel groups
				String tenMeterWidthRatioString = ""; // N/A for channel groups
				String twoMeterValidityString = "";
				String tenMeterValidityString = "";
				String validTwoMeterVolString = "";
				String validTenMeterVolString = "";
				String dsm2MinusValid2mVolString = "";
				String dsm2MinusValid10mVolString = "";
				String validTwoMeterVolPercentDiffString = "";
				String validTenMeterVolPercentDiffString = "";
				String validCombinedTwoTenMeterVolPercentDiffString = "";

				if(this.twoMeterValiditySpecified) {
					twoMeterVolPercentDiffString = String.format("%.1f", this.dsm2PercentVolumeDifferenceVsTwoMeterChanGroupsHashtable.get(chanGroupName));
					boolean twoMeterDEMValidity = this.twoMeterDEMValidityChanGroupsHashtable.get(chanGroupName);
					twoMeterValidityString = Boolean.toString(twoMeterDEMValidity);
					if(twoMeterDEMValidity) {
						validTwoMeterVolString = String.format("%.1f", this.gisValid2mVolumeChanGroupsHashtable.get(chanGroupName));
						dsm2MinusValid2mVolString = String.format("%.1f", this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable.get(chanGroupName));
						validTwoMeterVolPercentDiffString = String.format("%.1f", this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.get(chanGroupName));
					}
					dsm2Minus10mVolString = String.format("%.1f", this.dsm2VolumeMinusGISTenMeterVolumeChanGroupsHashtable.get(chanGroupName));
					tenMeterVolPercentDiffString = String.format("%.1f", this.dsm2PercentVolumeDifferenceVsTenMeterChanGroupsHashtable.get(chanGroupName));
					boolean tenMeterDEMValidity = this.tenMeterDEMValidityChanGroupsHashtable.get(chanGroupName);
					tenMeterValidityString = Boolean.toString(this.tenMeterDEMValidityChanGroupsHashtable.get(chanGroupName));
					if(tenMeterDEMValidity) {
						validTenMeterVolString = String.format("%.1f", this.gisValid10mVolumeChanGroupsHashtable.get(chanGroupName));
						dsm2MinusValid10mVolString = String.format("%.1f", this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable.get(chanGroupName));
						validTenMeterVolPercentDiffString = String.format("%.1f", this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.get(chanGroupName));
					}
					if(this.dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterChanGroupsHashtable.containsKey(chanGroupName)) {
						double validCombined = this.dsm2ValidCombinedPercentVolumeDiffVsTwoAndTenMeterChanGroupsHashtable.get(chanGroupName);
						validCombinedTwoTenMeterVolPercentDiffString = String.format("%.1f", validCombined);
					}
					chanGroupsResultsLines[i] += 
							"\t"+dsm2Minus2mVolString+
							"\t"+dsm2Minus10mVolString+
							"\t"+twoMeterVolPercentDiffString+
							"\t"+tenMeterVolPercentDiffString+
							"\t"+csdpAvgWidthString+
							"\t"+twoMeterWidthRatioString+
							"\t"+tenMeterWidthRatioString+
							"\t"+twoMeterValidityString+
							"\t"+tenMeterValidityString+
							"\t"+validTwoMeterVolString+
							"\t"+validTenMeterVolString+
							"\t"+dsm2MinusValid2mVolString+
							"\t"+dsm2MinusValid10mVolString+
							"\t"+validTwoMeterVolPercentDiffString+
							"\t"+validTenMeterVolPercentDiffString+
							"\t"+validCombinedTwoTenMeterVolPercentDiffString;
				}//if two meter validity specified
				//
				chanGroupsResultsLines[i] += "\t"+na+"\t"+na+"\t"+na+"\t"+na+"\t"+na+"\t"+na+"\t"+na+"\t"+na;
				if(this.histMinStageFileSpecified) {
					chanGroupsResultsLines[i]+="\t"+na+"\t"+na+"\t"+na;
				}
			}//for each chanGroup
		}//if there are any chanGroup strings
		
		AsciiFileWriter asciiFileWriter = new AsciiFileWriter(this.csdpFrame, this.outputFilePath);
		for(int i=0; i<reportText.size(); i++) {
			String line = reportText.get(i);
			asciiFileWriter.writeLine(line);
		}
		if(chanGroupsResultsLines!=null && chanGroupsResultsLines.length>0) {
			for(int i=0; i<chanGroupsResultsLines.length; i++) {
				asciiFileWriter.writeLine(chanGroupsResultsLines[i]);
			}
		}
		asciiFileWriter.close();
		JOptionPane.showMessageDialog(this.csdpFrame, "Done writing Network Summary report", "Done", JOptionPane.INFORMATION_MESSAGE);
	}//writeResults

	//these methods are all for the GISSummaryGraphWindow class
	public Vector<String> getChannelsVector(){return this.chanVector;}
	public Hashtable<String, String> getChannelGroupsHashtable(){return this.chanGroupsHashtable;}
	public Hashtable<String, Double> getDsm2VolumeMinusGISValidTwoMeterVolumeHashtable(){
		return this.dsm2VolumeMinusGISValidTwoMeterVolumeHashtable;
	}
	public Hashtable<String, Double> getDsm2VolumeMinusGISValidTenMeterVolumeHashtable(){
		return this.dsm2VolumeMinusGISValidTenMeterVolumeHashtable;
	}
	public Hashtable<String, Double> getDsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable(){
		return this.dsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable;
	}
	public Hashtable<String, Double> getDsm2ValidPercentVolumeDifferenceVsTenMeterHashtable(){
		return this.dsm2ValidPercentVolumeDifferenceVsTenMeterHashtable;
	}
	public Vector<String> getChanGroupNamesInOrderVector(){return this.chanGroupNamesInOrderVector;} 
	public Hashtable<String, Double> getDsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable(){
		return this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable;
	} 
	public Hashtable<String, Double> getDsm2VolumeValidPercentVolumeDiffVsTenMeterChanGroupsHashtable(){
		return this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable;
	}
	public Hashtable<String, Double> getValidTwoMeterVolumeHashtable() {
		return this.gisValid2mVolumeHashtable;
	} 
	public Hashtable<String, Double> getValidTenMeterVolumeHashtable() {
		return this.gisValid10mVolumeHashtable;
	} 
	public Hashtable<String, Double> getDsm2VolumeHashtable() {
		return this.dsm2VolumeHashtable;
	}
	public Hashtable<String, Double> getDsm2VolumeChanGroupsHashtable(){return this.dsm2VolumeChanGroupsHashtable;}
	public Hashtable<String, Double> getValidTenMeterVolumeChanGroupsHashtable(){return this.gisValid10mVolumeChanGroupsHashtable;}

	public Hashtable<String, Double> getDsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable() {
		return this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable;
	}
	public Hashtable<String, Double> getDsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable() {
		return this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable;
	}

	public Hashtable<String, Double> getValidTwoMeterVolumeChanGroupsHashtable() {
		return this.gisValid2mVolumeChanGroupsHashtable;
	}
}//class NetworkSummary
