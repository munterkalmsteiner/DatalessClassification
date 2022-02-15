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

public class TwentyNGIndexer
    extends AbstractDocIndexer
{

    public TwentyNGIndexer(String fname, String indexDir)
        throws Exception
    {
        super(fname, indexDir, null, "standard");
    }

    @Override
    public Stats index() throws Exception
    {
        NewsgroupParser parser = new NewsgroupParser(new File(fname).getPath());
        parser.parse();
        
        parser.getArticles().forEach((key, articles) -> {
            articles.forEach(article -> {
                Document doc = createDocument(article);
                try {
                    indexer.addDocument(doc);
                }
                catch (CorruptIndexException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
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
        Fieldable newsgroupField = new Field("newsgroup", newsgroup, 
                Field.Store.YES, Field.Index.NO);
        doc.add(newsgroupField);
        
        // Subject
        Fieldable subjectField = new Field("Subject", subject,
            Field.Store.YES, Field.Index.ANALYZED);
        doc.add(subjectField);
        
        // Content
        Fieldable contentField = new Field("plain", plain, Field.Store.YES, 
                Field.Index.ANALYZED, Field.TermVector.YES);
        doc.add(contentField);

        return doc;
    }

}
