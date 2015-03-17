/**
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sagar6903
 *
 */
public class Test {
	 
	


	static int i = 0;
	static File collection = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
	static HashMap<String, Integer> tokenM = new HashMap<String, Integer>();
	static ArrayList<String> lstStopwords = new ArrayList<String>();
	
	public static void main(String[] str) throws IOException{
		
		/*
		tokenM.put("A", 5);
		tokenM.put("A", tokenM.get("A").intValue()+1);
		
		System.out.println(tokenM);
		getStopWords();
		File[] directoryListing = collection.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				System.out.println("Reading : " + ++i + " : " + child.getName());
				fetchContentBuilders(child);
			}
		}
		System.out.println("Token Count : " + tokenM.size());
		*/
		parseDocText(null,null);
	}
		
		public static void fetchContentBuilders(File input) throws IOException {

			String line = "";
			String docNumber = "";
			StringBuilder text = null;
			boolean isText = false;
			
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new FileReader(input));

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("<DOCNO>")) {
						if (text != null) {
							
							if(!text.toString().equals(" "))
								parseDocText(docNumber, text.toString());
							text = null;
						}

						docNumber = line.substring(line.indexOf("<TEXT>") + 7,
								line.indexOf("</DOCNO>") - line.indexOf("<TEXT>")).trim();

					} else if (line.startsWith("<TEXT>")) {
						isText = true;
					} else if (line.startsWith("</TEXT>")) {
						isText = false;
					} else {
						if (isText){
							if (text == null)
								text = new StringBuilder();
							text.append(" " + line);
						}
					}
				}
				if (text != null) {
					if(!text.toString().equals(" "))
						parseDocText(docNumber, text.toString());
					text = null;
				}
				
			} catch (IOException e) {

			} finally {
				reader.close();
			}
		}

		public static void parseDocText(String docNumber, String inputText) throws IOException{
			inputText = "permitted a somewhat personal response to the vice president's";
			Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
			Matcher matcher = pattern.matcher(inputText);
			while (matcher.find()) {
				System.out.println(matcher.group());
				/*
				String token = inputText.substring(matcher.start(), matcher.end());
				token = token.replaceAll("\\.$", "");
				token = token.toLowerCase();
				
				if(!lstStopwords.contains(token))
					tokenM.put(token, 0);
				*/
				
			}
		}
		
		public static ArrayList<String> getStopWords() throws IOException {

			BufferedReader reader;
			File stopWordsFile = new File("/Users/Pramukh/Documents/Information Retrieval Data/HW2/stoplist.txt");
			reader = new BufferedReader(new FileReader(stopWordsFile));
			String line = "";
			while ((line = reader.readLine()) != null) {
				lstStopwords.add(line.toLowerCase());
			}
			reader.close();
			
			return lstStopwords;
		}
}
