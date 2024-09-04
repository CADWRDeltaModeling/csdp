package DWR.CSDP;

import DWR.CSDP.dialog.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


import DWR.CSDP.CURVEFIT.LineSimplification;
import vista.app.CurveFactory;
import vista.app.GraphBuilderInfo;
import vista.app.MainProperties;
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
	private App _app;
	private BathymetryData _bathymetryData;
	private Network _net;
	private GECanvas _gC;
	private NetworkDataSet _networkDataSet;
	private NetworkDataSet _oldNetworkDataSet;

	/*
	 * size of cross-section points
	 */
	// protected static int squareDimension = 2;
	protected static final int stationIndex = 0;
	protected static final int elevationIndex = 1;
	private XsectEditInteractor _xei;
	private Xsect _xsect;
	// protected JCheckBoxMenuItem _xMovePointMenuItem, _xAddPointMenuItem,
	// _xInsertPointMenuItem, _xDeletePointMenuItem;

	private URL xsCloseUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"XSCloseButton.png");
	private URL cursorIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ArrowButton.png");
	private URL reverseXsectIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ReverseXsectButton.png");
	private URL movePointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"MoveXsectPointButton.png");
	//	private URL addPointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddXsectPointButton.png");
	private URL addLeftPointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddBeginningXsectPointButton.png");
	private URL addRightPointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddEndingXsectPointButton.png");
	private URL insertPointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"InsertXsectPointButton.png");
	private URL deletePointIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"DeleteXsectPointButton.png");
	private URL cursorIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ArrowButtonSelected.png");
	private URL movePointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"MoveXsectPointButtonSelected.png");
	//	private URL addPointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddXsectPointButtonSelected.png");
	private URL addBeginningPointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddBeginningXsectPointButtonSelected.png");
	private URL addEndingPointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AddEndingXsectPointButtonSelected.png");
	private URL insertPointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"InsertXsectPointButtonSelected.png");
	private URL deletePointIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"DeleteXsectPointButtonSelected.png");
	private URL keepIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"KeepButton.png");
	private URL restoreIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"RestoreButton.png");
	private URL colorByDistanceIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorDistanceButton.png");
	private URL colorByDistanceIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorDistanceButtonSelected.png");
	private URL colorBySourceIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorSourceButton.png");
	private URL colorBySourceIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorSourceButtonSelected.png");
	private URL colorByYearIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorYearButton.png");
	private URL colorByYearIconSelectedUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"ColorYearButtonSelected.png");
	private URL autoXSIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"AutoXSButton.png");
	
	private ImageIcon _xsCloseIcon = CsdpFunctions.createScaledImageIcon(xsCloseUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
	private ImageIcon _cursorIcon = CsdpFunctions.createScaledImageIcon(cursorIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _reverseXsectIcon = CsdpFunctions.createScaledImageIcon(reverseXsectIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _movePointIcon = CsdpFunctions.createScaledImageIcon(movePointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	//	private ImageIcon _addPointIcon = CsdpFunctions.createScaledImageIcon(addPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _addLeftPointIcon = CsdpFunctions.createScaledImageIcon(addLeftPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _addRightPointIcon = CsdpFunctions.createScaledImageIcon(addRightPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _insertPointIcon = CsdpFunctions.createScaledImageIcon(insertPointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _deletePointIcon = CsdpFunctions.createScaledImageIcon(deletePointIconUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _cursorIconSelected = CsdpFunctions.createScaledImageIcon(cursorIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _movePointIconSelected = CsdpFunctions.createScaledImageIcon(movePointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	//	private ImageIcon _addPointIconSelected = CsdpFunctions.createScaledImageIcon(addPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _addUpstreamPointIconSelected = CsdpFunctions.createScaledImageIcon(addBeginningPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _addDownstreamPointIconSelected = CsdpFunctions.createScaledImageIcon(addEndingPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _insertPointIconSelected = CsdpFunctions.createScaledImageIcon(insertPointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
	private ImageIcon _deletePointIconSelected = CsdpFunctions.createScaledImageIcon(deletePointIconSelectedUrl, ICON_WIDTH, ICON_HEIGHT);

	private ImageIcon _keepIcon = CsdpFunctions.createScaledImageIcon(keepIconUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
	private ImageIcon _restoreIcon = CsdpFunctions.createScaledImageIcon(restoreIconUrl, (int)(1.3*WIDE_ICON_WIDTH), WIDE_ICON_HEIGHT);

	private ImageIcon _colorByDistanceIcon = CsdpFunctions.createScaledImageIcon(colorByDistanceIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _colorByDistanceIconSelected = CsdpFunctions.createScaledImageIcon(colorByDistanceIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _colorBySourceIcon = CsdpFunctions.createScaledImageIcon(colorBySourceIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _colorBySourceIconSelected = CsdpFunctions.createScaledImageIcon(colorBySourceIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _colorByYearIcon = CsdpFunctions.createScaledImageIcon(colorByYearIconUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _colorByYearIconSelected = CsdpFunctions.createScaledImageIcon(colorByYearIconSelectedUrl, COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
	private ImageIcon _autoXSIcon = CsdpFunctions.createScaledImageIcon(autoXSIconUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
	
	private JRadioButton _colorByDistanceButton = new JRadioButton(_colorByDistanceIcon);
	private JRadioButton _colorBySourceButton = new JRadioButton(_colorBySourceIcon);
	private JRadioButton _colorByYearButton = new JRadioButton(_colorByYearIcon);

	private JButton _xsCloseButton = new JButton(_xsCloseIcon);
	private JButton _reverseButton = new JButton(_reverseXsectIcon);
	private JRadioButton _arrowButton = new JRadioButton(_cursorIcon);
	private JRadioButton _moveButton = new JRadioButton(_movePointIcon);
	//	private JRadioButton _addButton = new JRadioButton(_addPointIcon);
	private JRadioButton _addLeftPointButton = new JRadioButton(_addLeftPointIcon);
	private JRadioButton _addRightPointButton = new JRadioButton(_addRightPointIcon);
	private JRadioButton _insertButton = new JRadioButton(_insertPointIcon);
	private JRadioButton _deleteButton = new JRadioButton(_deletePointIcon);

	private JButton _restoreButton = new JButton(_restoreIcon);
	private JButton _keepButton = new JButton(_keepIcon);
	private JButton _cloneButton = new JButton("Clone");

	// for automatic cross-section creation
	/*
	 * for metadata entry
	 */
	public static String _userInitialsString = null;
	//	private JPanel _autoXSPanel = new JPanel(new GridLayout(1,6));
	private JPanel _autoXSPanel = new JPanel(new FlowLayout());
	private JButton _autoXSButton = new JButton(_autoXSIcon);
	private SliderPanel _autoXSEpsilonSlider;
	private JFormattedTextField _autoXSMinYearTextField;
	private JFormattedTextField _autoXSMaxYearTextField;
	private JFormattedTextField _autoXSMinStationTextField;
	private JFormattedTextField _autoXSMaxStationTextField;
	
	// private JButton _metadataButton = new JButton(_metadataIcon);

	private ButtonGroup _colorByButtonGroup, _xsectEditButtonGroup;

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
	/*
	 * Previously, bathymetry data sets were stored as BathymetryDataSet objects, and the cross-sections points were 
	 * stored as a NetworkDataSet object. This was changed so that both data set types are now stored as NetworkDataSet 
	 * objects, which simplifies things a bit. 
	 */
	protected Hashtable<String, NetworkDataSet> _bathymetryDataSets = new Hashtable<String, NetworkDataSet>();
	protected int _numBathymetryDataSets = 0;
	// protected boolean _colorByDistance = false;
	// protected boolean _colorBySource = false;
	// protected boolean _colorByYear = true;
	protected ResizableStringArray _bathymetryDataSetNames = new ResizableStringArray();

	private JPanel _eastPanel = new JPanel();
	private JPanel _xsPropPanel = new JPanel();
	private JPanel _dConveyancePanel = new JPanel();
	private JPanel _inputPanel = new JPanel();
	private JButton _elevationButton = new JButton("Change elevation");
	private JLabel _numPointsLabel = new JLabel();
	private JLabel _elevationLabel = new JLabel();
	private JLabel _widthLabel = new JLabel();
	private JLabel _areaLabel = new JLabel();
	private JLabel _wetpLabel = new JLabel();
	private JLabel _hDepthLabel = new JLabel();

	private JLabel _numPointsValueLabel = new JLabel();
	private JLabel _elevationValueLabel = new JLabel();
	private JLabel _widthValueLabel = new JLabel();
	private JLabel _wetpValueLabel = new JLabel();
	private JLabel _areaValueLabel = new JLabel();
	private JLabel _hDepthValueLabel = new JLabel();

	// private JLabel _dkLabel = new JLabel();
	//	private JEditorPane _dConveyanceEditorPane = new JEditorPane();
	//	private JTextArea _dConveyanceTextArea = new JTextArea();
	private JTextPane _dConveyanceTextPane = new JTextPane();
	private JTextArea _conveyanceCharacteristicsTextArea = new JTextArea();

	// private JCheckBoxMenuItem _bColorByDistance, _bColorBySource, _bColorByYear;
	private JMenuItem _bChangePointSize;
	private String _centerlineName = null;
	private int _xsectNum;
	private Legend _legend;
	private CsdpFrame _gui;
	public int COLOR_BY_DISTANCE = 0;
	public int COLOR_BY_YEAR = 1;
	public int COLOR_BY_SOURCE = 2;
	protected boolean _changesKept = false;
	private Border _raisedBevel = BorderFactory.createRaisedBevelBorder();
	private Border _lineBorder = BorderFactory.createLineBorder(Color.black, 2);

	// all new
	public JTextArea _metadataJTextArea;
	//	private ResizableStringArray _metadataMessage;
	private JScrollPane _metadataScrollPane;

	// DefaultGraphBuilder _dgb = new DefaultGraphBuilder();
	private Graph _graph;
	private GraphFactory _factory = new DefaultGraphFactory();
	private MultiPlot _multiPlot;
	private DataReference[] _refs;
	private GraphBuilderInfo _info;

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
	/*
	 * The background color for the graph. For reports, white background is required by accessibility guidelines
	 */
	//	private static final Color GRAPH_BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private static final Color GRAPH_BACKGROUND_COLOR = Color.WHITE;


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
		//		setPreferredSize(new Dimension(1200, 900));
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

		_metadataJTextArea = new UndoableJTextArea(_xsect.getMetadata());
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

		URL metadataIconUrl = this.getClass().getResource(CsdpFunctions.getIconImagePath()+File.separator+"MetadataIcon.png");
		//		ImageIcon _metadataIcon = new ImageIcon(metadataIconUrl);
		ImageIcon _metadataIcon = CsdpFunctions.createScaledImageIcon(metadataIconUrl, WIDE_ICON_WIDTH*4, WIDE_ICON_HEIGHT);
		JLabel _metadataLabel = new JLabel(_metadataIcon, SwingConstants.LEFT);

		_eastPanel.add(_metadataLabel);
		_eastPanel.add(_metadataScrollPane);
		getContentPane().add("East", _eastPanel);

		//// _inputPanel.add("South", _xsPropPanel);
		//// add("South", _inputPanel);

		updateXsectProp();

		JPanel northPanel = new JPanel(new BorderLayout());

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
		//		btnPanel.add(_addButton);
		btnPanel.add(_addLeftPointButton);
		btnPanel.add(_addRightPointButton);
		btnPanel.add(_insertButton);
		btnPanel.add(_deleteButton);
		btnPanel.add(_moveXsectXField);
		btnPanel.add(_moveXsectXButton);
		btnPanel.add(_moveXsectYField);
		btnPanel.add(_moveXsectYButton);
		btnPanel.add(_keepButton);
		btnPanel.add(_restoreButton);
		btnPanel.add(_cloneButton);

		northPanel.add(btnPanel, BorderLayout.WEST);

		//configure epsilon slider. Epsilon is a variable used in the line simplification algorithm.
		//A lower value uses more points, and a higher value uses fewer points.
		EpsilonChangeListener epsilonChangeListener = new EpsilonChangeListener(this);
		Hashtable<Integer, JLabel> labelValues = new Hashtable<Integer, JLabel>();
		labelValues.put(new Integer(0), new JLabel("0"));
		labelValues.put(new Integer(20), new JLabel("2"));
		labelValues.put(new Integer(40), new JLabel("4"));
		labelValues.put(new Integer(60), new JLabel("6"));
		labelValues.put(new Integer(80), new JLabel("8"));
		labelValues.put(new Integer(100), new JLabel("10"));
		
		_autoXSEpsilonSlider = new SliderPanel("epsilon", epsilonChangeListener, labelValues, 
				LineSimplification.MIN_EPSILON, LineSimplification.MAX_EPSILON,
				CsdpFunctions.RAMER_DOUGLAS_PEUCKER_EPSILON, 10.0);

		NumberFormat numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setGroupingUsed(false);
		
		_autoXSMinYearTextField = new JFormattedTextField(numberFormat);
		_autoXSMaxYearTextField = new JFormattedTextField(numberFormat);
		_autoXSMinYearTextField.setEnabled(false);
		_autoXSMaxYearTextField.setEnabled(false);
		_autoXSMinYearTextField.setValue(CsdpFunctions.AUTO_XS_MIN_YEAR);
		_autoXSMaxYearTextField.setValue(CsdpFunctions.AUTO_XS_MAX_YEAR);
		_autoXSMinYearTextField.addActionListener(new MinMaxYearFieldListener(this, _autoXSMinYearTextField, MinMaxYearFieldListener.MIN));
		_autoXSMaxYearTextField.addActionListener(new MinMaxYearFieldListener(this, _autoXSMaxYearTextField, MinMaxYearFieldListener.MAX));
		JButton _autoXSRestMinMaxStationsButton = new JButton("Reset Min/Max");
		_autoXSRestMinMaxStationsButton.addActionListener(new AutoLeveeListener(this));
		
		_autoXSMinStationTextField = new JFormattedTextField(numberFormat);
		_autoXSMaxStationTextField = new JFormattedTextField(numberFormat);
		_autoXSMinStationTextField.setText("");
		_autoXSMaxStationTextField.setText("");
		_autoXSMinStationTextField.addActionListener(new MinMaxStationFieldListener(this));
		_autoXSMaxStationTextField.addActionListener(new MinMaxStationFieldListener(this));
		
		_autoXSPanel.setBorder(_raisedBevel);
		_autoXSPanel.add(_autoXSButton);

		_autoXSPanel.add(_autoXSEpsilonSlider);

		JPanel minYearPanel = new JPanel(new FlowLayout());
		minYearPanel.setBorder(_raisedBevel);
		minYearPanel.add(new JLabel("Min Year", SwingConstants.RIGHT));
		minYearPanel.add(_autoXSMinYearTextField);
		_autoXSPanel.add(minYearPanel);

		JPanel maxYearPanel = new JPanel(new FlowLayout());
		maxYearPanel.setBorder(_raisedBevel);
		maxYearPanel.add(new JLabel("Max Year", SwingConstants.RIGHT));
		maxYearPanel.add(_autoXSMaxYearTextField);
		_autoXSPanel.add(maxYearPanel);

		JPanel minMaxStationPanel = new JPanel(new FlowLayout());
		minMaxStationPanel.setBorder(_raisedBevel);
		JPanel minStationPanel = new JPanel(new FlowLayout());
		minStationPanel.setBorder(_raisedBevel);
		minStationPanel.add(new JLabel("Min Station", SwingConstants.RIGHT));
		minStationPanel.add(_autoXSMinStationTextField);
		minMaxStationPanel.add(minStationPanel);

		JPanel maxStationPanel = new JPanel(new FlowLayout());
		maxStationPanel.setBorder(_raisedBevel);
		maxStationPanel.add(new JLabel("Max Station", SwingConstants.RIGHT));
		maxStationPanel.add(_autoXSMaxStationTextField);
		minMaxStationPanel.add(maxStationPanel);
		minMaxStationPanel.add(_autoXSRestMinMaxStationsButton);

		_autoXSPanel.add(minMaxStationPanel);
		
		//		_autoXSPanel.add(new JLabel("Min Year", SwingConstants.RIGHT));
//		_autoXSPanel.add(_autoXSMinYearTextField);
		
//		_autoXSPanel.add(new JLabel("Max Year", SwingConstants.RIGHT));
//		_autoXSPanel.add(_autoXSMaxYearTextField);
		
		northPanel.add(_autoXSPanel, BorderLayout.SOUTH);

		
		// btnPanel.add(_metadataButton);
		_arrowButton.setSelectedIcon(_cursorIconSelected);
		_moveButton.setSelectedIcon(_movePointIconSelected);
		//		_addButton.setSelectedIcon(_addPointIconSelected);
		_addLeftPointButton.setSelectedIcon(_addUpstreamPointIconSelected);
		_addRightPointButton.setSelectedIcon(_addDownstreamPointIconSelected);
		_insertButton.setSelectedIcon(_insertPointIconSelected);
		_deleteButton.setSelectedIcon(_deletePointIconSelected);
		_xsectEditButtonGroup = new ButtonGroup();
		_xsectEditButtonGroup.add(_arrowButton);
		_xsectEditButtonGroup.add(_moveButton);
		//		_xsectEditButtonGroup.add(_addButton);
		_xsectEditButtonGroup.add(_addLeftPointButton);
		_xsectEditButtonGroup.add(_addRightPointButton);
		_xsectEditButtonGroup.add(_insertButton);
		_xsectEditButtonGroup.add(_deleteButton);

		// use this for Frame
		// add("North", btnPanel);
		// use this instead for JFrame
		//		getContentPane().add("North", btnPanel);
		getContentPane().add("North", northPanel);

		_xsCloseButton.setToolTipText("close cross-section");
		_reverseButton.setToolTipText("reverse cross-section drawing  ");
		_arrowButton.setToolTipText("turn off edit mode");
		_moveButton.setToolTipText("move point  ");
		//		_addButton.setToolTipText("add point  ");
		_addLeftPointButton.setToolTipText("add left point ");
		_addRightPointButton.setToolTipText("add right point  ");
		_insertButton.setToolTipText("insert point  ");
		_deleteButton.setToolTipText("delete point  ");
		_restoreButton.setToolTipText("undo changes since last keep  ");
		_keepButton.setToolTipText("store changes in memory, update values in main application window and centerline/reach summaries (not on disk!) ");
		_cloneButton.setToolTipText("Replace user-created points in this XS with points from another XS");
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

		boolean newDummyXsect = updateGraphCanvas();

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
		if(newDummyXsect) {
			getXsect().setIsUpdated(true);
		}
		updateDisplay();
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
	public boolean updateGraphCanvas() {
		boolean newDummyXsect = false;
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
		_graph.setBackgroundColor(this.GRAPH_BACKGROUND_COLOR );
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
		//the _refs array contains DefaultReference objects, each containing a bathymetry data set (which is actually a NetworkDataSet), 
		// with the last being a network data set
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
			newDummyXsect = true;
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

		_xei = new XsectEditInteractor(_gui, _app, _net, this, _xsect, _gC, _graph);
		_gC.addMouseListener(_xei);
		_gC.addMouseMotionListener(_xei);
		_gC.addComponentListener(new FontResizeInteractor(_gC));

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
		return newDummyXsect;
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
		JMenuItem xReverse, xKeep, xRestore, xPrint, xSaveToImage, xClose, xAutoCreate;
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
		xgXsect.add(xAutoCreate = new JMenuItem("Auto create xs"));
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
		XsectEditMenu xsectEditMenu = new XsectEditMenu(this, _gui, _net, _app);
		ActionListener xReverseListener = xsectEditMenu.new XReverse(_xsect);
		ActionListener xKeepListener = xsectEditMenu.new XKeep();
		ActionListener xRestoreListener = xsectEditMenu.new XRestore();
		ActionListener xChangeElevationListener = xsectEditMenu.new XChangeElevation();
		ActionListener xAutoCreateListener = xsectEditMenu.new XAutoCreate(this);
		ActionListener xSaveToImageListener = xsectEditMenu.new XSaveToImage(this);
		ActionListener xPrintListener = xsectEditMenu.new XPrint(this);
		EventListener xCloseListener = xsectEditMenu.new XClose();
		ItemListener xMovePointListener = xsectEditMenu.new XMovePoint();
		//		ItemListener xAddPointListener = xsectEditMenu.new XAddPoint();
		ItemListener xAddBeginningPointListener = xsectEditMenu.new XAddPoint();
		ItemListener xAddEndingPointListener = xsectEditMenu.new XAddPoint();
		ItemListener xInsertPointListener = xsectEditMenu.new XInsertPoint();
		ItemListener xDeletePointListener = xsectEditMenu.new XDeletePoint();
		ItemListener xStopEditListener = xsectEditMenu.new XStopEdit();
		ActionListener xMoveXsectXListener = xsectEditMenu.new XMoveXsectX(_moveXsectXField, _xsect);
		ActionListener xMoveXsectYListener = xsectEditMenu.new XMoveXsectY(_moveXsectYField, _xsect);

		ActionListener xCloneListener = xsectEditMenu.new CloneXsect(_gui, _app, _centerlineName, this);

		DocumentListener xMetadataListener =
				// xsectEditMenu.new XMetadata(_centerlineName, _xsectNum,
				// _xsect,_gui);
				xsectEditMenu.new XMetadata();
		_metadataJTextArea.getDocument().addDocumentListener(xMetadataListener);

		// xReverse.addActionListener(xReverseListener);
		// xKeep.addActionListener(xKeepListener);
		// xRestore.addActionListener(xRestoreListener);
		_elevationButton.addActionListener(xChangeElevationListener);
		xAutoCreate.addActionListener(xAutoCreateListener);
		_autoXSButton.addActionListener(xAutoCreateListener);
		xSaveToImage.addActionListener(xSaveToImageListener);
		//		xPrint.addActionListener(xPrintListener);
		xClose.addActionListener((ActionListener) xCloseListener);

		addWindowListener((WindowListener) xCloseListener);
		_xsCloseButton.addActionListener((ActionListener) xCloseListener);


		_moveButton.addItemListener(xMovePointListener);
		//		_addButton.addItemListener(xAddPointListener);
		_addLeftPointButton.addItemListener(xAddBeginningPointListener);
		_addRightPointButton.addItemListener(xAddEndingPointListener);
		_insertButton.addItemListener(xInsertPointListener);
		_deleteButton.addItemListener(xDeletePointListener);
		_arrowButton.addItemListener(xStopEditListener);

		_moveButton.addItemListener(xMovePointListener);
		//		_addButton.addItemListener(xAddPointListener);
		_addLeftPointButton.addItemListener(xAddBeginningPointListener);
		_addRightPointButton.addItemListener(xAddEndingPointListener);
		_insertButton.addItemListener(xInsertPointListener);
		_deleteButton.addItemListener(xDeletePointListener);
		_arrowButton.addItemListener(xStopEditListener);

		_reverseButton.addActionListener(xReverseListener);
		_restoreButton.addActionListener(xRestoreListener);
		_keepButton.addActionListener(xKeepListener);
		_cloneButton.addActionListener(xCloneListener);

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
					System.out.println("restoring: _xsect,x,y=" + _xsect + "," + x[i] + "," + y[i]);
					_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, (double) x[i], (double) y[i]);
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
		//need to update reach summaries and info panels after restoring
		_app.updateAllOpenCenterlineOrReachSummaries(_centerlineName);
		_gui.updateInfoPanel(_centerlineName);
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
		//		_addButton.setEnabled(true);
		_addLeftPointButton.setEnabled(true);
		_addRightPointButton.setEnabled(true);
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
	 * This is for XsectEditInteractor, which needs to use pixel coordinates in 
	 * move point mode to determine which point the user wants to move.
	 */
	public NetworkDataSet getNetworkDataSet() {return _networkDataSet;}

	/*
	 * Makes a network data when there is no bathymetry data and there are no cross-section points.
	 */
	protected void makeDummyNetworkDataSet() {
		double[] x = new double[]{-1000.0, -900.0, 900.0, 1000.0};
		double[] y = new double[]{100.0, -100.0, -110.0, 100.0};
		_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, (float) x[0], (float)y[0]);
		_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, (float) x[1], (float)y[1]);
		_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, (float) x[2], (float)y[2]);
		_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, (float) x[3], (float)y[3]);
		//		_xsect.setIsUpdated(true);
		_networkDataSet = new NetworkDataSet("Network",  x, y);
	}

	/**
	 * For creating an automatic cross-section
	 * @param minYear
	 * @return
	 */
	private double[][] makeBathymetryArrayForAutoXS(int minYear, int maxYear) {
		int numBathymetryValues = _xsectBathymetryData.getNumEnclosedValues();
		int numDataSets = 0;
		double[] point = new double[2];
		int pointIndex = 0;
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
		numDataSets = _bathymetryData.getNumYears();

		ResizableDoubleArray stationRDA = new ResizableDoubleArray();
		ResizableDoubleArray elevationRDA = new ResizableDoubleArray();
		
		double[] yearArray = new double[numBathymetryValues];
		int index = 0;

		//		for (int i = 0; i <= numDataSets - 1; i++) {
		double binValue = 0.0;
		double dist = 0.0;

		HashSet<String> rejectedPointSet = new HashSet<String>();
		// beginning of new dataSet, so set index to zero.
		for (int j = 0; j < numBathymetryValues; j++) {
			pointIndex = _xsectBathymetryData.getEnclosedPointIndex(j);
			_bathymetryData.getPointFeet(pointIndex, bPoint);
			double x3 = bPoint[CsdpFunctions.xIndex];
			double y3 = bPoint[CsdpFunctions.yIndex];

			// if the distance from the point to the cross-section line is
			// within the range defined by the binValue, add it to the
			// dataset
			int yearIndex = _bathymetryData.getYearIndex(pointIndex);
			short year = _bathymetryData.getYear(yearIndex);
			if (year>=minYear && year<=maxYear){
				_xsectBathymetryData.getEnclosedStationElevation(j, sePoint);
				stationRDA.put(index, sePoint[stationIndex]);
				elevationRDA.put(index, sePoint[elevationIndex]);
				index++;
			}else {
				rejectedPointSet.add(minYear+","+maxYear+","+year);
//				System.out.println("rejecting point: minyear, maxyear, year:"+minYear+","+maxYear+","+year);
			}
		} // for j: looping through all enclosed bathymetry points

		
		double[] stationArray = stationRDA.getArray();
		double[] elevationArray = elevationRDA.getArray();
		
		
		
		//		} // for i
		//now sort the return arrays by station in ascending order
		//		double[][] sortedArrays = {stationArray, elevationArray};

		CsdpFunctions.qsort(stationArray, elevationArray, 0, index-1);

		//		AsciiFileWriter asciiFileWriter = new AsciiFileWriter(_gui, "d:/temp/sortedData.txt");
		//		for (int i=0; i<stationArray.length; i++) {
		//			asciiFileWriter.writeLine(stationArray[i]+","+elevationArray[i]);
		//		}
		//		asciiFileWriter.close();
		//		
		//		System.exit(0);

		double[][] returnArrays = {stationArray, elevationArray};
		//		returnArrays = CsdpFunctions.qsort(returnArrays, 0, dataSetPointIndex-1);

		//		returnArrays[0] = stationArray;
		//		returnArrays[1] = elevationArray;

		
		System.out.println("end of makeBathymetryArrayForAutoXS: sizes of station, elevation arrays, index: "+stationArray.length+","+elevationArray.length+","+index);
		
		return returnArrays;
	}// makeBathymetryArrayForAutoXS

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
		_xsect = _net.getCenterline(_centerlineName).getXsect(n);
		_graph.setTitle(
				"Cross-section " + _centerlineName + "_" + _xsectNum + "," + " thickness=" + _thickness + " ft.");
		_xei.updateXsect(_xsect);
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
			//set background color to yellow if elevation in in intertidal zone or if part of space between the adjacent layer
			//is within intertidal zone.
			Color intertidalBackgroundColor = Color.YELLOW;
			Color nonIntertidalBackgroundColor = Color.white;
			for(int i=elevations.length-1; i>=0; i--) {
				Color backgroundColor = null;
				if(elevations[i]>=CsdpFunctions.INTERTIDAL_LOW_TIDE && elevations[i]<=CsdpFunctions.INTERTIDAL_HIGH_TIDE) {
					backgroundColor = intertidalBackgroundColor;
				}else {
					if(elevations[i]<CsdpFunctions.INTERTIDAL_LOW_TIDE) {
						if(i<elevations.length-1 && elevations[i+1]>CsdpFunctions.INTERTIDAL_LOW_TIDE) {
							backgroundColor = intertidalBackgroundColor;
						}else {
							backgroundColor = nonIntertidalBackgroundColor;
						}
					}else if(elevations[i]>CsdpFunctions.INTERTIDAL_HIGH_TIDE) {
						if(i>0 && elevations[i-1]<CsdpFunctions.INTERTIDAL_HIGH_TIDE) {
							backgroundColor = intertidalBackgroundColor;
						}else {
							backgroundColor = nonIntertidalBackgroundColor;
						}
					}else {
						backgroundColor = nonIntertidalBackgroundColor;
					}
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
			//scroll to top
			_dConveyanceTextPane.setCaretPosition(0);
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
	 * Automatically creates cross-section, by averaging nearby bathymetry data. Similar to code in XsectEditInteractor.addPoint method
	 */
	public void averageBathymetryForAutoXS() {
		//find range of bathymetry data point station coordinates
		//divide range into number of portions equal to numAutoXSPoints-1. 
		//exclude points with year after autoXSMinYear
		//from remaining points, calculate average elevation coordinate
		_colorByYearButton.doClick();
		int numAutoXSPoints = 200;

		double[][] bathymetryArrays = makeBathymetryArrayForAutoXS(CsdpFunctions.AUTO_XS_MIN_YEAR, CsdpFunctions.AUTO_XS_MAX_YEAR);
		double[] stationArray = bathymetryArrays[0];
		double[] elevationArray = bathymetryArrays[1];

		//now try to find tops of levees, by starting in the middle of the range of points (middle=average of min and max station values)
		//then finding points with the maximum elevation on each side of the middle of the range.
		double midPointStation = (stationArray[0]+stationArray[stationArray.length-1])/2.0;
		double leftLeveeCrownStation = -Double.MAX_VALUE;
		double rightLeveeCrownStation = -Double.MAX_VALUE;
		double leftLeveeCrownElevation = -Double.MAX_VALUE;
		double rightLeveeCrownElevation = -Double.MAX_VALUE;
		
		boolean leftLeveeCrownFound = false;
		boolean rightLeveeCrownFound = false;
		_leftLeveeCrownIndex = 0;
		_rightLeveeCrownIndex = stationArray.length-1;
		//if user has specified a minimum station value (which should be the station of the left levee crown, 
		//then get the value and find the index of the value in the station array that is at or to the left of that value;
		if(_autoXSMinStationTextField!=null && _autoXSMinStationTextField.getText().length()>0) {
			try {
				leftLeveeCrownStation = Double.parseDouble(_autoXSMinStationTextField.getText());
				int index = 0;
				while (index < stationArray.length && stationArray[index]<leftLeveeCrownStation) {
					_leftLeveeCrownIndex = index;
					index++;
				}
				leftLeveeCrownFound = true;
			}catch(Exception e) {
				_autoXSMinStationTextField.setText("");
			}
		}
		
		//if user has specified a maximum station value (which should be the station of the right levee crown, 
		//then get the value and find the index of the value in the station array that is at or to the right of that value;
		if(_autoXSMaxStationTextField.getText()!=null && _autoXSMaxStationTextField.getText().length()>0) {
			try {
				rightLeveeCrownStation = Double.parseDouble(_autoXSMaxStationTextField.getText());
				int index = stationArray.length-1;
				while (index > 0 && stationArray[index]>=rightLeveeCrownStation) {
					_rightLeveeCrownIndex = index;
					index--;
				}
				rightLeveeCrownFound = true;
			}catch(Exception e) {
				_autoXSMaxStationTextField.setText("");
			}
		}		

		//if user has not specified a minimum and/or a maximum station value, find 
		//index of the maximum elevation values to the left and right of center.
		for(int i=0; i<stationArray.length; i++) {
			double station = stationArray[i];
			double elevation = elevationArray[i];
			if(!leftLeveeCrownFound && station<midPointStation) {
				if(elevation>leftLeveeCrownElevation) {
					leftLeveeCrownElevation = elevation;
					_leftLeveeCrownIndex = i;
				}
			}else if(!rightLeveeCrownFound && station>=midPointStation) {
				if(elevation>rightLeveeCrownElevation) {
					rightLeveeCrownElevation = elevation;
					_rightLeveeCrownIndex = i;
				}
			}
		}

		_autoXSMinStationTextField.setValue(stationArray[_leftLeveeCrownIndex]);
		_autoXSMaxStationTextField.setValue(stationArray[_rightLeveeCrownIndex]);
		
		//now create new arrays which should go from the left levee crown to the right
		stationArray = Arrays.copyOfRange(stationArray, _leftLeveeCrownIndex, _rightLeveeCrownIndex);
		elevationArray = Arrays.copyOfRange(elevationArray, _leftLeveeCrownIndex, _rightLeveeCrownIndex);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Now create arrays of points that are elevation averages.
		//Divide the bathymetry data spatially into averaging ranges, with each range being of equal length,
		//and defined by a minimum and maximum station value.
		//Create an averaged elevation point, whose station value is in the middle of the averaging range.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		double minX = stationArray[0];
		double maxX = stationArray[stationArray.length-1];
		double averagingRangeSize = (maxX-minX)/numAutoXSPoints; //the size of the averaging range
		double[] averagingRangeMinX = new double[numAutoXSPoints]; //the left hand boundary of the averaging range
		double[] averagingRangeMaxX = new double[numAutoXSPoints]; //the right hand boundary of the averaging range
		//store the station and elevation values of the averaged points. We don't know how many points there will be at this point, because 
		//there may be no bathymtry points in some averaging ranges.
		ResizableDoubleArray averagedStationsRDA = new ResizableDoubleArray(); 
		ResizableDoubleArray averagedElevationsRDA = new ResizableDoubleArray();
		//the index of the averaging point that is "good", meaning that at least one bathymetry point was found in the averaging range
		int goodAveragePointIndex = 0; 

		for(int i=0; i<numAutoXSPoints; i++) {
			double startX = minX + (double)i*averagingRangeSize;
			double endX = startX+averagingRangeSize;
			averagingRangeMinX[i] = startX; 
			averagingRangeMaxX[i] = endX; 
		}

		int averagingRangeIndex = 0; // the index of the current averaging range
		double avg = 0.0; //the average for the current averaging range
		int averagingIndex=0; // the index of the current point in the current averaging range
		double averagingPointMinX = Double.MAX_VALUE; //the left hand boundary of the current averaging range
		double averagingPointMaxX = -Double.MAX_VALUE; //the right hand boundary of the current averaging range

		///////////////////////////////////////////////////////////////////////////////////////////
		//the current averaging range is either the first or the last
		//We want the endpoints of the cross-section to be near the tops of the levees, 
		//so averaging doesn't not work well for the endpoints.
		//For first and last averging ranges, don't average. Instead,
		//1. Calculate the vertical range of all the points in the averaging range
		//2. For the points that are in the top 20% of the averaging range, 
		//calculate the station as the average of all the stations, and 
		//calculate the elevation as the highest elevation. 
		//////////////////////////////////////////////////////////////////////////////////////////////////
		//also need to update text fields when min max station are automatically calculated.
		//start by creating slices: 1) the first averaging range 2) the last averaging range 3) all other averaging ranges.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		int endOfFirstAvgRangeIndex = 0;
		int beginOfLastAvgRangeIndex = 0;
		double firstRangeMinElev = Double.MAX_VALUE;
		double firstRangeMaxElev = -Double.MAX_VALUE;
		double lastRangeMinElev = Double.MAX_VALUE;
		double lastRangeMaxElev = -Double.MAX_VALUE;
		boolean beginLastRangeFound = false;
		
		for(int i=0; i<stationArray.length; i++) {
			double s = stationArray[i];
			double e = elevationArray[i];
			if(s<averagingRangeMaxX[0]) {
				endOfFirstAvgRangeIndex = i;
				firstRangeMinElev = Math.min(e, firstRangeMinElev);
				firstRangeMaxElev = Math.max(e, firstRangeMaxElev);
			}
			if(s>=averagingRangeMinX[numAutoXSPoints-1] && !beginLastRangeFound) {
				beginOfLastAvgRangeIndex = i;
				beginLastRangeFound = true;
			}else if(beginLastRangeFound) {
				lastRangeMinElev = Math.min(e, lastRangeMinElev);
				lastRangeMaxElev = Math.max(e, lastRangeMaxElev);
			}
		}

		//1. get index of end of first averaging range and beginning of last averaging range
		double[] stationArrayFirstRange = Arrays.copyOfRange(stationArray, 0, endOfFirstAvgRangeIndex);
		double[] elevationArrayFirstRange = Arrays.copyOfRange(elevationArray, 0, endOfFirstAvgRangeIndex);
		double[] stationArrayLastRange = Arrays.copyOfRange(stationArray, beginOfLastAvgRangeIndex, stationArray.length-1);
		double[] elevationArrayLastRange = Arrays.copyOfRange(elevationArray, beginOfLastAvgRangeIndex, stationArray.length-1);
		// replace original arrays with new arrays with first and last averaging ranges removed
		stationArray = Arrays.copyOfRange(stationArray, endOfFirstAvgRangeIndex+1, beginOfLastAvgRangeIndex-1);
		elevationArray = Arrays.copyOfRange(elevationArray, endOfFirstAvgRangeIndex+1, beginOfLastAvgRangeIndex-1);
		//2. First averaging range: 
		double top20PercentLevel = firstRangeMinElev+ (4.0/5.0) * (firstRangeMaxElev-firstRangeMinElev);
		double averageStationFirstRange = 0.0;
		for(int i=0; i<stationArrayFirstRange.length; i++) {
			if(elevationArrayFirstRange[i] >= top20PercentLevel) {
				averageStationFirstRange = (averageStationFirstRange*(double)averagingIndex + stationArrayFirstRange[i])/(double)(averagingIndex+1);
				averagingIndex++;
			}
		}
		averagingIndex=0;
		System.out.println("first range: average station, top20percentLevel, firstRangeMaxElevation="+averageStationFirstRange+","+top20PercentLevel+","+firstRangeMaxElev);

		top20PercentLevel = lastRangeMinElev+ (4.0/5.0) * (lastRangeMaxElev-lastRangeMinElev);
		double averageStationLastRange = 0.0;
		for(int i=0; i<stationArrayLastRange.length; i++) {
			if(elevationArrayLastRange[i] >= top20PercentLevel) {
				averageStationLastRange = (averageStationLastRange*(double)averagingIndex + stationArrayLastRange[i])/(double)(averagingIndex+1);
				averagingIndex++;
			}
		}
		
		averagingIndex=0;
		//now add first range points to arrays
		averagedStationsRDA.put(goodAveragePointIndex, averageStationFirstRange);
		averagedElevationsRDA.put(goodAveragePointIndex, firstRangeMaxElev);
		goodAveragePointIndex++;
		
		// loop through all bathymetry points, and calculate averages
		for(int i=0; i<stationArray.length; i++) {
			averagingPointMinX = averagingRangeMinX[averagingRangeIndex];
			averagingPointMaxX = averagingRangeMaxX[averagingRangeIndex];
			double x = stationArray[i];
			double y = elevationArray[i];

			//current averaging range is not the first or last. 
			//Create a new station value which is in the middle of the averaging range.
			//Create a new elevation value which is the average elevation of all the bathymetry points in the averaging range.
			if(x < averagingPointMaxX) {
				//the current point is inside the current averaging range. Adjust average and increment averagingIndex
				avg = (avg*(double)averagingIndex + y)/(double)(averagingIndex+1.0);
				averagingIndex++;
			}else {
				if(averagingIndex>0) {
					//the current point is outside (to the right of) the current averaging range, 
					//but some points in the averaging range have already been found.
					//Add the averaged point to the averaged bathymetry arrays, and increment the goodAveragePointIndex
					averagedStationsRDA.put(goodAveragePointIndex, averagingPointMinX+0.5*averagingRangeSize);
					averagedElevationsRDA.put(goodAveragePointIndex, avg);
					goodAveragePointIndex++;
				}else {
					//the current point is outside (to the right of) the current averaging range, and
					//there were no points found in the current averaging range. 
					//Loop through the averaging ranges until finding one that contains the current bathymetry point.
					while(x > averagingPointMaxX && averagingRangeIndex<averagingRangeMinX.length-1) {
						averagingRangeIndex++;
						averagingPointMaxX = averagingRangeMaxX[averagingRangeIndex];
						x = stationArray[i];
					}
				}
				avg=0.0;
				averagingIndex=0;
				if(averagingRangeIndex<averagingRangeMinX.length-1)
					averagingRangeIndex++;
			}
		}//for each bathymetry point

		//now add last range points to arrays
		averagedStationsRDA.put(goodAveragePointIndex, averageStationLastRange);
		averagedElevationsRDA.put(goodAveragePointIndex, lastRangeMaxElev);
		goodAveragePointIndex++;

		
		//now create averaged points array, which stores points using 1 array for each coordinate pair (station, elevation)
		double[] averagedStations = averagedStationsRDA.getArray();
		double[] averagedElevations = averagedElevationsRDA.getArray();
		_averagedBathymetryPoints = new double[averagedStations.length][2];
		for(int i=0; i<averagedStations.length; i++) {
			_averagedBathymetryPoints[i][0] = averagedStations[i];
			_averagedBathymetryPoints[i][1] = averagedElevations[i];
		}
		System.out.println("num averaged stations, elevations="+averagedStations.length+","+averagedElevations.length);
	}//averageBathymetryForAutoXS

	/*
	 * Must be called after averageBathymetryForAutoXS. Creates the cross-section drawing using the previous epsilon value.
	 * Called when the create xs button is clicked or when epsilon, min year, or max year are called.
	 */
	public void createAutoXS() {
		//use the line simplification algorithm to only keep inflection points
		double[][] simplifiedPoints = LineSimplification.simplifyLine(_averagedBathymetryPoints, CsdpFunctions.RAMER_DOUGLAS_PEUCKER_EPSILON);
		//replace all the points in the cross-section drawing
		_xsect.removeAllPoints();
		
		for(int i=0; i<simplifiedPoints.length; i++) {
			_xsect.addXsectPoint(XsectEditInteractor.ADD_RIGHT_POINT, simplifiedPoints[i][0], simplifiedPoints[i][1]);
		}
		updateDisplay();
		_xsect.setIsUpdated(true);
		_app.updateAllOpenCenterlineOrReachSummaries(_net.getSelectedCenterlineName());
		_gui.updateInfoPanel(_net.getSelectedCenterlineName());
		_autoXSEpsilonSlider.setSliderEnabled(true);
		_autoXSMinYearTextField.setEnabled(true);
		_autoXSMaxYearTextField.setEnabled(true);
		
		//now update metadata. Find index of last nonblank line
		String metadataText =  _metadataJTextArea.getText().trim();
		String[] lines = metadataText.split("\\R");
		String line = null;
		int mIndex=0;
		int lastNonBlankLineIndex = 0;

		//find idex of last nonblank line for comparison
		if(lines.length>0) {
			for(int i=0; i<lines.length; i++) {
				line=lines[i];
				if(line.trim().length() > 0) {
					lastNonBlankLineIndex=i;
				}
			}
		}

		String replaceText = lines[lastNonBlankLineIndex];
		while(_userInitialsString==null || (_userInitialsString.length()!=2 && _userInitialsString.length()!=3)) {
			_userInitialsString = JOptionPane.showInputDialog(_gui, "Enter your initials (for autoXS metadata entry; 2-3 characters)");
		}
		String metadataAddition =
				_userInitialsString+": AutoXS: "+
				CsdpFunctions.getCurrentDatetimeFormattedForDSM2InputComments() + 
				": Min Year, Max Year, epsilon="+ 
				CsdpFunctions.AUTO_XS_MIN_YEAR+","+
				CsdpFunctions.AUTO_XS_MAX_YEAR+","+
				CsdpFunctions.RAMER_DOUGLAS_PEUCKER_EPSILON;

		//if metadata addition date matches the date in the first nonblank metadata line, 
		//then replace the first nonblank metadata line with the metadata addition. Otherwise, add a new line.
		String[] firstNBParts = line.split(":");
		String[] newMDParts = metadataAddition.split(":"); 
		
		if(firstNBParts.length>=3 && newMDParts.length>=3 && 
				firstNBParts.length == newMDParts.length && 
				firstNBParts[0].equals(newMDParts[0]) && 
				firstNBParts[1].equals(newMDParts[1]) &&
				firstNBParts[2].equals(newMDParts[2])) {
//			replace with metadataAddition
			String content = _metadataJTextArea.getText();
			String modifiedContent = content.replace(replaceText, metadataAddition);
			try {
				_metadataJTextArea.setText(modifiedContent);
			}catch(java.lang.NullPointerException e) {
				_metadataJTextArea.append(modifiedContent);
			}
		}else {
			//append
			_metadataJTextArea.append(System.lineSeparator()+System.lineSeparator()+metadataAddition+System.lineSeparator());
		}
	}//autoCreateXS


	private class EpsilonChangeListener implements DocumentListener{
		private XsectGraph _xsectGraph;
		private SliderPanel _sliderPanel;
		public EpsilonChangeListener(XsectGraph xsectGraph) {
			_xsectGraph = xsectGraph;
		}

		private void updateEpsilon() {
			_sliderPanel = _xsectGraph.getSliderPanel();
			CsdpFunctions.RAMER_DOUGLAS_PEUCKER_EPSILON = _sliderPanel.getValue();
			_xsectGraph.createAutoXS();
		}

		public void insertUpdate(DocumentEvent e) {updateEpsilon();}
		public void removeUpdate(DocumentEvent e) {updateEpsilon();}
		public void changedUpdate(DocumentEvent e) {updateEpsilon();}
	}//class EpsilonChangeListener

	
	
	/**
	 * Listener for JFormattedTextField objects, allowing user to specify min and max years for automatic XS creation
	 * @author btom
	 *
	 */
	private class MinMaxStationFieldListener implements ActionListener{
		public static final int MAX = 0;
		public static final int MIN = 1;
		private XsectGraph _xsectGraph;
		public MinMaxStationFieldListener(XsectGraph xsectGraph) {
			_xsectGraph = xsectGraph;
		}
		/*
		 * When enter pressed, update the graph
		 */
		public void actionPerformed(ActionEvent arg0) {
			_xsectGraph.averageBathymetryForAutoXS();
			_xsectGraph.createAutoXS();
		}
	}//class MinMaxYearFieldListener
	/**
	 * Listener for JFormattedTextField objects, allowing user to specify min and max years for automatic XS creation
	 * @author btom
	 *
	 */
	private class MinMaxYearFieldListener implements ActionListener{
		public static final int MAX = 0;
		public static final int MIN = 1;
		private XsectGraph _xsectGraph;
		private int _minOrMax;
		private JFormattedTextField _parent;
		public MinMaxYearFieldListener(XsectGraph xsectGraph, JFormattedTextField parent, int minOrMax) {
			_xsectGraph = xsectGraph;
			_parent = parent;
			_minOrMax = minOrMax;
		}
		/*
		 * When enter pressed, update the graph
		 */
		public void actionPerformed(ActionEvent arg0) {
			if(_minOrMax==MIN) {
				CsdpFunctions.AUTO_XS_MIN_YEAR = Integer.parseInt(_parent.getText());
			}else if(_minOrMax==MAX) {
				CsdpFunctions.AUTO_XS_MAX_YEAR = Integer.parseInt(_parent.getText());
			}
			_xsectGraph.averageBathymetryForAutoXS();
			_xsectGraph.createAutoXS();
		}
	}//class MinMaxYearFieldListener

	public class AutoLeveeListener implements ActionListener {
		private XsectGraph _xsectGraph;
		public AutoLeveeListener(XsectGraph xsectGraph) {
			_xsectGraph = xsectGraph;
		}
		public void actionPerformed(ActionEvent e) {
			_xsectGraph._autoXSMinStationTextField.setText("");
			_xsectGraph._autoXSMaxStationTextField.setText("");
			_xsectGraph.averageBathymetryForAutoXS();
			_xsectGraph.createAutoXS();
		}
	}//class AutoLeveeListener

	
	/**
	 * called when keep button pressed.
	 */
	public void setChangesKept(boolean b) {
		_changesKept = b;
	}

	public SliderPanel getSliderPanel() {
		return _autoXSEpsilonSlider;
	}

	/**
	 * Only called when closing window to see if there are any changes that need
	 * to be saved (in memory, not in network file).
	 */
	public boolean getChangesKept() {
		return _changesKept;
	}

	//	public boolean getAddPointMode() {
	//		return _addUpstreamButton.isSelected() || _addDownstreamButton.isSelected();
	//	}	
	public boolean getAddBeginningPointMode() {
		return _addLeftPointButton.isSelected();
	}	
	public boolean getAddEndingPointMode() {
		return _addRightPointButton.isSelected();
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

	/*
	 * called when cross-section window is closing and updates are to be saved.
	 */
	public void updateMainWindowInfoPanel() {
		// TODO Auto-generated method stub
		//		_gui.updateInfoPanel(_net.getCenterline(_centerlineName));
		_gui.updateInfoPanel(_centerlineName);
	}

	double[][] _averagedBathymetryPoints;
	
	public int getXsectNum() {return _xsectNum;}
	public Xsect getXsect() {return _xsect;}
	public void redoNextPaint() {_gC.redoNextPaint();}
	public String getCenterlineName() {return _centerlineName;}

	public JPanel getGraphGECanvas() {return _gC;}
	public JPanel getConveyanceCharacteristicsAndMetadataPanel() {return _eastPanel;}

	public JTextPane getConveyanceCharacteristicsPanel() {return _dConveyanceTextPane;}
	public JScrollPane getMetadataPanel() {return _metadataScrollPane;}
	public Component getDConveyancePanel() {return _dConveyanceTextPane;}

	private int _leftLeveeCrownIndex = -Integer.MAX_VALUE;
	private int _rightLeveeCrownIndex = -Integer.MAX_VALUE;

}// class XsectGraph
