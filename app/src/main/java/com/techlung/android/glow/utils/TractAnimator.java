package com.techlung.android.glow.utils;

import java.util.ArrayList;

import com.techlung.android.glow.model.Tract;

import android.app.Activity;

public class TractAnimator {

	Activity activity;
	
	private static final int POSITION_ARRAY_SIZE_WIDTH_FACTOR = 2;
	
	int screenWidth;
	int screenHeight;
	
	ArrayList<Tract> tracts = new ArrayList<Tract>();
	int amountTracts = 0;

	int positionArraySize;
	float[] positionArrayX;
	float[] positionArrayY;
	
	float[] tractPositions;
	float[] tractSpeeds;

	double[] positionAcceleration;

	public TractAnimator(Activity activity) {
		this.activity = activity;
	}
	
	public void setTracts(ArrayList<Tract> tracts) {
		this.tracts = tracts;
		init();
	}
	
	private void init() {
		screenWidth = ToolBox.getScreenWidthPx(activity);
		screenHeight = ToolBox.getScreenHeightPx(activity);
		amountTracts = tracts.size();

		positionArraySize = screenHeight * POSITION_ARRAY_SIZE_WIDTH_FACTOR;
		
		positionArrayX = new float[positionArraySize];
		positionArrayY = new float[positionArraySize];
		
		for (int i = 0; i < positionArraySize; i++) {
			positionArrayX[i] = (screenWidth / 2);
			positionArrayY[i] = i;
		}
		
		positionAcceleration = new double[positionArraySize];
		positionAcceleration[0] = 0; 
		for (int i = 1; i < positionArraySize; ++i) {
			positionAcceleration[i] = Math.sin(Math.PI * ((double) i / (double) positionArraySize));
		}
		
		// Todo set Seep of tracs to zero
		tractSpeeds = new float[amountTracts];
		for (int i = 0; i < amountTracts; ++i) {
			tractSpeeds[i] = 0;
		}
		
		tractPositions = new float[amountTracts];
		
	}

}
