package DWR.CSDP;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import DWR.CSDP.dialog.MessageDialog;

/**
 * Read the DSM2 output ASCII file (.hof), and calculate volume, wetted area, and surface area for each channel
 * at the elevation specified in the Display-Parameters menu
 * Hydro must be run with printlevel >= 5 to get virtual cross-section output.
 * @author btom
 *
 */
public class DSM2VirtualCrossSectionVolume{
	private static final int CHANNEL_GEOMETRY_INFORMATION = 10;
	private static final int INT_EXT = 20;
	private static final int VIRTUAL_CROSS_SECTION = 30;
	
	public static final int VOLUME_RESULTS = 100;
	public static final int WETTED_AREA_RESULTS = 110;
	public static final int SURFACE_AREA_RESULTS = 120;
	public static final int MAX_AREA_RATIO_RESULTS = 130;
	private static final boolean DEBUG=false;
	
	//key is chanNum
	Hashtable<String, DSM2Chan> extToDSM2ChanHashtable = new Hashtable<String, DSM2Chan>();
	Vector<String> externalChannelNumbers = new Vector<String>();

	int _filechooserState = -Integer.MAX_VALUE;
	String _directory = null;
	String _filename = null;
	
	public static void main(String[] args) {
		DSM2VirtualCrossSectionVolume c = new DSM2VirtualCrossSectionVolume("E:/delta/dsm2_v8.2beta/studies/historical/output/", "historical_v82.hof");
		c.printResults();
	}
	
	public DSM2VirtualCrossSectionVolume(String directory, String filename) {
		_directory = directory;
		_filename = filename;
		readFileAndCalculateChannelConveyanceCharacteristics();
	}
	
	public Hashtable<String, Double> getResults(int resultsType){
		Hashtable<String, Double> returnHashtable = new Hashtable<String, Double>(); 
		for(int i=0; i<externalChannelNumbers.size(); i++) {
			String chan = externalChannelNumbers.get(i);
			DSM2Chan dsm2Chan = extToDSM2ChanHashtable.get(chan);
//			System.out.println("Chan, zero elevation volume="+chan+","+dsm2Chan.getZeroVolume());
			if(resultsType==VOLUME_RESULTS) {
				returnHashtable.put(chan, dsm2Chan.getVolumeAtSpecifiedElev());
			}else if(resultsType==WETTED_AREA_RESULTS) {
				returnHashtable.put(chan, dsm2Chan.getWettedAreaAtSpecifiedElev());
			}else if(resultsType==SURFACE_AREA_RESULTS) {
				returnHashtable.put(chan, dsm2Chan.getSurfaceAreaAtSpecifiedElev());
			}else if(resultsType==MAX_AREA_RATIO_RESULTS) {
				returnHashtable.put(chan, dsm2Chan.getMaxAreaRatioAtSpecifiedElev());
			}else {
				JOptionPane.showMessageDialog(null, "Error in "+this.getClass().getName()+
						".getResults: unrecognized type requested", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return returnHashtable;
	}
	
	/*
	 * Read the DSM2 .hof file and calculate volume, wetted area, and surface area.
	 */
	private void readFileAndCalculateChannelConveyanceCharacteristics() {
		AsciiFileReader afr = new AsciiFileReader(_directory+File.separator+_filename);
		double calculationElev = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
		int i=0;
		int lastHeaderFound = -Integer.MAX_VALUE; 
		int lastDataSectionRead = -Integer.MAX_VALUE;
		Hashtable<String, String> intToExtHashtable = new Hashtable<String, String>();
		Hashtable<String, String> intToLengthHashtable = new Hashtable<String, String>();
		double elev= -Double.MAX_VALUE;
		double lastElev = Double.MAX_VALUE;
		double area = -Double.MAX_VALUE;
		double lastArea = Double.MAX_VALUE;
		double width = -Double.MAX_VALUE;
		double lastWidth = -Double.MAX_VALUE;
		double wetP = -Double.MAX_VALUE;
		double lastWetP = -Double.MAX_VALUE;
		
		int currentVsec=-Integer.MAX_VALUE;
		double chanLength=-Double.MAX_VALUE;
		double lastChanLength = -Double.MAX_VALUE;
		double[] areaValues = null;
		double[] lastAreaValues = null;
		double[] widthValues = null;
		double[] lastWidthValues = null;
		double[] wetPValues = null;
		double[] lastWetPValues = null;
		int lastNumComputationalPoints = 0;
		String lastChannel = "1";
		boolean foundLastXSValues = false;
		
		String currentChan="";

		int numComputationalPoints = -Integer.MAX_VALUE;
		while(true){
			String line = afr.getNextLine();
			if(line==null || line.contains("Warning:")) break;
			
			if(line.contains("CHANNEL GEOMETRY INFORMATION")) {
				lastHeaderFound = CHANNEL_GEOMETRY_INFORMATION;
			}else if(line.contains("INT  EXT")) {
				lastHeaderFound = INT_EXT;
			}else if(line.contains("VIRTUAL CROSS-SECTION LOOKUP TABLE")) {
				lastHeaderFound = VIRTUAL_CROSS_SECTION;
			}else if(lastHeaderFound==CHANNEL_GEOMETRY_INFORMATION && lastDataSectionRead<0) {
				//do nothing
			}else if(lastHeaderFound==INT_EXT && lastDataSectionRead<0) {
				String[] parts = line.trim().split("\\s+");
				if(parts.length<=0) {
					lastDataSectionRead=INT_EXT;
				}else {
					if(line.length()==0) {
						//done
						lastDataSectionRead = INT_EXT;
					}else if(!line.contains("---")) {
//						parse int and ext..
						intToExtHashtable.put(parts[0], parts[1]);
						intToLengthHashtable.put(parts[0], parts[2]);
					}
				}
			}else if(lastHeaderFound==VIRTUAL_CROSS_SECTION && lastDataSectionRead==INT_EXT) {
				String[] parts = line.trim().split(",|\\s+");
				int numXS = 0;
				if(parts[0].equals("Warning:")) {
					lastDataSectionRead=VIRTUAL_CROSS_SECTION;
				}else {
					if(parts[0].equals("Channel")) {
						//sometimes the areas are all zero because the bottoms are above elevation
						currentChan = parts[1];
						currentVsec = Integer.parseInt(parts[4]);
						if(DEBUG) System.out.println("currentChan, currentVec="+currentChan+","+currentVsec);
						chanLength = Double.parseDouble(intToLengthHashtable.get(currentChan));
						numComputationalPoints = getNumComputationalPoints(chanLength);
						if(currentVsec==1) {
							if(!externalChannelNumbers.contains(intToExtHashtable.get(lastChannel))) {
								String extChanNum = intToExtHashtable.get(lastChannel);
								if(lastAreaValues==null) {
									lastAreaValues = new double[lastNumComputationalPoints];
									lastWidthValues = new double[lastNumComputationalPoints];
									lastWetPValues = new double[lastNumComputationalPoints];
								}
								if(DEBUG) System.out.println("Adding results for last chan="+extChanNum);
								DSM2Chan dsm2Chan = new  DSM2Chan(lastChanLength, lastNumComputationalPoints, lastAreaValues, lastWidthValues, lastWetPValues);
								extToDSM2ChanHashtable.put(extChanNum, dsm2Chan); 
								externalChannelNumbers.addElement(extChanNum);
							}
							areaValues = new double[numComputationalPoints];
							widthValues = new double[numComputationalPoints];
							wetPValues = new double[numComputationalPoints];
						}
					}else if(!parts[0].equals("Height") && !line.contains("---")) {
						elev = Double.parseDouble(parts[0]);
						width = Double.parseDouble(parts[1]);
						area = Double.parseDouble(parts[2]);
						wetP = Double.parseDouble(parts[3]);
						if(lastElev<Double.MAX_VALUE) {
							if(lastElev<=calculationElev && elev>=calculationElev) {
								double widthAtElev = (calculationElev-lastElev)*((width-lastWidth)/(elev-lastElev))+lastWidth;
								double wetPAtElev = (calculationElev-lastElev)*((wetP-lastWetP)/(elev-lastElev))+lastWetP;
								double areaAtElev = lastArea + 0.5*(widthAtElev+lastWidth)*(calculationElev-lastElev);
								areaValues[currentVsec-1] = areaAtElev;
								widthValues[currentVsec-1] = widthAtElev;
								wetPValues[currentVsec-1] = wetPAtElev;
								if(currentVsec==numComputationalPoints) {
									lastAreaValues = areaValues.clone();
									lastWidthValues = widthValues.clone();
									lastWetPValues = wetPValues.clone();
								}
							}
						}
					}
				}
				lastElev = elev;
				lastWidth = width;
				lastArea = area;
				lastWetP = wetP;
				lastChannel = currentChan;
				lastChanLength = chanLength;
				lastNumComputationalPoints = numComputationalPoints;
			}
		    i++;
		}
		//last channel results
		String extChanNum = intToExtHashtable.get(currentChan);
		System.out.println("Adding results for last chan="+extChanNum);
		if(areaValues==null) {
			//only if channel has no results--bottom elevations of all cross-sections are above the specified elevation
			areaValues = new double[getNumComputationalPoints(chanLength)];
			widthValues = new double[getNumComputationalPoints(chanLength)];
			wetPValues = new double[getNumComputationalPoints(chanLength)];
		}
		DSM2Chan dsm2Chan = new  DSM2Chan(lastChanLength, numComputationalPoints, areaValues, widthValues, wetPValues);
		extToDSM2ChanHashtable.put(extChanNum, dsm2Chan); 
		externalChannelNumbers.addElement(extChanNum);

		afr.close();

	}
	
	public Vector<String> getExternalChannelNumbers(){
		return externalChannelNumbers;
	}

	private void printResults() {
		String message = "DSM2 output file used: "+_directory+File.separator+_filename+"\n\n";
		message += "External Chan, Volume (ft3)\n";
		for(int i=0; i<externalChannelNumbers.size(); i++) {
			String chan = externalChannelNumbers.get(i);
			DSM2Chan dsm2Chan = extToDSM2ChanHashtable.get(chan);
//			System.out.println("Chan, zero elevation volume="+chan+","+dsm2Chan.getZeroVolume());
			message += chan+","+String.format("%.2f", dsm2Chan.getVolumeAtSpecifiedElev())+"\n";
		}
		MessageDialog mDialog = new MessageDialog(null, "Channel Volumes calculated at elevation="+CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS+" using DSM2 Virtual Cross-Sections", 
				message, false, false, 100, externalChannelNumbers.size()+3);
		mDialog.setVisible(true);
	}//printResults
	
	public int getNumComputationalPoints(double length) {
		return 3 + 2*(int)(Math.max(0.0, length-CsdpFunctions.DELTAX)/CsdpFunctions.DELTAX);
	}//getNumComputationalPoints
	
	/**
	 * Calculates virtual cross-section volume
	 * @author btom
	 *
	 */
	private class DSM2Chan{
		private double length;
		private double[] areaAtSpecifiedElev;
		private int numComputationalPoints;
		private double[] widthAtSpecifiedElev;
		private double[] wetPAtSpecifiedElev;

		public DSM2Chan(double length, int numComputationalPoints, double[] areaAtSpecifiedElev, double[] widthAtSpecifiedElev,
				double[] wetPAtSpecifiedElev) {
			this.length = length;
			this.numComputationalPoints = numComputationalPoints;
			this.areaAtSpecifiedElev = areaAtSpecifiedElev;
			this.widthAtSpecifiedElev = widthAtSpecifiedElev;
			this.wetPAtSpecifiedElev = wetPAtSpecifiedElev;
		}
		
		public double getVolumeAtSpecifiedElev(){
			double distBetweenComputationalPoints = length/((double)numComputationalPoints-1.0);
			double volume = 0.0;
			for(int i=1; i<areaAtSpecifiedElev.length; i++) {
				double lastArea = areaAtSpecifiedElev[i-1];
				double area = areaAtSpecifiedElev[i];
				volume += 0.5 * (lastArea+area) * distBetweenComputationalPoints;
			}
			return volume;
		}
		public double getWettedAreaAtSpecifiedElev(){
			double distBetweenComputationalPoints = length/((double)numComputationalPoints-1.0);
			double wettedArea = 0.0;
			for(int i=1; i<areaAtSpecifiedElev.length; i++) {
				double lastWetP = wetPAtSpecifiedElev[i-1];
				double wetP = wetPAtSpecifiedElev[i];
				wettedArea += 0.5 * (lastWetP+wetP) * distBetweenComputationalPoints;
			}
			return wettedArea;
		}
		public double getSurfaceAreaAtSpecifiedElev(){
			double distBetweenComputationalPoints = length/((double)numComputationalPoints-1.0);
			double surfaceArea = 0.0;
			for(int i=1; i<areaAtSpecifiedElev.length; i++) {
				double lastWidth = widthAtSpecifiedElev[i-1];
				double width = widthAtSpecifiedElev[i];
				surfaceArea += 0.5 * (lastWidth+width) * distBetweenComputationalPoints;
			}
			return surfaceArea;
		}

		public double getMaxAreaRatioAtSpecifiedElev() {
			double maxArea = -Double.MAX_VALUE;
			double minArea = Double.MAX_VALUE;
			for(int i=1; i<areaAtSpecifiedElev.length; i++) {
				double area = areaAtSpecifiedElev[i];
				if(area>maxArea) maxArea = area;
				if(area<minArea) minArea = area;
			}
			return maxArea/minArea;
		}
	}//inner class DSM2Chan
	
}//class CalculateDSM2VirtualCrossSectionVolume
