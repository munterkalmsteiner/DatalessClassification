package se.bth.serl.word.classifier.run;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

import se.bth.serl.word.classifier.ExperimentConfig;

public class ResultsWriter {
	
	public static void toCSV(HashMap<String, Double[]> results, HashMap<String, ExperimentConfig> experiments, String basePath, String cs, String method) {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		String date = df.format(new Date());
		
		String filePath = basePath + "." + date + ".csv";
		try (FileWriter fw = new FileWriter(filePath); CSVWriter writer = new CSVWriter(fw)) {
			
			String[] headers = {
					"Id", "#Requirements","CS", "CS - Table", "Nodes per table","Method", "Hierarchy","Recommended labels (K)","Super topic",
					"Description", "μRecall","MRecall","μPrecision", "MPrecision","μF1","MF1", "min", "max", "average"};
			writer.writeNext(headers);
			
			for (String id : experiments.keySet()) {
				ExperimentConfig ex = experiments.get(id);				
				Double[] result = results.get(id);
				String desc = ex.getDescription() + "." + cs + "-" + ex.getCsTable() + "-" + ex.getTopK();
				String[] config = {ex.getDescription(), " ", cs, ex.getCsTable(), "", method, ex.getHierarchy() , String.valueOf(ex.getTopK()), ex.getIncludeSuperTopic() ? "yes": "no", desc};
				String[] configAndResult = Stream.concat(Arrays.stream(config), Arrays.stream(result).map(e -> String.valueOf(e)))
			      .toArray(size -> (new String[size]));
				 
				writer.writeNext(configAndResult);
			}
			System.out.println("Wrote resuls to " + filePath);			

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
}
