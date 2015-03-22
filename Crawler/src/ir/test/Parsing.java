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

			// Drop starting '/../'
			while (path.startsWith("/../")) {
				path = path.substring(3);
			}

             
			path = path.trim();

			if (path.length() == 0) {
				path = "/" + path;
			}

			// remove port
			int port = canonicalURL.getPort();
			if (port == canonicalURL.getDefaultPort()) {
				port = -1;
			}
			String protocol = canonicalURL.getProtocol().toLowerCase();
			String host = canonicalURL.getHost().toLowerCase();
			
			if(path.equals("/"))
				path = "";
			String paths = path;

            return new URL(protocol, host, port, paths);

		} catch (MalformedURLException ex) {
			return null;
		} catch (URISyntaxException ex) {
			return null;
		}
	}
}
