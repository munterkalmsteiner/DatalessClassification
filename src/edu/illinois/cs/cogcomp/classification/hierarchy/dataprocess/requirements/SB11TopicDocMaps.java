package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements;

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
	private Boolean includeSuperTopic = false;
	
	public SB11TopicDocMaps (String sb11FilePath, String table, Boolean includeSuperTopic) {
		super();
		this.sb11FilePath = sb11FilePath;
		this.table = table;
		this.includeSuperTopic = includeSuperTopic;
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
					Document doc = reader.document(i);
					String docID = doc.get("uri");
					String topics = doc.get("sb11Labels"); //doc.get("Body");
					if (docID == null || topics == null) {
						 continue;
					}
		
					String[] topicArray = topics.split(" ");
					
					for (String topic: topicArray) {
						//Doc without true labels will not be put in the maps
						if(topic.isBlank()) {
							continue;
						}
						this.populateMaps(docID, topic.toLowerCase(), parentIndex);	
					}					
				}
			}
			System.out.println ("TopicDocMap: Read SB11 data with true labels for " + maxDocNum + " docs.");
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
			int readDocs = 0;
			for (int i = 0; i < maxDocNum; ++i) {
				if (reader.isDeleted(i) == false) { 
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
						//Doc without true labels will not be put in the maps
						if(topic.isBlank()) {
							continue;
						}
						this.populateMaps(docID, topic.toLowerCase(), parentIndex);	
						readDocs++;
					}	
				}
			}
			System.out.println ("TopicDocMap: Read SB11 data with true labels for " + readDocs + " docs.");
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
		
		
		//String superTopic = parentIndex.get(topic);
		
	//	this.populateMaps(docID, superTopic, parentIndex);
		
	}
}
