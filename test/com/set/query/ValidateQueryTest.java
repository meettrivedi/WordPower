package com.set.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidateQueryTest {

	@Test
	public void shouldBeTrue() {
		String valid1 = "abdec";
		String valid2 = "shakes \"jamba juice\" smoothies mango shakes (smoothies mango)";
		String valid3 = "\"jamba juice\"";
		String valid4 = "-shakes smoothies";
		String valid5 = "shakes -smoothies";
		String valid7 = "shakes -smoothies \"Jamba Juice\"";
		String valid8 = "shakes + smoothies -mango";
		String valid9 = "-shakes + smoothies";
		String valid6 = "shakes -smoothies \"Jamba Juice\"";
		String valid10 = "shakes + smoothies -mango";
		String valid11 = "du-mmy";
		String valid12 = "\"angels NEAR/2 baseball\"";
		String valid13 = "-\"angels NEAR/2 baseball\" angels";
		String valid14 = "-\"angels baseball\" angels";

		assertEquals(true, ValidateQuery.isValidQuery(valid1));
		assertEquals(true, ValidateQuery.isValidQuery(valid2));
		assertEquals(true, ValidateQuery.isValidQuery(valid3));
		assertEquals(true, ValidateQuery.isValidQuery(valid4));
		assertEquals(true, ValidateQuery.isValidQuery(valid5));
		assertEquals(true, ValidateQuery.isValidQuery(valid6));
		assertEquals(true, ValidateQuery.isValidQuery(valid7));
		assertEquals(true, ValidateQuery.isValidQuery(valid8));
		assertEquals(true, ValidateQuery.isValidQuery(valid9));
		assertEquals(true, ValidateQuery.isValidQuery(valid10));
		assertEquals(true, ValidateQuery.isValidQuery(valid11));
		assertEquals(true, ValidateQuery.isValidQuery(valid12));
		assertEquals(true, ValidateQuery.isValidQuery(valid13));
		assertEquals(true, ValidateQuery.isValidQuery(valid14));
		
	}

	@Test
	public void shouldBeFalse() {
		String invalid0 = "((abcc))";
		String invalid01 = "()";
		String invalid05 = "shakes \"jamba juice\" + smoothies mango (\"jamba juice\")";
		String invalid06 = "dummy-";
		String invalid02 = "\"\"";
		String invalid03 = "\"\"\"\"";
		String invalid04 = "\"\"\"";
		String invalid1 = "(((((abcc)))";
		String invalid07 = "(-shakes + smoothie)";
		String invalid08 = "(-shakes smoothie)";
		String invalid09 = "shakes \"jamba juice\" smoothies mango shakes (smoothies + mango)";
		String invalid010 = "(smoothies + mango)";
		String invalid2 = "shakes \"jamba juice\"    +     +   +      smoothies mango +  + shakes + (((((smoothies + +++++ mango ))) + shakes";
		String invalid3 = "shakes \"jamba juice\"    +     +   +      smoothies mango +  + shakes + smoothies mango ) + shakes";
		String invalid4 = "+ shakes \"jamba juice\" smoothies mango shakes (smoothies mango)";
		String invalid5 = "shakes \"jamba juice\" smoothies mango shakes (smoothies mango) +";
		String invalid6 = "(+ kill)";
		String invalid7 = "\"jamba + juice\"";
		String invalid8 = "\"(jamba + juice)\"";
		String invalid9 = "(-kill)";
		String invalid10 = "-kill";
		String invalid11 = "\"jamba - juice\"";
		String invalid12 = "a - kill";
		String invalid13 = "a --kill bad";
		String invalid14 = "-";
		String invalid15 = "angels NEAR/2 baseball";
		String invalid16 = "angels / baseball";
		String invalid17 = " ";
		String invalid18 = "";
		String invalid19 = "\"angels NEAR/2 baseball NEAR/2 team\"";
		String invalid20 = "-\"angels NEAR/2 baseball\"";
		String invalid21 = "-\"angels baseball\"";
		
		assertEquals(false, ValidateQuery.isValidQuery(invalid0));
		assertEquals(false, ValidateQuery.isValidQuery(invalid1));
		assertEquals(false, ValidateQuery.isValidQuery(invalid2));
		assertEquals(false, ValidateQuery.isValidQuery(invalid3));
		assertEquals(false, ValidateQuery.isValidQuery(invalid4));
		assertEquals(false, ValidateQuery.isValidQuery(invalid5));
		assertEquals(false, ValidateQuery.isValidQuery(invalid6));
		assertEquals(false, ValidateQuery.isValidQuery(invalid7));
		assertEquals(false, ValidateQuery.isValidQuery(invalid8));
		assertEquals(false, ValidateQuery.isValidQuery(invalid9));
		assertEquals(false, ValidateQuery.isValidQuery(invalid10));
		assertEquals(false, ValidateQuery.isValidQuery(invalid11));
		assertEquals(false, ValidateQuery.isValidQuery(invalid12));
		assertEquals(false, ValidateQuery.isValidQuery(invalid13));
		assertEquals(false, ValidateQuery.isValidQuery(invalid14));
		assertEquals(false, ValidateQuery.isValidQuery(invalid15));
		assertEquals(false, ValidateQuery.isValidQuery(invalid16));
		assertEquals(false, ValidateQuery.isValidQuery(invalid17));
		assertEquals(false, ValidateQuery.isValidQuery(invalid18));
		assertEquals(false, ValidateQuery.isValidQuery(invalid19));
		assertEquals(false, ValidateQuery.isValidQuery(invalid01));
		assertEquals(false, ValidateQuery.isValidQuery(invalid02));
		assertEquals(false, ValidateQuery.isValidQuery(invalid03));
		assertEquals(false, ValidateQuery.isValidQuery(invalid04));
		assertEquals(false, ValidateQuery.isValidQuery(invalid05));
		assertEquals(false, ValidateQuery.isValidQuery(invalid06));
		assertEquals(false, ValidateQuery.isValidQuery(invalid07));
		assertEquals(false, ValidateQuery.isValidQuery(invalid08));
		assertEquals(false, ValidateQuery.isValidQuery(invalid09));
		assertEquals(false, ValidateQuery.isValidQuery(invalid010));
		assertEquals(false, ValidateQuery.isValidQuery(invalid20));
		assertEquals(false, ValidateQuery.isValidQuery(invalid21));
	}
}
