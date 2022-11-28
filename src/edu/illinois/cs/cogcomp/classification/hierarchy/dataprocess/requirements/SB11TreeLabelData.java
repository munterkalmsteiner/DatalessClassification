package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements;

import java.util.HashSet;
import java.util.LinkedHashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTreeLabelData;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class SB11TreeLabelData extends AbstractTreeLabelData {

	private static final long serialVersionUID = 1L;
	
	private String table;
	private String lang;
	/**
	 * Holds SB11 taxonomy
	 * @param table SB11 table name
	 */
	public SB11TreeLabelData(String table, String lang) {
		super();
		this.table = table;
		this.lang = lang;
	}
	
	@Override
	public void readTreeHierarchy(String fileTopicHierarchyPath) {
		CSTopicHierarchy sbTH = new CSTopicHierarchy("SB11","EN", fileTopicHierarchyPath);
		//TODO find a way to provide table from the experiment class
		
		treeIndex.put("root", new HashSet<String>());

		if (table.equals("Byggdelar")) {

			LinkedHashMap<String, String> topicMap = sbTH.getTopicHierarchy("Byggdelar");
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
		} else if (table.equals("Landskapsinformation")) {
			LinkedHashMap<String, String> topicMap = sbTH.getTopicHierarchy("Landskapsinformation");
			for (String topicKey : topicMap.keySet()) {
				String topicValue = topicMap.get(topicKey);
				
				treeIndex.get("root").add(topicKey);
				parentIndex.put(topicKey, "root");
				treeLabelNameHashMap.put(topicKey, topicValue);
				
				treeIndex.put(topicKey, new HashSet<String>());
			}
		} else if (table.equals("Alternativtabell")) {
			LinkedHashMap<String, String> topicMap = sbTH.getTopicHierarchy("Alternativtabell");
			for (String topicKey : topicMap.keySet()) {
				int keyLength = topicKey.length();
				String topicValue = topicMap.get(topicKey);
				
				if (topicKey.substring(0,1).equalsIgnoreCase("U") && keyLength > 1)
				{
					String parentId = topicKey.substring(0, keyLength - 1);
					treeIndex.get(parentId).add(topicKey);
					parentIndex.put(topicKey, parentId);
					treeLabelNameHashMap.put(topicKey, topicValue);
					
				}
				else {
					treeIndex.get("root").add(topicKey);
					parentIndex.put(topicKey, "root");
					treeLabelNameHashMap.put(topicKey, topicValue);
				}
				
				treeIndex.put(topicKey, new HashSet<String>());
			}
		}
	}

	@Override
	public void readTopicDescription(String topicDescriptionPath) {
		// TODO Auto-generated method stub

	}

}
