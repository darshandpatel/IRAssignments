package ir.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlerSimpleTest {

	private final String USER_AGENT = "Mozilla 5.0";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String link = "http://www.immigrationpolicy.org/sites/default/files/docs/pdf/DeRomanticizing11-25-08.";
		System.out.println(link.contains(".pdf"));
		HashMap<String,String> abc = new HashMap<String,String>();
		abc.put("abc1", "def1");
		abc.put("abc2", "def2");
		
		HashMap<String,String> def = new HashMap<String,String>();
		def.putAll(abc);
		abc = new HashMap<String,String>();
		System.out.println(def.size());
		System.out.println(abc.size());
		
		
		URL url = new URL("http://en.wikipedia.org/wiki/Immigration_to_the_United_States");
		System.out.println(url.getHost());
		Document doc = Jsoup.connect("http://en.wikipedia.org/wiki/Immigration_to_the_United_States").get();
		Response responce = Jsoup.connect("http://en.wikipedia.org/wiki/Immigration_to_the_United_States").ignoreHttpErrors(true).timeout(10000).execute();
		
		
		String title = doc.title();
		System.out.println(title);
		String rawHTML = doc.toString();
		String lines[] = rawHTML.split("\n");
		ArrayList<String> cleanedPageContent = new ArrayList<String>();
		//for(String line : lines){
		String parsedLine = Jsoup.parse(rawHTML).body().text();
			//cleanedPageContent.append(parsedLine);
		/*	
		if (!parsedLine.equals(""))
				//cleanedPageContent.append(parsedLine);
				cleanedPageContent.add(parsedLine);
		}
		int length = cleanedPageContent.size();
		for(int i=0;i<length;i++){
			System.out.println(cleanedPageContent.get(i));
		}
		*/
		System.out.println(parsedLine);
		
		/*
		while ((inputLine = in.readLine()) != null) {
            // Process each line.
        	strBuilder.append(inputLine);
        	Document doc = Jsoup.parse(inputLine);
    		String text = doc.body().text(); // "An example link"
    		if(!text.equals("")){
    			System.out.println(text);
    		}
    		
        }
       
		
		Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
        System.out.println("\nImports: "+  links.size());
        for (Element link : links) {
        	 System.out.println(link.attr("abs:href"));
        	 System.out.println(getCanonicalizedForm(link.attr("abs:href")));
        }
         */
	}
	
	public static String getCanonicalizedForm(String url) {
		URL canonicalURL = getCanonicalURL(url);
		if (canonicalURL != null) {
			return canonicalURL.toExternalForm();
		}
		return null;
	}

	public static URL getCanonicalURL(String href) {
	
	       try {
	                URL canonicalURL;
	              
	              canonicalURL = new URL(href);
	              
	               String path = canonicalURL.getPath();
	               path = new URI(path).normalize().toString();
	                
					// Convert '//' -> '/'
	               int idx = path.indexOf("//");
	               while (idx >= 0) {
	                       path = path.replace("//", "/");
	                       idx = path.indexOf("//");
	               }
	                //Drop starting '/../'
	               while (path.startsWith("/../")) {
	                       path = path.substring(3);
	               }
	                //trim
	               path = path.trim();
	                
	                //Add starting slash if needed
	               if (path.length() == 0) {
	                       path = "/" + path;
	               }
	                
	               //Drop default port: example.com:80 -> example.com?
	               int port = canonicalURL.getPort();
	               if (port == canonicalURL.getDefaultPort()) {
	                       port = -1;
	               }
	                /*
	                * Lowercasing protocol and host
	                */
	               String protocol = canonicalURL.getProtocol().toLowerCase();
	               String host = canonicalURL.getHost().toLowerCase();
	               String paths = normalizePath(path);
	               return new URL(protocol, host, port, paths);
	       } catch (MalformedURLException ex) {
	               return null;
	       } catch (URISyntaxException ex) {
	               return null;
	       }
	}
	
	private static String normalizePath(final String path) {
		if(path.equals("/"))
			return "";
		else
			return path.replace("%7E", "~").replace(" ", "%20");
	}
}
