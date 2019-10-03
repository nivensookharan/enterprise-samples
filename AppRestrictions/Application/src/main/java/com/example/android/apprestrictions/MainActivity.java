/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apprestrictions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

/**
 * This is the main user interface of the App Restrictions sample app.  It demonstrates the use
 * of the App Restriction feature, which is available on Android 4.3 and above tablet devices
 * with the multi-user feature.
 * <p>
 * When launched under the primary User account, you can toggle between standard app restriction
 * types and custom.  When launched under a restricted profile, this activity displays app
 * restriction settings, if available.
 * <p>
 * Follow these steps to exercise the feature:
 * 1. If this is the primary user, go to Settings > Users.
 * 2. Create a restricted profile, if one doesn't exist already.
 * 3. Open the profile settings, locate the sample app, and tap the app restriction settings
 * icon. Configure app restrictions for the app.
 * 4. In the lock screen, switch to the user's restricted profile, launch this sample app,
 * and see the configured app restrictions displayed.
 */
public class MainActivity extends AppCompatActivity {

    // Checkbox to indicate whether custom or standard app restriction types are selected.
    private CheckBox mCustomConfig;

    public static final String CUSTOM_CONFIG_KEY = "custom_config";

    private TextView mMultiEntryValue;
    private TextView mChoiceEntryValue;
    private TextView mBooleanEntryValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up  user interface elements.
        setContentView(R.layout.main);

        mCustomConfig = findViewById(R.id.custom_app_limits);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean customChecked = prefs.getBoolean(CUSTOM_CONFIG_KEY, false);
        if (customChecked) mCustomConfig.setChecked(true);

        mMultiEntryValue = findViewById(R.id.multi_entry_id);
        mChoiceEntryValue = findViewById(R.id.choice_entry_id);
        mBooleanEntryValue = findViewById(R.id.boolean_entry_id);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If app restrictions are set for this package, when launched from a restricted profile,
        // the settings are available in the returned Bundle as key/value pairs.
        Bundle restrictionsBundle = null;
        final UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        if (userManager != null) {
            restrictionsBundle = userManager.getApplicationRestrictions(getPackageName());
        }
        if (restrictionsBundle == null) {
            restrictionsBundle = new Bundle();
        }

        // Reads and displays values from a boolean type restriction entry, if available.
        // An app can utilize these settings to restrict its content under a restricted profile.
        final String booleanRestrictionValue =
                restrictionsBundle.containsKey(GetRestrictionsReceiver.KEY_BOOLEAN) ?
                        restrictionsBundle.getBoolean(GetRestrictionsReceiver.KEY_BOOLEAN) + "" :
                        getString(R.string.na);
        mBooleanEntryValue.setText(booleanRestrictionValue);

        // Reads and displays values from a single choice restriction entry, if available.
        final String singleChoiceRestrictionValue =
                restrictionsBundle.containsKey(GetRestrictionsReceiver.KEY_CHOICE) ?
                        restrictionsBundle.getString(GetRestrictionsReceiver.KEY_CHOICE) :
                        getString(R.string.na);
        mChoiceEntryValue.setText(singleChoiceRestrictionValue);

        // Reads and displays values from a multi-select restriction entry, if available.
        final String[] multiSelectValues =
                restrictionsBundle.getStringArray(GetRestrictionsReceiver.KEY_MULTI_SELECT);
        if (multiSelectValues == null || multiSelectValues.length == 0) {
            mMultiEntryValue.setText(getString(R.string.na));
        } else {
            StringBuilder builder = new StringBuilder();
            for (String value : multiSelectValues) {
                builder.append(value);
                builder.append(" ");
            }
            mMultiEntryValue.setText(builder.toString());
        }
    }

    /**
     * Saves custom app restriction to the shared preference.
     * <p>
     * This flag is used by {@code GetRestrictionsReceiver} to determine if a custom app
     * restriction activity should be used.
     */
    public void onCustomClicked(View view) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putBoolean(CUSTOM_CONFIG_KEY, mCustomConfig.isChecked())
                .apply();
    }
}
