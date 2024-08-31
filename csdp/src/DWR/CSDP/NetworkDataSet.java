package DWR.CSDP;

////  import DWR.Graph.*;
////  import DWR.Graph.Canvas.*;
//import vista.graph.*;
import vista.set.DataSetIterator;
import vista.set.DefaultDataSet;

/**
 * used for creating and modifying set of network data to plot in xsect view
 */
public class NetworkDataSet extends DefaultDataSet {

	protected static final boolean DEBUG = false;

	/**
	 * constructor with name for data set
	 */
	public NetworkDataSet(String name, double[] xArray, double[] yArray) {
		super(name, xArray, yArray);
	}

	public double getMaxX() {
		double[] xArray = getXArray();
		double returnValue = -Float.MAX_VALUE;
		for (int i = 0; i <= xArray.length - 1; i++) {
			if (xArray[i] > returnValue) {
				returnValue = xArray[i];
			}
		}
		return returnValue;
	}

	public double getMaxY() {
		double[] yArray = getYArray();
		double returnValue = -Float.MAX_VALUE;
		for (int i = 0; i <= yArray.length - 1; i++) {
			if (yArray[i] > returnValue) {
				returnValue = yArray[i];
			}
		}
		return returnValue;
	}

	public double getMinX() {
		double[] xArray = getXArray();
		double returnValue = Float.MAX_VALUE;
		for (int i = 0; i <= xArray.length - 1; i++) {
			if (xArray[i] < returnValue) {
				returnValue = xArray[i];
			}
		}
		return returnValue;
	}

	public double getMinY() {
		double[] yArray = getYArray();
		double returnValue = Float.MAX_VALUE;
		for (int i = 0; i <= yArray.length - 1; i++) {
			if (yArray[i] < returnValue) {
				returnValue = yArray[i];
			}
		}
		return returnValue;
	}

	public double[] getXArray() {
		double xArray[] = new double[size()];
		DataSetIterator dsi = getIterator();
		dsi.resetIterator();
		for (int i = 0; i <= size() - 1; i++) {
			xArray[i] = dsi.getElement().getX();
			dsi.advance();
		}
		dsi.resetIterator();
		return xArray;
	}

	public double[] getYArray() {
		double yArray[] = new double[size()];
		DataSetIterator dsi = getIterator();
		dsi.resetIterator();
		for (int i = 0; i <= size() - 1; i++) {
			yArray[i] = dsi.getElement().getY();
			dsi.advance();
		}
		dsi.resetIterator();
		return yArray;
	}

}// NetworkDataSet
