package malloc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allocate {
	private static final int HEADER_SIZE = 1;
	private static final int FOOTER_SIZE = 1;
	private static final int WORD_SIZE = 4;
	private static final int EXPLICIT = 2;
	private static final int SBRK = 10;
	private static final int ROOT = 0;
	private static final int FREE = 1;
	private static final int BEST = 2;
		
	private Heap heap;
	private int pointer[];
	
	/**
	 * Allocated: 	Last bit = 0
	 * Free: 		Last bit = 1
	 */
	public Allocate(Heap heap, int pointers[]) {
		this.heap = heap;
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
	
	public int split(int blckPtr, int blckSize) {
		// Declaring variables
		int oldHeaderValue;
		int oldHeaderIndex;
		int newHeaderIndex;
		int oldFooterIndex;
		int newFooterIndex;
		int payloadPtr;
		int prev;
		int next;
		
		// Get old header index
		oldHeaderIndex = blckPtr;
		
		// Get old header value
		oldHeaderValue = heap.getHeap(blckPtr) - FREE;
		
		// Get payload ptr
		payloadPtr = oldHeaderIndex + 1;
		
		// If the size of the free block is greater than the size requested then split
		if (oldHeaderValue > blckSize) {
			
			// Change size in old header
			heap.setHeap(oldHeaderIndex, blckSize);
			
			// Get new footer index
			newFooterIndex = oldHeaderIndex + blckSize - FOOTER_SIZE;
			
			// Set size in new Footer
			heap.setHeap(newFooterIndex, blckSize);
			
			// SPLIT into -> [OLD_HEADER][NEW_FOOTER][NEW_HEADER][OLD_FOOTER]
			// Get new header index
			newHeaderIndex = newFooterIndex + FOOTER_SIZE;
			
			// Set size in new Header
			heap.setHeap(newHeaderIndex, oldHeaderValue - blckSize + FREE);
			
			// Get old footer index
			oldFooterIndex = oldHeaderIndex + oldHeaderValue - FOOTER_SIZE;				
			
			// Change size in old footer
			heap.setHeap(oldFooterIndex, oldHeaderValue - blckSize + FREE);
			
			// If explicit
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Remove the prev and next addresses to the next split free block
				prev = heap.getHeap(oldHeaderIndex + HEADER_SIZE);
				next = heap.getHeap(oldHeaderIndex + 2 * HEADER_SIZE);
				
				// Reset the contents of the payload
				heap.setHeap(oldHeaderIndex + HEADER_SIZE, 1);
				heap.setHeap(oldHeaderIndex + 2 * HEADER_SIZE, 1);
				
				// Transfer to the new header
				heap.setHeap(newHeaderIndex + HEADER_SIZE, prev);
				heap.setHeap(newHeaderIndex + 2 * HEADER_SIZE, next);
				
				// Set the curr at the most recent free block
				heap.setCurr(newHeaderIndex + 2 * HEADER_SIZE);
				
				// if prev is 0, then it points to root
				if (prev == 0) {
					heap.setHeap(prev, newHeaderIndex + 2 * HEADER_SIZE);
				// Change the next pointer from previous block to point to the new payload
				} else if (prev != 0) {
					// Prev will take you back to the prev ptr or prev block, add 1 to get its next
					heap.setHeap(prev + 1, newHeaderIndex + 2 * HEADER_SIZE);	
				}
				
			}
		// Else if block fits
		} else if (oldHeaderValue == blckSize) {
			// Change size in old header
			heap.setHeap(oldHeaderIndex, blckSize);
			
			// Get new footer index
			oldFooterIndex = oldHeaderIndex + blckSize - FOOTER_SIZE;
			
			// Set size in new Footer
			heap.setHeap(oldFooterIndex, blckSize);
			
			// If explicit
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Remove the prev and next addresses to the next split free block
				prev = heap.getHeap(oldHeaderIndex + HEADER_SIZE);
				next = heap.getHeap(oldHeaderIndex + 2 * HEADER_SIZE);
				
				// Reset the contents of the payload
				heap.setHeap(oldHeaderIndex + HEADER_SIZE, 1);
				heap.setHeap(oldHeaderIndex + 2 * HEADER_SIZE, 1);
				
				// If there is not next when we allocate, allocate a new free block
				if (next == -1) {
					newHeaderIndex = mysbrk(SBRK);
					// Transfer to the new header
					heap.setHeap(newHeaderIndex + HEADER_SIZE, prev);
					heap.setHeap(newHeaderIndex + 2 * HEADER_SIZE, next);
					
					// Set the curr at the most recent free block
					heap.setCurr(newHeaderIndex + 2 * HEADER_SIZE);
					
					// if prev is 0, then it points to root
					if (prev == 0) {
						heap.setHeap(prev, newHeaderIndex + 2 * HEADER_SIZE);
					// Change the next pointer from previous block to point to the new payload
					} else if (prev != 0) {
						// Prev will take you back to the prev ptr or prev block, add 1 to get its next
						heap.setHeap(prev + 1, newHeaderIndex + 2 * HEADER_SIZE);	
					}
				
				// If there is a next, we have to change pointers of prev and next
				} else {
					// Since next != -1, change next's prev to point to prev
					heap.setHeap(next - HEADER_SIZE, prev);
					
					// if prev is 0, then it points to root
					if (prev == 0) {
						heap.setHeap(prev, next);
					// Change the next pointer from previous block to point to the new payload
					} else if (prev != 0) {
						// Prev will take you back to the prev ptr or prev block, add 1 to get its next
						heap.setHeap(prev + HEADER_SIZE, next);	
					}
				}
				
			}
			
			
		}
		
		return payloadPtr;
	}
	
	public int firstFit(int blckSize) {
		int headerValue;
		int headerIndex;
		int payloadPtr;
		
		// If explicit, grab first header from root
		if (Heap.getImplicitOrExplicit() == EXPLICIT) {
			headerIndex = heap.getHeap(ROOT) - 2 * HEADER_SIZE;
		// First header starts at 1s
		} else {
			headerIndex = 1;			
		}
		
		// Initialize payloadPtr
		payloadPtr = -1;
				
		// Go through heap, start at second word
		while (headerIndex > 0 && headerIndex < heap.getSize() - 1) {
			// Get header value
			headerValue = heap.getHeap(headerIndex);
			
			// When i = 1 is free and its size is 
			if (headerValue % 2 == FREE && (headerValue - FREE) >= blckSize) {
				// Split if necessary, if -1, then did not allocate but used sbrk
				return split(headerIndex, blckSize);		
			// If block is allocated	
			} else {
				
				// If Explicit grab the next header in free list
				if (Heap.getImplicitOrExplicit() == EXPLICIT) {
					headerIndex = heap.getHeap(headerIndex + 2 * HEADER_SIZE) - 2 * HEADER_SIZE;
				// If not explicit grab the next header in memory
				} else {
					// Next header
					headerIndex += headerValue % 2 == FREE ? headerValue - FREE : headerValue;					
				}
			}
		}
		
		// If no space left to fit block in the heap, allocate more
		if (payloadPtr == -1) {
			do {	
				headerIndex = mysbrk(SBRK);
			} while ((headerValue = heap.getHeap(headerIndex)) < blckSize);
			return split(headerIndex, blckSize);
		}
		
		
		
		return -1;
	}
	
	public int bestFit(int blckSize) {
		int headerValue;
		int headerIndex;
		int minSize;
		int minIndex;

		// Set minSize to a large number
		minSize = Integer.MAX_VALUE;
		
		// If explicit, grab first header from root
		if (Heap.getImplicitOrExplicit() == EXPLICIT) {
			headerIndex = heap.getHeap(ROOT) - 2 * HEADER_SIZE;
		// First header starts at 1s
		} else {
			headerIndex = 1;			
		}
		
		// Initialize variables
		headerValue = -1;
		minIndex = -1;
		
		// Go through heap, start at second word
		while (headerIndex > 0 && headerIndex < heap.getSize() - 1) {
			// Get header value
			headerValue = heap.getHeap(headerIndex);
			
			// When i = 1 is free and its size is 
			if (headerValue % 2 == FREE && (headerValue - FREE) >= blckSize && (headerValue - FREE) < minSize) {
				minSize = headerValue - FREE;
				minIndex = headerIndex;
			}
			
			// If Explicit grab the next header in free list
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				headerIndex = heap.getHeap(headerIndex + 2 * HEADER_SIZE) - 2 * HEADER_SIZE;
			// If not explicit grab the next header in memory
			} else {
				// Next header
				headerIndex += headerValue % 2 == FREE ? headerValue - FREE : headerValue;					
			}
		}
		
		// Call with the headerIndex that has the best fit block
		if (minIndex != -1 && minSize != Integer.MAX_VALUE) {
			return split(minIndex, blckSize);
		// If not blocks suitable
		} else if (minIndex == -1) {
			// Call sbrk as many times as needed
			do {	
				headerIndex = mysbrk(SBRK);
			} while ((headerValue = heap.getHeap(headerIndex)) < blckSize);
			return split(headerIndex, blckSize);
		}
		
		return -1;
	}
	
	public int mymalloc(int size) throws Exception {
		// Declaring variables
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
		
		// Best fit
		if (Heap.getFirstOrBestFit() == BEST) {
			payloadPtr = bestFit(size); 
		// First fit
		} else {
			payloadPtr = firstFit(size);
		}
		
		return payloadPtr;
	}

	
	/**
	 * Coalescence function that joins two adjacent
	 * free blocks together.
	 * In case of explicit lists, uses LIFO policy
	 * insert the new freed blocks.
	 * 
	 * @param blckPtr
	 * @param size
	 * @return
	 */
	public void coalesce(int blckPtr, int size) {
		// Declaring variables
		int nextBlckPtr;
		int prevBlckPtr;
		
		int nextBlckVal;
		int prevBlckVal;
		
		int tempPrev;
		int tempNext;
		
		int newBlckPtr = 0;
		int rootVal;
		
		
		// Get next block header
		nextBlckPtr = blckPtr + size;

		// Get prev block footer
		prevBlckPtr = blckPtr - HEADER_SIZE;
		
		// Get next block header
		nextBlckPtr = blckPtr + size;

		// Get next block value
		nextBlckVal = heap.getHeap(nextBlckPtr);
				
		// Get prev block footer
		prevBlckPtr = blckPtr - HEADER_SIZE;
		
		// Get prev block value
		prevBlckVal = heap.getHeap(prevBlckPtr);
		
		// Get prev block header
		prevBlckPtr = prevBlckPtr - (prevBlckVal - FREE) + HEADER_SIZE;
		
		// Get root's next
		rootVal = heap.getHeap(ROOT);
		
		// If no coalescence needed
		if (nextBlckVal % 2 != FREE && prevBlckVal % 2 != FREE) {
			
			// Return the same block
			newBlckPtr = blckPtr;
			
			// If explicit heap
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Insert right after root
				// Point current's next to be root's next
				heap.setHeap(newBlckPtr + 2 * HEADER_SIZE, rootVal);
				
				// Point root to this block
				heap.setHeap(ROOT, newBlckPtr + 2 * HEADER_SIZE);
				
				// Point prev of current block to root
				heap.setHeap(newBlckPtr + HEADER_SIZE, ROOT);
				
				// Point prev of root's next to current's prev
				if (rootVal != -1) {
					heap.setHeap(rootVal - 1, newBlckPtr + HEADER_SIZE);					
				}
			}
			
		// Coalescence with next only
		} else if (nextBlckVal % 2 == FREE && prevBlckVal % 2 != FREE) {
			// Explicit: SPLICE
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Assuming current = next
				// Get previous of current
				tempPrev = heap.getHeap(nextBlckPtr + HEADER_SIZE);
				tempNext = heap.getHeap(nextBlckPtr + 2 * HEADER_SIZE);
				
				// Point previous' next to current's next, if prev is not root
				if (tempPrev != 0) {
					heap.setHeap(tempPrev + HEADER_SIZE, tempNext);
				// If prev is root	
				} else {
					// Point root's next to current's next
					heap.setHeap(tempPrev, tempNext);
				}
				
				// Point next's prev to current's prev, if next exists i.e. !(-1)
				if (tempNext != -1) {
					heap.setHeap(tempNext - HEADER_SIZE, tempPrev);
				}
			}
			
			// EXPLICIT: COALESCE
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Remove the old pointers
				heap.setHeap(nextBlckPtr + HEADER_SIZE, 1);
				heap.setHeap(nextBlckPtr + 2 * HEADER_SIZE, 1);
			}
			// Set the size of the current block's header
			heap.setHeap(blckPtr, nextBlckVal + size);
			
			// Reset the size of the current block's footer
			heap.setHeap(blckPtr + size - FOOTER_SIZE, 1);
			
			// Reset the size of the next block's header
			heap.setHeap(nextBlckPtr, 1);
			
			// Set the size of the next blocks's footer
			heap.setHeap(nextBlckPtr + (nextBlckVal - FREE) - FOOTER_SIZE, nextBlckVal + size);
			
			// New block
			newBlckPtr = blckPtr;
			
			// EXPLICIT: INSERT AT ROOT
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Get root's next
				rootVal = heap.getHeap(ROOT);
				
				// Insert right after root
				// Point current's next to be root's next
				heap.setHeap(newBlckPtr + 2 * HEADER_SIZE, rootVal);
				
				// Point root to this block
				heap.setHeap(ROOT, newBlckPtr + 2 * HEADER_SIZE);
				
				// Point prev of current block to root
				heap.setHeap(newBlckPtr + HEADER_SIZE, ROOT);
				
				// Point prev of root's next to current's prev
				if (rootVal != -1) {
					heap.setHeap(rootVal - 1, newBlckPtr + HEADER_SIZE);					
				}
			}
			
		// Coalescence with prev only
		} else if (nextBlckVal % 2 != FREE && prevBlckVal % 2 == FREE) {
			// Explicit: SPLICE
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Assuming current = prev
				// Get previous of current
				tempPrev = heap.getHeap(prevBlckPtr + HEADER_SIZE);
				tempNext = heap.getHeap(prevBlckPtr + 2 * HEADER_SIZE);
				
				// Point previous' next to current's next, if prev is not root
				if (tempPrev != 0) {
					heap.setHeap(tempPrev + HEADER_SIZE, tempNext);
				// If prev is root	
				} else {
					// Point root's next to current's next
					heap.setHeap(tempPrev, tempNext);
				}
				
				// Point next's prev to current's prev, if next exists i.e. !(-1)
				if (tempNext != -1) {
					heap.setHeap(tempNext - HEADER_SIZE, tempPrev);
				}
			}
			
			// EXPLICIT: COALESCE
			// Set the size of the prev block's footer
			heap.setHeap(prevBlckPtr + (prevBlckVal - FREE) - FOOTER_SIZE, 1);
			
			// Reset the size of the prev block's header
			heap.setHeap(prevBlckPtr, prevBlckVal + size);
			
			// Reset the size of the current block's header
			heap.setHeap(blckPtr, 1);
			
			// Set the size of the current blocks's footer
			heap.setHeap(blckPtr + size - FOOTER_SIZE, prevBlckVal + size);
			
			// New block
			newBlckPtr = prevBlckPtr;
			
			// EXPLICIT: INSERT AT ROOT
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Get root's next
				rootVal = heap.getHeap(ROOT);
				
				// Insert right after root
				// Point current's next to be root's next
				heap.setHeap(newBlckPtr + 2 * HEADER_SIZE, rootVal);
				
				// Point root to this block
				heap.setHeap(ROOT, newBlckPtr + 2 * HEADER_SIZE);
				
				// Point prev of current block to root
				heap.setHeap(newBlckPtr + HEADER_SIZE, ROOT);
				
				// Point prev of root's next to current's prev
				if (rootVal != -1) {
					heap.setHeap(rootVal - 1, newBlckPtr + HEADER_SIZE);					
				}
			}
			
		// Coalescence with both prev and next
		} else {
			// Explicit: SPLICE
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Assuming current = prev
				// Get previous of current
				tempPrev = heap.getHeap(prevBlckPtr + HEADER_SIZE);
				tempNext = heap.getHeap(prevBlckPtr + 2 * HEADER_SIZE);
				
				// Point previous' next to current's next, if prev is not root
				if (tempPrev != 0) {
					heap.setHeap(tempPrev + HEADER_SIZE, tempNext);
				// If prev is root	
				} else {
					// Point root's next to current's next
					heap.setHeap(tempPrev, tempNext);
				}
				
				// Point next's prev to current's prev, if next exists i.e. !(-1)
				if (tempNext != -1) {
					heap.setHeap(tempNext - HEADER_SIZE, tempPrev);
				}
				
				// Assuming current = next
				// Get previous of current
				tempPrev = heap.getHeap(nextBlckPtr + HEADER_SIZE);
				tempNext = heap.getHeap(nextBlckPtr + 2 * HEADER_SIZE);
				
				// Point previous' next to current's next, if prev is not root
				if (tempPrev != 0) {
					heap.setHeap(tempPrev + HEADER_SIZE, tempNext);
				// If prev is root	
				} else {
					// Point root's next to current's next
					heap.setHeap(tempPrev, tempNext);
				}
				
				// Point next's prev to current's prev, if next exists i.e. !(-1)
				if (tempNext != -1) {
					heap.setHeap(tempNext - HEADER_SIZE, tempPrev);
				}
			}
			
			// EXPLICIT: COALESCE
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Remove the old pointers
				heap.setHeap(nextBlckPtr + HEADER_SIZE, 1);
				heap.setHeap(nextBlckPtr + 2 * HEADER_SIZE, 1);
			}
			// Reset the size of the prev block's header
			heap.setHeap(prevBlckPtr, (prevBlckVal - FREE) + size + nextBlckVal);			
			
			// Set the size of the prev block's footer
			heap.setHeap(prevBlckPtr + (prevBlckVal - FREE) - FOOTER_SIZE, 1);
			
			// Reset the size of the current block's header
			heap.setHeap(blckPtr, 1);
			
			// Set the size of the current blocks's footer
			heap.setHeap(blckPtr + size - FOOTER_SIZE, 1);		
			
			// Reset the size of the next block's header
			heap.setHeap(nextBlckPtr, 1);
			
			// Set the size of the next blocks's footer
			heap.setHeap(nextBlckPtr + (nextBlckVal - FREE) - FOOTER_SIZE, prevBlckVal + size + (nextBlckVal - FREE));
			
			// New block
			newBlckPtr = prevBlckPtr;
			// EXPLICIT: INSERT AT ROOT
			if (Heap.getImplicitOrExplicit() == EXPLICIT) {
				// Get root's next
				rootVal = heap.getHeap(ROOT);
				
				// Insert right after root
				// Point current's next to be root's next
				heap.setHeap(newBlckPtr + 2 * HEADER_SIZE, rootVal);
				
				// Point root to this block
				heap.setHeap(ROOT, newBlckPtr + 2 * HEADER_SIZE);
				
				// Point prev of current block to root
				heap.setHeap(newBlckPtr + HEADER_SIZE, ROOT);
				
				// Point prev of root's next to current's prev
				if (rootVal != -1) {
					heap.setHeap(rootVal - 1, newBlckPtr + HEADER_SIZE);					
				}
			}
		}
	}
	
	public boolean myfree(int ptr) {
		int newBlckPtr;
		int blckPtr;
		int size;
		int curr;
		
		if (ptr < 0) {
			return false;
		}
		// Get start of memory block index
		blckPtr = pointer[ptr] - HEADER_SIZE;
		
		// Get the size of the current block
		size = heap.getHeap(blckPtr);
		
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
	
	public int mysbrk(int size) {
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
			// If footer is allocated, new header will be created later
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
				
				newHeaderIndex = oldHeaderIndex;
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
			return newHeaderIndex;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return -1;
		}
		
	}
	
	/**
	 * Used to round the numbers to the nearest multiple of 8
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
