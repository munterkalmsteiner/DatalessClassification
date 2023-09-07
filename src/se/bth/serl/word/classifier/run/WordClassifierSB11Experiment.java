package se.bth.serl.word.classifier.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.SB11TopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig.SB11Table;
import se.bth.serl.word.classifier.Evaluator;
import se.bth.serl.word.classifier.ExperimentConfig;

public class WordClassifierSB11Experiment {

	public static void main(String[] args) {
		String annotatedData = GenericCSConfig.rawData;
		String textIndex = "data/sb11/textindex/";
		String outputClassificationFile = "data/sb11/output/results.word2vec.classification.sb11";
		String outputLabelComparisonFile = "data/sb11/output/results.word2vec.labelComparison.sb11";
		String[] htypes = { "bottomup", "flat"};
		int[] kValues = { 5, 10, 15, 20 };
		HashMap<String, ExperimentConfig> experiments = new HashMap<>();

		int counter = 1;
		for (int h = 0; h < htypes.length; h++) {
			for (int kId = 0; kId < kValues.length; kId++) {
				for (int t = 0; t < SB11Table.values().length; t++) {
					int expId = counter;
					int sb11Offset = 100;
					ExperimentConfig exp = new ExperimentConfig();
					exp.setDescription(String.valueOf((expId + sb11Offset)));
					exp.setTextIndex(textIndex);
					exp.setCsTable(SB11Table.values()[t].toString());
					exp.setHierarchy(htypes[h]);
					exp.setTopK(kValues[kId]);
					exp.setRawDataFile(annotatedData);
					exp.setOutputClassificationFile(outputClassificationFile);
					exp.setOutputLabelComparisonFile(outputLabelComparisonFile);
					exp.setIncludeSuperTopic(false);
					
					experiments.put(exp.getDescription(),exp);
					counter++;
				}
			}
		}

		HashMap<String, Double[]> results = new HashMap<String, Double[]>();
		
		String resultsBaseFilePath = "data/sb11/output/combined-results.word2vec.sb11";
		for (String id : experiments.keySet()) {
			ExperimentConfig ex = experiments.get(id);								
			Double[] result = WordClassifierSB11Experiment.RunWord2Vec(ex);
			results.put(id, result);
			System.out.println("Completed word2vec SB11 classifier experiment: " + id);
		}
		
		ResultsWriter.toCSV(results, experiments, resultsBaseFilePath, "SB11", "word2vec");
	}

	public static Double[] RunWord2Vec(ExperimentConfig conf) {

		String language = SB11ExperimentConfig.language;
		String csname = SB11ExperimentConfig.csName;
		String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
		String csModelFilename = SB11ExperimentConfig.csModelFile.substring(0, SB11ExperimentConfig.csModelFile.length() - 4) 
				+ conf.getHierarchy() + ".json";
		String csTable = conf.getCsTable();
		String annotatedData = conf.getRawDataFile();
		String textIndex = conf.getTextIndex() + conf.getCsTable() + "/";
		String outputClassificationFile = conf.getOutputClassificationFile() + "." + conf.getCsTable() + "."
				+ conf.getHierarchy() + "." + conf.getDescription();
		String outputLabelComparisonFile = conf.getOutputLabelComparisonFile() + "." + conf.getCsTable() + "."
				+ conf.getHierarchy() + "." + conf.getDescription();
		String outputTermsScoreFile = "data/sb11/output/results.word2vec.termsscore.sb11." + conf.getCsTable() + "."
				+ conf.getHierarchy() + "." + conf.getDescription();
		String hierarchy = conf.getHierarchy();

		int topK = conf.getTopK();
		boolean includeSuperTopic = conf.getIncludeSuperTopic();

		SB11TopicDocMaps sb11TDM = new SB11TopicDocMaps(csrawdata, csTable, includeSuperTopic);
		sb11TDM.readTopicDocMap(textIndex);
		System.out.println("Found " + sb11TDM.getDocTopicMap().size() + " requirements with true labels form " + csname
				+ "/" + csTable);

		boolean runClassification = !Files.exists(new File(outputLabelComparisonFile).toPath());
		if (runClassification) {
			System.out.println("Classifying requirements");
			HashMap<String, HashMap<String, Double>> classifiedRequirements = RequirementsClassifier
					.ClassifyWithWord2vec(language, csname, csTable, csrawdata, csModelFilename, annotatedData,
							outputTermsScoreFile, hierarchy, topK);
			if (classifiedRequirements == null) {
				System.out.println("Failed to classify requirements in " + annotatedData);
				return null;
			}
			RequirementsClassifier.DumpResults(outputLabelComparisonFile, classifiedRequirements,
					sb11TDM.getDocTopicMap());
		}

		Evaluator eval = new Evaluator(outputLabelComparisonFile, sb11TDM.getDocTopicMap());
		eval.calculateMetrics();
		eval.dumpMetrics(outputClassificationFile);

		Double[] resultArray = { eval.getuRecall(), eval.getMRecall(), eval.getuPrecision(), eval.getMPrecision(),
				eval.getuF1(), eval.getMF1(), eval.getMinClassificationsCount(), eval.getMaxClassificationsCount(),
				eval.getAverageClassificationsCount() };

		return resultArray;
	}
}
