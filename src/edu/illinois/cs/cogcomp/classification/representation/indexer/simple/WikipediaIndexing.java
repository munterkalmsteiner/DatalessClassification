package edu.illinois.cs.cogcomp.classification.representation.indexer.simple;

import java.util.Date;

import edu.illinois.cs.cogcomp.descartes.AnalyzerFactory;
import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer;
import edu.illinois.cs.cogcomp.descartes.indexer.WikiDocIndexer;
import edu.illinois.cs.cogcomp.descartes.retrieval.simple.Searcher;

/**
 * yqsong@illinois.edu
 */

public class WikipediaIndexing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		String fname = "/shared/corpora/yqsong/data/wikipedia/crossLanguage/";
//		String indexDir = "/shared/corpora/yqsong/data/wikipedia/crossLanguageIndex/";
		String fname = "/home/waleed/Documents/Waleed's_PhD/DCAT/WP4/external_data/Waleed/";
		String indexDir = "/home/waleed/Documents/Waleed's_PhD/DCAT/WP4/external_data/Waleed/SVWikiIndex";
		String configFile = "conf/configurations.properties";
		String lang = "sv";
		String[] fileNames = {
				"svwiki-20220820-pages-articles.xml.bz2",
		};
		
		String[] indexNames = {
				"svwiki-20220820-original",
		};
		
		int langId = 0;
		try {
			WikiDocIndexer indexer = new WikiDocIndexer(fname + fileNames[langId], indexDir + indexNames[langId], configFile, lang);
			Date start = new Date();
			indexer.index();
			Date end = new Date();
			System.out.println("Done. " + (end.getTime() - start.getTime())
					/ (float) 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
