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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Collection;
import java.util.Optional;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasPool;
import org.dkpro.core.corenlp.CoreNlpLemmatizer;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.snowball.SnowballStemmer;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class NLP
{
    private static final int AE_POOL_SIZE = 10;
    private static final int CAS_POOL_SIZE = 10;
    private static AnalysisEngine aBaseEngine = null;
    private static AnalysisEngine aLemStemEngine = null;
    private static CasPool aCasPool = null;
    
    public enum Language { EN, SV };

    public static AnalysisEngine baseAnalysisEngine() throws ResourceInitializationException
    {
        if (aBaseEngine == null) {
            AggregateBuilder builder = new AggregateBuilder();
            builder.add(createEngineDescription(CoreNlpSegmenter.class));
            builder.add(createEngineDescription(CoreNlpPosTagger.class));
            builder.add(createEngineDescription(SnowballStemmer.class));
            builder.add(createEngineDescription(CoreNlpLemmatizer.class));
            aBaseEngine = UIMAFramework.produceAnalysisEngine(
                    builder.createAggregateDescription(), AE_POOL_SIZE, 0);
        }

        return aBaseEngine;
    }
    
    public static Optional<Term> lemstem(String term, Language lang) {
        Optional<Term> aTerm = Optional.empty();
        
        CAS aCas = aCasPool.getCas(0);
        try {
            initLemStemEngine(term);
            aCas.setDocumentLanguage("sv");
            aCas.setDocumentText(term);
            SimplePipeline.runPipeline(aCas, aLemStemEngine);
            
            Collection<Token> tokens = JCasUtil.select(aCas.getJCas(), Token.class);
            
            if (tokens.size() > 0) {
                Optional<Token> token = tokens.stream().findFirst(); 
                if(token.isPresent()) {
                    aTerm = Optional.of(new Term(token.get(), lang));
                }
            }
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (UIMAException e) {
            e.printStackTrace();
        }
        finally {
            aCasPool.releaseCas(aCas);
        }
        
        return aTerm;
    }
    
    private static void initLemStemEngine(String term) throws ResourceInitializationException {
        if (aLemStemEngine == null) {
            AggregateBuilder builder = new AggregateBuilder();
            builder.add(createEngineDescription(CoreNlpSegmenter.class));
            builder.add(createEngineDescription(SnowballStemmer.class));
            builder.add(createEngineDescription(CoreNlpLemmatizer.class));
            aLemStemEngine = UIMAFramework.produceAnalysisEngine(
                    builder.createAggregateDescription(), AE_POOL_SIZE, 0);
            aCasPool = new CasPool(CAS_POOL_SIZE, aLemStemEngine);
        }        
    }
}
