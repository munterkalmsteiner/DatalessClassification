package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.util.HashMap;

public class SB11ExperimentConfig {
	public final static String sb11Taxonomy = "data/sb11/raw/SB11_SV_EN_20220520.csv";
	public final static String csModelFile = "data/sb11/sb11LookupTable.json";
	public final static String language = "EN";
	public final static String csName = "SB11";
	public static enum SB11Table { Byggdelar,Landskapsinformation, Alternativtabell }
	
	public final static HashMap<String,Integer> nodesCount = new HashMap<>() {{
		put(csName, 2074);
		put(SB11Table.Byggdelar.toString(), 1183);
		put(SB11Table.Landskapsinformation.toString(), 635);
		put(SB11Table.Alternativtabell.toString(), 256);
	}}; 
	
}	