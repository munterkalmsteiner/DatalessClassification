package se.bth.serl.word.classifier.classificationsystem;

import java.util.HashSet;
import java.util.Set;


public class CSTreeNode extends CSObject {
	private static final long serialVersionUID = 8946391212816911360L;
	
	private int depth;
	
	public CSTreeNode() {
		
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}	

}
