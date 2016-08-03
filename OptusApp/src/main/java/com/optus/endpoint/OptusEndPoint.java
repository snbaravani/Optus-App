package com.optus.endpoint;
import static com.optus.constants.OptusConstants.SEARCH;
import static com.optus.constants.OptusConstants.TOPWORDS;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.optus.entiry.SearchText;
import com.optus.entiry.WordSearchList;
import com.optus.manager.FileDataManager;

/**
 * End-pont entry class for Optus Rest application
 * 
 * @author Subbu
 *
 */
@Controller
@RequestMapping("/counter-api")
public class OptusEndPoint {

	private static final Logger logger = Logger.getLogger(OptusEndPoint.class);
	@Autowired
	private FileDataManager fileManager;

	@RequestMapping(value = SEARCH, method = RequestMethod.POST)
	@ResponseBody
	public WordSearchList getWordCount( @RequestBody SearchText searchText) {
		 logger.info("getWordCount");
         return fileManager.getWordsCount(searchText.getSearchText());
    }

	@RequestMapping(value = TOPWORDS, method = RequestMethod.GET)
	@ResponseBody
	public void getTopWords(HttpServletResponse response, @PathVariable int rank) throws IOException {
	    logger.info("getTopWords");
		fileManager.convertToCsv(response, fileManager.getTopWords(rank));
	}

}