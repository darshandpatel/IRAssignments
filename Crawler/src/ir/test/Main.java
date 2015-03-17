package ir.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
 
public class Main {
	public static void main(String[] args) {
        try {
            URL google = new URL("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");
            BufferedReader in = new BufferedReader(new InputStreamReader(google.openStream()));
            String inputLine; 
            StringBuilder strBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                // Process each line.
            	strBuilder.append(inputLine);
            	Document doc = Jsoup.parse(inputLine);
        		String text = doc.body().text(); // "An example link"
        		if(!text.equals("")){
        			System.out.println(text);
        		}
        		
            }
            in.close(); 
            
            
 
        } catch (MalformedURLException me) {
            System.out.println(me); 
 
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }//end main
}