package DWR.CSDP.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.text.JTextComponent;

/**
 * Created for use by DataEntryDialog, to display instructions that go with a dialog. Creates a window displaying a message. 
 * @author btom
 *
 */
public class InstructionsDialog extends JDialog {

	public InstructionsDialog(Dialog parentDialog, String title, boolean modal, JTextComponent jTextComponent) {
		super(parentDialog, title, modal);
		setLayout(new BorderLayout());
		add(jTextComponent, BorderLayout.NORTH);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OkButtonListener());
		add(okButton, BorderLayout.SOUTH);
		pack();
	}

	public class OkButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}// inner class OkButtonListener
	
}//class InstructionsDialog
