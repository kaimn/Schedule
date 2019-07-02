package com.example.schedule;

import android.app.Application;

import com.example.schedule.utils.SharePreferenceUntil;

/**
 * Created by PC on 2016/9/26.
 */
public class AppService extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharePreferenceUntil.init(this);
    }
}
