package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.AbstractConceptTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeTopDownML;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class DumpConceptTreeSB11 {

	public static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();;
	
	public static void testSB11DataESA (int conceptNum, String fileOutputPath, String fileTopicHierarchyPath, String sb11Table, String lang) {
		String classSpecs = "sb11," + sb11Table + "," + lang;  
		AbstractConceptTree tree = new ConceptTreeTopDownML(classSpecs, "simple", conceptWeights, true);
		System.out.println("process tree...");
		tree.treeLabelData.readTreeHierarchy(fileTopicHierarchyPath);
		ConceptTreeNode rootNode = tree.initializeTree("root", 0);
		tree.setRootNode(rootNode);
		tree.aggregateChildrenDescription(rootNode);
		tree.setConceptNum(conceptNum);
		tree.conceptualizeTreeLabels(rootNode, ClassifierConstant.isBreakConcepts);
		tree.dumpTree(fileOutputPath);
		System.out.println("process tree finished");
	}
	
}
