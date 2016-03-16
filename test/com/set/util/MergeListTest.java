package com.set.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MergeListTest {

	@Test
	public void test() {
		int[] a = new int[]{1,2,3,4,5,6,7};
		int[] b = new int[]{8,9,10};
		assertEquals("[]", MergeList.andTwoLists(a, b).toString());
		assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]", MergeList.orTwoLists(a, b).toString());
		assertEquals("[1, 2, 3, 4, 5, 6, 7]", MergeList.notTwoLists(a, b).toString());
	}
	
	@Test
	public void test1() {
		int[] a = new int[]{1,2,3,4,5,6,7};
		int[] b = new int[]{2,3,4};
		assertEquals("[2, 3, 4]", MergeList.andTwoLists(a, b).toString());
		assertEquals("[1, 2, 3, 4, 5, 6, 7]", MergeList.orTwoLists(a, b).toString());
		assertEquals("[1, 5, 6, 7]", MergeList.notTwoLists(a, b).toString());
	}
	
	@Test
	public void test2() {
		int[] a = new int[]{};
		int[] b = new int[]{};
		assertEquals("[]", MergeList.andTwoLists(a, b).toString());
		assertEquals("[]", MergeList.orTwoLists(a, b).toString());
		assertEquals("[]", MergeList.notTwoLists(a, b).toString());
	}
	
	@Test
	public void test3() {
		int[] a = new int[]{2,3,4};
		int[] b = new int[]{2,3,4};
		assertEquals("[2, 3, 4]", MergeList.andTwoLists(a, b).toString());
		assertEquals("[2, 3, 4]", MergeList.orTwoLists(a, b).toString());
		assertEquals("[]", MergeList.notTwoLists(a, b).toString());
	}
	
	
	@Test
	public void test4() {
		int[] a = new int[]{1,3,4};
		int[] b = new int[]{2,3,4};
		int[] c = new int[]{3,4};
		int[][] d = new int[][] {a, b, c};
		assertEquals(true, MergeList.phraseLists(d,1));
	}
	
	@Test
	public void test5() {
		int[] a = new int[]{2,4};
		int[] b = new int[]{2,3,4};
		int[] c = new int[]{3,4};
		int[][] d = new int[][] {a, b, c};
		assertEquals(false, MergeList.phraseLists(d,1));
	}
	
	@Test
	public void test6() {
		int[] a = new int[]{4,5,6};
		int[] b = new int[]{2,3,5};
		int[] c = new int[]{3,6};
		int[][] d = new int[][] {a, b, c};
		assertEquals(true, MergeList.phraseLists(d,1));
	}
	
	@Test
	public void test7() {
		int[] a = new int[]{};
		int[] b = new int[]{};
		int[] c = new int[]{};
		int[][] d = new int[][] {a, b, c};
		assertEquals(false, MergeList.phraseLists(d,1));
	}
	
	@Test
	public void test8() {
		int[] a = new int[]{1};
		int[] b = new int[]{1};
		int[] c = new int[]{1};
		int[][] d = new int[][] {a, b, c};
		assertEquals(false, MergeList.phraseLists(d,1));
	}
	
	@Test
	public void test9() {
		int[] a = new int[]{1,3,5};
		int[] b = new int[]{2,5};
//		int[] c = new int[]{1,7};
		int[][] d = new int[][] {a, b};
		assertEquals(true, MergeList.phraseLists(d,2));
	}
	
	@Test
	public void test11() {
		int[] a = new int[]{15, 52, 78, 186, 264, 315, 438, 521, 582, 605, 640, 744, 866};
		int[] b = new int[]{16, 47, 249, 358, 439, 497, 648, 864};
		int[] c = new int[]{440};
		int[] d = new int[]{5, 441, 870};
		int[][] e = new int[][] {a, b, c, d};
		assertEquals(true, MergeList.phraseLists(e,1));
	}
	
	@Test
	public void test12() {
		int[] a = new int[]{108, 128, 135, 219, 220, 238, 298, 306, 375, 426, 430, 450, 559, 576};
		int[] b = new int[]{84, 101, 174, 199, 261, 399, 418, 431, 464, 562, 577};
		int[] c = new int[]{432};
		int[] d = new int[]{433};
		int[][] e = new int[][] {a, b, c, d};
		assertEquals(true, MergeList.phraseLists(e,1));
	}
	
	@Test
	public void test13() {
		int[] a = new int[]{72, 79, 154, 172, 223, 239, 252, 301, 393, 450, 477, 526, 534, 588};
		int[] b = new int[]{111, 177, 194, 478, 514, 625, 633};
		int[] c = new int[]{479};
		int[] d = new int[]{480};
		int[][] e = new int[][] {a, b, c, d};
		assertEquals(true, MergeList.phraseLists(e,1));
	}
	
	@Test
	public void test10() {
		// NEAR /x query is only handling 2 words
		// i.e. "hello NEAR/2 World"
		// We are not supporting "hello NEAR/2 World NEAR/2 there"
		int[] a = new int[]{1,3,5};
		int[] b = new int[]{2,5};
		int[] c = new int[]{1,2,3,4,7};
		int[][] d = new int[][] {a, b, c};
		assertEquals(true, MergeList.phraseLists(d,2));
	}
	
	@Test
	public void test14() {
		int[] a = new int[]{1,3};
		int[] b = new int[]{2,3,4,5};
		int[][] d = new int[][] {a, b};
		assertEquals(true, MergeList.phraseLists(d,2));
	}

}
