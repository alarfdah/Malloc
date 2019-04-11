package malloc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allocate {
	private static final int HEADER_SIZE = 1;
	private static final int FOOTER_SIZE = 1;
	private static final int WORD_SIZE = 4;
	private static final int SBRK = 10;
	private static final int FREE = 1;
		
	private Heap heap;
	private int pointer[];
	
	/**
	 * Allocated: 	Last bit = 0
	 * Free: 		Last bit = 1
	 */
	public Allocate(int pointers[]) {
		heap = new Heap();
		this.pointer = pointers;
	}
	
	public void process(List<String> content) throws Exception {
		// Declaring variables
		String line[] = new String[4];
		String instruction;
		int alloc;
		int realloc;
		int reference;
		int i;
		
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
				pointer[reference] = mymalloc(alloc);
				break;
			// In case of: realloc
			case "r":
				// Initialize variables
				realloc = Integer.parseInt(line[1]);
				alloc = Integer.parseInt(line[2]);
				reference = Integer.parseInt(line[3]);
				
				// Call realloc
				pointer[reference] = myrealloc(alloc, realloc);
				break;
			// In case of: free
			case "f":
				// Initialize variables
				reference = Integer.parseInt(line[1]);
				
				// Call free
				if (!myfree(reference)) {
					throw new Exception("Block is unallocated");
				}
				break;
				// In any other case
				default:
					throw new Exception("Invalid instruction " + instruction);
			}
		}
	}
	
	public int split(int blckPtr, int mallocSize) {
		// Declaring variables
		int oldHeaderValue;
		int oldHeaderIndex;
		int newHeaderIndex;
		int oldFooterIndex;
		int newFooterIndex;
		int payloadPtr;
		
		// Get old header index
		oldHeaderIndex = blckPtr;
		
		// Get old header value
		oldHeaderValue = heap.getHeap(blckPtr) - FREE;
		
		// Get payload ptr
		payloadPtr = oldHeaderIndex + 1;
		
		// If the size of the free block is greater than the size requested then split
		if ((oldHeaderValue - FREE) > mallocSize) {
			// Change size in old header
			heap.setHeap(oldHeaderIndex, mallocSize);
			
			// Get new footer index
			newFooterIndex = oldHeaderIndex + mallocSize - FOOTER_SIZE;
			
			// Set size in new Footer
			heap.setHeap(newFooterIndex, mallocSize);
			
			// SPLIT into -> [OLD_HEADER][NEW_FOOTER][NEW_HEADER][OLD_FOOTER]
			// Get new header index
			newHeaderIndex = newFooterIndex + FOOTER_SIZE;
			
			// Set size in new Header
			heap.setHeap(newHeaderIndex, oldHeaderValue - mallocSize + FREE);
			
			// Get old footer index
			oldFooterIndex = oldHeaderIndex + oldHeaderValue - FOOTER_SIZE;				
			
			// Change size in old footer
			heap.setHeap(oldFooterIndex, oldHeaderValue - mallocSize + FREE);
			
		// Otherwise, don't split
		} else if ((oldHeaderValue - FREE) == mallocSize) {
			// Change size in old header
			heap.setHeap(oldHeaderIndex, mallocSize);
			
			// Get old footer index
			oldFooterIndex = oldHeaderIndex + oldHeaderValue - FOOTER_SIZE;				
			
			// Change size in old footer
			heap.setHeap(oldFooterIndex, mallocSize);
			
		// If size not enough, sbrk()
		} else {
			mysbrk(SBRK);
			payloadPtr = -1;
		}
		
		return payloadPtr;
	}
	
	public int mymalloc(int size) throws Exception {
		// Declaring variables
		int headerValue;
		int headerIndex;
		int payloadPtr;
		
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
		
		// First header starts at 1s 
		headerIndex = 1;
		
		// Go through heap, start at second word
		while (headerIndex < heap.getSize()) {
			// Get header value
			headerValue = heap.getHeap(headerIndex);
			
			// When i = 1 is free and its size is 
			if (headerValue % 2 == FREE) {
				
				// Split if necessary, if -1, then did not allocate but used sbrk
				if ((payloadPtr = split(headerIndex, size)) != -1) {
					return payloadPtr;
				}		
			// If block is allocated	
			} else {
				
				// Next header
				headerIndex += headerValue;		
			}
		}
		
		return -1;
	}

	
	public void coalesce(int blckPtr, int size) {
		// Declaring variables
		int nextBlckPtr;
		int prevBlckPtr;
		int nextBlckVal;
		int prevBlckVal;
		
		// Get next block header
		nextBlckPtr = blckPtr + size;

		// Get prev block footer
		prevBlckPtr = blckPtr - HEADER_SIZE;
		
		// Coalesce with next if possible
		// Check if next exists
		if ((nextBlckVal = heap.getHeap(nextBlckPtr)) != -1) {
			// If block is free
			if (nextBlckVal >= 4 && nextBlckVal % 2 == FREE) {
				// Set the size of the current block's header
				heap.setHeap(blckPtr, nextBlckVal + size);
				
				// Reset the size of the current block's footer
				heap.setHeap(blckPtr + size - FOOTER_SIZE, 1);
				
				// Reset the size of the next block's header
				heap.setHeap(nextBlckPtr, 1);
				
				// Set the size of the next blocks's footer
				heap.setHeap(nextBlckPtr + (nextBlckVal - FREE) - FOOTER_SIZE, nextBlckVal + size);
				
				// Set new size to not mess up values for prev
				size += nextBlckVal - FREE;
			}
		}
		
		// Coalesce with next if possible
		// Check if next exists
		if ((prevBlckVal = heap.getHeap(prevBlckPtr)) != -1) {
			// If block is free
			if (prevBlckVal >= 4 && prevBlckVal % 2 == FREE) {
				// Set the size of the prev block's footer
				heap.setHeap(prevBlckPtr, 1);
				
				// Reset the size of the prev block's header
				heap.setHeap(prevBlckPtr - (prevBlckVal - FREE) + HEADER_SIZE, prevBlckVal + size);
				
				// Reset the size of the current block's header
				heap.setHeap(blckPtr, 1);
				
				// Set the size of the current blocks's footer
				heap.setHeap(blckPtr + size - FOOTER_SIZE, prevBlckVal + size);
			}
		}
	}
	
	public boolean myfree(int ptr) {
		if (ptr < 0) {
			return false;
		}
		// Get start of memory block index
		int blckPtr = pointer[ptr] - HEADER_SIZE;
		
		// Get the size of the current block
		int size = heap.getHeap(blckPtr);
		
		// Set the size of the header to (size + 1)
		heap.setHeap(blckPtr, size + FREE);
		
		// Set the size of the footer to (size + 1)
		heap.setHeap(blckPtr + size - FOOTER_SIZE, size + FREE);
		
		// remove from reference array
		pointer[ptr] = 0;
		
		// coalesce
		coalesce(blckPtr, size);
		
		return true;
	}
	
	public int myrealloc(int ptr, int size) throws Exception {
		// Declaring variables
		int content[];
		int blckPtr;
		int blckSize;
		int payloadPtr;
		int i;
		
		// Get block pointer
		blckPtr = pointer[ptr] - HEADER_SIZE;
		
		// Get block size
		blckSize = heap.getHeap(blckPtr);
		
		// Copy contents of block
		content = new int[blckSize];
		for(i = 0; i < (blckSize - HEADER_SIZE - FOOTER_SIZE); i++) {
			content[i] = heap.getHeap(blckPtr + i + HEADER_SIZE);
		}
		
		// free he current block
		if (!myfree(ptr)) {
			throw new Exception("Block is unallocated");
		}
		
		// malloc the new block
		payloadPtr = mymalloc(size);
		
		return payloadPtr;
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
			
			// Get size before expanding
			oldSize = heap.getSize();
			
			// Get old footer index
			oldFooterIndex = oldSize - 2;
			
			// Get old footer value which is at the second to last index
			oldFooterValue = heap.getHeap(oldFooterIndex);
			
			
			// Get header associated with that footer if free
			// If footer is allocated, size will be even,
			// Otherwise, size will be odd
			if (oldFooterValue % 2 == FREE) {
				// Get old header index
				oldHeaderIndex = oldFooterIndex - (oldFooterValue - FREE) + HEADER_SIZE;			
			} else {
				// Error
				oldHeaderIndex = -1;
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
				heap.setHeap(oldFooterIndex, 1);
				
				// Set the size on the new footer
				heap.setHeap(newFooterIndex, newFooterValue);
				
				// Set the old header's size to match the new footer
				heap.setHeap(oldHeaderIndex, newFooterValue);
			// In case of the last block being allocated	
			} else {
				// Get new header's index
				newHeaderIndex = oldFooterIndex + FOOTER_SIZE;
				
				// Calculate the new header's value
				newHeaderValue = size;
				
				// Set the new header on the heap
				heap.setHeap(newHeaderIndex, newHeaderValue + FREE);
				
				// Get the new footer's index
				newFooterIndex = newHeaderIndex + size - FOOTER_SIZE;
				
				// Set the new footer's value
				newFooterValue = size;
				
				// Set the new footer on the heap
				heap.setHeap(newFooterIndex, newFooterValue + FREE);
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
