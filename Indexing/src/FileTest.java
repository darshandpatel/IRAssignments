import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;


public class FileTest {
	
	public static void main(String args[]){
		RandomAccessFile file;
		try {
			String test = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWOStepWO.txt";
			
			/*
			testWriter.print("Hello");
			testWriter.
			testWriter.print(abc);
			testWriter.close();
			*/
			file = new RandomAccessFile(test, "r");
			System.out.println(file.getFilePointer());
			file.seek(22);
			System.out.println(file.readLine());

			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
