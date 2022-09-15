package se.bth.serl.flatclassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bth.serl.flatclassifier.utils.ConfusionMatrix;

public class Evaluator {
	
	private static final Logger log = LoggerFactory.getLogger(Evaluator.class);
	
	double MRecall;
	double uRecall;
	double MPrecision;
	double uPrecision;
	double uF1;
	double MF1;
	HashMap<String, ConfusionMatrix> cms;
	
	public Evaluator (HashMap<String, HashMap<String, Double>> classifiedRequirements,
			HashMap<String, HashSet<String>> docTopicMap) {
		cms = calculateConfusionMatrix(classifiedRequirements, docTopicMap);
	}

	
	public Evaluator (String resultsFile, HashMap<String, HashSet<String>> docTopicMap) {
		HashMap<String, HashMap<String, Double>> classifiedRequirements = readResultsFromDump(resultsFile);
		cms = calculateConfusionMatrix(classifiedRequirements, docTopicMap);
	}
	
	private HashMap<String, HashMap<String, Double>> readResultsFromDump(String resultsFile){
		HashMap<String, HashMap<String, Double>> classifiedRequirements = new HashMap<String, HashMap<String, Double>>();
		try {
			FileReader reader = new FileReader(resultsFile);
			BufferedReader bf = new BufferedReader(reader);
			String line = "";
			int count = -1; //skip title
			while ((line = bf.readLine()) != null) {
				if (line.equals("") == true)
					continue;

				count++;
				String[] tokens = line.trim().split("\t");
				
				if (tokens.length != 3)
					continue;
				
				String docID = tokens[0];
				String[] classResults = tokens[1].split(";");
				HashMap<String, Double> classificationsMap = new HashMap<String, Double>();
				
				for(String resultString : classResults) {
					String[] resultTokens = resultString.split(",");
					if(resultTokens.length < 2) {
						continue;
					}
					String label = resultTokens[0];
					Double score = Double.parseDouble(resultTokens[1]);
					classificationsMap.put(label, score);
				}
				//String[] trueLables = tokens[2].split(";");
				
				classifiedRequirements.put(docID, classificationsMap);
			}
			log.info("Read " + count + " requirements from dump.");
			bf.close();
			reader.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return classifiedRequirements;
	}
	

	public HashMap<String, ConfusionMatrix> calculateConfusionMatrix(HashMap<String, HashMap<String, Double>> docClassifedTopicMap, 
			HashMap<String, HashSet<String>> docTrueTopicMap) {
		HashMap<String, ConfusionMatrix> cms = new HashMap<String,ConfusionMatrix>();
		
		for(String docId : docTrueTopicMap.keySet()) {
			Map<String, Double> classifiedLabels = docClassifedTopicMap.get(docId);
			HashSet<String> trueLabels = docTrueTopicMap.get(docId);
			if(trueLabels != null && trueLabels.size() > 0) {
				log.debug(docId + " true labels: " + trueLabels.toString());	
			}
			else {
				log.debug(docId + " has no true labels.");
			}
			
			for(String trueLabel : trueLabels) {
				//skip empty labels
				if(trueLabel.isBlank()) {
					continue;
				}
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
	
	public void calculateMetrics() {		
		log.info(cms.size() + " labels used.");
		
		//Recall
		int allTp = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp())
				.sum();
		
		int allTpAndFn = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp() +  e.getValue().getFn())
				.sum();
		
		this.uRecall =  (double) allTp / (double) allTpAndFn; 
				
		this.MRecall = (double) cms.entrySet().stream()
				.mapToDouble(e -> e.getValue().getTp() == 0 ? 0 : ( e.getValue().getTp() / (e.getValue().getTp() + e.getValue().getFn())))
				.sum()
				/ 
				(double) cms.size();
		
		log.info("micro recall:" + this.uRecall);
		log.info("macro recall:" + this.MRecall);
		
		//Precision
		Integer allTpAndFp = cms.entrySet().stream()
				.mapToInt(e -> e.getValue().getTp() +  e.getValue().getFp())
				.sum();
		
		uPrecision =  (double) allTp / (double) allTpAndFp; 
		
		MPrecision = (double) cms.entrySet().stream()
				.mapToDouble(e -> e.getValue().getTp() == 0 ? 0 : ( e.getValue().getTp() / (e.getValue().getTp() + e.getValue().getFp())))
				.sum()
				/ 
				(double) cms.size();
		
		log.info("micro precision:" + uPrecision);
		log.info("macro precision:" + MPrecision);
		
		//F1
		Integer B = 1;
		
		uF1 = ((Math.pow(B, 2) + 1) * this.uPrecision * uRecall) / (Math.pow(B, 2) * uPrecision + uRecall);
		MF1 = ((Math.pow(B, 2) + 1) * this.MPrecision * MRecall) / (Math.pow(B, 2) * MPrecision + MRecall);
		
		log.info("micro F1:" + this.uF1);
		log.info("macro F1:" + this.MF1);
	}
	
	public double getMRecall() {
		return MRecall;
	}

	public double getuRecall() {
		return uRecall;
	}

	public double getMPrecision() {
		return MPrecision;
	}

	public double getuPrecision() {
		return uPrecision;
	}

	public double getuF1() {
		return uF1;
	}

	public double getMF1() {
		return MF1;
	}

	public HashMap<String, ConfusionMatrix> getCms() {
		return cms;
	}
}
