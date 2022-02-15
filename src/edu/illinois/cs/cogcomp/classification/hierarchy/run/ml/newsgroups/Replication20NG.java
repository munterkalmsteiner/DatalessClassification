package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.newsgroups;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.newsgroups.CorpusESAConceptualization20NewsGroups;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.newsgroups.DumpConceptTree20NewsGroups;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.newsgroups.TwentyNGIndexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class Replication20NG
{

    public static void main(String[] args) {
        /*
         * Make sure that the following paths in conf/configurations.properties are set correctly:
         * - cogcomp.esa.simple.wikiIndex
         */
        
        DatalessResourcesConfig.initialization();
        
        int numConcepts = 500;
        String rawData20Newsgroups = "data/20newsgroups/raw/";
        String textindex = "data/20newsgroups/textindex/";
        String conceptTreeFile = "data/20newsgroups/output/tree.20newsgroups.simple.esa.concepts.newrefine." + numConcepts;
        String conceptFile = "data/20newsgroups/output/20newsgroups.simple.esa.concepts." + numConcepts;
        
        
        boolean indexRawData = false;
        try {
            indexRawData = Files.list(new File(textindex).toPath())
                    .filter(Files::isRegularFile)
                    .count() == 0;
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if (indexRawData) {
            try {
               TwentyNGIndexer t = new TwentyNGIndexer(rawData20Newsgroups, textindex);
                t.index();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        boolean createConceptTree = !Files.exists(new File(conceptTreeFile).toPath());
        if (createConceptTree) {
            DumpConceptTree20NewsGroups.test20NewsgroupsDataESA(numConcepts, conceptTreeFile);
        }
        
        boolean createConceptFile = !Files.exists(new File(conceptFile).toPath());
        if (createConceptFile) {
            CorpusESAConceptualization20NewsGroups.conceptualizeCorpus(numConcepts, textindex, conceptFile);
        }
        
        ConceptClassificationESAML.test20NGSimpleConcepts(1);
    }
}
