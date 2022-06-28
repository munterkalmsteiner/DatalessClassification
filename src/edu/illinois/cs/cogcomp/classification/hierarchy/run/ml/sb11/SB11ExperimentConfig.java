package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.sb11;

public class SB11ExperimentConfig {
	public final static String sb11Taxonomy = "data/sb11/raw/SB11_SV_EN_20220520.csv";
	public final static String rawDataSB11 = "data/sb11/raw/reqs_with_annotation_for_hc_20220412.csv";
	public final static String csModelFile = "data/sb11/sb11LookupTable.json";
	public final static String sb11Table = "Byggdelar";
	public final static String language = "EN";
	public final static String csName = "SB11";
	// Word2Vec model: http://vectors.nlpl.eu/repository/20/222.zip
	public final static String word2vecmodel = "/home/mun/nosync/word2vec/nlpl.eu/english/wikipedia2021/model.bin";	
}