package DWR.CSDP;

import java.util.Hashtable;

/**
 * Stores data which are written to irregular_xsects.inp--used to assign cross-
 * sections to channels
 *
 * @author
 * @version
 */
public class XsectsInp {

	/**
	 * Returns number of Centerlines
	 */
	public int getNumAssignments() {
		return _numXsects;
	}

	public void addXsect(int xsectNum, float width, float botelv, float initStage, float initFlow) {
		// String xn = (new Integer(xsectNum)).toString();
		String xn = Integer.toString(xsectNum);
		setXsectNum(_numXsects, xsectNum);
		setWidthFeet(xn, width);
		setBotelvFeet(xn, botelv);
		setInitStageFeet(xn, initStage);
		setInitFlowCfs(xn, initFlow);
		_numXsects++;
	}

	/**
	 * Stores number of Centerlines
	 */
	public void putNumXsects(int value) {
		_numXsects = value;
	}

	public int getXsectNum(int index) {
		return _xsectNum.get(index);
	}

	public float getWidthFeet(String xsectNum) {
		return ((Float) _width.get(xsectNum)).floatValue();
	}

	public float getBotelvFeet(String xsectNum) {
		return ((Float) (_botelv.get(xsectNum))).floatValue();
	}

	public float getInitStageFeet(String xsectNum) {
		return ((Float) (_initStage.get(xsectNum))).floatValue();
	}

	public float getInitFlowCfs(String xsectNum) {
		return ((Float) (_initFlow.get(xsectNum))).floatValue();
	}

	public void setXsectNum(int index, int xsectNum) {
		_xsectNum.put(index, xsectNum);
	}

	public void setWidthFeet(String xsectNum, float width) {
		_width.put(xsectNum, new Float(width));
	}

	public void setBotelvFeet(String xsectNum, float botelv) {
		_botelv.put(xsectNum, new Float(botelv));
	}

	public void setInitStageFeet(String xsectNum, float initStage) {
		_initStage.put(xsectNum, new Float(initStage));
	}

	public void setInitFlowCfs(String xsectNum, float initFlow) {
		_initFlow.put(xsectNum, new Float(initFlow));
	}

	/**
	 * the number of lines in the file
	 */
	private int _numXsects = 0;
	private ResizableIntArray _xsectNum = new ResizableIntArray();
	private Hashtable _width = new Hashtable();
	private Hashtable _botelv = new Hashtable();
	private Hashtable _initStage = new Hashtable();
	private Hashtable _initFlow = new Hashtable();

} // class XsectsInp
