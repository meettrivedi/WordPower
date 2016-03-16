package com.set.util;
import java.util.ArrayList;

public class MergeList {
	
//	// a AND b
//	public static ArrayList<Integer> andTwoLists (List<Integer> a, List<Integer> b) {
//		int alen = a.size(),
//			blen = b.size();
//		ArrayList<Integer> c = new ArrayList<>();
//	
//		int i = 0,j = 0;
//		while (i<alen && j<blen) {
//			if (a.get(i) < b.get(j)) {
//				i++;
//			} else if (a.get(i) > b.get(j)) {
//				j++;
//			} else if  (a.get(i) == b.get(j)) {
//				c.add(a.get(i));
//				i++;j++;
//			}
//		}
//		return c;
//	}
//	
//	// a OR b
//	public static ArrayList<Integer> orTwoLists (List<Integer> a, List<Integer> b) {
//		int alen = a.size(),
//			blen = b.size();
//		ArrayList<Integer> c = new ArrayList<>();
//			
//			int i = 0,j = 0;
//			while (i<alen && j<blen) {
//				if (a.get(i) < b.get(j)) {
//					c.add(a.get(i));
//					i++;
//				} else if (a.get(i) > b.get(j)) {
//					c.add(b.get(j));
//					j++;
//				} else if  (a.get(i) == b.get(j)) {
//					c.add(a.get(i));
//					i++;j++;
//				}
//			}
//			if (i<alen) {
//				while (i<alen) {
//					c.add(a.get(i));
//					i++;
//				}
//			} else if (j<blen) {
//				while (j<blen) {
//					c.add(b.get(j));
//					j++;
//				}
//			}
//			return c;
//	}
//	
//	// a NOT b
//	public static ArrayList<Integer> notTwoLists (List<Integer> a, List<Integer> b) {
//		int alen = a.size(),
//			blen = b.size();
//		ArrayList<Integer> c = new ArrayList<>();
//		
//		int i = 0,j = 0;
//		while (i<alen && j<blen) {
//			if (a.get(i) < b.get(j)) {
//				c.add(a.get(i));
//				i++;
//			} else if (a.get(i) > b.get(j)) {
//				j++;
//			} else if  (a.get(i) == b.get(j)) {
//				i++;j++;
//			}
//		}
//		if (i<alen) {
//			while (i<alen) {
//				c.add(a.get(i));
//				i++;
//			}
//		}
//		return c;
//	}
	
	// a AND b
	public static ArrayList<Integer> andTwoLists (int[] a, int [] b) {
		int alen = a.length,
			blen = b.length;
		ArrayList<Integer> c = new ArrayList<>();
	
		int i = 0,j = 0;
		while (i<alen && j<blen) {
			if (a[i] < b[j]) {
				i++;
			} else if (a[i] > b[j]) {
				j++;
			} else if  (a[i] == b[j]) {
				c.add(a[i]);
				i++;j++;
			}
		}
		return c;
	}
	
	// a OR b
	public static ArrayList<Integer> orTwoLists (int[] a, int [] b) {
		int alen = a.length,
			blen = b.length;
		ArrayList<Integer> c = new ArrayList<>();
			
			int i = 0,j = 0;
			while (i<alen && j<blen) {
				if (a[i] < b[j]) {
					c.add(a[i]);
					i++;
				} else if (a[i] > b[j]) {
					c.add(b[j]);
					j++;
				} else if  (a[i] == b[j]) {
					c.add(a[i]);
					i++;j++;
				}
			}
			if (i<alen) {
				while (i<alen) {
					c.add(a[i]);
					i++;
				}
			} else if (j<blen) {
				while (j<blen) {
					c.add(b[j]);
					j++;
				}
			}
			return c;
	}
	
	// a NOT b
	public static ArrayList<Integer> notTwoLists (int[] a, int [] b) {
		int alen = a.length,
			blen = b.length;
		ArrayList<Integer> c = new ArrayList<>();
		
		int i = 0,j = 0;
		while (i<alen && j<blen) {
			if (a[i] < b[j]) {
				c.add(a[i]);
				i++;
			} else if (a[i] > b[j]) {
				j++;
			} else if  (a[i] == b[j]) {
				i++;j++;
			}
		}
		if (i<alen) {
			while (i<alen) {
				c.add(a[i]);
				i++;
			}
		}
		return c;
	}
	
	
	
	// PHRASE 
	// For a document, its term positions
	//[term][positions]
	// near operation will fail, if more than 2 lists are presented.
	// check test cases for more details.
	public static boolean phraseLists (int[][] a, int near) {
//		print2DArray(a);
		int alen = a.length;
		int[] ptrs = new int[alen]; // size
		
		while (true) { // Worst case time complexity ==> (4 * size) * (sizes of all the arrays)
			int [] tmpArr = new int[alen]; // size
			for (int i=0; i<alen; i++) {
				if (ptrs[i] >= a[i].length) {
					return false;
				}
				tmpArr[i] = a[i][ptrs[i]];
			}
			int k = 0;
			int[] equiIndexes = initIntArray(-1, alen); // size
			int equiIndexesPtr =  0;
			int smallestIndex = 0;
			int smallestValue = tmpArr[0];
			for (int j=1; j<alen; j++) {
				// 2nd - 1st
				if ((tmpArr[j] - tmpArr[j-1]) == near) {
					k++;
				}
				if (tmpArr[j] == tmpArr[j-1]) {
					equiIndexes[equiIndexesPtr] = j;
					++equiIndexesPtr;
					if (equiIndexesPtr != alen) {
						equiIndexes[equiIndexesPtr] = j-1;
						++equiIndexesPtr;
					}
				} else if (tmpArr[j] < tmpArr[j-1] && smallestValue > tmpArr[j]) {
					smallestIndex = j;
					smallestValue =  tmpArr[j];
				} else if (tmpArr[j] > tmpArr[j-1] && smallestValue > tmpArr[j-1]) {
					smallestIndex = j-1;
					smallestValue =  tmpArr[j-1];
				}
			}
			
			if (k == alen-1) {
				return true;
			} else {
				// increment pointers
				boolean found = false;
				for (int f=0; f<equiIndexes.length; f++) { // max size
					if (equiIndexes[f] == smallestIndex) {
						found  = true;
						break;
					}
				}
				if (found) {
					// increment all equi pointers
					for (int f=0; f<equiIndexes.length; f++) { // max size
						// increment all equi pointers
						if (equiIndexes[f] != -1) {
							ptrs[equiIndexes[f]] += 1;
						}
					}
				} else {
					// increment only the small pointer
					ptrs[smallestIndex] += 1;
				}
			}
		}
	}
	
	private static int []  initIntArray (int num, int size) {
		int[] arr = new int[size];
		for (int i=0; i<size; i++) {
			arr[i] = num;
		}
		return arr;
	}
	
	private static void print2DArray (int [][] a) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				System.out.print(a[i][j]+", ");
			}
			System.out.println();
			System.out.println();
		}
	}
	
}