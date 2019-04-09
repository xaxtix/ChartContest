package com.example.isamorodov.telegramcontest.ui.chart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.isamorodov.telegramcontest.R;

public class ChartHeaderView extends FrameLayout {
    public ChartHeaderView(@NonNull Context context) {
        super(context);
        init();
    }

    public ChartHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.chart_header,this,true);
    }
}
