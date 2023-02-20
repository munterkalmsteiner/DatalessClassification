package edu.illinois.cs.cogcomp.classification.hierarchy.run.ml.requirements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.classification.hierarchy.datastructure.LabelKeyValuePair;

public class RequirementsClassifier {
	public static void printLabelDepthResults (HashMap<Integer,List<LabelKeyValuePair>> labelResultsInDepth) {
		for (int x : labelResultsInDepth.keySet()) {
		//Collections.sort(labelResultsInDepth.get(x), (c1, c2) -> c1.getLabel().compareTo(c2.getLabel()));
			for (LabelKeyValuePair y : labelResultsInDepth.get(x)) {
				System.out.println("key: " + x + ". label: " + y.getLabel() + ". score: " + y.getScore());
			}
		}
	}
	public static LabelKeyValuePair topTwoDiffClassification(HashMap<Integer,List<LabelKeyValuePair>> labelResultsInDepth) {
//		SB11Evaluation.printLabelDepthResults(labelResultsInDepth);
		double prevDiff = 0;
		LabelKeyValuePair result = null;
		LabelKeyValuePair highestScoreLabel;
		LabelKeyValuePair secondHighestScoreLabel;
		for (int depth : labelResultsInDepth.keySet()) {
			if(depth == 0)
				continue;
			
			List<LabelKeyValuePair> labelResultInDepthBranch = labelResultsInDepth.get(depth);
			if(result != null) {
				String resultLabel = result.getLabel();
				labelResultInDepthBranch = labelResultsInDepth.get(depth)
						.stream()
						.filter(e -> e.getLabel().startsWith(resultLabel))
						.collect(Collectors.toList());
			}
			Integer branchSize = labelResultInDepthBranch.size();
			if(branchSize == 0) {
				return result;
			}else {
				highestScoreLabel = labelResultInDepthBranch.get(0);
				if(branchSize == 1) {
					secondHighestScoreLabel = new LabelKeyValuePair("none", 0);	
				}
				else {
					secondHighestScoreLabel = labelResultInDepthBranch.get(1);
				}
			}
		
			double diff = highestScoreLabel.getScore() - secondHighestScoreLabel.getScore();
			if(diff >= prevDiff) {
				result = highestScoreLabel;
				prevDiff = diff;
			}
			else {
				return result;
			}
		}
		return result;
	}
	
	public static LabelKeyValuePair siblingsDiffClassification(HashMap<Integer,List<LabelKeyValuePair>> labelResultsInDepth) {
//		SB11Evaluation.printLabelDepthResults(labelResultsInDepth);
		double prevDiff =  - Double.MAX_VALUE;
		LabelKeyValuePair result = null;
		LabelKeyValuePair highestScoreLabel;
		double sumOfsibilingsScore = 0;
		for (int depth : labelResultsInDepth.keySet()) {
			if(depth == 0)
				continue;
			
			List<LabelKeyValuePair> labelResultInDepthBranch = labelResultsInDepth.get(depth);
			if(result != null) {
				String resultLabel = result.getLabel();
				labelResultInDepthBranch = labelResultsInDepth.get(depth)
						.stream()
						.filter(e -> e.getLabel().startsWith(resultLabel))
						.collect(Collectors.toList());
			}
			Integer branchSize = labelResultInDepthBranch.size();
			if(branchSize == 0) {
				return result;
			}else {
				highestScoreLabel = labelResultInDepthBranch.get(0);
				if(branchSize == 1) {
					sumOfsibilingsScore = 0;	
				}
				else {
					String highestLabel = highestScoreLabel.getLabel();
					sumOfsibilingsScore = labelResultInDepthBranch
							.stream()
							.filter(e -> !e.getLabel().equalsIgnoreCase(highestLabel))
							.mapToDouble(e -> e.getScore())
							.sum();
				}
			}
		
			double diff = highestScoreLabel.getScore() - sumOfsibilingsScore;
			if(diff >= prevDiff) {
				result = highestScoreLabel;
				prevDiff = diff;
			}
			else {
				return result;
			}
		}
		return result;
	}
	
	
	public static List<LabelKeyValuePair> topScorefromAllClasses(HashMap<Integer,List<LabelKeyValuePair>> labelResultsInDepth, int numOfLabels) {
		List<LabelKeyValuePair> result = new ArrayList<LabelKeyValuePair>();
		
		//Flatten classifications
		if(numOfLabels < 1) return null;
		
		for (int depth : labelResultsInDepth.keySet()) {
			if(depth == 0)
				continue;
			
			List<LabelKeyValuePair> labelResultInDepthBranch =  labelResultsInDepth.get(depth);
			
			result.addAll(labelResultInDepthBranch
				.stream()
				.limit(numOfLabels)
				.collect(Collectors.toList()));
		}
		
		return result.stream()
				.sorted((a,b) -> Double.compare(b.getScore(),a.getScore()))
				.limit(numOfLabels)
				.collect(Collectors.toList());
	}
}
