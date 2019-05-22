package DWR.CSDP.dialog;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Creates a JPanel containing a legend with specified title,
 * and each line containing a rectangle with specified color and a JLabel containing a string.
 * Created for use by the DataEntryDialog class.
 * @author btom
 *
 */
public class DialogLegendFactory {
	public static JPanel createLegendPanel(String title, Color[] colors, String[] text, boolean reverseOrder) {
		//add a legend to identify required vs optional field colors
		GridBagLayout legendLayout = new GridBagLayout();
		GridBagConstraints legendLayoutConstraints = new GridBagConstraints();
		legendLayoutConstraints.insets = new Insets(5, 5, 5, 5);
		//natural height, maximum width
		legendLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		legendLayoutConstraints.weightx=1.0;
		legendLayoutConstraints.gridwidth=2;
		legendLayoutConstraints.gridx=0;
		legendLayoutConstraints.gridy=0;
		legendLayoutConstraints.anchor=GridBagConstraints.CENTER;
		JPanel legendPanel = new JPanel(legendLayout);
		JLabel legendTitleLabel = new JLabel(title);
		int numLegendItems = text.length;
		legendPanel.add(legendTitleLabel, legendLayoutConstraints);
		legendLayoutConstraints.gridwidth = 1;
		//JButtons are used to create a colored rectangle
		//JLabels are used to create text to be displayed after the JButton
		for(int i=0; i<numLegendItems; i++) {
			JButton button = new JButton("   ");
			button.setBackground(colors[i]);
			button.setEnabled(false);
			String labelText = text[i];
			if(reverseOrder) labelText = text[numLegendItems-i-1];
			JLabel label = new JLabel(labelText);
			legendLayoutConstraints.gridx=0;
			legendLayoutConstraints.gridy=i+1;
			legendPanel.add(button, legendLayoutConstraints);
			legendLayoutConstraints.gridx=1;
			legendPanel.add(label,legendLayoutConstraints);
		}
		legendPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		return legendPanel;
	}//createLegendPanel

	public static JToolBar createLegendJToolbar(Color[] colors, String[] text) {
		//add a legend to identify required vs optional field colors
		JToolBar legendToolbar = new JToolBar();
		legendToolbar.setLayout(new GridLayout(0,1));
//		JLabel legendTitleLabel = new JLabel(title);
		int numLegendItems = text.length;
//		legendPanel.add(legendTitleLabel);
		//JButtons are used to create a colored rectangle
		//JLabels are used to create text to be displayed after the JButton
		for(int i=0; i<numLegendItems; i++) {
			JButton button = new JButton("   ");
			button.setBackground(colors[i]);
			button.setEnabled(false);
			JLabel label = new JLabel(text[i]);
			JPanel buttonAndLabelPanel = new JPanel(new GridLayout(1, 2));
			buttonAndLabelPanel.add(button);
			buttonAndLabelPanel.add(label);
			legendToolbar.add(buttonAndLabelPanel);
		}
//		legendPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		return legendToolbar;
	}//createLegendPanel
}//class DialogLegend
