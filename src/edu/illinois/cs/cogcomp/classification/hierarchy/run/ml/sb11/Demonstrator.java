package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeTopDownML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11TopicHierarchy;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.StopWords;
import edu.illinois.cs.cogcomp.classification.hierarchy.evaluation.SB11Classifier;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class Demonstrator {

	public static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();

	public static void main(String[] args) {

		HashMap<String, List<LabelKeyValuePair>> results = Demonstrator.classifyReq(
				"Superstructure thickness shall be selected to meet the requirements of Table 19-1 and Table 19- 2 while meeting the dimensions of the gravel wear layer and support layer shown in Figure 19-1.",
				"Road Superstructure / Road superstructure, Dimensioning and design",
				"19 Gravel Superstructure# 19.2 Superstructure thickness gravel road",
				"Byggdelar",
				10);

		
		for (String key : results.keySet()) {
			List<LabelKeyValuePair> annotation = results.get(key);
			annotation.stream().forEach(e -> System.out.println(key + ": " + e.getLabel() + " - " + e.getScore()));
		}
	}

	public static HashMap<String, List<LabelKeyValuePair>> classifyReq(String requirement, 
			String documentTitle,
			String sectionsTitle,
			String sb11Table,
			int numOfClasses) {
		int numConcepts = 500;

		DatalessResourcesConfig.initialization();
		String baseDir = "/home/waleed/dev/DatalessClassification/";
		String conceptTreeFile = baseDir + "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine." + numConcepts + "."
				+ sb11Table;

		String stopWordsFile = baseDir + "data/rcvTest/english.stop";
		String treeConceptFile = "";
		String method = "simple";
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String data = "sb11," + sb11Table;
		String textToClassify = documentTitle + " " + sectionsTitle + " " + requirement;  
				
		treeConceptFile = conceptTreeFile;

		StopWords.rcvStopWords = StopWords.readStopWords(stopWordsFile);

		ConceptTreeTopDownML tree = new ConceptTreeTopDownML(data, method, conceptWeights, true);

		System.out.println("process tree...");
		tree.readLabelTreeFromDump(treeConceptFile, ClassifierConstant.isBreakConcepts);
		ConceptTreeNode rootNode = tree.initializeTreeWithConceptVector("root", 0, ClassifierConstant.isBreakConcepts);
		tree.setRootNode(rootNode);
		System.out.println("process tree finished");

		// Waleed
		SB11TopicHierarchy sb11 = new SB11TopicHierarchy("EN", sb11Taxonomy);
		SparseVector reqVector = tree.convertDocToVector(textToClassify, ClassifierConstant.isBreakConcepts);
		HashMap<Integer, List<LabelKeyValuePair>> labelsInDepth = tree.labelDocumentML(reqVector);

		LabelKeyValuePair sibilingDiffResult = SB11Classifier.siblingsDiffClassification(labelsInDepth);
		sibilingDiffResult.setLabel(
				sibilingDiffResult.getLabel() + " - " + sb11.getLabelName(sibilingDiffResult.getLabel()));
		
		LabelKeyValuePair topTwoDiffResult = SB11Classifier.topTwoDiffClassification(labelsInDepth);
		topTwoDiffResult.setLabel(
				topTwoDiffResult.getLabel() + " - " + sb11.getLabelName(topTwoDiffResult.getLabel()));
		
		
		List<LabelKeyValuePair> topScoreFromAllResult =  SB11Classifier.topScorefromAllClasses(labelsInDepth, numOfClasses);
		for (int i = 0; i < topScoreFromAllResult.size(); i++) {
			topScoreFromAllResult.get(i).setLabel(
					topScoreFromAllResult.get(i).getLabel() + " - " + sb11.getLabelName(topScoreFromAllResult.get(i).getLabel()));
		}
		
		HashMap<String, List<LabelKeyValuePair>> results = new LinkedHashMap<String, List<LabelKeyValuePair>>();
		//results.put("Siblings Diff", new ArrayList<LabelKeyValuePair>() { { add(sibilingDiffResult); }}) ;
		//results.put("Top Two Diff", new ArrayList<LabelKeyValuePair>() { { add(topTwoDiffResult); }});
		results.put("Top Scores", topScoreFromAllResult);
		
		return results;
	}
	
	

}
