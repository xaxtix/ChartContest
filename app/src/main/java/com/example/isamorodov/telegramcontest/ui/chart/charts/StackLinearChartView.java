package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.StackLinearChartData;
import com.example.isamorodov.telegramcontest.ui.chart.ChartHeaderView;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.StackLinearViewData;

public class StackLinearChartView extends BaseChartView<StackLinearChartData, StackLinearViewData> {

    public StackLinearChartView(@NonNull Context context) {
        super(context);
        superDraw = true;
    }

    @Override
    StackLinearViewData createLineViewData(ChartData.Line line) {
        return new StackLinearViewData(line);
    }

    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

            for (int k = 0; k < lines.size(); k++) {
                lines.get(k).chartPath.reset();
            }

            int lastEnabled = -1;

            int start = startXIndex - 1;
            if (start < 0) start = 0;
            int end = endXIndex + 1;
            if (end > chartData.lines.get(0).y.length - 1)
                end = chartData.lines.get(0).y.length - 1;
            for (int i = start; i <= end; i++) {
                float stackOffset = 0;
                float sum = 0;

                for (int k = 0; k < lines.size(); k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0) continue;
                    sum += line.line.y[i] * (line.alpha / 255f);
                    lastEnabled = k;
                }

                for (int k = 0; k < lines.size(); k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0 || lastEnabled == k) continue;

                    float p = chartData.xPercentage[1] * fullWidth;
                    int[] y = line.line.y;

                    if (y[i] < 0) continue;
                    float xPoint = chartData.xPercentage[i] * fullWidth - offset;
                    float yPercentage = (float) y[i] * (line.alpha / 255f) / sum;

                    float height = (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT) * (line.alpha / 255f);

                    float yPoint = getMeasuredHeight() - chartBottom - height - stackOffset;

                    if (i == start) {
                        line.chartPath.moveTo(0, getMeasuredHeight() - chartBottom);
                    }

                    line.chartPath.lineTo(xPoint, yPoint);

                    if (i == end) {
                        line.chartPath.lineTo(getMeasuredWidth(), getMeasuredHeight() - chartBottom);
                    }

                    stackOffset += height;
                }
            }


            canvas.save();
            canvas.clipRect(viewSizes.chartStart, SIGNATURE_TEXT_HEIGHT, viewSizes.chartEnd, getMeasuredHeight() - chartBottom);
            for (int k = lines.size() - 1; k >= 0; k--) {
                LineViewData line = lines.get(k);
                if (k == lastEnabled) {
                    canvas.drawColor(line.paint.getColor());
                } else {
                    canvas.drawPath(line.chartPath, line.paint);
                }
            }
            canvas.restore();
        }
    }


    @Override
    protected void drawPickerChart() {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

            for (int k = 0; k < lines.size(); k++) {
                lines.get(k).chartPath.reset();
            }

            int lastEnabled = -1;
            int n = chartData.xPercentage.length;

            for (int i = 0; i < n; i++) {
                float stackOffset = 0;
                float sum = 0;

                for (int k = 0; k < lines.size(); k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0) continue;
                    sum += line.line.y[i] * (line.alpha / 255f);
                    lastEnabled = k;
                }

                for (int k = 0; k < lines.size(); k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0 || lastEnabled == k) continue;

                    int[] y = line.line.y;

                    if (y[i] < 0) continue;
                    float xPoint = chartData.xPercentage[i] * viewSizes.pickerWidth;
                    float yPercentage = (float) y[i] * (line.alpha / 255f) / sum;

                    float height = (yPercentage) * (viewSizes.pikerHeight) * (line.alpha / 255f);

                    float yPoint = viewSizes.pikerHeight - height - stackOffset;

                    if (i == 0) {
                        line.chartPath.moveTo(0, getMeasuredHeight() - chartBottom);
                    }

                    line.chartPath.lineTo(xPoint, yPoint);

                    if (i == n - 1) {
                        line.chartPath.lineTo(getMeasuredWidth(), getMeasuredHeight() - chartBottom);
                    }

                    stackOffset += height;
                }
            }


            bottomChartCanvas.save();
            for (int k = lines.size() - 1; k >= 0; k--) {
                LineViewData line = lines.get(k);
                if (k == lastEnabled) {
                    bottomChartCanvas.drawColor(line.paint.getColor());
                } else {
                    bottomChartCanvas.drawPath(line.chartPath, line.paint);
                }
            }
            bottomChartCanvas.restore();
        }
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

    @Override
    public int findMaxValue(int startXIndex, int endXIndex) {
        return 100;
    }

}
