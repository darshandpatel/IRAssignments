package ir.test;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Test {

	public static void main(String args[]){
		HashMap<String,Integer> frontier = new LinkedHashMap<String,Integer>();
		frontier.put("A",7);
		frontier.put("K",33);
		frontier.put("J",33);
		frontier.put("E",19);
		frontier.put("D",33);
		frontier.put("B",33);
		frontier.put("L",33);
		frontier.put("M",33);
		
		String url = Crawler.getURLWithMaxInlineCounts(frontier);
		System.out.println(url);
		HashMap<String,StringBuilder> check = new LinkedHashMap<String,StringBuilder>();
		StringBuilder abc = new StringBuilder();
		abc.append("Hello ");
		check.put("test", abc);
		check.get("test").append("World");
		System.out.println(check.get("test"));
		
	}
}
