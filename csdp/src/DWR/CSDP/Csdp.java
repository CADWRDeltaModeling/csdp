package DWR.CSDP;

import org.apache.log4j.BasicConfigurator;
import DWR.CSDP.semmscon.UseSemmscon;

/**
 * Cross-section development Program
 *
 * @author
 * @version $Id: Csdp.java,v 1.4 2005/04/08 00:21:53 btom Exp $
 */
public class Csdp {

	/**
	 * main method for Csdp
	 */
	public static void main(String args[]) {
		//this is required for jzy3d library, used for 3d plotting of bathymetry points.
		BasicConfigurator.configure();

		try {
			App app = new App();
			if(args.length > 0) {
				readGitHashAndUpdateVersionNumber(args[0]);
			}
			// if(args.length > 0){
			// CsdpFunctions._csdpHome=args[0];
			// }else{
			// CsdpFunctions._csdpHome="";
			// }
			CsdpFrame gui = new CsdpFrame(app);
			System.out.println("Cross-Section Development Program Version " + CsdpFunctions.getVersion());
		} catch (Exception e) {
			System.out.println("Error in Csdp.main:");
			e.printStackTrace();
		}

		/**
		 * Make CsdpFileMetadata object which has default datum: horizontal: UTM
		 * Zone 10 Nad 83 meters vertical: NAVD1988 U.S. Survey Feet
		 */

		CsdpFileMetadata b = new CsdpFileMetadata();
		b.setHDatum(CsdpFileMetadata.UTMNAD83);
		b.setHZone(10);
		b.setHUnits(CsdpFileMetadata.METERS);
		b.setVDatum(CsdpFileMetadata.NAVD1988);
		b.setVUnits(CsdpFileMetadata.USSURVEYFEET);
		CsdpFunctions.setBathymetryMetadata(b);

		/**
		 * Test semmscon
		 */
		String bitness = System.getProperty("sun.arch.data.model");
		if(bitness.equals("32")) {
			CsdpFunctions.BITNESS=CsdpFunctions.BIT_32;
			UseSemmscon us = new UseSemmscon();
			try {
				us.init_convert();
			} catch (java.lang.UnsatisfiedLinkError e) {
				System.out.println("java.lang.UnsatisfiedLinkError caught. e=" + e);
			} // try/catch

			final short utm83_units = 3;
			final short utm83_zone = 10;
			final short utm27_zone = 10;
			final short utm27_units = 3;

			final double utm27x = 629355.0;
			final double utm27y = 4.199384e6;
			/**
			 * IMPORTANT: It seems that utm83ToUtm27 must be called first in order
			 * for utm28ToUtm83 to work properly
			 */

			double[] utm83 = us.utm83ToUtm27(utm27x, utm27y, utm27_zone, utm27_units, utm83_zone, utm83_units);
			utm83 = us.utm27ToUtm83(utm27x, utm27y, utm27_zone, utm27_units, utm83_zone, utm83_units);
			System.out.println("---------- Semmscon Test Results -----------");
			System.out.println("Input: utm27x, utm27y=" + utm27x + "," + utm27y);
			System.out.println("NOTE:  the following values should be ");
			System.out.println(" 629258.5972470464 and 4199579.604951745");
			System.out.println("If they are not, there may be a problem ");
			System.out.println("with the installation of the coordinate ");
			System.out.println("conversion routines");
			System.out.println("result of utm27ToUtm83:\n");
			System.out.println("utm83x, utm83y=" + utm83[0] + "," + utm83[1]);
			System.out.println("--------------------------------------------");
			CsdpFunctions.setUseSemmscon(us);
		}else if(bitness.equals("64")) {
			CsdpFunctions.BITNESS=CsdpFunctions.BIT_64;
			System.out.println("You are using the 64 bit JRE. CSDP coordinate conversion is disabled.");
			System.out.println("All input files must use the same datum/units.");
		}
	}// main

	/*
	 * Read git hash from specified file and append to version number.
	 */
	private static void readGitHashAndUpdateVersionNumber(String path) {
		try {
			AsciiFileReader asciiFileReader = new AsciiFileReader(path);
			int i = 0;
			String gitHash = "";

			while(true){
				String line = asciiFileReader.getNextLine();
				if(line==null) break;
				String parts[] = line.split("\\t+");
				gitHash = parts[0];
				i++;
			}
			CsdpFunctions.appendGitHashToVersionNumber(gitHash);
			asciiFileReader.close();
		}catch(Exception e) {
			System.out.println("Error in Csdp.readGitHashAndUpdateVersionNumber: "+e.getMessage());
			System.out.println("Git hash will not be appended to version number.");
		}
	}//readGitHash

}// class Csdp
