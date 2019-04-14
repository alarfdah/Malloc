package malloc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class FileIO {
	private static final String OUTPUT = "output.txt";
	
	private Scanner kbrd;
	private PrintWriter print;
	private String fileName;
	
	
	public FileIO(String fileName) {
		this.setFileName(fileName);
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> readFile(List<String> content) {
		try {
			kbrd = new Scanner(new File(getFileName()));
			
			while (kbrd.hasNext()) {
				content.add(kbrd.nextLine());
			}
			kbrd.close();
			return content;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Reference: https://www.baeldung.com/java-write-to-file
	 */
	public void writeFile(int heap[]) {
		try {
			int i;
			print = new PrintWriter(new FileWriter(OUTPUT));
			for (i = 0; i < heap.length; i++) {
				print.println(i + ", " + String.format("0x%08X", heap[i]));				
			}
			print.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	

}
