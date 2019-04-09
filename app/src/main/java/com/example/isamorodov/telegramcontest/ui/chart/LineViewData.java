package com.example.isamorodov.telegramcontest.ui.chart;

import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class LineViewData {

    public final ChartData.Line line;
    public final Paint bottomLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public final Path bottomLinePath = new Path();
    public final Path chartPath = new Path();
    public ValueAnimator animatorIn;
    public ValueAnimator animatorOut;
    public int linesPathBottomSize;

    public float[] linesPath;
    public float[] linesPathBottom;


    public int lineColor;


    public boolean enabled = true;

    public int alpha = 255;

    public LineViewData(ChartData.Line line) {
        this.line = line;

        paint.setStrokeWidth(dpFloat(2));
        paint.setStyle(Paint.Style.STROKE);
      //  paint.setStrokeCap(Paint.Cap.BUTT);
        //if (!LinearChartView.USE_LINES) paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(line.color);

        bottomLinePaint.setStrokeWidth(dpFloat(1));
        bottomLinePaint.setStyle(Paint.Style.STROKE);
        //bottomLinePaint.setStrokeCap(Paint.Cap.ROUND);
        //bottomLinePaint.setStrokeJoin(Paint.Join.ROUND);
        bottomLinePaint.setColor(line.color);

        selectionPaint.setStrokeWidth(dpFloat(10));
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setColor(line.color);


        linesPath = new float[line.y.length << 2];
        linesPathBottom = new float[line.y.length << 2];
    }

    public void updateColors() {
        paint.setColor(ThemeHelper.isDark() ? line.colorDark : line.color);
        bottomLinePaint.setColor(lineColor = ThemeHelper.isDark() ? line.colorDark : line.color);
        selectionPaint.setColor(lineColor);
    }
}
