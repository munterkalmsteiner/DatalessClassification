package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements;

import java.io.FileWriter;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.word.classifier.utils.NLP.Language;

public class CharacteristicsExtractor {

	public static void main(String[] args) {
		String dataFile = "data/sb11/raw/reqs_with_annotation_for_hc_20220901.csv";
		ClassificationSystem cs = ClassificationSystem.COCLASS;
		String csTable = CoClassExperimentConfig.CoClassTable.Tillgångssystem.toString();
		CharacteristicsExtractor extractor = new CharacteristicsExtractor(cs, csTable, dataFile);
		extractor.print();
		extractor.dump("data/" + cs.toString().toLowerCase() + "/output/data.characteristics." + cs + "." + csTable);

	}

	private ClassificationSystem cs;
	private String csTable;
	private String dataFile;
	private int nodesCount;

	public CharacteristicsExtractor(ClassificationSystem cs, String csTable, String dataFile) {
		this.cs = cs;
		this.csTable = csTable;
		this.dataFile = dataFile;

		if (cs == ClassificationSystem.SB11) {
			this.nodesCount = SB11ExperimentConfig.nodesCount.get(csTable);
		} else if (cs == ClassificationSystem.COCLASS) {
			this.nodesCount = CoClassExperimentConfig.nodesCount.get(csTable);
		}
	}

	public DataCharacteristics calculate() {
		DataParser p = new DataParser("data/sb11/raw/reqs_with_annotation_for_hc_20220412.csv");
		p.parse();

		boolean calculateForCS = cs.toString() == csTable;

		int reqsLabeledWithTable = (int) p.getRequirements().stream()
				.filter(e -> calculateForCS ? e.getLabels(cs, Language.EN).size() > 0
						: e.getLabels(cs, Language.EN, csTable).size() > 0)
				.count();

		int labelsCount = (int) p.getRequirements().stream()
				.map(e -> calculateForCS ? e.getLabels(cs, Language.EN).size()
						: e.getLabels(cs, Language.EN, csTable).size())
				.mapToInt(Integer::intValue).sum();

		double labelDensity = (double) p.getRequirements().stream()
				.map(e -> calculateForCS ? ((double) e.getLabels(cs, Language.EN).size() / (double) nodesCount)
						: ((double) e.getLabels(cs, Language.EN, csTable).size() / (double) nodesCount))
				.mapToDouble(Double::doubleValue).sum() / reqsLabeledWithTable;
		
		double labelCadinality = (double)labelsCount / (double) reqsLabeledWithTable; 

		DataCharacteristics dc = new DataCharacteristics(cs, csTable);
		dc.setDocumentsCount(p.getRequirements().size());
		dc.setLabeledDocumentsCount(reqsLabeledWithTable);
		dc.setLabelsCount(labelsCount);
		dc.setLabelDensity(labelDensity);
		dc.setLabelCardinality(labelCadinality);

		return dc;
	}

	public void print() {
		DataCharacteristics dc = calculate();

		System.out.println("Printing data characteristics for " + dataFile);
		System.out.println("Classification system: " + cs.toString());
		System.out.println("Table: " + csTable);
		System.out.println(dc.getDocumentsCount() + " requirements found");
		System.out.println(dc.getLabeledDocumentsCount() + " requirements labeled");
		System.out.println(dc.getLabelsCount() + " labels instances");
		System.out.println(dc.getLabelDensity() + " label density");
		System.out.println(dc.getLabelCardinality() + " label cadinality");
	}

	public void dump(String outputFile) {
		DataCharacteristics dc = calculate();
		try {
			String separator = "\t";
			FileWriter writer = new FileWriter(outputFile);

			writer.write("Classification System" + separator + "Table" + separator + "DocumentsCount" + separator
					+ "LabeledDocumentsCount" + separator + "LabelsCount" + separator + "LabelDensity" + separator
					+ "LabelCardinality" + separator
					+ "\n\r");

			writer.write(cs + separator + csTable + separator + dc.getDocumentsCount() + separator
					+ dc.getLabeledDocumentsCount() + separator + dc.getLabelsCount() + separator
					+ dc.getLabelDensity() + separator +
					+ dc.getLabelCardinality() + separator +"\n\r");

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
