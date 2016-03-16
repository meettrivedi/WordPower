package com.set.token.processing.pre;

public class StripToAlphanumeric {
//	Remove all non-alphanumeric characters from the beginning and end.
	public String stip() {
		return "";
	}
//	For hyphens in words, do both:
//		(a) Remove the hyphens from the token and then proceed with the modied token;
//		(b) Split the original hyphenated token into two tokens without a hyphen, and proceed with both.
//		(So the token Hewlett-Packard would turn into HewlettPackard, Hewlett, and Packard.)
	public String[] stripHyphens() {
		return new String[] {};
	}
//	3. Convert the token to lowercase.
	public String toLowerCase(String token) {
		return token.toLowerCase();
	}
}
