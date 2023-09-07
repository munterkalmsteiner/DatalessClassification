package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.SB11Indexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import se.bth.serl.word.classifier.ExperimentConfig;

public class SentenceClassifierSB11Experiment {

	public static void main(String[] args) {

		int numOfConcepts = 500;
		
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String rawData = GenericCSConfig.rawData;
		String textIndex = "data/sb11/textindex/"; // sub directory will be created with table name
		String conceptTreeFile = "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine." + numOfConcepts;
		String conceptFile = "data/sb11/output/sb11.simple.esa.concepts." + numOfConcepts;
		String outputClassificationFile = "data/sb11/output/result.concept.sb11.classification." + numOfConcepts;
		String outputLabelComparisonFile = "data/sb11/output/result.concept.sb11.labelComparison." + numOfConcepts;

		String sb11Table = "Alternativtabell"; // Byggdelar, Landskapsinformation or Alternativtabell

		HashMap<String, ExperimentConfig> experiments = new HashMap<String,ExperimentConfig>();
		
		experiments.put("1", new ExperimentConfig(
				"1",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				1, "Byggdelar", false));
		
		experiments.put("2", new ExperimentConfig(
				"2",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				1, "Landskapsinformation", false));
		
		experiments.put("3", new ExperimentConfig(
				"3",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				1, "Alternativtabell", false));
		
		experiments.put("4", new ExperimentConfig(
				"4",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				2, "Byggdelar", false));
		
		experiments.put("5", new ExperimentConfig(
				"5",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				2, "Landskapsinformation", false));
		
		experiments.put("6", new ExperimentConfig(
				"6",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				2, "Alternativtabell", false));
		
		experiments.put("7", new ExperimentConfig(
				"7",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				5, "Byggdelar", false));
		
		experiments.put("8", new ExperimentConfig(
				"8",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				5, "Landskapsinformation", false));
		
		experiments.put("9", new ExperimentConfig(
				"9",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				5, "Alternativtabell", false));
		
		experiments.put("10", new ExperimentConfig(
				"10",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				10, "Byggdelar", false));
		
		experiments.put("11", new ExperimentConfig(
				"11",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				10, "Landskapsinformation", false));
		
		experiments.put("12", new ExperimentConfig(
				"12",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				10, "Alternativtabell", false));
		
		experiments.put("13", new ExperimentConfig(
				"13",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				15, "Byggdelar", false));
		
		experiments.put("14", new ExperimentConfig(
				"14",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				15, "Landskapsinformation", false));
		
		experiments.put("15", new ExperimentConfig(
				"15",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				15, "Alternativtabell", false));
		
		experiments.put("16", new ExperimentConfig(
				"16",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Byggdelar", false));
		
		experiments.put("17", new ExperimentConfig(
				"17",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Landskapsinformation", false));
		
		experiments.put("18", new ExperimentConfig(
				"18",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				20, "Alternativtabell", false));
		
		experiments.put("19", new ExperimentConfig(
				"19",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				25, "Byggdelar", false));
		
		experiments.put("20", new ExperimentConfig(
				"20",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				25, "Landskapsinformation", false));
		
		experiments.put("21", new ExperimentConfig(
				"21",sb11Taxonomy, rawData, textIndex, conceptTreeFile, conceptFile, 
				outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 
				25, "Alternativtabell", false));

		for(String ex:experiments.keySet()) {
			SentenceClassifierSB11Experiment.RunESA(experiments.get(ex), false);	
		}
		
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
		conf.setOutputClassificationFile(conf.getOutputClassificationFile() + "." + conf.getCsTable() + "." + conf.getDescription());
		conf.setOutputLabelComparisonFile(conf.getOutputLabelComparisonFile() + "." + conf.getCsTable() +  "." + conf.getDescription());

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
		
		System.out.print("Experiment number "  + conf.getDescription() + " is completed.");
	}

}
