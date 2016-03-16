package com.set.index.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.set.index.inter.IndexInter;
//Indexes: You will maintain one index for your corpus: the PositionalInvertedIndex, a positional index
//as discussed in class, where postings lists consist of <documentID, [position1, position2, ...]> pairs. Use
//NaiveInvertedIndex as a reference point. You may need to write your own simple PositionalPosting
//class to pair an integer document ID with a list of integer document positions; your index can then map a
//string term to a list of PositionalPosting objects. The addTerm method will need an additional parameter
//for the term's position.
public class PostionalInvertedIndexImpl implements IndexInter{
   
   private HashMap<String,HashMap<Integer ,List<Integer>>> mIndex;
   
   public PostionalInvertedIndexImpl() {
      
      mIndex = new HashMap<String, HashMap<Integer ,List<Integer>>>();
   }
   
   public void addTerm(String term, int documentID, int tokenNumber) {
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
         HashMap<Integer ,List<Integer>> index = new HashMap<>();
         List<Integer> postings = new ArrayList<>();
         postings.add(tokenNumber);
         index.put(documentID, postings);
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
                HashMap<Integer ,List<Integer>> index;	  
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
                    }
          }
      
   }
   
   public List<Integer> getPostings(String term) {
      // TO-DO: return the postings list for the given term from the index map.
      HashMap<Integer ,List<Integer>> index;
      ArrayList<Integer> docs= new ArrayList<>();
      index = new HashMap<>();
      index = mIndex.get(term);
      if(index == null){
    	  return new ArrayList<>();
      }
   for(int i:index.keySet()){
       docs.add(i);
   }
   // To-Do: Don't sort this, find a better solution. Ask professor.
   Collections.sort(docs);
   return docs;
   }
   
   public int getTermCount() {
      
      return mIndex.size();
   }
   
   public List<Integer> getPosition(String s,int l){
       HashMap<Integer,List<Integer>> docIndex = mIndex.get(s);
       if(docIndex == null){
     	  return new ArrayList<>();
       }
       List<Integer> i= docIndex.get(l);
       
       return i;
   }
   
   public HashMap<Integer,List<Integer>> phraseCheck(String s,int l){
       HashMap<Integer,List<Integer>> docPositions = new HashMap<>();
       HashMap<Integer,List<Integer>> docIndex = mIndex.get(s);
       List<Integer> i= docIndex.get(l);
       docPositions.put(l, i);
    return docPositions;
}
   
   public String[] getDictionary() {
      // TO-DO: fill an array of Strings with all the keys from the hashtable.
      // Sort the array and return it.
      Set<String> s= new TreeSet<>(mIndex.keySet());
      String[] s1 = s.toArray(new String[s.size()]); 
      return s1;
   }
  
}



