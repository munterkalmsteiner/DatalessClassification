/*
 * Copyright 2019
 * Software Engineering Research Lab
 * Blekinge Institute of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.bth.serl.flatclassifier.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import se.bth.serl.flatclassifier.utils.NLP.Language;

public class Term
{
    private Language lang;
    private String term;
    private String stem;
    private String lemma;
    private String posValue;
    private int begin;
    private int end;
    private static Set<String> nounBlackList = Stream.of(".")
            .collect(Collectors.toCollection(HashSet::new));
    

    public Term(Token aToken, Language lang)
    {
        this.lang = lang;
        // Copying the string values to make sure they continue to exist, even if the CAS, 
        // and therefore the Token, is reset. 
        term = new String(aToken.getText());
        stem = new String(aToken.getStemValue());
        lemma = new String(aToken.getLemmaValue());
        posValue = aToken.getPosValue();
        begin = aToken.getBegin();
        end = aToken.getEnd();
    }

    public Term(String aText, Language lang)
    {
        this.lang = lang;
        term = aText;
        stem = aText;
        lemma = aText;
        posValue = "";
        begin = 0;
        end = aText.length();
    }

    public String getTerm()
    {
        return term.toLowerCase();
    }

    public String getStem()
    {
        return stem.toLowerCase();
    }
    
    public String getLemma()
    {
        return lemma.toLowerCase();
    }
    
    public Language getLanguage() 
    {
        return lang;
    }

    public boolean isNoun()
    {
        boolean result = false;
        
        switch (this.lang) {
        case EN:
            /*
             * https://dkpro.github.io/dkpro-core/releases/2.2.0/docs/tagset-reference.html#tagset-en-ptb-pos
             */
            result = (posValue.equals("NN") || posValue.equals("NNP") || posValue.equals("NNPS")
                    || posValue.equals("NNS") || posValue.equals("NP") || posValue.equals("NPS")) 
                    && !nounBlackList.contains(term);
            break;
        case SV:
            /*
             * Identifiers are from Talbanken76, which is used by the OpenNLP POS tagger
             * https://cl.lingfil.uu.se/~nivre/research/Talbanken05.html
             */
            result = (posValue.equals("PN") || posValue.equals("MN") || posValue.equals("AN")
                    || posValue.equals("VN") || posValue.equals("NN")) && !nounBlackList.contains(term);
        default:
            break;
        }
        
        return result;
    }

    @Override
    public String toString()
    {
        return "Term [term=" + term + ", stem=" + stem + ", lemma=" + lemma + ", posValue=" 
                + posValue + ", begin=" + begin + ", end=" + end + "]";
    }

    /*
     * We deliberately do not include posValue in the calculation as we want Terms with different 
     * noun POS tags be recognized as equal (see also comment in CoClassLinker).
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + begin;
        result = prime * result + end;
        result = prime * result + ((stem == null) ? 0 : stem.hashCode());
        result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
        result = prime * result + ((term == null) ? 0 : term.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Term)) {
            return false;
        }
        Term other = (Term) obj;
        if (begin != other.begin) {
            return false;
        }
        if (end != other.end) {
            return false;
        }
        if (stem == null) {
            if (other.stem != null) {
                return false;
            }
        }
        else if (!stem.equals(other.stem)) {
            return false;
        }
        if (lemma == null) {
            if (other.lemma != null) {
                return false;
            }
        }
        else if (!lemma.equals(other.lemma)) {
            return false;
        }
        if (term == null) {
            if (other.term != null) {
                return false;
            }
        }
        else if (!term.equals(other.term)) {
            return false;
        }
        return true;
    }
}
