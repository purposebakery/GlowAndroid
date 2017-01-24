package com.techlung.android.glow.utils;

import android.support.annotation.ColorRes;

import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.ColorTheme;
import com.techlung.android.glow.settings.Preferences;

public class ThemeUtil {
    public static @ColorRes int getBackgroundColorId() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.color.readingBackground_light;
        } else {
            return R.color.readingBackground_dark;
        }
    }
}
