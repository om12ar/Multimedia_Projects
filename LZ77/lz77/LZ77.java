
package lz77;


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

public class LZ77 {

    static final byte SEARCH_WINDOW = 125;
    static final byte LOOK_AHEAD_WINDOW = 125;

    static  String DECOMPRESSED_FILE = "decomposed.txt";
    static  String COMPRESSED_FILE = "compressed.txt";
    static  String INPUT_FILE = "in.txt";

    static ArrayList<Tag> tags = new ArrayList<>();
    static char[] buffer;

    static void compress(String path) {
        
        setPaths(path);
        compressFileReader();

        for (int i = 0; i < buffer.length; i++) {
            int st = -1;
            int sz = -1;
            int maxSZ = -1;
            int maxST = -1;
            char next = 0;
            int ii;
            int jj;
            int Jlimit = Math.max(i - SEARCH_WINDOW, 0);

            for (int j = Jlimit; j < i; j++) {
                ii = i;
                jj = j;

                if (buffer[i] == buffer[j]) {
                    st = j;
                    sz = 0;
                    ii = i;
                    jj = j;

                    while (ii < buffer.length && jj < buffer.length && jj - Jlimit < LOOK_AHEAD_WINDOW && buffer[ii] == buffer[jj]) {
                        ii++;
                        jj++;
                        sz++;
                    }
                   
                    if (sz >= maxSZ) {
                        maxSZ = sz;
                        maxST = st;
                        next = buffer[Math.min(ii, buffer.length - 1)];
                    }
                }
            }
            if (st != -1) {
                tags.add(new Tag((byte) (i - maxST), (byte) (maxSZ), next));
                i += maxSZ;
            } else {
                tags.add(new Tag((byte) 0, (byte) 0, buffer[i]));
            }

        }
        System.err.println("Tags size :" + tags.size() + " ");
        System.err.println(tags);
        compressFileWriter();

    }

    static void deCompress() {
        deCompressFileReader();
        String decode = "";

        for (int i = 0; i < tags.size(); i++) {

            Tag tempTag = tags.get(i);
            int idx = Math.max(decode.length() - tempTag.index, 0);
            for (int j = 0; j < tempTag.length; j++) {
                decode += decode.charAt(idx);
                idx++;
            }
            decode += tempTag.nextCharacter;
        }

        buffer = new char[decode.length()];
        buffer = decode.toCharArray();
        deCompressFileWrite();
        
    }

    private static void setPaths(String path) {
        try {
            INPUT_FILE = path;
            int dotPlace = path.length()-1;
            while(path.charAt(dotPlace)!='.') dotPlace--;
            
            COMPRESSED_FILE = path.substring(0, dotPlace) + "COMPRESSED" + path.substring(dotPlace, path.length());
            DECOMPRESSED_FILE = path.substring(0, dotPlace) + "DECOMPRESSED" + path.substring(dotPlace, path.length());
            
            File f =null;
            f = new File(COMPRESSED_FILE);
            f.createNewFile();
            f = new File(DECOMPRESSED_FILE);
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        }
          
    }

    public LZ77() {

    }

    static void compressFileReader() {
        try {
            File inFile = new File(INPUT_FILE);
            FileReader fr = null;
            try {
                fr = new FileReader(inFile);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
            }
            String s = "";
            
            int k ,i=0;
            while((k=fr.read())!=-1)
            {                
                s+=(char)k;
            }
                    buffer= s.toCharArray();

            System.out.print("\n");
            
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void compressFileWriter() {
        
        try {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;

            fos = new FileOutputStream(COMPRESSED_FILE);

             oos = new ObjectOutputStream(fos);
             oos.writeObject(tags);

            oos.close();
            fos.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    static void deCompressFileReader() {
        tags.clear();
        try {
            FileInputStream fis = null;
            ObjectInputStream ois = null;

            fis = new FileInputStream(COMPRESSED_FILE);
            ois = new ObjectInputStream(fis);

            tags = (ArrayList< Tag>) ois.readObject();
            ois.close();
            fis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

    static void deCompressFileWrite() {
        try {
            File outFile = new File(DECOMPRESSED_FILE);
            FileWriter fr = null;
            fr = new FileWriter(outFile);

            fr.write(buffer);

            System.out.print("\n");

            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(LZ77.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
