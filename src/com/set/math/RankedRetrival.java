package com.set.math;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.set.constants.query.ModeConst;
import com.set.index.impl.DiskInvertedIndex;
import com.set.token.processing.pre.PorterStemmer;

public class RankedRetrival {

	public static List<Integer> rankedQuery(String s, DiskInvertedIndex index, int mode){
		String path = index.getDirPath();
		List<String> fileNames = index.getFileNames();
		HashMap<Double, Integer> docsWithAcc  = new HashMap<>();
		HashMap<Integer, Double> accWithDoc = new HashMap<>();
		double total = fileNames.size();
		double averageWeight = 0.00;
		if ( ModeConst.OKAPI == mode) {
			averageWeight = EuclidianNormalization.getAverageWeight(path,(int)total);
		}
		if(s==null){
			return new ArrayList<Integer>();
		}
		String[] terms = s.split(" ");
		Set<String> term= new HashSet<>(Arrays.asList(terms));
		for(String token : term){
			token.toLowerCase();
			token = tokenProcess(token);
			int[][] docIdsNTermFreq = index.getTermFrequenciesWithDocIds(token);
			int[] docIds =  docIdsNTermFreq[0];
			int[] tfreq =  docIdsNTermFreq[1];
			if (docIds == null) {
				continue;
			}
			int len = docIds.length;
			double Wqt= calcWqt(docIds, total, token, mode);
			double[] Ad= new double[len];
			
			for(int i=0;i<docIds.length;i++){
				double Wdt = calcWdt(tfreq, i,docIds[i], path, mode,averageWeight);
				
				if(accWithDoc.containsKey(docIds[i])) {
					Ad[i] = accWithDoc.get(docIds[i]);
				}
				
				Ad[i] += (Wqt*Wdt);//EuclidianNormalization.readOneENCalc(path, docIds[i]) ;
				
				
				accWithDoc.put(docIds[i], Ad[i]);	
				
			}
		}
		
		for(int i:accWithDoc.keySet()){
			double Ad = accWithDoc.get(i);
			//Ad = Ad/EuclidianNormalization.readOneENCalc(path, i);
			//docsWithAcc.remove(Ad);
			String name= fileNames.get(i);
			double ld = calcLd(i, name, path, mode);
			Ad = Ad/ld;
			
			docsWithAcc.put(Ad,i);
		}
		return getTopTen(docsWithAcc, fileNames);
	}
	
	private static double calcWdt(int termFreq[],int number,int docID, String path , int mode,double averageWeight){
		
		double termF= termFreq[number];
		
		double Wdt =0;
		double kd = 0;
		
		switch (mode) {
		case ModeConst.DEFAULT:
			return 1.00+Math.log(termF);
			

		case ModeConst.TRADITIONAL:
			
			return termF;
			
		case ModeConst.OKAPI:
			double docWeight = EuclidianNormalization.readOneENCalc(path, docID); 
			
			kd=1.2*(0.25+(0.75*(docWeight/averageWeight)));
			Wdt = (termF*2.2)/(kd+termF);
			return Wdt;
			
		case ModeConst.WACKY:
			Wdt = 1+ Math.log(1+termF);
			kd = 1+Math.log(EuclidianNormalization.readAverageFrequeny(path, docID));
			return Wdt/kd;
		}
		
		return 00.0;
	}

private static double calcWqt(int docIds[],double total, String token, int mode){
		
		if (docIds == null) {
			return 0.00;
		}
		int len = docIds.length;
		double docFreq=len;
		double freqRatio= 0;
		
		
		switch (mode) {
		case ModeConst.DEFAULT:
			freqRatio= total/docFreq;
			return Math.log(1.00+freqRatio);
			

		case ModeConst.TRADITIONAL:
			freqRatio= total/docFreq;
			//Wqt calculation
			return Math.log(freqRatio);
			
		case ModeConst.OKAPI:
			freqRatio= (total-docFreq+0.5)/(docFreq+0.5);
			return Math.log(freqRatio);
			
		case ModeConst.WACKY:
			freqRatio= (total-docFreq)/(docFreq);
			freqRatio = Math.log(freqRatio);
			return Math.max(0,freqRatio);
		}
		
		return 00.0;
	}
	

private static double calcLd(int docID,String name, String path, int mode){
	switch (mode) {
	case ModeConst.DEFAULT:
		return EuclidianNormalization.readOneENCalc(path, docID);
		

	case ModeConst.TRADITIONAL:
		return EuclidianNormalization.readOneENCalc(path, docID);
		
	case ModeConst.OKAPI:
		return 1.0;
		
	case ModeConst.WACKY:
		double ld = (new File(path, name).length());
		return Math.sqrt(ld);
	}
	
	return 00.0;
}

	private static String tokenProcess(String str){
		if(str!=null && !str.isEmpty()){
			str = str.toLowerCase();
			str = PorterStemmer.processToken(str);
		}
		return str;
	}
	
	private static List<Integer> getTopTen (HashMap<Double, Integer> docsWithAcc, List<String> fileNames) {
		List<Integer> rankedResult = new ArrayList<>();
		PriorityQueue<Double> finalList = new PriorityQueue<>(100,Collections.reverseOrder());
		for(double d: docsWithAcc.keySet()){
			finalList.add(d);
		}
		int flag=0;
		while(!finalList.isEmpty() && flag<10){
			double value= finalList.poll();
			int doc = docsWithAcc.get(value);
			String name = fileNames.get(doc);
			System.out.println(value + " : "+ name);
			rankedResult.add(doc);
			flag++;
		}
		return rankedResult;
	}

}
