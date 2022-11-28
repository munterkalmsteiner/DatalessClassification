package se.bth.serl.flatclassifier.run;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.CoClassTopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import se.bth.serl.flatclassifier.Evaluator;

public class FlatClassifierCoClassExperiment {
	
	public static void main (String[] args) {
		String language = CoClassExperimentConfig.language;
        String csname = CoClassExperimentConfig.csName;
        String csrawdata = CoClassExperimentConfig.coClassTaxonomy;
        String annotatedData = CoClassExperimentConfig.rawData;
        String cstable = CoClassExperimentConfig.CoClassTables.Tillgångssystem.toString();
        String csModelFilename = CoClassExperimentConfig.csModelFile;
        String textIndex = "data/coclass/textindex/" + cstable;
        
        int topK = 1;
        
        String outputFile = "data/coclass/output/flat.classification.top." + topK + "." + cstable;
        
        CoClassTopicDocMaps coclassTDM = new CoClassTopicDocMaps(csrawdata, cstable);
		coclassTDM.readTopicDocMap(textIndex);
		System.out.println("Found " + coclassTDM.getDocTopicMap().size() + " requirements with true labels form " + csname + "/" + cstable);
		
    	boolean runClassification = !Files.exists(new File(outputFile).toPath());
		if (runClassification) {
			System.out.println("Classifying requirements");
			HashMap<String, HashMap<String, Double>>  classifiedRequirements = RequirementsClassifier.ClassifyWithWord2vec(language,
	        		csname,
	        		cstable,
	        		csrawdata,
	        		csModelFilename,
	        		annotatedData,
	        		topK);
	        if(classifiedRequirements == null) {
	        	System.out.println("Failed to classify requirements in " + annotatedData);
	        	return;
	        }
			RequirementsClassifier.DumpResults(outputFile, classifiedRequirements, coclassTDM.getDocTopicMap());
		}
		
        Evaluator eval = new Evaluator(outputFile, coclassTDM.getDocTopicMap());
        eval.calculateMetrics();
	}
}
