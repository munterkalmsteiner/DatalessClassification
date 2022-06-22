package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;

import com.wojtuch.NewsgroupParser;
import com.wojtuch.models.NewsgroupsArticle;

import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer;
import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer.Stats;

/**
 * 
 * Extends AbstractDocIndexer to handle the indexing process of the raw data of
 * sb11
 *
 */
public class SB11Indexer extends AbstractDocIndexer {

	/**
	 * Creates a new SB11Indexer, and calls the parent constructor of
	 * AbstractDocIndexer where initialization is taken place.
	 * 
	 * @param fname    the name of the file where the raw data is located.
	 * @param indexDir the directory where the output of the indexing process will
	 *                 be stored.
	 * @param table    sb11Table name to read documents classified with this table
	 */
	private String table; 
	public SB11Indexer(String fname, String indexDir, String table) throws Exception {
		super(fname, indexDir, null, "standard");
		// TODO Auto-generated constructor stub
		this.table = table;
	}

	/**
	 * Loads the requirement annotated with sb11 and creates an index. The
	 * requirements are read from the folder fname, and converted into Documents.
	 * Then the Documents are indexed using IndexWriter.
	 * 
	 */
	@Override
	public Stats index() throws Exception {
		SB11DataParser parser = new SB11DataParser(fname);
		parser.parse("EN", this.table);

		for (Requirement req : parser.getRequirements()) {
			/*
			 * Waleed: reads the properties from the requirement and create a document with
			 * the reqs fields
			 **/
			Document doc = createDocument(req);
			try {
				indexer.addDocument(doc);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("Optimizing.");
		indexer.optimize();
		System.out.println("Finished optimizing");
		indexer.close();
		System.out.println("Done.");

		return null;
	}

	/**
	 * Creates a document from a given requirement.
	 * 
	 * @param article the input article of type requirement to be converted to
	 *                document.
	 * 
	 * @returns a document that includes the fields: uri, document, section, text,
	 *          and advice.
	 */
	private Document createDocument(Requirement req) {
		String uri = req.getDocumentTitle() + "_" + req.getId();
		String documentTitle = req.getDocumentTitle();
		String sectionTitles = req.getSectionTitles();
		String text = req.getText();
		String advice = req.getAdvice();
		String sb11Labels = req.getSB11Labels().toLowerCase();
		Document doc = new Document();

		// Uri
		Fieldable uriField = new Field("uri", uri, Field.Store.YES, Field.Index.NO);
		doc.add(uriField);

		// Document Title
		Fieldable docField = new Field("documentTitle", documentTitle, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(docField);

		// Section Titles
		Fieldable sectionField = new Field("sectionTitles", sectionTitles, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(sectionField);

		// Text
		Fieldable textField = new Field("text", text, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES);
		doc.add(textField);

		// Advice
		Fieldable adviceField = new Field("advice", advice, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(adviceField);

		// SB11 label (true labels)
		Fieldable sb11LabelField = new Field("sb11Labels", sb11Labels, Field.Store.YES, Field.Index.NO);

		doc.add(sb11LabelField);

		return doc;
	}

}
