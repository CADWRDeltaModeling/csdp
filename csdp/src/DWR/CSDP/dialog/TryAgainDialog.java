
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
public class TryAgainDialog extends JDialog {
	JFrame _f;
	JButton _tryAgainButton = new JButton("Try Again");
	JButton _cancelButton = new JButton("Cancel");
	private String _message = null;

	public TryAgainDialog(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);
		_f = parent;
		configure(title.length());
	}// constructor

	public TryAgainDialog(JFrame parent, String title, boolean modal, int messageLength) {
		super(parent, title, modal);
		_f = parent;
		configure(messageLength);
	}

	public TryAgainDialog(JFrame parent, String title, boolean modal, String message) {
		super(parent, title, modal);
		_f = parent;
		_message = message;
		configure(title.length());
	}// constructor

	public void reset() {
		_tryAgain = false;
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
		btnPanel.add(_tryAgainButton);
		btnPanel.add(_cancelButton);
		getContentPane().add("Center", btnPanel);
		ActionListener tryAgainListener = new SetTryAgain(this);
		ActionListener cancelListener = new SetCancel(this);
		_tryAgainButton.addActionListener(tryAgainListener);
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

	public class SetTryAgain implements ActionListener {
		TryAgainDialog _tad = null;

		public SetTryAgain(TryAgainDialog tad) {
			_tad = tad;
		}// constructor

		public void actionPerformed(ActionEvent e) {
			_tad._tryAgain = true;
			_tad._cancel = false;
			// setVisible(false);
			dispose();
		}// actionPerformed
	}// class SetYes

	public class SetCancel implements ActionListener {
		TryAgainDialog _tad = null;

		public SetCancel(TryAgainDialog tad) {
			_tad = tad;
		}

		public void actionPerformed(ActionEvent e) {
			_tad._cancel = true;
			_tad._tryAgain = false;
			// setVisible(false);
			dispose();
		}
	}// class SetCancel

	public boolean _tryAgain = false;
	public boolean _cancel = false;
	public static final float CHARACTER_TO_PIXELS = 300.0f / 44.0f;

}// class TryAgainDialog
