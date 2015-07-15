package com.darqueflux.tipulator;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

public class SettingsActivity extends PreferenceActivity {

@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'Security Tipping' preferences, and a corresponding header.
        PreferenceCategory securityTippingHeader = new PreferenceCategory(this);
        securityTippingHeader.setTitle("SecuriTip");
        getPreferenceScreen().addPreference(securityTippingHeader);
        addPreferencesFromResource(R.xml.pref_security_tipping);

        //Add 'Personlization' preferences,
        PreferenceCategory personalizationHeader = new PreferenceCategory(this);
        personalizationHeader.setTitle("Personalize");
        getPreferenceScreen().addPreference(personalizationHeader);
        addPreferencesFromResource(R.xml.pref_personalization);


        //Bindings
        bindSecurityModeListeners();
        bindConstantChangeAmountListeners();
        bindSummaryToValue(findPreference("rounding_style"));
        bindFloatSummaryToValue(findPreference("min_tip_percent"));
        bindFloatSummaryToValue(findPreference("max_tip_percent"));
        bindFloatSummaryToValue(findPreference("resolution_tip_percent"));
    }

    /**
     * Binds the summary to the value of the preference
     */
    private void bindSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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

                } else if (preference instanceof RingtonePreference) {
                    // For ringtone preferences, look up the correct display value
                    // using RingtoneManager.
                    if (TextUtils.isEmpty(stringValue)) {
                        // Empty values correspond to 'silent' (no ringtone).
                        preference.setSummary(R.string.pref_ringtone_silent);

                    } else {
                        Ringtone ringtone = RingtoneManager.getRingtone(
                                preference.getContext(), Uri.parse(stringValue));

                        if (ringtone == null) {
                            // Clear the summary if there was a lookup error.
                            preference.setSummary(null);
                        } else {
                            // Set the summary to reflect the new ringtone display
                            // name.
                            String name = ringtone.getTitle(preference.getContext());
                            preference.setSummary(name);
                        }
                    }

                } else {
                    // For all other preferences, set the summary to the value's
                    // simple string representation.
                    preference.setSummary(stringValue);
                }
                return true;
            }
        });

        // Trigger the listener immediately with the preference's
        // current value.
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void bindFloatSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();

                if((stringValue.length() <= 0) || stringValue.equals("."))
                {
                    return false;
                }

                preference.setSummary(stringValue);

                return true;
            }
        });

        // Trigger the listener immediately with the preference's
        // current value.
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void bindConstantChangeAmountListeners() {
        Preference preference = findPreference("constant_change_amount");

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();

                if(stringValue.length() <= 0)
                {
                    return false;
                }
                else
                {
                    preference.setSummary(stringValue);
                    return true;
                }
            }
        });

        // Trigger the listener immediately with the preference's
        // current value.
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void bindSecurityModeListeners() {
        Preference preference = findPreference("security_mode");

        final Preference prefConstantChangeAmount = findPreference("constant_change_amount");
        final Preference prefRoundingStyle = findPreference("rounding_style");

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
             @Override
             public boolean onPreferenceChange(Preference preference, Object value) {
                 String stringValue = value.toString();
                 ListPreference listPreference = (ListPreference) preference;
                 int index = listPreference.findIndexOfValue(stringValue);

                 Integer modeValue = Integer.valueOf((stringValue.length() >= 0) ? (stringValue) : ("-1"));

                 switch(modeValue)
                 {
                     case 0:
                         prefConstantChangeAmount.setEnabled(false);
                         prefRoundingStyle.setEnabled(false);
                         break;
                     case 1:
                         prefConstantChangeAmount.setEnabled(true);
                         prefRoundingStyle.setEnabled(true);
                         break;
                     case 2:
                         prefConstantChangeAmount.setEnabled(false);
                         prefRoundingStyle.setEnabled(true);
                         break;
                     default:
                         prefConstantChangeAmount.setEnabled(false);
                         prefRoundingStyle.setEnabled(false);
                         break;
                 }

                 // Set the summary to reflect the new value.
                 preference.setSummary(
                         index >= 0
                                 ? listPreference.getEntries()[index]
                                 : null);
                 return true;
             }
            }
        );

        // Trigger the listener immediately with the preference's
        // current value.
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
