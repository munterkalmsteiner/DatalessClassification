package se.bth.serl.flatclassifier.classificationsystem;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.utils.NLP;
import se.bth.serl.flatclassifier.utils.Term;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class CoClassReader
    extends CSReader
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    
    public static void main(String[] args) {
        CoClassReader r = new CoClassReader(new File("data/coclass/raw/coclass_eng_swe_by_michael.csv"), Language.EN);
        r.read();
    }
    
    public CoClassReader(File csRawData, Language lang) 
    {
        this.csRawData = csRawData;
        this.lang = lang;
    }

    @Override
    public Map<String, List<CSObject>> read()
    {
    	 Map<String, List<CSObject>> lookupTable = new HashMap<>();
         
         try {
             Reader reader = Files.newBufferedReader(csRawData.toPath());
         
         
             CSVParser parser = new CSVParserBuilder()
                     .withSeparator(';')
                     .build();
         
             CSVReader csvreader = new CSVReaderBuilder(reader)
                     .withSkipLines(1)
                     .withCSVParser(parser)
                     .build();
             
             List<String[]> rows = new ArrayList<>();
             rows = csvreader.readAll();
             csvreader.close();
     
             JCas jcas = JCasFactory.createJCas();
         
             int i = 0;
             for (String[] row : rows) {
                 CSObject cso = new CSObject();
                 
                 cso.setTable(row[0]);
                 cso.setCode(row[1]);
                 
                 switch (lang) {
                 case EN:
                     cso.setName(row[2]);
                     cso.setDefinition(row[3]);
                     if(!row[4].isBlank()) {
                    	 cso.setSynonyms(Arrays.asList(row[4].split(",")));	 
                     }
                     break;
                 case SV:
                     cso.setName(row[6]);
                     cso.setDefinition(row[7]);
                     if(!row[8].isBlank()) {
                    	 cso.setSynonyms(Arrays.asList(row[8].split(",")));	 
                     }
                     break;
                 default:
                     break;
                 }
                 
                 jcas.setDocumentText(cso.getText());
                 jcas.setDocumentLanguage(NLP.getLanguageString(lang));
                 cso.setIri(cso.getTable() + "_" + cso.getCode());
                 
                 SimplePipeline.runPipeline(jcas, NLP.baseAnalysisEngine());
                 
                 for (Token token : JCasUtil.select(jcas, Token.class)) {
                     Term term = new Term(token, lang);
                     if (term.isNoun()) {
                         cso.addNoun(token.getLemmaValue().toLowerCase());
                     }
                 }

                 jcas.reset();
              
                 for (String noun : cso.getNouns()) {
                     if (lookupTable.containsKey(noun)) {
                         lookupTable.get(noun).add(cso);
                     }
                     else {
                         List<CSObject> ccobjects = new ArrayList<>();
                         ccobjects.add(cso);
                         lookupTable.put(noun, ccobjects);
                     }
                 }

                 i++;
                 log.info("Analyzed {}/{} CoClass objects.", i, rows.size());
             }
         } catch (IOException | CsvException | CASException| ResourceInitializationException | 
                 AnalysisEngineProcessException e) {
             e.printStackTrace();
         }
         
         log.info("Found {} nouns in SB11.", lookupTable.size());
         
         return lookupTable;        
    }
    
    @Override
    public ClassificationSystem getClassificationSystem()
    {
        return ClassificationSystem.COCLASS;
    }

}
