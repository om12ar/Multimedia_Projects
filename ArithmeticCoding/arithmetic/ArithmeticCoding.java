package arithmetic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ArithmeticCoding {

	static String DECOMPRESSED_FILE = "decomposed.txt";
	static String COMPRESSED_FILE = "compressed.txt";
	static String INPUT_FILE = "in.txt";

	static char[] buffer;

	static ArrayList<Pair<Character, Pair<Double, Double>>> AccumProb = new ArrayList<>();
	static ArrayList<Pair<Character, Double>> charProb = new ArrayList<>();
	static ArrayList<Pair<Character, Integer>> charCount = new ArrayList<>();

	static void compress(String path) {
		setPaths(path);
		readFileCompress();
		countChars();
		AccumulativeProbabilities();
		
		double code ;
		double low = 0, high = 1 ,range = 0;
		
		for (int i = 0; i < buffer.length; i++) {
			int idx = find(buffer[i]);

			range = high - low; 
			high = low + (range * AccumProb.get(idx).second.second);
			low = low + (range * AccumProb.get(idx).second.first);
			
				
			System.out.println("ArithmeticCoding.Compress():" + low + " "+high +" " + ((high - low) / 2));
		}

		code= low + ((high - low) / 2);
		
		writeFileCompress(code);
	}

	
	static void deCompress() {
		double code = readFileDecompress();

		AccumulativeProbabilities();
		double high =1,low=0 ;
		
		for(int i =0 ;i <buffer.length ; i++){
			
			int charidx =findRange(code);
			buffer[i]=AccumProb.get(charidx).first;
			 low = AccumProb.get(charidx).second.first;
			 high = AccumProb.get(charidx).second.second;
			double range = high - low ;
			
			code = (code - low )/range;
			
			
		}
		System.out.println(buffer);
		writeFileDecompress();
	}

	private static int findRange(double code) {
		for(int i =0 ;i < AccumProb.size() ; i++){
			if(AccumProb.get(i).second.first<=code && AccumProb.get(i).second.second>=code){
				return i;
			}
				
		}
		return -1;
	}


	private static void AccumulativeProbabilities() {
		AccumProb.clear();
		charProb.clear();
		
		for (int i = 0; i < charCount.size(); i++) {
			charProb.add(new Pair<Character, Double>(charCount.get(i).first,
					charCount.get(i).second.doubleValue() / buffer.length));
		}

		AccumProb.add(new Pair<Character, Pair<Double, Double>>(charProb.get(0).first,
				new Pair<Double, Double>(0.0, charProb.get(0).second)));
		
		for (int i = 1; i < charProb.size(); i++) {
			AccumProb.add(new Pair<Character, Pair<Double, Double>>(charProb.get(i).first, new Pair<Double, Double>(
					AccumProb.get(i - 1).second.second, AccumProb.get(i - 1).second.second + charProb.get(i).second)));
		}
		System.out.println("ArithmeticCoding.countChars()" + AccumProb.toString());

	}

	
	private static int find(char c) {
		for (int i = 0; i < AccumProb.size(); i++) {
			if (AccumProb.get(i).first == c) {
				return i;
			}
		}
		return -1;
	}

	static void countChars() {
		charCount.clear();
		for (int i = 0; i < buffer.length; i++) {
			boolean found = false;
			for (int j = 0; j < charCount.size(); j++) {
				if (charCount.get(j).first == buffer[i]) {
					found = true;
					charCount.get(j).second++;
				}
			}
			if (!found)
				charCount.add(new Pair<Character, Integer>(buffer[i], 1));
		}
	}

	static void setPaths(String path) {
		try {
			INPUT_FILE = path;
			int dotPlace = path.length() - 1;
			while (path.charAt(dotPlace) != '.') {
				dotPlace--;
			}
			COMPRESSED_FILE = path.substring(0, dotPlace) + "COMPRESSED" + path.substring(dotPlace, path.length());
			DECOMPRESSED_FILE = path.substring(0, dotPlace) + "DECOMPRESSED" + path.substring(dotPlace, path.length());

			File f = null;
			f = new File(COMPRESSED_FILE);
			f.createNewFile();
			f = new File(DECOMPRESSED_FILE);
			f.createNewFile();
		} catch (IOException ex) {
			Logger.getLogger(ArithmeticCoding.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	static void readFileCompress() {
		try {
			File inFile = new File(INPUT_FILE);
			FileReader fr = null;
			try {
				fr = new FileReader(inFile);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(ArithmeticCoding.class.getName()).log(Level.SEVERE, null, ex);
			}
			String s = "";

			int k, i = 0;
			while ((k = fr.read()) != -1) {
				// Print what is read
				s += (char) k;
			}

			buffer = s.toCharArray();

			System.out.print("\n");

			try {
				fr.close();
			} catch (IOException ex) {
				Logger.getLogger(ArithmeticCoding.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(ArithmeticCoding.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	static void writeFileCompress(double code) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(COMPRESSED_FILE));
			oos.writeDouble(code);
			oos.writeInt(buffer.length);
			oos.writeObject(charCount);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static double readFileDecompress() {
		charCount.clear();
		ObjectInputStream ois = null;
		double code = 0;
		try {
			ois = new ObjectInputStream(new FileInputStream(COMPRESSED_FILE));
			code = ois.readDouble();
			buffer = new char[ois.readInt()];
			charCount = (ArrayList<Pair<Character, Integer>>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return code;
	}

	static void writeFileDecompress() {
		try {
			File outFile = new File(DECOMPRESSED_FILE);
			FileWriter fr = null;
			fr = new FileWriter(outFile);

			fr.write(buffer);

			System.out.print("\n");

			fr.close();
		} catch (IOException ex) {
			Logger.getLogger(ArithmeticCoding.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}
