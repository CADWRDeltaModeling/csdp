package DWR.CSDP;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Stores data which are written to irregular_xsects.inp--used to assign cross-
 * sections to channels
 *
 * @author
 * @version
 */
public class IrregularXsectsInp {

	/**
	 * Returns number of Centerlines
	 */
	public int getNumChan() {
		return _numChan;
	}

	public String getChanNum(int index) {
		String chanNum = null;
		if (index <= _allIxiChan.size() - 1) {
			Enumeration e = _allIxiChan.keys();
			for (int i = 0; i <= index; i++) {
				chanNum = (String) e.nextElement();
			}
		} else {
			System.out.println("error in IrregularXsectsInp.getChanNum");
			System.out.println("probably called by App.calcRect");
			System.out.println("index, number of elements=" + index + "," + _allIxiChan.size());
		}
		return chanNum;
	}

	/**
	 *
	 */
	public IXIChan getChan(String chanNum) {
		return (IXIChan) (_allIxiChan.get(chanNum));
	}

	/**
	 * chanNum can't be index because some lines will have duplicate channel
	 * numbers.
	 */
	public void addLine(String chanNum, float distance, String filename) {
		IXIChan ixiChan = null;

		if (_allIxiChan.containsKey(chanNum)) {
			ixiChan = (IXIChan) (_allIxiChan.get(chanNum));
		} else {
			ixiChan = new IXIChan(chanNum);
			_allIxiChan.put(chanNum, ixiChan);
			_numChan++;
		}
		ixiChan.addIXILine(distance, filename);
		_allIxiChan.put(chanNum, ixiChan);
	}

	public void removeLine(String chanNum, float distance, String filename) {
		IXIChan ixiChan = (IXIChan) (_allIxiChan.get(chanNum));
		ixiChan.removeIXILine(distance, filename);
	}

	public boolean chanExists(String chanNum) {
		boolean returnValue = false;
		if (_allIxiChan.containsKey(chanNum)) {
			returnValue = true;
		}

		// Enumeration e = _allIxiChan.keys();
		// while(e.hasMoreElements()){
		// System.out.println("element="+(String)e.nextElement());
		// }

		return returnValue;
	}

	/**
	 * stores IXILines
	 */
	public class IXIChan {
		public IXIChan(String chanNum) {
			_chanNum = chanNum;
		}

		public int getNumLines() {
			return _numLines;
		}

		public IXILine getLine(int index) {
			return (IXILine) (_ixiLines.elementAt(index));
		}

		public void addIXILine(float distance, String filename) {
			IXILine ixiLine = new IXILine(distance, filename);
			_ixiLines.addElement(ixiLine);
			_numLines++;
		}

		public void removeIXILine(float distance, String filename) {
			int ixiLineIndex = -Integer.MAX_VALUE;
			for (int i = 0; i <= _numLines - 1; i++) {
				if (((IXILine) _ixiLines.elementAt(i)).getDistance() == distance
						&& ((IXILine) _ixiLines.elementAt(i)).getFilename() == filename) {
					ixiLineIndex = i;
				}
			}
			_ixiLines.removeElementAt(ixiLineIndex);
			_numLines--;
		}

		private int _numLines = 0;
		private Vector _ixiLines = new Vector();
		private String _chanNum;
	}// class IXIChan

	public class IXILine {
		public IXILine(float distance, String filename) {
			_distance = distance;
			_filename = filename;
		}

		public float getDistance() {
			return _distance;
		}

		public String getFilename() {
			return _filename;
		}

		public void setDistance(float dist) {
			_distance = dist;
		}

		public void setFilename(String filename) {
			_filename = filename;
		}

		private float _distance;
		private String _filename;
	}// class IXILine

	private int _numChan = 0;
	private Hashtable _allIxiChan = new Hashtable();
} // class IrregularXsectsInp
