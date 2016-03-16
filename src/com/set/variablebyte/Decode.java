package com.set.variablebyte;

import java.nio.ByteBuffer;

public class Decode {
	
	public static int decode(byte[] b) {
		byte [] bytesOfInt = new byte[]{0,0,0,0};
		int len = b.length;
		if (len != 4) {
			for (int i=0; i<len; i++) {
				bytesOfInt[4-len+i] = b[i];
			}
		}
		ByteBuffer wrapped = ByteBuffer.wrap(bytesOfInt);
		int num = wrapped.getInt();
		String s  = Integer.toBinaryString(num);
		return getIntFrmStr(decodedString(s));
	}
	
	private static int getIntFrmStr (String bitString) {
		int a = Integer.parseInt(bitString, 2);
	    return a;
	}
	
	private static String decodedString (String s) {
		int limiter = 8;
		int reminder = s.length()%limiter;
//		int quotient = s.length()/limiter;
		StringBuilder zeroedString = new StringBuilder();
		zeroedString.append(createZeroString(limiter-reminder));
		zeroedString.append(s);
		int length = zeroedString.length()/limiter;
		for (int i=0; i<length; i++) {
			zeroedString.deleteCharAt(i*limiter-i);
		}
//		int j=0;
//		while (true) {
//			String firstChar = ""+zeroedString.charAt(j);
//			if (firstChar.equals("0")) {
//				zeroedString.deleteCharAt(0);
//			} else if (firstChar.equals("1")) {
//				break;
//			}
//		}
		return zeroedString.toString();
	}
	
	private static String createZeroString (int i) {
		StringBuilder s = new StringBuilder();
		int j=0;
		while (j<i) {
			s.append('0');
			j++;
		}
		return s.toString();
	}
	
//	public static List<Integer> decode(byte[] byteStream) {
//        List<Integer> numbers = new ArrayList<Integer>();
//        int n = 0;
//        for (byte b : byteStream) {
//            if ((b & 0xff) < 128) {
//                n = 128 * n + b;
//            } else {
//                int num = (128 * n + ((b - 128) & 0xff));
//                numbers.add(num);
//                n = 0;
//            }
//        }
//        return numbers;
//    }
}
