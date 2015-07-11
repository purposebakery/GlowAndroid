package com.techlung.android.glow.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;

public class Common {
	public static final String STATE_SHARED_PREFS = "COM.TECHLUNG.ANDROID.GLOW.STATE_SHARED_PREFS";
	
	public static final String FILE_META = "meta.txt"; // default
	public static final String FILE_CONTACT = "contact.txt"; // default
	public static final String FILE_INFO = "info.html"; // default
	public static final String FILE_CONTENT = "content.html"; // default
	public static final String FILE_ADDITIONAL = "additional.html"; // default
	public static final String FILE_COVER = "cover.png"; // default
	
	public static final String META_TITLE = "title";
	public static final String META_URL = "url";
	
	public static final String CONTACT_EMAIL = "email";
	public static final String CONTACT_PHONE = "phone";
	public static final String CONTACT_WWW = "www";
	public static final String CONTACT_SHOP = "shop";
	public static final String CONTACT_APP_URL = "appurl";

	public static final int VERSION = 7;
	
	public static boolean isXLargeScreen(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
