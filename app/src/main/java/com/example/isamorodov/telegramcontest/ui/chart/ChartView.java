package com.example.isamorodov.telegramcontest.ui.chart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.isamorodov.telegramcontest.R;
import com.example.isamorodov.telegramcontest.data.ChartData;
import com.example.isamorodov.telegramcontest.utils.ThemeHelper;

import java.util.ArrayList;

import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dp;
import static com.example.isamorodov.telegramcontest.utils.AndroidUtilities.dpFloat;

public class ChartView extends FrameLayout implements PickerDelegate.Listener {

    ArrayList<ChartHorizontalLinesData> horizontalLines = new ArrayList<>(10);
    ArrayList<ChartBottomSignatureData> bottomSignatureDate = new ArrayList<>(100);

    public ArrayList<LineViewData> lines = new ArrayList<>();

    private final int ANIM_DURATION = 400;
    public final static int HORIZONTAL_PADDING = dp(16f);
    private final static float LINE_WIDTH = dpFloat(1f);
    private final static float SELECTED_LINE_WIDTH = dpFloat(1.3f);
    private final static float SIGNATURE_TEXT_SIZE = dpFloat(12f);
    private final static int SIGNATURE_TEXT_HEIGHT = dp(18f);
    private final static int BOTTOM_SIGNATURE_TEXT_HEIGHT = dp(14f);
    public final static int BOTTOM_SIGNATURE_START_ALPHA = dp(10f);
    private final static int PICKER_PADDING = dp(16f);
    private final static int PICKER_CAPTURE_WIDTH = dp(24);
    private final static int LANDSCAPE_END_PADDING = dp(144);
    private final static int BOTTOM_SIGNATURE_OFFSET = dp(10);
    private final static int DP_10 = dp(10);
    private final static int DP_5 = dp(5);
    private final static int DP_2 = dp(2);


    public final static boolean USE_LINES = android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P;
    private final static boolean ANIMATE_PICKER_SIZES = android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;

    int chartBottom;
    float currentMaxHeight = 250;
    float thresholdMaxHeight = 0;

    int startXIndex;
    int endXIndex;

    boolean landscape = false;


    public boolean parentCanScrollVertically = false;


    Paint linePaint = new Paint();
    Paint selectedLinePaint = new Paint();
    Paint signaturePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    Paint bottomSignaturePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    Paint pickerSelectorPaint = new Paint();
    Paint unactiveBottomChartPaint = new Paint();
    Paint selectionBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Rect pickerRect = new Rect();

    ValueAnimator maxValueAnimator;
    ValueAnimator alphaAnimator;
    ValueAnimator alphaBottomAnimator;
    ValueAnimator pickerAnimator;
    int animateToHeight = 0;

    PickerDelegate pickerDelegate = new PickerDelegate(this);
    ChartViewSizes viewSizes = new ChartViewSizes();
    ChartData chartData;

    ChartBottomSignatureData currentBottomSignatures;
    private float pickerMaxHeight;
    private float animatedToPickerMaxHeight;
    private int tmpN;
    private int tmpI;
    private int bottomSignatureOffset;


    private boolean chartCaptured = false;
    private int selectedIndex = -1;

    private LegendSignatureView legendSignatureView;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        horizontalLines.add(new ChartHorizontalLinesData(250));
    }

    private void init() {
        linePaint.setStrokeWidth(LINE_WIDTH);
        selectedLinePaint.setStrokeWidth(SELECTED_LINE_WIDTH);

        signaturePaint.setTextSize(SIGNATURE_TEXT_SIZE);
        bottomSignaturePaint.setTextSize(SIGNATURE_TEXT_SIZE);
        bottomSignaturePaint.setTextAlign(Paint.Align.CENTER);

        selectionBackgroundPaint.setStrokeWidth(dpFloat(6f));
        selectionBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        setLayerType(LAYER_TYPE_HARDWARE, null);
        setWillNotDraw(false);

        legendSignatureView = new LegendSignatureView(getContext());

        addView(legendSignatureView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );

        legendSignatureView.setVisibility(GONE);


        updateColors();
    }

    public void updateColors() {

        signaturePaint.setColor(ThemeHelper.getColor(R.attr.signature));
        bottomSignaturePaint.setColor(ThemeHelper.getColor(R.attr.signature));
        linePaint.setColor(ThemeHelper.getColor(R.attr.hint_line));
        selectedLinePaint.setColor(ThemeHelper.getColor(R.attr.active_line));
        pickerSelectorPaint.setColor(ThemeHelper.getColor(R.attr.active_picker_chart));
        unactiveBottomChartPaint.setColor(ThemeHelper.getColor(R.attr.inactive_picker_chart));
        selectionBackgroundPaint.setColor(ThemeHelper.getColor(R.attr.card_background));
        ripplePaint.setColor(ThemeHelper.getColor(R.attr.ripple));
        legendSignatureView.recolor();

        for (LineViewData l : lines) {
            l.updateColors();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!landscape) {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(widthMeasureSpec)
            );
        } else {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec)
            );
        }

        measureSizes();
    }

    private void measureSizes() {
        if (getMeasuredHeight() <= 0 || getMeasuredWidth() <= 0) {
            return;
        }
        viewSizes.pickerWidth = getMeasuredWidth() - (HORIZONTAL_PADDING * 2);
        viewSizes.chartStart = HORIZONTAL_PADDING;
        viewSizes.chartEnd = getMeasuredWidth() - (landscape ? LANDSCAPE_END_PADDING : HORIZONTAL_PADDING);
        viewSizes.chartWidth = viewSizes.chartEnd - viewSizes.chartStart;
        viewSizes.chartFullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));

        measurePickerCharts();
        updateLineSignature();
        chartBottom = dp(118f);
        viewSizes.chartArea.set(viewSizes.chartStart - HORIZONTAL_PADDING, 0, viewSizes.chartEnd + HORIZONTAL_PADDING, getMeasuredHeight() - chartBottom);

        if (chartData != null) {
            bottomSignatureOffset = (int) (dp(20) / ((float) viewSizes.pickerWidth / chartData.x.length));
        }
        measureHeightThreshold();
    }

    private void measureHeightThreshold() {
        int chartHeight = getMeasuredHeight() - chartBottom;
        if (animateToHeight == 0 || chartHeight == 0) return;
        thresholdMaxHeight = ((float) animateToHeight / chartHeight) * SIGNATURE_TEXT_SIZE;
    }

    private void measurePickerCharts() {
        int bottom = getMeasuredHeight() - PICKER_PADDING;
        int top = getMeasuredHeight() - viewSizes.pikerHeight - PICKER_PADDING;

        if (chartData != null) {
            for (int k = 0; k < lines.size(); k++) {
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
                    float xPoint = chartData.xPercentage[i] * viewSizes.pickerWidth + HORIZONTAL_PADDING;
                    float h = ANIMATE_PICKER_SIZES ? pickerMaxHeight : chartData.maxValue;
                    float yPercentage = (float) y[i] / h;
                    float yPoint = bottom - yPercentage * (bottom - top);

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
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBottomLine(canvas);
        tmpN = horizontalLines.size();
        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            drawHorizontalLines(canvas, horizontalLines.get(tmpI));
        }
        drawBottomSignature(canvas);
        drawChart(canvas);
        drawPicker(canvas);
        drawSelection(canvas);

        super.onDraw(canvas);
    }


    private void drawBottomSignature(Canvas canvas) {

        if (chartData == null) return;

        tmpN = bottomSignatureDate.size();


        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            int resultAlpha = bottomSignatureDate.get(tmpI).alpha;
            int step = bottomSignatureDate.get(tmpI).step;

            int start = startXIndex - bottomSignatureOffset;
            while (start % step != 0) {
                start--;
            }

            int end = endXIndex - bottomSignatureOffset;
            while (end % step != 0 || end < chartData.x.length - 1) {
                end++;
            }

            start += bottomSignatureOffset;
            end += bottomSignatureOffset;


            float offset = viewSizes.chartFullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

            for (int i = start; i < end; i += step) {
                if (i < 0 || i >= chartData.x.length - 1) continue;
                float xPercentage = (float) (chartData.x[i] - chartData.x[0]) /
                        (float) ((chartData.x[chartData.x.length - 1] - chartData.x[0]));
                float xPoint = xPercentage * viewSizes.chartFullWidth - offset;
                float xPointOffset = xPoint - BOTTOM_SIGNATURE_OFFSET;
                if (xPointOffset > 0 &&
                        xPointOffset <= viewSizes.chartWidth + HORIZONTAL_PADDING) {
                    if (xPointOffset < BOTTOM_SIGNATURE_START_ALPHA) {
                        float a = 1f - (BOTTOM_SIGNATURE_START_ALPHA - xPointOffset) / BOTTOM_SIGNATURE_START_ALPHA;
                        bottomSignaturePaint.setAlpha((int) (resultAlpha * a));
                    } else if (xPointOffset > viewSizes.chartWidth) {
                        float a = 1f - (xPointOffset - viewSizes.chartWidth) / HORIZONTAL_PADDING;
                        bottomSignaturePaint.setAlpha((int) (resultAlpha * a));
                    } else {
                        bottomSignaturePaint.setAlpha(resultAlpha);
                    }
                    canvas.drawText(chartData.getDayString(i), xPoint, getMeasuredHeight() - chartBottom + BOTTOM_SIGNATURE_TEXT_HEIGHT, bottomSignaturePaint);
                }
            }
        }
    }

    private void drawBottomLine(Canvas canvas) {
        linePaint.setAlpha(255);
        signaturePaint.setAlpha(255);
        int textOffset = (int) (SIGNATURE_TEXT_HEIGHT - signaturePaint.getTextSize());
        int y = (getMeasuredHeight() - chartBottom);
        canvas.drawLine(
                viewSizes.chartStart,
                y,
                viewSizes.chartEnd,
                y,
                linePaint);

        canvas.drawText("0", HORIZONTAL_PADDING, y - textOffset, signaturePaint);
    }

    private void drawSelection(Canvas canvas) {
        if (selectedIndex < 0) return;


        float fullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));
        float offset = fullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;

        float xPoint = chartData.xPercentage[selectedIndex] * fullWidth - offset;

        canvas.drawLine(xPoint, 0, xPoint, viewSizes.chartArea.bottom, selectedLinePaint);

        tmpN = lines.size();
        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            LineViewData line = lines.get(tmpI);
            if (!line.enabled) continue;
            float yPercentage = (float) line.line.y[selectedIndex] / currentMaxHeight;
            float yPoint = getMeasuredHeight() - chartBottom - (yPercentage) * (getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT);

            canvas.drawPoint(xPoint, yPoint, line.selectionPaint);
            canvas.drawPoint(xPoint, yPoint, selectionBackgroundPaint);
        }
    }

    private void drawChart(Canvas canvas) {
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

    private void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;

        linePaint.setAlpha(a.alpha);
        signaturePaint.setAlpha(a.alpha);
        int chartHeight = getMeasuredHeight() - chartBottom - SIGNATURE_TEXT_HEIGHT;

        int textOffset = (int) (SIGNATURE_TEXT_HEIGHT - signaturePaint.getTextSize());
        for (int i = 1; i < n; i++) {
            int y = (int) ((getMeasuredHeight() - chartBottom) - chartHeight * (a.values[i] / currentMaxHeight));
            canvas.drawLine(
                    viewSizes.chartStart,
                    y,
                    viewSizes.chartEnd,
                    y,
                    linePaint
            );

            canvas.drawText(a.valuesStr[i], HORIZONTAL_PADDING, y - textOffset, signaturePaint);
        }
    }

    private void drawPicker(Canvas canvas) {
        pickerDelegate.pickerWidth = viewSizes.pickerWidth;
        int bottom = getMeasuredHeight() - PICKER_PADDING;
        int top = getMeasuredHeight() - viewSizes.pikerHeight - PICKER_PADDING;

        int start = (int) (HORIZONTAL_PADDING + viewSizes.pickerWidth * pickerDelegate.pickerStart);
        int end = (int) (HORIZONTAL_PADDING + viewSizes.pickerWidth * pickerDelegate.pickerEnd);

        canvas.save();
        canvas.clipRect(HORIZONTAL_PADDING,
                top,
                getMeasuredWidth() - HORIZONTAL_PADDING,
                bottom
        );

        if (chartData != null) {
            for (int k = 0; k < lines.size(); k++) {
                LineViewData line = lines.get(k);
                if (!line.enabled && line.alpha == 0) continue;
                line.bottomLinePaint.setAlpha(line.alpha);
                if (USE_LINES)
                    canvas.drawLines(line.linesPathBottom, 0, line.linesPathBottomSize, line.bottomLinePaint);
                else canvas.drawPath(line.bottomLinePath, line.bottomLinePaint);
            }
        }
        canvas.restore();

        canvas.drawRect(HORIZONTAL_PADDING,
                top,
                start,
                bottom, unactiveBottomChartPaint);

        canvas.drawRect(end,
                top,
                getMeasuredWidth() - HORIZONTAL_PADDING,
                bottom, unactiveBottomChartPaint);

        pickerRect.set(start,
                top,
                end,
                bottom);


        pickerDelegate.middlePickerArea.set(pickerRect);


        canvas.drawRect(pickerRect.left,
                pickerRect.top, pickerRect.left + DP_5,
                pickerRect.bottom, pickerSelectorPaint);


        canvas.drawRect(pickerRect.right - DP_5,
                pickerRect.top, pickerRect.right,
                pickerRect.bottom, pickerSelectorPaint);

        canvas.drawRect(pickerRect.left + DP_5,
                pickerRect.bottom - DP_2, pickerRect.right - DP_5,
                pickerRect.bottom, pickerSelectorPaint);

        canvas.drawRect(pickerRect.left + DP_5,
                pickerRect.top, pickerRect.right - DP_5,
                pickerRect.top + DP_2, pickerSelectorPaint);


        PickerDelegate.CapturesData middleCap = pickerDelegate.getMiddleCaptured();

        int r = ((pickerRect.bottom - pickerRect.top) >> 1);
        int cY = pickerRect.top + r;

        if (middleCap != null) {
            canvas.drawCircle(pickerRect.left + ((pickerRect.right - pickerRect.left) >> 1), cY, r * middleCap.aValue + HORIZONTAL_PADDING, ripplePaint);
        } else {
            PickerDelegate.CapturesData lCap = pickerDelegate.getLeftCaptured();
            PickerDelegate.CapturesData rCap = pickerDelegate.getRightCaptured();

            if (lCap != null)
                canvas.drawCircle(pickerRect.left + DP_2, cY, r * lCap.aValue - DP_2, ripplePaint);
            if (rCap != null)
                canvas.drawCircle(pickerRect.right - DP_2, cY, r * rCap.aValue - DP_2, ripplePaint);
        }

        int cX = start;
        pickerDelegate.leftPickerArea.set(
                cX - PICKER_CAPTURE_WIDTH,
                top,
                cX + (PICKER_CAPTURE_WIDTH >> 1),
                bottom
        );

        cX = end;
        pickerDelegate.rightPickerArea.set(
                cX - (PICKER_CAPTURE_WIDTH >> 1),
                top,
                cX + PICKER_CAPTURE_WIDTH,
                bottom
        );
    }


    long lastTime = 0;

    private void setMaxValue(int newMaxHeight, boolean animated) {
        setMaxValue(newMaxHeight, animated, false);
    }

    private void setMaxValue(int newMaxHeight, boolean animated, boolean force) {
        final ChartHorizontalLinesData newData = new ChartHorizontalLinesData(newMaxHeight);
        newMaxHeight = newData.values[5];

        if ((Math.abs(newMaxHeight - animateToHeight) < thresholdMaxHeight) || newMaxHeight == 0) {
            return;
        }

        long t = System.currentTimeMillis();
        //  debounce
        if (t - lastTime < 320 && !force) {
            return;
        }

        lastTime = t;

        animateToHeight = newMaxHeight;
        measureHeightThreshold();

        if (maxValueAnimator != null) {
            maxValueAnimator.cancel();
        }

        if (alphaAnimator != null) {
            alphaAnimator.removeAllListeners();
            alphaAnimator.cancel();
        }

        if (!animated) {
            currentMaxHeight = newMaxHeight;
            horizontalLines.clear();
            horizontalLines.add(newData);
            newData.alpha = 255;
            return;
        }


        if (horizontalLines.size() > 1) {

        }
        horizontalLines.add(newData);


        maxValueAnimator = createAnimator(currentMaxHeight, newMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentMaxHeight = ((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        maxValueAnimator.start();

        for (ChartHorizontalLinesData a : horizontalLines) {
            if (a != newData) a.fixedAlpha = a.alpha;
        }

        alphaAnimator = createAnimator(0, 255, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                newData.alpha = (int) ((float) animation.getAnimatedValue());
                for (ChartHorizontalLinesData a : horizontalLines) {
                    if (a != newData)
                        a.alpha = (int) ((a.fixedAlpha / 255f) * (255 - newData.alpha));
                }
                invalidate();
            }
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                horizontalLines.clear();
                horizontalLines.add(newData);
            }
        });

        alphaAnimator.start();
    }

    ValueAnimator createAnimator(float f1, float f2, ValueAnimator.AnimatorUpdateListener l) {
        ValueAnimator a = ValueAnimator.ofFloat(f1, f2);
        a.setDuration(ANIM_DURATION);
        a.setInterpolator(new FastOutSlowInInterpolator());
        a.addUpdateListener(l);
        return a;
    }


    int lastX;
    int lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX(event.getActionIndex());
        int y = (int) event.getY(event.getActionIndex());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                boolean captured = tryCapture(x, y, event.getActionIndex());
                if (captured) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }

                if (viewSizes.chartArea.contains(x, y)) {
                    lastX = x;
                    lastY = y;
                    chartCaptured = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    selectXOnChart(x);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_POINTER_DOWN:

                return tryCapture(x, y, event.getActionIndex());
            case MotionEvent.ACTION_MOVE:

                if (pickerDelegate.captured()) {
                    pickerDelegate.move(x, y, event.getActionIndex());
                    if (event.getPointerCount() > 1) {
                        x = (int) event.getX(1);
                        y = (int) event.getY(1);
                        pickerDelegate.move(x, y, 1);
                    }
                    return true;
                }

                if (chartCaptured) {

                    int dx = x - lastX;
                    int dy = y - lastY;


                    boolean disable = Math.abs(dx) > Math.abs(dy) || Math.abs(dy) < DP_5;
                    lastX = x;
                    lastY = y;

                    getParent().requestDisallowInterceptTouchEvent(disable);
                    selectXOnChart(x);
                }


                return true;
            case MotionEvent.ACTION_POINTER_UP:
                pickerDelegate.uncapture(event.getActionIndex());
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                updateLineSignature();
                pickerDelegate.uncapture(event.getActionIndex());
                getParent().requestDisallowInterceptTouchEvent(false);
                chartCaptured = false;
                selectedIndex = -1;
                legendSignatureView.setVisibility(GONE);
                invalidate();
                setMaxValue(findMaxValue(startXIndex, endXIndex), true, true);
                return true;


        }

        return false;
    }

    private void selectXOnChart(int x) {
        if (chartData == null) return;
        float offset = viewSizes.chartFullWidth * (pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
        float xP = (offset + x) / viewSizes.chartFullWidth;
        if (xP < 0) {
            selectedIndex = 0;
        } else if (xP > 1) {
            selectedIndex = chartData.x.length - 1;
        } else {
            selectedIndex = chartData.findIndex(startXIndex, endXIndex, xP);
            if (selectedIndex > endXIndex) selectedIndex = endXIndex;
            if (selectedIndex < startXIndex) selectedIndex = startXIndex;
        }

        legendSignatureView.setData(selectedIndex, chartData.x[selectedIndex], lines);
        legendSignatureView.setVisibility(VISIBLE);
        float lXPoint = chartData.xPercentage[selectedIndex] * viewSizes.chartFullWidth - offset;
        if (lXPoint > (viewSizes.chartStart + viewSizes.chartWidth) >> 1) {
            lXPoint -= (legendSignatureView.getWidth() + DP_5);
        } else {
            lXPoint += DP_5;
        }
        legendSignatureView.setTranslationX(
                lXPoint
        );
        invalidate();
    }

    private boolean tryCapture(int x, int y, int pointerIndex) {
        if (pickerDelegate.capture(x, y, pointerIndex)) {
            return true;
        }
        return false;
    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        int linesSize = lines.size();
        int maxValue = 0;
        for (int j = 0; j < linesSize; j++) {
            if (!lines.get(j).enabled) continue;
            int lineMax = lines.get(j).line.segmentTree.rMaxQ(startXIndex, endXIndex);
            if (lineMax > maxValue)
                maxValue = lineMax;
        }
        return maxValue;
    }

    public void setData(ChartData chartData) {
        this.chartData = chartData;
        measureSizes();
        invalidate();
        lines.clear();
        for (int i = 0; i < chartData.lines.size(); i++) {
            LineViewData lineViewData = new LineViewData(chartData.lines.get(i));
            lines.add(lineViewData);
        }

        updateIndexes();
        setMaxValue(findMaxValue(startXIndex, endXIndex), false);

        pickerMaxHeight = 0;
        for (LineViewData l : lines) {
            if (l.enabled && l.line.maxValue > pickerMaxHeight) pickerMaxHeight = l.line.maxValue;
        }
        legendSignatureView.setSize(lines.size());
        measurePickerCharts();
        updateLineSignature();
    }

    public void onPickerDataChanged() {
        if (chartData == null) return;
        viewSizes.chartFullWidth = (viewSizes.chartWidth / (pickerDelegate.pickerEnd - pickerDelegate.pickerStart));

        updateIndexes();
        setMaxValue(findMaxValue(startXIndex, endXIndex), true);
        updateBottomMaxHeight();

        tmpN = lines.size();
        for (tmpI = 0; tmpI < tmpN; tmpI++) {
            final LineViewData lineViewData = lines.get(tmpI);
            if (lineViewData.enabled && lineViewData.alpha != 255) {
                if (lineViewData.animatorIn != null && lineViewData.animatorIn.isRunning()) {
                    continue;
                }
                if (lineViewData.animatorOut != null) lineViewData.animatorOut.cancel();
                lineViewData.animatorIn = createAnimator(lineViewData.alpha, 255, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float a = ((float) animation.getAnimatedValue());
                        lineViewData.alpha = (int) a;
                        invalidate();
                    }
                });
                lineViewData.animatorIn.start();
            }

            if (!lineViewData.enabled && lineViewData.alpha != 0) {
                if (lineViewData.animatorOut != null && lineViewData.animatorOut.isRunning()) {
                    continue;
                }
                if (lineViewData.animatorIn != null) lineViewData.animatorIn.cancel();
                lineViewData.animatorOut = createAnimator(lineViewData.alpha, 0, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float a = ((float) animation.getAnimatedValue());
                        lineViewData.alpha = (int) a;
                        invalidate();
                    }
                });
                lineViewData.animatorOut.start();
            }
        }
        invalidate();
    }

    private void updateIndexes() {
        if (chartData == null) return;
        startXIndex = chartData.findStartIndex(Math.max(
                pickerDelegate.pickerStart, 0f
        ));
        endXIndex = chartData.findEndIndex(startXIndex, Math.min(
                pickerDelegate.pickerEnd, 1f
        ));
        updateLineSignature();
    }

    private final static int BOTTOM_SIGNATURE_COUNT = 6;
    private final static int BOTTOM_SIGNATURE_COUNT_MAX = 4;
    private final static int BOTTOM_SIGNATURE_COUNT_MIN = 8;

    private void updateLineSignature() {
        if (chartData == null || viewSizes.chartWidth == 0) return;
        float d = viewSizes.chartFullWidth * chartData.oneDayPercentage;

        float k = viewSizes.chartWidth / d;
        int step = (int) (k / BOTTOM_SIGNATURE_COUNT);
        updateDates(step);

    }


    private void updateDates(int step) {

        if (currentBottomSignatures == null || step >= currentBottomSignatures.stepMax || step <= currentBottomSignatures.stepMin) {


            step = Integer.highestOneBit(step) << 1;
            if (currentBottomSignatures != null && currentBottomSignatures.step == step) {
                return;
            }

            if (alphaBottomAnimator != null) {
                alphaBottomAnimator.removeAllListeners();
                alphaBottomAnimator.cancel();
            }

            int stepMax = (int) (step + step * 0.2);
            int stepMin = (int) (step - step * 0.2);


            final ChartBottomSignatureData data = new ChartBottomSignatureData(step, stepMax, stepMin);
            data.alpha = 255;

            if (currentBottomSignatures == null) {
                currentBottomSignatures = data;
                data.alpha = 255;
                bottomSignatureDate.add(data);
                return;
            }

            currentBottomSignatures = data;


            for (ChartBottomSignatureData a : bottomSignatureDate) {
                a.fixedAlpha = a.alpha;
            }

            bottomSignatureDate.add(data);

            alphaBottomAnimator = createAnimator(0f, 1f, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (float) animation.getAnimatedValue();
                    for (ChartBottomSignatureData a : bottomSignatureDate) {
                        if (a == data) {
                            data.alpha = (int) (255 * alpha);
                        } else {
                            a.alpha = (int) ((1f - alpha) * (a.fixedAlpha));
                        }
                    }
                    invalidate();
                }
            });
            alphaBottomAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    bottomSignatureDate.clear();
                    bottomSignatureDate.add(data);
                }
            });

            alphaBottomAnimator.start();
        }

    }

    public void onCheckChanged() {
        onPickerDataChanged();
    }

    private void updateBottomMaxHeight() {
        if (!ANIMATE_PICKER_SIZES) return;
        int max = 0;
        for (LineViewData l : lines) {
            if (l.enabled && l.line.maxValue > max) max = l.line.maxValue;
        }

        if (max > 0 && max != animatedToPickerMaxHeight) {
            animatedToPickerMaxHeight = max;
            if (pickerAnimator != null) pickerAnimator.cancel();

            pickerAnimator = createAnimator(pickerMaxHeight, animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pickerMaxHeight = (float) animation.getAnimatedValue();
                    measurePickerCharts();
                    invalidate();
                }
            });
            pickerAnimator.start();
        } else {
            measurePickerCharts();
        }


    }

    public void setLandscape(boolean b) {
        landscape = b;
    }

    public void saveState(Bundle outState) {
        if (outState == null) return;

        outState.putFloat("chart_start", pickerDelegate.pickerStart);
        outState.putFloat("chart_end", pickerDelegate.pickerEnd);


        if (lines != null) {
            int n = lines.size();
            boolean[] bArray = new boolean[n];
            for (int i = 0; i < n; i++) {
                bArray[i] = lines.get(i).enabled;
            }
            outState.putBooleanArray("chart_line_enabled", bArray);

        }

    }

    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        pickerDelegate.pickerStart = savedInstanceState.getFloat("chart_start");
        pickerDelegate.pickerEnd = savedInstanceState.getFloat("chart_end");

        if (lines != null) {
            int n = lines.size();
            boolean[] bArray = savedInstanceState.getBooleanArray("chart_line_enabled");
            if (bArray != null) {
                for (int i = 0; i < n; i++) {
                    lines.get(i).enabled = bArray[i];
                    lines.get(i).alpha = bArray[i] ? 255 : 0;
                }
            }
        }

        updateIndexes();
        setMaxValue(findMaxValue(startXIndex, endXIndex), false);
    }
}
