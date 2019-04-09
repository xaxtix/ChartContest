package com.example.isamorodov.telegramcontest.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

public class BgView extends View {
    public BgView(Context context) {
        super(context);
    }

    public BgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ThemeHelper.getColor(R.attr.window_background));
    }
}
