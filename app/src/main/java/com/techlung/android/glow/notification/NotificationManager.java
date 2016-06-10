package com.techlung.android.glow.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.NotificationFrequency;
import com.techlung.android.glow.enums.NotificationWeekday;
import com.techlung.android.glow.settings.Preferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NotificationManager {

    public static void setNextNotification(Context context, boolean showToast) {
        if (!Preferences.isNotificationEnabled()) {
            return;
        }

        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long nextNotificationTime = getNextNotificationTime();

        alarmManager.set(AlarmManager.RTC, nextNotificationTime, pendingIntent);
        if (showToast) {
            toastNextNotificationTime(nextNotificationTime, context);
        }
    }

    private static void toastNextNotificationTime(long time, Context context) {
        DateFormat format = new SimpleDateFormat("EEEE dd.MM.yyyy HH:mm", Locale.GERMAN);
        Date date = new Date();
        date.setTime(time);

        Toast.makeText(context, context.getString(R.string.pref_notification_toast) + "\n" + format.format(date), Toast.LENGTH_LONG).show();
    }

    private static long getNextNotificationTime() {
        Calendar day = new GregorianCalendar();
        day.setTime(new Date());

        NotificationFrequency frequency = Preferences.getNotificationFrequency();
        int hour = Preferences.getNotificationTimeHour();
        int minute = Preferences.getNotificationTimeMinute();
        NotificationWeekday weekday = Preferences.getNotificationWeekday();

        // set day
        if (frequency == NotificationFrequency.WEEKLY) {
            int weekdayGregorianCalendar = Calendar.SUNDAY;
            switch (weekday) {
                case SUNDAY:
                    weekdayGregorianCalendar = Calendar.SUNDAY;
                    break;
                case MONDAY:
                    weekdayGregorianCalendar = Calendar.MONDAY;
                    break;
                case TUESDAY:
                    weekdayGregorianCalendar = Calendar.TUESDAY;
                    break;
                case WEDNESDAY:
                    weekdayGregorianCalendar = Calendar.WEDNESDAY;
                    break;
                case THURSDAY:
                    weekdayGregorianCalendar = Calendar.THURSDAY;
                    break;
                case FRIDAY:
                    weekdayGregorianCalendar = Calendar.FRIDAY;
                    break;
                case SATURDAY:
                    weekdayGregorianCalendar = Calendar.SATURDAY;
                    break;
            }
            day.set(Calendar.DAY_OF_WEEK, weekdayGregorianCalendar);
        }

        // Set time of day
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        Date dayNowTemp = new Date();
        if (dayNowTemp.after(day.getTime())) {
            if (frequency == NotificationFrequency.WEEKLY) {
                day.add(Calendar.HOUR, 168); // one week in hours
            } else if (frequency == NotificationFrequency.DAILY) {
                day.add(Calendar.HOUR, 24); // one day in hours
            }
        }

        return day.getTimeInMillis();
    }}
