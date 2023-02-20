package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.SB11Indexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import se.bth.serl.flatclassifier.ExperimentConfig;

public class HierarchicalClassifierSB11Experiment {

	public static void main(String[] args) {

		int numOfConcepts = 500;
		
		int topK = 15;
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String rawData = GenericCSConfig.rawData;
		String textIndex = "data/sb11/textindex/"; // sub directory will be created with table name
		String conceptTreeFile = "data/sb11/output/tree.sb11.simple.concepts.newrefine." + numOfConcepts;
		String conceptFile = "data/sb11/output/sb11.simple.concepts." + numOfConcepts;
		String outputClassificationFile = "data/sb11/output/result.concept.sb11.classification." + numOfConcepts;
		String outputLabelComparisonFile = "data/sb11/output/result.concept.sb11.labelComparison." + numOfConcepts;

		String sb11Table = "Alternativtabell"; // Byggdelar, Landskapsinformation or Alternativtabell

		HashMap<String, ExperimentConfig> experiments = new HashMap<String,ExperimentConfig>();
		
		experiments.put("26", new ExperimentConfig(
				"26",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Byggdelar", false));
		
		experiments.put("27", new ExperimentConfig(
				"27",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Landskapsinformation", false));
		
		experiments.put("28", new ExperimentConfig(
				"28",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Alternativtabell", false));
		
		HierarchicalClassifierSB11Experiment.RunESA(experiments.get("28"), false);
		
	}

	public static void RunESA(ExperimentConfig conf, boolean cleanRun) {
		/*
		 * Make sure that the following paths in conf/configurations.properties are set
		 * correctly: - cogcomp.esa.simple.wikiIndex
		 */

		DatalessResourcesConfig.initialization();

		conf.setTextIndex(conf.getTextIndex() + conf.getCsTable() + "/");
		conf.setConceptTreeFile(conf.getConceptTreeFile() + "." + conf.getCsTable());
		conf.setConceptFile(conf.getConceptFile() + "." + conf.getCsTable());
		conf.setOutputClassificationFile(conf.getOutputClassificationFile() + "." + conf.getCsTable());
		conf.setOutputLabelComparisonFile(conf.getOutputLabelComparisonFile() + "." + conf.getCsTable());

		boolean indexRawData = false;
		try {
			indexRawData = Files.list(new File(conf.getTextIndex()).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (indexRawData || cleanRun) {
			try {
				SB11Indexer t = new SB11Indexer(conf.getRawDataFile(), conf.getTextIndex(), conf.getCsTable());
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		boolean createConceptTree = !Files.exists(new File(conf.getConceptTreeFile()).toPath());
		if (createConceptTree || cleanRun) {
			DumpConceptTreeSB11.testSB11DataESA(conf.getNumOfConcepts(), conf.getConceptTreeFile(),
					conf.getTaxonomyFile(), conf.getCsTable(), "simple");
			;
		}

		boolean createConceptFile = !Files.exists(new File(conf.getConceptFile()).toPath());
		if (createConceptFile || cleanRun) {
			CorpusESAConceptualizationSB11.conceptualizeCorpus(conf.getNumOfConcepts(), 
					conf.getTextIndex(), conf.getConceptFile());
		}
		
		ConceptClassificationESAML.testSB11SimpleConcept(conf.getTopK(), conf.getCsTable(),
				conf.getTextIndex(), conf.getConceptTreeFile(), conf.getConceptFile(),
				conf.getOutputClassificationFile(), conf.getOutputLabelComparisonFile(), conf.getIncludeSuperTopic());
	}

}
