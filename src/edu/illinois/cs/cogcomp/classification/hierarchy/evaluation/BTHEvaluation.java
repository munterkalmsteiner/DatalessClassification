package edu.illinois.cs.cogcomp.classification.hierarchy.evaluation;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import edu.illinois.cs.cogcomp.classification.hierarchy.classificationinterface.InterfaceMultiLabelConceptClassificationTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.classificationinterface.InterfaceMultiLabelContentClassificationTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.RequirementsClassifier;
import se.bth.serl.flatclassifier.Evaluator;


public class BTHEvaluation {

	public static HashMap<String, EvalResults> testMultiLabelConceptTreeResultsWithoutDepth(
			InterfaceMultiLabelConceptClassificationTree tree, 
			HashMap<String, SparseVector> docIdContentMap,
			HashMap<String, HashSet<String>> topicDocMap,
			HashMap<String, HashSet<String>> docTopicMap,
			String outputDataFilePath,
			String outputLabelFilePath,
			int topK) {
		// classification
		HashMap<String, EvalResults> resultsMap = new HashMap<String, EvalResults>();
		try {
			int count = 0;
			FileWriter writer = new FileWriter(outputDataFilePath);
			
			HashMap<String, HashMap<String, Double>> docClassifiedTopics = new LinkedHashMap<String, HashMap<String, Double>>();

			for (String docID : docIdContentMap.keySet()) {
				writer.write(docID + "\t");
				// get vector
				SparseVector document = docIdContentMap.get(docID);
				// process document with labels

				HashMap<Integer, List<LabelKeyValuePair>> labelResultsInDepth = tree.labelDocumentML(document);

				// write true labels to output
				HashSet<String> trueLabelSet = docTopicMap.get(docID);
				if (trueLabelSet == null) {
					System.out.println(docID + " has no true labels (skipping)");
					trueLabelSet = new HashSet<String>();
				}
				HashMap<Integer, HashSet<String>> trueDepthsLabels = new HashMap<Integer, HashSet<String>>();
				for (String label : trueLabelSet) {
					int depth = tree.searchLabelDepth(label);
					if (trueDepthsLabels.containsKey(depth) == false) {
						trueDepthsLabels.put(depth, new HashSet<String>());
					}
					trueDepthsLabels.get(depth).add(label);
					writer.write(depth + "," + label + ";");
				}
				writer.write("\t");
				// end write true labels to output
				
				List<LabelKeyValuePair> classifiedLabelScoreList = RequirementsClassifier
						.topScorefromAllClasses(labelResultsInDepth, topK);
				
				// write classified labels to output
				for (int i = 0; i < Math.min(topK, classifiedLabelScoreList.size()); ++i) {
					int depth = tree.searchLabelDepth(classifiedLabelScoreList.get(i).getLabel());
					writer.write(depth + "," + classifiedLabelScoreList.get(i).getLabel() + ";");
				}
				// end write classified labels to output
				
				if (!docClassifiedTopics.containsKey(docID)) {
					docClassifiedTopics.put(docID, new HashMap<String, Double>());
				}

				if (classifiedLabelScoreList.size() != 0) {
					for (LabelKeyValuePair kvp : classifiedLabelScoreList) {
						if (!docClassifiedTopics.get(docID).containsKey(kvp.getLabel().toUpperCase())) {
							docClassifiedTopics.get(docID).put(kvp.getLabel(), kvp.getScore());
						}
					}
				}
				
				writer.write("\n\r");
				count++;
			}
			System.out.println("Classified " + count + " documents ...");
			
			Evaluator eval = new Evaluator(docClassifiedTopics, docTopicMap);
			eval.calculateMetrics();
			
			resultsMap.put("all", new EvalResults());
			resultsMap.get("all").precision = eval.getuPrecision();
			resultsMap.get("all").recall = eval.getuRecall();
			resultsMap.get("all").mf1 = eval.getuF1();
			resultsMap.get("all").Mf1 = eval.getMF1();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsMap;
	}
	
	public static HashMap<String, EvalResults> testMultiLabelContentTreeResultsWithoutDepth (
			InterfaceMultiLabelContentClassificationTree tree,
			HashMap<String, String> testDocIdContentMap, 
			HashMap<String, String> allDocIdConceptStringMap, 
			HashMap<String, HashSet<String>> topicDocMap,
			HashMap<String, HashSet<String>> docTopicMap,
			String outputDataFilePath, String outputLabelFilePath,
			int topK,
			boolean isUseConcept) {
		HashMap<String, EvalResults> resultsMap = new HashMap<String, EvalResults>();
		
		try {
			int count = 0;
			FileWriter writer = new FileWriter(outputDataFilePath);
			
			HashMap<String, HashMap<String, Double>> docClassifiedTopics = new LinkedHashMap<String, HashMap<String, Double>>();
			
			for (String docID : testDocIdContentMap.keySet()) {
				writer.write(docID + "\t");
				// get vector
				// process document with labels
				
				HashMap<Integer, List<LabelKeyValuePair>> labelResultsInDepth = null;
				
				if (isUseConcept == false) {
					String document = testDocIdContentMap.get(docID);
					labelResultsInDepth = tree.labelDocumentContentML(document);
				} else {
					String document = allDocIdConceptStringMap.get(docID);
					labelResultsInDepth = tree.labelDocumentConceptML(document);
				}
								
				// write true labels to output
				HashSet<String> trueLabelSet = docTopicMap.get(docID);
				if (trueLabelSet == null) {
					System.out.println(docID + " has no true labels (skipping)");
					trueLabelSet = new HashSet<String>();
				}
				HashMap<Integer, HashSet<String>> trueDepthsLabels = new HashMap<Integer, HashSet<String>>();
				for (String label : trueLabelSet) {					
					int depth = tree.getLabelDepth(label);
					if (trueDepthsLabels.containsKey(depth) == false) {
						trueDepthsLabels.put(depth, new HashSet<String>());
					}
					trueDepthsLabels.get(depth).add(label);
					writer.write(depth + "," + label + ";");
				}
				writer.write("\t");
				// end write true labels to output
				
				//classified labels
				List<LabelKeyValuePair> classifiedLabelScoreList = RequirementsClassifier
						.topScorefromAllClasses(labelResultsInDepth, topK);
				
				// write classified labels to output
				for (int i = 0; i < Math.min(topK, classifiedLabelScoreList.size()); ++i) {
					int depth = tree.getLabelDepth(classifiedLabelScoreList.get(i).getLabel());
					writer.write(depth + "," + classifiedLabelScoreList.get(i).getLabel() + ";");
				}
				// end write classified labels to output
				
				if (!docClassifiedTopics.containsKey(docID)) {
					docClassifiedTopics.put(docID, new HashMap<String, Double>());
				}

				if (classifiedLabelScoreList.size() != 0) {
					for (LabelKeyValuePair kvp : classifiedLabelScoreList) {
						if (!docClassifiedTopics.get(docID).containsKey(kvp.getLabel().toUpperCase())) {
							docClassifiedTopics.get(docID).put(kvp.getLabel(), kvp.getScore());
						}
					}
				}
				writer.write("\n\r");
				count++;
			}
			writer.close();
			System.out.println("Classified " + count + " documents ...");
			
			// evaluate
			Evaluator eval = new Evaluator(docClassifiedTopics, docTopicMap);
			eval.calculateMetrics();
			
			resultsMap.put("all", new EvalResults());
			resultsMap.get("all").precision = eval.getuPrecision();
			resultsMap.get("all").recall = eval.getuRecall();
			resultsMap.get("all").mf1 = eval.getuF1();
			resultsMap.get("all").Mf1 = eval.getMF1();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsMap;

	}
}
