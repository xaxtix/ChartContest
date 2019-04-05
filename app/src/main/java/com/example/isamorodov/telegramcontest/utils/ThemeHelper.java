package com.example.isamorodov.telegramcontest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.TelegramContestApp;

public class ThemeHelper {

    private static ContextThemeWrapper contextThemeWrapper;
    private static TypedValue typedValue = new TypedValue();

    private static int[] themes = {R.style.LightTheme, R.style.DarkTheme};
    public static int currentTheme = R.style.LightTheme;
    private static int currentThemeIndex = 0;

    static SharedPreferences sp = TelegramContestApp.context.getSharedPreferences("theme",Context.MODE_PRIVATE);

    public static void initContext(Context context) {
        currentThemeIndex = sp.getInt("t",0);
        currentTheme = themes[currentThemeIndex];
        contextThemeWrapper = new ContextThemeWrapper(context, currentTheme);

    }

    public static void switchTheme(Activity activity) {
        currentThemeIndex++;
        if(currentThemeIndex >= themes.length){
            currentThemeIndex = 0;
        }
        currentTheme = themes[currentThemeIndex];

        sp.edit().putInt("t",currentThemeIndex).apply();

        activity.setTheme(currentTheme);
        contextThemeWrapper = new ContextThemeWrapper(TelegramContestApp.context, currentTheme);
    }


    public static int getColor(int attrRes) {
        contextThemeWrapper.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    public static boolean isDark() {
        return currentTheme == R.style.DarkTheme;
    }
}
