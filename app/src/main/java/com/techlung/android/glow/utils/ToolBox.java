package com.techlung.android.glow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ToolBox {
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static int convertDpToPixel(int dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int) Math.floor(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics()));
		return px;
	}
	
	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static int convertPixelsToDp(int px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int dp = (int) Math.floor(px / (metrics.densityDpi / 160f));
		return dp;
	}
	
	public static int getMaxNumberOfItemsOnScreen(Activity activity, int itemWidthDp) {
		int widthDp = getScreenWidthDp(activity);
		return widthDp / itemWidthDp;
	}
	
	public static int getScreenWidthDp(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int widthPx = metrics.widthPixels;
		int widthDp = ToolBox.convertPixelsToDp(widthPx, activity);
		return widthDp;
	}
	
	public static int getScreenHeightDp(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int heightPx = metrics.heightPixels;
		int heightDp = ToolBox.convertPixelsToDp(heightPx, activity);
		return heightDp;
	}
	
	public static int getScreenWidthPx(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int widthPx = metrics.widthPixels;
		return widthPx;
	}
	
	public static int getScreenHeightPx(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int heightPx = metrics.heightPixels;
		return heightPx;
	}
}
