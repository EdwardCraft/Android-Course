package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));

    }

    /*
        * Attaches a listener so the summary is always updated width the preference value.
        * Also fires the listener once, to initialize the summary(so it shows up before the value
        * is changed.)
        *
        * */
    private void bindPreferenceSummaryToValue(Preference preference){
        // set the listener to watch for values changes.
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if(preference instanceof ListPreference){
            //for list preference, look up the current display value int
            //the preference's 'entries'  list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference)preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }else{
            //for other  preferences, set the summary  to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        return true;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent(){
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

}
