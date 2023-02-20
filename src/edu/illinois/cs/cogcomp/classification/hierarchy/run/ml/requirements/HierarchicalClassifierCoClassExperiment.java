package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CoClassIndexer;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.CorpusESAConceptualizationCoClass;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DumpConceptTreeCoClass;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;
import edu.illinois.cs.cogcomp.lbjava.learn.Log;
import se.bth.serl.flatclassifier.ExperimentConfig;

public class HierarchicalClassifierCoClassExperiment {
	public static void main(String[] args) {

		int numOfConcepts = 500;
		// Tillgångssystem, Grundfunktioner-och-Komponenter or Konstruktiva-system

		String coClassTaxonomy = CoClassExperimentConfig.coClassTaxonomy;
		String rawData = CoClassExperimentConfig.rawData;
		String textIndex = "data/coclass/textindex/"; // sub directory will be created with table name
		String conceptTreeFile = "data/coclass/output/tree.coclass.simple.esa.concepts.newrefine." + numOfConcepts;
		String conceptFile = "data/coclass/output/coclass.simple.esa.concepts." + numOfConcepts;
		String outputClassificationFile = "data/coclass/output/result.concept.coclass.classification." + numOfConcepts;
		String outputLabelComparisonFile = "data/coclass/output/result.concept.coclass.labelComparison."
				+ numOfConcepts;

		HashMap<String, ExperimentConfig> experiments = new HashMap<String, ExperimentConfig>();

		experiments.put("4",
				new ExperimentConfig("4", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 2, "Tillgångssystem",
						false));

		experiments.put("5",
				new ExperimentConfig("5", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5, "Tillgångssystem",
						false));

		experiments.put("6",
				new ExperimentConfig("6", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("7",
				new ExperimentConfig("7", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5, "Konstruktiva-system",
						false));

		experiments.put("8",
				new ExperimentConfig("8", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10, "Tillgångssystem",
						false));

		experiments.put("9",
				new ExperimentConfig("9", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("10",
				new ExperimentConfig("10", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10, "Konstruktiva-system",
						false));

		experiments.put("11",
				new ExperimentConfig("11", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15, "Tillgångssystem",
						false));

		experiments.put("12",
				new ExperimentConfig("12", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("13",
				new ExperimentConfig("13", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15, "Konstruktiva-system",
						false));

		experiments.put("20",
				new ExperimentConfig("20", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Tillgångssystem",
						false));

		experiments.put("21",
				new ExperimentConfig("21", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("22",
				new ExperimentConfig("22", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Konstruktiva-system",
						false));

		experiments.put("23",
				new ExperimentConfig("23", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Tillgångssystem",
						false));

		experiments.put("24",
				new ExperimentConfig("24", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("25",
				new ExperimentConfig("25", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Konstruktiva-system",
						false));

		HierarchicalClassifierCoClassExperiment.RunESA(experiments.get("25"), false);

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
				CoClassIndexer t = new CoClassIndexer(conf.getRawDataFile(), conf.getTextIndex(), conf.getCsTable());
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean createConceptTree = !Files.exists(new File(conf.getConceptTreeFile()).toPath());
		if (createConceptTree || cleanRun) {
			DumpConceptTreeCoClass.testCoClassDataESA(conf.getNumOfConcepts(), conf.getConceptTreeFile(),
					conf.getTaxonomyFile(), conf.getCsTable());
			;
		}

		boolean createConceptFile = !Files.exists(new File(conf.getConceptFile()).toPath());
		if (createConceptFile || cleanRun) {
			CorpusESAConceptualizationCoClass.conceptualizeCorpus(conf.getNumOfConcepts(), conf.getTextIndex(),
					conf.getConceptFile());
		}

		ConceptClassificationESAML.testCoClassSimpleConcept(conf.getTopK(), conf.getCsTable(), conf.getTextIndex(),
				conf.getConceptTreeFile(), conf.getConceptFile(), conf.getOutputClassificationFile(),
				conf.getOutputLabelComparisonFile(), conf.getIncludeSuperTopic());
	}
}
