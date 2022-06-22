package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.util.List;

public class Requirement {

	private String id;
	private String text;
	private String documentTitle;
	private String sectionTitles;
	private String advice;
	private String sb11Labels;
	private String sb11Table;

	/**
	 * A natural language requirement
	 * 
	 * @param id
	 * @param text
	 * @param documentTitle
	 * @param sectionTitles
	 * @param advice
	 * @param sb11Label
	 */
	public Requirement(String id, String text, String documentTitle, String sectionTitles, String advice,
			String sb11Labels, String sb11Table) {
		this.id = id;
		this.text = text;
		this.documentTitle = documentTitle;
		this.sectionTitles = sectionTitles;
		this.advice = advice;
		this.sb11Labels = sb11Labels;
		this.sb11Table = sb11Table;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public String getSectionTitles() {
		return sectionTitles;
	}

	public String getAdvice() {
		return advice;
	}

	public String getSB11Labels() {
		return sb11Labels;
	}
	
	public String getSb11Table() {
		return sb11Table;
	}
}
