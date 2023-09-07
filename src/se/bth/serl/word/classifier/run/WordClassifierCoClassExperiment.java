package se.bth.serl.word.classifier.run;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;




import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.CoClassTopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig.CoClassTable;
import se.bth.serl.word.classifier.Evaluator;
import se.bth.serl.word.classifier.ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;

public class WordClassifierCoClassExperiment {
	
	public static void main (String[] args) {
		String annotatedData = GenericCSConfig.rawData;
		String textIndex = "data/coclass/textindex/";
		String outputClassificationFile = "data/coclass/output/results.word2vec.classification.coclass";
		String outputLabelComparisonFile = "data/coclass/output/results.word2vec.labelComparison.coclass";
		String[] htypes = { "flat", "bottomup" };
		int[] kValues = { 5, 10, 15, 20 };
		HashMap<String, ExperimentConfig> experiments = new HashMap<>();		

		int counter = 1;
		for (int h = 0; h < htypes.length; h++) {
			for (int kId = 0; kId < kValues.length; kId++) {
				for (int t = 0; t < CoClassTable.values().length; t++) {
					int expId = counter;
					int coClassOffset = 200;
					ExperimentConfig exp = new ExperimentConfig();
					exp.setDescription(String.valueOf((expId + coClassOffset)));
					exp.setTextIndex(textIndex);
					exp.setCsTable(CoClassTable.values()[t].getValue());
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
		
		String resultsBaseFilePath = "data/coclass/output/combined-results.word2vec.coclass";
		for (String id : experiments.keySet()) {
			ExperimentConfig ex = experiments.get(id);								
			Double[] result = WordClassifierCoClassExperiment.RunWord2Vec(ex);
			results.put(id, result);
			System.out.println("Completed word2vec CoClass classifier experiment: " + id);
		}
		
		ResultsWriter.toCSV(results, experiments, resultsBaseFilePath, "CoClass", "word2vec");
		
	}
	
	public static Double[] RunWord2Vec(ExperimentConfig conf) {

		String language = CoClassExperimentConfig.language;
		String csname = CoClassExperimentConfig.csName;
		String csrawdata = CoClassExperimentConfig.coClassTaxonomy;
		String csModelFilename = CoClassExperimentConfig.csModelFile.substring(0, CoClassExperimentConfig.csModelFile.length() - 4) 
				+ conf.getHierarchy() + ".json";
		String csTable = conf.getCsTable();
		String annotatedData = conf.getRawDataFile();
		String textIndex = conf.getTextIndex() + conf.getCsTable() + "/" ; 
		String outputClassificationFile = conf.getOutputClassificationFile() + "." + conf.getCsTable() + "." +  conf.getHierarchy() + "."
				+ conf.getDescription();
		String outputLabelComparisonFile = conf.getOutputLabelComparisonFile() + "." + conf.getCsTable() + "." +  conf.getHierarchy() + "."
				+ conf.getDescription();
		String outputTermsScoreFile = "data/coclass/output/results.word2vec.termsscore.coclass." + conf.getCsTable() + "." + conf.getHierarchy()  + "." + conf.getDescription();
		String hierarchy = conf.getHierarchy();
		
		int topK = conf.getTopK();
		boolean includeSuperTopic = conf.getIncludeSuperTopic();

		CoClassTopicDocMaps coclassTDM = new CoClassTopicDocMaps(csrawdata, csTable, includeSuperTopic);
		coclassTDM.readTopicDocMap(textIndex);
		System.out.println("Found " + coclassTDM.getDocTopicMap().size() + " requirements with true labels form " + csname
				+ "/" + csTable);

		boolean runClassification = !Files.exists(new File(outputLabelComparisonFile).toPath());
		if (runClassification) {
			System.out.println("Classifying requirements");
			HashMap<String, HashMap<String, Double>> classifiedRequirements = RequirementsClassifier
					.ClassifyWithWord2vec(language, csname, csTable.replace("-", " "), csrawdata, csModelFilename, annotatedData,outputTermsScoreFile, hierarchy, topK);
			if (classifiedRequirements == null) {
				System.out.println("Failed to classify requirements in " + annotatedData);
				return null;
			}
			RequirementsClassifier.DumpResults(outputLabelComparisonFile, classifiedRequirements,
					coclassTDM.getDocTopicMap());
		}

		Evaluator eval = new Evaluator(outputLabelComparisonFile, coclassTDM.getDocTopicMap());
		eval.calculateMetrics();
		eval.dumpMetrics(outputClassificationFile);
		
		Double[] resultArray = { eval.getuRecall(), eval.getMRecall(), eval.getuPrecision(), eval.getMPrecision(),
				eval.getuF1(), eval.getMF1(), eval.getMinClassificationsCount(), eval.getMaxClassificationsCount(),
				eval.getAverageClassificationsCount() };

		return resultArray;
	}
}
