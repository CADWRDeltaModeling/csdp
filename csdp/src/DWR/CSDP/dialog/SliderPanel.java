package DWR.CSDP.dialog;

/*w w  w .j a v  a2 s.co  m*/
/*
   This program is a part of the companion code for Core Java 8th ed.
   (http://horstmann.com/corejava)

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import DWR.CSDP.CsdpFunctions;

/**
 * @version 1.13 2007-06-12
 * Adapted by Brad Tom for CSDP from JTestFrame class by Cay Horstman
 * @author Cay Horstmann
 */

/**
 * A frame with many sliders and a text field to show slider values.
 */
public class SliderPanel extends JPanel{
	public static void main(String[] args)	{
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				Hashtable<Integer, JLabel> labelValues = new Hashtable<Integer, JLabel>();
				labelValues.put(new Integer(0), new JLabel("0"));
				labelValues.put(new Integer(20), new JLabel("2"));
				labelValues.put(new Integer(40), new JLabel("4"));
				labelValues.put(new Integer(60), new JLabel("6"));
				labelValues.put(new Integer(80), new JLabel("8"));
				labelValues.put(new Integer(100), new JLabel("10"));

				SliderPanel frame = new SliderPanel("epsilon", null, labelValues, 0.1, 10.0, 3.4, 10.0);
				frame.setVisible(true);
			}
		});
	}


	public SliderPanel(String title, DocumentListener epsilonChangeListener, Hashtable<Integer, JLabel> labelValues,
			double minValue, double maxValue, double startingValue, double multiplier){
//		sliderPanel = new JPanel();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createRaisedBevelBorder());
		listener = new SliderChangeListener(multiplier);
		int lowValue = (int)Math.round(minValue*multiplier);
		int highValue = (int)Math.round(maxValue*multiplier);
		int initialValue = (int)Math.round(startingValue*multiplier);
		System.out.println("low, high, start="+lowValue+","+highValue+","+initialValue);
		slider = new JSlider(lowValue, highValue, initialValue);
		slider.getModel().setValue(initialValue);
		// add a slider with numeric labels

		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		textField = new JTextField("" + startingValue);
		textField.getDocument().addDocumentListener(epsilonChangeListener);
		slider.setEnabled(false);
		textField.setEnabled(false);

		addSlider(slider, title, labelValues);
		repaint();
	}

	private class SliderChangeListener implements ChangeListener{
		double _multiplier;
//		private JPanel _frame;
		public SliderChangeListener(double multiplier) {
//			_frame = frame;
			_multiplier = multiplier;

		}
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			sliderValue = (double)source.getValue()/_multiplier;
//			System.out.println("source.getValue="+sliderValue);
			textField.setText("" + sliderValue);
			//            _frame.pack();
		}
	}//class SliderChangeListener

	/**
	 * Adds a slider to the slider panel and hooks up the listener
	 * @param s the slider
	 * @param description the slider description
	 */
	public void addSlider(JSlider s, String description, Hashtable<Integer, JLabel> sliderLabels){
		s.addChangeListener(listener);
		s.setLabelTable(sliderLabels);
		JPanel panel = new JPanel();
		panel.add(new JLabel(description), BorderLayout.SOUTH);
		panel.add(s, BorderLayout.NORTH);
		panel.add(textField, BorderLayout.EAST);
		add(panel, BorderLayout.CENTER);
	}

	public void setSliderEnabled(boolean e) {slider.setEnabled(e);}
	
	private JSlider slider;
	public double getValue() {return sliderValue;}
	
	/*
	 * Display value selected using slider
	 */
	private JTextField textField;
	/*
	 * Gets the value from the slider, and displays it in the text field
	 */
	private ChangeListener listener;
	private double sliderValue;
}