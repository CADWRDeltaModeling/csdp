package DWR.CSDP;

import java.io.File;
import java.util.Hashtable;

import javax.swing.JOptionPane;

/**
 * Reads file with the following information:
 * Column 1: DSM2 channel number with "_chanpoly"
 * Column 2: Volume, m3, calculated by arcpy CutFill operation
 * Column 3: Surface area (2d area), m2, calculated by arcpy CutFill operation
 * Example data:
 * 60,160_chanpoly,461050.809173,111392.0,0.0,E:/dsm2GisReference/bay_delta_dem_v4.gdb/dem_columbia_cut_2m_20120911
 * 216,315_chanpoly,62.6759294644,156.0,0.0,E:/dsm2GisReference/bay_delta_dem_v4.gdb/dem_columbia_cut_2m_20120911
 * 217,316_chanpoly,3215.55096205,1036.0,0.0,E:/dsm2GisReference/bay_delta_dem_v4.gdb/dem_columbia_cut_2m_20120911
 * 220,319_chanpoly,2306.73289159,700.0,0.0,E:/dsm2GisReference/bay_delta_dem_v4.gdb/dem_columbia_cut_2m_20120911
 * 221,31_chanpoly,709673.208273,103684.0,0.0,E:/dsm2GisReference/bay_delta_dem_v4.gdb/dem_columbia_cut_2m_20120911
 */
public class GISCutFillResults {
	private Hashtable<String, Double> gis10mVolumeResultsCuFtHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis10mSurfAreaResultsSqFtHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis2mVolumeResultsCuFtHashtable = new Hashtable<String, Double>();
	private Hashtable<String, Double> gis2mSurfAreaResultsSqFtHashtable = new Hashtable<String, Double>();
		
	public GISCutFillResults(String[] gisCutFillPathnames) {
		for(int i=0; i<gisCutFillPathnames.length; i++) {
			String path = gisCutFillPathnames[i];
			Hashtable<String, Double> volHashtable = null;
			Hashtable<String, Double> areaHashtable = null;
			
			int lastIndexOfFileSep = path.lastIndexOf(File.separator);
			String filename = path.substring(lastIndexOfFileSep);
			if(filename.indexOf("2m")>=0 && path.indexOf("10m")>=0) {
				JOptionPane.showMessageDialog(null, "Error: pathname string cannot be identified as 2m or 10m, because it "
						+ "contains both the strings '2m' and '10m' ", "Error", JOptionPane.ERROR_MESSAGE);
			}else if(filename.indexOf("2m")>=0 || filename.indexOf("10m")<=0){
				//the yolo 2m DEM does not have "2m" in the filename.
				volHashtable = this.gis2mVolumeResultsCuFtHashtable;
				areaHashtable = this.gis2mSurfAreaResultsSqFtHashtable;
			}else if(filename.indexOf("10m")>=0) {
				volHashtable = this.gis10mVolumeResultsCuFtHashtable;
				areaHashtable = this.gis10mSurfAreaResultsSqFtHashtable;
			}else {
				JOptionPane.showMessageDialog(null, "Error: filename string cannot be identified as 2m or 10m, because it "
						+ "does not contain the strings '2m' or '10m':\n"+filename, "Error", JOptionPane.ERROR_MESSAGE);
			}
					
			AsciiFileReader asciiFileReader = new AsciiFileReader(path);
			while(true) {
				String line = asciiFileReader.getNextLine();
				if(line==null) break;
				String[] parts = line.split(",");
				String chan = parts[1].trim();
				chan = chan.replace("_chanpoly", "");
				String v = parts[2];
				String sa = parts[3];
				if(chan.indexOf("_")>0) {
					System.out.println("adding values for channel group: "+chan+","+v+","+sa);
				}
				try {
					double vCuFt = CsdpFunctions.cubicMetersToCubicFeet * Double.parseDouble(v);
					double saSqFt = CsdpFunctions.squareMetersToSquareFeet * Double.parseDouble(sa);
					if(vCuFt>=0.0) {
						if(volHashtable.containsKey(chan)) {
							vCuFt = Math.max(vCuFt, volHashtable.get(chan));
							System.out.println("duplicate volume value read for chan "+chan+". saving max value:"+vCuFt);
						}
						volHashtable.put(chan, vCuFt);
					}
					if(saSqFt>=0.0) {
						if(areaHashtable.containsKey(chan)) {
							saSqFt = Math.max(saSqFt, areaHashtable.get(chan));
							System.out.println("duplicate volume value read for chan "+chan+". saving max value:"+saSqFt);
						}
						areaHashtable.put(chan, saSqFt);
					}
				}catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Error parsing line from CutFill file", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			asciiFileReader.close();
		}//for each file
	}//constructor

	public Hashtable<String, Double> get2mVolumeCuFtHashtable() {
		return gis2mVolumeResultsCuFtHashtable;
	}

	public Hashtable<String, Double> get2mSurfAreaSqFtHashtable() {
		return gis2mSurfAreaResultsSqFtHashtable;
	}
	public Hashtable<String, Double> get10mVolumeCuFtHashtable() {
		return gis10mVolumeResultsCuFtHashtable;
	}

	public Hashtable<String, Double> get10mSurfAreaSqFtHashtable() {
		return gis10mSurfAreaResultsSqFtHashtable;
	}
	
}//class GISCutFillResults
