package edu.illinois.cs.cogcomp.classification.hierarchy.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.classification.hierarchy.classificationinterface.InterfaceMultiLabelConceptClassificationTree;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11.SB11Classifier;
import se.bth.serl.flatclassifier.ConfusionMatrix;


public class SB11Evaluation {

	public static HashMap<String, EvalResults> testTopKWithoutDepth(
			InterfaceMultiLabelConceptClassificationTree tree, HashMap<String, SparseVector> docIdContentMap,
			HashMap<String, HashSet<String>> topicDocMap, HashMap<String, HashSet<String>> docTopicMap,
			String outputDataFilePath, String outputLabelFilePath, int topK) {
		// classification
		HashMap<String, EvalResults> resultsMap = new HashMap<String, EvalResults>();
		try {

			HashMap<String, Map<String, Double>> docClassifiedTopics = new LinkedHashMap<String, Map<String, Double>>();

			for (String docID : docIdContentMap.keySet()) {
				// get vector
				SparseVector document = docIdContentMap.get(docID);
				// process document with labels

				HashMap<Integer, List<LabelKeyValuePair>> labelResultsInDepth = tree.labelDocumentML(document);

				List<LabelKeyValuePair> classifiedLabelScoreList = SB11Classifier
						.topScorefromAllClasses(labelResultsInDepth, topK);

				if (!docClassifiedTopics.containsKey(docID)) {
					docClassifiedTopics.put(docID, new HashMap<String, Double>());
				}

				if (classifiedLabelScoreList.size() != 0) {
					for (LabelKeyValuePair kvp : classifiedLabelScoreList) {
						if (!docClassifiedTopics.get(docID).containsKey(kvp.getLabel().toUpperCase())) {
							docClassifiedTopics.get(docID).put(kvp.getLabel().toLowerCase(), kvp.getScore());
						}
					}
				}
			}
			
			HashMap<String, ConfusionMatrix> cms = se.bth.serl.flatclassifier.Evaluation.calculateConfusionMatrix(docClassifiedTopics, docTopicMap);
			se.bth.serl.flatclassifier.Evaluation.calculateMetrics(cms);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsMap;
	}
}
