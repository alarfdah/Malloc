package malloc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allocate {
	private static final int HEADER_SIZE = 1;
	private static final int FOOTER_SIZE = 1;
	private static final int WORD_SIZE = 4;
	private static final int FREE = 1;
		
	private Heap heap;
	private int pointers[];
	
	/**
	 * Allocated: 	Last bit = 0
	 * Free: 		Last bit = 1
	 */
	public Allocate(int pointers[]) {
		heap = new Heap();
		this.pointers = pointers;
	}
	
	public void process(List<String> content) throws Exception {
		// Declaring variables
		String line[] = new String[4];
		String instruction;
		int payload;
		int alloc;
		int realloc;
		int reference;
		int i = 0;
		
		// Loop through instructions
		for (i = 0; i < content.size(); i++) {
			// Parse string
			line = content.get(i).split(", ");
			
			// Get instruction
			instruction = line[0];
			
			// Switch on instruction
			switch (instruction) {
			// In case of: malloc
			case "a":
				// Initialize variables
				alloc = Integer.parseInt(line[1]);
				reference = Integer.parseInt(line[2]);
				
				// Call malloc
				pointers[reference] = mymalloc(alloc);
				break;
			// In case of: realloc
			case "r":
				// Initialize variables
				realloc = Integer.parseInt(line[1]);
				alloc = Integer.parseInt(line[2]);
				reference = Integer.parseInt(line[3]);
				
				// Call realloc
				pointers[reference] = myrealloc(alloc, realloc);
				break;
			// In case of: free
			case "f":
				// Initialize variables
				alloc = Integer.parseInt(line[1]);
				
				// Call free
				if (!myfree(alloc)) {
					throw new Exception("Block is unallocated");
				}
				break;
				// In any other case
				default:
					throw new Exception("Invalid instruction " + instruction);
			}
		}
	}
	
	public int mymalloc(int size) throws Exception {
		// Declaring variables
		int oldHeaderValue;
		int newHeaderValue;
		int oldFooterValue;
		int newFooterValue;
		int oldHeaderIndex;
		int newHeaderIndex;
		int oldFooterIndex;
		int newFooterIndex;
		int payloadSize;
		int payloadPtr;
		int i = 1;
		
		// Trivial case
		if (size == 0) {
			return 0;
		}
		
		// Convert size to a multiple of 8
		size = roundUp(size, 8);
		
		// Convert size from bytes to words
		size /= WORD_SIZE;
		
		// Add Header and Footer to the size
		size += HEADER_SIZE + FOOTER_SIZE;
		
		// Go through heap, start at second word
		while (i < heap.getSize()) {
			// When i = 1 is free and its size is 
			if (heap.getImplicitList(i) % 2 == 1 
					&& (heap.getImplicitList(i) - FREE) > size) {
				
				// Get old header index
				oldHeaderIndex = i;
				
				// Get old header value
				oldHeaderValue = heap.getImplicitList(i) - FREE;	
				
				// Get payload ptr
				payloadPtr = oldHeaderIndex + 1;
				
				// Change size in old header
				heap.setImplicitList(oldHeaderIndex, size);
				
				// Get new footer index
				newFooterIndex = oldHeaderIndex + size - FOOTER_SIZE;
				
				// Set size in new Footer
				heap.setImplicitList(newFooterIndex, size);
				
				// Get new header index
				newHeaderIndex = newFooterIndex + FOOTER_SIZE;
				
				// Set size in new Header
				heap.setImplicitList(newHeaderIndex, oldHeaderValue - size + FREE);
				
				// Get old footer index
				oldFooterIndex = oldHeaderIndex + oldHeaderValue - FOOTER_SIZE;				
				
				// Change size in old footer
				heap.setImplicitList(oldFooterIndex, oldHeaderValue - size + FREE);
				
				
				return payloadPtr;
			// If only block left is exact fit
			} else if (heap.getImplicitList(i) % 2 == 1 
					&& (heap.getImplicitList(i) - 1) == size) {
				// Get old header index
				oldHeaderIndex = i;
				
				// Get old header value
				oldHeaderValue = heap.getImplicitList(i) - FREE;	
				
				// Get payload ptr
				payloadPtr = oldHeaderIndex + 1;
				
				// Change size in old header
				heap.setImplicitList(oldHeaderIndex, size);
				
				// Get old footer index
				oldFooterIndex = oldHeaderIndex + oldHeaderValue - FOOTER_SIZE;				
				
				// Change size in old footer
				heap.setImplicitList(oldFooterIndex, size);
				
				return payloadPtr;
			// If block is allocated	
			} else if (heap.getImplicitList(i) % 2 == 0) {
				
				// Get header index
				newHeaderIndex = i;
				
				// Get header value
				newHeaderValue = heap.getImplicitList(newHeaderIndex);
				
				// Get Footer index
				newFooterIndex = newHeaderIndex + newHeaderValue - FOOTER_SIZE;
				
				// Next header
				i = newFooterIndex + FOOTER_SIZE;
				System.out.println("hello");
			// If non of the above, then not enough space
			} else {
				mysbrk(1000);
			}
		}
		
		
		return -1;
	}

	
	public boolean myfree(int ptr) {
		
		
		
		return false;
	}
	
	public int myrealloc(int ptr, int size) {
		
		return 0;
	}
	
	public void mysbrk(int size) {
		try {
			// Declaring variables
			int oldSize;
			int oldHeaderValue;
			int oldFooterValue;
			int newHeaderValue;
			int newFooterValue;
			int oldHeaderIndex;
			int oldFooterIndex;
			int newHeaderIndex;
			int newFooterIndex;
			int lastBlockAllocated;
			
			// Get size before expanding
			oldSize = heap.getSize();
			
			// Get old footer index
			oldFooterIndex = oldSize - 2;
			
			// Get old footer value which is at the second to last index
			oldFooterValue = heap.getImplicitList(oldFooterIndex);
			
			
			// Get header associated with that footer if free
			// If footer is allocated, size will be even,
			// Otherwise, size will be odd
			if (oldFooterValue % 2 == FREE) {
				// Get old header index
				oldHeaderIndex = oldFooterIndex - (oldFooterValue - FREE) + HEADER_SIZE;
				
				// Get old header value
				oldHeaderValue = heap.getImplicitList(oldHeaderIndex);				
			} else {
				// Error
				oldHeaderIndex = -1;
				oldHeaderValue = -1;
			}
			
			// Expand the head
			heap.expand(size);
			
			// Change the footer's index to second to last on new array
			// Only if the footer is free.
			// Otherwise create a new header and footer
			if (oldFooterValue % 2 == FREE) {
				// Grab new footer's index
				newFooterIndex =  oldFooterIndex + size;
				
				// Grab new footer's value
				newFooterValue = oldFooterValue + size;
				
				// Set oldFooter's value back to default
				heap.setImplicitList(oldFooterIndex, 1);
				
				// Set the size on the new footer
				heap.setImplicitList(newFooterIndex, newFooterValue);
				
				// Set the old header's size to match the new footer
				heap.setImplicitList(oldHeaderIndex, newFooterValue);
			// In case of the last block being allocated	
			} else {
				// Get new header's index
				newHeaderIndex = oldFooterIndex + FOOTER_SIZE;
				
				// Calculate the new header's value
				newHeaderValue = size;
				
				// Set the new header on the heap
				heap.setImplicitList(newHeaderIndex, newHeaderValue + FREE);
				
				// Get the new footer's index
				newFooterIndex = newHeaderIndex + size - FOOTER_SIZE;
				
				// Set the new footer's value
				newFooterValue = size;
				
				// Set the new footer on the heap
				heap.setImplicitList(newFooterIndex, newFooterValue + FREE);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Reference: https://stackoverflow.com/questions/3407012/c-rounding-up-to-the-nearest-multiple-of-a-number
	 * @param numToRound
	 * @param multiple
	 * @return
	 */
	public int roundUp(int numToRound, int multiple) {
	    if (multiple == 0)
	        return numToRound;

	    int remainder = Math.abs(numToRound) % multiple;
	    if (remainder == 0)
	        return numToRound;

	    if (numToRound < 0)
	        return -(Math.abs(numToRound) - remainder);
	    else
	        return numToRound + multiple - remainder;
	}

}
