package se.bth.serl.flatclassifier.run;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.SB11TopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import se.bth.serl.flatclassifier.Evaluator;
import se.bth.serl.flatclassifier.ExperimentConfig;

public class FlatClassifierSB11Experiment {

	public static void main(String[] args) {
		String annotatedData = GenericCSConfig.rawData;
		String textIndex = "data/sb11/textindex/";
		String outputClassificationFile = "data/sb11/output/results.flat.classification.sb11";
		String outputLabelComparisonFile = "data/sb11/output/results.flat.labelComparison.sb11";

		HashMap<String, ExperimentConfig> experiments = new HashMap<>();

		experiments.put("1", new ExperimentConfig("1", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("2", new ExperimentConfig("2", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("3", new ExperimentConfig("3", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("4", new ExperimentConfig("4", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("5", new ExperimentConfig("5", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("6", new ExperimentConfig("6", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("7", new ExperimentConfig("7", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));
		
		experiments.put("8", new ExperimentConfig("8", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));
	
		experiments.put("9", new ExperimentConfig("9", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));

		experiments.put("10", new ExperimentConfig("10", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));
		
		experiments.put("11", new ExperimentConfig("11", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));
	
		experiments.put("12", new ExperimentConfig("12", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));

		experiments.put("13", new ExperimentConfig("13", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
		
		experiments.put("14", new ExperimentConfig("14", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
	
		experiments.put("15", new ExperimentConfig("15", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
		
		experiments.put("16", new ExperimentConfig("16", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
		
		experiments.put("17", new ExperimentConfig("17", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
	
		experiments.put("18", new ExperimentConfig("18", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
		
		experiments.put("19", new ExperimentConfig("19", SB11ExperimentConfig.SB11Table.Byggdelar.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));
		
		experiments.put("20", new ExperimentConfig("20", SB11ExperimentConfig.SB11Table.Landskapsinformation.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));
	
		experiments.put("21", new ExperimentConfig("21", SB11ExperimentConfig.SB11Table.Alternativtabell.toString(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));

		for(String id:experiments.keySet()) {
			FlatClassifierSB11Experiment.RunWord2Vec(experiments.get(id));
			System.out.println("Completed flat SB11 classifier experiment: " + id);
		}
		
	}

	public static void RunWord2Vec(ExperimentConfig conf) {

		String language = SB11ExperimentConfig.language;
		String csname = SB11ExperimentConfig.csName;
		String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
		String csModelFilename = SB11ExperimentConfig.csModelFile;
		String csTable = conf.getCsTable();
		String annotatedData = conf.getRawDataFile();
		String textIndex = conf.getTextIndex() + conf.getCsTable() + "/" ; 
		String outputClassificationFile = conf.getOutputClassificationFile() + "." + conf.getCsTable() + "."
				+ conf.getDescription();
		String outputLabelComparisonFile = conf.getOutputLabelComparisonFile() + "." + conf.getCsTable() + "."
				+ conf.getDescription();
		String outputTermsScoreFile = "data/sb11/output/results.flat.termsscore.sb11" + conf.getCsTable() + "." + conf.getDescription();
		
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
					.ClassifyWithWord2vec(language, csname, csTable, csrawdata, csModelFilename, annotatedData, outputTermsScoreFile, topK);
			if (classifiedRequirements == null) {
				System.out.println("Failed to classify requirements in " + annotatedData);
				return;
			}
			RequirementsClassifier.DumpResults(outputLabelComparisonFile, classifiedRequirements,
					sb11TDM.getDocTopicMap());
		}

		Evaluator eval = new Evaluator(outputLabelComparisonFile, sb11TDM.getDocTopicMap());
		eval.calculateMetrics();
		eval.dumpMetrics(outputClassificationFile);
	}
}
