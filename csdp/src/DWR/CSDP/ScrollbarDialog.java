package DWR.CSDP;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * scroll bar to define a value. factor is used to determine and number of
 * decimal places.
 *
 * @author
 * @version
 */
public class ScrollbarDialog extends Dialog implements AdjustmentListener, ActionListener {
	public ScrollbarDialog(Frame parent, String title, boolean modal, int numBars, double[] initialValue, int min,
			int max, int factor) {
		super(parent, title, modal);
		_factor = factor;
		_frame = (CsdpFrame) parent;
		_numBars = numBars;
		_sbValueLabel = new Label[_numBars];
		_sb = new Scrollbar[_numBars];
		_initialValue = initialValue;
		AdjustmentListener scrollbarListener = this;
		for (int i = 0; i <= numBars - 1; i++) {
			// _sbValueLabel[i] = new Label((new
			// Float(initialValue[i])).toString());
			_sbValueLabel[i] = new Label(Double.toString(initialValue[i]));
			_sb[i] = new Scrollbar(Scrollbar.HORIZONTAL, (int) (initialValue[i] * _factor), max * _factor / 5, min,
					(max * _factor) + max * _factor / 5);
			add(_sbValueLabel[i]);
			add(_sb[i]);
			_sb[i].addAdjustmentListener(scrollbarListener);
		}
		ActionListener okListener = this;
		Button okButton = new Button("ok");
		add(okButton);
		okButton.addActionListener(okListener);

		if (isOdd(_numBars) && _numBars >= 3) {
			_previewButton = new Button();
			_previewButton
					.setBackground(new Color((int) _initialValue[0], (int) _initialValue[1], (int) _initialValue[2]));
			add(_previewButton);
		}

		setSize(_dialogWidth, _dialogHeight + _barHeight * _numBars);
		setLayout(new GridLayout(2 + numBars - 1, 2));
	}// constructor

	public Insets getInsets() {
		return new Insets(30, 10, 10, 10);
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		double value;
		for (int i = 0; i <= _numBars - 1; i++) {
			value = (double) (_sb[i].getValue()) / _factor;
			if (DEBUG)
				System.out.println("changing adjustment value to " + value);
			// _sbValueLabel[i].setText((new Double(value)).toString());
			_sbValueLabel[i].setText(Double.toString(value));
		}
		if (_numBars >= 3)
			_previewButton.setBackground(
					new Color((int) getScrollbarValue(0), (int) getScrollbarValue(1), (int) getScrollbarValue(2)));
	}// adjustmentValueChanged

	public void updateColor(int buttonNumber) {

		// int value0 = CsdpFunctions.getColor(buttonNumber).getRed();
		// int value1 = CsdpFunctions.getColor(buttonNumber).getGreen();
		// int value2 = CsdpFunctions.getColor(buttonNumber).getBlue();
		int value0 = _frame.getColor(buttonNumber).getRed();
		int value1 = _frame.getColor(buttonNumber).getGreen();
		int value2 = _frame.getColor(buttonNumber).getBlue();

		// Float value0Object = new Float(value0);
		// Float value1Object = new Float(value1);
		// Float value2Object = new Float(value2);

		String value0String = Double.toString((double) value0);
		String value1String = Double.toString((double) value1);
		String value2String = Double.toString((double) value2);
		double value0Double = Double.parseDouble(value0String);
		double value1Double = Double.parseDouble(value1String);
		double value2Double = Double.parseDouble(value2String);

		_initialValue[0] = value0Double;
		_initialValue[1] = value1Double;
		_initialValue[2] = value2Double;
		_sbValueLabel[0].setText(value0String);
		_sbValueLabel[1].setText(value1String);
		_sbValueLabel[2].setText(value2String);

		// _initialValue[0] = value0Object.doubleValue();
		// _initialValue[1] = value1Object.doubleValue();
		// _initialValue[2] = value2Object.doubleValue();

		// _sbValueLabel[0].setText(value0Object.toString());
		// _sbValueLabel[1].setText(value1Object.toString());
		// _sbValueLabel[2].setText(value2Object.toString());
		setScrollbarValue(0, value0);
		setScrollbarValue(1, value1);
		setScrollbarValue(2, value2);

		_previewButton.setBackground(new Color(value0, value1, value2));
	}// updateColor

	public void actionPerformed(ActionEvent e) {
		dispose();
	}// actionPerformed

	public void updateScrollbar(int index, int value) {
		setScrollbarValue(index, value);
		double doubleValue = (double) _sb[index].getValue() / (double) _factor;
		_sbValueLabel[index].setText(Double.toString(doubleValue));
	}

	/**
	 * returns the value of a scrollbar. (not the value label)
	 */
	public double getScrollbarValue(int index) {
		return (double) _sb[index].getValue() / (double) _factor;
	}

	/**
	 * sets the value of a scrollbar. (not the value label)
	 */
	public void setScrollbarValue(int index, int value) {
		_sb[index].setValue(value * _factor);
	}

	/**
	 * returns true if the value is odd
	 */
	protected boolean isOdd(int value) {
		boolean returnValue;
		int value1 = (int) (((double) value) / 2.0f);
		int value2 = value;
		if (value1 == value2)
			returnValue = false;
		else
			returnValue = true;
		return returnValue;
	}

	protected static final boolean DEBUG = false;
	// Frame _frame;
	CsdpFrame _frame;
	Scrollbar[] _sb;
	Label[] _sbValueLabel = null;
	/*
	 * Use a higher scrollbar to make finer adjustments
	 */
	int _factor;
	int _numBars;
	Button _previewButton;
	int _dialogWidth = 600;
	int _dialogHeight = 150;
	int _barHeight = 50;
	double[] _initialValue;
}// ScrollbarDialog
