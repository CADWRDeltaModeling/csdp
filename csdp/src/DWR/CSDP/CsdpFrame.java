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
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import DWR.CSDP.NetworkMenu.NClearChannelsInp;
import DWR.CSDP.NetworkMenu.NCreateNetworkAllDSM2Chan;
import DWR.CSDP.NetworkMenu.NXSCheckReport;
import DWR.CSDP.ToolsMenu.TCreateDCDNodeLandmarkFile;
import DWR.CSDP.dialog.DataEntryDialog;

/**
 * Display Frame
 *
 * @author $Author:
 * @version $Id: CsdpFrame.java,v 1.7 2005/04/08 03:12:40 btom Exp $
 */
public class CsdpFrame extends JFrame {
	
	Border _raisedBevel = BorderFactory.createRaisedBevelBorder();

	JRadioButton _zoomBoxButton;
	JRadioButton _zoomPanButton;
	JRadioButton _selectPointsFor3dViewButton;
	JRadioButton _deleteCenterlinePointsInBoxButton;
	JRadioButton _deleteCenterlinePointsOutsideBoxButton;
	JRadioButton _exportChannelsInWindowButton;
	JButton _zoomFitButton;
	JButton _zoomUndoButton;

	ImageIcon _fileOpenIcon, _networkOpenIcon, _networkSaveIcon, _cursorIcon, _insertIcon, _movePointIcon, _addUpstreamPointIcon, 
		_addDownstreamPointIcon, _deleteIcon, _addXsectIcon, _removeXsectIcon, _moveXsectIcon;

	ImageIcon _viewIcon, _colorUniformIcon, _colorElevIcon, _colorSourceIcon, _colorYearIcon;
	ImageIcon _networkCalculateIcon, _cursorIconSelected, _insertIconSelected, _movePointIconSelected, _addUpstreamPointIconSelected,
		_addDownstreamPointIconSelected, _deleteIconSelected, _addXsectIconSelected, _removeXsectIconSelected, _moveXsectIconSelected;

	// ImageIcon _landmarkAddIcon, _landmarkEditIcon, _landmarkDeleteIcon,
	// _landmarkMoveIcon,
	// _landmarkAddSelectedIcon, _landmarkEditSelectedIcon,
	// _landmarkMoveSelectedIcon, _landmarkDeleteSelectedIcon;

	ImageIcon _colorUniformIconSelected, _colorElevIconSelected, _colorSourceIconSelected, _colorYearIconSelected,
			_filterYearIcon, _filterSourceIcon, _filterLabelIcon, _propOpenIcon, _zoomBoxIcon, _zoomBoxIconSelected,
			_zoomPanIcon, _zoomPanIconSelected, _zoomFitIcon, _zoomFitIconRollover, _zoomUndoIcon;

	ImageIcon _selectPointsFor3dViewIcon, _selectPointsFor3dViewIconSelected,	_specifyCenterlinesFor3dViewIcon;
	
	JButton _fileOpenButton, _networkOpenButton, _networkSaveButton, _networkCalculateButton, _propOpenButton,
			_filterYearButton, _filterSourceButton;
	JLabel _filterLabel;

	JRadioButton _colorUniformButton, _colorByElevButton, _colorBySourceButton, _colorByYearButton;

	/**
	 * turns all centerline edit modes off
	 */
	JRadioButton _cursorButton;
	/**
	 * turns insert centerline point mode on
	 */
	JRadioButton _insertButton;
	/**
	 * turns move centerline point mode on
	 */
	JRadioButton _moveButton;
	/**
	 * turns add centerline point mode on
	 */
	JRadioButton _addDownstreamPointButton;
	
	JRadioButton _addUpstreamPointButton;
	/**
	 * turns delete centerline point mode on
	 */
	JRadioButton _deleteButton;
	/**
	 * turns add xsect mode on
	 */
	JRadioButton _addXsectButton;
	/**
	 * turns remove xsect mode on
	 */
	JRadioButton _removeXsectButton;
	/**
	 * turns move xsect mode on
	 */
	JRadioButton _moveXsectButton;
	/**
	 * turns all other modes off
	 */
	// JRadioButton _invisibleRadioButton = new JRadioButton();
	/**
	 * view selected cross-section
	 */
	JRadioButton _viewXsectButton;
	
	/*
	 * This mode only used to selected a centerline to fill in a value in a DataEntryDialog
	 */
	JRadioButton _selectCenterlineForDataEntryDialogButton;

	// JRadioButton _landmarkOpenButton, _landmarkSaveButton,
	// _landmarkAddButton,
	// _landmarkEditButton, _landmarkMoveButton, _landmarkDeleteButton,
	// _landmarkAddSelectedButton, _landmarkEditSelectedButton,
	// _landmarkMoveSelectedButton, _landmarkDeleteSelectedButton;
	JButton _landmarkOpenButton, _landmarkSaveButton, _landmarkAddButton, _landmarkEditButton, _landmarkMoveButton,
			_landmarkDeleteButton, _landmarkAddSelectedButton, _landmarkEditSelectedButton, _landmarkMoveSelectedButton,
			_landmarkDeleteSelectedButton, _specifyCenterlinesFor3dViewButton;

//	private static final Dimension _iconSize = new Dimension(25, 25);
//	private static final Dimension _wideIconSize = new Dimension(35, 25);
//	public static final Dimension _colorByIconSize = new Dimension(40, 15);
	private static final int ICON_WIDTH = 38;
	private static final int ICON_HEIGHT = 38;
	private static final int COLOR_BY_ICON_WIDTH = 53;
	private static final int COLOR_BY_ICON_HEIGHT = 20;
	private static final int WIDE_ICON_WIDTH = 53;
	private static final int WIDE_ICON_HEIGHT = 38;
	// JButton _restoreButton = new JButton("Restore");
	// JButton _keepButton = new JButton("Keep");
	// protected JRadioButtonMenuItem _cMovePointMenuItem, _cAddPointMenuItem,
	// _cInsertPointMenuItem, _cDeletePointMenuItem, _cAddXsectMenuItem,
	// _cRemoveXsectMenuItem, _cMoveXsectMenuItem;

	private Landmark _landmark;
	private DigitalLineGraph _dDigitalLineGraph;
//	private DSMChannels _DSMChannels;
	private JMenuBar menubar;

	private JMenu cfFile, cfProperties, cfModify, cfDisplay, cfTools, cfNetwork, cfLandmark, cfCenterline, cfXsect,
			cfZoom, cfWindow, cfHelp;
	private JPopupMenu cfLandmarkPopup;

	private JMenuItem fNew, fOpen, bImportFromAsciiRaster, fClose, fSave, fSaveAs, fSaveAsNAVD88, fSaveZoomed, fSavePointsInsideOutsidePolygon, fMerge, 
		fExtract, fPrintPreview, fPrint, fPrintSetup, fConvert, fExit;
	private JMenuItem pLoad, pSave, pSaveAs;
	private JMenuItem mSource, mYear, mZSign, mErase, mRestore, mPurge, mStatus;
	private JMenuItem dParameters, dSource, dYear, dColorBy, dErased, oLandmark, cLandmarks, dDigitalLineGraph, clearDigitalLineGraph, 
			dCopyToClipboard, dElevBins;
	// JRadioButtonMenuItem dColorUniformRadioButton, dColorByDepthRadioButton,
	// dColorBySourceRadioButton, dColorByYearRadioButton;
	private JRadioButtonMenuItem dFitByBathymetryMenuItem, dFitByNetworkMenuItem, dFitByLandmarkMenuItem;
	private ButtonGroup _colorByButtonGroup, _fitByButtonGroup, _centerlineLandmarkEditButtonGroup;

	private JCheckBoxMenuItem oEchoTimeSeriesInput, oEchoXsectInput, oPrintXsectResults, oUseFremontWeir,
			oUseToeDrainRestriction, oEchoToeDrainInput;
	private JMenu tOpenWaterOptionsMenu, nExport, nExportOptions;
	private JMenuItem tCompareNetwork, tCalcRect, tOpenWaterCalc, tCreateDSM2ChanPolygons, tClosePolygonCenterlines,
		tRemoveAllCrossSections, tCreateDSM2OutputLocations, tCrossSectionSlideshow, tManningsDispersionSpatialDistribution, 
		tExtendCenterlinesToNodes, tCreateStraightlineChanForGridmap, tCreateDCDNodeLandmarkFile;
	// 1/3/2019 AWDSummary and dConveyance report are now obsolete. Network Summary report has this information. 
	private JMenuItem nOpen, nSave, nSaveAs, nSaveSpecifiedChannelsAs, nExportToWKT, nExportXsectMidpointCoordToWKT, nList, nSummary, nClearNetwork, nClearChannelsInp, nCreateAllChannelNetwork, 
		nDisplayReachSummary, nDisplay3dReachView, nSelectPointsFor3dReachView, nCalculate, nExportToSEFormat, nExportTo3DFormat,
		nExportChannelsInWindow, nExportXsectMetadataTable, nAWDSummaryReport, nXSCheckReport, nDConveyanceReport, nNetworkSummaryReport, nNetworkColorLegend;
	private JMenuItem lSave, lSaveAs, lExportToWKT, lAdd, lMove, lEdit, lDelete, lHelp;

	private JRadioButtonMenuItem lAddPopup, lMovePopup, lEditPopup, lDeletePopup, lHelpPopup;

	private JCheckBoxMenuItem noChannelLengthsOnly;
	private JMenuItem cCursor, cCreate, cDSMCreate, cRemove, cDisplaySummary, cView3d, cReverseCenterline, cPlotAllCrossSections, cDeletePointsInWindow,
		cDeletePointsOutsideWindow, cAddXSAtComputationalPoints, cRemoveAllCrossSections, cScaleCrossSectionLineLengths;
	/*
	 * For adjusting centerlines that are actually representations of polygons used to estimate channel volume
	 * Will move points to a given centerline, which is actually representing a leveee
	 */
	private JMenu cMovePolygonCenterlinePointsToLeveeCenterline;
	// JMenuItem cRemove;
	//// JMenuItem cRename;
	// JMenuItem cMovePoint, cAddPoint, cDelPoint,
	private JMenuItem cRestore, cKeep, cSplit, cJoin, cView, cInfo, cList, cSummary;
	private JMenuItem xAutoGen, xCreate, xRemove, xMove, xPosition, xView, xInfo;
	private JMenuItem xSummary, xAdjustLength, xExtractData;
	private JMenuItem zPan, zFit, zFactor, zBox, zUndo, zZoomToCenterline, zZoomToNode;
	private JMenuItem wCascade, wTile, wArrangeIcons, wCloseAll, wRepaint;
	private JMenuItem hContents, hUsingHelp, hAbout;

	private ActionListener _oLandmarkListener = null;
	private ActionListener _nSaveListener = null;
	private ActionListener _nSaveAsListener = null;
	private ActionListener _nSaveSpeficiedChannelsAsListener = null;
	private ActionEvent _nullActionEvent = new ActionEvent(this, 0, null);
	private ActionListener LSaveLandmarksListener;
	private ActionListener LSaveLandmarksAsListener;
	private static final int COLOR_BY_DEPTH = 0;
	private static final int COLOR_BY_SOURCE = 1;
	private static final int COLOR_BY_YEAR = 2;
	private JToolBar _legendPanel;
	private String _depthLegendTitle = "Elevation";
	private String _sourceLegendTitle = "Source";
	private String _yearLegendTitle = "Year";
	private JPanel _infoPanel;
	private JLabel _horDatumLabel = new JLabel("Horizontal Datum:");
	private JLabel _horDatumUnitsLabel = new JLabel("Hor. Datum Units:");
	private JLabel _verDatumLabel = new JLabel("Vertical Datum:");
	private JLabel _verDatumUnitsLabel = new JLabel("Ver. Datum Units:");

	private JLabel _centerlineLabel = new JLabel("Selected Centerline:");
	private JLabel _centerlineLengthLabel = new JLabel("Centerline length:");
	private JLabel _centerlineVolumeLabel = new JLabel("Centerline Volume:");
	private JLabel _centerlineMaxAreaRatioLabel = new JLabel("Centerline MAR:");
	private JLabel _xsectLabel = new JLabel("Selected Xsect:");
	private JLabel _mouseXLabel = new JLabel("X coordinate (UTM):");
	private JLabel _mouseYLabel = new JLabel("Y coordinate (UTM):");
	private JLabel _areaLabel = new JLabel("Xsect Area:");
	private JLabel _wetPLabel = new JLabel("Wetted Perimeter:");
	private JLabel _widthLabel = new JLabel("Top Width:");
	private JLabel _hydraulicDepthLabel = new JLabel("Hydraulic Depth:");
	private JLabel _bathymetryFileLabel = new JLabel("Bathymetry Filename:");
	private JLabel _networkFileLabel = new JLabel("Network Filename:");
	private JLabel _landmarkFileLabel = new JLabel("Landmark Filename:");
	private JLabel _propertiesFileLabel = new JLabel("Properties Filename:");
	private JLabel _dlgFileLabel = new JLabel("DLG Filename:");

	private static Vector _colorsVector = new Vector();
	private final int _initialWidth = 1200;
	private final int _initialHeight = 800;

	// default values
	private double _minElevBin = -40.0;
	private double _maxElevBin = 10.0;
	private int _numElevBins = 11;

	// for ngvd29 to navd88 corrections
	// private double _minElevBin = 1.95f;
	// private double _maxElevBin = 2.70f;
	// private int _numElevBins = 11;
	/**
	 * true if a centerline selected.
	 */
	private boolean _centerlineSelected = false;
	/**
	 * true if a xsect selected.
	 */
	private boolean _xsectSelected = false;

	private DataEntryDialog parentDialog;


	/**
	 * stores values for coloring bathymetry data in color by elevation mode
	 */
	private static double _elevationBins[];

	/**
	 * These are only for turning menus off.
	 */
	private static final boolean _addFileMenu = true;
	private static final boolean _addPropertiesMenu = true;
	private static final boolean _addDisplayMenu = true;
	private static final boolean _addNetworkMenu = true;
	private static final boolean _addLandmarkMenu = true;
	private static final boolean _addCenterlineMenu = true;
	private static final boolean _addXsectMenu = true;
	private static final boolean _addZoomMenu = true;
	private static final boolean _addToolsMenu = true;
	private static final boolean _addHelpMenu = true;

	private static final boolean _addZoomWindowOption = true;
	private static final boolean _addCompareNetworkOption = false;
	private static final boolean _addRectXSOption = true;
	private static final boolean _addOWACalcOption = true;
	private double[] _cursorPosition = new double[2];
	private final boolean DEBUG = false;
	private Hashtable<Integer, Color> _userSetColors = new Hashtable<Integer, Color>();

	public CsdpFrame(App app) {
		makeIconButtons();
		setTitle("Cross-Section Development Program Version " + CsdpFunctions.getVersion());
		// setIconImage(Toolkit.getDefaultToolkit().
		// createImage("DWR_Logo-1.0in.gif"));

		// only for JFrame
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		_app = app;

		setSize(getInitialWidth(), getInitialHeight());
		// setCurrentWidth(getInitialWidth());
		// setCurrentHeight(getInitialHeight());
		_dim = getSize();

		_canvas1.setPlotter(app._bathymetryPlot, _dim);

		///// _canvas1.setBackground(Color.white);

		createGui();
		// setColorByDepth();
		setResizable(true);
		setBackground(Color.white);
		pack();
		setVisible(true);
		setDefaultColors();
	}

	private void makeIconButtons() {
		URL bathUrl = this.getClass().getResource("images/FileOpenButton.png");
		URL propUrl = this.getClass().getResource("images/PropOpenButton.png");
		URL netOpenUrl = this.getClass().getResource("images/NetworkOpenButton.png");
		URL netSaveUrl = this.getClass().getResource("images/NetworkSaveButton.png");
		URL arrowUrl = this.getClass().getResource("images/ArrowButton.png");
		URL insertPointUrl = this.getClass().getResource("images/InsertPointButton.png");
		URL movePointUrl = this.getClass().getResource("images/MovePointButton.png");
		URL addUpstreamPointUrl = this.getClass().getResource("images/AddUpstreamPointButton.png");
		URL addDownstreamPointUrl = this.getClass().getResource("images/AddDownstreamPointButton.png");
		URL deletePointUrl = this.getClass().getResource("images/DeletePointButton.png");
		URL addXsectUrl = this.getClass().getResource("images/AddXsectButton.png");

		URL removeXsectUrl = this.getClass().getResource("images/RemoveXsectButton.png");
		URL moveXsectUrl = this.getClass().getResource("images/MoveXsectButton.png");

		URL viewXsectUrl = this.getClass().getResource("images/ViewXsectButton.png");

		URL colorUniformUrl = this.getClass().getResource("images/ColorUniformButton.png");
		URL colorElevUrl = this.getClass().getResource("images/ColorElevButton.png");
		URL colorSourceUrl = this.getClass().getResource("images/ColorSourceButton.png");
		URL colorYearUrl = this.getClass().getResource("images/ColorYearButton.png");
		URL filterSourceUrl = this.getClass().getResource("images/FilterSourceButton.png");
		URL filterYearUrl = this.getClass().getResource("images/FilterYearButton.png");
		URL filterLabelUrl = this.getClass().getResource("images/FilterLabel.png");

//		_fileOpenIcon = new ImageIcon(bathUrl);
//		_propOpenIcon = new ImageIcon(propUrl);
//		_networkOpenIcon = new ImageIcon(netOpenUrl);
//		_networkSaveIcon = new ImageIcon(netSaveUrl);
//		_cursorIcon = new ImageIcon(arrowUrl);
//		_insertIcon = new ImageIcon(insertPointUrl);
//		_moveIcon = new ImageIcon(movePointUrl);
//		_addIcon = new ImageIcon(addPointUrl);
//		_deleteIcon = new ImageIcon(deletePointUrl);
//		_addXsectIcon = new ImageIcon(addXsectUrl);
//		_removeXsectIcon = new ImageIcon(removeXsectUrl);
//		_moveXsectIcon = new ImageIcon(moveXsectUrl);

		_fileOpenIcon = CsdpFunctions.createScaledImageIcon(bathUrl, WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
		_propOpenIcon = CsdpFunctions.createScaledImageIcon(propUrl,WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
		_networkOpenIcon = CsdpFunctions.createScaledImageIcon(netOpenUrl,WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
		_networkSaveIcon = CsdpFunctions.createScaledImageIcon(netSaveUrl,WIDE_ICON_WIDTH, WIDE_ICON_HEIGHT);
		_cursorIcon = CsdpFunctions.createScaledImageIcon(arrowUrl,ICON_WIDTH, ICON_HEIGHT);
		_insertIcon = CsdpFunctions.createScaledImageIcon(insertPointUrl,ICON_WIDTH, ICON_HEIGHT);
		_movePointIcon = CsdpFunctions.createScaledImageIcon(movePointUrl,ICON_WIDTH, ICON_HEIGHT);
		
		_addUpstreamPointIcon = CsdpFunctions.createScaledImageIcon(addUpstreamPointUrl,ICON_WIDTH, ICON_HEIGHT);
		_addDownstreamPointIcon = CsdpFunctions.createScaledImageIcon(addDownstreamPointUrl,ICON_WIDTH, ICON_HEIGHT);
		_deleteIcon = CsdpFunctions.createScaledImageIcon(deletePointUrl,ICON_WIDTH, ICON_HEIGHT);
		_addXsectIcon = CsdpFunctions.createScaledImageIcon(addXsectUrl,ICON_WIDTH, ICON_HEIGHT);
		_removeXsectIcon = CsdpFunctions.createScaledImageIcon(removeXsectUrl,ICON_WIDTH, ICON_HEIGHT);
		_moveXsectIcon = CsdpFunctions.createScaledImageIcon(moveXsectUrl,ICON_WIDTH, ICON_HEIGHT);
		_viewIcon = CsdpFunctions.createScaledImageIcon(viewXsectUrl,ICON_WIDTH, ICON_HEIGHT);

		_colorUniformIcon = CsdpFunctions.createScaledImageIcon(colorUniformUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorElevIcon = CsdpFunctions.createScaledImageIcon(colorElevUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorSourceIcon = CsdpFunctions.createScaledImageIcon(colorSourceUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorYearIcon = CsdpFunctions.createScaledImageIcon(colorYearUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_filterSourceIcon = CsdpFunctions.createScaledImageIcon(filterSourceUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_filterYearIcon = CsdpFunctions.createScaledImageIcon(filterYearUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_filterLabelIcon = CsdpFunctions.createScaledImageIcon(filterLabelUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);

		URL networkCalculateUrl = this.getClass().getResource("images/NetworkCalculateButton.png");
		URL cursorIconSelectedUrl = this.getClass().getResource("images/ArrowButtonSelected.png");
		URL insertPointButtonSelectedUrl = this.getClass().getResource("images/InsertPointButtonSelected.png");
		URL movePointIconSelectedUrl = this.getClass().getResource("images/MovePointButtonSelected.png");
		URL addUpstreamPointIconSelectedUrl = this.getClass().getResource("images/AddUpstreamPointButtonSelected.png");
		URL addDownstreamPointIconSelectedUrl = this.getClass().getResource("images/AddDownstreamPointButtonSelected.png");
		URL deleteIconSelectedUrl = this.getClass().getResource("images/DeletePointButtonSelected.png");
		URL addXsectIconSelectedUrl = this.getClass().getResource("images/AddXsectButtonSelected.png");
		URL removeXsectIconSelectedUrl = this.getClass().getResource("images/RemoveXsectButtonSelected.png");
		URL moveXsectIconSelectedUrl = this.getClass().getResource("images/MoveXsectButtonSelected.png");
		URL colorUniformIconSelectedUrl = this.getClass().getResource("images/ColorUniformButtonSelected.png");
		URL colorElevIconSelectedUrl = this.getClass().getResource("images/ColorElevButtonSelected.png");
		URL colorSourceIconSelectedUrl = this.getClass().getResource("images/ColorSourceButtonSelected.png");
		URL colorYearIconSelectedUrl = this.getClass().getResource("images/ColorYearButtonSelected.png");
		URL zoomBoxIconUrl = this.getClass().getResource("images/ZoomBoxButton.png");
		URL zoomBoxIconSelectedUrl = this.getClass().getResource("images/ZoomBoxButtonSelected.png");
		URL zoomPanIconUrl = this.getClass().getResource("images/ZoomPanButton.png");
		URL zoomPanIconSelectedUrl = this.getClass().getResource("images/ZoomPanButtonSelected.png");
		URL zoomFitIconUrl = this.getClass().getResource("images/ZoomFitButton.png");
		URL zoomFitIconRolloverUrl = this.getClass().getResource("images/ZoomFitButtonRollover.png");
		URL zoomUndoIconUrl = this.getClass().getResource("images/Undo24.gif");

		URL selectPointsFor3dViewUrl = this.getClass().getResource("images/SelectPointsFor3dViewButton.png");
		URL selectPointsFor3dViewSelectedUrl = this.getClass().getResource("images/SelectPointsFor3dViewButtonSelected.png");
		URL specifyCenterlinesFor3dViewButtonUrl = this.getClass().getResource("images/SpecifyCenterlinesFor3dViewButton.png");
		// URL landmarkAddUrl =
		// this.getClass().getResource("images/LAddButton.gif");
		// URL landmarkEditUrl =
		// this.getClass().getResource("images/LEditButton.gif");
		// URL landmarkMoveUrl =
		// this.getClass().getResource("images/LMoveButton.gif");
		// URL landmarkDeleteUrl =
		// this.getClass().getResource("images/LDeleteButton.gif");
		// URL landmarkAddSelectedUrl =
		// this.getClass().getResource("images/LAddButtonSelected.gif");
		// URL landmarkEditSelectedUrl =
		// this.getClass().getResource("images/LEditButtonSelected.gif");
		// URL landmarkMoveSelectedUrl =
		// this.getClass().getResource("images/LMoveButtonSelected.gif");
		// URL landmarkDeleteSelectedUrl =
		// this.getClass().getResource("images/LDeleteButtonSelected.gif");

		_networkCalculateIcon = CsdpFunctions.createScaledImageIcon(networkCalculateUrl,ICON_WIDTH, ICON_HEIGHT);
		_cursorIconSelected = CsdpFunctions.createScaledImageIcon(cursorIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_insertIconSelected = CsdpFunctions.createScaledImageIcon(insertPointButtonSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_movePointIconSelected = CsdpFunctions.createScaledImageIcon(movePointIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_addUpstreamPointIconSelected = CsdpFunctions.createScaledImageIcon(addUpstreamPointIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_addDownstreamPointIconSelected = CsdpFunctions.createScaledImageIcon(addDownstreamPointIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_deleteIconSelected = CsdpFunctions.createScaledImageIcon(deleteIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_addXsectIconSelected = CsdpFunctions.createScaledImageIcon(addXsectIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_removeXsectIconSelected = CsdpFunctions.createScaledImageIcon(removeXsectIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_moveXsectIconSelected = CsdpFunctions.createScaledImageIcon(moveXsectIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		// landmark edit
		// _landmarkAddIcon = new ImageIcon(landmarkAddUrl);
		// _landmarkEditIcon = new ImageIcon(landmarkEditUrl);
		// _landmarkMoveIcon = new ImageIcon(landmarkMoveUrl);
		// _landmarkDeleteIcon = new ImageIcon(landmarkDeleteUrl);
		// _landmarkAddSelectedIcon = new ImageIcon(landmarkAddSelectedUrl);
		// _landmarkEditSelectedIcon = new ImageIcon(landmarkEditSelectedUrl);
		// _landmarkMoveSelectedIcon = new ImageIcon(landmarkMoveSelectedUrl);
		// _landmarkDeleteSelectedIcon = new
		// ImageIcon(landmarkDeleteSelectedUrl);

		_colorUniformIconSelected = CsdpFunctions.createScaledImageIcon(colorUniformIconSelectedUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorElevIconSelected = CsdpFunctions.createScaledImageIcon(colorElevIconSelectedUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorSourceIconSelected = CsdpFunctions.createScaledImageIcon(colorSourceIconSelectedUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		_colorYearIconSelected = CsdpFunctions.createScaledImageIcon(colorYearIconSelectedUrl,COLOR_BY_ICON_WIDTH, COLOR_BY_ICON_HEIGHT);
		
		_zoomBoxIcon = CsdpFunctions.createScaledImageIcon(zoomBoxIconUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomBoxIconSelected = CsdpFunctions.createScaledImageIcon(zoomBoxIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomPanIcon = CsdpFunctions.createScaledImageIcon(zoomPanIconUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomPanIconSelected = CsdpFunctions.createScaledImageIcon(zoomPanIconSelectedUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomFitIcon = CsdpFunctions.createScaledImageIcon(zoomFitIconUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomFitIconRollover = CsdpFunctions.createScaledImageIcon(zoomFitIconRolloverUrl,ICON_WIDTH, ICON_HEIGHT);
		_zoomUndoIcon = CsdpFunctions.createScaledImageIcon(zoomUndoIconUrl,ICON_WIDTH, ICON_HEIGHT);
		_selectPointsFor3dViewIcon = CsdpFunctions.createScaledImageIcon(selectPointsFor3dViewUrl, ICON_WIDTH, ICON_HEIGHT);
		_selectPointsFor3dViewIconSelected = CsdpFunctions.createScaledImageIcon(selectPointsFor3dViewSelectedUrl, ICON_WIDTH, ICON_HEIGHT);
		_specifyCenterlinesFor3dViewIcon = CsdpFunctions.createScaledImageIcon(specifyCenterlinesFor3dViewButtonUrl, ICON_WIDTH, ICON_HEIGHT);
		
		_fileOpenButton = new JButton(_fileOpenIcon);
		_propOpenButton = new JButton(_propOpenIcon);
		_networkOpenButton = new JButton(_networkOpenIcon);
		_networkSaveButton = new JButton(_networkSaveIcon);
		_networkCalculateButton = new JButton(_networkCalculateIcon);
		_colorUniformButton = new JRadioButton(_colorUniformIcon);
		_colorByElevButton = new JRadioButton(_colorElevIcon, true);
		_colorBySourceButton = new JRadioButton(_colorSourceIcon);
		_colorByYearButton = new JRadioButton(_colorYearIcon);
		_filterSourceButton = new JButton(_filterSourceIcon);
		_filterYearButton = new JButton(_filterYearIcon);
		_filterLabel = new JLabel(_filterLabelIcon);

		removeBackgroundAndBorder(_fileOpenButton);
		removeBackgroundAndBorder(_propOpenButton);
		removeBackgroundAndBorder(_networkOpenButton);
		removeBackgroundAndBorder(_networkSaveButton);
		removeBackgroundAndBorder(_networkCalculateButton);
		removeBackgroundAndBorder(_colorUniformButton);
		removeBackgroundAndBorder(_colorByElevButton);
		removeBackgroundAndBorder(_colorBySourceButton);
		removeBackgroundAndBorder(_colorByYearButton);
		removeBackgroundAndBorder(_filterSourceButton);
		
		_cursorButton = new JRadioButton(_cursorIcon);
		_insertButton = new JRadioButton(_insertIcon);
		_moveButton = new JRadioButton(_movePointIcon);
		_addDownstreamPointButton = new JRadioButton(_addDownstreamPointIcon);
		_addUpstreamPointButton = new JRadioButton(_addUpstreamPointIcon);
		_deleteButton = new JRadioButton(_deleteIcon);
		_addXsectButton = new JRadioButton(_addXsectIcon);
		_removeXsectButton = new JRadioButton(_removeXsectIcon);
		_moveXsectButton = new JRadioButton(_moveXsectIcon);
		_viewXsectButton = new JRadioButton(_viewIcon);
		_zoomBoxButton = new JRadioButton(_zoomBoxIcon);
		//this button currently not added to gui. Eventually add a button.
		_selectPointsFor3dViewButton = new JRadioButton(_selectPointsFor3dViewIcon);
		_specifyCenterlinesFor3dViewButton = new JButton(_specifyCenterlinesFor3dViewIcon);
		_zoomPanButton = new JRadioButton(_zoomPanIcon);
		_zoomFitButton = new JButton(_zoomFitIcon);
		_zoomUndoButton = new JButton(_zoomUndoIcon);
		_deleteCenterlinePointsInBoxButton = new JRadioButton("Delete Centerline Points in Box");
		_deleteCenterlinePointsOutsideBoxButton = new JRadioButton("Delete Centerline Points outside box");
		_selectCenterlineForDataEntryDialogButton = new JRadioButton("Select Centerline for DataEntryDialog button");
		_exportChannelsInWindowButton = new JRadioButton("Export channels in window");
		
		removeBackgroundAndBorder(_cursorButton);
		removeBackgroundAndBorder(_insertButton);
		removeBackgroundAndBorder(_moveButton);
		removeBackgroundAndBorder(_addDownstreamPointButton);
		removeBackgroundAndBorder(_addUpstreamPointButton);
		removeBackgroundAndBorder(_deleteButton);
		removeBackgroundAndBorder(_addXsectButton);
		removeBackgroundAndBorder(_removeXsectButton);
		removeBackgroundAndBorder(_moveXsectButton);
		removeBackgroundAndBorder(_viewXsectButton);
		removeBackgroundAndBorder(_zoomBoxButton);
		removeBackgroundAndBorder(_selectPointsFor3dViewButton);
		removeBackgroundAndBorder(_specifyCenterlinesFor3dViewButton);
		removeBackgroundAndBorder(_zoomPanButton);
		removeBackgroundAndBorder(_zoomFitButton);
		removeBackgroundAndBorder(_zoomUndoButton);
		removeBackgroundAndBorder(_deleteCenterlinePointsInBoxButton);
		removeBackgroundAndBorder(_deleteCenterlinePointsOutsideBoxButton);
		removeBackgroundAndBorder(_selectCenterlineForDataEntryDialogButton);
		removeBackgroundAndBorder(_exportChannelsInWindowButton);
		
		// _landmarkOpenButton = new JButton("Open Landmark File");
		// _landmarkSaveButton = new JButton("Save Landmark File");
		// _landmarkAddButton = new JButton("Add landmark");
		// _landmarkEditButton = new JButton("Edit landmark text");
		// _landmarkMoveButton = new JButton("Move landmark");
		// _landmarkDeleteButton = new JButton("Delete landmark");
		// _landmarkAddSelectedButton = new
		// JRadioButton(_landmarkAddSelectedIcon);
		// _landmarkEditSelectedButton = new
		// JRadioButton(_landmarkEditSelectedIcon);
		// _landmarkMoveSelectedButton = new
		// JRadioButton(_landmarkMoveSelectedIcon);
		// _landmarkDeleteSelectedButton = new
		// JRadioButton(_landmarkDeleteSelectedIcon);
	}

	/*
	 * For buttons with icons. Removes the background and border.
	 */
	private void removeBackgroundAndBorder(AbstractButton button) {
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
	}
	
	/**
	 * makes menus and buttons; creates and registers listeners
	 */
	public void createGui() {
		// editing buttons
		updateElevBinValues(getMinElevBin(), getMaxElevBin(), getNumElevBins());
		//// JPanel btnPanel = new JPanel(true);
		JToolBar btnPanel = new JToolBar();
		btnPanel.setFloatable(false);

//		_fileOpenButton.setPreferredSize(_wideIconSize);
//		_propOpenButton.setPreferredSize(_wideIconSize);
//		_networkOpenButton.setPreferredSize(_wideIconSize);
//		_networkSaveButton.setPreferredSize(_wideIconSize);
//		_cursorButton.setPreferredSize(_iconSize);
//		_colorUniformButton.setPreferredSize(_colorByIconSize);
//		_colorByElevButton.setPreferredSize(_colorByIconSize);
//		_colorBySourceButton.setPreferredSize(_colorByIconSize);
//		_colorByYearButton.setPreferredSize(_colorByIconSize);
//		_filterSourceButton.setPreferredSize(_colorByIconSize);
//		_filterYearButton.setPreferredSize(_colorByIconSize);
		// _moveButton.setPreferredSize(_iconSize);
		// _insertButton.setPreferredSize(_iconSize);
		// _addButton.setSize(_iconSize);
		// _deleteButton.setPreferredSize(_iconSize);
		// _addXsectButton.setPreferredSize(_iconSize);
		// _removeXsectButton.setPreferredSize(_iconSize);
		// _moveXsectButton.setPreferredSize(_iconSize);
		// _viewXsectButton.setPreferredSize(_iconSize);
		// _networkCalculateButton.setPreferredSize(_iconSize);
		// _zoomBoxButton.setPreferredSize(_iconSize);
		// _zoomPanButton.setPreferredSize(_iconSize);
//		_zoomFitButton.setPreferredSize(_iconSize);
//		_zoomUndoButton.setPreferredSize(_iconSize);

		_cursorButton.setSelectedIcon(_cursorIconSelected);
		_colorUniformButton.setSelectedIcon(_colorUniformIconSelected);
		_colorByElevButton.setSelectedIcon(_colorElevIconSelected);
		_colorBySourceButton.setSelectedIcon(_colorSourceIconSelected);
		_colorByYearButton.setSelectedIcon(_colorYearIconSelected);
		_moveButton.setSelectedIcon(_movePointIconSelected);
		_insertButton.setSelectedIcon(_insertIconSelected);
		_addUpstreamPointButton.setSelectedIcon(_addUpstreamPointIconSelected);
		_addDownstreamPointButton.setSelectedIcon(_addDownstreamPointIconSelected);
		_deleteButton.setSelectedIcon(_deleteIconSelected);
		_addXsectButton.setSelectedIcon(_addXsectIconSelected);
		_removeXsectButton.setSelectedIcon(_removeXsectIconSelected);
		_moveXsectButton.setSelectedIcon(_moveXsectIconSelected);
		_zoomBoxButton.setSelectedIcon(_zoomBoxIconSelected);
		_zoomPanButton.setSelectedIcon(_zoomPanIconSelected);
		_selectPointsFor3dViewButton.setSelectedIcon(_selectPointsFor3dViewIconSelected);
		
		// _landmarkAddButton.setSelectedIcon(_landmarkAddSelectedIcon);
		// _landmarkEditButton.setSelectedIcon(_landmarkEditSelectedIcon);
		// _landmarkMoveButton.setSelectedIcon(_landmarkMoveSelectedIcon);
		// _landmarkDeleteButton.setSelectedIcon(_landmarkDeleteSelectedIcon);

		// rollover doesn't work?
		// _zoomFitButton.setRolloverEnabled(true);
		// _zoomFitButton.setRolloverIcon(_zoomFitIconRollover);
		// _zoomFitButton.setRolloverSelectedIcon(_zoomFitIconRollover);

		_fileOpenButton.setBorder(_raisedBevel);
		_propOpenButton.setBorder(_raisedBevel);
		_networkOpenButton.setBorder(_raisedBevel);
		_networkSaveButton.setBorder(_raisedBevel);
		_cursorButton.setBorder(_raisedBevel);
		_networkCalculateButton.setBorder(_raisedBevel);

		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(_fileOpenButton);
		btnPanel.add(_propOpenButton);
		btnPanel.add(_networkOpenButton);
		btnPanel.add(_networkSaveButton);

		ButtonGroup colorByGroup = new ButtonGroup();
		colorByGroup.add(_colorUniformButton);
		colorByGroup.add(_colorByElevButton);
		colorByGroup.add(_colorBySourceButton);
		colorByGroup.add(_colorByYearButton);
		// color panel
		JPanel colorByPanel = new JPanel(new GridLayout(2, 2));
		colorByPanel.setBorder(_raisedBevel);
		colorByPanel.add(_colorUniformButton);
		colorByPanel.add(_colorByElevButton);
		colorByPanel.add(_colorBySourceButton);
		colorByPanel.add(_colorByYearButton);
		// filter panel
		JPanel filterPanel = new JPanel(new GridLayout(2, 1));
		JPanel filterLabelPanel = new JPanel(new GridLayout(1, 1));
		JPanel filterButtonPanel = new JPanel(new GridLayout(1, 2));
		filterLabelPanel.setForeground(Color.white);
		filterLabelPanel.add(_filterLabel);
		filterButtonPanel.add(_filterSourceButton);
		filterButtonPanel.add(_filterYearButton);
		filterPanel.add(filterLabelPanel);
		filterPanel.add(filterButtonPanel);
		filterPanel.setBorder(_raisedBevel);
		btnPanel.add(filterPanel);

		btnPanel.add(colorByPanel);
		btnPanel.add(_cursorButton);
		btnPanel.add(_moveButton);
		btnPanel.add(_insertButton);
		btnPanel.add(_addUpstreamPointButton);
		btnPanel.add(_addDownstreamPointButton);
		btnPanel.add(_deleteButton);
		btnPanel.add(_addXsectButton);
		btnPanel.add(_moveXsectButton);
		btnPanel.add(_removeXsectButton);
		btnPanel.add(_viewXsectButton);
		btnPanel.add(_selectPointsFor3dViewButton);
		btnPanel.add(_specifyCenterlinesFor3dViewButton);
		btnPanel.add(_zoomBoxButton);
		btnPanel.add(_zoomPanButton);
		// don't use these buttons; better to right click
		// btnPanel.add(_landmarkAddButton);
		// btnPanel.add(_landmarkEditButton);
		// btnPanel.add(_landmarkMoveButton);
		// btnPanel.add(_landmarkDeleteButton);

		_centerlineLandmarkEditButtonGroup = new ButtonGroup();
		_centerlineLandmarkEditButtonGroup.add(_cursorButton);
		_centerlineLandmarkEditButtonGroup.add(_moveButton);
		_centerlineLandmarkEditButtonGroup.add(_insertButton);
		_centerlineLandmarkEditButtonGroup.add(_addUpstreamPointButton);
		_centerlineLandmarkEditButtonGroup.add(_addDownstreamPointButton);
		_centerlineLandmarkEditButtonGroup.add(_deleteButton);
		_centerlineLandmarkEditButtonGroup.add(_addXsectButton);
		_centerlineLandmarkEditButtonGroup.add(_moveXsectButton);
		_centerlineLandmarkEditButtonGroup.add(_removeXsectButton);
		_centerlineLandmarkEditButtonGroup.add(_zoomBoxButton);
		_centerlineLandmarkEditButtonGroup.add(_selectPointsFor3dViewButton);
		_centerlineLandmarkEditButtonGroup.add(_zoomPanButton);
		_centerlineLandmarkEditButtonGroup.add(_deleteCenterlinePointsInBoxButton);
		_centerlineLandmarkEditButtonGroup.add(_deleteCenterlinePointsOutsideBoxButton);
		_centerlineLandmarkEditButtonGroup.add(_selectCenterlineForDataEntryDialogButton);
		_centerlineLandmarkEditButtonGroup.add(_exportChannelsInWindowButton);
		
		btnPanel.add(_zoomUndoButton);
		btnPanel.add(_zoomFitButton);
		btnPanel.add(_networkCalculateButton);

		_fileOpenButton.setToolTipText("open bathymetry file");
		_propOpenButton.setToolTipText("open properties file");
		_networkOpenButton.setToolTipText("open network file");
		_networkSaveButton.setToolTipText("save network file");
		_cursorButton.setToolTipText("select centerline/xs");
		_colorUniformButton.setToolTipText("Color bathymetry uniformly");
		_colorByElevButton.setToolTipText("Color bathymetry by elevation");
		_colorBySourceButton.setToolTipText("Color bathymetry by source");
		_colorByYearButton.setToolTipText("Color bathymetry by year");
		_filterSourceButton.setToolTipText("Filter bathymetry by source");
		_filterYearButton.setToolTipText("Filter bathymetry by year");
		_moveButton.setToolTipText("move centerline point ");
		_addUpstreamPointButton.setToolTipText("Add upstream centerline point");
		_addDownstreamPointButton.setToolTipText("Add downstream centerline point ");
		_insertButton.setToolTipText("insert centerline point ");
		_deleteButton.setToolTipText("delete centerline point ");
		_addXsectButton.setToolTipText("add cross-section line ");
		_removeXsectButton.setToolTipText("remove cross-section line ");
		_moveXsectButton.setToolTipText("move cross-section line ");
		// _landmarkAddButton.setToolTipText("Add landmark");
		// _landmarkEditButton.setToolTipText("Edit landmark");
		// _landmarkMoveButton.setToolTipText("Move landmark");
		// _landmarkDeleteButton.setToolTipText("Delete landmark");
		_zoomBoxButton.setToolTipText("draw a box for zooming ");
		_zoomPanButton.setToolTipText("Pan");
		_zoomUndoButton.setToolTipText("Undo last zoom/pan");
		_zoomFitButton.setToolTipText("fit all data in window");
		_viewXsectButton.setToolTipText("view cross-section ");
		_networkCalculateButton.setToolTipText("Calculate Network");
		_specifyCenterlinesFor3dViewButton.setToolTipText("Specify centerlines for 3d Bathymetry/cross-section plot");
		_selectPointsFor3dViewButton.setToolTipText("Draw a window to select data for 3d Bathymetry/cross-section plot");
		_specifyCenterlinesFor3dViewButton.setEnabled(false);
		_selectPointsFor3dViewButton.setEnabled(false);
		
		// _infoPanel = new JToolBar();
		// _infoPanel.setLayout(new GridLayout(3,4));
		// _infoPanel.setFloatable(true);
		// The info panel is at the bottom of the window
		_infoPanel = new JPanel();
		_infoPanel.setLayout(new GridLayout(0, 4));
		//// _infoPanel.setOpaque(true);
		_infoPanel.add(_horDatumLabel);
		_infoPanel.add(_horDatumUnitsLabel);
		_infoPanel.add(_verDatumLabel);
		_infoPanel.add(_verDatumUnitsLabel);
		_infoPanel.add(_centerlineLabel);
		_infoPanel.add(_xsectLabel);
		_infoPanel.add(_mouseXLabel);
		_infoPanel.add(_mouseYLabel);

		//11/2018: remove hydraulic depth, shift others over and add centerline length
		_infoPanel.add(_centerlineLengthLabel);
		_infoPanel.add(_centerlineVolumeLabel);
		_infoPanel.add(_centerlineMaxAreaRatioLabel);
		_infoPanel.add(new JLabel(""));
		_infoPanel.add(new JLabel(""));
		_centerlineMaxAreaRatioLabel.setToolTipText("largest cross-sectiontal area/smallest");
		_infoPanel.add(_areaLabel);
		_infoPanel.add(_wetPLabel);
		_infoPanel.add(_widthLabel);
//		_infoPanel.add(_hydraulicDepthLabel);
		
		_infoPanel.add(_bathymetryFileLabel);
		_infoPanel.add(_networkFileLabel);
		_infoPanel.add(_landmarkFileLabel);
		_infoPanel.add(_propertiesFileLabel);

		menubar = new JMenuBar();
		this.setJMenuBar(menubar);

		// add Canvas to ScrollPane to CardLayout to Frame and set its
		// listeners;
		_planViewJPanel.setLayout(new BorderLayout());

		// replaced by new zoom feature. no scrolling needed
		// _sp1 = new JScrollPane(_canvas1,
		// JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		// JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// _sp1.setPreferredSize(new Dimension(_initialWidth,_initialHeight));
		// //IMPORTANT! SIMPLE_SCROLL_MODE or BACKINGSTORE_SCROLL_MODE
		// //must be used, or redrawing will be insufficient when stuff comes
		// //into viewport from outside viewport. Backingstore uses extra
		// memory,
		// //so if an outOfMemoryError is thrown when trying to zoom in too far,
		// //it won't be able to zoom back out. Simple is slower, but uses less
		// mem.
		// _sp1.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		// // _panelObjects.put("Plan View ScrollPane", _sp1);
		// _planViewJPanel.add("Plan View ScrollPane", _sp1);

		// do this instead of adding the scroll pane to the panel
		_planViewJPanel.add(_canvas1, BorderLayout.CENTER);

		// this won't work unless all whitespace removed from canvas
		// Box northBox = new Box(BoxLayout.Y_AXIS);
		// northBox.add(Box.createVerticalGlue());
		// Box westBox = new Box(BoxLayout.X_AXIS);
		// westBox.add(Box.createVerticalGlue());
		// Box eastBox = new Box(BoxLayout.X_AXIS);
		// eastBox.add(Box.createVerticalGlue());
		// Box southBox = new Box(BoxLayout.Y_AXIS);
		// southBox.add(Box.createVerticalGlue());
		// _planViewJPanel.add(northBox, BorderLayout.NORTH);
		// _planViewJPanel.add(westBox, BorderLayout.WEST);
		// _planViewJPanel.add(eastBox, BorderLayout.EAST);
		// _planViewJPanel.add(southBox, BorderLayout.SOUTH);

		_networkInteractor = new NetworkInteractor(this, _canvas1, _app);

		// ZoomInteractor zi = new ZoomInteractor(_canvas1);
		// _canvas1.addMouseListener(zi);
		// _canvas1.addMouseMotionListener(zi);
		_li = new LandmarkInteractor(this, _canvas1, _app);

		_canvas1.addMouseListener(_networkInteractor);
		_canvas1.addMouseMotionListener(_networkInteractor);
		_canvas1.addMouseListener(_li);
		_canvas1.addMouseMotionListener(_li);

		// is this necessary?????????????????????????????????/
		JPanel jp = new JPanel();
		jp.setOpaque(true);
		setContentPane(jp);

		// _legendPanel = new JPanel();
		_legendPanel = new JToolBar();
		_legendPanel.setFloatable(false);
		jp.setLayout(new BorderLayout());
		jp.add("Center", _planViewJPanel);
		jp.add("North", btnPanel);
		jp.add("East", _legendPanel);
		jp.add("South", _infoPanel);
		/*
		 * File menu
		 */
		cfFile = new JMenu("Bathymetry");
		cfFile.add(fOpen = new JMenuItem("Open File"));
		cfFile.add(bImportFromAsciiRaster = new JMenuItem("Import Bathymetry from ASCII Raster"));
		cfFile.add(fClose = new JMenuItem("Close File"));
		cfFile.addSeparator();
		cfFile.add(fSave = new JMenuItem("Save File"));
		cfFile.add(fSaveAs = new JMenuItem("Save File As..."));
		cfFile.add(fSaveAsNAVD88 = new JMenuItem("Save As NAVD88..."));
		cfFile.add(fSaveZoomed = new JMenuItem("Save Zoomed data"));
		cfFile.add(fSavePointsInsideOutsidePolygon = new JMenuItem("Save Points inside/outside polygon"));
		cfFile.addSeparator();
		cfFile.add(fConvert = new JMenuItem("Convert Bathymetry File(s)"));
		// cfFile.add(fMerge = new JMenuItem("Merge"));
		// cfFile.add(fExtract = new JMenuItem("Extract"));
		// cfFile.addSeparator();
		// cfFile.add(fPrintPreview = new JMenuItem("Print Preview"));
		// cfFile.add(fPrint = new JMenuItem("Print..."));
		// cfFile.add(fPrintSetup = new JMenuItem("Print Setup"));
		// cfFile.addSeparator();
		cfFile.add(fExit = new JMenuItem("Exit"));

		cfFile.setMnemonic(KeyEvent.VK_F);
		fOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fOpen.setMnemonic(KeyEvent.VK_O);
		fSave.setMnemonic(KeyEvent.VK_S);
		fSaveAs.setMnemonic(KeyEvent.VK_A);
		fExit.setMnemonic(KeyEvent.VK_X);
		fExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

		// cfFile.add(fExit = new JMenuItem("Exit", KeyEvent.VK_X));
		if (_addFileMenu)
			menubar.add(cfFile);

		// create and register action listener objects for the File menu items
		FileMenu fileMenu = new FileMenu(_app);
		ActionListener fOpenListener = fileMenu.new FOpen(this);
		ActionListener bImportFromAsciiRasterListener = fileMenu.new BImportFromAsciiRaster(this);
		ActionListener fCloseListener = fileMenu.new FClose();
		ActionListener fSaveListener = fileMenu.new FSave();
		ActionListener fSaveAsListener = fileMenu.new FSaveAs(this);
		ActionListener fSaveAsNAVD88Listener = fileMenu.new FSaveAsNAVD88(this);
		ActionListener fSaveZoomedListener = fileMenu.new FSaveBathZoomed(this);
		ActionListener fSavePointsInsideOutsidePolygonListener = fileMenu.new FSaveBathInsideOutsidePolygon(this);
		// ActionListener fMergeListener = fileMenu.new FMerge();
		// ActionListener fExtractListener = fileMenu.new FExtract();
		ActionListener fPrintPreviewListener = fileMenu.new FPrintPreview();
		ActionListener fPrintListener = fileMenu.new FPrint();
		ActionListener fPrintSetupListener = fileMenu.new FPrintSetup();
		ActionListener fConvertListener = fileMenu.new FConvert(this);
		EventListener fExitListener = fileMenu.new FExit(this);

		addWindowListener((WindowListener) fExitListener);
		addComponentListener(fileMenu.new FResizeListener(this));

		// fNew.addActionListener(fNewListener);
		fOpen.addActionListener(fOpenListener);
		bImportFromAsciiRaster.addActionListener(bImportFromAsciiRasterListener);
		fClose.addActionListener(fCloseListener);
		fSave.addActionListener(fSaveListener);
		fSaveAs.addActionListener(fSaveAsListener);
		fSaveAsNAVD88.addActionListener(fSaveAsNAVD88Listener);
		fSaveZoomed.addActionListener(fSaveZoomedListener);
		fSavePointsInsideOutsidePolygon.addActionListener(fSavePointsInsideOutsidePolygonListener);
		// fMerge.addActionListener(fMergeListener);
		// fExtract.addActionListener(fExtractListener);
		// fPrintPreview.addActionListener(fPrintPreviewListener);
		// fPrint.addActionListener(fPrintListener);
		// fPrintSetup.addActionListener(fPrintSetupListener);
		fConvert.addActionListener(fConvertListener);
		fExit.addActionListener((ActionListener) fExitListener);

		_fileOpenButton.addActionListener(fOpenListener);

		/*
		 * Properties menu
		 */
		cfProperties = new JMenu("Properties");
		cfProperties.add(pLoad = new JMenuItem("Load"));
		cfProperties.add(pSave = new JMenuItem("Save"));
		cfProperties.add(pSaveAs = new JMenuItem("SaveAs"));

		cfProperties.setMnemonic(KeyEvent.VK_P);
		pLoad.setMnemonic(KeyEvent.VK_L);
		pSave.setMnemonic(KeyEvent.VK_S);
		pSaveAs.setMnemonic(KeyEvent.VK_A);

		PropertiesMenu propertiesMenu = new PropertiesMenu(_app);
		ActionListener pLoadListener = propertiesMenu.new PLoad(this);
		ActionListener pSaveListener = propertiesMenu.new PSave(this);
		ActionListener pSaveAsListener = propertiesMenu.new PSaveAs(this);
		pLoad.addActionListener(pLoadListener);
		_propOpenButton.addActionListener(pLoadListener);
		pSave.addActionListener(pSaveListener);
		pSaveAs.addActionListener(pSaveAsListener);
		if (_addPropertiesMenu)
			menubar.add(cfProperties);

		/*
		 * Display menu
		 */
		cfDisplay = new JMenu("Display");
		cfDisplay.setMnemonic(KeyEvent.VK_D);
		cfDisplay.add(dParameters = new JMenuItem("Parameters"));
		dParameters.setMnemonic(KeyEvent.VK_A);
		cfDisplay.addSeparator();
		cfDisplay.add(dSource = new JMenuItem("Filter By Source"));
		cfDisplay.add(dYear = new JMenuItem("Filter By Year"));
		cfDisplay.addSeparator();

		// JMenu cb = new JMenu("Color By");
		// _colorByButtonGroup = new ButtonGroup();

		// _colorByButtonGroup.add
		// (dColorUniformRadioButton = new JRadioButtonMenuItem("Uniform"));
		// _colorByButtonGroup.add
		// (dColorByDepthRadioButton = new JRadioButtonMenuItem("Depth",true));
		// _colorByButtonGroup.add
		// (dColorBySourceRadioButton = new JRadioButtonMenuItem("Source"));
		// _colorByButtonGroup.add
		// (dColorByYearRadioButton = new JRadioButtonMenuItem("Year"));
		// cb.add(dColorUniformRadioButton);
		// cb.add(dColorByDepthRadioButton);
		// cb.add(dColorBySourceRadioButton);
		// cb.add(dColorByYearRadioButton);
		// cfDisplay.add(cb);

		JMenu fb = new JMenu("Fit By");
		_fitByButtonGroup = new ButtonGroup();

		_fitByButtonGroup.add(dFitByBathymetryMenuItem = new JRadioButtonMenuItem("Bathymetry", true));
		_fitByButtonGroup.add(dFitByNetworkMenuItem = new JRadioButtonMenuItem("Network"));
		_fitByButtonGroup.add(dFitByLandmarkMenuItem = new JRadioButtonMenuItem("Landmark"));
		fb.add(dFitByBathymetryMenuItem);
		fb.add(dFitByNetworkMenuItem);
		fb.add(dFitByLandmarkMenuItem);
		cfDisplay.add(fb);

		// cfDisplay.add(dErased = new MenuItem("Erased"));
		// cfDisplay.addSeparator();
		cfDisplay.add(dDigitalLineGraph = new JMenuItem("Digital Line Graph(Channel Outline)"));
		cfDisplay.add(clearDigitalLineGraph = new JMenuItem("Clear Digital Line Graph (Channel Outline)"));
		cfDisplay.addSeparator();
		cfDisplay.add(dElevBins = new JMenuItem("Change Elevation Bins"));
		// cfDisplay.addSeparator();
		// cfDisplay.add(dCopyToClipboard = new JMenuItem("Copy to Clipboard"));
		if (_addDisplayMenu)
			menubar.add(cfDisplay);

		DisplayMenu displayMenu = new DisplayMenu(_app, _net);
		ActionListener dParametersListener = displayMenu.new DParameters(this);
		ActionListener dDigitalLineGraphListener = displayMenu.new DDigitalLineGraph(this);
		ActionListener clearDigitalLineGraphListener = displayMenu.new ClearDigitalLineGraph();
		ActionListener dSourceListener = displayMenu.new DSource(this);
		ActionListener dYearListener = displayMenu.new DYear(this);
		ItemListener dColorUniformListener = displayMenu.new DColorUniform(this);
		ItemListener dColorByElevListener = displayMenu.new DColorByElev(this);
		ItemListener dColorBySourceListener = displayMenu.new DColorBySource(this);
		ItemListener dColorByYearListener = displayMenu.new DColorByYear(this);
		EventListener dFitByBathymetryListener = displayMenu.new DFitByBathymetry(this);
		EventListener dFitByNetworkListener = displayMenu.new DFitByNetwork(this);
		EventListener dFitByLandmarkListener = displayMenu.new DFitByLandmark(this);
		ActionListener dElevBinsListener = displayMenu.new DElevBins(this);

		dParameters.addActionListener(dParametersListener);
		dDigitalLineGraph.addActionListener(dDigitalLineGraphListener);
		clearDigitalLineGraph.addActionListener(clearDigitalLineGraphListener);
		dSource.addActionListener(dSourceListener);
		dYear.addActionListener(dYearListener);
		dElevBins.addActionListener(dElevBinsListener);

		_filterSourceButton.addActionListener(dSourceListener);
		_filterYearButton.addActionListener(dYearListener);

		_colorUniformButton.addItemListener(dColorUniformListener);
		_colorByElevButton.addItemListener(dColorByElevListener);
		_colorBySourceButton.addItemListener(dColorBySourceListener);
		_colorByYearButton.addItemListener(dColorByYearListener);

		// dColorUniformRadioButton.addItemListener((ItemListener)dColorUniformListener);
		// dColorByDepthRadioButton.addItemListener((ItemListener)dColorByDepthListener);
		// dColorBySourceRadioButton.addItemListener((ItemListener)dColorBySourceListener);
		// dColorByYearRadioButton.addItemListener((ItemListener)dColorByYearListener);

		dFitByBathymetryMenuItem.addItemListener((ItemListener) dFitByBathymetryListener);
		dFitByNetworkMenuItem.addItemListener((ItemListener) dFitByNetworkListener);
		dFitByLandmarkMenuItem.addItemListener((ItemListener) dFitByLandmarkListener);
		/*
		 * Network menu
		 */
		cfNetwork = new JMenu("Network");
		cfNetwork.add(nOpen = new JMenuItem("Open"));
		cfNetwork.add(nSave = new JMenuItem("Save"));
		cfNetwork.add(nSaveAs = new JMenuItem("Save As"));
		cfNetwork.add(nSaveSpecifiedChannelsAs = new JMenuItem("Save Specified Channels"));
		
		cfNetwork.setMnemonic(KeyEvent.VK_N);
		nOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		nOpen.setMnemonic(KeyEvent.VK_R);
		nSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		nSave.setMnemonic(KeyEvent.VK_S);
		nSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		nSaveAs.setMnemonic(KeyEvent.VK_A);

		cfNetwork.add(nNetworkColorLegend = new JMenuItem("Show Network Color Legend"));
		cfNetwork.add(nClearNetwork = new JMenuItem("Clear Network"));
		cfNetwork.add(nClearChannelsInp = new JMenuItem("Clear channels.inp"));
		cfNetwork.add(nCreateAllChannelNetwork = new JMenuItem("Create centerlines for all DSM2 chan"));
		cfNetwork.addSeparator();
		// cfNetwork.add(nList = new JMenuItem("List"));
		// cfNetwork.add(nSummary = new JMenuItem("Summary"));
		// cfNetwork.addSeparator();
		cfNetwork.add(nDisplayReachSummary = new JMenuItem("View Reach Summary"));
		nDisplay3dReachView = new JMenuItem("Enter channel number range");
		nSelectPointsFor3dReachView= new JMenuItem("Draw window to select data");
		JMenu threeDimensionalPlotMenu = new JMenu("3D Plots");
		threeDimensionalPlotMenu.add(nDisplay3dReachView);
		threeDimensionalPlotMenu.add(nSelectPointsFor3dReachView);
		cfNetwork.add(threeDimensionalPlotMenu);
		cfNetwork.add(nCalculate = new JMenuItem("Calculate"));
		nCalculate.setMnemonic(KeyEvent.VK_C);

		cfNetwork.add(nExport = new JMenu("Export"));
		nExport.add(nExportToSEFormat = new JMenuItem("Export to Station/Elevation format"));
		nExport.add(nExportTo3DFormat = new JMenuItem("Export to 3D format"));
		nExport.add(nExportToWKT = new JMenuItem("Export to WKT format for GIS"));
		nExport.add(nExportChannelsInWindow = new JMenuItem("Draw window to select channels to export"));
		nExport.add(nExportXsectMetadataTable = new JMenuItem("Export Cross-Section Metadata"));
		nExport.add(nExportXsectMidpointCoordToWKT = new JMenuItem("Export Cross-Section Locations to WKT for GIS"));
		nExport.add(nExportOptions = new JMenu("Network export options"));
		
		JMenu reportsMenu = new JMenu("Reports");
		reportsMenu.add(nNetworkSummaryReport = new JMenuItem("Network Summary Report"));
		//		reportsMenu.add(nAWDSummaryReport = new JMenuItem("AWD Summary"));
		//now obsolete--this info is in the Network Summary Report
//		reportsMenu.add(nXSCheckReport = new JMenuItem("Cross-sections with errors"));
//		reportsMenu.add(nDConveyanceReport = new JMenuItem("Cross-sections with -dConveyance"));
		cfNetwork.add(reportsMenu);
		if (_addNetworkMenu)
			menubar.add(cfNetwork);

		nExportOptions.add(noChannelLengthsOnly = new JCheckBoxMenuItem(
				"for Station/Elevation format, only export channel lengths"));
		CsdpFunctions.setChannelLengthsOnly(false);

		NetworkMenu networkMenu = new NetworkMenu(_app);
		ActionListener nOpenListener = networkMenu.new NOpen(this);
		_nSaveListener = networkMenu.new NSave(this);
		_nSaveAsListener = networkMenu.new NSaveAs(this);
		ActionListener nExportToWKTListener = networkMenu.new NExportToWKTFormat(this);
		ActionListener nExportXsectMidpointCoordToWKTListener = networkMenu.new NXsectMidpointCoordToWKTFormat(this);
		_nSaveSpeficiedChannelsAsListener = networkMenu.new NSaveSpecifiedChannelsAs(this);
		ActionListener nClearNetworkListener = networkMenu.new NClearNetwork(this);
		ActionListener nClearChannelsInpListener = networkMenu.new NClearChannelsInp(_app);
		ActionListener nCreateNetworkAllDSM2ChanListener = networkMenu.new NCreateNetworkAllDSM2Chan(_app, this);
		
		ActionListener nExportToSEFormatListener = networkMenu.new NExportToSEFormat(this);
		ActionListener nExportTo3DFormatListener = networkMenu.new NExportTo3DFormat(this);
		ActionListener nExportChannelsInWindowListener = networkMenu.new NExportChannelsInWindow(this);
		ActionListener nExportXsectMetadataTableListener = networkMenu.new NExportXsectMetadataTable(this);
		EventListener noChannelLengthsOnlyListener = networkMenu.new NChannelLengthsOnly();
		//// ActionListener nListListener = networkMenu.new NList();
		//// ActionListener nSummaryListener = networkMenu.new NSummary();
		ActionListener nDisplayReachSummaryListener = networkMenu.new NDisplayReachSummaryWindow(this);
		ActionListener nDisplay3dReachViewListener = networkMenu.new NDisplay3dReachView(this);
		ActionListener nSelectPointsFor3dReachViewListener = networkMenu.new NSelectPointsFor3dReachView(this);
		ActionListener nCalculateListener = networkMenu.new NCalculate(this);
		ActionListener nNetworkSummaryReportListener = networkMenu.new NNetworkSummaryReport(this);
		ActionListener nShowNetworkColorLegendListener = networkMenu.new NShowNetworkColorLegend();
//		ActionListener nAWDSummaryReportListener = networkMenu.new NAWDSummaryReport(this);
		ActionListener nXSCheckReportListener = networkMenu.new NXSCheckReport(this);
//		ActionListener nDConveyanceReportListener = networkMenu.new NDConveyanceReport(this);
		
		nOpen.addActionListener(nOpenListener);
		nSave.addActionListener(_nSaveListener);
		nSaveAs.addActionListener(_nSaveAsListener);
		nSaveSpecifiedChannelsAs.addActionListener(_nSaveSpeficiedChannelsAsListener);
		nExportToWKT.addActionListener(nExportToWKTListener);
		nExportXsectMidpointCoordToWKT.addActionListener(nExportXsectMidpointCoordToWKTListener);
		nClearNetwork.addActionListener(nClearNetworkListener);
		nClearChannelsInp.addActionListener(nClearChannelsInpListener);
		nCreateAllChannelNetwork.addActionListener(nCreateNetworkAllDSM2ChanListener);
		nNetworkColorLegend.addActionListener(nShowNetworkColorLegendListener);
		nExportToSEFormat.addActionListener(nExportToSEFormatListener);
		nExportTo3DFormat.addActionListener(nExportTo3DFormatListener);
		nExportChannelsInWindow.addActionListener(nExportChannelsInWindowListener);
		nExportXsectMetadataTable.addActionListener(nExportXsectMetadataTableListener);
		noChannelLengthsOnly.addItemListener((ItemListener) noChannelLengthsOnlyListener);
		//// nList.addActionListener(nListListener);
		//// nSummary.addActionListener(nSummaryListener);
		nDisplayReachSummary.addActionListener(nDisplayReachSummaryListener);
		nDisplay3dReachView.addActionListener(nDisplay3dReachViewListener);
		_specifyCenterlinesFor3dViewButton.addActionListener(nDisplay3dReachViewListener);
		nSelectPointsFor3dReachView.addActionListener(nSelectPointsFor3dReachViewListener);
		nCalculate.addActionListener(nCalculateListener);
//		nAWDSummaryReport.addActionListener(nAWDSummaryReportListener);
//		nXSCheckReport.addActionListener(nXSCheckReportListener);
		nNetworkSummaryReport.addActionListener(nNetworkSummaryReportListener);
//		nDConveyanceReport.addActionListener(nDConveyanceReportListener);
		_networkOpenButton.addActionListener(nOpenListener);
		_networkSaveButton.addActionListener(_nSaveListener);
		_networkCalculateButton.addActionListener(nCalculateListener);

		/*
		 * Landmark Menu
		 */
		cfLandmark = new JMenu("Landmark");
		cfLandmark.add(oLandmark = new JMenuItem("Open Landmark File"));
		cfLandmark.add(cLandmarks = new JMenuItem("Clear Landmarks"));
		cfLandmark.add(lSave = new JMenuItem("Save"));
		cfLandmark.add(lSaveAs = new JMenuItem("Save As"));
		cfLandmark.add(lExportToWKT = new JMenuItem("Export to WKT Format for GIS"));
		cfLandmark.add(lHelp = new JMenuItem("Landmark Editing Help"));

		if (_addLandmarkMenu)
			menubar.add(cfLandmark);

		LandmarkMenu landmarkMenu = new LandmarkMenu(_app, this);
		_oLandmarkListener = landmarkMenu.new LOpen(this);
		ActionListener LClearLandmarksListener = landmarkMenu.new LClear();
		LSaveLandmarksListener = landmarkMenu.new LSave(this);
		LSaveLandmarksAsListener = landmarkMenu.new LSaveAs(this);
		ActionListener LExportLandmarksToWKTListener = landmarkMenu.new LExportToWKT(this);
		ActionListener LAddListener = landmarkMenu.new LAdd();
		// these may not need listeners?
		ActionListener LMoveListener = landmarkMenu.new LMove();
		ActionListener LEditListener = landmarkMenu.new LEdit();
		ActionListener LDeleteListener = landmarkMenu.new LDelete();
		ActionListener LHelpListener = landmarkMenu.new LHelp();

		oLandmark.addActionListener(_oLandmarkListener);
		cLandmarks.addActionListener(LClearLandmarksListener);
		lSave.addActionListener(LSaveLandmarksListener);
		lSaveAs.addActionListener(LSaveLandmarksAsListener);
		lExportToWKT.addActionListener(LExportLandmarksToWKTListener);
		lHelp.addActionListener(LHelpListener);
		// _landmarkOpenButton.addActionListener(LAddListener);
		// _landmarkAddButton.addActionListener(LAddListener);
		// _landmarkEditButton.addActionListener(LEditListener);
		// _landmarkMoveButton.addActionListener(LMoveListener);
		// _landmarkDeleteButton.addActionListener(LDeleteListener);

		/*
		 * Landmark Popup menu (for right clicking on landmark)
		 */
		cfLandmarkPopup = new JPopupMenu("Landmark Menu");

		cfLandmarkPopup.add(lAddPopup = new JRadioButtonMenuItem("Add landmark"));
		cfLandmarkPopup.add(lMovePopup = new JRadioButtonMenuItem("Move landmark"));
		cfLandmarkPopup.add(lEditPopup = new JRadioButtonMenuItem("Edit landmark"));
		cfLandmarkPopup.add(lDeletePopup = new JRadioButtonMenuItem("Delete landmark"));
		cfLandmarkPopup.add(lHelpPopup = new JRadioButtonMenuItem("Landmark Editing Help"));
		ActionListener LAddPopupListener = landmarkMenu.new LAddPopup(this);
		ActionListener LMovePopupListener = landmarkMenu.new LMovePopup();
		ActionListener LEditPopupListener = landmarkMenu.new LEditPopup();
		ActionListener LDeletePopupListener = landmarkMenu.new LDeletePopup();

		lAddPopup.addActionListener(LAddPopupListener);
		lMovePopup.addActionListener(LMovePopupListener);
		lEditPopup.addActionListener(LEditPopupListener);
		lDeletePopup.addActionListener(LDeletePopupListener);
		lHelpPopup.addActionListener(LHelpListener);

		cfLandmarkPopup.add(lAddPopup);
		cfLandmarkPopup.add(lMovePopup);
		cfLandmarkPopup.add(lEditPopup);
		cfLandmarkPopup.add(lDeletePopup);
		cfLandmarkPopup.add(lHelpPopup);

		// for some reason, these have to be added last
		_centerlineLandmarkEditButtonGroup.add(lAddPopup);
		_centerlineLandmarkEditButtonGroup.add(lEditPopup);
		_centerlineLandmarkEditButtonGroup.add(lMovePopup);
		_centerlineLandmarkEditButtonGroup.add(lDeletePopup);
		_centerlineLandmarkEditButtonGroup.add(lHelpPopup);

		/*
		 * Centerline menu
		 */
		cfCenterline = new JMenu("Centerline");
		cfCenterline.add(cCreate = new JMenuItem("Create"));
		cfCenterline.add(cDSMCreate = new JMenuItem("Create DSM2 Chan"));
		cfCenterline.add(cCursor = new JMenuItem("Done Editing"));
		cfCenterline.add(cRemove = new JMenuItem("Remove Centerline"));

		//// cfCenterline.add(cRename = new JMenuItem("Rename"));
		// cfCenterline.addSeparator();
		// cfCenterline.add(_cMovePointMenuItem = new JCheckBoxMenuItem("Move
		// Point"));
		// cfCenterline.add(_cInsertPointMenuItem = new
		// JCheckBoxMenuItem("Insert Point"));
		// cfCenterline.add(_cAddPointMenuItem = new JCheckBoxMenuItem("Add
		// Point"));
		// cfCenterline.add(_cDeletePointMenuItem = new
		// JCheckBoxMenuItem("Delete Point"));
		// cfCenterline.add(_cAddXsectMenuItem = new JCheckBoxMenuItem("Add
		// Xsect"));
		// cfCenterline.add(_cMoveXsectMenuItem = new JCheckBoxMenuItem("Move
		// Xsect"));
		// cfCenterline.add(_cRemoveXsectMenuItem = new
		// JCheckBoxMenuItem("Remove Xsect"));

		// cfCenterline.add(cRestore = new JMenuItem("Restore"));
		// cfCenterline.add(cKeep = new JMenuItem("Keep"));
		// cfCenterline.addSeparator();
		// cfCenterline.add(cSplit = new JMenuItem("Split"));
		// cfCenterline.add(cJoin = new JMenuItem("Join"));
		// cfCenterline.addSeparator();
		// cfCenterline.add(cView = new JMenuItem("View"));
		// cfCenterline.add(cInfo = new JMenuItem("Info"));
		// cfCenterline.add(cList = new JMenuItem("List"));
		// cfCenterline.add(cSummary = new JMenuItem("Summary"));
		cfCenterline.add(cDisplaySummary = new JMenuItem("Centerline Summary Window"));
		cfCenterline.add(cView3d = new JMenuItem("Centerline 3D View"));
		cfCenterline.add(cReverseCenterline = new JMenuItem("Reverse Centerline"));
		cfCenterline.add(cPlotAllCrossSections = new JMenuItem("Multiple Cross-Section Graph"));
		cfCenterline.add(cDeletePointsInWindow = new JMenuItem("Delete Centerline Points In Window"));
		cfCenterline.add(cDeletePointsOutsideWindow = new JMenuItem("Delete Centerline Points Outside of Window"));
		cfCenterline.add(cAddXSAtComputationalPoints = new JMenuItem("Add cross-sections at Computational Pts"));
		cfCenterline.add(cRemoveAllCrossSections = new JMenuItem("Remove all cross-sections in centerline"));
		cfCenterline.add(cScaleCrossSectionLineLengths = new JMenuItem("Scale cross-section line lengths"));
		
		cfCenterline.setMnemonic(KeyEvent.VK_C);

		if (_addCenterlineMenu)
			menubar.add(cfCenterline);

		_centerlineMenu = new CenterlineMenu(this);
		ActionListener cCursorListener = _centerlineMenu.new CCursor(this);
		ActionListener cCreateListener = _centerlineMenu.new CCreate(_app, this);
		ActionListener cDSM2CreateListener = _centerlineMenu.new CDSM2Create(_app, this);
		ActionListener cRemoveListener = _centerlineMenu.new CRemove(this);
		//// ActionListener cRenameListener = _centerlineMenu.new CRename();
		// ActionListener cRestoreListener = _centerlineMenu.new CRestore();
		// ActionListener cKeepListener = _centerlineMenu.new CKeep();
		ActionListener cMovePointListener = _centerlineMenu.new CMovePoint();
		ActionListener cInsertPointListener = _centerlineMenu.new CInsertPoint();
		ActionListener cAddPointListener = _centerlineMenu.new CAddPoint();
		ActionListener cDeletePointListener = _centerlineMenu.new CDeletePoint();
		ActionListener cAddXsectListener = _centerlineMenu.new CAddXsect();
		ActionListener cRemoveXsectListener = _centerlineMenu.new CRemoveXsect();
		ActionListener cMoveXsectListener = _centerlineMenu.new CMoveXsect();
		ActionListener cDisplaySummaryListener = _centerlineMenu.new DisplayCenterlineSummaryWindow();
		ActionListener cView3dListener = _centerlineMenu.new DisplayCenterline3DView(); 
		ActionListener cReverseCenterlineListener = _centerlineMenu.new ReverseCenterline();
		ActionListener cPlotAllCrossSectionsListener = _centerlineMenu.new PlotAllCrossSections();
		ActionListener cDeletePointsInWindowListener = _centerlineMenu.new DeleteCenterlinePointsInWindow();
		ActionListener cDeletePointsOutsideOfWindowListener = _centerlineMenu.new DeleteCenterlinePointsOutsideOfWindow();
		ActionListener cAddXSAtComputationalPointsListener = _centerlineMenu.new AddXSAtComputationalPoints(_networkInteractor);
		ActionListener cRemoveAllCrossSectionsListener = _centerlineMenu.new RemoveAllCrossSections();
		ActionListener cScaleCrossSectionLineLengthsListener = _centerlineMenu.new ScaleCrossSectionLineLengths();
		cCursor.addActionListener(cCursorListener);
		cCreate.addActionListener(cCreateListener);
		cDSMCreate.addActionListener(cDSM2CreateListener);
		cRemove.addActionListener(cRemoveListener);
		//// cRename.addActionListener(cRenameListener);
		// cRestore.addActionListener(cRestoreListener);
		// cKeep.addActionListener(cKeepListener);
		cDisplaySummary.addActionListener(cDisplaySummaryListener);
		cView3d.addActionListener(cView3dListener);
		cReverseCenterline.addActionListener(cReverseCenterlineListener);
		cPlotAllCrossSections.addActionListener(cPlotAllCrossSectionsListener);
		cDeletePointsInWindow.addActionListener(cDeletePointsInWindowListener);
		cDeletePointsOutsideWindow.addActionListener(cDeletePointsOutsideOfWindowListener);
		cAddXSAtComputationalPoints.addActionListener(cAddXSAtComputationalPointsListener);
		cRemoveAllCrossSections.addActionListener(cRemoveAllCrossSectionsListener);
		cScaleCrossSectionLineLengths.addActionListener(cScaleCrossSectionLineLengthsListener);
		
		_cursorButton.addActionListener(cCursorListener);
		_moveButton.addActionListener(cMovePointListener);
		_insertButton.addActionListener(cInsertPointListener);
		_deleteButton.addActionListener(cDeletePointListener);
		_addXsectButton.addActionListener(cAddXsectListener);
		_removeXsectButton.addActionListener(cRemoveXsectListener);
		_moveXsectButton.addActionListener(cMoveXsectListener);
		// _restoreButton.addActionListener(cRestoreListener);
		// _keepButton.addActionListener(cRestoreListener);

		/*
		 * Xsect Menu
		 */
		cfXsect = new JMenu("Xsect");
		// cfXsect.add(xAutoGen = new JMenuItem("Auto Gen"));
		// cfXsect.addSeparator();
		// cfXsect.add(xCreate = new JMenuItem("Create"));
		// cfXsect.add(xPosition = new JMenuItem("Position"));
		// cfXsect.addSeparator();
		cfXsect.add(xView = new JMenuItem("View"));
		cfXsect.add(xAdjustLength = new JMenuItem("AdjustLength"));
		// cfXsect.add(xInfo = new JMenuItem("Info"));
		// cfXsect.add(xSummary = new JMenuItem("Summary"));
		cfXsect.add(xExtractData = new JMenuItem("ExtractData"));
		cfXsect.setMnemonic(KeyEvent.VK_S);
		xView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		xView.setMnemonic(KeyEvent.VK_V);

		if (_addXsectMenu)
			menubar.add(cfXsect);

		// create and register action listener objects for the Xsect menu items
		_xsectMenu = new XsectMenu(_app, this, _networkInteractor);
		// ActionListener xCreateListener = _xsectMenu.new XCreate();
		// ActionListener xRemoveListener = _xsectMenu.new XRemove();
		// ActionListener xMoveListener = _xsectMenu.new XMove();
		// // ActionListener xPositionListener = _xsectMenu.new XPosition();
		ActionListener xViewListener = _xsectMenu.new XView();
		ActionListener xAdjustLengthListener = _xsectMenu.new XAdjustLength();
		ActionListener xExtractDataListener = _xsectMenu.new XExtractData(this);
		// ActionListener xInfoListener = _xsectMenu.new XInfo();
		// ActionListener xSummaryListener = _xsectMenu.new XSummary();

		// xCreate.addActionListener(xCreateListener);
		// xRemove.addActionListener(xRemoveListener);
		// xMove.addActionListener(xMoveListener);
		// xPosition.addActionListener(xPositionListener);
		xView.addActionListener(xViewListener);

		xAdjustLength.addActionListener(xAdjustLengthListener);
		// xInfo.addActionListener(xInfoListener);
		// xSummary.addActionListener(xSummaryListener);
		_viewXsectButton.addActionListener(xViewListener);
		xExtractData.addActionListener(xExtractDataListener);

		/*
		 * Zoom menu
		 */
		cfZoom = new JMenu("Zoom");
		cfZoom.add(zBox = new JMenuItem("Box"));
		cfZoom.add(zPan = new JMenuItem("Pan"));
		cfZoom.addSeparator();
		cfZoom.add(zFit = new JMenuItem("Fit"));
		cfZoom.add(zUndo = new JMenuItem("Undo Zoom"));

		cfZoom.add(zZoomToCenterline = new JMenuItem("Zoom to centerline"));
		zZoomToCenterline.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		cfZoom.add(zZoomToNode = new JMenuItem("Zoom to node"));

		cfZoom.setMnemonic(KeyEvent.VK_Z);
		if (_addZoomMenu)
			menubar.add(cfZoom);

		// create and register action listener objects for the Zoom menu items
		ZoomMenu zoomMenu = new ZoomMenu(this);
		ActionListener zPanListener = zoomMenu.new ZPan();
		ActionListener zFitListener = zoomMenu.new ZFit();
		ActionListener zBoxListener = zoomMenu.new ZBox();
		ActionListener zUndoListener = zoomMenu.new ZUndo();
		ActionListener zPanMIListener = zoomMenu.new ZPanMI();
		ActionListener zBoxMIListener = zoomMenu.new ZBoxMI();
		ActionListener nZoomToCenterlineListener = zoomMenu.new NZoomToCenterline(this);
		ActionListener nZoomToNodeListener = zoomMenu.new NZoomToNode(this); 

		zZoomToCenterline.addActionListener(nZoomToCenterlineListener);
		zZoomToNode.addActionListener(nZoomToNodeListener);

		zPan.addActionListener(zPanMIListener);
		zFit.addActionListener(zFitListener);
		zBox.addActionListener(zBoxMIListener);
		zUndo.addActionListener(zUndoListener);

		_zoomBoxButton.addActionListener(zBoxListener);
		_zoomPanButton.addActionListener(zPanListener);
		_zoomUndoButton.addActionListener(zUndoListener);
		_zoomFitButton.addActionListener(zFitListener);

		zPan.setEnabled(_addZoomWindowOption);
		zFit.setEnabled(_addZoomWindowOption);
		zBox.setEnabled(_addZoomWindowOption);
		/*
		 * Tools menu
		 */
		cfTools = new JMenu("Tools");
		// cfTools.add(tCompareNetwork = new JMenuItem("Compare two network
		// files"));
		cfTools.add(tCalcRect = new JMenuItem("Calculate Equivalent Rectangular cross-sections"));
		cfTools.add(tOpenWaterCalc = new JMenuItem("Open Water Area Calculations"));
//		cfTools.add(tCreateDSM2ChanPolygons = new JMenuItem("Create DSM2 channel polygons"));
		cfTools.add(tClosePolygonCenterlines = new JMenuItem("Close All Polygon Centerlines"));
		cfTools.add(tRemoveAllCrossSections = new JMenuItem("Remove All Cross-Sections in network"));
		cfTools.add(tCreateDSM2OutputLocations = new JMenuItem("Create DSM2 output locations file"));
		cfTools.add(tCrossSectionSlideshow = new JMenuItem("Cross-Section Slideshow"));
		cfTools.add(tManningsDispersionSpatialDistribution = new JMenuItem("Manning's & Dispersion Spatial Distribution"));
		cfTools.add(tExtendCenterlinesToNodes = new JMenuItem("Extend Centerlines to Nodes"));
		//This is not the way, because we need to edit some of the centerlines  
//		cfTools.add(tCreateStraightlineChanForGridmap = new JMenuItem("Create Network Chan For Gridmap"));
		cfTools.add(tCreateDCDNodeLandmarkFile = new JMenuItem("Create DCD Node Landmark File"));
		tExtendCenterlinesToNodes.setToolTipText("Extend centerlines to nodes. One possibility for creating GIS gridmap.");
//		tCreateStraightlineChanForGridmap.setToolTipText("Create a WKT file containing lines connecting nodes in landmark file");
		tCreateDCDNodeLandmarkFile.setToolTipText("Create a landmark file containing nodes used by DCD model");
		if (_addToolsMenu)
			menubar.add(cfTools);
		cfTools.add(cMovePolygonCenterlinePointsToLeveeCenterline = 
				new JMenu("Snap Polygon points to levee centerline."));
		JMenuItem enterCenterlineNames = new JMenuItem("Enter Centerline Coord");
		JMenuItem readCenterlineNamesFromFile = new JMenuItem("Read Centerline Coord From File");
		cMovePolygonCenterlinePointsToLeveeCenterline.add(enterCenterlineNames);
		cMovePolygonCenterlinePointsToLeveeCenterline.add(readCenterlineNamesFromFile);
		
		tCalcRect.setEnabled(false);
		tClosePolygonCenterlines.setEnabled(false);
		tRemoveAllCrossSections.setEnabled(false);
		tCreateDSM2OutputLocations.setEnabled(false);
		
		cfTools.setMnemonic(KeyEvent.VK_T);

		// removed temporarily(?) options now displayed in dialog.
		// tOpenWaterOptionsMenu.add
		// (oEchoTimeSeriesInput = new JCheckBoxMenuItem("Echo Time Series
		// Input"));
		// tOpenWaterOptionsMenu.add
		// (oEchoXsectInput = new JCheckBoxMenuItem("Echo Xsect Input"));
		// tOpenWaterOptionsMenu.add
		// (oEchoToeDrainInput = new JCheckBoxMenuItem("Echo Toe Drain Input"));
		// tOpenWaterOptionsMenu.add
		// (oPrintXsectResults = new JCheckBoxMenuItem("Print Xsect Results
		// (long)"));
		// tOpenWaterOptionsMenu.add
		// (oUseFremontWeir = new JCheckBoxMenuItem("Use Fremont Weir"));
		// tOpenWaterOptionsMenu.add
		// (oUseToeDrainRestriction = new JCheckBoxMenuItem("Use toe drain
		// restriction"));
		// cfTools.add(tOpenWaterOptionsMenu);

		// oEchoTimeSeriesInput.setSelected(getEchoTimeSeriesInput());
		// oEchoXsectInput.setSelected(getEchoXsectInput());
		// oEchoToeDrainInput.setSelected(getEchoToeDrainInput());
		// oPrintXsectResults.setSelected(getPrintXsectResults());
		// oUseFremontWeir.setSelected(CsdpFunctions.getUseFremontWeir());
		// oUseToeDrainRestriction.setSelected(CsdpFunctions.getUseToeDrainRestriction());
		// tOpenWaterCalc.setEnabled(_addOWACalcOption);
		// tOpenWaterOptionsMenu.setEnabled(_addOWACalcOption);

		tCalcRect.setEnabled(_addRectXSOption);

		_toolsMenu = new ToolsMenu(_app, this);
		// ActionListener tCompareNetworkListener = _toolsMenu.new
		// TCompareNetwork(this);
		ActionListener tCalcRectListener = _toolsMenu.new TCalcRect(this);
		ActionListener tOpenWaterCalcListener = _toolsMenu.new TOpenWaterCalc(this);
		ActionListener tCreateDSM2ChanPolygonsListener = _toolsMenu.new TCreateDSM2ChanPolygons();
		ActionListener tClosePolygonCenterlinesListener = _toolsMenu.new TClosePolygonCenterlines();
		ActionListener tRemoveAllCrossSectionsListener = _toolsMenu.new TRemoveAllCrossSections();
		ActionListener tCreateDSM2OutputLocationsListener = _toolsMenu.new TCreateDSM2OutputLocationsForLandmarks();
		ActionListener tCrossSectionSlideshowListener = _toolsMenu.new TCrossSectionSlideshow();
		ActionListener tManningsDispersionSpatialDistributionListener = _toolsMenu.new TManningsDispersionSpatialDistribution();
		ActionListener tExtendCenterlinesToNodesListener = _toolsMenu.new TExtendCenterlinesToNodes();
//		ActionListener tCreateStraightlineChanForGridmapListener = _toolsMenu.new TCreateStraightlineChanForGridmap();
		ActionListener tCreateDCDNodeLandmarkFileListener = _toolsMenu.new TCreateDCDNodeLandmarkFile(this);
		// removed temporarily(?) options now appear in dialog
		// EventListener oEchoTimeSeriesInputListener = _toolsMenu.new
		// TEchoTimeSeriesInput();
		// EventListener oEchoXsectInputListener = _toolsMenu.new
		// TEchoXsectInput();
		// EventListener oEchoToeDrainInputListener = _toolsMenu.new
		// TEchoToeDrainInput();
		// EventListener oPrintXsectResultsListener = _toolsMenu.new
		// TPrintXsectResults();
		// EventListener oUseFremontWeirListener = _toolsMenu.new
		// TUseFremontWeir();
		// EventListener oUseToeDrainRestrictionListener = _toolsMenu.new
		// TUseToeDrainRestriction();
		// oEchoTimeSeriesInput.addItemListener((ItemListener)oEchoTimeSeriesInputListener);
		// oEchoXsectInput.addItemListener((ItemListener)oEchoXsectInputListener);
		// oEchoToeDrainInput.addItemListener((ItemListener)oEchoToeDrainInputListener);
		// oPrintXsectResults.addItemListener((ItemListener)oPrintXsectResultsListener);
		// oUseFremontWeir.addItemListener((ItemListener)oUseFremontWeirListener);
		// oUseToeDrainRestriction.addItemListener((ItemListener)oUseToeDrainRestrictionListener);
		// tCompareNetwork.addActionListener(tCompareNetworkListener);
		tCalcRect.addActionListener(tCalcRectListener);
		tOpenWaterCalc.addActionListener(tOpenWaterCalcListener);
		ActionListener tMovePolygonCenterlinePointsToLeveeCenterlineEnterCoordListener = 
				_toolsMenu.new SnapPolygonCenterlinePointsToLeveeCenterline(ToolsMenu.ENTER_CENTERLINE_NAMES);
		ActionListener tMovePolygonCenterlinePointsToLeveeCenterlineReadFileListener = 
				_toolsMenu.new SnapPolygonCenterlinePointsToLeveeCenterline(ToolsMenu.READ_CENTERLINE_NAMES_FROM_FILE);
		tClosePolygonCenterlines.addActionListener(tClosePolygonCenterlinesListener);
		tRemoveAllCrossSections.addActionListener(tRemoveAllCrossSectionsListener);
		tCreateDSM2OutputLocations.addActionListener(tCreateDSM2OutputLocationsListener);
		enterCenterlineNames.addActionListener(tMovePolygonCenterlinePointsToLeveeCenterlineEnterCoordListener);		
		readCenterlineNamesFromFile.addActionListener(tMovePolygonCenterlinePointsToLeveeCenterlineReadFileListener);
//		tCreateDSM2ChanPolygons.addActionListener(tCreateDSM2ChanPolygonsListener);
		tCrossSectionSlideshow.addActionListener(tCrossSectionSlideshowListener);
		tManningsDispersionSpatialDistribution.addActionListener(tManningsDispersionSpatialDistributionListener);
		tExtendCenterlinesToNodes.addActionListener(tExtendCenterlinesToNodesListener);
//		tCreateStraightlineChanForGridmap.addActionListener(tCreateStraightlineChanForGridmapListener);
		tCreateDCDNodeLandmarkFile.addActionListener(tCreateDCDNodeLandmarkFileListener);
		
		// tCompareNetwork.setEnabled(_addCompareNetworkOption);

		/*
		 * Window menu
		 */
		// cfWindow = new JMenu("Window");
		// cfWindow.setMnemonic(KeyEvent.VK_W);
		// cfWindow.add(wCascade = new JMenuItem("Cascade"));
		// cfWindow.add(wTile = new JMenuItem("Tile"));
		// cfWindow.add(wArrangeIcons = new JMenuItem("Arrange Icons"));
		// cfWindow.add(wCloseAll = new JMenuItem("Close All"));
		// cfWindow.addSeparator();
		// cfWindow.add(wRepaint = new JMenuItem("Repaint"));
		// // also show open windows
		// menubar.add(cfWindow);

		cfHelp = new JMenu("Help");
		cfHelp.setMnemonic(KeyEvent.VK_H);
		// cfHelp.add(hContents = new JMenuItem("Contents"));
		// cfHelp.add(hUsingHelp = new JMenuItem("Using Help"));
		// cfHelp.addSeparator();
		cfHelp.add(hAbout = new JMenuItem("About CSDP"));
		if (_addHelpMenu) {
			menubar.add(Box.createHorizontalGlue());
			menubar.add(cfHelp);
			menubar.add(Box.createHorizontalGlue());
		}
		HelpMenu helpMenu = new HelpMenu();
		//// menubar.setHelpMenu(cfHelp);
		ActionListener hAboutListener = helpMenu.new HAbout(this);
		hAbout.addActionListener(hAboutListener);

		disableButtonsAndMenuItems();

	} // createGui

	/**
	 * Creates new instance of canvas for plotting bathymetry data.
	 */
	/// public void addPlanViewCanvas(String name){
	/// PlanViewCanvas can = new PlanViewCanvas(name);
	/// _pvCanvases.put(name, can);
	/// }

	/**
	 * returns canvas with specified name
	 */
	/// public PlanViewCanvas getPlanViewCanvas(String name){
	/// return (PlanViewCanvas)_pvCanvases.get(name);
	/// }

	/**
	 * removes canvas from hashtable(close file)
	 */
	/// public void removePlanViewCanvas(String name){
	/// Object value = _pvCanvases.remove(name);
	/// }

	/// Hashtable _pvCanvases = new Hashtable();
	/// Hashtable _filenames = new Hashtable();

	/**
	 * sets Network object
	 */
	public void setNetwork(Network net) {
		_net = net;
		_networkInteractor.setNetwork(net);
		_xsectMenu.setNetwork(net);
		_canvas1.setNetwork(net);
	}// setNetwork

	/**
	 * returns handle to the network object.
	 */
	public Network getNetwork() {
		return _net;
	}// getNetwork

	public NetworkInteractor getNetworkInteractor() {
		return _networkInteractor;
	}
	
	public void saveLandmark() {
		if(CsdpFunctions.getLandmarkFilename()==null) {
			LSaveLandmarksAsListener.actionPerformed(_nullActionEvent);
		}else {
			LSaveLandmarksListener.actionPerformed(_nullActionEvent);
		}
	}
	
	/**
	 * save network file when quitting program (if user clicks "yes")
	 */
	public void saveNetwork() {
		if (CsdpFunctions.getNetworkFilename() == null) {
			_nSaveAsListener.actionPerformed(_nullActionEvent);
		} else {
			_nSaveListener.actionPerformed(_nullActionEvent);
		}
	}// saveNetwork

	/**
	 * returns landmark object
	 */
	public Landmark getLandmark() {
		Landmark landmark = null;
		if(_landmark != null) {
			landmark = _landmark;
		}else{
			_oLandmarkListener.actionPerformed(_nullActionEvent);
			landmark = _landmark;
		}
		return landmark;
	}// getLandmark

	/**
	 * sets Landmark object
	 */
	public void setLandmark(Landmark landmark) {
		_landmark = landmark;
		_li.setLandmark(landmark);
		_canvas1.setLandmark(landmark);
		_centerlineMenu.setLandmark(landmark);
	}// setLandmark

//	/**
//	 * sets DSMChannels object
//	 */
//	public void setDSMChannels(DSMChannels DSMChannels) {
//		_DSMChannels = DSMChannels;
//		// _canvas1.setLandmark(landmark);
//	}// setDSMChannels

	/**
	 * returns instance of bathymetry plot object
	 */
	public BathymetryPlot getPlotObject() {
		return _plot;
	}

	/**
	 * sets BathymetryPlot object
	 */
	public void setPlotObject(BathymetryPlot plot) {
		_plot = plot;
		_networkInteractor.setPlotter(plot);
		_li.setPlotter(plot);
	}// setPlotObject

	// /**
	// * add a canvas to the hashtable that stores all the panels contained in
	// the frame
	// */
	// public void addXsectWindow(String name, XsectGraph xg){
	// _panelObjects.put(name, xg);
	// _planViewJPanel.add(name, (Canvas)_panelObjects.get(name));
	// _cl.next(_p);
	// }//addXsectWindow

	/**
	 * called when user wants to stop editing
	 */
	public void setStopEditingMode() {
		enableCenterlineEditButtons();
		enableLandmarkEditButtons();
		setDefaultModesStates();
		// setZoomBoxMode(false);
		// setZoomPanMode(false);

		if (getCenterlineSelected()) {
			_moveButton.setEnabled(true);
			_insertButton.setEnabled(true);

			_addUpstreamPointButton.setEnabled(true);
			_addDownstreamPointButton.setEnabled(true);
			_deleteButton.setEnabled(true);
			_addXsectButton.setEnabled(true);
			_removeXsectButton.setEnabled(true);
			cDisplaySummary.setEnabled(true);
			cView3d.setEnabled(true);
			cPlotAllCrossSections.setEnabled(true);
			cDeletePointsInWindow.setEnabled(true);
			cDeletePointsOutsideWindow.setEnabled(true);
			cAddXSAtComputationalPoints.setEnabled(true);
			cReverseCenterline.setEnabled(true);
			cRemoveAllCrossSections.setEnabled(true);
			if (getXsectSelected()) {
				_moveXsectButton.setEnabled(true);
				_viewXsectButton.setEnabled(true);
			} else {
				_moveXsectButton.setEnabled(false);
				_viewXsectButton.setEnabled(false);
			}
		} else {
			_moveButton.setEnabled(false);
			_insertButton.setEnabled(false);
			_addUpstreamPointButton.setEnabled(false);
			_addDownstreamPointButton.setEnabled(false);
			_deleteButton.setEnabled(false);
			_addXsectButton.setEnabled(false);
			_removeXsectButton.setEnabled(false);
			_moveXsectButton.setEnabled(false);
			_viewXsectButton.setEnabled(false);
			// _landmarkAddButton.setEnabled(false);
			// _landmarkEditButton.setEnabled(false);
			// _landmarkMoveButton.setEnabled(false);
			// _landmarkDeleteButton.setEnabled(false);
			lAddPopup.setEnabled(false);
			lEditPopup.setEnabled(false);
			lMovePopup.setEnabled(false);
			lDeletePopup.setEnabled(false);
			cDisplaySummary.setEnabled(false);
			cView3d.setEnabled(true);
			cPlotAllCrossSections.setEnabled(false);
			cDeletePointsInWindow.setEnabled(false);
			cDeletePointsOutsideWindow.setEnabled(false);
			cReverseCenterline.setEnabled(false);
			cAddXSAtComputationalPoints.setEnabled(false);
			cRemoveAllCrossSections.setEnabled(true);
		}
	}// setStopEditingMode

	/**
	 * called when user wants to add centerline points to the end of the
	 * set(after last pt)--only called when adding a centerline
	 */
	public void setAddDownstreamPointMode() {
		_addDownstreamPointButton.setSelected(true);
	}// setAddPointMode

	public void setAddUpstreamPointMode() {
		_addUpstreamPointButton.setSelected(true);
	}
	
	/**
	 * toggles zoom mode when button is not clicked by user. Allows another
	 * event to toggle mode.
	 */
	public void pressZoomBoxButton() {
		if (DEBUG)
			System.out.println("zoom box button pressed indirectly");
		_zoomBoxButton.doClick();
	}

	public void pressDeleteCenterlinePointsInBoxButton() {
		_deleteCenterlinePointsInBoxButton.doClick();
	}
	
	public void pressExportChannelsInWindowButton() {
		_exportChannelsInWindowButton.doClick();
	}
	
	/**
	 * toggles zoom pan mode when button is not clicked by user. Allows another
	 * event to toggle mode.
	 */
	public void pressZoomPanButton() {
		_zoomPanButton.doClick();
	}

	public void pressSelectCursorAkaArrowButton() {
		_cursorButton.doClick();
	}

	public void pressSelectCenterlineForDataEntryDialogButton() {
		_selectCenterlineForDataEntryDialogButton.doClick();
	}
	
	
	/**
	 * turns off all edit modes
	 */
	public void turnOffEditModes() {
		setDefaultModesStates();
		enableCenterlineEditButtons();
		setCursor(CsdpFunctions._defaultCursor);
		pressSelectCursorAkaArrowButton();
	}// turnOffEditModes

	/**
	 * disable buttons and menu items which should not be enabled until
	 * bathymetry loaded
	 */
	public void disableButtonsAndMenuItems() {
		_cursorButton.setEnabled(false);
		_propOpenButton.setEnabled(false);
		_networkOpenButton.setEnabled(false);
		_networkSaveButton.setEnabled(false);
		_moveButton.setEnabled(false);
		_insertButton.setEnabled(false);
		_addUpstreamPointButton.setEnabled(false);
		_addDownstreamPointButton.setEnabled(false);
		_deleteButton.setEnabled(false);
		_addXsectButton.setEnabled(false);
		_moveXsectButton.setEnabled(false);
		_removeXsectButton.setEnabled(false);
		_zoomBoxButton.setEnabled(false);
		_zoomPanButton.setEnabled(false);
		_zoomFitButton.setEnabled(false);
		_viewXsectButton.setEnabled(false);
		_filterSourceButton.setEnabled(false);
		_filterYearButton.setEnabled(false);

		fSavePointsInsideOutsidePolygon.setEnabled(false);
		// _cMovePointMenuItem.setEnabled(false);
		// _cAddPointMenuItem.setEnabled(false);
		// _cInsertPointMenuItem.setEnabled(false);
		// _cDeletePointMenuItem.setEnabled(false);
		// _cAddXsectMenuItem.setEnabled(false);
		// _cRemoveXsectMenuItem.setEnabled(false);
		// _cMoveXsectMenuItem.setEnabled(false);

		fClose.setEnabled(false);
		fSave.setEnabled(false);
		fSaveAs.setEnabled(false);
		// fPrintPreview.setEnabled(false);
		// fPrint.setEnabled(false);
		// fPrintSetup.setEnabled(false);

		pLoad.setEnabled(false);
		pSaveAs.setEnabled(false);
		pSave.setEnabled(false);

		dParameters.setEnabled(false);
		dSource.setEnabled(false);
		dYear.setEnabled(false);
		// dColorBy.setEnabled(false);
		// dErased.setEnabled(false);
		dDigitalLineGraph.setEnabled(false);
		clearDigitalLineGraph.setEnabled(false);
		oLandmark.setEnabled(false);
		cLandmarks.setEnabled(false);
		lSave.setEnabled(false);
		lSaveAs.setEnabled(false);
		lExportToWKT.setEnabled(false);
		
		lAddPopup.setEnabled(false);
		lEditPopup.setEnabled(false);
		lMovePopup.setEnabled(false);
		lDeletePopup.setEnabled(false);
		// dColorUniformRadioButton.setEnabled(false);
		// dColorByDepthRadioButton.setEnabled(false);
		// dColorBySourceRadioButton, dColorByYearRadioButton;
		dFitByNetworkMenuItem.setEnabled(false);
		dFitByLandmarkMenuItem.setEnabled(false);

		nOpen.setEnabled(false);
		nSave.setEnabled(false);
		nSaveAs.setEnabled(false);
		nSaveSpecifiedChannelsAs.setEnabled(false);
		nExportToWKT.setEnabled(false);
		nExportXsectMidpointCoordToWKT.setEnabled(false);
		nExportToSEFormat.setEnabled(false);
		nExportTo3DFormat.setEnabled(false);
		nExportChannelsInWindow.setEnabled(false);
		nExportXsectMetadataTable.setEnabled(false);
		// nList.setEnabled(false);nSummary.setEnabled(false);
		nClearNetwork.setEnabled(false);
		zZoomToCenterline.setEnabled(false);
		zZoomToNode.setEnabled(false);
		nCalculate.setEnabled(false);
		_networkCalculateButton.setEnabled(false);
		cCreate.setEnabled(false);
		cDSMCreate.setEnabled(false);
		//// cRename.setEnabled(false);
		cDisplaySummary.setEnabled(false);
		cView3d.setEnabled(false);
		nDisplayReachSummary.setEnabled(false);
		nDisplay3dReachView.setEnabled(false);
		_selectPointsFor3dViewButton.setEnabled(false);
		_specifyCenterlinesFor3dViewButton.setEnabled(false);
		nSelectPointsFor3dReachView.setEnabled(false);
		cPlotAllCrossSections.setEnabled(false);
		cDeletePointsInWindow.setEnabled(false);
		cDeletePointsOutsideWindow.setEnabled(false);
		cAddXSAtComputationalPoints.setEnabled(false);
		cReverseCenterline.setEnabled(false);
		cMovePolygonCenterlinePointsToLeveeCenterline.setEnabled(false);
		cRemoveAllCrossSections.setEnabled(false);
//		nAWDSummaryReport.setEnabled(false);
//		nXSCheckReport.setEnabled(false);
//		nDConveyanceReport.setEnabled(false);
		nNetworkSummaryReport.setEnabled(false);
		
		tClosePolygonCenterlines.setEnabled(false);
		tRemoveAllCrossSections.setEnabled(false);
		tCreateDSM2OutputLocations.setEnabled(false);
		tCalcRect.setEnabled(false);
		tCrossSectionSlideshow.setEnabled(false);
		tManningsDispersionSpatialDistribution.setEnabled(false);
		tExtendCenterlinesToNodes.setEnabled(false);
//		tCreateStraightlineChanForGridmap.setEnabled(false);
		tCreateDCDNodeLandmarkFile.setEnabled(false);
		// xMove.setEnabled(false);
		// xCreate.setEnabled(false);
		xView.setEnabled(false);
		xAdjustLength.setEnabled(false);
		xExtractData.setEnabled(false);
		// xInfo.setEnabled(false);xSummary.setEnabled(false);
		zPan.setEnabled(false);
		zFit.setEnabled(false);
		zBox.setEnabled(false);
		zUndo.setEnabled(false);
		_zoomUndoButton.setEnabled(false);
	}// disableButtonsAndMenuItems

	/**
	 * enable buttons and menu items which should be enabled when bathymetry
	 * loaded
	 */
	public void enableAfterBathymetry() {
		pSaveAs.setEnabled(true);
		pLoad.setEnabled(true);
		cfDisplay.setEnabled(true);
		cfNetwork.setEnabled(true);
		cfCenterline.setEnabled(true);
		cfZoom.setEnabled(true);
		_zoomBoxButton.setEnabled(true);
		_zoomPanButton.setEnabled(true);
		_zoomFitButton.setEnabled(true);
		_propOpenButton.setEnabled(true);
		_filterSourceButton.setEnabled(true);
		_filterYearButton.setEnabled(true);

		//// fClose.setEnabled(true);
		//// fSave.setEnabled(true);//not implemented
		fSaveAs.setEnabled(true);
		// fPrintPreview.setEnabled(true);
		// fPrint.setEnabled(true);
		// fPrintSetup.setEnabled(true);
		dParameters.setEnabled(true);
		dSource.setEnabled(true);
		dYear.setEnabled(true);
		// dColorBy.setEnabled(true);
		// dErased.setEnabled(true);
		dDigitalLineGraph.setEnabled(true);
		clearDigitalLineGraph.setEnabled(true);
		oLandmark.setEnabled(true);
//		lSave.setEnabled(true);
//		lSaveAs.setEnabled(true);
		lExportToWKT.setEnabled(true);
		
		lAddPopup.setEnabled(true);
		lEditPopup.setEnabled(true);
		lMovePopup.setEnabled(true);
		lDeletePopup.setEnabled(true);
		// dColorUniformRadioButton.setEnabled(true);
		// dColorByDepthRadioButton.setEnabled(true);
		// dColorBySourceRadioButton, dColorByYearRadioButton;
		nOpen.setEnabled(true);
		nSelectPointsFor3dReachView.setEnabled(true);
		_selectPointsFor3dViewButton.setEnabled(true);
		cCreate.setEnabled(true);
		cDSMCreate.setEnabled(true);
		cDisplaySummary.setEnabled(false);
		cView3d.setEnabled(false);
//		cPlotAllCrossSections.setEnabled(false);
		cAddXSAtComputationalPoints.setEnabled(false);
		cReverseCenterline.setEnabled(false);

		tCrossSectionSlideshow.setEnabled(true);
		
		// xMove.setEnabled(true);
		// xInfo.setEnabled(true);xSummary.setEnabled(true);
		zPan.setEnabled(_addZoomWindowOption);
		zFit.setEnabled(_addZoomWindowOption);
		zBox.setEnabled(_addZoomWindowOption);
		_cursorButton.setEnabled(true);
		_networkOpenButton.setEnabled(true);
	}// enableAfterBathymetry

	/**
	 * Used to enable/disable undo Zoom menuitem and/or button
	 */
	public void setUndoZoom(boolean b) {
		zUndo.setEnabled(b);
		_zoomUndoButton.setEnabled(b);
	}

	/**
	 * enable buttons and menu items which should be enabled after properties
	 * file saved or loaded
	 */
	public void enableAfterProperties() {
		pSave.setEnabled(true);
	}

	/**
	 * enable buttons and menu items which should be enabled after network
	 * loaded
	 */
	protected void enableAfterNetwork() {
		nSave.setEnabled(true);
		nSaveAs.setEnabled(true);
		nExportToWKT.setEnabled(true);
		nExportXsectMidpointCoordToWKT.setEnabled(true);
		nSaveSpecifiedChannelsAs.setEnabled(true);
		nClearNetwork.setEnabled(true);
		zZoomToCenterline.setEnabled(true);
		_networkSaveButton.setEnabled(true);
		// nList.setEnabled(true);nSummary.setEnabled(true);
		nExportToSEFormat.setEnabled(true);
		nExportTo3DFormat.setEnabled(true);
		nExportChannelsInWindow.setEnabled(true);
		nExportXsectMetadataTable.setEnabled(true);
		nDisplayReachSummary.setEnabled(true);
		nDisplay3dReachView.setEnabled(true);
		_selectPointsFor3dViewButton.setEnabled(true);
		_specifyCenterlinesFor3dViewButton.setEnabled(true);
		nSelectPointsFor3dReachView.setEnabled(true);
		nCalculate.setEnabled(true);
		_networkCalculateButton.setEnabled(true);
		dFitByNetworkMenuItem.setEnabled(true);
		tCalcRect.setEnabled(true);
		tClosePolygonCenterlines.setEnabled(true);
		tRemoveAllCrossSections.setEnabled(true);
		if(_landmark!=null) {
			tCreateDSM2OutputLocations.setEnabled(true);
			tExtendCenterlinesToNodes.setEnabled(true);
//			tCreateStraightlineChanForGridmap.setEnabled(true);
		}
		cMovePolygonCenterlinePointsToLeveeCenterline.setEnabled(true);
		tManningsDispersionSpatialDistribution.setEnabled(true);
		//		nAWDSummaryReport.setEnabled(true);
//		nXSCheckReport.setEnabled(true);
//		nDConveyanceReport.setEnabled(true);
		nNetworkSummaryReport.setEnabled(true);

	}// enableAfterNetwork

	/**
	 * enable buttons and menu items which should be enabled after landmark
	 * loaded/created
	 */
	protected void enableAfterLandmark() {
		lSave.setEnabled(true);
		lSaveAs.setEnabled(true);
		lExportToWKT.setEnabled(true);
		cLandmarks.setEnabled(true);
		zZoomToNode.setEnabled(true);
		// _landmarkSaveButton.setEnabled(true);
		// _landmarkAddButton.setEnabled(true);
		// _landmarkMoveButton.setEnabled(true);
		// _landmarkDeleteButton.setEnabled(true);
		lAddPopup.setEnabled(true);
		lEditPopup.setEnabled(true);
		lMovePopup.setEnabled(true);
		lDeletePopup.setEnabled(true);
		cLandmarks.setEnabled(true);
		dFitByNetworkMenuItem.setEnabled(true);
		if(_net!=null) {
			tCreateDSM2OutputLocations.setEnabled(true);
			tExtendCenterlinesToNodes.setEnabled(true);
//			tCreateStraightlineChanForGridmap.setEnabled(true);
		}
		tCreateDCDNodeLandmarkFile.setEnabled(true);
		tCalcRect.setEnabled(true);
	}// enableAfterLandmark

	/**
	 * disable buttons and menu items which should be disabled when network is
	 * cleared
	 */
	protected void disableWhenNetworkCleared() {
		nSave.setEnabled(false);
		nSaveAs.setEnabled(false);
		nExportToWKT.setEnabled(false);
		nExportXsectMidpointCoordToWKT.setEnabled(false);
		nSaveSpecifiedChannelsAs.setEnabled(false);
		nClearNetwork.setEnabled(false);
		zZoomToCenterline.setEnabled(false);
		_networkSaveButton.setEnabled(false);
		// nList.setEnabled(false);nSummary.setEnabled(false);
		nExportToSEFormat.setEnabled(false);
		nExportTo3DFormat.setEnabled(false);
		nExportChannelsInWindow.setEnabled(false);
		nExportXsectMetadataTable.setEnabled(false);
		nCalculate.setEnabled(false);
		nDisplayReachSummary.setEnabled(false);
		nDisplay3dReachView.setEnabled(false);
		_specifyCenterlinesFor3dViewButton.setEnabled(false);
//		nSelectPointsFor3dReachView.setEnabled(false);
		_networkCalculateButton.setEnabled(false);
		dFitByNetworkMenuItem.setEnabled(false);
		tCalcRect.setEnabled(false);
		tClosePolygonCenterlines.setEnabled(false);
		tRemoveAllCrossSections.setEnabled(false);
		tCreateDSM2OutputLocations.setEnabled(false);
//		nAWDSummaryReport.setEnabled(false);
//		nXSCheckReport.setEnabled(false);
//		nDConveyanceReport.setEnabled(false);
		cMovePolygonCenterlinePointsToLeveeCenterline.setEnabled(false);
		nNetworkSummaryReport.setEnabled(false);
		tManningsDispersionSpatialDistribution.setEnabled(false);
		tExtendCenterlinesToNodes.setEnabled(false);
//		tCreateStraightlineChanForGridmap.setEnabled(false);
	}//disableWhenNetworkCleared

	/**
	 * Enable buttons and menu items which should be enabled when a network is
	 * loaded
	 */
	public void enableWhenNetworkExists() {
		nSaveAs.setEnabled(true);
		nSaveSpecifiedChannelsAs.setEnabled(true);
		nExportToWKT.setEnabled(true);
		nExportXsectMidpointCoordToWKT.setEnabled(true);
		nClearNetwork.setEnabled(true);
		zZoomToCenterline.setEnabled(true);
		nExportToSEFormat.setEnabled(true);
		nExportTo3DFormat.setEnabled(true);
		nExportChannelsInWindow.setEnabled(true);
		nExportXsectMetadataTable.setEnabled(true);
		nCalculate.setEnabled(true);
		nDisplayReachSummary.setEnabled(true);
		nDisplay3dReachView.setEnabled(true);
		nSelectPointsFor3dReachView.setEnabled(true);
		_selectPointsFor3dViewButton.setEnabled(true);
		_specifyCenterlinesFor3dViewButton.setEnabled(true);
		_networkCalculateButton.setEnabled(true);
		dFitByNetworkMenuItem.setEnabled(true);
		tCalcRect.setEnabled(true);
		tClosePolygonCenterlines.setEnabled(true);
		tRemoveAllCrossSections.setEnabled(true);
		if(_landmark!=null) {
			tCreateDSM2OutputLocations.setEnabled(true);
//			tCreateStraightlineChanForGridmap.setEnabled(true);
		}
		cMovePolygonCenterlinePointsToLeveeCenterline.setEnabled(true);
//		nAWDSummaryReport.setEnabled(true);
//		nXSCheckReport.setEnabled(true);
//		nDConveyanceReport.setEnabled(true);
		nNetworkSummaryReport.setEnabled(true);
		tManningsDispersionSpatialDistribution.setEnabled(true);
		tExtendCenterlinesToNodes.setEnabled(true);
	}

	/**
	 * disable buttons and menu items which should be disabled when landmard is
	 * cleared
	 */
	public void disableWhenLandmarkCleared() {
		lSave.setEnabled(false);
		lSaveAs.setEnabled(false);
		dFitByLandmarkMenuItem.setEnabled(false);
		zZoomToNode.setEnabled(false);
		cLandmarks.setEnabled(false);
		tCreateDSM2OutputLocations.setEnabled(false);
		tExtendCenterlinesToNodes.setEnabled(false);
//		tCreateStraightlineChanForGridmap.setEnabled(false);
		tCreateDCDNodeLandmarkFile.setEnabled(false);
	}

	/**
	 * enable buttons and menu items which should be enabled after centerline
	 * selected
	 */
	protected void enableAfterCenterlineSelected() {
		if (DEBUG)
			System.out.println("centerline selected!");

		_moveButton.setEnabled(true);
		_insertButton.setEnabled(true);
		_addUpstreamPointButton.setEnabled(true);
		_addDownstreamPointButton.setEnabled(true);
		_deleteButton.setEnabled(true);
		_addXsectButton.setEnabled(true);
		cDisplaySummary.setEnabled(true);
		cView3d.setEnabled(true);
		nDisplayReachSummary.setEnabled(true);
		nDisplay3dReachView.setEnabled(true);
		_selectPointsFor3dViewButton.setEnabled(true);
		_specifyCenterlinesFor3dViewButton.setEnabled(true);
		nSelectPointsFor3dReachView.setEnabled(true);
		cPlotAllCrossSections.setEnabled(true);
		cDeletePointsInWindow.setEnabled(true);
		cDeletePointsOutsideWindow.setEnabled(true);
		cAddXSAtComputationalPoints.setEnabled(true);
		cReverseCenterline.setEnabled(true);
		cMovePolygonCenterlinePointsToLeveeCenterline.setEnabled(true);
		fSavePointsInsideOutsidePolygon.setEnabled(true);
		cRemoveAllCrossSections.setEnabled(true);
		if (getXsectSelected()) {
			_removeXsectButton.setEnabled(true);
			_moveXsectButton.setEnabled(true);
			_viewXsectButton.setEnabled(true);
		} else {
			_removeXsectButton.setEnabled(false);
			_moveXsectButton.setEnabled(false);
			_viewXsectButton.setEnabled(false);
		}
		// _cMovePointMenuItem.setEnabled(true);
		// _cAddPointMenuItem.setEnabled(true);
		// _cInsertPointMenuItem.setEnabled(true);
		// _cDeletePointMenuItem.setEnabled(true);
		// _cAddXsectMenuItem.setEnabled(true);
		// _cRemoveXsectMenuItem.setEnabled(true);

		setCenterlineSelected(true);

		//// cRename.setEnabled(true);
	}// enableAfterCenterlineSelected

	/**
	 * disable buttons and menu items which should not be enabled if not
	 * centerline selected
	 */
	protected void disableIfNoCenterlineSelected() {
		setDefaultModesStates();
		_moveButton.setEnabled(false);
		_insertButton.setEnabled(false);
		_addUpstreamPointButton.setEnabled(false);
		_addDownstreamPointButton.setEnabled(false);
		_deleteButton.setEnabled(false);
		_addXsectButton.setEnabled(false);
		_moveXsectButton.setEnabled(false);
		_removeXsectButton.setEnabled(false);
		_viewXsectButton.setEnabled(false);
		fSavePointsInsideOutsidePolygon.setEnabled(false);

		// _cMovePointMenuItem.setEnabled(false);
		// _cAddPointMenuItem.setEnabled(false);
		// _cInsertPointMenuItem.setEnabled(false);
		// _cDeletePointMenuItem.setEnabled(false);
		// _cAddXsectMenuItem.setEnabled(false);
		// _cRemoveXsectMenuItem.setEnabled(false);
		// _cMoveXsectMenuItem.setEnabled(false);
		cfXsect.setEnabled(false);

		setCenterlineSelected(false);
		//// cRename.setEnabled(false);
	}// disableIfNoCenterlineSelected

	/**
	 * enable buttons and menu items which should be enabled when xsect selected
	 */
	protected void enableAfterXsectSelected() {
		cfXsect.setEnabled(true);
		_moveXsectButton.setEnabled(true);
		// _cMoveXsectMenuItem.setEnabled(true);
		xView.setEnabled(true);
		_removeXsectButton.setEnabled(true);
		_viewXsectButton.setEnabled(true);
		xAdjustLength.setEnabled(true);
		xExtractData.setEnabled(true);
		setXsectSelected(true);
	}// enableAfterXsectSelected

	/**
	 * disable buttons and menu items which should not be enabled if no xsect
	 * selected
	 */
	protected void disableIfNoXsectSelected() {
		_moveXsectButton.setEnabled(false);
		// _cMoveXsectMenuItem.setEnabled(false);

		cfXsect.setEnabled(false);
		xView.setEnabled(false);
		_viewXsectButton.setEnabled(false);
		xAdjustLength.setEnabled(false);
		xExtractData.setEnabled(false);
		setXsectSelected(false);
	}// disableIfNoXsectSelected

	/**
	 * adds a legend (panel of buttons) which shows the colors being used to
	 * plot bathymetry data. Called when colorBy option changed, and possibly in
	 * other situations.
	 */
	public void updateColorLegend() {
		int numButtons = 0;
		_legendPanel.setLayout(new GridLayout(numButtons, 1));
		String legendItemName = null;
		Color buttonColor = null;

		_legendPanel.removeAll();

		if (_plot != null && getColorUniform() == false) {
			if (getColorByElev()) {
				numButtons = getNumElevBins();
			} else if (getColorBySource()) {
				numButtons = _plot._bathymetryData.getNumSources();
			} else if (getColorByYear()) {
				numButtons = _plot._bathymetryData.getNumYears();
			}
			setDefaultColors(numButtons);

			if (getColorByElev()) {
				headerButton.setText(_depthLegendTitle);
			} else if (getColorBySource()) {
				headerButton.setText(_sourceLegendTitle);
			} else if (getColorByYear()) {
				headerButton.setText(_yearLegendTitle);
			}

			_legendPanel.add(headerButton);

			for (int i = 0; i <= numButtons - 1; i++) {
				if (getColorByElev()) {
					legendItemName = _plot.getLegendItemName(i);
				} else if (getColorBySource()) {
					legendItemName = _plot._bathymetryData.getSource(i);
				} else if (getColorByYear()) {
					legendItemName = Integer.toString(_plot._bathymetryData.getYear(i));
				}
				buttonColor = _plot.getLegendItemColor(i);
				// _legendPanel.add(new Button(legendItemName));
				JButton b = getButton(i);
				b.setBorder(_raisedBevel);
				b.setToolTipText("click to change color");
				_legendPanel.add(b);
				getButton(i).setText(legendItemName);
				_legendPanel.getComponent(i + 1).setBackground(buttonColor);
			}
		} // if plot isn't null
		_canvas1.redoNextPaint();
		_canvas1.repaint();
		// validate();
	}// updateColorLegend

	/**
	 * sets all buttons to true
	 */
	private void enableCenterlineEditButtons() {
		_moveButton.setEnabled(true);
		_addUpstreamPointButton.setEnabled(true);
		_addDownstreamPointButton.setEnabled(true);
		_insertButton.setEnabled(true);
		_deleteButton.setEnabled(true);
		_addXsectButton.setEnabled(true);
		_removeXsectButton.setEnabled(true);
		_moveXsectButton.setEnabled(true);
		_zoomBoxButton.setEnabled(true);
	}// enableCenterlineEditButtons

	private void enableLandmarkEditButtons() {
		// _landmarkAddButton.setEnabled(true);
		// _landmarkEditButton.setEnabled(true);
		// _landmarkMoveButton.setEnabled(true);
		// _landmarkDeleteButton.setEnabled(true);
		lAddPopup.setEnabled(true);
		lEditPopup.setEnabled(true);
		lMovePopup.setEnabled(true);
		lDeletePopup.setEnabled(true);
	}

	/*
	 * is this different from pressArrowButton?
	 */
	public void setDefaultModesStates() {
		_cursorButton.setSelected(true);
		////////// _centerlineLandmarkEditButtonGroup.setSelected(_cursorButton,true);
	}

	/**
	 * updates displayed value of centerline name/num and mouse position
	 */
	public void updateInfoPanel(String centerlineName) {
		_centerlineLabel.setText("Selected Centerline:  " + centerlineName);
		updateInfoPanel(_net.getCenterline(centerlineName));
	}// updateInfoPanel

	private void updateInfoPanel(Centerline centerline) {
		if(centerline==null) {
			_centerlineLengthLabel.setText("Centerline Length: ");
			_centerlineVolumeLabel.setText("Centerline Volume: ");
			_centerlineMaxAreaRatioLabel.setText("Centerline MAR: ");
		}else {
			_centerlineLengthLabel.setText("Centerline Length: "+String.format("%,.0f", centerline.getLengthFeet()));
			_centerlineVolumeLabel.setText("Centerline Volume: "+String.format("%,.0f", centerline.getChannelVolumeEstimateNoInterp(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS)));
			_centerlineMaxAreaRatioLabel.setText("Centerline MAR: " +String.format("%.2f", centerline.getMaxAreaRatio()));
		}
	}
	
	/**
	 * updates displayed value of cross-section properties
	 */
	public void updateInfoPanelXSProp() {
		Xsect xsect = _net.getSelectedXsect();
		if(xsect!=null) {
			double area = xsect.getAreaSqft(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			double width = xsect.getWidthFeet(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			double wetp = xsect.getWettedPerimeterFeet(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			double hd = xsect.getHydraulicDepthFeet(CsdpFunctions.ELEVATION_FOR_CENTERLINE_SUMMARY_CALCULATIONS);
			double nf = 100.0;
			if (area >= 0.0f && width >= 0.0f && wetp >= 0.0f && hd >= 0.0f) {
				String sa = Double.toString((((double) ((int) (area * nf))) / nf));
				String sw = Double.toString((((double) ((int) (width * nf))) / nf));
				String sp = Double.toString((((double) ((int) (wetp * nf))) / nf));
				String shd = Double.toString((((double) ((int) (hd * nf))) / nf));
				_areaLabel.setText("Xsect Area:  " + sa);
				_widthLabel.setText("Top Width:  " + sw);
				_wetPLabel.setText("Wetted Perimeter:  " + sp);
				_hydraulicDepthLabel.setText("Hydraulic Depth:  " + shd);
			} else {
				_areaLabel.setText("Xsect Area:  ");
				_widthLabel.setText("Top Width:  ");
				_wetPLabel.setText("Wetted Perimeter:  ");
				_hydraulicDepthLabel.setText("Hydraulic Depth:  ");
			}
		}
	}// updateInfoPanel

	public void updateMetadataDisplay(CsdpFileMetadata m) {
		_horDatumLabel.setText("Horizontal Datum: " + m.getHDatumString());
		_horDatumUnitsLabel.setText("Hor. Datum Units: " + m.getHUnitsString());
		_verDatumLabel.setText("Vertical Datum: " + m.getVDatumString());
		_verDatumUnitsLabel.setText("Ver. Datum Units: " + m.getVUnitsString());
	}

	/**
	 * updates filename display
	 */
	public void updateBathymetryFilename(String bfName) {
		_bathymetryFileLabel.setText("Bathymetry Filename:  " + bfName);
	}// updateBathymetryFilename

	/**
	 * updates filename display
	 */
	public void updateNetworkFilename(String nfName) {
		_networkFileLabel.setText("Network Filename:  " + nfName);
	}// updateNetworkFilename

	/**
	 * updates filename display
	 */
	public void updateLandmarkFilename(String lfName) {
		_landmarkFileLabel.setText("Landmark Filename:  " + lfName);
	}// updateLandmarkFilename

	/**
	 * updates filename display
	 */
	public void updatePropertiesFilename(String pfName) {
		_propertiesFileLabel.setText("Properties Filename:  " + pfName);
	}// updatePropertiesFilename

	/**
	 * updates filename display
	 */
	public void updateDigitalLineGraphFilename(String dlgFilename) {
		_dlgFileLabel.setText("DLG Filename:  " + dlgFilename);
	}

	public void setDigitalLineGraph(DigitalLineGraph dlg) {
		_dDigitalLineGraph = dlg;
		_canvas1.setDigitalLineGraph(_dDigitalLineGraph);
	}

	/**
	 * updates displayed value of centerline name/num and mouse position
	 */
	public void updateInfoPanel(int xsectNum) {
		if (xsectNum >= 0)
			_xsectLabel.setText("Xsect:  " + xsectNum);
		else
			_xsectLabel.setText("Xsect:");
	}// updateInfoPanel

	/**
	 * updates displayed value of centerline name/num and mouse position
	 */
	public void updateInfoPanel(double mouseX, double mouseY) {
		// _plot is instance of BathymetryPlot

		if (_plot != null) {
			String xLabel = null;
			String yLabel = null;

			try {
				ZoomState zs = _plot.getCurrentZoomState();
				CoordConv cc = zs.getCoordConv();
				double[] bb = zs.getPlotBoundaries();
				double minX = bb[CsdpFunctions.minXIndex];
				double minY = bb[CsdpFunctions.minYIndex];

				cc.pixelsToLength((int) mouseX, (int) mouseY, minX, minY, _cursorPosition);

				double x = _cursorPosition[0];
				double y = _cursorPosition[1];

				// if(! _canvas1._useZoomBox){
				// x -= _plot._centerX;
				// y -= _plot._centerY;
				// }
				x = CsdpFunctions.feetToMeters(x);
				y = CsdpFunctions.feetToMeters(y);
				xLabel = "Easting (UTM):  "+ String.format("%.1f", x);
				yLabel = "Northing (UTM): "+ String.format("%.1f", y);
				_mouseXLabel.setText(xLabel);
				_mouseYLabel.setText(yLabel);

				if (DEBUG) {
					System.out.println("updating coordinate display");
					System.out.println("--------------------------------------");
					System.out.println(
							"minX, minY=" + CsdpFunctions.feetToMeters(minX) + "," + CsdpFunctions.feetToMeters(minY));
					System.out.println("x,y=" + x + "," + y);
					System.out.println("--------------------------------------");
				}

			} catch (java.lang.NullPointerException e) {
				System.out.println("null pointer in CsdpFrame.updateInfoPanel: bathymetry probably not loaded yet.");
			}
		} else {
			_mouseXLabel.setText(Double.toString(mouseX));
			_mouseYLabel.setText(Double.toString(mouseY));
		}
	}// updateInfoPanel

	/**
	 * returns color from color table. Used for coloring bathymetry
	 */
	public Color getColor(int index) {
		if (getNumColors() == 0) {
			if (DEBUG)
				System.out.println("setting default colors");
			setDefaultColors();
		}
		Color c = null;
		if (index < getNumColors())
			c = (Color) _colorsVector.elementAt(index);
		else {
			while (index >= getNumColors()) {
				_colorsVector.addElement(_colorsVector.elementAt(getNumColors() - 1));
			} // while
			c = (Color) _colorsVector.elementAt(index);
		} // else
		return c;
	}// getColor

	/**
	 * sets color used to color bathymetry. called by AdjustRGBColor, which
	 * allows user to specify color.
	 */
	public void setColor(int index, Color c) {
		if (!_userSetColors.containsKey(new Integer(index))) {
			System.out.println("before null: _userSetColors, index, color=" + _userSetColors + "," + index + "," + c);
			_userSetColors.put(new Integer(index), c);
		}
		setDefaultColors();
		// if(getNumColors() == 0) setDefaultColors();
		// if(index < getNumColors()) _colors.setElementAt(c,index);
		// else{
		// _colors.addElement(c);
		// }//else
	}// setColor

	/**
	 *
	 */
	public static int getNumColors() {
		return _colorsVector.size();
	}

	private void setDefaultColors() {
		setDefaultColors(_colorsVector.size());
	}


	public Color[] generateColors(int n)
	{
	    Color[] cols = new Color[n];
	    for(int i = 0; i < n; i++)
	    {
	        cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
	    }
	    return cols;
	}

	private void setDefaultColors(int numColors) {
		// to make rainbow, use HSB colors. Set S to 100, B to 100, and vary H
		// from 1 to 280
		
		List<Color> colorsList = ColorGenerator.pick(numColors);
		
		System.out.println("setDefaultColors: " + numColors);
		_colorsVector.clear();

		for(int i=colorsList.size()-1; i>=0; i--) {
			_colorsVector.add(colorsList.get(i));
		}
		//		float increment = (float) (0.8 / ((float) numColors - 1));
//		float s = (float) 1.0;
//		float b = (float) 1.0;
//
//		for (int h = 0; h < numColors; h++) {
//			float hue = (float) h * increment;
//			int rgb = Color.HSBtoRGB(hue, s, b);
//			_colorsVector.addElement(new Color(rgb));
//			System.out.println("adding color " + hue + "," + s + "," + b + "," + rgb);
//		}

		//this might do a better job, but colors can be hard to distinguish
		//	 * Copied from https://stackoverflow.com/questions/223971/generating-spectrum-color-palettes
//		for(int i = 0; i < numColors; i++){
//	        _colorsVector.addElement(Color.getHSBColor((float) i / (float) numColors, 0.85f, 1.0f));
//	    }
		// go through user set colors if any, and put them into color palette
		for (Enumeration<Integer> e = _userSetColors.keys(); e.hasMoreElements();) {
			Integer indexObject = e.nextElement();
			int index = indexObject.intValue();
			Color c = _userSetColors.get(indexObject);
			System.out.println("before out of bounds: colors.size, index=" + _colorsVector.size() + "," + index);
			if(_colorsVector.size()<index+1) {
				while(_colorsVector.size()<index+1) {
					_colorsVector.addElement(c);
				}
			}else {
				_colorsVector.set(index, c);
			}
		}

		// _colors.addElement(new Color(255, 0, 0 )); // bright red
		// _colors.addElement(new Color(214,130, 50 )); // dark orange 204,102,0
		// _colors.addElement(new Color(255,204,102 )); // light orange
		// _colors.addElement(new Color(210, 30, 30 )); // dark red
		// _colors.addElement(new Color(255,255, 0 )); // dark yellow
		// _colors.addElement(new Color( 0,255, 0 )); // dark green
		// _colors.addElement(new Color(153,204,102 )); // light green
		// _colors.addElement(new Color(102,255,255 )); // medium blue
		// _colors.addElement(new Color(153,204,255 )); // light blue
		// _colors.addElement(new Color(153,153,255 ));// light purple
		// _colors.addElement(new Color( 0,102,204 )); // dark blue

		// _colors.addElement(11] = new Color(153,102,255));// medium purple
		// _colors.addElement(12] = new Color(119,119,119));// dark purple
		// _colors.addElement(13] = new Color(119,119,119));// light gray

		// 12 colors from Colorbrewer
		// http://www.personal.psu.edu/faculty/c/a/cab38/ColorBrewerBeta2.html
		// 141,211,199
		// 255,255,179
		// 190,186,218
		// 251,128,114
		// 128,177,211
		// 253,180,98
		// 179,222,105
		// 252,205,229
		// 217,217,217
		// 188,128,189
		// 204,235,197
		// 255,237,111

	}// setDefaultColors

	public double getElevationBin(int index) {
		double f = 0.0;
		if (index < getNumElevBins())
			f = _elevationBins[index];
		else {
			f = _elevationBins[getNumElevBins() - 1];
		}
		return f;
	}

	// public void setDepth(int index, double f){
	// if(index < getNumColors()) depthTable[index] = f;
	// else depthTable[getNumColors()-1] = f;
	// }

	// /**
	// * returns number of depths in depth table. used for coloring by depth
	// */
	// public int getNumDepths(){
	// return _numDepths;
	// }

	public boolean getFitByBathymetryOption() {
		return dFitByBathymetryMenuItem.isSelected();
	}

	public boolean getFitByNetworkOption() {
		return dFitByNetworkMenuItem.isSelected();
	}

	public boolean getFitByLandmarkOption() {
		return dFitByLandmarkMenuItem.isSelected();
	}

	/**
	 * button that displays header information for plan view legend
	 */
	public static JButton headerButton = new JButton("header");

	/**
	 * returns button from array of buttons
	 */
	public JButton getButton(int index) {
		JButton b = null;
		if (index < NUM_BUTTONS)
			b = (JButton) (_buttons.elementAt(index));
		else {
			while (index >= NUM_BUTTONS) {
				_buttons.addElement(new JButton());
				_legendButtonListener.addElement(new AdjustRGBColor(this, index));
				((JButton) _buttons.elementAt(index))
						.addActionListener((ActionListener) (_legendButtonListener.elementAt(index)));
				NUM_BUTTONS++;
			} // while
			b = (JButton) (_buttons.elementAt(index));
		} // else
		return b;
	}// getButton

	/**
	 *
	 */
	public PlanViewCanvas getPlanViewCanvas(int canvasNum) {
		return _canvas1;
	}

	private Vector _legendButtonListener = new Vector();
	protected int NUM_BUTTONS = 0;
	private Vector _buttons = new Vector();
	/**
	 * The component on which the graph is drawn.
	 */
	private PlanViewCanvas _canvas1 = new PlanViewCanvas(this);
	// PlanViewCanvas _canvas1 = PlanViewCanvas.getInstance();
	JPanel _planViewJPanel = new JPanel(true);
	JScrollPane _sp1;
	// if you want to have more than one bathymetry file open
	// Hashtable _panelObjects = new Hashtable();
	App _app;

	/**
	 * returns inital value of canvas width
	 */
	public int getInitialWidth() {
		return _initialWidth;
	}

	/**
	 * returns inital value of canvas height
	 */
	public int getInitialHeight() {
		return _initialHeight;
	}

	/**
	 * stores size of frame
	 */
	protected Dimension _dim = null;
	protected Network _net;
	NetworkInteractor _networkInteractor;
	LandmarkInteractor _li;
	BathymetryPlot _plot;
	// display parameters

	protected XsectMenu _xsectMenu;
	protected CenterlineMenu _centerlineMenu;
	protected ToolsMenu _toolsMenu;

	public LandmarkInteractor getLandmarkInteractor() {
		return _li;
	}

	public boolean getInsertPointMode() {
		return _insertButton.isSelected();
	}

	public boolean getMovePointMode() {
		return _moveButton.isSelected();
	}

	public boolean getAddUpstreamPointMode() {
		return _addUpstreamPointButton.isSelected();
	}
	
	public boolean getAddDownstreamPointMode() {
		return _addDownstreamPointButton.isSelected();
	}

	public boolean getDeletePointMode() {
		return _deleteButton.isSelected();
	}

	public boolean getAddXsectMode() {
		return _addXsectButton.isSelected();
	}

	public boolean getRemoveXsectMode() {
		return _removeXsectButton.isSelected();
	}

	public boolean getMoveXsectMode() {
		return _moveXsectButton.isSelected();
	}

	public boolean getViewXsectMode() {
		return _viewXsectButton.isSelected();
	}

	public boolean getZoomBoxMode() {
		return _zoomBoxButton.isSelected();
	}

	public boolean getDeleteCenterlinePointsInBoxMode() {
		return _deleteCenterlinePointsInBoxButton.isSelected();
	}
	public boolean getExportChannelsInWindowMode() {
		return _exportChannelsInWindowButton.isSelected();
	}
	
	public boolean getDeleteCenterlinePointsOutsideBoxMode() {
		return _deleteCenterlinePointsOutsideBoxButton.isSelected();
	}
	
	public void pressDeleteCenterlinePointsOutsideBoxButton() {
		_deleteCenterlinePointsOutsideBoxButton.doClick();
	}

	public boolean getSelectCenterlineForDataEntryDialogMode() {
		return _selectCenterlineForDataEntryDialogButton.isSelected();
	}
	
	
	public boolean getZoomPanMode() {
		return _zoomPanButton.isSelected();
	}

	public boolean getSelectPointsFor3dViewMode() {
		return _selectPointsFor3dViewButton.isSelected();
	}
	
	// add landmark, edit landmark(move, rename), delete landmark
	public boolean getAddLandmarkMode() {
		return lAddPopup.isSelected();
	}

	public boolean getEditLandmarkMode() {
		return lEditPopup.isSelected();
	}

	public boolean getMoveLandmarkMode() {
		return lMovePopup.isSelected();
	}

	public boolean getDeleteLandmarkMode() {
		return lDeletePopup.isSelected();
	}

	/**
	 * Checks to see if color bathymetry uniform is selected
	 */
	public boolean getColorUniform() {
		return _colorUniformButton.isSelected();
	}

	/**
	 * Checks to see if color bathymetry by depth option is selected
	 */
	public boolean getColorByElev() {
		return _colorByElevButton.isSelected();
	}

	/**
	 * Checks to see if color bathymetry by source option is selected
	 */
	public boolean getColorBySource() {
		return _colorBySourceButton.isSelected();
	}

	/**
	 * Checks to see if color bathymetry by year option is selected
	 */
	public boolean getColorByYear() {
		return _colorByYearButton.isSelected();
	}


	private void setCenterlineSelected(boolean b) {
		_centerlineSelected = b;
	}

	private boolean getCenterlineSelected() {
		return _centerlineSelected;
	}

	private void setXsectSelected(boolean b) {
		_xsectSelected = b;
	}

	private boolean getXsectSelected() {
		return _xsectSelected;
	}

	public void showEditLandmarkMenu(boolean landmarkSelected, MouseEvent e) {
		lAddPopup.setEnabled(true);
		lMovePopup.setEnabled(landmarkSelected);
		lEditPopup.setEnabled(landmarkSelected);
		lDeletePopup.setEnabled(landmarkSelected);
		cfLandmarkPopup.show(e.getComponent(), e.getX(), e.getY());
	}// showEditLandmarkMenu

	public boolean landmarkMenuIsVisible() {
		return cfLandmarkPopup.isVisible();
	}

	public double getMinElevBin() {
		return _minElevBin;
	}

	public double getMaxElevBin() {
		return _maxElevBin;
	}

	public int getNumElevBins() {
		return _numElevBins;
	}

	public void updateElevBinValues(double min, double max, int num) {
		_minElevBin = min;
		_maxElevBin = max;
		_numElevBins = num;
		_elevationBins = new double[num];
		double binSize = (max - min) / (num - 1.0f);
		_elevationBins[0] = max;
		for (int i = 1; i <= num - 1; i++) {
			_elevationBins[i] = _elevationBins[i - 1] - binSize;
		}
	}//updateElevBinValues

	public void setCenterlineSelectionDialog(DataEntryDialog parentDialog) {
		this.parentDialog = parentDialog;
	}

	public void sendSelectedCenterlineNameToDataEntryDialog(String centerlineName) {
		this.parentDialog.setSelectedCenterlineName(centerlineName);
		this.parentDialog.setVisible(true);
	}

	public void setSelectPointsFor3dViewMode() {
		_selectPointsFor3dViewButton.doClick();
	}





}//class CSDPFrame
