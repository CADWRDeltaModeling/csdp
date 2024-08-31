package DWR.CSDP;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 * Adjust color of legend button. When button is clicked, a dialog with 3
 * scrollbars appears, allowing user to adjust RGB values.
 *
 * @author
 * @version $Id: AdjustRGBColor.java,v 1.3 2003/03/22 00:32:04 btom Exp $
 */

public class AdjustRGBColor implements ActionListener {

	public AdjustRGBColor(CsdpFrame gui, int buttonNumber) {
		_gui = gui;
		float[] oldColorValues = new float[3];
		_buttonNumber = buttonNumber;
		// oldColorValues[0] = CsdpFunctions.getColor(buttonNumber).getRed();
		// oldColorValues[1] = CsdpFunctions.getColor(buttonNumber).getGreen();
		// oldColorValues[2] = CsdpFunctions.getColor(buttonNumber).getBlue();
		oldColorValues[0] = _gui.getColor(buttonNumber).getRed();
		oldColorValues[1] = _gui.getColor(buttonNumber).getGreen();
		oldColorValues[2] = _gui.getColor(buttonNumber).getBlue();
		//// _s = new ScrollbarDialog(_gui,"Select new values for RGB Color",
		//// true,_numScrollBars,oldColorValues,
		//// MIN_VAL,MAX_VAL,FACTOR);

		Color c = new Color((int) oldColorValues[0], (int) oldColorValues[1], (int) oldColorValues[2]);

		_jcc = new JColorChooser(new DefaultColorSelectionModel(c));
	}// adjustRGBColor

	/**
	 * called when legend button is pressed
	 */
	public void actionPerformed(ActionEvent e) {
		float[] oldColorValues = new float[3];
		float[] newColorValues = new float[3];

		// _s.updateColor(_buttonNumber);
		// _s.show();
		Color newColor = null;

		_jcc.setColor(_gui.getColor(_buttonNumber));
		newColor = _jcc.showDialog(_gui, "Select a new color", _gui.getColor(_buttonNumber));

		if (newColor != null) {
			oldColorValues[0] = _gui.getColor(_buttonNumber).getRed();
			oldColorValues[1] = _gui.getColor(_buttonNumber).getGreen();
			oldColorValues[2] = _gui.getColor(_buttonNumber).getBlue();

			newColorValues[0] = newColor.getRed();
			newColorValues[1] = newColor.getGreen();
			newColorValues[2] = newColor.getBlue();

			//// for(int i=0; i<=_numScrollBars-1; i++){
			//// newColorValues[i] = _s.getScrollbarValue(i);
			//// }

			// CsdpFunctions.setColor(_buttonNumber,null);
			// CsdpFunctions.setColor(_buttonNumber,

			///// WHAT WAS THIS FOR???? _gui.setColor(_buttonNumber,null);
			_gui.setColor(_buttonNumber,
					new Color((int) newColorValues[0], (int) newColorValues[1], (int) newColorValues[2]));
			if ((oldColorValues[0] != newColorValues[0]) || (oldColorValues[1] != newColorValues[1])
					|| (oldColorValues[2] != newColorValues[2])) {
				_gui.updateColorLegend();
				// problem: causes null pointer when more than one graph
				// open....
				// _gui._app.updateAllXsectGraphs();
				_gui.getPlanViewCanvas(0).setUpdateCanvas(true);
				// removed for conversion to swing
				// _gui._canvas1.repaint();
				for (int i = 0; i <= _numScrollBars - 1; i++) {
					oldColorValues[0] = newColorValues[0];
				}
			} // if the new color is different
		} // if newColor isn't null
	}// actionPerformed

	/**
	 * instance of CsdpFrame
	 */
	protected CsdpFrame _gui;
	/**
	 * RGB values of current color. compare to new values to see if changed
	 */
	//// protected float[] _oldColorValues;
	/**
	 * new RGB values. compare to old values to see if changed
	 */
	protected float[] _newColorValues;
	/**
	 * number of button that was pressed--also number of color in table to
	 * change
	 */
	protected int _buttonNumber;
	/**
	 * minimum scrollbar value
	 */
	protected final int MIN_VAL = 0;
	/**
	 * maximum scrollbar value
	 */
	protected final int MAX_VAL = 255;
	/**
	 * higher value makes finer scrollbar adjustments
	 */
	protected final int FACTOR = 1;
	/**
	 * instance of ScrollbarDialog class
	 */
	//// protected ScrollbarDialog _s;
	/**
	 * number of scrollbars in the ScrollbarDialog
	 */
	protected final int _numScrollBars = 3;

	protected JColorChooser _jcc;

}// class AdjustRGBColor
	// test...--file icon isn't red...
