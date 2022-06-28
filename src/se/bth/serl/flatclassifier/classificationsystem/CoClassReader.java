package se.bth.serl.flatclassifier.classificationsystem;

import java.io.File;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.Annotation.ClassificationSystem;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class CoClassReader
    extends CSReader
{
    
    public CoClassReader(File csRawData, Language lang) 
    {
        this.csRawData = csRawData;
        this.lang = lang;
    }

    @Override
    public Map<String, List<CSObject>> read()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ClassificationSystem getClassificationSystem()
    {
        return ClassificationSystem.COCLASS;
    }

}
