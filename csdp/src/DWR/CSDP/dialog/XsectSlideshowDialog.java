package DWR.CSDP.dialog;

import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import DWR.CSDP.App;
import DWR.CSDP.BathymetryData;
import DWR.CSDP.CsdpFrame;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.Network;
import DWR.CSDP.Xsect;
import DWR.CSDP.XsectBathymetryData;
import DWR.CSDP.XsectGraph;
import vista.graph.GECanvas;

public class XsectSlideshowDialog extends JDialog {
	public XsectSlideshowDialog(CsdpFrame gui, App app, BathymetryData bathymetryData,  
			Network network0, Network network1, 
			String networkDirectory0, String networkFilename0, String networkDirectory1, String networkFilename1, 
			String centerlineName0, int xsectIndex0, Xsect xsect0, double dist0, 
			String centerlineName1, int xsectIndex1, Xsect xsect1, double dist1) {
		setTitle("Cross-Section Slideshow window for ...");
		setLayout(new GridLayout(0,2));
		Hashtable xsectDisplayData0 = network0.findXsectDisplayRegion(centerlineName0, xsectIndex0, CsdpFunctions.getXsectThickness());
		XsectBathymetryData xsectBathymetryData0 = bathymetryData.findXsectData(xsectDisplayData0);

		XsectGraph xsectGraph0 = new XsectGraph(gui, app, bathymetryData, xsectBathymetryData0, network0, 
				centerlineName0, xsectIndex0, CsdpFunctions.getXsectThickness(), 0);
		JLabel xsectLabel = new JLabel();
		GECanvas geCanvas0 = xsectGraph0.getGC();
		JTextPane conveyanceCharacteristicsJTextPane0 = xsectGraph0.getConveyanceCharacteristicsJPanel();

		if(centerlineName1!=null && xsect1!=null) {
			Hashtable xsectDisplayData1 = network1.findXsectDisplayRegion(centerlineName1, xsectIndex1, CsdpFunctions.getXsectThickness());
			XsectBathymetryData xsectBathymetryData1 = bathymetryData.findXsectData(xsectDisplayData1);
			XsectGraph xsectGraph1 = new XsectGraph(gui, app, bathymetryData, xsectBathymetryData1, network1, 
					centerlineName0, xsectIndex1, CsdpFunctions.getXsectThickness(), 0);
			GECanvas geCanvas1 = xsectGraph1.getGC();
			JTextPane conveyanceCharacteristicsJTextPane1 = xsectGraph1.getConveyanceCharacteristicsJPanel();
		}
		
	}
}
