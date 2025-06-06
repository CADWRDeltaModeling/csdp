package DWR.CSDP;

public class ResizableStringArray {
	public ResizableStringArray() {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(INITIAL_RESIZE_STEP);
		setArrayLength(INITIAL_ARRAY_LENGTH);
		initializeArray();
	}

	// constructor
	public ResizableStringArray(int initialSize) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(INITIAL_RESIZE_STEP);
		setArrayLength(initialSize);
		initializeArray();
	}

	public ResizableStringArray(int initialSize, int resizeStepSize) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		setResizeStep(resizeStepSize);
		setArrayLength(initialSize);
		initializeArray();
	}

	// copy constructor
	public ResizableStringArray(ResizableStringArray rhs) {
		numberOfResizeRequests = 0;
		resizeAccelerationStep = 5;
		resizeStep = rhs.resizeStep;
		arrayLength = rhs.arrayLength;
		array = new String[arrayLength];
		System.arraycopy(rhs.array, 0, this.array, 0, arrayLength);
	}

	// return element for read without checks
	public String get(int index) {
		if (index < 0 || index >= arrayLength)
			throw new ArrayIndexOutOfBoundsException("Illegal access to ResizableStringArray element.  index=" + index);
		else
			return (array[index]);
	}

	// return element for read/write: put value
	public void put(int index, String value) {
		if (index >= 0 && index < arrayLength)
			array[index] = value;
		else if (index >= arrayLength) {
			this.resize();
			this.put(index, value);
		} else
			throw new ArrayIndexOutOfBoundsException("Illegal access to ResizableStringArray element");
	}

	// request for length of array
	public int getSize() {
		return arrayLength;
	}

	// resize array
	public void resizeTo(int newSize) {
		String[] tmpArray;
		tmpArray = new String[newSize];
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
		String[] tmpArray;
		tmpArray = new String[arrayLength + resizeStep];
		System.arraycopy(array, 0, tmpArray, 0, arrayLength);
		arrayLength = arrayLength + resizeStep;
		array = tmpArray;
	}

	// remove an element. This method assumes that all elements are unique.
	public void removeElement(String s) {
		numberOfResizeRequests++;
		if (numberOfResizeRequests > 5) {
			resizeStep *= resizeAccelerationStep;
			numberOfResizeRequests = 0;
		}
		if (DEBUG)
			System.out.println("removing element.  arrayLength=" + arrayLength);
		String[] tmpArray;
		tmpArray = new String[arrayLength];

		if (DEBUG)
			System.out.println("about to remove element:  size=" + getSize());

		int tmpArrayIndex = 0;
		int arraySize = getSize();

		for (int i = 0; i <= arraySize - 1; i++) {
			if (DEBUG)
				System.out.println("i,array[i]=" + i + "," + array[i]);
			if (array[i] == null || array[i].equals(s) == false) {
				tmpArray[tmpArrayIndex] = array[i];
				tmpArrayIndex++;
				// setArrayLength(arraySize-1);
			}
		}
		if (DEBUG)
			System.out.println("after removing centerline. arraylength=" + arrayLength);
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
		array = new String[arrayLength];
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
	protected String[] array;

	public static final int INITIAL_RESIZE_STEP = 20;
	public static final int INITIAL_ARRAY_LENGTH = 10;
	public static final boolean DEBUG = false;
}
