package se.bth.serl.word.classifier.classificationsystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.AbstractConceptTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeBottomUpML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeTopDownML;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig.CoClassTable;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig.SB11Table;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class CSHierarchicalReader {

	private HashMap<String, AbstractConceptTree> trees;
	private static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();;
	private Logger log = LoggerFactory.getLogger(CSHierarchicalReader.class);
	private String csFile = "";

	public static void main(String[] args) {
		String csFile = CoClassExperimentConfig.coClassTaxonomy;
		CSHierarchicalReader reader = new CSHierarchicalReader(ClassificationSystem.COCLASS, "bottomup", csFile);
		reader.ReadCS();
		String key = "EA";
		String desc = reader.getNodeDescription(key, CoClassTable.GrundfunktionerochKomponenter.getValue());
		System.out.print(key + ": " + desc);
	}

	public CSHierarchicalReader(ClassificationSystem cs, String hierarchy, String csFile) {
		this.csFile = csFile;
		this.trees = new HashMap<String, AbstractConceptTree>();
		String method = "simple";
		DatalessResourcesConfig.initialization();
		if (hierarchy.equals("bottomup")) {
			if (cs.equals(ClassificationSystem.SB11)) {
				for (SB11Table table : SB11Table.values()) {
					trees.put(table.toString(),
							new ConceptTreeBottomUpML("sb11," + table.toString(), method, conceptWeights, true));
				}
			} else if (cs.equals(ClassificationSystem.COCLASS)) {
				for (CoClassTable table : CoClassTable.values()) {
					trees.put(table.getValue(),
							new ConceptTreeBottomUpML("coClass," + table.getValue(), method, conceptWeights, true));
				}
			}
		}else {
			log.error("wrong hierarchy: " + hierarchy);
		}
	}

	public void ReadCS() {
		log.info("Processing tree...");
		for (String treeName : trees.keySet()) {
			AbstractConceptTree tree = trees.get(treeName);
			tree.treeLabelData.readTreeHierarchy(this.csFile);
			ConceptTreeNode rootNode = tree.initializeTree("root", 0);
			tree.setRootNode(rootNode);
			tree.aggregateChildrenDescription(rootNode);
			System.out.println("process tree finished");
		}
	}

	public String getNodeDescription(String code, String csTable) {
		AbstractConceptTree tree =  trees.get(csTable);
		List<ConceptTreeNode> nodes = tree.getTreeNodeList().stream()
				.filter(e -> e.getLabelString().equals(code.toLowerCase())).collect(Collectors.toList());

		if (nodes.size() > 1) {
			log.error(nodes.size() + " matches found for the code: " + code);
		}

		if (nodes.size() == 1) {
			return nodes.get(0).getLabelDescriptioinString();
		}

		log.error(code + " not found in concepts tree");
		return null;
	}
}
