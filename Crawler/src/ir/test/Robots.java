package ir.test;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;
import crawlercommons.robots.SimpleRobotRulesParser;

public class Robots {

	public static String blackListedDomain[] = {"wikimediafoundation.org","shop.wikimedia.org","www.mediawiki.org","wiki/Wikipedia","wiki/Help","donate.wikimedia.org","wiki/Portal","wiki/Special","wiki.creativecommons.org","www.questia.com","mailto","sitemap","tools"};
	
	public static BaseRobotRules getBaseRobotRules(URL urlObj){
		
		BaseRobotRules rules = null;
		try{
			
			String hostId = urlObj.getProtocol() + "://" + urlObj.getHost()
	                + (urlObj.getPort() > -1 ? ":" + urlObj.getPort() : "");
			
			URL newUrlObj = new URL(hostId);
			
	        //byte[] array = IOUtils.toByteArray(new InputStreamReader(newUrlObj.openStream()));
	        
	        HttpGet httpget = new HttpGet(hostId + "/robots.txt");
		    HttpContext context = new BasicHttpContext();
		    
		    HttpClient httpClient = new DefaultHttpClient();
		    HttpResponse response = httpClient.execute(httpget, context);
		    
		    if (response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 404) {
		        rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
		        rules.setCrawlDelay(1000);
		        // consume entity to deallocate connection
		        EntityUtils.consume(response.getEntity());
		    } else {
		        BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
		        SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
		        rules = robotParser.parseContent(hostId, IOUtils.toByteArray(entity.getContent()),
		                "text/plain", "Mozilla 5.0");
		    }
	        
	        //System.out.println("Check 2");
	        //SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
	        //BaseRobotRules br = parser.parseContent(hostId, array,"text/plain","Mozilla 5.0");
	        
	        //System.out.println(br.getCrawlDelay());
	        return rules;
		}catch(Exception e){
			System.out.println("Robot.txt doesn't exist");
			rules = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);
	        rules.setCrawlDelay(1000);
			return rules;
		}
	}
	
	public static int getCrawlerDelay(String domain){
		
		try{
			URL url = new URL("http://"+domain+"/robots.txt");
	        byte[] array = IOUtils.toByteArray(new InputStreamReader(url.openStream()));
	        
	        SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
	        BaseRobotRules br = parser.parseContent(domain+"/robots.txt", array,"text/plain", "Mozilla 5.0");
	        if ((int) br.getCrawlDelay() == 0){
	        	return 1000;
	        }else
	        	return (int) br.getCrawlDelay();
		}catch(Exception e){
			
			return 1000;
		}
	}	
	
	public static boolean isBlackListedURL(String url){
		
		int size = Robots.blackListedDomain.length;
		for(int i=0;i<size;i++){
			if(url.contains(Robots.blackListedDomain[i]))
				return true;
		}
		return false;
	}
	
	public static void main(String args[]){
		String domain = "http://books.google.com/books/";
		
		URL url;
		try {
			url = new URL(domain);
			BaseRobotRules br = Robots.getBaseRobotRules(url);
			System.out.println(br.isAllowed("http://books.google.com/books/"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
	}


}
