package com.set.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//import com.set.index.inter.IndexInter;

public class PositionalInvertedBayesian{
	   private HashMap<String,HashMap<String ,List<Integer>>> mIndex;
	   
	   public PositionalInvertedBayesian() {
	      
	      mIndex = new HashMap<String, HashMap<String ,List<Integer>>>();
	   }
	   
	   public void addTerm(String term, String fileName, int tokenNumber) {
	     if (!mIndex.containsKey(term)) {
		/* HashMap<Integer ,List<Integer>> index;	  
	         index = mIndex.get(term);
	         if(!index.containsKey(documentID)){
	            List<Integer> postings = new ArrayList<>();
	            postings.add(tokenNumber);
	            index.put(documentID, postings);
	            mIndex.put(term, index); 
	         }
	         else{
	             List<Integer> postings = index.get(documentID);
	             postings.add(tokenNumber);
	             mIndex.put(term, index);
	         }*/
	         HashMap<String ,List<Integer>> index = new HashMap<>();
	         List<Integer> postings = new ArrayList<>();
	         postings.add(tokenNumber);
	         index.put(fileName, postings);
	         mIndex.put(term, index);
		  } 
	     else {/*
			  ArrayList<Integer> postings = (ArrayList<Integer>) mIndex.get(term);
			  int size  =  postings.size();
			  int lastEle = postings.get(size-1);
			  
			  if (documentID  != lastEle) {
				  postings.add(documentID);
				  mIndex.put(term, postings);
	                    }*/
	                HashMap<String ,List<Integer>> index;	  
	                index = mIndex.get(term);
	                if(!index.containsKey(fileName)){
	                        List<Integer> postings = new ArrayList<>();
	                        postings.add(tokenNumber);
	                        index.put(fileName, postings);
	                        mIndex.put(term, index); 
	                    }
	                else{
	                        List<Integer> postings = index.get(fileName);
	                        postings.add(tokenNumber);
	                        mIndex.put(term, index);
	                    }
	          }
	      
	   }
	   
	   public List<String> getPostings(String term) {
	      // TO-DO: return the postings list for the given term from the index map.
	      HashMap<String ,List<Integer>> index;
	      ArrayList<String> docs= new ArrayList<>();
	      index = new HashMap<>();
	      index = mIndex.get(term);
	      if(index == null){
	    	  return new ArrayList<>();
	      }
	   for(String i:index.keySet()){
	       docs.add(i);
	   }
	   // To-Do: Don't sort this, find a better solution. Ask professor.
	   Collections.sort(docs);
	   return docs;
	   }
	   
	   public int getTermCount() {
	      
	      return mIndex.size();
	   }
	   
	   public int getPositionFreq(String s,String docName){
	       HashMap<String,List<Integer>> termIndex = mIndex.get(s);
	       if(termIndex == null){
	     	  return 0;
	       }
	       
	       if(termIndex.containsKey(docName))
	       {
	       List<Integer> i= termIndex.get(docName);
	       
	       return i.size();
	       }
	       return 0;
	   }
	   
	 
	   
	   public String[] getDictionary() {
	      // TO-DO: fill an array of Strings with all the keys from the hashtable.
	      // Sort the array and return it.
	      Set<String> s= new TreeSet<>(mIndex.keySet());
	      String[] s1 = s.toArray(new String[s.size()]); 
	      return s1;
	   }
	   
	   public HashMap<String,HashMap<String ,List<Integer>>> getMindex(){
		   return mIndex;
	   }
	   
	   
	public HashMap<String ,List<String>> getMindexPostings(){
		   HashMap<String ,List<String>> MindexPostings = new HashMap<>();
		   for(String s:mIndex.keySet()){
			   HashMap<String ,List<Integer>> temp = mIndex.get(s);
			   List<String> keyset = new ArrayList<>();
			   for(String file: temp.keySet()){
				   keyset.add(file);
			   }
			   MindexPostings.put(s, keyset);
		   }
		   
		   
		   return MindexPostings;
	   }
	  
}
