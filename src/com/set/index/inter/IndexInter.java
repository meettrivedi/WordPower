package com.set.index.inter;

import java.util.List;

public interface IndexInter {
	// TO-DO: add the term to the index hashtable. If the table does not have
	// an entry for the term, initialize a new ArrayList<Integer>, add the 
	// docID to the list, and put it into the map. Otherwise add the docID
	// to the list that already exists in the map, but ONLY IF the list does
	// not already contain the docID.
	public void addTerm(String term, int documentID, int tokenNumber);
	// Not needed because we are indexing the documents in a given order and we will only have to
	// check the last element in the list to see if the document already exists.
	// TO-DO: return the postings list for the given term from the index map.
	public List<Integer> getPostings(String term);
	//TO-DO: return the number of terms in the index.
	public int getTermCount();
	//TO-DO: fill an array of Strings with all the keys from the hashtable.
	// Sort the array and return it.
	public String[] getDictionary();
}
