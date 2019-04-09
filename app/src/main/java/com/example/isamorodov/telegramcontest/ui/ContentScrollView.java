package com.example.isamorodov.telegramcontest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.example.isamorodov.telegramcontest.data.ChartData;

import java.util.ArrayList;

public class ContentScrollView extends ScrollView {

    public ArrayList<ChartData.Line> viewLines = new ArrayList<>();

    public static int contentHeight = 0;

    public ContentScrollView(Context context) {
        super(context);
    }

    public ContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentHeight = getMeasuredHeight();
    }
}
