package com.set.scraping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BuildCorpus {
//	http://www.latimes.com/topic/crime-law-justice/crime/02001000-topic.html?page=2&target=stories&spell=on&#trb_topicGallery_search
	private static final String base_url = "http://www.latimes.com";
	// start from page 1 and index through
	private static final String list_of_crimes = "/topic/crime-law-justice/crime/02001000-topic.html?page=%d&target=stories&spell=on#trb_topicGallery_search";
	
	public static void main(String[] args) {
		HashSet<String> unique_article_links = new HashSet<>();
		// i=25; i<60; 
		for (int i=0; i<60; i++) {
			try {
				Thread.sleep((long) (5 * 1000 + Math.floor(Math.random()*5000)));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			HashSet<String> ual = goToPageNumbered(i);
			if (ual.size() == 0) {
				break;
			} else {
				unique_article_links.addAll(ual);
			}
		}
		
		String dirName = createDir("CrimeCorpus");
		if (dirName.length() == 0) {
			System.exit(1);
		}
		int id = 0;
		for (String link: unique_article_links) {
			try {
				Thread.sleep((long) (5 * 1000 + Math.floor(Math.random()*5000)));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			saveToFile(String.format("%s/Article%d.txt", dirName, id), getArticle(link));
			id++;
		}
	}
	
	public static String createDir (String dirName) {
		// Build directory
		File f  = new File(dirName);
		if (!f.exists()) {
			if (f.mkdir()) {
				System.out.println("Created directory: " + dirName);
			} else {
				System.out.println("Failed to create directory: " + dirName);
				return "";
			}
		}
		return dirName;
	}
	
	public static boolean fileExists (String fname) {
		File f = new File(fname);
		if(f.exists() && !f.isDirectory()) { return true; }
		return false;
	}
	
	public static void saveToFile (String fname, ArrayList<String> paraTxt) {
		if (!fileExists(fname)) {
			PrintWriter writer;
			try {
				writer = new PrintWriter(fname, "UTF-8");
				for (String para: paraTxt) {
					writer.println(para);
				}
				writer.close();
				System.out.println("File created: "+ fname);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File name exists!");
		}
	}
	
	public static ArrayList<String> getArticle (String link) {
		ArrayList<String> paraTxt = new ArrayList<>();
		try {
			Document doc = getDocument(base_url+link);
			Elements  paragraphs = doc.select("section.trb_mainContent div.trb_article_page p");
			for (Element para : paragraphs) {
			  String linkHref = para.text();
			  paraTxt.add(linkHref);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paraTxt;
	}
	public static Document getDocument (String link) throws IOException {
		Document doc = Jsoup.connect(link).get();
		return doc;
	}
	
	public static int getArticleCount (Document doc) {
		// Find total number of articles
		Elements all_category= doc.select("span.trb_topicGallery_search_filters_category_a_span");
		int total_articles = 0;
		for (Element category: all_category) {
			if (category.text() == "stories") {
				total_articles = Integer.parseInt(category.data());
				break;
			}
		}
		return total_articles;
	}
	
	public static HashSet<String> goToPageNumbered (int pgNo) {
		System.out.println(pgNo);
		String page_link = String.format(list_of_crimes, pgNo);
		HashSet<String> article_links = new HashSet<>();
		try {
			Document doc = getDocument(base_url+page_link);
//			int total_articles = getArticleCount(doc);
			Elements  articl_links = doc.select("section.trb_outfit_sections div.trb_search_results div.trb_search_result a.trb_search_result_title[href]");
//			Elements  pagination = doc.select("div.trb_search_pagination a[href].trb_search_pagination_page");
			for (Element link : articl_links) {
			  String linkHref = link.attr("href");
			  article_links.add(linkHref);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return article_links;
	}
}
