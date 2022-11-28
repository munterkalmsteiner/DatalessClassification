package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CoClassIndexer;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationCoClass;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeCoClass;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class HierarchicalClassifierCoClassExperiment {
	public static void main(String[] args) {
	      
		int numConcepts = 500;
		int topK = 5;
		String coClassTaxonomy = CoClassExperimentConfig.coClassTaxonomy;
		String rawData = CoClassExperimentConfig.rawData;
		String textIndex = "data/coclass/textindex/"; // sub directory will be created with table name
		String conceptTreeFile = "data/coclass/output/tree.coclass.simple.esa.concepts.newrefine." + numConcepts;
		String conceptFile = "data/coclass/output/coclass.simple.esa.concepts." + numConcepts;
		String outputClassificationFile = "data/coclass/output/result.concept.coclass.classification." + numConcepts;
		String outputLabelComparisonFile = "data/coclass/output/result.concept.coclass.labelComparison." + numConcepts;

		String coClassTable = "Tillgångssystem"; // Byggdelar, Landskapsinformation ot Alternativtabell

		HierarchicalClassifierCoClassExperiment.RunESA(numConcepts, coClassTaxonomy, coClassTable, rawData, textIndex,
				conceptTreeFile, conceptFile, outputClassificationFile, outputLabelComparisonFile, topK, false);

	}

	public static void RunESA(int numConcepts, String coClassTaxonomy, String coClassTable, String rawData, String textIndex,
			String conceptTreeFile, String conceptFile, String outputClassificationFile,
			String outputLabelComparisonFile, int topK, boolean cleanRun) {
		/*
		 * Make sure that the following paths in conf/configurations.properties are set
		 * correctly: - cogcomp.esa.simple.wikiIndex
		 */

		DatalessResourcesConfig.initialization();

		textIndex += coClassTable + "/";
		conceptTreeFile += "." + coClassTable;
		conceptFile += "." + coClassTable;
		outputClassificationFile += "." + coClassTable;
		outputLabelComparisonFile += "." + coClassTable;

		boolean indexRawData = false;
		try {
			indexRawData = Files.list(new File(textIndex).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (indexRawData || cleanRun) {
			try {
				CoClassIndexer t = new CoClassIndexer(rawData, textIndex, coClassTable);
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean createConceptTree = !Files.exists(new File(conceptTreeFile).toPath());
		if (createConceptTree || cleanRun) {
			DumpConceptTreeCoClass.testCoClassDataESA(numConcepts, conceptTreeFile, coClassTaxonomy, coClassTable);
			;
		}

		boolean createConceptFile = !Files.exists(new File(conceptFile).toPath());
		if (createConceptFile || cleanRun) {
			CorpusESAConceptualizationCoClass.conceptualizeCorpus(numConcepts, textIndex, conceptFile);
		}

		ConceptClassificationESAML.testCoClassSimpleConcept(topK, coClassTable, textIndex, conceptTreeFile, conceptFile,
				outputClassificationFile, outputLabelComparisonFile);
	}
}
