package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.DoubleLinearChartData;
import com.example.isamorodov.telegramcontest.ui.chart.ChartHorizontalLinesData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;

import static com.example.isamorodov.telegramcontest.ui.chart.ChartHorizontalLinesData.formatWholeNumber;

public class DoubleLinearChartView extends BaseChartView<DoubleLinearChartData, LineViewData> {
    public DoubleLinearChartView(Context context) {
        super(context);
    }

    public DoubleLinearChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleLinearChartView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                        transitionParams.progress, transitionParams.progress,
                        transitionParams.pX, transitionParams.pY
                );
            }

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
                    float yPercentage = ((float) y[i] * chartData.linesK[k] - currentMinHeight) / (currentMaxHeight - currentMinHeight);
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

                if (endXIndex - startXIndex > 100) {
                    line.paint.setStrokeCap(Paint.Cap.SQUARE);
                } else {
                    line.paint.setStrokeCap(Paint.Cap.ROUND);
                }
                line.paint.setAlpha((int) (line.alpha * transitionAlpha));
                if (!USE_LINES) canvas.drawPath(line.chartPath, line.paint);
                else canvas.drawLines(line.linesPath, 0, j, line.paint);
            }

            canvas.restore();
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

                    float yPercentage = (float) y[i] * chartData.linesK[k] / h;
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
    protected void drawSelection(Canvas canvas) {
        if (selectedIndex < 0 || !legendShowing) return;

        int alpha = (int) (255 * selectionA);


        float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
        float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

        float xPoint = chartData.xPercentage[selectedIndex] * fullWidth - offset;


        selectedLinePaint.setAlpha(alpha);
        canvas.drawLine(xPoint, 0, xPoint, viewSizes.chartArea.bottom, selectedLinePaint);

        tmpN = lines.size();
        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            LineViewData line = lines.get(tmpI);
            if (!line.enabled) continue;
            float yPercentage = ((float) line.line.y[selectedIndex] * chartData.linesK[tmpI] - currentMinHeight) / (currentMaxHeight - currentMinHeight);
            float yPoint = getMeasuredHeight() - chartBottom - (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT);

            line.selectionPaint.setAlpha(alpha);
            selectionBackgroundPaint.setAlpha(alpha);

            canvas.drawPoint(xPoint, yPoint, line.selectionPaint);
            canvas.drawPoint(xPoint, yPoint, selectionBackgroundPaint);
        }
    }

    protected void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;
        int rightIndex = chartData.linesK[0] == 1 ? 1 : 0;
        int leftIndex = (rightIndex + 1) % 2;
        float k = chartData.linesK[rightIndex];

        float transitionAlpha = 1f;
        if (transitionMode == TRANSITION_MODE_PARENT) {
            transitionAlpha = 1f - transitionParams.progress;
        } else if (transitionMode == TRANSITION_MODE_CHILD) {
            transitionAlpha = transitionParams.progress;
        }


        linePaint.setAlpha((int) (a.alpha * 0.1f * transitionAlpha));

        int chartHeight = getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT;

        int textOffset = (int) (SIGNATURE_TEXT_HEIGHT - signaturePaint.getTextSize());
        for (int i = 0; i < n; i++) {
            int y = (int) ((getMeasuredHeight() - chartBottom) - chartHeight * ((a.values[i] - currentMinHeight) / (currentMaxHeight - currentMinHeight)));
            canvas.drawLine(
                    viewSizes.chartStart,
                    y,
                    viewSizes.chartEnd,
                    y,
                    linePaint
            );


            signaturePaint.setColor(lines.get(leftIndex).lineColor);
            signaturePaint.setAlpha((int) (a.alpha * (lines.get(leftIndex).alpha / 255f) * transitionAlpha));

            canvas.drawText(a.valuesStr[i], HORIZONTAL_PADDING, y - textOffset, signaturePaint);
            signaturePaint2.setColor(lines.get(rightIndex).lineColor);
            signaturePaint2.setAlpha((int) (a.alpha * (lines.get(rightIndex).alpha / 255f) * transitionAlpha));
            canvas.drawText(a.valuesStr2[i], getMeasuredWidth() - HORIZONTAL_PADDING, y - textOffset, signaturePaint2);
        }
    }

    protected void drawBottomLine(Canvas canvas) {
        float transitionAlpha = 1f;
        if (transitionMode == TRANSITION_MODE_PARENT) {
            transitionAlpha = 1f - transitionParams.progress;
        } else if (transitionMode == TRANSITION_MODE_CHILD) {
            transitionAlpha = transitionParams.progress;
        }

        linePaint.setAlpha((int) (255 * 0.1f * transitionAlpha));


        signaturePaint.setAlpha((int) (255 * signaturePaintAlpha));
        int y = (getMeasuredHeight() - chartBottom);
        canvas.drawLine(
                viewSizes.chartStart,
                y,
                viewSizes.chartEnd,
                y,
                linePaint);

    }

    @Override
    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }


    public int findMaxValue(int startXIndex, int endXIndex) {
        int max1 = lines.get(0).enabled ? (int) (chartData.lines.get(0).segmentTree.rMaxQ(startXIndex, endXIndex) * chartData.linesK[0]) : 0;
        int max2 = lines.get(1).enabled ? (int) (chartData.lines.get(1).segmentTree.rMaxQ(startXIndex, endXIndex) * chartData.linesK[1]) : 0;
        return Math.max(max1, max2);
    }

    public int findMinValue(int startXIndex, int endXIndex) {
        int min1 = lines.get(0).enabled ? (int) (chartData.lines.get(0).segmentTree.rMinQ(startXIndex, endXIndex) * chartData.linesK[0]) : 0;
        int min2 = lines.get(1).enabled ? (int) (chartData.lines.get(1).segmentTree.rMinQ(startXIndex, endXIndex) * chartData.linesK[1]) : 0;
        return Math.min(min1, min2);
    }

    protected void updateBottomMaxHeight() {
        if (!ANIMATE_PICKER_SIZES) return;
        if (lines.get(0).enabled) {
            super.updateBottomMaxHeight();
            return;
        }

        int max = 0;
        for (LineViewData l : lines) {
            if (l.enabled && l.line.maxValue > max) max = l.line.maxValue;
        }

        max = (int) (max * chartData.linesK[1]);

        if (max > 0 && max != animatedToPickerMaxHeight) {
            animatedToPickerMaxHeight = max;
            if (pickerAnimator != null) pickerAnimator.cancel();

            pickerAnimator = createAnimator(pickerMaxHeight, animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pickerMaxHeight = (float) animation.getAnimatedValue();
                    invalidatePickerChart = true;
                    invalidate();
                }
            });
            pickerAnimator.start();
        }
    }

    protected ChartHorizontalLinesData createHorizontalLinesData(int newMaxHeight, int newMinHeight) {
        int rightIndex = chartData.linesK[0] == 1 ? 1 : 0;
        float k = chartData.linesK[rightIndex];
        return new ChartHorizontalLinesData(newMaxHeight, newMinHeight, useMinHeight, k);
    }
}
