package com.techlung.android.glow.settings;


import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.NotificationFrequency;
import com.techlung.android.glow.notification.NotificationManager;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /*
    private static void bindListPreferenceSummaryToValue(Preference preference, int resourceIdTitles, int resourceIdValues, String currentValue) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        String[] titles = preference.getContext().getResources().getStringArray(resourceIdTitles);
        String[] values = preference.getContext().getResources().getStringArray(resourceIdValues);
        String title = "";
        for (int i = 0; i < values.length; ++i) {
            if (currentValue.equals(values[i])) {
                title = titles[i];
            }
        }

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, title);
        preference.setSummary(title);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(Preferences.GENERAL_USER_TYPE));

            bindPreferenceSummaryToValue(findPreference(Preferences.GENERAL_COLOR_THEME));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // ENABLED
            final Preference preferenceEnabled = findPreference(Preferences.NOTIFICATION_ENABLED);

            // TIME
            final Preference preferenceTime = findPreference(Preferences.NOTIFICATION_TIME);
            preferenceTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    TimePickerDialog tpd = new TimePickerDialog(NotificationPreferenceFragment.this.getActivity(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Preferences.setNotificationTimeHour(hourOfDay);
                            Preferences.setNotificationTimeMinute(minute);

                            Preferences.setNotificationTime(getEntireTime(hourOfDay, minute));
                            preferenceTime.setSummary(Preferences.getNotificationTime());

                        }
                    }, Preferences.getNotificationTimeHour(), Preferences.getNotificationTimeMinute(), true);

                    tpd.show();

                    return false;
                }
            });
            preferenceTime.setSummary(Preferences.getNotificationTime());


            // WEEKDAY
            final Preference preferenceWeekday = findPreference(Preferences.NOTIFICATION_WEEKDAY);
            bindPreferenceSummaryToValue(preferenceWeekday);

            // Changing
            preferenceEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateNewNotificationPreferenceVisibilityState(ViewEnabledStateChanger.ENABLED, newValue);
                    return true;
                }
            });

            // FREQUENCY
            final Preference preferenceFrequency = findPreference(Preferences.NOTIFICATION_FREQUENCY);
            updateFrequencyPreferenceSummary(preferenceFrequency, Preferences.getNotificationFrequency());
            preferenceFrequency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateFrequencyPreferenceSummary(preferenceFrequency, NotificationFrequency.valueOf(newValue.toString()));
                    updateNewNotificationPreferenceVisibilityState(ViewEnabledStateChanger.FREQUENCY, newValue);
                    return true;
                }
            });

            updateNewNotificationPreferenceVisibilityState(ViewEnabledStateChanger.NONE, null);

        }

        ;

        private void updateFrequencyPreferenceSummary(Preference preferenceFrequency, NotificationFrequency frequency) {
            switch (frequency) {
                case DAILY:
                    preferenceFrequency.setSummary(getActivity().getResources().getStringArray(R.array.pref_notifications_frequency_titles)[0]);
                    break;
                case WEEKLY:
                    preferenceFrequency.setSummary(getActivity().getResources().getStringArray(R.array.pref_notifications_frequency_titles)[1]);
                    break;
            }
        }

        private void updateNewNotificationPreferenceVisibilityState(ViewEnabledStateChanger changer, @Nullable Object newValue) {
            final Preference preferenceTime = findPreference(Preferences.NOTIFICATION_TIME);
            final Preference preferenceFrequency = findPreference(Preferences.NOTIFICATION_FREQUENCY);
            final Preference preferenceWeekday = findPreference(Preferences.NOTIFICATION_WEEKDAY);

            boolean enabled = Preferences.isNotificationEnabled();
            NotificationFrequency frequency = Preferences.getNotificationFrequency();

            if (changer == ViewEnabledStateChanger.ENABLED) {
                enabled = (boolean) newValue;
            } else if (changer == ViewEnabledStateChanger.FREQUENCY) {
                frequency = NotificationFrequency.valueOf((String) newValue);
            }

            if (enabled) {
                preferenceFrequency.setEnabled(true);
                preferenceTime.setEnabled(true);
                if (frequency == NotificationFrequency.WEEKLY) {
                    preferenceWeekday.setEnabled(true);
                } else if (frequency == NotificationFrequency.DAILY) {
                    preferenceWeekday.setEnabled(false);
                }
            } else {
                preferenceFrequency.setEnabled(false);
                preferenceWeekday.setEnabled(false);
                preferenceTime.setEnabled(false);
            }
        }

        private String getEntireTime(int hour, int minute) {
            DecimalFormat format = new DecimalFormat("00");
            String hourStr = format.format(hour);
            String minuteStr = format.format(minute);
            return hourStr + ":" + minuteStr;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            NotificationManager.setNextNotification(getActivity(), true);
        }

        public enum ViewEnabledStateChanger {NONE, ENABLED, FREQUENCY}
    }

}
