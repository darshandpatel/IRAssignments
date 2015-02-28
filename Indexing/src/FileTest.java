import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
	
	public static void main(String args[]){
		RandomAccessFile file;
		try {
			int abc= 1000000;
			String test = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/number.dat";
			FileOutputStream fos = new FileOutputStream(test);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(abc);
			dos.writeInt(abc);
			dos.write((new String("Hello")).getBytes());
			dos.close();
			fos.close();
			
			
			FileInputStream inputStream = new FileInputStream(test);
			FileChannel inChannel = inputStream.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(8);
			inChannel.read(buf);
			
			
		    System.out.println(buf.getInt(4));
		    /*
			PrintWriter testWriter = new PrintWriter(new BufferedWriter(new FileWriter(test, false)));
			
			//testWriter.print("Hello");
			testWriter.write(abc);
			testWriter.close();
			
			
			file = new RandomAccessFile(test, "r");
			System.out.println(file.getFilePointer());
			file.seek(0);
			System.out.println(file.readInt());

			file.close();
			*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
