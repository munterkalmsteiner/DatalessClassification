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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.bth.serl.word.classifier.classificationsystem.CSObject;
import se.bth.serl.word.classifier.utils.Term;

/**
 * If the token is a noun, we look for CoClass objects that contain this noun. The more frequent the
 * noun in CoClass is, the lower the score.
 */
public class SimpleNounPredictor
    extends PredictorBase
{

    public SimpleNounPredictor(Map<String, List<CSObject>> acsModel, String acsTable)
    {
        super(acsModel, acsTable);
    }

    @Override
    public String getName()
    {
        return "Simple noun predictor";
    }

    @Override
    public Map<String, Double> score(Term aTerm)
    {
        Map<String, Double> result = new HashMap<>();

        List<CSObject> hits = new ArrayList<>(); 
        
        updateHits(hits, aTerm.getLemma());
                
        /*if (hits.size() == 0) {
            List<Term> components = decompound(aTerm);
            components.forEach(term -> {
                updateHits(hits, aTerm.getStem());
            });
        }*/
        
        if (hits.size() > 0) {
            int numberOfHits = hits.size();
            for (CSObject hit : hits) {
                result.put(hit.getIri(), new Double(1.0 / numberOfHits));
            }
        }

        return result;
    }
    
    private void updateHits(List<CSObject> aHits, String value)
    {
        if (csModel.containsKey(value)) {
            List<CSObject> hits = csModel.get(value);
            filtercsObjectsonTable(hits);
            if (hits.size() > 0)
                aHits.addAll(hits);
        }
    }
}
