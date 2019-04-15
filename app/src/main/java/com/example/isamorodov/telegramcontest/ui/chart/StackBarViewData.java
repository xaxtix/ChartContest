package com.example.isamorodov.telegramcontest.ui.chart;

import android.graphics.Paint;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class StackBarViewData extends LineViewData {


    public final Paint unselectedPaint = new Paint();

    public int blendColor = 0;

    public void updateColors() {
        super.updateColors();
        blendColor = ColorUtils.blend(lineColor,ThemeHelper.getColor(R.attr.card_background),0.3f);
    }

    public StackBarViewData(ChartData.Line line) {
        super(line);
        paint.setStrokeWidth(dpFloat(1));
        paint.setStyle(Paint.Style.STROKE);
        unselectedPaint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(false);
    }
}
