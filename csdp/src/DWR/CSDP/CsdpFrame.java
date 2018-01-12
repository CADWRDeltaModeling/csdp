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
import vista.graph.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory; 
import java.lang.System;
import java.net.URL;

/**
 * Display Frame
 *
 * @author $Author: 
 * @version $Id: CsdpFrame.java,v 1.7 2005/04/08 03:12:40 btom Exp $
 */
public class CsdpFrame extends JFrame{  

public CsdpFrame(App app) {
    makeIconButtons();
  setTitle("Cross-Section Development Program Version "+CsdpFunctions.getVersion());
  //  setIconImage(Toolkit.getDefaultToolkit().
  //   createImage("DWR_Logo-1.0in.gif"));

  //only for JFrame
  setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  _app=app;

  setSize(getInitialWidth(), getInitialHeight());
  //  setCurrentWidth(getInitialWidth());
  //setCurrentHeight(getInitialHeight());
  _dim = getSize();

  _canvas1.setPlotter(app._bathymetryPlot, _dim);

  /////  _canvas1.setBackground(Color.white);

  createGui();
  //  setColorByDepth();
  setResizable(true);
  setBackground(Color.white);
  pack();
  setVisible(true);
  setDefaultColors();
}

    private void makeIconButtons(){
	URL bathUrl = this.getClass().getResource("images/FileOpenButton.jpg");
	URL propUrl = this.getClass().getResource("images/PropOpenButton.jpg");
	URL netOpenUrl = this.getClass().getResource("images/NetworkOpenButton.jpg");
	URL netSaveUrl = this.getClass().getResource("images/NetworkSaveButton.jpg");
	URL arrowUrl = this.getClass().getResource("images/ArrowButton.jpg");
	URL insertPointUrl = this.getClass().getResource("images/InsertPointButton.gif");
	URL movePointUrl = this.getClass().getResource("images/MovePointButton.gif");
	URL addPointUrl = this.getClass().getResource("images/AddPointButton.gif");
	URL deletePointUrl = this.getClass().getResource("images/DeletePointButton.gif");
	URL addXsectUrl = this.getClass().getResource("images/AddXsectButton.gif");

	URL removeXsectUrl = this.getClass().getResource("images/RemoveXsectButton.gif");
	URL moveXsectUrl = this.getClass().getResource("images/MoveXsectButton.gif");

	URL viewXsectUrl = this.getClass().getResource("images/ViewXsectButton.gif");

	URL colorUniformUrl = this.getClass().getResource("images/ColorUniformButton.gif");
	URL colorElevUrl = this.getClass().getResource("images/ColorElevButton.gif");
	URL colorSourceUrl = this.getClass().getResource("images/ColorSourceButton.gif");
	URL colorYearUrl = this.getClass().getResource("images/ColorYearButton.gif");
	URL filterSourceUrl = this.getClass().getResource("images/FilterSourceButton.gif");
	URL filterYearUrl = this.getClass().getResource("images/FilterYearButton.gif");
	URL filterLabelUrl = this.getClass().getResource("images/FilterLabel.gif");

	_fileOpenIcon = new ImageIcon(bathUrl);
	_propOpenIcon = new ImageIcon(propUrl);
	_networkOpenIcon = new ImageIcon(netOpenUrl);
	_networkSaveIcon = new ImageIcon(netSaveUrl);
	_cursorIcon = new ImageIcon(arrowUrl);
	_insertIcon = new ImageIcon(insertPointUrl);
	_moveIcon = new ImageIcon(movePointUrl);
	_addIcon = new ImageIcon(addPointUrl);
	_deleteIcon = new ImageIcon(deletePointUrl);
	_addXsectIcon = new ImageIcon(addXsectUrl);
	_removeXsectIcon = new ImageIcon(removeXsectUrl);
	_moveXsectIcon = new ImageIcon(moveXsectUrl);

	_viewIcon = new ImageIcon(viewXsectUrl);

	_colorUniformIcon = new ImageIcon(colorUniformUrl);
	_colorElevIcon = new ImageIcon(colorElevUrl);
	_colorSourceIcon = new ImageIcon(colorSourceUrl);
	_colorYearIcon = new ImageIcon(colorYearUrl);
	_filterSourceIcon = new ImageIcon(filterSourceUrl);
	_filterYearIcon = new ImageIcon(filterYearUrl);
	_filterLabelIcon = new ImageIcon(filterLabelUrl);

	URL networkCalculateUrl = this.getClass().getResource("images/NetworkCalculateButton.gif");
	URL cursorIconSelectedUrl = this.getClass().getResource("images/ArrowButtonSelected.jpg");
	URL insertPointButtonSelectedUrl = this.getClass().getResource("images/InsertPointButtonSelected.gif");
	URL moveIconSelectedUrl = this.getClass().getResource("images/MovePointButtonSelected.jpg");
	URL addIconSelectedUrl = this.getClass().getResource("images/AddPointButtonSelected.jpg");
	URL deleteIconSelectedUrl = this.getClass().getResource("images/DeletePointButtonSelected.gif");
	URL addXsectIconSelectedUrl = this.getClass().getResource("images/AddXsectButtonSelected.jpg");
	URL removeXsectIconSelectedUrl = this.getClass().getResource("images/RemoveXsectButtonSelected.jpg");
	URL moveXsectIconSelectedUrl = this.getClass().getResource("images/MoveXsectButtonSelected.jpg");
	URL colorUniformIconSelectedUrl = this.getClass().getResource("images/ColorUniformButtonSelected.gif");
	URL colorElevIconSelectedUrl = this.getClass().getResource("images/ColorElevButtonSelected.gif");
	URL colorSourceIconSelectedUrl = this.getClass().getResource("images/ColorSourceButtonSelected.gif");
	URL colorYearIconSelectedUrl = this.getClass().getResource("images/ColorYearButtonSelected.gif");
	URL zoomBoxIconUrl = this.getClass().getResource("images/ZoomBoxButton.jpg");
	URL zoomBoxIconSelectedUrl = this.getClass().getResource("images/ZoomBoxButtonSelected.jpg");
	URL zoomPanIconUrl = this.getClass().getResource("images/ZoomPanButton.gif");
	URL zoomPanIconSelectedUrl = this.getClass().getResource("images/ZoomPanButtonSelected.gif");
	URL zoomFitIconUrl = this.getClass().getResource("images/ZoomFitButton.jpg");
	URL zoomFitIconRolloverUrl = this.getClass().getResource("images/ZoomFitButtonRollover.jpg");
	URL zoomUndoIconUrl = this.getClass().getResource("images/Undo24.gif");

// 	URL landmarkAddUrl = this.getClass().getResource("images/LAddButton.gif");
// 	URL landmarkEditUrl = this.getClass().getResource("images/LEditButton.gif");
// 	URL landmarkMoveUrl = this.getClass().getResource("images/LMoveButton.gif");
// 	URL landmarkDeleteUrl = this.getClass().getResource("images/LDeleteButton.gif");
// 	URL landmarkAddSelectedUrl = 
// 	    this.getClass().getResource("images/LAddButtonSelected.gif");
// 	URL landmarkEditSelectedUrl = 
// 	    this.getClass().getResource("images/LEditButtonSelected.gif");
// 	URL landmarkMoveSelectedUrl = 
// 	    this.getClass().getResource("images/LMoveButtonSelected.gif");
// 	URL landmarkDeleteSelectedUrl = 
// 	    this.getClass().getResource("images/LDeleteButtonSelected.gif");

	_networkCalculateIcon = new ImageIcon(networkCalculateUrl);
	_cursorIconSelected = new ImageIcon(cursorIconSelectedUrl);
	_insertIconSelected = new ImageIcon(insertPointButtonSelectedUrl);
	_moveIconSelected = new ImageIcon(moveIconSelectedUrl);
	_addIconSelected = new ImageIcon(addIconSelectedUrl);
	_deleteIconSelected = new ImageIcon(deleteIconSelectedUrl);
	_addXsectIconSelected = new ImageIcon(addXsectIconSelectedUrl);
	_removeXsectIconSelected = new ImageIcon(removeXsectIconSelectedUrl);
	_moveXsectIconSelected = new ImageIcon(moveXsectIconSelectedUrl);
	//landmark edit
// 	_landmarkAddIcon = new ImageIcon(landmarkAddUrl);
// 	_landmarkEditIcon = new ImageIcon(landmarkEditUrl);
// 	_landmarkMoveIcon = new ImageIcon(landmarkMoveUrl);
// 	_landmarkDeleteIcon = new ImageIcon(landmarkDeleteUrl);
// 	_landmarkAddSelectedIcon = new ImageIcon(landmarkAddSelectedUrl);
// 	_landmarkEditSelectedIcon = new ImageIcon(landmarkEditSelectedUrl);
// 	_landmarkMoveSelectedIcon = new ImageIcon(landmarkMoveSelectedUrl);
// 	_landmarkDeleteSelectedIcon = new ImageIcon(landmarkDeleteSelectedUrl);

	_colorUniformIconSelected = new ImageIcon(colorUniformIconSelectedUrl);
	_colorElevIconSelected = new ImageIcon(colorElevIconSelectedUrl);
	_colorSourceIconSelected = new ImageIcon(colorSourceIconSelectedUrl);
	_colorYearIconSelected = new ImageIcon(colorYearIconSelectedUrl);
	_zoomBoxIcon = new ImageIcon(zoomBoxIconUrl);
	_zoomBoxIconSelected = new ImageIcon(zoomBoxIconSelectedUrl);
	_zoomPanIcon = new ImageIcon(zoomPanIconUrl);
	_zoomPanIconSelected = new ImageIcon(zoomPanIconSelectedUrl);
	_zoomFitIcon = new ImageIcon(zoomFitIconUrl);
	_zoomFitIconRollover = new ImageIcon(zoomFitIconRolloverUrl);
	_zoomUndoIcon = new ImageIcon(zoomUndoIconUrl);

	_fileOpenButton    = new JButton(_fileOpenIcon);
	_propOpenButton    = new JButton(_propOpenIcon);
	_networkOpenButton    = new JButton(_networkOpenIcon);
	_networkSaveButton    = new JButton(_networkSaveIcon);
	_networkCalculateButton = new JButton(_networkCalculateIcon);
	_colorUniformButton = new JRadioButton(_colorUniformIcon);
	_colorByElevButton = new JRadioButton(_colorElevIcon,true);
	_colorBySourceButton = new JRadioButton(_colorSourceIcon);
	_colorByYearButton = new JRadioButton(_colorYearIcon);
	_filterSourceButton = new JButton(_filterSourceIcon);
	_filterYearButton = new JButton(_filterYearIcon);
	_filterLabel = new JLabel(_filterLabelIcon);

	_cursorButton    = new JRadioButton(_cursorIcon);
	_insertButton   = new JRadioButton(_insertIcon);
	_moveButton     = new JRadioButton(_moveIcon);
	_addButton      = new JRadioButton(_addIcon);
	_deleteButton   = new JRadioButton(_deleteIcon);
	_addXsectButton = new JRadioButton(_addXsectIcon);
	_removeXsectButton = new JRadioButton(_removeXsectIcon);
	_moveXsectButton = new JRadioButton(_moveXsectIcon);
	_viewXsectButton = new JRadioButton(_viewIcon);
	_zoomBoxButton = new JRadioButton(_zoomBoxIcon);
	_zoomPanButton = new JRadioButton(_zoomPanIcon);
	_zoomFitButton = new JButton(_zoomFitIcon);
	_zoomUndoButton = new JButton(_zoomUndoIcon);

//  	_landmarkOpenButton = new JButton("Open Landmark File");
//  	_landmarkSaveButton = new JButton("Save Landmark File");
//  	_landmarkAddButton = new JButton("Add landmark");
//  	_landmarkEditButton = new JButton("Edit landmark text");
//  	_landmarkMoveButton = new JButton("Move landmark");
//  	_landmarkDeleteButton = new JButton("Delete landmark");
//  	_landmarkAddSelectedButton = new JRadioButton(_landmarkAddSelectedIcon);
//  	_landmarkEditSelectedButton = new JRadioButton(_landmarkEditSelectedIcon);
//  	_landmarkMoveSelectedButton = new JRadioButton(_landmarkMoveSelectedIcon);
//  	_landmarkDeleteSelectedButton = new JRadioButton(_landmarkDeleteSelectedIcon);
    }

  /**
   * makes menus and buttons; creates and registers listeners
   */
    public void createGui() {
	//editing buttons
	updateElevBinValues(getMinElevBin(), getMaxElevBin(), getNumElevBins());
      ////    JPanel btnPanel = new JPanel(true);
      JToolBar btnPanel = new JToolBar();
      btnPanel.setFloatable(false);

    _fileOpenButton.setPreferredSize(_wideIconSize);
    _propOpenButton.setPreferredSize(_wideIconSize);
    _networkOpenButton.setPreferredSize(_wideIconSize);
    _networkSaveButton.setPreferredSize(_wideIconSize);
    _cursorButton.setPreferredSize(_iconSize);
    _colorUniformButton.setPreferredSize(_colorByIconSize);
    _colorByElevButton.setPreferredSize(_colorByIconSize);
    _colorBySourceButton.setPreferredSize(_colorByIconSize);
    _colorByYearButton.setPreferredSize(_colorByIconSize);
    _filterSourceButton.setPreferredSize(_colorByIconSize);
    _filterYearButton.setPreferredSize(_colorByIconSize);
//      _moveButton.setPreferredSize(_iconSize);
//      _insertButton.setPreferredSize(_iconSize);
//      _addButton.setPreferredSize(_iconSize);
//      _deleteButton.setPreferredSize(_iconSize);
//      _addXsectButton.setPreferredSize(_iconSize);
//      _removeXsectButton.setPreferredSize(_iconSize);
//      _moveXsectButton.setPreferredSize(_iconSize);
//      _viewXsectButton.setPreferredSize(_iconSize);
//      _networkCalculateButton.setPreferredSize(_iconSize);
//      _zoomBoxButton.setPreferredSize(_iconSize);
//      _zoomPanButton.setPreferredSize(_iconSize);
    _zoomFitButton.setPreferredSize(_iconSize);
    _zoomUndoButton.setPreferredSize(_iconSize);
    _cursorButton.setSelectedIcon(_cursorIconSelected);
    _colorUniformButton.setSelectedIcon(_colorUniformIconSelected);
    _colorByElevButton.setSelectedIcon(_colorElevIconSelected);
    _colorBySourceButton.setSelectedIcon(_colorSourceIconSelected);
    _colorByYearButton.setSelectedIcon(_colorYearIconSelected);
    _moveButton.setSelectedIcon(_moveIconSelected);
    _insertButton.setSelectedIcon(_insertIconSelected);
    _addButton.setSelectedIcon(_addIconSelected);
    _deleteButton.setSelectedIcon(_deleteIconSelected);
    _addXsectButton.setSelectedIcon(_addXsectIconSelected);
    _removeXsectButton.setSelectedIcon(_removeXsectIconSelected);
    _moveXsectButton.setSelectedIcon(_moveXsectIconSelected);
    _zoomBoxButton.setSelectedIcon(_zoomBoxIconSelected);
    _zoomPanButton.setSelectedIcon(_zoomPanIconSelected);

//     _landmarkAddButton.setSelectedIcon(_landmarkAddSelectedIcon);
//     _landmarkEditButton.setSelectedIcon(_landmarkEditSelectedIcon);
//     _landmarkMoveButton.setSelectedIcon(_landmarkMoveSelectedIcon);
//     _landmarkDeleteButton.setSelectedIcon(_landmarkDeleteSelectedIcon);

    //rollover doesn't work?
//      _zoomFitButton.setRolloverEnabled(true);
//      _zoomFitButton.setRolloverIcon(_zoomFitIconRollover);
    //_zoomFitButton.setRolloverSelectedIcon(_zoomFitIconRollover);

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
    //color panel
    JPanel colorByPanel = new JPanel(new GridLayout(2,2));
    colorByPanel.setBorder(_raisedBevel);
    colorByPanel.add(_colorUniformButton);
    colorByPanel.add(_colorByElevButton);
    colorByPanel.add(_colorBySourceButton);
    colorByPanel.add(_colorByYearButton);
    //filter panel
    JPanel filterPanel = new JPanel(new GridLayout(2,1));
    JPanel filterLabelPanel = new JPanel(new GridLayout(1,1));
    JPanel filterButtonPanel = new JPanel(new GridLayout(1,2));
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
    btnPanel.add(_addButton);
    btnPanel.add(_deleteButton);
    btnPanel.add(_addXsectButton);
    btnPanel.add(_moveXsectButton);
    btnPanel.add(_removeXsectButton);
    btnPanel.add(_viewXsectButton);
    btnPanel.add(_zoomBoxButton);
    btnPanel.add(_zoomPanButton);
    //don't use these buttons; better to right click
//     btnPanel.add(_landmarkAddButton);
//     btnPanel.add(_landmarkEditButton);
//     btnPanel.add(_landmarkMoveButton);
//     btnPanel.add(_landmarkDeleteButton);

    _centerlineLandmarkEditButtonGroup = new ButtonGroup();
    _centerlineLandmarkEditButtonGroup.add(_cursorButton);
    _centerlineLandmarkEditButtonGroup.add(_moveButton);
    _centerlineLandmarkEditButtonGroup.add(_insertButton);
    _centerlineLandmarkEditButtonGroup.add(_addButton);
    _centerlineLandmarkEditButtonGroup.add(_deleteButton);
    _centerlineLandmarkEditButtonGroup.add(_addXsectButton);
    _centerlineLandmarkEditButtonGroup.add(_moveXsectButton);
    _centerlineLandmarkEditButtonGroup.add(_removeXsectButton);
    _centerlineLandmarkEditButtonGroup.add(_zoomBoxButton);
    _centerlineLandmarkEditButtonGroup.add(_zoomPanButton);
    
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
    _addButton.setToolTipText("add centerline point ");
    _insertButton.setToolTipText("insert centerline point ");
    _deleteButton.setToolTipText("delete centerline point ");
    _addXsectButton.setToolTipText("add cross-section line ");
    _removeXsectButton.setToolTipText("remove cross-section line ");
    _moveXsectButton.setToolTipText("move cross-section line ");
//     _landmarkAddButton.setToolTipText("Add landmark");
//     _landmarkEditButton.setToolTipText("Edit landmark");
//     _landmarkMoveButton.setToolTipText("Move landmark");
//     _landmarkDeleteButton.setToolTipText("Delete landmark");
    _zoomBoxButton.setToolTipText("draw a box for zooming ");
    _zoomPanButton.setToolTipText("Pan");
    _zoomUndoButton.setToolTipText("Undo last zoom/pan");
    _zoomFitButton.setToolTipText("fit all data in window");
    _viewXsectButton.setToolTipText("view cross-section ");
    _networkCalculateButton.setToolTipText("Calculate Network");

//      _infoPanel = new JToolBar();
//      _infoPanel.setLayout(new GridLayout(3,4));
//      _infoPanel.setFloatable(true);
    //The info panel is at the bottom of the window
    _infoPanel = new JPanel();
    _infoPanel.setLayout(new GridLayout(4,4));
    ////_infoPanel.setOpaque(true);
    _infoPanel.add(_horDatumLabel);
    _infoPanel.add(_horDatumUnitsLabel);
    _infoPanel.add(_verDatumLabel);
    _infoPanel.add(_verDatumUnitsLabel);
    _infoPanel.add(_centerlineLabel);
    _infoPanel.add(_xsectLabel);
    _infoPanel.add(_mouseXLabel);
    _infoPanel.add(_mouseYLabel);
    _infoPanel.add(_areaLabel);
    _infoPanel.add(_wetPLabel);
    _infoPanel.add(_widthLabel);
    _infoPanel.add(_hydraulicDepthLabel);
    _infoPanel.add(_bathymetryFileLabel);
    _infoPanel.add(_networkFileLabel);
    _infoPanel.add(_landmarkFileLabel);
    _infoPanel.add(_propertiesFileLabel);
    
    menubar = new JMenuBar();
    this.setJMenuBar(menubar);

    //  add Canvas to ScrollPane to CardLayout to Frame and set its listeners;
    _planViewJPanel.setLayout(new BorderLayout());
    
    
    //replaced by new zoom feature. no scrolling needed
//     _sp1 = new JScrollPane(_canvas1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//       			   JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//      _sp1.setPreferredSize(new Dimension(_initialWidth,_initialHeight));
//        //IMPORTANT!  SIMPLE_SCROLL_MODE or BACKINGSTORE_SCROLL_MODE
//        //must be used, or redrawing will be insufficient when stuff comes
//        //into viewport from outside viewport.  Backingstore uses extra memory,
//        //so if an outOfMemoryError is thrown when trying to zoom in too far,
//        //it won't be able to zoom back out.  Simple is slower, but uses less mem.
//          _sp1.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
// //      _panelObjects.put("Plan View ScrollPane", _sp1);
//         _planViewJPanel.add("Plan View ScrollPane", _sp1);

//do this instead of adding the scroll pane to the panel
    _planViewJPanel.add(_canvas1, BorderLayout.CENTER);


    //this won't work unless all whitespace removed from canvas
//     Box northBox = new Box(BoxLayout.Y_AXIS);
//     northBox.add(Box.createVerticalGlue());
//     Box westBox = new Box(BoxLayout.X_AXIS);
//     westBox.add(Box.createVerticalGlue());
//     Box eastBox = new Box(BoxLayout.X_AXIS);
//     eastBox.add(Box.createVerticalGlue());
//     Box southBox = new Box(BoxLayout.Y_AXIS);
//     southBox.add(Box.createVerticalGlue());
//     _planViewJPanel.add(northBox, BorderLayout.NORTH);
//     _planViewJPanel.add(westBox, BorderLayout.WEST);
//     _planViewJPanel.add(eastBox, BorderLayout.EAST);
//     _planViewJPanel.add(southBox, BorderLayout.SOUTH);

    _ni = new NetworkInteractor(this, _canvas1, _app);

    //    ZoomInteractor zi = new ZoomInteractor(_canvas1);
    //_canvas1.addMouseListener(zi);
    //_canvas1.addMouseMotionListener(zi);
    _li = new LandmarkInteractor(this, _canvas1, _app);

      _canvas1.addMouseListener(_ni);
      _canvas1.addMouseMotionListener(_ni);
      _canvas1.addMouseListener(_li);
      _canvas1.addMouseMotionListener(_li);

    //is this necessary?????????????????????????????????/
      JPanel jp = new JPanel();
      jp.setOpaque(true);
      setContentPane(jp);
      
      //    _legendPanel = new JPanel();
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
    cfFile = new JMenu("File");
    cfFile.add(fOpen         = new JMenuItem("Open"));
    cfFile.add(fClose        = new JMenuItem("Close"));
    cfFile.addSeparator();
    cfFile.add(fSave         = new JMenuItem("Save"));
    cfFile.add(fSaveAs       = new JMenuItem("Save As..."));
    cfFile.add(fSaveZoomed   = new JMenuItem("Save Zoomed data"));
    cfFile.addSeparator();
    //   cfFile.add(fMerge        = new JMenuItem("Merge"));
    //cfFile.add(fExtract      = new JMenuItem("Extract"));
    //   cfFile.addSeparator();
    //   cfFile.add(fPrintPreview = new JMenuItem("Print Preview"));
    //   cfFile.add(fPrint        = new JMenuItem("Print..."));
    //   cfFile.add(fPrintSetup   = new JMenuItem("Print Setup"));
    //  cfFile.addSeparator();
    cfFile.add(fExit         = new JMenuItem("Exit"));

    cfFile.setMnemonic(KeyEvent.VK_F);
    fOpen.setAccelerator
	(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    fOpen.setMnemonic(KeyEvent.VK_O);
    fSave.setMnemonic(KeyEvent.VK_S);
    fSaveAs.setMnemonic(KeyEvent.VK_A);
    fExit.setMnemonic(KeyEvent.VK_X);
    fExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

    //    cfFile.add(fExit         = new JMenuItem("Exit", KeyEvent.VK_X));
    if(_addFileMenu) menubar.add(cfFile);
    
    // create and register action listener objects for the File menu items
    FileMenu fileMenu = new FileMenu(_app);
    ActionListener fOpenListener         = fileMenu.new FOpen(this);
    ActionListener fCloseListener        = fileMenu.new FClose();
    ActionListener fSaveListener         = fileMenu.new FSave();
    ActionListener fSaveAsListener       = fileMenu.new FSaveAs(this);
    ActionListener fSaveZoomedListener   = fileMenu.new FSaveBathZoomed(this);
    //  ActionListener fMergeListener        = fileMenu.new FMerge();
    //ActionListener fExtractListener      = fileMenu.new FExtract();
    ActionListener fPrintPreviewListener = fileMenu.new FPrintPreview();
    ActionListener fPrintListener        = fileMenu.new FPrint();
    ActionListener fPrintSetupListener   = fileMenu.new FPrintSetup();
    EventListener  fExitListener          = fileMenu.new FExit(this);
    
    addWindowListener((WindowListener)fExitListener);
    addComponentListener(fileMenu.new FResizeListener(this));

    //  fNew.addActionListener(fNewListener);
    fOpen.addActionListener(fOpenListener);
    fClose.addActionListener(fCloseListener);
    fSave.addActionListener(fSaveListener);
    fSaveAs.addActionListener(fSaveAsListener);
    fSaveZoomed.addActionListener(fSaveZoomedListener);
    //   fMerge.addActionListener(fMergeListener);
    //   fExtract.addActionListener(fExtractListener);
    //   fPrintPreview.addActionListener(fPrintPreviewListener);
    //   fPrint.addActionListener(fPrintListener);
    //   fPrintSetup.addActionListener(fPrintSetupListener);
    fExit.addActionListener((ActionListener)fExitListener);

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
    if(_addPropertiesMenu) menubar.add(cfProperties);

    /*
     * Display menu
     */
    cfDisplay = new JMenu("Display");
    cfDisplay.setMnemonic(KeyEvent.VK_D);
    cfDisplay.add(dParameters = new JMenuItem("Parameters"));
    dParameters.setMnemonic(KeyEvent.VK_A);
    cfDisplay.addSeparator();
    cfDisplay.add(dSource     = new JMenuItem("Filter By Source"));
    cfDisplay.add(dYear       = new JMenuItem("Filter By Year"));
    cfDisplay.addSeparator();

//      JMenu cb = new JMenu("Color By");
//      _colorByButtonGroup = new ButtonGroup();

//      _colorByButtonGroup.add
//  	(dColorUniformRadioButton = new JRadioButtonMenuItem("Uniform"));
//      _colorByButtonGroup.add
//  	(dColorByDepthRadioButton = new JRadioButtonMenuItem("Depth",true));
//      _colorByButtonGroup.add
//  	(dColorBySourceRadioButton = new JRadioButtonMenuItem("Source"));
//      _colorByButtonGroup.add
//  	(dColorByYearRadioButton = new JRadioButtonMenuItem("Year"));
//      cb.add(dColorUniformRadioButton);
//      cb.add(dColorByDepthRadioButton);
//      cb.add(dColorBySourceRadioButton);
//      cb.add(dColorByYearRadioButton);
//          cfDisplay.add(cb);
    

        JMenu fb = new JMenu("Fit By");
    _fitByButtonGroup = new ButtonGroup();
    
    _fitByButtonGroup.add
	(dFitByBathymetryMenuItem = new JRadioButtonMenuItem("Bathymetry",true));
    _fitByButtonGroup.add
	(dFitByNetworkMenuItem = new JRadioButtonMenuItem("Network"));
    _fitByButtonGroup.add
	(dFitByLandmarkMenuItem = new JRadioButtonMenuItem("Landmark"));
    fb.add(dFitByBathymetryMenuItem);
    fb.add(dFitByNetworkMenuItem);
    fb.add(dFitByLandmarkMenuItem);
    cfDisplay.add(fb);
    
    //  cfDisplay.add(dErased          = new MenuItem("Erased"));
    //  cfDisplay.addSeparator();
    cfDisplay.add(dDigitalLineGraph  = new JMenuItem
	("Digital Line Graph(Channel Outline)"));
    cfDisplay.addSeparator();
    cfDisplay.add(dElevBins        = new JMenuItem("Change Elevation Bins"));
    //   cfDisplay.addSeparator();
    //   cfDisplay.add(dCopyToClipboard = new JMenuItem("Copy to Clipboard"));
    if(_addDisplayMenu) menubar.add(cfDisplay);

    DisplayMenu displayMenu = new DisplayMenu(_app, _net);
    ActionListener dParametersListener  = displayMenu.new DParameters(this);
    ActionListener dDigitalLineGraphListener = displayMenu.new DDigitalLineGraph(this);
    ActionListener dSourceListener = displayMenu.new DSource(this);
    ActionListener dYearListener  = displayMenu.new DYear(this);
    ItemListener dColorUniformListener = displayMenu.new DColorUniform(this);
    ItemListener dColorByElevListener = displayMenu.new DColorByElev(this);
    ItemListener dColorBySourceListener = displayMenu.new DColorBySource(this);
    ItemListener dColorByYearListener  = displayMenu.new DColorByYear(this);
    EventListener dFitByBathymetryListener = displayMenu.new DFitByBathymetry(this);
    EventListener dFitByNetworkListener = displayMenu.new DFitByNetwork(this);
    EventListener dFitByLandmarkListener = displayMenu.new DFitByLandmark(this);
    ActionListener dElevBinsListener = displayMenu.new DElevBins(this);
    
    dParameters.addActionListener(dParametersListener);
    dDigitalLineGraph.addActionListener(dDigitalLineGraphListener);
    dSource.addActionListener(dSourceListener);
    dYear.addActionListener(dYearListener);
    dElevBins.addActionListener(dElevBinsListener);

    _filterSourceButton.addActionListener(dSourceListener);
    _filterYearButton.addActionListener(dYearListener);

    _colorUniformButton.addItemListener(dColorUniformListener);
    _colorByElevButton.addItemListener(dColorByElevListener);
    _colorBySourceButton.addItemListener(dColorBySourceListener);
    _colorByYearButton.addItemListener(dColorByYearListener);

//      dColorUniformRadioButton.addItemListener((ItemListener)dColorUniformListener);
//      dColorByDepthRadioButton.addItemListener((ItemListener)dColorByDepthListener);
//      dColorBySourceRadioButton.addItemListener((ItemListener)dColorBySourceListener);
//      dColorByYearRadioButton.addItemListener((ItemListener)dColorByYearListener);

    dFitByBathymetryMenuItem.addItemListener((ItemListener)dFitByBathymetryListener);
    dFitByNetworkMenuItem.addItemListener((ItemListener)dFitByNetworkListener);
    dFitByLandmarkMenuItem.addItemListener((ItemListener)dFitByLandmarkListener);
    /*
     * Network menu
     */
    cfNetwork = new JMenu("Network");
    cfNetwork.add(nOpen      = new JMenuItem("Open"));
    cfNetwork.add(nSave      = new JMenuItem("Save"));
    cfNetwork.add(nSaveAs    = new JMenuItem("Save As"));

    cfNetwork.setMnemonic(KeyEvent.VK_N);
    nOpen.setAccelerator
	(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    nOpen.setMnemonic(KeyEvent.VK_R);
    nSave.setAccelerator
	(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    nSave.setMnemonic(KeyEvent.VK_S);
    nSaveAs.setAccelerator
	(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    nSaveAs.setMnemonic(KeyEvent.VK_A);

    cfNetwork.add(nClearNetwork = new JMenuItem("Clear Network"));
    cfNetwork.add(nExportToSEFormat = new JMenuItem("Export to Station/Elevation format"));
    cfNetwork.add(nExportTo3DFormat = new JMenuItem("Export to 3D format"));
    cfNetwork.add(nExportOptions = new JMenu("Network export options")); 
    cfNetwork.addSeparator();
    //   cfNetwork.add(nList      = new JMenuItem("List"));
    //   cfNetwork.add(nSummary   = new JMenuItem("Summary"));
    //   cfNetwork.addSeparator();
    cfNetwork.add(nCalculate = new JMenuItem("Calculate"));
    nCalculate.setMnemonic(KeyEvent.VK_C);
    cfNetwork.add(nAWDSummary = new JMenuItem("AWD Summary"));
    cfNetwork.add(nXSCheck = new JMenuItem("Check cross-sections for errors"));
    if(_addNetworkMenu) menubar.add(cfNetwork);

    nExportOptions.add(noChannelLengthsOnly = new 
	JCheckBoxMenuItem("for Station/Elevation format, only export channel lengths"));
    CsdpFunctions.setChannelLengthsOnly(false);

    NetworkMenu networkMenu = new NetworkMenu(_app);
    ActionListener nOpenListener          = networkMenu.new NOpen(this);
    _nSaveListener          = networkMenu.new NSave(this);
    _nSaveAsListener        = networkMenu.new NSaveAs(this);
    ActionListener nClearNetworkListener = networkMenu.new NClearNetwork(this);
    ActionListener nExportToSEFormatListener = networkMenu.new NExportToSEFormat(this);
    ActionListener nExportTo3DFormatListener = networkMenu.new NExportTo3DFormat(this);
    EventListener noChannelLengthsOnlyListener = 
	networkMenu.new NChannelLengthsOnly();
    ////ActionListener nListListener          = networkMenu.new NList();
    ////ActionListener nSummaryListener       = networkMenu.new NSummary();
    ActionListener nCalculateListener     = networkMenu.new NCalculate(this);
    ActionListener nAWDSummaryListener    = networkMenu.new NAWDSummary(this);
    ActionListener nXSCheckListener       = networkMenu.new NXSCheck(this);
    
    nOpen.addActionListener(nOpenListener);
    nSave.addActionListener(_nSaveListener);
    nSaveAs.addActionListener(_nSaveAsListener);
    nClearNetwork.addActionListener(nClearNetworkListener);
    nExportToSEFormat.addActionListener(nExportToSEFormatListener);
    nExportTo3DFormat.addActionListener(nExportTo3DFormatListener);
    noChannelLengthsOnly.addItemListener((ItemListener)noChannelLengthsOnlyListener);
    ////nList.addActionListener(nListListener);
    ////nSummary.addActionListener(nSummaryListener);
    nCalculate.addActionListener(nCalculateListener);
    nAWDSummary.addActionListener(nAWDSummaryListener);
    nXSCheck.addActionListener(nXSCheckListener);
    _networkOpenButton.addActionListener(nOpenListener);
    _networkSaveButton.addActionListener(_nSaveListener);
    _networkCalculateButton.addActionListener(nCalculateListener);    

    /*
     * Landmark Menu
     */ 
    cfLandmark = new JMenu("Landmark");
    cfLandmark.add(oLandmark    = new JMenuItem("Open Landmark File"));
    cfLandmark.add(cLandmarks   = new JMenuItem("Clear Landmarks"));
    cfLandmark.add(lSave        = new JMenuItem("Save"));
    cfLandmark.add(lSaveAs      = new JMenuItem("Save As"));
    cfLandmark.add(lHelp        = new JMenuItem("Landmark Editing Help"));

    if(_addLandmarkMenu) menubar.add(cfLandmark);

    LandmarkMenu landmarkMenu = new LandmarkMenu(_app, this);
    _oLandmarkListener   = landmarkMenu.new LOpen(this);
    ActionListener LClearLandmarksListener = landmarkMenu.new LClear();
    ActionListener LSaveLandmarksListener  = landmarkMenu.new LSave(this);
    ActionListener LSaveLandmarksAsListener  = landmarkMenu.new LSaveAs(this);
    ActionListener LAddListener  = landmarkMenu.new LAdd();
    // these may not need listeners?
    ActionListener LMoveListener   = landmarkMenu.new LMove();
    ActionListener LEditListener   = landmarkMenu.new LEdit();
    ActionListener LDeleteListener = landmarkMenu.new LDelete();
    ActionListener LHelpListener   = landmarkMenu.new LHelp();
    
    oLandmark.addActionListener(_oLandmarkListener);
    cLandmarks.addActionListener(LClearLandmarksListener);
    lSave.addActionListener(LSaveLandmarksListener);
    lSaveAs.addActionListener(LSaveLandmarksAsListener);
    lHelp.addActionListener(LHelpListener);
//      _landmarkOpenButton.addActionListener(LAddListener);
//      _landmarkAddButton.addActionListener(LAddListener);
//      _landmarkEditButton.addActionListener(LEditListener);
//      _landmarkMoveButton.addActionListener(LMoveListener);
//      _landmarkDeleteButton.addActionListener(LDeleteListener);

    /*
     * Landmark Popup menu (for right clicking on landmark)
     */
    cfLandmarkPopup = new JPopupMenu("Landmark Menu");

    cfLandmarkPopup.add(lAddPopup    = new JRadioButtonMenuItem("Add landmark"));
    cfLandmarkPopup.add(lMovePopup   = new JRadioButtonMenuItem("Move landmark"));
    cfLandmarkPopup.add(lEditPopup   = new JRadioButtonMenuItem("Edit landmark"));
    cfLandmarkPopup.add(lDeletePopup = new JRadioButtonMenuItem("Delete landmark"));
    cfLandmarkPopup.add(lHelpPopup   = new JRadioButtonMenuItem("Landmark Editing Help"));
    ActionListener LAddPopupListener     = landmarkMenu.new LAddPopup(this);
    ActionListener LMovePopupListener    = landmarkMenu.new LMovePopup();
    ActionListener LEditPopupListener    = landmarkMenu.new LEditPopup();
    ActionListener LDeletePopupListener  = landmarkMenu.new LDeletePopup();
    
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

    //for some reason, these have to be added last
    _centerlineLandmarkEditButtonGroup.add(lAddPopup);
    _centerlineLandmarkEditButtonGroup.add(lEditPopup);
    _centerlineLandmarkEditButtonGroup.add(lMovePopup);
    _centerlineLandmarkEditButtonGroup.add(lDeletePopup);
    _centerlineLandmarkEditButtonGroup.add(lHelpPopup);
    
    /*
     * Centerline menu
     */
    cfCenterline = new JMenu("Centerline");
    cfCenterline.add(cCreate    = new JMenuItem("Create"));
    cfCenterline.add(cDSMCreate = new JMenuItem("Create DSM Chan"));
    cfCenterline.add(cCursor    = new JMenuItem("Done Editing"));
    cfCenterline.add(cRemove    = new JMenuItem("Remove Centerline"));

    ////    cfCenterline.add(cRename    = new JMenuItem("Rename"));
//      cfCenterline.addSeparator();
//      cfCenterline.add(_cMovePointMenuItem   = new JCheckBoxMenuItem("Move Point"));
//      cfCenterline.add(_cInsertPointMenuItem   = new JCheckBoxMenuItem("Insert Point"));
//      cfCenterline.add(_cAddPointMenuItem    = new JCheckBoxMenuItem("Add Point"));
//      cfCenterline.add(_cDeletePointMenuItem = new JCheckBoxMenuItem("Delete Point"));
//      cfCenterline.add(_cAddXsectMenuItem = new JCheckBoxMenuItem("Add Xsect"));
//      cfCenterline.add(_cMoveXsectMenuItem   = new JCheckBoxMenuItem("Move Xsect"));  
//      cfCenterline.add(_cRemoveXsectMenuItem = new JCheckBoxMenuItem("Remove Xsect"));

    //cfCenterline.add(cRestore   = new JMenuItem("Restore"));
    //cfCenterline.add(cKeep      = new JMenuItem("Keep"));
    //  cfCenterline.addSeparator();
    //cfCenterline.add(cSplit     = new JMenuItem("Split"));
    //cfCenterline.add(cJoin      = new JMenuItem("Join"));
    //cfCenterline.addSeparator();
    //cfCenterline.add(cView      = new JMenuItem("View"));
    //cfCenterline.add(cInfo      = new JMenuItem("Info"));
    //cfCenterline.add(cList      = new JMenuItem("List"));
    //cfCenterline.add(cSummary   = new JMenuItem("Summary"));

    cfCenterline.setMnemonic(KeyEvent.VK_C);

    if(_addCenterlineMenu) menubar.add(cfCenterline);
    
    _centerlineMenu = new CenterlineMenu(this);
    ActionListener cCursorListener      = _centerlineMenu.new CCursor(this);
    ActionListener cCreateListener      = _centerlineMenu.new CCreate(_app, this);
    ActionListener cDSMCreateListener   = _centerlineMenu.new CDSMCreate
      (_DSMChannels, _app, this);
    ActionListener cRemoveListener      = _centerlineMenu.new CRemove(this);
    ////    ActionListener cRenameListener      = _centerlineMenu.new CRename();
    // ActionListener cRestoreListener     = _centerlineMenu.new CRestore();
    //  ActionListener cKeepListener        = _centerlineMenu.new CKeep();
    ActionListener cMovePointListener = _centerlineMenu.new CMovePoint();
    ActionListener cInsertPointListener = _centerlineMenu.new CInsertPoint();
    ActionListener cAddPointListener = _centerlineMenu.new CAddPoint();
    ActionListener cDeletePointListener = _centerlineMenu.new CDeletePoint();
    ActionListener cAddXsectListener = _centerlineMenu.new CAddXsect();
    ActionListener cRemoveXsectListener = _centerlineMenu.new CRemoveXsect();
    ActionListener cMoveXsectListener = _centerlineMenu.new CMoveXsect();

    cCursor.addActionListener(cCursorListener);
    cCreate.addActionListener(cCreateListener);
    cDSMCreate.addActionListener(cDSMCreateListener);
    cRemove.addActionListener(cRemoveListener);
    ////  cRename.addActionListener(cRenameListener);
    //  cRestore.addActionListener(cRestoreListener);
    //cKeep.addActionListener(cKeepListener);

    _cursorButton.addActionListener(cCursorListener);
    _moveButton.addActionListener(cMovePointListener);
    _insertButton.addActionListener(cInsertPointListener);
    _deleteButton.addActionListener(cDeletePointListener);
    _addXsectButton.addActionListener(cAddXsectListener);
    _removeXsectButton.addActionListener(cRemoveXsectListener);
    _moveXsectButton.addActionListener(cMoveXsectListener);
    //_restoreButton.addActionListener(cRestoreListener);
    //_keepButton.addActionListener(cRestoreListener);
    
    /*
     * Xsect Menu
     */
    cfXsect = new JMenu("Xsect");
    //  cfXsect.add(xAutoGen = new JMenuItem("Auto Gen"));
    //cfXsect.addSeparator();
    //  cfXsect.add(xCreate  = new JMenuItem("Create"));
    //  cfXsect.add(xPosition = new JMenuItem("Position"));
    //cfXsect.addSeparator();
    cfXsect.add(xView    = new JMenuItem("View"));
    cfXsect.add(xAdjustLength = new JMenuItem("AdjustLength"));
    //   cfXsect.add(xInfo    = new JMenuItem("Info"));
    //   cfXsect.add(xSummary = new JMenuItem("Summary"));
    cfXsect.add(xExtractData = new JMenuItem("ExtractData"));
    cfXsect.setMnemonic(KeyEvent.VK_S);
    xView.setAccelerator
	(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    xView.setMnemonic(KeyEvent.VK_V);

    if(_addXsectMenu) menubar.add(cfXsect);
    
    // create and register action listener objects for the Xsect menu items
    _xsectMenu = new XsectMenu(_app, this, _ni);
    //   ActionListener xCreateListener   = _xsectMenu.new XCreate();
    //   ActionListener xRemoveListener   = _xsectMenu.new XRemove();
    //   ActionListener xMoveListener     = _xsectMenu.new XMove();
    //   //  ActionListener xPositionListener = _xsectMenu.new XPosition();
    ActionListener xViewListener     = _xsectMenu.new XView();
    ActionListener xAdjustLengthListener = _xsectMenu.new XAdjustLength();
    ActionListener xExtractDataListener = _xsectMenu.new XExtractData(this);
    //   ActionListener xInfoListener     = _xsectMenu.new XInfo();
    //   ActionListener xSummaryListener  = _xsectMenu.new XSummary();
    
    //   xCreate.addActionListener(xCreateListener);
    //   xRemove.addActionListener(xRemoveListener);
    //   xMove.addActionListener(xMoveListener);
    //   xPosition.addActionListener(xPositionListener);
    xView.addActionListener(xViewListener);

    xAdjustLength.addActionListener(xAdjustLengthListener);
    //   xInfo.addActionListener(xInfoListener);
    //   xSummary.addActionListener(xSummaryListener);
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

    cfZoom.setMnemonic(KeyEvent.VK_Z);
    if(_addZoomMenu) menubar.add(cfZoom);
    
    // create and register action listener objects for the Zoom menu items
    ZoomMenu zoomMenu = new ZoomMenu(this);
    ActionListener zPanListener    = zoomMenu.new ZPan();
    ActionListener zFitListener    = zoomMenu.new ZFit();
    ActionListener zBoxListener = zoomMenu.new ZBox();
    ActionListener zUndoListener = zoomMenu.new ZUndo();
    ActionListener zPanMIListener = zoomMenu.new ZPanMI();
    ActionListener zBoxMIListener = zoomMenu.new ZBoxMI();

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
    //    cfTools.add(tCompareNetwork = new JMenuItem("Compare two network files"));
    cfTools.add(tCalcRect = new JMenuItem
		("Calculate Equivalent Rectangular cross-sections"));
    cfTools.add(tOpenWaterCalc = new JMenuItem("Open Water Area Calculations"));
    if(_addToolsMenu) menubar.add(cfTools);

    tCalcRect.setEnabled(false);

    cfTools.setMnemonic(KeyEvent.VK_T);

    //removed temporarily(?) options now displayed in dialog.
//      tOpenWaterOptionsMenu.add
//  	(oEchoTimeSeriesInput = new JCheckBoxMenuItem("Echo Time Series Input"));
//      tOpenWaterOptionsMenu.add
//  	(oEchoXsectInput = new JCheckBoxMenuItem("Echo Xsect Input"));
//      tOpenWaterOptionsMenu.add
//  	(oEchoToeDrainInput = new JCheckBoxMenuItem("Echo Toe Drain Input"));
//      tOpenWaterOptionsMenu.add
//  	(oPrintXsectResults = new JCheckBoxMenuItem("Print Xsect Results (long)"));
//      tOpenWaterOptionsMenu.add
//  	(oUseFremontWeir = new JCheckBoxMenuItem("Use Fremont Weir"));
//      tOpenWaterOptionsMenu.add
//  	(oUseToeDrainRestriction = new JCheckBoxMenuItem("Use toe drain restriction"));
//      cfTools.add(tOpenWaterOptionsMenu);

//      oEchoTimeSeriesInput.setSelected(getEchoTimeSeriesInput());
//      oEchoXsectInput.setSelected(getEchoXsectInput());
//      oEchoToeDrainInput.setSelected(getEchoToeDrainInput());
//      oPrintXsectResults.setSelected(getPrintXsectResults());
//      oUseFremontWeir.setSelected(CsdpFunctions.getUseFremontWeir());
//      oUseToeDrainRestriction.setSelected(CsdpFunctions.getUseToeDrainRestriction());
//      tOpenWaterCalc.setEnabled(_addOWACalcOption);
//      tOpenWaterOptionsMenu.setEnabled(_addOWACalcOption);

    tCalcRect.setEnabled(_addRectXSOption);

    _toolsMenu = new ToolsMenu(_app, this);
    //ActionListener tCompareNetworkListener = _toolsMenu.new TCompareNetwork(this);
    ActionListener tCalcRectListener      = _toolsMenu.new TCalcRect(this);
    ActionListener tOpenWaterCalcListener = _toolsMenu.new TOpenWaterCalc(this);
    //removed temporarily(?) options now appear in dialog
//      EventListener oEchoTimeSeriesInputListener = _toolsMenu.new TEchoTimeSeriesInput();
//      EventListener oEchoXsectInputListener = _toolsMenu.new TEchoXsectInput();
//      EventListener oEchoToeDrainInputListener = _toolsMenu.new TEchoToeDrainInput();
//      EventListener oPrintXsectResultsListener = _toolsMenu.new TPrintXsectResults();
//      EventListener oUseFremontWeirListener = _toolsMenu.new TUseFremontWeir();
//      EventListener oUseToeDrainRestrictionListener = _toolsMenu.new TUseToeDrainRestriction();
//      oEchoTimeSeriesInput.addItemListener((ItemListener)oEchoTimeSeriesInputListener);
//      oEchoXsectInput.addItemListener((ItemListener)oEchoXsectInputListener);
//      oEchoToeDrainInput.addItemListener((ItemListener)oEchoToeDrainInputListener);
//      oPrintXsectResults.addItemListener((ItemListener)oPrintXsectResultsListener);
//      oUseFremontWeir.addItemListener((ItemListener)oUseFremontWeirListener);
//      oUseToeDrainRestriction.addItemListener((ItemListener)oUseToeDrainRestrictionListener);
    //tCompareNetwork.addActionListener(tCompareNetworkListener);
    tCalcRect.addActionListener(tCalcRectListener);
    tOpenWaterCalc.addActionListener(tOpenWaterCalcListener);

    //tCompareNetwork.setEnabled(_addCompareNetworkOption);
    
    /*
     * Window menu
     */
    //   cfWindow = new JMenu("Window");
    //    cfWindow.setMnemonic(KeyEvent.VK_W);
    //   cfWindow.add(wCascade      = new JMenuItem("Cascade"));
    //   cfWindow.add(wTile         = new JMenuItem("Tile"));
    //   cfWindow.add(wArrangeIcons = new JMenuItem("Arrange Icons"));
    //   cfWindow.add(wCloseAll     = new JMenuItem("Close All"));
    //   cfWindow.addSeparator();
    //   cfWindow.add(wRepaint      = new JMenuItem("Repaint"));
    //   // also show open windows
    //   menubar.add(cfWindow);
    
    cfHelp = new JMenu("Help");
    cfHelp.setMnemonic(KeyEvent.VK_H);
    //cfHelp.add(hContents  = new JMenuItem("Contents"));
    //cfHelp.add(hUsingHelp = new JMenuItem("Using Help"));
    //cfHelp.addSeparator();
    cfHelp.add(hAbout     = new JMenuItem("About CSDP"));
    if(_addHelpMenu){
	menubar.add(Box.createHorizontalGlue());
	menubar.add(cfHelp);
	menubar.add(Box.createHorizontalGlue());
    }
    HelpMenu helpMenu = new HelpMenu();
    ////    menubar.setHelpMenu(cfHelp);
    ActionListener hAboutListener = helpMenu.new HAbout(this);
    hAbout.addActionListener(hAboutListener);

    disableButtonsAndMenuItems();

  } //createGui
  
  /**
   * Creates new instance of canvas for plotting bathymetry data.
   */
  ///public void addPlanViewCanvas(String name){
  ///PlanViewCanvas can = new PlanViewCanvas(name);
  ///_pvCanvases.put(name, can);
  ///}

  /**
   * returns canvas with specified name
   */
  ///public PlanViewCanvas getPlanViewCanvas(String name){
  ///return (PlanViewCanvas)_pvCanvases.get(name);
  ///}

  /**
   * removes canvas from hashtable(close file)
   */
  ///public void removePlanViewCanvas(String name){
  ///Object value = _pvCanvases.remove(name);
  ///}

  ///  Hashtable _pvCanvases = new Hashtable();
  ///Hashtable _filenames  = new Hashtable();


  /**
   * sets Network object
   */
  public void setNetwork(Network net){
    _net = net;
    _ni.setNetwork(net);
    _xsectMenu.setNetwork(net);
    _canvas1.setNetwork(net);
  }//setNetwork
  
  /**
   * returns handle to the network object.
   */
  public Network getNetwork(){
    return _net;
  }//getNetwork

  /**
   * save network file when quitting program (if user clicks "yes")
   */
  public void saveNetwork(){
    if(CsdpFunctions._networkFilename == null){
	_nSaveAsListener.actionPerformed(_nullActionEvent);
    }
    else{
	_nSaveListener.actionPerformed(_nullActionEvent);
    }
  }//saveNetwork

  /**
   * returns landmark object
   */
  public Landmark getLandmark(){
    Landmark landmark = null;
    //    if(_landmark != null) landmark = _landmark;
    //else{
      _oLandmarkListener.actionPerformed(_nullActionEvent);
      landmark = _landmark;
      //}
    return landmark;
  }//getLandmark

  /**
   * sets Landmark object
   */
public void setLandmark(Landmark landmark){
  _landmark = landmark;
  _li.setLandmark(landmark);
  _canvas1.setLandmark(landmark);
  _centerlineMenu.setLandmark(landmark);
}//setLandmark

  /**
   * sets DSMChannels object
   */
  public void setDSMChannels(DSMChannels DSMChannels){
    _DSMChannels = DSMChannels;
    //  _canvas1.setLandmark(landmark);
  }//setDSMChannels

  /**
   * returns instance of bathymetry plot object
   */
  public BathymetryPlot getPlotObject(){
    return _plot;
  }

  /**
   * sets BathymetryPlot object
   */
  public void setPlotObject(BathymetryPlot plot){
    _plot = plot;
    _ni.setPlotter(plot);
    _li.setPlotter(plot);
  }//setPlotObject

//    /**
//     * add a canvas to the hashtable that stores all the panels contained in the frame
//     */
//    public void addXsectWindow(String name, XsectGraph xg){
//      _panelObjects.put(name, xg);
//      _planViewJPanel.add(name, (Canvas)_panelObjects.get(name));
//      _cl.next(_p);
//    }//addXsectWindow

    /**
     * called when user wants to stop editing
     */
    public void setStopEditingMode(){
	enableCenterlineEditButtons();
	enableLandmarkEditButtons();
	setDefaultModesStates();
	// 	setZoomBoxMode(false);
	// 	setZoomPanMode(false);
	
	if(getCenterlineSelected()){
	    _moveButton.setEnabled(true);
	    _insertButton.setEnabled(true);
	    _addButton.setEnabled(true);
	    _deleteButton.setEnabled(true);
	    _addXsectButton.setEnabled(true);
	    _removeXsectButton.setEnabled(true);
	    if(getXsectSelected()){
		_moveXsectButton.setEnabled(true);
		_viewXsectButton.setEnabled(true);
	    }else{
		_moveXsectButton.setEnabled(false);
		_viewXsectButton.setEnabled(false);
	    }
	}else{
	    _moveButton.setEnabled(false);
	    _insertButton.setEnabled(false);
	    _addButton.setEnabled(false);
	    _deleteButton.setEnabled(false);
	    _addXsectButton.setEnabled(false);
	    _removeXsectButton.setEnabled(false);
	    _moveXsectButton.setEnabled(false);
	    _viewXsectButton.setEnabled(false);
//  	    _landmarkAddButton.setEnabled(false);
// 	    _landmarkEditButton.setEnabled(false);
// 	    _landmarkMoveButton.setEnabled(false);
// 	    _landmarkDeleteButton.setEnabled(false);
	    lAddPopup.setEnabled(false);
	    lEditPopup.setEnabled(false);
	    lMovePopup.setEnabled(false);
	    lDeletePopup.setEnabled(false);
	}
    }//setStopEditingMode
    
    /**
     * called when user wants to add centerline points to 
     * the end of the set(after last pt)--only called when adding a centerline
     */
    public void setAddPointMode(){
	  _addButton.setSelected(true);
    }//setAddPointMode
    
    /**
     * toggles zoom mode when button is not clicked by user.  Allows another event to toggle mode.
     */
    public void pressZoomBoxButton(){
	if(DEBUG)System.out.println("zoom box button pressed indirectly");
	_zoomBoxButton.doClick();
    }

    /**
     * toggles zoom pan mode when button is not clicked by user.  Allows another event to toggle mode.
     */
    public void pressZoomPanButton(){_zoomPanButton.doClick();}
    public void pressCursorButton(){_cursorButton.doClick();}

  /**
   * turns off all edit modes
   */
  public void turnOffEditModes(){
    setDefaultModesStates();
    enableCenterlineEditButtons();
    setCursor(CsdpFunctions._defaultCursor);
    pressCursorButton();
  }//turnOffEditModes

  /**
   * disable buttons and menu items which should not be enabled until bathymetry loaded
   */
  protected void disableButtonsAndMenuItems(){
      _cursorButton.setEnabled(false);
      _propOpenButton.setEnabled(false);
      _networkOpenButton.setEnabled(false);
      _networkSaveButton.setEnabled(false);
      _moveButton.setEnabled(false);
      _insertButton.setEnabled(false);
      _addButton.setEnabled(false);
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

//      _cMovePointMenuItem.setEnabled(false);
//      _cAddPointMenuItem.setEnabled(false); 
//      _cInsertPointMenuItem.setEnabled(false);
//      _cDeletePointMenuItem.setEnabled(false);
//      _cAddXsectMenuItem.setEnabled(false); 
//      _cRemoveXsectMenuItem.setEnabled(false);
//      _cMoveXsectMenuItem.setEnabled(false);

    fClose.setEnabled(false);
    fSave.setEnabled(false);
    fSaveAs.setEnabled(false);
    //fPrintPreview.setEnabled(false);
    //fPrint.setEnabled(false);
    //fPrintSetup.setEnabled(false);

    pLoad.setEnabled(false);
    pSaveAs.setEnabled(false);
    pSave.setEnabled(false);

    dParameters.setEnabled(false);
    dSource.setEnabled(false);
    dYear.setEnabled(false);
    //dColorBy.setEnabled(false);
    //dErased.setEnabled(false);
    dDigitalLineGraph.setEnabled(false);
    oLandmark.setEnabled(false);
    cLandmarks.setEnabled(false);
    lSave.setEnabled(false);
    lSaveAs.setEnabled(false);

    lAddPopup.setEnabled(false);
    lEditPopup.setEnabled(false);
    lMovePopup.setEnabled(false);
    lDeletePopup.setEnabled(false);
    //dColorUniformRadioButton.setEnabled(false);
    //dColorByDepthRadioButton.setEnabled(false); 
    //dColorBySourceRadioButton, dColorByYearRadioButton;
    dFitByNetworkMenuItem.setEnabled(false);
    dFitByLandmarkMenuItem.setEnabled(false);

    nOpen.setEnabled(false);
    nSave.setEnabled(false);
    nSaveAs.setEnabled(false);
    nExportToSEFormat.setEnabled(false);
    nExportTo3DFormat.setEnabled(false);
    //nList.setEnabled(false);nSummary.setEnabled(false);
    nClearNetwork.setEnabled(false);
    nCalculate.setEnabled(false);
    _networkCalculateButton.setEnabled(false);
    cCreate.setEnabled(false);
    cDSMCreate.setEnabled(false);
    ////    cRename.setEnabled(false); 

    tCalcRect.setEnabled(false);

    //    xMove.setEnabled(false);
    //xCreate.setEnabled(false);
    xView.setEnabled(false);
    xAdjustLength.setEnabled(false);
    xExtractData.setEnabled(false);
    //xInfo.setEnabled(false);xSummary.setEnabled(false);
    zPan.setEnabled(false);
    zFit.setEnabled(false);
    zBox.setEnabled(false);
    zUndo.setEnabled(false);
    _zoomUndoButton.setEnabled(false);
  }//disableButtonsAndMenuItems

  /**
   * enable buttons and menu items which should be enabled when bathymetry loaded
   */
  public void enableAfterBathymetry(){
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
  
    ////    fClose.setEnabled(true);
    ////fSave.setEnabled(true);//not implemented
    fSaveAs.setEnabled(true);
    //fPrintPreview.setEnabled(true);
    //fPrint.setEnabled(true);
    //fPrintSetup.setEnabled(true);
    dParameters.setEnabled(true);
    dSource.setEnabled(true);
    dYear.setEnabled(true);
    //dColorBy.setEnabled(true);
    //dErased.setEnabled(true);
    dDigitalLineGraph.setEnabled(true);
    oLandmark.setEnabled(true);
    lSave.setEnabled(true);
    lSaveAs.setEnabled(true);

    lAddPopup.setEnabled(true);
    lEditPopup.setEnabled(true);
    lMovePopup.setEnabled(true);
    lDeletePopup.setEnabled(true);
    //dColorUniformRadioButton.setEnabled(true);
    //dColorByDepthRadioButton.setEnabled(true); 
    //dColorBySourceRadioButton, dColorByYearRadioButton;
    nOpen.setEnabled(true);
    cCreate.setEnabled(true);
    cDSMCreate.setEnabled(true);
    //    xMove.setEnabled(true);
    //xInfo.setEnabled(true);xSummary.setEnabled(true);
    zPan.setEnabled(_addZoomWindowOption);
    zFit.setEnabled(_addZoomWindowOption);
    zBox.setEnabled(_addZoomWindowOption);
    _cursorButton.setEnabled(true);
    _networkOpenButton.setEnabled(true);
  }//enableAfterBathymetry

    /**
     * Used to enable/disable undo Zoom menuitem and/or button
     */
    public void setUndoZoom(boolean b){
	zUndo.setEnabled(b);
	_zoomUndoButton.setEnabled(b);
    }

  /**
   * enable buttons and menu items which should be enabled after properties file
   * saved or loaded
   */
  public void enableAfterProperties(){
    pSave.setEnabled(true);
  }

  /**
   * enable buttons and menu items which should be enabled after network loaded
   */
  protected void enableAfterNetwork(){
    nSave.setEnabled(true);
    nSaveAs.setEnabled(true);
    nClearNetwork.setEnabled(true);
    _networkSaveButton.setEnabled(true);
    //nList.setEnabled(true);nSummary.setEnabled(true);
    nExportToSEFormat.setEnabled(true);
    nExportTo3DFormat.setEnabled(true);
    nCalculate.setEnabled(true);
    _networkCalculateButton.setEnabled(true);
    dFitByNetworkMenuItem.setEnabled(true);
    tCalcRect.setEnabled(true);
  }//enableAfterNetwork

  /**
   * enable buttons and menu items which should be enabled after landmark loaded/created
   */
  protected void enableAfterLandmark(){
      lSave.setEnabled(true);
      lSaveAs.setEnabled(true);
      cLandmarks.setEnabled(true);
//       _landmarkSaveButton.setEnabled(true);
//       _landmarkAddButton.setEnabled(true);
//       _landmarkMoveButton.setEnabled(true);
//       _landmarkDeleteButton.setEnabled(true);
      lAddPopup.setEnabled(true);
      lEditPopup.setEnabled(true);
      lMovePopup.setEnabled(true);
      lDeletePopup.setEnabled(true);
      cLandmarks.setEnabled(true);

      dFitByNetworkMenuItem.setEnabled(true);
      tCalcRect.setEnabled(true);
  }//enableAfterNetwork
    
    /**
     * disable buttons and menu items which should be disabled when network is cleared
     */
    protected void disableWhenNetworkCleared(){
	nSave.setEnabled(false);
	nSaveAs.setEnabled(false);
	nClearNetwork.setEnabled(false);
	_networkSaveButton.setEnabled(false);
	//nList.setEnabled(false);nSummary.setEnabled(false);
	nExportToSEFormat.setEnabled(false);
	nExportTo3DFormat.setEnabled(false);
	nCalculate.setEnabled(false);
	_networkCalculateButton.setEnabled(false);
	dFitByNetworkMenuItem.setEnabled(false);
	tCalcRect.setEnabled(false);
    }

    /**
     * Enable buttons and menu items which should be enabled when a network is loaded
     */
  public void enableWhenNetworkExists(){
    nSaveAs.setEnabled(true);
    nClearNetwork.setEnabled(true);
    nExportToSEFormat.setEnabled(true);
    nExportTo3DFormat.setEnabled(true);
    nCalculate.setEnabled(true);
    _networkCalculateButton.setEnabled(true);
    dFitByNetworkMenuItem.setEnabled(true);
    tCalcRect.setEnabled(true);
  }

    /**
     * disable buttons and menu items which should be disabled when landmard is cleared
     */
    public void disableWhenLandmarkCleared(){
	dFitByLandmarkMenuItem.setEnabled(false);
	cLandmarks.setEnabled(false);
    }

  /**
   * enable buttons and menu items which should be enabled after centerline selected
   */
  protected void enableAfterCenterlineSelected(){
      if(DEBUG)System.out.println("centerline selected!");

    _moveButton.setEnabled(true);
    _insertButton.setEnabled(true);
    _addButton.setEnabled(true);
    _deleteButton.setEnabled(true);
    _addXsectButton.setEnabled(true);
    if(getXsectSelected()){
	_removeXsectButton.setEnabled(true);
	_moveXsectButton.setEnabled(true);
	_viewXsectButton.setEnabled(true);
    }else{
	_removeXsectButton.setEnabled(false);
	_moveXsectButton.setEnabled(false);
	_viewXsectButton.setEnabled(false);
    }
//      _cMovePointMenuItem.setEnabled(true);
//      _cAddPointMenuItem.setEnabled(true); 
//      _cInsertPointMenuItem.setEnabled(true);
//      _cDeletePointMenuItem.setEnabled(true);
//      _cAddXsectMenuItem.setEnabled(true); 
//      _cRemoveXsectMenuItem.setEnabled(true);

    setCenterlineSelected(true);

    ////    cRename.setEnabled(true); 
  }//enableAfterCenterlineSelected

  /**
   * disable buttons and menu items which should not be enabled if not centerline 
   * selected
   */
  protected void disableIfNoCenterlineSelected(){
    setDefaultModesStates();
    _moveButton.setEnabled(false);
    _insertButton.setEnabled(false);
    _addButton.setEnabled(false);
    _deleteButton.setEnabled(false);
    _addXsectButton.setEnabled(false);
    _moveXsectButton.setEnabled(false);
    _removeXsectButton.setEnabled(false);
    _viewXsectButton.setEnabled(false);

//      _cMovePointMenuItem.setEnabled(false);
//      _cAddPointMenuItem.setEnabled(false); 
//      _cInsertPointMenuItem.setEnabled(false);
//      _cDeletePointMenuItem.setEnabled(false);
//      _cAddXsectMenuItem.setEnabled(false); 
//      _cRemoveXsectMenuItem.setEnabled(false);
//      _cMoveXsectMenuItem.setEnabled(false);
    cfXsect.setEnabled(false);

    setCenterlineSelected(false);
    ////cRename.setEnabled(false); 
  }//disableIfNoCenterlineSelected

  /**
   * enable buttons and menu items which should be enabled when xsect selected
   */
  protected void enableAfterXsectSelected(){
    cfXsect.setEnabled(true);
    _moveXsectButton.setEnabled(true);
//      _cMoveXsectMenuItem.setEnabled(true);
    xView.setEnabled(true);
    _removeXsectButton.setEnabled(true);
    _viewXsectButton.setEnabled(true);
    xAdjustLength.setEnabled(true);
    xExtractData.setEnabled(true);
    setXsectSelected(true);
  }//enableAfterXsectSelected

  /**
   * disable buttons and menu items which should not be enabled if no xsect selected
   */
  protected void disableIfNoXsectSelected(){
    _moveXsectButton.setEnabled(false);
//      _cMoveXsectMenuItem.setEnabled(false);

    cfXsect.setEnabled(false);
    xView.setEnabled(false);
    _viewXsectButton.setEnabled(false);
    xAdjustLength.setEnabled(false);
    xExtractData.setEnabled(false);
    setXsectSelected(false);
  }//disableIfNoXsectSelected

   /**
    * adds a legend (panel of buttons) which shows the colors being used to
    * plot bathymetry data.  Called when colorBy option changed, and 
    * possibly in other situations.
    */
    public void updateColorLegend(){
	int numButtons = 0;
	_legendPanel.setLayout(new GridLayout(numButtons, 1));
	String legendItemName = null;
	Color buttonColor = null;
	
	_legendPanel.removeAll();
	
	if(_plot != null && getColorUniform() == false){
	    if(getColorByElev()){
		numButtons = getNumElevBins();
	    }else if(getColorBySource()){
		numButtons = _plot._bathymetryData.getNumSources();
	    }else if(getColorByYear()){
		numButtons = _plot._bathymetryData.getNumYears();
	    }
	    setDefaultColors(numButtons);


	    if(getColorByElev()){
		headerButton.setText(_depthLegendTitle);
	    }else if(getColorBySource()){ 
		headerButton.setText(_sourceLegendTitle);
	    }else if(getColorByYear()){
		headerButton.setText(_yearLegendTitle);
	    }	    

	    _legendPanel.add(headerButton);
	    
	    for(int i=0; i<=numButtons-1; i++){
		if(getColorByElev()){
		    legendItemName = _plot.getLegendItemName(i);
		}else if(getColorBySource()){
		    legendItemName = _plot._bathymetryData.getSource(i);
		}else if(getColorByYear()){
		    legendItemName = Integer.toString(_plot._bathymetryData.getYear(i));
		}
		buttonColor = _plot.getLegendItemColor(i);
		//	_legendPanel.add(new Button(legendItemName));
		JButton b = getButton(i);
		b.setBorder(_raisedBevel);
		b.setToolTipText("click to change color");
		_legendPanel.add(b);
		getButton(i).setText(legendItemName);
		_legendPanel.getComponent(i+1).setBackground(buttonColor);
	    }
	}//if plot isn't null
	_canvas1.redoNextPaint();
	_canvas1.repaint();
	//    validate();
    }//updateColorLegend

  /**
   * sets all buttons to true
   */
  private void enableCenterlineEditButtons(){
    _moveButton.setEnabled(true);
    _addButton.setEnabled(true);
    _insertButton.setEnabled(true);
    _deleteButton.setEnabled(true);
    _addXsectButton.setEnabled(true);
    _removeXsectButton.setEnabled(true);
    _moveXsectButton.setEnabled(true);
    _zoomBoxButton.setEnabled(true);
  }//enableCenterlineEditButtons

    private void enableLandmarkEditButtons(){
// 	_landmarkAddButton.setEnabled(true);
// 	_landmarkEditButton.setEnabled(true);
// 	_landmarkMoveButton.setEnabled(true);
// 	_landmarkDeleteButton.setEnabled(true);
	lAddPopup.setEnabled(true);
	lEditPopup.setEnabled(true);
	lMovePopup.setEnabled(true);
	lDeletePopup.setEnabled(true);
    }

    public void setDefaultModesStates(){
	_cursorButton.setSelected(true);
	//////////	_centerlineLandmarkEditButtonGroup.setSelected(_cursorButton,true);
    }

  /**
   * updates displayed value of centerline name/num and mouse position
   */
  public void updateInfoPanel(String centerlineName){
    _centerlineLabel.setText("Selected Centerline:  "+centerlineName);
  }//updateInfoPanel

  /**
   * updates displayed value of cross-section properties
   */
  public void updateInfoPanel(double area, double width, double wetp, double hd){
    double nf = 100.0;
    if(area >=0.0f && width >=0.0f && wetp>=0.0f && hd>=0.0f){
	String sa = Double.toString( (((double)((int)(area*nf))) /nf));
	String sw = Double.toString( (((double)((int)(width*nf)))/nf));
	String sp = Double.toString( (((double)((int)(wetp*nf)))/nf));
      String shd = Double.toString( (((double)((int)(hd*nf)))/nf));
      _areaLabel.setText("Xsect Area:  "+sa);
      _widthLabel.setText("Top Width:  "+sw);
      _wetPLabel.setText("Wetted Perimeter:  "+sp);
      _hydraulicDepthLabel.setText("Hydraulic Depth:  "+shd);
    } else {
      _areaLabel.setText("Xsect Area:  ");
      _widthLabel.setText("Top Width:  ");
      _wetPLabel.setText("Wetted Perimeter:  ");
      _hydraulicDepthLabel.setText("Hydraulic Depth:  ");
    }
  }//updateInfoPanel

    public void updateMetadataDisplay(CsdpFileMetadata m){
	_horDatumLabel.setText("Horizontal Datum: "+m.getHDatumString());
	_horDatumUnitsLabel.setText("Hor. Datum Units: "+m.getHUnitsString());
	_verDatumLabel.setText("Vertical Datum: "+m.getVDatumString());
	_verDatumUnitsLabel.setText("Ver. Datum Units: "+m.getVUnitsString());
    }

  /**
   * updates filename display
   */
  public void updateBathymetryFilename(String bfName){
    _bathymetryFileLabel.setText("Bathymetry Filename:  "+bfName);
  }//updateBathymetryFilename

  /**
   * updates filename display
   */
  public void updateNetworkFilename(String nfName){
    _networkFileLabel.setText("Network Filename:  "+nfName);
  }//updateNetworkFilename

  /**
   * updates filename display
   */
  public void updateLandmarkFilename(String lfName){
    _landmarkFileLabel.setText("Landmark Filename:  "+lfName);
  }//updateLandmarkFilename

  /**
   * updates filename display
   */
  public void updatePropertiesFilename(String pfName){
    _propertiesFileLabel.setText("Properties Filename:  "+pfName);
  }//updatePropertiesFilename

    /**
     * updates filename display
     */
    public void updateDigitalLineGraphFilename(String dlgFilename){
	_dlgFileLabel.setText("DLG Filename:  "+dlgFilename);
    }

    public void setDigitalLineGraph(DigitalLineGraph dlg){
	_dlg = dlg;
	_canvas1.setDigitalLineGraph(_dlg);
    }

  /**
   * updates displayed value of centerline name/num and mouse position
   */
  public void updateInfoPanel(int xsectNum){
    if(xsectNum >= 0) _xsectLabel.setText("Xsect:  "+xsectNum);
    else _xsectLabel.setText("Xsect:");
  }//updateInfoPanel

    /**
     * updates displayed value of centerline name/num and mouse position
     */
    public void updateInfoPanel(double mouseX, double mouseY){
	//_plot is instance of BathymetryPlot

	if(_plot != null){
	    String xLabel = "X coordinate (UTM):  ";
	    String yLabel = "Y coordinate (UTM):  ";

	    try{
		ZoomState zs = _plot.getCurrentZoomState();
		CoordConv cc = zs.getCoordConv();
		double[] bb = zs.getPlotBoundaries();
		double minX = bb[CsdpFunctions.minXIndex];
		double minY = bb[CsdpFunctions.minYIndex];

		cc.pixelsToLength((int)mouseX,(int)mouseY,minX,minY, _cursorPosition);

		double x = _cursorPosition[0];
		double y = _cursorPosition[1];

// 		if(! _canvas1._useZoomBox){
// 		    x -= _plot._centerX;
// 		    y -= _plot._centerY;
// 		}
		x = CsdpFunctions.feetToMeters(x);
		y = CsdpFunctions.feetToMeters(y);
		xLabel += x;
		yLabel += y;
		_mouseXLabel.setText(xLabel);
		_mouseYLabel.setText(yLabel);

		if(DEBUG){
		    System.out.println("updating coordinate display");
		    System.out.println("--------------------------------------");
		    System.out.println("minX, minY="+CsdpFunctions.feetToMeters(minX)+","+
				       CsdpFunctions.feetToMeters(minY));
		    System.out.println("x,y="+x+","+y);
		    System.out.println("--------------------------------------");
		}

	    }catch(java.lang.NullPointerException e){
		System.out.println("null pointer in CsdpFrame.updateInfoPanel: bathymetry probably not loaded yet.");
	    }
	}else{
	    _mouseXLabel.setText(Double.toString(mouseX));
	    _mouseYLabel.setText(Double.toString(mouseY));
	}
    }//updateInfoPanel

    /**
     * returns color from color table.  Used for coloring bathymetry
     */
  public Color getColor(int index){
    if(getNumColors() == 0){
	if(DEBUG)System.out.println("setting default colors");
      setDefaultColors();
    }
    Color c = null;
    if(index < getNumColors()) c = (Color)(_colors.elementAt(index));
    else{
      while(index >= getNumColors()){
	_colors.addElement(_colors.elementAt(getNumColors()-1));
      }//while
      c = (Color)(_colors.elementAt(index));
    }//else
    return c;
  }//getColor

    /**
     * sets color used to color bathymetry. called by AdjustRGBColor, which
     * allows user to specify color.
     */
    public void setColor(int index, Color c){
	if(!_userSetColors.containsKey(new Integer(index))){
	    System.out.println("before null: _userSetColors, index, color="+_userSetColors+","+index+","+c);
	    _userSetColors.put(new Integer(index), c);
	}
	setDefaultColors();
	//if(getNumColors() == 0) setDefaultColors();
	//     if(index < getNumColors()) _colors.setElementAt(c,index);
	//     else{
	//       _colors.addElement(c);
	//     }//else
    }//setColor

    /**
     *
     */
    public static int getNumColors(){
	return _colors.size();
    }

    private void setDefaultColors(){
	setDefaultColors(DEFAULT_NUM_COLORS);
    }

  private void setDefaultColors(int numColors){
      // to make rainbow, use HSB colors. Set S to 100, B to 100, and vary H from 1 to 280
      System.out.println("setDefaultColors: "+numColors);
      _colors.clear();
      float increment = (float)( 0.8/((float)numColors-1));
      float s=(float)1.0;
      float b=(float)1.0;

      for(int h=0; h<numColors; h++){
	  float hue=(float)h*increment;
	  int rgb = Color.HSBtoRGB(hue, s, b);
	  _colors.addElement(new Color(rgb));
	  System.out.println("adding color "+hue+","+s+","+b+","+rgb);
      }
      //go through user set colors if any, and put them into color palette
      for(Enumeration<Integer> e = _userSetColors.keys(); e.hasMoreElements();){
	  Integer indexObject = e.nextElement();
	  int index = indexObject.intValue();
	  Color c = _userSetColors.get(indexObject);
	  System.out.println("before out of bounds: colors.size, index="+_colors.size()+","+index);
	  _colors.set(index, c);
      }


//     _colors.addElement(new Color(255,  0,  0 )); // bright red
//     _colors.addElement(new Color(214,130, 50 )); // dark orange 204,102,0
//     _colors.addElement(new Color(255,204,102 )); // light orange
//     _colors.addElement(new Color(210, 30, 30 )); // dark red
//     _colors.addElement(new Color(255,255,  0 )); // dark yellow
//     _colors.addElement(new Color(  0,255,  0 )); // dark green
//     _colors.addElement(new Color(153,204,102 )); // light green
//     _colors.addElement(new Color(102,255,255 )); // medium blue
//     _colors.addElement(new Color(153,204,255 )); // light blue
//     _colors.addElement(new Color(153,153,255 ));// light purple
//     _colors.addElement(new Color(  0,102,204 )); // dark blue


//      _colors.addElement(11] = new Color(153,102,255));// medium purple
//      _colors.addElement(12] = new Color(119,119,119));// dark purple
//      _colors.addElement(13] = new Color(119,119,119));// light gray

//12 colors from Colorbrewer
//http://www.personal.psu.edu/faculty/c/a/cab38/ColorBrewerBeta2.html
//     141,211,199
// 	255,255,179
// 	190,186,218
// 	251,128,114
// 	128,177,211
// 	253,180,98
// 	179,222,105
// 	252,205,229
// 	217,217,217
// 	188,128,189
// 	204,235,197
// 	255,237,111

  }//setDefaultColors

  public double getElevationBin(int index){
    double f = 0.0;
    if(index < getNumElevBins()) f = _elevationBins[index];
    else{
	f = _elevationBins[getNumElevBins()-1];
    }
    return f;
  }

//   public void setDepth(int index, double f){
//     if(index < getNumColors()) depthTable[index] = f;
//     else depthTable[getNumColors()-1] = f;
//   }

//     /**
//      * returns number of depths in depth table.  used for coloring by depth
//      */
//     public int getNumDepths(){
// 	return _numDepths;
//     }

  public boolean getFitByBathymetryOption(){return dFitByBathymetryMenuItem.isSelected();}
  public boolean getFitByNetworkOption(){return dFitByNetworkMenuItem.isSelected();}
  public boolean getFitByLandmarkOption(){return dFitByLandmarkMenuItem.isSelected();}

  /**
   * button that displays header information for plan view legend
   */
  public static JButton headerButton = new JButton("header");

  /**
   * returns button from array of buttons
   */
  public JButton getButton(int index){
    JButton b = null;
    if(index < NUM_BUTTONS) b = (JButton)(_buttons.elementAt(index));
    else{
      while(index >= NUM_BUTTONS){
	_buttons.addElement(new JButton());
	_legendButtonListener.addElement(new AdjustRGBColor(this,index));
	((JButton)_buttons.elementAt(index)).addActionListener
	  ((ActionListener)(_legendButtonListener.elementAt(index)));
	NUM_BUTTONS++;
      }//while
      b = (JButton)(_buttons.elementAt(index));
    }//else
    return b;
  }//getButton

    /**
     *
     */
    public PlanViewCanvas getPlanViewCanvas(int canvasNum){
	return _canvas1;
    }

  private Vector _legendButtonListener = new Vector();
  protected int NUM_BUTTONS = 0;
  private Vector _buttons = new Vector();
  /**
   * The component on which the graph is drawn.
   */
    private PlanViewCanvas _canvas1 = new PlanViewCanvas(this);
  //PlanViewCanvas _canvas1 = PlanViewCanvas.getInstance();
    JPanel _planViewJPanel = new JPanel(true);
    JScrollPane _sp1;
    //if you want to have more than one bathymetry file open
    //    Hashtable _panelObjects = new Hashtable();
    App _app;

    /**
     * returns inital value of canvas width
     */
    public int getInitialWidth(){
	return _initialWidth;
    }

    /**
     * returns inital value of canvas height
     */
    public int getInitialHeight(){
	return _initialHeight;
    }

  /**
   * stores size of frame
   */
  protected Dimension _dim = null;
  protected Network _net;
  NetworkInteractor _ni;
    LandmarkInteractor _li;
  BathymetryPlot _plot;
  //display parameters

  protected XsectMenu _xsectMenu;
  protected CenterlineMenu _centerlineMenu;
    protected ToolsMenu _toolsMenu;

    public LandmarkInteractor getLandmarkInteractor(){return _li;}
    public boolean getInsertPointMode(){return _insertButton.isSelected();}
    public boolean getMovePointMode(){return _moveButton.isSelected();}
    public boolean getAddPointMode(){return _addButton.isSelected();}
    public boolean getDeletePointMode(){return _deleteButton.isSelected();}
    public boolean getAddXsectMode(){return _addXsectButton.isSelected();}
    public boolean getRemoveXsectMode(){return _removeXsectButton.isSelected();}
    public boolean getMoveXsectMode(){return _moveXsectButton.isSelected();}
    public boolean getViewXsectMode(){return _viewXsectButton.isSelected();}
    public boolean getZoomBoxMode(){return _zoomBoxButton.isSelected();}
    public boolean getZoomPanMode(){return _zoomPanButton.isSelected();}

    //add landmark, edit landmark(move, rename), delete landmark
    public boolean getAddLandmarkMode(){return lAddPopup.isSelected();}
    public boolean getEditLandmarkMode(){return lEditPopup.isSelected();}
    public boolean getMoveLandmarkMode(){return lMovePopup.isSelected();}
    public boolean getDeleteLandmarkMode(){return lDeletePopup.isSelected();}

    /**
     * Checks to see if color bathymetry uniform is selected
     */
    public boolean getColorUniform(){return _colorUniformButton.isSelected();}
    /**
     * Checks to see if color bathymetry by depth option is selected
     */
    public boolean getColorByElev(){return _colorByElevButton.isSelected();}
    /**
     * Checks to see if color bathymetry by source option is selected
     */
    public boolean getColorBySource(){return _colorBySourceButton.isSelected();}
    /**
     * Checks to see if color bathymetry by year option is selected
     */
    public boolean getColorByYear(){return _colorByYearButton.isSelected();}

    /**
     * true if a centerline selected.
     */
    private boolean _centerlineSelected = false;
    /**
     * true if a xsect selected.
     */
    private boolean _xsectSelected = false;

    private void setCenterlineSelected(boolean b){_centerlineSelected = b;}
    private boolean getCenterlineSelected(){return _centerlineSelected;}
    private void setXsectSelected(boolean b){_xsectSelected = b;}
    private boolean getXsectSelected(){return _xsectSelected;}

    
    public void showEditLandmarkMenu(boolean landmarkSelected, MouseEvent e){
	lAddPopup.setEnabled(true);
 	lMovePopup.setEnabled(landmarkSelected);
 	lEditPopup.setEnabled(landmarkSelected);
 	lDeletePopup.setEnabled(landmarkSelected);
 	cfLandmarkPopup.show(e.getComponent(),e.getX(),e.getY());
    }//showEditLandmarkMenu

    public boolean landmarkMenuIsVisible(){return cfLandmarkPopup.isVisible();}

    Border _raisedBevel = BorderFactory.createRaisedBevelBorder();

    JRadioButton _zoomBoxButton;
    JRadioButton _zoomPanButton;
    JButton _zoomFitButton;
    JButton _zoomUndoButton;
    
    ImageIcon _fileOpenIcon, _networkOpenIcon, _networkSaveIcon, _cursorIcon, _insertIcon,
	_moveIcon, _addIcon, _deleteIcon, _addXsectIcon, _removeXsectIcon, _moveXsectIcon;

    ImageIcon _viewIcon, _colorUniformIcon, _colorElevIcon, _colorSourceIcon, _colorYearIcon;
    ImageIcon _networkCalculateIcon, _cursorIconSelected, _insertIconSelected, _moveIconSelected,
	_addIconSelected, _deleteIconSelected, _addXsectIconSelected, _removeXsectIconSelected, 
	_moveXsectIconSelected;

//     ImageIcon _landmarkAddIcon, _landmarkEditIcon, _landmarkDeleteIcon, _landmarkMoveIcon,
// 	_landmarkAddSelectedIcon, _landmarkEditSelectedIcon, 
// 	_landmarkMoveSelectedIcon, _landmarkDeleteSelectedIcon;

    ImageIcon _colorUniformIconSelected, _colorElevIconSelected, _colorSourceIconSelected,
	_colorYearIconSelected, _filterYearIcon, _filterSourceIcon, _filterLabelIcon,
	_propOpenIcon, _zoomBoxIcon, _zoomBoxIconSelected, _zoomPanIcon, _zoomPanIconSelected,
	_zoomFitIcon, _zoomFitIconRollover, _zoomUndoIcon;

    JButton _fileOpenButton, _networkOpenButton, _networkSaveButton, _networkCalculateButton,
	_propOpenButton, _filterYearButton, _filterSourceButton;
    JLabel _filterLabel;

    JRadioButton _colorUniformButton, _colorByElevButton, _colorBySourceButton,
	_colorByYearButton;
    
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
    JRadioButton _addButton;
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
    //JRadioButton _invisibleRadioButton = new JRadioButton();
    /**
     * view selected cross-section
     */
    JRadioButton _viewXsectButton;

//     JRadioButton _landmarkOpenButton, _landmarkSaveButton, _landmarkAddButton, 
//  	_landmarkEditButton, _landmarkMoveButton, _landmarkDeleteButton, 
//  	_landmarkAddSelectedButton, _landmarkEditSelectedButton, 
//  	_landmarkMoveSelectedButton, _landmarkDeleteSelectedButton;
    JButton _landmarkOpenButton, _landmarkSaveButton, _landmarkAddButton, 
 	_landmarkEditButton, _landmarkMoveButton, _landmarkDeleteButton, 
 	_landmarkAddSelectedButton, _landmarkEditSelectedButton, 
 	_landmarkMoveSelectedButton, _landmarkDeleteSelectedButton;
    
    private static final Dimension _iconSize = new Dimension(25,25);
    private static final Dimension _wideIconSize = new Dimension(35,25);
    private static final Dimension _colorByIconSize = new Dimension(40, 15);
  //JButton _restoreButton  = new JButton("Restore");
  //JButton _keepButton     = new JButton("Keep");
//    protected JRadioButtonMenuItem _cMovePointMenuItem, _cAddPointMenuItem, 
//      _cInsertPointMenuItem, _cDeletePointMenuItem, _cAddXsectMenuItem, 
//      _cRemoveXsectMenuItem, _cMoveXsectMenuItem;
    
    private Landmark _landmark;
    private DigitalLineGraph _dlg;
    private DSMChannels _DSMChannels;
    private JMenuBar menubar;
    
    private JMenu cfFile, cfProperties, cfModify, cfDisplay, cfTools, cfNetwork, cfLandmark, cfCenterline,
	cfXsect, cfZoom, cfWindow, cfHelp;
    private JPopupMenu cfLandmarkPopup; 


    private JMenuItem fNew, fOpen, fClose, fSave, fSaveAs, fSaveZoomed, fMerge,
	fExtract, fPrintPreview, fPrint, fPrintSetup, fExit;
    private JMenuItem pLoad, pSave, pSaveAs;
    private JMenuItem mSource, mYear, mZSign, mErase, mRestore, mPurge, mStatus;
    private JMenuItem dParameters, dSource, dYear, dColorBy, dErased, oLandmark, cLandmarks,
	dDigitalLineGraph, dCopyToClipboard, dElevBins;
    //    JRadioButtonMenuItem dColorUniformRadioButton, dColorByDepthRadioButton, 
    //        dColorBySourceRadioButton, dColorByYearRadioButton; 
    private JRadioButtonMenuItem dFitByBathymetryMenuItem,
	dFitByNetworkMenuItem, dFitByLandmarkMenuItem;
    private ButtonGroup _colorByButtonGroup, _fitByButtonGroup, _centerlineLandmarkEditButtonGroup;
    
    private JCheckBoxMenuItem oEchoTimeSeriesInput, oEchoXsectInput, oPrintXsectResults,
	oUseFremontWeir, oUseToeDrainRestriction, oEchoToeDrainInput;
    private JMenu tOpenWaterOptionsMenu, nExportOptions;
    private JMenuItem tCompareNetwork, tCalcRect, tOpenWaterCalc;
    private JMenuItem nOpen, nSave, nSaveAs, nList, nSummary, nClearNetwork, nCalculate, 
	nExportToSEFormat, nExportTo3DFormat, nAWDSummary, nXSCheck;
    private JMenuItem lSave, lSaveAs, lAdd, lMove, lEdit, lDelete, lHelp;

    private JRadioButtonMenuItem lAddPopup, lMovePopup, lEditPopup, lDeletePopup, lHelpPopup;

    private JCheckBoxMenuItem noChannelLengthsOnly;
    private JMenuItem cCursor, cCreate, cDSMCreate, cRemove;
    //JMenuItem cRemove;
    ////JMenuItem cRename; 
    //JMenuItem cMovePoint, cAddPoint, cDelPoint, 
    private JMenuItem cRestore, cKeep, cSplit, cJoin, cView, cInfo, cList, cSummary;
    private JMenuItem xAutoGen, xCreate, xRemove, xMove, xPosition, xView, xInfo;
    private JMenuItem xSummary, xAdjustLength, xExtractData;
    private JMenuItem zPan, zFit, zFactor, zBox, zUndo;
    private JMenuItem wCascade, wTile, wArrangeIcons, wCloseAll, wRepaint;
    private JMenuItem hContents, hUsingHelp, hAbout;
    
    private ActionListener _oLandmarkListener = null;
    private ActionListener _nSaveListener = null;
    private ActionListener _nSaveAsListener = null;
    private ActionEvent _nullActionEvent = new ActionEvent(this,0,null);
    
    private static final int COLOR_BY_DEPTH  = 0;
    private static final int COLOR_BY_SOURCE = 1;
    private static final int COLOR_BY_YEAR   = 2;
    private JToolBar _legendPanel;
    private String _depthLegendTitle  = "Elev(NGVD)";
    private String _sourceLegendTitle = "Source";
    private String _yearLegendTitle   = "Year";
    private JPanel _infoPanel;
    private JLabel _horDatumLabel = new JLabel("Horizontal Datum:");
    private JLabel _horDatumUnitsLabel = new JLabel("Hor. Datum Units:");
    private JLabel _verDatumLabel = new JLabel("Vertical Datum:");
    private JLabel _verDatumUnitsLabel = new JLabel("Ver. Datum Units:");

    private JLabel _centerlineLabel = new JLabel("Selected Centerline:");
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

    private static Vector _colors = new Vector();
    private final int _initialWidth = 800;
    private final int _initialHeight = 500;

    //default values
     private double _minElevBin=-40.0;
     private double _maxElevBin=10.0;
     private int _numElevBins = 11;

    //for ngvd29 to navd88 corrections
//     private double _minElevBin = 1.95f;
//     private double _maxElevBin = 2.70f;
//     private int _numElevBins = 11;

    public double getMinElevBin(){return _minElevBin;}
    public double getMaxElevBin(){return _maxElevBin;}
    public int getNumElevBins(){return _numElevBins;}
    public void updateElevBinValues(double min, double max, int num){
	_minElevBin=min;
	_maxElevBin=max;
	_numElevBins=num;
	_elevationBins = new double[num];
	double binSize = (max-min)/(num-1.0f);
	_elevationBins[0]=max;
	for(int i=1; i<=num-1; i++){
	    _elevationBins[i] = _elevationBins[i-1] - binSize;
	}
    }
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
    private final boolean DEBUG=false;
    private static final int DEFAULT_NUM_COLORS = 1;
    private Hashtable<Integer, Color> _userSetColors= new Hashtable<Integer, Color>();
}
