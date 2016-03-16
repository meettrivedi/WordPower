package com.set.classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.set.token.impl.SimpleTokenStreamImpl;
import com.set.token.processing.pre.PorterStemmer;

public class Bayesian {
	static HashMap<String, List<Integer>> termInClass = new HashMap<>();
	static HashMap<String, List<Integer>> termDocsClass = new HashMap<>();
	static List<HashMap<String, Integer>> filteredTokens = new ArrayList<>();
	static List<String> fileNames = new ArrayList<>();
	static int numClasses = 4; //Just for reading classes while classifying do not take 4
	static PositionalInvertedBayesian index = new PositionalInvertedBayesian();
	static int totalDocs = 0;
	static double[] classDocs = new double[4];
	static double[] totalTerms = new double[4];
	static List<HashMap<String,Double>> filteredProbability = new ArrayList<>();
	
	//static Set<String> left= new HashSet<>();

	public static void main(String[] args) throws IOException {
		String stringPath = "C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\ALL";
		//Scanner in = new Scanner(System.in);
		final Path currentWorkingPath;

		currentWorkingPath = Paths.get(stringPath).toAbsolutePath();
		visitingFiles(currentWorkingPath, 1, null, null, null, 0, null);
		String[] dict = index.getDictionary();
		for (String dictToken : dict) {
			List<Integer> summary1 = new ArrayList<>();
			for (int j = 0; j < numClasses; j++) {
				summary1.add(0);
			}
			termInClass.put(dictToken, summary1);
		}

		String[] trainingSet = new String[4];
		trainingSet[0] = "C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\HAMILTON";
		trainingSet[1] = "C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\JAY";
		trainingSet[2] = "C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\MADISON";
		trainingSet[3] = "C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\HAMILTON AND MADISON";
		String[] dictionary = index.getDictionary();
		Path cWP;
		int i = 0;
		while (i < numClasses) {
			System.out.println("Learning class " + (i + 1));

			System.out.println("Learning class " + (i + 1));

			cWP = Paths.get(trainingSet[i]).toAbsolutePath();
			classCalculation(cWP, index, i, dictionary);
			i++;
		}
		//System.out.println("Enter no for feature set:");
		//siseOfFS = in.nextInt();
		final long st = System.currentTimeMillis();
		featureSeletion(dictionary);
		final long et = System.currentTimeMillis();
		preClassification();
		final long time = et - st;
		System.out.println(":     " + (float) time / 1000);
		System.out.println("Enter url for classification:");
		final Path cWP1 = Paths
				.get("C:\\Users\\Meet\\Desktop\\Fall_2015\\search_engine\\Federalist_ByAuthors\\HAMILTON OR MADISON")
				.toAbsolutePath();
		visitingFiles(cWP1, 2, null, null, null, 0, null);
//		System.out.println(left.size());
//		System.out.println(left);
	}
	
	public static void visitingFiles(final Path cWP1, final int type, HashMap<String, List<Integer>> tracker,
			final HashMap<String, List<String>> postingsHash, HashMap<String, Integer> tokenProb, final int classNo,
			final List<String> dictionary) throws IOException {

		Files.walkFileTree(cWP1, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				// make sure we only process the current working directory
				if (cWP1.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {

				if (file.toString().endsWith(".txt")) {

					if (type == 2) {
						String[] ans = { "Hamilton", "Jay", "Madison" };
						String fileName = file.getFileName().toString();
						fileNames.add(fileName);
						String answer = ans[classifyFile(file.toFile())];
						System.out.println(fileName + " in  class: " + answer);
					} else if (type == 1) {
						String fileName = file.getFileName().toString();
						fileNames.add(fileName);
						indexFile(file.toFile(), index, fileName);
						totalDocs++;
					}

					else {
						String fileName = file.getFileName().toString();
						if (fileNames.contains(fileName)) {

							for (String testToken : dictionary) {
								if(classNo==4){
									List<Integer> summary2= new ArrayList<>();
									summary2.add(0);summary2.add(0);summary2.add(0);summary2.add(0);
									if(!termDocsClass.containsKey(testToken)){
										termDocsClass.put(testToken, summary2);
										
									}
									if(!termInClass.containsKey(testToken)){
										termInClass.put(testToken, summary2);
									}
									
								}
								
								else{
									if (postingsHash.get(testToken).contains(fileName)) {
										List<Integer> summary1 = termInClass.get(testToken);
										int freq = summary1.get(classNo);
										freq = freq + index.getPositionFreq(testToken, fileName);
										summary1.set(classNo, freq);
										termInClass.put(testToken, summary1);
										if(!termDocsClass.containsKey(testToken)){
											
											List<Integer> summary2= new ArrayList<>();
											summary2.add(0);summary2.add(0);summary2.add(0);summary2.add(0);
											int freq2 = summary2.get(classNo);
											freq2++;
											summary2.set(classNo, freq2);
											termDocsClass.put(testToken, summary2);
											
										
									}
										else{
											
											List<Integer> summary2= termDocsClass.get(testToken);
											int freq2 = summary2.get(classNo);
											freq2++;
											summary2.set(classNo, freq2);
											termDocsClass.put(testToken, summary2);
										}
									
								}
								}
									
							}
						}
						classDocs[classNo]++;
					}

				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException e) {

				return FileVisitResult.CONTINUE;
			}

		});

	}

	private static void indexFile(File file, PositionalInvertedBayesian index, String fileName) throws FileNotFoundException {
		SimpleTokenStreamImpl simple = new SimpleTokenStreamImpl(file);
		int tokenNumber = 0;
		while (simple.hasNextToken()) {
			// System.out.println(tokenNumer);
			String s = simple.nextToken();

			if (s != null) {
				if (s.contains("-")) {
					String[] splited = s.split("-");
					String hyphenFull = "";
					for (int i = 0; i < splited.length; i++) {
						String hyphenToken = splited[i];
						// hyphenToken=PorterStemmer.processToken(hyphenToken);
						index.addTerm(hyphenToken, fileName, tokenNumber);
						hyphenFull += splited[i];
					}
					// hyphenFull =PorterStemmer.processToken(hyphenFull);
					index.addTerm(hyphenFull, fileName, tokenNumber);
					tokenNumber++;
				} else {
					// s=PorterStemmer.processToken(s);

					s = s.toLowerCase();
					s = PorterStemmer.processToken(s);
					index.addTerm(s, fileName, tokenNumber);
					tokenNumber++;
					// }
				}

			}

		}
		//termsPerfile.put(fileName, tokenNumber);
	}

	public static void classCalculation(Path p, PositionalInvertedBayesian index, int i, String[] dictionary)
			throws IOException {
		
		HashMap<String, List<String>> postingsHash = index.getMindexPostings();
		
		List<String> dict = Arrays.asList(dictionary);
		visitingFiles(p, 3, null, postingsHash, null, i, dict);
	}

	public static List<Double> mutualInformationNumbers(String term, int classNum, Set<String> dict) {
		List<Double> sendListPart = new ArrayList<>();
		if (dict.contains(term)) {
			
			double n11 = 0;
			//int i=0;
			if(termDocsClass.containsKey(term)){
				n11 = termDocsClass.get(term).get(classNum);
			}else{
				System.out.println(" token not present  "+term);
				System.out.println(termDocsClass.size()-(dict.size()));
				
			}
			
			//double n11 = termDocsClass.get(term).get(classNum); // present in doc, doc is in class
			double n10 = classDocs[classNum] - n11; // not present in doc, doc
													// in class

			double n01 = index.getPostings(term).size() - n11; // in doc, but
																// doc not in
																// class
			double n00 = totalDocs - n11 - n01 - n10; // not in doc, doc not in
														// class

			sendListPart.add(n11);
			sendListPart.add(n10);
			sendListPart.add(n01);
			sendListPart.add(n00);

		}

		return sendListPart;
	}

	public static void featureSeletion(String[] dic) {
		HashSet<String> dict = new HashSet<String>();
		dict.addAll(Arrays.asList(dic));
		for (int i = 0; i < numClasses-1; i++) {
			Queue<Double> queue = new PriorityQueue<>(Collections.reverseOrder());
			HashMap<String, Double> temp = new HashMap<>();
			for (String s : dic) {
				List<Double> mutualInfo = mutualInformationNumbers(s, i, dict);
				if (mutualInfo.size() != 0) {

					double total = totalDocs;
					double n11 = mutualInfo.get(0);
					double n10 = mutualInfo.get(1);
					double n01 = mutualInfo.get(2);
					double n00 = mutualInfo.get(3);
					double n1 = n11 + n01;
					double n0 = n00 + n10;

					Double iucInner = ((n11 / total) * Math.log((n11 * total) / (n11 + n01) * (n11 + n10)))
							+ ((n10 / total) * Math.log((n10 * total) / (n10 + n00) * (n11 + n10)))
							+ ((n01 / total) * Math.log((n01 * total) / (n01 + n00) * (n01 + n10)))
							+ ((n00 / total) * Math.log((n00 * total) / (n00 + n10) * (n00 + n01)));
					temp.put(s, iucInner);
					queue.add(iucInner);
				}

			}
			HashMap<String, Integer> filterTemp = new HashMap<>();
			Set<String> allTerms = temp.keySet();
			int flag = 0;
			while (flag < 200  && !queue.isEmpty()) {
				double poll = queue.poll();
				for (String s : allTerms) {
					if (temp.get(s) == poll) {
						if (!filterTemp.containsKey(s)) {
							filterTemp.put(s, termDocsClass.get(s).get(i));
							flag++;
						}
					}
				}

			}
			filteredTokens.add(filterTemp);
		}

	}

	public static int classifyFile(File file) throws FileNotFoundException {
		System.out.println(file.toString());
		SimpleTokenStreamImpl simple = new SimpleTokenStreamImpl(file);
		double sum[] = new double[numClasses];
		// int size = index.getMindex().size();
		double[] docProb = new double[4];
		for (int i = 0; i < numClasses-1; i++) {
			docProb[i] = (classDocs[i] / totalDocs);
		}

		while (simple.hasNextToken()) {
			// System.out.println(tokenNumer);
			String s = simple.nextToken();
			if (s != null) {
				s = s.toLowerCase();
				s = PorterStemmer.processToken(s);

				for (int i = 0; i < numClasses-1; i++) {

					HashMap<String, Double> temp = filteredProbability.get(i);
					Set<String> classTokens = filteredProbability.get(i).keySet();
					int size = temp.size();
					if (classTokens.contains(s)) {
						double prob = 0.0;

				if (termInClass.get(s).get(i)==null) {
					//System.out.println(s+" is not present");
//
					//left.add(s);
					prob = 1.0 / (totalTerms[i] + size+1);
							sum[i] += Math.log(prob);
		} 
				else{
					
					prob = filteredProbability.get(i).get(s);
					sum[i] += Math.log(prob);
				}
				}

				}
			}

		}
		double max = sum[0];
		int flag = 0;
		for (int i = 0; i < sum.length-1; i++) {
			sum[i] += Math.log(docProb[i]);
			if (sum[i] >= max) {
				max = sum[i];
				flag = i;
			}
		}
		return flag;
	}


	public static void preClassification(){
		
		for (int i = 0; i < numClasses-1; i++) {
			HashMap <String,Double> termProb = new HashMap<>();
			HashMap<String, Integer> temp = filteredTokens.get(i);
			Set<String> allFiltered = temp.keySet();
			int size= temp.size();
			for (String s : allFiltered) {
				totalTerms[i] += termInClass.get(s).get(i);
			}
			for (String s : allFiltered) {
				double probability = (1.0+termInClass.get(s).get(i))/(totalTerms[i]+size+1);
				termProb.put(s, probability);
			}
			
			filteredProbability.add(termProb);
		}
		
	}



}

Status 