package DWR.CSDP;

import java.io.File;
import java.util.Hashtable;

public class HistMinStageResults {
	Hashtable<String, Double> resultsHashtable = new Hashtable<String, Double>();
	
	public HistMinStageResults(File directory, String filename) {
		AsciiFileReader asciiFileReader = new AsciiFileReader(directory.toString()+File.separator+filename);
		while(true) {
			String line = asciiFileReader.getNextLine();
			if(line==null) break;
			String[] parts = line.split(",");
			String chan = parts[0];
			String elev = parts[1];
			resultsHashtable.put(chan, new Double(elev));
		}
	}

	public Hashtable<String, Double> getMinStageHashtable() {
		return resultsHashtable;
	}
	
}
