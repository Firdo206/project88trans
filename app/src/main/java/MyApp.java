package com.example.project88trans;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Matikan DayNight → selalu terang (Light Mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}

