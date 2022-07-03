package se.bth.serl.flatclassifier.classificationsystem;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bth.serl.flatclassifier.utils.NLP.Language;

public class CSReaderFactory
{
    private static final Logger log = LoggerFactory.getLogger(CSReaderFactory.class);
    
    public static Optional<CSReader> getReader(String csname, String csRawData, String language)
    {
        CSReader csr = null;
        File csFile = new File(csRawData);
               
        if (!csFile.exists()) {
            log.error("Classification system data file does not exist: " + csRawData);
        } else {
            Optional<Language> lang = getLanguage(language);
            if (lang.isEmpty())  {
                log.error("Language not defined.");
            } else {
                if (csname.equals("SB11"))
                    csr = new SB11Reader(csFile, lang.get());
                else if (csname.equals("COCLASS"))
                    csr = new CoClassReader(csFile, lang.get());
                else 
                    log.error("Classification system not defined.");
            }
        }
        
        return Optional.ofNullable(csr);
    }
    
    private static Optional<Language> getLanguage(String language)
    {
        Language lang = null;
        
        if (language.equals("SV"))
            lang = Language.SV;
        else if (language.equals("EN"))
            lang = Language.EN;
        
        return Optional.ofNullable(lang);
    }
}
