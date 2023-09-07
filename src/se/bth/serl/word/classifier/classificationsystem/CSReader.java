package se.bth.serl.word.classifier.classificationsystem;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.word.classifier.utils.NLP.Language;

public abstract class CSReader
{
	protected String[] htypes = {"bottomup", "topdown"};
	
    protected File csRawData;
    protected Language lang;
    protected String hierarchy;
    
    abstract public Map<String, List<CSObject>> read();
    
    public Language getLanguage() 
    {
        return lang;
    }
    
    public String getHierarchy() {
    	return hierarchy;
    }
    
    public abstract ClassificationSystem getClassificationSystem();
}
