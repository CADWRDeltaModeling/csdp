
package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import DWR.CSDP.App;
import DWR.CSDP.Centerline;
import DWR.CSDP.CsdpFrame;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.Network;
import DWR.CSDP.ResizableDoubleArray;
import DWR.CSDP.Xsect;

/**
 * Displays scatter plots of area, width, wetted perimeter, and bottom elevation.
 * On right side, displays volume, wetted area, and surface area.
 * Will work for selected centerline or for specified range of centerlines.
 * @author btom
 *
 */
public class CenterlineOrReachSummaryWindow extends JFrame {
	
	public static final int START_AT_DOWNSTREAM_END = 10;
	public static final int START_AT_UPSTREAM_END = 20;
	/*
	 * identifies which end plots will start on. Plots will either go from downstream-upstream or upstream-downstream.
	 */
	private int startingEnd;
	private Network network;
//	private String chanNum;
	private Vector<String> chanNumbersVector = new Vector<String>();
	private String reachName;
	private CsdpFrame csdpFrame;
	private String centerlineNames;
	private App app;
	
	/*
	 * Constructor for single centerline summary window
	 */
	public CenterlineOrReachSummaryWindow(CsdpFrame csdpFrame, App app, Network network, int startingEnd) {
		super("Summary for Channel "+network.getSelectedCenterlineName());
		this.chanNumbersVector.addElement(network.getSelectedCenterlineName());
		this.csdpFrame = csdpFrame;
		this.app = app;
		this.network = network;
		this.startingEnd = startingEnd;
		this.centerlineNames = null;
		addContent();
	}
	
	/*
	 * Constructor for creating a Reach Summary window for multiple centerlines, which will be specified as a string 
	 * in one of the following formats: 1-21,23,385 or 385,23,21-1 for reverse order
	 */
	public CenterlineOrReachSummaryWindow(CsdpFrame csdpFrame, App app, Network network, String reachName, String centerlineNames,
			int startingEnd) {
		super("Reach Summary for "+reachName);
		this.csdpFrame = csdpFrame;
		this.app = app;
		this.reachName = reachName;
		this.centerlineNames = centerlineNames;
		this.network = network;
		String[] parts = centerlineNames.split(",");
		this.chanNumbersVector = CsdpFunctions.parseChanGroupString(csdpFrame, centerlineNames);
		this.network = network;
		this.startingEnd = startingEnd;
		addContent();
	}//CenterlineSummaryWindow

	/**
	 * When user closes window, will be removed from Vector or Hashtable storing all instances of this class.
	 * @author btom
	 *
	 */
	private class CloseWindowListener implements WindowListener {
		private App app;
		private CenterlineOrReachSummaryWindow parent;
		public CloseWindowListener(CenterlineOrReachSummaryWindow parent) {
			this.parent = parent;
			this.app = this.parent.app;
		}
		public void windowClosing(WindowEvent arg0) {
			String keyString = null;
			if(this.parent.centerlineNames==null) {
				keyString = this.parent.chanNumbersVector.get(0);
			}else {
				keyString = this.parent.centerlineNames;
			}
			this.app.removeCenterlineOrReachSummaryWindow(keyString);
		}
		public void windowActivated(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		public void windowDeactivated(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowOpened(WindowEvent arg0) {}
	}
	
	/*
	 * returns true if this instance includes the given centerline.
	 */
	public boolean containsCenterline(String centerlineName) {
		boolean returnValue = false;
		for(int i=0; i<this.chanNumbersVector.size(); i++) {
			if(this.chanNumbersVector.get(i).equals(centerlineName)){
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}//containsCenterline

	public void updateWindow() {
		this.getContentPane().removeAll();
		addContent();
	}
	
	private void addContent(){
		this.addWindowListener(new CloseWindowListener(this));
		
		if(startingEnd!=START_AT_DOWNSTREAM_END && startingEnd!=START_AT_UPSTREAM_END) {
			JOptionPane.showMessageDialog(this.csdpFrame, "Starting end was not specified correctly. Starting at downstream end.", "Warning", JOptionPane.INFORMATION_MESSAGE);
			startingEnd = START_AT_DOWNSTREAM_END;
		}
		String xLabel = null;
		if(this.startingEnd==START_AT_UPSTREAM_END) {
			xLabel = "Distance from Upstream End, ft";
		}else {
			xLabel = "Distance from Downstream end, ft";
		}
    	String titleArea = null;
    	String titleWetP = null;
    	String titleWidth = null;
    	String titleBottomElevation = null;
    	
    	boolean singlePlot = true;
    	if(this.chanNumbersVector.size()==1) {
    		singlePlot=true;
    	}else if(this.chanNumbersVector.size()>1) {
    		singlePlot = false;
    	}else {
    		//this shouldn't ever happen
    		singlePlot = true;
    	}
    	
    	if(singlePlot) {
    		String chanNum = this.chanNumbersVector.get(0);
	    	titleArea = "Area Profile for Channel " + chanNum;
	    	titleWetP = "Wetted Perimeter Profile for Channel "+chanNum;
	    	titleWidth = "Width Profile for Channel "+chanNum;
	    	titleBottomElevation = "Bottom Elevation Profile for Channel "+chanNum;
    	}else{
	    	titleArea = "Area Profile for "+this.reachName;
	    	titleWetP = "Wetted Perimeter Profile for "+this.reachName;
	    	titleWidth = "Width Profile for "+this.reachName;
	    	titleBottomElevation = "Bottom Elevation Profile for "+this.reachName;
    	}
	    	
    	String yLabelArea = "Area, ft2";
    	String yLabelWetP = "WetP, ft";
    	String yLabelWidth = "Width, ft";
    	String yLabelBottomElevation = "Bottom Elevation, ft";
		
//    	XYDataset[] areaDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
//    	XYDataset[] widthDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
//    	XYDataset[] wetPDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
//    	XYDataset[] bottomElevationDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
    	XYSeriesCollection areaSeriesCollection = new XYSeriesCollection();
    	XYSeriesCollection widthSeriesCollection = new XYSeriesCollection();
    	XYSeriesCollection wetPSeriesCollection = new XYSeriesCollection();
    	XYSeriesCollection bottomElevationSeriesCollection = new XYSeriesCollection();
    	boolean displayWindow = true;
    	for(int i=0; i<this.chanNumbersVector.size(); i++) {
    		String centerlineName = this.chanNumbersVector.get(i);
    		if(!this.network.centerlineExists(centerlineName)) {
    			this.chanNumbersVector.remove(i);
    			int response = JOptionPane.showConfirmDialog(this, "Your channel specification contains one or more non-existent centerlines. "
    					+ "Continue?", "Continue?", JOptionPane.YES_NO_OPTION);
    			if(response==JOptionPane.YES_OPTION) {
    				displayWindow = true;
    			}else {
    				displayWindow = false;
    			}
    		}
    	}

    	if(displayWindow) {
    		populateSeriesCollections(areaSeriesCollection, wetPSeriesCollection, widthSeriesCollection, bottomElevationSeriesCollection);
    		
    		boolean legend = false;
	        JFreeChart areaChart = CsdpFunctions.createChartWithScatterPlot(this.csdpFrame, titleArea, xLabel, yLabelArea, areaSeriesCollection, 
	        		legend);
	        JFreeChart wetPChart = CsdpFunctions.createChartWithScatterPlot(this.csdpFrame, titleWetP,xLabel, yLabelWetP, wetPSeriesCollection, 
	        		legend);
	        JFreeChart widthChart = CsdpFunctions.createChartWithScatterPlot(this.csdpFrame, titleWidth, xLabel, yLabelWidth, widthSeriesCollection, 
	        		legend);
	        JFreeChart bottomElevationChart = CsdpFunctions.createChartWithScatterPlot(this.csdpFrame, titleBottomElevation, 
	        		xLabel, yLabelBottomElevation, bottomElevationSeriesCollection, legend);
	    	  	
	        ChartPanel areaChartPanel = new ChartPanel(areaChart);
	        ChartPanel wetPChartPanel = new ChartPanel(wetPChart);
	        ChartPanel widthChartPanel = new ChartPanel(widthChart);
	        ChartPanel bottomElevationChartPanel = new ChartPanel(bottomElevationChart);
			setSize(1000, 1000);
			
			JPanel chartsPanel = new JPanel(new GridLayout(0,1,5,5));
			chartsPanel.add(areaChartPanel);
			chartsPanel.add(wetPChartPanel);
			chartsPanel.add(widthChartPanel);
			chartsPanel.add(bottomElevationChartPanel);

			//Right Column: legend and dconveyance panel
			JPanel valuesPanel = new JPanel(new BorderLayout());
			valuesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			if(this.chanNumbersVector.size()>1) {
				Color[] legendColors = new Color[chanNumbersVector.size()];
				String[] legendText = new String[chanNumbersVector.size()];
				for(int i=0; i<chanNumbersVector.size(); i++) {
					legendColors[i] = this.csdpFrame.getColor(i);
					legendText[i] = this.chanNumbersVector.get(i);
				}
				String title = "Legend"; 
				//Beware: reversing order only affects channels, not cross-sections.
				boolean reverseOrder = true;
				if(this.startingEnd==START_AT_UPSTREAM_END) {
					reverseOrder = false;
				}
				JPanel legendPanel = DialogLegendFactory.createLegendPanel(title, legendColors, legendText, reverseOrder);
				//for some reason the colors used in the plots doesn't match the colors here, so fix before adding legend.
				JPanel channelsAndLegendPanel = new JPanel(new BorderLayout());

				JLabel requestedChanJLabel = new JLabel("<HTML><B>Requested channels:</B> <BR>"+this.centerlineNames+"<BR></HTML>");
				channelsAndLegendPanel.add(requestedChanJLabel, BorderLayout.NORTH);
				channelsAndLegendPanel.add(legendPanel, BorderLayout.SOUTH);
				valuesPanel.add(channelsAndLegendPanel, BorderLayout.NORTH);
				//Displays the string entered by user. Shouldn't be necessary with legend.
				//				valuesPanel.add(new JLabel(this.centerlineNames), BorderLayout.SOUTH);
			}

			//////////////////////////////////////////////////////////////////////////////////////
			// now add conveyance characteristics for the channel or group of channels (reach)  //
			//////////////////////////////////////////////////////////////////////////////////////
			
			JPanel conveyanceCharacteristicsPanel = new JPanel(new GridLayout(0,2));
			conveyanceCharacteristicsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			double elevation = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
			String summaryTitle = "Centerline";
			if(this.chanNumbersVector.size()>1) {
				summaryTitle = "Reach";
			}
			JLabel centerlineOrReachSummaryLabel = new JLabel("<html><h3>"+summaryTitle + " Summary</h3><BR>"
					+ "Volume, Wetted Area, and Surface area are estimates<BR>"
					+ "assuming no intepolation from adjacent channels,<BR>"
					+ "an elevation of " + elevation + " in the current datum, and <BR>"
					+ "linear variation between cross-sections.<BR>"
					+ "Intertidal Zone: <B>"+CsdpFunctions.INTERTIDAL_LOW_TIDE+"&lt;=Z&lt;="+CsdpFunctions.INTERTIDAL_HIGH_TIDE+"</B><BR><BR>"
							+ "Maximum Adjacent Area Ratio (MAAR) is, for each 0.5 foot elevation increment in the intertidal zone, the <BR>"
							+ "maximum ratio of cross-sectional areas between adjacent cross-sections. </html>");
			centerlineOrReachSummaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			centerlineOrReachSummaryLabel.setBackground(Color.LIGHT_GRAY);
			JLabel elevationLabel = new JLabel("Elevation, ft");
			JLabel centerlineLengthLabel = null;
			JLabel channelVolumeLabel = null;
			JLabel channelWettedAreaLabel = null;
			JLabel channelSurfaceAreaLabel = null;
			JLabel maxAreaRatioLabel = null;
			JLabel maxAdjacentAreaRatioLabel = null;
			JLabel maxAdjacentAreaRatioElevationLabel = null;
			if(this.chanNumbersVector.size()>1) {
				centerlineLengthLabel = new JLabel("Reach Length, ft");
				channelVolumeLabel = new JLabel("Reach Volume, ft3");
				channelWettedAreaLabel = new JLabel("Reach Wetted Area, ft2");
				channelSurfaceAreaLabel = new JLabel("Reach Surface Area, ft2");
				maxAreaRatioLabel = new JLabel("Reach Max Area Ratio");
				maxAdjacentAreaRatioElevationLabel = new JLabel("Reach Max Adjacent Area Ratio Elevation");
				maxAdjacentAreaRatioLabel = new JLabel("Reach Max Adjacent Area Ratio");
			}else {
				centerlineLengthLabel = new JLabel("Centerline Length, ft");
				channelVolumeLabel = new JLabel("Channel Volume, ft3");
				channelWettedAreaLabel = new JLabel("Channel Wetted Area, ft2");
				channelSurfaceAreaLabel = new JLabel("Channel Surface Area, ft2");
				maxAreaRatioLabel = new JLabel("Channel Max Area Ratio");
				maxAdjacentAreaRatioElevationLabel = new JLabel("Channel Max Adjacent Area Ratio Elevation");
				maxAdjacentAreaRatioLabel = new JLabel("Channel Max Adjacent Area Ratio");
			}
			
			double length = 0.0;
			double volume = 0.0;
			double wettedArea = 0.0;
			double surfaceArea = 0.0;
			double maxAreaRatio = -Double.MAX_VALUE;
			double reachMinArea = Double.MAX_VALUE;
			double reachMaxArea = -Double.MAX_VALUE;
			
			double elevIncrement = 0.5;
			int numMaarValues =  ((int)((CsdpFunctions.INTERTIDAL_HIGH_TIDE - CsdpFunctions.INTERTIDAL_LOW_TIDE) / elevIncrement)+1)-1;
			double[] maarElevForPlotting = new double[numMaarValues];
			double[] maarValuesForPlotting = new double[numMaarValues];
			//these are for the maximum value for the entire elevation range
			double maxAdjacentAreaRatioElevation = 0.0;
			double maxAdjacentAreaRatio = 0.0;
			
			for(int i=0; i<this.chanNumbersVector.size(); i++) {
				String centerlineName = this.chanNumbersVector.get(i);
				Centerline centerline = this.network.getCenterline(centerlineName);
				length += centerline.getLengthFeet();
				volume += centerline.getChannelVolumeEstimateNoInterp(elevation);
				wettedArea += centerline.getChannelWettedAreaEstimateNoInterp(elevation);
				surfaceArea += centerline.getChannelSurfaceAreaEstimateNoInterp(elevation);
				if(this.chanNumbersVector.size()==1 && i==0) {
					maxAreaRatio = centerline.getMaxAreaRatio();
				}else {
					reachMinArea = Math.min(centerline.getMinArea(), reachMinArea);
					reachMaxArea = Math.max(centerline.getMaxArea(), reachMaxArea);
					maxAreaRatio = reachMaxArea/reachMinArea;
				}

//				double[][] maarArrays = network.calcMAAR(this.chanNumbersVector.get(i), this.app.getDSMChannels());
				Object[] maarObjectArray = network.calcMAAR(this.chanNumbersVector.get(i), this.app.getDSMChannels());
				maarElevForPlotting = ((double[][])(maarObjectArray[0]))[0];
				maarValuesForPlotting = ((double[][])(maarObjectArray[0]))[1];
				maxAdjacentAreaRatioElevation = (double)(maarObjectArray[1]);
				maxAdjacentAreaRatio = (double)(maarObjectArray[2]);
				System.out.println("maar elevation, value="+maxAdjacentAreaRatioElevation+","+maxAdjacentAreaRatio);
				//
//				//calculate max adjacent area ratio (MAAR) using last xs from previous channel, if any, and the first xs from current channel
//				//this will not execute if only single channel or if it's the first channel
//				Xsect firstXSCurrentChan = centerline.getXsect(0);
//				int maarValuesIndex = 0;
//				if(lastXsectPreviousChannel!=null && !lastXsectPreviousChannel.hasNoPoints() && !firstXSCurrentChan.hasNoPoints() ) {
//					for(double elev=CsdpFunctions.INTERTIDAL_LOW_TIDE; elev<CsdpFunctions.INTERTIDAL_HIGH_TIDE; elev+=0.5) {
//						double a1 = lastXsectPreviousChannel.getAreaSqft(elev);
//						double a2 = firstXSCurrentChan.getAreaSqft(elev);
//						double maarAdjacentChan = Math.max(a1/a2, a2/a1);
//						if(maarAdjacentChan > maxAdjacentAreaRatio) {
//							maxAdjacentAreaRatioElevation = elev;
//							maxAdjacentAreaRatio = maarAdjacentChan;
//						}
//						if(maarAdjacentChan > maarValuesForPlotting[maarValuesIndex]) {
//							maarValuesForPlotting[maarValuesIndex] = maarAdjacentChan;
//						}
//						maarValuesIndex++;
//					}
//				}
//				
//				//now calculate MAAR within the channel. Result: for each elevation increment in the user specified
//				//intertidal zone, the maximum ratio of cross-sectional areas found by comparing adjacent cross-sections.
//				double[][] maarWithinChannelResults = centerline.getMaxAdjacentAreaRatioInRange(numMaarValues, 
//						CsdpFunctions.INTERTIDAL_LOW_TIDE, CsdpFunctions.INTERTIDAL_HIGH_TIDE, 0.5);
//				maarElevForPlotting = maarWithinChannelResults[0];
//				double[] currentChanMaarValues = maarWithinChannelResults[1];
//				for(int j=0; j<maarElevForPlotting.length; j++) {
//					if(currentChanMaarValues[j] > maarValuesForPlotting[j]) {
//						maarValuesForPlotting[j]= currentChanMaarValues[j]; 
//					}
//					if(currentChanMaarValues[j]>maxAdjacentAreaRatio) {
//						maxAdjacentAreaRatioElevation = maarElevForPlotting[j];
//						maxAdjacentAreaRatio = currentChanMaarValues[j];
//					}
//				}
//				
//				lastXsectPreviousChannel = centerline.getXsect(centerline.getNumXsects()-1);
			}		
			
			//now add MAAR and MAR graphs
	    	String graphTitleMAAR = "MAAR vs Elev";
	    	String graphTitleMAR = "MAR vs Elev";
	    	String yLabelMAAR = "MAAR";
	    	String yLabelMAR = "MAR";
	    	String xLabelMAAR = "Elevation";
	    	XYSeriesCollection MAARSeriesCollection = new XYSeriesCollection();
	    	XYSeriesCollection MARSeriesCollection = new XYSeriesCollection();

	    	XYSeries MAARSeries = new XYSeries(yLabelMAAR, false);
	    	XYSeries MARSeries = new XYSeries(yLabelMAR, false);
	    	
	    	for(int i=0; i<maarElevForPlotting.length; i++) {
	    		MAARSeries.add(maarValuesForPlotting[i], maarElevForPlotting[i]);
	    	}
	    	MAARSeriesCollection.addSeries(MAARSeries);
	    	MARSeriesCollection.addSeries(MARSeries);
			
			
			JLabel elevValueLabel = new JLabel(String.format("%,.2f", elevation), SwingConstants.RIGHT);
			JLabel cLengthValueLabel = new JLabel(String.format("%,.1f", length), SwingConstants.RIGHT);
			JLabel volValueLabel = new JLabel(String.format("%,.1f", volume), SwingConstants.RIGHT);
			JLabel wetAreaValueLabel = new JLabel(String.format("%,.1f", wettedArea), SwingConstants.RIGHT);
			JLabel surfAreaValueLabel = new JLabel(String.format("%,.1f",surfaceArea), SwingConstants.RIGHT);
			JLabel maxAreaRatioValueLabel = new JLabel(String.format("%,.1f", maxAreaRatio), SwingConstants.RIGHT);
			JLabel maxAdjacentAreaRatioElevationValueLabel = new JLabel(String.format("%,.1f", maxAdjacentAreaRatioElevation), SwingConstants.RIGHT);
			JLabel maxAdjacentAreaRatioValueLabel = new JLabel(String.format("%,.1f", maxAdjacentAreaRatio), SwingConstants.RIGHT);

			JFreeChart maarChart = CsdpFunctions.createChartWithScatterPlot(this.csdpFrame, graphTitleMAAR, xLabelMAAR, yLabelMAAR, MAARSeriesCollection, legend);
			ChartPanel maarChartPanel = new ChartPanel(maarChart);
			
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
			conveyanceCharacteristicsPanel.add(maxAreaRatioLabel);
			conveyanceCharacteristicsPanel.add(maxAreaRatioValueLabel);
			conveyanceCharacteristicsPanel.add(maxAdjacentAreaRatioElevationLabel);
			conveyanceCharacteristicsPanel.add(maxAdjacentAreaRatioElevationValueLabel);
			conveyanceCharacteristicsPanel.add(maxAdjacentAreaRatioLabel);
			conveyanceCharacteristicsPanel.add(maxAdjacentAreaRatioValueLabel);
			
			JPanel topValuesPanel = new JPanel(new GridLayout(0,1,5,5));
			topValuesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			topValuesPanel.add(centerlineOrReachSummaryLabel);
			topValuesPanel.add(conveyanceCharacteristicsPanel);
			chartsPanel.add(maarChartPanel);
			
			valuesPanel.add(topValuesPanel, BorderLayout.SOUTH);
			
			
			JPanel centerlineOrReachInfoPanel = new JPanel(new BorderLayout());
			centerlineOrReachInfoPanel.add(valuesPanel, BorderLayout.NORTH);
				
			getContentPane().setLayout(new BorderLayout());
	    	getContentPane().add(chartsPanel, BorderLayout.CENTER);
	    	getContentPane().add(centerlineOrReachInfoPanel, BorderLayout.EAST);
	    	setVisible(true);
    	}
	}//addContent

	/*
	 * Copy values to SeriesCollection objects. Order will depend upon specified order: starting at downstream vs upstream end
	 */
	private void populateSeriesCollections(XYSeriesCollection areaSeriesCollection,
			XYSeriesCollection wetPSeriesCollection, XYSeriesCollection widthSeriesCollection,
			XYSeriesCollection bottomElevationSeriesCollection) {
		double lengthIncrement = 0.0;

		int startingCenterlineIndex = -Integer.MAX_VALUE;
		int endingCenterlineIndex = -Integer.MAX_VALUE;
		int centerlineIncrement = -Integer.MAX_VALUE;
		if(this.startingEnd==START_AT_UPSTREAM_END) {
			startingCenterlineIndex = 0;
			endingCenterlineIndex = this.chanNumbersVector.size();
			centerlineIncrement = 1;
		}else {
			startingCenterlineIndex = this.chanNumbersVector.size()-1;
			endingCenterlineIndex = 0;
			centerlineIncrement = -1;
		}

		for(int i=startingCenterlineIndex; ((startingEnd==START_AT_UPSTREAM_END && i<endingCenterlineIndex) || 
				(startingEnd==START_AT_DOWNSTREAM_END && i>=endingCenterlineIndex)); i+=centerlineIncrement) {
			String centerlineName = this.chanNumbersVector.get(i);
    		Centerline centerline = this.network.getCenterline(centerlineName);
    		int numXsects = centerline.getNumXsects();
	    	XYSeries areaSeries = new XYSeries(centerlineName, false);
	    	XYSeries wetPSeries = new XYSeries(centerlineName, false);
	    	XYSeries widthSeries = new XYSeries(centerlineName, false);
	    	XYSeries bottomElevationSeries = new XYSeries(centerlineName, false);
	    	
	    	int startingXsectIndex = -Integer.MAX_VALUE;
	    	int endingXsectIndex = -Integer.MAX_VALUE;
	    	int xsectIncrement = -Integer.MAX_VALUE;
	    	if(startingEnd==START_AT_UPSTREAM_END) {
	    		startingXsectIndex = 0;
	    		endingXsectIndex = numXsects;
	    		xsectIncrement = 1;
	    	}else {
	    		startingXsectIndex = numXsects-1;
	    		endingXsectIndex = 0;
	    		xsectIncrement = -1;
	    	}
	    	
	    	for(int j=startingXsectIndex; ((startingEnd==START_AT_UPSTREAM_END && j<endingXsectIndex) || 
	    			(startingEnd==START_AT_DOWNSTREAM_END && j>=endingXsectIndex)); j+=xsectIncrement) {
				Xsect currentXsect = centerline.getXsect(j);
				if(currentXsect.getNumPoints()>0) {
					double distanceAlong = -Double.MAX_VALUE;
					if(startingEnd==START_AT_UPSTREAM_END) {
						distanceAlong = lengthIncrement + currentXsect.getDistAlongCenterlineFeet();
					}else {
						distanceAlong = lengthIncrement + centerline.getLengthFeet() - currentXsect.getDistAlongCenterlineFeet();
					}
					double e = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
					double area = currentXsect.getAreaSqft(e);
					double wetP = currentXsect.getWettedPerimeterFeet(e);
					double width = currentXsect.getWidthFeet(e);
					double botElev = currentXsect.getMinimumElevationFeet();
					areaSeries.add(area, distanceAlong);
					wetPSeries.add(wetP, distanceAlong);
					widthSeries.add(width, distanceAlong);
					bottomElevationSeries.add(botElev, distanceAlong);
				}
			}
			lengthIncrement += centerline.getLengthFeet();
			areaSeriesCollection.addSeries(areaSeries);
			wetPSeriesCollection.addSeries(wetPSeries);
			widthSeriesCollection.addSeries(widthSeries);
			bottomElevationSeriesCollection.addSeries(bottomElevationSeries);
		}
	}//populateSeriesCollections

//	/*
//	 * Create chart for one time series
//	 */
//	private JFreeChart createChartWithScatterPlot(String title, String xLabel, String yLabel, XYDataset dataset) {
//	//  return ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset);
//		//The class is developed differently from the api: with this many args, it wants them in a different order.
//	  return ChartFactory.createScatterPlot(title, yLabel, xLabel, dataset, PlotOrientation.HORIZONTAL, false, true, true);
//	}
	

}//class CenterlineSumamryWindow

