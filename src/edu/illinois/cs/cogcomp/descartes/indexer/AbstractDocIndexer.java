/**
 * 
 */
package edu.illinois.cs.cogcomp.descartes.indexer;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.illinois.cs.cogcomp.descartes.AnalyzerFactory;
import edu.illinois.cs.cogcomp.descartes.similarity.UnNormalizedLuceneSimilarity;
import edu.illinois.cs.cogcomp.descartes.util.IOManager;

/**
 * @author Vivek Srikumar
 * 
 *         This is an abstract class that should be extended for each data
 *         corpus to be classified. This class handles the operations of reading
 *         and indexing the raw data using an IndexWriter.
 */
public abstract class AbstractDocIndexer {
	public class Stats {
		public int numPages;
		public int numIndexed;
	}

	protected final String fname;
	protected final String indexDir;
	protected final IndexWriter indexer;

	/**
	 * Creates a new AbstractDocIndexer to index the data in <code>fname<code>.
	 * 
	 * @param fname        the name of the file where the raw data is located.
	 * @param indexDir     the directory where the output of the indexing process
	 *                     will be stored.
	 * @param configFile   ???
	 * @param analyzerType the language based analyzer type identifed with language 
	 * @throws Exception
	 */
	public AbstractDocIndexer(String fname, String indexDir, String configFile, String analyzerType) throws Exception {
		this.fname = fname;
		this.indexDir = indexDir;
		if (IOManager.isDirectoryExist(this.indexDir)) {
			System.out.println(
					"The directory " + this.indexDir + " already exists in the system. " + "It will be deleted now.");
			IOManager.deleteDirectory(this.indexDir);
		}
		Analyzer analyzer = AnalyzerFactory.initialize(analyzerType);

		Directory dir = FSDirectory.open(new File(this.indexDir));

		indexer = new IndexWriter(dir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

		indexer.setSimilarity(new UnNormalizedLuceneSimilarity());

	}

	/**
	 * Creates an index for the raw data. In the implementation of this method,
	 * the data should be read, parsed as Documents and stored using the 
	 * IndexWriter in <code>indexDir</code>. 
	 * The purpose of using an indexer is to speed up the search process.
	 */
	public abstract Stats index() throws Exception;
}
