package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.SB11Indexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class HierarchicalClassiferSB11SVExperiment {

	public static void main(String[] args) {

		int numConcepts = 500;
		int topK = 5;
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String rawData = GenericCSConfig.rawData;
		String textIndex = "data/sb11/textindex_sv"; // sub directory will be created with table name
		String conceptTreeFile = "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine." + numConcepts;
		String conceptFile = "data/sb11/output/sb11.simple.esa.concepts." + numConcepts;
		String outputClassificationFile = "data/sb11/output/result.concept.sb11.classification." + numConcepts;
		String outputLabelComparisonFile = "data/sb11/output/result.concept.sb11.labelComparison." + numConcepts;

		String sb11Table = "Byggdelar"; // Byggdelar, Landskapsinformation ot Alternativtabell

		Run(numConcepts, sb11Taxonomy, sb11Table, rawData, textIndex, conceptTreeFile, conceptFile,
				outputClassificationFile, outputLabelComparisonFile, topK, false);

	}

	public static void Run(int numConcepts, String sb11Taxonomy, String sb11Table, String rawData, String textIndex,
			String conceptTreeFile, String conceptFile, String outputClassificationFile,
			String outputLabelComparisonFile, int topK,  boolean cleanRun) {
		/*
		 * Make sure that the following paths in conf/configurations.properties are set
		 * correctly: - cogcomp.esa.simple.wikiIndex
		 */

		DatalessResourcesConfig.initialization();

		textIndex += sb11Table + "/";
		conceptTreeFile += "." + sb11Table;
		conceptFile += "." + sb11Table;
		outputClassificationFile += "." + sb11Table;
		outputLabelComparisonFile += "." + sb11Table;

		boolean indexRawData = false;
		try {
			indexRawData = Files.list(new File(textIndex).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (indexRawData || cleanRun) {
			try {
				SB11Indexer t = new SB11Indexer(rawData, textIndex, sb11Table);
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean createConceptTree = !Files.exists(new File(conceptTreeFile).toPath());
		if (createConceptTree || cleanRun) {
			DumpConceptTreeSB11.testSB11DataESA(numConcepts, conceptTreeFile, sb11Taxonomy, sb11Table, "simple");
			;
		}

		boolean createConceptFile = !Files.exists(new File(conceptFile).toPath());
		if (createConceptFile || cleanRun) {
			CorpusESAConceptualizationSB11.conceptualizeCorpus(numConcepts, textIndex, conceptFile);
		}

		ConceptClassificationESAML.testSB11SimpleConcept(topK, sb11Table, textIndex, conceptTreeFile, conceptFile,
				outputClassificationFile, outputLabelComparisonFile);
	}

}
