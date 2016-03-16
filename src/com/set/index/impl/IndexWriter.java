package com.set.index.impl;
/*
 * ENCODE IN THIS FILE
 * 
 * FIRST TRY TO ENCODE ONLY POSTINGS.BIN FILE IF THAT IS TRUE WE CAN ENCODE 
 * VOCABTABLE.BIN AND DOCWEIGTHS.BIN
 * 
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.set.math.EuclidianNormalization;
import com.set.token.impl.SimpleTokenStreamImpl;
import com.set.token.processing.pre.PorterStemmer;

/**
Writes an inverted indexing of a directory to disk.
*/
public class IndexWriter {

   private String mFolderPath;

   /**
   Constructs an IndexWriter object which is prepared to index the given folder.
   */
   public IndexWriter(String folderPath) {
      mFolderPath = folderPath;
   }

   /**
   Builds and writes an inverted index to disk. Creates three files: 
   vocab.bin, containing the vocabulary of the corpus; 
   postings.bin, containing the postings list of document IDs;
   vocabTable.bin, containing a table that maps vocab terms to postings locations
   */
   public void buildIndex() {
      buildIndexForDirectory(mFolderPath);
   }

   /**
   Builds the normal NaiveInvertedIndex for the folder.
   */
   private static void buildIndexForDirectory(String folder) {
      PostionalInvertedIndexImpl index = new PostionalInvertedIndexImpl();

      // Index the directory using a naive index
      indexFiles(folder, index);

			// at this point, "index" contains the in-memory inverted index 
      // now we save the index to disk, building three files: the postings index,
      // the vocabulary list, and the vocabulary table.

      // the array of terms
      String[] dictionary = index.getDictionary();
      // an array of positions in the vocabulary file
      long[] vocabPositions = new long[dictionary.length];

      buildVocabFile(folder, dictionary, vocabPositions);
      buildPostingsFile(folder, index, dictionary, vocabPositions);
   }

   /**
   Builds the postings.bin file for the indexed directory, using the given
   NaiveInvertedIndex of that directory.
   */
   private static void buildPostingsFile(String folder, PostionalInvertedIndexImpl index,
    String[] dictionary, long[] vocabPositions) {
	  
      FileOutputStream postingsFile = null;
      try {
         postingsFile = new FileOutputStream(
          new File(folder, "postings.bin")
         );

         // simultaneously build the vocabulary table on disk, mapping a term index to a
         // file location in the postings file.
         FileOutputStream vocabTable = new FileOutputStream(
          new File(folder, "vocabTable.bin")
         );

         // the first thing we must write to the vocabTable file is the number of vocab terms.
         
         byte[] tSize = ByteBuffer.allocate(4)
          .putInt(dictionary.length).array();
         vocabTable.write(tSize, 0, tSize.length);
         int vocabI = 0;
         for (String s : dictionary) {
            // for each String in dictionary, retrieve its postings.
            List<Integer> postings = index.getPostings(s);

            // write the vocab table entry for this term: the byte location of the term in the vocab list file,
            // and the byte location of the postings for the term in the postings file.
            byte[] vPositionBytes = ByteBuffer.allocate(8)
             .putLong(vocabPositions[vocabI]).array();
            vocabTable.write(vPositionBytes, 0, vPositionBytes.length);

            byte[] pPositionBytes = ByteBuffer.allocate(8)
             .putLong(postingsFile.getChannel().position()).array();
            vocabTable.write(pPositionBytes, 0, pPositionBytes.length);

            // write the postings file for this term. first, the document frequency for the term, then
            // the document IDs, encoded as gaps.
            byte[] docFreqBytes = ByteBuffer.allocate(4)
             .putInt(postings.size()).array();
            /*
             * ENCODE HERE TO SEND
             * 
             * 
             * */
            postingsFile.write(docFreqBytes, 0, docFreqBytes.length);
            
            int lastDocId = 0;
            for (int docId : postings) {
               byte[] docIdBytes = ByteBuffer.allocate(4)
                .putInt(docId-lastDocId).array(); // encode a gap, not a doc ID
                
               lastDocId = docId;
             // ENCODE HERE 
               postingsFile.write(docIdBytes, 0, docIdBytes.length);
               
               List<Integer> positions = index.getPosition(s, docId);
               byte[] termFreqBytes = ByteBuffer.allocate(4)
                       .putInt(positions.size()).array();
            // ENCODE HERE 
               postingsFile.write(termFreqBytes,0,termFreqBytes.length);
               int lastTermId = 0;
               for(int termId : positions){
                    byte[] termIdBytes = ByteBuffer.allocate(4)
                           .putInt(termId-lastTermId).array();
                 // ENCODE HERE 
            	   postingsFile.write(termIdBytes,0,termIdBytes.length);
                   lastTermId= termId;
               }
               
            }

            vocabI++;
         }
         vocabTable.close();
         postingsFile.close();
      }
      catch (FileNotFoundException ex) {
      }
      catch (IOException ex) {
      }
      finally {
         try {
            postingsFile.close();
         }
         catch (IOException ex) {
         }
      }
   }

   private static void buildVocabFile(String folder, String[] dictionary,
    long[] vocabPositions) {
      OutputStreamWriter vocabList = null;
      try {
         // first build the vocabulary list: a file of each vocab word concatenated together.
         // also build an array associating each term with its byte location in this file.
         int vocabI = 0;
         vocabList = new OutputStreamWriter(
          new FileOutputStream(new File(folder, "vocab.bin")), "ASCII"
         );
         
         int vocabPos = 0;
         for (String vocabWord : dictionary) {
            // for each String in dictionary, save the byte position where that term will start in the vocab file.
            vocabPositions[vocabI] = vocabPos;
            vocabList.write(vocabWord); // then write the String
            vocabI++;
            vocabPos += vocabWord.length();
         }
      }
      catch (FileNotFoundException ex) {
         System.out.println(ex.toString());
      }
      catch (UnsupportedEncodingException ex) {
         System.out.println(ex.toString());
      }
      catch (IOException ex) {
         System.out.println(ex.toString());
      }
      finally {
         try {
            vocabList.close();
         }
         catch (IOException ex) {
            System.out.println(ex.toString());
         }
      }
   }

   private static void indexFiles(String folder, final PostionalInvertedIndexImpl index) {
      
      final Path currentWorkingPath = Paths.get(folder).toAbsolutePath();
      ArrayList<Double> docWeights = new ArrayList <>();
      
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
                  
                  double[] docWeightVars=indexFile(file.toFile(), index, mDocumentID);
                  
                  docWeights.add(docWeightVars[0]);
                  docWeights.add(docWeightVars[1]);
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
          EuclidianNormalization.persistENCalc(folder,docWeights);
      }
      catch (IOException ex) {
         Logger.getLogger(IndexWriter.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private static double[] indexFile(File fileName, PostionalInvertedIndexImpl index,
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
					   index.addTerm(hyphenToken,documentID,tokenNumber);
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
				   index.addTerm(hyphenFullStr,documentID,tokenNumber);
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
					   index.addTerm(stemmed, documentID,tokenNumber);
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
		   Set<String> st=mWeights.keySet();
		   String[] st_array= st.toArray(new String[st.size()]);
		   double[] wd= new double[st_array.length];
		   for(int j=0;j<st_array.length;j++){
			   wd[j]= EuclidianNormalization.calcTermWeight(mWeights.get(st_array[j]));
		   }
		   double ld = EuclidianNormalization.calcEuclidianNormalization(wd);
		   //         EuclidianNormalization.persistENCalc(fileName.getParent(),wd);
		   double[] weightValues= new double[3];
		   double averageTermFreq=0;
		   double k = 1;
		   for(String s:mWeights.keySet()){
			   averageTermFreq += mWeights.get(s);k++;
		   }
		   averageTermFreq = averageTermFreq/k;
		   weightValues[0]=ld;
		   weightValues[1]=averageTermFreq;
		   return weightValues;
	   }
	   catch (Exception ex) {
		   ex.printStackTrace();
		   return null;
	   }
   }
}
