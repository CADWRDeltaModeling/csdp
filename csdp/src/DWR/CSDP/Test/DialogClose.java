package DWR.CSDP.Test;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;


/** 
 * @see http://stackoverflow.com/questions/5540354 
 */
public class DialogClose extends JDialog {

	public class DoSomethingListener implements ActionListener{
		public DoSomethingListener(JDialog theDialog) {}
		public void actionPerformed(ActionEvent arg0) {

			System.out.println("DoSomethingListener");
		}

	}

	JButton closeButton;
	
    public DialogClose() {
        this.setLayout(new GridLayout(0, 1));
        this.add(new JLabel("Dialog close test.", JLabel.CENTER));
        closeButton = new JButton(new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e) {
                DialogClose.this.setVisible(false);
                DialogClose.this.dispatchEvent(new WindowEvent(
                    DialogClose.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        this.add(closeButton);
        JButton somethingButton = new JButton("Something");
        somethingButton.addActionListener(new DoSomethingListener(this));
        this.add(somethingButton);
    }

    public void close() {
    	this.closeButton.doClick();
    }
    
    private void display() {
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
                DialogClose dialogClose = new DialogClose();
                dialogClose.display();
                dialogClose.close();
//            }
//        });
        
    }
}