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

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DWR.CSDP.ResizableBooleanArray;
import DWR.CSDP.ResizableStringArray;

/**
 * make checkbox to select data to be displayed. Adds specified number of
 * checkboxes to frame.
 *
 * @author
 * @version $Id: DataFilterCheckbox.java,v 1.1.1.1 2002/06/10 20:15:01 btom Exp
 *          $
 */
public class DataFilterCheckbox extends JDialog implements ActionListener {

	public DataFilterCheckbox(JFrame parent, String title, boolean modal, ResizableStringArray names,
			ResizableBooleanArray initState, int numNames) {
		super(parent, title, modal);
		_parentJFrame = parent;
		_names = names;
		_numNames = numNames;
		_initStates = initState;
		// setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().setLayout(new GridLayout(numNames + 3, 1));
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel"); 

		for (int i = 0; i <= numNames - 1; i++) {
			_checkboxes.put(names.get(i), new JCheckBox(names.get(i), initState.get(i).booleanValue()));
			add(_checkboxes.get(names.get(i)));
			if (DEBUG)
				System.out.println(names.get(i));
		}
		
		//if there are an odd number of buttons, add a blank label to make sure 
		if(numNames % 2 != 0) {
			add(new JLabel(""));
		}

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel);
		okButton.setName("OK");
		cancelButton.setName("Cancel");
		ActionListener buttonListener = this;
		okButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		setSize(HORIZONTAL, VERTICAL + COMPONENT_HEIGHT * numNames);
		addWindowClosingListener();
	}// constructor
	
//	public DataFilterCheckbox(Frame parent, String title, boolean modal, float defaultValue, ResizableStringArray names,
//			ResizableBooleanArray initState, int numNames) {
//		super(parent, title, modal);
//		_names = names;
//		String d = Float.toString(defaultValue);
//		_frame = parent;
//		_initStates = initState;
//
//		_numNames = numNames;
//		// setLayout(new FlowLayout(FlowLayout.LEFT));
//		setLayout(new GridLayout(numNames + 1, 1));
//		Button _okButton = new Button("ok");
//
//		for (int i = 0; i <= numNames - 1; i++) {
//			_checkboxes.put(names.get(i), new Checkbox(names.get(i), initState.get(i).booleanValue()));
//			add(_checkboxes.get(names.get(i)));
//			if (DEBUG)
//				System.out.println(names.get(i));
//		}
//		add(_okButton);
//		ActionListener okButtonListener = this;
//		_okButton.addActionListener(okButtonListener);
//		setSize(HORIZONTAL, VERTICAL + COMPONENT_HEIGHT * numNames);
//		addWindowClosingListener();
//	}// contructor

	private void addWindowClosingListener() {
		addWindowListener(new WindowAdapter() {
		  public void windowClosed(WindowEvent e){
			  resetStatesToInitial();
			  dispose();
		  }

		  public void windowClosing(WindowEvent e){
			  resetStatesToInitial();
			  dispose();
		  }
		});
	}//addWindowClosingListener

	/*
	 * Restores checkbox selection states to initial states 
	 */	
	  private void resetStatesToInitial() {
			for (int i = 0; i <= _numNames - 1; i++) {
				JCheckBox checkBox = _checkboxes.get(_names.get(i));
				checkBox.setSelected(_initStates.get(i));
			}
	  }//resetStatesToInitial
	
	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void actionPerformed(ActionEvent e) {
		// setVisible(false);
		if(((JButton)e.getSource()).getName().equals("OK")) {
			if(DEBUG) System.out.println("ok clicked");
		}else {
			if(DEBUG) System.out.println("ok was NOT clicked");
			resetStatesToInitial();
		}
		
		dispose();
	}

	/**
	 * Handles events from checkbox
	 */
	public class CListener implements ItemListener {
		String _name = null;
		DataFilterCheckbox _dfc = null;

		public CListener(String name, DataFilterCheckbox dfc) {
			if (DEBUG)
				System.out.println("called constructor");
			_name = name;
			_dfc = dfc;
		}// CListener

		public void itemStateChanged(ItemEvent e) {
			if (DEBUG)
				System.out.println("state has changed");
			if ((_dfc._checkboxes.get(_name)).isSelected() == false) {
				if (DEBUG)
					System.out.println("setting checkbox true");
				(_dfc._checkboxes.get(_name)).setSelected(true);
			} // if false then set true
			else {
				if (DEBUG)
					System.out.println("setting checkbox false");
				(_dfc._checkboxes.get(_name)).setSelected(false);
			} // else
		}// itemStateChanged
	}// CListener

	int _numNames = 0;
	public ResizableStringArray _names = null;
	public Hashtable<String, JCheckBox> _checkboxes = new Hashtable<String, JCheckBox>();
	JFrame _parentJFrame;
	protected static final int HORIZONTAL = 300;
	protected static final int VERTICAL = 100;
	protected static final int COMPONENT_HEIGHT = 20;
	protected static final boolean DEBUG = false;
	/*
	 * The states of the checkboxes upon instantiation
	 */
	private ResizableBooleanArray _initStates;
	
}// DataFilterCheckbox
