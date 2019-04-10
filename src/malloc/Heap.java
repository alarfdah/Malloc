package malloc;

public class Heap {
	private static final int MAX_SIZE = 100000; // In Bytes
	private static final int WORD_SIZE = 4;
	private static final int DOUBLE_ALIGN = 8;
	
	private int curr;
	private int size;
	
	private int implicitList[];
	

	public Heap() {
		int i;
		// Initialize implicitList
		this.size = 10; // In Bytes
		this.implicitList = new int[this.size];
		
		for (i = 0; i < size; i++) {
			this.implicitList[i] = 1;
		}
		
		// In the first header and footer store the size of the block
		this.implicitList[1] = this.size - 1;
		this.implicitList[this.size - 2] = this.size - 1;
		
		// Start at 0x4 or word 1
		this.setCurr(1);
	}
		
	public static int getMaxSize() {
		return MAX_SIZE;
	}

	public static int getWordSize() {
		return WORD_SIZE;
	}

	public static int getDoubleAlign() {
		return DOUBLE_ALIGN;
	}

	public int getCurr() {
		return curr;
	}

	public void setCurr(int curr) {
		this.curr = curr;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public int[] getImplicitList() {
		return implicitList;
	}
	
	public int getImplicitList(int index) {
		int value = -1;
		try {
			value = implicitList[index];
		} catch (Exception e) {
			System.out.println("Reference to implicitList is out of bounds: " + index);
		}
		return value;
	}

	public void setImplicitList(int[] implicitList) {
		this.implicitList = implicitList;
	}
	
	public void setImplicitList(int index, int value) {
		this.implicitList[index] = value;
	}

	public int incrementSize(int inc) {
		this.curr += inc;
		return this.curr;
	}
	
	public int decrementSize(int dec) {
		this.curr += dec;
		return this.curr;
	}
	
	public int expand(int inc) throws Exception {
		int i;
		// Check if heap can be expanded
		if ((this.size + inc) >= MAX_SIZE) {
			throw new Exception("Could not expand heap beyond 100,000!");
		}
		
		// Set the size
		setSize(this.size + inc);
		
		// Create new array of the expanded size
		int newImplicitList[] = new int[this.size];
		
		// Copy content to new array
		for (i = 0; i < (this.size - inc); i++) {
			newImplicitList[i] = this.implicitList[i];
		}
		
		// Initialize the rest with 1
		for (i = (this.size - inc); i < this.size; i++) {
			newImplicitList[i] = 1;
		}
		
		implicitList = newImplicitList;
		return this.size;
	}
	
}
