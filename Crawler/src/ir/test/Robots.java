package ir.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class Robots {

	public static String blackListedDomain[] = {"wikimediafoundation.org","shop.wikimedia.org","www.mediawiki.org","wiki/Wikipedia","wiki/Help","donate.wikimedia.org","wiki/Portal","wiki/Special","wiki.creativecommons.org","www.questia.com","mailto","sitemap","tools"};
	
	public static BaseRobotRules getBaseRobotRules(String domain){
		
		try{
			URL url = new URL(domain+"/robots.txt");
	        byte[] array = IOUtils.toByteArray(new InputStreamReader(url.openStream()));
	        
	        SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
	        BaseRobotRules br = parser.parseContent(domain+"/robots.txt", array,"text/plain", "");
	        //System.out.println(br.getCrawlDelay());
	        return br;
		}catch(Exception e){
			System.out.println("Robot.txt doesn't exist");
			return null;
		}
	}
	
	public static int getCrawlerDelay(String domain){
		
		try{
			URL url = new URL(domain+"/robots.txt");
	        byte[] array = IOUtils.toByteArray(new InputStreamReader(url.openStream()));
	        
	        SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
	        BaseRobotRules br = parser.parseContent(domain+"/robots.txt", array,"text/plain", "");
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


}
