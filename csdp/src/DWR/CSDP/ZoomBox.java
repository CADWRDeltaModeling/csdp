package DWR.CSDP;

import java.awt.Rectangle;
import java.util.Stack;

/**
 * This class handles the zooming in and out on a canvas. It stores the previous
 * zoom state for zooming out.
 *
 * @author
 * @version
 */

public class ZoomBox {
	public ZoomBox(PlanViewCanvas can) {
		_can = can;
	}// constructor

	public void zoomIn(Rectangle zoomBox) {
		// find new plot region
		if (DEBUG)
			System.out.println("zoomIn in ZoomBox called");
		if (DEBUG)
			System.out.println("zoomBox=" + zoomBox);
		// int y = _can.getSize().height - zoomBox.y;
		// zoomBox.y = y;

		_zoomHistory.push(zoomBox);
		_can.zoomInOut(zoomBox);
	}// zoomIn

	public void zoomOut() {
		// find new plot region
		_can.zoomInOut((Rectangle) (_zoomHistory.pop()));
	}// zoomOut

	PlanViewCanvas _can;
	/**
	 * stores zoom history
	 */
	protected Stack _zoomHistory = new Stack();
	private static final boolean DEBUG = true;
}// class ZoomBox
