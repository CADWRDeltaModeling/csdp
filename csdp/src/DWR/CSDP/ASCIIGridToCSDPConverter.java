package DWR.CSDP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

/**
 * Convers an ASCII grid DEM file to CSDP prn file :-
 * The ASCII grid should be in NAD83 with units of meter.
 * 
 * <pre>
 *   ;HorizontalDatum: UTMNAD83
 *   ;HorizontalZone: 10
 *   ;HorizontalUnits: Meters 
 *   ;VerticalDatum: NAVD88
 *   ;VerticalUnits: USSurveyFeet 
 *   ;Filetype: bathmetry 
 *   ;NumElements: 3392903
 *   599225.70000,4209893.31000,-14.63,1950,NOAA
 *   ...
 * </pre>
 * 
 * @author psandhu
 * 
 */
public class ASCIIGridToCSDPConverter {

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Usage: ASCIIGridDEMSplitter <DEM file> <CSDP PRN File> <Year>");
			System.out.println("Alternate Usage: ASCIIGridDEMSplitter <DEM file> <CSDP PRN File> <Year> <Source>");
			System.out.println("Alternate Usage: ASCIIGridDEMSplitter <DEM file> <CSDP PRN File> <Year> <Source> <Description>");
			System.exit(2);
		}
		String year = args[2];
		
		String source = "RFW-DEM";
		if(args.length>3) {
			source = args[3];
		}
		String description = "";
		if (args.length>4) {
			description = args[4];
		}
		
		ASCIIGridToCSDPConverter converter = new ASCIIGridToCSDPConverter(null, args[0], args[1], year, source, description, 1);
		converter.convert();
	}

	private String inFilename;
	private String outFilename;
	/*
	 * Examples: "RFW-DEM", "DWR-DMS", "USGS", etc.
	 */
	private String source;
	/*
	 * The description field is currently not used by CSDP, but it can be helpful when managing data to determine which data sets
	 * to include in CSDP input files.
	 */
	private String description;
	private String year;
	/*
	 * Example: 4 would reduce the number of points by a factor of 4. Only write every 4th point.
	 */
	private int pointReductionFactor;
	private CsdpFrame csdpFrame;

	public ASCIIGridToCSDPConverter(CsdpFrame csdpFrame, String inFileName, String outFileName, String year, String source, String description, int pointReductionFactor) {
		this.csdpFrame = csdpFrame;
		this.inFilename = inFileName;
		this.outFilename = outFileName;
		this.year = year;
		this.source = source;
		this.description = description;
		this.pointReductionFactor = pointReductionFactor;
	}
	
	public boolean convert() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(this.inFilename));
		File tempFile = File.createTempFile("csdp", "prn");
		PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
		String[] headers = new String[] { ";HorizontalDatum:  UTMNAD83",
				";HorizontalZone:   10", ";HorizontalUnits:  Meters",
				";VerticalDatum:    NAVD88", ";VerticalUnits:    USSurveyFeet",
				";Filetype: bathmetry" };
		for (String h : headers) {
			writer.println(h);
		}
		String line = reader.readLine();
		int count = 0;
		int ncols = 0;
		int nrows = 0;
		double xllcorner = 0;
		double yllcorner = 0;
		double cellsize = 0;
		int nodataValue = 0;
		while (count < 6) {
			String[] fields = line.split("\\s+");
			if (fields.length != 2) {
				continue;
			}
			if (fields[0].equalsIgnoreCase("ncols")) {
				ncols = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nrows")) {
				nrows = Integer.parseInt(fields[1]);
			} else if (fields[0].equalsIgnoreCase("xllcorner")) {
				xllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("yllcorner")) {
				yllcorner = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("cellsize")) {
				cellsize = Double.parseDouble(fields[1]);
			} else if (fields[0].equalsIgnoreCase("nodata_value")) {
				nodataValue = Integer.parseInt(fields[1]);
			}
			line = reader.readLine();
			count++;
		}
		int numberOfValues = nrows * ncols;
		writer.println(";NumElements: " + numberOfValues);
		int pct = nrows / 100;
		int realCount=0;
		//this will be different from realCount if pointReductionFactor is not 1.
		int numValuesToWrite = 0;
		for (int i = 0; i < nrows; i++) {
			String[] fields = line.split("\\s");
//			if ((i % pct) == 0) {
//				System.out.println("Processed " + i + " of " + nrows
//						+ " rows from file " + inFilename);
//			}
			for (int j = 0; j < fields.length; j++) {
				double x = xllcorner + (j * cellsize);
				double y = yllcorner + ((nrows - i) * cellsize);
				double depth = 0;
				// int rawDepth = Integer.parseInt(fields[j]);
				// ASCII Grid values in meters in float format while output is
				// 10ths of feet in integer format
//				float rawDepth = Float.parseFloat(fields[j]);
				float rawDepth = -Float.MAX_VALUE;
				String rawDepthString = fields[j];
				if (rawDepthString.equals("-nan(ind)")){
					rawDepth = nodataValue;
				}else{
					rawDepth = Float.parseFloat(rawDepthString);
				}

				if (Math.abs(rawDepth - nodataValue) <= 1e-5) {
					depth = -9999;
				} else {
					depth = rawDepth / 0.3048;
					realCount++;
					if(realCount % this.pointReductionFactor == 0) {
						numValuesToWrite++;
						writer.println(String.format("%12.5f,%12.5f,%4.2f,%s,%s,%s", x, y,
								depth, this.year, this.source, this.description));
					}
				}
			}
			line = reader.readLine();
		}
		writer.close();
		reader.close();
		//
		System.out.println("Actual number of points in data: "+realCount);
		JOptionPane.showMessageDialog(csdpFrame, "Change numElements in the .prn file to "+numValuesToWrite, "Edit the file!", JOptionPane.OK_OPTION);
		// rewrite with realcount
		reader = new BufferedReader(new FileReader(tempFile));
		writer = new PrintWriter(new FileWriter(this.outFilename));
		 line = reader.readLine();
		while (line!=null){
			if (line.startsWith(";NumElements:")){
				line=";NumElements: "+realCount;
			}
			writer.println(line);
			line=reader.readLine();
		}
		reader.close();
		writer.close();
		tempFile.delete();

		return true;
	}

}
