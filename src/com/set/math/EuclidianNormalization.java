package com.set.math;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

// Calculated per document
public final class EuclidianNormalization {
	private static final String fileName = "docWeights.bin";
	
	public static double calcTermWeight (int termCount) {
		return (1+Math.log(termCount));
	}
	
	public static double calcEuclidianNormalization (double[] termWeights) {
		double sumSquaredWeights = 0.0;
		for (int i=0; i< termWeights.length; i++) {
			sumSquaredWeights += Math.pow(termWeights[i], 2);
		}
		return Math.sqrt(sumSquaredWeights);
	}
	
	public static void persistENCalc (String path, ArrayList<Double> docWeights) {
		FileOutputStream weightsFile = null;
		try {
			weightsFile = new FileOutputStream(new File(path, fileName));
                        double[] docW= new double[docWeights.size()];
                        
			// First 4 bytes define the total count.
//			byte[] tCount = ByteBuffer.allocate(4).putInt(docWeights.size()).array();
//			weightsFile.write(tCount, 0, tCount.length);
			for (double docWeight: docWeights) {
				byte[] bytes = ByteBuffer.allocate(8).putDouble(docWeight).array();
				weightsFile.write(bytes, 0, bytes.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				weightsFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	// ture -> read all the document weights
	// false -> read one weight
	public static double[] readENCalc (String path, int nthDoc, boolean flag) {
		// read 8 byte long double docWeight
		RandomAccessFile weightsFile = null;
		double[] docWeights = new double[0];
		try {
			weightsFile = new RandomAccessFile(new File(path, fileName), "r");
			long skipToLocation = (nthDoc)*16;
			// seek to the position in the file where the postings start.
			weightsFile.seek(skipToLocation);
			// read the 4 bytes for the document frequency
//			byte[] tCount = new byte[4];
//			weightsFile.read(tCount, 0, tCount.length);
			int count = 0;
			if (flag) {
//				count = ByteBuffer.wrap(tCount).getInt();
			} else {
				count = 1;
			}
			docWeights = new double[count];
			for (int i=0; i<count; i++) {
				// read the 8 bytes for the document weight
				byte[] buffer = new byte[8];
				weightsFile.read(buffer, 0, buffer.length);
				// use ByteBuffer to convert the 8 bytes into an double.
				double docWeight = ByteBuffer.wrap(buffer).getDouble();
				docWeights[i] = docWeight;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				weightsFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return docWeights;
	}
	
	public static double readOneENCalc (String path, int nthDoc) {
		return readENCalc (path, nthDoc, false)[0];
	}
	
	public static double getAverageWeight(String path,int total){
		double average=0;
		RandomAccessFile weightsFile = null;
		try {
			weightsFile = new RandomAccessFile(new File(path, fileName), "r");
			byte[] buffer = new byte[8];
			weightsFile.seek(0);
			weightsFile.read(buffer, 0, buffer.length);
			double i=1;
			while(i<=total){
				average += ByteBuffer.wrap(buffer).getDouble();
				weightsFile.skipBytes(8);
				weightsFile.read(buffer, 0, buffer.length);
				i++;
			}
		average = average/i;
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try {
				weightsFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return average;
	}
	
	public static double readAverageFrequeny (String path, int nthDoc) {
		// read 8 byte long double docWeight
		RandomAccessFile weightsFile = null;
		
		double termFerqAvg=0;
		try {
			weightsFile = new RandomAccessFile(new File(path, fileName), "r");
			long skipToLocation = (nthDoc)*16;
			// seek to the position in the file where the postings start.
			weightsFile.seek(skipToLocation);
			// read the 4 bytes for the document frequency
//			byte[] tCount = new byte[4];
//			weightsFile.read(tCount, 0, tCount.length);
			byte[] buffer = new byte[8];
			weightsFile.skipBytes(8);
		
			weightsFile.read(buffer, 0, buffer.length);
			// use ByteBuffer to convert the 8 bytes into an double.
			termFerqAvg = ByteBuffer.wrap(buffer).getDouble();
				
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				weightsFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return termFerqAvg;
	}
	public static double[] getDocWeightAverage (String path, int nthDoc) {
		// read 8 byte long double docWeight
		RandomAccessFile weightsFile = null;
		double[] docWeights = new double[0];
		try {
			weightsFile = new RandomAccessFile(new File(path, fileName), "r");
			long skipToLocation = (nthDoc)*16;
			
			weightsFile.seek(skipToLocation);
	
			docWeights = new double[3];
			
				// read the 8 bytes for the document weight
				byte[] buffer = new byte[8];
				weightsFile.read(buffer, 0, buffer.length);
				// use ByteBuffer to convert the 8 bytes into an double.
				double docWeight = ByteBuffer.wrap(buffer).getDouble();
				docWeights[0] = docWeight;
				
				weightsFile.read(buffer, 0, buffer.length);
				
				double docTermAverage = ByteBuffer.wrap(buffer).getDouble();
				
				docWeights[1] = docTermAverage;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				weightsFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return docWeights;
	}
	
}