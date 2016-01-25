package predictor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class Predictor {

	static String DECOMPRESSED_FILE = "decompressed.png";
	static String COMPRESSED_FILE = "compressed.txt";
	static String INPUT_FILE = "in.png";
	static int QUANTIZER_LEVELS = 64;	
	
	static void compress(String path) {

		 setPaths(path);

		ArrayList<Integer> quantizer = new ArrayList<Integer>(getQuantizer());
		
		ArrayList<ArrayList<Integer>> original = new ArrayList<ArrayList<Integer>>(compressRead());
		
		ArrayList<ArrayList<Integer>> sample = new ArrayList<ArrayList<Integer>>(Adaptive2DPredictor(original));

		ArrayList<ArrayList<Integer>> diff = new ArrayList<ArrayList<Integer>>(diffrance(original, sample));
		
		ArrayList<ArrayList<Integer>> Q = new ArrayList<ArrayList<Integer>>(Quantize(diff, quantizer));
	
		compressWrite(Q , original);

	}

	static void deCompress() {
		
		ArrayList<ArrayList<ArrayList<Integer>>> returns = new ArrayList<ArrayList<ArrayList<Integer>>>(decompressRead());
		ArrayList<ArrayList<Integer>> Q = new ArrayList<ArrayList<Integer>>(returns.get(0));
		ArrayList<ArrayList<Integer>> original = new ArrayList<ArrayList<Integer>>(returns.get(1));
		ArrayList<ArrayList<Integer>> sample = new ArrayList<ArrayList<Integer>>(Adaptive2DPredictor(original));
		for(int i =0 ;i < sample.size();i++){
			System.out.print( sample.get(i).get(0) + ", ");
		}
		ArrayList<Integer> quantizer = new ArrayList<Integer>(getQuantizer());
		ArrayList<ArrayList<Integer>> Q_1 = new ArrayList<ArrayList<Integer>>(DeQuantize(Q, quantizer));	
		ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>(addDiff(sample, Q_1));
	
		decompressWrite(output , DECOMPRESSED_FILE);
	}



	private static ArrayList<ArrayList<Integer>> addDiff(ArrayList<ArrayList<Integer>> sample,
			ArrayList<ArrayList<Integer>> q_1) {
		ArrayList<ArrayList<Integer>> dsample = new ArrayList<ArrayList<Integer>>();
		deepCopy(sample, dsample);
		for(int i =1 ;i <dsample.size();i++){
			for(int j =1 ;j <dsample.get(0).size();j++){
				dsample.get(i).set(j, dsample.get(i).get(j)+q_1.get(i).get(j));
			}
		}
		return dsample;
	}

	private static ArrayList<ArrayList<Integer>> DeQuantize(ArrayList<ArrayList<Integer>> diff,
			ArrayList<Integer> quantizer) {
		ArrayList<ArrayList<Integer>> Q_1 = new ArrayList<ArrayList<Integer>>();
		deepCopy(diff, Q_1);
		int levelSize = (int) Math.ceil(512 / (double) QUANTIZER_LEVELS) ;
	//	System.err.println(diff);
		for (int i = 1; i < diff.size(); i++) {
			for (int j = 1; j < diff.get(0).size(); j++) {
				int k = diff.get(i).get(j);
				Q_1.get(i).set(j, quantizer.get(k) +(levelSize/ 2));
			}

		}
		return Q_1;
	}

	private static ArrayList<ArrayList<Integer>> Quantize(ArrayList<ArrayList<Integer>> diff,
			ArrayList<Integer> quantizer) {
		ArrayList<ArrayList<Integer>> Q = new ArrayList<ArrayList<Integer>>();
		deepCopy(diff, Q);
		for (int i = 1; i < diff.size(); i++) {
			for (int j = 1; j < diff.get(0).size(); j++) {
				for (int k = 0; k < quantizer.size(); k++) {
					if (diff.get(i).get(j) < quantizer.get(k)) {
						
						Q.get(i).set(j, k);
						break;
					}
				}
			}
		}
		return Q;
	}

	private static void deepCopy(ArrayList<ArrayList<Integer>> source, ArrayList<ArrayList<Integer>> destination) {
		for (ArrayList<Integer> ai : source) {
			ArrayList<Integer> T = new ArrayList<Integer>();
			for (Integer i : ai) {
				T.add(new Integer(i));
			}
			destination.add(T);
		}

	}

	private static ArrayList<Integer> getQuantizer() {
		ArrayList<Integer> quantizer = new ArrayList<Integer>();
		
		
		int levelSize = (int) Math.ceil(512 / (double) QUANTIZER_LEVELS) ;
		int level = -256 ;
		for (int i = 0; i < QUANTIZER_LEVELS; i++) {
			quantizer.add(level);
			level+=levelSize;
			
		}
		quantizer.add(256);
		return quantizer;
	}


	private static ArrayList<ArrayList<Integer>> diffrance(ArrayList<ArrayList<Integer>> original,
			ArrayList<ArrayList<Integer>> sample) {

		ArrayList<ArrayList<Integer>> diff = new ArrayList<ArrayList<Integer>>();
		deepCopy(original, diff);

		for (int i = 1; i < sample.size(); i++) {
			for (int j = 1; j < sample.get(0).size(); j++) {
				diff.get(i).set(j, original.get(i).get(j) - sample.get(i).get(j));
			}
		}

		return diff;
	}

	private static ArrayList<ArrayList<Integer>> Adaptive2DPredictor(ArrayList<ArrayList<Integer>> original) {
		ArrayList<ArrayList<Integer>> sample = new ArrayList<ArrayList<Integer>>();
		deepCopy(original, sample);

		for (int i = 1; i < sample.size(); i++) {
			for (int j = 1; j < sample.get(0).size(); j++) {
				int a = sample.get(i).get(j - 1);
				int b = sample.get(i - 1).get(j - 1);
				int c = sample.get(i - 1).get(j);
				if (b <= Math.min(a, c)) {
					sample.get(i).set(j, Math.max(a, c));
				} else if (b >= Math.max(a, c)) {
					sample.get(i).set(j, Math.min(a, c));
				} else {
					sample.get(i).set(j, a + c - b);
				}
			}
		}
		return sample;
	}

	static String formatToLength(String binaryString, int len) {
		while (binaryString.length() < len) {
			binaryString = "0" + binaryString;

		}

		return binaryString;
	}

	static void setPaths(String path) {

		INPUT_FILE = path;
		int dotPlace = path.length() - 1;
		while (path.charAt(dotPlace) != '.') {
			dotPlace--;
		}
		COMPRESSED_FILE = path.substring(0, dotPlace) + "_COMPRESSED.txt";
		DECOMPRESSED_FILE = path.substring(0, dotPlace) + "_DECOMPRESSED.png";

	}

	static ArrayList<ArrayList<Integer>> compressRead() {
		BufferedImage img = null;
		Raster raster = null;
		try {
			File f = new File(INPUT_FILE);
			img = ImageIO.read(f);
			raster = img.getData();

		} catch (IOException e) {

			e.printStackTrace();
		}

		raster.getSampleModel();
		int w = raster.getWidth(), h = raster.getHeight();

		ArrayList<ArrayList<Integer>> pixels = new ArrayList<ArrayList<Integer>>();
		for (int x = 0; x < w; x++) {
			ArrayList<Integer> row = new ArrayList<>();
			for (int y = 0; y < h; y++) {
				row.add(raster.getSample(x, y, 0));
			}
			pixels.add(row);
		}
		return pixels;
	}

	private static void compressWrite(ArrayList<ArrayList<Integer>> q, ArrayList<ArrayList<Integer>> sample) {
		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(new FileOutputStream(COMPRESSED_FILE));
			ArrayList<Integer> firstRow = new ArrayList<Integer>(sample.get(0));
			ArrayList<Integer> firstCol = new ArrayList<Integer>();
			for(int i =0;i<sample.get(0).size();i++)
				firstCol.add(sample.get(i).get(0));
			
			
			oos.writeObject(firstRow);
			oos.writeObject(firstCol);
			oos.writeObject(q);
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	static ArrayList<ArrayList<ArrayList<Integer>>> decompressRead() {

		ObjectInputStream ois = null;
		ArrayList<ArrayList<Integer>> sample = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> q;
		ArrayList<ArrayList<ArrayList<Integer>>> returns = new ArrayList<ArrayList<ArrayList<Integer>>>();
		try {
			ois = new ObjectInputStream(new FileInputStream(COMPRESSED_FILE));
			ArrayList<Integer> firstRow = new ArrayList<>((ArrayList<Integer>) ois.readObject());
			ArrayList<Integer> firstCol = new ArrayList<>((ArrayList<Integer>) ois.readObject());
			q = new ArrayList<>((ArrayList<ArrayList<Integer>>) ois.readObject());
			deepCopy(q, sample);
			for(int i =0 ; i <firstRow.size() ; i++){
				sample.get(0).set(i, firstRow.get(i));
			}
			for(int i =0 ; i < firstCol.size() ; i++){
				sample.get(i).set(0, firstCol.get(i));
				System.out.print(sample.get(i).get(0)+ " ,");
				
			}
			System.out.println();
			ois.close();
			
			returns.add(q);
			returns.add(sample);
			return returns ;
		} catch (IOException | ClassNotFoundException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		

	
		
	}

	static void decompressWrite(ArrayList<ArrayList<Integer>> pixels, String Filename) {
		int w = pixels.size();
		int h = pixels.get(0).size();

		WritableRaster raster = Raster.createWritableRaster(
				new PixelInterleavedSampleModel(0, w, h, 1, 1920, new int[] { 0 }), new Point(0, 0));

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				raster.setSample(i, j, 0, pixels.get(i).get(j));
			}
		}

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		image.setData(raster);
		

		File output = new File(Filename);
		try {
			ImageIO.write(image, "png", output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

}

