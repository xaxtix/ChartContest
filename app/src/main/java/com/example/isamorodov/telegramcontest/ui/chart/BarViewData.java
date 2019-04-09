package com.example.isamorodov.telegramcontest.ui.chart;

import android.graphics.Paint;

import com.example.isamorodov.telegramcontest.data.ChartData;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class BarViewData extends LineViewData {


    public BarViewData(ChartData.Line line) {
        super(line);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(dpFloat(1));
    }
}
