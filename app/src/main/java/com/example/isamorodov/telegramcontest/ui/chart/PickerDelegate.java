package com.example.isamorodov.telegramcontest.ui.chart;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;

public class PickerDelegate {

    Listener view;

    public PickerDelegate(Listener view) {
        this.view = view;
    }

    final static float MIN_DIST = 0.1f;

    private final static int CAPTURE_NONE = 0;
    private final static int CAPTURE_LEFT = 1;
    private final static int CAPTURE_RIGHT = 1 << 1;
    private final static int CAPTURE_MIDDLE = 1 << 2;
    public int pickerWidth;

    public Rect leftPickerArea = new Rect();
    public Rect rightPickerArea = new Rect();
    public Rect middlePickerArea = new Rect();


    public float pickerStart = 0.7f;
    public float pickerEnd = 1f;

    public CapturesData getMiddleCaptured() {
        if (capturedStates[0] != null && capturedStates[0].state == CAPTURE_MIDDLE)
            return capturedStates[0];
        if (capturedStates[1] != null && capturedStates[1].state == CAPTURE_MIDDLE)
            return capturedStates[1];
        return null;
    }

    public CapturesData getLeftCaptured() {
        if (capturedStates[0] != null && capturedStates[0].state == CAPTURE_LEFT)
            return capturedStates[0];
        if (capturedStates[1] != null && capturedStates[1].state == CAPTURE_LEFT)
            return capturedStates[1];
        return null;
    }

    public CapturesData getRightCaptured() {
        if (capturedStates[0] != null && capturedStates[0].state == CAPTURE_RIGHT)
            return capturedStates[0];
        if (capturedStates[1] != null && capturedStates[1].state == CAPTURE_RIGHT)
            return capturedStates[1];
        return null;
    }


    class CapturesData {
        public final int state;
        public int x;
        public float start;
        public float end;

        ValueAnimator a = new ValueAnimator();
        public float aValue = 0f;

        public CapturesData(int state) {
            this.state = state;
        }

        public void captured() {
            a = ValueAnimator.ofFloat(0, 1f);
            a.setDuration(600);
            a.setInterpolator(new FastOutSlowInInterpolator());
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    aValue = (float) animation.getAnimatedValue();
                    view.invalidate();
                }
            });
            a.start();
        }

        public void uncapture() {
            if (a != null) a.cancel();
        }
    }

    CapturesData[] capturedStates = {null, null};

    public boolean capture(int x, int y, int pointerIndex) {
        if (pointerIndex == 0) {

            if (leftPickerArea.contains(x, y)) {
                if(capturedStates[0] != null) capturedStates[1] = capturedStates[0];
                capturedStates[0] = new CapturesData(CAPTURE_LEFT);
                capturedStates[0].start = pickerStart;
                capturedStates[0].x = x;
                capturedStates[0].captured();
                return true;
            }

            if (rightPickerArea.contains(x, y)) {
                if(capturedStates[0] != null) capturedStates[1] = capturedStates[0];
                capturedStates[0] = new CapturesData(CAPTURE_RIGHT);
                capturedStates[0].end = pickerEnd;
                capturedStates[0].x = x;
                capturedStates[0].captured();
                return true;
            }


            if (middlePickerArea.contains(x, y)) {
                capturedStates[0] = new CapturesData(CAPTURE_MIDDLE);
                capturedStates[0].end = pickerEnd;
                capturedStates[0].start = pickerStart;
                capturedStates[0].x = x;
                capturedStates[0].captured();
                return true;
            }
        } else if (pointerIndex == 1) {
            if (capturedStates[0] == null) return false;
            if (capturedStates[0].state == CAPTURE_MIDDLE) return false;


            if (leftPickerArea.contains(x, y) && capturedStates[0].state != CAPTURE_LEFT) {
                capturedStates[1] = new CapturesData(CAPTURE_LEFT);
                capturedStates[1].start = pickerStart;
                capturedStates[1].x = x;
                capturedStates[1].captured();
                return true;
            }

            if (rightPickerArea.contains(x, y)) {
                if (capturedStates[0].state == CAPTURE_RIGHT) return false;
                capturedStates[1] = new CapturesData(CAPTURE_RIGHT);
                capturedStates[1].end = pickerEnd;
                capturedStates[1].x = x;
                capturedStates[1].captured();
                return true;
            }
        }
        return false;
    }

    public boolean captured() {
        return capturedStates[0] != null;
    }

    public void move(int x, int y, int pointer) {
        CapturesData d = capturedStates[pointer];
        if (d == null) return;
        int capturedState = d.state;
        float capturedStart = d.start;
        float capturedEnd = d.end;
        int capturedX = d.x;


        if (capturedState == CAPTURE_LEFT) {
            pickerStart = capturedStart - (capturedX - x) / (float) pickerWidth;
            if (pickerStart < 0f) pickerStart = 0f;
            if (pickerEnd - pickerStart < MIN_DIST) pickerStart = pickerEnd - MIN_DIST;
            view.onPickerDataChanged();
        }

        if (capturedState == CAPTURE_RIGHT) {
            pickerEnd = capturedEnd - (capturedX - x) / (float) pickerWidth;
            if (pickerEnd > 1f) pickerEnd = 1f;
            if (pickerEnd - pickerStart < MIN_DIST) pickerEnd = pickerStart + MIN_DIST;
            view.onPickerDataChanged();
        }

        if (capturedState == CAPTURE_MIDDLE) {
            pickerStart = capturedStart - (capturedX - x) / (float) pickerWidth;
            pickerEnd = capturedEnd - (capturedX - x) / (float) pickerWidth;
            if (pickerStart < 0f) {
                pickerStart = 0f;
                pickerEnd = capturedEnd - capturedStart;
            }

            if (pickerEnd > 1f) {
                pickerEnd = 1f;
                pickerStart = 1f - (capturedEnd - capturedStart);
            }

            view.onPickerDataChanged();
        }
    }

    public void uncapture(int pointerIndex) {
        if (pointerIndex == 0) {
            if (capturedStates[0] != null) capturedStates[0].uncapture();
            capturedStates[0] = null;
            if (capturedStates[1] != null) {
                capturedStates[0] = capturedStates[1];
                capturedStates[1] = null;
            }
        } else {
            if (capturedStates[1] != null) capturedStates[1].uncapture();
            capturedStates[1] = null;
        }
    }

    public float selectedWidth() {
        return pickerEnd - pickerStart;
    }

    interface Listener {
        void onPickerDataChanged();

        void invalidate();
    }

}
