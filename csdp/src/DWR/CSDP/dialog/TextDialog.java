
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
