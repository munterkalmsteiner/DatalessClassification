package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.newsgroups;

import java.util.HashMap;
import java.util.HashSet;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTreeLabelData;

/**
 * yqsong@illinois.edu
 * <p>
 * 
 * Purpose: an implementation for the abstract class
 * <code>AbstractTreeLabelData</code>. Handles reading the labels (topics or
 * taxonomy) as a tree that will be used to classify the raw data.
 * 
 * @see AbstractTreeLabelData AbstractTreeLabelData for more details.
 */

public class NewsgroupsTreeLabelData extends AbstractTreeLabelData {

	private static final long serialVersionUID = 1L;

	/***
	 * Reads the topic hierarchy for the 20newsgroup data which is stored in the
	 * class <code>NewsgroupsTopicHierarchy</code>. The following properties in the
	 * abstract class are populated: <code>treeIndex</code>,
	 * <code>parentIndex</code>, <code>treeLabelNameHashMap</code>
	 * <p>
	 * See picture for more details.
	 * <p>
	 * 
	 * @see AbstractTreeLabelData AbstractTreeLabelData for more details.
	 */

	@Override
	public void readTreeHierarchy(String fileTopicHierarchyPath) {
		NewsgroupsTopicHierarchy ngTH = new NewsgroupsTopicHierarchy();

		HashMap<String, HashMap<String, String>> topicHierarchy = ngTH.getTopicHierarchy();

		treeIndex.put("root", new HashSet<String>());
		for (String topic : topicHierarchy.keySet()) {
			HashMap<String, String> subTopics = topicHierarchy.get(topic);
			treeIndex.get("root").add(topic);
			parentIndex.put(topic, "root");
			treeLabelNameHashMap.put(topic, topic);

			treeIndex.put(topic, new HashSet<String>());
			for (String subTopicKey : subTopics.keySet()) {
				String subTopicName = subTopics.get(subTopicKey);
				treeIndex.get(topic).add(subTopicKey);
				parentIndex.put(subTopicKey, topic);
				treeLabelNameHashMap.put(subTopicKey, subTopicName);
			}
		}
	}

	@Override
	public void readTopicDescription(String topicDescriptionPath) {

	}

}
