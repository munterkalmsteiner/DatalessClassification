package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.httpclient.util.IdleConnectionHandler;

import com.sun.tools.javac.code.Attribute.Array;
import com.sun.tools.javac.util.Pair;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts.AbstractTreeLabelData;

public class SB11TreeLabelData extends AbstractTreeLabelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void readTreeHierarchy(String fileTopicHierarchyPath) {
		SB11TopicHierarchy sbTH = new SB11TopicHierarchy("EN", fileTopicHierarchyPath);

		List<Pair<String, String>> buildingMap = sbTH.getTopicMappingBuildingList();
		
		treeIndex.put("root", new HashSet<String>());
		
		for (Pair<String, String> kV: buildingMap) {
			int keyLength = kV.fst.length();
			String topicKey = kV.fst;
			String topicValue = kV.snd;
			if(keyLength == 1) {
				treeIndex.get("root").add(topicKey);
				parentIndex.put(topicKey, "root");
				treeLabelNameHashMap.put(topicKey, topicValue);
			}
			else {
				String parentId = kV.fst.substring(0, keyLength - 1);
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
