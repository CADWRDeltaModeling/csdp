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

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import vista.app.commands.AddNewGroupCommand;

/**
 * dialog with multiple labels and text fields
 *
 * @author
 * @version $Id: TextFieldDialog.java,v 1.1 2002/06/12 18:48:38 btom Exp $
 */
public class DataEntryDialog extends JDialog implements ActionListener {
	public static final int NUMERIC_TYPE = 10;
	public static final int STRING_TYPE = 20;
	Frame _frame;

	public DataEntryDialog(Frame parent, String title, boolean modal, int numFields) {
		super(parent, title, modal);
		_frame = parent;
//		_names = names;
//		_numNames = names.length;
		GridLayout layout = new GridLayout(numFields + 1, 1);
		getContentPane().setLayout(layout);

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

	public void addFieldObjects(String name, String defaultValue, int dataType, int numDecimalPlaces, boolean lastField) {
		if(_textFields.containsKey(name)) {
			JOptionPane.showMessageDialog(_frame, "Error in DataEntryDialog.addFieldObjects: you are trying to add a field, "
					+ "but you have already added a field with the same name: "+name, "ERROR", JOptionPane.ERROR_MESSAGE);
		}else {
			add(new Label(name));
			JTextField jTextField = null;
			if(dataType==NUMERIC_TYPE) {
				NumberFormat numberFormat = null;
				if(numDecimalPlaces<=0) {
					numberFormat = NumberFormat.getIntegerInstance();
				}else {
					numberFormat = NumberFormat.getNumberInstance();
					numberFormat.setMaximumFractionDigits(numDecimalPlaces);
				}
				numberFormat.setGroupingUsed(false);
				jTextField = new JFormattedTextField(numberFormat);
			}else if(dataType==STRING_TYPE) {
				jTextField = new JTextField();
			}
			if(defaultValue!=null && defaultValue.length()>0) {
				jTextField.setText(defaultValue);
			}
			add(jTextField);
			_textFields.put(name, jTextField);
		}
		if(lastField) {
			Button _okButton = new Button("ok");
			add(_okButton);
			validate();
			doLayout();
			ActionListener okButtonListener = this;
			_okButton.addActionListener(okButtonListener);
			setSize(1000, 200);
		}
	}

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void actionPerformed(ActionEvent e) {
		dispose();
	}

	public String getValue(String name) {
		return _textFields.get(name).getText();
	}
	
	int _numNames = 0;
//	public String[] _names;
	public Hashtable<String, JTextField> _textFields = new Hashtable<String, JTextField>();
	protected static final int FIELD_WIDTH = 10;
	protected static final boolean DEBUG = false;
}// TextFieldDialog
