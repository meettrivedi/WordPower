package com.set.variablebyte;

import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;

import org.junit.Test;

public class EncodeTest {
	
	private static byte[] getByteArray (String bitString) {
		int a = Integer.parseInt(bitString, 2);
	    BigInteger bigInt = BigInteger.valueOf(a);      
	    return bigInt.toByteArray();
	}

	@Test
	public void test() {
		assertArrayEquals(Encode.encode(4000), getByteArray("0001111110100000"));
	}
	
	@Test
	public void test1() {
		assertArrayEquals(Encode.encode(4), getByteArray("10000100"));
	}
	
	@Test
	public void test2() {
		assertArrayEquals(Encode.encode(128), getByteArray("0000000110000000"));
	}
	
	@Test
	public void test3() {
		assertArrayEquals(Encode.encode(256), getByteArray("0000001010000000"));
	}

	@Test
	public void test4() {
		assertArrayEquals(Encode.encode(555555), getByteArray("001000010111010010100011"));
	}
	
}