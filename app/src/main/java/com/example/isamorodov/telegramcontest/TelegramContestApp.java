package com.example.isamorodov.telegramcontest;

import android.app.Application;
import android.content.Context;

import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

public class TelegramContestApp extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ThemeHelper.initContext(this);
    }
}
