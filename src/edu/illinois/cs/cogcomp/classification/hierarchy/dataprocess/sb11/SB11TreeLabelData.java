package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11;

import java.util.HashSet;
import java.util.LinkedHashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTreeLabelData;

public class SB11TreeLabelData extends AbstractTreeLabelData {

	private static final long serialVersionUID = 1L;
	
	private String table;
	
	/**
	 * Holds SB11 taxonomy
	 * @param table SB11 table name
	 */
	public SB11TreeLabelData(String table) {
		super();
		this.table = table;
	}
	
	@Override
	public void readTreeHierarchy(String fileTopicHierarchyPath) {
		SB11TopicHierarchy sbTH = new SB11TopicHierarchy("EN", fileTopicHierarchyPath);
		//TODO find a way to provide table from the experiment class
		
		treeIndex.put("root", new HashSet<String>());

		if (table.equals("Byggdelar")) {

			LinkedHashMap<String, String> topicMap = sbTH.getTopicMappingBuilding();
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
			LinkedHashMap<String, String> topicMap = sbTH.getTopicMappingLandscape();
			for (String topicKey : topicMap.keySet()) {
				String topicValue = topicMap.get(topicKey);
				
				treeIndex.get("root").add(topicKey);
				parentIndex.put(topicKey, "root");
				treeLabelNameHashMap.put(topicKey, topicValue);
				
				treeIndex.put(topicKey, new HashSet<String>());
			}
		} else if (table.equals("Alternativtabell")) {
			LinkedHashMap<String, String> topicMap = sbTH.getTopicMappingAlternative();
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
