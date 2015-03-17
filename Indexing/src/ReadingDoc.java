import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.*;



public class ReadingDoc {
	
	static String tempOffsetWOStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempOffsetWOStopWOStemmed.txt";
	static String tempIndexWOStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempIndexWOStopWOStemmed.txt";
	static String offsetWOStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/offsetWOStopWOStemmed.txt";
	static String indexWOStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWOStopWOStemmed.txt";
	
	static String tempOffsetWithStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempOffsetWithStopWOStemmed.txt";
	static String tempIndexWithStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempIndexWithStopWOStemmed.txt";
	static String offsetWithStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/offsetWithStopWOStemmed.txt";
	static String indexWithStopWOStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWithStopWOStemmed.txt";
	
	static String tempOffsetWOStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempOffsetWOStopWithStemmed.txt";
	static String tempIndexWOStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempIndexWOStopWithStemmed.txt";
	static String offsetWOStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/offsetWOStopWithStemmed.txt";
	static String indexWOStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWOStopWithStemmed.txt";
	
	static String tempOffsetWithStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempOffsetWithStopWithStemmed.txt";
	static String tempIndexWithStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/tempIndexWithStopWithStemmed.txt";
	static String offsetWithStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/offsetWithStopWithStemmed.txt";
	static String indexWithStopWithStemmedFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWtihStopWithStemmed.txt";
	
	// order true means ascending order
	// Order false meaning descending order
	public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
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
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public static void assignNumToDocId(){
		
		try {
			
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/doclist.txt";
			File doclistFile = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(doclistFile));
			LinkedHashMap<String,Integer> newDocId = new LinkedHashMap<String, Integer>();
			br.readLine();
			String currentLine;
			int number = 1;
			while((currentLine = br.readLine()) != null){
				String[] pars = currentLine.split(" ");
				newDocId.put(pars[1], number);
				number++;
			}
			br.close();
			
			String newDocIdFile = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/newdocids.txt";
			File newFile = new File(newDocIdFile);
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(newFile,true)));
			
			for( Map.Entry<String, Integer> pair : newDocId.entrySet()){
				pr.print(pair.getKey());
				pr.print(" ");
				pr.println(pair.getValue().toString());
			}
			pr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void newDocIDsAndLength(){
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		HashMap<String, Byte> stopWords = getStopWords();
		final HashMap<String, Integer> docLengthWOStop = new HashMap<String, Integer>();
		final HashMap<String, Integer> docLengthWithStop = new HashMap<String, Integer>();
		try {

			for(File file: folder.listFiles()){
				if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
				
					BufferedReader br = new BufferedReader(new FileReader(file));
					Boolean textStarted = false;
					String line;
					String docId = null;
					int counterWOStop = 0;
					int counterWithStop = 0;
					
					while((line = br.readLine()) != null){
						if (line.contains("<DOCNO>")){
							String words[]=line.split(" ");
							docId = words[1];
							counterWOStop = 0;
							counterWithStop = 0;
						}else if (line.contains("<TEXT>")){
							textStarted = true;
						}else if (textStarted == true){
							if (!(line.contains("</TEXT>"))){
								Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
								Matcher matcher = pattern.matcher(line.toLowerCase());
								while (matcher.find()) {
									String term = line.substring(matcher.start(), matcher.end());
									//term = term.replaceAll("\\.$", "");
									term = term.toLowerCase();
									if(!term.equals("")){
										if(!stopWords.containsKey(term)){
											counterWOStop++;
										}
										counterWithStop++;
									}
								}
							}else{
								textStarted = false;
							}
						}else if(line.contains("</DOC>")){
							docLengthWOStop.put(docId, counterWOStop);
							docLengthWithStop.put(docId, counterWithStop);
						}
					}

				br.close();
				}
			
			}
		
			LinkedHashMap<String, Integer> sortedDocLength = (LinkedHashMap<String, Integer>) sortByComparator(docLengthWOStop, false);
			
			String newDocIdFile = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/docdetails.txt";
			File newFile = new File(newDocIdFile);
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(newFile,false)));
			
			int length = 1;
			for( Map.Entry<String, Integer> pair : sortedDocLength.entrySet()){
				pr.print(pair.getKey());
				pr.print(" ");
				pr.print(length);
				pr.print(" ");
				pr.print(pair.getValue());
				pr.print(" ");
				pr.println(docLengthWithStop.get(pair.getKey()));
				length++;
			}
			pr.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static HashMap<String,String> getNewIdsbyDocIds(){
		
		HashMap<String,String> newDocId = new HashMap<String, String>();
		try {
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/docdetails.txt";
			File newDocIdsFile = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(newDocIdsFile));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				String[] pars = currentLine.split(" ");
				newDocId.put(pars[0], pars[1]);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newDocId;
	}
	
	public static HashMap<String,String> getDocIdsbyNewIds(){
		
		HashMap<String,String> docIds = new HashMap<String, String>();
		try {
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/docdetails.txt";
			File newDocIdsFile = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(newDocIdsFile));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				String[] pars = currentLine.split(" ");
				docIds.put(pars[1], pars[0]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docIds;
		
	}
	
	public static void assignNumToTermsWOStemmed(){
		
		//HashMap<String,Byte> stopWords = ReadingDoc.getStopWords();
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		try {
			final Map<String, Integer> uniqueTerms = new LinkedHashMap<String, Integer>();
			for(File file: folder.listFiles()){
				
				if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
					
						BufferedReader br = new BufferedReader(new FileReader(file));
						Boolean textStarted = false;
						String line;
						
						while((line = br.readLine()) != null){
							if (line.contains("<TEXT>")){
								textStarted = true;
							}else if (textStarted == true){
								if (!(line.contains("</TEXT>"))){
									Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
									Matcher matcher = pattern.matcher(line.toLowerCase());
									while (matcher.find()) {
										for (int i = 0; i < matcher.groupCount(); i++) {
											String term = matcher.group(i).toLowerCase().trim();
											if (!uniqueTerms.containsKey(term)){
												uniqueTerms.put(term,term.length());
											}
										}
									}
								}else{
									textStarted = false;
								}
							}
						} 
						br.close();
				}
				
			}
			
			// Sort the map
			List<String> list = new ArrayList<String>(uniqueTerms.keySet());
	
			Comparator<String> cmp = new Comparator<String>() {
				@Override
				public int compare(String a1, String a2) {
					Integer v1 = uniqueTerms.get(a1);
					Integer v2 = uniqueTerms.get(a2);
					return v2.compareTo(v1);
				}
			};
			Collections.sort(list, Collections.reverseOrder(cmp));
			
			String newTermIdFile = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/termIdsWOStemmed.txt";
			File newFile = new File(newTermIdFile);
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(newFile,false)));
			
			int length = 1;
			for(String doc: list){
				pr.print(doc);
				pr.print(" ");
				pr.println(length);
				length++;
			}
			pr.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int uniqueTermsCount(boolean removeStopWords,boolean stemming){
		
		HashMap<String,Byte> stopWords = null;
		SnowballStemmer stemmer= null;
		if (removeStopWords == true){
			stopWords = ReadingDoc.getStopWords();
		}
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		try {
			if (stemming == true){
				Class stemClass = Class.forName("org.tartarus.snowball.ext.porterStemmer");
				stemmer = (SnowballStemmer) stemClass.newInstance();
			}
			final Map<String, Integer> uniqueTerms = new LinkedHashMap<String, Integer>();
			for(File file: folder.listFiles()){
				
				if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
					
						BufferedReader br = new BufferedReader(new FileReader(file));
						Boolean textStarted = false;
						String line;
						
						while((line = br.readLine()) != null){
							if (line.contains("<TEXT>")){
								textStarted = true;
							}else if (textStarted == true){
								if (!(line.contains("</TEXT>"))){
									Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
									Matcher matcher = pattern.matcher(line.toLowerCase());
									while (matcher.find()) {
										for (int i = 0; i < matcher.groupCount(); i++) {
											String term = line.substring(matcher.start(), matcher.end());
											term = term.replaceAll("\\.$", "");
											term = term.toLowerCase();
											if (!term.equals("")){
												if (removeStopWords == true){
													if(!stopWords.containsKey(term)){
														if(stemming == true){
															stemmer.setCurrent(term);
															stemmer.stem();
															term = stemmer.getCurrent();
														}
														if (!uniqueTerms.containsKey(term)){
															uniqueTerms.put(term,term.length());
														}
													}
													
												}else{
													if(stemming == true){
														stemmer.setCurrent(term);
														stemmer.stem();
														term = stemmer.getCurrent();
													}
													if (!uniqueTerms.containsKey(term)){
														uniqueTerms.put(term,term.length());
													}
												}
											}
										}
									}
								}else{
									textStarted = false;
								}
							}
						} 
						br.close();
						
				}
				
			}
			return uniqueTerms.size();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return 0;
	}
	
	
	public static HashMap<String,String> getNewIdsOfTermsWOStemmed(boolean stemming){
		
		LinkedHashMap<String,String> newTermsId = new LinkedHashMap<String, String>();
		try {
			String path;
			if (stemming == false)
				path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/termIdsWOStemmed.txt";
			else
				path = "/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/termIdsWithStemmed.txt";
			File newTermIdsFile = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(newTermIdsFile));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				String[] pars = currentLine.split(" ");
				newTermsId.put(pars[0], pars[1]);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newTermsId;
		
		
	}
	
	public static HashMap<String, Byte> getStopWords(){
		
		HashMap<String,Byte> stopWords = new HashMap<String, Byte>();
		try {
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/stoplist.txt";
			File stoplist = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(stoplist));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				stopWords.put(currentLine, (byte) 1);
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stopWords;
	}
	
	public static void indexing(boolean removeStopWords,boolean stemming){
		
		HashMap<String, String> newDocIds = getNewIdsbyDocIds();
		//HashMap<String, String> newTermIds = getNewIdsOfTermsWOStemmed(stemming);
		HashMap<String, Byte> stopWords = null;
		String tempIndexFilePath = null;
		String tempOffsetFilePath = null;
		SnowballStemmer stemmer = null;
		if(removeStopWords == true){
			stopWords = getStopWords();
		}
		
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		Map<String, ArrayList<DBlock>> termsDBlock = new HashMap<String, ArrayList<DBlock>>();
		int docCounter = 0;
		int byteOffset = 0;
		try {
			if (stemming == true){
				Class stemClass = Class.forName("org.tartarus.snowball.ext.porterStemmer");
				stemmer = (SnowballStemmer) stemClass.newInstance();
			}
			for(File file: folder.listFiles()){
				if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
					BufferedReader br = new BufferedReader(new FileReader(file));
					String docId="";
					Boolean textStarted = false;
					int termIndex = 1;
					String line;
					
					//DefaultHashMap<String, Short> termFreqency = null;
					Map<String, ArrayList<Integer>> termPositions = null;
					while((line = br.readLine()) != null){
						if (line.contains("<DOCNO>")){
							String words[]=line.split(" ");
							//docId = words[1];
							docId = newDocIds.get(words[1]);
							termIndex = 1;
							//termFreqency = new DefaultHashMap<String, Short>(new Short((short) 0));
							termPositions = new HashMap<String, ArrayList<Integer>>();
						}else if (line.contains("<TEXT>")){
							textStarted = true;
						}else if (textStarted == true){
							if (!(line.contains("</TEXT>"))){
								Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
								Matcher matcher = pattern.matcher(line);
								while (matcher.find()) {
									String term = line.substring(matcher.start(), matcher.end());
									term = term.replaceAll("\\.$", "");
									term = term.toLowerCase();
									if (!term.equals("")){
										if(removeStopWords == true){
											
											if(!stopWords.containsKey(term)){
												
												if(stemming == true){
													stemmer.setCurrent(term);
													stemmer.stem();
													term = stemmer.getCurrent();
												}
												//termFreqency.put(term,(short) (termFreqency.get(term)+1));
												if (termPositions.containsKey(term))
													termPositions.get(term).add(new Integer(termIndex));
												else{
													ArrayList<Integer> pos = new ArrayList<Integer>();
													pos.add(new Integer(termIndex));
													termPositions.put(term, pos);
												}
												termIndex++;
											}
										}else{
											//termFreqency.put(term,(short) (termFreqency.get(term)+1));
											if(stemming == true){
												stemmer.setCurrent(term);
												stemmer.stem();
												term = stemmer.getCurrent();
											}
											if (termPositions.containsKey(term))
												termPositions.get(term).add(new Integer(termIndex));
											else{
												ArrayList<Integer> pos = new ArrayList<Integer>();
												pos.add(new Integer(termIndex));
												termPositions.put(term, pos);
											}
											termIndex++;
										}
								
									}
								}
							}else{
								textStarted = false;
							}
						}else if(line.contains("</DOC>")){
							docCounter++;
							for(Map.Entry<String, ArrayList<Integer>> pair : termPositions.entrySet()){
								String term = pair.getKey();
								DBlock db = new DBlock();
								db.setDocId(docId);
								//db.setTermFreq(pair.getValue());
								db.setPositions(pair.getValue());
								if (termsDBlock.containsKey(term)){
									termsDBlock.get(term).add(db);
								}else{
									ArrayList<DBlock> dBlock = new ArrayList<DBlock>();
									dBlock.add(db);
									termsDBlock.put(term,dBlock);
								}
							}
							
							if (docCounter == 1000){
								
								// Write into the files.
								if (removeStopWords == true){
									if (stemming == false){
										tempIndexFilePath = ReadingDoc.tempIndexWOStopWOStemmedFilePath;
										tempOffsetFilePath = ReadingDoc.tempOffsetWOStopWOStemmedFilePath;
									}else{
										tempIndexFilePath = ReadingDoc.tempIndexWOStopWithStemmedFilePath;
										tempOffsetFilePath = ReadingDoc.tempOffsetWOStopWithStemmedFilePath;
									}
								}else{
									if (stemming == false){
										tempIndexFilePath = ReadingDoc.tempIndexWithStopWOStemmedFilePath;
										tempOffsetFilePath = ReadingDoc.tempOffsetWithStopWOStemmedFilePath;
									}else{
										tempIndexFilePath = ReadingDoc.tempIndexWithStopWithStemmedFilePath;
										tempOffsetFilePath = ReadingDoc.tempOffsetWithStopWithStemmedFilePath;
									}
									
								}
								
								PrintWriter indexWriter = new PrintWriter(new BufferedWriter(new FileWriter(tempIndexFilePath, true)));
								PrintWriter offsetWriter = new PrintWriter(new BufferedWriter(new FileWriter(tempOffsetFilePath, true)));
							
								
								for(Map.Entry<String, ArrayList<DBlock>> pair : termsDBlock.entrySet()){
									StringBuilder indexWriterStr = new StringBuilder();
									int termStartOffset = byteOffset;
									//indexWriter.print(pair.getKey().toString()+" ");
									//String termID = newTermIds.get(pair.getKey());
									String termID = pair.getKey();
									//indexWriterStr.append(pair.getKey().toString()+" ");
									indexWriterStr.append(termID+" ");
									//byteOffset += (termID.length() + 1);
									
									offsetWriter.print(termID+" ");
									offsetWriter.print(termStartOffset);
									
									//int listLength = pair.getValue().size();
									for(DBlock dbTemp : pair.getValue()){
										indexWriterStr.append(dbTemp.getDocId()+":");
										//byteOffset += ((dbTemp.getDocId().length() + 1));
										StringBuilder posStr = new StringBuilder();
										for(Integer pos : dbTemp.getPositions()){
											posStr.append(pos);
											posStr.append(",");
											//byteOffset += (1 + (pos.toString().length()));
										}
										posStr.deleteCharAt(posStr.length()-1);
										//byteOffset -= 1;
										indexWriterStr.append(posStr.toString());
										//byteOffset  += posStr.toString().length();
										indexWriterStr.append(" ");
										//byteOffset += 1;
									}
									indexWriterStr.deleteCharAt(indexWriterStr.length()-1);
									//byteOffset -= 1;

									indexWriter.println(indexWriterStr.toString());
									byteOffset += (indexWriterStr.toString().length()+1);
									offsetWriter.print(" ");
									offsetWriter.println(byteOffset-1);
									}
									offsetWriter.close();
									indexWriter.close();
									docCounter = 0;
									termsDBlock =  new HashMap<String, ArrayList<DBlock>>();
							}
						}
					} 
					br.close();
				}
			}
			PrintWriter indexWriter = new PrintWriter(new BufferedWriter(new FileWriter(tempIndexFilePath, true)));
			PrintWriter offsetWriter = new PrintWriter(new BufferedWriter(new FileWriter(tempOffsetFilePath, true)));
		
			
			for(Map.Entry<String, ArrayList<DBlock>> pair : termsDBlock.entrySet()){
				
				StringBuilder indexWriterStr = new StringBuilder();
				int termStartOffset = byteOffset;
				//indexWriter.print(pair.getKey().toString()+" ");
				//String termID = newTermIds.get(pair.getKey());
				String termID = pair.getKey();
				//indexWriterStr.append(pair.getKey().toString()+" ");
				indexWriterStr.append(termID+" ");
				
				offsetWriter.print(termID+" ");
				offsetWriter.print(termStartOffset);
				
				//byteOffset += (termID.length() + 1);
				
				//int listLength = pair.getValue().size();
				for(DBlock dbTemp : pair.getValue()){
					indexWriterStr.append(dbTemp.getDocId()+":");
					//byteOffset += ((dbTemp.getDocId().length() + 1));
					StringBuilder posStr = new StringBuilder();
					for(Integer pos : dbTemp.getPositions()){
						posStr.append(pos);
						posStr.append(",");
						//byteOffset += (1 + (pos.toString().length()));
					}
					posStr.deleteCharAt(posStr.length()-1);
					//byteOffset -= 1;
					indexWriterStr.append(posStr.toString());
					indexWriterStr.append(" ");
					//byteOffset += 1;
				}
				indexWriterStr.deleteCharAt(indexWriterStr.length()-1);
				//byteOffset -= 1;

				indexWriter.println(indexWriterStr.toString());
				byteOffset += (indexWriterStr.toString().length()+1);
				offsetWriter.print(" ");
				offsetWriter.println(byteOffset-1);
			}
			offsetWriter.close();
			indexWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
	    
	}
	
	public static void mergeFile(String readOffsetFile,String readIndexFile, String writeOffsetFile,String writeIndexFile){
		// Now merging the file.
		
		// First of all create the hash map of the offset file
		HashMap<String, ArrayList<Integer>> tempTermOffsets = new HashMap<String, ArrayList<Integer>>();
		
		File offsetFileR = new File(readOffsetFile);
		File indexFileR = new File(readIndexFile);
		FileReader fr;
		try {
			fr = new FileReader(offsetFileR);
			BufferedReader br =  new BufferedReader(fr);
			String currentLine;
			while((currentLine = br.readLine()) != null){
				String str[] = currentLine.split(" ");
				if (!tempTermOffsets.containsKey(str[0])){
					ArrayList<Integer> offset = new ArrayList<Integer>();
					offset.add(Integer.parseInt(str[1]));
					tempTermOffsets.put(str[0],offset);
				}else{
					tempTermOffsets.get(str[0]).add(Integer.parseInt(str[1]));
				}
			}
			br.close();
			
			RandomAccessFile oldIndexFile;
			int count = 0;
			HashMap<String,StringBuilder> termStrings = new HashMap<String, StringBuilder>();
			oldIndexFile = new RandomAccessFile(readIndexFile, "r");
			long offset = 0;
			for(Map.Entry<String, ArrayList<Integer>> pair: tempTermOffsets.entrySet()){
				String term = pair.getKey();
				ArrayList<Integer> positions = pair.getValue();
				StringBuilder dblocks = new StringBuilder();
				for(Integer pos : positions){
					oldIndexFile.seek(pos);
					//System.out.println(pos);
					String fileLine = oldIndexFile.readLine();
					//System.out.println(fileLine);
					//String curDBlock = fileLine.substring(term.length());
					//dblocks.append(curDBlock);
					//System.out.println(term);
					//System.out.println(pos);
					String curDBlock = fileLine.substring(term.length());
					dblocks.append(curDBlock);
					/*
					String[] parts = fileLine.split(" ");
					for(String part : parts){
						dblocks.append(part);
						dblocks.append(" ");
					}
					*/
				}
				count++;
				termStrings.put(term, dblocks);
				if(count == 1000){
					PrintWriter newIndexWriter = new PrintWriter(new BufferedWriter(new FileWriter(writeIndexFile, true)));
					PrintWriter newOffsetWriter = new PrintWriter(new BufferedWriter(new FileWriter(writeOffsetFile, true)));
					
					for(Map.Entry<String,StringBuilder> termString : termStrings.entrySet()){
						newIndexWriter.print(termString.getKey());
						newOffsetWriter.print(termString.getKey()+" ");
						newOffsetWriter.print(offset+" ");
						offset += termString.getKey().length();
						newIndexWriter.println(termString.getValue().toString());
						offset += ((termString.getValue().toString()).length() + 1);
						newOffsetWriter.println(offset-1);
					}
					newIndexWriter.close();
					newOffsetWriter.close();
					count = 0;
					termStrings = new HashMap<String, StringBuilder>();
				}
			}
			PrintWriter newIndexWriter = new PrintWriter(new BufferedWriter(new FileWriter(writeIndexFile, true)));
			PrintWriter newOffsetWriter = new PrintWriter(new BufferedWriter(new FileWriter(writeOffsetFile, true)));
			
			for(Map.Entry<String,StringBuilder> termString : termStrings.entrySet()){
				newIndexWriter.print(termString.getKey());
				newOffsetWriter.print(offset+" ");
				offset += termString.getKey().length();
				newIndexWriter.println(termString.getValue().toString());
				offset += ((termString.getValue().toString()).length() + 1);
				newOffsetWriter.println(offset-1);
			}
			newIndexWriter.close();
			newOffsetWriter.close();
			oldIndexFile.close();
			indexFileR.delete();
			offsetFileR.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		
		// Assign a unique id to each document.
		//ReadingDoc.assignNumToDocId();
		
		// Assign a unique id to each unique term from documents by their lengths
		//ReadingDoc.assignNumToTermsWOStemmed();
		
		//ReadingDoc.uniqueTermsCount(false, false);
		//ReadingDoc.uniqueTermsCount(false, true);
		//ReadingDoc.uniqueTermsCount(true, false);
		//ReadingDoc.uniqueTermsCount(true, true);
		
		//ReadingDoc.newDocIdsTermIDsByLength(true);
		//ReadingDoc.newDocIdsTermIDsByLength(false);
		ReadingDoc.newDocIDsAndLength();
		
		
		boolean removeStopWords = true; 
		boolean stemming = true;
		if (removeStopWords ==  false){
			if (stemming == true){
				ReadingDoc.indexing(false,true);
				ReadingDoc.mergeFile(ReadingDoc.tempOffsetWithStopWithStemmedFilePath, ReadingDoc.tempIndexWithStopWithStemmedFilePath,
						ReadingDoc.offsetWithStopWithStemmedFilePath, ReadingDoc.indexWithStopWithStemmedFilePath);
			}
			else{
				ReadingDoc.indexing(false,false);
				ReadingDoc.mergeFile(ReadingDoc.tempOffsetWithStopWOStemmedFilePath, ReadingDoc.tempIndexWithStopWOStemmedFilePath,
						ReadingDoc.offsetWithStopWOStemmedFilePath, ReadingDoc.indexWithStopWOStemmedFilePath);
			}
			
		}else{
			if (stemming == true){
				ReadingDoc.indexing(true,true);
				ReadingDoc.mergeFile(ReadingDoc.tempOffsetWOStopWithStemmedFilePath, ReadingDoc.tempIndexWOStopWithStemmedFilePath,
						ReadingDoc.offsetWOStopWithStemmedFilePath, ReadingDoc.indexWOStopWithStemmedFilePath);
			}
			else{
				ReadingDoc.indexing(true,false);
				ReadingDoc.mergeFile(ReadingDoc.tempOffsetWOStopWOStemmedFilePath, ReadingDoc.tempIndexWOStopWOStemmedFilePath,
						ReadingDoc.offsetWOStopWOStemmedFilePath, ReadingDoc.indexWOStopWOStemmedFilePath);
			}
		}
		
		//ReadingDoc.newDocIdsByDocLength();
		//ReadingDoc.newDocIdsByDocLength();
		//
		//ReadingDoc.indexingWOStopWords();
		
	}

}
