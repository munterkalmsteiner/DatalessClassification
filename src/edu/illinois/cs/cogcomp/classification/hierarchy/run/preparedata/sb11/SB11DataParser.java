package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

/**
 * This is a parser for the data used with the SB11 taxonomy
 * 
 * @author waleed
 *
 */

public class SB11DataParser {
	/**
	 * 
	 * @param filePath relative file path of the raw data used with the SB11
	 *                 taxonomy
	 */

	private String filePath;
	private List<Requirement> requirements;
	private String DELIMITER = ";";

	public SB11DataParser(String filePath) {
		this.filePath = filePath;
	}

	public void parse(String lang, String table) {
		requirements = new ArrayList<Requirement>();
		if (!lang.equals("EN") && !lang.equals("SV")) {
			throw new IllegalArgumentException("SB11 data: Wrong language specified");
		}
		List<List<String>> rows = readCSVFile();

		int reqIdIndex = 5;
		int tableIndex = 13;
		int labelIndex = 14;
		// Lang SV
		int reqTextIndex = 8;
		int docTitleIndex = 3;
		int secTitleIndex = 6;
		int adviceIndex = 10;
		

		if (lang.equals("EN")) {
			reqTextIndex += 1;
			docTitleIndex += 1;
			secTitleIndex += 1;
			adviceIndex += 1;
		}

		for (List<String> row : rows) {
			if(row.get(13).strip().contains(table)) {
				String reqId = row.get(5);
				String text = row.get(reqTextIndex);
				String documentTitle = row.get(docTitleIndex);
				String sectionTitles = row.get(secTitleIndex);
				String advice = row.get(adviceIndex);
				String sb11Table = table;
				
				List<String> sb11LabelsList = new ArrayList<String>();
				String[] tempTablesArray = row.get(tableIndex).split("#");
				String[] tempLabelsArray = row.get(labelIndex).split("#");
				
				for(int i = 0; i < tempTablesArray.length; i++) {
					if (tempTablesArray[i].equals(table)) {
						sb11LabelsList.add(tempLabelsArray[i]);
					}
				}
				
				String sb11Labels = sb11LabelsList.stream().collect(Collectors.joining("#"));

				Requirement req = new Requirement(reqId, text, documentTitle, sectionTitles, advice, sb11Labels, sb11Table);
				requirements.add(req);
			}
		}
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
		return requirements;
	}

}
