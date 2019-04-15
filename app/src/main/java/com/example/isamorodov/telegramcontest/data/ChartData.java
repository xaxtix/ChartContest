package com.example.isamorodov.telegramcontest.data;

import android.graphics.Color;
import android.util.LongSparseArray;

import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChartData {

    public long[] x;
    public float[] xPercentage;
    public String[] daysLookup;
    public ArrayList<Line> lines = new ArrayList<>();
    public int maxValue = 0;

    public LongSparseArray<ChartData> childCharts = new LongSparseArray<>();

    public float oneDayPercentage = 0f;
    public float oneHourPercentage = 0f;

    protected ChartData() {
    }

    protected long timeStep;

    public ChartData(JSONObject jsonObject) throws JSONException {
        this(jsonObject, 86400000L);
    }

    public ChartData(JSONObject jsonObject, long timeStep) throws JSONException {
        JSONArray columns = jsonObject.getJSONArray("columns");

        this.timeStep = timeStep;


        int n = columns.length();
        for (int i = 0; i < columns.length(); i++) {
            JSONArray a = columns.getJSONArray(i);
            if (a.getString(0).equals("x")) {
                int len = a.length() - 1;
                x = new long[len];
                for (int j = 0; j < len; j++) {
                    x[j] = a.getLong(j + 1);
                }
            } else {
                Line l = new Line();
                lines.add(l);
                int len = a.length() - 1;
                l.id = a.getString(0);
                l.y = new int[len];
                l.ySimple = new int[len];
                for (int j = 0; j < len; j++) {
                    l.y[j] = a.getInt(j + 1);

                    if (l.y[j] > l.maxValue) l.maxValue = l.y[j];
                }
            }

            measure();
        }

        JSONObject colors = jsonObject.getJSONObject("colors");
        JSONObject names = jsonObject.getJSONObject("names");
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).color = Color.parseColor(colors.getString(lines.get(i).id));
            lines.get(i).name = names.getString(lines.get(i).id);
            lines.get(i).colorDark = ColorUtils.blend(lines.get(i).color, Color.WHITE, 0.85f);
        }
    }


    protected void measure() {
        int n = x.length;
        long start = x[0];
        long end = x[n - 1];

        xPercentage = new float[n];
        for (int i = 0; i < n; i++) {
            xPercentage[i] = (float) (x[i] - start) / (float) (end - start);
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).maxValue > maxValue)
                maxValue = lines.get(i).maxValue;

            lines.get(i).segmentTree = new SegmentTree(lines.get(i).y);
        }


        for (int i = 0; i < lines.size(); i++) {
            int[] y = lines.get(i).y;
            int[] ySimple = lines.get(i).ySimple;
            float maxValue = lines.get(i).maxValue;
            ySimple[0] = y[0];
            ySimple[y.length - 1] = y[y.length - 1];
            for (int j = 0; j < y.length - 2; j++) {
                int dif = (y[j + 2] + y[j]) >> 1;
                if (Math.abs(y[j + 1] - dif) / maxValue < 0.0023f) {
                    ySimple[j + 1] = -1;
                } else {
                    ySimple[j + 1] = y[j + 1];
                }
            }
        }

        daysLookup = new String[(int) ((end - start) / timeStep) + 10];
        SimpleDateFormat formatter;
        if (timeStep < 86400000L) formatter = new SimpleDateFormat("HH:mm");
        else formatter = new SimpleDateFormat("MMM d");

        for (int i = 0; i < daysLookup.length; i++) {
            daysLookup[i] = formatter.format(new Date(start + (i * timeStep)));
        }

        oneDayPercentage = timeStep / (float) (x[x.length - 1] - x[0]);
    }

    public String getDayString(int i) {
        return daysLookup[(int) ((x[i] - x[0]) / timeStep)];
    }

    public int findStartIndex(float v) {
        if (v == 0) return 0;
        int n = xPercentage.length;
        int left = 0;
        int right = n - 1;


        while (left <= right) {
            int middle = (right + left) >> 1;

            if (v < xPercentage[middle] && (middle == 0 || v > xPercentage[middle - 1])) {
                return middle;
            }
            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle - 1;
            }else if (v > xPercentage[middle]) {
                left = middle + 1;
            }
        }
        return left;
    }

    public int findEndIndex(int left, float v) {
        int n = xPercentage.length;
        if (v == 1f) return n - 1;
        int right = n - 1;

        while (left <= right) {
            int middle = (right + left) >> 1;
            if (v > xPercentage[middle] && (middle == n - 1 || v < xPercentage[middle + 1])) {
                return middle;
            }

            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle - 1;
            }else if (v > xPercentage[middle]) {
                left = middle + 1;
            }
        }
        return right;
    }


    public int findIndex(int left, int right, float v) {

        int n = xPercentage.length;

        if (v <= xPercentage[left]) {
            return left;
        }
        if (v >= xPercentage[right]) {
            return right;
        }

        while (left <= right) {
            int middle = (right + left) >> 1;
            if (v > xPercentage[middle] && (middle == n - 1 || v < xPercentage[middle + 1])) {
                return middle;
            }

            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle - 1;
            } else if (v > xPercentage[middle]) {
                left = middle + 1;
            }
        }
        return right;
    }

    public class Line {
        public int[] y;
        public int[] ySimple;

        public SegmentTree segmentTree;
        public String id;
        public String name;
        public int maxValue = 0;
        public int color = Color.BLACK;
        public int colorDark = Color.WHITE;

        public int findMax() {
            return segmentTree.rMaxQ(0, y.length - 1);
        }
    }
}
