package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.example.isamorodov.telegramcontest.data.BarChartData;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.ui.chart.BarViewData;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;

public class BarChartView extends BaseChartView<BarChartData, BarViewData> {

    public BarChartView(Context context) {
        super(context);
        superDraw = true;
        useAlphaSignature = true;
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

            canvas.save();
            canvas.clipRect(viewSizes.chartStart, 0, viewSizes.chartEnd, getMeasuredHeight() - chartBottom);


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


            for (int k = 0; k < lines.size(); k++) {
                BarViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;

                float p = chartData.xPercentage[1] * fullWidth;
                int[] y = line.line.y;
                int j = 0;

                float selectedX = 0f;
                float selectedY = 0f;
                boolean selected = false;
                float a = line.alpha / 255f;
                for (int i = start; i <= end; i++) {
                    float xPoint = p / 2 + chartData.xPercentage[i] * (fullWidth - p) - offset;
                    float yPercentage = y[i] / currentMaxHeight * a;

                    float yPoint = getMeasuredHeight() - chartBottom - (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT);

                    if (i == selectedIndex && legendShowing) {
                        selected = true;
                        selectedX = xPoint;
                        selectedY = yPoint;
                        continue;
                    }

                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = yPoint;

                    line.linesPath[j++] = xPoint;
                    line.linesPath[j++] = getMeasuredHeight() - chartBottom;
                }

                Paint paint = selected || postTransition ? line.unselectedPaint : line.paint;
                paint.setStrokeWidth(p);


                if (selected) line.unselectedPaint.setColor(ColorUtils.transformColor(
                        line.lineColor, line.blendColor, (1f - selectionA)));

                if (postTransition) {
                    line.unselectedPaint.setColor(ColorUtils.transformColor(
                            line.lineColor, line.blendColor, 0));
                }

                paint.setAlpha((int) (transitionAlpha * 255));
                canvas.drawLines(line.linesPath, 0, j, paint);

                if (selected) {
                    line.paint.setStrokeWidth(p);
                    line.paint.setAlpha((int) (transitionAlpha * 255));
                    canvas.drawLine(selectedX, selectedY,
                            selectedX, getMeasuredHeight() - chartBottom,
                            line.paint
                    );
                    line.paint.setAlpha(255);
                }


            }

            canvas.restore();
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

                line.paint.setStrokeWidth(p + 2);
                bottomChartCanvas.drawLines(line.linesPath, 0, j, line.paint);

            }
        }
    }

    @Override
    protected void drawSelection(Canvas canvas) {

    }

    @Override
    public BarViewData createLineViewData(ChartData.Line line) {
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
