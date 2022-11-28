package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.SB11Indexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class HierarchicalClassifierSB11Experiment {

	public static void main(String[] args) {

		int numConcepts = 500;
		int topK = 5;
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String rawData = GenericCSConfig.rawData;
		String method = "simple";
		String textIndex = "data/sb11/textindex/"; // sub directory will be created with table name
		String conceptTreeFile = "data/sb11/output/tree.sb11." + method + ".concepts.newrefine." + numConcepts;
		String conceptFile = "data/sb11/output/sb11." + method + ".concepts." + numConcepts;
		String outputClassificationFile = "data/sb11/output/result.concept." + method + ".sb11.classification." + numConcepts;
		String outputLabelComparisonFile = "data/sb11/output/result.concept. " + method + ".sb11.labelComparison." + numConcepts;

		String sb11Table = "Byggdelar"; // Byggdelar, Landskapsinformation ot Alternativtabell

		
		Run(numConcepts, sb11Taxonomy, sb11Table, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, topK, method, false);

	}

	public static void Run(int numConcepts, String sb11Taxonomy, String sb11Table, String rawData, String textIndex,
			String conceptTreeFile, String conceptFile, String outputClassificationFile,
			String outputLabelComparisonFile, int topK, String method, boolean cleanRun) {
		/*
		 * Make sure that the following paths in conf/configurations.properties are set
		 * correctly: - cogcomp.esa.simple.wikiIndex
		 */

		DatalessResourcesConfig.initialization();
		if(lang == Language.SV) {
			DatalessResourcesConfig.setSimpleESADocumentIndex(SB11ExperimentConfig.wikiIndexSV);
		}
		
		textIndex += sb11Table + "." + lang.toString() + "/";
		conceptTreeFile += "." + sb11Table + "." + lang.toString();
		conceptFile += "." + sb11Table + "." + lang.toString();
		outputClassificationFile += "." + sb11Table + "." + lang.toString();
		outputLabelComparisonFile += "." + sb11Table + "." + lang.toString();

		boolean indexRawData = false;
		try {
			indexRawData = Files.list(new File(textIndex).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (indexRawData || cleanRun) {
			try {
				SB11Indexer t = new SB11Indexer(rawData, textIndex, sb11Table, lang);
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean createConceptTree = !Files.exists(new File(conceptTreeFile).toPath());
		if (createConceptTree || cleanRun) {
			DumpConceptTreeSB11.testSB11DataESA(numConcepts, conceptTreeFile, sb11Taxonomy, sb11Table, method, lang.toString());
			;
		}

		boolean createConceptFile = !Files.exists(new File(conceptFile).toPath());
		if (createConceptFile || cleanRun) {
			if (method == "simple") {
				CorpusESAConceptualizationSB11.conceptualizeCorpus(numConcepts, textIndex, conceptFile, lang.toString());	
			}else if (method == "complex") {
				CorpusESAConceptualizationSB11.conceptualizeCorpusComplex(numConcepts, textIndex, lang.toString());	
			
			}
		}

		ConceptClassificationESAML.testSB11SimpleConcept(topK, sb11Table, lang.toString(), textIndex, conceptTreeFile, conceptFile,
				outputClassificationFile, outputLabelComparisonFile);
	}
	

}
