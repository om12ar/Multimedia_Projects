package lzw;

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

public class LZW {

    static String DECOMPRESSED_FILE = "decomposed.txt";
    static String COMPRESSED_FILE = "compressed.txt";
    static String INPUT_FILE = "in.txt";

    static ArrayList<String> dictionary = new ArrayList<>();
    static ArrayList<Short> tags = new ArrayList<>();
    static char[] buffer;

    static void compress(String path) {

        setPaths(path);
        initialize();
        for (int i = 0; i < buffer.length; i++) {
            String sig = "" ;
            Short lastIndex = -1;
            Short idx = -1;
             sig += buffer[i];
             //i++;
            lastIndex = Find(sig);
            while (i  < buffer.length && ((idx = Find(sig)) != -1)) {
                i++;
                if(i==buffer.length){
                	lastIndex = Find(sig);
                	break;
                }
                sig += buffer[i];
                lastIndex = idx;
            }
            tags.add(lastIndex);
            
            if(Find(sig)==-1){            	
            	dictionary.add(sig);
            }
            	
            if(i==buffer.length){
            	break;
            }
            sig=sig.substring(sig.length()-1);
            i--;
            
            
        }
        
        System.out.println("Dictionary size:> "+tags.size());
        System.out.println("Dictionary : \n" + dictionary);
        System.out.println("Tags size:> "+tags.size());
        System.out.println("Tags : \n" +tags);
        
        saveToFileCompress();

    }

    static void deCompress() {
        readDictionary();
        String Dict = "";

        
        Dict += dictionary.get(tags.get(0));
        for (int i = 1; i < tags.size(); i++) {
        	
            if (tags.get(i)<dictionary.size()) {
               Find(dictionary.get(tags.get(i)));
                Dict += dictionary.get(tags.get(i));
                String toAdd = dictionary.get(tags.get(i - 1)) + dictionary.get(tags.get(i)).charAt(0);
                dictionary.add(toAdd);
            } else {
                String toAdd = dictionary.get(tags.get(i)) + dictionary.get(tags.get(i)).charAt(0);
                dictionary.add(toAdd);
                Dict += toAdd;
            }

        }
        
        buffer = new char[Dict.length()];
        buffer = Dict.toCharArray();
        saveToFileDecompress();        
    }

    private static void setPaths(String path) {
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
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Short Find(String sig) {
        for (short i = 0; i < dictionary.size(); i++) {
            if (dictionary.get(i).equals(sig)) {
                return i;
            }
        }
        return -1;
    }

    static void initialize() {
        try {
            File inFile = new File(INPUT_FILE);
          
            FileReader fr = null;
            try {
                fr = new FileReader(inFile);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
            }

            buffer = new char[(int)inFile.length()];
            fr.read(buffer);

            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }

        // initalize dictinory
        for (int i = 0; i < 128; i++) {
            String temp = "" + (char) i;
            dictionary.add(temp);
        }

    }

    static void saveToFileCompress() {

        try {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;

            fos = new FileOutputStream(COMPRESSED_FILE);

            oos = new ObjectOutputStream(fos);
            oos.writeObject(dictionary);

            oos.close();
            fos.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static void readDictionary() {
        dictionary.clear();
        try {
            FileInputStream fis = null;
            ObjectInputStream ois = null;

            fis = new FileInputStream(COMPRESSED_FILE);
            ois = new ObjectInputStream(fis);
            dictionary = (ArrayList< String>) ois.readObject();
            ois.close();
            fis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static void saveToFileDecompress() {
        try {
            File outFile = new File(DECOMPRESSED_FILE);
            FileWriter fr = null;
            fr = new FileWriter(outFile);

            fr.write(buffer);

            System.out.print("\n");

            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
