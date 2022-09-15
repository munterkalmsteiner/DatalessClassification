package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeTopDownML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.CSTopicHierarchy;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.StopWords;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import edu.illinois.cs.cogcomp.descartes.retrieval.simple.Searcher;

public class Demonstrator {

	public static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();
	private static final Logger log = LoggerFactory.getLogger(Demonstrator.class);
	
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

		boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("jdwp") >= 0;
		DatalessResourcesConfig.initialization();
		String conceptTreeFile =  "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine." + numConcepts + "."
				+ sb11Table;
		String stopWordsFile = "data/rcvTest/english.stop";
		String treeConceptFile = "";
		String method = "simple";
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String data = "sb11," + sb11Table;
		String textToClassify = documentTitle + " " + sectionsTitle + " " + requirement;  
		
		log.info("Clasifying started");
		log.info(textToClassify);
		
		treeConceptFile = conceptTreeFile;
		StopWords.rcvStopWords = StopWords.readStopWords(stopWordsFile);
		ConceptTreeTopDownML tree = new ConceptTreeTopDownML(data, method, conceptWeights, true);

	    tree.setDebug(isDebug);
		log.info("process tree...");
		tree.readLabelTreeFromDump(treeConceptFile, ClassifierConstant.isBreakConcepts);
		ConceptTreeNode rootNode = tree.initializeTreeWithConceptVector("root", 0, ClassifierConstant.isBreakConcepts);
		tree.setRootNode(rootNode);
		log.info("process tree finished");

		// Waleed
		CSTopicHierarchy sb11 = new CSTopicHierarchy("SB11","EN", sb11Taxonomy);
		SparseVector reqVector = tree.convertDocToVector(textToClassify, ClassifierConstant.isBreakConcepts);
		HashMap<Integer, List<LabelKeyValuePair>> labelsInDepth = tree.labelDocumentML(reqVector);

		LabelKeyValuePair sibilingDiffResult = SB11Classifier.siblingsDiffClassification(labelsInDepth);
		sibilingDiffResult.setLabel(
				sibilingDiffResult.getLabel() + " - " + sb11.getLabelName(sibilingDiffResult.getLabel()));
		
		LabelKeyValuePair topTwoDiffResult = SB11Classifier.topTwoDiffClassification(labelsInDepth);
		topTwoDiffResult.setLabel(
				topTwoDiffResult.getLabel() + " - " + sb11.getLabelName(topTwoDiffResult.getLabel()));
		
		
		List<LabelKeyValuePair> topScoreFromAllResult =  SB11Classifier.topScorefromAllClasses(labelsInDepth, numOfClasses);
		topScoreFromAllResult =  topScoreFromAllResult
				.stream()
				.map(c -> new LabelKeyValuePair(c.getLabel().toUpperCase(), c.getScore()))
				.collect(Collectors.toList());
		
		for (int i = 0; i < topScoreFromAllResult.size(); i++) {
			topScoreFromAllResult.get(i).setLabel(
					topScoreFromAllResult.get(i).getLabel() + " - " + sb11.getLabelName(topScoreFromAllResult.get(i).getLabel()));
		}
		
		HashMap<String, List<LabelKeyValuePair>> results = new LinkedHashMap<String, List<LabelKeyValuePair>>();
		//results.put("Siblings Diff", new ArrayList<LabelKeyValuePair>() { { add(sibilingDiffResult); }}) ;
		//results.put("Top Two Diff", new ArrayList<LabelKeyValuePair>() { { add(topTwoDiffResult); }});
		results.put("Top Scores", topScoreFromAllResult);
		
		log.info("Classification completed");
		
		return results;
	}
	
	

}
