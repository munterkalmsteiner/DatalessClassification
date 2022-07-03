package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.Annotation.ClassificationSystem;
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

    private static final String DELIMITER = ";";
    
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
		List<List<String>> rows = readCSVFile();

		for (List<String> row : rows) {
		    if (row.size() < NAMEINDEX_CC_SV) {
		        log.error("Data row too short: " + row.stream().collect(Collectors.joining("; ")));
		        return false;
		    }
		    
			String reqId = row.get(REQIDINDEX);
				
			if (requirements.containsKey(reqId)) {
			    Requirement req = requirements.get(reqId);
                parseAnnotations(row, req);
            } else {
                Requirement req = new Requirement();
                req.setSampleId(row.get(SAMPLEIDINDEX));
                req.setDocumentId(row.get(DOCUMENTIDINDEX));
                req.setDocumentTitle(Language.EN, row.get(DOCTITLEINEDEX_EN));
                req.setDocumentTitle(Language.SV, row.get(DOCTITLEINEDEX_SV));
                req.setReqId(reqId);
                req.setSectionTitles(Language.EN, row.get(SECTITLEINDEX_EN));
                req.setSectionTitles(Language.SV, row.get(SECTITLEINDEX_SV));
                req.setText(Language.EN, row.get(REQTEXTINDEX_EN));
                req.setText(Language.SV, row.get(REQTEXTINDEX_SV));
                req.setAdvice(Language.EN, row.get(ADVICEINDEX_EN));
                req.setAdvice(Language.SV, row.get(ADVICEINDEX_SV));
                    
                parseAnnotations(row, req);

                requirements.put(reqId, req);
            }
				
		}
		
		return true;
	}
	
	private void parseAnnotations(List<String> row, Requirement req) 
	{
	    String[] tablesArray = row.get(TABLEINDEX_SB11).split("#");
        String[] labelsArray = row.get(LABELINDEX_SB11).split("#");
            
        for (int i = 0; i < tablesArray.length; i++) {
            req.addAnnotation(createAnnotation(
                    ClassificationSystem.SB11, 
                    Language.SV,
                    row.get(SPANINDEX), 
                    tablesArray[i], 
                    labelsArray[i],
                    row.get(NAMEINDEX_SB11_SV), 
                    null));
            
            req.addAnnotation(createAnnotation(
                    ClassificationSystem.SB11, 
                    Language.EN,
                    row.get(SPANINDEX), 
                    tablesArray[i], 
                    labelsArray[i],
                    row.get(NAMEINDEX_SB11_EN), 
                    null));
        }
        
        tablesArray = row.get(TABLEINDEX_CC).split("#");
        labelsArray = row.get(LABELINDEX_CC).split("#");
            
        for (int i = 0; i < tablesArray.length; i++) {
            req.addAnnotation(createAnnotation(
                    ClassificationSystem.COCLASS, 
                    Language.SV,
                    row.get(SPANINDEX), 
                    tablesArray[i], 
                    labelsArray[i],
                    row.get(NAMEINDEX_CC_SV), 
                    null));
            
            req.addAnnotation(createAnnotation(
                    ClassificationSystem.COCLASS, 
                    Language.EN,
                    row.get(SPANINDEX), 
                    tablesArray[i], 
                    labelsArray[i],
                    row.get(NAMEINDEX_CC_EN), 
                    null));
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

	private List<List<String>> readCSVFile() {
		List<List<String>> results = new ArrayList<List<String>>();

		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			List<String[]> r = reader.readAll();
			for (String[] arrays : r) {
				String line = "";
				for (String array : arrays) {
					line += array.replace("\n", " ");
				}
				results.add(Arrays.asList(line.split(this.DELIMITER)));
			}
			// remove header
			results.remove(0);
			return results;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String getFilePath() {
		return filePath;
	}

	public List<Requirement> getRequirements() {
		return new ArrayList<>(requirements.values());
	}

}
