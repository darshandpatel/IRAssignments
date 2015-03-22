package ir.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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
		
		HashMap<String,Integer> currentfrontier = new LinkedHashMap<String,Integer>();
		HashMap<String,Integer> futurefrontier = new LinkedHashMap<String,Integer>();
		//frontier.put("http://en.wikipedia.org/wiki/History_of_immigration_to_the_United_States",Integer.MAX_VALUE);
		currentfrontier.put("http://en.wikipedia.org/wiki/Category:Immigration_to_the_United_States",Integer.MAX_VALUE);
		currentfrontier.put("http://en.wikipedia.org/wiki/Immigration_to_the_United_States",Integer.MAX_VALUE);
		currentfrontier.put("http://en.wikipedia.org/wiki/List_of_United_States_immigration_legislation",Integer.MAX_VALUE);
		//frontier.put("http://connection.ebscohost.com/us/immigration-restrictions/current-immigration-laws-us",Integer.MAX_VALUE-1);
		//frontier.put("http://www.washingtonexaminer.com/obamas-immigration-order-appears-destined-for-the-supreme-court/article/2561510",Integer.MAX_VALUE-1);
		//currentfrontier.put("https://www.whitehouse.gov/issues/immigration",Integer.MAX_VALUE);
		//currentfrontier.put("http://www.timetoast.com/timelines/78852",Integer.MAX_VALUE);
		currentfrontier.put("http://www.immigrationinamerica.org/events-and-movements/",Integer.MAX_VALUE);
		currentfrontier.put("http://www.immigrationpolicy.org/issues/history",Integer.MAX_VALUE);
		
		
			
		//frontier.put("http://www.uscis.gov/news-releases",Integer.MAX_VALUE-2);
		
		
		//frontier.put("http://academic.udayton.edu/race/02rights/immigr01.htm",Integer.MAX_VALUE);
		//frontier.put("http://abcnews.go.com/ABC_Univision/News/23-defining-moments-immigration-policy-history/story?id=17810440",Integer.MAX_VALUE);
		
		HashMap<String,Integer> visitedDomains = new HashMap<String,Integer>();
		HashMap<String,Boolean> visitedURL = new HashMap<String,Boolean>();
		HashMap<String,BaseRobotRules> domainRobotRules = new HashMap<String,BaseRobotRules>();
		HashMap<String,StringBuilder> urlInlinks = new HashMap<String,StringBuilder>();
		HashMap<String,String> headers = null;
		BaseRobotRules baseRobotRules = null;
		DocumentData documentData = null;
		ArrayList<DocumentData> documentArray = new ArrayList<DocumentData>();
		int linkCount = 0;
		int fileWriterCounter = 10;
		int docInOneFile = 500;
		try {
			
			while(linkCount != 20000){
			//while(currentfrontier.size() != 0){
				
				if(currentfrontier.size() == 0){
					currentfrontier.putAll(futurefrontier);
					futurefrontier = new HashMap<String, Integer>();
				}
				String url = getURLWithMaxInlineCounts(currentfrontier);
				//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				//Calendar cal = Calendar.getInstance();
				
				//Thread.sleep(1500);
				//System.out.println(dateFormat.format(cal.getTime()));
				System.out.println("Selected URL is "+url+" with inlinks "+currentfrontier.get(url));
				URL urlObj = new URL(url);
				String domain = urlObj.getHost();
				
				//System.out.println(domain);
				
				if(visitedDomains.containsKey(domain)){
					baseRobotRules = domainRobotRules.get(domain);
					//System.out.println("Thread sleep "+domainVisited.get(domain));
					Thread.sleep(visitedDomains.get(domain));
					//System.out.println(dateFormat.format(cal.getTime()));
				}else{
					baseRobotRules = Robots.getBaseRobotRules(urlObj);
					int delay;
					//System.out.println("default score"+baseRobotRules.getCrawlDelay());
					
					if(baseRobotRules.getCrawlDelay() <= 0)
						delay = 1000;
					else
						delay = (int)baseRobotRules.getCrawlDelay();
					
					//System.out.println("Delay for domain "+domain+" is "+delay);
					visitedDomains.put(domain,delay);
					
					domainRobotRules.put(domain, baseRobotRules);
					//System.out.println("Thread sleep "+domainVisited.get(domain));
					Thread.sleep(delay);
					//System.out.println("Domain : "+domain+" delay is: "+delay);
					//System.out.println(dateFormat.format(cal.getTime()));
				}
				
				//System.out.println(disAllowedURL);
				//System.out.println(domain);
				
				if(baseRobotRules != null && !baseRobotRules.isAllowed(url)){
					currentfrontier.remove(url);
					visitedURL.put(url,true);
					//System.out.println("Link is not allowed for crawling");
					continue;
				}else{
					
					try{
						//Document doc = Jsoup.connect(url).get();
						Response responce = Jsoup.connect(url).ignoreHttpErrors(true).userAgent("Mozilla 5.0").timeout(10000).execute();
						
						if(responce.contentType().contains("text/html")){
							linkCount++;
							documentData = new DocumentData();
							headers = (HashMap<String,String>)responce.headers();
							documentData.setHeaders(headers);
							documentData.setId(url);
							documentData.setUrl(url);

							Document doc = Jsoup.connect(url).ignoreHttpErrors(true).userAgent("Mozilla 5.0").timeout(10000).get();
							
							documentData.setTitle(doc.title());
							String rawHTML = doc.toString();
							documentData.setRawHTML(rawHTML);
							String lines[] = rawHTML.split("\n");
							ArrayList<String> cleanedPageContent = new ArrayList<String>();
							for(String line : lines){
								String parsedLine = Jsoup.parse(line).body().text();
								if (!parsedLine.equals(""))
									cleanedPageContent.add(parsedLine);
							}
							documentData.setCleanedHTML(cleanedPageContent);
							
							Elements links = doc.select("a[href]");
							HashMap<String,Integer> allOutGoingLinkMap = new HashMap<String, Integer>();
							StringBuilder outLinks = new StringBuilder();
					        for (Element link : links) {
					        	
					        	 String tempLink = Parsing.getCanonicalizedForm(link.attr("abs:href"));
					        	 
					        	 if(tempLink != null && !tempLink.contains(".pdf")&& !Robots.isBlackListedURL(tempLink)){
						        	 if(!allOutGoingLinkMap.containsKey(tempLink)){
						        		 outLinks.append(tempLink+"\t");
						        		 allOutGoingLinkMap.put(tempLink,1);
						        		 
							        	 if(!visitedURL.containsKey(tempLink) && !currentfrontier.containsKey(tempLink)){
							        		 if(futurefrontier.containsKey(tempLink)){
							        			 futurefrontier.put(tempLink,(futurefrontier.get(tempLink)+1));
							        		 }else{
							        			 if(tempLink != null && !tempLink.equals(" "))
							        				 futurefrontier.put(tempLink,1);
							        		 }
							        		 
							        	 }else if(currentfrontier.containsKey(tempLink)){
							        		 currentfrontier.put(tempLink,(currentfrontier.get(tempLink)+1));
							        	 }
							        	 //Creating and updating inLink graph
							        	 if(urlInlinks.containsKey(tempLink)){
							        		 // Updating inLink Graph
							        		 urlInlinks.get(tempLink).append(url+"\t");
							        	 }else{
							        		// Creating inLink Graph
							        		 StringBuilder inLinks = new StringBuilder();
							        		 inLinks.append(url+"\t");
							        		 urlInlinks.put(tempLink, inLinks);
							        	 }
						        	 }
					        	 }
					        }
					        documentData.setOutLinks(outLinks);
					        documentArray.add(documentData);
					        // Now write all out going link for the given URL into the file
					        // allOutGoingLinkMap
					        
					       
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						currentfrontier.remove(url);
						visitedURL.put(url,true);
						System.out.println("URL not found");
						continue;
					}    
				}
				currentfrontier.remove(url);
				visitedURL.put(url,true);
				
				 if( (linkCount % fileWriterCounter) == 0){
			        	FileOperation.writeIntoFile(documentArray, docInOneFile, linkCount);
			        	documentArray = null;
			        	documentArray = new ArrayList<DocumentData>();
			        }
			}
			
			// Write inlinks into a file
			String inlinkFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/inlinkurlgraph.txt";
	        PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(inlinkFilePath,false)));
			for(Entry<String,Boolean> url : visitedURL.entrySet()){
				pr.print(url.getKey()+"::");
				pr.println(urlInlinks.get(url.getKey()).toString());
			}
			pr.close();
			FileOperation.writeIntoFile(documentArray, docInOneFile, linkCount);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			
			try {
				String currentURL = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/BackUp/currentURL.txt";
				PrintWriter curentPR;
				curentPR = new PrintWriter(new BufferedWriter(new FileWriter(currentURL,false)));
			
				for(Entry<String,Integer> current : currentfrontier.entrySet()){
					curentPR.println(current.getKey()+"::"+current.getValue());
				}
				curentPR.close();
			
				String futureURL = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/BackUp/futureURL.txt";
		        PrintWriter futurePR = new PrintWriter(new BufferedWriter(new FileWriter(futureURL,false)));
				for(Entry<String,Integer> future : futurefrontier.entrySet()){
					futurePR.println(future.getKey()+"::"+future.getValue());
				}
				futurePR.close();
				
				String visitedBackupURL = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/BackUp/visitedURL.txt";
				PrintWriter vistedPR;
				vistedPR = new PrintWriter(new BufferedWriter(new FileWriter(visitedBackupURL,false)));
			
				for(Entry<String,Boolean> current : visitedURL.entrySet()){
					vistedPR.println(current.getKey());
				}
				vistedPR.close();
				
				String tempInlinkFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW3/BackUp/tempInlinkurlgraph.txt";
		        PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(tempInlinkFilePath,false)));
				for(Entry<String,Boolean> url : visitedURL.entrySet()){
					pr.print(url.getKey()+"::");
					pr.println(urlInlinks.get(url.getKey()).toString());
				}
				pr.close();
				FileOperation.writeIntoFile(documentArray, docInOneFile, linkCount);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String args[]){
		Crawler.crawling();
	}

}
