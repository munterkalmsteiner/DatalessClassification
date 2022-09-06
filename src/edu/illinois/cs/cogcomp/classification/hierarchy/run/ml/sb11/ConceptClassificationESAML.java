package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeBottomUpML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeTopDownML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11CorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11TopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.ConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.SparseVector;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.StopWords;
import edu.illinois.cs.cogcomp.classification.hierarchy.evaluation.Evaluation;
import edu.illinois.cs.cogcomp.classification.hierarchy.evaluation.SB11Classifier;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;
import edu.illinois.cs.cogcomp.classification.representation.esa.simple.SimpleESALocal;

/**
 * 
 * @author waleed
 *
 */

public class ConceptClassificationESAML {

	public static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();

	
	public static void main(String[] args) {
		testSB11SimpleConcept(1,
				"Byggdelar",
				"EN",
				"",
				"",
				"",
				"",
				"");
	}

	
	public static void testSB11SimpleConcept(int topK, 
			String sb11Table,
			String lang,
			String textIndex,
			String conceptTreeFile,
			String conceptFile,
			String outputClassificationFile,
			String outputLabelComparisonFile) {
		
		int seed = 0;
		Random random = new Random(seed);
		double trainingRate = 0.06;

		String stopWordsFile = "data/rcvTest/english.stop";
		String docIDContentConceptFile = "";
		String docIDTopicMapFile = "";
		String treeConceptFile = "";
		String method = "simple";
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String data = "sb11," + sb11Table + "," + lang;
		
		docIDContentConceptFile = conceptFile;
		docIDTopicMapFile = textIndex;
		treeConceptFile = conceptTreeFile;
		

		StopWords.rcvStopWords = StopWords.readStopWords (stopWordsFile);
		
		SB11CorpusConceptData corpusContentProc = new SB11CorpusConceptData();
		corpusContentProc.readCorpusContentAndConcepts(docIDContentConceptFile, ClassifierConstant.isBreakConcepts, random, trainingRate, conceptWeights);

		// read topic doc maps
		SB11TopicDocMaps sb11TDM = new SB11TopicDocMaps(sb11Taxonomy, sb11Table, lang);
		sb11TDM.readFilteredTopicDocMap (docIDTopicMapFile, corpusContentProc.getCorpusConceptVectorMap().keySet());
		
		HashMap<String, HashSet<String>> topicDocMap = sb11TDM.getTopicDocMap();
		HashMap<String, HashSet<String>> docTopicMap = sb11TDM.getDocTopicMap();
		
		// read tree
		//ConceptTreeBottomUpML tree = new ConceptTreeBottomUpML(data, method, conceptWeights, false);
		ConceptTreeTopDownML tree = new ConceptTreeTopDownML(data, method, conceptWeights, false);

		System.out.println("process tree...");
		tree.readLabelTreeFromDump(treeConceptFile, ClassifierConstant.isBreakConcepts);
		ConceptTreeNode rootNode = tree.initializeTreeWithConceptVector("root", 0, ClassifierConstant.isBreakConcepts);
		tree.setRootNode(rootNode);
		System.out.println("process tree finished");
		
		Evaluation.testMultiLabelConceptTreeResults (tree,
				corpusContentProc.getCorpusConceptVectorMap(), 
				topicDocMap, docTopicMap,
				outputClassificationFile,  outputLabelComparisonFile, 
				topK);
		


	}

	
}
