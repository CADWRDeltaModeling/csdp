package DWR.CSDP;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class CoordConv {

	public AffineTransform _at = new AffineTransform();
	public AffineTransform _ati = new AffineTransform();
	public Point2D.Double _p2d = new Point2D.Double();
	public Point2D _p2dDest = null;
	public int[] _destInt = new int[2];

	/**
	 * Update affine transform and its inverse. always call when minSlope
	 * changes or height of canvas changes.
	 */
	public void updateAT(double minSlope, double height) {
		// reflect about x axis
		_at.setToScale(1, -1);
		// translate to move to display region
		_at.translate(1, -height);
		// scale with minSlope
		_at.scale(minSlope, minSlope);

		try {
			_ati = _at.createInverse();
		} catch (java.awt.geom.NoninvertibleTransformException e) {
			System.out.println("ERROR in CsdpFunctions.updateAT: " + e);
		}

		// System.out.println("updating affine transforms with minSlope,
		// height="+minSlope+","+height);
		double[] m1 = new double[6];
		_at.getMatrix(m1);
		double[] m2 = new double[6];
		_ati.getMatrix(m2);
	}

	/*
	 * convert pixels to length.
	 */
	public void pixelsToLength(int xPixels, int yPixels, double minX, double minY, double[] destDouble) {
		_p2d.setLocation((double) xPixels, (double) yPixels);
		_p2dDest = _ati.transform(_p2d, _p2dDest);
		destDouble[0] = (double) _p2dDest.getX() + minX;
		destDouble[1] = (double) _p2dDest.getY() + minY;
	}

	/**
	 * convert utm to pixels. consecutive calls will replace previous calculated
	 * values.
	 */
	public int[] utmToPixels(double x, double y, double minX, double minY) {
		_p2d.setLocation(x - minX, y - minY);
		_p2dDest = _at.transform(_p2d, _p2dDest);
		_destInt[0] = (int) _p2dDest.getX();
		_destInt[1] = (int) _p2dDest.getY();
		return _destInt;
	}
}// class CoordConv
