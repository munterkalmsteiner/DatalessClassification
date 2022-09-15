package se.bth.serl.flatclassifier.classificationsystem;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.requirements.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public abstract class CSReader
{
    protected File csRawData;
    protected Language lang;
    
    abstract public Map<String, List<CSObject>> read();
    
    public Language getLanguage() 
    {
        return lang;
    }
    
    public abstract ClassificationSystem getClassificationSystem();
}
