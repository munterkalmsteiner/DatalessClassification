package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;

public class DataCharacteristics {
	private int documentsCount;
	private int labeledDocumentsCount;
	private int labelsCount;
	private double labelDensity;
	private double labelCardinality;
	private ClassificationSystem cs;
	private String csTable;
	
	public DataCharacteristics(ClassificationSystem cs, String csTable) {
		this.cs = cs;
		this.csTable = csTable;
	}
	
	public int getDocumentsCount() {
		return documentsCount;
	}
	public void setDocumentsCount(int documentsCount) {
		this.documentsCount = documentsCount;
	}
	
	public int getLabeledDocumentsCount() {
		return labeledDocumentsCount;
	}
	
	public void setLabeledDocumentsCount(int labeledDocumentsCount) {
		this.labeledDocumentsCount = labeledDocumentsCount;
	}
	
	public int getLabelsCount() {
		return labelsCount;
	}
	
	public void setLabelsCount(int labelsCount) {
		this.labelsCount = labelsCount;
	}
	
	public double getLabelDensity() {
		return labelDensity;
	}
	
	public void setLabelDensity(double labelDensity) {
		this.labelDensity = labelDensity;
	}
	
	public double getLabelCardinality() {
		return labelCardinality;
	}

	public void setLabelCardinality(double labelCardinality) {
		this.labelCardinality = labelCardinality;
	}
	
	public String getCsTable() {
		return this.csTable;
	}
	
	public ClassificationSystem getCs() {
		return this.cs;
	}




}
