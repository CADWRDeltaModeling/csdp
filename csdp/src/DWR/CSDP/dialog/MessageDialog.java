
package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import DWR.CSDP.CsdpFunctions;

/**
 * a dialog box with a text field to get input from user
 */
public class MessageDialog extends JDialog{
	private static final boolean DEBUG = false;
	JTextArea ta;
	JFrame _f;
	JButton _okButton = new JButton("OK");
	int _numLines = 0;
	JScrollPane _sp;
	// ResizableStringArray _resizableMessage;
	String _stringMessage;
	boolean _editable;
	int _maxStringLength;
	// MessageDialog(JFrame parent, String title, ResizableStringArray message,
	// int numLines, boolean modal, boolean editable) {
	// super(parent, title, modal);
	// _resizableMessage = message;
	// _numLines = numLines;
	// _f = parent;
	// _editable = editable;
	// configure(_editable);
	// }

	public MessageDialog(JFrame parent, String title, String message, boolean modal, boolean editable,
			int maxStringLength, int numLines) {
		super(parent, title, modal);
		_stringMessage = message;
		_f = parent;
		_editable = editable;
		_maxStringLength = maxStringLength;
		_numLines = numLines;
		configure(_editable);
		System.out.println("Message Dialog Constructor");
		if (DEBUG)
			System.out.println("maxStringLength, numLines=" + _maxStringLength + "," + _numLines);
	}

	public void updateMessage(String updatedMessage) {
		ta.setText(updatedMessage);
	}

	// public void updateMessage(ResizableStringArray updatedMessage){
	// _resizableMessage = updatedMessage;
	// _numLines = _resizableMessage.getSize();
	// configure(_editable);
	// }

	// public ResizableStringArray getMessage(){
	// return _resizableMessage;
	// }

	public void configure(boolean editable) {
		// setLayout(new BorderLayout(10,10));
		getContentPane().setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);

		JButton copyToClipboardButton = new JButton("Copy to clipboard");
		copyToClipboardButton.getFont().deriveFont(CsdpFunctions.DIALOG_FONT_SIZE);
		ActionListener copyToClipboardListener = new CopyToClipboardListener(_stringMessage);
		copyToClipboardButton.addActionListener(copyToClipboardListener);
		getContentPane().add(copyToClipboardButton, BorderLayout.NORTH);
		
		ta = new JTextArea();
		ta.setEditable(editable);
		String line = " ";

		ta.setText(_stringMessage);

		// for(int i=0; i<=_numLines-1; i++){
		// ta.append(_resizableMessage.get(i));
		// ta.append("\n");
		// if(_resizableMessage.get(i) != null){
		// maxStringLength = Math.max(maxStringLength,
		// _message.get(i).length());
		// }
		// }
		ta.setColumns(_maxStringLength + 5);
		ta.setRows(_numLines);

		// add("Center", ta);
		// add("South", _okButton);
		//// _sp.setLayout(new ScrollPaneLayout());
		_sp = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		getContentPane().add("Center", _sp);
		// getContentPane().add("Center", ta);
		getContentPane().add("South", _okButton);
		
		ActionListener closeListener = new CloseListener();
		_okButton.addActionListener(closeListener);

		if(DEBUG) {
			System.out.println("size set to" + (int) ((float) (_maxStringLength) * (300.0f / 44.0f) + 50.0f) + ","
					+ (int) ((float) (_numLines) * (700.0f / 44.0f) + 100.0f));
	
			System.out.println("maxStringLength,numLines=" + _maxStringLength + "," + _numLines);
		}
		setSize((int) ((float) (_maxStringLength) * (300.0f / 44.0f) + 50.0f),
				(int) ((float) (_numLines) * (700.0f / 44.0f) + 100.0f));
		//calling RequestFocus was causing dialog to disappear when reszing. 
		//		requestFocus();
	}// configure

	public String getMessage() {
		return ta.getText();
	}

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	private class CloseListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	/**
	 * will copy displayed text to clipboard for pasting into other applications
	 */
	private class CopyToClipboardListener implements ActionListener{
		private String messageText;
		public CopyToClipboardListener(String messageText) {
			this.messageText = messageText;
		}
		public void actionPerformed(ActionEvent arg0) {
			StringSelection stringSelection = new StringSelection(this.messageText);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	}//CopyToClipboardListener
	
}// class MessageDialog
