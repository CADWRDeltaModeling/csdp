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
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * a dialog box with a text field to get input from user
 */
public class TextDialog extends JDialog implements ActionListener {
	public JTextField tf;
	Frame _f;
	JButton _okButton = new JButton("OK");
	private String instructions;

	public TextDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);

		_f = parent;
		configure();
	}

	public TextDialog(Frame parent, String title, String instructions, boolean modal) {
		super(parent, title, modal);
		this.instructions = instructions;
		_f = parent;
		configure();
	}

	public TextDialog(Frame parent, String title, boolean modal, double defaultValue) {
		super(parent, title, modal);

		String d = Double.toString(defaultValue);
		_f = parent;
		configure(d);
	}

	public TextDialog(Frame parent, String title, boolean modal, String defaultValue) {
		super(parent, title, modal);

		String d = defaultValue;
		_f = parent;
		configure(d);
	}

	public void configure() {
		getContentPane().setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);
		if(this.instructions!=null) {
			JTextArea instructionsTextArea = new JTextArea(this.instructions);
			add("North", instructionsTextArea);
		}
		tf = new JTextField(20);
		add("Center", tf);
		add("South", _okButton);
		ActionListener okListener = this;
		_okButton.addActionListener(okListener);
		setSize(300, 150);
		requestFocus();
	}

	public void configure(String s) {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);
		
		if(this.instructions!=null) {
			JTextArea instructionsTextArea = new JTextArea(this.instructions);
			add("North", instructionsTextArea);
		}
		tf = new JTextField(s, 20);
		add("Center", tf);
		add("South", _okButton);
		ActionListener okListener = this;
		_okButton.addActionListener(okListener);
		setSize(300, 150);
		requestFocus();
	}

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void actionPerformed(ActionEvent e) {
		// setVisible(false);
		dispose();
	}
}// class TextDialog
