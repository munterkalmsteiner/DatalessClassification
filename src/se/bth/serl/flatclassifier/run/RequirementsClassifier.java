package se.bth.serl.flatclassifier.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DataParser;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Requirement;
import edu.stanford.nlp.patterns.surface.SurfacePatternFactory.Genre;
import se.bth.serl.flatclassifier.Classifier;
import se.bth.serl.flatclassifier.classificationsystem.CSReader;
import se.bth.serl.flatclassifier.classificationsystem.CSReaderFactory;

public class RequirementsClassifier {

	private static final Logger log = LoggerFactory.getLogger(RequirementsClassifier.class);
	
	public static void main (String[] args) {
		String language = SB11ExperimentConfig.language;
        String csname = SB11ExperimentConfig.csName;
        String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
        String csTable = SB11ExperimentConfig.SB11Table.Byggdelar.toString();
        String csModelFilename = SB11ExperimentConfig.csModelFile;
        String annotatedData = GenericCSConfig.rawData;
        int topK = 2;
        
    	RequirementsClassifier.ClassifyWithWord2vec(language,
        		csname,
        		csTable,
        		csrawdata,
        		csModelFilename,
        		annotatedData,
        		topK);
	}
	
	public static HashMap<String, HashMap<String, Double>> ClassifyWithWord2vec (
			String language,
			String csName,
			String csTable,
			String csRawdata,
			String csModelFilename,
			String annotatedData,
			int topK
			) {
        String word2vecfilename = GenericCSConfig.word2vecmodel;
        
        Optional<CSReader> csr = CSReaderFactory.getReader(csName, csRawdata, language);
        if (csr.isEmpty())
            System.exit(1);
        
        File word2vecFile = new File(word2vecfilename);
        if (!word2vecFile.exists()) {
            log.error("Word2vec model file does not exist: " + word2vecfilename);
            System.exit(1);
        }
        
        File csModelFile = new File(csModelFilename);
        
        try {
            Classifier cl = new Classifier(csr.get(), csModelFile, csTable, word2vecFile);
            HashMap<String, HashMap<String, Double>> classifiedRequirements = new LinkedHashMap<String, HashMap<String,Double>>();
            
            DataParser parser = new DataParser(annotatedData);
            if (parser.parse()) {
                List<Requirement> reqs = parser.getRequirements();
                log.info("Found " + reqs.size() + " requirements.");
                
                int numReqs = reqs.size();
                for (int i = 0; i < numReqs; i++) {
                    Map<String, Double> classificationResults = cl.classifyRequirementWithTopK(reqs.get(i), topK);
                    
                    log.info("Classified requirements" + reqs.get(i).getReqId() + " with " + classificationResults.size());
                    
                    HashMap<String, Double> lowerCaseResults = new LinkedHashMap<String, Double>();
                    
                    if(classificationResults.size() > 0) {
                    	for(Entry<String, Double> e : classificationResults.entrySet()) {
                    		lowerCaseResults.put(e.getKey().toLowerCase(), e.getValue());
                    	}
                    }
                    
                    classifiedRequirements.put(reqs.get(i).getReqId(), lowerCaseResults);
                    
                    log.debug(reqs.get(i).toString());
                    log.debug("Classifications");
                    for(Entry<String, Double> e : classificationResults.entrySet()) {
                    	log.debug(e.getKey() + " , score: " + e.getValue());
                    }
                    log.debug("----------------");
                    log.info("Analyzed " + (i + 1) + " of " + numReqs + " requirements");
                    log.debug("----------------");
                }
                return classifiedRequirements;
            }
        }
        catch (CASException | ResourceInitializationException | IOException e) {
            e.printStackTrace();
        }
		return null; 
	}
	
	public static void DumpResults(String outputFile, HashMap<String, HashMap<String, Double>> classifiedRequirements,
			HashMap<String,HashSet<String>> docLabelMap) {
		try {
			FileWriter writer = new FileWriter(outputFile);
			writer.write("docId_classresults_truelabels\n\r");
			for(Entry<String,HashMap<String,Double>> entry : classifiedRequirements.entrySet()){
				String docID = entry.getKey();
				HashMap<String, Double> classes = entry.getValue();
				writer.write(docID + "\t");
				for(String label : classes.keySet()) {
					writer.write(label + "," + classes.get(label) + ";");	
				}
				writer.write("\t");
				HashSet<String> trueLabels = docLabelMap.get(docID);
				if(trueLabels != null) {
					for(String truelabel : trueLabels) {
						writer.write(truelabel + ";");
					}	
				}
				writer.write("\n\r");
			}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
