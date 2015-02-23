import java.util.ArrayList;


public class Test {

	public static void main(String[] args) {
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
		

	}

}
