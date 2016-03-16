package com.set.variablebyte;
import java.math.BigInteger;

public class Encode {
	
	public static byte[] encode(int i) {
		return encodedString(Integer.toBinaryString(i));
	}
	
	private static byte[] encodedString (String s) {
		int limiter = 7;
		int reminder = s.length()%limiter;
		int quotient = s.length()/limiter;
		String[] arr;
		if (reminder>0) {
			arr = new String[quotient+1];
		} else {
			arr = new String[quotient];
		}
		StringBuilder zeroedString = new StringBuilder();
		zeroedString.append(createZeroString(limiter-reminder));
		zeroedString.append(s);
		
		int length = arr.length;
		StringBuilder finalEncodedString = new StringBuilder();
		for (int i=0; i<length; i++) {
			StringBuilder encodedString = new StringBuilder();
			
			if (i == length-1) {
				encodedString.append("1");
			} else {
				encodedString.append("0");
			}
			arr[i] = encodedString.append(zeroedString.substring(i*limiter, (i+1)*limiter)).toString();
			finalEncodedString.append(arr[i]);
		}
		return getByteArray(finalEncodedString.toString());
	}
	
	private static byte[] getByteArray (String bitString) {
		int a = Integer.parseInt(bitString, 2);
//		ByteBuffer bytes = ByteBuffer.allocate(bitString.length()/8).putInt(a);
//		byte[] array = bytes.array();
//		return array;
	    BigInteger bigInt = BigInteger.valueOf(a);      
	    return bigInt.toByteArray();
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
//	public static byte[] encodeNumber(int n) {
//        if (n == 0) {
//            return new byte[]{0};
//        }
//        int i = (int) (log(n) / log(128)) + 1;
//        byte[] rv = new byte[i];
//        int j = i - 1;
//        do {
//            rv[j--] = (byte) (n % 128);
//            n /= 128;
//        } while (j >= 0);
//        rv[i - 1] += 128;
//        return rv;
//    }
}
//StringBuilder sb = new StringBuilder();
//int j = 0;
//while (j <= quotient) {
//	sb.append(obj);
//	j++;
//}
//sb.append(createZeroString(reminder));

//if (reminder > 0) {
//	StringBuilder sb = new StringBuilder(quotient*8);
//	int i = 1;
//	while (i<quotient) {
//		if (i == 0) {
//			sb.insert(sb.length()-i*7, 1);
//		} else {
//			sb.insert(sb.length()-i*7, 0);
//		}
//		i++;
//	}
//	if (i == quotient) {
//		
//	}
//} else if (quotient == 0) {
//	StringBuilder sb = new StringBuilder();
//	sb.append('1');
//	sb.append(createZeroString(reminder-1));
//	sb.append(s);
//	System.out.println(sb.toString());
//}
