package com.yuikibis.chat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created on 15/06/07.
 */
public class PrefFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}