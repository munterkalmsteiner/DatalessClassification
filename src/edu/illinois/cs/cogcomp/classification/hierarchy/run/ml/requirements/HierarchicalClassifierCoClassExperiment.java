package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

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
		String outputLabelComparisonFile = "data/coclass/output/result.concept.coclass.labelComparison." + numOfConcepts;

		HashMap<String, ExperimentConfig> experiments = new HashMap<String, ExperimentConfig>();

		experiments.put("22",
				new ExperimentConfig("22", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 1, "Tillgångssystem",
						false));
		
		experiments.put("23",
				new ExperimentConfig("23", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 1,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("24",
				new ExperimentConfig("24", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 1, "Konstruktiva-system",
						false));
		
		experiments.put("25",
				new ExperimentConfig("25", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 2, "Tillgångssystem",
						false));
		
		experiments.put("26",
				new ExperimentConfig("26", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 2,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("27",
				new ExperimentConfig("27", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 2, "Konstruktiva-system",
						false));

		experiments.put("28",
				new ExperimentConfig("28", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5, "Tillgångssystem",
						false));

		experiments.put("29",
				new ExperimentConfig("29", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("30",
				new ExperimentConfig("30", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 5, "Konstruktiva-system",
						false));

		experiments.put("31",
				new ExperimentConfig("31", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10, "Tillgångssystem",
						false));

		experiments.put("32",
				new ExperimentConfig("32", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("33",
				new ExperimentConfig("33", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 10, "Konstruktiva-system",
						false));

		experiments.put("34",
				new ExperimentConfig("34", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15, "Tillgångssystem",
						false));

		experiments.put("35",
				new ExperimentConfig("35", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("36",
				new ExperimentConfig("36", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 15, "Konstruktiva-system",
						false));

		experiments.put("37",
				new ExperimentConfig("37", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Tillgångssystem",
						false));

		experiments.put("38",
				new ExperimentConfig("38", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("39",
				new ExperimentConfig("39", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Konstruktiva-system",
						false));
		
		experiments.put("40",
				new ExperimentConfig("40", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 25, "Tillgångssystem",
						false));
		
		experiments.put("41",
				new ExperimentConfig("41", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 25,
						"Grundfunktioner-och-Komponenter", false));

		experiments.put("42",
				new ExperimentConfig("42", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 25, "Konstruktiva-system",
						false));
		
//		experiments.put("101",
//				new ExperimentConfig("101", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
//						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Tillgångssystem",
//						true));
//
//		experiments.put("102",
//				new ExperimentConfig("102", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
//						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20,
//						"Grundfunktioner-och-Komponenter", true));
//
//		experiments.put("103",
//				new ExperimentConfig("103", coClassTaxonomy, rawData, textIndex, conceptTreeFile, conceptFile,
//						outputClassificationFile, outputLabelComparisonFile, numOfConcepts, 20, "Konstruktiva-system",
//						true));
		
		
//		experiments.put("XX",
//				new ExperimentConfig("XX_Bootstrappin", coClassTaxonomy, 
//						"data/sb11/raw/reqs_with_annotation_for_hc_20220901_CS_T.csv",
//						textIndex, conceptTreeFile, conceptFile,
//						outputClassificationFile, outputLabelComparisonFile,
//						numOfConcepts, 20, "Tillgångssystem",
//						false));
		
	
		for(String ex:experiments.keySet()) {
			HierarchicalClassifierCoClassExperiment.RunESA(experiments.get(ex), false);	
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
//			System.setProperty("sun.jnu.encoding", "UTF-8");
			System.out.println(System.getProperty("sun.jnu.encoding"));
//			System.out.println("he");
			Path path = Paths.get(conf.getTextIndex());
			indexRawData = Files.list(new File(conf.getTextIndex()).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.print(System.getProperty("sun.jnu.encoding"));
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
		
		System.out.print("Experiment number "  + conf.getDescription() + " is completed.");
	}
}
