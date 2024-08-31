
package DWR.CSDP.dialog;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * dialog with multiple labels and text fields
 * Use DataEntryDialog instead
 *
 * @author
 * @version $Id: TextFieldDialog.java,v 1.1 2002/06/12 18:48:38 btom Exp $
 */
public class TextFieldDialog extends Dialog implements ActionListener {

	Frame _frame;

	public TextFieldDialog(Frame parent, String title, boolean modal, String[] names, double[] initValue) {
		super(parent, title, modal);
		_frame = parent;
		_names = names;
		_numNames = names.length;
		GridLayout layout = new GridLayout(_numNames + 1, 1);
		setLayout(layout);
		Button _okButton = new Button("ok");

		for (int i = 0; i <= _numNames - 1; i++) {
			// String index = (new Integer(i)).toString();
			String index = Integer.toString(i);
			add(new Label(names[i]));
			_textFields.put(names[i], new TextField(Double.toString(initValue[i]), FIELD_WIDTH));
			add((TextField) (_textFields.get(names[i])));
			if (DEBUG)
				System.out.println(names[i]);
		}
		add(_okButton);
		validate();
		doLayout();
		ActionListener okButtonListener = this;
		_okButton.addActionListener(okButtonListener);
		setSize(1000, 200);
	}// constructor

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void actionPerformed(ActionEvent e) {
		dispose();
	}

	int _numNames = 0;
	public String[] _names;
	public Hashtable<String, TextField> _textFields = new Hashtable<String, TextField>();
	protected static final int FIELD_WIDTH = 10;
	protected static final boolean DEBUG = false;
}// TextFieldDialog
