package com.set.index.impl;
/*
 * DECODE IN THIS FILE
 * 
 * FIRST TRY TO DECODE ONLY POSTINGS.BIN FILE IF THAT IS TRUE WE CAN DECODE 
 * VOCABTABLE.BIN AND DOCWEIGTHS.BIN
 * 
 * TRY TO TAKE 1 BYTE AT A TIME AS INSTRUCTED
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DiskInvertedIndex {

   private String mPath;
   private RandomAccessFile mVocabList;
   private RandomAccessFile mPostings;
   private long[] mVocabTable;
   private List<String> mFileNames;

   public DiskInvertedIndex(String path) {
      try {
         mPath = path;
         mVocabList = new RandomAccessFile(new File(path, "vocab.bin"), "r");
         mPostings = new RandomAccessFile(new File(path, "postings.bin"), "r");
         mVocabTable = readVocabTable(path);
         mFileNames = readFileNames(path);
      }
      catch (FileNotFoundException ex) {
         ex.printStackTrace();
      }
   }

   private static int[] readPostingsFromFile(RandomAccessFile postings, 
    long postingsPosition) {
      try {
         // seek to the position in the file where the postings start.
         postings.seek(postingsPosition);
         // read the 4 bytes for the document frequency
      // DECODE HERE
         byte[] buffer = new byte[4];
         postings.read(buffer, 0, buffer.length);
         // use ByteBuffer to convert the 4 bytes into an int.
         int documentFrequency = ByteBuffer.wrap(buffer).getInt();
         // initialize the array that will hold the postings. 
         int[] docIds = new int[documentFrequency];
         int i=0;
         while(i<documentFrequency){
             if(i==0){
                postings.read(buffer,0,buffer.length);
             // DECODE HERE
                 int docId = ByteBuffer.wrap(buffer).getInt();
                 docIds[i]=docId;
             }
             else{
                 postings.read(buffer,0,buffer.length);
              // DECODE HERE
                 int gap = ByteBuffer.wrap(buffer).getInt();
                 docIds[i]=docIds[i-1]+gap;
             }
             postings.read(buffer,0,buffer.length);
             int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
             postings.skipBytes(termPositionFreq*4);    
            i++; 
         }
         return docIds;
      }
      catch (IOException ex) {
         ex.printStackTrace();
      }
      return null;
   }
 private static int[] readPositionsFromFile(RandomAccessFile postings, long postingsPosition, int tempDocId, boolean flag) {
      try {
         postings.seek(postingsPosition);
         byte[] buffer = new byte[4];
      // DECODE HERE
         postings.read(buffer, 0, buffer.length);
         int documentFrequency = ByteBuffer.wrap(buffer).getInt();
         int i=0;
         int lastDocId=0;
         while(i<documentFrequency){
                postings.read(buffer,0,buffer.length);
             // DECODE HERE
                int docId = ByteBuffer.wrap(buffer).getInt()+lastDocId;
                lastDocId=docId;
                if(docId==tempDocId){
                    postings.read(buffer,0,buffer.length);
                 // DECODE HERE
                    int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
                    if (flag) {
                        return new int[]{termPositionFreq};
                    }
                    int[] positionIds = new int[termPositionFreq];
                    int j=0;
                    int lastPosition=0;
                    while(j<termPositionFreq){
                    	// DECODE HERE
                        postings.read(buffer,0,buffer.length);
                        positionIds[j]=ByteBuffer.wrap(buffer).getInt()+lastPosition;
                        lastPosition=positionIds[j];
                        j++;
                    }
                    return positionIds;
                }
             postings.read(buffer,0,buffer.length);
          // DECODE HERE
             int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
             postings.skipBytes(termPositionFreq*4);    
            i++; 
         }
         return null;
      }
      catch (IOException ex) {
         ex.printStackTrace();
      }
      return null;
   }
 
 private static int[][] readAllPositionsFromFileForATerm(RandomAccessFile postings, long postingsPosition,int[] tempDocId) {
	 try {
		 int [][] positionsForDocIds = new int[tempDocId.length][];
		 postings.seek(postingsPosition);
		 byte[] buffer = new byte[4];
		// DECODE HERE
		 postings.read(buffer, 0, buffer.length);
		 int documentFrequency = ByteBuffer.wrap(buffer).getInt();
		 int i=0,docIndex=0;
		 int lastDocId=0;
		 while(i<documentFrequency){
			 postings.read(buffer,0,buffer.length);
			// DECODE HERE
			 int docId = ByteBuffer.wrap(buffer).getInt()+lastDocId;
			 lastDocId=docId;
			 if(docId==tempDocId[docIndex]){
				 postings.read(buffer,0,buffer.length);
				 // DECODE HERE
				 int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
				 int[] positionIds = new int[termPositionFreq];
				 int j=0;
				 int lastPosition=0;
				 while(j<termPositionFreq){
					// DECODE HERE
					 postings.read(buffer,0,buffer.length);
					 positionIds[j]=ByteBuffer.wrap(buffer).getInt()+lastPosition;
					 lastPosition=positionIds[j];
					 j++;
				 }
				 positionsForDocIds[docIndex] = positionIds;
				 if (docIndex == tempDocId.length-1) {
					 return positionsForDocIds;
				 }
				 docIndex++;
				 continue;
			 }
			 postings.read(buffer,0,buffer.length);
			// DECODE HERE
			 int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
			 postings.skipBytes(termPositionFreq*4);    
			 i++; 
		 }
		 return null;
	 }
	 catch (IOException ex) {
		 ex.printStackTrace();
	 }
	 return null;
 }
 
 public static int readTermFrequencyFromFile(RandomAccessFile postings, long postingsPosition,int tempDocId){
	 return readPositionsFromFile(postings, postingsPosition, tempDocId, true)[0];
//     try {
//        postings.seek(postingsPosition);
//        byte[] buffer = new byte[4];
//        postings.read(buffer, 0, buffer.length);
//        int documentFrequency = ByteBuffer.wrap(buffer).getInt();
//        int i=0;
//        int lastDocId=0;
//        while(i<documentFrequency){
//               postings.read(buffer,0,buffer.length);
//               int docId = ByteBuffer.wrap(buffer).getInt()+lastDocId;
//               lastDocId=docId;
//               if(docId==tempDocId){
//                   postings.read(buffer,0,buffer.length);
//                   int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
//                   return termPositionFreq;
//               }
//            postings.read(buffer,0,buffer.length);
//            int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
//            postings.skipBytes(termPositionFreq*4);    
//           i++; 
//        }
//        return 0;
//     }
//     catch (IOException ex) {
//        ex.printStackTrace();
//     }
//	 return 0;
 }
 
 public static int[][] getTermFrequenciesForATermInAllDocs(RandomAccessFile raFile, long postingsPosition){
	 try {
		 raFile.seek(postingsPosition);
		 byte[] buffer = new byte[4];
		 raFile.read(buffer, 0, buffer.length);
		 int documentFrequency = ByteBuffer.wrap(buffer).getInt();
		 int i=0;
		 int[] docIds = new int[documentFrequency];
		 int[] termFrequencies = new int[documentFrequency];
		 int[][] finalArr = new int[][]{docIds, termFrequencies};
		 int lastDocId=0;
		 while(i<documentFrequency){
			 raFile.read(buffer,0,buffer.length);
			 int docId = ByteBuffer.wrap(buffer).getInt()+lastDocId;
			 lastDocId=docId;
			 docIds[i] = docId;
			 raFile.read(buffer,0,buffer.length);
			 int termPositionFreq = ByteBuffer.wrap(buffer).getInt();
			 raFile.skipBytes(termPositionFreq*4);  
			 termFrequencies[i] = termPositionFreq;
			 i++; 
		 }
		 return finalArr;
	 }
	 catch (IOException ex) {
		 ex.printStackTrace();
	 }
	 return null;
 }

   public int[] getPostings(String term) {
      long postingsPosition = binarySearchVocabulary(term);
      if (postingsPosition >= 0) {
         return readPostingsFromFile(mPostings, postingsPosition);
      }
      return null;
   }
   
   public int[] getPositions(String term,int docId) {
      long postingsPosition = binarySearchVocabulary(term);
      if (postingsPosition >= 0) {
         return readPositionsFromFile(mPostings, postingsPosition,docId, false);
      }
      return null;
   }
   
   public int getTermFrequency(String term,int docId) {
	   long postingsPosition = binarySearchVocabulary(term);
	   if (postingsPosition >= 0) {
		   return readTermFrequencyFromFile(mPostings, postingsPosition,docId);
	   }
	   return 0;
   }
   
   public int[][] getTermFrequenciesWithDocIds(String term) {
	   long postingsPosition = binarySearchVocabulary(term);
	   if (postingsPosition >= 0) {
		   return getTermFrequenciesForATermInAllDocs(mPostings, postingsPosition);
	   }
	   return null;
   }
   
   /*
    * Send in an array of doc ids and then find postings list for all of them
    * */
   public int[][] getPositionsForDocIds(String term,int[] docId) {
      long postingsPosition = binarySearchVocabulary(term);
      if (postingsPosition >= 0) {
         return readAllPositionsFromFileForATerm(mPostings, postingsPosition,docId);
      }
      return null;
   }

   private long binarySearchVocabulary(String term) {
      // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
      int i = 0, j = mVocabTable.length / 2 - 1;
      while (i <= j) {
         try {
            int m = (i + j) / 2;
            long vListPosition = mVocabTable[m * 2];
            int termLength;
            if (m == mVocabTable.length / 2 - 1){
               termLength = (int)(mVocabList.length() - mVocabTable[m*2]);
            }
            else {
               termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
            }

            mVocabList.seek(vListPosition);

            byte[] buffer = new byte[termLength];
            mVocabList.read(buffer, 0, termLength);
            String fileTerm = new String(buffer, "ASCII");

            int compareValue = term.compareTo(fileTerm);
            if (compareValue == 0) {
               // found it!
               return mVocabTable[m * 2 + 1];
            }
            else if (compareValue < 0) {
               j = m - 1;
            }
            else {
               i = m + 1;
            }
         }
         catch (IOException ex) {
            ex.printStackTrace();
         }
      }
      return -1;
   }


   private static List<String> readFileNames(String indexName) {
      try {
         final List<String> names = new ArrayList<String>();
         final Path currentWorkingPath = Paths.get(indexName).toAbsolutePath();

         Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
            int mDocumentID = 0;

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
                  names.add(file.toFile().getName());
               }
               return FileVisitResult.CONTINUE;
            }

            // don't throw exceptions if files are locked/other errors occur
            public FileVisitResult visitFileFailed(Path file,
             IOException e) {

               return FileVisitResult.CONTINUE;
            }

         });
         return names;
      }
      catch (IOException ex) {
         ex.printStackTrace();
      }
      return null;
   }

   private static long[] readVocabTable(String indexName) {
      try {
         long[] vocabTable;
         
         RandomAccessFile tableFile = new RandomAccessFile(
          new File(indexName, "vocabTable.bin"),
          "r");
         
         byte[] byteBuffer = new byte[4];
         tableFile.read(byteBuffer, 0, byteBuffer.length);
        
         int tableIndex = 0;
         vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
         byteBuffer = new byte[8];
         
         while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
            vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
            tableIndex++;
         }
         tableFile.close();
         return vocabTable;
      }
      catch (FileNotFoundException ex) {
         ex.printStackTrace();
      }
      catch (IOException ex) {
    	 ex.printStackTrace();
      }
      return null;
   }

   public List<String> getFileNames() {
      return mFileNames;
   }
   
   public int getTermCount() {
      return mVocabTable.length / 2;
   }
   
   public String getDirPath() {
      return mPath;
   }
}
