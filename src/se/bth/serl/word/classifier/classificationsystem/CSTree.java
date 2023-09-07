package se.bth.serl.word.classifier.classificationsystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.SB11TreeLabelData;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;

public class CSTree {
	
	private CSTreeNode rootNode;
	private HashMap<String, String> parentIndex;
	private HashMap<String, HashSet<String>> childrenIndex;
	private HashMap<String, CSTreeNode> nodesIndex;
	private ClassificationSystem cs;
	private String table;
	private String csFile;
	
	public CSTree (ClassificationSystem cs, String table, String csFile) {
		rootNode = null;
		nodesIndex = new HashMap<String, CSTreeNode>();
		childrenIndex = new HashMap<>();
		parentIndex = new HashMap<>();
		this.table = table;
		this.csFile = csFile;
	}
	
	public void initialize() {
		SB11TreeLabelData sb11TreeData = new SB11TreeLabelData(table);
		sb11TreeData.readTreeHierarchy(csFile);
		sb11TreeData.getTreeChildrenIndex();
		
	}

	public void CreateNode(CSTreeNode node, String id, String parentId) {
		if(parentId.isBlank()) {
			parentIndex.put(id, id);
			return;
		}else {
			childrenIndex.get(parentId).add(id);	
			parentIndex.put(id, parentId);	
		}
		nodesIndex.put(id, node);		
	};
	
	public CSTreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(CSTreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public HashMap<String, HashSet<String>> getChildrenIndex() {
		return childrenIndex;
	}
	
	public HashMap<String, CSTreeNode> getNodesIndex() {
		return nodesIndex;
	}
	
	public HashMap<String, String> getParentIndex() {
		return parentIndex;
	}
	
	public List<CSTreeNode> children(String parentId) {
		HashSet<String> indices = childrenIndex.get(parentId);
				
		return nodesIndex.entrySet()
	                        .stream()
	                        .filter(a -> indices.contains(a.getKey()))
	                        .map(e -> e.getValue())
	                        .collect(Collectors.toList());
	                        
	}
}
