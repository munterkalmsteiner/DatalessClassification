package se.bth.serl.flatclassifier.run;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements.SB11TopicDocMaps;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import se.bth.serl.flatclassifier.Evaluator;

public class FlatClassifierSB11Experiment {
	
	public static void main (String[] args) {
		String language = SB11ExperimentConfig.language;
        String csname = SB11ExperimentConfig.csName;
        String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
        String cstable = SB11ExperimentConfig.sb11Table;
        String annotatedData = GenericCSConfig.rawData;
        String textIndex = "data/sb11/textindex/" + cstable;
        String csModelFilename = SB11ExperimentConfig.csModelFile;
        Boolean includeSuperTopic = false;
        int topK = 1;
        
        String outputFile = "data/sb11/output/flat.classification.top." + topK + "." + cstable;
        
        SB11TopicDocMaps sb11TDM = new SB11TopicDocMaps(csrawdata, cstable, includeSuperTopic);
		sb11TDM.readTopicDocMap(textIndex);
		System.out.println("Found " + sb11TDM.getDocTopicMap().size() + " requirements with true labels form " + csname + "/" + cstable);
		
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
			RequirementsClassifier.DumpResults(outputFile, classifiedRequirements, sb11TDM.getDocTopicMap());
		}
		
        Evaluator eval = new Evaluator(outputFile, sb11TDM.getDocTopicMap());
        eval.calculateMetrics();
	}
}
