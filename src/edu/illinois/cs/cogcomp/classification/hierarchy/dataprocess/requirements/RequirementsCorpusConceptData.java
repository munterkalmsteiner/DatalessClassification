package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements;

import java.io.File;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractCorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.StopWords;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;


/**
 * Requirements data corpus processor
 * @author waleed
 *
 */
public class RequirementsCorpusConceptData extends AbstractCorpusConceptData {

	/***
	 * Pre-process the indexed corpus data. The following steps are applied:
	 * <ul>
	 * <li>Data is read.
	 * <li>White spaces and special symbols are removed.
	 * <li>Data is split into training and test.
	 * <li>Data is stored in corpusContentMap,
	 * corpustContentMapTraining,corpustContentMapTest
	 * 
	 * @param file         the directory where the data corpus index is stored
	 * @param random       a seeded random for data splitting.
	 * @param trainingRate specifies the rate of splitting the data into training
	 *                     and test data .
	 */
	@Override
	public void readCorpusContentOnly(String file, Random random, double trainingRate) {
		try {
			Directory inputDir = FSDirectory.open(new File(file));
			IndexReader reader = IndexReader.open(inputDir, true);			
			
			int maxDocNum = reader.maxDoc();
			for (int i = 0; i < maxDocNum; ++i) {
				if (reader.isDeleted(i) == false) {
					if (i % 10 == 0) {
						System.out.println("[Read Requirements Data: ] " + i + "docs ..");
					}
					Document doc = reader.document(i);
					String id = doc.get("uri");
					String text = " ";
					if (doc.get("documentTitle") != null) {
						text += " " + doc.get("documentTitle");
					}
					if (doc.get("sectionTitles") != null) {
						text += " " + doc.get("sectionTitles");
					}
//					if (doc.get("advice") != null) {
//						text += " " + doc.get("advice");
//					}
					text += " " + doc.get("text");
					text = text.replaceAll("\n", " ");
					text = text.replaceAll("\r", " ");
					text = text.replaceAll("\t", " ");
					text = text.replaceAll("\\pP", " ");
					// >|<=+`~!@#$%^&*()-_{}
//					text = text.replaceAll("[>|<=+@#$%^&*()_{}]", " ");
					text = text.replaceAll("[>|<=+`~!@#$%^&*()_{}]", " ");
					text = text.replaceAll("[1-9]", " ");

//					text = text.replaceAll("[^a-zA-Z\\s]", "");
					text = text.replaceAll("\\s+", " ").toLowerCase();
					if (id != null && text != null) {
						corpusContentMap.put(id, text);
						if (random.nextDouble() < trainingRate) {
							corpusContentMapTraining.put(id, text);
						} else {
							corpusContentMapTest.put(id, text);
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readCorpusContentOnly(String file, Random random, double trainingRate, Set<String> set) {
		try {
			Directory inputDir = FSDirectory.open(new File(file));
			IndexReader reader = IndexReader.open(inputDir, true);
			int maxDocNum = reader.maxDoc();
			for (int i = 0; i < maxDocNum; ++i) {
				if (reader.isDeleted(i) == false) {
					if (i % 10 == 0) {
						System.out.println("[Read Requirements Data: ] " + i + "docs ..");
					}
					Document doc = reader.document(i);

					String topic = doc.get("sb11labels"); // doc.get("Body");
					String id = doc.get("uri");
					if (id == null || topic == null) {
						continue;
					}
					if (set.contains(topic) == false) {
						continue;
					}

					String text = " "; // doc.get("Body");
					if (doc.get("documentTitle") != null) {
						text += " " + doc.get("documentTitle");
					}
					if (doc.get("sectionTitles") != null) {
						text += " " + doc.get("sectionTitles");
					}
					text += " " + doc.get("text");
					text = text.replaceAll("\n", " ");
					text = text.replaceAll("\r", " ");
					text = text.replaceAll("\t", " ");
					text = text.replaceAll("\\pP", " ");
					// >|<=+`~!@#$%^&*()-_{}
//					text = text.replaceAll("[>|<=+@#$%^&*()_{}]", " ");
					text = text.replaceAll("[>|<=+`~!@#$%^&*()_{}]", " ");
					text = text.replaceAll("[1-9]", " ");
					text = text.replaceAll("\\s+", " ").toLowerCase();
					if (id != null && text != null) {
						corpusContentMap.put(id, text);
						if (random.nextDouble() < trainingRate) {
							corpusContentMapTraining.put(id, text);
						} else {
							corpusContentMapTest.put(id, text);
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readCorpusContentOnly(String file, int readNum, Random random, double trainingRate) {
		try {
			Directory inputDir = FSDirectory.open(new File(file));
			IndexReader reader = IndexReader.open(inputDir, true);
			int maxDocNum = reader.maxDoc();
			for (int i = 0; i < readNum; ++i) {
				if (reader.isDeleted(i) == false) {
					if (i % 10 == 0) {
						System.out.println("[Read Requirements Data: ] " + i + "docs ..");
					}
					Document doc = reader.document(i);
					String id = doc.get("uri");
					String text = doc.get("text"); // doc.get("Body");
					if (doc.get("documentTitle") != null) {
						text += " " + doc.get("documentTitle");
					}
					if (doc.get("sectionTitles") != null) {
						text += " " + doc.get("sectionTitles");
					}
					text = text.replaceAll("\n", " ");
					text = text.replaceAll("\r", " ");
					text = text.replaceAll("\t", " ");
					text = text.replaceAll("\\pP", " ");
					text = text.replaceAll("[>|<=+`~!@#$%^&*()-_{}]", " ");
					text = text.replaceAll("[1-9]", " ");
					text = text.replaceAll("\\s+", " ").toLowerCase();
					if (id != null && text != null) {
						corpusContentMap.put(id, text);
						if (random.nextDouble() < trainingRate) {
							corpusContentMapTraining.put(id, text);
						} else {
							corpusContentMapTest.put(id, text);
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
