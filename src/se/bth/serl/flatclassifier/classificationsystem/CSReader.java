package se.bth.serl.flatclassifier.classificationsystem;

import java.io.File;
import java.util.List;
import java.util.Map;

import se.bth.serl.flatclassifier.utils.NLP.Language;

public interface CSReader
{
    public Map<String, List<CSObject>> read(File csRawData, Language lang);
}
