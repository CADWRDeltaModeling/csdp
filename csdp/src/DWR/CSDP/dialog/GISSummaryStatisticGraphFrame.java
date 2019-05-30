package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import DWR.CSDP.CsdpFrame;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.NetworkSummary;

/**
 * Uses data in NetworkSummary object to create window displaying summary statistics graphs.
 * @author btom
 *
 */
public class GISSummaryStatisticGraphFrame extends JFrame {

	private CsdpFrame csdpFrame;
	private NetworkSummary networkSummary;
	private Vector<String> individualChannelsVector;
	private Vector<String> chanGroupNamesInOrderVector; 
	private Hashtable<String, String> channelGroupsHashtable;
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTwoMeterVolumeHashtable;
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTenMeterVolumeHashtable;
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTwoMeterHashtable;
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTenMeterHashtable;
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable;
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable;
	private Hashtable<String, Double> twoMeterVolumeHashtable;
	private Hashtable<String, Double> tenMeterVolumeHashtable;
	private Hashtable<String, Double> dsm2VolumeHashtable;
	private Hashtable<String, Double> dsm2VolumeChanGroupsHashtable;
	private Hashtable<String, Double> validTenMeterVolumeChanGroupsHashtable;

	private Hashtable<String, Double> validTwoMeterVolumeChanGroupsHashtable;
	private Hashtable<String, Double> dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable;
	private Hashtable<String, Double> dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable;

	
	/*
	 * Constructor
	 * 
	 * includeDifferenceGraphs: if true, dsm2-2m DEM and dsm2-10m DEM difference plots will be included.
	 */
	public GISSummaryStatisticGraphFrame(CsdpFrame csdpFrame, NetworkSummary networkSummary, boolean includeDifferenceGraphs) {
		this.csdpFrame = csdpFrame;
		this.networkSummary = networkSummary;
		this.individualChannelsVector = networkSummary.getChannelsVector();
		this.chanGroupNamesInOrderVector = networkSummary.getChanGroupNamesInOrderVector();
		this.channelGroupsHashtable = networkSummary.getChannelGroupsHashtable();
		this.twoMeterVolumeHashtable = networkSummary.getValidTwoMeterVolumeHashtable();
		this.tenMeterVolumeHashtable = networkSummary.getValidTenMeterVolumeHashtable();
		this.dsm2VolumeHashtable = networkSummary.getDsm2VolumeHashtable();
		
		this.dsm2VolumeMinusGISValidTwoMeterVolumeHashtable  = networkSummary.getDsm2VolumeMinusGISValidTwoMeterVolumeHashtable();
		this.dsm2VolumeMinusGISValidTenMeterVolumeHashtable  = networkSummary.getDsm2VolumeMinusGISValidTenMeterVolumeHashtable();
		this.dsm2ValidPercentVolumeDiffVsTwoMeterHashtable=networkSummary.getDsm2ValidPercentVolumeDifferenceVsTwoMeterHashtable();
		this.dsm2ValidPercentVolumeDiffVsTenMeterHashtable = networkSummary.getDsm2ValidPercentVolumeDifferenceVsTenMeterHashtable();
		this.dsm2VolumeChanGroupsHashtable = networkSummary.getDsm2VolumeChanGroupsHashtable();

		this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable = networkSummary.getDsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable();
		this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable = networkSummary.getDsm2VolumeValidPercentVolumeDiffVsTenMeterChanGroupsHashtable();
		this.validTenMeterVolumeChanGroupsHashtable = networkSummary.getValidTenMeterVolumeChanGroupsHashtable();

		this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable = networkSummary.getDsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable();
		this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable = networkSummary.getDsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable();
		this.validTwoMeterVolumeChanGroupsHashtable = networkSummary.getValidTwoMeterVolumeChanGroupsHashtable();

		createGraphs(includeDifferenceGraphs);
		pack();
		setVisible(true);
	}
	
	private void createGraphs(boolean includeDifferenceGraphs) {
        Border border = BorderFactory.createLineBorder(Color.black);

		this.getContentPane().setLayout(new GridLayout(0, 2));
		Vector<ChartPanel> individualChannelChartPanelVector = getIndividualChannelGraphs(includeDifferenceGraphs);
		Vector<JPanel> channelGroupChartPanelVector = getChannelGroupGraphs(includeDifferenceGraphs);
		int numRows = Math.max(individualChannelChartPanelVector.size(), channelGroupChartPanelVector.size());
		for(int i=0; i<numRows; i++) {
			JPanel panel = null;
			if(individualChannelChartPanelVector.size()>i && individualChannelChartPanelVector.get(i) != null ) {
				panel = individualChannelChartPanelVector.get(i);
			}else {
				panel = new JPanel();
			}
			panel.setBorder(border);
			getContentPane().add(panel);
			if(channelGroupChartPanelVector.size()>i && channelGroupChartPanelVector.get(i) != null) {
				panel = channelGroupChartPanelVector.get(i);
			}else {
				panel = new JPanel();
			}
			panel.setBorder(border);
			getContentPane().add(panel);
		}
	}//createGraphs

	
	private void addValueToXYSeries(String chanString, Hashtable<String, Double> hashtable, XYSeries xySeries) {
		double chanDouble = Double.parseDouble(chanString);
		if(hashtable.containsKey(chanString)) {
			double value = hashtable.get(chanString);
			xySeries.add(chanDouble, value);
		}else {
			xySeries.add(chanDouble, Double.NaN);
		}
	}
	
	private Vector<ChartPanel> getIndividualChannelGraphs(boolean includeDifferenceGraphs) {
		Vector<ChartPanel> returnVector = new Vector<ChartPanel>(); 
		XYSeries twoMeterMaxVolSeries = new XYSeries("2m DEM", true);
		XYSeries tenMeterMaxVolSeries = new XYSeries("10m DEM", true);
		XYSeries dsm2VolSeries = new XYSeries("DSM2", true);
		XYSeries twoMeterVolDiffSeries = new XYSeries("2m DEM Vol Diff", true);
		XYSeries tenMeterVolDiffSeries = new XYSeries("10m DEM Vol Diff", true);
		XYSeries twoMeterVolPercentDiffSeries = new XYSeries("2m DEM Vol % Diff", true);
		XYSeries tenMeterVolPercentDiffSeries = new XYSeries("10m DEM Vol % Diff", true);
		for(int i=0; i<this.individualChannelsVector.size(); i++) {
			try {
				String chanString = this.individualChannelsVector.get(i);
				addValueToXYSeries(chanString, this.dsm2VolumeHashtable, dsm2VolSeries);
				addValueToXYSeries(chanString, this.twoMeterVolumeHashtable, twoMeterMaxVolSeries);
				addValueToXYSeries(chanString, this.tenMeterVolumeHashtable, tenMeterMaxVolSeries);
				addValueToXYSeries(chanString, this.dsm2VolumeMinusGISValidTwoMeterVolumeHashtable, twoMeterVolDiffSeries);
				addValueToXYSeries(chanString, this.dsm2VolumeMinusGISValidTenMeterVolumeHashtable, tenMeterVolDiffSeries);
				addValueToXYSeries(chanString, this.dsm2ValidPercentVolumeDiffVsTwoMeterHashtable, twoMeterVolPercentDiffSeries);
				addValueToXYSeries(chanString, this.dsm2ValidPercentVolumeDiffVsTenMeterHashtable, tenMeterVolPercentDiffSeries);
			}catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(this.csdpFrame, "Error in GISSummaryStatisticGraphFrame.getIndividualChannelGraphs: "
						+ "can't parse channel number as double", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		XYSeriesCollection channelVolumeSeriesCollection = new XYSeriesCollection();
		channelVolumeSeriesCollection.addSeries(dsm2VolSeries);
		channelVolumeSeriesCollection.addSeries(tenMeterMaxVolSeries);
		channelVolumeSeriesCollection.addSeries(twoMeterMaxVolSeries);

		XYSeriesCollection twoMVolDiffSeriesCollection = new XYSeriesCollection(twoMeterVolDiffSeries);
		XYSeriesCollection tenMVolDiffSeriesCollection = new XYSeriesCollection(tenMeterVolDiffSeries);
		XYSeriesCollection twoMVolPercentDiffSeriesCollection = new XYSeriesCollection(twoMeterVolPercentDiffSeries);
		XYSeriesCollection tenMVolPercentDiffSeriesCollection = new XYSeriesCollection(tenMeterVolPercentDiffSeries);

		String channelVolumesTitle = "GIS Calculated Individual Channel Volumes vs DSM2 Volumes";
		String xLabel = "DSM2 Channel Number";
		String yLabel = "Channel Volume, ft3";
		JFreeChart channelVolumesChart = CsdpFunctions.createChartWithXYPlot(this.csdpFrame, channelVolumesTitle, 
				xLabel, yLabel, channelVolumeSeriesCollection, true, false, new float[] {1f,3f,6f});
		String diffYLabel = "Volume Diff, ft3";
		String percentDiffYLabel = "Volume % Diff";

		String twoMeterVolDiffTitle = "Volume Difference: DSM2 Volume - 2m DEM Volume";
		JFreeChart twoMeterVolDiffChart = CsdpFunctions.createChartWithXYPlot(this.csdpFrame, twoMeterVolDiffTitle, 
				xLabel, diffYLabel, twoMVolDiffSeriesCollection, true, true, new float[] {1f});
		String tenMeterVolDiffTitle = "Volume Difference: DSM2 Volume - 10m DEM Volume";
		JFreeChart tenMeterVolDiffChart = CsdpFunctions.createChartWithXYPlot(this.csdpFrame, tenMeterVolDiffTitle, 
				xLabel, diffYLabel, tenMVolDiffSeriesCollection, true, true, new float[] {1f});
		String twoMeterVolPercentDiffTitle = "% Volume Difference: DSM2 vs 2m DEM";
		JFreeChart twoMeterVolPercentDiffChart = CsdpFunctions.createChartWithXYPlot(this.csdpFrame, twoMeterVolPercentDiffTitle, 
				xLabel, percentDiffYLabel, twoMVolPercentDiffSeriesCollection, true, true, new float[] {1f});
		String tenMeterVolPercentDiffTitle = "% Volume Difference: DSM2 vs 10m DEM";
		JFreeChart tenMeterVolPercentDiffChart = CsdpFunctions.createChartWithXYPlot(this.csdpFrame, tenMeterVolPercentDiffTitle, 
				xLabel, percentDiffYLabel, tenMVolPercentDiffSeriesCollection, true, true, new float[] {1f});

        ChartPanel channelVolumesChartPanel = new ChartPanel(channelVolumesChart);
        ChartPanel twoMeterVolDiffChartPanel = new ChartPanel(twoMeterVolDiffChart);
        ChartPanel tenMeterVolDiffChartPanel = new ChartPanel(tenMeterVolDiffChart);
        ChartPanel twoMeterVolPercentDiffChartPanel = new ChartPanel(twoMeterVolPercentDiffChart);
        ChartPanel tenMeterVolPercentDiffChartPanel = new ChartPanel(tenMeterVolPercentDiffChart);
        returnVector.addElement(channelVolumesChartPanel);
        if(includeDifferenceGraphs)
        	returnVector.addElement(twoMeterVolDiffChartPanel);
        returnVector.addElement(twoMeterVolPercentDiffChartPanel);
        if(includeDifferenceGraphs)
        	returnVector.addElement(tenMeterVolDiffChartPanel);
        returnVector.addElement(tenMeterVolPercentDiffChartPanel);
        return returnVector;
	}//getIndividualChannelGraphs

	/*
	 * 	create graphs for channel groups
	 */
	private Vector<JPanel> getChannelGroupGraphs(boolean includeDifferenceGraphs) {
		Vector<JPanel> returnVector = new Vector<JPanel>();
		DefaultCategoryDataset channelGroup2mVolumeDifferenceDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset channelGroup2mVolumePercentDifferenceDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset channelGroup2m10mVolumesDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset channelGroup10mVolumeDifferenceDataset = new DefaultCategoryDataset();
		DefaultCategoryDataset channelGroup10mVolumePercentDifferenceDataset = new DefaultCategoryDataset();
		
		
		String twoMeterVolSeriesName = "2m DEM"; 
		String tenMeterVolSeriesName = "10m DEM";
		String dsm2VolSeriesName = "DSM2";
		double twoMeterVolume = Double.NaN;
		double twoMeterVolDiff = Double.NaN;
		double twoMeterPercentVolDiff = Double.NaN;
		double tenMeterVolume = Double.NaN;
		double dsm2Volume = Double.NaN;
		double tenMeterVolDiff = Double.NaN;
		double tenMeterPercentVolDiff = Double.NaN;
		
		for(int i=0; i<this.chanGroupNamesInOrderVector.size(); i++) {
			String chanGroupName = this.chanGroupNamesInOrderVector.get(i);
			if(this.validTwoMeterVolumeChanGroupsHashtable.containsKey(chanGroupName)) {
				twoMeterVolume = this.validTwoMeterVolumeChanGroupsHashtable.get(chanGroupName);
			}
			if(this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable.containsKey(chanGroupName)) {
				twoMeterVolDiff = this.dsm2VolumeMinusGISValidTwoMeterVolumeChanGroupsHashtable.get(chanGroupName);
			}
			if(this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.containsKey(chanGroupName)) {
				twoMeterPercentVolDiff = this.dsm2ValidPercentVolumeDiffVsTwoMeterChanGroupsHashtable.get(chanGroupName);
				if(twoMeterPercentVolDiff==Double.POSITIVE_INFINITY) twoMeterPercentVolDiff=Double.NaN;
			}
			if(this.validTenMeterVolumeChanGroupsHashtable.containsKey(chanGroupName)) {
				tenMeterVolume = this.validTenMeterVolumeChanGroupsHashtable.get(chanGroupName);
			}
			if(this.dsm2VolumeChanGroupsHashtable.containsKey(chanGroupName)) {
				dsm2Volume = this.dsm2VolumeChanGroupsHashtable.get(chanGroupName);
			}
			if(this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable.containsKey(chanGroupName)) {
				tenMeterVolDiff = this.dsm2VolumeMinusGISValidTenMeterVolumeChanGroupsHashtable.get(chanGroupName);
			}
			if(this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.containsKey(chanGroupName)) {
				tenMeterPercentVolDiff = this.dsm2ValidPercentVolumeDiffVsTenMeterChanGroupsHashtable.get(chanGroupName);
				if(tenMeterPercentVolDiff==Double.POSITIVE_INFINITY) tenMeterPercentVolDiff=Double.NaN;
			}
			
			channelGroup2m10mVolumesDataset.addValue(twoMeterVolume, twoMeterVolSeriesName, chanGroupName);
			channelGroup2mVolumeDifferenceDataset.addValue(twoMeterVolDiff, twoMeterVolSeriesName, chanGroupName);
			channelGroup2mVolumePercentDifferenceDataset.addValue(twoMeterPercentVolDiff, twoMeterVolSeriesName, chanGroupName);
			channelGroup2m10mVolumesDataset.addValue(tenMeterVolume, tenMeterVolSeriesName, chanGroupName);
			channelGroup2m10mVolumesDataset.addValue(dsm2Volume, dsm2VolSeriesName, chanGroupName);
			channelGroup10mVolumeDifferenceDataset.addValue(tenMeterVolDiff, tenMeterVolSeriesName, chanGroupName);
			channelGroup10mVolumePercentDifferenceDataset.addValue(tenMeterPercentVolDiff, tenMeterVolSeriesName, chanGroupName);
		}
		
		String channelGroup2mVolumeDiffChartTitle = "Channel Group Volume Difference: DSM2 - GIS 2m DEM";
		String channelGroup2mVolumePercentDiffChartTitle = "Channel Group % Volume Difference from 2m DEM";
		String channelGroup2m10mVolumesChartTitle = "Channel Group Volumes: 2m & 10m GIS vs DSM2";
		String channelGroup10mVolumeDiffChartTitle = "Channel Group Volume Difference: DSM2 - GIS 10m DEM";
		String channelGroup10mVolumePercentDiffChartTitle = "Channel Group % Volume Difference from 10m DEM";
		String channelGroupXLabel = "";
		String valueAxisLabel = "Volume, ft3";
		String valueAxisLabelDiff = "Volume Diff, ft3";
		String valueAxisLabelPercentDiff = "% Volume Diff";
		JFreeChart channelGroup2m10mVolumesChart = CsdpFunctions.createLineChart(csdpFrame, channelGroup2m10mVolumesChartTitle, channelGroupXLabel, 
				valueAxisLabel, channelGroup2m10mVolumesDataset, false);
		JFreeChart channelGroup2mVolumeDiffChart = CsdpFunctions.createLineChart(csdpFrame, channelGroup2mVolumeDiffChartTitle, channelGroupXLabel, 
				valueAxisLabelDiff, channelGroup2mVolumeDifferenceDataset, true);
		JFreeChart channelGroup2mVolumePercentDiffChart = CsdpFunctions.createLineChart(csdpFrame, channelGroup2mVolumePercentDiffChartTitle, 
				channelGroupXLabel, valueAxisLabelPercentDiff, channelGroup2mVolumePercentDifferenceDataset, true);
		JFreeChart channelGroup10mVolumeDiffChart = CsdpFunctions.createLineChart(csdpFrame, channelGroup10mVolumeDiffChartTitle, channelGroupXLabel, 
				valueAxisLabelDiff, channelGroup10mVolumeDifferenceDataset, true);
		JFreeChart channelGroup10mVolumePercentDiffChart = CsdpFunctions.createLineChart(csdpFrame, channelGroup10mVolumePercentDiffChartTitle, 
				channelGroupXLabel, valueAxisLabelPercentDiff, channelGroup10mVolumePercentDifferenceDataset, true);

		String channelGroupsString = "<HTML><BODY>Channel Group Definitions<BR>";
		for(int i=0; i<this.chanGroupNamesInOrderVector.size(); i++) {
			String chanGroupName = this.chanGroupNamesInOrderVector.get(i);
			channelGroupsString += chanGroupName+": "+this.channelGroupsHashtable.get(chanGroupName) + "<BR>";
		}
		channelGroupsString += "</BODY></HTML>";
		
		returnVector.addElement(new ChartPanel(channelGroup2m10mVolumesChart));
//		if(INCLUDE_DIFFERENCE_GRAPHS) {
//			returnVector.addElement(createMessageJPanel("There are currently no 2m Results for channel groups", false)); 
//			returnVector.addElement(createMessageJPanel(channelGroupsString, true));
//			returnVector.addElement(new ChartPanel(channelGroup10mVolumeDiffChart));
//		}else {
//			returnVector.addElement(createMessageJPanel("", false));
//		}
		if(includeDifferenceGraphs) {
			returnVector.addElement(new ChartPanel(channelGroup2mVolumeDiffChart));
		}
		returnVector.addElement(new ChartPanel(channelGroup2mVolumePercentDiffChart));
		if(includeDifferenceGraphs) {
			returnVector.addElement(new ChartPanel(channelGroup10mVolumeDiffChart));
		}
		returnVector.addElement(new ChartPanel(channelGroup10mVolumePercentDiffChart));
		return returnVector;
	}//getChannelGroupGraphs

	private JPanel createMessageJPanel(String message, boolean multipleLines) {
		JPanel returnJPanel = new JPanel(new BorderLayout());
		JLabel messageLabel = new JLabel(message);
		returnJPanel.add(messageLabel);
		return returnJPanel;
	}
	
}//class GISSummaryStatisticsGraphFrame
