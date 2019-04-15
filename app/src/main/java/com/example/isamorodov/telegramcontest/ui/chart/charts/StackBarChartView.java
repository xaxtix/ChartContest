package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.StackBarChartData;
import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.StackBarViewData;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;

public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {

    public StackBarChartView(Context context) {
        super(context);
        superDraw = true;
        useAlphaSignature = true;
    }

    @Override
    public StackBarViewData createLineViewData(ChartData.Line line) {
        return new StackBarViewData(line);
    }

    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData == null) return;
        float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
        float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;


        int start = startXIndex - 2;
        if (start < 0) start = 0;
        int end = endXIndex + 2;
        if (end > chartData.lines.get(0).y.length - 1)
            end = chartData.lines.get(0).y.length - 1;

        float p = chartData.xPercentage[1] * fullWidth;

        for (int k = 0; k < lines.size(); k++) {
            LineViewData line = lines.get(k);
            line.linesPathBottomSize = 0;
        }

        float transitionAlpha = 1f;
        canvas.save();
        if (transitionMode == TRANSITION_MODE_PARENT) {
            postTransition = true;
            selectionA = 0f;
            transitionAlpha = 1f - transitionParams.progress;

            canvas.scale(
                    1 + 2 * transitionParams.progress, 1f,
                    transitionParams.pX, transitionParams.pY
            );

        } else if (transitionMode == TRANSITION_MODE_CHILD) {

            transitionAlpha = transitionParams.progress;

            canvas.scale(
                    transitionParams.progress, 1f,
                    transitionParams.pX, transitionParams.pY
            );
        }

        boolean selected = selectedIndex >= 0 && legendShowing;

        for (int i = start; i <= end; i++) {
            float stackOffset = 0;
            if (selectedIndex == i && selected) continue;
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;


                int[] y = line.line.y;


                float xPoint = p / 2 + chartData.xPercentage[i] * (fullWidth - p) - offset;
                float yPercentage = (float) y[i] / currentMaxHeight;

                float height = (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT) * (line.alpha / 255f);
                float yPoint = getMeasuredHeight() - chartBottom - height;

                line.linesPath[line.linesPathBottomSize++] = xPoint;
                line.linesPath[line.linesPathBottomSize++] = yPoint - stackOffset;

                line.linesPath[line.linesPathBottomSize++] = xPoint;
                line.linesPath[line.linesPathBottomSize++] = getMeasuredHeight() - chartBottom - stackOffset;

                stackOffset += height;
            }
        }


        canvas.save();
        canvas.clipRect(viewSizes.chartStart, SIGNATURE_TEXT_HEIGHT, viewSizes.chartEnd, getMeasuredHeight() - chartBottom);


        for (int k = 0; k < lines.size(); k++) {
            StackBarViewData line = lines.get(k);

            Paint paint = selected || postTransition ? line.unselectedPaint : line.paint;
            if (selected) line.unselectedPaint.setColor(ColorUtils.transformColor(
                    line.lineColor, line.blendColor, (1f - selectionA)));

            if(postTransition){
                line.unselectedPaint.setColor(ColorUtils.transformColor(
                        line.lineColor, line.blendColor, 0));
            }

            paint.setAlpha((int) (255 * transitionAlpha));
            paint.setStrokeWidth(p);
            canvas.drawLines(line.linesPath, 0, line.linesPathBottomSize, paint);
        }

        if (selected) {
            float stackOffset = 0;
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;


                int[] y = line.line.y;


                float xPoint = p / 2 + chartData.xPercentage[selectedIndex] * (fullWidth - p) - offset;
                float yPercentage = (float) y[selectedIndex] / currentMaxHeight;

                float height = (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT) * (line.alpha / 255f);
                float yPoint = getMeasuredHeight() - chartBottom - height;

                line.paint.setStrokeWidth(p);
                line.paint.setAlpha((int) (255 * transitionAlpha));
                canvas.drawLine(xPoint, yPoint - stackOffset,
                        xPoint, getMeasuredHeight() - chartBottom - stackOffset, line.paint);

                stackOffset += height;
            }
        }
        canvas.restore();
        canvas.restore();

    }

    @Override
    protected void drawPickerChart() {
        super.drawPickerChart();

        bottomChartBitmap.eraseColor(0);


        if (chartData != null) {

            int n = chartData.xPercentage.length;
            int nl = lines.size();
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                line.linesPathBottomSize = 0;
            }

            int step = Math.max(1,Math.round(n / 100f));
            for (int i = 0; i < n; i += step) {
                float stackOffset = 0;
                float xPoint = chartData.xPercentage[i] * viewSizes.pickerWidth;

                for (int k = 0; k < nl; k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0) continue;

                    int[] y = line.line.y;


                    float h = ANIMATE_PICKER_SIZES ? pickerMaxHeight : chartData.maxValue;
                    float yPercentage = (float) y[i] / h * (line.alpha / 255f);
                    float yPoint = (yPercentage) * (viewSizes.pikerHeight);


                    line.linesPath[line.linesPathBottomSize++] = xPoint;
                    line.linesPath[line.linesPathBottomSize++] = viewSizes.pikerHeight - yPoint - stackOffset;

                    line.linesPath[line.linesPathBottomSize++] = xPoint;
                    line.linesPath[line.linesPathBottomSize++] = viewSizes.pikerHeight - stackOffset;

                    stackOffset += yPoint;
                }
            }
            float p = chartData.xPercentage[1] * viewSizes.pickerWidth;

            for (int k = 0; k < nl; k++) {
                LineViewData line = lines.get(k);
                line.paint.setStrokeWidth(p * step);
                line.paint.setAlpha(255);
                bottomChartCanvas.drawLines(line.linesPath, 0, line.linesPathBottomSize, line.paint);
            }
        }
    }

    public void onCheckChanged() {
        int n = chartData.lines.get(0).y.length;
        int k = chartData.lines.size();

        chartData.ySum = new int[n];
        for (int i = 0; i < n; i++) {
            chartData.ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                if (lines.get(j).enabled) chartData.ySum[i] += chartData.lines.get(j).y[i];
            }
        }

        chartData.ySumSegmentTree = new SegmentTree(chartData.ySum);
        super.onCheckChanged();
    }

    @Override
    protected void drawSelection(Canvas canvas) {

    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        return chartData.findMax(startXIndex, endXIndex);
    }


    protected void updateBottomMaxHeight() {
        if (!ANIMATE_PICKER_SIZES) return;
        int max = 0;
        for (LineViewData l : lines) {
            if (l.enabled) max += l.line.maxValue;
        }

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

    @Override
    protected void initPickerMaxHeight() {
        super.initPickerMaxHeight();
        pickerMaxHeight = 0;
        for (LineViewData l : lines) {
            if (l.enabled) pickerMaxHeight += l.line.maxValue;
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
    public void clearSelection() {
        super.clearSelection();
    }
}
