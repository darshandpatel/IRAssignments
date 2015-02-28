import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Test {

	public static void main(String[] args) {
		
		String abcd = "Hello#buddy#";
		String[] text = abcd.split("#");
		System.out.println("Number of parts are :"+text.length);
		for (String a : text){
			System.out.println(a);
		}
		
		// TODO Auto-generated method stub
		DefaultHashMap<String, ArrayList<String>> abc = new DefaultHashMap<String, ArrayList<String>>(new ArrayList<String>());
		abc.put("hello", new ArrayList<String>());
		abc.get("hello").add("hello2");
		System.out.println(abc.get("hello"));
		
		Short s = new Short((short) 10);
		System.out.println(s);
		System.out.println(s+100);
		
		byte[] buffer = "Help I am trapped in a fortune cookie factory\n".getBytes();
		
		
		System.out.println(new String(buffer));
		final String string = "Hello World ";
		Short x = 100;
		
		ByteBuffer buffer1 = ByteBuffer.allocate("Helll343o".length()+2);
		buffer1.putShort(new Short((short)12));
		buffer1.put("Helll343o".getBytes());
		System.out.println(buffer1);
		// Check encoded sizes
		byte[] utf8Bytes;
		utf8Bytes = string.getBytes();
		System.out.println(utf8Bytes.length); // prints "11
		
		
		

	}

}
