package se.bth.serl.flatclassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.GenericCSConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.DataParser;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Requirement;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.classificationsystem.CSObject;
import se.bth.serl.flatclassifier.classificationsystem.CSReader;
import se.bth.serl.flatclassifier.classificationsystem.CSReaderFactory;
import se.bth.serl.flatclassifier.predictor.IPredictor;
import se.bth.serl.flatclassifier.predictor.SimpleNounPredictor;
import se.bth.serl.flatclassifier.predictor.Word2VecPredictor;
import se.bth.serl.flatclassifier.utils.NLP;
import se.bth.serl.flatclassifier.utils.NLP.Language;
import se.bth.serl.flatclassifier.utils.Term;

public class Classifier
{
    private static Map<String, List<CSObject>> csModel = null;
    private static Word2Vec w2vModel = null;
    private static BufferedWriter bw;
    private static final Logger log = LoggerFactory.getLogger(Classifier.class);
    private ClassificationSystem cs;
    private Language lang;
    private String csTable;
    private List<IPredictor> predictors;
    private JCas jcas;
   
    public static void main(String[] args)
    {
        String language = SB11ExperimentConfig.language;
        String csname = SB11ExperimentConfig.csName;
        String csrawdata = SB11ExperimentConfig.sb11Taxonomy;
        String csModelfilename = SB11ExperimentConfig.csModelFile;
        String csTable = SB11ExperimentConfig.SB11Table.Byggdelar.toString();
        
        String word2vecfilename = GenericCSConfig.word2vecmodel;
        String annotatedData = GenericCSConfig.rawData;
         
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
            bw = new BufferedWriter(new FileWriter(new File("data/sb11/sb11result.txt")));
            Classifier cl = new Classifier(csr.get(), csModelFile, csTable, word2vecFile);
            
            DataParser parser = new DataParser(annotatedData);
            if (parser.parse()) {
                List<Requirement> reqs = parser.getRequirements();
                log.info("Found " + reqs.size() + " requirements.");
                
                int numReqs = reqs.size();
                for (int i = 0; i < numReqs; i++) {
                    cl.classify(reqs.get(i));
                    log.info("Analyzed " + (i + 1) + " of " + numReqs + " requirements");
                }
            }

            bw.close();
        }
        catch (CASException | ResourceInitializationException | IOException e) {
            e.printStackTrace();
        } 
    }
    
    public Classifier(CSReader csr, File csModelFile, String csTable, File word2vecfile) throws 
        CASException, 
        ResourceInitializationException, 
        IOException
    {
        this.cs = csr.getClassificationSystem();
        this.lang = csr.getLanguage();
        this.csTable = csTable; 
        reset();
        
        if (csModel == null) {
            csModel = csr.read();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(csModel);
            BufferedWriter bw = new BufferedWriter(new FileWriter(csModelFile));
            bw.write(json);
            bw.close();
        }

        if (w2vModel == null) {
            w2vModel = WordVectorSerializer.readWord2VecModel(word2vecfile);
        }
        
        if (jcas == null) {
            jcas = JCasFactory.createJCas();
        }

        predictors = new ArrayList<>();
        predictors.add(new SimpleNounPredictor(csModel, csTable));
        predictors.add(new Word2VecPredictor(csModel, csTable, w2vModel));
    }
    
    public void reset() {
    	csModel = null;
    	jcas = null;
    }
    
    /**
     * I added this class so that we return the classifications 
     * rather than writing them in the console
     *  
     * @param requirement
     * @return classifications
     */
    public Map<String, Double> classifyRequirement(Requirement requirement, int topKPerTerm) {
    	
    	Map<String, Double> results = new LinkedHashMap<String, Double>();
    	
    	jcas.setDocumentLanguage(NLP.getLanguageString(lang));
        jcas.setDocumentText(requirement.getText(lang));
     
        StringBuilder sb = new StringBuilder();
        
        sb.append("-----------");
        sb.append(requirement.getReqId());
        sb.append("-----------\n\n");
        sb.append(requirement.getText(lang));
        sb.append("\nAnnotations (");
        sb.append(csTable);
        sb.append("): ");
        sb.append(requirement.getAnnotationInfo(cs, lang, csTable));
        sb.append("\n\n");
        
        try {
            SimplePipeline.runPipeline(jcas, NLP.baseAnalysisEngine());
            
            /* For some reason, we get Tokens with different POS (and possibly other varying 
             * features) JCas with select. Hence, we keep track of the terms we are interested in 
             * (nouns). Note that the hash function of Term does not consider the POS value since
             * it seems that a term can be tagged with different noun POS tags. In this way, we
             * avoid that those terms appear twice. 
             */
            Set<Term> uniqueTerms = new HashSet<>();
            
            int nouns = 0;
            for (Token token : JCasUtil.select(jcas, Token.class)) {
                Term term = new Term(token, lang);
                if (term.isNoun() && !uniqueTerms.contains(term)) {
                    nouns++;
                    
                    Map<String, Score> result = calculateScore(term);
                    //uniqueTerms.add(term);

                    if(result.size() == 0) {
                    	continue;
                    }
                    
                    for(int k = 0; k < topKPerTerm; k++) {
                    	if(result.size() <= k) {
                    		break;
                    	}
                    	String topLabelIri = (String) result.keySet().toArray()[k];
                    	String topLabelCode = topLabelIri.split("_")[1];
                    	Double topScore = result.get(result.keySet().toArray()[k]).totalScore;
                        
                    	if(!result.containsKey(topLabelCode)) {
                    		results.put(topLabelCode, topScore);	
                    	}
                    }
                    
                }
            }
            
            sb.append("\n");
            sb.append("Total nouns: ");
            sb.append(nouns);
            sb.append("\n\n\n");
                                    
            jcas.reset();
            
            return results;
        }
        catch (AnalysisEngineProcessException | ResourceInitializationException e) {
            e.printStackTrace();
            return null;
        }

    }
    
    
    /**
     * This an implementation of the classifier that returns the top K 
     * labels per requirement
     *  
     * @param requirement
     * @return classifications
     * @throws IOException 
     */
    public Map<String, Double> classifyRequirementWithTopK(Requirement requirement, int topKPerRequirement,
    		BufferedWriter writer) throws IOException {
    	
    	Map<String, Double> results = new LinkedHashMap<String, Double>();
    	String textToClassify =  requirement.getText(lang)  + " " + requirement.getDocumentTitle(lang) + " " 
    	+ requirement.getSectionTitlesString(lang);
    	
    	jcas.setDocumentLanguage(NLP.getLanguageString(lang));
        jcas.setDocumentText(textToClassify);
     
        StringBuilder sb = new StringBuilder();
        
        sb.append("-----------");
        sb.append(requirement.getReqId());
        sb.append("-----------\n\n");
        sb.append(requirement.getText(lang));
        sb.append("\nAnnotations (");
        sb.append(csTable);
        sb.append("): ");
        sb.append(requirement.getAnnotationInfo(cs, lang, csTable));
        sb.append("\n\n");
        
        try {
            SimplePipeline.runPipeline(jcas, NLP.baseAnalysisEngine());
            
            /* For some reason, we get Tokens with different POS (and possibly other varying 
             * features) JCas with select. Hence, we keep track of the terms we are interested in 
             * (nouns). Note that the hash function of Term does not consider the POS value since
             * it seems that a term can be tagged with different noun POS tags. In this way, we
             * avoid that those terms appear twice. 
             */
            Set<Term> uniqueTerms = new HashSet<>();
            
            //count unique nouns
            int nouns = 0;
            for (Token token : JCasUtil.select(jcas, Token.class)) {
                Term term = new Term(token, lang);
                if (term.isNoun() && !uniqueTerms.contains(term)) {
                    nouns++;
                    
                    // the following line was commented out, i don't know why
                    uniqueTerms.add(term);
                }
            }
            
            //calculate topK per term
            int topKPerTerm = topKPerRequirement / nouns;
            topKPerTerm = Math.max(topKPerTerm, 1);
            log.info("TopK: " + topKPerRequirement + ", nouns: " +  nouns + ", topk per term: " + topKPerTerm);
            //classify
            for (Term term : uniqueTerms) {
                Map<String, Score> result = calculateScore(term);
                if(result.size() == 0) {
                	continue;
                }
                
                // select topK per term
                for(int k = 0; k < topKPerTerm; k++) {
                	if(result.size() <= k) {
                		break;
                	}
                	String topLabelIri = (String) result.keySet().toArray()[k];
                	String topLabelCode = topLabelIri.split("_")[1];
                	Double topScore = result.get(result.keySet().toArray()[k]).totalScore;
                    
                	if(!result.containsKey(topLabelCode)) {
                		results.put(topLabelCode, topScore);	
                	}
                }
                
                //Write terms and scores
                sb.append("Noun ");
                sb.append(nouns);
                sb.append(" (lemmatized): ");
                sb.append(term.getLemma());
                sb.append("\n");
                
                for (Map.Entry<String, Score> e : result.entrySet()) {
                    String iri = e.getKey();
                    Score score = e.getValue();
                    
                    sb.append("\t");
                    sb.append(iri);
                    sb.append(": ");
                    sb.append(score.getExplanation());
                    sb.append("\n");
                }
            }
            
            sb.append("\n");
            sb.append("Total nouns: ");
            sb.append(nouns);
            sb.append("\n\n\n");
            
            writer.write(sb.toString());
            jcas.reset();
            
            return results;
        }
        catch (AnalysisEngineProcessException | ResourceInitializationException e) {
            e.printStackTrace();
            return null;
        }

    }
    
    private void classify(Requirement requirement) 
    {
        jcas.setDocumentLanguage(NLP.getLanguageString(lang));
        jcas.setDocumentText(requirement.getText(lang));
     
        StringBuilder sb = new StringBuilder();
        
        sb.append("-----------");
        sb.append(requirement.getReqId());
        sb.append("-----------\n\n");
        sb.append(requirement.getText(lang));
        sb.append("\nAnnotations (");
        sb.append(csTable);
        sb.append("): ");
        sb.append(requirement.getAnnotationInfo(cs, lang, csTable));
        sb.append("\n\n");
        
        try {
            SimplePipeline.runPipeline(jcas, NLP.baseAnalysisEngine());
            
            /* For some reason, we get Tokens with different POS (and possibly other varying 
             * features) JCas with select. Hence, we keep track of the terms we are interested in 
             * (nouns). Note that the hash function of Term does not consider the POS value since
             * it seems that a term can be tagged with different noun POS tags. In this way, we
             * avoid that those terms appear twice. 
             */
            Set<Term> uniqueTerms = new HashSet<>();
            
            int nouns = 0;
            for (Token token : JCasUtil.select(jcas, Token.class)) {
                Term term = new Term(token, lang);
                if (term.isNoun() && !uniqueTerms.contains(term)) {
                    nouns++;
                    
                    Map<String, Score> result = calculateScore(term);
                    //uniqueTerms.add(term);
                    
                    sb.append("Noun ");
                    sb.append(nouns);
                    sb.append(" (lemmatized): ");
                    sb.append(term.getLemma());
                    sb.append("\n");
                    
                    for (Map.Entry<String, Score> e : result.entrySet()) {
                        String iri = e.getKey();
                        Score score = e.getValue();
                        
                        sb.append("\t");
                        sb.append(iri);
                        sb.append(": ");
                        sb.append(score.getExplanation());
                        sb.append("\n");
                    }
                }
            }
            
            sb.append("\n");
            sb.append("Total nouns: ");
            sb.append(nouns);
            sb.append("\n\n\n");
                        
            jcas.reset();
        }
        catch (AnalysisEngineProcessException | ResourceInitializationException e) {
            e.printStackTrace();
        }
    }
    
    private LinkedHashMap<String, Score> calculateScore(Term aTerm)
    {
        Map<String, Score> totalScore = new HashMap<>();

        // Ensure that the total score is in the range [0..1]
        double scalingFactor = 1.0 / predictors.size();

        for (IPredictor predictor : predictors) {
            Map<String, Double> intermediateScore = predictor.score(aTerm);
            intermediateScore.forEach((key, value) -> {
                value = value * scalingFactor;

                if (totalScore.containsKey(key)) {
                    totalScore.get(key).add(predictor.getName(), value);
                }
                else {
                    Score score = new Score();
                    score.add(predictor.getName(), value);
                    totalScore.put(key, score);
                }
            });
        }

        return totalScore.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry
                        .comparingByValue((e1, e2) -> e1.totalScore.compareTo(e2.totalScore))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                        LinkedHashMap::new));

    }

    private class Score
    {
        private Map<String, Double> components;
        private Double totalScore;

        public Score()
        {
            components = new HashMap<>();
            totalScore = new Double(0);
        }

        public void add(String aPredictorName, Double aScore)
        {
            components.put(aPredictorName, aScore);
            totalScore += aScore;
        }
        
        public Double getAverageScore() 
        {
            return totalScore / components.size();
        }

        public String getExplanation()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Total score: ");
            sb.append(totalScore);
            sb.append(" / Predictors: ");
            sb.append(components.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .map(e -> String.format("%s: %.2f", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(" | ")));
            
            return sb.toString();
        }
    }

}
