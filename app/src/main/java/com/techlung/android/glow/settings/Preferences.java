package com.techlung.android.glow.settings;

import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;
import com.techlung.android.glow.enums.NotificationFrequency;
import com.techlung.android.glow.enums.NotificationWeekday;
import com.techlung.android.glow.enums.UserType;

public class Preferences {

    public static final String GENERAL_USER_TYPE = "GENERAL_USER_TYPE";
    public static final String GENERAL_BRIGHT_BACKGROUND = "GENERAL_BRIGHT_BACKGROUND";

    public static final String NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED";
    public static final String NOTIFICATION_FREQUENCY = "NOTIFICATION_FREQUENCY";
    public static final String NOTIFICATION_TIME = "NOTIFICATION_TIME";
    public static final String NOTIFICATION_TIME_HOUR = "NOTIFICATION_TIME_HOUR";
    public static final String NOTIFICATION_TIME_MINUTE = "NOTIFICATION_TIME_MINUTE";
    public static final String NOTIFICATION_WEEKDAY = "NOTIFICATION_WEEKDAY";

    private static boolean isInited = false;

    public static boolean isNotificationEnabled() {
        return Prefs.getBoolean(NOTIFICATION_ENABLED, true);
    }
    public static void setNotificationEnabled(boolean notificationEnabled) {
        Prefs.putBoolean(NOTIFICATION_ENABLED, notificationEnabled);
    }

    public static String getNotificationTime() {
        return Prefs.getString(NOTIFICATION_TIME, "6:00");
    }
    public static void setNotificationTime(String notificationTime) {
        Prefs.putString(NOTIFICATION_TIME, notificationTime);
    }

    public static int getNotificationTimeHour() {
        return Prefs.getInt(NOTIFICATION_TIME_HOUR, 6);
    }
    public static void setNotificationTimeHour(int notificationTimeHour) {
        Prefs.putInt(NOTIFICATION_TIME_HOUR, notificationTimeHour);
    }

    public static int getNotificationTimeMinute() {
        return Prefs.getInt(NOTIFICATION_TIME_MINUTE, 0);
    }
    public static void setNotificationTimeMinute(int notificationTimeMinute) {
        Prefs.putInt(NOTIFICATION_TIME_MINUTE, notificationTimeMinute);
    }

    public static UserType getUserType() {
        return UserType.valueOf(Prefs.getString(GENERAL_USER_TYPE, UserType.DISTRIBUTOR.name()));
    }

    public static void setUserType(UserType userType) {
        Prefs.putString(GENERAL_USER_TYPE, userType.name());
    }

    public static NotificationFrequency getNotificationFrequency() {
        return NotificationFrequency.valueOf(Prefs.getString(NOTIFICATION_FREQUENCY, NotificationFrequency.WEEKLY.name()));
    }

    public static void setNotificationFrequency(NotificationFrequency notificationFrequency) {
        Prefs.putString(NOTIFICATION_FREQUENCY, notificationFrequency.name());
    }

    public static NotificationWeekday getNotificationWeekday() {
        return NotificationWeekday.valueOf(Prefs.getString(NOTIFICATION_WEEKDAY, NotificationWeekday.SATURDAY.name()));
    }

    public static void setNotificationWeekday(NotificationWeekday notificationWeekday) {
        Prefs.putString(NOTIFICATION_WEEKDAY, notificationWeekday.name());
    }

    public static void initPreferences(Context context) {
        if (isInited) {
            return;
        }

        isInited = true;

        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

}
