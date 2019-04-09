package malloc;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FileIO {
	private Scanner kbrd;
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
	
	public void writeFile() {
		
	}
	

}
