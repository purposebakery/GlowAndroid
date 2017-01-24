package com.techlung.android.glow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ToolBox {

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) Math.floor(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics()));
        return px;
    }

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
