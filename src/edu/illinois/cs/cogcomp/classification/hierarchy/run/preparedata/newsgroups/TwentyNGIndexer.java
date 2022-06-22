package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.newsgroups;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;

import com.wojtuch.NewsgroupParser;
import com.wojtuch.models.NewsgroupsArticle;

import edu.illinois.cs.cogcomp.descartes.indexer.AbstractDocIndexer;

/**
 * 
 * Extends AbstractDocIndexer to handle the indexing process of the raw data of
 * 20newsgroups.
 *
 */
public class TwentyNGIndexer extends AbstractDocIndexer {

	/**
	 * Creates a new TwentyNGIndexer, and calls the parent constructor of
	 * AbstractDocIndexer where initialization is taken place.
	 * 
	 * @param fname    the name of the file where the raw data is located.
	 * @param indexDir the directory where the output of the indexing process will
	 *                 be stored.
	 */
	public TwentyNGIndexer(String fname, String indexDir) throws Exception {
		super(fname, indexDir, null, "standard");
	}

	/**
	 * Loads the articles of the 20newsgroup and creates an index. The articles are
	 * read from the folder fname, and converted into Documents. Then the Documents
	 * are indexed using IndexWriter.
	 * 
	 */
	@Override
	public Stats index() throws Exception {
		/*
		 * Waleed: NewsgroupParser is an external library to parse the data. The source
		 * code of NewsgroupParser can be found here
		 * https://github.com/wojtuch/20newsgroups-parser
		 */
		NewsgroupParser parser = new NewsgroupParser(new File(fname).getPath());
		parser.parse();

		parser.getArticles().forEach((key, articles) -> {
			articles.forEach(article -> {
				/*
				 * Waleed: reads the properties from the article and create a document with the
				 * fields Uri, NewsGroup, Subject, Content
				 **/
				Document doc = createDocument(article);
				try {
					indexer.addDocument(doc);
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		});

//        File startdir = new File(fname);
//        File[] topics = startdir.listFiles(new FileFilter()
//        {
//            
//            @Override
//            public boolean accept(File pathname)
//            {
//                return pathname.isDirectory();
//            }
//        });
//        
//        for (File topic : topics) {
//            File[] newsdocs = topic.listFiles(new FileFilter()
//            {
//                
//                @Override
//                public boolean accept(File pathname)
//                {
//                    return pathname.isFile();
//                }
//            });
//            
//            for (File newsdoc : newsdocs) {
//                Document doc = createDocument(topic.getName(), newsdoc);
//                indexer.addDocument(doc);
//            }
//        }

		System.out.println("Optimizing.");
		indexer.optimize();
		System.out.println("Finished optimizing");
		indexer.close();
		System.out.println("Done.");

		return null;
	}

	/**
	 * Creates a document from a given NewsgroupsArticle.
	 * 
	 * @param article the input article of type NewsgroupsArticle to be converted to
	 *                document.
	 * 
	 * @returns a document that includes the fields: uri, newsgroup, subject and
	 *          content.
	 */
	private Document createDocument(NewsgroupsArticle article) {
		String uri = article.getLabel() + "_" + article.getHeader("Message-ID");
		String newsgroup = article.getLabel();
		String subject = article.getHeader("Subject");
		String plain = article.getRawText();

		Document doc = new Document();

		// Uri
		Fieldable uriField = new Field("uri", uri, Field.Store.YES, Field.Index.NO);
		doc.add(uriField);

		// newsgroup
		Fieldable newsgroupField = new Field("newsgroup", newsgroup, Field.Store.YES, Field.Index.NO);
		doc.add(newsgroupField);

		// Subject
		Fieldable subjectField = new Field("Subject", subject, Field.Store.YES, Field.Index.ANALYZED);
		doc.add(subjectField);

		// Content
		Fieldable contentField = new Field("plain", plain, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES);
		doc.add(contentField);

		return doc;
	}

}
