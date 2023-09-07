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

package se.bth.serl.word.classifier.predictor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.deeplearning4j.models.word2vec.Word2Vec;

import se.bth.serl.word.classifier.classificationsystem.CSObject;
import se.bth.serl.word.classifier.utils.NLP;
import se.bth.serl.word.classifier.utils.Term;

public class Word2VecPredictor
    extends PredictorBase
{
    private static Map<String, Collection<String>> wordsNearestCache = null;
    private static Map<String, Double> similarityCache = null;
    private Word2Vec w2vModel;
    private final int numSimilarWords = 10;

    public Word2VecPredictor(Map<String, List<CSObject>> acsModel, String acsTable, Word2Vec aW2vModel)
    {
        super(acsModel, acsTable);
        if (wordsNearestCache == null) {
            wordsNearestCache = new HashMap<>();
        }
        if (similarityCache == null) {
            similarityCache = new HashMap<>();
        }
        w2vModel = aW2vModel;
    }

    @Override
    public String getName()
    {
        return "Word2Vec predictor";
    }

    @Override
    public Map<String, Double> score(Term aTerm)
    {
        Map<String, Double> result = new HashMap<>();
        mergeResults(result, aTerm);
     
        //no similar term to aTerm was found in CoClass, hence try decompounding
        /*if (result.size() == 0) { 
            List<Term> components = decompound(aTerm);
            components.forEach(c -> {
                mergeResults(result, c.getTerm());
            });
        }*/
   
        return result;
    }
    
    private void mergeResults(Map<String, Double> aResult, Term aTerm) {
        Map<String, List<CSObject>> hits = new HashMap<>();
        
        String aWord = aTerm.getTerm();
        
        Collection<String> similarWords = wordsNearestCache.get(aWord);
        if (similarWords == null) {
            similarWords = w2vModel.wordsNearest(aWord, numSimilarWords);
            wordsNearestCache.put(aWord, similarWords);
        }

        for (String similarWord : similarWords) {
            Optional<Term> term = NLP.lemstem(similarWord.toLowerCase(), aTerm.getLanguage());
            if (term.isPresent()) {
                String similarLemma = term.get().getLemma();
                if (!similarLemma.equals(aTerm.getLemma())) {
                    List<CSObject> csObjects = csModel.get(similarLemma);
                    if (csObjects != null) {
                        filtercsObjectsonTable(csObjects);
                        if (csObjects.size() > 0)
                            hits.put(similarWord, csObjects);
                    }
                }
            } 
        }
        
        hits.forEach((similarWord, csObjects) -> {
            int numberOfHits = csObjects.size();
            for (CSObject hit : csObjects) {
                hit.addW2VSimilarWord(similarWord);

                /*
                 * A CoClass object can be associated with several similar words. Hence, we need to
                 * find these in all found CoClass objects, count them and add them to the number of
                 * hits for the current similar word in order to normalize correctly (otherwise, we
                 * may end up with scores > 1).
                 */
                numberOfHits += hits.values().stream().flatMap(List::stream)
                        .collect(Collectors.toList()).stream().filter(p -> p.equals(hit)).count()
                        - 1; // substract 1 as list contains also the hit

                String simKey = aWord + similarWord;
                Double newScore = similarityCache.get(simKey);
                if (newScore == null) {                    
                    newScore = new Double(
                            1.0 / numberOfHits * w2vModel.similarity(aWord, similarWord));
                    similarityCache.put(simKey, newScore);
                }

                aResult.merge(hit.getIri(), newScore, (score, increment) -> score += increment);
            }
        });
    }
}
