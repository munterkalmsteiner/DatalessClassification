package se.bth.serl.flatclassifier;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.CoClassExperimentConfig;

public class ExperimentConfig {
	String description;
	String taxonomyFile;
	String rawDataFile;
	String textIndex;
	String conceptTreeFile;
	String conceptFile;
	String outputClassificationFile;
	String outputLabelComparisonFile;
	int numOfConcepts;
	int topK;
	String csTable;
	Boolean includeSuperTopic;
	
	
	public Boolean getIncludeSuperTopic() {
		return includeSuperTopic;
	}

	public void setIncludeSuperTopic(Boolean includeSuperTopic) {
		this.includeSuperTopic = includeSuperTopic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaxonomyFile() {
		return taxonomyFile;
	}

	public void setTaxonomyFile(String taxonomyFile) {
		this.taxonomyFile = taxonomyFile;
	}

	public String getRawDataFile() {
		return rawDataFile;
	}

	public void setRawDataFile(String rawDataFile) {
		this.rawDataFile = rawDataFile;
	}

	public String getTextIndex() {
		return textIndex;
	}

	public void setTextIndex(String textIndex) {
		this.textIndex = textIndex;
	}

	public String getConceptTreeFile() {
		return conceptTreeFile;
	}

	public void setConceptTreeFile(String conceptTreeFile) {
		this.conceptTreeFile = conceptTreeFile;
	}

	public String getConceptFile() {
		return conceptFile;
	}

	public void setConceptFile(String conceptFile) {
		this.conceptFile = conceptFile;
	}

	public String getOutputClassificationFile() {
		return outputClassificationFile;
	}

	public void setOutputClassificationFile(String outputClassificationFile) {
		this.outputClassificationFile = outputClassificationFile;
	}

	public String getOutputLabelComparisonFile() {
		return outputLabelComparisonFile;
	}

	public void setOutputLabelComparisonFile(String outputLabelComparisonFile) {
		this.outputLabelComparisonFile = outputLabelComparisonFile;
	}

	public int getNumOfConcepts() {
		return numOfConcepts;
	}

	public void setNumOfConcepts(int numOfConcepts) {
		this.numOfConcepts = numOfConcepts;
	}

	public int getTopK() {
		return topK;
	}

	public void setTopK(int topK) {
		this.topK = topK;
	}

	public String getCsTable() {
		return csTable;
	}

	public void setCsTable(String csTable) {
		this.csTable = csTable;
	}

	public ExperimentConfig () {
		
	}
	
	public ExperimentConfig (String description, String taxonomyFile, String rawDataFile, String textIndex,
			String conceptTreeFile, String conceptFile, String outputClassificationFile,
			String outputLabelComparisonFile, int numOfConcepts, int topK, String csTable, Boolean includeSuperTopic) {
		
		this.description = description;
		this.taxonomyFile = taxonomyFile;
		this.rawDataFile = rawDataFile;
		this.textIndex = textIndex;
		this.conceptTreeFile = conceptTreeFile;
		this.conceptFile = conceptFile;
		this.outputClassificationFile = outputClassificationFile;
		this.outputLabelComparisonFile = outputLabelComparisonFile;
		this.numOfConcepts = numOfConcepts;
		this.topK = topK;
		this.csTable = csTable;
		this.includeSuperTopic = includeSuperTopic;
		
	}
}
