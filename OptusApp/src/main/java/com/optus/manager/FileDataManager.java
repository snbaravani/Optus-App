package com.optus.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.optus.entiry.WordRankPair;
import com.optus.entiry.WordSearchList;

/**
 * This class contains methods to manipulate input words. It also contains
 * methods to to fetch top ranked words and find the number of occurrences for a
 * given word and for csv formatting
 * 
 * @author Subbu
 *
 */
@Component
public class FileDataManager implements ResourceLoaderAware {
	
	private static final Logger logger = Logger.getLogger(FileDataManager.class);
	private ResourceLoader resourceLoader;

	/** Holds the words and their counts in order **/
	private TreeMap<Integer, ArrayList<String>> wordRankMap;

	/** Holds words and their individual count [John, 5] **/
	private HashMap<String, Integer> wordMap;

	/** Holds a list of words and their counts--> [ Alex, Peter], 10 | [John], 5 **/
	private HashMap<Integer, ArrayList<String>> wordListCountMap;

	/**
	 * This method is called only during the post instantiation. It creates a
	 * cache of words and their counts and stores them in descending order of
	 * occurrences
	 */
	@PostConstruct
	public void readFileContent() {
		logger.info(" Reading the input file and generating artifacts ");
		wordMap = new HashMap<String, Integer>();
		wordListCountMap = new HashMap<Integer, ArrayList<String>>();
		Resource banner = resourceLoader.getResource("classpath:wordSource.txt");
		InputStream in = null;
		try {
			in = banner.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while (true) {
			String line = null;
			try {
				line = reader.readLine();
				if (line == null)
					break;
				generateWordMaps(line);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		wordRankMap = new TreeMap<Integer, ArrayList<String>>(wordListCountMap);
	}

	/**
	 * It fetches the top N words and their counts and returns this data in a
	 * list
	 * 
	 * @param rank
	 * @return
	 */
	public ArrayList<WordRankPair> getTopWords(int rank) {
		HashMap<ArrayList<String>, Integer> rankMap = new HashMap<ArrayList<String>, Integer>();
		ArrayList<WordRankPair> wordRankList = new ArrayList<WordRankPair>();
		for (int i = wordRankMap.size(); i > 0 && rank > 0; i--) {
			if (wordRankMap.get(i).isEmpty()) {
				continue;
			}
			WordRankPair rankPair = new WordRankPair();
			rankPair.setWords(wordRankMap.get(i));
			rankPair.setRank(wordRankMap.ceilingKey(i));
			wordRankList.add(rankPair);
			rank--;
		}

		return wordRankList;
	}

	/**
	 * Convert the word and rank object to csv format
	 * 
	 * @param response
	 * @param wordRankList
	 * @throws IOException
	 */
	public void convertToCsv(HttpServletResponse response, ArrayList<WordRankPair> wordRankList) throws IOException {
		logger.info("Genrating csv data...");
		String csvFileName = "books.csv";
		response.setContentType("text/csv");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
		response.setHeader(headerKey, headerValue);
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "Words", "Rank" };
		csvWriter.writeHeader(header);
		for (WordRankPair pair : wordRankList) {
			csvWriter.write(pair, header);
		}

		csvWriter.close();
	}

	/**
	 * For the given keys, it returns the number of occurrences
	 * 
	 * @param keys
	 * @return
	 */
	public WordSearchList getWordsCount(List<String> keys) {
		HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
	    WordSearchList list = new WordSearchList();
		for (String key : keys) {
			int count = (wordMap.get(key) != null) ? wordMap.get(key): 0;
			wordCountMap.put(key, count);
		}
		list.setCounts(wordCountMap);
		
		return list;
	}

	/**
	 * Generate all maps
	 * 
	 * @param line
	 */
	private void generateWordMaps(String line) {
		logger.info("Generating word maps");
		if (line.isEmpty()) {
			return;
		}

		line = line.replaceAll(",", "");
		line = line.replaceAll("\\.", "");
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreElements()) {
			String word = (String) tokenizer.nextElement();
			if (word.isEmpty() || word == "") {
				continue;
			}

			if (wordMap.containsKey(word)) {
				int count = (Integer) wordMap.get(word);
				ArrayList<String> wordList = wordListCountMap.get(count);
				wordList.remove(word);
				wordListCountMap.put(count, wordList);
				wordMap.put(word, ++count);
				wordList = wordListCountMap.get(count);
				if (wordList == null) {
					wordList = new ArrayList<String>();
				}
				wordList.add(word);
				wordListCountMap.put(count, wordList);
			} else {
				wordMap.put(word, 1);
				ArrayList<String> wordList = wordListCountMap.get(1);
				if (wordList == null) {
					wordList = new ArrayList<String>();
				}
				wordList.add(word);
				wordListCountMap.put(1, wordList);
			}
		}

	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
