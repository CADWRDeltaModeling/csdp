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
package DWR.CSDP;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Date;
import java.util.EventListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import DWR.CSDP.XsectBathymetryData;
import vista.app.CurveFactory;
import vista.app.GraphBuilderInfo;
import vista.app.MainProperties;
//import DWR.Graph.*;
//import DWR.Graph.Canvas.*;
import vista.graph.AxisAttr;
import vista.graph.Curve;
import vista.graph.CurveAttr;
import vista.graph.DefaultGraphFactory;
import vista.graph.FontResizeInteractor;
import vista.graph.GECanvas;
import vista.graph.Graph;
import vista.graph.GraphAttr;
import vista.graph.GraphFactory;
import vista.graph.Legend;
import vista.graph.LegendItem;
import vista.graph.LegendItemAttr;
import vista.graph.MultiPlot;
import vista.graph.Plot;
import vista.graph.PlotAttr;
import vista.graph.SimpleTickGenerator;
import vista.graph.Symbol;
import vista.graph.SymbolAttr;
import vista.graph.TextLine;
import vista.graph.TextLineAttr;
import vista.graph.TickGenerator;
import vista.set.DataReference;
import vista.set.DataReferenceMath;
import vista.set.DataRetrievalException;
import vista.set.DataSet;
import vista.set.DefaultReference;

/**
 * Plots data in cross-section view.
 *
 * @author
 * @version $Id:
 */
public class XsectGraph extends JDialog implements ActionListener {

	protected BathymetryPlot _plotter;
	protected NetworkPlot _networkPlotter;
	App _app;
	BathymetryData _bathymetryData;
	Network _net;
	GECanvas _gC;
	private NetworkDataSet _networkDataSet;
	private NetworkDataSet _oldNetworkDataSet;
//	public static final int DATA_LENGTH = 100;
	/*
	 * size of cross-section points
	 */
	// protected static int squareDimension = 2;
	protected static final int stationIndex = 0;
	protected static final int elevationIndex = 1;

	Xsect _xsect;
	// protected JCheckBoxMenuItem _xMovePointMenuItem, _xAddPointMenuItem,
	// _xInsertPointMenuItem, _xDeletePointMenuItem;

	URL xsCloseUrl = this.getClass().getResource("images/XSCloseButton.png");
	URL cursorIconUrl = this.getClass().getResource("images/ArrowButton.png");
	URL reverseXsectIconUrl = this.getClass().getResource("images/ReverseXsectButton.png");
	URL movePointIconUrl = this.getClass().getResource("images/MoveXsectPointButton.png");
	URL addPointIconUrl = this.getClass().getResource("images/AddXsectPointButton.png");
	URL insertPointIconUrl = this.getClass().getResource("images/InsertXsectPointButton.png");
	URL deletePointIconUrl = this.getClass().getResource("images/DeleteXsectPointButton.png");
	URL cursorIconSelectedUrl = this.getClass().getResource("images/ArrowButtonSelected.png");
	URL movePointIconSelectedUrl = this.getClass().getResource("images/MoveXsectPointButtonSelected.png");
	URL addPointIconSelectedUrl = this.getClass().getResource("images/AddXsectPointButtonSelected.png");
	URL insertPointIconSelectedUrl = this.getClass().getResource("images/InsertXsectPointButtonSelected.png");
	URL deletePointIconSelectedUrl = this.getClass().getResource("images/DeleteXsectPointButtonSelected.png");
	URL keepIconUrl = this.getClass().getResource("images/KeepButton.png");
	URL restoreIconUrl = this.getClass().getResource("images/RestoreButton.png");
	URL colorByDistanceIconUrl = this.getClass().getResource("images/ColorDistanceButton.png");
	URL colorByDistanceIconSelectedUrl = this.getClass().getResource("images/ColorDistanceButtonSelected.png");
	URL colorBySourceIconUrl = this.getClass().getResource("images/ColorSourceButton.png");
	URL colorBySourceIconSelectedUrl = this.getClass().getResource("images/ColorSourceButtonSelected.png");
	URL colorByYearIconUrl = this.getClass().getResource("images/ColorYearButton.png");
	URL colorByYearIconSelectedUrl = this.getClass().getResource("images/ColorYearButtonSelected.png");

	ImageIcon _xsCloseIcon = CsdpFunctions.createScaledImageIcon(xsCloseUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
	ImageIcon _cursorIcon = CsdpFunctions.createScaledImageIcon(cursorIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _reverseXsectIcon = CsdpFunctions.createScaledImageIcon(reverseXsectIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _movePointIcon = CsdpFunctions.createScaledImageIcon(movePointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _addPointIcon = CsdpFunctions.createScaledImageIcon(addPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _insertPointIcon = CsdpFunctions.createScaledImageIcon(insertPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _deletePointIcon = CsdpFunctions.createScaledImageIcon(deletePointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _cursorIconSelected = CsdpFunctions.createScaledImageIcon(cursorIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _movePointIconSelected = CsdpFunctions.createScaledImageIcon(movePointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _addPointIconSelected = CsdpFunctions.createScaledImageIcon(addPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _insertPointIconSelected = CsdpFunctions.createScaledImageIcon(insertPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	ImageIcon _deletePointIconSelected = CsdpFunctions.createScaledImageIcon(deletePointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);

	ImageIcon _keepIcon = CsdpFunctions.createScaledImageIcon(keepIconUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
	ImageIcon _restoreIcon = CsdpFunctions.createScaledImageIcon(restoreIconUrl, (int)(1.3*WIDE_ICON_WIDTH), WIDE_ICON_HEIGHT);

	ImageIcon _colorByDistanceIcon = CsdpFunctions.createScaledImageIcon(colorByDistanceIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	ImageIcon _colorByDistanceIconSelected = CsdpFunctions.createScaledImageIcon(colorByDistanceIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	ImageIcon _colorBySourceIcon = CsdpFunctions.createScaledImageIcon(colorBySourceIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	ImageIcon _colorBySourceIconSelected = CsdpFunctions.createScaledImageIcon(colorBySourceIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	ImageIcon _colorByYearIcon = CsdpFunctions.createScaledImageIcon(colorByYearIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	ImageIcon _colorByYearIconSelected = CsdpFunctions.createScaledImageIcon(colorByYearIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);

	JRadioButton _colorByDistanceButton = new JRadioButton(_colorByDistanceIcon);
	JRadioButton _colorBySourceButton = new JRadioButton(_colorBySourceIcon);
	JRadioButton _colorByYearButton = new JRadioButton(_colorByYearIcon);

	JButton _xsCloseButton = new JButton(_xsCloseIcon);
	JButton _reverseButton = new JButton(_reverseXsectIcon);
	JRadioButton _arrowButton = new JRadioButton(_cursorIcon);
	JRadioButton _moveButton = new JRadioButton(_movePointIcon);
	JRadioButton _addButton = new JRadioButton(_addPointIcon);
	JRadioButton _insertButton = new JRadioButton(_insertPointIcon);
	JRadioButton _deleteButton = new JRadioButton(_deletePointIcon);

	JButton _restoreButton = new JButton(_restoreIcon);
	JButton _keepButton = new JButton(_keepIcon);
	// JButton _metadataButton = new JButton(_metadataIcon);

	ButtonGroup _colorByButtonGroup, _xsectEditButtonGroup;

//	private static final Dimension _iconSize = new Dimension(25, 25);
//	private static final Dimension _medIconSize = new Dimension(33, 25);
//	private static final Dimension _wideIconSize = new Dimension(50, 25);

	private static final int ICON_WIDTH = 38;
	private static final int ICON_HEIGHT = 38;
	private static final int COLOR_BY_ICON_WIDTH = 53;
	private static final int COLOR_BY_ICON_HEIGHT = 23;
	private static final int WIDE_ICON_WIDTH = 60;
	private static final int WIDE_ICON_HEIGHT = 38;
	protected boolean _restoreXsect = false;
	protected double _thickness;
	protected static final boolean DEBUG = false;

	protected Hashtable _bathymetryDataSets = new Hashtable();
	protected int _numBathymetryDataSets = 0;
	// protected boolean _colorByDistance = false;
	// protected boolean _colorBySource = false;
	// protected boolean _colorByYear = true;
	protected ResizableStringArray _bathymetryDataSetNames = new ResizableStringArray();

	JPanel _eastPanel = new JPanel();
	JPanel _xsPropPanel = new JPanel();
	JPanel _dConveyancePanel = new JPanel();
	JPanel _inputPanel = new JPanel();
	JButton _elevationButton = new JButton("Change elevation");
	JLabel _numPointsLabel = new JLabel();
	JLabel _elevationLabel = new JLabel();
	JLabel _widthLabel = new JLabel();
	JLabel _areaLabel = new JLabel();
	JLabel _wetpLabel = new JLabel();
	JLabel _hDepthLabel = new JLabel();

	JLabel _numPointsValueLabel = new JLabel();
	JLabel _elevationValueLabel = new JLabel();
	JLabel _widthValueLabel = new JLabel();
	JLabel _wetpValueLabel = new JLabel();
	JLabel _areaValueLabel = new JLabel();
	JLabel _hDepthValueLabel = new JLabel();

	// JLabel _dkLabel = new JLabel();
//	JEditorPane _dConveyanceEditorPane = new JEditorPane();
//	JTextArea _dConveyanceTextArea = new JTextArea();
	JTextPane _dConveyanceTextPane = new JTextPane();
	JTextArea _conveyanceCharacteristicsTextArea = new JTextArea();
	
	// JCheckBoxMenuItem _bColorByDistance, _bColorBySource, _bColorByYear;
	JMenuItem _bChangePointSize;
	String _centerlineName = null;
	int _xsectNum;
	Legend _legend;
	CsdpFrame _gui;
	public int COLOR_BY_DISTANCE = 0;
	public int COLOR_BY_YEAR = 1;
	public int COLOR_BY_SOURCE = 2;
	protected boolean _changesKept = false;
	Border _raisedBevel = BorderFactory.createRaisedBevelBorder();
	Border _lineBorder = BorderFactory.createLineBorder(Color.black, 2);

	// all new
	public JTextArea _metadataJTextArea;
//	private ResizableStringArray _metadataMessage;
	private JScrollPane _metadataScrollPane;

	// DefaultGraphBuilder _dgb = new DefaultGraphBuilder();
	Graph _graph;
	private GraphFactory _factory = new DefaultGraphFactory();
	private MultiPlot _multiPlot;
	private DataReference[] _refs;
	GraphBuilderInfo _info;

	private JTextField _moveXsectXField = new JTextField("0", 4);
	private JTextField _moveXsectYField = new JTextField("0", 4);
	private JButton _moveXsectXButton = new JButton("dX");
	private JButton _moveXsectYButton = new JButton("dY");

	/*
	 * When color by distance used, this is the maximum number of bins.
	 */
	private final int NUM_DISTANCE_BINS = 10;

	private double _xsPropElevation;
	private XsectBathymetryData _xsectBathymetryData;

	public XsectGraph(CsdpFrame gui, App app, BathymetryData data, XsectBathymetryData xsectBathymetryData, 
			Network net, String centerlineName, int xsectNum, double thickness, int colorOption) {
		super(gui, "Cross-section view", ModalityType.MODELESS);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setXSPropElevation(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
		_gui = gui;
		_app = app;
		_bathymetryData = data;
		_xsectBathymetryData = xsectBathymetryData;
		_net = net;
		Centerline centerline = _net.getCenterline(centerlineName);
		_xsect = centerline.getXsect(xsectNum);
		_thickness = thickness;
		_centerlineName = centerlineName;
		_xsectNum = xsectNum;

		// //see if there are any upstream or downstream xs, so centerline pts
		// can be displayed in grey.
		// Pattern p = Pattern.compile("_");
		// String[] cnameParts = p.split(centerlineName);
		// String xsNumString = cnameParts[1];
		// int numXS = Integer.parseInt(xsNumString);
		// boolean hasUpstrXS = false;
		// boolean hasDownstrXS = false;
		// if(xsNumString>0) hasUpstrXS = true;
		// int numXS = centerline.getNumXsects();
		// if(xsNum <= numXS-1) hasDownstrXS = true;

		// use this for Frame
		// setLayout(new BorderLayout());
		// use this instead for JFrame

		getContentPane().setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1200, 900));
		// setBackground(Color.gray);
		CsdpFileMetadata metadata = CsdpFunctions.getBathymetryMetadata();
		metadata.getVDatumString();
//		_numPointsLabel.setText("num points");
//		_elevationLabel.setText("Elevation, ft ("+metadata.getVDatumString());
//		_widthLabel.setText("Width, ft");
//		_areaLabel.setText("Area, square ft");
//		_wetpLabel.setText("Wetted Perimeter, ft");
//		_hDepthLabel.setText("HydraulicDepth, ft");

		
		_xsPropPanel.setLayout(new BoxLayout(_xsPropPanel, BoxLayout.Y_AXIS));
		_xsPropPanel.setBorder(_lineBorder);
		_xsPropPanel.add(_elevationButton);
		_xsPropPanel.add(_conveyanceCharacteristicsTextArea);
		
//		_xsPropPanel.add(new JLabel());
//		_xsPropPanel.add(_numPointsLabel);
//		_xsPropPanel.add(_numPointsValueLabel);
//		_xsPropPanel.add(_elevationLabel);
//		_xsPropPanel.add(_elevationValueLabel);
//		_xsPropPanel.add(_widthLabel);
//		_xsPropPanel.add(_widthValueLabel);
//		_xsPropPanel.add(_wetpLabel);
//		_xsPropPanel.add(_wetpValueLabel);
//		_xsPropPanel.add(_areaLabel);
//		_xsPropPanel.add(_areaValueLabel);
//		_xsPropPanel.add(_hDepthLabel);
//		_xsPropPanel.add(_hDepthValueLabel);

		//using a JEditorPane will make -dK red; but not laying out correctly yet
//		_dConveyanceEditorPane.setEditable(false);
//		_dConveyanceEditorPane.setContentType("text/html");
//		_dConveyancePanel.setLayout(new BoxLayout(_dConveyancePanel, BoxLayout.Y_AXIS));
//		_dConveyancePanel.setBorder(_lineBorder);
//		_dConveyancePanel.add(_dConveyanceEditorPane);
		
//		GridBagLayout dConveyancePanelLayout = new GridBagLayout();
//		GridBagConstraints dConveyancePanelGridBagConstraints = new GridBagConstraints();
//		dConveyancePanelGridBagConstraints.gridx=0;
//		dConveyancePanelGridBagConstraints.gridy=0;
//		dConveyancePanelGridBagConstraints.fill=GridBagConstraints.VERTICAL;
		_dConveyancePanel.setLayout(new BoxLayout(_dConveyancePanel, BoxLayout.Y_AXIS));
//		_dConveyancePanel.setLayout(dConveyancePanelLayout);
//		_dConveyancePanel.setBorder(_lineBorder);

		JScrollPane dConveyanceScrollpane = new JScrollPane(_dConveyanceTextPane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_dConveyancePanel.add(dConveyanceScrollpane);
//		_dConveyancePanel.add(_dConveyanceEditorPane, dConveyancePanelGridBagConstraints);
		
		// _xsPropPanel.add(_dkLabel);

		// use this for Frame
		// add("South",_xsPropPanel);
		// use this instead for JFrame

		_metadataJTextArea = new JTextArea(_xsect.getMetadata());
//		_metadataScrollPane = new JScrollPane(_metadata, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		_metadataJTextArea.setLineWrap(true);
		_metadataJTextArea.setWrapStyleWord(true);
		_metadataScrollPane = new JScrollPane(_metadataJTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_metadataScrollPane.setPreferredSize(new Dimension(300, 450));
		_eastPanel.setLayout(new BoxLayout(_eastPanel, BoxLayout.Y_AXIS));
		_eastPanel.add(_xsPropPanel);

		_eastPanel.add(_dConveyancePanel);
		
		URL metadataIconUrl = this.getClass().getResource("images/MetadataIcon.png");
//		ImageIcon _metadataIcon = new ImageIcon(metadataIconUrl);
		ImageIcon _metadataIcon = CsdpFunctions.createScaledImageIcon(metadataIconUrl, WIDE_ICON_WIDTH*4, WIDE_ICON_HEIGHT);
		JLabel _metadataLabel = new JLabel(_metadataIcon, SwingConstants.LEFT);

		_eastPanel.add(_metadataLabel);
		_eastPanel.add(_metadataScrollPane);
		getContentPane().add("East", _eastPanel);

		//// _inputPanel.add("South", _xsPropPanel);
		//// add("South", _inputPanel);

		updateXsectProp();

		JPanel btnPanel = new JPanel();
//		_xsCloseButton.setPreferredSize(_wideIconSize);
//		_reverseButton.setPreferredSize(_iconSize);
//		_arrowButton.setPreferredSize(_iconSize);
//		_moveButton.setPreferredSize(_iconSize);
//		_addButton.setPreferredSize(_iconSize);
//		_insertButton.setPreferredSize(_iconSize);
//		_deleteButton.setPreferredSize(_iconSize);
//		_keepButton.setPreferredSize(_medIconSize);
//		_restoreButton.setPreferredSize(_wideIconSize);
		// _metadataButton.setPreferredSize(_medIconSize);

		btnPanel.setLayout(new FlowLayout());
		JPanel colorByPanel = new JPanel(new GridLayout(2, 2));
		colorByPanel.setBorder(_raisedBevel);
		_colorByButtonGroup = new ButtonGroup();
		_colorByDistanceButton.setToolTipText("color points by dist. from xs line");
		_colorByYearButton.setToolTipText("color points by year");
		_colorBySourceButton.setToolTipText("color points by source");
		if (colorOption == COLOR_BY_DISTANCE)
			_colorByDistanceButton.setSelected(true);
		else if (colorOption == COLOR_BY_YEAR)
			_colorByYearButton.setSelected(true);
		else if (colorOption == COLOR_BY_SOURCE)
			_colorBySourceButton.setSelected(true);
		_colorByButtonGroup.add(_colorByDistanceButton);
		_colorByButtonGroup.add(_colorBySourceButton);
		_colorByButtonGroup.add(_colorByYearButton);
		_colorByDistanceButton.setSelectedIcon(_colorByDistanceIconSelected);
		_colorBySourceButton.setSelectedIcon(_colorBySourceIconSelected);
		_colorByYearButton.setSelectedIcon(_colorByYearIconSelected);
		colorByPanel.add(_colorByDistanceButton);
		colorByPanel.add(_colorBySourceButton);
		colorByPanel.add(_colorByYearButton);
		btnPanel.add(_xsCloseButton);
		btnPanel.add(colorByPanel);
		btnPanel.add(_reverseButton);
		btnPanel.add(_arrowButton);
		btnPanel.add(_moveButton);
		btnPanel.add(_addButton);
		btnPanel.add(_insertButton);
		btnPanel.add(_deleteButton);
		btnPanel.add(_keepButton);
		btnPanel.add(_restoreButton);

		btnPanel.add(_moveXsectXField);
		btnPanel.add(_moveXsectXButton);
		btnPanel.add(_moveXsectYField);
		btnPanel.add(_moveXsectYButton);

		// btnPanel.add(_metadataButton);
		_arrowButton.setSelectedIcon(_cursorIconSelected);
		_moveButton.setSelectedIcon(_movePointIconSelected);
		_addButton.setSelectedIcon(_addPointIconSelected);
		_insertButton.setSelectedIcon(_insertPointIconSelected);
		_deleteButton.setSelectedIcon(_deletePointIconSelected);
		_xsectEditButtonGroup = new ButtonGroup();
		_xsectEditButtonGroup.add(_arrowButton);
		_xsectEditButtonGroup.add(_moveButton);
		_xsectEditButtonGroup.add(_addButton);
		_xsectEditButtonGroup.add(_insertButton);
		_xsectEditButtonGroup.add(_deleteButton);

		// use this for Frame
		// add("North", btnPanel);
		// use this instead for JFrame
		getContentPane().add("North", btnPanel);

		_xsCloseButton.setToolTipText("close cross-section");
		_reverseButton.setToolTipText("reverse cross-section drawing  ");
		_arrowButton.setToolTipText("turn off edit mode");
		_moveButton.setToolTipText("move point  ");
		_addButton.setToolTipText("add point  ");
		_insertButton.setToolTipText("insert point  ");
		_deleteButton.setToolTipText("delete point  ");
		_restoreButton.setToolTipText("undo changes since last keep  ");
		_keepButton.setToolTipText("store changes in memory (not on disk!) ");
		_moveXsectXButton.setToolTipText("move cross-section in x dir.");
		_moveXsectYButton.setToolTipText("move cross-section in y dir.");
		// _metadataButton.setToolTipText("view/edit metadata ");
		_elevationButton.setToolTipText("change elevation for conveyance characteristics  ");

		createMenus();
		// setColorByDistance();

		setCursor(CsdpFunctions._waitCursor);

		// some of this is repeated in updateGraphCanvas but it is
		// necessary to do it here to create the oldNetworkDataSet
		// using any exisiting points in the network.
		makeNetworkDataSet();
		double[] x;
		double[] y;
		if (_networkDataSet != null) {
			x = _networkDataSet.getXArray();
			y = _networkDataSet.getYArray();
			if (_oldNetworkDataSet == null)
				_oldNetworkDataSet = new NetworkDataSet("old", x, y);
		}

		updateGraphCanvas();

		Plot plot = _graph.getPlot();
		if(plot!=null) {
			vista.graph.Axis bottomAxis = plot.getAxis(AxisAttr.BOTTOM);
			vista.graph.Axis leftAxis = plot.getAxis(AxisAttr.LEFT);
			if(bottomAxis!=null && leftAxis!=null) {
				bottomAxis.setAxisLabel("Distance Along Cross-Section Line, feet");
				leftAxis.setAxisLabel("Elevation), ft");
				bottomAxis.setPlotAxisLabel(true);
				leftAxis.setPlotAxisLabel(true);
				// Plot plot = _graph.getPlot();
				// /*
				// * The following code copied from updateGraphCanvas
				// */
				// plot.getAxis(AxisAttr.BOTTOM).setAxisLabel("Distance Along
				// Cross-Section Line, feet");
				// plot.getAxis(AxisAttr.LEFT).setAxisLabel("Elevation(NGVD), ft");
		
				// TickGenerator tg = new SimpleTickGenerator();
		
				// plot.getAxis(AxisAttr.BOTTOM).setTickGenerator(tg);
				// plot.getAxis(AxisAttr.LEFT).setTickGenerator(tg);
		
				// AxisAttr aa =
				// (AxisAttr)plot.getAxis(AxisAttr.BOTTOM).getAttributes();
				// aa._tickLocation = AxisAttr.INSIDE;
				// aa = (AxisAttr) plot.getAxis(AxisAttr.LEFT).getAttributes();
				// aa._tickLocation = AxisAttr.BOTH;
		
				// _graph.addGrid();
		
				// //use this for Frame
				// // add("Center", _gC);
				// //use instead for JFrame
				// getContentPane().add("Center", _gC);
				// //this doesn't work right in swing--it just hides the window
				// // enableEvents(WindowEvent.WINDOW_CLOSING);
				updateDisplay();
			}
			this.dispose();
			_xsCloseButton.doClick();
		}
		setCursor(CsdpFunctions._defaultCursor);
	}// constructor

	private void printGraphInfo(String a) {

		try {
			System.out.println("printing graph info. " + a);

			System.out.println(_graph);
			System.out.println(_graph.getPlot());
			System.out.println(_graph.getPlot().getAttributes());
		} catch (java.lang.NullPointerException e) {
			// System.out.println("NULL VALUE!!!");
		}
	}

	/**
	 * updates graph in xsect window
	 */
	public void updateGraphCanvas() {

		if (DEBUG)
			printGraphInfo("beginning of updateGraphCanvas");

		if (_gC != null)
			remove(_gC);
		// make bathymetry data set for plotting
		makeBathymetryDataSets();

		if (DEBUG)
			printGraphInfo("1");

		// make network data set for plotting
		makeNetworkDataSet();
		double[] x;
		double[] y;
		if (_networkDataSet != null) {
			x = _networkDataSet.getXArray();
			y = _networkDataSet.getYArray();
			///// if(_oldNetworkDataSet == null)_oldNetworkDataSet = new
			///// NetworkDataSet("old",x,y);
		}

		/*
		 * make new graph...Most of this code was copied from
		 * DefaultGraphBuilder
		 */

		_graph = _factory.createGraph();
		_multiPlot = _factory.createMultiPlot();
		_graph.addPlot(_multiPlot);
		_multiPlot.add(_factory.createPlot());
		_graph.setBackgroundColor(Color.lightGray);
		_multiPlot.setCurrentPlot(0);
		TextLineAttr tla = new TextLineAttr();
		tla._font = new Font("Times Roman", Font.PLAIN, 16);
		tla._foregroundColor = Color.blue;
		((GraphAttr) (_graph.getAttributes())).setTitleAttributes(tla);
		_graph.setTitle(
				"Cross-section " + _centerlineName + "_" + _xsectNum + "," + " thickness=" + _thickness + " ft.");

		// calculate number of bathymetry data sets. Add 1 if there is a network
		// data set.
		int ncurves = 0;
		if (_networkDataSet != null) {
			ncurves++;
		}
		ncurves += _numBathymetryDataSets;

		_graph.setInsets(new java.awt.Insets(5, 20, 10, 25));
		Font legendFont = new Font("Arial", Font.PLAIN, 14);
		Legend legend = _factory.createLegend();
		_graph.setLegend(legend);

		Plot plot = _graph.getPlot();
		plot.setInsets(new java.awt.Insets(20, 5, 20, 50));

		// now add dataReferences
		//the _refs array contains DefaultReference objects, each containing a bathymetry data set, with the last being a network data set
		//the network data set will always be black points connected by black lines, and will be editable.
		_refs = new DataReference[ncurves];
		for (int i = 0; i <= _numBathymetryDataSets - 1; i++) {
			_refs[i] = new DefaultReference((NetworkDataSet) _bathymetryDataSets.get(_bathymetryDataSetNames.get(i)));
		}

		if (_networkDataSet != null) {
			_refs[_numBathymetryDataSets] = new DefaultReference(_networkDataSet);
		}

		//if _refs is empty at this point, create a dummy network data set that will enable cross-section drawing where there are no data.
		if(_refs.length==0) {
			_refs = new DataReference[1];
			makeDummyNetworkDataSet();
			_refs[0]= new DefaultReference((NetworkDataSet)_networkDataSet); 
		}
		
		_info = new GraphBuilderInfo(_refs, MainProperties.getProperties());
		
		for (int i = 0; i < _refs.length; i++) {
			int xPos = _info.getXAxisPosition(_refs[i]);
			int yPos = _info.getYAxisPosition(_refs[i]);
			try {
				DataSet ds = _refs[i].getData();
				Curve crv = CurveFactory.createCurve(_refs[i], xPos, yPos, _info.getLegendLabel(_refs[i]));
				plot.addCurve(crv);
				if (_networkDataSet != null && i == _refs.length - 1) {
					CurveAttr c = (CurveAttr) crv.getAttributes();
					c.setDrawSymbol(true);
					c.setDrawLines(true);
					SymbolAttr networkSymbolAttr = new SymbolAttr();
					int ps = 4;
					int[] networkSymbolX = { -ps, ps, ps, -ps };
					int[] networkSymbolY = { ps, ps, -ps, -ps };
					networkSymbolAttr.setIsFilled(true);
					networkSymbolAttr.setSymbol(networkSymbolX, networkSymbolY, 4);
					c.setForegroundColor(Color.black);
					Symbol networkSymbol = new Symbol(networkSymbolAttr);
					crv.setSymbol(networkSymbol);
				} else {
					setBathymetryCurveAttributes(i);
				}
				LegendItem li = _factory.createLegendItem();
				// li.setLegendName( _info.getLegendLabel(_refs[i]) );

				try {
					li.setLegendName(_bathymetryDataSetNames.get(i));
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("Exception caught in XsectGraph.updateGraphCanvas: " + e);
				}
				if (_networkDataSet != null && i == _refs.length - 1) {
					li.setLegendName("cross-section points");
				}
				li.setCurve(crv);
				LegendItemAttr lia = (LegendItemAttr) li.getAttributes();
				legend.add(li);
				lia._foregroundColor = Color.black;
				lia.setFont(legendFont);
			} catch (DataRetrievalException dre) {
				dre.printStackTrace();
			}
		}

		// Scale components in layout resizing: Template file
		// GEBorderLayout plotLayout = (GEBorderLayout) plot.getLayout();
		// plotLayout.setScaleComponents(true);
		TextLineAttr dateAttr = new TextLineAttr();
		dateAttr._font = new java.awt.Font("Times Roman", java.awt.Font.PLAIN, 10);
		dateAttr._foregroundColor = java.awt.Color.red;
		dateAttr._resizeProportionally = true;
		dateAttr._justification = TextLineAttr.RIGHT;
		_graph.getLegend().add(new TextLine(dateAttr, new Date().toString()));
		/*
		 * End of code copied from CsdpGraphBuilder
		 */

		// _gC = new GraphCanvas();
		_gC = new GECanvas(_graph);

		_graph.setInsets(new Insets(5, 5, 5, 5));
		// add editing, listeners

		if (DEBUG)
			printGraphInfo("2");

		XsectEditInteractor xei = new XsectEditInteractor(this, _xsect, _gC, _graph);
		_gC.addMouseListener(xei);
		_gC.addMouseMotionListener(xei);
		_gC.addComponentListener(new FontResizeInteractor(_gC));

		if (DEBUG)
			printGraphInfo("3");

		PlotAttr pattr = (PlotAttr) plot.getAttributes();

		TickGenerator tg = new SimpleTickGenerator();

		plot.getAxis(AxisAttr.BOTTOM).setTickGenerator(tg);
		plot.getAxis(AxisAttr.LEFT).setTickGenerator(tg);

		AxisAttr aa = (AxisAttr) plot.getAxis(AxisAttr.BOTTOM).getAttributes();
		aa._tickLocation = AxisAttr.INSIDE;
		aa = (AxisAttr) plot.getAxis(AxisAttr.LEFT).getAttributes();
		aa._tickLocation = AxisAttr.BOTH;
		_graph.addGrid();

		// use this for Frame
		// add("Center", _gC);
		// use instead for JFrame
		getContentPane().add("Center", _gC);
		// this doesn't work right in swing--it just hides the window
		// enableEvents(WindowEvent.WINDOW_CLOSING);

	}// updateGraphCanvas

	/**
	 * was paint(); changed to paintComponent() for conversion to swing.
	 */
	public void paintComponents(Graphics ggg) {
		super.paintComponents(ggg);
	}

	public void actionPerformed(ActionEvent e) {
		// Component obj = (Component) e.getSource();
		// if(obj instanceof Button){
		// Button b = (Button) obj;
		// String label = b.getLabel();
		// if(label.equals("Print")){
		// doPrint();
		// }
		// }//if obj
	}// actionPerformed

	// public void processEvent(AWTEvent evt){
	// if(evt instanceof WindowEvent){
	// if(evt.getID() == WindowEvent.WINDOW_CLOSING){
	// System.exit(0);
	// }
	// }
	// }//processEvent

	/**
	 * sets value of isUpdated. Used to tell if xsect has changed.
	 */
	public void setIsUpdated(boolean b) {
		_xsect.setIsUpdated(b);
	}

	/**
	 * returns GECanvas (used to be GraphCanvas) object
	 */
	public GECanvas getGC() {
		return _gC;
	}

	// /**
	// * puts squares at cross-section points (network data)
	// */
	// protected void setNetworkCurveAttributes(){
	// Curve networkCurve = _gC.getPlot().getCurve(_networkDataSet);
	// CurveAttr c = (CurveAttr)networkCurve.getAttributes();
	// c.setDrawSymbol(true);
	// SymbolAttr networkSymbolAttr = new SymbolAttr();
	// int ps = getPointSize();
	// int[] networkSymbolX = {-ps,ps,ps,-ps};
	// int[] networkSymbolY = {ps,ps,-ps,-ps};
	// networkSymbolAttr.setIsFilled(true);
	// networkSymbolAttr.setSymbol(networkSymbolX, networkSymbolY, 4);
	// c._color = Color.black;
	// Symbol networkSymbol = new Symbol(networkSymbolAttr);
	// networkCurve.setSymbol(networkSymbol);

	// setLegendTextBlack(_numBathymetryDataSets);
	// }//setNetworkCurveAttributes

	// /**
	// * sets color of legend text to black
	// */
	// protected void setLegendTextBlack(int index){
	// LegendItem litem = (LegendItem)_legend.getElement(index);
	// LegendItemAttr liattr = (LegendItemAttr)litem.getAttributes();
	// TextLineAttr tlattr = (TextLineAttr)liattr;
	// // tlattr.setBackgroundColor(Color.black);
	// tlattr.setForegroundColor(Color.black);
	// }//setLegendTextBlack

	/**
	 * sets attributes for plotting bathymetry
	 */
	protected void setBathymetryCurveAttributes(int i) {
		NetworkDataSet dataSet = (NetworkDataSet) (_bathymetryDataSets.get(_bathymetryDataSetNames.get(i)));
		// Curve bathymetryCurve = _gC.getPlot().getCurve(dataSet);
		Curve bathymetryCurve = _graph.getPlot().getCurve(i);
		CurveAttr c = (CurveAttr) bathymetryCurve.getAttributes();

		// removed this line because of classCastException. Is it necessary?
		// ((LineElementAttr)(bathymetryCurve.getAttributes()))._backgroundColor
		// = _gui.getColor(i);

		// _legend.getAttributes().setForegroundColor(Color.black);

		// setLegendTextBlack(i);

		c.setDrawSymbol(true);
		c.setDrawLines(false);
		int symbolDim = 0;
		double symbolDimf = 0.0;
		if (getColorByDistanceMode()) {

			double sd = (double) getPointSize();
			double nds = (double) _numBathymetryDataSets;
			double fi = (double) i;
//			symbolDimf = (sd / 2.0 * ((nds - fi) / 4.0f)) + 1.0;
			//reversing order: make symbol for lower values smaller.
			symbolDimf = (sd / 2.0 * (fi / 4.0f)) + 1.0;
			// symbolDim = (getPointSize()/2 *
			// ((_numBathymetryDataSets-i)/4))+1;
			symbolDim = (int) symbolDimf;
			if (DEBUG)
				System.out.println("symbol size set.  i, getPointSize(), symbolDimf, symbolDim=" + i + ","
						+ getPointSize() + "," + symbolDimf + "," + symbolDim);
		} else {
			symbolDim = getPointSize() / 2;
		}
		SymbolAttr bathymetrySymbolAttr = new SymbolAttr();
		int[] bathymetrySymbolX = { -symbolDim, symbolDim, symbolDim, -symbolDim };
		int[] bathymetrySymbolY = { symbolDim, symbolDim, -symbolDim, -symbolDim };
		bathymetrySymbolAttr.setIsFilled(true);
		bathymetrySymbolAttr.setSymbol(bathymetrySymbolX, bathymetrySymbolY, 4);
		//to reverse order of colors
		Color bathymetryColor = _gui.getColor(_numBathymetryDataSets-(i+1));
		if (DEBUG)
			System.out.println("setting attributes for _bathymetryDataSetNames.get(i): color=" + bathymetryColor);

		bathymetrySymbolAttr._foregroundColor = bathymetryColor;
		c._foregroundColor = bathymetryColor;
		Symbol bathymetrySymbol = new Symbol(bathymetrySymbolAttr);
		bathymetryCurve.setSymbol(bathymetrySymbol);
	}// setBathymetryCurveAttributes

	/**
	 * Change the size of points in cross-section window
	 */
	public void setPointSize(int size) {
		if (size < 2) {
			System.out.println("Size is too small");
		} else {
			_app.setSquareDimension(size);
			// squareDimension = size;
		}
	}// setPointSize

	public int getPointSize() {
		return _app.getSquareDimension();
	}

	public void createMenus() {
		JMenuBar menubar;

		JMenu xgXsect, xgBathymetry, xgEdit;
		JMenuItem xReverse, xKeep, xRestore, xPrint, xSaveToImage, xClose;
		// , xMetadata;
		menubar = new JMenuBar();
		this.setJMenuBar(menubar);

		/*
		 * Xsect menu
		 */
		xgXsect = new JMenu("Xsect");
		// xgXsect.add(xReverse = new JMenuItem("Reverse Xsect"));
		// xReverse.setAccelerator
		// (KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		// xgXsect.add(xKeep = new JMenuItem("Keep Xsect"));
		// xKeep.setAccelerator
		// (KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK));
		// xgXsect.add(xRestore = new JMenuItem("Restore Xsect"));
		// xRestore.setAccelerator
		// (KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		xgXsect.add(xSaveToImage = new JMenuItem("Save to Image file"));
//		xgXsect.add(xPrint = new JMenuItem("Print Xsect"));
		// xgXsect.add(xMetadata = new JMenuItem("Metadata"));
		xgXsect.add(xClose = new JMenuItem("Close"));
		xClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		menubar.add(xgXsect);

		/*
		 * bathymetry menu
		 */
		xgBathymetry = new JMenu("Bathymetry");
		JMenu cb = new JMenu("Color By");
		xgBathymetry.add(_bChangePointSize = new JMenuItem("Change Point Size"));
		// cb.add(_bColorByDistance = new JCheckBoxMenuItem("distance"));
		// cb.add(_bColorBySource = new JCheckBoxMenuItem("source"));
		// cb.add(_bColorByYear = new JCheckBoxMenuItem("year"));
		xgBathymetry.add(cb);
		menubar.add(xgBathymetry);

		// create and register listeners for bathymetry menu
		XsectBathymetryMenu xsectBathymetryMenu = new XsectBathymetryMenu(this);
		ItemListener bColorByDistanceListener = xsectBathymetryMenu.new XColorByDistance();
		ItemListener bColorBySourceListener = xsectBathymetryMenu.new XColorBySource();
		ItemListener bColorByYearListener = xsectBathymetryMenu.new XColorByYear();
		ActionListener bChangePointSizeListener = xsectBathymetryMenu.new BChangePointSize();
		_colorByDistanceButton.addItemListener(bColorByDistanceListener);
		_colorBySourceButton.addItemListener(bColorBySourceListener);
		_colorByYearButton.addItemListener(bColorByYearListener);
		_bChangePointSize.addActionListener(bChangePointSizeListener);

		/*
		 * Edit menu
		 */
		xgEdit = new JMenu("Edit");
		// xgEdit.add(_xMovePointMenuItem = new JCheckBoxMenuItem("Move Point",
		// false));
		// xgEdit.add(_xAddPointMenuItem = new JCheckBoxMenuItem("Add Point",
		// false));
		// xgEdit.add(_xInsertPointMenuItem = new JCheckBoxMenuItem("Insert
		// Point", false));
		// xgEdit.add(_xDeletePointMenuItem = new JCheckBoxMenuItem("Delete
		// Point", false));
		//// menubar.add(xgEdit);

		// create and register action listener objects for the Xsect menu items
		// and for the buttons
		XsectEditMenu xsectEditMenu = new XsectEditMenu(this, _net, _app);
		ActionListener xReverseListener = xsectEditMenu.new XReverse(_xsect);
		ActionListener xKeepListener = xsectEditMenu.new XKeep();
		ActionListener xRestoreListener = xsectEditMenu.new XRestore();
		ActionListener xChangeElevationListener = xsectEditMenu.new XChangeElevation();
		ActionListener xSaveToImageListener = xsectEditMenu.new XSaveToImage(this);
		ActionListener xPrintListener = xsectEditMenu.new XPrint(this);
		EventListener xCloseListener = xsectEditMenu.new XClose();
		ItemListener xMovePointListener = xsectEditMenu.new XMovePoint();
		ItemListener xAddPointListener = xsectEditMenu.new XAddPoint();
		ItemListener xInsertPointListener = xsectEditMenu.new XInsertPoint();
		ItemListener xDeletePointListener = xsectEditMenu.new XDeletePoint();
		ItemListener xStopEditListener = xsectEditMenu.new XStopEdit();
		ActionListener xMoveXsectXListener = xsectEditMenu.new XMoveXsectX(_moveXsectXField, _xsect);
		ActionListener xMoveXsectYListener = xsectEditMenu.new XMoveXsectY(_moveXsectYField, _xsect);

		DocumentListener xMetadataListener =
				// xsectEditMenu.new XMetadata(_centerlineName, _xsectNum,
				// _xsect,_gui);
				xsectEditMenu.new XMetadata();
		_metadataJTextArea.getDocument().addDocumentListener(xMetadataListener);

		// xReverse.addActionListener(xReverseListener);
		// xKeep.addActionListener(xKeepListener);
		// xRestore.addActionListener(xRestoreListener);
		_elevationButton.addActionListener(xChangeElevationListener);
		xSaveToImage.addActionListener(xSaveToImageListener);
		//		xPrint.addActionListener(xPrintListener);
		xClose.addActionListener((ActionListener) xCloseListener);

		addWindowListener((WindowListener) xCloseListener);
		_xsCloseButton.addActionListener((ActionListener) xCloseListener);

		_moveButton.addItemListener(xMovePointListener);
		_addButton.addItemListener(xAddPointListener);
		_insertButton.addItemListener(xInsertPointListener);
		_deleteButton.addItemListener(xDeletePointListener);
		_arrowButton.addItemListener(xStopEditListener);

		_moveButton.addItemListener(xMovePointListener);
		_addButton.addItemListener(xAddPointListener);
		_insertButton.addItemListener(xInsertPointListener);
		_deleteButton.addItemListener(xDeletePointListener);
		_arrowButton.addItemListener(xStopEditListener);

		_reverseButton.addActionListener(xReverseListener);
		_restoreButton.addActionListener(xRestoreListener);
		_keepButton.addActionListener(xKeepListener);

		_moveXsectXButton.addActionListener(xMoveXsectXListener);
		_moveXsectYButton.addActionListener(xMoveXsectYListener);
		// _metadataButton.addActionListener(xMetadataListener);

	}// createMenus

	/**
	 * update displayed network data
	 */
	public void updateNetworkDataSet() {
		int numPoints = _xsect.getNumPoints();
		double[] xArray;
		double[] yArray;
		double networkMaxX = 0.0;
		double networkMaxY = 0.0;
		double networkMinX = 0.0;
		double networkMinY = 0.0;
		double bathymetryMaxX = 0.0;
		double bathymetryMaxY = 0.0;
		double bathymetryMinX = 0.0;
		double bathymetryMinY = 0.0;
		double maxX = 0.0;
		double maxY = 0.0;
		double minX = 0.0;
		double minY = 0.0;

		if (DEBUG)
			System.out.println("arraylength, numpoints=" + _networkDataSet.size() + "," + numPoints);
		// Curve networkCurve = _graph.getPlot().getCurve(_networkDataSet);
		makeNetworkDataSet();
		// // // Curve networkCurve =
		// _graph.getPlot().getCurve(_numBathymetryDataSets);
		// // // _refs[_numBathymetryDataSets] = new
		// DefaultReference(_networkDataSet);
		// // // networkCurve =
		// CurveFactory.createCurve(_refs[_numBathymetryDataSets], xPos, yPos,
		// "label");
		// if(networkCurve != null){
		// networkCurve.setDataSet( _networkDataSet );
		// }
		// must use getX and getY before calling next getMinimum or getMaximum
		// because values
		// not saved.
		// if(_networkDataSet.getMinimum() != null &&
		// _networkDataSet.getMaximum() != null &&
		// _bathymetryDataSet.getMinimum() != null &&
		// _bathymetryDataSet.getMaximum() != null){

		if (_networkDataSet != null && _numBathymetryDataSets > 0) {
			if (_networkDataSet != null) {
				networkMaxX = _networkDataSet.getMaxX();
				networkMaxY = _networkDataSet.getMaxY();
				networkMinX = _networkDataSet.getMinX();
				networkMinY = _networkDataSet.getMinY();
			} else {
				networkMaxX = -Double.MAX_VALUE;
				networkMaxY = -Double.MAX_VALUE;
				networkMinX = Double.MAX_VALUE;
				networkMinY = Double.MAX_VALUE;
			}
			NetworkDataSet dataSet = null;
			for (int i = 0; i <= _numBathymetryDataSets - 1; i++) {
				dataSet = (NetworkDataSet) (_bathymetryDataSets.get(_bathymetryDataSetNames.get(i)));
				bathymetryMaxX = Math.max(bathymetryMaxX, dataSet.getMaxX());
				bathymetryMaxY = Math.max(bathymetryMaxY, dataSet.getMaxY());
				bathymetryMinX = Math.min(bathymetryMinX, dataSet.getMinX());
				bathymetryMinY = Math.min(bathymetryMinY, dataSet.getMinY());
			} // for
			maxX = Math.max(bathymetryMaxX, networkMaxX);
			maxY = Math.max(bathymetryMaxY, networkMaxY);
			minX = Math.min(bathymetryMinX, networkMinX);
			minY = Math.min(bathymetryMinY, networkMinY);

			if (DEBUG)
				System.out.println("maxx, maxy, minx, miny=" + maxX + "," + maxY + "," + minX + "," + minY);
			_graph.getPlot().getAxis(AxisAttr.BOTTOM).setDCRange(minX, maxX);
			_graph.getPlot().getAxis(AxisAttr.LEFT).setDCRange(minY, maxY);
		}
		_restoreXsect = false;
	}// updateNetworkDataSet

	/**
	 * turns off all edit modes
	 */
	public void turnOffEditModes() {
		// setAllModesStatesFalse();
		setAllButtonsTrue();
	}

	/**
	 * restores xsect (undo changes made since last keep)
	 */
	public void restoreXsect() {
		if (_oldNetworkDataSet != null) {
			double[] x = _oldNetworkDataSet.getXArray();
			double[] y = _oldNetworkDataSet.getYArray();

			if (DEBUG) {
				System.out.println("##############################################################################");
				System.out.println("oldSize, newSize=" + _oldNetworkDataSet.size() + "," + _networkDataSet.size());
				System.out.println("restoring xsect: current x,y; restoring to old x,y=");
				double[] xCurrent = _networkDataSet.getXArray();
				double[] yCurrent = _networkDataSet.getYArray();
				for (int i = 0; i <= _oldNetworkDataSet.size() - 1; i++) {
					System.out.println(CsdpFunctions.formattedOutputString((double) xCurrent[i], 20, true) + ","
							+ CsdpFunctions.formattedOutputString((double) yCurrent[i], 20, true) + ","
							+ CsdpFunctions.formattedOutputString((double) x[i], 20, true) + ","
							+ CsdpFunctions.formattedOutputString((double) y[i], 20, true));
				}
				System.out.println("##############################################################################");
			}

			_xsect.removeAllPoints();
			if (_oldNetworkDataSet != null && _oldNetworkDataSet.size() > 0) {
				System.out.println("adding old points back to xsect. size=" + _oldNetworkDataSet.size());
				for (int i = 0; i <= _oldNetworkDataSet.size() - 1; i++) {
					System.out.println("restoring: _xsect,x,y=" + _xsect + "," + x + "," + y);
					_xsect.addXsectPoint((double) x[i], (double) y[i]);
				}
			} else {
				System.out.println("NOT ADDING POINTS TO XSECT. SIZE=" + _oldNetworkDataSet.size());
			}
			makeNetworkDataSet();
			_restoreXsect = true;

			System.out.println("restoring xsect");
			// called by listener, so don't need here...
			updateNetworkDataSet();
		} else {
			_xsect.removeAllPoints();
			_networkDataSet = null;

		}
	}// restoreXsect

	/**
	 * keeps xsect changes, but doesn't save to file
	 */
	public void keepChanges() {
		double[] x;
		double[] y;
		x = _networkDataSet.getXArray();
		y = _networkDataSet.getYArray();
		_oldNetworkDataSet = null;
		_oldNetworkDataSet = new NetworkDataSet("old", x, y);

		if (DEBUG) {
			System.out.println("#####################################################");
			System.out.println("keeping changes: x, y=");

			for (int i = 0; i <= _networkDataSet.size() - 1; i++) {
				System.out.println(CsdpFunctions.formattedOutputString((double) x[i], 20, true) + ","
						+ CsdpFunctions.formattedOutputString((double) y[i], 20, true));
			}
			System.out.println("#####################################################");
		}

		_xsect.setIsUpdated(true);
		if (DEBUG)
			System.out.println("_xsect.isUpdated=" + _xsect._isUpdated);

	}

	// /**
	// * adds network data set to graph
	// */
	// public void addNetworkDataSet(){
	// _gC.addData(_networkDataSet, AxisAttr.BOTTOM, AxisAttr.LEFT);
	// setNetworkCurveAttributes();
	// }

	/**
	 * sets all buttons to false(inactive)
	 */
	protected void setAllButtonsTrue() {
		_moveButton.setEnabled(true);
		_addButton.setEnabled(true);
		_insertButton.setEnabled(true);
		_deleteButton.setEnabled(true);
	}

	/**
	 * makes data set to store network data to be plotted
	 */
	protected void makeNetworkDataSet() {
		int numPoints = _xsect.getNumPoints();
		if (DEBUG)
			System.out.println("number of points in xsect=" + numPoints);

		if (numPoints > 0) {
			double[] x = new double[numPoints];
			double[] y = new double[numPoints];
			for (int i = 0; i <= numPoints - 1; i++) {
				x[i] = _xsect.getXsectPoint(i).getStationFeet();
				y[i] = _xsect.getXsectPoint(i).getElevationFeet();
				if (DEBUG)
					System.out.println("x[" + i + "]=" + x[i] + "," + "y[" + i + "]=" + y[i]);
			}
			_networkDataSet = new NetworkDataSet("Network", x, y);
		}
	}// make network data set
	
	/*
	 * Makes a network data when there is no bathymetry data and there are no cross-section points.
	 */
	protected void makeDummyNetworkDataSet() {
		double[] x = new double[]{-1000.0, -900.0, 900.0, 1000.0};
		double[] y = new double[]{100.0, -100.0, -110.0, 100.0};
		_xsect.addXsectPoint((float) x[0], (float)y[0]);
		_xsect.addXsectPoint((float) x[1], (float)y[1]);
		_xsect.addXsectPoint((float) x[2], (float)y[2]);
		_xsect.addXsectPoint((float) x[3], (float)y[3]);
//		_xsect.setIsUpdated(true);
		_networkDataSet = new NetworkDataSet("Network",  x, y);
	}

	/**
	 * Make NetworkDataSets to store bathymetry data to be plotted. Each set
	 * will be from a different year, source, or elevation range (bin) depending
	 * on plot options.
	 */
	protected void makeBathymetryDataSets() {

		int numBathymetryValues = _xsectBathymetryData.getNumEnclosedValues();
		if(DEBUG) {
			System.out.println("XsectGraph.makeBathymetryDataSets: numBathymetryValues=" + numBathymetryValues);
		}

		int numDataSets = 0;
		double[] point = new double[2];
		int pointIndex = 0;
		int dataSetPointIndex = 0;
		String dataSetName = null;
		_numBathymetryDataSets = 0;
		double distanceBinSize = 0.0;
		double[] bPoint = new double[3];
		double[] sePoint = new double[2];

		double[] xsectLine = _net.findXsectLineCoord(_centerlineName, _xsectNum);
		
		double x1 = xsectLine[CsdpFunctions.x1Index];
		double y1 = xsectLine[CsdpFunctions.y1Index];
		double x2 = xsectLine[CsdpFunctions.x2Index];
		double y2 = xsectLine[CsdpFunctions.y2Index];
		if(DEBUG) {
			System.out.println("got xsectLine. _centerlineName, _xsectNum, x1,y1,x2,y2=" + 
				_centerlineName + "," + _xsectNum + "," + x1 + "," + y1 + "," + x2 + "," + y2);
		}
		// find number of series (number of colors to use in graph)
		// why use number of colors?

		if (getColorByDistanceMode()) {
			int numBins = NUM_DISTANCE_BINS;
			// these were the original values, but they resulted in errors.
			// distanceBinSize = _thickness/_gui.getNumColors();
			// numDataSets = _gui.getNumColors();

			if (_gui.getNumColors() < NUM_DISTANCE_BINS) {
				numBins = _gui.getNumColors();
			}
			distanceBinSize = _thickness / numBins;
			numDataSets = numBins;

			if (DEBUG)
				System.out.println("colordistancemode. numColors, thickness=" + _gui.getNumColors() + "," + _thickness);
			if (DEBUG)
				System.out.println("distanceBinSize, numDataSets=" + distanceBinSize + "," + numDataSets);
		} // if colorByDistance
		else if (getColorBySourceMode())
			numDataSets = _bathymetryData.getNumSources();
		else if (getColorByYearMode())
			numDataSets = _bathymetryData.getNumYears();

		if (DEBUG)
			System.out.println("in xsectgraph: numsources=" + _numBathymetryDataSets);
		_numBathymetryDataSets = 0;
		_bathymetryDataSets.clear();

		for (int i = 0; i <= numDataSets - 1; i++) {
			double binValue = 0.0;
			double dist = 0.0;
			double[] station = new double[_xsectBathymetryData.getNumEnclosedValues()];
			double[] elevation = new double[_xsectBathymetryData.getNumEnclosedValues()];

			// find name of data set(distance, year, or source)
			if (getColorByDistanceMode()) {
//				binValue = distanceBinSize * (i);
				//reverse order of bins. This will make further away points plot first, putting closer points on top of them.
				binValue = distanceBinSize*(numDataSets - i-1);
				
				dataSetName = Double.toString(binValue) + " ft from Cross-section line";
//				System.out.println("dataSetName="+dataSetName);
			} else if (getColorBySourceMode())
				dataSetName = _bathymetryData.getSource(i);
			else if (getColorByYearMode())
				dataSetName = Short.toString(_bathymetryData.getYear(i));

			// beginning of new dataSet, so set index to zero.
			dataSetPointIndex = 0;
			for (int j = 0; j <= numBathymetryValues - 1; j++) {
				pointIndex = _xsectBathymetryData.getEnclosedPointIndex(j);
				_bathymetryData.getPointFeet(pointIndex, bPoint);
				double x3 = bPoint[CsdpFunctions.xIndex];
				double y3 = bPoint[CsdpFunctions.yIndex];

				if (DEBUG)
					System.out.println("x1x2x3y1y2y3=" + x1 + "," + x2 + "," + x3 + "," + y1 + "," + y2 + "," + y3);
				dist = CsdpFunctions.shortestDistLineSegment(x1, x2, x3, y1, y2, y3);
				// if the distance from the point to the cross-section line is
				// within the range defined by the binValue, add it to the
				// dataset
				if (getColorByDistanceMode()) {
					_xsectBathymetryData.getEnclosedStationElevation(j, sePoint);
					if (DEBUG)
						System.out.println("dist,binValue=" + dist + "," + binValue);

					if (binValue < dist && dist < binValue + distanceBinSize) {
						station[dataSetPointIndex] = (double) sePoint[stationIndex];
						elevation[dataSetPointIndex] = (double) sePoint[elevationIndex];
						dataSetPointIndex++;
					}
				} // if colorByDistance
				else if (getColorBySourceMode()) {
					if (dist < Double.MAX_VALUE && dataSetName
							.equals(_bathymetryData.getSource(_bathymetryData.getSourceIndex(pointIndex)))) {
						_xsectBathymetryData.getEnclosedStationElevation(j, sePoint);
						station[dataSetPointIndex] = (double) sePoint[stationIndex];
						elevation[dataSetPointIndex] = (double) sePoint[elevationIndex];
						dataSetPointIndex++;
					}
				} // else if
				else if (getColorByYearMode()) {
					int yearIndex = _bathymetryData.getYearIndex(pointIndex);
					short year = _bathymetryData.getYear(yearIndex);
					if (dist < Double.MAX_VALUE && dataSetName.equals(Short.toString(year))) {
						_xsectBathymetryData.getEnclosedStationElevation(j, sePoint);
						station[dataSetPointIndex] = (double) sePoint[stationIndex];
						elevation[dataSetPointIndex] = (double) sePoint[elevationIndex];
						dataSetPointIndex++;
					} // if
				} // else if
			} // for j: looping through all enclosed bathymetry points

			// if any points were added to the dataset, create new arrays which
			// are
			// dimensioned to the number of points in the dataset.
			if (dataSetPointIndex > 0) {
				if (DEBUG)
					System.out.println("making data set " + dataSetName + ", numPoints=" + dataSetPointIndex);

				double[] newStation = new double[dataSetPointIndex];
				double[] newElevation = new double[dataSetPointIndex];
				for (int j = 0; j <= dataSetPointIndex - 1; j++) {
					newStation[j] = station[j];
					newElevation[j] = elevation[j];
				}
				if (DEBUG)
					System.out.println(
							"xsectGraph.makeBathymetryDataSets: adding element to bathymetrydatasetnames. index, value="
									+ _numBathymetryDataSets + "," + dataSetName);

				_bathymetryDataSets.put(dataSetName, new NetworkDataSet(dataSetName, newStation, newElevation));
				_bathymetryDataSetNames.put(_numBathymetryDataSets, dataSetName);
				_numBathymetryDataSets++;
			} else {
				if (DEBUG)
					System.out.println("no points added to dataset " + dataSetName);
			}
		} // for i

		if (DEBUG) {
			System.out.println("end of makeBathymetryDataSets.  numBathymetryDataSets=" + _numBathymetryDataSets);
			System.out.println("End of makeBathymetryDataSets. numBathymetryDataSets = "+_numBathymetryDataSets);
		}		
	}// makeBathymetryDataSets

	/**
	 * change number of cross-section
	 */
	public void updateXsectNum(int n) {
		System.out.println("updating the xsect num to " + n);
		_xsectNum = n;
		_graph.setTitle(
				"Cross-section " + _centerlineName + "_" + _xsectNum + "," + " thickness=" + _thickness + " ft.");
		updateDisplay();
	}// updateXsectNum

	/**
	 * repaints canvas
	 */
	public void updateDisplay() {
		// needed for swing
		updateGraphCanvas();
		updateXsectProp();
		updateDConveyanceDisplay();
		// needed for swing
		validate();
		_gC.redoNextPaint();
		// removed for conversion to swing
		// _gC.paintAll(_gC.getGraphics());
		_gC.repaint();
	}// updateDisplay

	/**
	 * calculate xsect prop with new elevation
	 */
	protected void updateXsectProp() {
		double width = 0.0;
		double area = 0.0;
		double wetp = 0.0;
		double hDepth = 0.0;
		double e = getXSPropElevation();
		if(_xsect.getNumPoints()>0) {
			width = _xsect.getWidthFeet(e);
			area = _xsect.getAreaSqft(e);
			wetp = _xsect.getWettedPerimeterFeet(e);
			hDepth = _xsect.getHydraulicDepthFeet(e);
			// dk = _xsect.getDConveyance(_xsectPropElevation);
			CsdpFileMetadata metadata = CsdpFunctions.getBathymetryMetadata();
			String ccString="";
			ccString += String.format("%-22s", "Elevation, ft ("+metadata.getVDatumString()+")")+"\t"+ String.format("%.2f", e) +"\n";
			ccString += String.format("%-22s", "Num points")+ "\t"+ String.format("%d", _xsect.getNumPoints()) +"\n";
			ccString += String.format("%-22s", "Width, ft") + "\t\t"+ String.format("%.2f", width) +"\n";
			ccString += String.format("%-22s", "Area, sq ft") + "\t\t"+ String.format("%.2f", area) +"\n";
			ccString += String.format("%-22s", "Wetted Perimeter, ft") + "\t"+ String.format("%.2f", wetp) +"\n";
			ccString += String.format("%-22s", "Hydraulic Depth, ft") + "\t"+ String.format("%.2f", hDepth) +"\n";
//			_numPointsLabel.setText("num points");
//			_elevationLabel.setText("Elevation, ft ("+metadata.getVDatumString());
//			_widthLabel.setText("Width, ft");
//			_areaLabel.setText("Area, square ft");
//			_wetpLabel.setText("Wetted Perimeter, ft");
//			_hDepthLabel.setText("HydraulicDepth, ft");
			_conveyanceCharacteristicsTextArea.setText(ccString);
		}
	}//updateXsectProp

	/*
	 * Recalculates the dConveyance values and updates the display
	 */
	private void updateDConveyanceDisplay() {
		if(_xsect.getNumPoints()>0) {
			double[] dConveyanceValues = _xsect.getDConveyanceValues();
			double[] areaValues = _xsect.getAreaValues();
			double[] widthValues = _xsect.getWidthValues();
			double[] wetPValues = _xsect.getWetPValues();
			double[] elevations = _xsect.getSortedUniqueElevations();
			_dConveyanceTextPane.setText("");

			appendToPane(_dConveyanceTextPane, 
					String.format("%10s", "Elevation") +
					String.format("%20s", "dConveyance") +
					String.format("%15s", "area")+
					String.format("%10s", "width")+
					String.format("%10s", "wet_p")+
					"\n------------------------------------------------------------------------\n", 
					Color.BLACK, Color.WHITE);
			//add dConveyance values in reverse order
			Color intertidalBackgroundColor = Color.YELLOW;
			Color nonIntertidalBackgroundColor = Color.white;
			for(int i=elevations.length-1; i>=0; i--) {
				Color backgroundColor = null;
				if(elevations[i]>=CsdpFunctions.INTERTIDAL_LOW_TIDE && elevations[i]<=CsdpFunctions.INTERTIDAL_HIGH_TIDE) {
					backgroundColor = intertidalBackgroundColor;
				}else {
					backgroundColor = nonIntertidalBackgroundColor;
				}
				appendToPane(_dConveyanceTextPane, String.format("%10.2f", elevations[i])+"\t", Color.black, backgroundColor);
				if(dConveyanceValues[i]<0.0) {
					appendToPane(_dConveyanceTextPane, String.format("%20.2f", dConveyanceValues[i])+"\t", Color.red, backgroundColor);
				}else {
					appendToPane(_dConveyanceTextPane, String.format("%20.2f", dConveyanceValues[i])+"\t", Color.black, backgroundColor);
				}
				appendToPane(_dConveyanceTextPane, String.format("%15.2f", areaValues[i])+"\t", Color.black, backgroundColor);
				appendToPane(_dConveyanceTextPane, String.format("%10.2f", widthValues[i])+"\t", Color.BLACK, backgroundColor);
				appendToPane(_dConveyanceTextPane, String.format("%10.2f", wetPValues[i])+"\t", Color.black, backgroundColor);
				appendToPane(_dConveyanceTextPane, "\n", Color.black, Color.white);
			}
			appendToPane(_dConveyanceTextPane, "\n", Color.black, Color.white);
		}
	}//updateDConveyanceDisplay

	/*
	 * Copied from https://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea
	 */
    private void appendToPane(JTextPane tp, String msg, Color foregroundColor, Color backgroundColor)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, foregroundColor);
        aset = sc.addAttribute(aset, StyleConstants.Background, backgroundColor);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

	
	//	/*
//	 * Recalculates the dConveyance values and updates the display
//	 */
//	private void updateDConveyanceDisplay() {
//		if(_xsect.getNumPoints()>0) {
//			double[] dConveyanceValues = _xsect.getDConveyanceValues();
//			double[] areaValues = _xsect.getAreaValues();
//			double[] widthValues = _xsect.getWidthValues();
//			double[] wetPValues = _xsect.getWetPValues();
//			double[] elevations = _xsect.getSortedUniqueElevations();
//			String dConveyanceString = "Elevation\tdConveyance\tarea\twidth\twet_p\n"
//					+ "--------------------------------------------------------------------------------------------------------\n";
//			//add dConveyance values in reverse order
//			for(int i=elevations.length-1; i>=0; i--) {
//				dConveyanceString += String.format("%.2f", elevations[i])+
//						"\t"+String.format("%.2f", dConveyanceValues[i])+
//						"\t"+String.format("%.2f", areaValues[i])+
//						"\t"+String.format("%.2f", widthValues[i])+
//						"\t"+String.format("%.2f", wetPValues[i])+"\n";
//			}
//			_dConveyanceTextArea.setText(dConveyanceString);
//		}
//	}//updateDConveyanceDisplay
	
	//This will make -dK values red, but not layout out correctly yet.
	//need to figure out how to add horizontal, but not vertical spacing between cells,
	//and to prevent text from getting cut off at the bottom
//	/*
//	 * Recalculates the dConveyance values and updates the display
//	 */
//	private void updateDConveyanceDisplay() {
//		if(_xsect.getNumPoints()>0) {
//			double[] dConveyanceValues = _xsect.getDConveyanceValues();
//			double[] areaValues = _xsect.getAreaValues();
//			double[] widthValues = _xsect.getWidthValues();
//			double[] wetPValues = _xsect.getWetPValues();
//			double[] elevations = _xsect.getSortedUniqueElevations();
//			String dConveyanceString = "<html><BODY><TABLE cellspacing=\"0\" cellpadding=\"0\">"
//					+ "<TR><TD><b>Elevation</b></TD>"
//					+ "<TD><b>dConveyance</b></TD>"
//					+ "<TD><b>Area</b></TD>"
//					+ "<TD><b>Width</b></TD>"
//					+ "<TD><b>WetP</b></TD></TR>";
//			//add dConveyance values in reverse order
//			for(int i=elevations.length-1; i>=0; i--) {
//				double dK = dConveyanceValues[i];
//				String dKString = null;
//				if(dK<0) {
//					dKString = "<font color=red>"+String.format("%.2f", dK)+"</font>";
//				}else {
//					dKString = String.format("%.2f", dK);
//				}
//				dConveyanceString+="<TR>";
//				dConveyanceString += "<TD>"+String.format("%.2f", elevations[i])+"</TD>"+
//					"<TD>"+dKString+"</TD>"+
//					"<TD>"+String.format("%.2f", areaValues[i])+"</TD>"+
//					"<TD>"+String.format("%.2f", widthValues[i])+"</TD>"+
//					"<TD>"+String.format("%.2f", wetPValues[i])+"</TD>";
//				dConveyanceString+="</TR>";
//			}
//			dConveyanceString+="</TABLE></BODY></html>\n\n";
//			_dConveyanceEditorPane.setText(dConveyanceString);
//		}
//	}//updateDConveyanceDisplay
	
	/**
	 * called when keep button pressed.
	 */
	public void setChangesKept(boolean b) {
		_changesKept = b;
	}

	/**
	 * Only called when closing window to see if there are any changes that need
	 * to be saved (in memory, not in network file).
	 */
	public boolean getChangesKept() {
		return _changesKept;
	}

	public boolean getAddPointMode() {
		return _addButton.isSelected();
	}

	public boolean getInsertPointMode() {
		return _insertButton.isSelected();
	}

	public boolean getDeletePointMode() {
		return _deleteButton.isSelected();
	}

	public boolean getMovePointMode() {
		return _moveButton.isSelected();
	}

	public boolean getColorByDistanceMode() {
		return _colorByDistanceButton.isSelected();
	}

	public boolean getColorBySourceMode() {
		return _colorBySourceButton.isSelected();
	}

	public boolean getColorByYearMode() {
		return _colorByYearButton.isSelected();
	}
	public void setXSPropElevation(double nf) {
		_xsPropElevation = nf;
	}
	private double getXSPropElevation() {
		return _xsPropElevation;
	}
	
}// class XsectGraph
