package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.utils.NLP.Language;
/**
 * This is a parser for the data used with the SB11 taxonomy
 * 
 * @author waleed
 *
 */

public class DataParser {
    private static final int SAMPLEIDINDEX = 1;
    private static final int DOCUMENTIDINDEX = 2;
    private static final int DOCTITLEINEDEX_SV = 3;
    private static final int DOCTITLEINEDEX_EN = 4;
    private static final int REQIDINDEX = 5;
    private static final int SECTITLEINDEX_SV = 6;
    private static final int SECTITLEINDEX_EN = 7;
    private static final int REQTEXTINDEX_SV = 8;
    private static final int REQTEXTINDEX_EN = 9;
    private static final int ADVICEINDEX_SV = 10;
    private static final int ADVICEINDEX_EN = 11;
    private static final int SPANINDEX = 12;
    private static final int TABLEINDEX_SB11 = 13;
    private static final int LABELINDEX_SB11 = 14;
    private static final int NAMEINDEX_SB11_EN = 15;
    private static final int NAMEINDEX_SB11_SV = 16;
    private static final int TABLEINDEX_CC = 17;
    private static final int LABELINDEX_CC = 18;
    private static final int NAMEINDEX_CC_EN = 19;
    private static final int NAMEINDEX_CC_SV = 20;

    private static final char DELIMITER = ';';
    
    private final Logger log = LoggerFactory.getLogger(getClass());
	private String filePath;
	private Map<String,Requirement> requirements;
	
	public static void main(String[] args) {
        DataParser p = new DataParser("data/sb11/raw/reqs_with_annotation_for_hc_20220412.csv");
        p.parse();
        for (Requirement r : p.getRequirements()) {
            System.out.println(r.toString());
        }
    }
	
	public DataParser(String filePath) {
		this.filePath = filePath;
		requirements = new HashMap<String,Requirement>();
	}

	public boolean parse() {
		Optional<List<String[]>> rows = readCSVFile();
		if (rows.isPresent()) {
		    for (String[] row : rows.get()) {
	            String reqId = row[REQIDINDEX];
	                
	            if (requirements.containsKey(reqId)) {
	                Requirement req = requirements.get(reqId);
	                parseAnnotations(row, req);
	            } else {
	                Requirement req = new Requirement();
	                req.setSampleId(row[SAMPLEIDINDEX]);
	                req.setDocumentId(row[DOCUMENTIDINDEX]);
	                req.setDocumentTitle(Language.EN, row[DOCTITLEINEDEX_EN]);
	                req.setDocumentTitle(Language.SV, row[DOCTITLEINEDEX_SV]);
	                req.setReqId(reqId);
	                req.setSectionTitles(Language.EN, row[SECTITLEINDEX_EN]);
	                req.setSectionTitles(Language.SV, row[SECTITLEINDEX_SV]);
	                req.setText(Language.EN, row[REQTEXTINDEX_EN]);
	                req.setText(Language.SV, row[REQTEXTINDEX_SV]);
	                req.setAdvice(Language.EN, row[ADVICEINDEX_EN]);
	                req.setAdvice(Language.SV, row[ADVICEINDEX_SV]);
	                    
	                parseAnnotations(row, req);

	                requirements.put(reqId, req);
	            }
	                
	        }
	        return true; 
		} else {
		    log.error("Parsing of file %s failed", filePath);
		    return false;
		}
	}
	
	private void parseAnnotations(String[] row, Requirement req) 
	{
	    if (row[TABLEINDEX_SB11] != null) {
	        String[] tablesArray = row[TABLEINDEX_SB11].split("#");
	        String[] labelsArray = row[LABELINDEX_SB11].split("#");
	            
	        for (int i = 0; i < tablesArray.length; i++) {
	            req.addAnnotation(createAnnotation(
	                    ClassificationSystem.SB11, 
	                    Language.SV,
	                    row[SPANINDEX], 
	                    tablesArray[i], 
	                    labelsArray[i],
	                    row[NAMEINDEX_SB11_SV], 
	                    null));
	            
	            req.addAnnotation(createAnnotation(
	                    ClassificationSystem.SB11, 
	                    Language.EN,
	                    row[SPANINDEX], 
	                    tablesArray[i], 
	                    labelsArray[i],
	                    row[NAMEINDEX_SB11_EN], 
	                    null));
	        }
	    }
	    
	    if (row[TABLEINDEX_CC] != null) {
	        String[] tablesArray = row[TABLEINDEX_CC].split("#");
	        String[] labelsArray = row[LABELINDEX_CC].split("#");
	            
	        for (int i = 0; i < tablesArray.length; i++) {
	            req.addAnnotation(createAnnotation(
	                    ClassificationSystem.COCLASS, 
	                    Language.SV,
	                    row[SPANINDEX], 
	                    tablesArray[i], 
	                    labelsArray[i],
	                    row[NAMEINDEX_CC_SV], 
	                    null));
	            
	            req.addAnnotation(createAnnotation(
	                    ClassificationSystem.COCLASS, 
	                    Language.EN,
	                    row[SPANINDEX], 
	                    tablesArray[i], 
	                    labelsArray[i],
	                    row[NAMEINDEX_CC_EN], 
	                    null));
	        }
	    }
	}

    private Annotation createAnnotation(ClassificationSystem cs, Language lang, String span, 
            String table, String label, String name, String description) {
        Annotation an = new Annotation(cs, lang);
        an.setSpan(span);
        an.setTable(table);
        an.setLabel(label);
        an.setName(name);
        an.setDescription(description);
        return an;
    }

	private Optional<List<String[]>> readCSVFile() {
		Optional<List<String[]>> results = Optional.empty();

		CSVParser parser = new CSVParserBuilder()
		        .withSeparator(DELIMITER)
		        .withIgnoreQuotations(false)
		        .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
		        .build();
		
		try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
		        .withSkipLines(1)
	            .withCSVParser(parser)
	            .build()) {
		    
    		        results = Optional.of(reader.readAll());
    		        
    		        /* Replace new lines with spaces */
    		        if (results.isPresent()) {
        		        for (String[] r : results.get()) {
        		            for (int i = 0; i < r.length; i++) {
        		                if (r[i] != null)
        		                    r[i] = r[i].replaceAll("\n", " ");
        		            }
        		        }
    		        }
		        }
		        catch (IOException e) {
		            e.printStackTrace();
		        }
		        catch (CsvException e) {
		            e.printStackTrace();
		        }
		
		return results;
	}

	public String getFilePath() {
		return filePath;
	}

	public List<Requirement> getRequirements() {
		return new ArrayList<>(requirements.values());
	}

}
