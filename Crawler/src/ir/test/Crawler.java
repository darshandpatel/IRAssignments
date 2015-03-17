package ir.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawlercommons.robots.BaseRobotRules;

public class Crawler {
	
	public static String getURLWithMaxInlineCounts(HashMap<String,Integer> frontier){
		
		String key = Collections.max(frontier.entrySet(), new Comparator<Entry<String,Integer>>() {
			public int compare(Entry<String,Integer> e1,Entry<String,Integer> e2){
				if(e1.getValue() > e2.getValue())
					return 1;
				else
					return -1;
			}
		}).getKey();
		//System.out.println(key+" with value "+frontier.get(key));
		return key;
	}
	
	public static void crawling(){
		
		HashMap<String,Integer> frontier = new LinkedHashMap<String,Integer>();
		frontier.put("http://en.wikipedia.org/wiki/Immigration_to_the_United_States",Integer.MAX_VALUE-3);
		//frontier.put("http://en.wikipedia.org/wiki/History_of_immigration_to_the_United_States",Integer.MAX_VALUE);
		
		//frontier.put("http://connection.ebscohost.com/us/immigration-restrictions/current-immigration-laws-us",Integer.MAX_VALUE-1);
		//frontier.put("http://www.washingtonexaminer.com/obamas-immigration-order-appears-destined-for-the-supreme-court/article/2561510",Integer.MAX_VALUE-1);
		frontier.put("https://www.whitehouse.gov/issues/immigration",Integer.MAX_VALUE);
		frontier.put("http://www.timetoast.com/timelines/78852",Integer.MAX_VALUE-1);
		frontier.put("http://www.uscis.gov/news-releases",Integer.MAX_VALUE-2);
		
		
		//frontier.put("http://academic.udayton.edu/race/02rights/immigr01.htm",Integer.MAX_VALUE);
		//frontier.put("http://abcnews.go.com/ABC_Univision/News/23-defining-moments-immigration-policy-history/story?id=17810440",Integer.MAX_VALUE);
		
		HashMap<String,Integer> visitedDomains = new HashMap<String,Integer>();
		HashMap<String,Boolean> visitedURL = new HashMap<String,Boolean>();
		HashMap<String,BaseRobotRules> domainRobotRules = new HashMap<String,BaseRobotRules>();
		HashMap<String,StringBuilder> urlInlinks = new HashMap<String,StringBuilder>();
		BaseRobotRules baseRobotRules = null;
		try {
			int linkCount = 0;
			
			while(frontier.size() != 0){
				
				String url = getURLWithMaxInlineCounts(frontier);
				//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				//Calendar cal = Calendar.getInstance();
				
				//Thread.sleep(1500);
				//System.out.println(dateFormat.format(cal.getTime()));
				System.out.println("Selected URL is "+url+" with inlinks "+frontier.get(url));
				String parts[] = url.split("/");
				String domain;
				if(parts.length > 2)
					domain = parts[0]+"//"+parts[2];
				else
					domain = url;
				//System.out.println(domain);
				
				if(visitedDomains.containsKey(domain)){
					baseRobotRules = domainRobotRules.get(domain);
					//System.out.println("Thread sleep "+domainVisited.get(domain));
					Thread.sleep(visitedDomains.get(domain));
					//System.out.println(dateFormat.format(cal.getTime()));
				}else{
					int delay = Robots.getCrawlerDelay(domain);
					//System.out.println("Delay for domain"+domain+" is "+delay);
					visitedDomains.put(domain,delay);
					baseRobotRules = Robots.getBaseRobotRules(domain);
					domainRobotRules.put(domain, baseRobotRules);
					//System.out.println("Thread sleep "+domainVisited.get(domain));
					Thread.sleep(delay);
					System.out.println("Domain : "+domain+" delay is: "+delay);
					//System.out.println(dateFormat.format(cal.getTime()));
				}
				
				//System.out.println(disAllowedURL);
				//System.out.println(domain);
				
				if(baseRobotRules != null && !baseRobotRules.isAllowed(url)){
					frontier.remove(url);
					visitedURL.put(url,true);
					System.out.println("Link is not allowed for crawling");
					continue;
				}else{
					linkCount++;
					
					try{
						//Document doc = Jsoup.connect(url).get();
						
						Response responce = Jsoup.connect(url).ignoreHttpErrors(true).timeout(10000).execute();
						if(responce.contentType().contains("text/html")){
							// User egent
							// execute -> responce -> 
							// Timeout 
							// responce. header
							/*
							for(Entry<String, String> e: responce.headers().entrySet()){
								System.out.println(e.getKey() + " "+e.getValue());
							}
							*/
							Document doc = Jsoup.connect(url).ignoreHttpErrors(true).timeout(1000).get();
							
							Elements links = doc.select("a[href]");
							HashMap<String,Integer> allOutGoingLinkMap = new HashMap<String, Integer>();
							StringBuilder outLinks = new StringBuilder();
					        for (Element link : links) {
					        	
					        	 String tempLink = Parsing.getCanonicalizedForm(link.attr("abs:href"));
					        	 
					        	 if(tempLink != null && !Robots.isBlackListedURL(tempLink)){
						        	 if(!allOutGoingLinkMap.containsKey(tempLink)){
						        		 outLinks.append(tempLink+"	");
						        		 allOutGoingLinkMap.put(tempLink,1);
						        		 
							        	 if(!visitedURL.containsKey(tempLink)){
							        		 if(frontier.containsKey(tempLink)){
							        			 frontier.put(tempLink,(frontier.get(tempLink)+1));
							        		 }else{
							        			 if(tempLink != null && !tempLink.equals(" "))
							        				 frontier.put(tempLink,1);
							        		 }
							        		 
							        	 }
							        	 
							        	 //Creating and updating inLink graph
							        	 if(urlInlinks.containsKey(tempLink)){
							        		 // Updating inLink Graph
							        		 urlInlinks.get(tempLink).append(url+"	");
							        	 }else{
							        		// Creating inLink Graph
							        		 StringBuilder inLinks = new StringBuilder();
							        		 inLinks.append(url+"	");
							        		 urlInlinks.put(tempLink, inLinks);
							        	 }
						        	 }
					        	 }
					        	 
					        }
					        
					        // Now write all out going link for the given URL into the file
					        // allOutGoingLinkMap
					        String outlinkFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/outlinkurlgraph.txt";
					        PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(outlinkFilePath,true)));
					        pr.print(url+"|:|");
					        pr.println(outLinks.toString());
					        pr.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println("URL not found");
					}    
				       
					
				}
				frontier.remove(url);
				/*
				for(Entry<String, Integer> e: frontier.entrySet()){
					System.out.println(e.getKey() + " "+e.getValue());
				}
				*/
				visitedURL.put(url,true);
				if (linkCount == 50){
					// Write inlinks into a file
					String inlinkFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/inlinkurlgraph.txt";
			        PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(inlinkFilePath,false)));
					for(Entry<String,StringBuilder> link : urlInlinks.entrySet()){
						pr.print(link.getKey()+"|:|");
						pr.println(link.getValue().toString());
					}
					pr.close();
					break;
				}
			}
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String args[]){
		Crawler.crawling();
	}

}
