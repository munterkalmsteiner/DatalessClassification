package se.bth.serl.flatclassifier.run;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.CoClassTopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import se.bth.serl.flatclassifier.Evaluator;
import se.bth.serl.flatclassifier.ExperimentConfig;

public class FlatClassifierCoClassExperiment {
	
	public static void main (String[] args) {
		String annotatedData = GenericCSConfig.rawData;
		String textIndex = "data/coclass/textindex/";
		String outputClassificationFile = "data/coclass/output/results.flat.classification.coclass";
		String outputLabelComparisonFile = "data/coclass/output/results.flat.labelComparison.coclass";

		HashMap<String, ExperimentConfig> experiments = new HashMap<>();

		experiments.put("22", new ExperimentConfig("22", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("23", new ExperimentConfig("23", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("24", new ExperimentConfig("24", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 1, false));
		
		experiments.put("25", new ExperimentConfig("25", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("26", new ExperimentConfig("26", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("27", new ExperimentConfig("27", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 2, false));
		
		experiments.put("28", new ExperimentConfig("28", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));
		
		experiments.put("29", new ExperimentConfig("29", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));
	
		experiments.put("30", new ExperimentConfig("30", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 5, false));

		experiments.put("31", new ExperimentConfig("31", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));
		
		experiments.put("32", new ExperimentConfig("32", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));
	
		experiments.put("33", new ExperimentConfig("33", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 10, false));

		experiments.put("34", new ExperimentConfig("34", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
		
		experiments.put("35", new ExperimentConfig("35", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
	
		experiments.put("36", new ExperimentConfig("36", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 15, false));
		
		experiments.put("37", new ExperimentConfig("37", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
		
		experiments.put("38", new ExperimentConfig("38", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
	
		experiments.put("39", new ExperimentConfig("39", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 20, false));
		
		experiments.put("40", new ExperimentConfig("40", CoClassExperimentConfig.CoClassTable.Tillgångssystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));
		
		experiments.put("41", new ExperimentConfig("41", CoClassExperimentConfig.CoClassTable.GrundfunktionerochKomponenter.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));
	
		experiments.put("42", new ExperimentConfig("42", CoClassExperimentConfig.CoClassTable.Konstruktivasystem.getValue(),
				annotatedData, textIndex, outputClassificationFile, outputLabelComparisonFile, 25, false));

		for(String id:experiments.keySet()) {
			FlatClassifierCoClassExperiment.RunWord2Vec(experiments.get(id));	
			System.out.println("Completed flat coclass classifier experiment: " + id);
		}
	}
	
	public static void RunWord2Vec(ExperimentConfig conf) {

		String language = CoClassExperimentConfig.language;
		String csname = CoClassExperimentConfig.csName;
		String csrawdata = CoClassExperimentConfig.coClassTaxonomy;
		String csModelFilename = CoClassExperimentConfig.csModelFile;
		String csTable = conf.getCsTable();
		String annotatedData = conf.getRawDataFile();
		String textIndex = conf.getTextIndex() + conf.getCsTable() + "/" ; 
		String outputClassificationFile = conf.getOutputClassificationFile() + "." + conf.getCsTable() + "."
				+ conf.getDescription();
		String outputLabelComparisonFile = conf.getOutputLabelComparisonFile() + "." + conf.getCsTable() + "."
				+ conf.getDescription();
		String outputTermsScoreFile = "data/coclass/output/results.flat.termsscore.coclass" + conf.getCsTable() + "." + conf.getDescription();
		
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
					.ClassifyWithWord2vec(language, csname, csTable.replace("-", " "), csrawdata, csModelFilename, annotatedData,outputTermsScoreFile ,topK);
			if (classifiedRequirements == null) {
				System.out.println("Failed to classify requirements in " + annotatedData);
				return;
			}
			RequirementsClassifier.DumpResults(outputLabelComparisonFile, classifiedRequirements,
					coclassTDM.getDocTopicMap());
		}

		Evaluator eval = new Evaluator(outputLabelComparisonFile, coclassTDM.getDocTopicMap());
		eval.calculateMetrics();
		eval.dumpMetrics(outputClassificationFile);
	}
}
