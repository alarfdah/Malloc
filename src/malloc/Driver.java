package malloc;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 04/14/2019
 * Class: CS5541
 * Assignment: Assignment 4 - Malloc
 * Author: Ahmed Radwan
 * Email: ahmedabdelwaha.radwan@wmich.edu
 */
public class Driver {

	public static void main(String[] args) {
		// Declarations
		FileIO fileIO;
		Allocate alloc;
		Heap heap;
		String input[]; // 0: File name, 1: Implicit or Explicit, 2. First or Best fit
		List<String> content = new ArrayList<>();
		int pointers[] = new int[1000];
		
		// UserInput
		UserInput ui = new UserInput();
		input = ui.getUserInput();
		
		// File IO
		fileIO = new FileIO(input[0]);
		fileIO.readFile(content);
		
		// Set Implicit or Explicit
		Heap.setImplicitOrExplicit(Integer.parseInt(input[1]));
		
		// Set First or Best fit
		Heap.setFirstOrBestFit(Integer.parseInt(input[2]));
		
		// Process input
		try {
			heap = new Heap();
			alloc = new Allocate(heap, pointers);
			alloc.process(content);		
			fileIO.writeFile(heap.getHeap());
			heap.printHeap();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Done.");
		
	}

}
