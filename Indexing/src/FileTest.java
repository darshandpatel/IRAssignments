import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class FileTest {
	
	public static void testByteBuffer(){
		RandomAccessFile file;
		try {
			int abc= 1000000;
			String test = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/dataTEst.dat";
			FileOutputStream fos = new FileOutputStream(test);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.write((new String("term") + " ").getBytes());
			dos.writeInt(12343);
			dos.write((new String(":").getBytes()));
			dos.writeInt(1);
			dos.write((new String(":").getBytes()));
			dos.writeInt(6);
			
			dos.close();
			fos.close();
			
			
			FileInputStream inputStream = new FileInputStream(test);
			FileChannel inChannel = inputStream.getChannel();
			int offsetDiff = 25;
			ByteBuffer buf = ByteBuffer.allocate(offsetDiff);
			inChannel.position(0);
			
			
			
		    inChannel.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void readTestFile(){
		try{
			String test = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWOStopWithStemmed.txt";

			RandomAccessFile file;
			file = new RandomAccessFile(test, "r");
			file.seek(109405764);
			System.out.println(file.readLine());

			file.close();
		}catch(FileNotFoundException e){
			
		}catch(IOException e){
			
		}
	}
	
	public static void main(String args[]){
		
		    FileTest.readTestFile();

	}

}
