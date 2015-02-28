import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ReadingDoc {
	
	public static ArrayList<DBlock> getInvertedList(String term,String fileName){
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		
		ArrayList<DBlock> dBlocks = new ArrayList<DBlock>();
		
		boolean fileFound = false;
		for(File file: folder.listFiles()){
			
			if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
				
				if (fileFound == false){
					if(fileName.equals(file.getName())){
						fileFound = true;
					}else{
						continue;
					}
				}
	
				if (fileFound == true){
					try{
						BufferedReader br = new BufferedReader(new FileReader(file));
						String docId="";
						Boolean textStarted = false;
						short termIndex = 1;
						String line;
						Short termFreqency = 0;
						ArrayList<Short> termPositions = null;
						
						while((line = br.readLine()) != null){
							if (line.contains("<DOCNO>")){
								String words[]=line.split(" ");
								docId = words[1];
								termIndex = 1;
								termFreqency = 0;
								termPositions = new ArrayList<Short>();
							}else if (line.contains("<TEXT>")){
								textStarted = true;
							}else if (textStarted == true){
								if (!(line.contains("</TEXT>"))){
									Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
									Matcher matcher = pattern.matcher(line.toLowerCase());
									while (matcher.find()) {
										for (int i = 0; i < matcher.groupCount(); i++) {
											String foundTerm = matcher.group(i);
											termIndex++;
											if (foundTerm.equals(term)){
												termFreqency++;
												termPositions.add(termIndex);
											}
										}
									}
									
								}else{
									textStarted = false;
								}
							}else if(line.contains("</DOC>")){
								if (termFreqency != 0){
									DBlock db = new DBlock();
									db.setDocId(docId);
									db.setTermFreq(termFreqency);
									db.setPositions(termPositions);
									dBlocks.add(db);
								}
							}
						}
					br.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
				
		return dBlocks;
	}
	
	public static void readFolder(){
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		
		Map<String, ArrayList<DBlock>> termsDBlock = new HashMap<String, ArrayList<DBlock>>();;
		ArrayList<DBlock> tempDBlocks = null;
		int fileCounter = 0;
		for(File file: folder.listFiles()){
			
			if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
				try {
					fileCounter++;
					BufferedReader br = new BufferedReader(new FileReader(file));
					String docId="";
					Boolean textStarted = false;
					short termIndex = 1;
					String line;
					DefaultHashMap<String, Short> termFreqency = null;
					Map<String, ArrayList<Short>> termPositions = null;
					ArrayList<Short> temp = null;
					ArrayList<String> terms = null;
					while((line = br.readLine()) != null){
						if (line.contains("<DOCNO>")){
							String words[]=line.split(" ");
							docId = words[1];
							termIndex = 1;
							termFreqency = new DefaultHashMap<String, Short>(new Short((short) 0));
							termPositions = new HashMap<String, ArrayList<Short>>();
							terms = new ArrayList<String>();
						}else if (line.contains("<TEXT>")){
							textStarted = true;
						}else if (textStarted == true){
							if (!(line.contains("</TEXT>"))){
								Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
								Matcher matcher = pattern.matcher(line.toLowerCase());
								while (matcher.find()) {
									for (int i = 0; i < matcher.groupCount(); i++) {
										String term = matcher.group(i);
										terms.add(term);
										//System.out.println(term);
										termFreqency.put(term,(short) (termFreqency.get(term)+1));
										if (termPositions.get(term) != null)
											termPositions.get(term).add(new Short(termIndex));
										else{
											ArrayList<Short> pos = new ArrayList<Short>();
											pos.add(new Short(termIndex));
											termPositions.put(term, pos);
										}
										//int startIndex = matcher.start();
										//int endIndex = matcher.end();
										//System.out.println(termIndex + " " + term + " " + startIndex
										//		+ " " + endIndex);
										termIndex++;
									}
								}
								
							}else{
								textStarted = false;
							}
						}else if(line.contains("</DOC>")){
							
							for(String term : terms){
								DBlock db = new DBlock();
								db.setDocId(docId);
								db.setTermFreq(termFreqency.get(term));
								db.setPositions(termPositions.get(term));
								tempDBlocks = termsDBlock.get(term);
								if (tempDBlocks != null){
									tempDBlocks.add(db);
									termsDBlock.put(term,tempDBlocks);
								}else{
									ArrayList<DBlock> dBlock = new ArrayList<DBlock>();
									dBlock.add(db);
									termsDBlock.put(term,dBlock);
								}
							}
						}
						
					} 
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		Iterator it = termsDBlock.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue().toString());
	    }
	    
	}
	
	public static Map<String, String> getUniqueTerms(){
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		
		Map<String, String> terms = new HashMap<String, String>();
		for(File file: folder.listFiles()){
			
			if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
				try {
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
										String term = matcher.group(i);
										if (terms.get(term) == null)
											terms.put(term, file.getName());
									}
								}
								
							}else{
								textStarted = false;
							}
						}
						
					} 
					br.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		return terms;
	}
	
	public static void indexing(Map<String, String> terms){
		
		Iterator<Entry<String, String>> termIterator = terms.entrySet().iterator();
		short counter = 0;
		int number =0;
		ArrayList<TermData> termsData = new ArrayList<TermData>();
		//Map<String, ArrayList<DBlock>> termsDBlock = new HashMap<String, ArrayList<DBlock>>();
		while(termIterator.hasNext()){
			long startTime = System.currentTimeMillis();
			Map.Entry pair = (Map.Entry)termIterator.next();
			counter++;
			System.out.println("Counter "+counter);
			ArrayList<DBlock> dBlocks = getInvertedList(pair.getKey().toString(), pair.getValue().toString());
			//termsDBlock.put(pair.getKey().toString(), dBlock);
			TermData termData = new TermData();
			termData.setName(pair.getKey().toString());
			termData.setDblocks(dBlocks);
			termsData.add(termData);
	        if (counter == 10){
	        	// Write the dblocks into the files.
	        	try {
		        	FileOutputStream fout;
		        	ObjectOutputStream oos ;
					File file = new File("/Users/Pramukh/Documents/Information Retrieval Data/HW2/index.txt");
					fout = new FileOutputStream(file,true);
					oos = new ObjectOutputStream(fout);  
		        	for(TermData tempTermData : termsData){
			    		oos.writeObject(tempTermData);
		        	}
		        	oos.close();
		        	fout.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	counter = 0;
	        	termsData = new ArrayList<TermData>();
	        	System.out.println("Done for interation "+number + (System.currentTimeMillis() - startTime));
	        	number++;
	        	//termsDBlock = new HashMap<String, ArrayList<DBlock>>();
	        }
		}
		
	}
	
	
	public static void indexByDoc(){
		
		File folder = new File("/Users/Pramukh/Documents/Information Retrieval Data/AP_DATA/ap89_collection/");
		
		Map<String, ArrayList<DBlock>> termsDBlock = new HashMap<String, ArrayList<DBlock>>();;
		ArrayList<DBlock> tempDBlocks = null;
		int docCounter = 0;
		int byteOffset = 0;
		for(File file: folder.listFiles()){
			
			if(file.isFile() && !((file.getName().endsWith("readme") || (file.getName().endsWith(".DS_Store"))))){
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String docId="";
					Boolean textStarted = false;
					short termIndex = 1;
					String line;
					DefaultHashMap<String, Short> termFreqency = null;
					Map<String, ArrayList<Short>> termPositions = null;
					ArrayList<Short> temp = null;
					ArrayList<String> terms = null;
					while((line = br.readLine()) != null){
						if (line.contains("<DOCNO>")){
							String words[]=line.split(" ");
							docId = words[1];
							termIndex = 1;
							termFreqency = new DefaultHashMap<String, Short>(new Short((short) 0));
							termPositions = new HashMap<String, ArrayList<Short>>();
						}else if (line.contains("<TEXT>")){
							textStarted = true;
						}else if (textStarted == true){
							if (!(line.contains("</TEXT>"))){
								Pattern pattern = Pattern.compile("\\w+(\\.?\\w+)*");
								Matcher matcher = pattern.matcher(line.toLowerCase());
								while (matcher.find()) {
									for (int i = 0; i < matcher.groupCount(); i++) {
										String term = matcher.group(i);
										//System.out.println(term);
										termFreqency.put(term,(short) (termFreqency.get(term)+1));
										if (termPositions.get(term) != null)
											termPositions.get(term).add(new Short(termIndex));
										else{
											ArrayList<Short> pos = new ArrayList<Short>();
											pos.add(new Short(termIndex));
											termPositions.put(term, pos);
										}
										//int startIndex = matcher.start();
										//int endIndex = matcher.end();
										//System.out.println(termIndex + " " + term + " " + startIndex
										//		+ " " + endIndex);
										termIndex++;
									}
								}
								
							}else{
								textStarted = false;
							}
						}else if(line.contains("</DOC>")){
							docCounter++;
							System.out.println("In iteration"+docCounter);
							Iterator termInterator = termFreqency.entrySet().iterator();
							while(termInterator.hasNext()){
								Map.Entry<String, Short> pair = (Map.Entry<String, Short>)termInterator.next();
								String term = pair.getKey().toString();
								DBlock db = new DBlock();
								db.setDocId(docId);
								db.setTermFreq(pair.getValue());
								db.setPositions(termPositions.get(term));
								tempDBlocks = termsDBlock.get(term);
								if (tempDBlocks != null){
									tempDBlocks.add(db);
									termsDBlock.put(term,tempDBlocks);
								}else{
									ArrayList<DBlock> dBlock = new ArrayList<DBlock>();
									dBlock.add(db);
									termsDBlock.put(term,dBlock);
								}
							}
							
							if (docCounter == 1000){
								docCounter = 0;
								// Write into the files.
								try {
									Iterator termDBlockIterator = termsDBlock.entrySet().iterator();
									
									String indexFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/indexWOStepWO.txt";
									String offsetFilePath = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/offset.txt";
									
									PrintWriter indexWriter = new PrintWriter(new BufferedWriter(new FileWriter(indexFilePath, true)));
									PrintWriter offsetWriter = new PrintWriter(new BufferedWriter(new FileWriter(offsetFilePath, true)));
								
									
									while(termDBlockIterator.hasNext()){
										int termStartOffset = byteOffset;
										StringBuilder outString = new StringBuilder();
										ArrayList<Byte> bytes = new ArrayList<Byte>();
										Map.Entry pair = (Map.Entry)termDBlockIterator.next();
										indexWriter.print(pair.getKey().toString()+" ");
										offsetWriter.print(pair.getKey().toString()+" ");
										offsetWriter.print(termStartOffset);
										byteOffset += ((pair.getKey().toString().length() + 1));
										
										int listLength = ((ArrayList<DBlock>)pair.getValue()).size();
										for (int i = 0; i < (listLength-1) ; i++){
											DBlock db = ((ArrayList<DBlock>)pair.getValue()).get(i);
											String subDocId = db.getDocId().substring(4);
											indexWriter.print(subDocId+":");
											byteOffset += ((subDocId.length() + 1));
											indexWriter.print(db.getTermFreq());
											byteOffset += db.getTermFreq().toString().length();
											indexWriter.print(":");
											byteOffset += 1;
											for(Short pos : db.getPositions()){
												indexWriter.print(pos);
												indexWriter.print(",");
												byteOffset += (1 + (pos.toString().length()));
											}
											indexWriter.print(" ");
											byteOffset += 1;
										}
										if(listLength != 0){
											DBlock db = ((ArrayList<DBlock>)pair.getValue()).get(listLength-1);
											String subDocId = db.getDocId().substring(4);
											indexWriter.print(subDocId+":");
											byteOffset += ((subDocId.length() + 1));
											indexWriter.print(db.getTermFreq());
											byteOffset += db.getTermFreq().toString().length();
											indexWriter.print(":");
											byteOffset += 1;
											ArrayList<Short> positions = db.getPositions();
											int lenPos = positions.size();
											for(int i=0;i < (lenPos - 1);i++){
												Short pos = positions.get(i);
												indexWriter.print(pos);
												indexWriter.print(",");
												byteOffset += (1 + (pos.toString().length()));
											}
											if (lenPos != 0){
												Short pos = positions.get(lenPos-1);
												indexWriter.println(pos);
												byteOffset += 1;
												byteOffset += ((pos.toString().length()));
											}
										}
										offsetWriter.print(" ");
										offsetWriter.println(byteOffset-1);
									}
									offsetWriter.close();
									indexWriter.close();
									termsDBlock =  new HashMap<String, ArrayList<DBlock>>();
						        	
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					} 
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		
		// Now merging the file.
	    
	}
	
	
	public static void main(String args[]){
		//ReadingDoc.readFolder();	
		//System.out.println(ReadingDoc.getInvertedList("hello","ap890101"));
		//Map map = ReadingDoc.getUniqueTerms();
		//ReadingDoc.indexing(map);
		
		ReadingDoc.indexByDoc();
	}

}
