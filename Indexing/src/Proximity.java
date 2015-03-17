import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



public class Proximity {
	
	public static void rangeOfWindow(boolean removeStopWords,boolean stemming){
		
		LinkedHashMap<String, ArrayList<String>> termsPerQuery = Query.getQueryTermsByQuery(removeStopWords,stemming);
		HashMap<String,Long> offsets = FileOperations.getOffsets(removeStopWords, stemming);
		HashMap<String,Integer> docLengths = FileOperations.getDocumentLength(removeStopWords);
		HashMap<String,String> newDocIds = ReadingDoc.getDocIdsbyNewIds();
		HashMap<String,HashMap<String,ArrayList<Integer>>> docDetails = null;
		HashMap<String,Float> docScore = null; 
		int uniqueTermsCount = ReadingDoc.uniqueTermsCount(removeStopWords, stemming);
		try{
			
			for(Map.Entry<String, ArrayList<String>> qterms :  termsPerQuery.entrySet()){
				
				String qnumber = qterms.getKey();
				docScore = new HashMap<String, Float>();
				docDetails = new HashMap<String, HashMap<String,ArrayList<Integer>>>();
				//HashMap<String,HashMap<String,Float>> docScorePerTerms = new HashMap<String,HashMap<String,Float>>();
				//HashMap<String,ArrayList<String>> docPerTerms = new HashMap<String,ArrayList<String>>();
				for(String qterm : qterms.getValue()){
					if (!offsets.containsKey(qterm))
						continue;
					String currentLine = FileOperations.getLine(removeStopWords, stemming, offsets.get(qterm));
					String words[] = currentLine.split(" ");
					for(int i = 1; i < words.length; i++){
						String[] blocks = words[i].split(":");
						String docId = newDocIds.get(blocks[0]);
						String[] positions = blocks[1].split(",");
						ArrayList<Integer> positionArray = new ArrayList<Integer>();
						for(String position: positions){
							positionArray.add(Integer.parseInt(position));
						}
						
						if(docDetails.containsKey(docId)){
							docDetails.get(docId).put(qterm, positionArray);
						}else{
							HashMap<String,ArrayList<Integer>> termPositions = new HashMap<String, ArrayList<Integer>>();
							termPositions.put(qterm, positionArray);
							docDetails.put(docId, termPositions);
						}
						
					}
					//docPerTerms.put(qterm,docs);
					//docScorePerTerms.put(qterm, okapiScore);
					//System.out.println("Term "+qterm + " found in this main documents "+okapiScore.size());
				}
				System.out.println("For query no "+ qnumber+" : "+docDetails.size()+" unique documents has found");
				// Logic to score the documents.
				
				// Iterating over each document to score it.	
				for(Map.Entry<String,HashMap<String,ArrayList<Integer>>> doc: docDetails.entrySet()){
					
					// Scoring only those documents which contains more than (1/2) query terms.
					String docId = doc.getKey();
					HashMap<String,ArrayList<Integer>> termPositions = doc.getValue();
					//System.out.println(docId+" has "+termPositions.size() + " terms in it.");
					//System.out.println(termPositions.size() > (qterms.getValue().size()/2));
					//if(termPositions.size() > (qterms.getValue().size()/2)){
					if(termPositions.size() > 1){
						//System.out.println(termPositions);
						HashMap<String,Integer> termPositionPointer = new HashMap<String, Integer>();
						for(String qterm : termPositions.keySet()){
							termPositionPointer.put(qterm, 0); 
						}
						float minimumRange = Constants.maxValue;
						int range = 0;
						do{
							//System.out.println("At Start"+termPositionPointer);
							String minTerm = null;
							int min = Constants.maxValue;
							int max = Constants.minValue;
							for(Map.Entry<String, Integer> termArrayPos: termPositionPointer.entrySet()){
								String term = termArrayPos.getKey();
								Integer position = termArrayPos.getValue();
								ArrayList<Integer> posArrayList = termPositions.get(term);
								int value = posArrayList.get(position);
								if(value < min){
									min = value;
									minTerm = term;
								}
								if(value > max){
									max = value;
								}
								
							}
							range = max - min;
							if (range < minimumRange)
								minimumRange = range;
							//System.out.println("After seach min value is :"+min);
							//System.out.println("After seach range is :"+range);
							// Now increment the index.
							//System.out.println("Min term  is :"+minTerm);
							int newArrayPos = termPositionPointer.get(minTerm) + 1;
							//System.out.println("New position value of "+minTerm+" is "+newArrayPos);
							int totalArraySize = termPositions.get(minTerm).size();
							if (newArrayPos == totalArraySize){
								break;
							}
							termPositionPointer.put(minTerm, newArrayPos);
						}while(true);
						
						float score = (((float)((1500 - minimumRange)* termPositions.size()))/(uniqueTermsCount + docLengths.get(docId)));
						//float score = (float)((1500 - minimumRange) * termPositions.size());
						//System.out.println("Final minimum value is : "+minimumRange);
						docScore.put(docId, score);
					}
				}
				// true - ascending order
				Map<String, Float> sortedDoc =  Query.sortByComparator(docScore, false);
				String outputFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/proximity_result.txt";
				FileOperations.writeResult(qnumber, sortedDoc,outputFilePath,1000);
			}
		}catch(Exception e){
			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Proximity.rangeOfWindow(true, true);
	}

}
