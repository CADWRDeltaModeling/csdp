package DWR.CSDP.dialog;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import DWR.CSDP.App;
import DWR.CSDP.BathymetryData;
import DWR.CSDP.Centerline;
import DWR.CSDP.CsdpFrame;
import DWR.CSDP.CsdpFunctions;
import DWR.CSDP.Network;
import DWR.CSDP.Xsect;
import DWR.CSDP.XsectBathymetryData;
import DWR.CSDP.XsectGraph;
import DWR.CSDP.Test.DialogClose;
import vista.graph.GECanvas;

public class XsectSlideshowDialog extends JDialog{

	public static int NEXT_OPTION = 10;
	public static int PREVIOUS_OPTION = 20;
	public static int PREVIOUS_CENTERLINE_OPTION = 30;
	public static int NEXT_CENTERLINE_OPTION = 40;
	public static int JUMP_TO_CENTERLINE_OPTION = 50;
	public static int QUIT_OPTION = 60;
	private String requestedCenterlineName;
	private int _response;
	private CsdpFrame csdpFrame;
	private boolean autoSave;

	/*
	 * Constructor for interactive mode--user clicks buttons or presses keys to move to next cross-section
	 * Key mappings:
	 * s: 				 save to image
	 * ctrl-left arrow:  move to previous xsect in centerline, if any
	 * ctrl-right arrow: move to next xsect in centerline, if any
	 * alt-left arrow:   move to first xsect in previous centerline, if any
	 * alt-right arrow:  move to first xsect in next centerline, if any
	 */
	public XsectSlideshowDialog(CsdpFrame csdpFrame, App app, BathymetryData bathymetryData0, BathymetryData bathymetryData1, 
			int xsectColorOption, 
			Network network0, String centerlineName, int xsectIndex0, Network network1, int xsectIndex1, 
			String networkDirectory0, String networkFilename0, String networkDirectory1, String networkFilename1, 
			String saveImageDirectory, boolean includeConveyanceCharacteristics, boolean includeMetadata,
			boolean noPreviousCenterline, boolean noNextCenterline, boolean noPreviousXsect, boolean noNextXsect) {
		super(csdpFrame, "Cross-Section Comparison for Centerline "+centerlineName, true);
		this.autoSave = false;
		initialize(csdpFrame, app, bathymetryData0, bathymetryData1, xsectColorOption, network0, centerlineName, xsectIndex0, network1, xsectIndex1, 
				networkDirectory0, networkFilename0, networkDirectory1, networkFilename1, saveImageDirectory, includeConveyanceCharacteristics, 
				includeMetadata, noPreviousCenterline, noNextCenterline, noPreviousXsect, noNextXsect);
	}

	/*
	 * DOESN'T WORK YET
	 * Constructor for autosave mode. Will display window, save to file, and dispose.
	 */
	public XsectSlideshowDialog(final CsdpFrame csdpFrame, final App app, final BathymetryData bathymetryData0, final BathymetryData bathymetryData1, 
			final int xsectColorOption, 
			final Network network0, final String centerlineName, final int xsectIndex0, final Network network1, final int xsectIndex1, 
			final String networkDirectory0, final String networkFilename0, final String networkDirectory1, final String networkFilename1, 
			final String saveImageDirectory, final boolean includeConveyanceCharacteristics, final boolean includeMetadata, final boolean autoSave) {
		super(csdpFrame, "Cross-Section Comparison for Centerline "+centerlineName, true);
		this.autoSave = autoSave;
		final boolean noPreviousCenterline = true;
		final boolean noNextCenterline = true;
		final boolean noPreviousXsect = true;
		final boolean noNextXsect = true;

//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				initialize(csdpFrame, app, bathymetryData0, bathymetryData1, xsectColorOption, network0, centerlineName, xsectIndex0, network1, xsectIndex1, 
						networkDirectory0, networkFilename0, networkDirectory1, networkFilename1, saveImageDirectory, includeConveyanceCharacteristics, 
						includeMetadata, noPreviousCenterline, noNextCenterline, noPreviousXsect, noNextXsect);
//			}});
		saveImage(saveImageDirectory, centerlineName+"_"+xsectIndex0);
		dispose();
	}//constructor

	/*
	 * adds components to window
	 */
	private void initialize(CsdpFrame csdpFrame, App app, BathymetryData bathymetryData0, BathymetryData bathymetryData1, 
			int xsectColorOption, 
			Network network0, String centerlineName, int xsectIndex0, Network network1, int xsectIndex1, 
			String networkDirectory0, String networkFilename0, String networkDirectory1, String networkFilename1, 
			String saveImageDirectory, boolean includeConveyanceCharacteristics, boolean includeMetadata,
			boolean noPreviousCenterline, boolean noNextCenterline, boolean noPreviousXsect, boolean noNextXsect) {
		this.csdpFrame = csdpFrame;
		setLayout(new GridLayout(0, 2));
		Centerline centerline0 = network0.getCenterline(centerlineName);
		Centerline centerline1 = network1.getCenterline(centerlineName);

		Xsect xsect0 = null;
		double[] xsectOriginCoord0 = null;
		JLabel xsectLabel0 = null;
		Hashtable xsectDisplayData0 = null;
		XsectBathymetryData xsectBathymetryData0 = null;
		XsectGraph xsectGraph0 = null;
		GECanvas graphCanvas0 = null;
		if(xsectIndex0>=0) {
			xsect0 = centerline0.getXsect(xsectIndex0);
			xsectOriginCoord0 = network0.getXsectOriginCoord(centerlineName, xsectIndex0);
			xsectLabel0 = new JLabel("<HTML>"+networkDirectory0+File.separator+networkFilename0+"<BR>"
					+ "Number of XS in Centerline = "+centerline0.getNumXsectsWithPoints()+"<BR>"
					+ "Distance along centerline, feet="+String.format("%.0f", xsect0.getDistAlongCenterlineFeet())+"<BR>"
					+ "origin coordinates: "+String.format("%.0f", CsdpFunctions.feetToMeters(xsectOriginCoord0[CsdpFunctions.xIndex]))+","+
					String.format("%.0f", CsdpFunctions.feetToMeters(xsectOriginCoord0[CsdpFunctions.yIndex]))
					+ "</HTML>");
			xsectDisplayData0 = network0.findXsectDisplayRegion(centerlineName, xsectIndex0, CsdpFunctions.getXsectThickness());
			xsectBathymetryData0 = bathymetryData0.findXsectData(xsectDisplayData0);

			xsectGraph0 = new XsectGraph(csdpFrame, app, bathymetryData0, xsectBathymetryData0, 
					network0, centerlineName, xsectIndex0, CsdpFunctions.getXsectThickness(), xsectColorOption);
			graphCanvas0 = xsectGraph0.getGC();

		}
		Xsect xsect1 = null;
		double[] xsectOriginCoord1 = null;
		JLabel xsectLabel1 = null;
		Hashtable xsectDisplayData1 = null;
		XsectBathymetryData xsectBathymetryData1 = null;
		XsectGraph xsectGraph1 = null;
		GECanvas graphCanvas1 = null;
		if(xsectIndex1>=0) {
			xsect1 = centerline1.getXsect(xsectIndex1);
			xsectOriginCoord1 = network1.getXsectOriginCoord(centerlineName, xsectIndex1);
			xsectLabel1 = new JLabel("<HTML>"+networkDirectory1+File.separator+networkFilename1+"<BR>"
					+ "Number of XS in Centerline = "+centerline1.getNumXsectsWithPoints()+"<BR>"
					+ "Distance along centerline, feet="+String.format("%.0f", xsect1.getDistAlongCenterlineFeet())+"<BR>"
					+ "origin coordinates: "+String.format("%.0f", CsdpFunctions.feetToMeters(xsectOriginCoord1[CsdpFunctions.xIndex]))+","+
					String.format("%.0f", CsdpFunctions.feetToMeters(xsectOriginCoord1[CsdpFunctions.yIndex]))
					+ "</HTML>");
			xsectDisplayData1 = network1.findXsectDisplayRegion(centerlineName, xsectIndex1, CsdpFunctions.getXsectThickness());
			xsectBathymetryData1 = bathymetryData1.findXsectData(xsectDisplayData1);

			xsectGraph1 = new XsectGraph(csdpFrame, app, bathymetryData1, xsectBathymetryData1, 
					network1, centerlineName, xsectIndex1, CsdpFunctions.getXsectThickness(), xsectColorOption);
			graphCanvas1 = xsectGraph1.getGC();
		}

		JLabel labelForBothXSJLabel = new JLabel("<HTML>Distance between cross-sections: N/A");
		if(xsectIndex0>=0 && xsectIndex1>=0) {
			labelForBothXSJLabel = new JLabel("<HTML>Distance between cross-sections: "+ 
					String.format("%.0f", CsdpFunctions.pointDist(xsectOriginCoord0[CsdpFunctions.xIndex], xsectOriginCoord0[CsdpFunctions.yIndex], 
							xsectOriginCoord1[CsdpFunctions.xIndex], xsectOriginCoord1[CsdpFunctions.yIndex]))+" ft");
		}
		labelForBothXSJLabel.setFont(labelForBothXSJLabel.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
		labelForBothXSJLabel.setForeground(Color.BLUE);

		GridBagLayout mainWindowGridBagLayout = new GridBagLayout();
		GridBagConstraints mainWindowGridBagConstraints = new GridBagConstraints();
		mainWindowGridBagConstraints.gridx=0;
		mainWindowGridBagConstraints.gridy=0;
		//		mainWindowGridBagConstraints.weighty=1;

		getContentPane().setLayout(mainWindowGridBagLayout);

		GridBagLayout mainPanelLayout = new GridBagLayout();
		GridBagConstraints mainPanelLayoutConstraints = new GridBagConstraints();
		JPanel mainPanel = new JPanel(mainPanelLayout);
		mainPanelLayoutConstraints.anchor=GridBagConstraints.PAGE_END;
		mainPanel.setLayout(mainPanelLayout);
		//		JPanel instructionsAndLegendPanel = new JPanel(dialogLayout);
		mainPanelLayoutConstraints.insets = new Insets(5, 5, 5, 5);
		//natural height, maximum width
		mainPanelLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanelLayoutConstraints.weighty=1.0;
		//		dialogLayoutConstraints.gridwidth=1;
		//add labels
		mainPanelLayoutConstraints.gridx=0;
		mainPanelLayoutConstraints.gridy=0;
		mainPanelLayoutConstraints.gridwidth = 2;
		//		mainPanelLayoutConstraints.ipadx = 400;
		mainPanel.add(labelForBothXSJLabel, mainPanelLayoutConstraints);
		mainPanelLayoutConstraints.gridwidth = 1;
		mainPanelLayoutConstraints.ipadx = 0;

		mainPanelLayoutConstraints.gridx=0;
		mainPanelLayoutConstraints.gridy=1;
		if(xsectGraph0 != null) {
			mainPanel.add(xsectLabel0, mainPanelLayoutConstraints);
		}else {
			mainPanel.add(new JLabel(""), mainPanelLayoutConstraints);
		}

		mainPanelLayoutConstraints.gridx=1;
		mainPanelLayoutConstraints.gridy=1;
		if(xsectGraph1 != null) {
			mainPanel.add(xsectLabel1, mainPanelLayoutConstraints);
		}else {
			mainPanel.add(new JLabel(""), mainPanelLayoutConstraints);
		}

		mainPanelLayoutConstraints.gridx=0;
		mainPanelLayoutConstraints.gridy=2;
		if(xsectGraph0 != null) {
			mainPanel.add(graphCanvas0, mainPanelLayoutConstraints);
		}else {
			mainPanel.add(new JLabel("no cross-section"), mainPanelLayoutConstraints);
		}
		mainPanelLayoutConstraints.gridx=1;
		mainPanelLayoutConstraints.gridy=2;
		if(xsectGraph1!=null) {
			mainPanel.add(graphCanvas1, mainPanelLayoutConstraints);
		}else {
			mainPanel.add(new JLabel(""), mainPanelLayoutConstraints);
		}

		//allocate more vertical space to these components
		if(includeConveyanceCharacteristics) {
			mainPanelLayoutConstraints.ipady=30;
			mainPanelLayoutConstraints.gridx=0;
			mainPanelLayoutConstraints.gridy=3;
			if(xsectGraph0 !=null) {
				mainPanel.add(xsectGraph0.getConveyanceCharacteristicsPanel(), mainPanelLayoutConstraints);
			}else {
				mainPanel.add(new JLabel("no cross-section"), mainPanelLayoutConstraints);
			}
			mainPanelLayoutConstraints.gridx=1;
			mainPanelLayoutConstraints.gridy=3;
			if(xsectGraph1 !=null) {
				mainPanel.add(xsectGraph1.getConveyanceCharacteristicsPanel(), mainPanelLayoutConstraints);
			}else {
				mainPanel.add(new JLabel("no cross-section"), mainPanelLayoutConstraints);
			}
		}
		if(includeMetadata) {
			mainPanelLayoutConstraints.ipady=100;
			mainPanelLayoutConstraints.gridx=0;
			mainPanelLayoutConstraints.gridy=5;
			if(xsectGraph0 != null) {
				mainPanel.add(xsectGraph0.getMetadataPanel(), mainPanelLayoutConstraints);
			}else {
				mainPanel.add(new JLabel("no cross-section"), mainPanelLayoutConstraints);
			}
			
			mainPanelLayoutConstraints.gridx=1;
			mainPanelLayoutConstraints.gridy=5;
			if(xsectGraph1 != null) {
				mainPanel.add(xsectGraph1.getMetadataPanel(), mainPanelLayoutConstraints);
			}else {
				mainPanel.add(new JLabel("no cross-section"), mainPanelLayoutConstraints);
			}

		}
		//reset to default
		mainPanelLayoutConstraints.ipady=0;
		JButton previousButton = new JButton("Previous XS");
		JButton nextButton = new JButton("Next XS");
		JButton previousCenterlineButton = new JButton("Previous Centerline");
		JButton nextCenterlineButton = new JButton("Next Centerline");
		JButton jumpToCenterlineButton = new JButton("Jump to centerline");
		JButton quitButton = new JButton("Quit");
		ButtonListener buttonListener = new ButtonListener();
		previousButton.addActionListener(buttonListener);
		nextButton.addActionListener(buttonListener);
		previousCenterlineButton.addActionListener(buttonListener);
		nextCenterlineButton.addActionListener(buttonListener);
		jumpToCenterlineButton.addActionListener(buttonListener);
		quitButton.addActionListener(buttonListener);
		if(noPreviousCenterline) previousCenterlineButton.setEnabled(false);
		if(noNextCenterline) nextCenterlineButton.setEnabled(false);
		if(noPreviousXsect) previousButton.setEnabled(false);
		if(noNextXsect) nextButton.setEnabled(false);

		GridBagLayout buttonPanelLayout = new GridBagLayout();
		GridBagConstraints buttonPanelLayoutConstraints = new GridBagConstraints();
		buttonPanelLayoutConstraints.anchor=GridBagConstraints.PAGE_END;
		getContentPane().setLayout(buttonPanelLayout);
		//		JPanel instructionsAndLegendPanel = new JPanel(dialogLayout);
		buttonPanelLayoutConstraints.insets = new Insets(5, 5, 5, 5);
		//natural height, maximum height
		buttonPanelLayoutConstraints.fill = GridBagConstraints.VERTICAL;
		buttonPanelLayoutConstraints.weighty=1.0;
		buttonPanelLayoutConstraints.gridwidth=1;

		JPanel buttonPanel = new JPanel(buttonPanelLayout);
		buttonPanelLayoutConstraints.gridx=0;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(previousButton, buttonPanelLayoutConstraints);
		buttonPanelLayoutConstraints.gridx=1;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(nextButton, buttonPanelLayoutConstraints);
		buttonPanelLayoutConstraints.gridx=2;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(previousCenterlineButton, buttonPanelLayoutConstraints);
		buttonPanelLayoutConstraints.gridx=3;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(nextCenterlineButton, buttonPanelLayoutConstraints);
		buttonPanelLayoutConstraints.gridx=4;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(jumpToCenterlineButton, buttonPanelLayoutConstraints);
		buttonPanelLayoutConstraints.gridx=5;
		buttonPanelLayoutConstraints.gridy=0;
		buttonPanel.add(quitButton, buttonPanelLayoutConstraints);

		if(!this.autoSave) {
			mainPanelLayoutConstraints.gridx=0;
			mainPanelLayoutConstraints.gridy=6;
			mainPanelLayoutConstraints.gridwidth=2;
			mainPanel.add(buttonPanel, mainPanelLayoutConstraints);
		}

		getContentPane().add(mainPanel, mainWindowGridBagConstraints);
		if(!this.autoSave) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new SlideshowKeyListener(saveImageDirectory,
					noPreviousCenterline, noNextCenterline, noPreviousXsect, noNextXsect, centerlineName, xsectIndex0));
		}
		validate();
		pack();
		doLayout();
		setVisible(true);

	}//constructor


	private class SlideshowKeyListener implements KeyEventDispatcher{
		private String saveImageDirectory;
		private String centerlineName;
		private int xsectIndex;
		private boolean noPreviousCenterline;
		private boolean noNextCenterline;
		private boolean noPreviousXsect;
		private boolean noNextXsect;

		public SlideshowKeyListener(String saveImageDirectory, boolean noPreviousCenterline, boolean noNextCenterline,
				boolean noPreviousXsect, boolean noNextXsect, String centerlineName, int xsectIndex) {
			this.saveImageDirectory = saveImageDirectory;
			this.noPreviousCenterline = noPreviousCenterline;
			this.noNextCenterline = noNextCenterline;
			this.noPreviousXsect = noPreviousXsect;
			this.noNextXsect = noNextXsect;
			this.centerlineName = centerlineName;
			this.xsectIndex = xsectIndex;
		}
		public void keyReleased(KeyEvent arg0) {}
		public void keyTyped(KeyEvent arg0) {}

		/*
		 * Handles key events for the window
		 * Ctrl-s will save to image
		 * Ctrl- left arrow and right arrow will go to next/previous xsect
		 * Alt- left arrow and right arrow will go to next/previous centerline 
		 */
		public boolean dispatchKeyEvent(KeyEvent e) {
			boolean keyHandled = false;
			if(e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.isControlDown()) {
					if(this.saveImageDirectory!=null && e.getKeyCode()==KeyEvent.VK_S) {
						saveImage(this.saveImageDirectory, centerlineName+"_"+xsectIndex);
					}else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
						//Ctrl-left arrow: 
						if(this.noPreviousXsect) {
							//if displaying the first xsect, then go to previous centerline if there is one
							if(!this.noPreviousCenterline) {
								_response = XsectSlideshowDialog.PREVIOUS_CENTERLINE_OPTION;
								dispose();
							}
						}else{
							//otherwise, go to the previous xsect
							_response = XsectSlideshowDialog.PREVIOUS_OPTION;
							dispose();
						}
					}else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
						//Ctrl-right arrow
						if(this.noNextXsect) {
							//if displaying the last xsect, then go to the next centerline if there is one
							if(!this.noNextCenterline) {
								_response = XsectSlideshowDialog.NEXT_CENTERLINE_OPTION;
								dispose();
							}
						}else {
							//otherwise, go to the next xsect
							_response = XsectSlideshowDialog.NEXT_OPTION;
							dispose();
						}
					}
				}else if(e.isAltDown()) {
					if(e.getKeyCode()==KeyEvent.VK_LEFT) {
						//Alt-left arrow: go to the previous centerline if there is one
						if(!this.noPreviousCenterline) {
							_response = XsectSlideshowDialog.PREVIOUS_CENTERLINE_OPTION;
							dispose();
						}
					}else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
						//Alt-right arrow: go to the next centerline if there is one
						if(!this.noNextCenterline) {
							_response = XsectSlideshowDialog.NEXT_CENTERLINE_OPTION;
							dispose();
						}
					}
				}
			}//if key pressed
			return keyHandled;
		}//dispatchKeyEvent
	}//class SlideshowKeyListener

	/*
	 * Save screenshot of dialog to file
	 * adapted from one of the examples in https://stackoverflow.com/questions/19621105/save-image-from-jpanel-after-draw
	 */
	private void saveImage(String saveImageDirectory, String saveImageFilename){
		BufferedImage imagebuf=null;
		try {
			imagebuf = new Robot().createScreenCapture(bounds());
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		Graphics2D graphics2D = imagebuf.createGraphics();
		paint(graphics2D);
		try {
			ImageIO.write(imagebuf,"jpeg", new File(saveImageDirectory+File.separator+saveImageFilename+".jpg"));
			System.out.println("saved image: "+saveImageDirectory+File.separator+saveImageFilename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error");
		}
	}//saveImage

	private class ButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			String buttonNameString = ((JButton)(arg0.getSource())).getText();
			if(buttonNameString.equalsIgnoreCase("Previous XS")) {
				_response = XsectSlideshowDialog.PREVIOUS_OPTION;
			}else if(buttonNameString.equalsIgnoreCase("Next XS")) {
				_response = XsectSlideshowDialog.NEXT_OPTION;
			}else if(buttonNameString.equalsIgnoreCase("Previous Centerline")) {
				_response = XsectSlideshowDialog.PREVIOUS_CENTERLINE_OPTION;
			}else if(buttonNameString.equalsIgnoreCase("Next Centerline")) {
				_response = XsectSlideshowDialog.NEXT_CENTERLINE_OPTION;
			}else if(buttonNameString.equalsIgnoreCase("Jump to centerline")) {
				String title = "Jump to centelrine";
				String instructions = "Enter a centerline name for which you would like to display a cross-section comparison.";
				String[] names = new String[] {"Centerline name"};
				String[] defaultValueStrings = new String[] {""};
				int[] dataTypes = new int[] {DataEntryDialog.STRING_TYPE};
				boolean[] disableIfNull = new boolean[] {true};
				int[] numDecimalPlaces = new int[] {0};
				String[] tooltipStrings = new String[] {""};

				DataEntryDialog dataEntryDialog = new DataEntryDialog(csdpFrame, title, instructions, names, defaultValueStrings, dataTypes,
						disableIfNull, numDecimalPlaces, tooltipStrings, true);
				int response = dataEntryDialog.getResponse();
				if(response==DataEntryDialog.OK) {
					requestedCenterlineName = dataEntryDialog.getValue(names[0]);
					_response = XsectSlideshowDialog.JUMP_TO_CENTERLINE_OPTION;
				}
			}else {
				_response = XsectSlideshowDialog.QUIT_OPTION;
			}
			dispose();
		}
	}//class ButtonListener

	public int getResponse() {return _response;}

	public String getRequestedCenterlineName() {
		return this.requestedCenterlineName;
	}

}
