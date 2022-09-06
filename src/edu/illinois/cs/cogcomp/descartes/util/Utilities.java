package edu.illinois.cs.cogcomp.descartes.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.util.Version;

public class Utilities {

    private static Set<String> stopWords;
    private static CharArraySet charStopWords;

    public static Set<String> getStopWords() {
	if (stopWords == null) {
	    stopWords = new HashSet<String>();

	    stopWords.addAll(Arrays.asList("I", "a", "about", "an", "are",
		    "as", "at", "be", "by", "com", "de", "en", "for", "from",
		    "how", "in", "is", "it", "la", "of", "on", "or", "that",
		    "the", "this", "to", "was", "what", "when", "where", "who",
		    "will", "with", "und", "the", "www"));
	}
	return stopWords;
    }
    
    public static Set<String> getStopWords(String lang) {
    	String stopWordsFileName = "data/stopwords/stopwords_" + lang.toLowerCase() + ".txt";
		File stopWordsFile = new File(stopWordsFileName);
		if (stopWordsFile.exists() == false) {
			stopWordsFileName = "data/stopwords/stopwords_null.txt";
			stopWordsFile = new File(stopWordsFileName);
		}

		Path path = stopWordsFile.toPath();
		Reader reader = null;
		Set<String> charStopSet = new HashSet<String>();
		try {
			reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			BufferedReader br = getBufferedReader(reader);
			String word = null;
			while ((word = br.readLine()) != null) {
				charStopSet.add(word.trim());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

    	return charStopSet;
     }
    
    private static BufferedReader getBufferedReader(Reader reader) {
		return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
	}
    
    // public static Set<String> getStopWords(String configFile)
    // throws ConfigurationException {
    // if (stopWords == null) {
    //
    // stopWords = new HashSet<String>();
    // PropertiesConfiguration config = new PropertiesConfiguration(
    // configFile);
    // String s = config.getString("descartes.indexer.stopwords");
    //
    // stopWords.addAll(Arrays.asList(s.split(",+")));
    //
    // }
    // return stopWords;
    // }

}
