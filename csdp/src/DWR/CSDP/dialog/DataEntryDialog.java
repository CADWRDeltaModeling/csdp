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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import DWR.CSDP.CsdpFunctions;

/**
 * dialog with multiple labels and text fields
 * After instantiating, call getResponse to determine which button was clicked.
 * Instructions panel will help determine dialog width if it contains html tags.
 *
 * @author
 * @version $Id: TextFieldDialog.java,v 1.1 2002/06/12 18:48:38 btom Exp $
 */
public class DataEntryDialog extends JDialog {
	/*
	 * Identifies the type of one of the fields in the dialog as numeric
	 */
	public static final int NUMERIC_TYPE = 10;
	/*
	 * Identifies the type of one of the fields in the dialog as String
	 */
	public static final int STRING_TYPE = 20;
	/*
	 * Identifies the type of one of the fields in the dialog as boolean, which will create a checkbox
	 */
	public static final int BOOLEAN_TYPE = 30;
	/*
	 * Identifies the type of one of the fields in the dialog as file specification, which will create
	 * a text field and a button which will bring up a file selector dialog
	 */
	public static final int FILE_SPECIFICATION_TYPE = 40;
	/*
	 * Identifies the type of one of the fields in the dialog as directory specification, which will create
	 * a text field and a button which will bring up a file selector dialog that will only select a directory
	 */
	public static final int DIRECTORY_SPECIFICATION_TYPE = 50;
	/*
	 * The identifier of the button that was clicked. Will be compared to values above (NUMERIC_TYPE, STRING_TYPE, etc.) to 
	 * determine which button was clicked to close the dialog
	 */
	private int _response;
	/*
	 * Identifies the button that was clicked as the "Ok" button
	 */
	public static final int OK=100;
	/*
	 * Identifies the button that was clicked as the "Cancel" button
	 */
	public static final int CANCEL=200;

	/*
	 * The parent of this dialog, used for modality
	 */
	private JFrame _frame;
	/*
	 * The panel storing the data entry components
	 */
	private JPanel dataEntryPanel;
	JButton okButton;
	/*
	 * The number of names, each of which correspond to a data entry component in the dialog
	 */
	int _numNames = 0;
	/*
	 * Stores all data entry components. Key is the name
	 */
	public Hashtable<String, JComponent> _allComponents = new Hashtable<String, JComponent>();

	/*
	 * To turn on debugging, which will include extra print statements.
	 */
	protected static final boolean DEBUG = false;

	private Border blackLineBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), 
			new EmptyBorder(10, 10, 10, 10));
	/*
	 * If the jComponent corresponding to the component name contains a null selection and the value is required to be non-null
	 * (such as a required entry and a text field is blank), the ok button will be disabled)
	 */
	private Hashtable<String, Boolean> disableIfNullHashtable;
	/*
	 * Color to use for JLabels if required field
	 */
	public static final Color REQUIRED_COLOR = new Color(255, 126, 0);
	/*
	 * Color to use for JLabels if optional field (dark green)
	 */
	public static final Color OPTIONAL_COLOR = new Color(0, 153, 51);

	/*
	 * Constructor for only String or boolean values
	 */
	public DataEntryDialog(JFrame parent, String title, String instructions, String[] names,
			String[] defaultValues, int[] dataTypes, boolean[] disableIfNull, String[] tooltips, boolean modal) {
		super(parent, title, modal);
		int[] numDecimalPlaces = null;
		String[] extensions = null;
		createDialog(parent, title,instructions, names, defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
				extensions, tooltips, modal);
	}//constructor
	
	/*
	 * Constructor for only numeric values
	 */
	public DataEntryDialog(JFrame parent, String title, String instructions, String[] names, 
			String[] defaultValues, int[] dataTypes, boolean[] disableIfNull, int[] numDecimalPlaces, 
			String[] tooltips, boolean modal) {
		super(parent, title, modal);
		String[] extensions = null;
		createDialog(parent, title,instructions, names, defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
				extensions, tooltips, modal);
	}//constructor
		
	/*
	 * Constructor for only file selectors and strings or not.
	 */
	public DataEntryDialog(JFrame parent, String title, String instructions, String[] names, 
			String[] defaultValues, int[] dataTypes, boolean[] disableIfNull, 
			String[] extensions, String[] tooltips, boolean modal) {
		super(parent, title, modal);
		int[] numDecimalPlaces = null;
		createDialog(parent, title,instructions, names, defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
				extensions, tooltips, modal);
	}//constructor
	
	
	
	/*
	 * Constructor for String, numeric, boolean, and file selectors
	 */
	public DataEntryDialog(JFrame parent, String title, String instructions, String[] names, 
			String[] defaultValues, int[] dataTypes, boolean[] disableIfNull, int[] numDecimalPlaces, 
			String[] extensions, String[] tooltips, boolean modal) {
		super(parent, title, modal);
		createDialog(parent, title,instructions, names, defaultValues, dataTypes, disableIfNull, numDecimalPlaces, 
				extensions, tooltips, modal);
	}//constructor
	
	/*
	 * tooltips can be null to display nothing
	 * Names are used as labels and identifiers, and must all be unique
	 * numDecimalPlaces is only used for numeric types. 
	 * extensions is only used for file selector types. 
	 * GridBagLayout is used to make variable width columns in the layout, 
	 * mainly so that file selector components can have very wide text fields to display full path specification.
	 */
	private void createDialog(JFrame parent, String title, String instructions, String[] names, 
			String[] defaultValues, int[] dataTypes, boolean[] disableIfNull, int[] numDecimalPlaces, 
			String[] extensions, String[] tooltips, boolean modal) {
		_frame = parent;
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints mainWindowGridBagConstraints = new GridBagConstraints();
		mainWindowGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.disableIfNullHashtable = new Hashtable<String, Boolean>();
		for(int i=0; i<names.length; i++) {
			this.disableIfNullHashtable.put(names[i], disableIfNull[i]);
		}
		
		getContentPane().setLayout(layout);
		
		GridBagLayout dataEntryPanelLayout = new GridBagLayout();
		this.dataEntryPanel = new JPanel(dataEntryPanelLayout);
		this.dataEntryPanel.setBorder(this.blackLineBorder);

//		int dataEntryPanelHeightPixels = names.length * 75;
//		int dataEntryPanelPreferredWidth = 1000;
//		for(int i=0; i<dataTypes.length; i++) {
//			if(dataTypes[i]==DataEntryDialog.FILE_SPECIFICATION_TYPE) {
//				dataEntryPanelPreferredWidth=1500;
//				break;
//			}
//		}
//		this.dataEntryPanel.setPreferredSize(new Dimension(dataEntryPanelPreferredWidth, dataEntryPanelHeightPixels));

		//Add a JTextArea or a JEditorPane (for HTML) displaying instructions
		JTextComponent instructionsJTC = null;
		if(instructions.indexOf("<HTML>")>=0 || instructions.indexOf("<html>")>0){
	        // create a JEditorPane that renders HTML and defaults to the system font.
			//copied from https://explodingpixels.wordpress.com/2008/10/28/make-jeditorpane-use-the-system-font/
			instructionsJTC = new JEditorPane(new HTMLEditorKit().getContentType(), instructions);
	        // set the text of the JEditorPane to the given text.
	        instructionsJTC.setText(instructions);
	        // add a CSS rule to force body tags to use the default label font
	        // instead of the value in javax.swing.text.html.default.csss
	        Font font = UIManager.getFont("Label.font");
	        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
	                "font-size: " + font.getSize()*1.5 + "pt; }";
	        ((HTMLDocument) instructionsJTC.getDocument()).getStyleSheet().addRule(bodyRule);
		}else {
			instructionsJTC = new JTextArea(instructions);
			instructionsJTC.setText(instructions);
			instructionsJTC.setFont(instructionsJTC.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
			((JTextArea) instructionsJTC).setLineWrap(true);
			((JTextArea) instructionsJTC).setWrapStyleWord(true);
		}
		instructionsJTC.setEditable(false);
		instructionsJTC.setBorder(this.blackLineBorder);
		
		JPanel legendPanel = DialogLegendFactory.createLegendPanel("Legend", new Color[] {REQUIRED_COLOR, OPTIONAL_COLOR}, 
				new String[] {"Required Entry", "Optional Entry"});
		
		GridBagLayout instructionsAndLegendLayout = new GridBagLayout();
		GridBagConstraints instructionsAndLegendGridBagConstraints = new GridBagConstraints();
		JPanel instructionsAndLegendPanel = new JPanel(instructionsAndLegendLayout);
		instructionsAndLegendGridBagConstraints.insets = new Insets(5, 5, 5, 5);
		//natural height, maximum width
		instructionsAndLegendGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		instructionsAndLegendGridBagConstraints.weightx=1.0;
		instructionsAndLegendGridBagConstraints.gridwidth=1;
		instructionsAndLegendGridBagConstraints.gridx=0;
		instructionsAndLegendGridBagConstraints.gridy=0;
		//add instructions panel
		instructionsAndLegendPanel.add(instructionsJTC, instructionsAndLegendGridBagConstraints);
		//add legend panel
		instructionsAndLegendGridBagConstraints.gridx=1;
		instructionsAndLegendGridBagConstraints.anchor=GridBagConstraints.PAGE_END;
		instructionsAndLegendPanel.add(legendPanel, instructionsAndLegendGridBagConstraints);
		
//		add(instructionsAndLegendPanel, BorderLayout.NORTH);
		
		mainWindowGridBagConstraints.gridx=0;
		mainWindowGridBagConstraints.gridy=0;
		getContentPane().add(instructionsAndLegendPanel,  mainWindowGridBagConstraints);
		mainWindowGridBagConstraints.gridx=0;
		mainWindowGridBagConstraints.gridy=1;
		getContentPane().add(this.dataEntryPanel, mainWindowGridBagConstraints);

		
		//Add the data entry components to the dataEntryPanel
		for(int i=0; i<names.length; i++) {
			int numDecimalPlacesInt = -Integer.MAX_VALUE;
			String extension = null;
			String tooltip = null;
			boolean directoryOnly = false;
			if(numDecimalPlaces!=null) numDecimalPlacesInt = numDecimalPlaces[i];
			if(extensions!=null) extension = extensions[i];
			if(tooltips!=null) tooltip = tooltips[i];
			addFieldObjects(i, names[i], defaultValues[i], dataTypes[i], numDecimalPlacesInt, extension, tooltip);
		}

		//add the buttons to the bottom of the dialog.
		JPanel bottomButtonPanel = new JPanel(new GridLayout(1, 2));
		this.okButton = new JButton("ok");
		this.okButton.setFont(this.okButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
		bottomButtonPanel.add(this.okButton);
		boolean enableOk = okToEnableOkButton();
		this.okButton.setEnabled(enableOk);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(cancelButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
		CancelListener cancelListener = new CancelListener();
		cancelButton.addActionListener(cancelListener);
		bottomButtonPanel.add(cancelButton);
		addWindowListener(cancelListener);
//		add(bottomButtonPanel, BorderLayout.SOUTH);

		mainWindowGridBagConstraints.gridx=0;
		mainWindowGridBagConstraints.gridy=2;
		getContentPane().add(bottomButtonPanel, mainWindowGridBagConstraints);
		validate();
		pack();
		doLayout();
		OkListener okButtonListener = new OkListener(this);
		this.okButton.addActionListener(okButtonListener);
		//adding a MouseListener means that when the button loses focus, one click will activate it rather than 2 
		this.okButton.addMouseListener(okButtonListener);
		setVisible(true);
		//Let size be managed by preferred size of contents, rather than specifying a size here.
		//			setSize(1000, 200);
	}// constructor

	/*
	 * Adds a JLabel (containing field name) and a JComponent to the dataEntryPanel.
	 * What kind of JComponent will depend upon requested dataType:
	 * If numeric, jComponent will be a JFormattedTextField
	 * If String, jComponent will be a JTextField
	 * If boolean, jComponent will be a JCheckbox
	 * If file specification, jComponent will be a JTextField and a JButton, which will open up a file selector dialog.
	 */
	private void addFieldObjects(int index, String name, String defaultValue, int dataType, int numDecimalPlaces, String extension, 
			String tooltip) {
		if(_allComponents.containsKey(name)) {
			JOptionPane.showMessageDialog(_frame, "Error in DataEntryDialog.addFieldObjects: you are trying to add a field, "
					+ "but you have already added a field with the same name: "+name, "ERROR", JOptionPane.ERROR_MESSAGE);
		}else {
			JLabel nameLabel = new JLabel(name, JLabel.RIGHT);
			nameLabel.setFont(nameLabel.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
			if(disableIfNullHashtable.get(name)) {
				nameLabel.setForeground(REQUIRED_COLOR );
			}else if (!disableIfNullHashtable.get(name)){
				nameLabel.setForeground(OPTIONAL_COLOR);
			}
			GridBagConstraints jComponentGridBagConstraints = new GridBagConstraints();
			jComponentGridBagConstraints.insets = new Insets(5, 5, 5, 5);
			//natural height, maximum width
			jComponentGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			jComponentGridBagConstraints.weightx=1.0;
			jComponentGridBagConstraints.gridwidth=1;
			jComponentGridBagConstraints.gridx=0;
			jComponentGridBagConstraints.gridy=index;
			this.dataEntryPanel.add(nameLabel, jComponentGridBagConstraints);
			JComponent jComponent = null;
			if(dataType==NUMERIC_TYPE) {
				NumberFormat numberFormat = null;
				if(numDecimalPlaces<=0) {
					numberFormat = NumberFormat.getIntegerInstance();
				}else {
					numberFormat = NumberFormat.getNumberInstance();
					numberFormat.setMinimumFractionDigits(0);
					numberFormat.setMaximumFractionDigits(numDecimalPlaces);
				}
				numberFormat.setGroupingUsed(false);
				jComponent = new JFormattedTextField(numberFormat);
				if(defaultValue!=null && defaultValue.length()>0) {
					((JFormattedTextField) jComponent).setText(defaultValue);
				}
				//always add the document listener after setting default value
				((JFormattedTextField)jComponent).getDocument().addDocumentListener(new ValidateListener(this));
			}else if(dataType==STRING_TYPE) {
				jComponent = new JTextField("");
				if(defaultValue!=null && defaultValue.length()>0) {
					((JTextField)jComponent).setText(defaultValue);
				}
				//always add the document listener after setting default value
				((JTextField)jComponent).getDocument().addDocumentListener(new ValidateListener(this));
			}else if(dataType==BOOLEAN_TYPE) {
				boolean initVal = true;
				if(defaultValue!=null && defaultValue.length()>0) {
					if(defaultValue.equalsIgnoreCase("true")) {
						initVal = true;
					}else if(defaultValue.equalsIgnoreCase("false")) {
						initVal = false;
					}else {
						initVal = false;
					}
				}
				jComponent = new JCheckBox(name, initVal);
				((JCheckBox)jComponent).setSelected(initVal);
			}else if(dataType==FILE_SPECIFICATION_TYPE || dataType==DIRECTORY_SPECIFICATION_TYPE) {
				jComponent = new JPanel(new GridBagLayout());
				JTextField jTextField = new JTextField();
				if(defaultValue!=null && defaultValue.length()>0) {
					jTextField.setText(defaultValue);
				}
				//always add the document listener after setting default value
				jTextField.getDocument().addDocumentListener(new ValidateListener(this));
				jTextField.setFont(jTextField.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
				jTextField.setEditable(false);
				JButton selectFileOrDirectoryButton = null;
				if(dataType==DIRECTORY_SPECIFICATION_TYPE) {
					selectFileOrDirectoryButton = new JButton("Select Directory");
				}else {
					selectFileOrDirectoryButton = new JButton("Select File");
				}
				selectFileOrDirectoryButton.setFont(selectFileOrDirectoryButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
				JButton clearSelectionButton = new JButton("Clear");
				clearSelectionButton.setFont(clearSelectionButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));

				GridBagConstraints fileSpecGridBagConstraints = new GridBagConstraints();
				fileSpecGridBagConstraints.insets = new Insets(5, 5, 5, 5);
				//natural height, maximum width
				fileSpecGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
				fileSpecGridBagConstraints.weightx=1.0;
				fileSpecGridBagConstraints.gridwidth=3;
				fileSpecGridBagConstraints.gridx=0;
				fileSpecGridBagConstraints.gridy=0;
				jComponent.add(jTextField, fileSpecGridBagConstraints);
				
				fileSpecGridBagConstraints.weightx=0.0;
				fileSpecGridBagConstraints.gridwidth=1;
				fileSpecGridBagConstraints.gridx=3;
				fileSpecGridBagConstraints.gridy=0;
				jComponent.add(selectFileOrDirectoryButton, fileSpecGridBagConstraints);
				
				fileSpecGridBagConstraints.gridx=4;
				fileSpecGridBagConstraints.gridy=0;
				jComponent.add(clearSelectionButton, fileSpecGridBagConstraints);
				if(dataType==DIRECTORY_SPECIFICATION_TYPE) {
					selectFileOrDirectoryButton.addActionListener(new GetDirectory(_frame, "Specify "+name, jTextField));
				}else {
					selectFileOrDirectoryButton.addActionListener(new GetFile(_frame, "Specify "+name, extension, jTextField));
				}
				clearSelectionButton.addActionListener(new ClearSelection(jTextField));
			}

			jComponent.setFont(jComponent.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
			String adjustedTooltip = "";
			if(tooltip!=null) {

				if(tooltip.length()>50) {
					//if long string, add newline chars
					int lengthIndex = 0;
					boolean addNewlineAfterNextSpace = false;
					for(int i=0; i<tooltip.length(); i++) {
						adjustedTooltip += tooltip.charAt(i);
						lengthIndex++;
						if(lengthIndex>50) {
							addNewlineAfterNextSpace = true;
						}
						if(addNewlineAfterNextSpace && tooltip.charAt(i)==' ') {
							adjustedTooltip+="<BR>";
							addNewlineAfterNextSpace = false;
							lengthIndex=0;
						}
					}
					adjustedTooltip = "<HTML>"+adjustedTooltip+"</HTML>";
				}else {
					adjustedTooltip = tooltip;
				}
				nameLabel.setToolTipText(adjustedTooltip);
			}
			
			jComponentGridBagConstraints.insets = new Insets(5, 5, 5, 5);
			//natural height, maximum width
			jComponentGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			jComponentGridBagConstraints.weightx=1.0;
			jComponentGridBagConstraints.gridwidth=1;
			jComponentGridBagConstraints.gridx=1;
			jComponentGridBagConstraints.gridy=index;
			this.dataEntryPanel.add(jComponent, jComponentGridBagConstraints);
			_allComponents.put(name, jComponent);
		}//if already haven't added a component with specified name
	}//addFieldObjects

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public int getResponse() {
		return _response;
	}
	
	/*
	 * Listener for the Ok button. Needs to be a MouseListener because otherwise
	 * if user is editing text in a text field, ok button will require 2 clicks
	 */
	private class OkListener implements ActionListener, MouseListener{
		private DataEntryDialog dataEntryDialog;

		public OkListener(DataEntryDialog dataEntryDialog) {
			this.dataEntryDialog = dataEntryDialog;
		}
		public void actionPerformed(ActionEvent e) {
			_response = OK;
			dispose();
		}

		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {
			//MouseListeners will be active even if the button is disabled, so must check to see if button is enabled.
			if(this.dataEntryDialog.getOkButtonEnabled()) {
				_response = OK;
				dispose();
			}
		}//mousePressed

		public void mouseReleased(MouseEvent arg0) {}
	}//inner class OkListener
	
	public boolean getOkButtonEnabled() {return this.okButton.isEnabled();}

	/*
	 * Listener for Cancel button
	 */
	public class CancelListener extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			cancel();
		}

		public void actionPerformed(ActionEvent e) {
			cancel();
		}// actionPerformed

		private void cancel() {
			_response = CANCEL;
			dispose();
		}
	}// class CancelListener

	/*
	 * returns a numeric value or String entry entered by user in any component.
	 * Components are identified by name in this dialog. The names of each component are specified to the constructor.
	 */
	public String getValue(String name) {
		String returnValue = null;
		JComponent jComponent = _allComponents.get(name);
		if(jComponent instanceof JTextField) {
			returnValue = ((JTextField)jComponent).getText();
		}else if(jComponent instanceof JCheckBox) {
			boolean isSelected = ((JCheckBox)jComponent).isSelected();
			if(isSelected) {
				returnValue = "true";
			}else {
				returnValue = "false";
			}
		}else if(jComponent instanceof JPanel) {
			JTextField jTextField = (JTextField)(((JPanel)jComponent).getComponent(0));
			returnValue = jTextField.getText();
		}
		//		return _textFields.get(name).getText();
		return returnValue;
	}

	/*
	 * returns a directory specification specified by user in any file selection component.
	 * Components are identified by name in this dialog. The names of each component are specified to the constructor.
	 */
	public File getDirectory(String name) {
		String fullPath = getValue(name);
		//parse the path to get just the directory
		File returnFile = null;
		if(fullPath!=null && fullPath.length()>0) {
			int lastIndex = fullPath.lastIndexOf(File.separatorChar);
			returnFile = new File(fullPath.substring(0, lastIndex+1));
		}
		return returnFile;
	}

	/*
	 * returns a filename specification specified by user in any file selection component.
	 * Components are identified by name in this dialog. The names of each component are specified to the constructor.
	 */
	public String getFilename(String name) {
		String fullPath = getValue(name);
		//parse the path to get just the filename
		String returnString = null;
		if(fullPath!=null && fullPath.length()>0) {
			int lastIndex = fullPath.lastIndexOf(File.separatorChar);
			returnString = fullPath.substring(lastIndex+1, fullPath.length());
		}
		return returnString;
	}

	/*
	 * Sets the Ok button to enabled or disabled
	 */
	public void setOkButtonEnabled(boolean setting) {
		this.okButton.setEnabled(setting);
	}
	
	/*
	 * disables the Ok button for the parent dialog if the text field is a required input and is blank 
	 * Enables the Ok button if all required fields have entries.
	 */
	private class ValidateListener implements DocumentListener{

		private DataEntryDialog dataEntryDialog;
		public ValidateListener(DataEntryDialog dataEntryDialog) {
			this.dataEntryDialog = dataEntryDialog;
		}
		
		private void enableIfOk() {
			boolean enable = this.dataEntryDialog.okToEnableOkButton();
			if(enable) {
				this.dataEntryDialog.setOkButtonEnabled(true);
			}else {
				this.dataEntryDialog.setOkButtonEnabled(false);
			}
		}
		
		public void changedUpdate(DocumentEvent arg0) {
			enableIfOk();
		}

		public void insertUpdate(DocumentEvent arg0) {
			enableIfOk();
		}

		public void removeUpdate(DocumentEvent arg0) {
			enableIfOk();
		}
	}//inner class ValidateListener
	
	/*
	 * Checks to see if required entries are made 
	 */
	private boolean okToEnableOkButton() {
		Enumeration<String> names = _allComponents.keys();
		boolean returnValue = true;
		while(names.hasMoreElements()) {
			String name = names.nextElement();
			if(this.disableIfNullHashtable.get(name)) {
				JComponent jComponent = _allComponents.get(name);
				if(jComponent instanceof JTextField) {
					if(((JTextField)jComponent).getText().trim().length()<=0){
						returnValue = false;
						break;
					}
				}else if(jComponent instanceof JCheckBox) {
					//do nothing
				}else if(jComponent instanceof JPanel) {
					JTextField jTextField = (JTextField)(((JPanel)jComponent).getComponent(0));
					if(jTextField.getText().trim().length()<=0) {
						returnValue = false;
						break;
					}
				}
			}
		}
		return returnValue;
	}//okToEnableOkButton

	private class GetDirectory implements ActionListener{
		private JFrame csdpFrame;
		private String dialogTitle;
		private JTextField pathTextField;

		public GetDirectory(JFrame csdpFrame, String dialogTitle, JTextField pathTextField){
			this.csdpFrame = csdpFrame;
			this.dialogTitle = dialogTitle;
			this.pathTextField = pathTextField;
		}

		public void actionPerformed(ActionEvent arg0) {
			String directory = CsdpFunctions.selectDirectory(csdpFrame, dialogTitle);
			if(directory!=null && directory.length()>0) {
				pathTextField.setText(directory);
//				pathTextField.setPreferredSize(pathTextField.getPreferredSize());
			}
		}
	}
	
	/*
	 * Allows user to select a file using a file selector dialog, then puts the result into the JTextField object
	 */
	private class GetFile implements ActionListener{

		private JFrame csdpFrame;
		private String dialogTitle;
		private String extension;
		private JTextField pathTextField;

		public GetFile(JFrame csdpFrame, String dialogTitle, String extension, JTextField pathTextField){
			this.csdpFrame = csdpFrame;
			this.dialogTitle = dialogTitle;
			this.extension = extension;
			this.pathTextField = pathTextField;
		}

		public void actionPerformed(ActionEvent arg0) {
			String[] filePath = CsdpFunctions.selectFilePath(csdpFrame, dialogTitle, new String[] {extension});
			boolean updateText = true;
			if(filePath!=null) {
				for(int i=0; i<filePath.length; i++) {
					String string = filePath[i];
					if(string==null || string.length()<=0) {
						updateText=false;
						break;
					}
				}
			}
			if(updateText) {
				pathTextField.setText(filePath[0]+File.separator+filePath[1]);
//				pathTextField.setPreferredSize(pathTextField.getPreferredSize());
			}
		}
	}//inner class GetFile

	private class ClearSelection implements ActionListener{
		private JTextField jTextField;
		public ClearSelection(JTextField jTextField) {
			this.jTextField = jTextField;
		}
		public void actionPerformed(ActionEvent arg0) {
			jTextField.setText("");
		}
	}//inner class ClearSelection
	
}// class DataEntryDialog
