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
import java.util.HashMap;
import java.util.Map;


public class FileOperations {
	
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
	
	public static HashMap<String,Long> getOffsets(boolean removeStopWords,boolean stemming){
		
		String offsetFilePath = null;
		HashMap<String,Long> offsets = new HashMap<String,Long>();
		if ((removeStopWords == true) && (stemming == true))
			offsetFilePath = FileOperations.offsetWOStopWithStemmedFilePath;
		else if ((removeStopWords == true) && (stemming == false))
			offsetFilePath = FileOperations.offsetWOStopWOStemmedFilePath;
		else if ((removeStopWords == false) && (stemming == false))
			offsetFilePath = FileOperations.offsetWithStopWOStemmedFilePath;
		else
			offsetFilePath = FileOperations.offsetWithStopWithStemmedFilePath;

			FileReader fr;
			try {
				fr = new FileReader(offsetFilePath);
				BufferedReader br =  new BufferedReader(fr);
				String currentLine;
				while((currentLine = br.readLine()) != null){
					String str[] = currentLine.split(" ");
						offsets.put(str[0],Long.parseLong(str[1]));
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return offsets;
	}
	
	
	public static String getLine(boolean removeStopWords, boolean stemming, long location){
		
		RandomAccessFile indexFile=null;
		try {
			if ((removeStopWords == true) && (stemming == true))
				indexFile = new RandomAccessFile(FileOperations.indexWOStopWithStemmedFilePath, "r");
			else if ((removeStopWords == true) && (stemming == false))
				indexFile = new RandomAccessFile(FileOperations.indexWOStopWOStemmedFilePath, "r");
			else if ((removeStopWords == false) && (stemming == false))
				indexFile = new RandomAccessFile(FileOperations.indexWithStopWOStemmedFilePath, "r");
			else
				indexFile = new RandomAccessFile(FileOperations.indexWithStopWithStemmedFilePath, "r");
		
			indexFile.seek(location);
			return indexFile.readLine();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static HashMap<String,Integer> getDocumentLength(boolean removeStopWords){
		
		HashMap<String,Integer> docLengths = new HashMap<String,Integer>();
		try {
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/docdetails.txt";
			File newDocLengthFile = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(newDocLengthFile));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				String[] pars = currentLine.split(" ");
				if (removeStopWords == true)
					docLengths.put(pars[0],Integer.parseInt(pars[2]));
				else
					docLengths.put(pars[0],Integer.parseInt(pars[3]));
			}	
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docLengths;
	}
	
	public static float getAverageDocumentLength(boolean removeStopWords){
		
		int totalDoc = 0;
		float totalDocLength = 0;
		try {
			String path = "/Users/Pramukh/Documents/Information Retrieval Data/HW2/docdetails.txt";
			File newDocLengthFile = new File(path);
			BufferedReader br;
			br = new BufferedReader(new FileReader(newDocLengthFile));
			String currentLine;
			
			while((currentLine = br.readLine()) != null){
				totalDoc++;
				String[] pars = currentLine.split(" ");
				if (removeStopWords == true)
					totalDocLength +=Integer.parseInt(pars[2]);
				else
					totalDocLength +=Integer.parseInt(pars[3]);
			}	
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (float)totalDocLength/totalDoc;
	}

	public static void writeResult(String qnumber,Map<String, Float> sortedDoc,String filePath,int size){
		
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			int rank = 1;
			for(Map.Entry<String, Float> doc: sortedDoc.entrySet()){
				StringBuilder str =new StringBuilder();
				str.append(qnumber+" Q0 ");
				str.append(doc.getKey()+" "+rank+" "+doc.getValue()+" ");
				str.append("Exp");
				writer.println(str.toString());
				if(rank == size)
					break;
				rank++;
			}
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
