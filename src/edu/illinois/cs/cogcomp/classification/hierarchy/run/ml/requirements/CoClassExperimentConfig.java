package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements.SB11ExperimentConfig.SB11Table;

public class CoClassExperimentConfig {
	public final static String language = "EN";
	public final static String csName = "COCLASS";
	public final static String coClassTaxonomy = "data/coclass/raw/coclass_eng_swe_by_michael.csv";
	public final static String rawData = "data/sb11/raw/reqs_with_annotation_for_hc_20220901.csv";
	public final static String csModelFile = "data/coclass/coClassLookupTable.json";
	// Word2Vec model: http://vectors.nlpl.eu/repository/20/222.zip
	public final static String word2vecmodel = "/home/waleed/dev/local_resources/dataless_classification/word2vec_en_wikipedia2021/model.bin";	
	// Swedish language
	public final static String wikiIndexSV = "/home/waleed/Documents/Waleed's_PhD/DCAT/WP4/external_data/Waleed/SVWikiIndexsvwiki-20220820-original/";
	
	public static enum CoClassTable { 
		Tillgångssystem("Tillgångssystem"),
		GrundfunktionerochKomponenter("Grundfunktioner-och-Komponenter"),
		Konstruktivasystem("Konstruktiva-system");

		private String value;
		CoClassTable(String string) {
			// TODO Auto-generated constructor stub
			value = string;
		}
		
		public String getValue() {
			return value;
		}
		
		public String getValueWithSpaces() {
			return value.replace("-", " ");
		}
		
	};
	
	public final static HashMap<String,Integer> nodesCount = new HashMap<>() {{
		put(csName, 1426);
		put(CoClassTable.Tillgångssystem.getValue(), 250);
		put(CoClassTable.GrundfunktionerochKomponenter.getValue() , 864);
		put(CoClassTable.Konstruktivasystem.getValue() , 312);
	}}; 
	
}
