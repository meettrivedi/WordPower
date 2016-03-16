package com.set.legacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//import com.set.index.inter.IndexInter;

public class NaiveInvertedIndexImpl{
   private HashMap<String, List<Integer>> mIndex;
   
   public NaiveInvertedIndexImpl() {
      mIndex = new HashMap<String, List<Integer>>();
   }
   
   public void addTerm(String term, int documentID) {
	  if (!mIndex.containsKey(term)) {
		  List<Integer> postings = new ArrayList<Integer>();
		  postings.add(documentID);
		  mIndex.put(term, postings);
	  } else {
		  ArrayList<Integer> postings = (ArrayList<Integer>) mIndex.get(term);
		  int size  =  postings.size();
		  int lastEle = postings.get(size-1);
		  
		  if (documentID  != lastEle) {
			  postings.add(documentID);
			  mIndex.put(term, postings);
		  }
	  }
   }
   
   public List<Integer> getPostings(String term) {
      return  mIndex.get(term);
   }
   
   public int getTermCount() {
      return  mIndex.size();
   }
   
   public String[] getDictionary() {
	   Set<String> s = mIndex.keySet();
	   String[] dict = s.toArray(new String[s.size()]); 
	   Arrays.sort(dict);
      return  dict;
   }
}
