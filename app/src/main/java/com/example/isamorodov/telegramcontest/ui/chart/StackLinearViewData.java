package com.example.isamorodov.telegramcontest.ui.chart;

import android.graphics.Paint;

import com.example.isamorodov.telegramcontest.data.ChartData;

public class StackLinearViewData extends LineViewData {

    public StackLinearViewData(ChartData.Line line) {
        super(line);

        paint.setStyle(Paint.Style.FILL);
    }
}
