package se.bth.serl.flatclassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.neuralnet.SquareNeighbourhood;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11.SB11TopicDocMaps;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.DataParser;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.Requirement;
import se.bth.serl.flatclassifier.classificationsystem.CSReader;
import se.bth.serl.flatclassifier.classificationsystem.CSReaderFactory;

public class Evaluation {
	
	private static final Logger log = LoggerFactory.getLogger(Classifier.class);
	
    public static void main (String[] args) {
		String sb11Taxonomy = SB11ExperimentConfig.sb11Taxonomy;
		String sb11Table = "Byggdelar";
		String textIndex = "data/sb11/textindex/" + sb11Table;
		
		//Read true labels
    	HashMap<String, HashSet<String>> docTopicMap = Evaluation.readDocTopicMap(textIndex,
    			sb11Taxonomy,
    			sb11Table);
    	
    	Evaluation.standAlone(docTopicMap);	
    }

	public static void standAlone(HashMap<String, HashSet<String>> docTopicMap) {
		//Classify with SB11 flat classifer
    	String language = SB11ExperimentConfig.language;
        String csname = SB11ExperimentConfig.csName;
        String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
        String csModelfilename = SB11ExperimentConfig.csModelFile;
        String word2vecfilename = SB11ExperimentConfig.word2vecmodel;
        String annotatedData = SB11ExperimentConfig.rawDataSB11;
        String csTable = SB11ExperimentConfig.sb11Table;
        
        
        Optional<CSReader> csr = CSReaderFactory.getReader(csname, csrawdata, language);
        if (csr.isEmpty())
            System.exit(1);
        
        File word2vecFile = new File(word2vecfilename);
        if (!word2vecFile.exists()) {
            log.error("Word2vec model file does not exist: " + word2vecfilename);
            System.exit(1);
        }
        
        File csModelFile = new File(csModelfilename);
        
        try {
            Classifier cl = new Classifier(csr.get(), csModelFile, csTable, word2vecFile);
            HashMap<String, Map<String, Double>> classifiedRequirements = new LinkedHashMap<String, Map<String,Double>>();
            
            DataParser parser = new DataParser(annotatedData);
            if (parser.parse()) {
                List<Requirement> reqs = parser.getRequirements();
                log.info("Found " + reqs.size() + " requirements.");
                
                int numReqs = reqs.size();
                for (int i = 0; i < numReqs; i++) {
                    Map<String, Double> classificationResults = cl.classifyRequirement(reqs.get(i));
                    
                    Map<String, Double> lowerCaseResults = new LinkedHashMap<String, Double>();
                    
                    if(classificationResults.size() > 0) {
                    	for(Entry<String, Double> e : classificationResults.entrySet()) {
                    		lowerCaseResults.put(e.getKey().toLowerCase(), e.getValue());
                    	}
                    	
                    }
                    
                    classifiedRequirements.put(reqs.get(i).getReqId(), lowerCaseResults);
                    
                    log.debug(reqs.get(i).toString());
                    log.debug("Classififactions");
                    for(Entry<String, Double> e : classificationResults.entrySet()) {
                    	log.debug(e.getKey() + " , score: " + e.getValue());
                    }
                    log.debug("----------------");
                    log.info("Analyzed " + (i + 1) + " of " + numReqs + " requirements");
                    log.debug("----------------");
                }
                
        		//Calculate confusion matrix
                HashMap<String, ConfusionMatrix> cms = calculateConfusionMatrix(classifiedRequirements, docTopicMap);
                calculateMetrics(cms);
            }
        }
        catch (CASException | ResourceInitializationException | IOException e) {
            e.printStackTrace();
        } 

		//Calculate other metrics
		//Write results to file and console
	}
	
	public static HashMap<String, ConfusionMatrix> calculateConfusionMatrix(HashMap<String, Map<String, Double>> docClassifedTopicMap, 
			HashMap<String, HashSet<String>> docTrueTopicMap) {
		HashMap<String, ConfusionMatrix> cms = new HashMap<String,ConfusionMatrix>();
		for(String docId : docClassifedTopicMap.keySet()) {
			Map<String, Double> classifiedLabels = docClassifedTopicMap.get(docId);
			HashSet<String> trueLabels = docTrueTopicMap.get(docId);
			
			for(String trueLabel : trueLabels) {
				if(!cms.containsKey(trueLabel)) {
					cms.put(trueLabel, new ConfusionMatrix());
				}
				if(classifiedLabels.containsKey(trueLabel)) {
					cms.get(trueLabel).setTp(cms.get(trueLabel).getTp() + 1);
				}
				else {
					cms.get(trueLabel).setFn(cms.get(trueLabel).getFn() + 1);
				}
			}
			
			for(String classifiedLabel : classifiedLabels.keySet()) {
				if(!cms.containsKey(classifiedLabel)) {
					cms.put(classifiedLabel, new ConfusionMatrix());
				}
				if(!trueLabels.contains(classifiedLabel)) {
					cms.get(classifiedLabel).setFp(cms.get(classifiedLabel).getFp() + 1);
				}
			}
		}
		return cms;
		
	}
	
	public static void calculateMetrics(HashMap<String, ConfusionMatrix> cms) {
		
		//Recall
		Integer allTp = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp())
				.sum();
		
		Integer allTpAndFn = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp() +  e.getValue().getFn())
				.sum();
		
		Double microRecall =  (double) allTp / (double) allTpAndFn; 
				
		Double macroRecall = (double) cms.entrySet().stream()
				.mapToDouble(e -> e.getValue().getTp() == 0 ? 0 : ( e.getValue().getTp() / (e.getValue().getTp() + e.getValue().getFn())))
				.sum()
				/ 
				(double) cms.size();
		
		log.info("micro recall:" + microRecall);
		log.info("macro recall:" + macroRecall);
		
		//Precision
		Integer allTpAndFp = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp() +  e.getValue().getFp())
				.sum();
		
		Double microPrecision =  (double) allTp / (double) allTpAndFp; 
		
		Double macroPrecision = (double) cms.entrySet().stream()
				.mapToDouble(e -> e.getValue().getTp() == 0 ? 0 : ( e.getValue().getTp() / (e.getValue().getTp() + e.getValue().getFp())))
				.sum()
				/ 
				(double) cms.size();
		
		log.info("micro precision:" + microPrecision);
		log.info("macro precision:" + macroPrecision);
		
		//F1
		Integer B = 1;
		
		Double microF1 = ((Math.pow(B, 2) + 1) * microPrecision * microRecall) / (Math.pow(B, 2) * microPrecision + microRecall);
		Double macroF1 = ((Math.pow(B, 2) + 1) * macroPrecision * macroPrecision) / (Math.pow(B, 2) * macroPrecision + macroPrecision);
		
		log.info("micro F1:" + microF1);
		log.info("macro F1:" + macroF1);
	}

	
	private static HashMap<String, HashSet<String>> readDocTopicMap(String textIndex,
			String sb11Taxonomy,
			String sb11Table) {
		
		SB11TopicDocMaps sb11TDM = new SB11TopicDocMaps(sb11Taxonomy, sb11Table);
		sb11TDM.readTopicDocMap(textIndex);
		
		return sb11TDM.getDocTopicMap();
	}
}
