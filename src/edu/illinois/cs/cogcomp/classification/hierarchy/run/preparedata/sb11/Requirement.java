package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class Requirement {
	private String sampleId;
	private String documentId;
	private Map<Language, String> documentTitle;
	private String reqId;
	private Map<Language, List<String>> sectionTitles;
	private Map<Language, String> text;
	private Map<Language, String> advice;
	private List<Annotation> annotations;
	
	public Requirement() 
	{
	    documentTitle = new HashMap<>();
	    sectionTitles = new HashMap<>();
	    text = new HashMap<>();
	    advice = new HashMap<>();
		annotations = new ArrayList<>();
	}
	
	public String getSampleId() 
	{
        return sampleId;
    }
	
	public void setSampleId(String sampleId)
    {
        this.sampleId = sampleId;
    }
	
	public String getDocumentId() 
	{
        return documentId;
    }
	
	public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }

	public String getDocumentTitle(Language lang) 
	{
		return documentTitle.get(lang);
	}
	
	public void setDocumentTitle(Language lang, String documentTitle)
    {
        this.documentTitle.put(lang, documentTitle);
    }
	
	public String getReqId() 
	{
        return reqId;
    }
	
	public void setReqId(String reqId)
    {
        this.reqId = reqId;
    }

	public List<String> getSectionTitles(Language lang) 
	{
		return sectionTitles.get(lang);
	}
	
	public String getSectionTitlesString(Language lang) 
	{
	    return sectionTitles.get(lang).stream().collect(Collectors.joining("\n"));
	}
	
	public void setSectionTitles(Language lang, String sectionTitles)
    {
        this.sectionTitles.put(lang, Arrays.asList(sectionTitles.split("#")));
    }
	
	public String getText(Language lang) 
	{
        return text.get(lang);
    }
	
	public void setText(Language lang, String text)
    {
        this.text.put(lang, text);
    }

	public String getAdvice(Language lang) 
	{
		return advice.get(lang);
	}
	
	public void setAdvice(Language lang, String advice)
    {
        this.advice.put(lang, advice);
    }

	public List<String> getLabels(ClassificationSystem cs, Language lang, String table) 
	{
	    return annotations.stream()
	            .filter(a -> a.getClassificationSystem().equals(cs))
	            .filter(a -> a.getLanguage().equals(lang))
	            .filter(a -> a.getTable().equals(table))
                .map(a -> a.getLabel())
                .collect(Collectors.toList());
	}
	
	public String getLabelsString(ClassificationSystem cs, Language lang, String table) 
	{
	    return getLabels(cs, lang, table).stream().collect(Collectors.joining(" "));
	}
	
	public String getAnnotationInfo(ClassificationSystem cs, Language lang, String table) 
	{
	    return annotations.stream()
	            .filter(a -> a.getClassificationSystem().equals(cs))
                .filter(a -> a.getLanguage().equals(lang))
                .filter(a -> a.getTable().equals(table))
                .map(a -> a.getSpan() + " (" +  a.getLabel() + " - " + a.getName() + ")")
                .collect(Collectors.joining(", "));
	}

    public void addAnnotation(Annotation an)
    {
        annotations.add(an);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(reqId);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) obj;
        return Objects.equals(reqId, other.reqId);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Requirement [");
        if (sampleId != null) {
            builder.append("sampleId=");
            builder.append(sampleId);
            builder.append(", ");
        }
        if (documentId != null) {
            builder.append("documentId=");
            builder.append(documentId);
            builder.append(", ");
        }
        if (documentTitle != null) {
            builder.append("documentTitle=");
            builder.append(documentTitle);
            builder.append(", ");
        }
        if (reqId != null) {
            builder.append("reqId=");
            builder.append(reqId);
            builder.append(", ");
        }
        if (sectionTitles != null) {
            builder.append("sectionTitles=");
            builder.append(sectionTitles);
            builder.append(", ");
        }
        if (text != null) {
            builder.append("text=");
            builder.append(text);
            builder.append(", ");
        }
        if (advice != null) {
            builder.append("advice=");
            builder.append(advice);
            builder.append(", ");
        }
        if (annotations != null) {
            builder.append(annotations.size());
            builder.append(" annotations=");
            builder.append(annotations.stream()
                    .map( a -> a.toString())
                    .collect(Collectors.joining(", ", "{", "}")));
        }
        builder.append("]");
        return builder.toString();
    }
    
    
}
