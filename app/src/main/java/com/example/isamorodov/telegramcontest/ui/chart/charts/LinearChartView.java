package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.ui.chart.ChartHorizontalLinesData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;

public class LinearChartView extends BaseChartView<ChartData, LineViewData> {
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
    protected void init() {
        useMinHeight = true;
        super.init();

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
                    float yPercentage = ((float) y[i] - currentMinHeight) / (currentMaxHeight - currentMinHeight);
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

                canvas.save();
                float transitionAlpha = 1f;
                if (transitionMode == TRANSITION_MODE_PARENT) {

                    transitionAlpha = transitionParams.progress > 0.5f ? 0 : 1f - transitionParams.progress * 2f;

                    canvas.scale(
                            1 + 2 * transitionParams.progress, 1f,
                            transitionParams.pX, transitionParams.pY
                    );

                } else if (transitionMode == TRANSITION_MODE_CHILD) {

                    transitionAlpha = transitionParams.progress < 0.3f ? 0 : transitionParams.progress;

                    canvas.save();
                    canvas.scale(
                            transitionParams.progress, transitionParams.needScaleY ? transitionParams.progress : 1f,
                            transitionParams.pX, transitionParams.pY
                    );
                }
                line.paint.setAlpha((int) (line.alpha * transitionAlpha));
                if(endXIndex - startXIndex > 100){
                    line.paint.setStrokeCap(Paint.Cap.SQUARE);
                } else {
                    line.paint.setStrokeCap(Paint.Cap.ROUND);
                }
                if (!USE_LINES) canvas.drawPath(line.chartPath, line.paint);
                else canvas.drawLines(line.linesPath, 0, j, line.paint);

                canvas.restore();
            }
        }
    }

    @Override
    protected void drawPickerChart() {
        super.drawPickerChart();
        int bottom = getMeasuredHeight() - PICKER_PADDING;
        int top = getMeasuredHeight() - viewSizes.pikerHeight - PICKER_PADDING;

        bottomChartBitmap.eraseColor(0);
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
    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }
}
