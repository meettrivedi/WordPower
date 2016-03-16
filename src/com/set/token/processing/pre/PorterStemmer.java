package com.set.token.processing.pre;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.regex.Pattern;

public class PorterStemmer {

   // a single consonant
   private static final String c = "[^aeiou]";
   private static final String o = "[^aeiouwxy]";
   // a single vowel
   private static final String v = "[aeiouy]";

   // a sequence of consonants; the second/third/etc consonant cannot be 'y'
   private static final String C = c + "[^aeiouy]*";
   // a sequence of vowels; the second/third/etc cannot be 'y'
   private static final String V = v + "[aeiou]*";

   // this regex pattern tests if the token has measure > 0 [at least one VC].
   private static final Pattern mGr0 = Pattern.compile("^(" + C + ")?" + V + C);
   private static final Pattern mGr1 = Pattern.compile("^(" + C + ")?" + V + C+ V + C);
   private static final Pattern mEq1 = Pattern.compile("^(" + C + ")?" + V + C+ "(" + V + ")?$");
   private static final Pattern mv  = Pattern.compile("^(.*" + v + ".*)$" );
   private static final Pattern md = Pattern.compile("("+c+")\\1$");
   private static final Pattern mo = Pattern.compile( C + v +"[^aeiouwxy]$");

  
   public static String processToken(String token) {
      //System.out.println(mGr0.pattern());
      if(token==null) return null;
	  if (token.length() < 3) {
         return token; // token must be at least 3 chars
      }
      // step 1a
      if (token.endsWith("sses")) {
         token = token.substring(0, token.length() - 2);
      }
      else if(token.endsWith("ies")){
          token= token.substring(0, token.length()-2);
      }
      else if(token.endsWith("ss")){
          token= token.substring(0, token.length()-2);
      }
      else if(token.endsWith("s")){
          token= token.substring(0, token.length()-1);
      }
      

      // step 1b
      String stem;
      boolean doStep1bb = false;
      //		step 1b
      if (token.endsWith("eed")) { // 1b.1
         
         stem = token.substring(0, token.length() - 3);
         if (mGr0.matcher(stem).find()) { // if the pattern matches the stem
            token = stem + "ee";
         }
      }
      else if(mv.matcher(token).find() && token.endsWith("ed")){
          stem = token.substring(0, token.length()-2);
          if(mv.matcher(stem).find()){
              token= stem;
              doStep1bb = true;
          }
          
      }
      
      else if(mv.matcher(token).find() && token.endsWith("ing")){
          stem =token.substring(0, token.length()-3);
          if(mv.matcher(stem).find()){
              token= stem;
              doStep1bb = true;
          }
      }
     
      if (doStep1bb) {
         if (token.endsWith("at") || token.endsWith("bl")
          || token.endsWith("iz")) {

            token = token + "e";
         }
         else if(md.matcher(token).find() && !token.endsWith("l") &&  !token.endsWith("s") &&  !token.endsWith("z")){
             token=token.substring(0, token.length()-1);
         }
         else if(mo.matcher(token).find() && mEq1.matcher(token).find()){
             token=token+"e";
         }
         // use the regex patterns you wrote for 1b*.4 and 1b*.5
      }
      
      
      if(token.endsWith("y"))
      {
          stem = token.substring(0, token.length()-1);
          
          if(mv.matcher(stem).find()){
          token = stem+"i";
          }
      }
     
     final String[][] step2pairs = {  new String[] {"ational", "ate"},{"tional","tion"},{"enci","ence"},{"anci","ance"},{"izer","ize"},{"abli","able"},{"alli","al"},{"entli","ent"},{"eli","e"},{"ousli","ous"},{"ization","ize"},{"ation","ate"},{"ator","ate"},{"alism","al"},{"iveness","ive"},{"fulness","ful"},{"ousness","ous"},{"aliti","al"},{"iviti","ive"},{"biliti","ble"}};
     
     for(int i=0;i<step2pairs.length;i++){
         
         if(token.endsWith(step2pairs[i][0]) ){
             stem = token.substring(0, token.length()-step2pairs[i][0].length());
             if(mGr0.matcher(stem).find()){
             token = stem + step2pairs[i][1];
            }
          break;}
         
     }
    
    
      final String[][] step3pairs = {  new String[] {"icate", "ic"},{"ative",""},{"alize","al"},{"iciti","ic"},{"ical","ic"},{"ful",""},{"ness",""}};
      for(int i=0;i<step3pairs.length;i++){
         
         if(token.endsWith(step3pairs[i][0]) ){
             
             stem = token.substring(0, token.length()-step3pairs[i][0].length());
             if(mGr0.matcher(stem).find()){
             token = stem + step3pairs[i][1];
             }
         break;}
         
     }
     
      final String[] step4 = new String[] {"al","ance","ence","er","ic","able","ible","ant","ement","ment","ent","tion","sion","ou","ism","ate","iti","ous","ive","ize"};
      for(int i=0;i<step4.length;i++){
         if(token.endsWith(step4[i]) ){
             
             stem = token.substring(0, token.length()-step4[i].length());
             //System.out.println(stem);
             if(mGr1.matcher(stem).find()){
                 token=stem;
                    }
         break;}
         
     }
     
      if(token.endsWith("e")){
      stem=token.substring(0, token.length()-1);
      if(mGr1.matcher(stem).find()){
          token = stem;
          
      }
      else if(mEq1.matcher(stem).find() && !mo.matcher(stem).find()){
          token = stem;
      }
      }
      if(md.matcher(token).find() && token.endsWith("l")){
          stem = token.substring(0, token.length()-1);
          if(mGr1.matcher(stem).find()){
              token=stem;
          }
      }
      
      return token;
   }
}
