package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.sb11;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11.SB11ExperimentConfig;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class SB11TopicHierarchy {

	/**
	 * Test for the SB11 reading and parsing taxonomy
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = SB11ExperimentConfig.sb11Taxonomy;
		SB11TopicHierarchy tx = new SB11TopicHierarchy("SV", filePath);

		Object firstKey = tx.topicMappingBuilding.keySet().toArray()[1000];
		System.out.print(tx.topicMappingBuilding.get(firstKey));
		System.out.printf(". Key: %s", firstKey);
	}

	private String filePath = "";
	private HashMap<String, HashMap<String, String>> topicHierarchy = new HashMap<String, HashMap<String, String>>();
	private LinkedHashMap<String, String> topicMappingBuilding;
	private LinkedHashMap<String, String> topicMappingLandscape;
	private LinkedHashMap<String, String> topicMappingAlternative;
	private LinkedHashMap<String, String> labelLookupMap = new LinkedHashMap<String, String>();
	
	
	private String COMMA_DELIMITER = ";";

	public SB11TopicHierarchy(String lang, String filePath) {
		this.filePath = filePath;
		List <List<String>> classifications =  this.readCSVFile();
		this.readLabels(classifications, lang);
	}

	public HashMap<String, HashMap<String, String>> getTopicHierarchy() {
		return topicHierarchy;
	}
	
	public LinkedHashMap<String, String> getTopicMappingBuilding() {
		return topicMappingBuilding;
	}
	
	public LinkedHashMap<String, String> getTopicMappingLandscape() {
		return topicMappingLandscape;
	}
	
	public LinkedHashMap<String, String> getTopicMappingAlternative() {
		return topicMappingAlternative;
	}

	public String getLabelName(String labelKey) {
		return labelLookupMap.get(labelKey.toLowerCase());
	}
	
	private List<List<String>> readCSVFile() {
		List<List<String>> results = new ArrayList<List<String>>(); 
		
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			List<String[]> r = reader.readAll();
			for (String[] arrays : r) {
				String line = "";
				for (String array : arrays) {
					line +=  array.replace("\n", " ");
				}
				results.add(Arrays.asList(line.split(this.COMMA_DELIMITER)));
			}
			//remove header
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

	private void readLabels(List<List<String>> classifications, String lang) throws IllegalArgumentException {
		// ensure the maps are empty
		topicMappingBuilding = new LinkedHashMap<String, String>();
		topicMappingLandscape = new LinkedHashMap<String, String>();
		topicMappingAlternative = new LinkedHashMap<String, String>();
		
		int labelId;
		if(lang.equals("EN")) {
			labelId = 2;
		}
		else if (lang.equals("SV")) {
			labelId = 4;
		}
		else {
			throw new IllegalArgumentException("A wrong language is specified for SB11");
		}
		
		for (List<String> classification : classifications) {
			// skip empty lines
			if (classification.size() > 2) {
				String table = classification.get(0);
				String key = classification.get(1);
				String label = classification.get(labelId);
				if(table.equals("Byggdelar")) {
					topicMappingBuilding.put(key, label);
				}
				else if(table.equals("Landskapsinformation")) {
					topicMappingLandscape.put(key, label);
				}
				else if(table.equals("Alternativtabell")) {
					topicMappingAlternative.put(key, label);
				}
				labelLookupMap.put(key.toLowerCase(), label);
			}
		}
		topicHierarchy.put("Byggdelar", topicMappingBuilding);
		topicHierarchy.put("Landskapsinformation", topicMappingLandscape);
		topicHierarchy.put("Alternativtabell", topicMappingAlternative);
		
		System.out.printf("\n read %d Byggdelar classes", topicMappingBuilding.size());
		System.out.printf("\n read %d Landskapsinformation classes", topicMappingLandscape.size());
		System.out.printf("\n read %d Alternativtabell classes \n", topicMappingAlternative.size());
		System.out.println();
	}

}
