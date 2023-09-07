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

import java.util.Map;

import se.bth.serl.word.classifier.utils.Term;


public interface IPredictor
{
    public String getName();

    /**
     * Calculates a score for the given term. The returned score must be between 0 and 1. 
     * An exception is if the predictor wants to indicate that the suggestion should be removed
     * altogether. Then the predictor can set a score of Double.NEGATIVE_INFINITY. Returns a
     * map with the key being an IRI in the knowledge base and the value the calculated score.
     */
    public Map<String, Double> score(Term aTerm);
}
