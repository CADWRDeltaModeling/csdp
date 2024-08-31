package DWR.CSDP;

import java.util.Arrays;

public class ResizableDoubleArray {
	public ResizableDoubleArray() {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(INITIAL_RESIZE_STEP);
		setArrayLength(INITIAL_ARRAY_LENGTH);
		initializeArray();
	}

	// constructor
	public ResizableDoubleArray(int initialSize) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(INITIAL_RESIZE_STEP);
		setArrayLength(initialSize);
		initializeArray();
	}

	public ResizableDoubleArray(int initialSize, int resizeStepSize) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(resizeStepSize);
		setArrayLength(initialSize);
		initializeArray();
	}

	// copy constructor
	public ResizableDoubleArray(ResizableDoubleArray rhs) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		resizeStep = rhs.resizeStep;
		arrayLength = rhs.arrayLength;
		array = new double[arrayLength];
		max_index_with_value = rhs.max_index_with_value;
		System.arraycopy(rhs.array, 0, this.array, 0, arrayLength);
	}

	// return element for read without checks
	public double get(int index) {
		if (index < 0 || index >= arrayLength)
			throw new ArrayIndexOutOfBoundsException("Illegal access to ResizableDoubleArray element");
		else
			return (array[index]);
	}

	// return element for read/write: put value
	public void put(int index, double value) {
		if (index >= 0 && index < arrayLength) {
			array[index] = value;
			max_index_with_value = Math.max(max_index_with_value, index);
		}else if (index >= arrayLength) {
			this.resize();
			this.put(index, value);
			max_index_with_value = Math.max(max_index_with_value, index);
		} else
			throw new ArrayIndexOutOfBoundsException("Illegal access to ResizableDoubleArray element");
	}

	// return element for read/write: put object; store as value
	public void put(int index, Object object) {
		if (index >= 0 && index < arrayLength)
			array[index] = ((Double) object).doubleValue();
		else if (index >= arrayLength) {
			this.resize();
			this.put(index, ((Double) object).doubleValue());
		} else
			throw new ArrayIndexOutOfBoundsException("Illegal access to ResizableDoubleArray element");
	}

	// request for length of array
	public int getSize() {
		return arrayLength;
	}

	// resize array
	public void resizeTo(int newSize) {
		double[] tmpArray;
		tmpArray = new double[newSize];
		if (arrayLength < newSize) {
			System.arraycopy(array, 0, tmpArray, 0, arrayLength);
		} else {
			System.arraycopy(array, 0, tmpArray, 0, newSize);
		}
		arrayLength = newSize;
		array = tmpArray;
	}

	// request for resizing array
	public void resize() {
		numberOfResizeRequests++;
		if (numberOfResizeRequests > 5) {
			resizeStep *= resizeAccelerationStep;
			numberOfResizeRequests = 0;
		}
		double[] tmpArray;
		tmpArray = new double[arrayLength + resizeStep];
		System.arraycopy(array, 0, tmpArray, 0, arrayLength);
		arrayLength = arrayLength + resizeStep;
		array = tmpArray;
	}

	// set array length after checks
	protected void setArrayLength(int length) {
		if (length <= 0)
			length = INITIAL_ARRAY_LENGTH;
		arrayLength = length;
	}

	// set resize step after checks
	public void setResizeStep(int size) {
		if (size <= 0)
			size = INITIAL_RESIZE_STEP;
		resizeStep = size;
	}

	protected void initializeArray() {
		array = new double[arrayLength];
	}

	public double[] getArray() {
		return Arrays.copyOfRange(array, 0, max_index_with_value+1);
	}
	
	///////////
	// size of change of arrayLength on resize request
	protected int resizeStep;

	///////////
	// if too many resize requests are made the resize parameter
	// is increased by a multiple equal to this variable
	protected int resizeAccelerationStep;

	///////////
	// length of array
	protected int arrayLength;

	///////////
	// keeps track of number of resize requests
	protected int numberOfResizeRequests;
	///////////
	// array containing data
	protected double[] array;

	protected int max_index_with_value=-Integer.MAX_VALUE;
	public static final int INITIAL_RESIZE_STEP = 20;
	public static final int INITIAL_ARRAY_LENGTH = 10;

}
