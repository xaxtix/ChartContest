package com.example.isamorodov.telegramcontest.data;

import android.graphics.Color;

import com.example.isamorodov.telegramcontest.struct.SegmentTree;
import com.example.isamorodov.telegramcontest.utils.ColorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class StackLinearChartData extends ChartData {

    int[] ySum;
    SegmentTree ySumSegmentTree;

    public StackLinearChartData(JSONObject jsonObject) throws JSONException {
        super(jsonObject);


        int n = lines.get(0).y.length;
        int k = lines.size();

        ySum = new int[n];
        for (int i = 0; i < n; i++) {
            ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                ySum[i] += lines.get(j).y[i];
            }
        }

        ySumSegmentTree = new SegmentTree(ySum);
    }

    public StackLinearChartData(ChartData data, long d) {
        int index = Arrays.binarySearch(data.x, d);
        int startIndex = index - 4;
        int endIndex = index + 4;

        if (startIndex < 0) {
            endIndex += -startIndex;
            startIndex = 0;
        }
        if (endIndex > data.x.length - 1) {
            startIndex -= endIndex - data.x.length;
            endIndex = data.x.length - 1;
        }

        int n = endIndex - startIndex + 1;


        x = new long[n];
        xPercentage = new float[n];

        lines = new ArrayList<>();


        for (int i = 0; i < data.lines.size(); i++) {
            Line line = new Line();
            line.y = new int[n];
            line.ySimple = new int[n];
            line.id = data.lines.get(i).id;
            line.name = data.lines.get(i).name;
            line.color = data.lines.get(i).color;
            line.colorDark = data.lines.get(i).colorDark;
            lines.add(line);
        }


        int i = 0;
        for (int j = startIndex; j <= endIndex; j++) {
            x[i] = data.x[j];

            for (int k = 0; k < lines.size(); k++) {
                Line line = lines.get(k);
                line.y[i] = data.lines.get(k).y[j];
            }

            i++;
        }

        timeStep = 86400000L;
        measure();
        //public ArrayList<Line> lines = new ArrayList<>();

    }

//    public class Line {
//        public int[] y;
//        public int[] ySimple;
//
//        public SegmentTree segmentTree;
//        public String id;
//        public String name;
//        public int maxValue = 0;
//        public int color = Color.BLACK;
//        public int colorDark = Color.WHITE;
//
//        public int findMax() {
//            return segmentTree.rMaxQ(0, y.length - 1);
//        }
//    }

    public int findMax(int start, int end) {
        return ySumSegmentTree.rMaxQ(start, end);
    }
}
