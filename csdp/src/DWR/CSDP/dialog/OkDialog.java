package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * a dialog box with a message and an ok button
 */
public class OkDialog extends JDialog {
	JFrame _f;
	JButton _okButton = new JButton("Ok");
	JTextArea _messageTA = null;

	public OkDialog(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);
		_messageTA = new JTextArea(title);
		_messageTA.setLineWrap(true);
		_messageTA.setWrapStyleWord(true);
		_messageTA.setEditable(false);
		_f = parent;
		configure(title);
	}// constructor

	public OkDialog(JFrame parent, String title, boolean modal, boolean editable) {
		super(parent, title, modal);
		_messageTA = new JTextArea();
		_messageTA.setLineWrap(true);
		_messageTA.setWrapStyleWord(true);
		_messageTA.setEditable(editable);
		_f = parent;
		configure(title);
	}// constructor

	/*
	 * For editable dialogs
	 */
	public String getMessage() {
		return _messageTA.getText();
	}
	
	/**
	 * changes message displayed in top button
	 */
	public void setMessage(String m) {
		_messageTA.setText(m);
		int frameWidth = (int) ((m.length()) * CHARACTER_TO_PIXELS + 50.f);
		int height = 150;
		if (frameWidth > 400) {
			height += 10 * (frameWidth / 400);
			frameWidth = 400;
		}
		// System.out.println("OkDialog: frameWidth="+frameWidth);
		setSize(frameWidth, height);
		requestFocus();
	}

	public void configure(String title) {
		getContentPane().setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(2, 1));
		btnPanel.add(_messageTA);
		btnPanel.add(_okButton);
		getContentPane().add("Center", btnPanel);
		ActionListener okListener = new SetOk(this);
		_okButton.addActionListener(okListener);
		setMessage(title);
		setVisible(true);
	}// configure

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}// getInsets

	public class SetOk implements ActionListener {
		OkDialog _okd = null;

		public SetOk(OkDialog okd) {
			_okd = okd;
		}

		public void actionPerformed(ActionEvent e) {
			_okd._ok = true;
			// setVisible(false);
			dispose();
		}
	}// class SetCancel

	public boolean _ok = false;
	public static final float CHARACTER_TO_PIXELS = 300.0f / 44.0f;
}// class OkDialog
