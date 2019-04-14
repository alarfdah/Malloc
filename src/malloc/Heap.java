package malloc;

/**
 * Date: 04/14/2019
 * Class: CS5541
 * Assignment: Assignment 4 - Malloc
 * Author: Ahmed Radwan
 * Email: ahmedabdelwaha.radwan@wmich.edu
 */
public class Heap {
	private static final int MAX_SIZE = 100000;
	private static final int INITIAL_SIZE = 1000;
	private static final int EXPLICIT = 2;
	
	private static int implicitOrExplicit;
	private static int firstOrBestFit;
	
	private int heap[];
	private int size;
	private int curr;
	

	public Heap() {
		int i;
		// Initialize implicitList
		this.size = INITIAL_SIZE; // In Bytes
		this.heap = new int[this.size];
		
		for (i = 0; i < size; i++) {
			this.heap[i] = 1;
		}
		
		// In the first header and footer store the size of the block
		this.heap[1] = this.size - 1;
		this.heap[this.size - 2] = this.size - 1;		
		
		// If explicit set the root at index 0
		// Which points to the payload of the next free block
		if (Heap.getImplicitOrExplicit() == EXPLICIT) {
			// index three is the index of the "next" block
			this.heap[0] = 3;
			this.curr = 3;
			
			// set the prev to point to root and next to -1
			this.heap[2] = 0;
			this.heap[3] = -1;
		}
		
		
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

	public int getCurr() {
		return curr;
	}

	public void setCurr(int curr) {
		this.curr = curr;
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
	
	public void printHeap() {
		int i;
		System.out.println("*****PRINTING HEAP*****");
		for (i = 0; i < this.size; i++) {
			System.out.printf("[%4d] = %4d\n", i, heap[i]);
		}
		System.out.println("***********************");
	}
	
}
