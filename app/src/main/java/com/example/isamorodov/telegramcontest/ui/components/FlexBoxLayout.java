package com.example.isamorodov.telegramcontest.ui.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FlexBoxLayout extends FrameLayout {
    public FlexBoxLayout(@NonNull Context context) {
        super(context);
    }

    public FlexBoxLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexBoxLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int currentW = 0;
        int currentH = 0;
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            if (currentW + getChildAt(i).getMeasuredWidth() > getMeasuredWidth()) {
                currentW = 0;
                currentH += getChildAt(i).getMeasuredHeight();
            }
            currentW += getChildAt(i).getMeasuredWidth();
        }

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + currentH);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int currentW = 0;
        int currentH = 0;
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            if (currentW + getChildAt(i).getMeasuredWidth() > getMeasuredWidth()) {
                currentW = 0;
                currentH += getChildAt(i).getMeasuredHeight();
            }

            getChildAt(i).layout(currentW, currentH,
                    currentW + getChildAt(i).getMeasuredWidth(),
                    currentH + getChildAt(i).getMeasuredHeight());

            currentW += getChildAt(i).getMeasuredWidth();
        }
    }
}
