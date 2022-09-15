package edu.illinois.cs.cogcomp.classification.hierarchy.dataprocess.requirements;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CSTopicHierarchy {
	/**
	 * Test for the SB11 reading and parsing taxonomy
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// String filePath = "./data/sb11/raw/SB11_SV_EN_20220520.csv";
		String filePath = "data/coclass/raw/coclass_eng_swe_by_michael.csv";
		String cs = "CoClass"; // SB11 , CoClass
		CSTopicHierarchy tx = new CSTopicHierarchy(cs, "SV", filePath);
		String label = "1AHB";// "31B";
		String labelName = tx.getTopicHierarchy().get("Tillgångssystem").get(label);
		System.out.println(label + ": " + labelName);

		tx.getTopicHierarchy().keySet().forEach(table -> {
			System.out.println("table " + table + " has " + tx.getTopicHierarchy().get(table).size());
//			tx.getTopicHierarchy().get(table).entrySet().forEach(c-> 
//				System.out.println(c.getKey() + ": " + c.getValue())
//					);
		});
	}

	private Logger log = LoggerFactory.getLogger(CSTopicHierarchy.class);
	private String filePath = "";
	private String cs = "";
	private HashMap<String, LinkedHashMap<String, String>> topicHierarchy = new HashMap<String, LinkedHashMap<String, String>>();
	private LinkedHashMap<String, String> labelLookupMap = new LinkedHashMap<String, String>();

	private String COMMA_DELIMITER = ";";

	public CSTopicHierarchy(String cs, String lang, String filePath) {
		this.filePath = filePath;
		this.cs = cs;
		List<List<String>> classifications = this.readCSVFile();
		this.readLabels(classifications, lang);
	}

	public HashMap<String, LinkedHashMap<String, String>> getTopicHierarchy() {
		return topicHierarchy;
	}

	public LinkedHashMap<String, String> getTopicHierarchy(String table) {
		return topicHierarchy.get(table);
	}

	public String getLabelName(String labelKey) {
		return labelLookupMap.get(labelKey);
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
				results.add(Arrays.asList(line.split(this.COMMA_DELIMITER)));
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

	private void readLabels(List<List<String>> nodes, String lang) throws IllegalArgumentException {
		topicHierarchy = new HashMap<String, LinkedHashMap<String, String>>();

		int tableIndex = 0;
		int labelIndex = 1;
		// SB11
		int labelNameIndex_SB11_EN = 2;
		int labelNameIndex_SB11_SV = 4;
		// CoClass
		int labelNameIndex_CoClass_EN = 2;
		int labelDescriptionIndex_CoClass_EN = 3;
		int labelSynonymsIndex_CoClass_EN = 4;
		int labelNameIndex_CoClass_SV = 5;
		int labelDescriptionIndex_CoClass_SV = 6;
		int labelSynonymsIndex_CoClass_SV = 7;

		for (List<String> node : nodes) {
			// skip empty lines
			if (node.size() > 2) {
				String table = node.get(tableIndex).replace(" ", "");
				String label = node.get(labelIndex).toLowerCase();
				if (!topicHierarchy.containsKey(table)) {
					topicHierarchy.put(table, new LinkedHashMap<String, String>());
				}
				if (cs.equals("SB11")) {
					String labelName = "";
					if (lang.equals("EN")) {
						labelName = node.get(labelNameIndex_SB11_EN);
					} else if (lang.equals("SV")) {
						labelName = node.get(labelNameIndex_SB11_SV);
					}
					topicHierarchy.get(table).put(label, labelName);
					labelLookupMap.put(label, labelName);
				} else if (cs.equals("CoClass")) {
					String labelName = "";
					String labelDescription = "";
					String labelSynonyms = "";
					if (lang.equals("EN")) {
						labelName = node.get(labelNameIndex_CoClass_EN);
						labelDescription = node.get(labelDescriptionIndex_CoClass_EN);
						labelSynonyms = node.get(labelSynonymsIndex_CoClass_EN);
						
					} else if (lang.equals("SV")) {
						labelName = node.get(labelNameIndex_CoClass_SV);
						if(node.size() > labelDescriptionIndex_CoClass_SV) {
							labelDescription = node.get(labelDescriptionIndex_CoClass_SV);	
						}
						if(node.size() > labelSynonymsIndex_CoClass_SV) {
							labelSynonyms = node.get(labelSynonymsIndex_CoClass_SV);	
						}
					}
					topicHierarchy.get(table).put(label, labelName + ", " + labelDescription + ", " + labelSynonyms);
					labelLookupMap.put(label, labelName);
				}
				
			}
		}
		log.info("read classification system labels");
	}

}
