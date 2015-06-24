package com.techlung.android.glow.model;

import java.util.ArrayList;
import java.util.List;

public class TractPair {
	Tract tractOne;
	Tract tractTwo;
	public Tract getTractOne() {
		return tractOne;
	}
	public void setTractOne(Tract tractOne) {
		this.tractOne = tractOne;
	}
	public Tract getTractTwo() {
		return tractTwo;
	}
	public void setTractTwo(Tract tractTwo) {
		this.tractTwo = tractTwo;
	}
	
	public static ArrayList<TractPair> createPairList(List<Tract> tracts) {
		ArrayList<TractPair> result = new ArrayList<TractPair>();
		
		TractPair pair = new TractPair();
		for (Tract t : tracts) {
			
			if (pair.getTractOne() == null) {
				pair.setTractOne(t);
			} else if (pair.getTractTwo() == null) {
				pair.setTractTwo(t);
				result.add(pair);
				pair = new TractPair();
			}
		}
		
		if (pair.getTractOne() != null) {
			result.add(pair);
		}
		
		return result;
	}
}
