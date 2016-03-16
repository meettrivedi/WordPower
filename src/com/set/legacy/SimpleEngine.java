package com.set.legacy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.set.exception.WPException;
import com.set.index.impl.DiskInvertedIndex;
import com.set.index.impl.IndexWriter;
import com.set.index.impl.PostionalInvertedIndexImpl;
import com.set.token.impl.SimpleTokenStreamImpl;
import com.set.token.processing.pre.PorterStemmer;
import com.set.util.MergeList;

/**
 * A very simple search engine. Uses an inverted index over a folder of TXT
 * files.
 */
public class SimpleEngine {
	public static final List<String> fileNames = new ArrayList<>();

	public static void indexDir(String dirPath) throws IOException {
		IndexWriter iw =new IndexWriter(dirPath);
		iw.buildIndex();
		
	}
	private static void indexFile(File file, PostionalInvertedIndexImpl index, int docID) throws FileNotFoundException {
		PorterStemmer ps;
		SimpleTokenStreamImpl simple = new SimpleTokenStreamImpl(file);
		int tokenNumer = 0;
		while (simple.hasNextToken()) {
			// System.out.println(tokenNumer);
			ps = new PorterStemmer();
			String s = simple.nextToken();
			if (s != null) {
				if (s.contains("-")) {
					String[] splited = s.split("-");
					String hyphenFull = "";
					for (int i = 0; i < splited.length; i++) {
						String hyphenToken = splited[i];
						hyphenToken = PorterStemmer.processToken(hyphenToken);
						index.addTerm(hyphenToken, docID, tokenNumer);
						hyphenFull += splited[i];
					}
					hyphenFull = PorterStemmer.processToken(hyphenFull);
					index.addTerm(hyphenFull, docID, tokenNumer);
					tokenNumer++;
				} else {
					s = PorterStemmer.processToken(s);
					// System.out.println(tokenNumer+" "+s);
					index.addTerm(s, docID, tokenNumer);
					tokenNumer++;
				}
			}
		}
	}

	public static void printResults(PostionalInvertedIndexImpl index, List<String> fileNames) {

		String[] files = fileNames.toArray(new String[fileNames.size()]);
		int i;
		i = 0;
		int longest = 0;
		String[] s = index.getDictionary();
		for (String s1 : s) {
			longest = Math.max(longest, s1.length());
		}
		for (String s1 : s) {
			System.out.print(s1 + ":");
			for (i = 0; i < longest - s1.length(); i++) {
				System.out.print(" ");
			}
			List<Integer> postings = index.getPostings(s1);
			for (i = 0; i < fileNames.size(); i++) {
				if (postings.contains(i)) {
					System.out.print(files[i] + " ");
				}
			}
			System.out.println();
		}
	}

	public void checkWord(String s, PostionalInvertedIndexImpl index, List<String> fileNames) {
		try {

			String[] files = fileNames.toArray(new String[fileNames.size()]);
			int i = 0;
			List<Integer> postings = index.getPostings(s);

			for (i = 0; i < fileNames.size(); i++) {
				if (postings.contains(i)) {
					List<Integer> position = index.getPosition(s, i);
					System.out.println(files[i] + " At positions");
					position.stream().forEach((k) -> {
						System.out.print(k + " ");
					});
					System.out.println();
				}
			}
			System.out.println();
		} catch (NullPointerException n) {
			System.out.println("Sorry Word not present");
		}
	}
	
	// 1 - for AND (intersection)
	// 2 - for OR (union)
	// 3 - for NOT (negation)
	public static List<Integer> queryType(ArrayList<String> normalQueries, DiskInvertedIndex index,
			List<String> fileNames, int o, boolean print) {
		if (normalQueries.isEmpty()) {
			return new ArrayList<>();
		}
		String[] files = fileNames.toArray(new String[fileNames.size()]);
		int i = 0;
		ArrayList<String> splited = normalQueries;
		int size = splited.size();
		List<List<Integer>> postings = new ArrayList<> ();

		List<Integer> finalAndList = new ArrayList<>();
		for (i = 0; i < size; i++) {
			String str = splited.get(i);
			if (!str.isEmpty()) {
				str = str.toLowerCase();
				str = PorterStemmer.processToken(str);
				int [] intArrPostings  = index.getPostings(str);
				postings.add(arrayToListInt(intArrPostings));
			}
		}
		switch (o) {
		case 1:
			finalAndList = intersection(postings);
			break;
		case 2:
			finalAndList = union(postings);
			break;
		case 3:
			finalAndList = negation(postings);
			break;
		default:
			try {
				throw new WPException("invalid switch option");
			} catch (WPException e) {
				e.printStackTrace();
			}
		}
		if (print) {
			finalAndList.stream().forEach((k) -> {
				System.out.print(files[k] + " ");
			});
		}
		return finalAndList;
	}
	
	public static List<Integer> queryAND(ArrayList<String> normalQueries, DiskInvertedIndex index,
			List<String> fileNames) {
		return queryType(normalQueries, index, fileNames, 1, false);
	}

	public static List<Integer> queryOR(ArrayList<String> orQueries, DiskInvertedIndex index,
			List<String> fileNames) {
		return queryType(orQueries, index, fileNames, 2, true);
	}
	
//	public static List<Integer> queryNOT(ArrayList<String> notQueries, PostionalInvertedIndexImpl index,
//			List<String> fileNames) {
//		return queryType(notQueries, index, fileNames, 3, false);
//	}
	public static List<Integer> queryPHRASEType(ArrayList<String> phraseQueries, DiskInvertedIndex index,
			List<String> fileNames, boolean flag) {
		if (phraseQueries.isEmpty()) {
			return new ArrayList<>();
		}
		int near = 0;
		if (!flag) {
			 near = 1;
		}
		int i = 0;
		List<List<Integer>> listPhrase = new ArrayList<>();
		final Pattern validNearContent = Pattern.compile("\\w+\\s(NEAR\\/([1-9][0-9]*))?\\s?\\w+");
		// String s1=s.substring(1, s.length()-1);
		for (String str : phraseQueries) {
			if (flag) {
				System.out.println("Phrase Type String: " + str);
				Matcher m = validNearContent.matcher(str);
				m.find();
				str = str.replaceAll(m.group(1)+" ", "");
				near = Integer.parseInt(m.group(2));
			}
			ArrayList<String> splited = new ArrayList<String>(Arrays.asList(str.split(" ")));
			int size = splited.size();
			List<Integer>[] postings = new List[size];
			List<Integer>[] positions = new List[size];
			List<Integer> finalAndList = new ArrayList<Integer>();
			List<Integer> andList = new ArrayList<Integer>();
			for (i = 0; i < size; i++) {
				String s = splited.get(i);
				s = s.toLowerCase();
				s = PorterStemmer.processToken(s);
				postings[i] = arrayToListInt(index.getPostings(str));
			}
			List<List<Integer>> andSet = Arrays.asList(postings);
			andList = intersection(andSet);
			for (int k : andList) {
				for (i = 0; i < size; i++) {
					String s = splited.get(i);
					s = s.toLowerCase();
					s = PorterStemmer.processToken(s);
					positions[i] = arrayToListInt(index.getPositions(s, k));
				}
				int j = 0;
				while (j < positions[0].size()) {
					int m = positions[0].get(j);
					if (compareList(positions, m, near)) {
						finalAndList.add(k);
						break;
					}
					j++;
				}
			}
			listPhrase.add(finalAndList);
		}
		return intersection(listPhrase);
	}
	public static List<Integer> queryNEAR(ArrayList<String> phraseQueries, DiskInvertedIndex index,
			List<String> fileNames) {
		return queryPHRASEType(phraseQueries, index, fileNames, true);
	}

	public static List<Integer> queryPHRASE(ArrayList<String> phraseQueries, DiskInvertedIndex index,
			List<String> fileNames) {
		return queryPHRASEType(phraseQueries, index, fileNames, false);
	}
	// NEAR/1
	public static boolean compareList(List<Integer>[] postings, int first, int near) {
		if (near < 1) {
			try {
				throw new WPException("invalid near option: " + near);
			} catch (WPException e) {
				e.printStackTrace();
			}
		}
		List<Integer>[] test = postings;
		int k = first;
		if (test.length == 1) {
			if (test[0].contains(k)) {
				return true;
			}
		}
		else if (test.length == 2) {
			if (test[1].contains(k+near)) {
				return true;
			} else {
				return false;
			}
		} else {
			int i=1;
			while(i<test.length){
			if (test[i].contains(k+near)) {
				k++;
			} 
			else {
				return false;
			}
			i++;
			}
		}
		return true;
	}

	public static List<Integer> intersection(List<List<Integer>> andSet) {
		return setOperations(andSet, 1);
	}

	public static List<Integer> union(List<List<Integer>> orSet) {
		return setOperations(orSet, 2);
	}
	// should only have size of 2
	public static List<Integer> negation(List<List<Integer>> notSet) {
		if (notSet.size() != 2) {
			try {
				throw new WPException("not set");
			} catch (WPException e) {
				e.printStackTrace();
			}
		}
		return setOperations(notSet, 3);
	}
	
	// 1 - for AND (intersection)
	// 2 - for OR (union)
	// 3 - for NOT (negation)
	public static List<Integer> setOperations (List<List<Integer>> set, int o) {
		if (set.size()==0) {
			return new ArrayList<Integer>();
		}
		if (set.size()==1) {
			return set.get(0);
		}
		List<List<Integer>> resultSet = new ArrayList<>();
		for (int i=0; i<set.size(); i+=2) {
			List<Integer> a = set.get(i);
			List<Integer> b;
			if (i+1<set.size()) {
				b = set.get(i+1);
				switch (o) {
				case 1:
					resultSet.add(MergeList.andTwoLists(convertIntegers(a), convertIntegers(b)));
					break;
				case 2:
					resultSet.add(MergeList.orTwoLists(convertIntegers(a), convertIntegers(b)));
					break;
				case 3:
					resultSet.add(MergeList.notTwoLists(convertIntegers(a), convertIntegers(b)));
					break;
				default:
					try {
						throw new WPException("invalid switch option");
					} catch (WPException e) {
						e.printStackTrace();
					}
				}
			} else {
				resultSet.add(a);
			}
		}
		return setOperations(resultSet, o);
	}
	
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	private static List<Integer> arrayToListInt(int[] a){
		ArrayList<Integer> al = new ArrayList<>();
		for (int i: a) {
			al.add(i);
		}
		return al;
	}
}
