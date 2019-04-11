package malloc;

import java.util.Scanner;

public class UserInput {
	private Scanner kbrd;
	
	public UserInput() {
		
	}
	
	public String[] getUserInput() {
		// Declaring variables
		String input[] = new String[3];
		kbrd = new Scanner(System.in);
		
		do {
			System.out.println("Please input the name of the inputfile. Example: 'in.txt'");
			input[0] = kbrd.nextLine();
		} while (input[0].isEmpty());
		
		System.out.println();
		
		do {
			System.out.println("Please choose the type of list:");
			System.out.println("1. Implicit List");
			System.out.println("2. Explicit List");
			input[1] = kbrd.nextLine();
		} while (input[1].isEmpty());
		
		do {
			System.out.println("Please choose the type of search:");
			System.out.println("1. First-fit");
			System.out.println("2. Best-fit");
			input[2] = kbrd.nextLine();
		} while (input[2].isEmpty());
		
		return input;
	}
}
