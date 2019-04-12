package malloc;

import java.util.ArrayList;
import java.util.List;

public class Driver {

	public static void main(String[] args) {
		// Declarations
		FileIO fileIO;
		Allocate alloc;
		String input[]; // 0: File name, 1: Implicit or Explicit, 2. First or Best fit
		List<String> content = new ArrayList<>();
		int pointers[] = new int[1000];
		
		// UserInput
		UserInput ui = new UserInput();
//		input = ui.getUserInput();
		
		// File IO
//		fileIO = new FileIO(input[0]);
		fileIO = new FileIO("input.txt");
		fileIO.readFile(content);
		
		// Set Implicit or Explicit
//		Heap.setImplicitOrExplicit(Integer.parseInt(input[1]));
		Heap.setImplicitOrExplicit(2);
		
		// Set First or Best fit
//		Heap.setFirstOrBestFit(Integer.parseInt(input[2]));
		Heap.setFirstOrBestFit(1);
		
		
		// Process input
		try {
			alloc = new Allocate(pointers);
			alloc.process(content);		
			alloc.printHeap();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Allocation is successful!");
		
	}

}
