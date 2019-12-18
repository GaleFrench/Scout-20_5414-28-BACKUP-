package com.pearadox.scout_5414;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */

public class CubeSettingsFrag extends PreferenceFragmentCompat {
    String TAG = "CubeSettingsFrag";        // This CLASS name
    EditTextPreference txt_portal;
    EditTextPreference txt_exchange;
    EditTextPreference txt_Lift1;
    EditTextPreference txt_Lift2;
    EditTextPreference txt_Cubes;
    EditTextPreference txt_Climb;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        Log.e(TAG, "**** Cube Settings  ****");
        Log.w(TAG, " \n  \n");

    }

}
