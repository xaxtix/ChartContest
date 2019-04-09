package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.StackBarChartData;
import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.ui.chart.BarViewData;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.StackBarViewData;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {

    SegmentTree segmentTree = null;
    public StackBarChartView(Context context) {
        super(context);
        superDraw = true;
    }

    @Override
    StackBarViewData createLineViewData(ChartData.Line line) {
        return new StackBarViewData(line);
    }


    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData != null) {
            float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
            float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;


            int start = startXIndex - 1;
            if (start < 0) start = 0;
            int end = endXIndex + 1;
            if (end > chartData.lines.get(0).y.length - 1)
                end = chartData.lines.get(0).y.length - 1;

            float p = chartData.xPercentage[1] * fullWidth;

            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                line.linesPathBottomSize = 0;
            }

            for (int i = start; i <= end; i++) {
                float stackOffset = 0;
                for (int k = 0; k < lines.size(); k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0) continue;


                    int[] y = line.line.y;


                    float xPoint = chartData.xPercentage[i] * fullWidth - offset;
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
                LineViewData line = lines.get(k);
                line.paint.setStrokeWidth(p + dpFloat(0.8f));
                canvas.drawLines(line.linesPath,0,line.linesPathBottomSize,line.paint);
            }
            canvas.restore();
        }
    }

    @Override
    protected void drawPickerChart() {
        super.drawPickerChart();

        bottomChartCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (chartData != null) {

            int n = chartData.xPercentage.length;
            int nl = lines.size();
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                line.linesPathBottomSize = 0;
            }

            for (int i = 0; i < n; i++) {
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
                    line.linesPath[line.linesPathBottomSize++] =  viewSizes.pikerHeight - yPoint - stackOffset;

                    line.linesPath[line.linesPathBottomSize++] = xPoint;
                    line.linesPath[line.linesPathBottomSize++] = viewSizes.pikerHeight -stackOffset;

                    stackOffset += yPoint;
                }
            }
            float p = chartData.xPercentage[1] * viewSizes.pickerWidth;

            for (int k = 0; k < nl; k++) {
                LineViewData line = lines.get(k);
                line.paint.setStrokeWidth(p + dpFloat(0.8f));
                bottomChartCanvas.drawLines(line.linesPath,0,line.linesPathBottomSize,line.paint);
            }
        }
    }

    public void onCheckChanged(){
        int n = chartData.lines.get(0).y.length;
        int k = chartData.lines.size();

        chartData.ySum = new int[n];
        for (int i = 0; i < n; i++) {
            chartData.ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                if(lines.get(j).enabled) chartData.ySum[i] += chartData.lines.get(j).y[i];
            }
        }

        chartData.ySumSegmentTree = new SegmentTree(chartData.ySum);
        super.onCheckChanged();
    }

    @Override
    protected void drawSelection(Canvas canvas) {

    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        return chartData.findMax(startXIndex,endXIndex);
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
                    if (bottomChartCanvas != null && canUpdate) drawPickerChart();
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

}
