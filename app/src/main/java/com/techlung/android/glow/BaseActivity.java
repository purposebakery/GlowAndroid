package com.techlung.android.glow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.techlung.android.glow.settings.Preferences;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.initPreferences(this);
    }
}
