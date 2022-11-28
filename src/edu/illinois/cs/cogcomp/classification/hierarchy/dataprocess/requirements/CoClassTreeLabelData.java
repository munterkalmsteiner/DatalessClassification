package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements;

import java.util.HashSet;
import java.util.LinkedHashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTreeLabelData;

public class CoClassTreeLabelData extends AbstractTreeLabelData {

	private static final long serialVersionUID = 1L;
	
	private String table;
	
	/**
	 * Holds CoClass taxonomy
	 * @param table CoClass table name
	 */
	public CoClassTreeLabelData(String table) {
		super();
		this.table = table;
	}
	
	@Override
	public void readTreeHierarchy(String fileTopicHierarchyPath) {
		CSTopicHierarchy coClassTH = new CSTopicHierarchy("CoClass","EN", fileTopicHierarchyPath);
		//TODO find a way to provide table from the experiment class
		
		treeIndex.put("root", new HashSet<String>());
		
		LinkedHashMap<String, String> topicMap = coClassTH.getTopicHierarchy(this.table);
		for (String topicKey : topicMap.keySet()) {
			int keyLength = topicKey.length();
			String topicValue = topicMap.get(topicKey);
			if (keyLength == 1) {
				treeIndex.get("root").add(topicKey);
				parentIndex.put(topicKey, "root");
				treeLabelNameHashMap.put(topicKey, topicValue);
			} else {
				String parentId = topicKey.substring(0, keyLength - 1);
				treeIndex.get(parentId).add(topicKey);
				parentIndex.put(topicKey, parentId);
				treeLabelNameHashMap.put(topicKey, topicValue);
			}

			treeIndex.put(topicKey, new HashSet<String>());
		}
	}

	@Override
	public void readTopicDescription(String topicDescriptionPath) {
		// TODO Auto-generated method stub

	}

}
