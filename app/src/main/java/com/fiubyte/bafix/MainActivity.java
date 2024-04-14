package com.fiubyte.bafix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fiubyte.bafix.preferences.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar)); // FIXME: this crashes in some android
        SharedPreferencesManager.initialize(this);
    }
}