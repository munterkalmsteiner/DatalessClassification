package edu.illinois.cs.cogcomp.classification.hierarchy.evaluation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import edu.illinois.cs.cogcomp.classification.hierarchy.classificationinterface.InterfaceMultiLabelConceptClassificationTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.RequirementsClassifier;
import se.bth.serl.flatclassifier.Evaluator;


public class SB11Evaluation {

	public static HashMap<String, EvalResults> testTopKWithoutDepth(
			InterfaceMultiLabelConceptClassificationTree tree, HashMap<String, SparseVector> docIdContentMap,
			HashMap<String, HashSet<String>> topicDocMap, HashMap<String, HashSet<String>> docTopicMap,
			String outputDataFilePath, String outputLabelFilePath, int topK) {
		// classification
		HashMap<String, EvalResults> resultsMap = new HashMap<String, EvalResults>();
		try {

			HashMap<String, HashMap<String, Double>> docClassifiedTopics = new LinkedHashMap<String, HashMap<String, Double>>();

			for (String docID : docIdContentMap.keySet()) {
				// get vector
				SparseVector document = docIdContentMap.get(docID);
				// process document with labels

				HashMap<Integer, List<LabelKeyValuePair>> labelResultsInDepth = tree.labelDocumentML(document);

				List<LabelKeyValuePair> classifiedLabelScoreList = RequirementsClassifier
						.topScorefromAllClasses(labelResultsInDepth, topK);

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
			}
			
			Evaluator eval = new Evaluator(docClassifiedTopics, docTopicMap);
			eval.calculateMetrics();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsMap;
	}
}
