package com.example.isamorodov.telegramcontest.data;

import android.graphics.Color;
import android.util.Log;

import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.utils.ColorUtilites;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChartData {

    public long[] x;
    public float[] xPercentage;
    public String[] daysLookup;
    public ArrayList<Line> lines = new ArrayList<>();
    public int maxValue = 0;

    public float oneDayPercentage = 0f;

    public ChartData(JSONObject jsonObject) throws JSONException {
        JSONArray columns = jsonObject.getJSONArray("columns");

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
                l.name = a.getString(0);
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
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).color = Color.parseColor(colors.getString(lines.get(i).name));
            lines.get(i).colorDark = ColorUtilites.blend(lines.get(i).color, Color.WHITE, 0.85f);
        }
    }

    private long DAY = 86400000L;

    private void measure() {
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


        daysLookup = new String[(int) ((end - start) / DAY) + 10];
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d");

        for (int i = 0; i < daysLookup.length; i++) {
            daysLookup[i] = formatter.format(new Date(start + (i * DAY)));
        }

        oneDayPercentage = DAY / (float)(x[x.length - 1] - x[0]);
    }

    public String getDayString(int i) {
        return daysLookup[(int) ((x[i] - x[0]) / DAY)];
    }

    public int findStartIndex(float v) {
        if (v == 0) return 0;
        int n = xPercentage.length;
        int left = 0;
        int right = n - 1;
        int middle = (right + left) >> 1;

        while (true) {
            if (v < xPercentage[middle] && (middle == 0 || v > xPercentage[middle - 1])) {
                return middle;
            }
            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle;
                middle = (right + left) >> 1;
            }
            if (v > xPercentage[middle]) {
                left = middle;
                middle = (right + left) >> 1;
            }
        }
    }

    public int findEndIndex(int left, float v) {
        int n = xPercentage.length;
        if (v == 1f) return n - 1;
        int right = n - 1;
        int middle = (right + left) >> 1;
        while (true) {
            if (v > xPercentage[middle] && (middle == n - 1 || v < xPercentage[middle + 1])) {
                return middle;
            }

            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle;
                middle = (right + left) >> 1;
            }
            if (v > xPercentage[middle]) {
                left = middle;
                middle = (right + left) >> 1;
            }
        }
    }

    public int findIndex(int left, int right, float v) {

        int n = xPercentage.length;

        if (v <= xPercentage[left]) {
            return left;
        }

        if (v >= xPercentage[right]) {
            return right;
        }

        int middle = (right + left) >> 1;
        while (true) {
            if (v > xPercentage[middle] && (middle == n - 1 || v < xPercentage[middle + 1])) {
                return middle;
            }

            if (v == xPercentage[middle]) {
                return middle;
            }
            if (v < xPercentage[middle]) {
                right = middle;
                middle = (right + left) >> 1;
            }
            if (v > xPercentage[middle]) {
                left = middle;
                middle = (right + left) >> 1;
            }
        }
    }

    public class Line {
        public int[] y;
        public int[] ySimple;

        public SegmentTree segmentTree;
        public String name;
        public int maxValue = 0;
        public int color = Color.BLACK;
        public int colorDark = Color.WHITE;

    }
}
