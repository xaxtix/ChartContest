package com.example.isamorodov.telegramcontest.utils;
import android.content.Context;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.isamorodov.telegramcontest.TelegramContestApp;

public class AndroidUtilities {

    public static Handler bgHandler;
    public static FastOutSlowInInterpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    static {
        HandlerThread ht = new HandlerThread("bg");
        ht.start();
        bgHandler = new Handler(ht.getLooper());
    }

    public static float dpFloat(float value) {
        if (value == 0) {
            return 0;
        }
        return TelegramContestApp.context.getResources().getDisplayMetrics().density * value;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(TelegramContestApp.context.getResources().getDisplayMetrics().density * value);
    }

    public static int dp(Context context, float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
    }
}
