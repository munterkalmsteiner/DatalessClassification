package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer;
import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer.Stats;
import se.bth.serl.word.classifier.utils.NLP.Language;

/**
 * 
 * Extends AbstractDocIndexer to handle the indexing process of the raw data of
 * coclass
 *
 */
public class CoClassIndexer extends AbstractDocIndexer {

	/**
	 * Creates a new CoClasIndexer, and calls the parent constructor of
	 * AbstractDocIndexer where initialization is taken place.
	 * 
	 * @param fname    the name of the file where the raw data is located.
	 * @param indexDir the directory where the output of the indexing process will
	 *                 be stored.
	 * @param table    coClassTable name to read documents classified with this table
	 */
	private String table; 
	public CoClassIndexer(String fname, String indexDir, String table) throws Exception {
		super(fname, indexDir, null, "standard");
		// TODO Auto-generated constructor stub
		this.table = table;
	}

	/**
	 * Loads the requirement annotated with coclass and creates an index. The
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
		String coClassLabels = req.getLabelsString(ClassificationSystem.COCLASS, Language.EN, table).toLowerCase();
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

		// CoClass label (true labels)
		Fieldable coClassLabelField = new Field("coClassLabels", coClassLabels, Field.Store.YES, Field.Index.NO);
		doc.add(coClassLabelField);

		return doc;
	}

}
