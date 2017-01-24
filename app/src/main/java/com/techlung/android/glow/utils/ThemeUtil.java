package com.techlung.android.glow.utils;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.ColorTheme;
import com.techlung.android.glow.settings.Preferences;

public class ThemeUtil {
    public static
    @ColorRes
    int getBackgroundColorId() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.color.background_light;
        } else {
            return R.color.background_dark;
        }
    }

    public static
    @ColorRes
    int getTextColorId() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.color.text_light;
        } else {
            return R.color.text_dark;
        }
    }

    public static
    @DrawableRes
    int getImagePhone() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.drawable.ic_phone_black_24dp;
        } else {
            return R.drawable.ic_phone_white_24dp;
        }
    }

    public static
    @DrawableRes
    int getImageMail() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.drawable.ic_email_black_24dp;
        } else {
            return R.drawable.ic_email_white_24dp;
        }
    }

    public static
    @DrawableRes
    int getImageWww() {
        if (Preferences.getColorTheme() == ColorTheme.LIGHT) {
            return R.drawable.ic_home_black_24dp;
        } else {
            return R.drawable.ic_home_white_24dp;
        }
    }
}
