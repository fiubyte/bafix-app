package com.fiubyte.bafix.fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fiubyte.bafix.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
    }
}