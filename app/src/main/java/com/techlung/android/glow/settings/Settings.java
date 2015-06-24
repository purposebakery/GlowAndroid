package com.techlung.android.glow.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class Settings {

	private Activity a;

	private Settings(Activity a) {
		if (a == null) {
			throw new IllegalStateException("No Activity passed");
		}
		this.a = a;
	}
	
	private static Settings instance;
	
	public static Settings getInstance(Activity a) {
		if (instance == null) {
			instance = new Settings(a);
		}
		
		return instance;
	}

	// ------------------- //
	// Settings Properties //
	// ------------------- //

	
	private static final String VERSION_KEY = "VERSION_KEY";
	private int version = 0;
	
	// ----- //
	// Logic //
	// ----- //
	
	// State Saving and Loading
	public void save() {
		SharedPreferences settings = a.getSharedPreferences(Common.STATE_SHARED_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.clear();

		editor.putInt(VERSION_KEY, version);
		
		editor.apply();
		
		Log.d("Glow", "Saved Settings");
		
	}

	
	public void load() {
		SharedPreferences settings = a.getSharedPreferences(Common.STATE_SHARED_PREFS, 0);

		version = settings.getInt(VERSION_KEY, 0);
		
		Log.d("Glow", "Loaded Settings");

	}

	public boolean isFirstStart() {
		return version < Common.VERSION;
	}
	
	public void setFirstStart(boolean firstStart) {
		if (firstStart) {
			version = 0;
		} else {
			version = Common.VERSION; 
		}
	}

	
	
	
}
