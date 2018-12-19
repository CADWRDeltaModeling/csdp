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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.python.modules.jarray;

import DWR.CSDP.Csdp;
import DWR.CSDP.CsdpFunctions;

/**
 * dialog with multiple labels and text fields
 *
 * @author
 * @version $Id: TextFieldDialog.java,v 1.1 2002/06/12 18:48:38 btom Exp $
 */
public class DataEntryDialog extends JDialog implements ActionListener {
	public static final int NUMERIC_TYPE = 10;
	public static final int STRING_TYPE = 20;
	public static final int BOOLEAN_TYPE = 30;
	public static final int FILE_SPECIFICATION_TYPE = 40;

	JFrame _frame;
	private JPanel dataEntryPanel;
	//	private Border blackLineBorder = BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10), 
	//			BorderFactory.createLineBorder(Color.black));
	private Border blackLineBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), 
			new EmptyBorder(10, 10, 10, 10));

	public DataEntryDialog(JFrame parent, String title, boolean modal, int numFields) {
		super(parent, title, modal);
		_frame = parent;
		//		_names = names;
		//		_numNames = names.length;
		GridLayout layout = new GridLayout(numFields + 1, 1);
		getContentPane().setLayout(new BorderLayout());
		this.dataEntryPanel = new JPanel(layout);
		this.dataEntryPanel.setBorder(this.blackLineBorder);
		add(this.dataEntryPanel, BorderLayout.CENTER);
		this.dataEntryPanel.setPreferredSize(new Dimension(1000, 300));
		//		for (int i = 0; i <= _numNames - 1; i++) {
		//			// String index = (new Integer(i)).toString();
		//			String index = Integer.toString(i);
		//			add(new Label(names[i]));
		//			_textFields.put(names[i], new TextField(Double.toString(initValue[i]), FIELD_WIDTH));
		//			add((TextField) (_textFields.get(names[i])));
		//			if (DEBUG)
		//				System.out.println(names[i]);
		//		}
	}// constructor

	/*
	 * tooltips can be null to display nothing
	 */
	public DataEntryDialog(JFrame parent, String title, String instructions, String[] names, 
			String[] defaultValues, int[] dataTypes, int[] numDecimalPlaces, String[] extensions, String[] tooltips, boolean modal) {
		super(parent, title, modal);
		_frame = parent;
		//		_names = names;
		//		_numNames = names.length;
		GridLayout layout = new GridLayout(names.length + 1, 1);
		getContentPane().setLayout(new BorderLayout());
		this.dataEntryPanel = new JPanel(layout);
		this.dataEntryPanel.setBorder(this.blackLineBorder);
		add(this.dataEntryPanel, BorderLayout.CENTER);
		int dataEntryPanelHeightPixels = names.length * 75;
		this.dataEntryPanel.setPreferredSize(new Dimension(1000, dataEntryPanelHeightPixels));
		JTextArea instructionsJTA = new JTextArea(instructions);

		instructionsJTA.setFont(instructionsJTA.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
		instructionsJTA.setEditable(false);
		instructionsJTA.setBorder(this.blackLineBorder);
		instructionsJTA.setLineWrap(true);
		instructionsJTA.setWrapStyleWord(true);
		add(instructionsJTA, BorderLayout.NORTH);

		for(int i=0; i<names.length; i++) {
			addFieldObjects(names[i], defaultValues[i], dataTypes[i], numDecimalPlaces[i], extensions[i], tooltips[i]);
		}

		JButton _okButton = new JButton("ok");
		_okButton.setFont(_okButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
		add(_okButton, BorderLayout.SOUTH);
		validate();
		pack();
		doLayout();
		ActionListener okButtonListener = this;
		_okButton.addActionListener(okButtonListener);
		setVisible(true);
		//			setSize(1000, 200);

	}// constructor

	public void addFieldObjects(String name, String defaultValue, int dataType, int numDecimalPlaces, String extension, String tooltip) {
		if(_allComponents.containsKey(name)) {
			JOptionPane.showMessageDialog(_frame, "Error in DataEntryDialog.addFieldObjects: you are trying to add a field, "
					+ "but you have already added a field with the same name: "+name, "ERROR", JOptionPane.ERROR_MESSAGE);
		}else {
			JLabel nameLabel = new JLabel(name);
			nameLabel.setFont(nameLabel.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
			this.dataEntryPanel.add(nameLabel);
			JComponent jComponent = null;
			if(dataType==NUMERIC_TYPE) {
				NumberFormat numberFormat = null;
				if(numDecimalPlaces<=0) {
					numberFormat = NumberFormat.getIntegerInstance();
				}else {
					numberFormat = NumberFormat.getNumberInstance();
					numberFormat.setMaximumFractionDigits(numDecimalPlaces);
				}
				numberFormat.setGroupingUsed(false);
				jComponent = new JFormattedTextField(numberFormat);
				if(defaultValue!=null && defaultValue.length()>0) {
					((JFormattedTextField) jComponent).setText(defaultValue);
				}
			}else if(dataType==STRING_TYPE) {
				jComponent = new JTextField("");
				if(defaultValue!=null && defaultValue.length()>0) {
					((JTextField)jComponent).setText(defaultValue);
				}
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
			}else if(dataType==FILE_SPECIFICATION_TYPE) {
				jComponent = new JPanel(new GridLayout(1, 2));
				JTextField jTextField = new JTextField();
				JButton jButton = new JButton("Select File");
				jTextField.setFont(jTextField.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
				jTextField.setEditable(false);
				jButton.setFont(jButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE));
				jComponent.add(jTextField);
				jComponent.add(jButton);
				jButton.addActionListener(new GetFile(_frame, "Specify "+name, extension, jTextField));
				if(defaultValue!=null && defaultValue.length()>0) {
					jTextField.setText(defaultValue);
				}
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
			this.dataEntryPanel.add(jComponent);
			_allComponents.put(name, jComponent);
		}
	}

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("ok clicked");
		dispose();
	}

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

	public File getDirectory(String name) {
		String fullPath = getValue(name);
		//parse the path to get just the directory
		int lastIndex = fullPath.lastIndexOf(File.separatorChar);
		return new File(fullPath.substring(0, lastIndex));
	}

	public String getFilename(String name) {
		String fullPath = getValue(name);
		//parse the path to get just the filename
		int lastIndex = fullPath.lastIndexOf(File.separatorChar);
		return fullPath.substring(lastIndex, fullPath.length());
	}

	/*
	 * Allows user to select a file using a file selector dialog, then puts the result into the JTextField object
	 */
	private class GetFile implements ActionListener{

		private JFrame gui;
		private String dialogTitle;
		private String extension;
		private JTextField pathTextField;

		public GetFile(JFrame gui, String dialogTitle, String extension, JTextField pathTextField){
			this.gui = gui;
			this.dialogTitle = dialogTitle;
			this.extension = extension;
			this.pathTextField = pathTextField;
		}

		public void actionPerformed(ActionEvent arg0) {
			String[] filePath = CsdpFunctions.selectFilePath(gui, dialogTitle, new String[] {extension});
			pathTextField.setText(filePath[0]+File.separator+filePath[1]);
		}
	}//inner class GetFile

	int _numNames = 0;
	//	public String[] _names;
	public Hashtable<String, JComponent> _allComponents = new Hashtable<String, JComponent>();
	protected static final int FIELD_WIDTH = 10;
	protected static final boolean DEBUG = false;
}// TextFieldDialog
