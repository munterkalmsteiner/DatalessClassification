package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.Annotation.ClassificationSystem;
import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer;
import se.bth.serl.flatclassifier.utils.NLP.Language;

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
		DataParser parser = new DataParser(fname);
		if (parser.parse()) {
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
		}
		
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
		String uri = req.getReqId();
		String documentTitle = req.getDocumentTitle(Language.EN);
		String sectionTitles = req.getSectionTitlesString(Language.EN);
		String text = req.getText(Language.EN);
		String advice = req.getAdvice(Language.EN) == null ? "" : req.getAdvice(Language.EN);
		String sb11Labels = req.getLabelsString(ClassificationSystem.SB11, Language.EN, table).toLowerCase();
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
