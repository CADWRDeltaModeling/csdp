package DWR.CSDP;

import java.io.File;
import java.util.Hashtable;

import javax.swing.JOptionPane;

/**
 * Reads 2m DEM Validity hashtable, containing 3 columns:
 * Column 0: channel or channel group name
 * Column 1: True if 2m DEM coverage for channel polygon is complete or very close to complete. This was determined visually using ArcGIS.
 * Column 2: Any notes regarding validity/data coverage.
 * @author btom
 *
 */
public class TwoMeterDEMValidity {
	private Hashtable<String, Boolean> validityHashtable = new Hashtable<String, Boolean>();
	private Hashtable<String, String>notesHashtable = new Hashtable<String, String> ();
	
	public TwoMeterDEMValidity(File directory, String filename) {
		AsciiFileReader asciiFileReader = new AsciiFileReader(directory.toString()+File.separator+filename);
		boolean foundHeaderLine = false;
		while(true) {
			String line = asciiFileReader.getNextLine();
			if(line==null) break;
			if(foundHeaderLine==false && (line.indexOf("Polygon Name")>=0 || line.indexOf("Valid 2m")>=0 || line.indexOf("Notes")>=0)) {
				foundHeaderLine = true;
			}
			if(foundHeaderLine) {
				String[] parts = line.split(",");
				if(parts.length>=2) {
					String chan = parts[0];
					String validityString = parts[1];
					String notesString = "";
					if(parts.length>=3) {
						notesString = parts[2];
					}
					try {
						boolean v = new Boolean(validityString);
						this.validityHashtable.put(chan, v);
						this.notesHashtable.put(chan, notesString);
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null, "Error parsing 2m Validity file: unable to parse "+validityString+" as boolean", 
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		asciiFileReader.close();
	}//constructor

	public Hashtable<String, Boolean> getValidityHashtable() {
		return this.validityHashtable;
	}

	public Hashtable<String, String> getNotesHashtable() {
		return this.notesHashtable;
	}
	
}//class TwoMeterDEMValidity
