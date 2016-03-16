package com.set.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateQuery {
	// Patterns should be tested, in the order they are presented here.
	private static final Pattern acceptedQueryContents = Pattern.compile("[\\-A-Za-z0-9\\s+\\(\\)\"\\/]+");
	private static final Pattern beingWith = Pattern.compile("^[-\\w\"(]");
	private static final Pattern endWith = Pattern.compile("[\\w\")]$");
	private static final Pattern beginNEndWith = Pattern.compile("^[\\w\"(].*[\\w\")]$");
	private static final Pattern consecutivePluses = Pattern.compile("\\+(\\s*?\\+)+");
	private static final Pattern validQuotesContent = Pattern.compile("[A-Za-z0-9\\s]+?");
	private static final Pattern validQuotes = Pattern.compile("\""+validQuotesContent+"\"");
	private static final Pattern quotesContent = Pattern.compile("\"(.+?)\"");
	private static final Pattern bracketsContent = Pattern.compile("\\((.+?)\\)");
	private static final Pattern emptyBracket = Pattern.compile("\\(\\s*\\)");
	private static final Pattern emptyQuote = Pattern.compile("\"\\s*\"");
	private static final Pattern invalidNot = Pattern.compile("--+");
	private static final Pattern shouldPreceedByNot = Pattern.compile("-\\s");
	private static final Pattern singleNot = Pattern.compile("-\\w+");
	private static final Pattern hasNot = Pattern.compile("-");
	private static final Pattern hasQuote = Pattern.compile("\"");
	private static final Pattern hasSlash = Pattern.compile("\\/");
	private static final Pattern validNearContent = Pattern.compile("\\w+\\s(NEAR\\/[1-9][0-9]*)?\\s?\\w+");
	private static final Pattern validNear = Pattern.compile("\""+validNearContent+"\"");
	private static final Pattern hypenWord = Pattern.compile("\\w+-\\w+");
	private static final Pattern notPhrase = Pattern.compile("(-\"(.+?)\")");
	
	// Recursive searches
	private static final String dontMatchBrackets = "[^()]";
	private static final String matchBracketPair = "\\(\\)";
	private static final String dontMatchQuotes = "[^\"]";
	private static final String matchQuote = "\"\"";
	
	private static final Pattern validBrackets = Pattern.compile("\\(([\\w+\\s]*?)\\)");
	
	public static boolean isValidQuery (String query) {
		query = query.trim();
		System.out.println(query);
		if (query.length()>0 &&
				acceptedQueryContents.matcher(query).matches() &&
				beingWith.matcher(query).find() &&
				endWith.matcher(query).find() &&
				isValidNot(query) &&
				!consecutivePluses.matcher(query).find() &&
				isValidBrackets(query) &&
				isValidQuote(query) &&
				isValidBracketContent(query) &&
				isValidNear(query)) {
			return true;
		}
		return false;
	}
	
	private static boolean isValidNot (String query) {
//		if (hasNot.matcher(query).find() && !singleNot.matcher(query).matches() && singleNot.matcher(query).find()) {
//			Matcher m = hasNot.matcher(query);
//			Matcher m1 = singleNot.matcher(query);
//			// checking to see if the no of '-'  matches "-\w*"
//			int i=0,j=0;
//			while (m.find()) {
//				i++;
//			}
//			while (m1.find()) {
//				j++;
//			}
//			if (i == j) {
//				return true;
//			} else {
//				// check for hypen words 
//				// count the hypen words and decrement j
//				// then compare i with j
//				// true if equal, else false.
//				return false;
//			}
//		} else if (!hasNot.matcher(query).find()) {
//			return true;
//		}
//		return true;
		if (singleNot.matcher(query).matches() || invalidNot.matcher(query).find() || shouldPreceedByNot.matcher(query).find() || notPhrase.matcher(query).matches()) {
			return false;
		}
		return true;
	}
	
	
	private static boolean isValidNear (String query) {
		if (hasSlash.matcher(query).find()) {
			Matcher m = hasSlash.matcher(query);
			Matcher m1 = validNear.matcher(query);
			int i=0, j=0;
			while (m.find()) {
				i++;
			}
			while (m1.find()) {
				j++;
			}
			if (i == j) {
				return true;
			} else {
				return false;
			}
			
		} 
		if (hasQuote.matcher(query).find()) {
			return isValidQuoteContent(query);
		}
		return true;
	}
	
	private static boolean isValidBracketContent (String query) {
		Matcher m = bracketsContent.matcher(query);
		if (m.find()) {
			if (validQuotesContent.matcher(m.group(1)).matches()) {
				return true;
			 } else {
				 return false;
			 }
		}
		return true;
	}
	
	private static boolean isValidQuoteContent (String query) {
		 Matcher m = quotesContent.matcher(query);
		 while (m.find()) {
			 if (validQuotesContent.matcher(m.group(1)).matches()) {
				 return true;
			 } else {
				 return false;
			 }
		 }
		 return false;
	}
	
	private static boolean isValidQuote (String query) {
		if (emptyQuote.matcher(query).matches()) {
			return false;
		}
		// First remove non-quotes:
		String str = query.replaceAll(dontMatchQuotes, "");
		String temp = "";
		int i = 0;
		// Then remove quote pairs recursively
		while (!str.equalsIgnoreCase(temp)) {
			if (i > 0) {
				// nested brackets not supported
				return false;
			}
			i++;
//			temp = str;
			str=str.replaceAll(matchQuote,"");
		}
		if (str.length() > 0) {
			return false;
		}
		return true;
	}
	
	private static boolean isValidBrackets (String query) {
		if (emptyBracket.matcher(query).matches()) {
			return false;
		}
		// First remove non-brackets:
		String str = query.replaceAll(dontMatchBrackets, "");
		String temp = "";
		int i = 0;
		// Then remove bracket pairs recursively
		while (!str.equalsIgnoreCase(temp)) {
			if (i > 0) {
				// nested brackets not supported
				return false;
			}
			i++;
//			temp = str;
			str=str.replaceAll(matchBracketPair,"");
		}
		if (str.length() > 0) {
			return false;
		}
		return true;
	}
}
