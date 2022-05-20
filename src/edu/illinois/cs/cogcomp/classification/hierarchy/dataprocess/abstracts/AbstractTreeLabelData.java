package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.abstracts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * yqsong@illinois.edu
 * <p>
 * Holds the the labels' data. Provides methods to traverse through the label
 * trees. This class contains the following properties:
 * <ul>
 * <li><code>treeIndex</code> the children data <node key, child key>.
 * <li><code>parentIndex</code> the parent key data <node key, parent key>.
 * <li><code>treeLabelNameHashMap</code> all topics as <key,label>. Like a
 * flatten tree.
 */

public abstract class AbstractTreeLabelData implements Serializable {

	private static final long serialVersionUID = 6643907701289447311L;

	protected HashMap<String, HashSet<String>> treeIndex; // children index
	protected HashMap<String, String> parentIndex; // parent index
	protected HashMap<String, String> treeLabelNameHashMap;
	protected HashMap<String, String> topicDescriptionHashMap;

	/**
	 * The implementation should reads the topic hierarchy for the specific data
	 * corpus to be classified.
	 */
	public abstract void readTreeHierarchy(String fileTopicHierarchyPath);

	public abstract void readTopicDescription(String topicDescriptionPath);

	public AbstractTreeLabelData() {
		treeIndex = new HashMap<String, HashSet<String>>();
		parentIndex = new HashMap<String, String>();
		treeLabelNameHashMap = new HashMap<String, String>();
		topicDescriptionHashMap = new HashMap<String, String>();
	}

	public HashMap<String, HashSet<String>> getTreeChildrenIndex() {
		return this.treeIndex;
	}

	public HashMap<String, String> getTreeParentIndex() {
		return this.parentIndex;
	}

	public HashMap<String, String> getTreeLabelNameHashMap() {
		return this.treeLabelNameHashMap;
	}

	public HashMap<String, String> getTreeTopicDescription() {
		return this.topicDescriptionHashMap;
	}

}
