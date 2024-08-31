
package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * a dialog box with Yes, No, and Cancel buttons
 */
public class YesNoDialog extends JDialog {
	JFrame _f;
	JButton _yesButton = new JButton("Yes");
	JButton _noButton = new JButton("No");
	JButton _cancelButton = new JButton("Cancel");
	private String _message = null;

	public YesNoDialog(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);

		_f = parent;
		configure(title.length());
	}// constructor

	public YesNoDialog(JFrame parent, String title, boolean modal, int messageLength) {
		super(parent, title, modal);
		_f = parent;
		configure(messageLength);
	}

	public YesNoDialog(JFrame parent, String title, boolean modal, String message) {
		super(parent, title, modal);

		_f = parent;
		_message = message;
		configure(title.length());
	}// constructor

	public void reset() {
		_yes = false;
		_no = false;
		_cancel = false;
	}

	public void configure(int titleLength) {
		getContentPane().setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);
		if (_message != null) {
			JTextArea messageLabel = new JTextArea(_message);
			Font f = new Font("Arial", Font.BOLD, 16);
			messageLabel.setEditable(false);
			messageLabel.setBackground(Color.lightGray);
			messageLabel.setFont(f);
			getContentPane().add("North", messageLabel);
		}
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(_yesButton);
		btnPanel.add(_noButton);
		btnPanel.add(_cancelButton);
		getContentPane().add("Center", btnPanel);
		ActionListener yesListener = new SetYes(this);
		ActionListener noListener = new SetNo(this);
		ActionListener cancelListener = new SetCancel(this);
		_yesButton.addActionListener(yesListener);
		_noButton.addActionListener(noListener);
		_cancelButton.addActionListener(cancelListener);
		int maxLength = 0;
		if (_message != null) {
			maxLength = Math.max(titleLength, _message.length());
		} else {
			maxLength = titleLength;
		}
		setSize((int) (50.0f + (float) maxLength * CHARACTER_TO_PIXELS), 150);
		requestFocus();
	}// configure

	// what is this for???????
	// public Insets getInsets() {
	// return new Insets(30,10,10,10);
	// }//getInsets

	public class SetYes implements ActionListener {
		YesNoDialog _ynd = null;

		public SetYes(YesNoDialog ynd) {
			_ynd = ynd;
		}// constructor

		public void actionPerformed(ActionEvent e) {
			_ynd._yes = true;
			_ynd._no = false;
			_ynd._cancel = false;
			// setVisible(false);
			dispose();
		}// actionPerformed
	}// class SetYes

	public class SetNo implements ActionListener {
		YesNoDialog _ynd = null;

		public SetNo(YesNoDialog ynd) {
			_ynd = ynd;
		}

		public void actionPerformed(ActionEvent e) {
			_ynd._no = true;
			_ynd._yes = false;
			_ynd._cancel = false;
			// setVisible(false);
			dispose();
		}
	}// class SetNo

	public class SetCancel implements ActionListener {
		YesNoDialog _ynd = null;

		public SetCancel(YesNoDialog ynd) {
			_ynd = ynd;
		}

		public void actionPerformed(ActionEvent e) {
			_ynd._cancel = true;
			_ynd._no = false;
			_ynd._yes = false;
			// setVisible(false);
			dispose();
		}
	}// class SetCancel

	public boolean _yes = false;
	public boolean _no = false;
	public boolean _cancel = false;
	public static final float CHARACTER_TO_PIXELS = 300.0f / 44.0f;

}// class YesNoDialog
