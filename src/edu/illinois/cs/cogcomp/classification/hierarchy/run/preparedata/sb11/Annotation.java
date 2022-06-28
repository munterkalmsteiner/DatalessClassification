package edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11;

import se.bth.serl.flatclassifier.utils.NLP.Language;

public class Annotation
{
    public enum ClassificationSystem {SB11, COCLASS};
    
    private ClassificationSystem cs;
    private Language lang;
    private String table;
    private String span;
    private String label;
    private String name;
    private String description;
    
    
    public Annotation(ClassificationSystem cs, Language lang) 
    {
        this.cs = cs;
        this.lang = lang;
    }
    
    public ClassificationSystem getClassificationSystem() 
    {
        return this.cs;
    }
    
    public Language getLanguage()
    {
        return this.lang;
    }
    
    public String getTable()
    {
        return table;
    }
    
    public void setTable(String table)
    {
        this.table = table;
    }
    
    public String getSpan()
    {
        return span;
    }
    
    public void setSpan(String span)
    {
        this.span = span;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Annotation [");
        if (cs != null) {
            builder.append("cs=");
            builder.append(cs);
            builder.append(", ");
        }
        if (lang != null) {
            builder.append("lang=");
            builder.append(lang);
            builder.append(", ");
        }
        if (table != null) {
            builder.append("table=");
            builder.append(table);
            builder.append(", ");
        }
        if (span != null) {
            builder.append("span=");
            builder.append(span);
            builder.append(", ");
        }
        if (label != null) {
            builder.append("label=");
            builder.append(label);
            builder.append(", ");
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (description != null) {
            builder.append("description=");
            builder.append(description);
        }
        builder.append("]");
        return builder.toString();
    }
    
    
}
