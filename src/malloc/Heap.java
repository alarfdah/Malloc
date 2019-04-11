package malloc;

public class Heap {
	private static final int MAX_SIZE = 100000;
	
	private static int implicitOrExplicit;
	private static int firstOrBestFit;
	
	private int heap[];
	private int size;
	
	private FreeNode head;
	

	public Heap() {
		int i;
		// Initialize implicitList
		this.size = 10; // In Bytes
		this.heap = new int[this.size];
		
		for (i = 0; i < size; i++) {
			this.heap[i] = 1;
		}
		
		// In the first header and footer store the size of the block
		this.heap[1] = this.size - 1;
		this.heap[this.size - 2] = this.size - 1;
		
	}
		
	public static int getMaxSize() {
		return MAX_SIZE;
	}
	
	public static int getImplicitOrExplicit() {
		return implicitOrExplicit;
	}

	public static void setImplicitOrExplicit(int implicitOrExplicit) {
		Heap.implicitOrExplicit = implicitOrExplicit;
	}

	public static int getFirstOrBestFit() {
		return firstOrBestFit;
	}

	public static void setFirstOrBestFit(int firstOrBestFit) {
		Heap.firstOrBestFit = firstOrBestFit;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public FreeNode getHead() {
		return head;
	}

	public void setHead(FreeNode head) {
		this.head = head;
	}

	public int[] getHeap() {
		return heap;
	}
	
	public int getHeap(int index) {
		int value = -1;
		try {
			value = heap[index];
		} catch (Exception e) {
			System.out.println("Reference to implicitList is out of bounds: " + index);
		}
		return value;
	}

	public void setHeap(int[] implicitList) {
		this.heap = implicitList;
	}
	
	public void setHeap(int index, int value) {
		this.heap[index] = value;
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
			newImplicitList[i] = this.heap[i];
		}
		
		// Initialize the rest with 1
		for (i = (this.size - inc); i < this.size; i++) {
			newImplicitList[i] = 1;
		}
		
		heap = newImplicitList;
		return this.size;
	}
	
}
