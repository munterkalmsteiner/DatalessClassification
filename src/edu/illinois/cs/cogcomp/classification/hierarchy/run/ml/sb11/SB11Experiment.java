package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.CorpusESAConceptualizationSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.DumpConceptTreeSB11;
import edu.illinois.cs.cogcomp.classification.hierarchy.run.preparedata.sb11.SB11Indexer;
import edu.illinois.cs.cogcomp.classification.main.DatalessResourcesConfig;

public class SB11Experiment {

	public static void main(String[] args) {
		/*
		 * Make sure that the following paths in conf/configurations.properties are set
		 * correctly: - cogcomp.esa.simple.wikiIndex
		 */

		DatalessResourcesConfig.initialization();
		
		int numConcepts = 500;
		String sb11Taxonomy = "data/sb11/raw/SB11_SV_EN_20220520.csv";
		String rawDataSB11 = "data/sb11/raw/reqs_with_annotation_for_hc_20220412.csv";
		String textindex = "data/sb11/textindex/";
		String conceptTreeFile = "data/sb11/output/tree.sb11.simple.esa.concepts.newrefine."
				+ numConcepts;
		String conceptFile = "data/sb11/output/sb11.simple.esa.concepts." + numConcepts;

		boolean indexRawData = false;
		try {
			indexRawData = Files.list(new File(textindex).toPath()).filter(Files::isRegularFile).count() == 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (indexRawData) {
			try {
				SB11Indexer t = new SB11Indexer(rawDataSB11, textindex);
				t.index();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean createConceptTree = !Files.exists(new File(conceptTreeFile).toPath());
		if (createConceptTree) {
			DumpConceptTreeSB11.testSB11DataESA(numConcepts, conceptTreeFile, sb11Taxonomy);;
		}

		boolean createConceptFile = !Files.exists(new File(conceptFile).toPath());
		if (createConceptFile) {
			CorpusESAConceptualizationSB11.conceptualizeCorpus(numConcepts, textindex, conceptFile);
		}
		
		
		ConceptClassificationESAML.testSB11SimpleConcept(1);
	}
}
