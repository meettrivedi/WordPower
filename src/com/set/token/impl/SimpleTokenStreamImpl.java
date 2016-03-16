package com.set.token.impl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import com.set.token.inter.TokenStreamInter;

/**
Reads tokens one at a time from an input stream. Returns tokens with minimal
processing: removing all non-alphanumeric characters, and converting to 
lowercase.
*/
public class SimpleTokenStreamImpl implements TokenStreamInter {
   private Scanner mReader;

   /**
   Constructs a SimpleTokenStream to read from the specified file.
   */
   public SimpleTokenStreamImpl(File fileToOpen) throws FileNotFoundException {
      mReader = new Scanner(new FileReader(fileToOpen));
   }
   
   /**
   Constructs a SimpleTokenStream to read from a String of text.
   */
   public SimpleTokenStreamImpl(String text) {
      mReader = new Scanner(text);
   }

   /**
   Returns true if the stream has tokens remaining.
   */
   @Override
   public boolean hasNextToken() {
      return mReader.hasNext();
   }

   /**
   Returns the next token from the stream, or null if there is no token
   available.
   */
   @Override
   public String nextToken() {
      if (!hasNextToken())
         return null;
      
      String next = mReader.next().replaceAll("\\W", "").toLowerCase();
      return next.length() > 0 ? next : 
       hasNextToken() ? nextToken() : null;
   }
}