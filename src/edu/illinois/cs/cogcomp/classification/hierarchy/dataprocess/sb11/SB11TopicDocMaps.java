package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTopicDocMaps;

/**
 * 
 * @param file
 */

public class SB11TopicDocMaps extends AbstractTopicDocMaps {
	
	private String sb11FilePath = "";
	private String table = "";
	
	public SB11TopicDocMaps (String sb11FilePath, String table) {
		super();
		this.sb11FilePath = sb11FilePath;
		this.table = table;
	}
	
	@Override
	public void readTopicDocMap(String file) {

		SB11TreeLabelData treeLabelData = new SB11TreeLabelData(table);
		treeLabelData.readTreeHierarchy(this.sb11FilePath);
		HashMap<String, String> parentIndex = treeLabelData.getTreeParentIndex();
		try {
			Directory inputDir = FSDirectory.open(new File(file));
			IndexReader reader = IndexReader.open(inputDir, true);
			int maxDocNum = reader.maxDoc();
			for (int i = 0; i < maxDocNum; ++i) {
				if (reader.isDeleted(i) == false) { 
					if (i % 10 == 0) {
						System.out.println ("[Read SB11 Data for TopicMap: ] " + i + "docs ..");
					}
					Document doc = reader.document(i);
					String docID = doc.get("uri");
					String topic = doc.get("sb11Labels"); //doc.get("Body");
					if (docID == null || topic == null) {
						 continue;
					}
		
					this.populateMaps(docID, topic, parentIndex);
					
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFilteredTopicDocMap(String file, Set<String> docIDSet) {
		SB11TreeLabelData treeLabelData = new SB11TreeLabelData(table);
		treeLabelData.readTreeHierarchy(this.sb11FilePath);
		HashMap<String, String> parentIndex = treeLabelData.getTreeParentIndex();

		try {
			Directory inputDir = FSDirectory.open(new File(file));
			IndexReader reader = IndexReader.open(inputDir, true);
			int maxDocNum = reader.maxDoc();
			for (int i = 0; i < maxDocNum; ++i) {
				if (reader.isDeleted(i) == false) { 
					if (i % 10 == 0) {
						System.out.println ("[Read SB11 Data for TopicMap: ] " + i + "docs ..");
					}
					Document doc = reader.document(i);
					String docID = doc.get("uri");
					String topics = doc.get("sb11Labels"); //doc.get("Body");
					if (docID == null || topics == null) {
						 continue;
					}
					if (docIDSet.contains(docID) == false)
						continue;
					String[] topicArray = topics.split(" ");
					
					for (String topic: topicArray) {
						this.populateMaps(docID, topic, parentIndex);	
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateMaps(String docID, String topic, HashMap<String, String> parentIndex) {
		if (docID == null || topic == null || topic == "root") {
			 return;
		}
		
		if (topicDocMap.containsKey(topic) == true) {
			if (topicDocMap.get(topic).contains(docID) == false) {
				topicDocMap.get(topic).add(docID);
			}
		} else {
			topicDocMap.put(topic, new HashSet<String>());
			topicDocMap.get(topic).add(docID);
		}
	
		if (docTopicMap.containsKey(docID) == true) {
			if (docTopicMap.get(docID).contains(topic) == false) {
				docTopicMap.get(docID).add(topic);
			}
		} else {
			docTopicMap.put(docID, new HashSet<String>());
			docTopicMap.get(docID).add(topic);
		}
		
		
		String superTopic = parentIndex.get(topic);
		
		this.populateMaps(docID, superTopic, parentIndex);
		
	}
}
