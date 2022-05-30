package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ConceptTreeNode;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataless.representation.ml.ConceptTreeBottomUpML;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.newsgroups.NewsgroupsCorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.newsgroups.NewsgroupsTopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11CorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11TopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.StopWords;
import edu.illinois.cs.cogcomp.classification.hierarchy.evaluation.Evaluation;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;

/**
 * 
 * @author waleed
 *
 */

public class ConceptClassificationESAML {

	public static HashMap<String, Double> conceptWeights = new HashMap<String, Double>();

	
	public static void main(String[] args) {
		testSB11SimpleConcept(1);
	}
	
	public static void testSB11SimpleConcept(int topK) {
		
		int seed = 0;
		Random random = new Random(seed);
		double trainingRate = 0.06;

		String stopWordsFile = "";
		String docIDContentConceptFile = "";
		String docIDTopicMapFile = "";
		String treeConceptFile = "";
		String outputClassificationFile = "";
		String outputLabelComparisonFile = "";
		String method = "simple";
		String data = "sb11";
		String sb11FilePath = "data/sb11/raw/SB11_SV_EN_20220520.csv";
		stopWordsFile = "data/rcvTest/english.stop";
		docIDContentConceptFile = "data/sb11/output/sb11.simple.esa.concepts.500";
		docIDTopicMapFile = "data/sb11/textindex";
		treeConceptFile = "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine.500";
		outputClassificationFile = "data/sb11/output/result.concept.sb11.classification";
		outputLabelComparisonFile = "data/sb11/output/result.concept.sb11.labelComparison";
		

		StopWords.rcvStopWords = StopWords.readStopWords (stopWordsFile);
		
		SB11CorpusConceptData corpusContentProc = new SB11CorpusConceptData();
		corpusContentProc.readCorpusContentAndConcepts(docIDContentConceptFile, ClassifierConstant.isBreakConcepts, random, trainingRate, conceptWeights);

		// read topic doc maps
		SB11TopicDocMaps sb11TDM = new SB11TopicDocMaps(sb11FilePath);
		sb11TDM.readFilteredTopicDocMap (docIDTopicMapFile, corpusContentProc.getCorpusConceptVectorMap().keySet());
		
		HashMap<String, HashSet<String>> topicDocMap = sb11TDM.getTopicDocMap();
		HashMap<String, HashSet<String>> docTopicMap = sb11TDM.getDocTopicMap();
		
		// read tree
		ConceptTreeBottomUpML tree = new ConceptTreeBottomUpML(data, method, conceptWeights, false);
//		ConceptTreeTopDownML tree = new ConceptTreeTopDownML(data, method, conceptWeights, false);

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
