package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.newsgroups.NewsgroupsCorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.RequirementsCorpusConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.ConceptData;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ClassifierConstant;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import edu.illinois.cs.cogcomp.classification.representation.esa.AbstractESA;
import edu.illinois.cs.cogcomp.classification.representation.esa.complex.DiskBasedComplexESA;
import edu.illinois.cs.cogcomp.classification.representation.esa.simple.SimpleESALocal;

/**
 * Conceptualization of the coclass data corpus.
 * <p>
 * @author waleed
 *
 */

public class CorpusESAConceptualizationCoClass {

	public static void main(String[] args) {

//		ClassifierConstant.cutOff = Double.parseDouble(args[0]);//0.5 0.1;//;
//		
//		int type = 1;
//		ClassifierConstant.complexVectorType = ComplexESALocal.searchTypes[type];
//		conceptualizeCorpusComplex (500) ;
		
		DatalessResourcesConfig.initialization();
		String fileName = "trvinfra-00008-5-en";
		String rawCorpusFile = "data/coclass/raw/" + fileName + ".csv";
		String textIndex = "data/coclass/textindex/" +  fileName;
		String conceptsOutputFile = "data/coclass/output/" + fileName + ".simple.concepts.500";
		
		try {
			CoClassIndexer t = new CoClassIndexer(rawCorpusFile,textIndex,"Tillgångsystem");
			t.index();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		CorpusESAConceptualizationCoClass.conceptualizeCorpus(500, textIndex, conceptsOutputFile);

	}

	public static void conceptualizeCorpus(int conceptNum, String inputData, String outputData) {
		int seed = 0;
		Random random = new Random(seed);
		double trainingRate = 0.6;

		CorpusESAConceptualizationCoClass corpusContentProc = new CorpusESAConceptualizationCoClass();
		RequirementsCorpusConceptData coClassData = new RequirementsCorpusConceptData();
		coClassData.readCorpusContentOnly(inputData, random, trainingRate);
		corpusContentProc.writeCorpusSimpleConceptData(coClassData.getCorpusContentMap(), conceptNum, outputData);
		System.out.println("Completed conceptualizing data corpus");
	}

	public static void conceptualizeCorpusComplex(int conceptNum, String inputData, String outputData) {
		int seed = 0;
		Random random = new Random(seed);
		double trainingRate = 0.5;

		//String outputData = "data/sb11/output/sb11.complexGraph.cutoff" + ClassifierConstant.cutOff
		//		+ ".esa.concepts." + ClassifierConstant.complexVectorType + conceptNum;

		CorpusESAConceptualizationCoClass corpusContentProc = new CorpusESAConceptualizationCoClass();
		RequirementsCorpusConceptData coClassData = new RequirementsCorpusConceptData();
		coClassData.readCorpusContentOnly(inputData, random, trainingRate);
		corpusContentProc.writeCorpusComplexConceptData(coClassData.getCorpusContentMap(), conceptNum, outputData);

	}

	/***
	 * Retrieve and stores the top numConcepts concepts for the documents in corpusContentMap
	 * using <code>SimpleESALocal</code>. The matching is done based on the document content.
	 * 
	 * 
	 * @param corpusContentMap 
	 * @param numConcepts number of concepts to retrieve for each document.
	 * @param file the file where the data corpus with concepts are written.
	 * 
	 * @see SB11CorpusConceptData 
	 * @see SB11Indexer
	 */
	public void writeCorpusSimpleConceptData(HashMap<String, String> corpusContentMap, int numConcepts, String file) {
		String content = "";
		try {
			int count = 0;
			FileWriter writer = new FileWriter(file);
			SimpleESALocal esa = new SimpleESALocal();
			for (String docID : corpusContentMap.keySet()) {
				count++;
				System.out.println("written " + count + " documents with concepts");
				content = corpusContentMap.get(docID);
				List<ConceptData> concepts = esa.getConcepts(numConcepts, content);
				List<String> conceptsList = new ArrayList<String>();
				String docContent = corpusContentMap.get(docID);
				writer.write(docID + "\t" + docContent + "\t");
				for (int i = concepts.size() - 1; i >= 0; i--) {
					writer.write(concepts.get(i).concept + "," + concepts.get(i).score + ";");
				}
				writer.write("\n\r");
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(content);
			e.printStackTrace();
		}

	}

	public void writeCorpusComplexConceptData(HashMap<String, String> corpusContentMap, int numConcepts, String file) {
		try {
			int count = 0;
			FileWriter writer = new FileWriter(file);
			AbstractESA esa = null;
			esa = new DiskBasedComplexESA();
			for (String docID : corpusContentMap.keySet()) {
				count++;
				System.out.println("written " + count + " documents with concepts");
				List<ConceptData> concepts = esa.retrieveConcepts(corpusContentMap.get(docID), numConcepts,
						ClassifierConstant.complexVectorType);
				String docContent = corpusContentMap.get(docID).replace("\t", " ");
				writer.write(docID + "\t" + docContent + "\t");
				for (int i = concepts.size() - 1; i >= 0; i--) {
					writer.write(concepts.get(i).concept + "," + concepts.get(i).score + ";");
				}
				writer.write("\n\r");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
