package com.example.isamorodov.telegramcontest.ui.chart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.data.StackLinearChartData;
import com.example.isamorodov.telegramcontest.ui.chart.ChartHorizontalLinesData;
import com.example.isamorodov.telegramcontest.ui.chart.LegendSignatureView;
import com.example.isamorodov.telegramcontest.ui.chart.LineViewData;
import com.example.isamorodov.telegramcontest.ui.chart.PieLegendView;
import com.example.isamorodov.telegramcontest.utils.AndroidUtilities;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class PieChartView extends StackLinearChartView<PieChartViewData> {

    float[] values;
    int[] exactlyValues;
    float sum;

    int currentSelection = -1;

    RectF rectF = new RectF();

    TextPaint[] paintTable;

    float MIN_TEXT_SIZE = dp(9);
    float MAX_TEXT_SIZE = dp(22);

    String[] lookupTable = new String[101];
    CosSineTable cosSinTable = new CosSineTable();

    PieLegendView pieLegendView;


    public PieChartView(Context context) {
        super(context);
        for (int i = 1; i <= 100; i++) {
            lookupTable[i] = i + "%";
        }

        paintTable = new TextPaint[(int) (MAX_TEXT_SIZE - MIN_TEXT_SIZE)];
        for (int i = 0; i < paintTable.length; i++) {
            paintTable[i] = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paintTable[i].setTextAlign(Paint.Align.CENTER);
            paintTable[i].setColor(Color.WHITE);
            paintTable[i].setTextSize(MIN_TEXT_SIZE + i * dpFloat(1f));
            paintTable[i].setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        }

        //
    }

    TextPaint getTextPaint(float p) {
        int index = (int) (paintTable.length * p);
        if (index < 0) return paintTable[0];
        if (index > paintTable.length - 1) return paintTable[paintTable.length - 1];
        return paintTable[index];
    }

    @Override
    public void updateColors() {
        super.updateColors();
    }


    @Override
    protected void drawChart(Canvas canvas) {
        if (chartData == null) return;

        int transitionAlpha = 255;

        canvas.save();
        if (transitionMode == TRANSITION_MODE_CHILD) {
            transitionAlpha = (int) (transitionParams.progress * transitionParams.progress * 255);
            canvas.scale(transitionParams.progress, transitionParams.progress,
                    viewSizes.chartArea.centerX(),
                    viewSizes.chartArea.centerY()
            );

        }


        int n = values.length;

        for (int i = 0; i < n; i++) {
            values[i] = 0f;
            exactlyValues[i] = 0;
        }

        float startPercentage = pickerDelegate.pickerStart;
        float endPercentage = pickerDelegate.pickerEnd;

        int ln = chartData.x.length;
        float p = chartData.xPercentage[1];

        boolean start = true;
        int exactlySum = 0;
        for (int i = 0; i < ln; i++) {
            boolean startLocal = start;
            boolean br = false;
            for (int j = 0; j < n; j++) {
                LineViewData lineViewData = lines.get(j);
                if (lineViewData.alpha <= 0) continue;
                if (start) {
                    if (chartData.xPercentage[i] >= startPercentage) {
                        float part = (chartData.xPercentage[i] - startPercentage) / p;
                        values[j] += chartData.lines.get(j).y[i] * part;
                        exactlyValues[j] += chartData.lines.get(j).y[i];
                        exactlySum += chartData.lines.get(j).y[i];
                        startLocal = false;

                    }
                } else {
                    if (chartData.xPercentage[i] > endPercentage) {
                        float part = 1f - (chartData.xPercentage[i] - endPercentage) / p;
                        values[j] += chartData.lines.get(j).y[i] * part;
                        br = true;
                    } else {
                        values[j] += chartData.lines.get(j).y[i];
                        exactlyValues[j] += chartData.lines.get(j).y[i];
                        exactlySum += chartData.lines.get(j).y[i];
                    }
                }
            }
            if (br) break;
            start = startLocal;
        }


        int radius = (int) ((viewSizes.chartArea.width() > viewSizes.chartArea.height() ? viewSizes.chartArea.height() : viewSizes.chartArea.width()) * 0.45f);
        rectF.set(
                viewSizes.chartArea.centerX() - radius,
                viewSizes.chartArea.centerY() - radius,
                viewSizes.chartArea.centerX() + radius,
                viewSizes.chartArea.centerY() + radius
        );

        sum = 0;
        for (int i = 0; i < n; i++) {
            if (lines.get(i).alpha > 0) {
                values[i] *= (lines.get(i).alpha / 255f);
                sum += values[i];
            }
        }

        float a = -90;
        float rText;

        for (int i = 0; i < n; i++) {
            if (lines.get(i).alpha <= 0) continue;
            lines.get(i).paint.setAlpha(transitionAlpha);
            lines.get(i).paint.setAntiAlias(false);

            float sweepAngel = values[i] / sum;


            canvas.save();

            double textAngle = a + (sweepAngel / 2f) * 360f;

            if (lines.get(i).selectionA > 0f) {
                float ai = AndroidUtilities.INTERPOLATOR.getInterpolation(lines.get(i).selectionA);
                canvas.translate(
                        (float) cosSinTable.getCos((int) textAngle) * dp(8) * ai,
                        (float) cosSinTable.getSine((int) textAngle) * dp(8) * ai
                );
            }

            lines.get(i).paint.setStyle(Paint.Style.FILL_AND_STROKE);
            lines.get(i).paint.setStrokeWidth(1);
            canvas.drawArc(
                    rectF,
                    a,
                    (sweepAngel) * 360f,
                    true,
                    lines.get(i).paint);

            lines.get(i).paint.setStyle(Paint.Style.STROKE);


            if (sweepAngel > 0.03f) {
                rText = (float) (rectF.width() * 0.42f * Math.sqrt(1f - sweepAngel));
                TextPaint textPaint = getTextPaint(values[i] / sum);
                textPaint.setAlpha(transitionAlpha);
                canvas.drawText(
                        lookupTable[Math.round(100f * exactlyValues[i] / exactlySum)],
                        (float) (rectF.centerX() + rText * cosSinTable.getCos((int) textAngle)),
                        (float) (rectF.centerY() + rText * cosSinTable.getSine((int) textAngle)) - ((textPaint.descent() + textPaint.ascent()) / 2),
                        textPaint);
            }

            canvas.restore();

            lines.get(i).paint.setAlpha(255);
            a += sweepAngel * 360f;
        }

        canvas.restore();
    }

    @Override
    protected void drawPickerChart() {
        bottomChartBitmap.eraseColor(0);

        if (chartData != null) {
            int n = chartData.xPercentage.length;
            int nl = lines.size();
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                line.linesPathBottomSize = 0;
            }

            float p = chartData.xPercentage[1] * viewSizes.pickerWidth;

            for (int i = 0; i < n; i++) {
                float stackOffset = 0;
                float xPoint = p / 2 + chartData.xPercentage[i] * (viewSizes.pickerWidth - p);

                int sum = 0;

                for (int k = 0; k < nl; k++) {
                    sum += lines.get(k).line.y[i] * (lines.get(k).alpha / 255f);
                }

                for (int k = 0; k < nl; k++) {
                    LineViewData line = lines.get(k);
                    if (!line.enabled && line.alpha == 0) continue;

                    int[] y = line.line.y;

                    float yPercentage = (float) y[i] / sum * (line.alpha / 255f);
                    float yPoint = (yPercentage) * (viewSizes.pikerHeight);


                    line.linesPath[line.linesPathBottomSize++] = xPoint;
                    line.linesPath[line.linesPathBottomSize++] = viewSizes.pikerHeight - yPoint - stackOffset;

                    line.linesPath[line.linesPathBottomSize++] = xPoint;
                    line.linesPath[line.linesPathBottomSize++] = viewSizes.pikerHeight - stackOffset;

                    stackOffset += yPoint;
                }
            }

            for (int k = 0; k < nl; k++) {
                LineViewData line = lines.get(k);
                line.paint.setStrokeWidth(p);
                line.paint.setAlpha(255);
                line.paint.setAntiAlias(false);
                bottomChartCanvas.drawLines(line.linesPath, 0, line.linesPathBottomSize, line.paint);
            }
        }
    }

    @Override
    protected void drawBottomLine(Canvas canvas) {

    }

    @Override
    protected void drawSelection(Canvas canvas) {

    }

    @Override
    protected void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {

    }

    @Override
    void drawBottomSignature(Canvas canvas) {

    }


    @Override
    public void setData(StackLinearChartData chartData) {
        super.setData(chartData);
        values = new float[chartData.lines.size()];
        exactlyValues = new int[chartData.lines.size()];
    }

    @Override
    public PieChartViewData createLineViewData(ChartData.Line line) {
        PieChartViewData l = new PieChartViewData(line);
        l.paint.setAntiAlias(false);
        return l;
    }


    protected void selectXOnChart(int x, int y) {
        if (chartData == null) return;
        double theta = Math.atan2(viewSizes.chartArea.centerY() - y, viewSizes.chartArea.centerX() - x);

        float a = (float) (Math.toDegrees(theta) - 90);
        if (a < 0) a += 360D;
        a /= 360;

        float p = 0;
        int newSelection = -1;

        float selectionStartA = 0f;
        float selectionEndA = 0f;
        for (int i = 0; i < values.length; i++) {
            if (a > p && a < p + (values[i] / sum)) {
                newSelection = i;
                selectionStartA = p;
                selectionEndA = p + (values[i] / sum);
                break;
            }
            p += (values[i] / sum);

        }
        if (currentSelection != newSelection && newSelection >= 0) {
            currentSelection = newSelection;
            invalidate();
            pieLegendView.setVisibility(VISIBLE);
            LineViewData l = lines.get(newSelection);

            boolean start;

            pieLegendView.setData(l.line.name, exactlyValues[currentSelection], l.lineColor);

            float r = rectF.width() / 2;
            int xl = (int) Math.min(
                    rectF.centerX() + r * cosSinTable.getCos((int) (selectionEndA * 360) - 90),
                    rectF.centerX() + r * cosSinTable.getCos((int) (selectionStartA * 360) - 90)
            );

            if (xl < 0) xl = 0;

            int yl = (int) Math.min(
                    (rectF.centerY() + r * cosSinTable.getSine((int) (selectionStartA * 360) - 90)),
                    rectF.centerY() + r * cosSinTable.getSine((int) (selectionEndA * 360) - 90)
            );

            yl = (int) Math.min(rectF.centerY(), yl);

            yl -= dp(50);
            if (yl < 0) yl = 0;

            pieLegendView.setTranslationX(xl);
            pieLegendView.setTranslationY(yl);
        }
        //moveLegend();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (chartData != null) {
            for (int i = 0; i < lines.size(); i++) {
                if (i == currentSelection) {
                    if (lines.get(i).selectionA < 1f) {
                        lines.get(i).selectionA += 0.1f;
                        if (lines.get(i).selectionA > 1f) lines.get(i).selectionA = 1f;
                        invalidate();
                    }
                } else {
                    if (lines.get(i).selectionA > 0) {
                        lines.get(i).selectionA -= 0.1f;
                        if (lines.get(i).selectionA < 0) lines.get(i).selectionA = 0;
                        invalidate();
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    protected void onActionUp() {
        currentSelection = -1;
        pieLegendView.setVisibility(GONE);
    }

    int oldW = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != oldW) {
            oldW = getMeasuredWidth();
            int r = (int) ((viewSizes.chartArea.width() > viewSizes.chartArea.height() ? viewSizes.chartArea.height() : viewSizes.chartArea.width()) * 0.45f);
            MIN_TEXT_SIZE = r / 13;
            MAX_TEXT_SIZE = r / 7;

            paintTable = new TextPaint[(int) (MAX_TEXT_SIZE - MIN_TEXT_SIZE)];
            for (int i = 0; i < paintTable.length; i++) {
                paintTable[i] = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                paintTable[i].setTextAlign(Paint.Align.CENTER);
                paintTable[i].setColor(Color.WHITE);
                paintTable[i].setTextSize(MIN_TEXT_SIZE + i * dpFloat(1f));
                paintTable[i].setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            }
        }
    }

    public void updatePicker(ChartData chartData, long d) {
        int n = chartData.x.length;
        long startOfDay = d - d % 86400000L;
        long endOfDay = startOfDay + 86400000L;
        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < n; i++) {
            if (startOfDay > chartData.x[i]) startIndex = i;
            if (endOfDay > chartData.x[i]) endIndex = i;
        }
        float p = chartData.xPercentage[1] / 2;

        if (startIndex == 0) {
            pickerDelegate.pickerStart = 0;
            pickerDelegate.pickerEnd = chartData.xPercentage[1];
            return;
        }

        if (endIndex == chartData.x.length - 1) {
            pickerDelegate.pickerStart = 1f - chartData.xPercentage[1];
            pickerDelegate.pickerEnd = 1f;
            return;
        }

        pickerDelegate.pickerStart = chartData.xPercentage[startIndex] + p;
        pickerDelegate.pickerEnd = chartData.xPercentage[endIndex] + p;
    }

    public static class CosSineTable {
        double[] cos = new double[361];
        double[] sin = new double[361];


        private CosSineTable() {
            for (int i = 0; i <= 360; i++) {
                cos[i] = Math.cos(Math.toRadians(i));
                sin[i] = Math.sin(Math.toRadians(i));
            }
        }

        public double getSine(int angle) {
            int angleCircle = angle % 360;
            if (angleCircle < 0) angleCircle = 360 + angleCircle;
            return sin[angleCircle];
        }

        public double getCos(int angle) {
            int angleCircle = angle % 360;
            if (angleCircle < 0) angleCircle = 360 + angleCircle;
            return cos[angleCircle];
        }
    }

    @Override
    protected LegendSignatureView createLegendView() {
        return pieLegendView = new PieLegendView(getContext());
    }
}
