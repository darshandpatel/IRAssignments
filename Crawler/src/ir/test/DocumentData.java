package ir.test;

import java.util.ArrayList;
import java.util.HashMap;

public class DocumentData {
	
	String id;
	String url;
	String title;
	HashMap<String,String> headers;
	String rawHTML;
	ArrayList<String> cleanedHTML;
	StringBuilder outLinks;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	public String getRawHTML() {
		return rawHTML;
	}
	public void setRawHTML(String rawHTML) {
		this.rawHTML = rawHTML;
	}
	public ArrayList<String> getCleanedHTML() {
		return cleanedHTML;
	}
	public void setCleanedHTML(ArrayList<String> cleanedHTML) {
		this.cleanedHTML = cleanedHTML;
	}
	public StringBuilder getOutLinks() {
		return outLinks;
	}
	public void setOutLinks(StringBuilder outLinks) {
		this.outLinks = outLinks;
	}
	

}
