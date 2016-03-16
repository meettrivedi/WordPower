package com.set.query;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.set.constants.query.QueryConst;
import com.set.ds.query.inter.QLLiteral;

public class ProcessQueryTest {

	@Test
	public void test1() {
//		ArrayList<ClassQ> orLiteralArr = new ArrayList<>();
//		ClassQ q = new ClassQ();
//		NormalLiteral nl = new NormalLiteral();
//		nl.setNormalLiteral("abdec");
//		q.setQueryLiteral(nl);
		
		String valid1 = "abdec";
		QLLiteral ql = ProcessQuery.objectifyQuery(valid1).get(0).getQueryLiterals().get(0);
    	assertEquals(valid1, ql.getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.getType());
	}
	
	@Test
	public void test2() {
		String valid2 = "shakes \"jamba juice\" smoothies mango shakes (smoothies mango)";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid2).get(0).getQueryLiterals();
    	assertEquals("jamba juice", ql.get(0).getLiteral());
    	assertEquals(QueryConst.PHRASE, ql.get(0).getType());
    	assertEquals("shakes", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(1).getType());
    	assertEquals("smoothies", ql.get(2).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(2).getType());
    	assertEquals("mango", ql.get(3).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(3).getType());
    	assertEquals(4, ql.size());
	}
	
	@Test
	public void test3() {
		String valid3 = "\"jamba juice\"";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid3).get(0).getQueryLiterals();
    	assertEquals("jamba juice", ql.get(0).getLiteral());
    	assertEquals(QueryConst.PHRASE, ql.get(0).getType());
    	assertEquals(1, ql.size());
	}
	
	@Test
	public void test4() {
		String valid4 = "-shakes smoothies";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid4).get(0).getQueryLiterals();
    	assertEquals("shakes", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(0).getType());
    	assertEquals("smoothies", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(1).getType());
    	assertEquals(2, ql.size());
	}
	
	@Test
	public void test5() {
		String valid5 = "shakes -smoothies";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid5).get(0).getQueryLiterals();
		assertEquals("shakes", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(0).getType());
    	assertEquals("smoothies", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(1).getType());
    	assertEquals(2, ql.size());
	}
	
	@Test
	public void test6() {
		String valid6 = "shakes -smoothies \"Jamba Juice\"";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid6).get(0).getQueryLiterals();
		assertEquals("Jamba Juice", ql.get(0).getLiteral());
    	assertEquals(QueryConst.PHRASE, ql.get(0).getType());
    	assertEquals("shakes", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(1).getType());
    	assertEquals("smoothies", ql.get(2).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(2).getType());
    	assertEquals(3, ql.size());
	}
	
	@Test
	public void test7() {
		String valid7 = "-shakes -smoothies \"Jamba Juice\"";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid7).get(0).getQueryLiterals();
		assertEquals("Jamba Juice", ql.get(0).getLiteral());
    	assertEquals(QueryConst.PHRASE, ql.get(0).getType());
    	assertEquals("shakes", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(1).getType());
    	assertEquals("smoothies", ql.get(2).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(2).getType());
    	assertEquals(3, ql.size());
	}
	
	@Test
	public void test8() {
		String valid8 = "shakes + smoothies -mango";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid8).get(0).getQueryLiterals();
		ArrayList<QLLiteral> ql1 = ProcessQuery.objectifyQuery(valid8).get(1).getQueryLiterals();
		assertEquals("shakes", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(0).getType());
    	assertEquals("smoothies", ql1.get(0).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql1.get(0).getType());
    	assertEquals("mango", ql1.get(1).getLiteral());
    	assertEquals(QueryConst.NOT, ql1.get(1).getType());
    	assertEquals(1, ql.size());
    	assertEquals(2, ql1.size());
	}
	
	@Test
	public void test9() {
		String valid9 = "-shakes + smoothies";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid9).get(0).getQueryLiterals();
		ArrayList<QLLiteral> ql1 = ProcessQuery.objectifyQuery(valid9).get(1).getQueryLiterals();
		assertEquals("shakes", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(0).getType());
    	assertEquals("smoothies", ql1.get(0).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql1.get(0).getType());
    	assertEquals(1, ql.size());
    	assertEquals(1, ql1.size());
	}

	@Test
	public void test10() {
		String valid10 = "du-mmy";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid10).get(0).getQueryLiterals();
		assertEquals("du-mmy", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(0).getType());
    	assertEquals(1, ql.size());
	}
	
	@Test
	public void test11() {
		String valid11 = "\"angels NEAR/2 baseball\"";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid11).get(0).getQueryLiterals();
		assertEquals("angels NEAR/2 baseball", ql.get(0).getLiteral());
    	assertEquals(QueryConst.NEAR, ql.get(0).getType());
    	assertEquals(1, ql.size());
	}
	
	@Test
	public void test12() {
		String valid12 = "(smoothies mango) -shakes \"hello\" + smoothies \"angels NEAR/2 baseball\" ";
		ArrayList<QLLiteral> ql = ProcessQuery.objectifyQuery(valid12).get(0).getQueryLiterals();
		ArrayList<QLLiteral> ql1 = ProcessQuery.objectifyQuery(valid12).get(1).getQueryLiterals();
		assertEquals("hello", ql.get(0).getLiteral());
    	assertEquals(QueryConst.PHRASE, ql.get(0).getType());
    	assertEquals("smoothies", ql.get(1).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(1).getType());
    	assertEquals("mango", ql.get(2).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql.get(2).getType());
    	assertEquals("shakes", ql.get(3).getLiteral());
    	assertEquals(QueryConst.NOT, ql.get(3).getType());
    	assertEquals(4, ql.size());
    	assertEquals("angels NEAR/2 baseball", ql1.get(0).getLiteral());
    	assertEquals(QueryConst.NEAR, ql1.get(0).getType());
    	assertEquals("smoothies", ql1.get(1).getLiteral());
    	assertEquals(QueryConst.NORMAL, ql1.get(1).getType());
    	assertEquals(2, ql1.size());
	}
}
