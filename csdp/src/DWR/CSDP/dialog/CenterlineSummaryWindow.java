/*
    Copyright (C) 1998 State of California, Department of Water
    Resources.

    This program is licensed to you under the terms of the GNU General
    Public License, version 2, as published by the Free Software
    Foundation.

    You should have received a copy of the GNU General Public License
    along with this program; if not, contact Dr. Francis Chung, below,
    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
    02139, USA.

    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
    DAMAGE.

    For more information, contact:

    Dr. Francis Chung
    California Dept. of Water Resources
    Division of Planning, Delta Modeling Section
    1416 Ninth Street
    Sacramento, CA  95814
    916-653-5601
    chung@water.ca.gov

    or see our home page: http://wwwdelmod.water.ca.gov/
*/
package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import DWR.CSDP.Centerline;
import DWR.CSDP.CsdpFrame;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.Network;
import DWR.CSDP.Xsect;

/**
 * Displays scatter plots of area, width, wetted perimeter, and bottom elevation.
 * On right side, displays volume, wetted area, and surface area.
 * Will work for selected centerline or for specified range of centerlines.
 * @author btom
 *
 */
public class CenterlineSummaryWindow extends JFrame {
	private Network network;
//	private String chanNum;
	private Vector<String> chanNumbersVector = new Vector<String>();
	private String reachName;
	private CsdpFrame csdpFrame;
	private String centerlineNames;
	
	public CenterlineSummaryWindow(CsdpFrame csdpFrame, Network network) {
		super("Summary for Channel "+network.getSelectedCenterlineName());
		this.chanNumbersVector.addElement(network.getSelectedCenterlineName());
		this.csdpFrame = csdpFrame;
		this.network = network;
		addContent();
	}
	
	/*
	 * Constructor for creating a window for multiple centerlines, which will be specified as a string in one of the following formats:
	 * 1-21,23,385
	 */
	public CenterlineSummaryWindow(CsdpFrame csdpFrame, Network network, String reachName, String centerlineNames) {
		super("Reach Summary for "+reachName);
		this.csdpFrame = csdpFrame;
		this.reachName = reachName;
		this.centerlineNames = centerlineNames;
		this.network = network;
		String[] parts = centerlineNames.split(",");
//		try {
			chanNumbersVector = new Vector<String>();
			for(int i=0; i<parts.length; i++) {
				if(parts[i].indexOf("-")>=0) {
					String[] rangeParts = parts[i].split("-");
					System.out.println("about to parse values: "+rangeParts[0]+","+rangeParts[1]);
					int firstNum = Integer.parseInt(rangeParts[0]);
					int lastNum = Integer.parseInt(rangeParts[1]);
					if(firstNum<lastNum) {
						for(int k=firstNum; k<=lastNum; k++) {
							chanNumbersVector.addElement(String.valueOf(k));
						}
					}else if(firstNum>lastNum) {
						for(int k=firstNum; k>=lastNum; k--) {
							chanNumbersVector.addElement(String.valueOf(k));
						}
					}else if(firstNum==lastNum) {
						chanNumbersVector.addElement(String.valueOf(firstNum));
					}
				}else {
					chanNumbersVector.addElement(parts[i]);
				}
			}//for

			this.network = network;
			addContent();
//		}catch(Exception e) {
//			JOptionPane.showMessageDialog(gui, "Unable to parse numeric centerline names string: "+centerlineNames, "Error", JOptionPane.ERROR_MESSAGE);
//		}
	}//CenterlineSummaryWindow
	
	private void addContent(){
    	String xLabel = "Distance from Upstream End, ft";

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
    	String yLabelWetP = "WetP, ft2";
    	String yLabelWidth = "Width, ft2";
    	String yLabelBottomElevation = "Bottom Elevation, ft";
		
    	XYDataset[] areaDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
    	XYDataset[] widthDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
    	XYDataset[] wetPDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
    	XYDataset[] bottomElevationDatasetArray = new XYSeriesCollection[this.chanNumbersVector.size()];
    	boolean allCenterlinesExist = true;
    	for(int i=0; i<this.chanNumbersVector.size(); i++) {
    		String centerlineName = this.chanNumbersVector.get(i);
    		if(!this.network.centerlineExists(centerlineName)) {
    			allCenterlinesExist = false;
    			JOptionPane.showMessageDialog(this, "Your channel specification contains one or more non-existent centerlines.", "Error", JOptionPane.ERROR_MESSAGE);
    		}
    	}

    	if(allCenterlinesExist) {
    		double lengthIncrement = 0.0;
    		for(int i=0; i<this.chanNumbersVector.size(); i++) {
	    		String centerlineName = this.chanNumbersVector.get(i);
	    		Centerline centerline = this.network.getCenterline(centerlineName);
	    		int numXsects = centerline.getNumXsects();
		    	XYSeries areaSeries = new XYSeries("Area", false);
		    	XYSeries wetPSeries = new XYSeries("Wetted Perimeter", false);
		    	XYSeries widthSeries = new XYSeries("Width", false);
		    	XYSeries bottomElevationSeries = new XYSeries("Botton Elevation", false);
				for(int j=0; j<numXsects; j++){
					Xsect currentXsect = centerline.getXsect(j);
					if(currentXsect.getNumPoints()>0) {
						double distanceAlong = lengthIncrement + currentXsect.getDistAlongCenterlineFeet();
						double e = CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS;
						double area = currentXsect.getAreaSqft(e);
						double wetP = currentXsect.getWettedPerimeterFeet(e);
						double width = currentXsect.getWettedPerimeterFeet(e);
						double botElev = currentXsect.getMinimumElevationFeet();
						areaSeries.add(area, distanceAlong);
						wetPSeries.add(wetP, distanceAlong);
						widthSeries.add(width, distanceAlong);
						bottomElevationSeries.add(botElev, distanceAlong);
					}
				}
				lengthIncrement += centerline.getLengthFeet();
		        areaDatasetArray[i] = new XYSeriesCollection(areaSeries);
		        wetPDatasetArray[i] = new XYSeriesCollection(wetPSeries);
		        widthDatasetArray[i] = new XYSeriesCollection(widthSeries);
		        bottomElevationDatasetArray[i] = new XYSeriesCollection(bottomElevationSeries);
	    	}
	        JFreeChart areaChart = createChartWithScatterPlot(titleArea, xLabel, yLabelArea, areaDatasetArray);
	        JFreeChart wetPChart = createChartWithScatterPlot(titleWetP,xLabel, yLabelWetP, wetPDatasetArray);
	        JFreeChart widthChart = createChartWithScatterPlot(titleWidth, xLabel, yLabelWidth, widthDatasetArray);
	        JFreeChart bottomElevationChart = createChartWithScatterPlot(titleBottomElevation, xLabel, yLabelBottomElevation, bottomElevationDatasetArray);
	    	  	
	        ChartPanel areaChartPanel = new ChartPanel(areaChart);
	        ChartPanel wetPChartPanel = new ChartPanel(wetPChart);
	        ChartPanel widthChartPanel = new ChartPanel(widthChart);
	        ChartPanel bottomElevationChartPanel = new ChartPanel(bottomElevationChart);
			setSize(800, 800);
			
			JPanel chartsPanel = new JPanel(new GridLayout(0,1,5,5));
			chartsPanel.add(areaChartPanel);
			chartsPanel.add(wetPChartPanel);
			chartsPanel.add(widthChartPanel);
			chartsPanel.add(bottomElevationChartPanel);

			//Right Column
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
				JPanel legendPanel = DialogLegendFactory.createLegendPanel(title, legendColors, legendText);
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
					+ "linear variation between cross-sections.</html>");
			centerlineOrReachSummaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			centerlineOrReachSummaryLabel.setBackground(Color.LIGHT_GRAY);
			JLabel elevationLabel = new JLabel("Elevation, ft");
			JLabel centerlineLengthLabel = new JLabel("Centerline Length, ft");
			JLabel channelVolumeLabel = new JLabel("Channel Volume, ft3");
			JLabel channelWettedAreaLabel = new JLabel("Channel Wetted Area, ft2");
			JLabel channelSurfaceAreaLabel = new JLabel("Channel Surface Area, ft2");
	
			double length = 0.0;
			double volume = 0.0;
			double wettedArea = 0.0;
			double surfaceArea = 0.0;
			for(int i=0; i<this.chanNumbersVector.size(); i++) {
				String centerlineName = this.chanNumbersVector.get(i);
				Centerline centerline = this.network.getCenterline(centerlineName);
				length += centerline.getLengthFeet();
				volume += centerline.getChannelVolumeEstimateNoInterp(elevation);
				wettedArea += centerline.getChannelWettedAreaEstimateNoInterp(elevation);
				surfaceArea += centerline.getChannelSurfaceAreaEstimateNoInterp(elevation);
			}		
			JLabel elevValueLabel = new JLabel(String.format("%,.2f", elevation), SwingConstants.RIGHT);
			JLabel cLengthValueLabel = new JLabel(String.format("%,.1f", length), SwingConstants.RIGHT);
			JLabel volValueLabel = new JLabel(String.format("%,.1f", volume), SwingConstants.RIGHT);
			JLabel wetAreaValueLabel = new JLabel(String.format("%,.1f", wettedArea), SwingConstants.RIGHT);
			JLabel surfAreaValueLabel = new JLabel(String.format("%,.1f",surfaceArea), SwingConstants.RIGHT);

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
			
			JPanel topValuesPanel = new JPanel(new GridLayout(0,1,5,5));
			topValuesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			topValuesPanel.add(centerlineOrReachSummaryLabel);
			topValuesPanel.add(conveyanceCharacteristicsPanel);
			
			valuesPanel.add(topValuesPanel, BorderLayout.SOUTH);
			
			
			JPanel centerlineOrReachInfoPanel = new JPanel(new BorderLayout());
			centerlineOrReachInfoPanel.add(valuesPanel, BorderLayout.NORTH);
				
			getContentPane().setLayout(new BorderLayout());
	    	getContentPane().add(chartsPanel, BorderLayout.CENTER);
	    	getContentPane().add(centerlineOrReachInfoPanel, BorderLayout.EAST);
	    	setVisible(true);
    	}
	}//addContent

//	/*
//	 * Create chart for one time series
//	 */
//	private JFreeChart createChartWithScatterPlot(String title, String xLabel, String yLabel, XYDataset dataset) {
//	//  return ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset);
//		//The class is developed differently from the api: with this many args, it wants them in a different order.
//	  return ChartFactory.createScatterPlot(title, yLabel, xLabel, dataset, PlotOrientation.HORIZONTAL, false, true, true);
//	}
	
	/*
	 * Create chart for multiple time series
	 */
	private JFreeChart createChartWithScatterPlot(String title, String xLabel, String yLabel, XYDataset[] datasets) {
		JFreeChart jFreeChart = ChartFactory.createScatterPlot(title, yLabel, xLabel, datasets[0], PlotOrientation.HORIZONTAL, false, true, true);
		XYPlot plot = jFreeChart.getXYPlot();
		XYLineAndShapeRenderer[] renderers = new XYLineAndShapeRenderer[datasets.length];
		renderers[0] = new XYLineAndShapeRenderer(false, true);
		renderers[0].setSeriesPaint(0, this.csdpFrame.getColor(0));
		plot.setRenderer(0, renderers[0]);
		for(int i=1; i<datasets.length; i++) {

			plot.setDataset(i, datasets[i]);
			renderers[i] = new XYLineAndShapeRenderer(false, true);
//			renderers[i].setSeriesPaint(i, this.csdpFrame.getColor(i));
			//this may be deprecated, but setSeriesPaint doesn't work properly--it changes the color.
			renderers[i].setPaint(this.csdpFrame.getColor(i));
//			plot.getRendererForDataset(plot.getDataset(i)).setSeriesPaint(i, this.csdpFrame.getColor(i));
			plot.setRenderer(i, renderers[i]);
//			plot.getRenderer().setSeriesPaint(i, this.csdpFrame.getColor(i)); 
		}
		return jFreeChart;
	}

}//class CenterlineSumamryWindow

