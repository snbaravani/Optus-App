package com.optus.entiry;

import java.util.HashMap;

/**
 * Response body for the the search API
 * 
 * @author Subbu 
 *
 */
public class WordSearchList {
	private HashMap<String, Integer> counts;

	public HashMap<String, Integer> getCounts() {
		return counts;
	}

	public void setCounts(HashMap<String, Integer> counts) {
		this.counts = counts;
	}

	 
}
