package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import DWR.CSDP.Centerline;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.Network;
import DWR.CSDP.Xsect;

public class CenterlineSummaryWindow extends JFrame {
	private Network network;
	private String chanNum;

	public CenterlineSummaryWindow(Network network) {
		super("Summary Data for Channel "+network.getSelectedCenterlineName());
		this.chanNum = network.getSelectedCenterlineName();
		this.network = network;
		addContent();
	}
	
	private void addContent(){
    	String xLabel = "Distance from Upstream End, ft";
    	String titleArea = "Area Profile for Channel " + this.chanNum;
    	String titleWetP = "Wetted Perimeter Profile for Channel "+this.chanNum;
    	String titleWidth = "Width Profile for Channel "+this.chanNum;
    	String yLabelArea = "Area, ft2";
    	String yLabelWetP = "WetP, ft2";
    	String yLabelWidth = "Width, ft2";
		
		//			Xsect xsect = _net.getSelectedXsect();
		String centerlineName = this.network.getSelectedCenterlineName();
//		int xsectNum = _net.getSelectedXsectNum();
		Centerline centerline = this.network.getSelectedCenterline();
		int numXsects = centerline.getNumXsects();
    	XYSeries areaSeries = new XYSeries("Area");
    	XYSeries wetPSeries = new XYSeries("Wetted Perimeter");
    	XYSeries widthSeries = new XYSeries("Width");
		for(int i=0; i<numXsects; i++){
			Xsect currentXsect = centerline.getXsect(i);
			double distanceAlong = currentXsect.getDistAlongCenterlineFeet();
			double e = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
			double area = currentXsect.getAreaSqft(e);
			double wetP = currentXsect.getWettedPerimeterFeet(e);
			double width = currentXsect.getWettedPerimeterFeet(e);
			areaSeries.add(area, distanceAlong);
			wetPSeries.add(wetP, distanceAlong);
			widthSeries.add(width, distanceAlong);
		}

        XYDataset areaDataset = new XYSeriesCollection(areaSeries);
        XYDataset wetPDataset = new XYSeriesCollection(wetPSeries);
        XYDataset widthDataset = new XYSeriesCollection(widthSeries);
        JFreeChart areaChart = createChart(titleArea, xLabel, yLabelArea, areaDataset);
        JFreeChart wetPChart = createChart(titleWetP,xLabel, yLabelWetP, wetPDataset);
        JFreeChart widthChart = createChart(titleWidth, xLabel, yLabelWidth, widthDataset);
    	
    	  	
        ChartPanel areaChartPanel = new ChartPanel(areaChart);
        ChartPanel wetPChartPanel = new ChartPanel(wetPChart);
        ChartPanel widthChartPanel = new ChartPanel(widthChart);
		setSize(800, 600);
		
		JPanel chartsPanel = new JPanel(new GridLayout(0,1));
		chartsPanel.add(areaChartPanel);
		chartsPanel.add(wetPChartPanel);
		chartsPanel.add(widthChartPanel);

		JPanel conveyanceCharacteristicsPanel = new JPanel(new GridLayout(0,2,2,2));
		conveyanceCharacteristicsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		double elevation = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
		JTextArea centerlineOrReachSummaryLabel = new JTextArea("Centerline or Reach Summary\n\n"
				+ "Volume, Wetted Area, and Surface area are estimates\n"
				+ "assuming no intepolation from adjacent channels,\n"
				+ "an elevation of " + elevation + " in the current datum, and \n"
				+ "linear variation between cross-sections.\n");
		centerlineOrReachSummaryLabel.setBackground(Color.LIGHT_GRAY);
		JLabel elevationLabel = new JLabel("Elevation, ft");
		JLabel centerlineLengthLabel = new JLabel("Centerline Length, ft");
		JLabel channelVolumeLabel = new JLabel("Channel Volume, ft3");
		JLabel channelWettedAreaLabel = new JLabel("Channel Wetted Area, ft2");
		JLabel channelSurfaceAreaLabel = new JLabel("Channel Surface Area, ft2");
//		centerlineLengthLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		channelVolumeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		channelWettedAreaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		channelSurfaceAreaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		double length = centerline.getLengthFeet();
		double volume = centerline.getChannelVolumeEstimateNoInterp(0.0);
		double wettedArea = centerline.getChannelWettedAreaEstimateNoInterp(0.0);
		double surfaceArea = centerline.getChannelSurfaceAreaEstimateNoInterp(0.0);
		
		JLabel elevValueLabel = new JLabel(String.format("%,.1f", elevation), SwingConstants.RIGHT);
		JLabel cLengthValueLabel = new JLabel(String.format("%,.1f", length), SwingConstants.RIGHT);
		JLabel volValueLabel = new JLabel(String.format("%,.1f", volume), SwingConstants.RIGHT);
		JLabel wetAreaValueLabel = new JLabel(String.format("%,.1f", wettedArea), SwingConstants.RIGHT);
		JLabel surfAreaValueLabel = new JLabel(String.format("%,.1f",surfaceArea), SwingConstants.RIGHT);
//		cLengthLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		volLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		wetAreaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		surfAreaLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		conveyanceCharacteristicsPanel.add(elevationLabel);
		conveyanceCharacteristicsPanel.add(elevValueLabel);
		conveyanceCharacteristicsPanel.add(centerlineLengthLabel);
		conveyanceCharacteristicsPanel.add(cLengthValueLabel);
		conveyanceCharacteristicsPanel.add(channelVolumeLabel);
		conveyanceCharacteristicsPanel.add(volValueLabel);
		conveyanceCharacteristicsPanel.add(channelWettedAreaLabel);
		conveyanceCharacteristicsPanel.add(wetAreaValueLabel);
		conveyanceCharacteristicsPanel.add(channelSurfaceAreaLabel);
		conveyanceCharacteristicsPanel.add(surfAreaValueLabel);
		
		JPanel ccPanel2 = new JPanel(new GridLayout(0,1,5,5));
		ccPanel2.add(centerlineOrReachSummaryLabel);
		ccPanel2.add(conveyanceCharacteristicsPanel);
		
		JPanel centerlineInfoPanel = new JPanel(new BorderLayout());
		centerlineInfoPanel.add(ccPanel2, BorderLayout.NORTH);
		
		getContentPane().setLayout(new BorderLayout());
    	getContentPane().add(chartsPanel, BorderLayout.CENTER);
    	getContentPane().add(centerlineInfoPanel, BorderLayout.EAST);
    	setVisible(true);
	}//addContent

	private JFreeChart createChart(String title, String xLabel, String yLabel, XYDataset dataset) {
	//  return ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset);
		//The class is developed differently from the api: with this many args, it wants them in a different order.
	  return ChartFactory.createScatterPlot(title, yLabel, xLabel, dataset, PlotOrientation.HORIZONTAL, false, true, true);
	}

}//class CenterlineSumamryWindow

