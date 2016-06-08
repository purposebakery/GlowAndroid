package com.techlung.android.glow.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.UserType;
import com.techlung.android.glow.notification.NotificationManager;

public class Settings {

	private Activity a;
	private int versionCode;

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

		try {
			PackageInfo pInfo = GlowActivity.getInstance().getPackageManager().getPackageInfo(GlowActivity.getInstance().getPackageName(), 0);
			instance.versionCode = pInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return instance;
	}

	// ------------------- //
	// Settings Properties //
	// ------------------- //

	
	private static final String VERSION_KEY = "VERSION_KEY";
	private int versionProductive = 0;
	
	// ----- //
	// Logic //
	// ----- //
	
	// State Saving and Loading
	private void save() {
		SharedPreferences settings = a.getSharedPreferences(Common.STATE_SHARED_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.clear();

		editor.putInt(VERSION_KEY, versionProductive);
		
		editor.apply();
		
		Log.d("Glow", "Saved Settings");
		
	}

	
	private void load() {
		SharedPreferences settings = a.getSharedPreferences(Common.STATE_SHARED_PREFS, 0);

		versionProductive = settings.getInt(VERSION_KEY, 0);
		
		Log.d("Glow", "Loaded Settings");
	}

	public boolean isFirstStart() {
		load();
		return versionProductive < versionCode;
	}
	
	public void setFirstStart(boolean firstStart) {
		if (firstStart) {
			versionProductive = 0;
		} else {
			versionProductive = versionCode;
		}
		save();
	}


}