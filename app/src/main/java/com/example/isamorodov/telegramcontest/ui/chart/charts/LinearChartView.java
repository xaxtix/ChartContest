package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class LinearChartView extends BaseChartView<ChartData,LineViewData> {
    public LinearChartView(Context context) {
        super(context);
    }

    public LinearChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;


            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;


                int j = 0;

                float p = chartData.xPercentage[1] * fullWidth;
                int[] y = p < (line.paint.getStrokeWidth() / 1.5f) ? line.line.ySimple : line.line.y;

                line.chartPath.reset();
                boolean first = true;
                for (int i = startXIndex; i <= endXIndex; i++) {
                    if (y[i] < 0) continue;
                    float xPoint = chartData.xPercentage[i] * fullWidth - offset;
                    float yPercentage = (float) y[i] / currentMaxHeight;
                    float yPoint = getMeasuredHeight() - chartBottom - (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT);

                    if (USE_LINES) {
                        if (j == 0) {
                            line.linesPath[j++] = xPoint;
                            line.linesPath[j++] = yPoint;
                        } else {
                            line.linesPath[j++] = xPoint;
                            line.linesPath[j++] = yPoint;
                            line.linesPath[j++] = xPoint;
                            line.linesPath[j++] = yPoint;
                        }
                    } else {
                        if (first) {
                            first = false;
                            line.chartPath.moveTo(xPoint, yPoint);
                        } else {
                            line.chartPath.lineTo(xPoint, yPoint);
                        }
                    }
                }

                line.paint.setAlpha(line.alpha);
                if (!USE_LINES) canvas.drawPath(line.chartPath, line.paint);
                else canvas.drawLines(line.linesPath, 0, j, line.paint);
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
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;

                line.bottomLinePath.reset();

                int n = chartData.xPercentage.length;
                int j = 0;

                float p = chartData.xPercentage[1] * viewSizes.pickerWidth;
                int[] y = p < (line.paint.getStrokeWidth() / 1.5f) ? line.line.ySimple : line.line.y;

                line.chartPath.reset();
                for (int i = 0; i < n; i++) {
                    if (y[i] < 0) continue;
                    float xPoint = chartData.xPercentage[i] * viewSizes.pickerWidth;
                    float h = ANIMATE_PICKER_SIZES ? pickerMaxHeight : chartData.maxValue;
                    float yPercentage = (float) y[i] / h;
                    float yPoint = (1f - yPercentage) * (bottom - top);

                    if (USE_LINES) {
                        if (j == 0) {
                            line.linesPathBottom[j++] = xPoint;
                            line.linesPathBottom[j++] = yPoint;
                        } else {
                            line.linesPathBottom[j++] = xPoint;
                            line.linesPathBottom[j++] = yPoint;
                            line.linesPathBottom[j++] = xPoint;
                            line.linesPathBottom[j++] = yPoint;
                        }
                    } else {
                        if (i == 0) {
                            line.bottomLinePath.moveTo(xPoint, yPoint);
                        } else {
                            line.bottomLinePath.lineTo(xPoint, yPoint);
                        }
                    }
                }

                line.linesPathBottomSize = j;


                if (!line.enabled && line.alpha == 0) continue;
                line.bottomLinePaint.setAlpha(line.alpha);
                if (USE_LINES)
                    bottomChartCanvas.drawLines(line.linesPathBottom, 0, line.linesPathBottomSize, line.bottomLinePaint);
                else
                    bottomChartCanvas.drawPath(line.bottomLinePath, line.bottomLinePaint);

            }
        }
    }

    @Override
    LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }
}
