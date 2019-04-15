package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.StackLinearChartData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.StackLinearViewData;

public class StackLinearChartView<T extends StackLinearViewData> extends BaseChartView<StackLinearChartData, T> {

    public StackLinearChartView(@NonNull Context context) {
        super(context);
        superDraw = true;
        useAlphaSignature = true;
    }

    @Override
    public T createLineViewData(ChartData.Line line) {
        return (T) new StackLinearViewData(line);
    }

    Path ovalPath = new Path();

    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

            for (int k = 0; k < lines.size(); k++) {
                lines.get(k).chartPath.reset();
            }

            canvas.save();

            int transitionAlpha = 255;
            if (transitionMode == TRANSITION_MODE_PARENT) {

                transitionAlpha = (int) ((1f - transitionParams.progress) * 255);
                ovalPath.reset();

                int radiusStart = (viewSizes.chartArea.width() > viewSizes.chartArea.height() ? viewSizes.chartArea.width() : viewSizes.chartArea.height());
                int radiusEnd = (int) ((viewSizes.chartArea.width() > viewSizes.chartArea.height() ? viewSizes.chartArea.height() : viewSizes.chartArea.width()) / 2f);
                float radius = radiusEnd + ((radiusStart - radiusEnd) / 2) * (1 - transitionParams.progress);

                radius *= 1f - transitionParams.progress;
                RectF rectF = new RectF();
                rectF.set(
                        viewSizes.chartArea.centerX() - radius,
                        viewSizes.chartArea.centerY() - radius,
                        viewSizes.chartArea.centerX() + radius,
                        viewSizes.chartArea.centerY() + radius
                );
                ovalPath.addRoundRect(
                        rectF, radius, radius, Path.Direction.CW
                );
                canvas.clipPath(ovalPath);

//                canvas.scale(
//                        1f - transitionParams.progress,
//                        1f - transitionParams.progress ,
//                        viewSizes.chartArea.centerX(), viewSizes.chartArea.centerY()
//                );
//                canvas.rotate(30 *  transitionParams.progress,
//                        viewSizes.chartArea.centerX(), viewSizes.chartArea.centerY()
//                );
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
                line.paint.setAlpha(transitionAlpha);
                if (k == lastEnabled) {
                    canvas.drawColor(line.paint.getColor());
                } else {
                    canvas.drawPath(line.chartPath, line.paint);
                }
                line.paint.setAlpha(255);
            }
            canvas.restore();
            canvas.restore();
        }
    }


    @Override
    protected void drawPickerChart() {
        if (chartData != null) {

            for (int k = 0; k < lines.size(); k++) {
                lines.get(k).chartPath.reset();
            }

            int lastEnabled = -1;
            int n = chartData.xPercentage.length;

            for (int i = 0; i < n; i += 4) {
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
