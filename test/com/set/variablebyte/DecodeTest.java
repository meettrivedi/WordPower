package com.set.variablebyte;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class DecodeTest {
	
	private static byte[] getByteArray (String bitString) {
		int a = Integer.parseInt(bitString, 2);
	    BigInteger bigInt = BigInteger.valueOf(a);      
	    return bigInt.toByteArray();
	}

	@Test
	public void test() {
		assertEquals(Decode.decode(getByteArray("0001111110100000")), 4000);
	}
	
	@Test
	public void test1() {
		assertEquals(Decode.decode(getByteArray("10000100")), 4);
	}
	@Test
	public void test2() {
		assertEquals(Decode.decode(getByteArray("0000000110000000")), 128);
	}
	
	@Test
	public void test3() {
		assertEquals(Decode.decode(getByteArray("0000001010000000")), 256);
	}
	
	@Test
	public void test4() {
		assertEquals(Decode.decode(getByteArray("001000010111010010100011")), 555555);
	}

}
