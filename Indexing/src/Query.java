import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.SnowballStemmer;


public class Query {
	
	static String queryFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/query_desc.51-100.short.txt";
	
	public static LinkedHashMap<String, ArrayList<String>> getQueryTermsByQuery(boolean removeStopWords,boolean stemming){
		
		HashMap<String,Byte> stopWords = ReadingDoc.getStopWords();
		SnowballStemmer stemmer = null;
		LinkedHashMap<String, ArrayList<String>> termsPerQuery = new LinkedHashMap<String, ArrayList<String>>();
		try{
			File queryFile = new File(queryFilePath);
			BufferedReader br = new BufferedReader(new FileReader(queryFile));
			String currentLine = null;
			if (stemming == true){
				Class stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
				stemmer = (SnowballStemmer) stemClass.newInstance();
			}
			while((currentLine = br.readLine()) != null){
				String queryNo = null;
				ArrayList<String> qterms = new ArrayList<String>();
				Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
				Matcher matcher = pattern.matcher(currentLine.toLowerCase());
				int i = 0;
				while (matcher.find()) {
					if (i == 0){
						String temp = currentLine.substring(matcher.start(), matcher.end());
						temp = temp.replaceAll("\\.$", "");
						queryNo = temp.toLowerCase();
					}
					else if(i > 3){
						String term = currentLine.substring(matcher.start(), matcher.end());
						term = term.replaceAll("\\.$", "");
						term = term.toLowerCase();
						if (removeStopWords == true){
							if(!stopWords.containsKey(term)){
								if(stemming == true){
									stemmer.setCurrent(term);
									stemmer.stem();
									term = stemmer.getCurrent();
								}
								if(!qterms.contains(term))
									qterms.add(term);
							}
						}else{
							if(stemming == true){
								stemmer.setCurrent(term);
								stemmer.stem();
								term = stemmer.getCurrent();
								qterms.add(term);
							}else{
								qterms.add(term);
							}
						}
					}
					i++;
				}
				if(queryNo != null)
					termsPerQuery.put(queryNo, qterms);
			}
		}catch(IOException e){
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return termsPerQuery;
		
	}

	
	protected static Map<String, Float> sortByComparator(Map<String, Float> unsortMap, final boolean order)
    {

        List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Float>>()
        {
            public int compare(Entry<String, Float> o1,
                    Entry<String, Float> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Entry<String, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public static void performOkapiTFQueries(boolean removeStopWords,boolean stemming){
		
		LinkedHashMap<String, ArrayList<String>> termsPerQuery = getQueryTermsByQuery(removeStopWords,stemming);
		HashMap<String,Long> offsets = FileOperations.getOffsets(removeStopWords, stemming);
		HashMap<String,Integer> docLengths = FileOperations.getDocumentLength(removeStopWords);
		HashMap<String,String> newDocIds = ReadingDoc.getDocIdsbyNewIds();
		RandomAccessFile indexFile;
		float averageDocLength = FileOperations.getAverageDocumentLength(removeStopWords);
		System.out.println("Total number of query " + termsPerQuery.size());
		System.out.println("Phase 1 clear");
		try{
			
			for(Map.Entry<String, ArrayList<String>> qterms :  termsPerQuery.entrySet()){
				String qnumber = qterms.getKey();
				HashMap<String,HashMap<String,Float>> docScorePerTerms = new HashMap<String,HashMap<String,Float>>();
				HashMap<String,ArrayList<String>> docPerTerms = new HashMap<String,ArrayList<String>>();
				System.out.println(qterms.getValue());
				System.out.println("Phase 2 clear");
				for(String qterm : qterms.getValue()){
					//System.out.println(qterm);
					HashMap<String,Float> okapiScore = new HashMap<String, Float>();
					if (offsets.get(qterm) == null)
						continue;
					String currentLine = FileOperations.getLine(removeStopWords, stemming, offsets.get(qterm));
					String words[] = currentLine.split(" ");
					ArrayList<String> docs = new ArrayList<String>();
					//System.out.println(words.length);
					for(int i = 1; i < words.length; i++){
						String[] blocks = words[i].split(":");
						String docId = newDocIds.get(blocks[0]);
						//System.out.println(docId);
						Integer tf = blocks[1].split(",").length;
						//System.out.println(tf);
						docs.add(docId);
						Float score = (float)(tf/(tf+0.5+(1.5*((float)docLengths.get(docId)/averageDocLength))));
						okapiScore.put(docId, score);
						
					}
					docPerTerms.put(qterm,docs);
					docScorePerTerms.put(qterm, okapiScore);
					//System.out.println("Term "+qterm + " found in this main documents "+okapiScore.size());
				}
				
				ArrayList<String> allDoc = new ArrayList<String>();
				for(Map.Entry<String, ArrayList<String>> pair :docPerTerms.entrySet()){
					allDoc.addAll(pair.getValue());
				}
				Set<String> uniqueDoc = new HashSet<String>(allDoc);
				HashMap<String,Float> docOkapiTFValue = new HashMap<String,Float>();
				for(String doc : uniqueDoc){
					float score = 0;
					for(String term : qterms.getValue()){
						if(docScorePerTerms.containsKey(term) && docScorePerTerms.get(term).containsKey(doc)){
							score += docScorePerTerms.get(term).get(doc);
						}
					}
					docOkapiTFValue.put(doc, score);
				}
				
				Map<String, Float> sortedDoc = sortByComparator(docOkapiTFValue, false);
				String outputFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/okapi_tf_result.txt";
				FileOperations.writeResult(qnumber, sortedDoc,outputFilePath,1000);
			}
			
		}catch(Exception e){
			
		}
	}
	
	public static void performLaplaceQueries(boolean removeStopWords,boolean stemming){
		
		LinkedHashMap<String, ArrayList<String>> termsPerQuery = getQueryTermsByQuery(removeStopWords,stemming);
		HashMap<String,Long> offsets = FileOperations.getOffsets(removeStopWords, stemming);
		HashMap<String,Integer> docLengths = FileOperations.getDocumentLength(removeStopWords);
		HashMap<String,String> newDocIds = ReadingDoc.getDocIdsbyNewIds();
		int uniqueTerms = ReadingDoc.uniqueTermsCount(removeStopWords, stemming);
		try{
			
			for(Map.Entry<String, ArrayList<String>> qterms :  termsPerQuery.entrySet()){
				String qnumber = qterms.getKey();
				HashMap<String,HashMap<String,Integer>> docScorePerTerms = new HashMap<String,HashMap<String,Integer>>();
				HashMap<String,ArrayList<String>> docPerTerms = new HashMap<String,ArrayList<String>>();

				for(String qterm : qterms.getValue()){
					HashMap<String,Integer> tfs = new HashMap<String, Integer>();
					if (offsets.get(qterm) == null)
						continue;
					String currentLine = FileOperations.getLine(removeStopWords, stemming, offsets.get(qterm));
					String words[] = currentLine.split(" ");
					ArrayList<String> docs = new ArrayList<String>();
					//System.out.println(words.length);
					for(int i = 1; i < words.length; i++){
						String[] blocks = words[i].split(":");
						String docId = newDocIds.get(blocks[0]);
						Integer tf = blocks[1].split(",").length;
						docs.add(docId);
						tfs.put(docId, tf);
						
					}
					docPerTerms.put(qterm,docs);
					docScorePerTerms.put(qterm, tfs);
					//System.out.println("Term "+qterm + " found in this main documents "+okapiScore.size());
				}
				/*
				ArrayList<String> allDoc = new ArrayList<String>();
				for(Map.Entry<String, ArrayList<String>> pair :docPerTerms.entrySet()){
					allDoc.addAll(pair.getValue());
				}
				Set<String> uniqueDoc = new HashSet<String>(allDoc);
				System.out.println("Unique documents"+uniqueDoc.size());
				*/
				
				HashMap<String,Float> docUnigarmLSValue = new HashMap<String,Float>();
				for(Map.Entry<String, Integer> pair : docLengths.entrySet()){
					String doc = pair.getKey();
					float score = 0;
					float laplacesScore = 0;
					for(String term : qterms.getValue()){
						if(docScorePerTerms.containsKey(term) && docScorePerTerms.get(term).containsKey(doc)){
							laplacesScore += Math.log(((float)docScorePerTerms.get(term).get(doc) + 1.0)/
									(float)(uniqueTerms + docLengths.get(doc)));
						}else{
							laplacesScore += Math.log(((float)1.0) / (uniqueTerms + docLengths.get(doc)));
						}
						score += laplacesScore;
					}
					docUnigarmLSValue.put(doc, score);
				}
				
				Map<String, Float> sortedDoc = sortByComparator(docUnigarmLSValue, false);
				
				String outputFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/unigram_ls_result.txt";
				FileOperations.writeResult(qnumber, sortedDoc,outputFilePath,1000);
			}
			
		}catch(Exception e){
			
		}
	}
	
	
	public static void performBM25Queries(boolean removeStopWords,boolean stemming){
		
		LinkedHashMap<String, ArrayList<String>> termsPerQuery = getQueryTermsByQuery(removeStopWords,stemming);
		HashMap<String,Long> offsets = FileOperations.getOffsets(removeStopWords, stemming);
		HashMap<String,Integer> docLengths = FileOperations.getDocumentLength(removeStopWords);
		HashMap<String,String> newDocIds = ReadingDoc.getDocIdsbyNewIds();
		int uniqueTerms = ReadingDoc.uniqueTermsCount(removeStopWords, stemming);
		int noOfDocs = docLengths.size();
		float averageDocLength = FileOperations.getAverageDocumentLength(removeStopWords);
		try{
			
			for(Map.Entry<String, ArrayList<String>> qterms :  termsPerQuery.entrySet()){
				String qnumber = qterms.getKey();
				HashMap<String,HashMap<String,Integer>> docScorePerTerms = new HashMap<String,HashMap<String,Integer>>();
				HashMap<String,ArrayList<String>> docPerTerms = new HashMap<String,ArrayList<String>>();
				HashMap<String,Integer> docFrequency = new HashMap<String,Integer>();
				HashMap<String,Integer> tfInQuery = new HashMap<String,Integer>();
				for(String qterm : qterms.getValue()){
					HashMap<String,Integer> tfs = new HashMap<String, Integer>();
					if (offsets.get(qterm) == null)
						continue;
					if(tfInQuery.containsKey(qterm)){
						tfInQuery.put(qterm,tfInQuery.get(qterm).intValue()+1);
					}else
						tfInQuery.put(qterm,1);
					String currentLine = FileOperations.getLine(removeStopWords, stemming, offsets.get(qterm));
					String words[] = currentLine.split(" ");
					ArrayList<String> docs = new ArrayList<String>();
					//System.out.println(words.length);
					docFrequency.put(qterm, words.length - 1);
					for(int i = 1; i < words.length; i++){
						String[] blocks = words[i].split(":");
						String docId = newDocIds.get(blocks[0]);
						Integer tf = blocks[1].split(",").length;
						docs.add(docId);
						tfs.put(docId, tf);
						
					}
					docPerTerms.put(qterm,docs);
					docScorePerTerms.put(qterm, tfs);
					//System.out.println("Term "+qterm + " found in this main documents "+okapiScore.size());
				}

				float k1 = (float) 1.2;
			    float k2 = (float) 2.0;
			    float b = (float) 0.75;
				HashMap<String,Float> docBM25Value = new HashMap<String,Float>();
				for(Map.Entry<String, Integer> pair : docLengths.entrySet()){
					String doc = pair.getKey();
					float score = 0;
					for(String term : qterms.getValue()){
						if(docScorePerTerms.containsKey(term) && docScorePerTerms.get(term).containsKey(doc)){
        
						    float part1 = (float) Math.log(( 0.5 + noOfDocs)/(0.5 + docFrequency.get(term)));
						    float part21 = (docScorePerTerms.get(term).get(doc) + (k1 * docScorePerTerms.get(term).get(doc)));
						    float part22 = (docScorePerTerms.get(term).get(doc) + (k1 * ((1-b) + (b * (float)(docLengths.get(doc)/averageDocLength)))));
						    float part2 = part21/part22;
						    float part3 = ((tfInQuery.get(term)+(k2 * tfInQuery.get(term)))/((float)(k2+tfInQuery.get(term))));
						    score += (part1*part2*part3);
						}
					}
					docBM25Value.put(doc, score);
				}
				
				Map<String, Float> sortedDoc = sortByComparator(docBM25Value, false);
				
				String outputFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/bm25_result.txt";
				FileOperations.writeResult(qnumber, sortedDoc,outputFilePath,1000);
			}
			
		}catch(Exception e){
			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Query.getQueryTermsByQuery(true, true);
		Query.performOkapiTFQueries(true, true);
		Query.performLaplaceQueries(true, true);
		Query.performBM25Queries(true, true);
	}

}
