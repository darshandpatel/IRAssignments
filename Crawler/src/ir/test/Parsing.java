package ir.test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Parsing {

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
