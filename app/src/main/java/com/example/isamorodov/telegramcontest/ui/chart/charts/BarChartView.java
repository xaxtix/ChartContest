package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.example.isamorodov.telegramcontest.data.BarChartData;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.ui.chart.BarViewData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.charts.BaseChartView;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class BarChartView extends BaseChartView<BarChartData, BarViewData> {

    public BarChartView(Context context) {
        super(context);
        superDraw = true;
    }

    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

            int start = startXIndex - 1;
            if (start < 0) start = 0;
            int end = endXIndex+ 1;
            if (end > chartData.lines.get(0).y.length - 1)
                end = chartData.lines.get(0).y.length - 1;

            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;

                float p = chartData.xPercentage[1] * fullWidth;
                int[] y = line.line.y;
                int j = 0;

                float a = line.alpha / 255f;
                for (int i = start; i <= end; i++) {
                    float xPoint = chartData.xPercentage[i] * fullWidth - offset;
                    float yPercentage = (float) y[i] / currentMaxHeight * a;

                    float yPoint = getMeasuredHeight() - chartBottom - (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT);



                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = yPoint;

                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = getMeasuredHeight() - chartBottom;

                }

                canvas.save();
                canvas.clipRect(viewSizes.chartStart, SIGNATURE_TEXT_HEIGHT, viewSizes.chartEnd, getMeasuredHeight() - chartBottom);
                line.paint.setStrokeWidth(p + dp(0.5f));
                canvas.drawLines(line.linesPath, 0, j, line.paint);
                canvas.restore();
            }
        }
    }

    @Override
    protected void drawPickerChart() {
        super.drawPickerChart();
        int bottom = getMeasuredHeight() - PICKER_PADDING;
        int top = getMeasuredHeight() - viewSizes.pikerHeight - PICKER_PADDING;

        bottomChartCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int nl = lines.size();
        if (chartData != null) {
            for (int k = 0; k < nl; k++) {
                BarViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;

                line.bottomLinePath.reset();

                int n = chartData.xPercentage.length;
                int j = 0;

                float p = chartData.xPercentage[1] * viewSizes.pickerWidth;
                int[] y = line.line.y;

                float a = line.alpha / 255f;

                for (int i = 0; i < n; i++) {
                    if (y[i] < 0) continue;
                    float xPoint = chartData.xPercentage[i] * viewSizes.pickerWidth;
                    float h = ANIMATE_PICKER_SIZES ? pickerMaxHeight : chartData.maxValue;
                    float yPercentage = (float) y[i] / h * a;
                    float yPoint = (1f - yPercentage) * (bottom - top);

                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = yPoint;

                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = getMeasuredHeight() - chartBottom;
                }


                line.paint.setStrokeWidth(p + dp(0.5f));
                bottomChartCanvas.drawLines(line.linesPath, 0, j, line.paint);

            }
        }
    }

    @Override
    protected void drawSelection(Canvas canvas) {

    }

    @Override
    BarViewData createLineViewData(ChartData.Line line) {
        return new BarViewData(line);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawChart(canvas);
        drawBottomLine(canvas);
        tmpN = horizontalLines.size();
        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            drawHorizontalLines(canvas, horizontalLines.get(tmpI));
        }
        drawBottomSignature(canvas);
        drawPicker(canvas);
        drawSelection(canvas);

        super.onDraw(canvas);
    }
}
