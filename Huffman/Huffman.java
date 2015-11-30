package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Huffman {

	static String DECOMPRESSED_FILE = "decomposed.txt";
	static String COMPRESSED_FILE = "compressed.txt";
	static String INPUT_FILE = "in.txt";

	static ArrayList<Node> tree = new ArrayList<>();
	static HashMap<Character, String> dictionary = new HashMap<>();
	static HashMap<Character, Integer> charCount = new HashMap<>();
	

	static void compress(String path) {
		setPaths(path);
		String buffer = readFileCompress();
		countChars(buffer);
		initializeTree();
		reduceTree();
		setCodes(tree.get(0), "");
		buildDictionary(tree.get(0));
		
		String encode = "";
		for (int i = 0; i < buffer.length(); i++) {
			encode += dictionary.get(buffer.charAt(i));
		}
		
		System.err.println("compressed Text : " +buffer);
		System.err.println("Huffman.dictionary : "+dictionary);
		System.err.println("Huffman.charCount : "+ charCount);
		System.err.println("Compress Stream : " + encode);
		
		writeFileCompress(encode);
		

	}

	static void deCompress() {
		String buffer = readFileDecompress();
		initializeTree();
		reduceTree();
		setCodes(tree.get(0), "");
		buildDictionary(tree.get(0));

		String dict = "";
		String sig = "";
		for (int i = 0; i < buffer.length(); i++) {

			sig += buffer.charAt(i);
			if (Find(sig) == -1) {
				continue;
			} else {
				int idx = Find(sig.substring(0, sig.length()));
				dict += (char) idx;

				sig = "";
			}
		}
		
		System.err.println("Decompressed Text : " +dict);
		writeFileDecompress(dict);
	}

	public static class compareNodes implements Comparator<Node> {
		@Override
		public int compare(Node o1, Node o2) {
			return o1.getCount().compareTo(o2.getCount());
		}
	}

	public static void setCodes(Node n, String s) {
		if (n.getLeft() != null)
			setCodes(n.getLeft(), s + "0");
		if (n.getRight() != null)
			setCodes(n.getRight(), s + "1");
		n.setCode(s);	
	}

	public static void buildDictionary(Node n) {
		if (n.getLeft() != null)
			buildDictionary(n.getLeft());
		if (n.getRight() != null)
			buildDictionary(n.getRight());
		if (n.getChars().length() == 1) {
			dictionary.put(n.getChars().charAt(0), n.getCode());
		}
	}

	static void initializeTree() {
		Set<Map.Entry<Character, Integer>> entrySet = charCount.entrySet();
		for (Map.Entry<Character, Integer> entry : entrySet) {
			tree.add(new Node(entry.getKey().toString(), entry.getValue()));
		}

	}

	static void countChars(String buffer) {
		for (int i = 0; i < buffer.length(); i++) {
			if (charCount.get(buffer.charAt(i)) == null) {
				charCount.put(buffer.charAt(i), 1);
			} else
				charCount.replace(buffer.charAt(i), charCount.get(buffer.charAt(i)) + 1);
		}

	}

	static void reduceTree() {
		Collections.sort(tree, new compareNodes());
		while (tree.size() != 1) {
			Node t = new Node();
			t.addNode(tree.get(0));
			t.addNode(tree.get(1));
			t.setLeft(tree.get(0));
			t.setRight(tree.get(1));
			tree.remove(0);
			tree.remove(0);
			tree.add(t);
			Collections.sort(tree, new compareNodes());
		}

	}

	static int Find(String sig) {
		
		Iterator<Entry<Character, String>> iter = dictionary.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Character, String> entry = iter.next();
			if (entry.getValue().equals(sig)) {
				return (int) entry.getKey();
			}
		}
		return -1;
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
			Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	static String readFileCompress() {
		String s = "";
		try {
			File inFile = new File(INPUT_FILE);
			FileReader fr = null;
			try {
				fr = new FileReader(inFile);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
			}
			

			int k, i = 0;
			while ((k = fr.read()) != -1) {
				s += (char) k;
			}

			try {
				fr.close();
			} catch (IOException ex) {
				Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
		}
		return s;
	}

	static void writeFileCompress(String encode) {
		int bitCount = encode.length();
		
		while (encode.length() % 8 != 0) {
			encode += "0";
		}
		try {
			ObjectOutputStream oos = null;
			oos = new ObjectOutputStream(new FileOutputStream(COMPRESSED_FILE));
			oos.writeObject(charCount);
			oos.writeInt(bitCount);
			
			int startPointer = 0 , endPointer = 7;
			int numOfBytesWrittenToFile =0;
			while(startPointer < encode.length()){
				if(endPointer > encode.length()){
					endPointer = encode.length();
				}
				Byte number = Byte.parseByte(encode.substring(startPointer, endPointer), 2);
				oos.writeByte(number);
				startPointer = endPointer;
				endPointer += 7;
				numOfBytesWrittenToFile++;
			}
			
			System.err.println("numOfBytesWrittenToFile :"+numOfBytesWrittenToFile);
			oos.close();
		} catch (IOException ex) {
			System.out.println("FileNotFoundException : " + ex);
		}

	}

	static String readFileDecompress() {
		charCount.clear();
		dictionary.clear();
		tree.clear();
		
		ObjectInputStream ois = null;
		FileInputStream fis  = null ;
		int bitCount = 0;
		
		String decode = "";
		try {
			fis = new FileInputStream(COMPRESSED_FILE);
			ois = new ObjectInputStream(fis);
			charCount = (HashMap) ois.readObject();
			bitCount = (int) ois.readInt();
			String zeros = "0000000";
			for(int i = bitCount ; i > 0; i-=7){
				byte stream = ois.readByte();
				String streamInString = Integer.toBinaryString(stream);
				
				if (i < 7){
					
					decode += zeros.substring(0, i - streamInString.length());
				}
				else if(streamInString.length() < 7){
					
					decode += zeros.substring(streamInString.length());
				}
				
				decode += streamInString;
				
			}
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return decode;
	}

	static void writeFileDecompress(String decode) {
		try {
			File outFile = new File(DECOMPRESSED_FILE);
			FileWriter fr = null;
			fr = new FileWriter(outFile);

			fr.write(decode.toCharArray());

			//System.out.print("\n");

			fr.close();
		} catch (IOException ex) {
			Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}
