package com.example.isamorodov.telegramcontest.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BackgroundDrawable extends Drawable {

    static int offset = 0;
    Paint p;

    public BackgroundDrawable(int color) {
        p = new Paint();
        p.setColor(color);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        if(offset == -1) return;
        int top = offset;
        if (top < 0) return;
        canvas.drawRect(getBounds().left, top, getBounds().right, getBounds().bottom, p);

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
