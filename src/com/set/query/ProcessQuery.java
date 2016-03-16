package com.set.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.set.ds.query.ClassQ;
import com.set.ds.query.impl.NearLiteral;
import com.set.ds.query.impl.NormalLiteral;
import com.set.ds.query.impl.NotLiteral;
import com.set.ds.query.impl.NotNearLiteral;
import com.set.ds.query.impl.NotPhraseLiteral;
import com.set.ds.query.impl.PhraseLiteral;

//FAILING ON THIS CASE: mango shakes (smoothies + mango)
public class ProcessQuery {
	public static ArrayList<ClassQ> objectifyQuery (String str) {
//		String str = "shakes \"jamba juice\" + smoothies mango (\"jamba juice\")";
		str = str.trim();
		System.out.println(str);
		// Strip those brackets
		str = str.replaceAll("[\\(\\)]", "");
		String arrOR[] = str.split("\\+");
		
		//rearrange
		// split on +
		// find any phrase query -> get it -> substitute the string with ""
		// split on \\s
		// end rearrange
		
//		Pattern phrase = Pattern.compile("\"(.+?)\"");
		Pattern notPhrase = Pattern.compile("(-?\"(.+?)\")");
		
		ArrayList<ClassQ> orLiteralArr = new ArrayList<>();
		for (String s: arrOR) {
			if (!s.isEmpty()) {
				s = Arrays.stream(s.split(" ")).distinct().collect(Collectors.joining(" "));
				// get phrase words
				ClassQ q = new ClassQ();
				Matcher m = notPhrase.matcher(s);
				while (m.find()) {
					if (m.group(1).startsWith("-")) {
						if  (m.group(2).contains("/")) {
							NotNearLiteral pl = new NotNearLiteral();
						    pl.setNotNearLiteral(m.group(2));
						    q.setQueryLiteral(pl);
						    System.out.println("NOT NEAR LIT## " +m.group(2));
						} else {
							NotPhraseLiteral nl = new NotPhraseLiteral();
							nl.setNotPhraseLiteral(m.group(2));
							q.setQueryLiteral(nl);
							System.out.println("NOT PHRASE LIT## " +m.group(2));
						}
					} else if  (m.group(2).contains("/")) {
						NearLiteral pl = new NearLiteral();
					    pl.setNearLiteral( m.group(2));
					    q.setQueryLiteral(pl);
					    System.out.println("NEAR LIT## " +m.group(2));
					} else {
						PhraseLiteral pl = new PhraseLiteral();
					    pl.setPhraseLiteral( m.group(2));
					    q.setQueryLiteral(pl);
					    System.out.println("PHRASE LIT## " +m.group(2));
					}
				}
				// remove phrase words from the string
				s = s.replaceAll("-?\"(.+?)\"", "").trim();
				// get literals
				String strArr[] = s.split("\\s+");
				for (String literal: strArr) {
					if (!literal.isEmpty()) {
						if (literal.startsWith("-")) {
							literal = literal.substring(1);
							NotLiteral nl = new NotLiteral();
							nl.setNotLiteral(literal);
							q.setQueryLiteral(nl);
							System.out.println("NOT LIT## " +literal);
						} else {
							NormalLiteral nl = new NormalLiteral();
							nl.setNormalLiteral(literal);
							q.setQueryLiteral(nl);
							System.out.println("NORMAL LIT## " +literal);
						}
					}
				}
				// put it all together now
				// or of and's
				System.out.println("###########OR############");
				orLiteralArr.add(q);
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		//orLiteralArr has the processed query now.
		// do we really need to convert the query into an object ? any advantages ?
		// - I see n^2 for loop, review and remove it.
		return orLiteralArr;
	}
}
