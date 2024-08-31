package DWR.CSDP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;


public class XsectBathymetryMenu {

	public XsectBathymetryMenu(XsectGraph xsectGraph) {
		_xsectGraph = xsectGraph;
	}

	/**
	 * color bathymetry points in xsect view by distance from cross-section line
	 */
	public class XColorByDistance implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
				_xsectGraph.updateGraphCanvas();
				_xsectGraph.redoNextPaint();
				_xsectGraph.validate();
				// removed for conversion to swing
				// _xsectGraph._gC.repaint();
			}
		}// itemStateChanged
	}// XColorByDistance

	/**
	 * color bathymetry points in xsect view by source
	 */
	public class XColorBySource implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
				_xsectGraph.updateGraphCanvas();
				_xsectGraph.redoNextPaint();
				_xsectGraph.validate();
				// removed for conversion to swing
				// _xsectGraph._gC.repaint();
			}
		}// itemStateChanged
	}// XColorBySource

	/**
	 * color bathymetry points in xsect view by year
	 */
	public class XColorByYear implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
				_xsectGraph.updateGraphCanvas();
				_xsectGraph.redoNextPaint();
				_xsectGraph.validate();
				// removed for conversion to swing
				// _xsectGraph._gC.repaint();
			}
		}// itemStateChanged
	}// XColorByYear

	/**
	 * Change size of points in cross-section window
	 */
	public class BChangePointSize implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String dimensionString = JOptionPane.showInputDialog(_xsectGraph, "Enter new point dimension", _xsectGraph.getPointSize());
			float f = Float.parseFloat(dimensionString);
			int nin = (int) (f);
			if (nin != _xsectGraph.getPointSize()) {
				System.out.println("Changing value");
				_xsectGraph.setPointSize(nin);
				_xsectGraph.updateGraphCanvas();
				_xsectGraph.redoNextPaint();
				_xsectGraph.validate();
				// removed for conversion to swing
				// _xsectGraph._gC.repaint();
			} // if
		}// actionPerformed
	}// BChangePointSize

	XsectGraph _xsectGraph;
	protected static final boolean DEBUG = true;
}// XsectEditMenu
