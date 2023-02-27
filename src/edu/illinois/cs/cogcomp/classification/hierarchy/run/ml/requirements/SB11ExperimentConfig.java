package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

public class SB11ExperimentConfig {
	public final static String sb11Taxonomy = "data/sb11/raw/SB11_SV_EN_20220520.csv";
	public final static String csModelFile = "data/sb11/sb11LookupTable.json";
	//public final static String sb11Table = "Alternativtabell"; //Byggdelar, Landskapsinformation, Alternativtabell
	public final static String language = "EN";
	public final static String csName = "SB11";
	
	public static enum SB11Table { Byggdelar,Landskapsinformation, Alternativtabell }
}	