package com.set.classifier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.junit.experimental.categories.Categories;

import com.set.index.impl.IndexWriter;
import com.set.index.impl.PostionalInvertedIndexImpl;
import com.set.math.EuclidianNormalization;
import com.set.token.impl.SimpleTokenStreamImpl;
import com.set.token.processing.pre.PorterStemmer;

public class Rocchio {
	private static String[] dictionary = null;
	private static HashMap<String, Double[]>  classCentroids = new HashMap<>();
	static PostionalInvertedIndexImpl indexMaster = new PostionalInvertedIndexImpl();
	private static int classCount = 0;
	// scanner - to index all - build dictionary with tftd
	// loop	- to get class folder
		// use the buildCentroidVector
		// save centroid in a hashmap <String, Double[]> // string folder name // double the computed vector
	// scanner - ask for a test document
		// calculate the normalized vector for this new document // you could use the same funciton buildCentroidVector
		// compute eculidian distance for the all the vectors
		// find the smallest distance
		// print out the folder name this files has to be in
	// END
	
	public static void main(String[] args) throws IOException {
		// build vd vector
		// normalize it
		// get the normalized vector dimension
		// get centroid
		// to add two vectors, they should be of the same dimensions
		// divide the size of the class
		// Number of dimensions of this vector is equal to the terms in the doc 
		System.out.println("Directory to index:");
//		Scanner folder= new Scanner(System.in);
//		String s= folder.nextLine();
		// test data
		String s = "C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\ALL";
		indexFiles(s, indexMaster, true);
		System.out.println("Enter the number of classes:");
//		int num = Integer.parseInt(folder.nextLine());
//		classCount = num;
		// test data
		int num = 4;
		classCount = 4;
		String[] trainingSet = new String[4];
		trainingSet[0] = "C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\HAMILTON";
		trainingSet[1] = "C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\HAMILTON AND MADISON";
		trainingSet[2] = "C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\JAY";
		trainingSet[3] = "C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\MADISON";
		for(int i=0;i<num;i++){
			System.out.println("Enter path for class "+ (i+1) + " :");
//			String className = folder.nextLine();
			// test data
			String className = trainingSet[i];
			classOfFiles(className);
		}
		
		System.out.println("Enter path for classification:");
		//Path currentWorkingPath = Paths.get().toAbsolutePath();
//		File f = new File(folder.nextLine());
		// test data
		File f = new File("C:\\Users\\vllmstl\\Desktop\\Fall 2015\\SET\\Federalist_ByAuthors\\HAMILTON OR MADISON");
	    FilenameFilter textFilter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".txt");
	        }
	    };

	    File[] files = f.listFiles(textFilter);
	    for (File file : files) {
	        if (!file.isDirectory()) {
	        	HashMap<String, Integer> current =indexFile(file, null, -1);
	        	List<HashMap<String, Integer>> cover= new ArrayList<>();
	        	cover.add(current);
	        	buildCentroidVector(cover,file.toString(),false);
	        	System.out.println();
	        }
	    }
	}

	public static String buildCentroidVector(List<HashMap<String, Integer>> mWeights,String name,boolean flag) {
		// dictionary of terms
		Set<String> terms = new LinkedHashSet<>();
		String[] st =indexMaster.getDictionary();
		for (String s: st){
			terms.add(s);
		}
		Double[] centroidVector = new Double[terms.size()];
		Arrays.fill(centroidVector, 0.0);
		for (HashMap<String, Integer> hmap: mWeights) {
			int i=0;
			double[] vector = new double[terms.size()];
			for (String term :  terms) {
				if (hmap.get(term) != null) {
					int num = hmap.get(term);
					vector[i] = EuclidianNormalization.calcTermWeight(num);
				} else {
					vector[i] = 0;
				}
				i++;
			}
			double ld = EuclidianNormalization.calcEuclidianNormalization(vector);
			int j=0;
			for (double d: vector) {
				centroidVector[j] += d/ld;
				j++;
			}
		}
		int k=0;
		if (flag) {
			for (double val: centroidVector) {
				centroidVector[k] = val/classCount;
				k++;
			}
			//re-normalize the centroid vector
			double ld = EuclidianNormalization.calcEuclidianNormalization(unBoxDouble(centroidVector));
			int j=0;
			for (double d: centroidVector) {
				centroidVector[j] = d/ld;
				j++;
			}
			lengthOfTheVector(centroidVector);
			classCentroids.put(name, centroidVector);
			return "batman";
		} else {
			double min=0;
			String catagory = null;
			for(String nameClass:classCentroids.keySet()){
				System.out.println(nameClass);
				int m=0;double dist=0;
				Double[] base = classCentroids.get(nameClass);
				
				for(m=0;m<base.length;m++){
					dist += Math.pow((base[m]-centroidVector[m]),2);
				}
				dist = Math.sqrt(dist);
				System.out.println("Distance from this class ####" + dist);
				if (min == 0) {
					min = dist;
					catagory=nameClass;
				}
				if(min>dist){
					min=dist;
					catagory=nameClass;
				}
			}
			System.out.println("Least distance ####" + min);
			System.out.println("***********************************");
			System.out.println(catagory);
			System.out.println("***********************************");
			return catagory;
		}
	}
	
	private static void classOfFiles(String folder) {
		indexFiles(folder, null, false);
	}
	
	private static void indexFiles(String folder, final PostionalInvertedIndexImpl index, boolean isAll) {
	      
	      final Path currentWorkingPath = Paths.get(folder).toAbsolutePath();
	      ArrayList<Double> docWeights = new ArrayList <>();
	      List<HashMap<String, Integer>> mWeights = new ArrayList<>();
	      try {
	    	 
	         Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
	            int mDocumentID  = 0;
	            double docWeightAverage = 0;
	            public FileVisitResult preVisitDirectory(Path dir,
	             BasicFileAttributes attrs) {
	               // make sure we only process the current working directory
	               if (currentWorkingPath.equals(dir)) {
	                  return FileVisitResult.CONTINUE;
	               }
	               return FileVisitResult.SKIP_SUBTREE;
	            }
	            
	            public FileVisitResult visitFile(Path file,
	             BasicFileAttributes attrs) {
	               // only process .txt files
	            	
	               if (file.toString().endsWith(".txt")) {
	                  // we have found a .txt file; add its name to the fileName list,
	                  // then index the file and increase the document ID counter.
	                  // System.out.println("Indexing file " + file.getFileName());
	                  if (index != null) {
	                	  indexFile(file.toFile(), index, mDocumentID);
	                  } else {
	                	  mWeights.add(indexFile(file.toFile(), null, -1));
	                  }
//	                  double[] docWeightVars=
//	                  docWeights.add(docWeightVars[0]);
//	                  docWeights.add(docWeightVars[1]);
	                  mDocumentID++;
	               }
	               return FileVisitResult.CONTINUE;
	            }
	            
	            // don't throw exceptions if files are locked/other errors occur
	            public FileVisitResult visitFileFailed(Path file,
	             IOException e) {
	               
	               return FileVisitResult.CONTINUE;
	            }
	            
	         });
//	          EuclidianNormalization.persistENCalc(folder,docWeights);
	         if (!isAll) {
	        	 buildCentroidVector(mWeights,folder,true);
	         }
	      }
	      catch (IOException ex) {
	         Logger.getLogger(IndexWriter.class.getName()).log(Level.SEVERE, null, ex);
	      }
	   }

	   private static HashMap<String, Integer> indexFile(File fileName, PostionalInvertedIndexImpl index,
			   int documentID) {
		   HashMap<String, Integer> mWeights;
		   mWeights = new HashMap<String, Integer>();
		   try {
			   SimpleTokenStreamImpl stream = new SimpleTokenStreamImpl(fileName);
			   int tokenNumber =0 ;
			   while (stream.hasNextToken()) {
				   String term = stream.nextToken();
				   if(term != null && !term.isEmpty() && term.contains("-")){
					   String[] splited = term.split("-");
					   StringBuilder hyphenFull= new StringBuilder("");
					   for(int i=0;i<splited.length;i++){
						   String hyphenToken=splited[i];
						   hyphenToken=PorterStemmer.processToken(hyphenToken);
						   if (index != null) {
							   index.addTerm(hyphenToken,documentID,tokenNumber);
						   }
						   if (!mWeights.containsKey(hyphenToken)) {
							   mWeights.put(hyphenToken, 1);
						   }
						   else{
							   int count=mWeights.get(hyphenToken);
							   count++;
							   mWeights.put(hyphenToken, count);
						   }
						   hyphenFull.append(splited[i]);
					   }
					   String hyphenFullStr =  PorterStemmer.processToken(hyphenFull.toString());
					   if (index != null) {
						   index.addTerm(hyphenFullStr,documentID,tokenNumber);
					   }
					   if (!mWeights.containsKey(hyphenFullStr)) {
						   mWeights.put(hyphenFullStr, 1);
					   }
					   else{
						   int count=mWeights.get(hyphenFullStr);
						   count++;
						   mWeights.put(hyphenFullStr, count);
					   }
					   tokenNumber++;
				   }
				   else{
					   String stemmed = PorterStemmer.processToken(term);
					   if (stemmed != null && stemmed.length() > 0) {
						   if (index != null) {
							   index.addTerm(stemmed, documentID,tokenNumber);
						   }
						   if (!mWeights.containsKey(stemmed)) {
							   mWeights.put(stemmed, 1);
						   }
						   else{
							   int count=mWeights.get(stemmed);
							   count++;
							   mWeights.put(stemmed, count);
						   }
						   tokenNumber++;
					   }
				   }
			   }
//			   Set<String> st=mWeights.keySet();
//			   String[] st_array= st.toArray(new String[st.size()]);
//			   double[] wd= new double[st_array.length];
//			   for(int j=0;j<st_array.length;j++){
//				   wd[j]= EuclidianNormalization.calcTermWeight(mWeights.get(st_array[j]));
//			   }
//			   double ld = EuclidianNormalization.calcEuclidianNormalization(wd);
//			   //         EuclidianNormalization.persistENCalc(fileName.getParent(),wd);
//			   double[] weightValues= new double[3];
//			   double averageTermFreq=0;
//			   double k = 1;
//			   for(String s:mWeights.keySet()){
//				   averageTermFreq += mWeights.get(s);k++;
//			   }
//			   averageTermFreq = averageTermFreq/k;
//			   weightValues[0]=ld;
//			   weightValues[1]=averageTermFreq;
			   return mWeights;
		   }
		   catch (Exception ex) {
			   ex.printStackTrace();
			   return null;
		   }
	   }
	   
	   private static void lengthOfTheVector (Double [] d) {
		   double dist = 0.0;
		   for(int m=0;m<d.length;m++){
				dist += Math.pow((d[m]),2);
			}
			dist = Math.sqrt(dist);
		   System.out.println("It should be 1 " + dist);
	   }
	   
	   private static double [] unBoxDouble (Double[] d) {
		   double [] newArr = new double [d.length];
		   for (int i=0; i< d.length; i++) {
			   newArr[i] = d[i];
		   }
		   return newArr;
	   }
}