package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

public class Requirement {

	private String id;
	private String text;
	private String documentTitle;
	private String sectionTitles;
	private String advice;
	private String sb11Label;

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
			String sb11Label) {
		this.id = id;
		this.text = text;
		this.documentTitle = documentTitle;
		this.sectionTitles = sectionTitles;
		this.advice = advice;
		this.sb11Label = sb11Label;
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
	

	public String getSB11Label() {
		return sb11Label;
	}
}
