package malloc;

import java.util.ArrayList;
import java.util.List;

public class Driver {

	public static void main(String[] args) {
		// Declarations
		FileIO fileIO;
		Allocate alloc;
		List<String> content = new ArrayList<>();
		int pointers[] = new int[1000];
		
		
		// File IO
		fileIO = new FileIO("./input.txt");
		fileIO.readFile(content);
		
		// Process input
		try {
			alloc = new Allocate(pointers);
			alloc.process(content);			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Allocation is successful!");
		
	}

}
